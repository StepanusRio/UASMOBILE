package com.example.sparepartmotorahasshonda.ui.profile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.sparepartmotorahasshonda.API.RetrofitAPI;
import com.example.sparepartmotorahasshonda.ChangePassword;
import com.example.sparepartmotorahasshonda.EditProfile;
import com.example.sparepartmotorahasshonda.Login;
import com.example.sparepartmotorahasshonda.MainActivity;
import com.example.sparepartmotorahasshonda.Model.User;
import com.example.sparepartmotorahasshonda.OrderHistory;
import com.example.sparepartmotorahasshonda.R;
import com.example.sparepartmotorahasshonda.Utils.UserManager;

public class ProfileFragment extends Fragment implements UserManager.UserLoginListener {
    TextView TvUsernameProfile,TvEmailProfile,TvAddressProfile,TvCityProfile,TvProvinceProfile;
    ImageView profileImage;
    Button btnLogOut,btnEditProfile,btnHistoryOrder,ResetPassword;
    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        TvUsernameProfile = view.findViewById(R.id.TvUsernameProfile);
        TvEmailProfile = view.findViewById(R.id.tvEmailProfile);
        TvAddressProfile = view.findViewById(R.id.tvAddressProfile);
        profileImage = view.findViewById(R.id.profileImage);
        TvCityProfile=view.findViewById(R.id.tvCityProfile);
        TvProvinceProfile = view.findViewById(R.id.tvProvinceProfile);
        btnLogOut = view.findViewById(R.id.btnLogout);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnHistoryOrder = view.findViewById(R.id.btnHistoryOrder);
        ResetPassword = view.findViewById(R.id.ResetPassword);
        SharedPreferences loginPreferences = getActivity().getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
        String username = loginPreferences.getString("username", null); // "" is the default value if "username" doesn't exist
        if (username != null) {
            CheckUserLogin(username);
            btnEditProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), EditProfile.class);
                    startActivity(intent);
                }
            });
            btnHistoryOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Go to Order History Activity
                    Intent intent = new Intent(getContext(), OrderHistory.class);
                    startActivity(intent);
                }
            });
            ResetPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), ChangePassword.class);
                    intent.putExtra("username",username);
                    startActivity(intent);
                }
            });
        }else{
            TvUsernameProfile.setText("");
            TvEmailProfile.setText("");
            TvAddressProfile.setText("");
            TvCityProfile.setText("");
            TvProvinceProfile.setText("");
            Glide.with(this)
                    .load(RetrofitAPI.BASE_URL+"images/user/default.jpg")
                    .centerCrop()
                    .transform(new RoundedCorners(150))
                    .into(profileImage);
            btnLogOut.setText("Login");
            btnLogOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent loginIntent = new Intent(getActivity(), Login.class);
                    startActivity(loginIntent);
                }
            });
            btnEditProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentLogin = new Intent(getContext(), Login.class);
                    startActivity(intentLogin);
                    Toast.makeText(getContext(), "PLEASE LOGIN FIRST", Toast.LENGTH_SHORT).show();
                }
            });
            btnHistoryOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentLogin = new Intent(getContext(), Login.class);
                    startActivity(intentLogin);
                    Toast.makeText(getContext(), "PLEASE LOGIN FIRST", Toast.LENGTH_SHORT).show();
                }
            });
            ResetPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentLogin = new Intent(getContext(), Login.class);
                    startActivity(intentLogin);
                    Toast.makeText(getContext(), "PLEASE LOGIN FIRST", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return view;
    }

    public void CheckUserLogin(String username){
        UserManager.CheckUserLogin(username,getContext(), this);
    }

    @Override
    public void onUserLoginSuccess(User user,Context context) {
        TvUsernameProfile.setText(user.getUsername());
        TvEmailProfile.setText(user.getEmail());
        TvAddressProfile.setText(user.getAlamat());
        TvCityProfile.setText(user.getKota());
        TvProvinceProfile.setText(user.getProvinsi());
        Glide.with(context)
                .load(RetrofitAPI.BASE_URL+"images/user/"+user.getImage())
                .centerCrop()
                .transform(new RoundedCorners(150))
                .into(profileImage);
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
    }

    @Override
    public void onUserLoginFailure(String errorMessage) {

    }
}