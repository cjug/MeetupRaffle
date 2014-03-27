package org.cjug.meetupraffle.connector;

import org.cjug.meetupraffle.library.Event;
import org.cjug.meetupraffle.library.Member;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * Created by Freddy on 3/22/2014.
 * Connector provider for Meetup Information
 */
public interface Connector {

    void start();
    void stop();

    QueryResult getEvents(Collection<Event> events, Predicate<Event> predicate);
    QueryResult getMembers(Collection<Member> members, Event event);
    QueryResult retrievePhoto(Member member);
}
