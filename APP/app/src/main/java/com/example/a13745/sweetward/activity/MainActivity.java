package com.example.a13745.sweetward.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Environment;
import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a13745.sweetward.R;
import com.example.a13745.sweetward.database.DbOperator;
import com.example.a13745.sweetward.database.info.ClothesInfo;
import com.example.a13745.sweetward.socket.SocketClient;
import com.example.a13745.sweetward.socket.info.Clothes;
import com.example.a13745.sweetward.socket.info.UserInfo;
import com.example.a13745.sweetward.tools.Info;
import com.example.a13745.sweetward.weather.gson.Forecast;
import com.example.a13745.sweetward.weather.gson.Weather;
import com.example.a13745.sweetward.weather.util.HttpUtil;
import com.example.a13745.sweetward.weather.util.Utility;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

//主界面
//需要获取读取SD卡的权限问题
//需要初始化数据库
public class MainActivity extends AppCompatActivity {
    //下面两行跟权限有关
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {  Manifest.permission.WRITE_EXTERNAL_STORAGE };

    //数据库
    public DbOperator db;

    //天气控件
    private TextView areaText;
    private TextView areaTemp;
    private TextView areaWStatus;
    private TextView areaTime;
    private TextView dressSuggestion;
    private TextView first;
    private TextView second;
    private TextView third;
    public Weather nowWeather;
    public ArrayList<Integer> weatherData;

    //对应socket信息
    private String host="10.0.2.2";        //对应客户机ip
    private int post=55534;                 //对应端口

    public SocketClient socketclient=new SocketClient(host,post);   //对应客户端
    public int user_id=-1;                                            //用户id

    //存储的是标号
    List<Integer> list_store_coat=new ArrayList<Integer>();                   //外套
    List<Integer> list_store_up=new ArrayList<Integer>();                     //上衣
    List<Integer> list_store_down=new ArrayList<Integer>();                   //裤装
    List<Integer> list_store_shoe=new ArrayList<Integer>();                   //鞋子
    //对应于上面的index
    int num_coat=-1;
    int num_up=-1;
    int num_down=-1;
    int num_shoe=-1;
    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //解决关于SD的访问问题
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED)
        { // We don't have permission so prompt the user
            ActivityCompat.requestPermissions( activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE );
        }
        else
        {
            Log.d(TAG,"我们有权限了");
        }
    }

    private SharedPreferences preferences;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //似乎与网络权限有关
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());
        fullScreenChange();
        super.onCreate(savedInstanceState);
        db=new DbOperator();
        setContentView(R.layout.activity_main);

        //初始的数据信息只能初始化一次
        //插入基本的数据信息
        //3件外套
        list_store_coat.add(4);
        list_store_coat.add(5);
        list_store_coat.add(6);
        //3件上衣
        list_store_up.add(1);
        list_store_up.add(2);
        list_store_up.add(3);
        //3件裤装
        list_store_down.add(7);
        list_store_down.add(8);
        list_store_down.add(9);
        //3件鞋子
        list_store_shoe.add(10);
        list_store_shoe.add(11);
        list_store_shoe.add(12);
        insertData();

        //天气
        initWeather();
        //点击事件
        addClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if(resultCode == RESULT_OK) {
                    String weatherString = data.getStringExtra("weather_id");
                    requestWeather(weatherString);
                }
        }
    }

    public void fullScreenChange() {
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean fullScreen = mPreferences.getBoolean("fullScreen", false);
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        mPreferences.edit().putBoolean("fullScreen", true).commit();
    }

    //天气初始化
    public void initWeather() {
        Log.d("initWeather", "开始天气初始化");
        nowWeather = null;
        weatherData = null;
        areaTemp = (TextView)findViewById(R.id.weaTemp1);
        areaWStatus = (TextView)findViewById(R.id.weaTemp2);
        areaTime = (TextView)findViewById(R.id.weaCond1);
        areaText = (TextView)findViewById(R.id.weaCond2);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String weatherString = prefs.getString("weather", null);
        if(weatherString != null) {
            //有缓存
            requestWeather(weatherString);
        }
        else {
            //无缓存
            String weatherId = getIntent().getStringExtra("weather_id");
            requestWeather(weatherId);
        }
    }

    //天气功能函数
    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=23c852cf8c3946349afc0612c6e06705";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("requestWeather", "到这了！！！！！！！");
                        Toast.makeText(MainActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                            editor.putString("weather", weatherId);
                            editor.apply();
                            Log.d("RecommendActivity", responseText);
                            nowWeather = weather;
                            showWeatherInfo(weather);
                            setWeatherData(weather);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                            Log.d(TAG,"获取天气信息失败");
                        }
                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime;
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        //String suggestion = weather.suggestion.dressSuggestion.info;
        /*
        int i = 0;
        for(Forecast forecast : weather.forecastList) {
            if(i == 3)
                break;
            else
                i++;
            String forecastTemp = forecast.date + "\t" +forecast.temperature.max + "℃~" + forecast.temperature.min + "℃\t";
            String forecastStatus;
            if(forecast.more.info_n != null)
                forecastStatus = forecast.more.info_d + "转" + forecast.more.info_n;
            else
                forecastStatus = forecast.more.info_d;
            String forecastTxt = forecastTemp + forecastStatus;
            if(i == 1)
                first.setText(forecastTxt);
            else if(i == 2)
                second.setText(forecastTxt);
            else if(i == 3)
                third.setText(forecastTxt);
        }*/
        areaText.setText(cityName);
        areaTemp.setText(degree);
        areaWStatus.setText(weatherInfo);
        areaTime.setText(updateTime);
        //dressSuggestion.setText(suggestion);
    }

    public void setWeatherData(Weather weather) {
        if(weatherData == null)
            weatherData = new ArrayList<Integer>();

    }

    //基本的按钮功能
    public void addClick() {
        final ImageButton close_icon = (ImageButton)findViewById(R.id.closeIcon);
        //退出按钮
        close_icon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Dialog dialog = new AlertDialog.Builder(MainActivity.this) .setTitle("退出").setMessage("是否退出")
                        .setPositiveButton("确认",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.this.finish();
                            }
                        }).setNegativeButton("取消",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {}}).create();
                dialog.show();
            }
        });

    	//最后一行
        //脏衣篮
        final ImageButton dirty_icon = (ImageButton)findViewById(R.id.dirtyIcon);
        dirty_icon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, DirtyActivity.class);
                MainActivity.this.startActivity(intent);
                //MainActivity.this.finish();
            }
        });

        //衣服导入
        ImageButton input_icon = (ImageButton)findViewById(R.id.inputIcon);
        input_icon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, InputActivity.class);
                MainActivity.this.startActivity(intent);
                //MainActivity.this.finish();
            }
        });

        //我的衣柜
        ImageButton my_icon = (ImageButton)findViewById(R.id.myIcon);
        my_icon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, WardActivity.class);
                MainActivity.this.startActivity(intent);
                //MainActivity.this.finish();
            }
        });

        //试衣间
        ImageButton try_icon = (ImageButton)findViewById(R.id.tryIcon);
        try_icon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, RecomActivity.class);
                MainActivity.this.startActivity(intent);
                //MainActivity.this.finish();
            }
        });

        //收藏夹
        ImageButton store_icon = (ImageButton)findViewById(R.id.storeIcon);
        store_icon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, StoreActivity.class);
                MainActivity.this.startActivity(intent);
                //MainActivity.this.finish();
            }
        });

        //天气
        Button adress_icon = (Button)findViewById(R.id.adressIcon);
        adress_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        //添加四件套的点击事件
        final ImageButton clothes1_button = (ImageButton) findViewById(R.id.coat);
        clothes1_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                num_up=(num_up+1)%list_store_up.size();
                String return_path=Environment.getExternalStorageDirectory().getPath()+"/s"+user_id+"_"+list_store_up.get(num_up)+".jpg";
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inSampleSize = 4;
                Bitmap bm_top = BitmapFactory.decodeFile(return_path, option);
                clothes1_button.setImageBitmap(bm_top);
            }
        });

        final ImageButton clothes2_button = (ImageButton) findViewById(R.id.clothes2);
        clothes2_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                num_down=(num_down+1)%list_store_down.size();
                String return_path=Environment.getExternalStorageDirectory().getPath()+"/s"+user_id+"_"+list_store_down.get(num_down)+".jpg";
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inSampleSize = 4;
                Bitmap bm_top = BitmapFactory.decodeFile(return_path, option);
                clothes2_button.setImageBitmap(bm_top);
            }
        });

        final ImageButton coat_button = (ImageButton) findViewById(R.id.clothes1);
        coat_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                num_coat=(num_coat+1)%list_store_coat.size();
                String return_path=Environment.getExternalStorageDirectory().getPath()+"/s"+user_id+"_"+list_store_coat.get(num_coat)+".jpg";
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inSampleSize = 4;
                Bitmap bm_top = BitmapFactory.decodeFile(return_path, option);
                coat_button.setImageBitmap(bm_top);
            }
        });

        final ImageButton shoe_button = (ImageButton) findViewById(R.id.shoes);
        shoe_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                num_shoe=(num_shoe+1)%list_store_shoe.size();
                String return_path=Environment.getExternalStorageDirectory().getPath()+"/s"+user_id+"_"+list_store_shoe.get(num_shoe)+".jpg";
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inSampleSize = 4;
                Bitmap bm_top = BitmapFactory.decodeFile(return_path, option);
                shoe_button.setImageBitmap(bm_top);
            }
        });
        //整套下一套，未实现
        ImageButton recom_icon1=(ImageButton)findViewById(R.id.next_1);     //推荐整套套装
        recom_icon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这部分推荐信息不准确
                List<Integer> val2 = Arrays.asList(2,10,-2,55,38,5,4);
                com.example.a13745.sweetward.socket.info.Weather send_weather = new com.example.a13745.sweetward.socket.info.Weather(val2);
                Map<String, Integer> params=new HashMap<>();
                params.put("裤装",num_down==-1?-1:list_store_down.get(num_down));
                params.put("鞋子",num_shoe==-1?-1:list_store_shoe.get(num_shoe));
                params.put("上衣",num_up==-1?-1:list_store_up.get(num_up));
                try {
                    String return_id=socketclient.sendTodayInfo(user_id,send_weather,params,1);  //期待得到返回外套
                    params=new HashMap<>();
                    params.put("裤装",num_down==-1?-1:list_store_down.get(num_down));
                    params.put("鞋子",num_shoe==-1?-1:list_store_shoe.get(num_shoe));
                    params.put("外套",num_coat==-1?-1:list_store_coat.get(num_coat));
                    return_id=socketclient.sendTodayInfo(user_id,send_weather,params,2);  //期待得到返回信息上衣
                    params=new HashMap<>();
                    params.put("上衣",num_up==-1?-1:list_store_up.get(num_up));
                    params.put("鞋子",num_shoe==-1?-1:list_store_shoe.get(num_shoe));
                    params.put("外套",num_coat==-1?-1:list_store_coat.get(num_coat));
                    return_id=socketclient.sendTodayInfo(user_id,send_weather,params,3);  //期待得到返回信息裤装
                    params=new HashMap<>();
                    params.put("上衣",num_up==-1?-1:list_store_up.get(num_up));
                    params.put("裤装",num_down==-1?-1:list_store_down.get(num_down));
                    params.put("外套",num_coat==-1?-1:list_store_coat.get(num_coat));
                    return_id=socketclient.sendTodayInfo(user_id,send_weather,params,4);  //期待得到返回信息鞋子
                    Log.d(TAG,"返回的推荐信息为:"+return_id+",6,7,8");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //改变上装
                num_up=(num_up+1)%list_store_up.size();
                String return_path=Environment.getExternalStorageDirectory().getPath()+"/s"+user_id+"_"+list_store_up.get(num_up)+".jpg";
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inSampleSize = 4;
                Bitmap bm_top = BitmapFactory.decodeFile(return_path, option);
                clothes1_button.setImageBitmap(bm_top);
                //下装
                num_down=(num_down+1)%list_store_down.size();
                return_path=Environment.getExternalStorageDirectory().getPath()+"/s"+user_id+"_"+list_store_down.get(num_down)+".jpg";
                option = new BitmapFactory.Options();
                option.inSampleSize = 4;
                bm_top = BitmapFactory.decodeFile(return_path, option);
                clothes2_button.setImageBitmap(bm_top);
                //外套
                num_coat=(num_coat+1)%list_store_coat.size();
                return_path=Environment.getExternalStorageDirectory().getPath()+"/s"+user_id+"_"+list_store_coat.get(num_coat)+".jpg";
                option = new BitmapFactory.Options();
                option.inSampleSize = 4;
                bm_top = BitmapFactory.decodeFile(return_path, option);
                coat_button.setImageBitmap(bm_top);
                //鞋子
                num_shoe=(num_shoe+1)%list_store_shoe.size();
                return_path=Environment.getExternalStorageDirectory().getPath()+"/s"+user_id+"_"+list_store_shoe.get(num_shoe)+".jpg";
                option = new BitmapFactory.Options();
                option.inSampleSize = 4;
                bm_top = BitmapFactory.decodeFile(return_path, option);
                shoe_button.setImageBitmap(bm_top);
            }
        });

        final String[] texts=new String[]{"外套","鞋子","上衣","裤装"}; //不同的类别选择
        ImageButton recom_icon2=(ImageButton)findViewById(R.id.next_2);     //推荐某种特定的服装
        recom_icon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this) // build AlertDialog
                        .setTitle("选择衣服类别") // title
                        .setItems(texts,new DialogInterface.OnClickListener() { //content
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String temp = texts[which];
                                new AlertDialog.Builder(MainActivity.this)
                                        .setMessage("你选择的是" + texts[which])
                                        .setNeutralButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {


                                                switch(temp){
                                                    case "外套":
                                                    {
                                                        ImageButton clothes_image = (ImageButton) findViewById(R.id.coat);
                                                        //需要从天气中获取
                                                        List<Integer> val2 = Arrays.asList(2,3,4,5,6,7,8);
                                                        com.example.a13745.sweetward.socket.info.Weather send_weather = new com.example.a13745.sweetward.socket.info.Weather(val2);
                                                        Map<String, Integer> params=new HashMap<>();
                                                        params.put("裤装",num_down==-1?-1:list_store_down.get(num_down));
                                                        params.put("鞋子",num_shoe==-1?-1:list_store_shoe.get(num_shoe));
                                                        params.put("上衣",num_up==-1?-1:list_store_up.get(num_up));
                                                        try {
                                                            String return_id=socketclient.sendTodayInfo(user_id,send_weather,params,1);  //期待得到返回信息
                                                            Log.d(TAG,"返回的推荐信息为:"+return_id);
                                                            String return_path=Environment.getExternalStorageDirectory().getPath()+"/s"+user_id+"_"+return_id+".jpg";
                                                            BitmapFactory.Options option = new BitmapFactory.Options();
                                                            option.inSampleSize = 4;
                                                            Bitmap bm_top = BitmapFactory.decodeFile(return_path, option);
                                                            clothes_image.setImageBitmap(bm_top);
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                        break;
                                                    }
                                                    case "鞋子":
                                                    {
                                                        ImageButton clothes_image = (ImageButton) findViewById(R.id.shoes);
                                                        List<Integer> val2 = Arrays.asList(2,3,4,5,6,7,8);
                                                        com.example.a13745.sweetward.socket.info.Weather send_weather = new com.example.a13745.sweetward.socket.info.Weather(val2);
                                                        Map<String, Integer> params=new HashMap<>();
                                                        params.put("裤装",num_down==-1?-1:list_store_down.get(num_down));
                                                        params.put("外套",num_coat==-1?-1:list_store_coat.get(num_coat));
                                                        params.put("上衣",num_up==-1?-1:list_store_up.get(num_up));
                                                        try {
                                                            String return_id=socketclient.sendTodayInfo(user_id,send_weather,params,4);  //期待得到返回信息
                                                            Log.d(TAG,"返回的推荐信息为:"+return_id);
                                                            String return_path=Environment.getExternalStorageDirectory().getPath()+"/s"+user_id+"_"+return_id+".jpg";
                                                            BitmapFactory.Options option = new BitmapFactory.Options();
                                                            option.inSampleSize = 4;
                                                            Bitmap bm_top = BitmapFactory.decodeFile(return_path, option);
                                                            clothes_image.setImageBitmap(bm_top);
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                        break;
                                                    }
                                                    case "上衣":
                                                    {
                                                        ImageButton clothes_image = (ImageButton) findViewById(R.id.clothes1);
                                                        List<Integer> val2 = Arrays.asList(2,3,4,5,6,7,8);
                                                        com.example.a13745.sweetward.socket.info.Weather send_weather = new com.example.a13745.sweetward.socket.info.Weather(val2);
                                                        Map<String, Integer> params=new HashMap<>();
                                                        params.put("裤装",num_down==-1?-1:list_store_down.get(num_down));
                                                        params.put("鞋子",num_shoe==-1?-1:list_store_shoe.get(num_shoe));
                                                        params.put("外套",num_coat==-1?-1:list_store_coat.get(num_coat));
                                                        try {
                                                            String return_id=socketclient.sendTodayInfo(user_id,send_weather,params,2);  //期待得到返回信息
                                                            Log.d(TAG,"返回的推荐信息为:"+return_id);
                                                            String return_path=Environment.getExternalStorageDirectory().getPath()+"/s"+user_id+"_"+return_id+".jpg";
                                                            BitmapFactory.Options option = new BitmapFactory.Options();
                                                            option.inSampleSize = 4;
                                                            Bitmap bm_top = BitmapFactory.decodeFile(return_path, option);
                                                            clothes_image.setImageBitmap(bm_top);
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                        break;
                                                    }
                                                    case "裤装":
                                                    {
                                                        ImageButton clothes_image = (ImageButton) findViewById(R.id.clothes2);
                                                        List<Integer> val2 = Arrays.asList(2,3,4,5,6,7,8);
                                                        com.example.a13745.sweetward.socket.info.Weather send_weather = new com.example.a13745.sweetward.socket.info.Weather(val2);
                                                        Map<String, Integer> params=new HashMap<>();
                                                        params.put("外套",num_down==-1?-1:list_store_coat.get(num_coat));
                                                        params.put("鞋子",num_shoe==-1?-1:list_store_shoe.get(num_shoe));
                                                        params.put("上衣",num_up==-1?-1:list_store_up.get(num_up));
                                                        try {
                                                            String return_id=socketclient.sendTodayInfo(user_id,send_weather,params,3);  //期待得到返回信息
                                                            Log.d(TAG,"返回的推荐信息为:"+return_id);
                                                            String return_path=Environment.getExternalStorageDirectory().getPath()+"/s"+user_id+"_"+return_id+".jpg";
                                                            BitmapFactory.Options option = new BitmapFactory.Options();
                                                            option.inSampleSize = 4;
                                                            Bitmap bm_top = BitmapFactory.decodeFile(return_path, option);
                                                            clothes_image.setImageBitmap(bm_top);
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                        break;
                                                    }
                                                }
                                            }
                                        }).show();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    //主要将cloth.txt放进数据库中，把每件衣服的照片放进SD卡中，将cloth信息插入到服务器端的数据库中
    public void insertData()
    {
        //测试注册
        String name="lgy";
        String password="1234567";
        //注册成功会在函数里获得一个用户id
        Log.d(TAG,"开始注册");
        try {
            user_id = socketclient.sendUserInfo_register(name, password);
        } catch (IOException e) {
            Log.d(TAG,"注册IOException");
            e.printStackTrace();
        }
        Log.d(TAG,"注册的ID为:"+user_id);
        if(user_id==-1)
        {
            Log.d(TAG,"注册失败");
            return;
        }
        UserInfo userinfo=new UserInfo(user_id,name,password,null);

        //测试衣服文本信息
        Clothes cloth1=new Clothes(1,1,0,12,0);
        Clothes cloth2=new Clothes(2,1,0,7,0);
        Clothes cloth3=new Clothes(3,1,0,4,0);
        Clothes cloth4=new Clothes(4,0,2,13,0);
        Clothes cloth5=new Clothes(5,0,2,13,0);
        Clothes cloth6=new Clothes(6,0,0,7,0);
        Clothes cloth7=new Clothes(7,2,1,6,0);
        Clothes cloth8=new Clothes(8,2,1,4,0);
        Clothes cloth9=new Clothes(9,2,1,11,0);
        Clothes cloth10=new Clothes(10,3,0,7,0);
        Clothes cloth11=new Clothes(11,3,0,0,0);
        Clothes cloth12=new Clothes(12,3,0,4,0);
        //用于插入历史信息  //这里可能还要多构造历史信息
        //构造历史信息必须按外套，上衣，裤装，鞋子的顺序放入clothesList
        List<Integer> clothsIdList=new ArrayList<>();
        clothsIdList.add(cloth4.getClothesId());
        clothsIdList.add(cloth1.getClothesId());
        clothsIdList.add(cloth7.getClothesId());
        clothsIdList.add(cloth10.getClothesId());
        List<Integer> val1 = Arrays.asList(2,3,4,5,6,7,8);
        com.example.a13745.sweetward.socket.info.Weather weather1 = new com.example.a13745.sweetward.socket.info.Weather(val1);
        //不期待得到推荐信息
        try {
            socketclient.sendClothInfo_text(userinfo.getId(), cloth1);
            socketclient.sendClothInfo_text(userinfo.getId(), cloth2);
            socketclient.sendClothInfo_text(userinfo.getId(), cloth3);
            socketclient.sendClothInfo_text(userinfo.getId(), cloth4);
            socketclient.sendClothInfo_text(userinfo.getId(), cloth5);
            socketclient.sendClothInfo_text(userinfo.getId(), cloth6);
            socketclient.sendClothInfo_text(userinfo.getId(), cloth7);
            socketclient.sendClothInfo_text(userinfo.getId(), cloth8);
            socketclient.sendClothInfo_text(userinfo.getId(), cloth9);
            socketclient.sendClothInfo_text(userinfo.getId(), cloth10);
            socketclient.sendClothInfo_text(userinfo.getId(), cloth11);
            socketclient.sendClothInfo_text(userinfo.getId(), cloth12);
            //socketclient.sendHistoryInfo(userinfo.getId(),weather1,clothsIdList);
            clothsIdList=new ArrayList<>();
            clothsIdList.add(cloth5.getClothesId());
            clothsIdList.add(cloth2.getClothesId());
            clothsIdList.add(cloth8.getClothesId());
            clothsIdList.add(cloth11.getClothesId());
            val1 = Arrays.asList(3,20,30,2,10,7,8);
            weather1 = new com.example.a13745.sweetward.socket.info.Weather(val1);
            //socketclient.sendHistoryInfo(userinfo.getId(),weather1,clothsIdList);
            clothsIdList=new ArrayList<>();
            clothsIdList.add(cloth6.getClothesId());
            clothsIdList.add(cloth3.getClothesId());
            clothsIdList.add(cloth9.getClothesId());
            clothsIdList.add(cloth12.getClothesId());
            val1 = Arrays.asList(3,15,20,5,6,3,5);
            weather1 = new com.example.a13745.sweetward.socket.info.Weather(val1);
            //socketclient.sendHistoryInfo(userinfo.getId(),weather1,clothsIdList);
        } catch (IOException e) {
            e.printStackTrace();
        }

        cloth1=new Clothes(1,4,1,12,0);
        cloth2=new Clothes(2,3,0,7,2);
        cloth3=new Clothes(3,1,2,4,4);
        cloth4=new Clothes(4,7,7,13,0);
        cloth5=new Clothes(5,1,9,13,0);
        cloth6=new Clothes(6,3,2,7,2);
        cloth7=new Clothes(7,2,1,6,4);
        cloth8=new Clothes(8,2,1,4,2);
        cloth9=new Clothes(9,2,2,3,0);
        cloth10=new Clothes(10,3,3,7,5);
        cloth11=new Clothes(11,2,5,2,1);
        cloth12=new Clothes(12,6,2,3,5);

        try {
            clothsIdList=new ArrayList<>();
            clothsIdList.add(6);
            clothsIdList.add(2);
            clothsIdList.add(10);
            clothsIdList.add(12);
            val1 = Arrays.asList(2,27,17,57,33,4,3 );
            weather1 = new com.example.a13745.sweetward.socket.info.Weather(val1);
            socketclient.sendHistoryInfo(userinfo.getId(),weather1,clothsIdList);
            clothsIdList=new ArrayList<>();
            clothsIdList.add(1);
            clothsIdList.add(7);
            clothsIdList.add(9);
            clothsIdList.add(12);
            val1 = Arrays.asList(3,28,        17 ,       85,      64,        4,         3);
            weather1 = new com.example.a13745.sweetward.socket.info.Weather(val1);
            socketclient.sendHistoryInfo(userinfo.getId(),weather1,clothsIdList);
            clothsIdList=new ArrayList<>();
            clothsIdList.add(6);
            clothsIdList.add(1);
            clothsIdList.add(10);
            clothsIdList.add(11);
            val1 = Arrays.asList(3   ,       27,        12,        71,      50,        4,3);
            weather1 = new com.example.a13745.sweetward.socket.info.Weather(val1);
            socketclient.sendHistoryInfo(userinfo.getId(),weather1,clothsIdList);

            //
            clothsIdList=new ArrayList<>();
            clothsIdList.add(4);
            clothsIdList.add(1);
            clothsIdList.add(12);
            clothsIdList.add(11);
            val1 = Arrays.asList(2  ,        21    ,    9    ,     42    ,  25    ,    5   ,      4);
            weather1 = new com.example.a13745.sweetward.socket.info.Weather(val1);
            socketclient.sendHistoryInfo(userinfo.getId(),weather1,clothsIdList);

            //
            clothsIdList=new ArrayList<>();
            clothsIdList.add(3);
            clothsIdList.add(2);
            clothsIdList.add(9);
            clothsIdList.add(10);
            val1 = Arrays.asList(4    ,      11  ,      8   ,      91  ,    72   ,     5   ,      4);
            weather1 = new com.example.a13745.sweetward.socket.info.Weather(val1);
            socketclient.sendHistoryInfo(userinfo.getId(),weather1,clothsIdList);

            //
            clothsIdList=new ArrayList<>();
            clothsIdList.add(5);
            clothsIdList.add(1);
            clothsIdList.add(10);
            clothsIdList.add(12);
            val1 = Arrays.asList(2 ,         16   ,     5  ,       74   ,   43    ,    6  ,       5 );
            weather1 = new com.example.a13745.sweetward.socket.info.Weather(val1);
            socketclient.sendHistoryInfo(userinfo.getId(),weather1,clothsIdList);

            //
            clothsIdList=new ArrayList<>();
            clothsIdList.add(5);
            clothsIdList.add(2);
            clothsIdList.add(13);
            clothsIdList.add(10);
            val1 = Arrays.asList(1     ,     13  ,      6   ,      48  ,    21   ,     5  ,       4);
            weather1 = new com.example.a13745.sweetward.socket.info.Weather(val1);
            socketclient.sendHistoryInfo(userinfo.getId(),weather1,clothsIdList);
            //
            clothsIdList=new ArrayList<>();
            clothsIdList.add(4);
            clothsIdList.add(1);
            clothsIdList.add(10);
            clothsIdList.add(12);
            val1 = Arrays.asList(1    ,      22    ,    10   ,     45  ,    27   ,     5     ,    4);
            weather1 = new com.example.a13745.sweetward.socket.info.Weather(val1);
            socketclient.sendHistoryInfo(userinfo.getId(),weather1,clothsIdList);
            //
            clothsIdList=new ArrayList<>();
            clothsIdList.add(6);
            clothsIdList.add(2);
            clothsIdList.add(10);
            clothsIdList.add(12);
            val1 = Arrays.asList(1     ,     26    ,    14  ,      37   ,   18  ,      5  ,       4 );
            weather1 = new com.example.a13745.sweetward.socket.info.Weather(val1);
            socketclient.sendHistoryInfo(userinfo.getId(),weather1,clothsIdList);
            //
            clothsIdList=new ArrayList<>();
            clothsIdList.add(1);
            clothsIdList.add(7);
            clothsIdList.add(10);
            clothsIdList.add(11);
            val1 = Arrays.asList(2    ,      29    ,    17   ,     35  ,    18   ,     5  ,       4);
            weather1 = new com.example.a13745.sweetward.socket.info.Weather(val1);
            socketclient.sendHistoryInfo(userinfo.getId(),weather1,clothsIdList);
            //
            clothsIdList=new ArrayList<>();
            clothsIdList.add(3);
            clothsIdList.add(1);
            clothsIdList.add(12);
            clothsIdList.add(11);
            val1 = Arrays.asList(4    ,      14   ,     4   ,      97 ,     73   ,     5   ,      4);
            weather1 = new com.example.a13745.sweetward.socket.info.Weather(val1);
            socketclient.sendHistoryInfo(userinfo.getId(),weather1,clothsIdList);
            //
            clothsIdList=new ArrayList<>();
            clothsIdList.add(3);
            clothsIdList.add(2);
            clothsIdList.add(12);
            clothsIdList.add(9);
            val1 = Arrays.asList(4      ,    12     ,   7    ,     89  ,    66 ,       5    ,     4 );
            weather1 = new com.example.a13745.sweetward.socket.info.Weather(val1);
            socketclient.sendHistoryInfo(userinfo.getId(),weather1,clothsIdList);
            //
            clothsIdList=new ArrayList<>();
            clothsIdList.add(4);
            clothsIdList.add(2);
            clothsIdList.add(9);
            clothsIdList.add(1);
            val1 = Arrays.asList(4    ,      22    ,    14     ,   79,      67    ,    4 ,3);
            weather1 = new com.example.a13745.sweetward.socket.info.Weather(val1);
            socketclient.sendHistoryInfo(userinfo.getId(),weather1,clothsIdList);

            //
            clothsIdList=new ArrayList<>();
            clothsIdList.add(5);
            clothsIdList.add(1);
            clothsIdList.add(9);
            clothsIdList.add(12);
            val1 = Arrays.asList(1    ,      10  ,      1     ,    46  ,    25  ,      4  ,       3 );
            weather1 = new com.example.a13745.sweetward.socket.info.Weather(val1);
            socketclient.sendHistoryInfo(userinfo.getId(),weather1,clothsIdList);
            //
            clothsIdList=new ArrayList<>();
            clothsIdList.add(5);
            clothsIdList.add(2);
            clothsIdList.add(3);
            clothsIdList.add(12);
            val1 = Arrays.asList(2,          11 ,        -11,       55,      38,        5  ,       4);
            weather1 = new com.example.a13745.sweetward.socket.info.Weather(val1);
            socketclient.sendHistoryInfo(userinfo.getId(),weather1,clothsIdList);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG,"原始单件衣服信息导入中");
        String res_cloth = "";
        try{
            InputStream in = getResources().openRawResource(R.raw.cloth);
            int length = in.available();
            byte [] buffer = new byte[length];
            in.read(buffer);
            res_cloth = new String(buffer,"UTF-8");
            Log.d(TAG,"初始化的单件所有衣服："+res_cloth);
            in.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        verifyStoragePermissions(this);
        String res_cloth_temp=new String(res_cloth);
        saveClothPicToSD(res_cloth_temp); //使用本函数将drawablez中图片转至SD卡；
        //--------------------------------------------------------------------------------->暂时不插入
        //再对res_cloth的衣服插入进db中
        res_cloth.replace("\r","");
        res_cloth.replace("\t","");
        res_cloth.replace("\n","");
        String[] res_suits =res_cloth.split(";");
        for(int i = 0; i < res_suits.length;i++){     //修改
            if (res_suits[i]=="") ;
            else {
                String[] su = res_suits[i].split(",");
                ClothesInfo clothesInfo=new ClothesInfo();
                clothesInfo.setId(Integer.parseInt(su[0]));
                clothesInfo.setClothType(Integer.parseInt(su[1]));
                clothesInfo.setClothKind(Integer.parseInt(su[2]));
                clothesInfo.setClothColor(Integer.parseInt(su[3]));
                clothesInfo.setClothDirtyIndex(Integer.parseInt(su[4]));
                clothesInfo.setClothFile(su[5]);
                Log.d(TAG,"数据库插入:"+su[5]);
                db.insert("cloth_info",clothesInfo);
            }
        }
    }

    //把图片信息插入进去,供insertData函数使用
    public void saveClothPicToSD(String res){
        res.replace("\r","");
        res.replace("\t","");
        res.replace("\n","");
        String[] res_suits =res.split(";");

        String new_res = "";
        for(int i = 0; i < res_suits.length;i++){                     //注意这里length比实际所想的个数大1！！！！！！！！当心！
            String[] su = res_suits[i].split(",");
            Log.d(TAG, "saveClothPicToSD: "+res_suits[i]+"\n");
            Log.d(TAG, "saveClothPicToSD: su[5] :"+su[5]);
            String cloth_name =new String(su[5]);              //problem is here!

            //加载图片
            File imageFile = null;
            int resID=getResources().getIdentifier(cloth_name, "drawable", getPackageName());       //修改
            Log.d(TAG, "saveClothPicToSD: resID----------------------->"+resID);
            Bitmap bm= BitmapFactory.decodeResource(getResources(), resID);

            //将图片存储好进SD中
            File file=new File(Environment.getExternalStorageDirectory().getPath()+"/"+cloth_name+".jpg");
            Log.d(TAG,"directory:"+Environment.getExternalStorageDirectory());
            try {
                FileOutputStream out = new FileOutputStream(file);
                if (bm.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                    Log.d(TAG, "saveClothPicToSD: 写入图片信息成功");
                    out.flush();
                    out.close();
                }
            } catch (Exception e) {
                Log.d(TAG,"写入图片失败！");
                e.printStackTrace();
            }
        }
        return ;
    }
}
