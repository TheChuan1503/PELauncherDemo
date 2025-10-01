package dev1503.pelauncher;

import static dev1503.pelauncher.Global.MINECRAFT_PACKAGE_NAME;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Bridge {
    public static void beforeOnCreate(BridgeActivity activity) {
        System.out.println("Bridge->beforeOnCreate");
        try {
            PackageManager packageManager = activity.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(MINECRAFT_PACKAGE_NAME, PackageManager.GET_META_DATA);
            AssetManager assets = activity.getAssets();
            Method addAssetPath = assets.getClass().getDeclaredMethod(
                    "addAssetPath", String.class);
            addAssetPath.invoke(assets, applicationInfo.sourceDir);

            Field cgpsui = Class.forName("com.google.android.gms.common.R$string").getDeclaredField("common_google_play_services_unknown_issue");
            cgpsui.setAccessible(true);
            cgpsui.setInt(Class.forName("com.google.android.gms.common.R$string"), android.R.string.ok);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static void afterOnCreate(BridgeActivity activity) {
        System.out.println("Bridge->afterOnCreate");
    }
}
