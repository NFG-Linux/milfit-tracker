package com.example.milfittracker.ui.log;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.example.milfittracker.R;
import com.example.milfittracker.room.Scores;
import com.example.milfittracker.ui.log.ScoreViewModel;

public class RunsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
    private LocationRequest request;
    private FusedLocationProviderClient fused;
    private final List<LatLng> points = new ArrayList<>();
    private Polyline poly;
    private boolean recording;
    private LocationCallback callback;
    private boolean running = false;
    private long startRealtime = 0L;
    private long elapsedTime = 0L;
    private double totalMeters = 0.0;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = new ViewModelProvider(requireActivity()).get(ScoreViewModel.class);

        fused = LocationServices.getFusedLocationProviderClient(requireContext());
        request = new LocationRequest.Builder(1000L)   // 1s
                .setMinUpdateIntervalMillis(500L)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .build();

        callback = new LocationCallback() {
            @Override public void onLocationResult(@NonNull LocationResult result) {
                List<Location> list = result.getLocations();
                if (list == null || list.isEmpty() || map == null) return;

                LatLng p = new LatLng(list.get(list.size()-1).getLatitude(),
                        list.get(list.size()-1).getLongitude());
                if (points.isEmpty()) {
                    points.add(p);
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(p, 17f));
                    ensurePolyline();
                } else {
                    LatLng last = points.get(points.size()-1);
                    totalMeters += SphericalUtil.computeDistanceBetween(last, p);
                    points.add(p);
                    if (poly != null) poly.setPoints(points);
                }
            }
        };

        permLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                res -> {
                    boolean ok = Boolean.TRUE.equals(res.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION,false)) ||
                            Boolean.TRUE.equals(res.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION,false));
                    if (ok) startRunInternal(); else toast("Location permission denied");
                });
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle b) {
        View v = inf.inflate(R.layout.fragment_runs, c, false);
        dist = v.findViewById(R.id.Distance);
        time = v.findViewById(R.id.Time);
        Start = v.findViewById(R.id.Start);
        Stop  = v.findViewById(R.id.Stop);

        Start.setOnClickListener(view -> startRun());
        Stop.setOnClickListener(view -> stopRun());

        SupportMapFragment mf = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mf != null) mf.getMapAsync(this);
        return v;
    }

    @Override public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(true);
        enableMyLocationIfPermitted();
    }

    private void ensurePolyline() {
        if (poly == null && map != null) {
            poly = map.addPolyline(new PolylineOptions().width(8f));
            poly.setPoints(points);
        }
    }

    private final Handler ui = new Handler(Looper.getMainLooper());
    private final Runnable ticker = new Runnable() {
        @Override public void run() {
            if (running) {
                elapsedTime = android.os.SystemClock.elapsedRealtime() - startRealtime;
                time.setText(formatTime(elapsedTime));
                dist.setText(formatDist(totalMeters));
                ui.postDelayed(this, 1000);
            }
        }
    };

    private TextView dist, time;
    private Button Start, Stop;

    private ActivityResultLauncher<String[]> permLauncher;

    private ScoreViewModel vm;

    private void startRun() {
        if (running) return;
        // permission check
        if (!hasPerm()) {
            permLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION});
            return;
        }
        startRunInternal();
    }

    @SuppressLint("MissingPermission")
    private void startRunInternal() {
        running = true;
        startRealtime = android.os.SystemClock.elapsedRealtime();
        elapsedTime = 0L;
        totalMeters = 0.0;
        points.clear();
        ensurePolyline();
        ui.post(ticker);

        fused.requestLocationUpdates(request, callback, Looper.getMainLooper());
        if (map != null) map.setMyLocationEnabled(true);
    }

    private void stopRun() {
        if (!running) return;
        running = false;
        fused.removeLocationUpdates(callback);
        ui.removeCallbacks(ticker);

        Scores score = new Scores();
        score.setBranch("Navy");
        score.setEvent("Outdoor Run");
        score.setGender("Male");
        score.setAge(0);
        score.setEventValue((int)Math.round(totalMeters));
        score.setUnit("m");
        score.setDate(Instant.now().toString());
        vm.insert(score);

        Toast.makeText(requireContext(), "Saved run: "+(totalMeters/1000f)+" km", Toast.LENGTH_SHORT).show();
    }

    private boolean hasPerm() {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocationIfPermitted() {
        if (map == null) return;
        if (hasPerm()) map.setMyLocationEnabled(true);
    }

    private static String formatDist(double meters) {
        return String.format(Locale.US, "%.2f km", meters/1000.0);
    }
    private static String formatTime(long ms) {
        long s = ms/1000, m = s/60, hh = m/60;
        return String.format(Locale.US, "%02d:%02d:%02d", hh, m%60, s%60);
    }
    private void toast(String msg){
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }
}