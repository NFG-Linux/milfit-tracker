package com.example.milfittracker.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.example.milfittracker.R;

public class HomeFragment extends Fragment {
    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        NavController navController = NavHostFragment.findNavController(this);

        ImageButton spaceforce = view.findViewById(R.id.spaceForce);
        ImageButton navy = view.findViewById(R.id.Navy);
        ImageButton marinecorps = view.findViewById(R.id.Marines);
        ImageButton army = view.findViewById(R.id.Army);
        ImageButton coastguard = view.findViewById(R.id.coastGuard);
        ImageButton airforce = view.findViewById(R.id.airForce);

        spaceforce.setOnClickListener(v -> navController.navigate(R.id.home_to_spaceforce));
        navy.setOnClickListener(v -> navController.navigate(R.id.home_to_navy));
        marinecorps.setOnClickListener(v -> navController.navigate(R.id.home_to_marines));
        army.setOnClickListener(v -> navController.navigate(R.id.home_to_army));
        coastguard.setOnClickListener(v -> navController.navigate(R.id.home_to_coastguard));
        airforce.setOnClickListener(v -> navController.navigate(R.id.home_to_airforce));

        return view;
    }
}