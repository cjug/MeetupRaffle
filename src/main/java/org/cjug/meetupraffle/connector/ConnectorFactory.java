package org.cjug.meetupraffle.connector;

/**
 * Created by Freddy on 3/22/2014.
 * Factory for connectors
 */

public class ConnectorFactory {
    public static Connector create (MeetupConnector.Configuration configuration, boolean online) {
        MeetupConnector meetupConnector = new MeetupConnector(configuration);
        CachedConnector cachedConnector = new CachedConnector(meetupConnector);
        cachedConnector.setOnline (online);
        return cachedConnector;
    }
}
