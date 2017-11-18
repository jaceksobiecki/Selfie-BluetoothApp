package com.selfie.app;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;



public class Controller implements Initializable {
    private byte[] frame =new byte[11];
    private byte[] sFlag = new byte[3];
    private boolean isRunning=false;
    Receive receive = new Receive();

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
    private TableView<Detector> table;
    @FXML
    private TableColumn<Detector, String> timeof;
    @FXML
    private TableColumn<Detector, String> valueof;
    /////////////////////////////////////////

    @FXML
    private void info() throws IOException {
        StartupController startupController = new StartupController();
        startupController.info="info";
        startupController.popupWindow();
    }
//////////////////////////////////////////////////////
    private HC05 hc05 = new HC05();
    ArrayList<String> list = new ArrayList<String>();

    public void start(){
        HC05send hc05Send = new HC05send();
        sFlag[0]= (byte) 0xFF;
        frame[0]=100;
        frame[1]=0;
        frame[2]=0;
        frame[3]=100;
        frame[4]=0;
        frame[5]=0;
        frame[6]=100;
        frame[7]=0;
        frame[8]=0;
        frame[9]=100;
        frame[10]=0;
        sFlag[1]=0;
        sFlag[2]=0x00;
        hc05Send.start();
        try {
            hc05.receiveData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        HC05send hc05Send = new HC05send();
        frame[0]=(byte)200;
        frame[1]=0;
        frame[2]=0;
        hc05Send.start();
        try {
            hc05.receiveData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void diagOFF(){
        HC05send hc05Send = new HC05send();
        frame[0]=111;
        frame[1]=0;
        frame[2]=0;
        hc05Send.start();
        receive.off();
    }

    @FXML
    public void diagON() {
        HC05send hc05Send = new HC05send();
        frame[0]=110;
        frame[1]=0;
        frame[2]=0;
        hc05Send.start();
        receive.on();

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
                    hc05.receiveData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                timeSeconds = TimeUnit.MILLISECONDS.toSeconds(time);

                Platform.runLater(() -> {

                    series1.getData().add(new XYChart.Data(String.valueOf(timeSeconds), hc05.getData().get(i)));
                    series2.getData().add(new XYChart.Data(String.valueOf(timeSeconds), hc05.getData().get(i+1)));
                    series3.getData().add(new XYChart.Data(String.valueOf(timeSeconds), hc05.getData().get(i+2)));
                    series4.getData().add(new XYChart.Data(String.valueOf(timeSeconds), hc05.getData().get(i+3)));
                    series5.getData().add(new XYChart.Data(String.valueOf(timeSeconds), hc05.getData().get(i+4)));
                    series6.getData().add(new XYChart.Data(String.valueOf(timeSeconds), hc05.getData().get(i+5)));
                    series7.getData().add(new XYChart.Data(String.valueOf(timeSeconds), hc05.getData().get(i+6)));
                    series8.getData().add(new XYChart.Data(String.valueOf(timeSeconds), hc05.getData().get(i+7)));

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
              //  hc05.drawTestData();
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
        receive.start();
        frame[3]=11;
    }


    public class HC05send extends Thread{
        public void run(){
            isRunning=true;
            try {
                hc05.send(frame,sFlag);
            } catch (Exception e) {
                e.printStackTrace();
            }
            isRunning=false;
        }
    }


    public class Receive extends Thread{
        private volatile boolean running = false;

        public void off() {
            running = false;
        }
        public void on(){
            running = true;
        }

        public void run() {
            while (true) {
                while (running) {
                    try {
                        hc05.receiveData();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
