package database;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private MyDatabase myDatabase;

    //用于初始化数据库
    public Main() {
        myDatabase = new MyDatabase();
    }

    //初始化表
    //这里的boolean代表什么
    public boolean initDatabase() {
        return myDatabase.initTable();
    }

    //插入用户信息
    public boolean insertUser(Integer userId, String userName, String userPassword) {
        return myDatabase.insertUser(userId, userName, userPassword);
    }

    //插入衣服信息
    public boolean insertClothes(Integer userId, Clothes clothes) {
        return myDatabase.insertClothes(userId, clothes);
    }

    //删除用户信息
    public boolean deleteUser(Integer userId) {
        return myDatabase.deleteUser(userId);
    }

    //插入历史信息，主要针对的是用户一天的装扮和这一天的天气，一天只调用这个函数一次
    public boolean insertHistory(Integer userId, Suit suit, Weather weather) {
        return myDatabase.insertHistory(userId, suit, weather);
    }

    //得到用户的信息
    public UserInfo getUserInfoById(Integer userId) {
        return myDatabase.getUserInfoById(userId);
    }

    public static void main(String[] args) {

    	String result="1,2,1,3;2,3,2,1;";
    	String[] res_suits=result.split(";");
    	//System.out.println(res_suits.length);
    	for(int i=0;i<res_suits.length;i++)
    	{
    		//System.out.println(res_suits[i]);
    		//for(int j = 0; j < res_suits.length;j++) {                     
                String[] su = res_suits[i].split(",");
                //System.out.println(su.length);
                for(int k=0;k<su.length;k++)
                	System.out.println(su[k]);
            //}
    		
    	}
    	/*
		Main userMain = new Main();
        userMain.initDatabase();

        ClothesInfo u_clothesInfo = new ClothesInfo();
        UserInfo userInfo = new UserInfo(123, "cjh", "123456", u_clothesInfo);
        userMain.insertUser(userInfo.getId(), userInfo.getName(), userInfo.getPassword());

        //userMain.deleteUser(userInfo);

        Clothes clothes = new Clothes(1, 2, 3, 4, 5);
        userMain.insertClothes(userInfo.getId(), clothes);
        Clothes clothes2 = new Clothes(2, 2, 4, 5, 6);
        userMain.insertClothes(userInfo.getId(), clothes2);

        List<Integer> clothesIdList = new ArrayList<>();
        List<Integer> clothesIdList2 = new ArrayList<>();
        clothesIdList.add(clothes2.getClothesId());
        clothesIdList.add(clothes.getClothesId());
        clothesIdList2.add(clothes.getClothesId());
        Suit suit = new Suit(clothesIdList);
        Suit suit2 = new Suit(clothesIdList2);
        List<Integer> valList = Arrays.asList(1,2,3,4,5,6,7);
        List<Integer> valList2 = Arrays.asList(2,3,4,5,6,7,8);
        Weather weather = new Weather(valList);
        Weather weather2 = new Weather(valList2);
        for(int i = 0; i < 30;i++)
            userMain.insertHistory(userInfo.getId(), suit, weather);
        userMain.insertHistory(userInfo.getId(), suit2, weather2);

        UserInfo userInfo1 = userMain.getUserInfoById(123);
    	*/
    }

}
