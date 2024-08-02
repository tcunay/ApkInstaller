package com.xbet.installapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import java.io.File;
import java.util.Objects;

public class ApkInstaller {

    public static void InstallAPK(String apkPath, Context context, InstallCallback callback) {
        File file = new File(apkPath);

        if (file.exists()) {
            Uri uri;
            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            int flags;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                uri = InstallApplicationFileProvider.getUriForFile(Objects.requireNonNull(context),
                        "com.xbet.installapplication.installapplicationfileprovider", file);
                flags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_SINGLE_TOP;
            } else {
                uri = Uri.fromFile(file);
                flags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP;
            }

            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.setFlags(flags);

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

    private static boolean CanPackageInstallAllowed(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return context.getPackageManager().canRequestPackageInstalls();
        } else {
            // На версиях Android ниже O это разрешение управляется вручную пользователем
            return true;
        }
    }

    public static void OpenSettings(Context context) {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
        } else {
            // На версиях Android ниже O перенаправляем пользователя в основные настройки
            intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
        }
        context.startActivity(intent);
    }

    public static void RestartApp(Context context) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            Intent intent = activity.getIntent();
            activity.finish();
            activity.startActivity(intent);
        }
    }

    public interface InstallCallback {
        void onPackageInstalled(String message);
        void onInstallFailed(String message);
    }
}

