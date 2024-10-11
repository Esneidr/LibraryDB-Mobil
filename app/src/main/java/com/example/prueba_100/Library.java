package com.example.prueba_100;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Library extends SQLiteOpenHelper {
    // Declaración de las tablas con sus claves primarias
    String tblBook = "CREATE TABLE Book(idbook INTEGER PRIMARY KEY, name TEXT, coste INTEGER, available INTEGER)";
    String tblUser = "CREATE TABLE User(iduser INTEGER PRIMARY KEY, name TEXT, email TEXT, password TEXT, status INTEGER)";
    String tblRent = "CREATE TABLE Rent(idrent INTEGER PRIMARY KEY, iduser INTEGER, idbook INTEGER, fecha DATETIME, " +
            "FOREIGN KEY (iduser) REFERENCES User(iduser), " +
            "FOREIGN KEY (idbook) REFERENCES Book(idbook))";

    // Constructor de la base de datos
    public Library (Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // Método para crear las tablas en la base de datos
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tblBook);
        db.execSQL(tblUser);
        db.execSQL(tblRent);
    }

    // Método para actualizar la base de datos cuando cambie la versión
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Si cambia la versión, eliminar tablas antiguas y crear nuevas
        db.execSQL("DROP TABLE IF EXISTS Rent");
        db.execSQL("DROP TABLE IF EXISTS User");
        db.execSQL("DROP TABLE IF EXISTS Book");
        onCreate(db);  // Vuelve a crear las tablas con el esquema actualizado
    }



}
