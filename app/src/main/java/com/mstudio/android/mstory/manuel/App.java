package com.mstudio.android.mstory.manuel;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.FirebaseDatabase;


public class App extends Application implements Application.ActivityLifecycleCallbacks {
    Context mContext;
    private AppOpenAdManager appOpenAdManager;
    private Activity currentActivity;
  



    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
       // if (!appOpenAdManager.isShowingAd) {
         //   currentActivity = activity;
       // }

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
     //   appOpenAdManager.showAdIfAvailable(activity, new OnShowAdCompleteListener() {
         //   @Override
          //  public void onShowAdComplete() {

           // }
       // });
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        //appOpenAdManager.loadAd(activity);
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    public interface OnShowAdCompleteListener {
        void onShowAdComplete();
    }

    private class AppOpenAdManager {

        private static final String LOG_TAG = "AppOpenAdManager";
        private static final String AD_UNIT_ID = "ca-app-pub-8467206635180635/7715636978";

        private AppOpenAd appOpenAd = null;
        private boolean isLoadingAd = false;
        private boolean isShowingAd = false;


        public void showAdIfAvailable(
                @NonNull final Activity activity,
                @NonNull OnShowAdCompleteListener onShowAdCompleteListener){
            // If the app open ad is already showing, do not show the ad again.
            if (isShowingAd) {
                Log.d(LOG_TAG, "The app open ad is already showing.");
                return;
            }

            // If the app open ad is not available yet, invoke the callback then load the ad.
            if (!isAdAvailable()) {
                Log.d(LOG_TAG, "The app open ad is not ready yet.");
                onShowAdCompleteListener.onShowAdComplete();
                loadAd(activity);
                return;
            }
            appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    Log.d(LOG_TAG, "Ad dismissed fullscreen content.");
                    appOpenAd = null;
                    isShowingAd = false;

                    onShowAdCompleteListener.onShowAdComplete();
                    loadAd(activity);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    Log.d(LOG_TAG, adError.getMessage());
                    appOpenAd = null;
                    isShowingAd = false;

                    onShowAdCompleteListener.onShowAdComplete();
                    loadAd(activity);
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                    Log.d(LOG_TAG, "Ad showed fullscreen content.");
                }
            });
            isShowingAd = true;
            appOpenAd.show(activity);
        }

        public void loadAd(Context context) {

            if (isLoadingAd || isAdAvailable()) {
                return;
            }

            isLoadingAd = true;
            AdRequest request = new AdRequest.Builder().build();
            AppOpenAd.load(
                    context, AD_UNIT_ID, request,
                    AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                    new AppOpenAd.AppOpenAdLoadCallback() {
                        @Override
                        public void onAdLoaded(AppOpenAd ad) {
                            // Called when an app open ad has loaded.
                            Log.d(LOG_TAG, "Ad was loaded.");
                            appOpenAd = ad;
                            isLoadingAd = false;
                        }

                        @Override
                        public void onAdFailedToLoad(LoadAdError loadAdError) {
                            // Called when an app open ad has failed to load.
                            Log.d(LOG_TAG, loadAdError.getMessage());
                            isLoadingAd = false;
                        }
                    });

        }
        public AppOpenAdManager() {


        }

        private boolean isAdAvailable() {
            return appOpenAd != null;
        }


    }
    @Override
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(this);
        mContext = this;

      
        com.google.firebase.FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        MobileAds.initialize(
                this,
                new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {}
                });
        appOpenAdManager = new AppOpenAdManager();



    }
}
