package com.geek.soft.illuwa.naverapi;

import android.support.v4.util.Pools;
import android.util.Log;

import com.nhn.android.maps.maplib.NGeoPoint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

 public class NaverAPI{
    public final static String Transform_ClientID = "77VcsUrDmA2d3B4LkguK";
    public final static String Transform_ClientSecret = "MHy3E25Hxy";
    public final static String SearchLocal_ClientID = "Ts_6xABH0kLov_edOtKm";
    public final static String SearchLocal_ClientSecret = "ImsHPjvJF9";
    public final static String UTF_8 = "utf-8";
    public final static String EUC_KR = "euc-kr";
    public final static String LATING = "latlng";
    public final static String TM128 = "tm128";
    public final static String REVIEW = "comment";



    //주소->좌표 변화 쓰레드
    public synchronized String TransformGeocode(final String ClientID, final String ClientSecret, final String address, final String encoding, final String coordType, final String coord) {
        try {
            String addr = URLEncoder.encode(address, "UTF-8");
            String apiURL = "https://openapi.naver.com/v1/map/geocode?query=" + addr +"&coordType="+coordType+"&encoding="+encoding; //json
            //String apiURL = "https://openapi.naver.com/v1/map/geocode.xml?query=" + addr; // xml

            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", ClientID);
            con.setRequestProperty("X-Naver-Client-Secret", ClientSecret);
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            System.out.println(response.toString());
            return response.toString();
        } catch (Exception e) {
            System.out.println(e);
            return e.toString();
        }
    }
    //좌표->주소 변화 쓰레드
    public String TransformAddr(final String ClientID, final String ClientSecret, final String point, final String encoding, final String coordType, final String coord) {
        try {
            String query = URLEncoder.encode(point, "UTF-8");
            String apiURL = "https://openapi.naver.com/v1/map/reversegeocode?query=" + query +"&coordType="+coordType+"&encoding="+encoding; //json
            //String apiURL = "https://openapi.naver.com/v1/map/geocode.xml?query=" + addr; // xml

            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", ClientID);
            con.setRequestProperty("X-Naver-Client-Secret", ClientSecret);
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            System.out.println(response.toString());
            return response.toString();
        } catch (Exception e) {
            System.out.println(e);
            return e.toString();
        }
    }

    //지역정보 검색
    public synchronized String SearchLocalInfo(final String ClientID, final String ClientSecret, final String Query, final int Display, final int Start){
        try {
            String text = URLEncoder.encode(Query, "UTF-8");
            String apiURL = "https://openapi.naver.com/v1/search/local.json?query=" + text; // json 결과
            apiURL+="&display=" + Display;
            apiURL+="&start=" + Start;
            apiURL+="&sort=" + "comment";
            Log.i("test","---------hong"+apiURL);
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", ClientID);
            con.setRequestProperty("X-Naver-Client-Secret", ClientSecret);
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            System.out.println(response.toString());
            return response.toString();
        } catch (Exception e) {
            System.out.println(e);
            return e.toString();
        }

    }

    //좌표 -> 주소 변환을 위한 JSON파서
    public synchronized String TransformAddrParser(String result){
        try{
            JSONObject object = new JSONObject(result).getJSONObject("result");
            JSONArray array = object.getJSONArray("items");

            JSONObject ob =  array.getJSONObject(0);
            JSONObject addr = ob.getJSONObject("addrdetail");
            String sido_sigugun = addr.get("sido").toString()+" "+addr.get("sigugun").toString();


            return sido_sigugun;
        }catch(Exception e){
            System.out.println("Error : "+e.toString());
            return null;
        }
    }

    //주소->좌표 변환을 위한 JSON파서
    public synchronized NGeoPoint TransformGeocodeParser_GeoPoint(String result){
        NGeoPoint point = new NGeoPoint();
        try{
            JSONObject object = new JSONObject(result).getJSONObject("result");
            JSONArray array = object.getJSONArray("items");
            JSONObject ob = array.getJSONObject(0);
            JSONObject obpoint = ob.getJSONObject("point");
            point.set(Double.parseDouble(obpoint.get("x").toString()),Double.parseDouble(obpoint.get("y").toString()));
            return point;
        }catch(Exception e){
            System.out.println("Error : "+e.toString());
            return null;
        }
    }

    //주소->좌표 변환을 위한 JSON파서
    public int TransformGeocodeParser_List(String result, ArrayList<HashMap<String, String>> Coord){
        try{
            JSONObject object = new JSONObject(result).getJSONObject("result");
            JSONArray array = object.getJSONArray("items");

            //System.out.println(array.toString());
            for(int i=0; i<array.length(); i++){
                JSONObject ob=array.getJSONObject(i);
                JSONObject point = ob.getJSONObject("point");

                String pointx = point.get("x").toString();
                String pointy = point.get("y").toString();

                //System.out.println("pointx : "+pointx);
                //System.out.println("pointy : "+pointy);

                HashMap<String, String>map=new HashMap<String, String>();

                //주소와 좌표계 값
                //map.put("address",address);
                map.put("pointx",pointx);
                map.put("pointy",pointy);

                Coord.add(map);
            }
        }catch(Exception e){
            System.out.println("Error : "+e.toString());
            return 0;
        }
        return Coord.size();
    }

    //지역정보 검색 파서
    public synchronized int SearchLocalParser(ArrayList<String> result,  ArrayList<HashMap<String, String>> array){
        try{
            if ((array.removeAll(array) == true)) {
                Log.i("ArrayList", "Remove all Array, size : "+array.size());
            } else {
                Log.i("ArrayList", " Don't Remove all Array");
            }

            for(int j=0; j<result.size();j++){
                JSONArray jArray=new JSONObject(result.get(j)).getJSONArray("items");
                for(int i=0; i<jArray.length(); i++){
                    JSONObject object=jArray.getJSONObject(i);

                    HashMap<String, String>map=new HashMap<String, String>();

                    //json 데이터 내 object null인 값 확인을 위한 if문들
                    //json 파서된 데이터들을 hashMap에 넣고 list열에 추가 시킨다.
                    if ((object.get("title").toString() != "")) {
                        String title = object.get("title").toString();
                        map.put("title",title);

                    } else {
                        Log.i("Parser", "Parser Title NULL");
                    }


                    if ((object.get("mapx").toString() != "") || (object.get("mapy").toString() != "")) {
                        String address = object.getString("address").toString();
                        map.put("address",address);
                        String link = object.get("link").toString();
                        map.put("link",link);
                        String category = object.get("category").toString();
                        map.put("category",category);
                        String mapx = object.get("mapx").toString();
                        String mapy = object.get("mapy").toString();
                        map.put("mapx",mapx);
                        map.put("mapy",mapy);
                    } else {
                        Log.i("Parser", "Parser Map NULL");
                    }

                    array.add(map);
                }
            }

        }catch(Exception e){
            Log.e("Error","Parser "+e.toString());
            return 0;
        }
        return array.size();
    }

}
