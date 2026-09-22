package com.graphprompt.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class ConversationController {

    @FXML private VBox chatContainer;
    @FXML private ScrollPane scrollPane;

    private Parent previousRoot;

    public void setPreviousRoot(Parent root) {
        this.previousRoot = root;
    }

    public void loadConversation(List<String> mockData) {
        chatContainer.getChildren().clear();
        for (String message : mockData) {
            boolean isUser = message.startsWith("User:");
            String text = isUser ? message.substring(5).trim() : message.substring(3).trim();

            Label bubble = new Label(text);
            bubble.setWrapText(true);
            bubble.setMaxWidth(400);

            if (isUser) {
                bubble.getStyleClass().add("chat-bubble-user");
            } else {
                bubble.getStyleClass().add("chat-bubble-ai");
            }

            HBox row = new HBox(bubble);
            row.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
            chatContainer.getChildren().add(row);
        }
        
        // Auto scroll to bottom
        chatContainer.heightProperty().addListener((obs, oldVal, newVal) -> scrollPane.setVvalue(1.0));
    }

    @FXML
    private void handleBack(ActionEvent event) {
        if (previousRoot != null) {
            Scene scene = ((Node) event.getSource()).getScene();
            scene.setRoot(previousRoot);
        }
    }
}
