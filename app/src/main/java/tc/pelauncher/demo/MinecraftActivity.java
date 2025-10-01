package tc.pelauncher.demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;

import com.mojang.minecraftpe.MainActivity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import dev1503.pelauncher.BridgeActivity;
import dev1503.pelauncher.Global;

public class MinecraftActivity extends BridgeActivity {
    static final String MINECRAFT_PACKAGE_NAME = Global.MINECRAFT_PACKAGE_NAME;

    static {
//        System.load("/data/app/com.mojang.minecraftpe-ZoFX8C6EMHJJsu6TSJJUvA==/lib/arm64/libfmod.so");
        System.loadLibrary("demo");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("MinecraftActivity onCreate");
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(MINECRAFT_PACKAGE_NAME, PackageManager.GET_META_DATA);
            AssetManager assets = getAssets();
            Method addAssetPath = assets.getClass().getDeclaredMethod(
                    "addAssetPath", String.class);
            addAssetPath.invoke(assets, applicationInfo.sourceDir);

//            Field cgpsui = Class.forName("com.google.android.gms.common.R$string").getDeclaredField("common_google_play_services_unknown_issue");
//            cgpsui.setAccessible(true);
//            cgpsui.setInt(Class.forName("com.google.android.gms.common.R$string"), android.R.string.ok);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    void loadNativeLibs() {
        PackageManager packageManager = getPackageManager();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(MINECRAFT_PACKAGE_NAME, PackageManager.GET_META_DATA);
            String libPath = applicationInfo.nativeLibraryDir;
            System.load(libPath + "/libfmod.so");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        System.out.println("MinecraftActivity->attachBaseContext");
        System.exit(1);
    }
}
