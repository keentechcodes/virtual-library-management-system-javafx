package com.smart.javafx;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginPage extends BorderPane {

    public LoginPage(Stage stage) {
        // Create input fields
        VBox fieldsBox = new VBox(10);
        fieldsBox.setAlignment(Pos.CENTER);
        fieldsBox.setMaxWidth(300);
        Label label = new Label("Provide your info to access\nVirtual Library");
        label.setStyle("-fx-font-weight: bold;-fx-font-size: 20;-fx-text-alignment: center;");
        Label nameLbl = new Label("Name");
        nameLbl.setMinWidth(80);
        TextField nameField = new TextField();
        nameField.setPromptText("e.g. John Smith");
        HBox nameBox = new HBox(5, nameLbl, nameField);
        Label contactLbl = new Label("ID Number#");
        contactLbl.setMinWidth(80);
        TextField contactField = new TextField();
        contactField.setPromptText("e.g. 12345678");
        HBox contactBox = new HBox(5, contactLbl, contactField);
        Button btnLogin = new Button("Login");
        fieldsBox.getChildren().addAll(label, nameBox, contactBox, btnLogin);

        btnLogin.setOnAction(event -> {
            if (nameField.getText().trim().isEmpty() || contactField.getText().trim().isEmpty()) {
                Database.showMessage("Both fields are required!");
                return;
            }
            User user = Database.getDatabase().login(nameField.getText(), contactField.getText());
            // Set current user
            Database.getDatabase().setCurrentUser(user);
            stage.getScene().setRoot(new MainPage(stage));
        });

        setCenter(fieldsBox);
    }
}
