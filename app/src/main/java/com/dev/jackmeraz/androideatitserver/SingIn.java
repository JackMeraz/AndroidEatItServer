package com.dev.jackmeraz.androideatitserver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dev.jackmeraz.androideatitserver.Common.Common;
import com.dev.jackmeraz.androideatitserver.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import info.hoang8f.widget.FButton;

public class SingIn extends AppCompatActivity {

    EditText txtTelefono, txtPass;
    Button btnSingIn;

    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_in);

        txtTelefono = (MaterialEditText) findViewById(R.id.txtphone);
        txtPass = (MaterialEditText) findViewById(R.id.txtpass);
        btnSingIn = (FButton) findViewById(R.id.btnLogin);

        //Iniciar Firebase
        db = FirebaseDatabase.getInstance();
        users = db.getReference("user");

        btnSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singInUser (txtTelefono.getText().toString(), txtPass.getText().toString());
            }
        });
    }

    private void singInUser(final String phone, final String password) {

        final ProgressDialog mDialog = new ProgressDialog(SingIn.this);
        mDialog.setMessage("Por Favor Espere...");
        mDialog.show();

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(phone).exists())
                {
                    mDialog.dismiss();
                    User user = dataSnapshot.child(phone).getValue(User.class);
                    user.setPhone(phone);
                    if (Boolean.parseBoolean(user.getIsStaff())) //Compara si IsStaff es Verdadero
                    {
                        if (user.getPassword().equals(password))
                        {
                            Intent login = new Intent(SingIn.this, Home.class);
                            Common.currentUser = user;
                            startActivity(login);
                            finish();
                        }
                        else
                            Toast.makeText(SingIn.this, "La contraseña no es correcta !!!", Toast.LENGTH_SHORT).show();
                        
                    }
                    else
                        Toast.makeText(SingIn.this, "Por Favor Ingrese con Cuenta de Staff", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mDialog.dismiss();
                    Toast.makeText(SingIn.this, "El Usuario no existe ne la Base de Datos", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
