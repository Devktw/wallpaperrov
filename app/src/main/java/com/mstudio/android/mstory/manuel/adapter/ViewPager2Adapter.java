package com.mstudio.android.mstory.manuel.adapter;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mstudio.android.mstory.manuel.R;
import com.mstudio.android.mstory.manuel.VideoCache;
import com.mstudio.android.mstory.manuel.model.Wall;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
public  class ViewPager2Adapter extends RecyclerView.Adapter<ViewPager2Adapter.ViewHolder> {

    private Context ctx;
    List<Wall> listwall = new ArrayList<>();
    private InterstitialAd mInterstitialAd;
    public ViewPager2Adapter(Context ctx, List<Wall> wall) {
        this.listwall = wall;
        this.ctx = ctx;


    }

    // This method returns our layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.wallpaper_item, parent, false);
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(ctx, "ca-app-pub-8467206635180635/8816521145", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;

                    }
                });

        MobileAds.initialize(ctx, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Wall wall = listwall.get(position);
        String id = ((Activity)ctx).getIntent().getStringExtra("id");
        String id2 = wall.getId();
        if(position==0) {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper").child(id);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                    Wall wall = snapshot.getValue(Wall.class);

                    if (!TextUtils.isEmpty(wall.getVideo_wallpaper())) {
                        holder.image.setVisibility(View.GONE);
                        holder.exoPlayerView.setVisibility(View.VISIBLE);
                        holder.exoPlayer = new ExoPlayer.Builder(ctx).build();
                        Uri videoUri = Uri.parse(wall.getVideo_wallpaper());
                        MediaItem mediaItem = MediaItem.fromUri(videoUri);
                        DefaultHttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true);
                        DefaultDataSource.Factory defaultDataSourceFactory = new DefaultDataSource.Factory(ctx, httpDataSourceFactory);
                        CacheDataSource.Factory cacheDataSourceFactory = new CacheDataSource.Factory()
                                .setCache(VideoCache.getInstance(ctx))
                                .setUpstreamDataSourceFactory(defaultDataSourceFactory)
                                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);

                        MediaSource mediaSource = new ProgressiveMediaSource.Factory(cacheDataSourceFactory)
                                .createMediaSource(mediaItem);
                        holder.exoPlayer.setMediaSource(mediaSource);


                        holder.exoPlayer.addListener(new Player.Listener() {
                            @Override
                            public void onPlaybackStateChanged(@Player.State int playbackState) {
                                switch(playbackState) {
                                    case Player.STATE_READY:
                                        holder.progressBar.setVisibility(View.GONE);
                                        break;
                                    case Player.STATE_ENDED:
                                        holder.exoPlayer.setPlayWhenReady(true);
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
                        holder.exoPlayerView.setPlayer( holder.exoPlayer);
                        holder.exoPlayer.setRepeatMode(holder.exoPlayer.REPEAT_MODE_ALL);
                        holder.exoPlayer.setVolume(0f);
                        holder.exoPlayer.prepare();
                        holder.exoPlayer.setPlayWhenReady(true);

                    } else {
                        holder.progressBar.setVisibility(View.GONE);
                        holder.image.setVisibility(View.VISIBLE);
                        holder.exoPlayerView.setVisibility(View.GONE);
                        if(wall.getType_wallpaper().equals("icon")||wall.getType_wallpaper().equals("cat")){
                            Glide.with(ctx).load(wall.getImage_wallpaper()).listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                                    holder.image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                    ((Activity)ctx).getWindow().getDecorView().setSystemUiVisibility(
                                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                                    return false;
                                }
                            }).into(holder.image);
                        }else {
                            Glide.with(ctx).load(wall.getImage_wallpaper()).listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                    ((Activity)ctx).getWindow().getDecorView().setSystemUiVisibility(
                                            View.SYSTEM_UI_FLAG_FULLSCREEN
                                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                                    return false;
                                }
                            }).into(holder.image);
                        }


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {


            if (!TextUtils.isEmpty(wall.getVideo_wallpaper())) {
                holder.image.setVisibility(View.GONE);
                holder.exoPlayerView.setVisibility(View.VISIBLE);
                holder.exoPlayer = new ExoPlayer.Builder(ctx).build();

                Uri videoUri = Uri.parse(wall.getVideo_wallpaper());
                MediaItem mediaItem = MediaItem.fromUri(videoUri);
                DefaultHttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true);
                DefaultDataSource.Factory defaultDataSourceFactory = new DefaultDataSource.Factory(ctx, httpDataSourceFactory);
                CacheDataSource.Factory cacheDataSourceFactory = new CacheDataSource.Factory()
                        .setCache(VideoCache.getInstance(ctx))
                        .setUpstreamDataSourceFactory(defaultDataSourceFactory)
                        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);

                MediaSource mediaSource = new ProgressiveMediaSource.Factory(cacheDataSourceFactory)
                        .createMediaSource(mediaItem);
                holder.exoPlayer.setMediaSource(mediaSource);
                holder.exoPlayer.addListener(new Player.Listener() {
                    @Override
                    public void onPlaybackStateChanged(@Player.State int playbackState) {
                        switch(playbackState) {
                            case Player.STATE_READY:
                                holder.progressBar.setVisibility(View.GONE);
                                break;
                            case Player.STATE_ENDED:
                                holder.exoPlayer.setPlayWhenReady(true);
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

                holder.exoPlayerView.setPlayer( holder.exoPlayer);
                holder.exoPlayer.setVolume(0f);
                holder.exoPlayer.prepare();
                holder.exoPlayer.setPlayWhenReady(true);

            }else {
                holder.image.setVisibility(View.VISIBLE);
                holder.exoPlayerView.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.GONE);
                
                if(wall.getType_wallpaper().equals("icon")||wall.getType_wallpaper().equals("cat")){
                    holder.image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    Glide.with(ctx).load(wall.getImage_wallpaper()).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            ActivityCompat.startPostponedEnterTransition(((Activity)ctx) );

                            return false;
                        }
                    }).into(holder.image);
                }else {
                    holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Glide.with(ctx).load(wall.getImage_wallpaper()).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            ActivityCompat.startPostponedEnterTransition(((Activity)ctx) );


                            return false;
                        }
                    }).into(holder.image);
                }

            }

        }

        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position==0){
                    Toast.makeText(ctx, "กำลังดาวน์โหลด...", Toast.LENGTH_SHORT).show();
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(((Activity)ctx) );
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdClicked() {
                                // Called when a click is recorded for an ad.
                                Log.d(TAG, "Ad was clicked.");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                Log.d(TAG, "Ad dismissed fullscreen content.");
                                mInterstitialAd = null;

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper").child(id);
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                                        Wall wall = snapshot.getValue(Wall.class);
                                        downloadFiles(((Activity)ctx), wall.getTag_wallpaper(), wall.getImage_wallpaper());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Log.e(TAG, "Ad failed to show fullscreen content.");
                                mInterstitialAd = null;
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper").child(id);
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                                        Wall wall = snapshot.getValue(Wall.class);
                                        downloadFiles(((Activity)ctx), wall.getTag_wallpaper(), wall.getImage_wallpaper());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                            @Override
                            public void onAdImpression() {
                                // Called when an impression is recorded for an ad.
                                Log.d(TAG, "Ad recorded an impression.");

                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.d(TAG, "Ad showed fullscreen content.");
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper").child(id);
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                                        Wall wall = snapshot.getValue(Wall.class);
                                        downloadFiles(((Activity)ctx), wall.getTag_wallpaper(), wall.getImage_wallpaper());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                    } else {

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper").child(id);
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                                Wall wall = snapshot.getValue(Wall.class);
                                downloadFiles(((Activity)ctx), wall.getTag_wallpaper(), wall.getImage_wallpaper());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                        Log.d("TAG", "The interstitial ad wasn't ready yet.");
                    }
                }else {
                    Toast.makeText(ctx, "กำลังดาวน์โหลด...", Toast.LENGTH_SHORT).show();
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(((Activity)ctx) );
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdClicked() {
                                // Called when a click is recorded for an ad.
                                Log.d(TAG, "Ad was clicked.");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                Log.d(TAG, "Ad dismissed fullscreen content.");
                                mInterstitialAd = null;

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper").child(id2);
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                                        Wall wall = snapshot.getValue(Wall.class);
                                        downloadFiles(((Activity)ctx), wall.getTag_wallpaper(), wall.getImage_wallpaper());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Log.e(TAG, "Ad failed to show fullscreen content.");
                                mInterstitialAd = null;
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper").child(id2);
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                                        Wall wall = snapshot.getValue(Wall.class);
                                        downloadFiles(((Activity)ctx), wall.getTag_wallpaper(), wall.getImage_wallpaper());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                            @Override
                            public void onAdImpression() {
                                // Called when an impression is recorded for an ad.
                                Log.d(TAG, "Ad recorded an impression.");

                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.d(TAG, "Ad showed fullscreen content.");
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper").child(id2);
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                                        Wall wall = snapshot.getValue(Wall.class);
                                        downloadFiles(((Activity)ctx), wall.getTag_wallpaper(), wall.getImage_wallpaper());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                    } else {

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper").child(id2);
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                                Wall wall = snapshot.getValue(Wall.class);
                                downloadFiles(((Activity)ctx), wall.getTag_wallpaper(), wall.getImage_wallpaper());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                        Log.d("TAG", "The interstitial ad wasn't ready yet.");
                    }
                }




            }
        });

        holder.setwall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position==0){
                    showmenu_wallpaper(id,wall.getType_wallpaper());
                }else {
                    showmenu_wallpaper(id2,wall.getType_wallpaper());
                }


            }
        });

    }


    @Override
    public int getItemCount() {
        return listwall.size();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        if(holder.exoPlayer!=null){
            holder.exoPlayer.setPlayWhenReady(false);

        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if(holder.exoPlayer!=null){
            holder.exoPlayer.setPlayWhenReady(true);

        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        CardView setwall;
        CardView download;
        StyledPlayerView exoPlayerView;
        SpinKitView progressBar;
        ExoPlayer exoPlayer;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.spin_kit);
            exoPlayerView = itemView.findViewById(R.id.exoplayer);
            setwall = itemView.findViewById(R.id.setwall);
            download = itemView.findViewById(R.id.download);
            image = itemView.findViewById(R.id.image_wallpaper);
        }

    }





    private void downloadFiles(Context context, String fileName, String Url){

        DownloadManager.Query query = null;
        Cursor c = null;
        DownloadManager downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(Url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName+".jpg");
        request.allowScanningByMediaScanner();
        downloadManager.enqueue(request);
        query = new DownloadManager.Query();
        if(query!=null) {
            query.setFilterByStatus(DownloadManager.STATUS_FAILED|DownloadManager.STATUS_PAUSED|DownloadManager.STATUS_SUCCESSFUL|
                    DownloadManager.STATUS_RUNNING|DownloadManager.STATUS_PENDING);
        } else {
            return;
        }
        c = downloadManager.query(query);
        if(c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch(status) {
                case DownloadManager.STATUS_PAUSED:
                    break;
                case DownloadManager.STATUS_PENDING:
                    Toast.makeText(ctx,"กำลังดาวน์โหลด...",Toast.LENGTH_SHORT).show();
                    break;
                case DownloadManager.STATUS_RUNNING:

                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    Toast.makeText(ctx,"บันทึกแล้ว",Toast.LENGTH_SHORT).show();
                    break;
                case DownloadManager.STATUS_FAILED:
                    Toast.makeText(ctx,"เกิดข้อผิดพลาด",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
    public void showmenu_wallpaper2(String id) {
        View bottomSheetView = ((Activity)ctx).getLayoutInflater().inflate(R.layout.bottom_wallpaper, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ctx, R.style.SheetDialogImage);
        RelativeLayout lock = bottomSheetView.findViewById(R.id.lock);
        RelativeLayout main = bottomSheetView.findViewById(R.id.main);
        RelativeLayout all = bottomSheetView.findViewById(R.id.all);
        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mInterstitialAd != null) {
                    mInterstitialAd.show(((Activity)ctx));
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                        @Override
                        public void onAdClicked() {
                            // Called when a click is recorded for an ad.
                            Log.d(TAG, "Ad was clicked.");
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Called when ad is dismissed.
                            // Set the ad reference to null so you don't show the ad a second time.
                            Log.d(TAG, "Ad dismissed fullscreen content.");
                            mInterstitialAd = null;
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper").child(id);
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                                    Wall wall = snapshot.getValue(Wall.class);
                                    Glide.with(ctx)
                                            .asBitmap()
                                            .load(wall.getImage_wallpaper())
                                            .into(new SimpleTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(Bitmap bitmap,
                                                                            Transition<? super Bitmap> transition) {
                                                    WallpaperManager myWallpaperManager
                                                            = WallpaperManager.getInstance(ctx);
                                                    try {
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                            myWallpaperManager.setBitmap(bitmap,null,true,WallpaperManager.FLAG_LOCK);//For Lock screen
                                                            Toast.makeText(ctx, "ตั้งค่าเรียบร้อย", Toast.LENGTH_SHORT).show();
                                                            bottomSheetDialog.dismiss();
                                                        }
                                                        else{
                                                            Toast.makeText(ctx, "อุปกรณ์ของคุณไม่รองรับหน้าจอล็อค",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }

                                                    } catch (IOException e) {
                                                        // TODO Auto-generated catch block
                                                        e.printStackTrace();
                                                    }

                                                }

                                            });
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            // Called when ad fails to show.
                            Log.e(TAG, "Ad failed to show fullscreen content.");
                            mInterstitialAd = null;
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper").child(id);
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                                    Wall wall = snapshot.getValue(Wall.class);
                                    Glide.with(ctx)
                                            .asBitmap()
                                            .load(wall.getImage_wallpaper())
                                            .into(new SimpleTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(Bitmap bitmap,
                                                                            Transition<? super Bitmap> transition) {
                                                    WallpaperManager myWallpaperManager
                                                            = WallpaperManager.getInstance(ctx);
                                                    try {
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                            myWallpaperManager.setBitmap(bitmap,null,true,WallpaperManager.FLAG_LOCK);//For Lock screen
                                                            Toast.makeText(ctx, "ตั้งค่าเรียบร้อย", Toast.LENGTH_SHORT).show();
                                                            bottomSheetDialog.dismiss();
                                                        }
                                                        else{
                                                            Toast.makeText(ctx, "อุปกรณ์ของคุณไม่รองรับหน้าจอล็อค",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }

                                                    } catch (IOException e) {
                                                        // TODO Auto-generated catch block
                                                        e.printStackTrace();
                                                    }

                                                }

                                            });
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        @Override
                        public void onAdImpression() {
                            // Called when an impression is recorded for an ad.
                            Log.d(TAG, "Ad recorded an impression.");

                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            // Called when ad is shown.
                            Log.d(TAG, "Ad showed fullscreen content.");
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper").child(id);
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                                    Wall wall = snapshot.getValue(Wall.class);
                                    Glide.with(ctx)
                                            .asBitmap()
                                            .load(wall.getImage_wallpaper())
                                            .into(new SimpleTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(Bitmap bitmap,
                                                                            Transition<? super Bitmap> transition) {
                                                    WallpaperManager myWallpaperManager
                                                            = WallpaperManager.getInstance(ctx);
                                                    try {
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                            myWallpaperManager.setBitmap(bitmap,null,true,WallpaperManager.FLAG_LOCK);//For Lock screen
                                                            Toast.makeText(ctx, "ตั้งค่าเรียบร้อย", Toast.LENGTH_SHORT).show();
                                                            bottomSheetDialog.dismiss();
                                                        }
                                                        else{
                                                            Toast.makeText(ctx, "อุปกรณ์ของคุณไม่รองรับหน้าจอล็อค",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }

                                                    } catch (IOException e) {
                                                        // TODO Auto-generated catch block
                                                        e.printStackTrace();
                                                    }

                                                }

                                            });
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });
                } else {

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper").child(id);
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                            Wall wall = snapshot.getValue(Wall.class);
                            Glide.with(ctx)
                                    .asBitmap()
                                    .load(wall.getImage_wallpaper())
                                    .into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap bitmap,
                                                                    Transition<? super Bitmap> transition) {
                                            WallpaperManager myWallpaperManager
                                                    = WallpaperManager.getInstance(ctx);
                                            try {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                    myWallpaperManager.setBitmap(bitmap,null,true,WallpaperManager.FLAG_LOCK);//For Lock screen
                                                    Toast.makeText(ctx, "ตั้งค่าเรียบร้อย", Toast.LENGTH_SHORT).show();
                                                    bottomSheetDialog.dismiss();
                                                }
                                                else{
                                                    Toast.makeText(ctx, "อุปกรณ์ของคุณไม่รองรับหน้าจอล็อค",
                                                            Toast.LENGTH_SHORT).show();
                                                }

                                            } catch (IOException e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            }

                                        }

                                    });
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }



            }
        });
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(((Activity)ctx));
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                        @Override
                        public void onAdClicked() {
                            // Called when a click is recorded for an ad.
                            Log.d(TAG, "Ad was clicked.");
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Called when ad is dismissed.
                            // Set the ad reference to null so you don't show the ad a second time.
                            Log.d(TAG, "Ad dismissed fullscreen content.");
                            mInterstitialAd = null;
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper").child(id);
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                                    Wall wall = snapshot.getValue(Wall.class);
                                    Glide.with(ctx)
                                            .asBitmap()
                                            .load(wall.getImage_wallpaper())
                                            .into(new SimpleTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(Bitmap bitmap,
                                                                            Transition<? super Bitmap> transition) {
                                                    WallpaperManager myWallpaperManager
                                                            = WallpaperManager.getInstance(ctx);
                                                    try {
                                                        myWallpaperManager.setBitmap(bitmap,null,true,WallpaperManager.FLAG_SYSTEM);//For Lock screen
                                                        Toast.makeText(ctx, "ตั้งค่าเรียบร้อย", Toast.LENGTH_SHORT).show();
                                                        bottomSheetDialog.dismiss();
                                                    } catch (IOException e) {
                                                        // TODO Auto-generated catch block
                                                        e.printStackTrace();
                                                    }

                                                }
                                            });
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            // Called when ad fails to show.
                            Log.e(TAG, "Ad failed to show fullscreen content.");
                            mInterstitialAd = null;
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper").child(id);
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                                    Wall wall = snapshot.getValue(Wall.class);
                                    Glide.with(ctx)
                                            .asBitmap()
                                            .load(wall.getImage_wallpaper())
                                            .into(new SimpleTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(Bitmap bitmap,
                                                                            Transition<? super Bitmap> transition) {
                                                    WallpaperManager myWallpaperManager
                                                            = WallpaperManager.getInstance(ctx);
                                                    try {
                                                        myWallpaperManager.setBitmap(bitmap,null,true,WallpaperManager.FLAG_SYSTEM);//For Lock screen
                                                        Toast.makeText(ctx, "ตั้งค่าเรียบร้อย", Toast.LENGTH_SHORT).show();
                                                        bottomSheetDialog.dismiss();
                                                    } catch (IOException e) {
                                                        // TODO Auto-generated catch block
                                                        e.printStackTrace();
                                                    }

                                                }
                                            });
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        @Override
                        public void onAdImpression() {
                            // Called when an impression is recorded for an ad.
                            Log.d(TAG, "Ad recorded an impression.");
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            // Called when ad is shown.
                            Log.d(TAG, "Ad showed fullscreen content.");
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper").child(id);
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                                    Wall wall = snapshot.getValue(Wall.class);
                                    Glide.with(ctx)
                                            .asBitmap()
                                            .load(wall.getImage_wallpaper())
                                            .into(new SimpleTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(Bitmap bitmap,
                                                                            Transition<? super Bitmap> transition) {
                                                    WallpaperManager myWallpaperManager
                                                            = WallpaperManager.getInstance(ctx);
                                                    try {
                                                        myWallpaperManager.setBitmap(bitmap,null,true,WallpaperManager.FLAG_SYSTEM);//For Lock screen
                                                        Toast.makeText(ctx, "ตั้งค่าเรียบร้อย", Toast.LENGTH_SHORT).show();
                                                        bottomSheetDialog.dismiss();
                                                    } catch (IOException e) {
                                                        // TODO Auto-generated catch block
                                                        e.printStackTrace();
                                                    }

                                                }
                                            });
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    });
                } else {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper").child(id);
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                            Wall wall = snapshot.getValue(Wall.class);
                            Glide.with(ctx)
                                    .asBitmap()
                                    .load(wall.getImage_wallpaper())
                                    .into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap bitmap,
                                                                    Transition<? super Bitmap> transition) {
                                            WallpaperManager myWallpaperManager
                                                    = WallpaperManager.getInstance(ctx);
                                            try {
                                                myWallpaperManager.setBitmap(bitmap);
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                    myWallpaperManager.setBitmap(bitmap,null,true,WallpaperManager.FLAG_SYSTEM);//For Lock screen

                                                    Toast.makeText(ctx, "ตั้งค่าเรียบร้อย", Toast.LENGTH_SHORT).show();
                                                    bottomSheetDialog.dismiss();
                                                }
                                                else{
                                                    Toast.makeText(ctx, "อุปกรณ์ของคุณไม่รองรับ",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (IOException e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            }

                                        }
                                    });
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

            }
        });
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper").child(id);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                        Wall wall = snapshot.getValue(Wall.class);
                        Glide.with(ctx)
                                .asBitmap()
                                .load(wall.getImage_wallpaper())
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap bitmap,
                                                                Transition<? super Bitmap> transition) {
                                        WallpaperManager myWallpaperManager
                                                = WallpaperManager.getInstance(ctx);
                                        try {
                                            myWallpaperManager.setBitmap(bitmap);
                                            Toast.makeText(ctx, "ตั้งค่าเรียบร้อย", Toast.LENGTH_SHORT).show();
                                            bottomSheetDialog.dismiss();
                                        } catch (IOException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        });

        bottomSheetDialog.setContentView(bottomSheetView);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
        bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // do something
            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // do something
            }
        });
        BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // do something
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // do something
            }
        };
        bottomSheetDialog.show();
    }


    public void showmenu_wallpaper(String id,String type) {
        View bottomSheetView = ((Activity)ctx).getLayoutInflater().inflate(R.layout.bottom_wallpaper, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ctx, R.style.SheetDialogImage);
        RelativeLayout lock = bottomSheetView.findViewById(R.id.lock);
        RelativeLayout main = bottomSheetView.findViewById(R.id.main);
        RelativeLayout all = bottomSheetView.findViewById(R.id.all);
        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!type.equals("video")){
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(((Activity)ctx));
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdClicked() {
                                // Called when a click is recorded for an ad.
                                Log.d(TAG, "Ad was clicked.");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                Log.d(TAG, "Ad dismissed fullscreen content.");
                                mInterstitialAd = null;
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper").child(id);
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                                        Wall wall = snapshot.getValue(Wall.class);
                                        Glide.with(ctx)
                                                .asBitmap()
                                                .load(wall.getImage_wallpaper())
                                                .into(new SimpleTarget<Bitmap>() {
                                                    @Override
                                                    public void onResourceReady(Bitmap bitmap,
                                                                                Transition<? super Bitmap> transition) {
                                                        WallpaperManager myWallpaperManager
                                                                = WallpaperManager.getInstance(ctx);
                                                        try {
                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                                myWallpaperManager.setBitmap(bitmap,null,true,WallpaperManager.FLAG_LOCK);//For Lock screen
                                                                Toast.makeText(ctx, "ตั้งค่าเรียบร้อย", Toast.LENGTH_SHORT).show();
                                                                bottomSheetDialog.dismiss();
                                                            }
                                                            else{
                                                                Toast.makeText(ctx, "อุปกรณ์ของคุณไม่รองรับหน้าจอล็อค",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }

                                                        } catch (IOException e) {
                                                            // TODO Auto-generated catch block
                                                            e.printStackTrace();
                                                        }

                                                    }

                                                });
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Log.e(TAG, "Ad failed to show fullscreen content.");
                                mInterstitialAd = null;
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper").child(id);
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                                        Wall wall = snapshot.getValue(Wall.class);
                                        Glide.with(ctx)
                                                .asBitmap()
                                                .load(wall.getImage_wallpaper())
                                                .into(new SimpleTarget<Bitmap>() {
                                                    @Override
                                                    public void onResourceReady(Bitmap bitmap,
                                                                                Transition<? super Bitmap> transition) {
                                                        WallpaperManager myWallpaperManager
                                                                = WallpaperManager.getInstance(ctx);
                                                        try {
                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                                myWallpaperManager.setBitmap(bitmap,null,true,WallpaperManager.FLAG_LOCK);//For Lock screen
                                                                Toast.makeText(ctx, "ตั้งค่าเรียบร้อย", Toast.LENGTH_SHORT).show();
                                                                bottomSheetDialog.dismiss();
                                                            }
                                                            else{
                                                                Toast.makeText(ctx, "อุปกรณ์ของคุณไม่รองรับหน้าจอล็อค",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }

                                                        } catch (IOException e) {
                                                            // TODO Auto-generated catch block
                                                            e.printStackTrace();
                                                        }

                                                    }

                                                });
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                            @Override
                            public void onAdImpression() {
                                // Called when an impression is recorded for an ad.
                                Log.d(TAG, "Ad recorded an impression.");

                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.d(TAG, "Ad showed fullscreen content.");
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper").child(id);
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                                        Wall wall = snapshot.getValue(Wall.class);
                                        Glide.with(ctx)
                                                .asBitmap()
                                                .load(wall.getImage_wallpaper())
                                                .into(new SimpleTarget<Bitmap>() {
                                                    @Override
                                                    public void onResourceReady(Bitmap bitmap,
                                                                                Transition<? super Bitmap> transition) {
                                                        WallpaperManager myWallpaperManager
                                                                = WallpaperManager.getInstance(ctx);
                                                        try {
                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                                myWallpaperManager.setBitmap(bitmap,null,true,WallpaperManager.FLAG_LOCK);//For Lock screen
                                                                Toast.makeText(ctx, "ตั้งค่าเรียบร้อย", Toast.LENGTH_SHORT).show();
                                                                bottomSheetDialog.dismiss();
                                                            }
                                                            else{
                                                                Toast.makeText(ctx, "อุปกรณ์ของคุณไม่รองรับหน้าจอล็อค",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }

                                                        } catch (IOException e) {
                                                            // TODO Auto-generated catch block
                                                            e.printStackTrace();
                                                        }

                                                    }

                                                });
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                    } else {

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper").child(id);
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                                Wall wall = snapshot.getValue(Wall.class);
                                Glide.with(ctx)
                                        .asBitmap()
                                        .load(wall.getImage_wallpaper())
                                        .into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(Bitmap bitmap,
                                                                        Transition<? super Bitmap> transition) {
                                                WallpaperManager myWallpaperManager
                                                        = WallpaperManager.getInstance(ctx);
                                                try {
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                        myWallpaperManager.setBitmap(bitmap,null,true,WallpaperManager.FLAG_LOCK);//For Lock screen
                                                        Toast.makeText(ctx, "ตั้งค่าเรียบร้อย", Toast.LENGTH_SHORT).show();
                                                        bottomSheetDialog.dismiss();
                                                    }
                                                    else{
                                                        Toast.makeText(ctx, "อุปกรณ์ของคุณไม่รองรับหน้าจอล็อค",
                                                                Toast.LENGTH_SHORT).show();
                                                    }

                                                } catch (IOException e) {
                                                    // TODO Auto-generated catch block
                                                    e.printStackTrace();
                                                }

                                            }

                                        });
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Log.d("TAG", "The interstitial ad wasn't ready yet.");
                    }

                }else {
                    Toast.makeText(ctx, "kk", Toast.LENGTH_SHORT).show();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper").child(id);
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                            Wall wall = snapshot.getValue(Wall.class);
                            InputStream in =null;
                            Bitmap bmp=null;
                            int responseCode = -1;
                            try{

                                URL url = new URL(wall.getVideo_wallpaper());
                                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                                con.setDoInput(true);
                                con.connect();
                                responseCode = con.getResponseCode();
                                if(responseCode == HttpURLConnection.HTTP_OK)
                                {
                                    //download
                                    in = con.getInputStream();
                                    bmp = BitmapFactory.decodeStream(in);
                                    in.close();
                                    WallpaperManager myWallpaperManager
                                            = WallpaperManager.getInstance(ctx);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                                        myWallpaperManager.setBitmap(bmp,null,true,WallpaperManager.FLAG_SYSTEM);//For Lock screen

                                        bottomSheetDialog.dismiss();
                                    }
                                }

                            }
                            catch(Exception ex){
                                Log.e("Exception",ex.toString());
                                Toast.makeText(ctx, ex.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }



            }
        });
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(((Activity)ctx));
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                        @Override
                        public void onAdClicked() {
                            // Called when a click is recorded for an ad.
                            Log.d(TAG, "Ad was clicked.");
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Called when ad is dismissed.
                            // Set the ad reference to null so you don't show the ad a second time.
                            Log.d(TAG, "Ad dismissed fullscreen content.");
                            mInterstitialAd = null;
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper").child(id);
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                                    Wall wall = snapshot.getValue(Wall.class);
                                    Glide.with(ctx)
                                            .asBitmap()
                                            .load(wall.getImage_wallpaper())
                                            .into(new SimpleTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(Bitmap bitmap,
                                                                            Transition<? super Bitmap> transition) {
                                                    WallpaperManager myWallpaperManager
                                                            = WallpaperManager.getInstance(ctx);
                                                    try {
                                                        myWallpaperManager.setBitmap(bitmap,null,true,WallpaperManager.FLAG_SYSTEM);//For Lock screen
                                                        Toast.makeText(ctx, "ตั้งค่าเรียบร้อย", Toast.LENGTH_SHORT).show();
                                                        bottomSheetDialog.dismiss();
                                                    } catch (IOException e) {
                                                        // TODO Auto-generated catch block
                                                        e.printStackTrace();
                                                    }

                                                }
                                            });
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            // Called when ad fails to show.
                            Log.e(TAG, "Ad failed to show fullscreen content.");
                            mInterstitialAd = null;
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper").child(id);
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                                    Wall wall = snapshot.getValue(Wall.class);
                                    Glide.with(ctx)
                                            .asBitmap()
                                            .load(wall.getImage_wallpaper())
                                            .into(new SimpleTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(Bitmap bitmap,
                                                                            Transition<? super Bitmap> transition) {
                                                    WallpaperManager myWallpaperManager
                                                            = WallpaperManager.getInstance(ctx);
                                                    try {
                                                        myWallpaperManager.setBitmap(bitmap,null,true,WallpaperManager.FLAG_SYSTEM);//For Lock screen
                                                        Toast.makeText(ctx, "ตั้งค่าเรียบร้อย", Toast.LENGTH_SHORT).show();
                                                        bottomSheetDialog.dismiss();
                                                    } catch (IOException e) {
                                                        // TODO Auto-generated catch block
                                                        e.printStackTrace();
                                                    }

                                                }
                                            });
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        @Override
                        public void onAdImpression() {
                            // Called when an impression is recorded for an ad.
                            Log.d(TAG, "Ad recorded an impression.");
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            // Called when ad is shown.
                            Log.d(TAG, "Ad showed fullscreen content.");
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper").child(id);
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                                    Wall wall = snapshot.getValue(Wall.class);
                                    Glide.with(ctx)
                                            .asBitmap()
                                            .load(wall.getImage_wallpaper())
                                            .into(new SimpleTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(Bitmap bitmap,
                                                                            Transition<? super Bitmap> transition) {
                                                    WallpaperManager myWallpaperManager
                                                            = WallpaperManager.getInstance(ctx);
                                                    try {
                                                        myWallpaperManager.setBitmap(bitmap,null,true,WallpaperManager.FLAG_SYSTEM);//For Lock screen
                                                        Toast.makeText(ctx, "ตั้งค่าเรียบร้อย", Toast.LENGTH_SHORT).show();
                                                        bottomSheetDialog.dismiss();
                                                    } catch (IOException e) {
                                                        // TODO Auto-generated catch block
                                                        e.printStackTrace();
                                                    }

                                                }
                                            });
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    });
                } else {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper").child(id);
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                            Wall wall = snapshot.getValue(Wall.class);
                            Glide.with(ctx)
                                    .asBitmap()
                                    .load(wall.getImage_wallpaper())
                                    .into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap bitmap,
                                                                    Transition<? super Bitmap> transition) {
                                            WallpaperManager myWallpaperManager
                                                    = WallpaperManager.getInstance(ctx);
                                            try {
                                                myWallpaperManager.setBitmap(bitmap);
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                    myWallpaperManager.setBitmap(bitmap,null,true,WallpaperManager.FLAG_SYSTEM);//For Lock screen

                                                    Toast.makeText(ctx, "ตั้งค่าเรียบร้อย", Toast.LENGTH_SHORT).show();
                                                    bottomSheetDialog.dismiss();
                                                }
                                                else{
                                                    Toast.makeText(ctx, "อุปกรณ์ของคุณไม่รองรับ",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (IOException e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            }

                                        }
                                    });
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

            }
        });
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Wallpaper").child(id);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                        Wall wall = snapshot.getValue(Wall.class);
                        Glide.with(ctx)
                                .asBitmap()
                                .load(wall.getImage_wallpaper())
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap bitmap,
                                                                Transition<? super Bitmap> transition) {
                                        WallpaperManager myWallpaperManager
                                                = WallpaperManager.getInstance(ctx);
                                        try {
                                            myWallpaperManager.setBitmap(bitmap);
                                            Toast.makeText(ctx, "ตั้งค่าเรียบร้อย", Toast.LENGTH_SHORT).show();
                                            bottomSheetDialog.dismiss();
                                        } catch (IOException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        });

        bottomSheetDialog.setContentView(bottomSheetView);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
        bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // do something
            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // do something
            }
        });
        BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // do something
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // do something
            }
        };
        bottomSheetDialog.show();
    }

}
