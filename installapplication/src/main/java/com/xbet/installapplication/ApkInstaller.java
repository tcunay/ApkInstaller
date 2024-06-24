package com.xbet.installapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import java.io.File;
import java.util.Objects;

public class ApkInstaller {

    public static void InstallAPK(String apkPath, Context context, InstallCallback callback) {
        File file = new File(apkPath);

        if (file.exists()) {
            Uri uri;
            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                uri = InstallApplicationFileProvider.getUriForFile(Objects.requireNonNull(context),
                        "com.xbet.installapplication.installapplicationfileprovider", file);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                uri = Uri.fromFile(file);
            }

            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
                callback.onPackageInstalled(context.getPackageName());
            } else {
                callback.onInstallFailed("There is no suitable application to install");
            }
        } else {
            callback.onInstallFailed("File does not exist");
        }
    }

    public interface InstallCallback {
        void onPackageInstalled(String message);
        void onInstallFailed(String message);
    }
}

