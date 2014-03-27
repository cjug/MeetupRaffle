package org.cjug.meetupraffle;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import org.cjug.meetupraffle.library.Event;
import org.cjug.meetupraffle.library.Member;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Freddy on 3/22/2014.
 * Main Model for application
 */
public class RaffleMainModel extends Observable {

    public enum EventType {MEMBER_PHOTO_RETRIEVED}


    private List<Event> events = new ArrayList<>();
    private ConcurrentMap<String,Member> memberMap = new ConcurrentHashMap<>();
    private List<Member> members = new ArrayList<>();
    private ObservableList<Event> observableEvents = new ObservableListWrapper<>(events);
    private ObservableList<Member> observableMembers = new ObservableListWrapper<>(members);
    private ReadOnlyStringWrapper failureMessage = new ReadOnlyStringWrapper(null);

    public void start() {

    }

    public void stop() {

    }

    public ObservableList<Event> getObservableEvents() {
        return observableEvents;
    }

    public void setEvents(Collection<Event> events) {
        Platform.runLater(() -> observableEvents.setAll(events));
    }

    public void setFailureMessage(String message) {
        Platform.runLater(() -> failureMessage.set(message));
    }

    public String getFailureMessage() {
        return failureMessage.get();
    }

    public ReadOnlyStringWrapper failureMessageProperty() {
        return failureMessage;
    }

    public void setMembers(Collection<Member> members) {
        Collection<Member> assignedMembers = new ArrayList<>();

        members.stream().forEach(e -> {
            memberMap.putIfAbsent(e.getMember_id(), e);
            assignedMembers.add(memberMap.get(e.getMember_id()));
        });

        Platform.runLater(() -> {
            observableMembers.setAll(assignedMembers);
        });
    }

    public ObservableList<Member> getObservableMembers() {
        return observableMembers;
    }

    public void memberPhotoUpdated(Member member) {
        Platform.runLater(() -> {
            setChanged();
            notifyObservers(new ModelEvent(EventType.MEMBER_PHOTO_RETRIEVED, member));
        });
    }

    public static class ModelEvent {
        final EventType type;
        final Object object;

        public ModelEvent(EventType type, Object object) {
            this.type = type;
            this.object = object;
        }
    }

}
