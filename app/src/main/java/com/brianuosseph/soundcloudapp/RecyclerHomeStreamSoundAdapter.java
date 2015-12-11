package com.brianuosseph.soundcloudapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.brianuosseph.soundcloudapp.HomeStreamFragment.OnHomeStreamListFragmentInteractionListener;
import com.brianuosseph.soundcloudapp.dummy.DummyContent.DummyItem;
import com.brianuosseph.soundcloudapp.model.Sound;
import com.brianuosseph.soundcloudapp.model.Track;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Sound} and makes a call to the
 * specified {@link OnHomeStreamListFragmentInteractionListener}.
 */
public class RecyclerHomeStreamSoundAdapter
        extends RecyclerView.Adapter<RecyclerHomeStreamSoundAdapter.ViewHolder>
        implements OnHomeStreamListFragmentInteractionListener {

    // TODO: Debug NullPointerException relating to ImageLoader and Context
    private Context mContext;
    private final OnHomeStreamListFragmentInteractionListener mListener;
    private List<Sound> mSounds;

    public RecyclerHomeStreamSoundAdapter(Context context,
                                          List<Sound> sounds,
                                          OnHomeStreamListFragmentInteractionListener listener) {
        super();
        mContext = context;
        mSounds = sounds;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_sound, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // Set corresponding model to ViewHolder
        holder.mSound = mSounds.get(position);

        ImageLoader mImageLoader = NetworkManager.getInstance(mContext).getImageLoader();

        // Update ViewHolder children view data
        holder.mUserNameView.setText(holder.mSound.userName);
        holder.mTitleView.setText(holder.mSound.title);
        holder.mDurationView.setText(holder.mSound.getFormattedDuration());

        // Status changes on sound type (and shouldn't be playing)
        if (holder.mSound instanceof Track) {
            Track sound = (Track) holder.mSound;
            holder.mStatusView.setText(sound.getFormattedPlaybackCount());
        }

        // Image
        holder.mArtworkView.setImageUrl(holder.mSound.artworkUrl, mImageLoader);
        holder.mArtworkView.setDefaultImageResId(R.drawable.sound_artwork_default);
        holder.mArtworkView.setErrorImageResId(R.drawable.sound_artwork_error);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mSound);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSounds.size();
    }

    @Override
    public void onListFragmentInteraction(Sound sound) {
        // Handle sound on event
        // This is where the sound would begin streaming
    }

    /**
     * Represents view of single item in adapter, actual view is bound in onBindViewHolder
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mUserNameView;
        public final TextView mTitleView;
        public final TextView mDurationView;
        public final TextView mStatusView;
        public final NetworkImageView mArtworkView;

        public Sound mSound;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mUserNameView = (TextView) view.findViewById(R.id.sound_user);
            mTitleView = (TextView) view.findViewById(R.id.sound_title);
            mDurationView = (TextView) view.findViewById(R.id.sound_duration);
            mStatusView = (TextView) view.findViewById(R.id.sound_status);
            mArtworkView = (NetworkImageView) view.findViewById(R.id.sound_art);
        }
    }

}