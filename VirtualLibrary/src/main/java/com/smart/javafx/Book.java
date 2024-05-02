package com.smart.javafx;

import java.io.Serializable;
import java.util.Objects;

public class Book implements Serializable {

    private String title;
    private String author;
    private String ISBN;
    private int yearOfIssue;
    private boolean isRented;

    public Book(){}
    public Book(String title, String author, String ISBN, int yearOfIssue) {
        this.title = title;
        this.author = author;
        this.ISBN = ISBN;
        this.yearOfIssue = yearOfIssue;
        this.isRented = false; // By default, the book is not rented
    }

    // Getters and setters for all properties

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public int getYearOfIssue() {
        return yearOfIssue;
    }

    public void setYearOfIssue(int yearOfIssue) {
        this.yearOfIssue = yearOfIssue;
    }

    public boolean isRented() {
        return isRented;
    }

    public void setRented(boolean rented) {
        isRented = rented;
    }

    @Override
    public String toString() {
        return "(" + ISBN + ") " + title + " by " + author + ", " + yearOfIssue
                + " [" + (isRented ? "RENTED" : "AVAILABLE") + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Book) {
            return ISBN.equals(((Book) obj).ISBN);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, ISBN, yearOfIssue);
    }
}
