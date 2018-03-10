package com.dev.jackmeraz.androideatitserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.jackmeraz.androideatitserver.Common.Common;
import com.dev.jackmeraz.androideatitserver.Interface.ItemClickListener;
import com.dev.jackmeraz.androideatitserver.Model.Categoria;
import com.dev.jackmeraz.androideatitserver.ViewHolder.MenuViewHolder;
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

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView txtFullName;

    //Firebase
    FirebaseDatabase database;
    DatabaseReference categorias;
    FirebaseStorage almacenamiento;
    StorageReference storageReference;
    FirebaseRecyclerAdapter<Categoria, MenuViewHolder> adapter;

    //View
    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    //Agregar nuevo Layout Menu
    MaterialEditText txtName;
    FButton btnCargar, btnSelect;

    Categoria newCategoria;

    Uri saveUri;


    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu Administracion");
        setSupportActionBar(toolbar);


        //Iniciar Firebase

        database = FirebaseDatabase.getInstance();
        categorias = database.getReference("Categorias");
        almacenamiento = FirebaseStorage.getInstance();
        storageReference = almacenamiento.getReference();



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Establece Nombre para Usuario
        View headerView = navigationView.getHeaderView(0);
        txtFullName = (TextView)headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(Common.currentUser.getName());

        //Iniciar View
        recycler_menu = (RecyclerView) findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);

        cargarMenu();

    }

    private void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Agregar Nueva Categoria");
        alertDialog.setMessage("Por favor completa la información");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_menu_layout, null);

        txtName = add_menu_layout.findViewById(R.id.txtName);
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

                //Aquí, solo crea una nueva categoría
                if (newCategoria != null)
                {
                    categorias.push().setValue(newCategoria);
                    Snackbar.make(drawer, "Nueva Categoria "+newCategoria.getName()+ " agregada correctamente", Snackbar.LENGTH_SHORT)
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
                            Toast.makeText(Home.this, "Cargado Correctamente !!!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //establecer el valor para la nueva categoría si la carga de la imagen y podemos obtener el enlace de descarga
                                    newCategoria = new Categoria(txtName.getText().toString(),uri.toString());

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar Imagen"), Common.PICK_IMAGE_REQUEST);

    }

    private void cargarMenu() {

        adapter = new FirebaseRecyclerAdapter<Categoria, MenuViewHolder>(
                Categoria.class,
                R.layout.menu_item,
                MenuViewHolder.class,
                categorias
        ) {
            @Override
            protected void populateViewHolder(MenuViewHolder menuViewHolder, Categoria categoria, int i) {

                menuViewHolder.txtMenuName.setText(categoria.getName());
                Picasso.with(Home.this).load(categoria.getImage())
                        .into(menuViewHolder.imageView);

                menuViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int pos, Boolean isLongClick) {
                        //Envia CategoriaID e inicia nuevo activity
                        Intent comidaList = new Intent(Home.this, ComidaList.class);
                        comidaList.putExtra("CategoriaId", adapter.getRef(pos).getKey());
                        startActivity(comidaList);
                    }
                });

            }
        };

        adapter.notifyDataSetChanged(); //Refresca informacion si la informacion cambia
        recycler_menu.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //Actualizar / Borrar


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(Common.UPDATE))
        {
            showUpdateDialog (adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }
        else if (item.getTitle().equals(Common.DELETE))
        {
            BorrarCategoria (adapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);
    }

    private void BorrarCategoria(String key) {
        categorias.child(key).removeValue();
        Toast.makeText(this, "Elemento Borrado !!!", Toast.LENGTH_SHORT).show();
    }

    private void showUpdateDialog(final String key, final Categoria item) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Actualizar Categoria");
        alertDialog.setMessage("Por favor completa la información");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_menu_layout, null);

        txtName = add_menu_layout.findViewById(R.id.txtName);
        btnSelect = add_menu_layout.findViewById(R.id.btnSelect);
        btnCargar = add_menu_layout.findViewById(R.id.btnUpload);

        //Establece Nombre por default
        txtName.setText(item.getName());


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

                //Actualiza Informacion
                item.setName(txtName.getText().toString());
                categorias.child(key).setValue(item);
                Toast.makeText(Home.this, "Elemento Actualizado !!!", Toast.LENGTH_SHORT).show();

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

    private void cambiarImagen(final Categoria item) {
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
                            Toast.makeText(Home.this, "Cargado Correctamente !!!", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
