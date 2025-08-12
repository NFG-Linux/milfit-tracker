package com.example.milfittracker.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.milfittracker.databinding.FragmentHomeBinding;
import com.example.milfittracker.R;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        View Navy = root.findViewById(R.id.navy);
        Navy.setOnClickListener(view ->
                NavHostFragment.findNavController(this).navigate(R.id.home_to_navy)
        );
                return root;
    }
}