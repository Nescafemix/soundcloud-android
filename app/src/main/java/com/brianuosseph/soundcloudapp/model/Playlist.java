package com.brianuosseph.soundcloudapp.model;

/**
 * A collection of playable sounds.
 */
public class Playlist extends Sound {
    public Playlist(long id,
                    String createdAt,
                    String userId,
                    boolean isRepost) {
        super(id, createdAt, userId, isRepost);
    }
}
