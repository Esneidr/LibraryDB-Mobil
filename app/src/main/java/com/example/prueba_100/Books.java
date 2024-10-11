package com.example.prueba_100;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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

public class Books extends AppCompatActivity {
    TextView user, date, message;
    EditText idBook, book, price;
    Spinner check;
    Button search, add, edit, delete;
    String[] arryTypeStatus = {"DISPONIBLE", "NO DISPONIBLE"};

    Library lDB = new Library(this, "DBLibrary", null, 1);
    Book lcBook = new Book();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_books);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.books), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar los elementos de la interfaz
        user = findViewById(R.id.etProfileBook);
        date = findViewById(R.id.tvDateTimeBooks);
        message = findViewById(R.id.tvMessageBook);
        idBook = findViewById(R.id.etBookId);
        book = findViewById(R.id.etBookName);
        price = findViewById(R.id.etBookCost);
        check = findViewById(R.id.spBookAvailable);
        search = findViewById(R.id.btnBookSearch);
        add = findViewById(R.id.btnBookAdd);
        edit = findViewById(R.id.btnBookEdit);
        delete = findViewById(R.id.btnBookDelete);

        // Obtener nombre de usuario y mostrar la fecha actual
        Intent intent = getIntent();
        String loginName = intent.getStringExtra("name");
        user.setText("Hola, " + loginName);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String DateTime = sdf.format(new Date());
        date.setText("Fecha: " + DateTime);

        // Configurar el Spinner
        ArrayAdapter<String> adStatus = new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked, arryTypeStatus);
        check.setAdapter(adStatus);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lidbook = idBook.getText().toString().trim();

                if (!lidbook.isEmpty()) {
                    try {
                        int bookId = Integer.parseInt(lidbook);
                        if (searchID(String.valueOf(bookId)).size() > 0) {
                            book.setText(lcBook.getBookName());
                            price.setText(String.valueOf(lcBook.getCoste()));
                            check.setSelection(lcBook.getAvailable() == 1 ? 0 : 1);
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
                String lidbook = String.valueOf(Integer.parseInt(idBook.getText().toString()));
                String lbook = book.getText().toString();
                String lprice = String.valueOf(Integer.parseInt(price.getText().toString()));
                String lcheck = check.getSelectedItem().toString();
                if(emptySpace(lcheck, lbook, lprice)){
                    if(searchID(lidbook).size() == 0){
                        SQLiteDatabase dbWrite = lDB.getWritableDatabase();
                        ContentValues cvBook = new ContentValues();
                        cvBook.put("idbook", lidbook);
                        cvBook.put("name", lbook);
                        cvBook.put("coste", lprice);
                        cvBook.put("available", lcheck.equals("DISPONIBLE")? 1 : 0 );

                        dbWrite.insert("Book", null,cvBook);
                        dbWrite.close();

                        Toast.makeText(getApplicationContext(), "Libro Añadido...", Toast.LENGTH_LONG).show();
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
                int lidbook = Integer.parseInt(idBook.getText().toString());
                String lbook = book.getText().toString();
                int lprice = Integer.parseInt(price.getText().toString());
                int lcheck = check.getSelectedItem().toString().equals("DISPONIBLE") ? 1 : 0;
                if(emptySpace(String.valueOf(lidbook), lbook, String.valueOf(lprice))){
                    SQLiteDatabase dbWrite = lDB.getWritableDatabase();
                    dbWrite.execSQL("UPDATE Book SET name = '"+lbook+"', coste = '"+lprice+"', available ="+lcheck+" WHERE idbook = '"+lidbook+"'");

                    Toast.makeText(getApplicationContext(), "Libro Actulizado...", Toast.LENGTH_LONG).show();
                    dbWrite.close();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Faltan campos...", Toast.LENGTH_LONG).show();
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lidbook = idBook.getText().toString().trim();
                if (!lidbook.isEmpty()) {
                    SQLiteDatabase dbWrite = lDB.getWritableDatabase();
                    int deletedRows = dbWrite.delete("Book", "idbook = ?", new String[]{lidbook});
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
    private ArrayList<Book> searchID(String lidbook) {
        ArrayList<Book> arrBook = new ArrayList<Book>();

        SQLiteDatabase dbRead = lDB.getReadableDatabase();
        String query = "SELECT name, coste, available FROM Book WHERE idbook = '"+lidbook+"'";
        Cursor cBook = dbRead.rawQuery(query,null);

        if(cBook.moveToFirst()){
            lcBook.setIdbook (Integer.parseInt(lidbook));
            lcBook.setBookName(cBook.getString(0));
            lcBook.setCoste(cBook.getInt(1));
            lcBook.setAvailable(cBook.getInt(2));
            arrBook.add(lcBook);
        }
        dbRead.close();
        cBook.close();
        return arrBook;
    }

    private boolean emptySpace(String lcheck, String lbook, String lprice) {
            return !lcheck.isEmpty() && !lbook.isEmpty() && !lprice.isEmpty();
    }
}