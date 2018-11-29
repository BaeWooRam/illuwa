package com.geek.soft.illuwa.utils;

import java.io.UnsupportedEncodingException;

/**
 * Created by jayjay on 15. 4. 8..
 */
public class StringUtil {
    private static final String TAG = StringUtil.class.getCanonicalName();

    public static final String EMPTY = "";

    /**
     * 인자로 넘긴 문자열이 빈 문자열이 아닌지 체크
     *
     * @param text
     * @return true; 빈 문자열 아님
     */
    public static boolean isNotNull(String text) {
        if (text != null && text.trim().length() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 주어진 길이(iLength)만큼 주어진 문자(cPadder)를 strSource의 왼쪽에 붙혀서 보내준다.
     * ex) lpad("abc", 5, '^') ==> "^^abc"
     * lpad("abcdefghi", 5, '^') ==> "abcde"
     * lpad(null, 5, '^') ==> "^^^^^"
     *
     * @param source
     * @param length
     * @param padder
     */
    public static String lpad(int source, int length, char padder) {
        return lpad(String.valueOf(source), length, padder);
    }

    /**
     * 주어진 길이(iLength)만큼 주어진 문자(cPadder)를 strSource의 왼쪽에 붙혀서 보내준다.
     * ex) lpad("abc", 5, '^') ==> "^^abc"
     * lpad("abcdefghi", 5, '^') ==> "abcde"
     * lpad(null, 5, '^') ==> "^^^^^"
     *
     * @param source
     * @param length
     * @param padder
     */
    public static String lpad(String source, int length, char padder) {
        StringBuilder sbBuilder = null;

        if (!isNotNull(source)) {
            //int iPadLength = iLength;
            sbBuilder = new StringBuilder();
            for (int j = 0; j < length; j++) {
                sbBuilder.append(padder);
            }
            return sbBuilder.toString();
        }

        int iByteSize = getByteSize(source);
        if (iByteSize > length) {
            return source.substring(0, length);
        } else if (iByteSize == length) {
            return source;
        } else {
            int iPadLength = length - iByteSize;
            sbBuilder = new StringBuilder();
            for (int j = 0; j < iPadLength; j++) {
                sbBuilder.append(padder);
            }
            sbBuilder.append(source);
            return sbBuilder.toString();
        }
    }

    /**
     * byte size를 가져온다.
     *
     * @param str String target
     * @return int bytelength
     */
    public static int getByteSize(String str) {
        if (str == null || str.length() == 0)
            return 0;
        byte[] byteArray = null;
        try {
            byteArray = str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
        }
        if (byteArray == null) return 0;
        return byteArray.length;
    }

    public static String PriceFormmater(String price)
    {
        if(!isNotNull(price))
        {
            return "0";
        }

        int priceLength = price.length();
        String priceStr = price.toString();
        int cnt = priceLength/4;


        StringBuilder priceStrBuilder = new StringBuilder(priceStr);

        if(priceLength >= 4)
        {

            while(cnt > 0)
            {
                priceStrBuilder.insert(priceLength - 3*cnt,',');

                cnt--;
            }
        }

        priceStr = priceStrBuilder.toString();

        return priceStr;

    }

    public static int stringToInt(String value){
        int returnValue = 0;

        if(!isNotNull(value)){
            return returnValue;
        }

        try{
            returnValue = Integer.parseInt(value);
        }catch(Exception e){
            returnValue = 0;
        }

        return returnValue;
    }

    public static String intToString(int value){
        String temp = EMPTY;

        try{
            temp = String.valueOf(value);
        } catch(Exception e){
            temp = EMPTY;
        }
        return temp;
    }

    public static String longToString(long value){
        String temp = EMPTY;

        try{
            temp = String.valueOf(value);
        } catch(Exception e){
            temp = EMPTY;
        }
        return temp;
    }

    public static float stringToFloat(String value){
        float returnValue = 0;

        if(!isNotNull(value)){
            return returnValue;
        }

        try{
            returnValue = Float.parseFloat(value);
        }catch(Exception e){
            returnValue = 0;
        }

        return returnValue;
    }

    public static Double stringToDouble(String value){
        Double returnValue = (double)0;

        if(!isNotNull(value)){
            return returnValue;
        }

        try{
            returnValue = Double.parseDouble(value);
        }catch(Exception e){
            returnValue = (double)0;
        }

        return returnValue;
    }

    public static long stringToLong(String value){
        long returnValue = (long)0;

        if(!isNotNull(value)){
            return returnValue;
        }

        try{
            returnValue = Long.parseLong(value);
        }catch(Exception e){
            returnValue = (long)0;
        }

        return returnValue;
    }

    public static String transPhoneNumber(String tel){
        String returnValue = "";

        if(!isNotNull(tel) || tel.length() != 11){
            return tel;
        }

        try{
            returnValue += tel.substring(0, 3) + "-" + tel.substring(3, 7) + "-" + tel.substring(7, tel.length());
        }catch(Exception e){
            returnValue = tel;
        }

        return returnValue;
    }

    public static boolean lengthCheck(String str, int length){
        if(!isNotNull(str) || str.length() != length){
            return false;
        }

        return true;
    }

    public static String addEmptyFront(String str1, int maxLength){

        int textLength = 0;

        for(int i = 0; i < str1.length(); i++){
            if(Character.getType(str1.charAt(i)) == Character.OTHER_LETTER){
                textLength++;
            }
            textLength++;
        }

        String empty = "";
        for(int i = textLength; i < maxLength; i++){
            empty += " ";
        }

        return str1 + empty ;
    }

    public static String addEmptyBack(String str1, int maxLength){

        int textLength = 0;

        for(int i = 0; i < str1.length(); i++){
            if(Character.getType(str1.charAt(i)) == Character.OTHER_LETTER){
                textLength++;
            }
            textLength++;
        }

        String empty = "";
        for(int i = textLength; i < maxLength; i++){
            empty += " ";
        }

        return empty + str1 ;
    }

    public static boolean isEqual(String str1, String str2){
        if(isNotNull(str1) && isNotNull(str2) && str1.equals(str2)){
            return true;
        }

        return false ;
    }

    public static boolean isEqualsIgnoreCase(String str1, String str2){
        if(isNotNull(str1) && isNotNull(str2) && str1.equalsIgnoreCase(str2)){
            return true;
        }

        return false ;
    }

    public static boolean isContains(String str1, String str2){
        if(isNotNull(str1) && str1.contains(str2)){
            return true;
        }

        return false ;
    }

    public static boolean isUpLowContains(String str1, String str2){
        if(isNotNull(str1) && (str1.contains(str2) || str1.contains(str2.toUpperCase()) || str1.contains(str2.toLowerCase()) )){
            return true;
        }

        return false ;
    }

    public static boolean isVlidateId(String str)
    {
        if(isNotNull(str) && str.matches("^[a-zA-Z]{1}[a-zA-Z0-9_]{4,11}$"))
        {
            return true;
        }

        return false;
    }

    public static String getNull(String str)
    {
        String returnValue = StringUtil.EMPTY;

        if(isNotNull(str))
        {
            returnValue = str;
        }

        return returnValue;
    }

    public static String getNull(String str1, String str2)
    {
        String returnValue = StringUtil.EMPTY;

        if(isNotNull(str1))
        {
            returnValue = str1;
        }
        else
        {
            returnValue = str2;
        }

        return returnValue;
    }


}
