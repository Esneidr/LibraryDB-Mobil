package com.example.prueba_100;

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

public class Login extends AppCompatActivity {
    EditText user, password;
    Button login, register;

    Library lDB = new Library(this, "DBLibrary", null, 1);
    User luser = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = findViewById(R.id.etNamelogin);
        password = findViewById(R.id.etPasswordLogin);
        login = findViewById(R.id.btnSesionLogin);
        register = findViewById(R.id.btnRegisterLogin);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String muser = user.getText().toString().trim();
                String mpassword = password.getText().toString().trim();

                if (muser.isEmpty() || mpassword.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Ingrese usuario o contraseña", Toast.LENGTH_LONG).show();
                    return;
                }

                if (searchUser(muser, mpassword)) {
                    SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", muser);
                    editor.putString("password", mpassword);
                    editor.apply();

                    Intent intRent = new Intent(getApplicationContext(), Admin.class);
                    intRent.putExtra("name", luser.getName()); // Corregido para usar el nombre de usuario (o correo)
                    startActivity(intRent);
                } else {
                    Toast.makeText(getApplicationContext(), "Usuario o contraseña inválido...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });
    }

    private boolean searchUser(String muser, String mpassword) {
        SQLiteDatabase dbRead = lDB.getReadableDatabase();
        String query = "SELECT name, password FROM User WHERE name = ? AND password = ?";
        Cursor cUser = dbRead.rawQuery(query, new String[]{muser, mpassword});
        boolean userFound = false;

        if (cUser.moveToFirst()) {
            luser.setName(cUser.getString(0)); // Se asigna el nombre si es encontrado
            luser.setPassword(cUser.getString(1));
            userFound = true;
        }

        cUser.close();
        dbRead.close();
        return userFound;
    }
}