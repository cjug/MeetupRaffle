package org.cjug.meetupraffle.library;

import java.io.Serializable;

/**
 * Created by Freddy on 3/22/2014.
 */
@SuppressWarnings("UnusedDeclaration")
public class MemberPhoto implements Serializable {
    String photo_link;
    String photo_id;

    public String getPhoto_link() {
        return photo_link;
    }

    public void setPhoto_link(String photo_link) {
        this.photo_link = photo_link;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

}
