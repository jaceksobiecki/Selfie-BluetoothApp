package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable{
    private static double speed;
    @FXML
    Slider slider;

    @FXML
    private void stop(ActionEvent event) throws Exception {
        HC05.send(250);
    }
    @FXML
    private void up(ActionEvent event) throws Exception {
        HC05.send(251);
    }
    @FXML
    private void down(ActionEvent event) throws Exception {
        HC05.send(252);
    }
    @FXML
    private void left(ActionEvent event) throws Exception {
        HC05.send(253);
    }
    @FXML
    private void right(ActionEvent event) throws Exception {
        HC05.send(254);
    }
    @FXML
    private void connect(ActionEvent event) throws IOException {
        new HC05().go();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb){

        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                                Number oldValue, Number newValue) {
                speed = (double)newValue;
                try {
                    HC05.send((int)speed);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public void getData() throws IOException {
        HC05.getValueOfDetector();
    }
}
