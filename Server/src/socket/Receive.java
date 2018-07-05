package socket;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Receive {

	public Receive() {
		// TODO Auto-generated constructor stub
	}
	
	public static String receiveMessage(InputStream inputStream) throws IOException
	{
		byte[] bytes;	//因为可以复用，Socket且能判断长度，所以可以一个用到底
		//首先读取两个字节表示的长度
		int first=inputStream.read();	//读取一个字节
		//如果读取到的值为-1 说明到了流的末尾
		//表明Socket已经被关闭，此时将不再去读取
		if(first==-1)
		{
			return null;
		}
		int second=inputStream.read();
		int length=(first<<8)+second;		//两个字节表示长度
		//然后构造出一个指定长度的消息即可
		bytes=new byte[length];
		//再读取指定长度的消息
		inputStream.read(bytes);
		return new String(bytes,"UTF-8");			//可能有问题
	}
	//功能，在服务器端创建为name的图片，name中已有jpg后缀
	public static void receivePicture(InputStream inputStream,String name) throws IOException
	{
		FileOutputStream fileoutputStream=new FileOutputStream(name);
		byte[] buf=new byte[1024];
		int len=0;
		//往字节流里写图片数据
		while((len=inputStream.read(buf))!=-1)
		{
			fileoutputStream.write(buf,0,len);
		}
		fileoutputStream.close();
	}
}
