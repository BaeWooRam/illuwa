package com.geek.soft.illuwa.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;



public class MarketUtil {

    public static void startMarket(Context context, String packageName) {
        String url = "";

        try {
            //Check whether Google Play store is installed or not:
            context.getPackageManager().getPackageInfo(packageName, 0);

            url = "market://details?id=" + packageName;
        } catch (final Exception e) {
            url = "https://play.google.com/store/apps/details?id=" + packageName;
        }

        //Open the app page in Google Play store:
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
