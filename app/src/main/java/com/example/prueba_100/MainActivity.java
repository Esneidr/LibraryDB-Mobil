package com.example.prueba_100;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TextView user, date, price, status;
    EditText book;
    ListView listV;
    Button search, rent, clear;

    Library lDB = new Library(this, "DBLibrary", null, 1);
    Book clbook = new Book();
    User cluser = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = findViewById(R.id.tvProfileRent);
        date = findViewById(R.id.tvDateTimeAdmin);
        price = findViewById(R.id.tvBookPrice);
        status = findViewById(R.id.tvBookStatus);
        book = findViewById(R.id.etBookRent);
        listV = findViewById(R.id.lvBookList);
        search = findViewById(R.id.btnRentsearch);
        rent = findViewById(R.id.btnRent);
        clear = findViewById(R.id.btnClear);

        // Obtener nombre de usuario y mostrar la fecha actual
        Intent intent = getIntent();
        String loginName = intent.getStringExtra("name");
        user.setText(loginName);

        // Obtner la fecha y hora actual
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String DateTime = sdf.format(new Date());
        date.setText("Fecha : " + DateTime);

        // obtener del usuario guardados en el login
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String Username = sharedPreferences.getString("username", null);
        String Password = sharedPreferences.getString("password", null);

        if (Username != null && Password != null) {
            Toast.makeText(getApplicationContext(), "Usuario almacenado: " + Username, Toast.LENGTH_SHORT).show();
        }

        loadAvailableBooks();

        rent.setEnabled(false);

        if(Username != null && Password != null) {
            validateUser(Username, Password);
        }

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nbook = book.getText().toString().trim();
                if (!nbook.isEmpty()) {
                    searchBook(nbook);
                } else {
                    Toast.makeText(MainActivity.this, "Ingrese el nombre del libro", Toast.LENGTH_LONG).show();
                }
            }
        });

        rent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RentBook();
            }
        });
    }

    private ArrayList<Book> searchBook(String nbook) {
        ArrayList<Book> arrBook = new ArrayList<Book>();
        SQLiteDatabase dbRead = lDB.getReadableDatabase();
        String query = "SELECT name, coste, available FROM Book WHERE name =?";
        Cursor cBook = dbRead.rawQuery(query, new String[]{nbook});

        if (cBook.moveToFirst()) {
            do {
                clbook.setBookName(cBook.getString(0));
                clbook.setCoste(cBook.getInt(1));
                clbook.setAvailable(cBook.getInt(2));
                arrBook.add(clbook);

                // Verificar la disponibilidad del libro
                if (clbook.getAvailable() == 1) {
                    rent.setEnabled(true);
                    price.setText(String.valueOf(clbook.getCoste()));
                    status.setText("Disponible");
                } else {
                    rent.setEnabled(false);
                    price.setText(String.valueOf(clbook.getCoste()));
                    status.setText("No Disponible");
                    Toast.makeText(getApplicationContext(), "Este libro no está disponible...", Toast.LENGTH_LONG).show();
                }
            } while (cBook.moveToNext());
        } else {
            rent.setEnabled(false);
            Toast.makeText(getApplicationContext(), "Libro no encontrado...", Toast.LENGTH_LONG).show();
        }

        cBook.close();
        dbRead.close();
        return arrBook;
    }

    private ArrayList<User> validateUser(String Username, String Password) {
        ArrayList<User> arrUser = new ArrayList<User>();
        SQLiteDatabase dbRead = lDB.getReadableDatabase();
        String query = "SELECT name, password, status FROM User WHERE name =?";
        Cursor cUser = dbRead.rawQuery(query, new String[]{Username});

        if (cUser.moveToFirst()) {
            do {
                cluser.setName(cUser.getString(0));
                cluser.setPassword(cUser.getString(1));
                cluser.setStatus(cUser.getInt(2));
                arrUser.add(cluser);

                if(cluser.getStatus() == 1 && cluser.getPassword().equals(Password)){
                    rent.setEnabled(true);
                }
                else {
                    rent.setEnabled(false);
                    search.setEnabled(false);
                    Toast.makeText(getApplicationContext(), "No tienes permiso para rentar...", Toast.LENGTH_LONG).show();
                }
            } while (cUser.moveToNext());
        }

        cUser.close();
        dbRead.close();
        return arrUser;
    }

    private void RentBook(){
        SQLiteDatabase dbWrite = lDB.getWritableDatabase();
        String query = "SELECT idbook, available FROM Book WHERE name = ?";
        Cursor cBook = dbWrite.rawQuery(query, new String[]{book.getText().toString().trim()});

        if(cBook.moveToFirst()){
            int lidbook = cBook.getInt(0);
            int lavailable = cBook.getInt(1);

            if(lavailable == 1) {
                ContentValues values = new ContentValues();
                values.put("available", 0);
                int rowsAffected = dbWrite.update("Book", values, "idbook = ?", new String[]{String.valueOf(lidbook)});

                if (rowsAffected > 0) {
                    Toast.makeText(this, "Libro rentado exitosamente", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Error al rentar el libro", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "El libro no está disponible", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Libro no encontrado", Toast.LENGTH_LONG).show();
        }
        cBook.close();
        dbWrite.close();
    }

    private void loadAvailableBooks() {
        SQLiteDatabase dbRead = lDB.getReadableDatabase();
        String query = "SELECT name, coste FROM Book WHERE available = 1";
        Cursor cBook = dbRead.rawQuery(query, null);
        if (cBook.getCount() > 0) {
            List<String> booksList = new ArrayList<>();
            while (cBook.moveToNext()) {
                String lbook = cBook.getString(0);
                int lprice = cBook.getInt(1);
                booksList.add("Titulo: " + lbook + "     Precio: $" + lprice);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, booksList);
            listV.setAdapter(adapter);
        } else {
            Toast.makeText(this, "No hay libros disponibles", Toast.LENGTH_LONG).show();
        }
        cBook.close();
        dbRead.close();
    }
}