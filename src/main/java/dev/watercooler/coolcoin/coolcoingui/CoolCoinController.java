package dev.watercooler.coolcoin.coolcoingui;

import dev.watercooler.coolcoin.Block;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CoolCoinController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
        System.out.println(new Block("0"));
    }
}
