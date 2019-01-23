package kr.changhan.mytravels.discovery;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import kr.changhan.mytravels.R;

public class DiscoveryAdapter extends RecyclerView.Adapter<DiscoveryAdapter.ViewHolder> {
    Context context;
    ArrayList<Discovery> discoveries;
    LayoutInflater layoutInflater;

    public DiscoveryAdapter(Context context) {
        this.context = context;
        if (discoveries == null) discoveries = new ArrayList<>();
        layoutInflater = LayoutInflater.from(context);
    }

    public void setDiscoveries(ArrayList<Discovery> discoveries) {
        this.discoveries = discoveries;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.activity_discovery_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Discovery discovery = discoveries.get(position);
        final GeoDataClient mGeoDataClient = Places.getGeoDataClient(context);
        Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(discovery.placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                PlacePhotoMetadataResponse photos = task.getResult();
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                if (photoMetadataBuffer.getCount() > 0) {
                    PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                    Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                    photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                            PlacePhotoResponse photo = task.getResult();
                            Bitmap bitmap = photo.getBitmap();
                            holder.placeIv.setImageBitmap(bitmap);
                        }
                    });
                }
            }
        });
        holder.placeAddressTv.setText(discovery.vinicity);
    }

    @Override
    public int getItemCount() {
        return discoveries.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        ImageView placeIv;
        TextView placeNameTv, placeAddressTv;

        public ViewHolder(@NonNull View v) {
            super(v);

            placeIv = v.findViewById(R.id.place_iv);
            placeNameTv = v.findViewById(R.id.place_name_tv);
            placeAddressTv = v.findViewById(R.id.place_address_tv);
        }
    }
}
