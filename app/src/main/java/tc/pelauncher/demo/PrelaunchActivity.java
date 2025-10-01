package tc.pelauncher.demo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import dev1503.pelauncher.Global;

public class PrelaunchActivity extends Activity {
    final String MINECRAFT_PACKAGE_NAME = Global.MINECRAFT_PACKAGE_NAME;
    ApplicationInfo peApplicationInfo;
    Object pathList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("PrelaunchActivity->onCreate");
        super.onCreate(savedInstanceState);

        try {
            peApplicationInfo = getPackageManager().getApplicationInfo(MINECRAFT_PACKAGE_NAME, PackageManager.GET_META_DATA);
            pathList = getPathList(getClassLoader());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        clearCache();
        patchDex();
        patchNativeLibrary();
        loadNativeLibs();

        launch();
    }

    void patchDex() {
        try {
            Method addDexPath = pathList.getClass().getDeclaredMethod("addDexPath", String.class, File.class);
            ApplicationInfo mcInfo = peApplicationInfo;
            File cacheDexDir = new File(getCodeCacheDir(), "dex");
            if (!cacheDexDir.exists() && !cacheDexDir.mkdirs()) {
                throw new IOException("Failed to create cache directory");
            }

            final File patchedPEDex1 = new File(cacheDexDir, "classes.dex");
            copy(getAssets().open("pe_classes.dex"), patchedPEDex1);

            try (ZipFile zipFile = new ZipFile(mcInfo.sourceDir)) {
                for (int i = 2; i >= 0; i--) {
                    final String dexName = "classes" + (i == 0 ? "" : i) + ".dex";
                    ZipEntry dexFile = zipFile.getEntry(dexName);
                    if (dexFile != null) {
                        final File mcDex = new File(cacheDexDir, dexName);
                        if (!Objects.equals(dexName, "classes.dex")) {
                            copy(zipFile.getInputStream(dexFile), mcDex);
                        }
                        if (mcDex.setReadOnly()) {
                            addDexPath.invoke(pathList, mcDex.getAbsolutePath(), null);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void copy(InputStream from, File to) throws IOException {
        File parentDir = to.getParentFile();
        if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
            throw new IOException("Failed to create directories");
        }
        if (!to.exists() && !to.createNewFile()) {
            throw new IOException("Failed to create new file");
        }
        try (BufferedInputStream input = new BufferedInputStream(from);
             BufferedOutputStream output = new BufferedOutputStream(Files.newOutputStream(to.toPath()))) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        }
    }

    Object getPathList(ClassLoader classLoader) throws Exception {
        Field pathListField = Objects.requireNonNull(classLoader.getClass().getSuperclass()).getDeclaredField("pathList");
        pathListField.setAccessible(true);
        return pathListField.get(classLoader);
    }

    void patchNativeLibrary(){
        String libPath = peApplicationInfo.nativeLibraryDir;
        try {
            File cacheLibDir = new File(getCodeCacheDir(), "lib");
            if (!cacheLibDir.exists() && !cacheLibDir.mkdirs()) {
                throw new IOException("Failed to create cache directory");
            }
             Files.list(new File(libPath).toPath()).forEach(lib -> {
                 try {
                     Files.copy(lib, new File(cacheLibDir, lib.getFileName().toString()).toPath());
                     File so = new File(cacheLibDir, lib.getFileName().toString());
                     so.setExecutable(true);
                     so.setReadable(true);
                     so.setReadOnly();
                 } catch (IOException e) {
                     throw new RuntimeException(e);
                 }
             });

            Field libDirsField = pathList.getClass().getDeclaredField("nativeLibraryDirectories");
            libDirsField.setAccessible(true);
            List<File> libDirs = (List<File>) libDirsField.get(pathList);
            libDirs.add(cacheLibDir);
            libDirsField.set(pathList, libDirs);
            System.out.println(libDirs.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void launch() {
        try {
            getClassLoader().loadClass( "com.mojang.minecraftpe.MainActivity");
            startActivity(new Intent(this, getClassLoader().loadClass("dev1503.pelauncher.BridgeActivity")));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    void loadNativeLibs() {
        try {
            File cacheLibDir = new File(getCodeCacheDir(), "lib");
            Files.list(cacheLibDir.toPath()).forEach(lib -> {
                System.load(lib.toAbsolutePath().toString());
                System.out.println("Loaded native library: " + lib.toAbsolutePath().toString());
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void clearCache(){
        File cacheLibDir = new File(getCodeCacheDir(), "lib");
        File cacheDexDir = new File(getCodeCacheDir(), "dex");
        removeFolder(cacheLibDir);
        removeFolder(cacheDexDir);
    }

    void removeFolder(File folder){
        if(folder.isDirectory()){
            for(File file : folder.listFiles()){
                removeFolder(file);
            }
        }
        folder.delete();
    }
}
