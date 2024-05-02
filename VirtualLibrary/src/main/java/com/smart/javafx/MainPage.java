package com.smart.javafx;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Hashtable;
import java.util.TreeMap;

public class MainPage extends BorderPane {
    private TreeMap<String, Book> bookMapTitles = new TreeMap<>();
    private TreeMap<String, Book> bookMapAuthors = new TreeMap<>();
    private ObservableList<Book> observableBookList = FXCollections.observableArrayList();
    private ObservableList<Book> observableRecommendedList = FXCollections.observableArrayList();


    private Button btnBorrow = new Button("Borrow");
    private Button btnReturn = new Button("Return");
    private Button btnInfo = new Button("Borrower Info");
    private Button btnFavorite = new Button("Add to Favorites");
    private Button btnMyFavorite = new Button("My Favorites");
    private Button btnMyBorrowed = new Button("My Borrowings");
    private Button btnMyHistory = new Button("My Reading History");
    private RadioButton radioAuthor = new RadioButton("Author");
    private RadioButton radioTitle = new RadioButton("Title");

    public MainPage(Stage stage) {

        // Add the books to the maps
        for (Book book : Database.getDatabase().getBooks()) {
            bookMapTitles.put(book.getTitle().toLowerCase(), book);
            bookMapAuthors.put(book.getAuthor().toLowerCase(), book);
        }

        observableBookList.setAll(Database.getDatabase().getBooks());
        // Create the search box and button
        TextField searchField = new TextField();
        searchField.setPromptText("Search for a book...");
        Button searchButton = new Button("Search");
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Clear the listview list
                observableBookList.clear();
                // Check if search field is empty
                // then add all books to listview and return
                if (searchField.getText().trim().isEmpty()) {
                    if (radioTitle.isSelected()) {
                        observableBookList.addAll(bookMapTitles.values());
                    } else if (radioAuthor.isSelected()) {
                        observableBookList.addAll(bookMapAuthors.values());
                    } else {
                        observableBookList.addAll(Database.getDatabase().getBooks());
                    }
                    return;
                }
                // If some book is searched, get it from map and add to list
                Hashtable<String, Book> bookHashtable = new Hashtable<>(bookMapTitles);
                Book book = bookHashtable.get(searchField.getText().toLowerCase());
                if (book != null) {
                    observableBookList.add(book);
                }
            }
        });

        observableRecommendedList.addAll(Database.getDatabase().getRecommender().getRecommendations());
        // Create the recommended book list view
        ListView<Book> recommendationListView = new ListView<>(observableRecommendedList);
        recommendationListView.setPrefHeight(150);


        // Create the book list view
        ListView<Book> bookListView = new ListView<>(observableBookList);
        bookListView.setPrefHeight(200);

        // Manually design listview cells
        bookListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Book book, boolean empty) {
                super.updateItem(book, empty);
                if (empty || book == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(book.toString());

                    // Add a heart icon to the cell if the book is favorite
                    if (Database.getDatabase().isMyFavorite(book)) {
                        Label heartLabel = new Label("\u2665"); // Unicode for heart
                        setGraphic(heartLabel);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
        // Change listener fired when some book is selected from list
        bookListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Book>() {
            @Override
            public void changed(ObservableValue<? extends Book> observable, Book oldValue, Book newValue) {
                disableButtons(true);
                // If something selected
                if (newValue != null) {
                    // Allow to add to favorites
                    btnFavorite.setDisable(false);
                    if (Database.getDatabase().isMyFavorite(newValue)) {
                        btnFavorite.setText("Remove Favorite");
                    } else {
                        btnFavorite.setText("Add to Favorites");
                    }
                    // If it is rented
                    if (newValue.isRented()) {
                        if (Database.getDatabase().isBorrowedByMe(newValue)) {
                            btnReturn.setDisable(false);
                        }
                        btnInfo.setDisable(false);// Enable info button to check who borrowed
                    } else {
                        // If it is available book, allow to borrow
                        btnBorrow.setDisable(false);
                    }
                }
            }
        });

        // Create sort options
        // Toggle group to group radio buttons in same group
        // so that only one is selected
        ToggleGroup group = new ToggleGroup();
        radioAuthor.setToggleGroup(group);
        radioTitle.setToggleGroup(group);
        Label label = new Label("Sort By: ");
        HBox sortBox = new HBox(5, label, radioAuthor, radioTitle);
        // RadioButton selection listener
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                RadioButton btn = (RadioButton) newValue;
                if (btn.getText().equals("Author")) {
                    // Clear list and add sorted values with authors from tree
                    observableBookList.clear();
                    observableBookList.addAll(bookMapAuthors.values());
                } else {
                    // create tree using default map which has books sorted by title
                    observableBookList.clear();
                    observableBookList.addAll(bookMapTitles.values());
                }
            }
        });

        // Add bottom buttons
        HBox btnBox = new HBox(5);
        disableButtons(true);
        btnBox.getChildren().addAll(btnFavorite, btnInfo, btnReturn, btnBorrow);
        HBox btnMyBox = new HBox(5, btnMyBorrowed, btnMyFavorite, btnMyHistory);
        btnMyBox.setAlignment(Pos.CENTER);
        btnBox.setAlignment(Pos.CENTER);

        // Button events
        btnBorrow.setOnAction(event -> {
            Database.getDatabase().rentBook(bookListView.getSelectionModel().getSelectedItem());
            refreshLists();
        });
        btnReturn.setOnAction(event -> {
            Database.getDatabase().returnBook(bookListView.getSelectionModel().getSelectedItem());
            refreshLists();
        });
        btnFavorite.setOnAction(event -> {
            Database.getDatabase().addToFavorite(bookListView.getSelectionModel().getSelectedItem());
            refreshLists();
        });
        btnInfo.setOnAction(event -> {
            User user = Database.getDatabase().getBorrower(bookListView.getSelectionModel().getSelectedItem());
            Database.showMessage("Borrower details..\n" +
                    "Name: " + user.getName() + "\n" +
                    "Contact: " + user.getContact());
        });
        btnMyBorrowed.setOnAction(event -> {
            //Create and show a small stage with a list of borrowed books
            ObservableList<String> borrowedList = FXCollections.observableArrayList();
            ListView<String> borrowedListView = new ListView<>(borrowedList);
            for (Book book : Database.getDatabase().getCurrentUser().getBorrowed()) {
                borrowedList.add(book.getTitle() + " by " + book.getAuthor());
            }
            Stage borrowStage = new Stage();
            borrowStage.setTitle("My Borrowings");
            borrowStage.setScene(new Scene(borrowedListView, 400, 300));
            borrowStage.show();
        });
        btnMyFavorite.setOnAction(event -> {
            //Create and show a small stage with a list of favorite books
            ObservableList<String> borrowedList = FXCollections.observableArrayList();
            ListView<String> borrowedListView = new ListView<>(borrowedList);
            for (Book book : Database.getDatabase().getCurrentUser().getFavorite()) {
                borrowedList.add(book.getTitle() + " by " + book.getAuthor());
            }
            Stage borrowStage = new Stage();
            borrowStage.setTitle("My Favorites");
            borrowStage.setScene(new Scene(borrowedListView, 400, 300));
            borrowStage.show();
        });
        btnMyHistory.setOnAction(event -> {
            //Create and show a small stage with a list of reading history books
            ObservableList<Book> readList = FXCollections.observableArrayList(Database.getDatabase().getCurrentUser().getHistory());
            ListView<Book> readListView = new ListView<>(readList);
            Stage borrowStage = new Stage();
            borrowStage.setTitle("My Reading History");
            borrowStage.setScene(new Scene(readListView, 400, 300));
            borrowStage.show();
        });

        // Create the main layout
        setPadding(new Insets(10));
        HBox hBox = new HBox(10, searchField, searchButton, sortBox);
        BorderPane.setMargin(hBox, new Insets(10));
        BorderPane.setMargin(btnBox, new Insets(10));
        hBox.setAlignment(Pos.CENTER);
        setTop(hBox);
        Label recommLbl = new Label("Recommendations");
        recommLbl.setStyle("-fx-font-weight: bold");
        Label booksLbl = new Label("All Books");
        booksLbl.setStyle("-fx-font-weight: bold");
        setCenter(new VBox(5, recommLbl,
                observableRecommendedList.isEmpty() ?
                        new Label("No Recommendations") : recommendationListView,
                booksLbl, bookListView));

        //Create logout button
        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.getScene().setRoot(new LoginPage(stage));
            }
        });
        VBox bottomBox=new VBox(5, btnBox, btnMyBox, btnLogout);
        bottomBox.setAlignment(Pos.CENTER);
        setBottom(bottomBox);
    }

    private void refreshLists() {
        observableBookList.clear();
        observableBookList.addAll(Database.getDatabase().getBooks());
        observableRecommendedList.clear();
        observableRecommendedList.addAll(Database.getDatabase().getRecommender().getRecommendations());
    }

    private void disableButtons(boolean disable) {
        btnBorrow.setDisable(disable);
        btnFavorite.setDisable(disable);
        btnInfo.setDisable(disable);
        btnReturn.setDisable(disable);
    }
}
