package com.dev.jackmeraz.androideatitserver;

import android.content.DialogInterface;
import android.graphics.ColorSpace;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.dev.jackmeraz.androideatitserver.Common.Common;
import com.dev.jackmeraz.androideatitserver.Interface.ItemClickListener;
import com.dev.jackmeraz.androideatitserver.Model.Pedido;
import com.dev.jackmeraz.androideatitserver.ViewHolder.OrdenViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;

public class OrdenStatus extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Pedido, OrdenViewHolder> adapter;

    FirebaseDatabase db;
    DatabaseReference pedidos;

    MaterialSpinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orden_status);

        //Firebase
        db = FirebaseDatabase.getInstance();
        pedidos = db.getReference("Pedidos");

        //Inicia
        recyclerView =(RecyclerView) findViewById(R.id.listaOrden);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        cargarOrdenes(); //Carga toda las ordenes

    }

    private void cargarOrdenes() {
        adapter = new FirebaseRecyclerAdapter<Pedido, OrdenViewHolder>(
                Pedido.class,
                R.layout.orden_layout,
                OrdenViewHolder.class,
                pedidos

        ) {
            @Override
            protected void populateViewHolder(OrdenViewHolder ordenViewHolder, Pedido pedido, int i) {
                ordenViewHolder.txtOrdenId.setText(adapter.getRef(i).getKey());
                ordenViewHolder.txtOrdenStatus.setText(Common.convertirCodigoToStatus(pedido.getStatus()));
                ordenViewHolder.txtOrdenDireccion.setText(pedido.getDireccion());
                ordenViewHolder.txtOrdenTelefono.setText((pedido.getTelefono()));

                ordenViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int pos, Boolean isLongClick) {
                        //Solo se impleménta para corregir el bloqueo al hacer clic en este elemento
                    }
                });

            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE))
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        else if (item.getTitle().equals(Common.DELETE))
            deleteOrder (adapter.getRef(item.getOrder()).getKey());
        return super.onContextItemSelected(item);
    }

    private void deleteOrder(String key) {
        pedidos.child(key).removeValue();
    }

    private void showUpdateDialog(String key, final Pedido item) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrdenStatus.this);
        alertDialog.setTitle("Actualizar Orden");
        alertDialog.setMessage("Por Favor seleccione estatus de la orden");

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.actualizar_orden_layout,null);

        spinner = (MaterialSpinner) findViewById(R.id.statusSpinner);
        spinner.setItems ("En Espera de ser Procesado", "Preparando Orden para ser Enviada", "Orden en proceso de entrega");

        alertDialog.setView(view);

        final String localKey = key;
        alertDialog.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                item.setStatus(String.valueOf(spinner.getSelectedIndex()));

                pedidos.child(localKey).setValue(item);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }
}
