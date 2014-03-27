package org.cjug.meetupraffle.connector;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import org.cjug.meetupraffle.library.Event;
import org.cjug.meetupraffle.library.Globals;
import org.cjug.meetupraffle.library.Member;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * Created by Freddy on 3/22/2014.
 * Connector interface
 */
public class CachedConnector implements Connector {
    volatile boolean online = true;
    Connector connector;
    Collection<Event> cachedEvents = new ArrayList<>();
    Map<String, Collection<Member>> cachedMembersMap = new ConcurrentHashMap<>();
    Map<String, ImageBytes> cachedMemberPhoto = new ConcurrentHashMap<>();
    private OnlineListener onlineListener = null;
    private File cacheFile = new File (Globals.HOME_FOLDER+"cache.bin");

    public CachedConnector(Connector connector) {
        this.connector = connector;
    }


    @Override
    public void start() {
        connector.start();
        if (cacheFile.exists()) {
            // load cache.
            try {
                FileInputStream fis = new FileInputStream(cacheFile);
                ObjectInputStream ois = new ObjectInputStream(fis);
                CacheInfo cacheInfo = (CacheInfo) ois.readObject();
                cacheInfo.prepare();
                cachedEvents = cacheInfo.getEvents();
                cachedMembersMap.putAll(cacheInfo.getMembersMap());
                cachedMemberPhoto.putAll(cacheInfo.getMemberPhotos());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void stop() {
        connector.stop();
        // let's save cache.
        CacheInfo info = new CacheInfo(cachedEvents, cachedMembersMap, cachedMemberPhoto);
        //noinspection ResultOfMethodCallIgnored
        cacheFile.getParentFile().mkdirs();
        try {
            FileOutputStream fos = new FileOutputStream(cacheFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(info);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setOnlineListener(OnlineListener onlineListener) {
        this.onlineListener = onlineListener;
    }

    public boolean isOnline() {
        return online;
    }

    public void clearCache() {
        cachedEvents.clear();
        cachedMembersMap.clear();
        cachedMemberPhoto.clear();

    }

    public interface OnlineListener {
        void changed();
    }

    private static class CacheInfo implements Serializable {
        Collection<Event> events = new ArrayList<>();
        Set<Member> members = new HashSet<>();
        Map<String,ImageBytes> memberImagesMap = new HashMap<>();
        Map<String,Collection<String>> membersToEventMap = new HashMap<>();

        public CacheInfo(Collection<Event> cachedEvents, Map<String, Collection<Member>> cachedMembersMap, Map<String, ImageBytes> cachedMemberPhoto) {
            events.addAll(cachedEvents);
            for (Map.Entry<String, Collection<Member>> entry : cachedMembersMap.entrySet()) {
                members.addAll(entry.getValue());
                Collection<String> members = new ArrayList<>();
                for (Member member : entry.getValue()) {
                    members.add(member.getMember_id());
                }
                membersToEventMap.put(entry.getKey(),members);
            }

            memberImagesMap.putAll(cachedMemberPhoto);
        }

        public Collection<Event> getEvents() {
            return events;
        }

        public Map<String, Collection<Member>> getMembersMap() {
            Map<String,Collection<Member>> membersMap = new HashMap<>();
            Map<String,Member> memberIdMap = getMemberIdMap();
            for (Map.Entry<String,Collection<String>> entry : membersToEventMap.entrySet()) {
                Collection<Member> members = new ArrayList<>();
                for (String memberId : entry.getValue()) {
                    members.add(memberIdMap.get(memberId));
                }
                membersMap.put(entry.getKey(), members);

            }
            return membersMap;
        }

        private Map<String, Member> getMemberIdMap() {
            Map<String,Member> map = new HashMap<>();
            for (Member member : members) {
                map.put(member.getMember_id(), member);
            }
            return map;
        }

        public void prepare() {
            for (Member member : members) {
                ImageBytes image = memberImagesMap.get(member.getMember_id());
                if (image != null) {
                    Image fxImage = image.createImage();
                    if (fxImage != null) {
                        member.setImage(fxImage);
                    }
                }
            }
        }

        public Map<String,ImageBytes> getMemberPhotos() {
            return memberImagesMap;
        }
    }

    private static class ImageBytes implements Serializable {
        final byte[] bytes;
        transient Image image;

        private ImageBytes(Image image) {
            this.image = image;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image,null), "png", baos);
            } catch (IOException e) {
                e.printStackTrace();
            }
            bytes =baos.toByteArray();
        }


        public Image createImage() {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            WritableImage fxImage = null;
            try {
                BufferedImage image = ImageIO.read(bais);
                fxImage = SwingFXUtils.toFXImage(image, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.image = fxImage;
            return fxImage;
        }
    }

    @Override
    public QueryResult getEvents(Collection<Event> events, Predicate<Event> predicate) {
        if (online) {
            Collection<Event> cachedEvents = new ArrayList<>();
            QueryResult connectorResult = connector.getEvents(cachedEvents, p-> true);  // all to cache
            if (connectorResult.getStatus() == QueryResult.Status.SUCCESS) {
                this.cachedEvents = cachedEvents;
                populateFromCache(events, predicate);
                return new QueryResult(QueryResult.Status.SUCCESS, null);
            } else {
                setOnline(false);
                populateFromCache(events, predicate);
                return new QueryResult(QueryResult.Status.FAILURE, connectorResult.getReason());
            }
        } else {
            populateFromCache(events, predicate);
            return new QueryResult(QueryResult.Status.SUCCESS, null);
        }
    }

    @Override
    public QueryResult getMembers(Collection<Member> members, Event event) {
        if (online) {
            Collection<Member> cachedMembers = new ArrayList<>();
            QueryResult connectorResult = connector.getMembers(cachedMembers, event);
            if (connectorResult.getStatus() == QueryResult.Status.SUCCESS) {
                cachedMembersMap.put(event.getId(),cachedMembers);
                members.addAll(cachedMembers);
                return new QueryResult(QueryResult.Status.SUCCESS, null);
            } else {
                cachedMembers = cachedMembersMap.get(event.getId());
                if (cachedMembers != null) {
                    members.addAll(cachedMembers);
                }
                return new QueryResult(QueryResult.Status.FAILURE, connectorResult.getReason());
            }
        } else {
            Collection<Member> cachedMembers = cachedMembersMap.get(event.getId());
            if (cachedMembers != null) {
                members.addAll(cachedMembers);
            }
            return new QueryResult(QueryResult.Status.SUCCESS, null);
        }
    }

    @Override
    public QueryResult retrievePhoto(Member member) {
        cachedMemberPhoto.get(member.getMember_id());
        if (member.getImage() != null) return new QueryResult(QueryResult.Status.SUCCESS, null);
        if (online) {
            QueryResult result = connector.retrievePhoto(member);
            if (result.getStatus() == QueryResult.Status.SUCCESS && member.getImage() != null) {
                cachedMemberPhoto.put(member.getMember_id(), new ImageBytes(member.getImage()));
            }
            return result;
        } else {
            return new QueryResult(QueryResult.Status.FAILURE, "No cached image for "+member.getName());
        }
    }

    private void populateFromCache(Collection<Event> events, Predicate<Event> predicate) {
        cachedEvents.<Event>stream().filter(predicate).forEach(events::add);
    }

    public void setOnline(boolean online) {
        this.online = online;
        if (onlineListener != null) onlineListener.changed();
    }
}
