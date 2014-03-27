package org.cjug.meetupraffle.configuration;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.cjug.meetupraffle.RaffleMainController;
import org.cjug.meetupraffle.connector.MeetupConnector;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Freddy on 3/22/2014.
 * Configuration Settings
 */
public class GeneralConfigurationController implements Initializable {
    private RaffleMainController raffleMainController;


    @FXML
    private TextField apiTextField;

    @FXML
    private TextField groupNameTextField;

    public GeneralConfigurationController() {

    }


    public void setRaffleMainController(RaffleMainController raffleMainController) {
        this.raffleMainController = raffleMainController;
        loadInfo();
    }

    private void saveConfiguration() {
        raffleMainController.setMeetupConfiguration(new MeetupConnector.Configuration(apiTextField.getText(), groupNameTextField.getText()));
    }

    public void loadInfo() {
        String apiKey = raffleMainController.getConfiguration().getMeetupConnectorConfiguration().getKey();
        if (apiKey != null) apiTextField.setText(apiKey);
        String groupUrl = raffleMainController.getConfiguration().getMeetupConnectorConfiguration().getGroupUrl();
        if (groupUrl != null) groupNameTextField.setText(groupUrl);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void handleSaveButton(javafx.event.ActionEvent actionEvent) {
        saveConfiguration();
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    public void handleCancelButton(javafx.event.ActionEvent actionEvent) {
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }
}
