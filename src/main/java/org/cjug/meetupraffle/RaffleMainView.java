package org.cjug.meetupraffle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.cjug.meetupraffle.library.ApplicationConfiguration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Freddy on 3/22/2014.
 * Implements the main raffle view
 */
public class RaffleMainView extends Application implements Initializable {


    @Override
    public void start(Stage stage) throws Exception {

//        FXMLLoader loader = new FXMLLoader(new File("src/main/java/org/cjug/meetupraffle/RaffleMainView.fxml").toURI().toURL());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/RaffleMainView.fxml"));
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("/src/main/java/org/cjug/meetupraffle/RaffleMainView.fxml"));

        Parent root = loader.load();
        stage.setTitle("Meetup Raffle");
        stage.setScene(new Scene(root, 800, 600));
        //stage.getOwner().setX();
        RaffleMainController controller = loader.getController();

        ApplicationConfiguration.Dimension dimension = controller.getConfiguration().getDimension();
        if (dimension != null) {
            stage.setX(dimension.getX());
            stage.setY(dimension.getY());
            stage.setWidth(dimension.getWidth());
            stage.setHeight(dimension.getHeight());
        }

        stage.setOnCloseRequest(e -> controller.stop(stage));
        stage.show();
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public static void main(String[] args) throws IOException {
        launch(RaffleMainView.class);
    }

}
