package com.selfie.app;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StartupController implements Initializable {
    private static String info;
    private static boolean problem = false;
    @FXML
    AnchorPane anchorPane;
    private HC05 hc05 = new HC05();
    private HC05Search hc05Search = new HC05Search();
    private HC05go hc05go = new HC05go();
    private boolean newListVisible = false;
    @FXML
    private ListView<String> listViewLast = new ListView<>();
    @FXML
    private ListView<String> listViewNew = new ListView<>();
    @FXML
    private Label newDevicesLabel;
    @FXML
    private Label lastDeviceLabel;

    public static String getInfo() {
        return info;
    }

    public static void setInfo(String info) {
        StartupController.info = info;
    }

    public static void setProblem(boolean problem) {
        StartupController.problem = problem;
    }

    @FXML
    private void chooseDevice() throws IOException, InterruptedException {
        if (listViewLast.isVisible()) {
            if (newListVisible) {
                if (listViewNew.getSelectionModel().isEmpty())
                    connect();
                else {
                    if (listViewNew.getSelectionModel().getSelectedItem().matches("HC.*")) {
                        hc05.saveUrl();
                        connect();
                    } else {
                        info = "Unsupported device";
                        try {
                            popupWindow();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else
                connect();

        } else {
            if (listViewNew.getSelectionModel().isEmpty()) {
                info = "Choose device";
                try {
                    popupWindow();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                if (listViewNew.getSelectionModel().getSelectedItem().matches("HC.*")) {
                    hc05.saveUrl();
                    connect();
                } else {
                    info = "Unsupported device";
                    try {
                        popupWindow();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void connect() throws IOException, InterruptedException {
        //hc05Search.kill();
        //hc05Search.stop();
        hc05go.start();

    }

    public void newWindow() throws IOException {
        if (problem)
            popupWindow();
        else {
            Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Selfie App");
            stage.setScene(new Scene(root));
            stage.show();
            anchorPane.getScene().getWindow().hide();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent t) {
                    Platform.exit();
                    System.exit(0);
                }
            });
        }
    }

    public void popupWindow() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("PopupWindow.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Selfie App");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            hc05.readUrl();
        } catch (IOException e) {
            lastDeviceLabel.setVisible(false);
            listViewLast.setVisible(false);
            newDevicesLabel.setLayoutY(120);
            listViewNew.setLayoutY(145);
            listViewNew.setPrefHeight(200);
        }
        ObservableList<String> list1 = FXCollections.observableArrayList(hc05.getURL());
        listViewLast.setItems(list1);
        listViewLast.getSelectionModel().select(0);

        listViewLast.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (listViewLast.getSelectionModel().isEmpty() == false)
                    listViewNew.getSelectionModel().clearSelection();
            }
        });
        listViewNew.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (listViewNew.getSelectionModel().isEmpty() == false)
                    listViewLast.getSelectionModel().clearSelection();
            }
        });

        hc05Search.start();
    }

    public class HC05Search extends Thread {
        private volatile boolean isRunning = true;

        public void run() {
            while (isRunning) {
                try {
                    hc05.search();
                    ObservableList<String> list2 = FXCollections.observableArrayList(hc05.getDevices());
                    listViewNew.setItems(list2);
                    Platform.runLater(() -> {
                        if (listViewLast.isVisible() == false)
                            newDevicesLabel.setText("Devices Found");
                        else
                            newDevicesLabel.setText("New Devices:");
                    });
                    newListVisible = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isRunning = false;
            }
        }

        public void kill() {
            isRunning = false;
        }
    }

    public class HC05go extends Thread {
        public void run() {
            try {
                hc05.go();
            } catch (Exception e) {
                System.out.println("mamy problem");
            }
            Platform.runLater(() -> {
                try {
                    newWindow();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            System.out.println("koniec");
        }
    }
}
