package com.example.a13745.sweetward.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by 13745 on 2018/4/9.
 */

//该类用于处理各个模块的信息转化问题
public class Info {
    private static final String TAG = "Info";

    public Info(){}  //只是构造函数，没有实际作用

    //---------------------------------->一下两个函数在收藏夹中需要用到
    //写文本文件数据到SD卡中
    public void writeFileToSdcardFile(String fileName,String write_str) throws IOException {
        String Text = Environment.getExternalStorageDirectory()
                .getPath()+ "//PhotoWallFalls//";
        File myImageDir = new File(Text);
        if (!myImageDir.exists())
        {
            myImageDir.mkdirs();
        }
        Log.d(TAG, "writeFileToSdcardFile: name->"+myImageDir.getName()+" path->"+myImageDir.getPath());
        try{
            File myfile=new File(Text+fileName);        //修改
            if (myfile.exists())
            {
                Log.d(TAG, "writeFileToSdcardFile: file is occured!");
            }
            else
            {
                Log.d(TAG, "writeFileToSdcardFile: file name:"+myfile.getName()+" path:"+myfile.getPath());
                myfile.createNewFile();
            }
            FileOutputStream fout = new FileOutputStream(Text+fileName);    //修改

            byte [] bytes = write_str.getBytes();

            fout.write(bytes);

            Log.d(TAG,"write new Tag to "+fileName);
            fout.close();
        }

        catch(Exception e){
            e.printStackTrace();
        }
    }


    //从SD中读取文本数据
    public String readFileFromSdcardFile(String fileName) throws IOException{
        String res="";

        String Text = Environment.getExternalStorageDirectory()
                .getPath() + "//PhotoWallFalls//";


        try{
            FileInputStream fin = new FileInputStream(Text+fileName);

            int length = fin.available();

            byte [] buffer = new byte[length];
            fin.read(buffer);

            res =  new String(buffer,"UTF-8");
            Log.d(TAG,"read "+fileName+" \n");
            fin.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        // Log.d(TAG, "readFileFromSdcardFile: res-> "+res+" \n"); 正确了
        return res;
    }




}
