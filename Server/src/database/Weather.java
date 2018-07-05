package database;


import java.util.ArrayList;
import java.util.List;


public class Weather {
    private WeatherState state;
    private List<Integer> temperature;  //len = 2, �ֱ�洢�û�������Ϣ�������ߡ�����¶�
    private List<Integer> humidity;     //len = 2, �ֱ�洢�û�������Ϣ�������ߡ����ʪ��
    private List<Integer> windForce;    //len = 2, �ֱ�洢�û�������Ϣ�������ߡ���ͷ����ȼ�

    public Weather(WeatherState state, List<Integer> temperature, List<Integer> humidity, List<Integer> windForce) {
        this.state = state;
        assert temperature.size() == 2;
        assert humidity.size() == 2;
        assert windForce.size() == 2;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windForce = windForce;
    }

    public Weather(List<Integer> valList) {
        assert valList.size() == 7;
        this.state = WeatherState.values()[valList.get(0)];
        this.temperature = new ArrayList<>(2);
        this.temperature.add(valList.get(1));
        this.temperature.add(valList.get(2));
        this.humidity = new ArrayList<>(2);
        this.humidity.add(valList.get(3));
        this.humidity.add(valList.get(4));
        this.windForce = new ArrayList<>(2);
        this.windForce.add(valList.get(5));
        this.windForce.add(valList.get(6));
    }

    public WeatherState getState() {
        return state;
    }

    public Integer getUpperTemperature() {
        return temperature.get(0);
    }

    public Integer getLowerTemperature() {
        return temperature.get(1);
    }

    public Integer getUpperHumidity() {
        return humidity.get(0);
    }

    public Integer getLowerHumidity() {
        return humidity.get(1);
    }

    public Integer getUpperWindForce() {
        return windForce.get(0);
    }

    public Integer getLowerWindForce() {
        return windForce.get(1);
    }

    public List<Integer> formatWeather() {
        List<Integer> digitalWeather = new ArrayList<>();
        Integer digitalState;
        switch (state) {
            case DEFAULT:
                digitalState = -1;
                break;
            case SUNNY:
                digitalState = 0;
                break;
            case CLOUDY:
                digitalState = 1;
                break;
            case SHOWERY:
                digitalState = 2;
                break;
            case RAINY:
                digitalState = 3;
                break;
            case FOGGY:
                digitalState = 4;
                break;
            case WINDY:
                digitalState = 5;
                break;
            case SNOWY:
                digitalState = 6;
                break;
            default:
                digitalState = -1;
                break;
        }
        digitalWeather.add(digitalState);
        digitalWeather.addAll(temperature);
        digitalWeather.addAll(humidity);
        digitalWeather.addAll(windForce);
        return digitalWeather;
    }

    public String transformWeatherToString() {
        Integer i_state = state.ordinal();
        String s_state = i_state.toString();
        String s_temp = temperature.get(0).toString() + "," + temperature.get(1).toString();
        String s_humidity = humidity.get(0).toString() + "," + humidity.get(1).toString();
        String s_windForce = windForce.get(0).toString() + "," + windForce.get(1).toString();
        String str = s_state + "," + s_temp + "," + s_humidity + "," + s_windForce + ";";
        return str;
    }
}
