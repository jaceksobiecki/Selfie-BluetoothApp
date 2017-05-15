package com.selfie.app;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
    private int dataSend;
    private boolean isRunning=false;

    private static double speed;
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

    private HC05 hc05 = new HC05();
    String[] a1 = new String[300];


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

    @FXML
    private void connect(ActionEvent event) throws IOException{
        hc05.go();
    }

    //Test
    @FXML
    public void showTestChart() {

        series = new XYChart.Series();
        chart.getData().addAll(series);


        Thread thread2 = new Thread(new TestChartThread());
        thread2.start();
    }
    @FXML
    public void showTable() {

        PrintWriter zapis = null;
        try {
            zapis = new PrintWriter("TEST.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int time4 = 0;
        for (String y : a1) {
            if (y != null) {
                zapis.println(y);
            }
        }
        zapis.close();

        ArrayList<String> list = new ArrayList<String>();
        try (BufferedReader br = new BufferedReader(new FileReader("TEST.txt"))) {

            String value;

            while ((value = br.readLine()) != null) {
                list.add(value);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

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


    }

    @FXML
    public void getData() {

        series = new XYChart.Series();
        chart.getData().addAll(series);

        Thread thread2 = new Thread(new ChartThread());
        thread2.start();
    }


    public class ChartThread implements Runnable {
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
                    String dataS=String.valueOf(hc05.getData().get(i));;
                    String timeS=String.valueOf(timeSeconds);
                    a1[i]=dataS+"\t"+timeS;
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
    public class TestChartThread implements Runnable {
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
                    String dataS=String.valueOf(hc05.getData().get(i));;
                    String timeS=String.valueOf(timeSeconds);
                    a1[i]=dataS+"\t"+timeS;
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
