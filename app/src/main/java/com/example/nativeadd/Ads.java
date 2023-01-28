package com.example.nativeadd;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.util.ArrayList;
import java.util.List;

public class Ads {


    private static final String TAG = "Ads";
    //nativeads
    public static com.google.android.gms.ads.nativead.NativeAd googlenativeAd1;
    public static NativeAd fbnativeAd;
    public static NativeAdLayout fbnativeAdLayout;
    public static LinearLayout fbadView1;



    //fbnativeads
    public static void fbloadNativeAd(Activity activity) {
        // Instantiate a NativeAd object.
        // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
        // now, while you are testing and replace it later when you have signed up.
        // While you are using this temporary code you will only get test ads and if you release
        // your code like this to the Google Play your users will not receive ads (you will get a no fill error).

        fbnativeAd = new NativeAd(activity, Config.fb_native);

        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                // Native ad finished downloading all assets
                Log.e(TAG, "Native ad finished downloading all assets.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Native ad failed to load
                googleloadNativeAd(activity);

                Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Native ad is loaded and ready to be displayed
                Log.d(TAG, "Native ad is loaded and ready to be displayed!");
                if (fbnativeAd == null || fbnativeAd != ad) {
                    return;
                }
                // Inflate Native Ad into Container
                inflateAd(fbnativeAd, activity);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Native ad clicked
                Log.d(TAG, "Native ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Native ad impression
                Log.d(TAG, "Native ad impression logged!");
            }
        };

        // Request an ad
        fbnativeAd.loadAd(
                fbnativeAd.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build());

        showNativeAdWithDelay(activity);
    }

    public static void inflateAd(NativeAd nativeAd, Activity activity) {

        nativeAd.unregisterView();

        // Add the Ad view into the ad container.

        fbnativeAdLayout = activity.findViewById(R.id.fl_adplaceholder);
//        fbnativeAdLayout = activity.findViewById(R.id.native_ad_container);

        LayoutInflater inflater = LayoutInflater.from(activity);
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.

        fbadView1 = (LinearLayout) inflater.inflate(R.layout.live_sixty_six_fb_native, fbnativeAdLayout, false);
//        fbadView1 = (LinearLayout) inflater.inflate(R.layout.fb_nativeads, fbnativeAdLayout, false);

        fbnativeAdLayout.addView(fbadView1);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = activity.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(activity, nativeAd, fbnativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        MediaView nativeAdIcon = fbadView1.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = fbadView1.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = fbadView1.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = fbadView1.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = fbadView1.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = fbadView1.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = fbadView1.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
                fbadView1, nativeAdMedia, nativeAdIcon, clickableViews);
    }

    public static void showNativeAdWithDelay(Activity activity) {
        /**
         * Here is an example for displaying the ad with delay;
         * Please do not copy the Handler into your project
         */
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            public void run() {
                // Check if nativeAd has been loaded successfully
                if (fbnativeAd == null || !fbnativeAd.isAdLoaded()) {
                    return;
                }
                // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
                if (fbnativeAd.isAdInvalidated()) {
                    return;
                }
                inflateAd(fbnativeAd, activity); // Inflate NativeAd into a container, same as in previous code examples
            }
        }, 1000 * 60 * 15); // Show the ad after 15 minutes
    }

    //googlenativeads
    @SuppressLint("MissingPermission")
    public static void googleloadNativeAd(Activity activity) {
        AdLoader.Builder builder = new AdLoader.Builder(activity, Config.admob_native);
        builder.forNativeAd(new com.google.android.gms.ads.nativead.NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(com.google.android.gms.ads.nativead.NativeAd nativeAd) {
                Log.d(TAG, "onNativeAdLoaded: " + nativeAd);
                if (googlenativeAd1 != null) {
                    googlenativeAd1.destroy();
                }

                googlenativeAd1 = nativeAd;
                FrameLayout frameLayout = (FrameLayout) activity.findViewById(R.id.fl_adplaceholder);
                NativeAdView unifiedNativeAdView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.live_sixty_six_ad_unified, null);

                populateUnifiedNativeAdView(nativeAd, unifiedNativeAdView);
                frameLayout.removeAllViews();
                frameLayout.addView(unifiedNativeAdView);
            }
        });

        builder.withAdListener(new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {

            }
        }).build().loadAd(new AdRequest.Builder().build());

    }


    public static void populateUnifiedNativeAdView(com.google.android.gms.ads.nativead.NativeAd nativeAd, NativeAdView nativeAdView) {
        nativeAdView.setMediaView(nativeAdView.findViewById(R.id.ad_media));
        nativeAdView.setHeadlineView(nativeAdView.findViewById(R.id.ad_headline));
        nativeAdView.setBodyView(nativeAdView.findViewById(R.id.ad_body));
        nativeAdView.setCallToActionView(nativeAdView.findViewById(R.id.ad_call_to_action));
        nativeAdView.setIconView(nativeAdView.findViewById(R.id.ad_app_icon));
        nativeAdView.setPriceView(nativeAdView.findViewById(R.id.ad_price));
        nativeAdView.setStarRatingView(nativeAdView.findViewById(R.id.ad_stars));
        nativeAdView.setStoreView(nativeAdView.findViewById(R.id.ad_store));
        nativeAdView.setAdvertiserView(nativeAdView.findViewById(R.id.ad_advertiser));
        ((TextView) nativeAdView.getHeadlineView()).setText(nativeAd.getHeadline());
        if (nativeAd.getBody() == null) {
            nativeAdView.getBodyView().setVisibility(4);
        } else {
            nativeAdView.getBodyView().setVisibility(0);
            ((TextView) nativeAdView.getBodyView()).setText(nativeAd.getBody());
        }
        if (nativeAd.getCallToAction() == null) {
            nativeAdView.getCallToActionView().setVisibility(4);
        } else {
            nativeAdView.getCallToActionView().setVisibility(0);
            ((Button) nativeAdView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }
        if (nativeAd.getIcon() == null) {
            nativeAdView.getIconView().setVisibility(8);
        } else {
            ((ImageView) nativeAdView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
            nativeAdView.getIconView().setVisibility(0);
        }
        if (nativeAd.getPrice() == null) {
            nativeAdView.getPriceView().setVisibility(4);
        } else {
            nativeAdView.getPriceView().setVisibility(0);
            ((TextView) nativeAdView.getPriceView()).setText(nativeAd.getPrice());
        }
        if (nativeAd.getStore() == null) {
            nativeAdView.getStoreView().setVisibility(4);
        } else {
            nativeAdView.getStoreView().setVisibility(0);
            ((TextView) nativeAdView.getStoreView()).setText(nativeAd.getStore());
        }
        if (nativeAd.getStarRating() == null) {
            nativeAdView.getStarRatingView().setVisibility(4);
        } else {
            ((RatingBar) nativeAdView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
            nativeAdView.getStarRatingView().setVisibility(0);
        }
        if (nativeAd.getAdvertiser() == null) {
            nativeAdView.getAdvertiserView().setVisibility(4);
        } else {
            ((TextView) nativeAdView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            nativeAdView.getAdvertiserView().setVisibility(0);
        }
        nativeAdView.setNativeAd(nativeAd);

    }
}
