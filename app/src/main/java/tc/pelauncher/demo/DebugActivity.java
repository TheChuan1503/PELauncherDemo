package tc.pelauncher.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.TextView;

import java.lang.reflect.Method;

public class DebugActivity extends Activity {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            TextView textView = new TextView(this);
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = null;
//            try {
//                applicationInfo = packageManager.getApplicationInfo("com.mojang.minecraftpe", PackageManager.GET_META_DATA);
//            } catch (PackageManager.NameNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//            String libPath = applicationInfo.nativeLibraryDir;
//            System.out.println("libPath: " + libPath);
//            textView.setText(libPath);
//            setContentView(textView);
//            try {
//                AssetManager assets = getAssets();
//                Method addAssetPath = assets.getClass().getDeclaredMethod(
//                        "addAssetPath", String.class);
//                addAssetPath.invoke(assets, applicationInfo.sourceDir);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//            startActivity(new Intent(this, com.mojang.minecraftpe.MainActivity.class));
            startActivity(new Intent(this, MinecraftActivity.class));
        }
}
