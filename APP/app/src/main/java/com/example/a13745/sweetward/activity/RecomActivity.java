package com.example.a13745.sweetward.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.a13745.sweetward.R;
import com.example.a13745.sweetward.socket.SocketClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//推荐界面
//显示装扮信息
//添加收藏按钮
public class RecomActivity extends AppCompatActivity {

    private static final String TAG = "RecomActivity";

    //对应socket信息
    private String host="10.0.2.2";        //对应客户机ip
    private int post=55534;                 //对应端口
    public SocketClient socketclient=new SocketClient(host,post);   //对应客户端
    //推荐信息
    public List<String> list_recom=new ArrayList<String>();        //从数据库中获得
    //当前推荐信息的位置
    int now_num=-2;

    private int click_Recom_nums=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"进入了推荐物内！！");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recom);
        addClick();
    }

    public void addClick()
    {
        //上衣
        final ImageView topCloth=(ImageView)findViewById(R.id.Recom_up_cloth);
        //下装
        final ImageView underCloth=(ImageView)findViewById(R.id.Recom_down_cloth);

        //下一套推荐装扮
        ImageButton NextButton=(ImageButton)findViewById(R.id.Recom_next_icon);

        //收藏按钮
        ImageButton StoreButton=(ImageButton)findViewById(R.id.Recom_store_icon);

        NextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果是第一次按
                if(click_Recom_nums==0)
                {
                    //需要向socket发送信息，然后填补list_Recom
                    try {
                        String result=socketclient.sendRecomRequest(0);     //返回的推荐信息
                        //将result拆分成若干个小份 解析成若干个如 s1_0, s2_9这样的
                        //假想格式为1,2,1,3;2,3,2,1;
                        String[] res_suits=result.split(";");
                        for(int i = 0; i < res_suits.length;i++) {         //每一个res_suits[i]为一套装扮
                            String[] su = res_suits[i].split(",");
                            for(int j=0;j<su.length;j=j+2) {
                                list_recom.add("s"+su[j]+"_"+su[j+1]);      //这是一件衣服
                                Log.d(TAG,"s"+su[j]+"_"+su[j+1]+"开始接收");
                                socketclient.sendPictureRequest("s"+su[j]+"_"+su[j+1]); //服务器端该衣服存入客户端中

                                Log.d(TAG,"s"+su[j]+"_"+su[j+1]+"接收完成");
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                {

                }
                click_Recom_nums++;
                changeSuit(topCloth,underCloth);
            }
        });

        StoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //完成收藏过程
                //String up=list_recom.get(now_num);
                //String down=list_recom.get(now_num+1);      //获取当前推荐套装的名称信息，没有jpg然后存入数据库中

                //如果添加成功
            }
        });
    }

    public void changeSuit(ImageView cloth_up,ImageView cloth_down)    //flag为1，表示往后看，flag为0，表示往前看
    {
        if(list_recom.size()==0)
        {
            return;
        }
        String up="";
        String down="";
        now_num+=1;
        now_num+=1;  //存储中按 上衣、下装依次存放
        now_num=(now_num+list_recom.size())%list_recom.size();  //取模 循环

        up=list_recom.get(now_num);
        String path= Environment.getExternalStorageDirectory().getPath();   //图片的根目录，这里可能要调整
        //---------------------------------------------------------->不知道上述该路径是如何确定的

        String name=path+"/"+up+".jpg";
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

        down=(String)list_recom.get(now_num+1);
        path=Environment.getExternalStorageDirectory().getPath();
        name=path+"/"+down+".jpg";
        option=new BitmapFactory.Options();
        Log.d(TAG, "changeSuit: the new down name is"+name);
        bm=BitmapFactory.decodeFile(name,option);

        bm=zoomBitmap(bm,cloth_down.getWidth(),cloth_down.getHeight());     //新增内容，调整图片大小

        cloth_down.setImageBitmap(bm);  //显示

    }

    //用于调整图片大小，以使得图片刚好放入ImageView所给的大小中，这个也许可以放入一个另外的包中(工具集合)，因为可能很多需要显示图片的界面都要使用这个
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
