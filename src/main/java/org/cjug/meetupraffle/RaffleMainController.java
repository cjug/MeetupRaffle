package org.cjug.meetupraffle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.cjug.meetupraffle.configuration.GeneralConfigurationController;
import org.cjug.meetupraffle.connector.CachedConnector;
import org.cjug.meetupraffle.connector.ConnectorFactory;
import org.cjug.meetupraffle.connector.MeetupConnector;
import org.cjug.meetupraffle.connector.QueryResult;
import org.cjug.meetupraffle.library.ApplicationConfiguration;
import org.cjug.meetupraffle.library.Event;
import org.cjug.meetupraffle.library.Member;

import java.awt.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Freddy on 3/22/2014.
 * Controller for Meetup Raffle
 */
public class RaffleMainController extends Observable implements Observer, Initializable {
    public TableColumn<Member, String> idColumn;
    public TableColumn<Member, String> nameColumn;
    public TableView<Member> membersTable;
    public ImageView memberView;
    public static RaffleMainController instance;
    public Label nameLabel;
    public Button onlineButton;
    public Button offlineButton;
    Random random = new Random();

    @FXML
    Parent root;

    @FXML
    private ComboBox<Event> eventComboBox;

    @FXML
    private TextArea messageArea;


    private final RaffleMainModel model = new RaffleMainModel();
    ApplicationConfiguration configuration;
    CachedConnector connector = null;
//    public final static RaffleMainController instance = new RaffleMainController();
    BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    ThreadPoolExecutor executor = new ThreadPoolExecutor(2,2,1000, TimeUnit.MILLISECONDS, queue);


    public RaffleMainController() {
        executor.prestartAllCoreThreads();
        instance = this;

    }

    public void start() {
        // launch proper app.
        loadConfiguration();
        createConnector();
        updateOnlineView();
    }

    private void createConnector() {
        if (connector != null) {
            connector.stop();
        }
        connector = (CachedConnector) ConnectorFactory.create(configuration.getMeetupConnectorConfiguration(),configuration.isOnline());
        connector.start();
        queue.add(this::populateModel);
        connector.setOnlineListener(() -> Platform.runLater(this::updateOnlineView));
    }

    private void updateOnlineView() {
        if (connector.isOnline()) {
            onlineButton.setStyle("-fx-base: #7DEB79;");
            offlineButton.setStyle(null);
        } else {
            offlineButton.setStyle("-fx-base: #F06B4D;");
            onlineButton.setStyle(null);
        }
        configuration.setOnline(connector.isOnline());
        queue.add(this::populateModel);
    }

    private void populateModel() {
        Collection<Event> events = new ArrayList<>();
        QueryResult result = connector.getEvents(events, p -> true); // get all events.
        if (result.getStatus()== QueryResult.Status.FAILURE) {
            model.setFailureMessage(result.getReason());
        } else {
            model.setFailureMessage(null);
        }
        model.setEvents(events);
    }

    private void loadConfiguration() {
        configuration = new ApplicationConfiguration();
        configuration.load();
    }

    public void stop(Stage stage) {
        model.stop();
        if (connector != null) {
            connector.stop();
        }
        executor.shutdown();
        try {
            executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        configuration.setDimension(new ApplicationConfiguration.Dimension((int) stage.getX(), (int) stage.getY(), (int) stage.getWidth(), (int) stage.getHeight()));
        saveConfiguration();

    }

    private void saveConfiguration() {
        configuration.save();
    }

    public ApplicationConfiguration getConfiguration() {
        return configuration;
    }

    public void setMeetupConfiguration(MeetupConnector.Configuration configuration) {
        this.configuration.setMeetupConnectorConfiguration (configuration);
        saveConfiguration();
        createConnector();
    }

    public void loadMembers(Event event) {
        queue.add(() -> loadMembersImpl(event));
    }

    private void loadMembersImpl(Event event) {
        model.setMembers(Collections.emptyList());
        Collection<Member> members = new ArrayList<>();
        QueryResult result = connector.getMembers(members, event);
        if (result.getStatus()== QueryResult.Status.FAILURE) {
            model.setFailureMessage(result.getReason());
        } else {
            model.setFailureMessage(null);
        }
        model.setMembers(members);
    }

    public void retrievePhoto(Member member) {
        queue.add(() -> {
            QueryResult result = connector.retrievePhoto (member);
            if (result.getStatus() == QueryResult.Status.FAILURE) {
                model.setFailureMessage(result.getReason());
            } else {
                model.memberPhotoUpdated(member);
            }
        });
    }


    @FXML
    public void configure() {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GeneralConfigurationView.fxml"));
            Parent root = loader.load();
            stage.setTitle("Meetup Raffle - Configuration");
            Scene scene = new Scene(root, 388,133);
            stage.setScene(scene);
            GeneralConfigurationController controller = loader.getController();
            controller.setRaffleMainController(this);
            Point location = MouseInfo.getPointerInfo().getLocation();
            stage.setX(location.getX());
            stage.setY(location.getY()+20);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayMember() {
        Member member = membersTable.getSelectionModel().getSelectedItem();

        if (member == null) {
            memberView.setImage(null);
            nameLabel.setText(null);
            return;
        }
        nameLabel.setText(member.getName());
        if (member.isPhotoRetrieved() && member.getImage() != null) {
            loadPhoto(member.getImage());
        } else if (!member.isPhotoRetrieved()) {
            memberView.setImage(null);
            retrievePhoto(member);
        } else  {
            memberView.setImage(null);
        }

    }


    private void loadPhoto(Image photo) {
        memberView.setImage(photo);
    }

    public void loadMembers() {
        Event event = eventComboBox.getSelectionModel().getSelectedItem();
        if (event != null) {
            loadMembers(event);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        RaffleMainModel.ModelEvent event = (RaffleMainModel.ModelEvent) arg;
        switch (event.type) {
            case MEMBER_PHOTO_RETRIEVED:
            default:
                Member selectedItem = membersTable.getSelectionModel().getSelectedItem();
                if (selectedItem != null && selectedItem.equals(event.object)) {
                    loadPhoto(((Member) event.object).getImage());
                }
                break;
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addObserver(this);
        start();
        model.start();
        eventComboBox.setItems(model.getObservableEvents());
        model.failureMessageProperty().addListener(observable -> messageArea.setText(model.getFailureMessage()));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("member_id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        membersTable.setItems(model.getObservableMembers());
        model.addObserver(this);
        membersTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        membersTable.getSelectionModel().getSelectedIndices().addListener((javafx.collections.ListChangeListener<? super Integer>) e -> displayMember());
    }

    public void goOnline() {
        connector.setOnline(true);
    }

    public void goOffline() {
        connector.setOnline(false);

    }

    public void refreshMembers() {
        queue.add(this::loadMembers);
    }

    public void pickRandomWinner() {
        int size = model.getObservableMembers().size();
        if (size <= 0) {
            showAlert("There aren't any members to pick a winner from ");
            return;
        }
        int winner = random.nextInt(size);
        membersTable.getSelectionModel().select(winner);
        displayMember();

    }

    private void showAlert(String s) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(5));
        vBox.getChildren().add(new Text(s));
        Region region = new Region();
        region.setPrefHeight(10);
        vBox.getChildren().add(region);
        Button ok = new Button("OK");
        ok.setOnAction(e -> dialogStage.close());
        vBox.getChildren().add(ok);

        dialogStage.setScene(new Scene(vBox));
        dialogStage.sizeToScene();
        Point location = MouseInfo.getPointerInfo().getLocation();
        dialogStage.setX(location.getX());
        dialogStage.setY(location.getY()+10);
        dialogStage.centerOnScreen();


        dialogStage.show();
    }

    public void clearCache() {
        connector.clearCache();
    }
}
