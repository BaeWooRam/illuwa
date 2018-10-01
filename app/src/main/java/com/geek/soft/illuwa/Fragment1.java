package com.geek.soft.illuwa;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.geek.soft.illuwa.category.CategoryAdapter;
import com.geek.soft.illuwa.category.CategoryBin;
import com.geek.soft.illuwa.naverapi.AppPermission;
import com.geek.soft.illuwa.naverapi.GeoTrans;
import com.geek.soft.illuwa.naverapi.GeoTransPoint;
import com.geek.soft.illuwa.naverapi.NMapPOIflagType;
import com.geek.soft.illuwa.naverapi.NMapViewerResourceProvider;
import com.geek.soft.illuwa.naverapi.NaverAPI;

import com.nhn.android.maps.NMapContext;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapProjection;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;

import com.nhn.android.maps.overlay.NMapCircleData;
import com.nhn.android.maps.overlay.NMapCircleStyle;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;

import com.nhn.android.maps.overlay.NMapPathLineStyle;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapPathDataOverlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.geek.soft.illuwa.naverapi.AppPermission.checkAndRequestPermission;

public class Fragment1 extends NMapFragment implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener{
    private final int CATEGORY_MAX= 6;
    private final int CLICK_MAX= 6;
    private final int SUBWAY = 0;
    private final int FOOD = 2;
    private final int ENT = 3;

    /*----------------FAB 관련 필요 변수들 모음----------------*/
    boolean isFABOpen = true;
    boolean gpsPower  = false;
    Button fab, fab1, fab2, fab3, fab4;
    Button btnNavi, btnOption, btnZoomin, btnZoomout;
    TextView btnHelp_1,btnHelp_2,btnHelp_3,btnHelp_4;

    /*----------------다이얼로그 관련 필요 변수들 모음----------------*/
    LinearLayout dlgView;
    FrameLayout layout;

    /*----------------설정 다이얼로그 관련 필요 변수들 모음----------------*/
    Switch switchHelp;
    CheckBox checkTraffic, checkBicyicle;
    Button btnGuide;
    boolean setting[]= {true,false,false};
    SettingClickEvent settingClickEvent = new SettingClickEvent();

    /*----------------인원수 다이얼로그 관련 필요 변수들 모음----------------*/
    private static final int DEFALUT_MARKER_ID = 10000;//유저들 클릭시
    private int ClickMarker_id = DEFALUT_MARKER_ID, ClickMarker_MAX;    //마커 아이디와 최대 갯수
    NMapPOIdata ClickMarker_data[]= new NMapPOIdata[CLICK_MAX];                 //마커 데이터 저장 공
    private int iPersonCount=0;

    /*----------------카테고리 다이얼로그 관련 필요 변수들 모음----------------*/
    List<CategoryBin> categoryBinList=null;
    private String strList[] = {"지하철","카페","음식점","놀거리","주차장","주점"};
    private int iDrawableID[] = new int[]{R.drawable.category_subway,R.drawable.category_cafe,R.drawable.category_food,R.drawable.categoty_ent,R.drawable.category_parking,R.drawable.category_beer};
    ListView listView;
    CategoryAdapter adapter;
    boolean bCurrentSeleted[] = {false, false, false, false, false, false};
    int Checkedposition;
    int CheckedItem;
    Spinner SpinnerSearch_radius;
    float Search_radius = 500.0f;
    int SearchRadiueSelect_postion=0;

    /*----------------주소 검색 관련 필요 변수들 모음----------------*/
    NaverAPI api;
    EditText textSearchAddr;
    ImageView btnSearch;
    boolean isSearchOpen=false;
    String AddrSearch_Query;

    /*----------------중앙 지점 계산 관련 필요 변수들 모음----------------*/
    NGeoPoint Center_Point;                                         // 중앙 지점 좌표
    NMapPOIdata Center_poidata;                                     // 중앙 지점 좌표 데이터 저장
    NMapCircleData circleData;                                       // 중앙 반경 표시

    /*----------------API에서 가져온 정보들을 저장하기 위한 변수 설정----------------*/
    ArrayList<NGeoPoint> geoPoints_Click;                           // 지도API 클릭 시 좌표 저장

    /*----------------Naver지도 관련 필요 변수들 모음----------------*/
    NMapViewerResourceProvider mMapViewerResourceProvider;          // 뷰어 상에서 리소스 제공을 위한 필요 클래스
    NMapOverlayManager mOverlayManager;                             // 뷰어 상에 오버레이시키기 위한 필요 클래스
    NMapPOIdataOverlay poiDataOverlay;                              // 뷰어 상에 오버레이시키기 위한 필요 클래스
    NMapController controller;                                       // 지도 컨트롤 하기위한 클래스
    NMapLocationManager locationManager;                            // 현재 위치 좌표를 반환시키기위한 클래스
    NMapMyLocationOverlay mMyLocationOverlay;
    NMapPathDataOverlay pathDataOverlay;                            // 뷰어 상에 폴리곤 데이터를 오버레이 위한 클래스
    NMapPOIdata Category_filter_poiData[]=new NMapPOIdata[CATEGORY_MAX];             // 카테고리 별 필터 상에 나타낼 데이터들
    private int PIN_ID[] = new int[]{ NMapPOIflagType.SUBWAY_PIN, NMapPOIflagType.CAFE_PIN,
            NMapPOIflagType.FOOD_PIN, NMapPOIflagType.ENT_PIN, NMapPOIflagType.PARKING_PIN, NMapPOIflagType.BEER_PIN};


    /*--------지역 API 호출 시, 변수 값을 위해 설정(내용은 검색 지역 API에서 참고하세요~)------------*/
    String query[]= new String[CATEGORY_MAX];
    boolean AddrThead_Check = false;

    /*----------------Naver지도 관련 필요 변수들 모음----------------*/
    private NMapContext mMapContext;                                // 지도를 띄우기 위한 context

    //   /*--------기타 변수-------*/
    NMapView mapView;
    int permissionCheck;
    IlluwaApplication application;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = (FrameLayout)inflater.inflate(R.layout.activity_fragment1, null);
        return layout;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //지역 정보 조사하는 NAVERAPI 실행 -> JSON파일 받음
        api = new NaverAPI();

        //application
        application = (IlluwaApplication) getContext().getApplicationContext();

        //Context 지정
        mMapContext =  new NMapContext(super.getActivity());
        mMapContext.onCreate();

        //지역주소와 지도 API의 정보들을 담기 위한 List 정의
        geoPoints_Click = new ArrayList<NGeoPoint>();

        //카테고리 정보들을 담기 위한 List 정의
        categoryBinList = new ArrayList<CategoryBin>();

        //카테고리 리스트 내용 설정
        for(int i =0;i<CATEGORY_MAX;i++){
            CategoryBin bin = new CategoryBin(iDrawableID[i], strList[i],false);
            categoryBinList.add(bin);
        }

        /*-------- 주소를 좌표로 변환하는 작업 -------*/
        //검색을 위한 EditText 주소 가져오기
        textSearchAddr = (EditText)getActivity().findViewById(R.id.textSearchAddr);
        btnSearch = (ImageView) getActivity().findViewById(R.id.Search_btnSearch);
        textSearchAddr.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String text = textSearchAddr.getText().toString();
                if(!text.equals("")){
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        AddrSearch_Query = textSearchAddr.getText().toString();
                        new TransformGeocodeThread().execute(null, null);
                        return true;
                    }
                }
                return false;
            }
        });

        //버튼을 눌러 가져온 주소를 좌표로 변환하기 위한 작업업
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddrSearch_Query = textSearchAddr.getText().toString();
                new TransformGeocodeThread().execute(null,null);
            }
        });

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //NAVER MAP을 실질적으로 화면에 띄우기 위한 설정들
        mapView = (NMapView)getView().findViewById(R.id.mapView);
        mapView.setClientId(NaverAPI.Transform_ClientID);// 클라이언트 아이디 설정
        mapView.setClickable(true);
        mapView.setEnabled(true);
        mapView.setFocusable(true);
        mapView.setFocusableInTouchMode(true);
        mapView.requestFocus();
        mMapContext.setupMapView(mapView);
        mapView.setScalingFactor(3,true);
        mapView.displayZoomControls(true);

        //NAVER MAP 뷰어 상에 나타내기 위해 ResourceProvider와 OverlayManger를 생성
        mMapViewerResourceProvider = new NMapViewerResourceProvider(getContext());
        mOverlayManager = new NMapOverlayManager(getContext(), mapView, mMapViewerResourceProvider);
        pathDataOverlay = mOverlayManager.createPathDataOverlay();
        controller = mapView.getMapController();
        locationManager = new NMapLocationManager(mapView.getContext());
        mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(locationManager, null);
        locationManager.setOnLocationChangeListener(new NMapLocationManager.OnLocationChangeListener() {
            @Override
            public boolean onLocationChanged(NMapLocationManager nMapLocationManager, NGeoPoint nGeoPoint) {
                mMyLocationOverlay.refresh();
                controller.setMapCenter(nGeoPoint);
                return false;
            }

            @Override
            public void onLocationUpdateTimeout(NMapLocationManager nMapLocationManager) {
                Toast.makeText(getContext(),"현재 위치를 탐색하지 못했습니다. 다시 시도해주세요!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLocationUnavailableArea(NMapLocationManager nMapLocationManager, NGeoPoint nGeoPoint) {
                Toast.makeText(getContext(),"위치가 불가능한 지역입니다. 다시 시도해주세요!",Toast.LENGTH_SHORT).show();
            }
        });

        //버튼
        fab = (Button) layout.findViewById(R.id.btnFAB_Top);
        fab1 = (Button) layout.findViewById(R.id.btnFAB_1);
        fab2 = (Button) layout.findViewById(R.id.btnFAB_2);
        fab3 = (Button) layout.findViewById(R.id.btnFAB_3);
        fab4 = (Button) layout.findViewById(R.id.btnFAB_4);
        btnOption = (Button) layout.findViewById(R.id.btnOption);
        btnNavi = (Button) layout.findViewById(R.id.btnNavi);
        btnZoomin = (Button) layout.findViewById(R.id.btnZoomin);
        btnZoomout = (Button) layout.findViewById(R.id.btnZoomout);
        btnHelp_1=(TextView)layout.findViewById(R.id.btn_help_1);
        btnHelp_2=(TextView)layout.findViewById(R.id.btn_help_2);
        btnHelp_3=(TextView)layout.findViewById(R.id.btn_help_3);
        btnHelp_4=(TextView)layout.findViewById(R.id.btn_help_4);


        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this); //fab3셋온클릭
        fab4.setOnClickListener(this); //fab3셋온클릭
        btnNavi.setOnClickListener(this);
        btnOption.setOnClickListener(this);
        btnZoomin.setOnClickListener(this);
        btnZoomout.setOnClickListener(this);

        //NAVER MAP 뷰어 상 터치 이벤트 처리
        mapView.setOnMapViewTouchEventListener(new NMapView.OnMapViewTouchEventListener() {

            @Override
            public void onLongPress(NMapView nMapView, MotionEvent motionEvent) {
            }

            @Override
            public void onLongPressCanceled(NMapView nMapView) {

            }

            @Override
            public void onTouchDown(NMapView nMapView, MotionEvent motionEvent) {


            }

            @Override
            public void onTouchUp(NMapView nMapView, MotionEvent motionEvent) {

            }

            @Override
            public void onScroll(NMapView nMapView, MotionEvent motionEvent, MotionEvent motionEvent1) {

            }

            @Override
            public void onSingleTapUp(NMapView nMapView, MotionEvent motionEvent) {
                //터치 시, 생성될 핀 최대 갯수 설정
                ClickMarker_MAX = DEFALUT_MARKER_ID + iPersonCount;
                Log.i("Click max+","value : "+ ClickMarker_MAX);
                Log.i("Click id","value : "+ ClickMarker_id);
                if(ClickMarker_MAX>ClickMarker_id){
                    //현재 인덱스
                    final int index = ClickMarker_id-DEFALUT_MARKER_ID;

                    //뷰어 상에 경위도 좌표와 거리를 계산하기 위한 변수 정의
                    NMapProjection projection = nMapView.getMapProjection();

                    //뷰어 상에서 터치한 곳에 경위도 좌표 생성 및 리스트 추가
                    final NGeoPoint geoPoint_Touch = projection.fromPixels((int)motionEvent.getX(),(int)motionEvent.getY());

                    int markerId = NMapPOIflagType.PIN;
                    ClickMarker_data[index] = new NMapPOIdata(1, mMapViewerResourceProvider);
                    ClickMarker_data[index].beginPOIdata(1); //POI 아이템 추가를 시작한다.
                    /*----아이템 추가 시작----*/
                    NMapPOIitem item = ClickMarker_data[index].addPOIitem(geoPoint_Touch, "NO."+(index+1), markerId, ClickMarker_id);
                    item.setFloatingMode(NMapPOIitem.FLOATING_DRAG);
                    /*----아이템 추가 끝----*/
                    ClickMarker_data[index].endPOIdata(); //POI 아이템 추가를 종료한다.

                    poiDataOverlay = mOverlayManager.createPOIdataOverlay(ClickMarker_data[index], null);
                    poiDataOverlay.setOnFloatingItemChangeListener(new NMapPOIdataOverlay.OnFloatingItemChangeListener() {
                        @Override
                        public void onPointChanged(NMapPOIdataOverlay nMapPOIdataOverlay, NMapPOIitem nMapPOIitem) {
                            int index = nMapPOIitem.getId()-10000;

                            NGeoPoint point = nMapPOIitem.getPoint();
                            if(geoPoints_Click != null){
                                if(geoPoints_Click.size() > 0){
                                    geoPoints_Click.remove(index);
                                }
                                if(ClickMarker_MAX>ClickMarker_id)
                                    geoPoints_Click.add(index,point);
                            }
                        }
                    });

                    ClickMarker_id++;
                    geoPoints_Click.add(geoPoint_Touch);

                    if(ClickMarker_id>=DEFALUT_MARKER_ID+2){
                        fab4.setVisibility(View.VISIBLE);
                        if(setting[0] && (isFABOpen==false))
                            btnHelp_4.setVisibility(View.VISIBLE);
                    }
                    Toast.makeText(getContext(),"남은 인원수 :"+(iPersonCount-(index+1)),Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    //각종 버튼 이벤트 처리
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            //현재 위치 버튼시
            case R.id.btnNavi:
                gpsPower = !gpsPower;
                if(gpsPower){
                    btnNavi.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.navermap_location_on));
                    permissionCheck=ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION)+ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION);
                    //권한 없을때
                    if(permissionCheck<0){
                        checkAndRequestPermission(getActivity(),AppPermission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
                        checkAndRequestPermission(getActivity(),AppPermission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);

                        gpsPower = false;
                        btnNavi.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.navermap_location_off));
                    }
                    //권한 생겼을때
                    else {
                        FindMyLocation();
                    }
                }
                else{
                    btnNavi.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.navermap_location_off));
                    mOverlayManager.removeMyLocationOverlay();
                    locationManager.disableMyLocation();

                    locationManager = new NMapLocationManager(mapView.getContext());
                    mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(locationManager, null);
                    locationManager.setOnLocationChangeListener(new NMapLocationManager.OnLocationChangeListener() {
                        @Override
                        public boolean onLocationChanged(NMapLocationManager nMapLocationManager, NGeoPoint nGeoPoint) {
                            mMyLocationOverlay.refresh();
                            controller.setMapCenter(nGeoPoint);
                            return false;
                        }

                        @Override
                        public void onLocationUpdateTimeout(NMapLocationManager nMapLocationManager) {
                            Toast.makeText(getContext(),"현재 위치를 탐색하지 못했습니다. 다시 시도해주세요!",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onLocationUnavailableArea(NMapLocationManager nMapLocationManager, NGeoPoint nGeoPoint) {
                            Toast.makeText(getContext(),"위치가 불가능한 지역입니다. 다시 시도해주세요!",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;

            //옵션 버튼 클릭시
            case R.id.btnOption:
                SettingDialog(dlgView);
                break;

            //네이버 지도 줌 인 버튼 클릭시
            case R.id.btnZoomin:
                controller.zoomIn();
                break;

            //네이버 지도 줌 아웃 버튼 클릭시
            case R.id.btnZoomout:
                controller.zoomOut();
                break;

            //메뉴 버튼 클릭시
            case R.id.btnFAB_Top:
                if(isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
                break;

            //중간 위치 버튼 클릭시
            case R.id.btnFAB_4:
                //초기화
                CategoryClear(Category_filter_poiData,categoryBinList);

                /*----------- 중심점 계산 -------------*/
                if(ClickMarker_id-DEFALUT_MARKER_ID>=3){
                    //초기화
                    PoidataClear(Center_poidata,controller);

                    int index = ClickMarker_id-DEFALUT_MARKER_ID;
                    double latitude[] = LatitudeToArrayDouble(geoPoints_Click,index);
                    double longitude[] = LongitudeToArrayDouble(geoPoints_Click,index);

                    double center_x = latitude[0]+((latitude[index-1]-latitude[0])/2);
                    double center_y = longitude[0]+((longitude[index-1]-longitude[0])/2);

                    Center_Point = new NGeoPoint(center_x,center_y);

                    CreateCenterPin();

                    controller.setMapCenter(Center_Point.getLatitude(),Center_Point.getLongitude());
                }
                //두 점 사이에 중점
                else if(ClickMarker_id-DEFALUT_MARKER_ID<3){
                    PoidataClear(Center_poidata,controller);

                    double center_x =  (geoPoints_Click.get(0).getLatitude()+geoPoints_Click.get(1).getLatitude())/2;
                    double center_y =  (geoPoints_Click.get(0).getLongitude()+geoPoints_Click.get(1).getLongitude())/2;

                    Center_Point = new NGeoPoint(center_x,center_y);

                    CreateCenterPin();

                    controller.setMapCenter(Center_Point.getLatitude(),Center_Point.getLongitude());
                }

                //마커에 찍힌 반경 지우기
                if(Center_Point!=null){
                    mOverlayManager.removeOverlay(pathDataOverlay);
                    pathDataOverlay = mOverlayManager.createPathDataOverlay();
                    circleData=CreatePathCircle(pathDataOverlay,mOverlayManager,NMapPathLineStyle.TYPE_SOLID,Center_Point,Search_radius,mapView);
                }
                break;

            //인원수 버튼 클릭 시
            case R.id.btnFAB_3:
                PersonCountDiag(dlgView,R.style.MyAlertDialogStyle, R.id.peopleCount);
                break;

            //필터 버튼 클릭 시
            case R.id.btnFAB_2:
                if(Center_Point!=null) {
                    CategoryDiag_ListView(dlgView,R.layout.category_list, categoryBinList);
                }
                else
                    Toast.makeText(getContext(),"중앙 위치부터 찾아줘요",Toast.LENGTH_SHORT).show();

                break;

            //검색 버튼 클릭 시
            case R.id.btnFAB_1:
                isSearchOpen = !isSearchOpen;
                if(isSearchOpen){
                    textSearchAddr.setVisibility(EditText.VISIBLE);
                    btnSearch.setVisibility(ImageView.VISIBLE);
                }else{
                    textSearchAddr.setVisibility(EditText.GONE);
                    btnSearch.setVisibility(ImageView.GONE);
                }
                break;

            default:
                Toast.makeText(getContext(),"Click Error",Toast.LENGTH_SHORT).show();

        }
    }

    //설정 다이얼로그 창
    public void SettingDialog(View dlgView) {

        //초기화
        int result=0;
        dlgView=(LinearLayout)getLayoutInflater().inflate(R.layout.setting_list,null);

        //설정에 필요한 버튼들 주소 가져옴
        switchHelp = (Switch) dlgView.findViewById(R.id.swicthHelp);
        checkTraffic = (CheckBox) dlgView.findViewById(R.id.checkTraffic);
        checkBicyicle = (CheckBox) dlgView.findViewById(R.id.checkBicyicle);

        //현재 설정된 값 반영
        if(setting[0]) {
            switchHelp.setChecked(true);
        }else {
            switchHelp.setChecked(false);
        }
        if(setting[1])
            checkTraffic.setChecked(true);
        if(setting[2])
            checkBicyicle.setChecked(true);

        //버튼들 이벤트 관리
        switchHelp.setOnCheckedChangeListener(this);

        checkTraffic.setOnClickListener(settingClickEvent);
        checkBicyicle.setOnClickListener(settingClickEvent);

        btnGuide = (Button) dlgView.findViewById(R.id.btnGuide);
        btnGuide.setOnClickListener(settingClickEvent);


        //다이얼로그 창 생성
        AlertDialog.Builder dlg_CategorySelect=new AlertDialog.Builder(getContext(),R.style.MyAlertDialogStyle);
        dlg_CategorySelect.setView(dlgView)
                .setIcon(R.drawable.navermap_option_btn)
                .setTitle("설정")
                .setCancelable(false)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                            //실시간 교통 정보 보기 모드 체크 저장
                            if(setting[1]){
                               controller.setMapViewTrafficMode(true);
                            }
                            else{
                                controller.setMapViewTrafficMode(false);
                            }

                            //자전거 도로 보기 모드 체크 저장
                            if(setting[2]){
                                controller.setMapViewBicycleMode(true);
                            }
                            else{
                                controller.setMapViewBicycleMode(false);
                            }
                    }
                })
                .show();
    }

    //PersonCount를 받기위한 다이얼로그창
    public void PersonCountDiag(View dlgView, final int styleid, final int textid){

            dlgView=(LinearLayout)getLayoutInflater().inflate(R.layout.personcount_dlgview,null);
            final EditText et=(EditText)dlgView.findViewById(textid);//dlgview를 위에서 인플래이트

            //실질적인 다이얼로그창 설정
            AlertDialog.Builder dlg_PersonCount=new AlertDialog.Builder(getContext(),styleid);
            dlg_PersonCount
                    .setView(dlgView)
                    .setNegativeButton("취소", null)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //------------초기화---------------------
                            geoPoints_Click.clear();                 //중앙 값을 구하기 위한 클릭 좌표들 초기화
                            fab4.setVisibility(View.GONE);           //버튼 보이는거 초기화
                            btnHelp_4.setVisibility(View.GONE);
                            ClickMarker_id = DEFALUT_MARKER_ID;      //중앙 값을 구하기 위한 클릭 좌표들 초기화

                            //클릭해서 생긴 마커, ,마커 갯수, 지도 초기화
                            PoidataListClear(ClickMarker_data);

                            //카테고리 초기화
                            CategoryClear(Category_filter_poiData,categoryBinList);

                            //탐색 반경 지우기
                            if((Center_Point!=null) && (pathDataOverlay!=null)){
                                mOverlayManager.removeOverlay(pathDataOverlay);
                                Center_Point = null;                     //중앙 값을 구하기 위한 클릭 좌표들 초기화
                            }

                            //중앙 마크 지우기
                            PoidataClear(Center_poidata,controller);


                            //적은 인원수에 대한 예외 처리
                            if((et.getText().toString()).equals("")) {
                                iPersonCount=0;
                                Toast.makeText(getContext(),"입력이 안됐어요",Toast.LENGTH_SHORT).show();
                            }
                            else if(Integer.parseInt(et.getText().toString())<=1) {
                                iPersonCount=0;
                                Toast.makeText(getContext(),"너무 적어요(2명 이상)",Toast.LENGTH_SHORT).show();
                            }
                            else if(Integer.parseInt(et.getText().toString())>6) {
                                iPersonCount=0;
                                Toast.makeText(getContext(),"너무 많아요(6명 까지)",Toast.LENGTH_SHORT).show();
                            }
                            else
                                iPersonCount= Integer.parseInt(et.getText().toString());
                        }
                    })
                    .show();

    }

    //Category 선택을 위한 다이얼로그창
    public void CategoryDiag_ListView(View dlgView, final int layoutid, final List<CategoryBin> categoryBinList){
        //inflate로 category_list 펼치고 객체 넣는다.
        dlgView=(View)View.inflate(getContext(), layoutid,null);

        //카테고리 리스트 내용을 위한 adapter 설정
        adapter = new CategoryAdapter(getContext(), categoryBinList);

        listView = (ListView)dlgView.findViewById(R.id.category_list);
        listView.setAdapter(adapter);


        //카테고리에서 검색 반경을 위한 Spinner 이벤트 처리
        SpinnerSearch_radius = (Spinner) dlgView.findViewById(R.id.category_spinner_radius);
        SpinnerSearch_radius.setSelection(SearchRadiueSelect_postion);
        SpinnerSearch_radius.setOnItemSelectedListener(this);


        //카테고리 리스트 내용을 위한 listener 설정
        listView.setOnItemClickListener(this);

        //카테고리 다이얼로그 생성 밑 버튼 이벤트
        AlertDialog.Builder dlg_CategorySelect=new AlertDialog.Builder(getContext(),R.style.MyAlertDialogStyle);
        dlg_CategorySelect.setView(dlgView)
                .setIcon(R.drawable.fab_filter)
                .setTitle("주변 검색")
                .setCancelable(false)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(Center_Point !=null){
                            for(int i=0;i<CATEGORY_MAX;i++){

                                if((categoryBinList.get(i).isSelected())){
                                    if(bCurrentSeleted[i]==true)
                                    {
                                        CheckedItem = i;
                                        new GeoTransPointThread().execute(null, null);

                                    }
                                }
                                else{
                                    if (Category_filter_poiData[i] != null) {
                                        Category_filter_poiData[i].removeAllPOIdata();

                                    }
                                }

                            }
                            controller.reload();
                            //현재 선택된 것에 대한 정보들을 초기화한다.
                            for(int i=0;i<CATEGORY_MAX;i++)
                                bCurrentSeleted[i]=false;
                        }
                    }
                })
                .show();

    }

    //중간 위치를 찾기 위해 정렬 해주는 메서드
    public double[] LatitudeToArrayDouble(ArrayList<NGeoPoint> ArrayGeo, int Count) {
        if((ArrayGeo !=null)) {
            if((ArrayGeo.size() > 1) && (Count > 0)) {
                double ArrayDouble[] = new double[Count];
                for (int i = 0; i < ArrayGeo.size(); i++) {
                    ArrayDouble[i]=ArrayGeo.get(i).getLatitude();
                }
                Arrays.sort(ArrayDouble);

                return ArrayDouble;
            }
            else
                Log.i("Center Calculator Erorr","ArrayGeo SIZE 1,0 ");
        }
        else
            Log.i("Center Calculator Erorr"," ArrayGeo NULL");

        return null;
    }

    //중간 위치를 찾기 위해 정렬 해주는 메서드
    public double[] LongitudeToArrayDouble(ArrayList<NGeoPoint> ArrayGeo, int Count) {
        if((ArrayGeo !=null)) {
            if((ArrayGeo.size() > 1) && (Count > 0)) {
                double ArrayDouble[] = new double[Count];
                for (int i = 0; i < ArrayGeo.size(); i++) {
                    ArrayDouble[i]=ArrayGeo.get(i).getLongitude();
                }
                Arrays.sort(ArrayDouble);

                return ArrayDouble;
            }
            else
                Log.i("Center Calculator Erorr","ArrayGeo SIZE 1,0 ");
        }
        else
            Log.i("Center Calculator Erorr"," ArrayGeo NULL");

        return null;
    }

    //카테고리 리스트 내용을 위한 listener 설정
    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        //초기화
        query[position] = "";
        AddrThead_Check = false;

        //현재 선택한 리스트 아이템 번호
        Checkedposition = position;
        bCurrentSeleted[position] = true;

        boolean Selected = !categoryBinList.get(position).isSelected();
        adapter.notifyDataSetChanged();

        //현재 선택한 리스트 아이템이 선택되었는지 선택이 해제되었는지 파악
        if( (Category_filter_poiData[position] != null) && categoryBinList.get(position).isSelected() == false){
            Category_filter_poiData[position].removeAllPOIdata();

        }

        //현재 중앙 지점의 시도를 query문에 넣어줌
        if((Center_Point != null) && (Selected == true)) {
            new AddrThread().execute(null, null);
        }

        //FOOD과 ENT 클릭시 예외처리를 위한 명령문
        if((position != FOOD) && (position != ENT)){
            categoryBinList.get(position).setSelected(Selected);
            adapter.notifyDataSetChanged();

        }else{
            //ENT 클릭시
            if(position == ENT){
                if(Selected){
                    final String ent_list[] = {"노래방","당구장","피시방","볼링장"};
                    //다이얼로그창 생성
                    AlertDialog.Builder dlg_CategorySelect=new AlertDialog.Builder(getContext(),R.style.MyAlertDialogStyle);
                    dlg_CategorySelect
                            .setIcon(R.drawable.categoty_ent)
                            .setTitle("일루와")
                            .setItems(ent_list, new DialogInterface.OnClickListener() {
                                //현재 선택된 음식점 종류를 query문에 넣어줌
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //현재 선택한 리스트 아이템이 선택되었다고 표시
                                    categoryBinList.get(position).setSelected(!categoryBinList.get(position).isSelected());
                                    adapter.notifyDataSetChanged();

                                    if(AddrThead_Check == true){
                                        query[position] += ent_list[which];
                                    }
                                    else
                                        Toast.makeText(getContext(),"다시 선택해주세요",Toast.LENGTH_SHORT).show();

                                }
                            }).show();
                }else {
                    categoryBinList.get(position).setSelected(!categoryBinList.get(position).isSelected());
                    adapter.notifyDataSetChanged();
                }
            }else {
                //FOOD 클릭시
                if(Selected){
                    final String food_list[] = {"일식","한식","중식","양식"};

                    //다이얼로그 생성
                    AlertDialog.Builder dlg_CategorySelect=new AlertDialog.Builder(getContext(),R.style.MyAlertDialogStyle);
                    dlg_CategorySelect
                            .setIcon(R.drawable.category_food)
                            .setTitle("일루와")
                            .setItems(food_list, new DialogInterface.OnClickListener() {
                                //현재 선택된 음식점 종류를 query문에 넣어줌
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //현재 선택한 리스트 아이템이 선택되었다고 표시
                                    categoryBinList.get(position).setSelected(!categoryBinList.get(position).isSelected());
                                    adapter.notifyDataSetChanged();

                                    if(AddrThead_Check == true){
                                        query[position] += food_list[which];
                                    }
                                    else
                                        Toast.makeText(getContext(),"다시 선택해주세요",Toast.LENGTH_SHORT).show();
                                }
                            }).show();
                }else {
                    categoryBinList.get(position).setSelected(!categoryBinList.get(position).isSelected());
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }


    /*----------카테고리에서 검색 반경을 위한 Spinner 이벤트 처리------------*/
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //초기화
        for(int i=0;i<CATEGORY_MAX;i++){
            categoryBinList.get(i).setSelected(false);
            adapter.notifyDataSetChanged();
        }

        String item = (String)parent.getItemAtPosition(position);
        SearchRadiueSelect_postion=position;

        //중간 위치에 주변 검색 필터 반경을 정해줌
        switch (item){
            case "500m":
                Search_radius = 500.0f;
                if(Center_Point!=null){
                    mOverlayManager.removeOverlay(pathDataOverlay);
                    pathDataOverlay = mOverlayManager.createPathDataOverlay();
                    circleData=CreatePathCircle(pathDataOverlay,mOverlayManager,NMapPathLineStyle.TYPE_SOLID,Center_Point,Search_radius,mapView);
                }
                break;

            case "800m":
                Search_radius = 800.0f;
                if(Center_Point!=null){
                    mOverlayManager.removeOverlay(pathDataOverlay);
                    pathDataOverlay = mOverlayManager.createPathDataOverlay();
                    circleData=CreatePathCircle(pathDataOverlay,mOverlayManager,NMapPathLineStyle.TYPE_SOLID,Center_Point,Search_radius,mapView);
                }
                break;

            case "1km":
                Search_radius = 1000.0f;
                if(Center_Point!=null){
                    mOverlayManager.removeOverlay(pathDataOverlay);
                    pathDataOverlay = mOverlayManager.createPathDataOverlay();
                    circleData=CreatePathCircle(pathDataOverlay,mOverlayManager,NMapPathLineStyle.TYPE_SOLID,Center_Point,Search_radius,mapView);
                }
                break;

            case "1.2km":
                Search_radius = 1200.0f;
                if(Center_Point!=null){
                    mOverlayManager.removeOverlay(pathDataOverlay);
                    pathDataOverlay = mOverlayManager.createPathDataOverlay();
                    circleData=CreatePathCircle(pathDataOverlay,mOverlayManager,NMapPathLineStyle.TYPE_SOLID,Center_Point,Search_radius,mapView);
                }
                break;

            default:
                Log.e("Spinner Error","Seleted item is default");
        }
        Log.i("OverlayManger","Overlays size : "+mOverlayManager.sizeofOverlays());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        setting[0] = !setting[0];
        if(isChecked){ //버튼 도움말 스위치 온
            if(isFABOpen==false){ //버튼 위로 올라온 상태 (도움말이 보이는 중)
                btnHelp_1.setVisibility(View.VISIBLE);
                btnHelp_2.setVisibility(View.VISIBLE);
                btnHelp_3.setVisibility(View.VISIBLE);
                if(ClickMarker_id>=DEFALUT_MARKER_ID+2) {
                    btnHelp_4.setVisibility(View.VISIBLE);
                }
            }
        }else{ //버튼 도움말 스위치 오프
            btnHelp_1.setVisibility(View.GONE);
            btnHelp_2.setVisibility(View.GONE);
            btnHelp_3.setVisibility(View.GONE);
            btnHelp_4.setVisibility(View.GONE);
        }
    }

    //세팅버튼 이벤트 처리
    private class SettingClickEvent implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                //가이드 버튼 클릭시
                case R.id.btnGuide:
                    Intent intent=new Intent(getContext(), ActivityGuide.class);
                    startActivity(intent);
                    break;

                //설정트래픽
                case R.id.checkTraffic:
                    setting[1] = !setting[1];
                    if(setting[2] = true){
                        setting[2] = false;
                        checkBicyicle.setChecked(false);
                    }
                    break;

                case R.id.checkBicyicle:
                    setting[2] = !setting[2];
                    if(setting[1] = true){
                        setting[1] = false;
                        checkTraffic.setChecked(false);
                    }
                    break;
            }
        }
    }
    /*-----------------------------------------------------------------------*/
    //주소->좌표 변화 쓰레드
    class TransformGeocodeThread extends AsyncTask<String, Void, NGeoPoint> {
        ProgressDialog progressDialog;
        @Override
        protected NGeoPoint doInBackground(String... strings) {
            String result = api.TransformGeocode(NaverAPI.Transform_ClientID,NaverAPI.Transform_ClientSecret,AddrSearch_Query,NaverAPI.UTF_8,NaverAPI.LATING,null);

            //받은 JSON파일을 파싱해서 String으로 정보 저장
            NGeoPoint geoPoint;
            if((geoPoint = api.TransformGeocodeParser_GeoPoint(result)) == null)
                Log.i("TransformGeocodeThread","GeoPoint NULL! Error : ");

            return geoPoint;
        }

        @Override
        protected void onPreExecute() {
            //진행바
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("데이터를 가져오는 중입니다...");
            progressDialog.show();

            super.onPreExecute();
        }

        //뷰어 상에 나타내기 위해 필요한 메소드
        @Override
        protected void onPostExecute(NGeoPoint point) {
            progressDialog.dismiss();
            if(point!= null) {
                controller.setMapCenter(point);
                Log.i("TransformGeocodeThread","GeoPoint x:"+point.getLatitude()+" y:"+point.getLongitude());
            }
            else
                Toast.makeText(getContext(), "검색 결과가 없어요. 더 상세히 적어주세요!", Toast.LENGTH_SHORT).show();
        }

    }

    //좌표->주소 변화 쓰레드
    class AddrThread extends AsyncTask<String, String, String> {
        String point = Center_Point.getLatitude()+","+Center_Point.getLongitude();
        ProgressDialog progressDialog;
        int index = Checkedposition;
        @Override
         protected String doInBackground(String... strings) {
            String CenterAddr_Result = api.TransformAddr(api.Transform_ClientID,api.Transform_ClientSecret, point,api.UTF_8,api.LATING,null);
            query[Checkedposition] = api.TransformAddrParser(CenterAddr_Result)+" ";
            if((index!= FOOD) && (index != ENT) && (index != SUBWAY))
                query[Checkedposition] += strList[Checkedposition];
            else if(index==SUBWAY)
                query[Checkedposition] += "지하철 출구";

            AddrThead_Check = true;
            return null;
        }
         @Override
        protected void onPreExecute() {
            //진행바
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("데이터를 가져오는 중입니다...");
            progressDialog.show();

            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
        }
    }

    //주소->좌표 변화 쓰레드
    class GeoTransPointThread extends AsyncTask<String, String, String> {
        //현재 눌린 버튼에 대한 이벤트 처리를 위한 iCurrentSelet 저장
        int index = CheckedItem;
        ProgressDialog progressDialog;
        ArrayList<NGeoPoint> arrayGeoPoint= new ArrayList<NGeoPoint>();                             //지도API JSON파서에서 가져온 값을 좌표로 변환한 값 저장, 클릭 시 좌표 저장
        ArrayList<String> arrayTitle= new ArrayList<String>();
        ArrayList<HashMap<String, String>> arrayAddress = new ArrayList<HashMap<String,String>>();  // 지역API JSON파서에서 가져온 값을 저장
        int display = 100,start = 1;

        @Override
        protected String doInBackground(String... strings) {
            //초기화
            start=1;
            ArrayList<String> LocalInfo_Result= new ArrayList<String>();

            /*----------지역에 대한 정보 검색------------*/
            for(;start<=1001; start += 100){
                if(start==1001)
                    start = 1000;
                String result = api.SearchLocalInfo(api.SearchLocal_ClientID,api.SearchLocal_ClientSecret,query[index],display,start);
                System.out.println(result);
                LocalInfo_Result.add(result);
            }

            /*----------얻은 지역 정보를 분석해서 저장------------*/
            int cnt = api.SearchLocalParser(LocalInfo_Result, arrayAddress);
            Log.i("Cnt"," size : "+cnt);
            if(cnt != 0){
                for(int i =0; i < cnt; i++){
                    //arrayAddress에 있는 각 정보들을 카텍 -> 경위도 변경
                    try {
                        //arrayAddress에 있는 각 정보들 중 mapx,y를 가져옴
                        double dMapx = Double.parseDouble(arrayAddress.get(i).get("mapx"));
                        double dMapy = Double.parseDouble(arrayAddress.get(i).get("mapy"));

                        //가져온 카텍스 좌표로 되어있는 mapx,y를 경위도 변경
                        GeoTransPoint oKA = new GeoTransPoint(dMapx, dMapy);
                        GeoTransPoint oGeo = GeoTrans.convert(GeoTrans.KATEC, GeoTrans.GEO, oKA);
                        Double lat = oGeo.getY() * 1E6;
                        Double lng = oGeo.getX() * 1E6;

                        //변경된 자표를 NGeoPoint로 생성해서 arrayGeoPoint안에 넣어준다.
                        NGeoPoint geoPoint = new NGeoPoint(lat.intValue(), lng.intValue());  // 맵뷰에서 사용가능한 좌표계


                        float Distance[] = new float[2];
                        Location.distanceBetween(Center_Point.getLatitude(),Center_Point.getLongitude(),geoPoint.getLatitude(),geoPoint.getLongitude(),Distance);

                        if(Distance[0]<=Search_radius){
                            arrayGeoPoint.add(geoPoint);
                            arrayTitle.add(arrayAddress.get(i).get("title"));

                        }

                    }catch (Exception e){
                        Log.e("Geo Parser","Geo Parse Erorr : "+e.toString());
                    }
                }
                Log.i("GeoPoint","GeoPoint size : "+arrayGeoPoint.size());
            }
            else
                Log.i("Parser","Parser Result = 0");

            return null;
        }

        /*----------ProgressBAR 생성------------*/
        @Override
        protected void onPreExecute() {
            //진행바
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("데이터를 가져오는 중입니다...");
            progressDialog.show();

            super.onPreExecute();
        }

        /*----------위 정보들을 뷰에 실행시키기 위해------------*/
        @Override
        protected void onPostExecute(String s) {

            //생성된 arrayGeoPoint의 사이즈를 반환해서 Log에 나타낸다. <-- 오류 파악을 위해
            int arraySize = arrayGeoPoint.size();


            if(arraySize>0){
                //NAVER MAP 뷰어 상 나타내기 위한 여러 설정들
                int markerId = PIN_ID[index];

                Category_filter_poiData[index] = new NMapPOIdata(arraySize, mMapViewerResourceProvider);
                Category_filter_poiData[index].beginPOIdata(arraySize); //POI 아이템 추가를 시작한다.

                for(int i =0;i<arraySize;i++){

                    if(arrayGeoPoint.get(i)!=null){
                        NGeoPoint point = arrayGeoPoint.get(i);
                        Spanned t=Html.fromHtml(arrayTitle.get(i));
                        NMapPOIitem item = Category_filter_poiData[index].addPOIitem(point.getLatitude(),point.getLongitude(),t.toString(), markerId, i);
                        item.setRightAccessory(true, NMapPOIflagType.CLICKABLE_ARROW); //마커 선택 시 표시되는 말풍선의 오른쪽 아이콘을 설정한다.
                        item.setRightButton(true); //마커 선택 시 표시되는 말풍선의 오른쪽 버튼을 설정한다.
                    }

                }
                Category_filter_poiData[index].endPOIdata(); //POI 아이템 추가를 종료한다.

                poiDataOverlay = mOverlayManager. createPOIdataOverlay(Category_filter_poiData[index], null); //POI 데이터를 인자로 전달하여 NMapPOIdataOverlay 객체를 생성한다.
                poiDataOverlay.setOnStateChangeListener(new NMapPOIdataOverlay.OnStateChangeListener() {
                    @Override
                    public void onFocusChanged(NMapPOIdataOverlay nMapPOIdataOverlay, NMapPOIitem nMapPOIitem) {

                    }

                    @Override
                    public void onCalloutClick(NMapPOIdataOverlay nMapPOIdataOverlay, NMapPOIitem nMapPOIitem) {
                        try{
                            Spanned t=Html.fromHtml(arrayTitle.get(nMapPOIitem.getId()));
                            Uri uri = Uri.parse("https://search.naver.com/search.naver?query="+t.toString());
                            Intent it  = new Intent(Intent.ACTION_VIEW,uri);
                            startActivity(it);
                        }catch (Exception e){
                            Toast.makeText(getContext(),"해당 사이트에 접속 실패했어요ㅠㅅㅠ",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
            else
                Toast.makeText(getContext(),"'"+strList[index]+"'"+" 데이터가 없습니다.",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

        }

    }

    // 반경 그리기 위한 메소드
    public NMapCircleData CreatePathCircle(NMapPathDataOverlay pathDataOverlay, NMapOverlayManager mOverlayManager,int linetype, NGeoPoint point, float radius, NMapView nMapView){
        if(pathDataOverlay != null){
            //pathDataOverlay = mOverlayManager.createPathDataOverlay();
            // set circle data
            NMapCircleData circleData = new NMapCircleData(1);
            circleData.initCircleData();
            circleData.addCirclePoint(point.getLatitude(), point.getLongitude(), radius);
            circleData.endCircleData();
            pathDataOverlay.addCircleData(circleData);

            // set circle style
            NMapCircleStyle circleStyle = new NMapCircleStyle(nMapView.getContext());
            circleStyle.setLineType(linetype);
            circleStyle.setStrokeColor(0x002266, 0x90);
            circleStyle.setStrokeWidth(1f);
            circleStyle.setFillColor(0xA8F1FF, 0x4f);
            circleData.setCircleStyle(circleStyle);
            Log.i("Circle","radius : "+radius);
            return  circleData;
        }
        return  null;
    }

    //카테고리 필터 데이터들과 선택 초기화
    public void CategoryClear(NMapPOIdata[] poIdata, List<CategoryBin> binList){
        for(int i=0;i<poIdata.length;i++){
            if(poIdata[i] != null){
                if(poIdata[i].count() > 0)
                    poIdata[i].removeAllPOIdata();
            }
            binList.get(i).setSelected(false);
        }
    }

    //Poidata
    public void PoidataClear(NMapPOIdata poidata, NMapController controller){
        if(poidata != null) {
            poidata.removeAllPOIdata();
            controller.reload();
        }
    }

    //Poidata List
    public void PoidataListClear(NMapPOIdata[] poidata){
        for(int i=0;i<poidata.length;i++){
            if(poidata[i] != null){
                if(poidata[i].count() > 0)
                    poidata[i].removeAllPOIdata();
            }
        }
    }

    //코드 줄이기 위해 CreateCenterPin 만드는 함수 사용
    public void CreateCenterPin(){
        int markerId = NMapPOIflagType.CENTER;
        Center_poidata = new NMapPOIdata(1,mMapViewerResourceProvider);

        Center_poidata.beginPOIdata(1);
        Center_poidata.addPOIitem(Center_Point.getLatitude(),Center_Point.getLongitude(),"중간 위치",markerId,1000);
        Center_poidata.endPOIdata();

        poiDataOverlay = mOverlayManager.createPOIdataOverlay(Center_poidata, null);

    }


    //FAB Menu 애니메이션
    private void showFABMenu(){
        fab.setClickable(false);
        fab1.setClickable(false);
        fab2.setClickable(false);
        fab3.setClickable(false);
        fab4.setClickable(false);
        isFABOpen = !isFABOpen;
        fab.animate().rotationBy(45);
        fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_58));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_108));
        fab3.animate().translationY(-getResources().getDimension(R.dimen.standard_158));
        fab4.animate().translationY(-getResources().getDimension(R.dimen.standard_208));
        //btnHelp 는 팹버튼 옆에 도움말

            btnHelp_1.animate().translationY(-getResources().getDimension(R.dimen.standard_58));
            btnHelp_2.animate().translationY(-getResources().getDimension(R.dimen.standard_108));
            btnHelp_3.animate().translationY(-getResources().getDimension(R.dimen.standard_158));
            btnHelp_4.animate().translationY(-getResources().getDimension(R.dimen.standard_208));


        if(setting[0]) {
            btnHelp_1.setVisibility(View.VISIBLE);
            btnHelp_2.setVisibility(View.VISIBLE);
            btnHelp_3.setVisibility(View.VISIBLE);
            if (fab4.getVisibility() == View.VISIBLE)
                btnHelp_4.setVisibility(View.VISIBLE);
            else
                btnHelp_4.setVisibility(View.GONE);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fab.setClickable(true);
                fab1.setClickable(true);
                fab2.setClickable(true);
                fab3.setClickable(true);
                fab4.setClickable(true);
            }
        },300);

    }

    private void closeFABMenu(){
        fab.setClickable(false);
        fab1.setClickable(false);
        fab2.setClickable(false);
        fab3.setClickable(false);
        fab4.setClickable(false);
        isFABOpen = !isFABOpen;
        fab.animate().rotationBy(-45);
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        fab3.animate().translationY(0);
        fab4.animate().translationY(0);

        btnHelp_1.animate().translationY(0);
        btnHelp_2.animate().translationY(0);
        btnHelp_3.animate().translationY(0);
        btnHelp_4.animate().translationY(0);

        btnHelp_1.setVisibility(View.GONE);
        btnHelp_2.setVisibility(View.GONE);
        btnHelp_3.setVisibility(View.GONE);
        btnHelp_4.setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fab.setClickable(true);
                fab1.setClickable(true);
                fab2.setClickable(true);
                fab3.setClickable(true);
                fab4.setClickable(true);


            }
        },300);
    }

    private void FindMyLocation(){
        if (locationManager.isMyLocationEnabled()) {
            if (locationManager.enableMyLocation(true)) {
                if(locationManager.isMyLocationFixed()){
                    if(mMyLocationOverlay != null){
                        mMyLocationOverlay.refresh();
                        controller.setMapCenter(locationManager.getMyLocation());
                        controller.reload();
                    }
                    else{
                        if(mMyLocationOverlay.hasPathData()){
                            mMyLocationOverlay.refresh();
                            controller.setMapCenter(locationManager.getMyLocation());
                        }
                    }

                }
                else{
                    locationManager.disableMyLocation();
                    Toast.makeText(getContext(),"현재 위치탐색 실패!",Toast.LENGTH_SHORT).show();
                }
            }
            else{
                locationManager.disableMyLocation();
                Toast.makeText(getContext(),"현재 위치탐색 불가능",Toast.LENGTH_SHORT).show();
            }

        } else {
            boolean isMyLocationEnabled = locationManager.enableMyLocation(true);
            if (!isMyLocationEnabled) {
                new AlertDialog.Builder(getContext()).setTitle("위치 서비스 설정")
                        .setMessage("일루와 앱에서 내 위치를 보려면, 단말기의 설정에서 '위치서비스' 사용을 허용해주세요.")
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                gpsPower = false;
                                btnNavi.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.navermap_location_off));
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                startActivity(intent);}
                        })
                        .show();
                return;
            }
        }
    }

}

