package com.dev.jackmeraz.androideatitserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dev.jackmeraz.androideatitserver.Common.Common;
import com.dev.jackmeraz.androideatitserver.Interface.ItemClickListener;
import com.dev.jackmeraz.androideatitserver.Model.Categoria;
import com.dev.jackmeraz.androideatitserver.Model.Comida;
import com.dev.jackmeraz.androideatitserver.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class ComidaList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    RelativeLayout rootLayout;

    FloatingActionButton fab;

    //Firebase
    FirebaseDatabase db;
    DatabaseReference comidaList;
    FirebaseStorage storage;
    StorageReference storageReference;

    String categoriaId ="";

    FirebaseRecyclerAdapter<Comida,FoodViewHolder> adapter;

    //Agregar Nueva comida
    MaterialEditText txtName, txtDescripcion, txtPrecio, txtDescuento;
    FButton btnSelect, btnCargar;

    Comida newComida;

    Uri saveUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comida_list);

        //Firebase
        db = FirebaseDatabase.getInstance();
        comidaList = db.getReference("Comida");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Iniciar
        recyclerView = (RecyclerView) findViewById(R.id.recycler_comida);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFoodDialog();
            }
        });

        if (getIntent() != null)
            categoriaId = getIntent().getStringExtra("CategoriaId");
        if (!categoriaId.isEmpty())
            loadListFood (categoriaId);
    }

    private void showAddFoodDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ComidaList.this);
        alertDialog.setTitle("Agregar Nueva Comida");
        alertDialog.setMessage("Por favor completa la información");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_food_layout, null);

        txtName = add_menu_layout.findViewById(R.id.txtName);
        txtDescripcion = add_menu_layout.findViewById(R.id.txtDescripcion);
        txtPrecio = add_menu_layout.findViewById(R.id.txtPrecio);
        txtDescuento = add_menu_layout.findViewById(R.id.txtDescuento);
        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);
        btnCargar = add_menu_layout.findViewById(R.id.btnUpload);

        //Evento para boton
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage(); //Permita que el usuario seleccione la imagen de la galería y guarde la uri de esta imagen
            }
        });

        btnCargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();

            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        //Establecer Boton
        alertDialog.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                //Aquí, solo crea una nueva Comida
                if (newComida != null)
                {
                    comidaList.push().setValue(newComida);
                    Snackbar.make(rootLayout, "Nueva Comida " + newComida.getName()+ " agregada correctamente", Snackbar.LENGTH_SHORT)
                            .show();
                }

            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });
        alertDialog.show();


    }

    private void uploadImage() {
        if (saveUri != null )
        {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Cargando...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(ComidaList.this, "Cargado Correctamente !!!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //establecer el valor para la nueva comida si la carga de la imagen y podemos obtener el enlace de descarga
                                    newComida = new Comida();
                                    newComida.setName(txtName.getText().toString());
                                    newComida.setDescripcion(txtDescripcion.getText().toString());
                                    newComida.setPrecio(txtPrecio.getText().toString());
                                    newComida.setDescuento(txtDescuento.getText().toString());
                                    newComida.setMenuId(categoriaId);
                                    newComida.setImage(uri.toString());

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(ComidaList.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Cargando " + progress+"%");
                        }
                    });
        }

    }

    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar Imagen"), Common.PICK_IMAGE_REQUEST);

    }

    private void loadListFood(String categoriaId) {
        adapter = new FirebaseRecyclerAdapter<Comida, FoodViewHolder>(
                Comida.class,
                R.layout.comida_item,
                FoodViewHolder.class,
                comidaList.orderByChild("menuId").equalTo(categoriaId)

        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder foodViewHolder, Comida comida, int i) {
                foodViewHolder.food_name.setText(comida.getName());
                Picasso.with(getBaseContext())
                        .load(comida.getImage())
                        .into(foodViewHolder.food_image);

                foodViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int pos, Boolean isLongClick) {
                        //Mas tarde
                    }
                });
            }
        };

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null)
        {
            saveUri = data.getData();
            btnSelect.setText("Imagen Seleccionada!!!");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE))
        {
            showUpdateFoodDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if (item.getTitle().equals(Common.DELETE))
        {
            deleteComida(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteComida(String key) {
        comidaList.child(key).removeValue();
        Snackbar.make(rootLayout, "Comida Borrada exitosamente", Snackbar.LENGTH_SHORT)
                .show();
    }

    private void showUpdateFoodDialog(final String key, final Comida item) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ComidaList.this);
        alertDialog.setTitle("Editar Comida");
        alertDialog.setMessage("Por favor completa la información");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_food_layout, null);

        txtName = add_menu_layout.findViewById(R.id.txtName);
        txtDescripcion = add_menu_layout.findViewById(R.id.txtDescripcion);
        txtPrecio = add_menu_layout.findViewById(R.id.txtPrecio);
        txtDescuento = add_menu_layout.findViewById(R.id.txtDescuento);

        //Establece valor default para mostrar
        txtName.setText(item.getName());
        txtDescripcion.setText(item.getDescripcion());
        txtPrecio.setText(item.getPrecio());
        txtDescuento.setText(item.getDescuento());

        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);
        btnCargar = add_menu_layout.findViewById(R.id.btnUpload);

        //Evento para boton
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage(); //Permita que el usuario seleccione la imagen de la galería y guarde la uri de esta imagen
            }
        });

        btnCargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarImagen(item);

            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        //Establecer Boton
        alertDialog.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();


                    //Actualizar Informacion
                    item.setName(txtName.getText().toString());
                    item.setDescripcion(txtDescripcion.getText().toString());
                    item.setPrecio(txtPrecio.getText().toString());
                    item.setDescuento(txtDescuento.getText().toString());

                    comidaList.child(key).setValue(item);
                    Snackbar.make(rootLayout, "Comida " + item.getName()+ " ha sido actualizada", Snackbar.LENGTH_SHORT)
                            .show();


            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });
        alertDialog.show();

    }

    private void cambiarImagen(final Comida item) {
        if (saveUri != null )
        {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Cargando...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(ComidaList.this, "Cargado Correctamente !!!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //establecer el valor para la nueva categoría si la carga de la imagen y podemos obtener el enlace de descarga
                                    item.setImage(uri.toString());

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(ComidaList.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage("Cargando " + progress+"%");
                        }
                    });
        }

    }
}
