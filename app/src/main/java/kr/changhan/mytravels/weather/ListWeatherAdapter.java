package kr.changhan.mytravels.weather;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import kr.changhan.mytravels.R;

public class ListWeatherAdapter extends RecyclerView.Adapter<ListWeatherAdapter.ItemWeatherViewHolder> {

    List<Weather> weatherList;

    public ListWeatherAdapter(List<Weather> weatherList) {
        this.weatherList = weatherList;
    }

    @NonNull
    @Override
    public ItemWeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather, parent, false);
        return new ItemWeatherViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ItemWeatherViewHolder holder, int position) {

        Weather weather = weatherList.get(position);
        holder.bind(weather);

    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    class ItemWeatherViewHolder extends RecyclerView.ViewHolder {

        TextView tvDate;
        ImageView imvDescription;
        TextView tvTemperatureMax;
        TextView tvTemperatureMin;
        TextView tvMain;

        public ItemWeatherViewHolder(@NonNull View v) {
            super(v);

            tvDate = v.findViewById(R.id.tvDate);
            imvDescription = v.findViewById(R.id.imvDescription);
            tvTemperatureMax = v.findViewById(R.id.tvTemperatureMax);
            tvTemperatureMin = v.findViewById(R.id.tvTemperatureMin);
            tvMain = v.findViewById(R.id.tvMain);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        void bind(final Weather weather) {

            Date date = new Date(weather.dt * 1000L);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT-4"));
            String formattedDate = sdf.format(date);

            int max = (int) (weather.temp_max - 273.15);
            int min = (int) (weather.temp_min - 273.15);

            tvDate.setText(formattedDate);
            tvTemperatureMax.setText(String.valueOf(max));
            tvTemperatureMin.setText(String.valueOf(min));
            tvMain.setText(weather.description);

            if (weather.description.equals("broken clouds") || weather.description.equals("overcast clouds")) {
                imvDescription.setImageResource(R.drawable.ic_emoji_u2601);
            } else if (weather.description.equals("light rain")) {
                imvDescription.setImageResource(R.drawable.ic_emoji_u26c8);
            } else if (weather.description.equals("few clouds")) {
                imvDescription.setImageResource(R.drawable.ic_emoji_u1f326);
            } else if (weather.description.equals("clear sky")) {
                imvDescription.setImageResource(R.drawable.ic_emoji_u1f324);
            } else {
                imvDescription.setImageResource(R.drawable.ic_emoji_u2601);
            }
        }
    }
}
