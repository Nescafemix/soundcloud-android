package com.brianuosseph.soundcloudapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.brianuosseph.soundcloudapp.model.Playlist;
import com.brianuosseph.soundcloudapp.model.Sound;
import com.brianuosseph.soundcloudapp.model.Track;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnHomeStreamListFragmentInteractionListener}
 * interface.
 */
public class HomeStreamFragment extends Fragment {
    private SessionManager session;
    private OnHomeStreamListFragmentInteractionListener mListener;
    private int pageSize = 15;
    private String nextPageLink;
    private String updatedListLink;
    private List<Sound> mSounds;
    private ImageLoader mImageLoader;

    // UI References
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HomeStreamFragment() {
    }

    @SuppressWarnings("unused")
    public static HomeStreamFragment newInstance() {
        return new HomeStreamFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImageLoader = NetworkManager.getInstance(getActivity().getApplicationContext())
                .getImageLoader();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_stream_list, container, false);
        Context context = view.getContext();

        session = new SessionManager(context);
        mSounds = new ArrayList<>();

        mSwipeRefreshLayout = (SwipeRefreshLayout) view;
        mRecyclerView = (RecyclerView) view.findViewById(R.id.home_stream_list);

        // Layout size does not change; this will improve performance
        mRecyclerView.setHasFixedSize(true);

        // Set the layout manager
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Set the adapter
        mAdapter = new RecyclerHomeStreamSoundAdapter(getActivity().getApplicationContext(),
                mSounds,
                mListener);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items in list
                mSwipeRefreshLayout.setRefreshing(true);
                refreshItems();
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    getMoreItems();
                }
            }
        });

        // Get first page of sounds
        refreshItems();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHomeStreamListFragmentInteractionListener) {
            mListener = (OnHomeStreamListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnHomeStreamListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Sound sound);
    }

    /**
     * Makes a volley request to retrieve an updated list of tracks in the user
     * home stream.
     */
    private void refreshItems() {
        // TODO: Redesign to check if updated list is equivalent, in that case don't clear the data
        mSounds.clear();

        String url;
        if (updatedListLink == null) {
            url = NetworkManager.BASE_URL + "/me/activities/tracks/affiliated"
                    + "?oauth_token=" + session.getTokenAccess()
                    + "&limit=" + pageSize;
        }
        else {
            url = updatedListLink + "&oauth_token=" + session.getTokenAccess();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        parseResponseAndAppendSounds(response);
                        mAdapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VolleyError", "Unable to refresh home stream list");
                        error.printStackTrace();

                        // TODO: Update user on error
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });

        request.addMarker(MainActivity.TAG);

        NetworkManager.getInstance(getActivity().getApplicationContext())
                .addToRequestQueue(request);
    }

    /**
     * Makes a volley request using the nextPageLink provided by the SoundCloud API.
     */
    private void getMoreItems() {
        if (nextPageLink != null) {
            String url = nextPageLink + "&oauth_token=" + session.getTokenAccess();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            parseResponseAndAppendSounds(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("VolleyError", "Unable to refresh home stream list");
                            error.printStackTrace();

                            // TODO: Update user on error
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });

            request.addMarker(MainActivity.TAG);

            NetworkManager.getInstance(getActivity().getApplicationContext())
                    .addToRequestQueue(request);
        }
    }

    /**
     * Parses the response for sounds and adds them to the recycler view adapter.
     * @param response The JSONObject response returned by the SoundCloud API
     */
    private void parseResponseAndAppendSounds(JSONObject response) {
        boolean isError = false;
        ArrayList<Sound> soundBuffer = new ArrayList<>();

        try {
            nextPageLink = response.getString("next_href");
            updatedListLink = response.getString("future_href");
            JSONArray collection = response.getJSONArray("collection");

            for (int i = 0; i < pageSize; i++) {
                JSONObject json = collection.getJSONObject(i);

                Sound sound;
                String soundType = json.getString("type");
                JSONObject soundJson = json.getJSONObject("origin");

                long id = soundJson.getLong("id");
                String createdAt = soundJson.getString("created_at");
                String userId = soundJson.getString("user_id");

                boolean isRepost = false;
                switch (soundType) {
                    case "playlist-repost":
                        isRepost = true;
                    case "playlist":
                        sound = new Playlist(id, createdAt, userId, isRepost);
                        break;

                    case "track-repost":
                        isRepost = true;
                    case "track":
                        Track track = new Track(id, createdAt, userId, isRepost);
                        track.playbackCount = soundJson.getLong("playback_count");
                        sound = track;
                        break;

                    default:
                        continue;
                }

                // attributes common in all sounds
                sound.userName = soundJson.getJSONObject("user").getString("username");
                sound.title = soundJson.getString("title");
                sound.permalink = soundJson.getString("permalink");
                sound.artworkUrl = soundJson.getString("artwork_url");
                sound.duration = soundJson.getLong("duration");

                soundBuffer.add(sound);
            }
        }
        catch (JSONException e) {
            Log.e("JsonParse", "Unable to parse new JSON data");
            e.printStackTrace();

            // TODO: Update user on error
            isError = true;
        }

        if (!isError) {
            mSounds.addAll(soundBuffer);
        }
    }
}
