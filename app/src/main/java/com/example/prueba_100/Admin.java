package com.example.prueba_100;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Admin extends AppCompatActivity {
    TextView login, date, bookInf;
    Button users, books, rent;

    Library lDB = new Library(this, "DBLibrary", null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        login = findViewById(R.id.tvProfileAdmin);
        date = findViewById(R.id.tvDateTimeAdmin);
        bookInf = findViewById(R.id.tvAvailableBooksAdmin);
        users = findViewById(R.id.btnUsersAdmin);
        books = findViewById(R.id.btnBooksAdmin);
        rent = findViewById(R.id.btnRentAdmin);

        // Obtener el login de inicio de sesión
        Intent intent = getIntent();
        String loginName = intent.getStringExtra("name");
        login.setText("Hola, " + loginName);

        // Obtener la fecha y hora actual
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String DateTime = sdf.format(new Date());
        date.setText("Fecha: " + DateTime);

        // Cargar la información de los libros
        CountBooks();

        // Configurar el botón de usuarios
        users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intAdmin = new Intent(getApplicationContext(), Customer.class);
                intAdmin.putExtra("name", login.getText());
                startActivity(intAdmin);
            }
        });

        // Corregir el Intent del botón de libros
        books.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intAdmin = new Intent(getApplicationContext(), Books.class);
                intAdmin.putExtra("name", login.getText()); // Asignar el loginName correctamente
                startActivity(intAdmin);
            }
        });

        // Configurar el botón de renta
        rent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intAdmin = new Intent(getApplicationContext(), MainActivity.class);
                intAdmin.putExtra("name", login.getText());
                startActivity(intAdmin);
            }
        });
    }
    private ArrayList<Book> CountBooks() {
        ArrayList<Book> arrbook = new ArrayList<>();
        SQLiteDatabase dbRead = lDB.getReadableDatabase();
        String query = "SELECT name, available FROM Book";
        Cursor cBook = dbRead.rawQuery(query, null);

        // Inicializamos los contadores
        int countAvailable = 0;
        int countUnavailable = 0;

        if (cBook.moveToFirst()) {
            do {
                // Crear un nuevo objeto `Book` para cada iteración
                Book lbook = new Book();

                // Obtener los valores del query
                String bookName = cBook.getString(0);
                int available = cBook.getInt(1);

                // Asignar los valores al objeto
                lbook.setBookName(bookName);
                lbook.setAvailable(available);

                // Añadir el objeto a la lista
                arrbook.add(lbook);

                // Contar los libros disponibles y no disponibles
                if (available == 1) {
                    countAvailable++;
                } else {
                    countUnavailable++;
                }
            } while (cBook.moveToNext());
        }

        // Cerrar el cursor y la base de datos
        cBook.close();
        dbRead.close();

        // Mostrar la información de los libros
        String libraryInfo = "Android library tiene " + (countAvailable + countUnavailable) + " Libros.\n" +
                "Disponibles: " + countAvailable + "\n" +
                "No disponibles: " + countUnavailable;
        bookInf.setText(libraryInfo);

        return arrbook;
    }
}