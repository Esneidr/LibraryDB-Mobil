package com.example.prueba_100;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class Register extends AppCompatActivity {
    EditText name, email, password;
    Button register;

    Library lDB = new Library(this, "DBLibrary", null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        name = findViewById(R.id.etNameRegister);
        email = findViewById(R.id.etemailRegister);
        password = findViewById(R.id.etpasswordRegister);
        register = findViewById(R.id.btnregister);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rname = name.getText().toString().trim();
                String remail = email.getText().toString().trim();
                String rpassword = password.getText().toString().trim();

                if (checkData(rname, remail, rpassword)) {
                    if (searchUser(rname).size() == 0) {
                        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", rname);
                        editor.putString("password", rpassword);
                        editor.apply();

                        SQLiteDatabase dbWrite = lDB.getWritableDatabase();
                        ContentValues cUser = new ContentValues();
                        cUser.put("name", rname);
                        cUser.put("email", remail);
                        cUser.put("password", rpassword);
                        dbWrite.insert("User", null, cUser);
                        dbWrite.close();
                        Toast.makeText(getApplicationContext(), "Usuario agregado...", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), Admin.class);
                        intent.putExtra("name", rname);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Usuario Existente...", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Ingrese todos los datos...", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private ArrayList<User> searchUser(String rname) {
        ArrayList<User> arruser = new ArrayList<>();
        SQLiteDatabase dbRead = lDB.getReadableDatabase();
        String query = "SELECT name, email, password FROM User WHERE name = ?";
        Cursor Cuser = dbRead.rawQuery(query, new String[]{rname});
        if (Cuser.moveToFirst()) {
            do {
                // Crear una nueva instancia de 'user' por cada registro que se encuentre
                User luser = new User();
                luser.setName(Cuser.getString(0));
                luser.setEmail(Cuser.getString(1));
                luser.setPassword(Cuser.getString(2));
                arruser.add(luser);
            } while (Cuser.moveToNext());
        }
        Cuser.close();
        dbRead.close(); // Cerrar la base de datos después de leer
        return arruser;
    }

    private boolean checkData(String rname, String remail, String rpassword) {
        // Validar que los campos no estén vacíos
        if (rname.isEmpty() || remail.isEmpty() || rpassword.isEmpty()) {
            return false;
        }
        return true;
    }
}