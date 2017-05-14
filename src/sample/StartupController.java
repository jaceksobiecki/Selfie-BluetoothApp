package sample;

import javafx.application.Platform;
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
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Jacek on 13.05.2017.
 */
public class StartupController implements Initializable {
    private HC05 hc05 = new HC05();
    @FXML
    private ListView<String> listViewLast = new ListView<>();
    @FXML
    private ListView<String> listViewNew = new ListView<>();
    @FXML
    private Label newDevicesLabel;

    @FXML
    private void connect() throws IOException {
        if(listViewNew.getSelectionModel().getSelectedIndex()==0)
            hc05.saveUrl();
        hc05.go();
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Selfie Bluetooth App");
        stage.setScene(new Scene(root));
        stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                try {
                    HC05.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Platform.exit();
                System.exit(0);
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            hc05.readUrl();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObservableList<String> list1 = FXCollections.observableArrayList(hc05.getURL());
        listViewLast.setItems(list1);
        //listViewLast.getSelectionModel().select(0);
        HC05Search hc05Search = new HC05Search();
        hc05Search.start();
    }

    public class HC05Search extends Thread {
        public void run() {
            try {
                hc05.search();
                ObservableList<String> list2 = FXCollections.observableArrayList(hc05.getDevices());
                listViewNew.setItems(list2);
                Platform.runLater(() -> {
                    newDevicesLabel.setText("New Devices:");
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
