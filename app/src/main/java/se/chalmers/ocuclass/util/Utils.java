package se.chalmers.ocuclass.util;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import se.chalmers.ocuclass.ui.NfcDetectActivity;

/**
 * Created by admin on 2015-08-09.
 */
public class Utils {

    private static final String SCHEME = "package";
    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    private static final String APP_PKG_NAME_22 = "pkg";
    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";
    public static final int REQ_CODE_SETTINGS = 0x3f4f;



    public static int dpToPx(Context context, float dp){
        return (int)(context.getResources().getDisplayMetrics().density*dp);
    }

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int result = 0;
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }


    public static int parseColor(String value) {
        if (value == null) {
            return -1;
        } else {
            try {
                return Color.parseColor(value);
            } catch (IllegalArgumentException e) {
                return -1;
            }
        }
    }
    public static String millisToString(long l) {
        long h, m;
        h = l / 3600000;
        m = (l % 3600000) / 60000;
        if (h == 0) {
            return m + "m";
        } else {
            return h + "h " + m + "m";
        }
    }

    public static int getNavigationBarHeight(Context context){
        Resources resources = context.getResources();

        int id = resources.getIdentifier(
                resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_height_landscape",
                "dimen", "android");
        if (id > 0) {
            return resources.getDimensionPixelSize(id);
        }
        return 0;
    }

    public static int getActionBarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        int actionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        return actionBarSize;
    }

    public static void showInstalledAppDetails(Activity activity) {
        String packageName = activity.getApplicationContext().getPackageName();
        Intent intent = new Intent();
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) { // above 2.3
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(SCHEME, packageName, null);
            intent.setData(uri);
        } else { // below 2.3
            final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
                    : APP_PKG_NAME_21);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME,
                    APP_DETAILS_CLASS_NAME);
            intent.putExtra(appPkgName, packageName);
        }
        activity.startActivityForResult(intent,REQ_CODE_SETTINGS);
    }
}
