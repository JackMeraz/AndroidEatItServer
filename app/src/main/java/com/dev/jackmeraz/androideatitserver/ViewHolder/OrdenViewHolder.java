package com.dev.jackmeraz.androideatitserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import com.dev.jackmeraz.androideatitserver.Interface.ItemClickListener;
import com.dev.jackmeraz.androideatitserver.R;

/**
 * Created by jacobo.meraz on 10/03/2018.
 */

public class OrdenViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

    public TextView txtOrdenId, txtOrdenStatus, txtOrdenTelefono, txtOrdenDireccion;

    private ItemClickListener itemClickListener;

    public OrdenViewHolder(View itemView) {
        super(itemView);

        txtOrdenDireccion = (TextView) itemView.findViewById(R.id.orden_direccion);
        txtOrdenTelefono = (TextView) itemView.findViewById(R.id.orden_telefono);
        txtOrdenStatus = (TextView) itemView.findViewById(R.id.orden_status);
        txtOrdenId = (TextView) itemView.findViewById(R.id.orden_id);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {

        itemClickListener.onClick(v, getAdapterPosition(), false);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Seleccione la Acción");

        menu.add(0,0,getAdapterPosition(), "Actualizar");
    }
}

