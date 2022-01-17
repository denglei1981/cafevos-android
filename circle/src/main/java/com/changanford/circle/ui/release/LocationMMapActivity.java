package com.changanford.circle.ui.release;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.changanford.circle.R;
import com.changanford.circle.bean.NavationMap;
import com.changanford.circle.databinding.MmapActivityBinding;
import com.changanford.circle.databinding.MmapLocationActivityBinding;
import com.changanford.common.basic.BaseActivity;
import com.changanford.common.basic.BaseApplication;
import com.changanford.common.bean.LocationDataBean;
import com.changanford.common.bean.MapReturnBean;
import com.changanford.common.net.CommonResponse;
import com.changanford.common.sharelib.util.JsonUtil;
import com.changanford.common.ui.dialog.LoadDialog;
import com.changanford.common.util.AppUtils;
import com.changanford.common.util.JumpUtils;
import com.changanford.common.util.MConstant;
import com.changanford.common.util.SPUtils;
import com.google.gson.Gson;
import com.luck.picture.lib.tools.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class LocationMMapActivity extends BaseActivity<MmapLocationActivityBinding, PerfectQuessionActivityViewModule> implements OnGetPoiSearchResultListener {
    private LocationClient mLocationClient;
    BaiduMap baiduMap;

    //防止每次定位都重新设置中心点和marker
    private boolean isFirstLocation = true;
    private boolean shoudPoi = true;
    private boolean shoudMove = true;
    //初始化LocationClient定位类
    //BDAbstractLocationListener为7.2版本新增的Abstract类型的监听接口，原有BDLocationListener接口
    private BDLocationListener myListener = new MyLocationListener();
    //经纬度
    private double lat;
    private double lon;
    private String address;
    private String city;
    private BDLocation mlocation;
    private PoiInfo poiInfo;
    private PoiSearch mPoiSearch;

    private MapReturnBean mapReturnBean = new MapReturnBean();

    private List<PoiInfo> ml = new ArrayList<>();
    private boolean located = false;

    private boolean isquerypoi = false;
    private LoadDialog alertDialog;

    //    double lat ;
//    double lan ;
    @Override
    public void initView() {
        AppUtils.setStatusBarHeight(binding.ivBack, this);

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lat = getIntent().getDoubleExtra("lat", 0.0);
        lon = getIntent().getDoubleExtra("lon", 0.0);
        address=getIntent().getStringExtra("address");
        if(!TextUtils.isEmpty(address)){
            binding.tvLocationBig.setText(address);
        }
        navationLocation=new NavationMap(lat, lon, address);
        initMap();
    }

    private void shearch(String shearch) {
        shoudPoi = true;
        shoudMove = true;
        PoiCitySearchOption poiCitySearchOption = new PoiCitySearchOption();
        poiCitySearchOption.mIsCityLimit = false;
        poiCitySearchOption.city(city);
        poiCitySearchOption.keyword(shearch);
        mPoiSearch.searchInCity(poiCitySearchOption);
    }

    @Override
    public void initData() {

        binding.ivNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navationLocation != null) {
                    Gson mGson = new Gson();
                    Objects.requireNonNull(JumpUtils.getInstans()).jump(69, mGson.toJson(navationLocation));
                }
            }
        });
    }

    /**
     * 初始化地图
     */
    public void initMap() {
        //得到地图实例
        baiduMap = binding.bmapView.getMap();
        mPoiSearch = PoiSearch.newInstance();
        baiduMap.setOnMapStatusChangeListener(listener);
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        /*
        设置地图类型
         */
        //普通地图
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //卫星地图
        //baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        //空白地图, 基础地图瓦片将不会被渲染。在地图类型中设置为NONE，将不会使用流量下载基础地图瓦片图层。使用场景：与瓦片图层一起使用，节省流量，提升自定义瓦片图下载速度。
        //baiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
        //开启交通图
        baiduMap.setTrafficEnabled(true);
        //关闭缩放按钮
        binding.bmapView.showZoomControls(false);
        // 开启定位图层
        baiduMap.setMyLocationEnabled(true);
        //声明LocationClient类
        mLocationClient = new LocationClient(this);
        //注册监听函数
        mLocationClient.registerLocationListener(myListener);

        if (lon >= 0 && lat >= 0) {
            movedot(lat, lon);
        } else {
            initLocation();
            //开始定位
            mLocationClient.start();
            alertDialog = new LoadDialog(this);
            alertDialog.setLoadingText("定位中请稍后...");
            alertDialog.show();
            alertDialog.setCanceledOnTouchOutside(true);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        binding.bmapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        binding.bmapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        baiduMap.setMyLocationEnabled(false);
        binding.bmapView.onDestroy();
    }


    /**
     * 配置定位参数
     */
    private void initLocation() {
        LocationClientOption locationOption = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
//        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("bd09ll");
        //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(0);
        //可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true);
        //可选，设置是否需要地址描述
        locationOption.setIsNeedLocationDescribe(true);
        //可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false);
        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.setLocationNotify(true);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否收集CRASH信息，默认收集;
        locationOption.SetIgnoreCacheException(false);
        //可选，默认false，设置是否开启Gps定位
        locationOption.setOpenGps(true);
        //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false);
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationOption.setOpenAutoNotifyMode();
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        locationOption.setOpenAutoNotifyMode(3000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        mLocationClient.setLocOption(locationOption);
        //开始定位
        //设置地图单击事件监听
    }


    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
            //创建PoiOverlay对象

            ml.clear();
            ml.addAll(poiResult.getAllPoi());

            if (ml != null && ml.size() > 0) {
                poiInfo = ml.get(0);
//                binding.tvAddress.setText(poiInfo.getName());
                shoudPoi = false;
                if (shoudMove) {
                    if (ml.size() > 0 && ml.get(0).getLocation() != null) {
                        movedot(ml.get(0).getLocation().latitude, ml.get(0).getLocation().longitude);
                    }
                }
            }
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    NavationMap navationLocation;

    /**
     * 实现定位监听 位置一旦有所改变就会调用这个方法
     * 可以在这个方法里面获取到定位之后获取到的一系列数据
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {


            mlocation = location;
            //获取定位结果
            location.getTime();    //获取定位时间
            location.getLocationID();    //获取定位唯一ID，v7.2版本新增，用于排查定位问题
            location.getLocType();    //获取定位类型
            location.getLatitude();    //获取纬度信息
            location.getLongitude();    //获取经度信息
            location.getRadius();    //获取定位精准度
            location.getAddrStr();    //获取地址信息
            location.getCountry();    //获取国家信息
            location.getCountryCode();    //获取国家码
            location.getCity();    //获取城市信息
            location.getCityCode();    //获取城市码
            location.getDistrict();    //获取区县信息
            location.getStreet();    //获取街道信息
            location.getStreetNumber();    //获取街道码
            location.getLocationDescribe();    //获取当前位置描述信息
            location.getPoiList();    //获取当前位置周边POI信息

            location.getBuildingID();    //室内精准定位下，获取楼宇ID
            location.getBuildingName();    //室内精准定位下，获取楼宇名称
            location.getFloor();    //室内精准定位下，获取当前位置所处的楼层信息
            JSONObject js = new JSONObject();
            js.put("latitude", location.getLatitude());
            js.put("longitude", location.getLongitude());
            js.put("address", location.getAddress());
            js.put("adCode", location.getAdCode());
            js.put("cityCode", location.getCityCode());
            js.put("city", location.getCity());
            js.put("countryCode", location.getCountryCode());
            js.put("country", location.getCountry());
            js.put("cityCode", location.getCityCode());
            js.put("district", location.getDistrict());
            js.put("province", location.getProvince());
            SPUtils.INSTANCE.setParam(LocationMMapActivity.this, MConstant.LOCATION_BD, js.toJSONString());
            //经纬度
            lat = location.getLatitude();
            lon = location.getLongitude();
            city = location.getCity();
            navationLocation = new NavationMap(lat, lon, location.getAddress().address);
            mapReturnBean.setCid(location.getCityCode());
            mapReturnBean.setCityName(location.getCity());
            mapReturnBean.setShefenName(location.getProvince());
            mapReturnBean.setQuxianName(location.getDistrict());
            binding.tvLocationSmall.setText(location.getLocationDescribe());
            binding.tvLocationBig.setText(location.getAddress().address);
            //这个判断是为了防止每次定位都重新设置中心点和marker
            if (!located) {
                if (!TextUtils.isEmpty(city)) {
                    isquerypoi = true;
                    movedot(lat, lon);
                    alertDialog.dismiss();
                } else {
                    alertDialog.dismiss();
                    ToastUtils.s(BaseApplication.INSTANT, "定位失败");
                }
                located = true;
            }

        }
    }

    /**
     * 设置中心点和添加marker
     *
     * @param map
     * @param bdLocation
     * @param isShowLoc
     */
    public void setPosition2Center(BaiduMap map, BDLocation bdLocation, Boolean isShowLoc) {
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(bdLocation.getRadius())
                .direction(bdLocation.getRadius()).latitude(bdLocation.getLatitude())
                .longitude(bdLocation.getLongitude()).build();
        map.setMyLocationData(locData);

        if (isShowLoc) {
            LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(18.0f);
            map.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }

    /**
     * 设置中心点和添加marker
     *
     * @param
     * @param
     * @param
     */
    public void movedot(double latitud, double longitude) {

        LatLng point = new LatLng(latitud, longitude);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_map_location);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
//在地图上添加Marker，并显示
        baiduMap.addOverlay(option);
        //设定中心点坐标
        LatLng cenpt = new LatLng(latitud, longitude);
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                //要移动的点
                .target(cenpt)
                //放大地图到20倍
                .zoom(16)
                .build();

        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);

        //改变地图状态
        baiduMap.setMapStatus(mMapStatusUpdate);
    }

    public void poi() {
        /**
         * 以天安门为中心，搜索半径100米以内的餐厅
         */
        mPoiSearch.searchNearby(new PoiNearbySearchOption()
                .location(new LatLng(lat, lon))
                .radius(1000)
                .keyword("公司")
                .pageNum(10));

    }


    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }


    BaiduMap.OnMapStatusChangeListener listener = new BaiduMap.OnMapStatusChangeListener() {
        /**
         * 手势操作地图，设置地图状态等操作导致地图状态开始改变。
         *
         * @param status 地图状态改变开始时的地图状态
         */
        @Override
        public void onMapStatusChangeStart(MapStatus status) {
        }

        /**
         * 手势操作地图，设置地图状态等操作导致地图状态开始改变。
         *
         * @param status 地图状态改变开始时的地图状态
         *
         * @param reason 地图状态改变的原因
         */

        //用户手势触发导致的地图状态改变,比如双击、拖拽、滑动底图
        //int REASON_GESTURE = 1;
        //SDK导致的地图状态改变, 比如点击缩放控件、指南针图标
        //int REASON_API_ANIMATION = 2;
        //开发者调用,导致的地图状态改变
        //int REASON_DEVELOPER_ANIMATION = 3;
        @Override
        public void onMapStatusChangeStart(MapStatus status, int reason) {
            if (reason == REASON_GESTURE) {
                shoudPoi = true;
                shoudMove = false;
            }
        }

        /**
         * 地图状态变化中
         *
         * @param status 当前地图状态
         */
        @Override
        public void onMapStatusChange(MapStatus status) {

        }

        /**
         * 地图状态改变结束
         *
         * @param status 地图状态改变结束后的地图状态
         */
        @Override
        public void onMapStatusChangeFinish(MapStatus status) {
            LatLng latLng = status.target;
            lat = latLng.latitude;
            lon = latLng.longitude;
            if (shoudPoi && isquerypoi) {
                poi();
            }
        }
    };


    private void getLocation(final double lat, final double lon) {
        viewModel.getCityDetailBylngAndlat(lat, lon, new Function1<CommonResponse<LocationDataBean>, Unit>() {
            @Override
            public Unit invoke(CommonResponse<LocationDataBean> response) {
                if (response.getCode() == 0) {
                    LocationDataBean locationDataBean = response.getData();
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    mapReturnBean.setSid(locationDataBean.getProvinceCode());
                    mapReturnBean.setCid(locationDataBean.getCityCode());
                    mapReturnBean.setCityName(locationDataBean.getCityName());
                    bundle.putParcelable("poi", mapReturnBean);
                    intent.putExtra("mbundaddress", bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                return null;
            }
        });

    }

}
