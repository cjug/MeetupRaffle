package org.cjug.meetupraffle.library;

import org.cjug.meetupraffle.connector.MeetupConnector;

import java.io.*;
import java.util.Properties;

/**
 * Created by Freddy on 3/22/2014.
 * Configuration of the application
 */
public class ApplicationConfiguration {
    private File confFile = new File (Globals.HOME_FOLDER+"MeetupRaffle.conf");
    private Dimension dimension = null;


    private boolean online = true;

    MeetupConnector.Configuration meetupConnectorConfiguration = new MeetupConnector.Configuration(null, null);


    public MeetupConnector.Configuration getMeetupConnectorConfiguration() {
        return meetupConnectorConfiguration;
    }

    public void setMeetupConnectorConfiguration(MeetupConnector.Configuration meetupConnectorConfiguration) {
        this.meetupConnectorConfiguration = meetupConnectorConfiguration;
    }

    public void load() {
        // let's load.
        Properties properties = new Properties();
        try {
            if (confFile.exists()) {
                Reader reader = new FileReader(confFile);
                properties.load(reader);
                String key = properties.getProperty("meetup.key", null);
                String groupUrl = properties.getProperty("meetup.groupUrl", null);
                meetupConnectorConfiguration = new MeetupConnector.Configuration(key, groupUrl);
                online = Boolean.valueOf(properties.getProperty("online","true"));
                Integer locationX = getNumber(properties.getProperty("locationX",""));
                Integer locationY = getNumber(properties.getProperty("locationY",""));
                Integer width = getNumber(properties.getProperty("width",""));
                Integer height = getNumber(properties.getProperty("height",""));
                if (locationX !=null && locationY != null && width != null && height != null) {
                    dimension = new Dimension(locationX, locationY, width, height);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Integer getNumber(String number) {
        if (number == null || number.isEmpty()) return null;
        return Integer.valueOf(number);
    }

    public void save() {
        Properties properties = new Properties();
        if (meetupConnectorConfiguration.getKey() != null) properties.put("meetup.key",meetupConnectorConfiguration.getKey());
        if (meetupConnectorConfiguration.getGroupUrl() != null) properties.put("meetup.groupUrl",meetupConnectorConfiguration.getGroupUrl());
        properties.put("online",String.valueOf(online));
        if (dimension != null) {
            properties.put("locationX",String.valueOf(dimension.getX()));
            properties.put("locationY",String.valueOf(dimension.getY()));
            properties.put("width",String.valueOf(dimension.getWidth()));
            properties.put("height",String.valueOf(dimension.getHeight()));
        }
        try {
            Writer writer = new FileWriter(confFile);
            properties.store(writer,"MeetupRaffle Configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }


    public static class Dimension {
        final int x;
        final int y;
        final int width;
        final int height;

        public Dimension(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

}
