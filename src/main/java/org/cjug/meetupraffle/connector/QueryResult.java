package org.cjug.meetupraffle.connector;

/**
 * Created by Freddy on 3/22/2014.
 */
public class QueryResult {
    public enum Status {SUCCESS, FAILURE}
    private final Status status;
    private final String reason;

    public QueryResult(Status status, String reason) {
        this.status = status;
        this.reason = reason;
    }

    public Status getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }
}
