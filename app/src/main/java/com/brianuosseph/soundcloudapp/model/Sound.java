package com.brianuosseph.soundcloudapp.model;

// TODO: Create getTimeSince()
/**
 * Represents a playable sound, or collection of sounds, from the SoundCloud API.
 */
public abstract class Sound {
    long id;
    boolean isRepost;
    String createdAt;
    String userId;
    public String userName;
    public String userAvatar;
    public String title;
    public String permalink;
    public String genre;
    public String artworkUrl;
    public long duration;

    public Sound(long id,
                 String createdAt,
                 String userId,
                 boolean isRepost) {
        this.id = id;
        this.createdAt = createdAt;
        this.userId = userId;
        this.isRepost = isRepost;
    }

    public long getId() {
        return id;
    }

    public boolean isRepost() {
        return isRepost;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public String getFormattedDuration() {
        long seconds = (duration / 1000) % 60;
        long minutes = (duration / (1000 * 60)) % 60;
        long hours = (duration / (1000 * 60 * 60));

        String time;
        if (hours == 0) {
            time = String.format("%2d:%02d", minutes, seconds);
        }
        else {
            time = String.format("%d:%02d:%02d", hours, minutes, seconds);
        }
        return time;
    }
}
