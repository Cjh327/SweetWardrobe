package com.example.a13745.sweetward.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.a13745.sweetward.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

//收藏夹
public class StoreActivity extends AppCompatActivity {


    private static final String TAG = "StoreActivity";

    //搭配信息
    public List<String> list_store=new ArrayList<String>();        //从数据库中获得
    //当前搭配的位置
    int now_num=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list_store.add("s0_1");
        list_store.add("s0_7");
        list_store.add("s0_2");
        list_store.add("s0_8");
        list_store.add("s0_3");
        list_store.add("s0_9");
        setContentView(R.layout.activity_store);
        addClick();
    }

    public void addClick()
    {
        //上衣
        final ImageView topCloth=(ImageView)findViewById(R.id.Store_up_cloth);
        //下装
        final ImageView underCloth=(ImageView)findViewById(R.id.Store_down_cloth);

        //left next button 上一套
        ImageButton leftNextButton=(ImageButton)findViewById(R.id.Store_left_next_icon);
        leftNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myClick(v,topCloth,underCloth);
            }
        });

        //right next button 下一套
        ImageButton rightNextButton=(ImageButton)findViewById(R.id.Store_right_next_icon);
        rightNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myClick(v,topCloth,underCloth);
            }
        });
    }

    public void myClick(View v,ImageView cloth_up,ImageView cloth_down) {
        switch (v.getId())
        {
            case R.id.Store_left_next_icon:
                changeSuit(-1,cloth_up,cloth_down);
                break;
            case R.id.Store_right_next_icon:
                changeSuit(1,cloth_up,cloth_down);
                break;
            default:
                break;
        }
    }

    public void changeSuit(int flag,ImageView cloth_up,ImageView cloth_down)    //flag为1，表示往后看，flag为0，表示往前看
    {
        if(list_store.size()==0)
        {
            return;
        }
        String up="";
        String down="";
        now_num+=flag;
        now_num+=flag;  //存储中按 上衣、下装依次存放
        now_num=(now_num+list_store.size())%list_store.size();  //取模 循环

        up=list_store.get(now_num);
        String path=Environment.getExternalStorageDirectory().getPath()+"/";   //图片的根目录，这里可能要调整
        //---------------------------------------------------------->不知道上述该路径是如何确定的

        String name=path+up+".jpg";
        //Bitmap代表一张位图，BitmapDrawable里封装的图片就是一个Bitmap对象．把Bitmap对象包装成BitmapDrawable对象，
        // 可以调用Bitmapdrawable的构造器
        //BitmapDrawable drawable = new BitmapDrawable(bitmap);初始化BitmapDrawable对象

        //如果要获取BitmapDrawable所包装的bitmap对象,可以调用getBitmap()方法
        //Bitmap bitmap = drawable.getBitmap();

        //如果需要访问其它存储路径的图片,需要借助于BitmapFactory来解析,创建Bitmap对象

        // BitmapFactory该类所有方法都是用来解码创建一个 bitmap 对象
        //该类的子类 options类 Options，该类用于解码Bitmap时的参数控制
        //inSample表示缩小bitmap的宽和高、降低分辨率，宽=宽/inSample，...像素下降为原来的(inSample*inSample)分之一

        //--------------------------------------------------------------->

        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inSampleSize = 4;
        Log.d(TAG, "changeSuit: the new up name is"+name);
        Bitmap bm = BitmapFactory.decodeFile(name,option);

        bm=zoomBitmap(bm,cloth_up.getWidth(),cloth_up.getHeight()); //新增内容，调整图片大小

        cloth_up.setImageBitmap(bm);//显示获取的图片

        down=(String)list_store.get(now_num+1);
        path=Environment.getExternalStorageDirectory().getPath()+"/";
        name=path+down+".jpg";
        option=new BitmapFactory.Options();
        Log.d(TAG, "changeSuit: the new down name is"+name);
        bm=BitmapFactory.decodeFile(name,option);

        bm=zoomBitmap(bm,cloth_down.getWidth(),cloth_down.getHeight());     //新增内容，调整图片大小

        cloth_down.setImageBitmap(bm);  //显示

    }

    //用于调整图片大小，以使得图片刚好放入ImageView所给的大小中
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        return newBmp;
    }

}
