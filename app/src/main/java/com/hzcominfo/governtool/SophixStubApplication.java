package com.hzcominfo.governtool;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Keep;
import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixApplication;
import com.taobao.sophix.SophixEntry;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;

/**
 *  Create by Ljw on 2020/12/8 18:48
 * Sophix入口类，专门用于初始化Sophix，不应包含任何业务逻辑。
 * 此类必须继承自SophixApplication，onCreate方法不需要实现。
 * 此类不应与项目中的其他类有任何互相调用的逻辑，必须完全做到隔离。
 * AndroidManifest中设置application为此类，而SophixEntry中设为原先Application类。
 * 注意原先Application里不需要再重复初始化Sophix，并且需要避免混淆原先Application类。
 * 如有其它自定义改造，请咨询官方后妥善处理。
 */
public class SophixStubApplication extends SophixApplication {

    private final String TAG = "SophixStubApplication";

    // 此处SophixEntry应指定真正的Application，并且保证RealApplicationStub类名不被混淆。
    @Keep
    @SophixEntry(MyApplication.class)
    static class RealApplicationStub {}

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//         如果需要使用MultiDex，需要在此处调用。
//         MultiDex.install(this);
        initSophix();
    }
    private void initSophix() {
        String appVersion = "0.0.0";
        try {
            appVersion = this.getPackageManager()
                    .getPackageInfo(this.getPackageName(), 0)
                    .versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        final SophixManager instance = SophixManager.getInstance();
        instance.setContext(this)
            .setAppVersion(appVersion)
            .setSecretMetaData("333339203", "7a4160f9e1aa4429b5f3519c9a3249a0", "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCSPkQe0MFejmUEhC1I9qIihFFomDj16IzDNaF75I8tKxPvx1TpHqXuECvhrVwghLR14iZXAL8I9YmdywJSCPsGYDcxTuj8n1PhTI/KYMNjNaYdD+W7nk8sNM+AVjvuoe67VGLV6kRz3ZrVNcRQASUC5ZgOYyTsSuSlteUBoooyTiMVeZSE4iXUtFLYAp1apdzGuoHuwR56jPUIHGbFhRpsg0d6CIa8o4lGB/vB+7oCxJYUJqlSuknUr/qZIXhX/NLHNxA6QIwNz30FrFQcBFV6dnikZZrotR6ipgYOXilIvN8PW7FK/cZS3j8hEP4CRI/ry5Qv6wDk7OYKCgOgb8mxAgMBAAECggEAVDX6r+vxjWtdjA44rW0ny8x5vEr7cc7pT3dQd7ETKSVHI6maoWp+jelN+3QCOs9vwvnorezGFHifN/ewbGCze+ZnocMYsm9ks7WMjzLNEBHSh6Wh2jm8yW3XlsURWcZxW/mpuBhgNoADKob9djxSfaqLtq0sk8xEgY+OQTTBc1lgG8ZszREsgqY3RoZdyfbwlTqLCvITtbgmxlHIUkKMLIkc/Y8JzJWSK75QmhKQDbHTycnqoQHfHik8qY+OMAOQXjcN2br/pZXcDToo3J7xguPJqEBNbVYYoyhAyglzlxzNuGRm3sxhMeCMgcCzXd6jgjeDukNOLTlPv735XvqAAQKBgQDJ+I1BvyeFopx1LYLPs7LXBp0heI0PwAz/RCjGSmluts7Fkb/vcj326GOde1eTtd7l4D7WyJoa1UIOwJ9HsNHHHCv5ILdWanoXC8v5XhYZvlNtHCvTqQ2x9gKw7aGw8fiPH4btEX3rmDK+3aeg8btcMwEpJit1L2aO6sjBE9eFsQKBgQC5XVmr2DwguB5yTbOrMGvLULTkzMgjrX1keIcVsBiFK+hZ3ax9pq9wrtoo9EDLw7hEyysnWwfNbAvTG3Yadb6SsNA1QR6P0L8W6ZgisL6cice07qiVg35Na6iVWmiq986hmnqXFAufwC4RxRC8HNreDXTx6VDHqLiE1iScq3mEAQKBgDhy9KAUmio6iboibzY4Blsc6RvqwppNVhSeazsInzEnfOExXFDDQFhbGURxrceGBHeBVm7XgGLRifXvllUGMYuR7yRhJXzMo7T/QhI/XrPxQwXy4rj0vfQoSxZCRHfdGVh/OknWIriPQFeiQfC0v2YEh2WF2o2VIvf83QFB13HxAoGASW8E9kIs1915GllUA06kdaBceYPzCcdSNLpW8a2YmTFi1C8whXrCaAatIKpa/R5lYsOGLgovFh9ms9RePm9E90Iaye4pWFofScly3tNcQBGJT1F48fpsz+kFmV1+t7SCZJ0ZUb68OCZfWeTTE2LvBicYnDmNKfvaVa2sRXDdLAECgYBHtrgda2aAXhZ0w0FaLtqYiuiuOoK84Jvii1+395EFCMZ0mS/UysmAsddcI04Pb4lgTIfN6sQReOxqdYf+4QGTYlutf54DhTNZpMtQjuKhI/QAVZoPF9qjQSxeGU9FNqdS5sZY5ZHNr8wC+KiXsM76HuOmTTHwtiUTUvH/hkEsvA==")
            .setEnableDebug(false) //正式发布该参数必须为false, false会对补丁做签名校验, 否则就可能存在安全漏洞风险
            .setEnableFullLog()
            .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                @Override
                public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                    if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                        Log.i(TAG, "sophix load patch success!");
                    } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                        // 如果需要在后台重启，建议此处用SharePreference保存状态。
                        Log.i(TAG, "sophix preload patch success. restart app to make effect.");
                    }
                }
            }).initialize();
    }
}
