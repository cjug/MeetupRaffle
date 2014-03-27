package org.cjug.meetupraffle.connector;

import com.google.gson.Gson;
import javafx.scene.image.Image;
import org.cjug.meetupraffle.library.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * Created by Freddy on 3/22/2014.
 * Actual Meetup Connector
 */
public class MeetupConnector implements Connector {
    private final Configuration configuration;

    public MeetupConnector(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public QueryResult getEvents(Collection<Event> events, Predicate<Event> predicate) {
        String getString = URLBuilder.create()
                .addParam("key", configuration.getKey())
                .addParam("group_urlname", configuration.getGroupUrl())
                .addParam("status", "upcoming,past")
                .addParam("time", "-1m,1m")
                .toString();
        String address = Globals.MEETUP_URL + "/" + "/2/events" + getString;
        String JSONString;
        try {
            JSONString = getUrl(address);
        } catch (IOException e) {
            return new QueryResult(QueryResult.Status.FAILURE, "Couldn't get JSON URL :"+e.toString());
        }
        Gson gson = new Gson();
        try {
            Event.EventResponse eventResponse = gson.fromJson(JSONString, Event.EventResponse.class);
            eventResponse.getResults().stream().filter(predicate).forEach(events::add);
        } catch (Exception e) {
            return new QueryResult(QueryResult.Status.FAILURE, "Couldn't parse JSON :"+e.toString()+" "+JSONString);
        }
        return new QueryResult(QueryResult.Status.SUCCESS, null);
    }

    @Override
    public QueryResult getMembers(Collection<Member> members, Event event) {
        String getString = URLBuilder.create()
                .addParam("key", configuration.getKey())
                .addParam("group_urlname", configuration.getGroupUrl())
                .addParam("event_id",event.getId())
                .toString();
        String address = Globals.MEETUP_URL + "/" + "/2/rsvps" +
                getString;
        String JSONString;
        try {
            JSONString = getUrl(address);
        } catch (IOException e) {
            return new QueryResult(QueryResult.Status.FAILURE, "Couldn't get JSON URL :"+e.toString());
        }
        try {
            Gson gson = new Gson();
            RSVP.Response rsvps = gson.fromJson(JSONString, RSVP.Response.class);
            rsvps.getResults().forEach(r -> {
                r.getMember().setMemberPhoto(r.getMember_photo());
                members.add(r.getMember());
            });
        } catch (Exception e) {
            return new QueryResult(QueryResult.Status.FAILURE, "Couldn't Parse :"+e.toString()+" "+JSONString);
        }

        return new QueryResult(QueryResult.Status.SUCCESS, null);
    }

    @Override
    public QueryResult retrievePhoto(Member member) {
        Image image = downloadPhoto(member.getMemberPhoto());
        if (image == null) {
            return new QueryResult(QueryResult.Status.FAILURE, "No Image");
        } else {
            member.setImage(image);
            member.setPhotoRetrieved(true);
            return new QueryResult(QueryResult.Status.SUCCESS, null);
        }
    }

    private Image downloadPhoto(MemberPhoto member_photo) {
        Image image = null;
        if (member_photo != null && member_photo.getPhoto_link() != null) {
            try {
                image = new Image(member_photo.getPhoto_link(), false);
            } catch (Exception e) {
                return null;
            }
        }
        return image;
    }

    private static String getUrl(String address) throws IOException {
        URL url;
        StringBuilder builder = new StringBuilder();
            url = new URL(address);
            URLConnection connection = url.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String strLine;
            while ((strLine = in.readLine()) != null) {
                builder.append(strLine);

            }
        return builder.toString();
    }


    public static class Configuration {
        final String key;
        final String groupUrl;

        public Configuration(String key, String groupUrl) {
            this.key = key;
            this.groupUrl = groupUrl;
        }

        public String getKey() {
            return key;
        }

        public String getGroupUrl() {
            return groupUrl;
        }


    }
}
