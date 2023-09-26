package com.mstudio.android.mstory.manuel.adapter;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioDeviceInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import androidx.constraintlayout.widget.Placeholder;
import androidx.core.app.ActivityOptionsCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.github.ybq.android.spinkit.SpinKitView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.DeviceInfo;
import com.google.android.exoplayer2.ExoPlaybackException;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.PlayerMessage;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.analytics.AnalyticsCollector;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.audio.AuxEffectInfo;
import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.ShuffleOrder;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.text.CueGroup;

import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionParameters;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource.Factory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.PriorityTaskManager;
import com.google.android.exoplayer2.util.Size;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoFrameMetadataListener;
import com.google.android.exoplayer2.video.VideoSize;
import com.google.android.exoplayer2.video.spherical.CameraMotionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mstudio.android.mstory.manuel.App;
import com.mstudio.android.mstory.manuel.Pojo;
import com.mstudio.android.mstory.manuel.R;
import com.mstudio.android.mstory.manuel.VideoCache;
import com.mstudio.android.mstory.manuel.activity.slide_wallpaper;
import com.mstudio.android.mstory.manuel.activity.slide_wallpaper;
import com.mstudio.android.mstory.manuel.fragment.all;
import com.mstudio.android.mstory.manuel.model.Wall;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.List;


public class adapter_wall_all extends RecyclerView.Adapter<adapter_wall_all.ViewHolde> {
    Context mContext;
    private List<Wall> mUser;
    private FirebaseUser firebaseuser;
    private static adapter_wall_all instance;
    public ExoPlayer exoPlayer;

    public adapter_wall_all(Context context,List<Wall> mUser) {
        this.mUser = mUser;
        this.mContext = context;
        instance = this;
        exoPlayer = new ExoPlayer.Builder(mContext).build();
        exoPlayer.prepare();
    }

    @Override
    public adapter_wall_all.ViewHolde onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_wallpaper, parent, false);

        return new ViewHolde(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolde holder, int position) {
        final Wall wall = mUser.get(position);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        RelativeLayout.LayoutParams pm = new RelativeLayout.LayoutParams(width/2-15*2,wall.getHeight()/4+wall.getHeight()/2);
        holder.card.setLayoutParams(pm);
        Glide.with(mContext.getApplicationContext())
                .asBitmap()
                .load(wall.getImage_wallpaper())
                .into(holder.wallpaper);
        RelativeLayout.LayoutParams it = new RelativeLayout.LayoutParams(width/2-15*2+200,wall.getHeight()/4+wall.getHeight()/2+30);
        holder.item.setLayoutParams(it);
        if(!TextUtils.isEmpty(wall.getVideo_wallpaper())){
            holder.wallpaper.setVisibility(View.GONE);
            holder.exoPlayerView.setVisibility(View.VISIBLE);

            Uri videoUri = Uri.parse(wall.getVideo_wallpaper());
            holder.mediaItem = MediaItem.fromUri(videoUri);
            DefaultHttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true);
            DefaultDataSource.Factory defaultDataSourceFactory = new DefaultDataSource.Factory(mContext, httpDataSourceFactory);
            CacheDataSource.Factory cacheDataSourceFactory = new CacheDataSource.Factory()
                    .setCache(VideoCache.getInstance(mContext))
                    .setUpstreamDataSourceFactory(defaultDataSourceFactory)
                    .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(cacheDataSourceFactory)
                    .createMediaSource(holder.mediaItem);

            exoPlayer.setMediaSource(mediaSource);


            exoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(@Player.State int playbackState) {
                    switch(playbackState) {
                        case Player.STATE_READY:
                            holder.progressBar.setVisibility(View.GONE);
                            break;
                        case Player.STATE_ENDED:
                            exoPlayer.setPlayWhenReady(true);
                            break;
                        case Player.STATE_BUFFERING:
                            holder.progressBar.setVisibility(View.VISIBLE);
                            break;
                        case Player.STATE_IDLE:
                            break;
                        default:
                            break;
                    }
                }
            });

            exoPlayer.setRepeatMode(exoPlayer.REPEAT_MODE_ALL);

            exoPlayer.setVolume(0f);
            exoPlayer.setPlayWhenReady(true);
            holder.exoPlayerView.setPlayer(exoPlayer);

        }else {
            holder.progressBar.setVisibility(View.GONE);
            holder.wallpaper.setVisibility(View.VISIBLE);
            holder.exoPlayerView.setVisibility(View.GONE);
        }


        holder.item
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Intent i = new Intent(mContext, slide_wallpaper.class);
                        i.putExtra("id", wall.getId());
                        mContext.startActivity(i);

                    }
                }, 150);

            }
        });

    }
    @Override
    public int getItemCount() {
        return mUser.size();
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolde holder) {
        super.onViewRecycled(holder);
        holder.releasePlayer();
    }

    public class ViewHolde extends RecyclerView.ViewHolder {
        ImageView wallpaper;
        CardView card;
        LinearLayout item;
        SpinKitView progressBar;
        MediaItem mediaItem;
        StyledPlayerView exoPlayerView;


        public ViewHolde(View itemView) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.spin_kit);
            exoPlayerView = itemView.findViewById(R.id.exoplayer);
            item = itemView.findViewById(R.id.item);
            card = itemView.findViewById(R.id.card);
            wallpaper = itemView.findViewById(R.id.image_wallpaper);
        }

        public void releasePlayer() {
            if (exoPlayer != null) {
                exoPlayer.release();
                exoPlayer = null;
            }
        }

    }

    public static adapter_wall_all getInstance() {
        return instance;
    }

    public void playvideo(ExoPlayer exoPlayer) {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(true);
        }
    }
    public void pausevideo(ExoPlayer exoPlayer){
        if(exoPlayer!=null){
            exoPlayer.setPlayWhenReady(false);
        }
    }
}
