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
    private byte[] dataButton=new byte[3];
    private boolean isRunning=false;

    private static double speed;
    @FXML
    private AnchorPane pane;
    @FXML
    private Button stopBtn;
    @FXML
    private AreaChart<?, ?> chart;
    private XYChart.Series series1;
    private XYChart.Series series2;
    private XYChart.Series series3;
    private XYChart.Series series4;
    private XYChart.Series series5;
    private XYChart.Series series6;
    private XYChart.Series series7;
    private XYChart.Series series8;
    @FXML
    private Slider slider;
    @FXML
    private CategoryAxis x;
    @FXML
    private NumberAxis y;
    @FXML
    private TableView<Detector> table;
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

    public void startData1(){
        HC05button hc05button = new HC05button();
        dataButton[0]=0x01;
        dataButton[1]=0;
        dataButton[2]=0;
        hc05button.start();
    }

    public void startData2(){
        HC05button hc05button = new HC05button();
        dataButton[0]=0x02;
        dataButton[1]=0;
        dataButton[2]=0;
        hc05button.start();
    }

    public void stopData(){
        HC05button hc05button = new HC05button();
        dataButton[0]=0x05;
        dataButton[1]=0;
        dataButton[2]=0;
        hc05button.start();
    }

    public void diagramOFF(){
        HC05button hc05button = new HC05button();
        dataButton[0]=0x04;
        dataButton[1]=0;
        dataButton[2]=0;
        hc05button.start();
    }

    @FXML
    public void diagramON() {

        HC05button hc05button = new HC05button();
        dataButton[0]=0x03;
        dataButton[1]=0;
        dataButton[2]=0;
        hc05button.start();

        series1 = new XYChart.Series();
        series1.setName("Czujnik 1");
        series2 = new XYChart.Series();
        series2.setName("Sharp 1");
        series3 = new XYChart.Series();
        series3.setName("Sharp 2");
        series4 = new XYChart.Series();
        series4.setName("Sharp 3");
        series5 = new XYChart.Series();
        series5.setName("Czujnik 1");
        series6 = new XYChart.Series();
        series6.setName("Sharp 1");
        series7 = new XYChart.Series();
        series7.setName("Sharp 2");
        series8 = new XYChart.Series();
        series8.setName("Sharp 3");
        chart.getData().addAll(series1,series2,series3,series4,series5,series6,series7,series8);

        Thread thread2 = new Thread(new DataThread());
        thread2.start();
    }


    //Test
    @FXML
    public void showTestData() {

        series1 = new XYChart.Series();
        chart.getData().addAll(series1);

        Thread thread2 = new Thread(new TestDataThread());
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

                    series1.getData().add(new XYChart.Data(String.valueOf(timeSeconds), hc05.getData().get(i)));
                    series2.getData().add(new XYChart.Data(String.valueOf(timeSeconds), hc05.getData().get(i+2)));
                    series3.getData().add(new XYChart.Data(String.valueOf(timeSeconds), hc05.getData().get(i+4)));
                    series4.getData().add(new XYChart.Data(String.valueOf(timeSeconds), hc05.getData().get(i+6)));
                    series5.getData().add(new XYChart.Data(String.valueOf(timeSeconds), hc05.getData().get(i+8)));
                    series6.getData().add(new XYChart.Data(String.valueOf(timeSeconds), hc05.getData().get(i+10)));
                    series7.getData().add(new XYChart.Data(String.valueOf(timeSeconds), hc05.getData().get(i+12)));
                    series8.getData().add(new XYChart.Data(String.valueOf(timeSeconds), hc05.getData().get(i+14)));


                    if (series1.getData().size() > 10) {
                        series1.getData().remove(0, 1);
                    }
                    if (series2.getData().size() > 10) {
                        series2.getData().remove(0, 1);
                    }
                    if (series3.getData().size() > 10) {
                        series3.getData().remove(0, 1);
                    }
                    if (series4.getData().size() > 10) {
                        series4.getData().remove(0, 1);
                    }
                    if (series5.getData().size() > 10) {
                        series5.getData().remove(0, 1);
                    }
                    if (series6.getData().size() > 10) {
                        series6.getData().remove(0, 1);
                    }
                    if (series7.getData().size() > 10) {
                        series7.getData().remove(0, 1);
                    }
                    if (series8.getData().size() > 10) {
                        series8.getData().remove(0, 1);
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
                    series1.getData().add(new XYChart.Data(String.valueOf(timeSeconds), hc05.getData().get(i)));
                    if (series1.getData().size() > 10) {
                        series1.getData().remove(0, 1);
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
                    System.out.println(time);
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


    public class HC05button extends Thread{
        public void run(){
            isRunning=true;
            try {
                hc05.send1(dataButton);
            } catch (Exception e) {
                e.printStackTrace();
            }
            isRunning=false;
        }
    }
}
