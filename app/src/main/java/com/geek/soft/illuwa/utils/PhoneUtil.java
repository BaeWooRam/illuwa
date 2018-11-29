package com.geek.soft.illuwa.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;

public class PhoneUtil {
    @SuppressLint("MissingPermission")
    public static String getPhoneNumber(Context context) {
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //테스트시 number = 를 +1~ 로 바꾸어서 테스트.

        String number = "";
        number = telManager.getLine1Number();
        //number = "+1153-158-1159";


        if (number == null) {
            number = "";
        }
        if (number.length() >= 5) {
            if (hasContryCode(number)) {
                number = "0" + number.substring(3);
            } else {
            }
        }
        return number;
    }
    public static boolean hasContryCode(String number) {
        return number.charAt(0) == '+' && number.charAt(1) == '8' && number.charAt(2) == '2';
    }

}