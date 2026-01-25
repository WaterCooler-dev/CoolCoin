module dev.watercooler.coolcoin.coolcoingui {
    requires javafx.controls;
    requires javafx.fxml;
    requires dev.watercooler.coolcoin.core;

    opens dev.watercooler.coolcoin.coolcoingui to javafx.fxml;
    exports dev.watercooler.coolcoin.coolcoingui;
}