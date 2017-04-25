package sample;

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

    public static double speed;
    @FXML
    public AreaChart<?,?> chart;
    public XYChart.Series series;
    @FXML
    public Slider slider;
    @FXML
    public CategoryAxis x;
    @FXML
    public NumberAxis y;
    @FXML
    public TableView table;
    @FXML
    public TableColumn<Detector, String> timeof;
    @FXML
    public TableColumn<Detector, String> valueof;

    private HC05 hc05 = new HC05();

    String[] a1 = new String[3000];

    public Controller() throws IOException {
    }


    @FXML
    private void stop(ActionEvent event) throws Exception {
        hc05.send(250);
    }

    @FXML
    private void up(ActionEvent event) throws Exception {
        hc05.send(251);
    }

    @FXML
    private void down(ActionEvent event) throws Exception {
        hc05.send(252);
    }

    @FXML
    private void left(ActionEvent event) throws Exception {
        hc05.send(253);
    }

    @FXML
    private void right(ActionEvent event) throws Exception {
        hc05.send(254);
    }

    @FXML
    private void connect(ActionEvent event) throws IOException {
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

    public void showTestTable() {

        PrintWriter zapis = null;
        try {
            zapis = new PrintWriter("C:\\Users\\Piotr Szubert\\Desktop\\TEST.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int time4=0;
        for(String y : a1){
            if(y !=null){
                zapis.println(y + "\t" + time4++);
            }
        }
        zapis.close();

        ArrayList<String> list = new ArrayList<String>();
        try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Piotr Szubert\\Desktop\\TEST.txt")))
        {

            String value;

            while ((value = br.readLine()) != null) {
                list.add(value);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Detector> lista = new ArrayList<Detector>();
        for(String l : list) {
            String[] tab = l.split("\t");
            if (tab.length > 1){
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

        Thread thread3 = new Thread(new TestTableThread());
        thread3.start();
    }

    @FXML
    public void getData(){

        series = new XYChart.Series();
        chart.getData().addAll(series);

        Thread thread2 = new Thread(new ChartThread());
        thread2.start();
    }

    public void getInfo(){
        PrintWriter zapis = null;
        try {
            zapis = new PrintWriter("C:\\Users\\Piotr Szubert\\Desktop\\TEST.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for(String y : a1){
            zapis.println(y +"\t"+ 0.5);
        }
        zapis.close();

        ArrayList<String> list = new ArrayList<String>();
        try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Piotr Szubert\\Desktop\\TEST.txt")))
        {

            String value;

            while ((value = br.readLine()) != null) {
                list.add(value);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Detector> lista = new ArrayList<Detector>();
        for(String l : list) {
            String[] tab = l.split("\t");
            if (tab.length > 1){
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

        Thread thread3 = new Thread(new TableThread());
        thread3.start();
    }

    public class ChartThread implements Runnable{
        long time = 0;
        long timeSeconds;
        int timeSeconds1=(int)timeSeconds;

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

                    series.getData().add(new XYChart.Data(timeSeconds, hc05.getData()));

                    if(series.getData().size() > 10){
                        series.getData().remove(0,1);
                    }
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

    public class TableThread implements Runnable{
        long time = 0;
        long timeSeconds;
        int i=0;

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
                    a1[i] = String.valueOf(hc05.getData());
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
        long time=0;
        long timeSeconds;
        int i=0;

        @Override
        public void run() {

            long currentTime;
            long endingTime;
            while (true) {
                currentTime = System.currentTimeMillis();
                hc05.drawTestData();
                timeSeconds = TimeUnit.MILLISECONDS.toSeconds(time);

                Platform.runLater(() -> {
                    series.getData().add(new XYChart.Data(String.valueOf(timeSeconds), hc05.getData()));
                    if(series.getData().size() > 10){
                        series.getData().remove(0,1);
                    }
                    a1[i] = String.valueOf(hc05.getData());
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

    public class TestTableThread implements Runnable {
        long time=0;
        long timeSeconds;
        int i =0;

        @Override
        public void run() {
            long currentTime;
            long endingTime;

            while (true) {

                    currentTime = System.currentTimeMillis();
                hc05.drawTestData();


                timeSeconds = TimeUnit.MILLISECONDS.toSeconds(time);
                Platform.runLater(() -> {
                    a1[i]=String.valueOf(hc05.getData());
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
                try {
                    hc05.send((int) speed);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

}
