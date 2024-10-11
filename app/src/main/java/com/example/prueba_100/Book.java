package com.example.prueba_100;

public class Book {
    private int idbook;
    private String bookName;
    private int coste;
    private int available;

    public int getIdbook() {
        return idbook;
    }

    public void setIdbook(int idbook) {
        this.idbook = idbook;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getCoste() {
        return coste;
    }

    public void setCoste(int coste) {
        this.coste = coste;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Book{" +
                "idbook=" + idbook +
                ", bookName='" + bookName + '\'' +
                ", coste=" + coste +
                ", available=" + available +
                '}';
    }
}
