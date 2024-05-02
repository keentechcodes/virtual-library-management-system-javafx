package com.smart.javafx;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.*;
import java.util.*;

public class Database {
    private List<Book> books;
    private List<User> users;
    private User currentUser;
    private static Database database = new Database();
    private Recommender recommender;

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public Recommender getRecommender() {
        return recommender;
    }

    private Database() {
        books = new ArrayList<>();
        users = new ArrayList<>();

        //read books
        try {
            File file = new File("data/books.txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] properties = line.split(";");
                String title = properties[0];
                String author = properties[1];
                String isbn = properties[2];
                int year = Integer.parseInt(properties[3]);
                Book book = new Book(title, author, isbn, year);
                book.setRented(properties[4].equals("RENTED"));
                books.add(book);
            }
            scanner.close();
        } catch (FileNotFoundException e) {

        }

        //read users
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data/users.lib"))) {
            users = (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        // read recommender data
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data/recommender.lib"))) {
            // read if present
            recommender = (Recommender) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // else initialize new recommender
            recommender = new Recommender();
        }
    }

    public User getBorrower(Book book) {
        for(User user:users){
            if(user.getBorrowed().contains(book)){
                return user;
            }
        }
        return null;
    }

    public boolean isBorrowedByMe(Book book){
        return currentUser.getBorrowed().contains(book);
    }

    public boolean isMyFavorite(Book book){
        return currentUser.getFavorite().contains(book);
    }
    public User login(String name, String contact) {
        for (User user : users) {
            if (user.getContact().equals(contact)) {
                return user;
            }
        }
        User user = new User(name, contact);
        users.add(user);
        return user;
    }

    public void rentBook(Book book) {
        books.get(books.indexOf(book)).setRented(true);
        users.get(users.indexOf(currentUser)).borrowBook(book);
        recommender.borrow(book);
    }

    // Return a book
    public void returnBook(Book book) {
        //change book status
        books.get(books.indexOf(book)).setRented(false);
        //remove from user's borrowed set
        users.get(users.indexOf(currentUser)).getBorrowed().remove(book);
        recommender.returnBook(book);
    }

    public void addToFavorite(Book book) {
        // If favorite already remove it, otherwise add to favorites
        if(isMyFavorite(book)){
            users.get(users.indexOf(currentUser)).getFavorite().remove(book);
        }else {
            users.get(users.indexOf(currentUser)).addToFavorite(book);
        }
    }

    public void saveFiles() {
        // Save books
        try (PrintWriter writer = new PrintWriter("data/books.txt")) {
            for (Book book : books) {
                String line = book.getTitle() + ";" + book.getAuthor() + ";" + book.getISBN() + ";"
                        + book.getYearOfIssue() + ";" + (book.isRented() ? "RENTED" : "AVAILABLE");
                writer.println(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("BOOKS: " + e.getMessage());
        }

        // Save users
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data/users.lib"))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.out.println("USERS: " + e.getMessage());
        }

        // Save recommender data
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data/recommender.lib"))) {
            oos.writeObject(recommender);
        } catch (IOException e) {
            System.out.println("RECOMMENDER: " + e.getMessage());
        }
    }

    public List<Book> getBooks() {
        return books;
    }

    public static Database getDatabase() {
        return database;
    }

    public static void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.show();
    }
}
