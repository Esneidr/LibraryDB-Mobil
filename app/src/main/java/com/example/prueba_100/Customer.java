package com.example.prueba_100;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Customer extends AppCompatActivity {
    TextView user, date, message;
    EditText iduser, name, email, password;
    Spinner status;
    Button search, add, edit, delete;
    String[] arryTypeStatus= {"ACTIVO", "INACTIVO"};

    Library lDB = new Library(this, "DBLibrary", null, 1);
    User luser = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.customer), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = findViewById(R.id.tvProfileUser);
        date = findViewById(R.id.tvDateTimeUser);
        message = findViewById(R.id.tvMessageCostumer);
        iduser = findViewById(R.id.etIdUser);
        name = findViewById(R.id.etNameUser);
        email = findViewById(R.id.etemailUser);
        password = findViewById(R.id.etPasswordUser);
        status = findViewById(R.id.spStatusUser);
        search = findViewById(R.id.btnSearchUser);
        add = findViewById(R.id.btnAddUser);
        edit = findViewById(R.id.btnEditUser);
        delete = findViewById(R.id.btnDeletUser);

        // Obtener nombre de usuario y mostrar la fecha actual
        Intent intent = getIntent();
        String loginName = intent.getStringExtra("name");
        user.setText(loginName);

        // Obtner la fecha y hora actual
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String DateTime = sdf.format(new Date());
        date.setText("Fecha: " + DateTime);

        // Definir el arrayAdapter para llenar el spinner
        ArrayAdapter<String> addStatus = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked,arryTypeStatus);
        status.setAdapter(addStatus);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String liduser = iduser.getText().toString().trim();
                if (!liduser.isEmpty()) {
                    try {
                        int userId = Integer.parseInt(liduser);
                        if (searchID(String.valueOf(userId)).size() > 0) {
                            name.setText(luser.getName());
                            email.setText((luser.getEmail()));
                            password.setText(luser.getPassword());
                            status.setSelection(luser.getStatus() == 1 ? 0 : 1);

                        } else {
                            Toast.makeText(getApplicationContext(), "No extiste...", Toast.LENGTH_LONG).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(getApplicationContext(), "Número no valido...", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Error al buscar...", Toast.LENGTH_LONG).show();
                        e.printStackTrace(); // Imprime el stack trace para el desarrollo (opcional)
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "LLenar campo ID...", Toast.LENGTH_LONG).show();
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String liduser = String.valueOf(Integer.parseInt(iduser.getText().toString()));
                String lname = name.getText().toString();
                String lemail = email.getText().toString();
                String lpassword = password.getText().toString();
                String lstatus = status.getSelectedItem().toString();
                if(emptySpace(lstatus, lname,lemail, lpassword)){
                    if(searchID(liduser).size() == 0){
                        SQLiteDatabase dbWrite = lDB.getWritableDatabase();
                        ContentValues cvUser = new ContentValues();
                        cvUser.put("iduser", liduser);
                        cvUser.put("name", lname);
                        cvUser.put("email", lemail);
                        cvUser.put("password", lpassword);
                        cvUser.put("status", lstatus.equals("ACTIVO")? 1 : 0 );

                        dbWrite.insert("User", null,cvUser);
                        dbWrite.close();

                        Toast.makeText(getApplicationContext(), "Usuario Añadido...", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Ya existe...", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "LLenar Campos...", Toast.LENGTH_LONG).show();
                }
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Validar que el campo ID no esté vacío y convertirlo a entero
                    String liduserStr = iduser.getText().toString().trim();
                    if (liduserStr.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Ingrese el ID...", Toast.LENGTH_LONG).show();
                        return;
                    }

                    int liduser = Integer.parseInt(liduserStr);
                    String lname = name.getText().toString().trim();
                    String lemail = email.getText().toString().trim();
                    String lpassword = password.getText().toString().trim();
                    int lstatus = status.getSelectedItem().toString().equals("ACTIVO") ? 1 : 0;

                    // Validar que no haya campos vacíos
                    if (emptySpace(String.valueOf(liduser), lname, lemail, lpassword)) {
                        SQLiteDatabase dbWrite = lDB.getWritableDatabase();

                        // Usar ContentValues para actualizar los valores en la tabla
                        ContentValues values = new ContentValues();
                        values.put("name", lname);
                        values.put("email", lemail);
                        values.put("password", lpassword);
                        values.put("status", lstatus);

                        // Actualizar el registro correspondiente al iduser
                        int updatedRows = dbWrite.update("User", values, "iduser = ?", new String[]{String.valueOf(liduser)});
                        dbWrite.close();

                        if (updatedRows > 0) {
                            Toast.makeText(getApplicationContext(), "Usuario Actualizado...", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "ID no encontrado...", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Faltan campos...", Toast.LENGTH_LONG).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "ID no válido...", Toast.LENGTH_LONG).show();
                }
            }
        });
        
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String liduser = iduser.getText().toString().trim();
                if (!liduser.isEmpty()) {
                    SQLiteDatabase dbWrite = lDB.getWritableDatabase();
                    int deletedRows = dbWrite.delete("User", "iduser = ?", new String[]{liduser});
                    dbWrite.close();

                    if (deletedRows > 0) {
                        Toast.makeText(getApplicationContext(), "Eliminado..", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "No se encontró...", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Ingrese ID...", Toast.LENGTH_LONG).show();
                }
            }

        });

    }

    private ArrayList<User> searchID(String liduser) {
        ArrayList<User> arrUser = new ArrayList<User>();

        SQLiteDatabase dbRead = lDB.getReadableDatabase();
        String query = "SELECT name, email, password, status FROM User WHERE iduser = '"+liduser+"'";
        Cursor cUser = dbRead.rawQuery(query,null);

        if(cUser.moveToFirst()){
            luser.setIdUser (Integer.parseInt(liduser));
            luser.setName(cUser.getString(0));
            luser.setEmail(cUser.getString(1));
            luser.setPassword(cUser.getString(2));
            luser.setStatus(cUser.getInt(3));
            arrUser.add(luser);
        }
        dbRead.close();
        cUser.close();
        return arrUser;
    }

    private boolean emptySpace(String lstatus, String lname, String lemail, String lpassword) {
        return !lstatus.isEmpty() && !lname.isEmpty() && !lemail.isEmpty() && !lpassword.isEmpty();
    }
}