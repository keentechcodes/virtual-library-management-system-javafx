package com.smart.javafx;

import java.io.Serializable;
import java.util.*;

public class User implements Serializable {
    private String name;
    private String contact;
    private Set<Book> borrowed;
    private Stack<Book> history;
    private Set<Book> favorite;

    public User() {
    }

    public User(String name, String contact) {
        this.name = name;
        this.contact = contact;
        borrowed = new HashSet<>();
        favorite = new HashSet<>();
        history = new Stack<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void borrowBook(Book book) {
        borrowed.add(book);
        history.add(book);
    }

    public Stack<Book> getHistory() {
        return history;
    }

    public void setHistory(Stack<Book> history) {
        this.history = history;
    }

    public void addToFavorite(Book book) {
        favorite.add(book);
    }

    public Set<Book> getBorrowed() {
        return borrowed;
    }

    public void setBorrowed(Set<Book> borrowed) {
        this.borrowed = borrowed;
    }

    public Set<Book> getFavorite() {
        return favorite;
    }

    public void setFavorite(Set<Book> favorite) {
        this.favorite = favorite;
    }
}
