package socket;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Send {

	public Send() {
		// TODO Auto-generated constructor stub
	}
	
	//在实际项目中，本地根据得到的一条新的衣物信息，就会开始传送
	//故这里设置一个函数来让本地能够调用起来
	public static void sendMessage(String message,OutputStream outputStream) throws IOException 
	{
		//首先计算传送消息的长度
		byte[] sendBytes=message.getBytes("UTF-8");
		//先将消息的长度优先发送
		outputStream.write(sendBytes.length>>8);			//注意此处默认消息的长度是定长两字节绝对可以表示的，如果不放心可以换成4字节
																						//如果想节省空间，可以使用变长字节表示消息的长度
		outputStream.write(sendBytes.length);				//注意此方法只传递低8位
		//然后再发送消息
		outputStream.write(sendBytes);
		//flush刷新此输出流并强制写出所有缓冲的输出字节
		//flush的常规协定：如果此输出流的实现已经缓冲了以前写入的任何数据
		//则调用此方法应将这些字节立即写入它们预期的目标
		//总之，flush强制将缓冲区所有的数据输出
		outputStream.flush();
	}
	public static void sendPicture(String addr,OutputStream outputStream) throws IOException
	{
		FileInputStream fileinputStream=new FileInputStream(addr);
		byte[] buf=new byte[1024];			
		int len=0;
		//往输出流里面投放数据
		//循环投放，投完为止
		while((len=fileinputStream.read(buf))!=-1)
		{
			outputStream.write(buf,0,len);
		}
		outputStream.flush();
	}
}
