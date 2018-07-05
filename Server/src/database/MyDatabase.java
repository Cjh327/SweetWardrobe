package database;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyDatabase {

    private String driver;
    private String url;
    private String username;
    private String password;
    private Connection conn;
    private Statement stmt;
    private PreparedStatement pstmt;
    private ResultSet rs;

    MyDatabase(String m_driver, String m_url, String m_username, String m_password) {
        driver = m_driver;
        url = m_url;
        username = m_username;
        password = m_password;
        conn = null;
        stmt = null;
        pstmt = null;
    }

    MyDatabase() {
        driver = "com.mysql.jdbc.Driver";
        url = "jdbc:mysql://localhost:3306/mysql?useUnicode=true&characterEncoding=utf-8&useSSL=false";
        username = "root";
        //����Ӧ�û����ҵ�����
        //password = "19980327cjh";
        password="1234";
        conn = null;
        stmt = null;
        pstmt = null;
    }

    private List<Integer> transformClothesStringToInteger(String clothesString) {
        List<Integer> clothesList = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(clothesString);
        while (matcher.find()) {
            clothesList.add(Integer.parseInt(matcher.group(0)));
        }
        return clothesList;
    }

    private List<Integer> transformWeatherValStringToInteger(String weatherValString) {
        List<Integer> valList = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d");
        Matcher matcher = pattern.matcher(weatherValString);
        while(matcher.find()){
            valList.add(Integer.parseInt(matcher.group(0)));
        }
        return valList;
    }

    private List<Weather> transformWeatherStringToWeather(String weatherString) {
        List<Weather> weatherList = new ArrayList<>();
        String[] weathers = weatherString.split(";");
        for(String valString : weathers){
            List<Integer> valList = transformWeatherValStringToInteger(valString);
            Weather weather = new Weather(valList);
            weatherList.add(weather);
        }
        return weatherList;
    }

    private List<Suit> transformSuitStringToSuit(String suitString) {
        List<Suit> suitList = new ArrayList<>();
        String[] suits = suitString.split(";");
        for(String clothesString : suits){
            List<Integer> clothesList = transformClothesStringToInteger(clothesString);
            Suit suit = new Suit(clothesList);
            suitList.add(suit);
        }

        return suitList;
    }

    private void closeDatabase() {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //create and initialize userlist and clotheslist
    public boolean initTable() {
        String sql;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
            stmt = conn.createStatement();
            sql = "drop table if exists userlist";
            stmt.executeUpdate(sql);
            sql = "drop table if exists clotheslist";
            stmt.executeUpdate(sql);
            sql = "create table if not exists userlist(" +
                    "User_Id int not null, " +
                    "User_Name varchar (20), " +
                    "User_Password varchar (40), " +
                    "Clothes varchar (200), " +
                    "SuitHistories varchar (800), " +
                    "WeatherHistories varchar (800), " +
                    "HistoryDays int not null, " +
                    "primary key(User_Id))";
            stmt.executeUpdate(sql);
            System.out.println("initialize userlist successfully");
            sql = "create table if not exists clotheslist(" +
                    "Clothes_Id int not null, " +
                    "Clothes_Class int not null, " +
                    "Clothes_Type int not null, " +
                    "Clothes_Color int not null, " +
                    "Clothes_DirtyDegree int not null, " +
                    "primary key(Clothes_Id))";
            stmt.executeUpdate(sql);
            System.out.println("initialize clotheslist successfully");
        } catch (Exception e) {
            e.printStackTrace();
            closeDatabase();
            return false;
        } finally {
            closeDatabase();
        }
        return true;
    }

    //insert id, name, password of a user
    public boolean insertUser(Integer userId, String userName, String userPassword) {

        String sql;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
            stmt = conn.createStatement();
            sql = "insert into userlist (User_Id, User_Name, User_Password, Clothes, SuitHistories, WeatherHistories, HistoryDays) " +
                    "values (?, ?, ?, ?, ?, ?, ?);";
          /*  sql = "insert into userlist (User_Id, User_Name, User_Password, Clothes, SuitHistories, WeatherHistories, HistoryDays) " +
                    "select * from (SELECT ?, ?, ?, ?, ?, ?, ?) as tmp " +
                    "where not exists( " +
                    "    select User_id FROM userlist WHERE User_id = ? " +
                    ") LIMIT 1;";                    */
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, userName);
            pstmt.setString(3, userPassword);
            pstmt.setString(4, ".");
            pstmt.setString(5, ".");
            pstmt.setString(6, ".");
            pstmt.setInt(7, 0);
            pstmt.executeUpdate();
            System.out.println("insert user successfully");
        } catch (Exception e) {
            e.printStackTrace();
            closeDatabase();
            return false;
        } finally {
            closeDatabase();
        }
        return true;
    }

    //delete user
    public boolean deleteUser(Integer userId) {
        String sql;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
            stmt = conn.createStatement();
            sql = "delete from userlist where User_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            System.out.println("delete user successfully");
        } catch (Exception e) {
            e.printStackTrace();
            closeDatabase();
            return false;
        } finally {
            closeDatabase();
        }
        return true;
    }

    //insert clothes by userId
    public boolean insertClothes(Integer userId, Clothes clothes) {
        String sql;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
            stmt = conn.createStatement();
            sql = "insert into clotheslist (Clothes_Id, Clothes_Class, Clothes_Type, Clothes_color, Clothes_DirtyDegree) " +
                    "values (?, ?, ?, ?, ?);";
         /*   sql = "insert into clotheslist (Clothes_Id, Clothes_Class, Clothes_Type, Clothes_color, Clothes_DirtyDegree) " +
                    "select * from (SELECT ?, ?, ?, ?, ?) as tmp " +
                    "where not exists( " +
                    "    select Clothes_Id FROM clotheslist WHERE Clothes_Id = ? " +
                    ") LIMIT 1;";*/
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, clothes.getClothesId());
            pstmt.setInt(2, clothes.getClothesClass());
            pstmt.setInt(3, clothes.getClothesType());
            pstmt.setInt(4, clothes.getClothesColor());
            pstmt.setInt(5, clothes.getClothesDirtyDegree());
            pstmt.executeUpdate();

            sql = "update userlist set Clothes = concat(Clothes, ?) where User_Id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, clothes.transformClothesToString());
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();

            System.out.println("inser clothes successfully");

        } catch (Exception e) {
            e.printStackTrace();
            closeDatabase();
            return false;
        } finally {
            closeDatabase();
        }
        return true;
    }

    //insert suitHistory and weatherHistory by userId
    public boolean insertHistory(Integer userId, Suit suit, Weather weather) {
        String sql;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
            stmt = conn.createStatement();
            sql = "select HistoryDays from userlist where User_Id = ?;";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            String suitHistory = suit.transformSuitToString();
            String weatherHistory = weather.transformWeatherToString();
            while(rs.next()) {
                if (rs.getInt("HistoryDays") < 30) {
                    sql = "update userlist set SuitHistories = concat(SuitHistories, ?), WeatherHistories = concat(WeatherHistories, ?), HistoryDays = HistoryDays + 1 where User_Id = ?";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, suitHistory);
                    pstmt.setString(2, weatherHistory);
                    pstmt.setInt(3, userId);
                    pstmt.executeUpdate();
                } else {
                    sql = "select * from userlist where User_Id = ?";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, userId);
                    ResultSet rs_history = pstmt.executeQuery();
                    while(rs_history.next()) {
                        String suitHistories = rs_history.getString("SuitHistories");
                        String weatherHistories = rs_history.getString("WeatherHistories");
                        int i = suitHistories.indexOf(';');
                        suitHistories = "." + suitHistories.substring(i + 1) + suitHistory;
                        i = weatherHistories.indexOf(';');
                        weatherHistories = "." + weatherHistories.substring(i + 1) + weatherHistory;

                        sql = "update userlist set SuitHistories = ?, WeatherHistories = ? where User_Id = ?";
                        pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, suitHistories);
                        pstmt.setString(2, weatherHistories);
                        pstmt.setInt(3, userId);
                        pstmt.executeUpdate();
                    }
                }
            }

            System.out.println("insert history successfully");

        } catch (Exception e) {
            e.printStackTrace();
            closeDatabase();
            return false;
        } finally {
            closeDatabase();
        }
        return true;
    }

    //get a user's information by userId
    public UserInfo getUserInfoById(Integer userId) {

        UserInfo userInfo = new UserInfo();
        String sql;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
            stmt = conn.createStatement();

            sql = "select * from userlist where User_Id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {

                //userInfo.name
                String userName = rs.getString("User_Name");

                //userInfo.password
                String userPassword = rs.getString("User_PassWord");

                //userInfo.clothesInfo.suitHistory
                String suitString = rs.getString("SuitHistories");
                List<Suit> suitHistory = transformSuitStringToSuit(suitString);

                //userInfo.clothesInfo.weatherHistory
                String weatherString = rs.getString("WeatherHistories");
                List<Weather> weatherHistory = transformWeatherStringToWeather(weatherString);

                //userInfo.clothesInfo.clothesList
                String clothesString = rs.getString("Clothes");
                List<Clothes> userClothesList = new ArrayList<>();
                List<Integer> userClothesIdList = transformClothesStringToInteger(clothesString);
                for (Integer clothesId : userClothesIdList) {
                    sql = "select * from clotheslist where Clothes_Id = ?;";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, clothesId);
                    ResultSet rs_clothes = pstmt.executeQuery();
                    while (rs_clothes.next()) {
                        List<Integer> features = new ArrayList<>(5);
                        features.add(rs_clothes.getInt("Clothes_Id"));
                        features.add(rs_clothes.getInt("Clothes_Class"));
                        features.add(rs_clothes.getInt("Clothes_Type"));
                        features.add(rs_clothes.getInt("Clothes_Color"));
                        features.add(rs_clothes.getInt("Clothes_DirtyDegree"));
                        Clothes clothes = new Clothes(features);
                        userClothesList.add(clothes);
                    }
                }

                //userInfo.clothesInfo
                ClothesInfo clothesInfo = new ClothesInfo(suitHistory, weatherHistory, userClothesList);

                userInfo.setInfo(userId, userName, userPassword, clothesInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            closeDatabase();
            return null;
        } finally {
            closeDatabase();
        }
        System.out.println("get userInfo successfully");
        return userInfo;
    }
}
