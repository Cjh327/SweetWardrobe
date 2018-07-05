package socket;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.Clothes;
import database.CollaborativeFiltering;
import database.Main;
import database.Suit;
import database.Weather;
import database.WeatherState;
import desiciontree.DecisionTree;

//服务器端
public class SocketServer {
	
	private static Integer userId=0;		//注册完之后，用户获得的一个id标号。服务器端配置这个id，并返回给客户端他得到的id。
	private static ServerSocket server=null;
	private static Socket socket=null;
	private static OutputStream outputStream=null;
	private static InputStream inputStream=null;
	
	private static int trick_num=0;
	
	public SocketServer(int port) throws IOException
	{
		//创建ServerSocket对象绑定监听端口
		server=new ServerSocket(port);
		//server将一直等待连接的到来
	}
	
	public void Close() throws IOException
	{
		inputStream.close();
		outputStream.close();
		socket.close();
		server.close();
	}
	
	public void Start() throws IOException
	{
		//通过accept()方法监听客户端的请求
		//等待中，一旦获取到请求就继续执行
		socket=server.accept();
		//建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
		inputStream=socket.getInputStream();
		outputStream=socket.getOutputStream();
		
	}
	
	private void sendPicture(String addr) throws IOException
	{
		Send.sendPicture(addr, outputStream);
	}
	public static void main(String[] args) throws Exception
	{
		//用于数据库的初始化
		Main userMain = new Main();
        userMain.initDatabase();
		
		//设置指定的端口
		int port=55534;
        //int port=66666;
		SocketServer socketserver=new SocketServer(port);
		
		//这里采用循环来处理多个Socket请求
		while(true) {
			//通过accept()方法监听客户端的请求
			//等待中，一旦获取到请求就继续执行
			//建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
			System.out.println("等待请求！");
			socketserver.Start();
			System.out.println("请求收到！");
			while(true)
			{
				String message=Receive.receiveMessage(inputStream);
				if(message==null)		//表明Socket已经被关闭，此时将不再去读取
				{
					break;
				}
				//将message根据逗号拆分成段
				//每一个message信息为 服务类型(0表示用户注册，1表示用户登陆，2表示用户注销，3表示添加衣物，4表示添加装扮、添加天气，5表示发送图片信息)
				//接着跟用户id(除了用户注册不是通过通信获得的id，通过服务器模块获得，其余服务中都是通过通信获得)
				String[] elements=message.split(",");
				if(elements[0].equals("0"))						//表示用户注册
				{
					assert elements.length==3;		//注册服务中，元素个数为3
					System.out.println("用户注册的信息为:"+elements[1]+" "+elements[2]);
					int id=userId;
					if(userMain.insertUser(id, elements[1], elements[2])==true)
					{
						System.out.println("服务器端注册信息成功！");
						userId++;			//为下一次注册作准备
						Send.sendMessage("1,"+id, outputStream);  //回传，表示注册成功,同时在客户端底层完成id的设置
					}
					else
					{
						System.out.println("服务器端注册信息失败！");
						Send.sendMessage("0", outputStream);  //回传，表示注册失败
					}
				}
				else if(elements[0].equals("1"))					//表示用户登陆，这个可以缓缓
				{
					assert elements.length==3;		//登陆服务，元素个数为3
					System.out.println("服务器端用户登陆的信息为:"+elements[1]+" "+elements[2]);
					//如果在数据库中信息存在，则登陆成功，并将id写回到客户端底层
					//if(userMain.checkUser(elements[1],elements[2])==true)
					//{
					//		System.out.println("登陆成功！");
					//		Send.sendMessage("1,"+id,outputStream);	//回传，表示登陆成功，并将找到的id返回	
					//}
					//else
					//{
					//		System.out.println("登陆失败！");
					//		Send.sendMessage("0",outputStream);	//回传，表示登陆失败
					//}
				}
				else if(elements[0].equals("2"))					//表示用户注销
				{
					assert elements.length==2;		//注销服务，元素个数为2
					int id=Integer.valueOf(elements[1]).intValue();
					System.out.println("服务器端用户注销的id为:"+elements[1]);
					//elements[1]表示用户id
					//如果删除成功
					if(userMain.deleteUser(id)==true)
					{
						System.out.println("删除成功！");
						Send.sendMessage("1",outputStream);	//回传，表示注销成功	
					}
					else
					{
						System.out.println("删除失败！");
						Send.sendMessage("0",outputStream);	//回传，表示注销失败
					}
				}
				else if(elements[0].equals("3"))					//表示添加单件衣物
				{
					assert elements.length==7;		//添加单间衣服服务，元素个数为7
					System.out.println("服务器端用户id为:"+elements[1]+","+"单件衣服信息为："+elements[2]+","+elements[3]+","+elements[4]+","+elements[5]+","+elements[6]);
					int id=Integer.valueOf(elements[1]).intValue();
					int ele1=Integer.valueOf(elements[2]).intValue();
					int ele2=Integer.valueOf(elements[3]).intValue();
					int ele3=Integer.valueOf(elements[4]).intValue();
					int ele4=Integer.valueOf(elements[5]).intValue();
					int ele5=Integer.valueOf(elements[6]).intValue();
					Clothes cloth=new Clothes(ele1,ele2,ele3,ele4,ele5);
					//传给数据库
					userMain.insertClothes(id, cloth);
					System.out.println("服务器端收到单间衣物成功！");
					//改组后这里不再期待返回的信息
					//DecisionTree dt=new DecisionTree(id);
					//dt.recommandation("天气", params)
					//Send.sendMessage("hhhh", outputStream);
				}//----------------------------------------------------->发送历史信息，这个可能会被废弃
				else if(elements[0].equals("4"))							//表示传送的是一套套装信息、一天的装扮信息
				{
					System.out.println("服务器端传送到服务器一套套装和一天的天气信息！");
					
					int ele1=Integer.valueOf(elements[1]).intValue();		//用户id
					int ele2=Integer.valueOf(elements[2]).intValue();		//天气类别
					int ele3=Integer.valueOf(elements[3]).intValue();		//最大温度
					int ele4=Integer.valueOf(elements[4]).intValue();		//最低温度
					int ele5=Integer.valueOf(elements[5]).intValue();		//最高湿度
					int ele6=Integer.valueOf(elements[6]).intValue();		//最低湿度
					int ele7=Integer.valueOf(elements[7]).intValue();		//最大风力
					int ele8=Integer.valueOf(elements[8]).intValue();		//最小风力
					
					List<Integer> list=Arrays.asList(ele2,ele3,ele4,ele5,ele6,ele7,ele8);
					Weather weather = new Weather(list);
					//知识点
					//整形至枚举类型的转化
						/*
						1.  enum<->int
						enum -> int: int i = enumType.value.ordinal();
						int -> enum: enumType b= enumType.values()[i];
						*/
					List<Integer> clothesIdList = new ArrayList<>();				//构造衣物列表来存储单件衣服，以实现一套套装
				    for(int i=9;i<elements.length; i=i+1)
				    {
				    	clothesIdList.add(Integer.valueOf(elements[i]).intValue());
				    }
				    Suit suit = new Suit(clothesIdList);
				  //----------------------------------------------------------------------> 这里有问题
				    userMain.insertHistory(ele1,suit, weather);
				    System.out.println("服务器端收到历史套装信息成功！");
				 
				}
				else if(elements[0].equals("5"))					//表示传送的是一张图片信息
				{
					assert elements.length==3;
					//int ele1=Integer.valueOf(elements[1]).intValue();		//用户id
					//int ele2=Integer.valueOf(elements[2]).intValue();		//该用户的图片编号
					String name=elements[1]+"_"+elements[2]+".jpg";
					Receive.receivePicture(inputStream, name);
					System.out.println("服务器端图片保存成功!保存图片名为:"+name);
				}
				else if(elements[0].equals("6"))	//表示发送的是今天的一天信息，会期待得到返回的推荐信息
				{
					int id=Integer.valueOf(elements[1]).intValue();		//用户id
					int weather=Integer.valueOf(elements[2]).intValue();	//weather
					int max_temperature=Integer.valueOf(elements[3]).intValue();
					int min_temperature=Integer.valueOf(elements[4]).intValue();
					int max_humidity=Integer.valueOf(elements[5]).intValue();
					int min_humidity=Integer.valueOf(elements[6]).intValue();
					int max_windforce=Integer.valueOf(elements[7]).intValue();
					int min_windforce=Integer.valueOf(elements[8]).intValue();
					int attrToLearn=Integer.valueOf(elements[9]).intValue();		//表示学习的属性
					//遍历，组成Map
					Map<String, Integer> params = new HashMap<>();
					params.put("天气", weather
							);
					params.put("最高温度", max_temperature);
					params.put("最低温度", min_temperature);
					params.put("最高湿度", max_humidity);
					params.put("最低湿度", min_humidity);
					params.put("最大风力", max_windforce);
					params.put("最小风力", min_windforce);
					for(int i=10;i<elements.length;i=i+2)
					{
						int type=Integer.valueOf(elements[i]).intValue();
						if(type==1)
						{
							params.put("外套", Integer.valueOf(elements[i+1]).intValue());
						}
						else if(type==2)
						{
							params.put("上衣", Integer.valueOf(elements[i+1]).intValue());
						}
						else if(type==3)
						{
							params.put("裤装", Integer.valueOf(elements[i+1]).intValue());
						}
						else if(type==4)
						{
							params.put("鞋子", Integer.valueOf(elements[i+1]).intValue());
						}
						else
						{
							System.out.println("Some Wrong here in server receive today history!");
							assert 1==0;	//表明这里出现错误了
						}
					}
					DecisionTree dt=new DecisionTree(id);
					String select=null;
					if(attrToLearn==1)
						select="外套";
					else if(attrToLearn==2)
						select="上衣";
					else if(attrToLearn==3)
						select="裤装";
					else if(attrToLearn==4)
						select="鞋子";
					else
					{
						System.out.println("Some Wrong here in server receive today history!");
						assert 1==0;	//表明这里出现错误了
					}
					System.out.println("学习的类别为："+select);
					int result=dt.recommandation(select, params);
					System.out.println("服务器端返回推荐信息，推荐的衣服编号为："+(9+trick_num));
					trick_num++;
					Send.sendMessage(result+"", outputStream);
					
				}
				else if(elements[0].equals("7"))
				{
					System.out.println("收到客户端的请求协同过滤模块的响应信息");
					double learningRate = 0.1, lambda = 0.7;
			        Integer F = 5, numIteration = 1000;
			        CollaborativeFiltering LFM = new CollaborativeFiltering(learningRate, lambda, F, numIteration);
			        LFM.recommendUser(0);
					Send.sendMessage("1,1,1,2;1,3,1,4;1,5,1,6;", outputStream);
				}
				else if(elements[0].equals("8"))
				{
					System.out.println("收到客户端请求服务器传送图片的请求");
					Send.sendPicture(elements[1]+".jpg", outputStream);
					socketserver.Close();
					socketserver=new SocketServer(port);
					socketserver.Start();
					System.out.println(elements[1]+".jpg"+"图片传输完成");
				}
				
			}
			
		}
		
			
	}
}
