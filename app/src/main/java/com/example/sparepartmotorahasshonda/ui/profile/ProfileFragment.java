package com.example.sparepartmotorahasshonda.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.sparepartmotorahasshonda.Login;
import com.example.sparepartmotorahasshonda.MainActivity;
import com.example.sparepartmotorahasshonda.R;
import com.example.sparepartmotorahasshonda.ui.home.HomeFragment;

public class ProfileFragment extends Fragment {
    TextView TvUsernameProfile;
    Button btnLogOut;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        TvUsernameProfile = view.findViewById(R.id.TvUsernameProfile);
        btnLogOut = view.findViewById(R.id.btnLogout);
        SharedPreferences loginPreferences = getActivity().getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
        String username = loginPreferences.getString("username", null); // "" is the default value if "username" doesn't exist
        if (username != null) {
            TvUsernameProfile.setText(username.toString());
        } else {
            TvUsernameProfile.setText("you are not login");
        }
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences loginPreferences = getActivity().getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = loginPreferences.edit();
                editor.remove("username");
                editor.apply();

                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Optional flags to clear previous activities
                startActivity(intent);
            }
        });
        return view;
    }
}