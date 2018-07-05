package database;

public class UserInfo {

    private Integer id;
    private String name;
    private String password;
    private ClothesInfo clothesInfo;

    public UserInfo(Integer u_id, String u_name, String u_password, ClothesInfo u_clothesInfo) {
        id = u_id;
        name = u_name;
        password = u_password;
        clothesInfo = u_clothesInfo;
    }

    public UserInfo() {
        id = null;
        name = null;
        password = null;
        clothesInfo = null;
    }

    public void setInfo(Integer u_id, String u_name, String u_password, ClothesInfo u_clothesInfo) {
        id = u_id;
        name = u_name;
        password = u_password;
        clothesInfo = u_clothesInfo;
    }

    public void addClothes(Clothes clothes) {
        clothesInfo.addClothes(clothes);
    }

    public void addHistory(Suit suit, Weather weather) {
        clothesInfo.addHistory(suit, weather);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public ClothesInfo getClothesInfo() {
        return clothesInfo;
    }
}