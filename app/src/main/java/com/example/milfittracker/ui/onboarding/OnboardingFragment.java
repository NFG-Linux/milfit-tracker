package com.example.milfittracker.ui.onboarding;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.milfittracker.R;
import com.example.milfittracker.helpers.LaunchOrder;
import com.example.milfittracker.repo.UserRepo;
import com.example.milfittracker.room.MilFitDB;
import com.example.milfittracker.room.User;
import com.example.milfittracker.MainActivity;
import java.util.Calendar;

public class OnboardingFragment extends Fragment {

    private EditText nameIn;
    private Button dobButton;
    private RadioGroup genderGrp;
    private RadioGroup altiGrp;
    private Button saveButton;

    private String selectedDob; // yyyy-MM-dd

    private UserRepo userRepo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_onboarding, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        nameIn     = v.findViewById(R.id.input_name);
        dobButton     = v.findViewById(R.id.button_dob);
        genderGrp   = v.findViewById(R.id.grp_gender);
        altiGrp = v.findViewById(R.id.grp_alti);
        saveButton    = v.findViewById(R.id.button_save);

        userRepo = new UserRepo(MilFitDB.getInstance(requireContext().getApplicationContext()));

        dobButton.setOnClickListener(view -> showDatePicker());
        saveButton.setOnClickListener(view -> saveUser());
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dlg = new DatePickerDialog(requireContext(),
                (DatePicker datePicker, int year, int month, int day) -> {
                    // month is 0-based
                    String mm = String.format("%02d", month + 1);
                    String dd = String.format("%02d", day);
                    selectedDob = year + "-" + mm + "-" + dd;
                    dobButton.setText(selectedDob);
                }, y, m, d);
        dlg.show();
    }

    private void saveUser() {
        String name = nameIn.getText() != null ? nameIn.getText().toString().trim() : "";

        if (TextUtils.isEmpty(name)) {
            nameIn.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(selectedDob)) {
            Toast.makeText(requireContext(), "Select date of birth", Toast.LENGTH_SHORT).show();
            return;
        }

        int genderId = genderGrp.getCheckedRadioButtonId();
        if (genderId == View.NO_ID) {
            Toast.makeText(requireContext(), "Select gender", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton genderBtn = genderGrp.findViewById(genderId);
        String gender = genderBtn.getText().toString();

        int altId = altiGrp.getCheckedRadioButtonId();
        if (altId == View.NO_ID) {
            Toast.makeText(requireContext(), "Select altitude group", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton altBtn = altiGrp.findViewById(altId);
        String altitudeGroupValue = altBtn.getText().toString(); // "< 5000 ft" / "≥ 5000 ft"

        User user = new User();
        user.setName(name);
        user.setBDay(selectedDob);
        user.setGender(gender);
        user.setAltiGrp(altitudeGroupValue);

        saveButton.setEnabled(false);
        userRepo.save(user, id -> {
            LaunchOrder.setOnboarded(requireContext(), true);
            Toast.makeText(requireContext(), "Welcome, " + name + "!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(requireContext(), MainActivity.class));
            requireActivity().finish();
        });
    }
}