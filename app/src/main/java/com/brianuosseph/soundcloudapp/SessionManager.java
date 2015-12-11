package com.brianuosseph.soundcloudapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.soundcloud.api.Token;

public class SessionManager {

    public static final String CLIENT_ID = "b5c9b2be84b94ad55d4d34e08de6e93c";
    public static final String CLIENT_SECRET = "fbfb0039139166b8045b1a2bada4ea40";

    private static final String PREF_NAME = "UserSessionPref";
    private static final String LOGIN_KEY = "IsLoggedIn";
    private static final String TOKEN_ACCESS_KEY = "TokenAccess";
    private static final String TOKEN_REFRESH_KEY = "TokenRefresh";
    private static final String TOKEN_SCOPE_KEY = "TokenScope";
    private static final String USER_ID_KEY = "UserId";
    private static final String USER_NAME_KEY = "UserName";
    private static final String USER_PERMALINK_KEY = "UserPermalink";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private int PRIVATE_MODE = 0;

    public SessionManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }

    /**
     * Creates user session, signing in the user and saving the provided user data.
     * @param token Token created by ApiWrapper on sign-in
     */
    public void createLoginSession(Token token /*,
                                   long userId,
                                   String username,
                                   String permalink*/) {
        editor = preferences.edit();
        editor.putBoolean(LOGIN_KEY, true);

        editor.putString(TOKEN_ACCESS_KEY, token.access);
        editor.putString(TOKEN_REFRESH_KEY, token.refresh);
        editor.putString(TOKEN_SCOPE_KEY, token.scope);

//        editor.putLong(USER_ID_KEY, userId);
//        editor.putString(USER_NAME_KEY, username);
//        editor.putString(USER_PERMALINK_KEY, permalink);
        editor.commit();
    }

    public Token getToken() {
        String access = getTokenAccess();
        String refresh = preferences.getString(TOKEN_REFRESH_KEY, "");
        String scope = preferences.getString(TOKEN_SCOPE_KEY, "");

        return new Token(access, refresh, scope);
    }

    public String getTokenAccess() {
        return preferences.getString(TOKEN_ACCESS_KEY, "");
    }

    public long getUserId() {
        // Valid user ids are non-zero values
        return preferences.getLong(USER_ID_KEY, 0);
    }

    public String getUserName() {
        return preferences.getString(USER_NAME_KEY, "");
    }

    public String getUserPermalink() {
        return preferences.getString(USER_PERMALINK_KEY, "");
    }

    /**
     * Checks if a session exists.
     * @return If the user is logged in
     */
    public boolean isLoggedIn() {
        return preferences.getBoolean(LOGIN_KEY, false);
    }

    /**
     * Clears session data, logging out user.
     */
    public void logoutUser() {
        // Clear all SharedPreferences data
        editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}
