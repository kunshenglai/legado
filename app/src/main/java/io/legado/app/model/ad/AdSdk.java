package io.legado.app.model.ad;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTCustomController;
import com.bytedance.sdk.openadsdk.mediation.init.MediationPrivacyConfig;

import java.util.concurrent.atomic.AtomicBoolean;

/*
 * Copyright (C) 2004 - 2023 UCWeb Inc. All Rights Reserved.
 * Description : 描述当前文件的功能和使用范围
 * Attention: 如果是公共类，仔细说明使用方法和注意事项：特别是一些设计上的职责边界
 *
 * Created by weiyi@alibaba-inc.com on 2023/8/19
 */
public class AdSdk {
    public static Context sContext = null;
    private final static AtomicBoolean sInitFinished = new AtomicBoolean(false);

    public static void init(@NonNull Context context, @NonNull TTAdSdk.InitCallback callback) {
        sContext = context.getApplicationContext();
        if (!sInitFinished.getAndSet(true)) {
            initAdSdk(context, callback);
        } else {
            callback.success();
        }
    }

    private static void initAdSdk(@NonNull Context context, @NonNull TTAdSdk.InitCallback callback) {
        TTAdSdk.init(context, new TTAdConfig.Builder() //fixme laiks
                /**
                 * 注：需要替换成在媒体平台申请的appID ，切勿直接复制
                 */
                .appId("5001121")
                .appName("APP测试媒体")
                /**
                 * 上线前需要关闭debug开关，否则会影响性能
                 */
                .debug(true)
                /**
                 * 使用聚合功能此开关必须设置为true，默认为false，不会初始化聚合模板，聚合功能会吟唱
                 */
                .useMediation(true)
                .customController(getTTCustomController()) // 隐私合规设置
                .build(), callback);
    }

    //函数返回值表示隐私开关开启状态，未重写函数使用默认值
    private static TTCustomController getTTCustomController() {
        return new TTCustomController() {

            @Override
            public boolean isCanUseWifiState() {
                return super.isCanUseWifiState();
            }

            @Override
            public String getMacAddress() {
                return super.getMacAddress();
            }

            @Override
            public boolean isCanUseWriteExternal() {
                return super.isCanUseWriteExternal();
            }

            @Override
            public String getDevOaid() {
                return super.getDevOaid();
            }

            @Override
            public boolean isCanUseAndroidId() {
                return super.isCanUseAndroidId();
            }

            @Override
            public String getAndroidId() {
                return super.getAndroidId();
            }

            @Override
            public MediationPrivacyConfig getMediationPrivacyConfig() {
                return new MediationPrivacyConfig() {

                    @Override
                    public boolean isLimitPersonalAds() {
                        return super.isLimitPersonalAds();
                    }

                    @Override
                    public boolean isProgrammaticRecommend() {
                        return super.isProgrammaticRecommend();
                    }
                };
            }

            @Override
            public boolean isCanUsePermissionRecordAudio() {
                return super.isCanUsePermissionRecordAudio();
            }
        };
    }
}
