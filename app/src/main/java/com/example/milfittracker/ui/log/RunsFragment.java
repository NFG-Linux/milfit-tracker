package com.example.milfittracker.ui.log;

import android.os.Bundle;
import android.os.Looper;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import java.util.ArrayList;
import java.util.List;
import com.example.milfittracker.room.Scores;
import com.example.milfittracker.ui.log.ScoreViewModel;
import com.example.milfittracker.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class RunsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
    private FusedLocationProviderClient fused;
    private final List<LatLng> points = new ArrayList<>();
    private Polyline poly;
    private boolean recording;
    private LocationCallback callback;

    @Override public View onCreateView(@NonNull LayoutInflater inf, ViewGroup c, Bundle b){
        View v = inf.inflate(R.layout.fragment_runs, c, false);
        fused = LocationServices.getFusedLocationProviderClient(requireContext());
        ((SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.map)).getMapAsync(this);

        v.findViewById(R.id.Start).setOnClickListener(view1 -> startRun());
        v.findViewById(R.id.Stop).setOnClickListener(view1 -> stopRun());
        return v;
    }

    @Override public void onMapReady(@NonNull GoogleMap gMap) {
        map = gMap;
        map.getUiSettings().setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 10);
    }

    private void startRun(){
        if (recording || map == null) return;
        recording = true; points.clear();
        if (poly != null) poly.remove();
        poly = map.addPolyline(new PolylineOptions().color(Color.parseColor("#4C5D34")).width(10f));

        LocationRequest req = LocationRequest.create()
                .setInterval(1000).setFastestInterval(500)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        callback = new LocationCallback(){
            @Override public void onLocationResult(@NonNull LocationResult result) {
                for (Location loc : result.getLocations()){
                    LatLng p = new LatLng(loc.getLatitude(), loc.getLongitude());
                    points.add(p);
                }
                poly.setPoints(points);
                if (!points.isEmpty()) map.animateCamera(CameraUpdateFactory.newLatLngZoom(points.get(points.size()-1), 16f));
            }
        };
        fused.requestLocationUpdates(req, callback, Looper.getMainLooper());
    }

    private void stopRun(){
        if (!recording) return;
        recording = false;
        fused.removeLocationUpdates(callback);

        // compute distance
        float distMeters = 0f;
        for (int i=1;i<points.size();i++){
            float[] res = new float[1];
            LatLng a = points.get(i-1), b = points.get(i);
            Location.distanceBetween(a.latitude,a.longitude,b.latitude,b.longitude,res);
            distMeters += res[0];
        }

        Scores score = new Scores();
        score.setBranch("Navy"); score.setEvent("Outdoor Run");
        score.setGender("Unspecified"); score.setAge(0);
        score.setEventValue(Math.round(distMeters)); score.setUnit("m");
        score.setDate(java.time.LocalDateTime.now().toString());
        new ViewModelProvider(this).get(ScoreViewModel.class).insert(score);

        Toast.makeText(requireContext(), "Saved run: "+(distMeters/1000f)+" km", Toast.LENGTH_SHORT).show();
    }
}