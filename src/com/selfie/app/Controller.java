package com.selfie.app;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;



public class Controller implements Initializable {
    private int dataSend;
    private boolean isRunning=false;

    private static double speed;
    @FXML
    private AnchorPane pane;
    @FXML
    private Button stopBtn;
    @FXML
    private AreaChart<?, ?> chart;
    private XYChart.Series series;
    @FXML
    private Slider slider;
    @FXML
    private CategoryAxis x;
    @FXML
    private NumberAxis y;
    @FXML
    private TableView table;
    @FXML
    private TableColumn<Detector, String> timeof;
    @FXML
    private TableColumn<Detector, String> valueof;
    /////////////////////////////////////////
    @FXML
    private TextField textField;
    @FXML
    private Button sendBtn;
    @FXML
    private void sendBtn(){
        try {
            dataSend = Integer.valueOf(textField.getText());
            System.out.println(dataSend);
            new HC05send().start();
        }catch(Exception e){
            StartupController startupController = new StartupController();
            startupController.info="wrong data, choose from 0-255";
            try {
                startupController.popupWindow();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    @FXML
    private void info() throws IOException {
        StartupController startupController = new StartupController();
        startupController.info="up - 251, stop - 250, down - 252, left - 253, right - 254, slider 0-200";
        startupController.popupWindow();
    }
//////////////////////////////////////////////////////
    private HC05 hc05 = new HC05();
    ArrayList<String> list = new ArrayList<String>();


    @FXML
    private void stop(ActionEvent event) throws Exception {
        HC05send hc05send = new HC05send();
        dataSend=250;
        hc05send.start();
    }

    @FXML
    private void up(ActionEvent event) throws Exception {
        HC05send hc05send = new HC05send();
        dataSend=251;
        hc05send.start();
    }

    @FXML
    private void down(ActionEvent event) throws Exception {
        HC05send hc05send = new HC05send();
        dataSend=252;
        hc05send.start();
    }

    @FXML
    private void left(ActionEvent event) throws Exception {
        HC05send hc05send = new HC05send();
        dataSend=253;
        hc05send.start();
    }

    @FXML
    private void right(ActionEvent event) throws Exception {
        HC05send hc05send = new HC05send();
        dataSend=254;
        hc05send.start();
    }

    //Test
    @FXML
    public void showTestData() {

        series = new XYChart.Series();
        chart.getData().addAll(series);

        Thread thread2 = new Thread(new TestDataThread());
        thread2.start();
    }

    @FXML
    public void getData() {

        series = new XYChart.Series();
        chart.getData().addAll(series);

        Thread thread2 = new Thread(new DataThread());
        thread2.start();
    }


    public class DataThread implements Runnable {
        long time = 0;
        long timeSeconds;
        int i =0;

        @Override
        public void run() {
            long currentTime;
            long endingTime;
            while (true) {
                currentTime = System.currentTimeMillis();
                try {
                    hc05.getValueOfDetector();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                timeSeconds = TimeUnit.MILLISECONDS.toSeconds(time);

                Platform.runLater(() -> {

                    series.getData().add(new XYChart.Data(String.valueOf(timeSeconds), hc05.getData().get(i)));
                    if (series.getData().size() > 10) {
                        series.getData().remove(0, 1);
                    }

                    list.add(String.valueOf(hc05.getData().get(i)+"\t"+String.valueOf(timeSeconds)));
                    ArrayList<Detector> lista = new ArrayList<Detector>();
                    for (String l : list) {
                        String[] tab = l.split("\t");
                        if (tab.length > 1) {
                            lista.add(new Detector(tab[0], tab[1]));
                        }
                    }
                    ObservableList<Detector> dane = FXCollections.observableArrayList(lista);

                    valueof.setCellValueFactory(
                            new PropertyValueFactory<Detector, String>("value")
                    );

                    timeof.setCellValueFactory(
                            new PropertyValueFactory<Detector, String>("time")
                    );
                    table.itemsProperty().setValue(dane);
                    i++;
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                endingTime = System.currentTimeMillis();
                time += endingTime - currentTime;
            }
        }
    }

    //Test
    public class TestDataThread implements Runnable {
        long time = 0;
        long timeSeconds;
        int i = 0;

        @Override
        public void run() {

            long currentTime;
            long endingTime;

            while (true) {
                currentTime = System.currentTimeMillis();
                hc05.drawTestData();
                timeSeconds = TimeUnit.MILLISECONDS.toSeconds(time);
                Platform.runLater(() -> {
                    series.getData().add(new XYChart.Data(String.valueOf(timeSeconds), hc05.getData().get(i)));
                    if (series.getData().size() > 10) {
                        series.getData().remove(0, 1);
                    }

                    list.add(String.valueOf(hc05.getData().get(i)+"\t"+String.valueOf(timeSeconds)));
                    ArrayList<Detector> lista = new ArrayList<Detector>();
                    for (String l : list) {
                        String[] tab = l.split("\t");
                        if (tab.length > 1) {
                            lista.add(new Detector(tab[0], tab[1]));
                        }
                    }
                    ObservableList<Detector> dane = FXCollections.observableArrayList(lista);

                    valueof.setCellValueFactory(
                            new PropertyValueFactory<Detector, String>("value")
                    );

                    timeof.setCellValueFactory(
                            new PropertyValueFactory<Detector, String>("time")
                    );
                    table.itemsProperty().setValue(dane);
                    i++;
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                endingTime = System.currentTimeMillis();

                time += endingTime - currentTime;
            }
        }
    }



    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pane.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                slider.requestFocus();
                if(event.getCode() == KeyCode.W){
                    HC05send hc05send = new HC05send();
                    dataSend=251;
                    hc05send.start();
                }
                else if(event.getCode() == KeyCode.S){
                    HC05send hc05send = new HC05send();
                    dataSend=252;
                    hc05send.start();
                }
                else if(event.getCode() == KeyCode.A){
                    HC05send hc05send = new HC05send();
                    dataSend=253;
                    hc05send.start();
                }
                else if(event.getCode() == KeyCode.D){
                    HC05send hc05send = new HC05send();
                    dataSend=254;
                    hc05send.start();
                }
            }
        });
        pane.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.W){
                    HC05send hc05send = new HC05send();
                    dataSend=250;
                    hc05send.start();
                }
                else if(event.getCode() == KeyCode.S){
                    HC05send hc05send = new HC05send();
                    dataSend=250;
                    hc05send.start();
                }
                else if(event.getCode() == KeyCode.A){
                    HC05send hc05send = new HC05send();
                    dataSend=250;
                    hc05send.start();
                }
                else if(event.getCode() == KeyCode.D){
                    HC05send hc05send = new HC05send();
                    dataSend=250;
                    hc05send.start();
                }
            }
        });

        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                                Number oldValue, Number newValue) {
                speed = (double) newValue;
                dataSend=(int)speed;
                if(!isRunning)
                    new HC05send().start();
            }
        });
    }

    public class HC05send extends Thread{
        public void run(){
            isRunning=true;
                try {
                    hc05.send(dataSend);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isRunning=false;
        }
    }

}
