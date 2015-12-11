package com.brianuosseph.soundcloudapp.model;

/**
 * A singular playable sound
 */
public class Track extends Sound {
    public long playbackCount;

    public Track(long id,
                 String createdAt,
                 String userId,
                 boolean isRepost) {
        super(id, createdAt, userId, isRepost);
    }

    // TODO: Redesign formatting algorithm
    public String getFormattedPlaybackCount() {
        long hundreds = playbackCount % 1000;
        long thousands = playbackCount / 1000;

        if (thousands > 0) {
            return String.format("%d.%01dK", thousands, hundreds);
        }
        else {
            return String.format("%d", playbackCount);
        }
    }
}
