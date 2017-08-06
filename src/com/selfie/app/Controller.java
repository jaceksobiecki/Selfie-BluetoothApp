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
    private byte[] frame =new byte[3];
    private boolean isRunning=false;

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

    public void startData1(){
        HC05send hc05Send = new HC05send();
        frame[0]=0x01;
        frame[1]=0;
        frame[2]=0;
        hc05Send.start();
    }

    public void startData2(){
        HC05send hc05Send = new HC05send();
        frame[0]=0x02;
        frame[1]=0;
        frame[2]=0;
        hc05Send.start();
    }

    public void stopData(){
        HC05send hc05Send = new HC05send();
        frame[0]=0x05;
        frame[1]=0;
        frame[2]=0;
        hc05Send.start();
    }

    public void diagramOFF(){
        HC05send hc05Send = new HC05send();
        frame[0]=0x04;
        frame[1]=0;
        frame[2]=0;
        hc05Send.start();
    }

    @FXML
    public void diagramON() {

        HC05send hc05Send = new HC05send();
        frame[0]=0x03;
        frame[1]=0;
        frame[2]=0;
        hc05Send.start();

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
                    hc05.recieveData();
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

    }


    public class HC05send extends Thread{
        public void run(){
            isRunning=true;
            try {
                hc05.send(frame);
            } catch (Exception e) {
                e.printStackTrace();
            }
            isRunning=false;
        }
    }
}
