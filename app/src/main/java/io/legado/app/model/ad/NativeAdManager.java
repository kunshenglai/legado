package io.legado.app.model.ad;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.bytedance.sdk.openadsdk.mediation.ad.MediationExpressRenderListener;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

/*
 * Copyright (C) 2004 - 2023 UCWeb Inc. All Rights Reserved.
 * Description : 描述当前文件的功能和使用范围
 * Attention: 如果是公共类，仔细说明使用方法和注意事项：特别是一些设计上的职责边界
 *
 * Created by weiyi@alibaba-inc.com on 2023/8/19
 */
public class NativeAdManager {
    private final static String TAG = "NativeAdManager";
    private final static NativeAdManager sInstance = new NativeAdManager();
    private Queue<TTFeedAd> mAdQueue = new ArrayDeque<>();
    private TTAdNative.FeedAdListener mFeedAdListener; // 广告加载监听器
    private MediationExpressRenderListener mExpressAdInteractionListener; // 模板广告展示监听器
    private TTNativeAd.AdInteractionListener mAdInteractionListener; // 自渲染广告展示监听器
    private NativeAdManager() {

    }

    public static NativeAdManager getInstance() {
        return sInstance;
    }

     public void getAd(@NonNull Context context, @NonNull IAdListener listener) {
         Log.d(TAG, "getAd: ad queue size: " + mAdQueue.size());
         for (TTFeedAd ad : mAdQueue) {
             Log.d(TAG, "cache ad, title: " + ad.getTitle() + " description: " + ad.getDescription());
         }

         TTFeedAd ad = mAdQueue.poll();
         if (ad != null) {
             Log.d(TAG, "get ad result, title: " + ad.getTitle() + " description: " + ad.getDescription());
             listener.onAdLoaded(ad);
         }

         if (mAdQueue.size() == 0) {
            preloadAd(context, listener);
        }

     }

     public void preloadAd(@NonNull final Context context, @Nullable IAdListener listener) {
        if (mAdQueue.isEmpty()) {
            Log.d(TAG, "load feed ad");

            loadFeedAd(context, new TTAdNative.FeedAdListener() {
                @Override
                public void onError(int error, String errorMessage) {
                    Log.d(TAG, "onError, error code: " + error + " errorMessage: " + errorMessage);
                }

                @Override
                public void onFeedAdLoad(List<TTFeedAd> list) {
                    if (list != null) {
                        mAdQueue.addAll(list);
                    }
                    if (listener != null) {
                        getAd(context, listener);
                    }
                }
            });
        }
     }


    private void loadFeedAd(@NonNull Context context, @NonNull TTAdNative.FeedAdListener listener) {
        /** 1、创建AdSlot对象 */
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId("945493689")
                .setImageAcceptedSize(640, 320)
                .setAdCount(3)
                .build();

        /** 2、创建TTAdNative对象 */
        TTAdNative adNativeLoader = TTAdSdk.getAdManager().createAdNative(context);

        /** 3、创建加载、展示监听器 */
        //initListeners();

        /** 4、加载广告 */
        adNativeLoader.loadFeedAd(adSlot, listener);
    }

    private void initListeners() {
        // 模板广告展示监听器
        // 自渲染广告展示监听器
        this.mAdInteractionListener = new TTNativeAd.AdInteractionListener() {
            @Override
            public void onAdClicked(View view, TTNativeAd ttNativeAd) {
                Log.d(TAG, "feed click");
            }

            @Override
            public void onAdCreativeClick(View view, TTNativeAd ttNativeAd) {
                Log.d(TAG, "feed creative click");
            }

            @Override
            public void onAdShow(TTNativeAd ttNativeAd) {
                Log.d(TAG, "feed show");
            }
        };
    }
    public interface IAdListener {
        void onAdLoaded(@NonNull TTFeedAd ad);
        void onError(int errorCode, @Nullable String errorMessage);
    }
}
