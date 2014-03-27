package org.cjug.meetupraffle.library;

import java.io.Serializable;

/**
 * Created by Freddy on 3/22/2014.
 * Represents a Meetup Event
 */
@SuppressWarnings("UnusedDeclaration")
public class Event implements Serializable {
    int rsvp_limit;
    String status;
    String visibility;
    String name;
    String id;
    String description;

    public static class EventResponse extends Response<Event> {

    }

    public int getRsvp_limit() {
        return rsvp_limit;
    }

    public void setRsvp_limit(int rsvp_limit) {
        this.rsvp_limit = rsvp_limit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return getName() + " (" + getId() + ")";
    }

}
