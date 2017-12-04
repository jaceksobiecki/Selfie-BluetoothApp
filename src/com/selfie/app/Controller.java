package com.selfie.app;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.PointLight;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;



public class Controller implements Initializable {
    private short[] frame =new short[8];
    private byte[] sFlag = new byte[3];
    private int[] JFlags = new int [8];
    private int[] STMFlags = new int [8];
    private boolean isRunning=false;
    Receive receive = new Receive();
    @FXML
    private Circle light11;
    @FXML
    private Circle light12;
    @FXML
    private TextField velocity;
    @FXML
    private Label angle1;
    @FXML
    private Label angle2;
    @FXML
    private TextField label1;
    @FXML
    private TextField label2;
    @FXML
    private TextField label3;
    @FXML
    private TextField label4;
    @FXML
    private TextField label5;
    @FXML
    private TextField label6;
    @FXML
    private TextField label7;
    @FXML
    private TextField label8;
    @FXML
    private AreaChart<?, ?> chart;
    private XYChart.Series series1;
    private XYChart.Series series2;
    @FXML
    private TableView<Detector> table;
    @FXML
    private TableColumn<Detector, String> timeof;
    @FXML
    private TableColumn<Detector, String> valueof;

    @FXML
    private Label servoLbl;
    @FXML
    private Label sharp1;
    @FXML
    private Label sharp2;
    @FXML
    private Label sharp3;
    @FXML
    private Label sharp4;
    @FXML
    private Label sharp5;
    @FXML
    private Label mapSpot;
    @FXML
    private Label instruction;
    @FXML
    private Label distanceObj;
    @FXML
    private Label batteryLbl;
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
        sFlag[0]=0x01;
        frame[0]=12;
        frame[1]=1200;
        frame[2]=100;
        frame[3]=50;
        frame[4]=40;
        frame[5]=200;
        frame[6]=11;
        frame[7]=5;
        sFlag[1]=0;
        sFlag[2]=0x01;
        hc05Send.start();
        try {
            hc05.receiveData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(){
        HC05send hc05Send = new HC05send();

        hc05Send.start();
        series1 = new XYChart.Series();
        chart.getData().addAll(series1);

        Thread thread=new Thread(new JDataThread());
        thread.start();
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
        receive.off();
    }

    @FXML
    public void diagON() {
        receive.on();

    }

    public class JDataThread implements Runnable {
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
                JFlags=hc05.getBit(hc05.getJetsonData()[8]);
                Platform.runLater(() -> {
                    series1.getData().add(new XYChart.Data(String.valueOf(timeSeconds), hc05.getJetsonData()[1]));
                    System.out.println(hc05.getJetsonData()[1]);
                    if (series1.getData().size() > 10) {
                        series1.getData().remove(0, 1);
                    }


                    if(JFlags[0]==0x01){
                        light11.setFill(Color.GREEN);
                    }
                    else{
                        light11.setFill(Color.RED);
                    }
                    if(JFlags[1]==0x01){
                        light12.setFill(Color.GREEN);
                    }
                    else{
                        light12.setFill(Color.RED);
                    }
                    angle1.setText(String.valueOf(hc05.getJetsonData()[2]));
                    angle2.setText(String.valueOf(hc05.getJetsonData()[3]));
                    list.add(String.valueOf(hc05.getJetsonData()[i]+"\t"+String.valueOf(timeSeconds)));
                    ArrayList<Detector> lista = new ArrayList<Detector>();
                    for (String l : list) {
                        String[] tab = l.split("\t");
                        if (tab.length > 1) {
                            lista.add(new Detector(tab[0], tab[1]));
                        }
                    }
                    /*
                    ObservableList<Detector> dane = FXCollections.observableArrayList(lista);

                    valueof.setCellValueFactory(
                            new PropertyValueFactory<Detector, String>("value")
                    );

                    timeof.setCellValueFactory(
                            new PropertyValueFactory<Detector, String>("time")
                    );
                    table.itemsProperty().setValue(dane);
*/

                    //map spot
                    if(JFlags[2]==0 && JFlags[3]==0){
                        mapSpot.setText("Droga wolna");
                    } else if(JFlags[2]==0 && JFlags[3]==1){
                        mapSpot.setText("Początek skrzyżowania");
                    } else if(JFlags[2]==1 && JFlags[3]==0){
                        mapSpot.setText("Początek parkingu");
                    } else if(JFlags[2]==1 && JFlags[3]==1){
                        mapSpot.setText("Początek przejścia");
                    } else{
                        mapSpot.setText("błąd flag");
                    }
                    //instruction
                    if(JFlags[2]==0 && JFlags[3]==1){
                        if(JFlags[4]==0 && JFlags[5]==0){
                            instruction.setText("STOP");
                        } else if(JFlags[4]==0 && JFlags[5]==1){
                            instruction.setText("Reguła prawej");
                        } else if(JFlags[4]==1 && JFlags[5]==0){
                            instruction.setText("Ustąp pierwszeństwa");
                        } else if(JFlags[4]==1 && JFlags[5]==1){
                            instruction.setText("Pierwszeństwo");
                        } else{
                            instruction.setText("błąd flag");
                        }
                    }
                    //distance from map spot
                    distanceObj.setText(String.valueOf(hc05.getJetsonData()[3]));

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

    public class STMDataThread implements Runnable {
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
                STMFlags=hc05.getBit(hc05.getJetsonData()[9]);
                Platform.runLater(() -> {
                    //servo
                    servoLbl.setText(String.valueOf(hc05.getSTMData()[1]));
                    //sharps
                    sharp1.setText(String.valueOf(hc05.getSTMData()[3]));
                    sharp2.setText(String.valueOf(hc05.getSTMData()[4]));
                    sharp3.setText(String.valueOf(hc05.getSTMData()[5]));
                    sharp4.setText(String.valueOf(hc05.getSTMData()[6]));
                    sharp5.setText(String.valueOf(hc05.getSTMData()[7]));
                    //battery
                    batteryLbl.setText(String.valueOf(hc05.getSTMData()[2]));

/*
                    list.add(String.valueOf(hc05.getJetsonData()[i]+"\t"+String.valueOf(timeSeconds)));
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

*/
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
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
