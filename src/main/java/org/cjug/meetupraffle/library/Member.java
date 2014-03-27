package org.cjug.meetupraffle.library;

import javafx.scene.image.Image;

import java.io.Serializable;

/**
 * Created by Freddy on 3/22/2014.
 * Meetup Member
 */
@SuppressWarnings("UnusedDeclaration")
public class Member implements Serializable {
    String member_id;
    String name;
    private transient Image image;
    boolean photoRetrieved = false;
    private MemberPhoto memberPhoto;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Image getImage() {
        return image;
    }

    public boolean isPhotoRetrieved() {
        return photoRetrieved;
    }

    public void setPhotoRetrieved(boolean photoRetrieved) {
        this.photoRetrieved = photoRetrieved;
    }

    public void setMemberPhoto(MemberPhoto memberPhoto) {
        this.memberPhoto = memberPhoto;
    }

    public MemberPhoto getMemberPhoto() {
        return memberPhoto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Member member = (Member) o;

        return member_id.equals(member.member_id);

    }

    @Override
    public int hashCode() {
        return member_id.hashCode();
    }
}
