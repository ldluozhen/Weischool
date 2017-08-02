package com.example.administrator.weischool;

                import com.baidu.location.BDLocation;
                import com.baidu.location.BDLocationListener;
                import com.baidu.location.LocationClient;
                import com.baidu.location.LocationClientOption;
                import com.baidu.mapapi.SDKInitializer;
                import com.baidu.mapapi.map.BaiduMap;
                import com.baidu.mapapi.map.MapStatus;
                import com.baidu.mapapi.map.MapStatusUpdateFactory;
                import com.baidu.mapapi.map.MapView;
                import com.baidu.mapapi.map.MyLocationData;
                import com.baidu.mapapi.model.LatLng;

                import android.app.Activity;
                import android.os.Bundle;
                import android.widget.GridView;
                import android.widget.SimpleAdapter;

                import java.util.ArrayList;
                import java.util.HashMap;
                import java.util.List;
                import java.util.Map;

public class MapActivity extends Activity {
    /**
     * 定位SDK核心类
     */
    private LocationClient locationClient;
    /**
     * 定位监听
     */
    public MyLocationListenner myListener = new MyLocationListenner();
    /**
     * 百度地图控件
     */
    private MapView mapView;
    /**
     * 百度地图对象
     */
    private BaiduMap baiduMap;

    boolean isFirstLoc = true; // 是否首次定位

    /*
    *   以下为GridView的相关声明
    * */
    private GridView map_grid;
    private List<Map<String, Object>> data_list;
    private SimpleAdapter sim_adapter;
    // 图片封装为一个数组



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.map_activity);
        /**
         * 地图初始化
         */
        //获取百度地图控件
        mapView = (MapView) findViewById(R.id.mv_map);
        //获取百度地图对象
        baiduMap = mapView.getMap();
        // 开启定位图层
        baiduMap.setMyLocationEnabled(true);
        /**
         * 定位初始化
         */
        //声明定位SDK核心类
        locationClient = new LocationClient(this);
        //注册监听
        locationClient.registerLocationListener(myListener);
        //定位配置信息
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);//定位请求时间间隔
        locationClient.setLocOption(option);
        //开启定位
        locationClient.start();

  /*      map_grid = (GridView) findViewById(R.id.map_gview);
        //新建List
        data_list = new ArrayList<Map<String, Object>>();
        //获取数据
        getData();
        //新建适配器
        String[] from = {"image", "text"};
        int[] to = {R.id.item_image, R.id.item_text};
        sim_adapter = new SimpleAdapter(this, data_list, R.layout.item, from, to);
        //配置适配器
        map_grid.setAdapter(sim_adapter);*/
    }



    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            baiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        locationClient.stop();
        // 关闭定位图层
        baiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView = null;
        super.onDestroy();
    }
        /*
    * 以下为GridView的相关函数
    * */
/*    public List<Map<String, Object>> getData() {
        //cion和iconName的长度是相同的，这里任选其一都可以
        for (int i = 0; i < icon.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", icon[i]);
            map.put("text", iconName[i]);
            data_list.add(map);
        }
        return data_list;
    }*/
}




