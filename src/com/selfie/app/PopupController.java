package com.selfie.app;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Jacek on 14.05.2017.
 */
public class PopupController implements Initializable{
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label label;

    @FXML
    private void close(){
        anchorPane.getScene().getWindow().hide();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        label.setText(StartupController.getInfo());
    }
}
