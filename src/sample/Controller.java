package sample;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
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
    ObservableList<Detector> data;


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

    public void showTestTable(){
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
        int timeSeconds1=(int)timeSeconds;

        @Override
        public void run() {
            long currentTime;
            long endingTime;

            String a=String.valueOf(hc05.getDatat());
            String b=String.valueOf(timeSeconds);
            data = FXCollections.observableArrayList(
                    new Detector(a,b),
                    new Detector(a,b)
            );
            while (true) {
                currentTime = System.currentTimeMillis();
                try {
                    hc05.getValueOfDetector();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                timeSeconds = TimeUnit.MILLISECONDS.toSeconds(time);

                Platform.runLater(() -> {
                    table.itemsProperty().setValue(data);

                    valueof.setCellValueFactory(
                            new PropertyValueFactory<Detector, String>("nazwa")
                    );

                    timeof.setCellValueFactory(
                            new PropertyValueFactory<Detector, String>("srednia")
                    );
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
        int timeSeconds1=(int)timeSeconds;

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

        @Override
        public void run() {
            long currentTime;
            long endingTime;
            while (true) {
                currentTime = System.currentTimeMillis();
                hc05.drawTestData();
                String a=String.valueOf(hc05.getDatat());
                String b=String.valueOf(timeSeconds);
                    data = FXCollections.observableArrayList(
                         new Detector(a,b),
                         new Detector(a,b)
                );
                timeSeconds = TimeUnit.MILLISECONDS.toSeconds(time);
                Platform.runLater(() -> {
                            table.itemsProperty().setValue(data);

                            valueof.setCellValueFactory(
                                    new PropertyValueFactory<Detector, String>("value")
                            );

                            timeof.setCellValueFactory(
                                    new PropertyValueFactory<Detector, String>("time")
                            );
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
