package kr.changhan.mytravels.weather;

public class Weather {

    public int dt;
    public double temp_min;
    public double temp_max;
    public String _main;
    public String description;

    public Weather(int dt, double temp_min, double temp_max, String _main, String description) {
        this.dt = dt;
        this.temp_min = temp_min;
        this.temp_max = temp_max;
        this._main = _main;
        this.description = description;
    }
}
