package org.cjug.meetupraffle.library;

import java.util.List;

/**
 * Created by Freddy on 3/22/2014.
 * Represents a GSON response
 */
@SuppressWarnings("UnusedDeclaration")
public class Response <T> {
    Meta meta;
    List<T> results;
    public Response() {


    }
    public static class Meta {
        String lon;
        String description;
        String title;
    }

    public List<T> getResults() {
        return results;
    }
}
