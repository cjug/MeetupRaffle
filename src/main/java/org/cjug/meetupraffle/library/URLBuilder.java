package org.cjug.meetupraffle.library;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Freddy on 3/22/2014.
 * Helper class to create an URL with get parameters
 */
public class URLBuilder {
    Map<String,String> parameterMap = new LinkedHashMap<>();
    public URLBuilder() {

    }

    public URLBuilder addParam(String key,String value) {
        parameterMap.put(key,value);
        return this;
    }

    @Override
    public String toString() {
        boolean first = true;
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String,String> entry : parameterMap.entrySet()) {
            if (entry.getValue() == null) continue; // can't set
            builder.append(first ? "?" : "&");
            try {
                builder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                builder.append("=");
                builder.append(URLEncoder.encode(entry.getValue(),"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            first = false;
        }
        return builder.toString();
    }

    public static URLBuilder create() {
        return new URLBuilder();
    }
}
