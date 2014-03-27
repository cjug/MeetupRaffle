package org.cjug.meetupraffle.library;

/**
 * Created by Freddy on 3/22/2014.
 * Contains RSVP Information from JSON
 */
@SuppressWarnings("UnusedDeclaration")
public class RSVP {
    String response;
    MemberPhoto member_photo;
    Member member;

    public boolean attending() {
        return response.equals("yes");
    }
    public MemberPhoto getMember_photo() {
        return member_photo;
    }

    public void setMember_photo(MemberPhoto member_photo) {
        this.member_photo = member_photo;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public static class Response extends org.cjug.meetupraffle.library.Response<RSVP> {

    }

    public String getPhotoUrl() {
        if (member_photo != null) return member_photo.getPhoto_link();
        return null;
    }

}
