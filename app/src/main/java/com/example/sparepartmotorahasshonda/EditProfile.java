package com.example.sparepartmotorahasshonda;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.sparepartmotorahasshonda.API.CostService;
import com.example.sparepartmotorahasshonda.API.ImageUpload;
import com.example.sparepartmotorahasshonda.API.RetrofitAPI;
import com.example.sparepartmotorahasshonda.API.RetrofitClient;
import com.example.sparepartmotorahasshonda.API.UserService;
import com.example.sparepartmotorahasshonda.Model.Kota;
import com.example.sparepartmotorahasshonda.Model.Provinsi;
import com.example.sparepartmotorahasshonda.Model.ResponseUpdateUser;
import com.example.sparepartmotorahasshonda.Model.ResponseUploadImage;
import com.example.sparepartmotorahasshonda.Model.User;
import com.example.sparepartmotorahasshonda.Utils.UserManager;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.sparepartmotorahasshonda.databinding.ActivityEditProfileBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditProfile extends AppCompatActivity implements UserManager.UserLoginListener {
    ImageView profileImage;
    Button btnUpdateProfile;
    ProgressDialog pd;
    Spinner spinProv,spinKota;
    Provinsi provinsi;
    ArrayList<String> province_name = new ArrayList<>();
    ArrayList<Integer> province_id = new ArrayList<>();
    Kota kota;
    ArrayList<String> city_name = new ArrayList<>();
    ArrayList<Integer> city_id = new ArrayList<>();
    ArrayList<Integer> postal_code = new ArrayList<>();
    int id_kota_tujuan;
    int postal_code_tujuan;
    int province_id_selected;
    String KotaTujuan,ProvinsiTujuan;
    EditText etUsername,etAlamat,etEmail,etPostCode;
    String path_image;
    String imgFileUploaded;
    int REQUEST_GALLERY=100;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE={
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        SharedPreferences loginPreferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
        String username = loginPreferences.getString("username", null); // "" is the default value if "username" doesn't exist
        if (username!=null) {
            CheckUserLogin(username);
        }else{
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            Toast.makeText(this, "PLEASE LOGIN FIRST", Toast.LENGTH_SHORT).show();
        }
        // Setup ID
        profileImage = (ImageView) findViewById(R.id.imageProfile);
        btnUpdateProfile = (Button) findViewById(R.id.btnUpdate);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etAlamat = (EditText) findViewById(R.id.etAlamat);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPostCode = (EditText) findViewById(R.id.etPostCode);
        spinKota = (Spinner) findViewById(R.id.spinKota);
        spinProv = (Spinner) findViewById(R.id.spinProv);
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        // Load Province
        loadProvince();
        spinProv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                int kode = 0;
                province_id_selected = province_id.get(position);
                ProvinsiTujuan=province_name.get(position);
                loadKota(province_id.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        // Select City
        spinKota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                id_kota_tujuan = city_id.get(position);
                KotaTujuan=city_name.get(position);
                postal_code_tujuan = postal_code.get(position);
                loadPostCode(postal_code_tujuan);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        // To Select Image
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Open Gallery"),REQUEST_GALLERY);
            }
        });
        // Save all on database user
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.show();
                BitmapDrawable draw = (BitmapDrawable) profileImage.getDrawable();
                Bitmap bitmap = draw.getBitmap();
                FileOutputStream outStream;
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath()+"/Pictures/upload");
                dir.mkdirs();
                String filename = String.format("%d.jpg",System.currentTimeMillis());
                Log.i("INFO FILE NAME", "Filename: "+filename);
                File outFile = new File(dir,filename);
                try {
                    outStream = new FileOutputStream(outFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,outStream);
                    outStream.flush();
                    outStream.close();
                    path_image=outFile.getAbsolutePath();
                    Log.i("INFO PATH UPLOAD", "Path Upload: "+path_image);
                    File imageFile = new File(path_image);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-file"),imageFile);
                    MultipartBody.Part partImage = MultipartBody.Part.createFormData("imageUploadProfile",imageFile.getName(),requestBody);
                    ImageUpload uploadService = RetrofitAPI.API().create(ImageUpload.class);
                    // Call<ResponseUploadImage> upload = uploadService.uploadImageProfile(partImage);
                    uploadService.uploadImageProfile(partImage).enqueue(new Callback<ResponseUploadImage>() {
                        @Override
                        public void onResponse(Call<ResponseUploadImage> call, Response<ResponseUploadImage> response) {
                            if (response.body().getKode().equals("1")) {
                                pd.dismiss();
                                Log.d("Retrofit", "onSuccess: "+response.body().getPesan());
                                imgFileUploaded=response.body().getImageUploaded();
                                updateUser(username);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseUploadImage> call, Throwable t) {
                            Toast.makeText(EditProfile.this, "GAGAL UPLOAD: "+t.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.i("FAILED UPLOAD IMAGE", "onFailure: "+t.getMessage());
                            pd.dismiss();
                        }
                    });
                }catch (Exception e){
                    Log.i("INFO SAVE ERROR", "Error: "+ e);
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void loadPostCode(int postalCodeTujuan) {
        etPostCode.setText(""+postalCodeTujuan);
    }

    private void loadKota(int province_id) {
        CostService api = RetrofitAPI.API().create(CostService.class);
        Call<ResponseBody> call = api.get_kota(province_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    kota = new Kota();
                    city_id.clear();
                    city_name.clear();
                    for (int i=0;i<json.getJSONArray("result").length();i++){
                        kota.city_id=Integer.parseInt(json.getJSONArray("result").getJSONObject(i).get("city_id").toString());
                        kota.city_name=json.getJSONArray("result").getJSONObject(i).get("city_name").toString();
                        kota.postal_code=Integer.parseInt(json.getJSONArray("result").getJSONObject(i).get("postal_code").toString());
                        city_name.add(kota.getCity_name());
                        city_id.add(kota.getCity_id());
                        postal_code.add(kota.getPostal_code());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,city_name);
                    spinKota.setAdapter(adapter);
                } catch (JSONException | IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void loadProvince() {
        CostService api = RetrofitAPI.API().create(CostService.class);
        Call<ResponseBody> call = api.get_provinsi();
        Log.i("[INFO LOAD PROVINSI]", "loadProvinsi: CEK LOAD");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    provinsi = new Provinsi();
                    for(int i=0;i<json.getJSONArray("result").length();i++){
                        provinsi.province_id=Integer.parseInt(json.getJSONArray("result").getJSONObject(i).get("province_id").toString());
                        provinsi.province_name=json.getJSONArray("result").getJSONObject(i).get("province_name").toString();
                        province_name.add(provinsi.getProvince_name());
                        province_id.add(provinsi.getProvince_id());
                    }
                    Log.i("[CHECK RESPONSE]", "onResponse: "+json.get("result").toString());
                    Log.i("[Info Length]", "onResponse: "+json.getJSONArray("result").length());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,province_name);
                    spinProv.setAdapter(adapter);
                } catch (JSONException | IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("[FAILURE]", "onFailure: "+t.getMessage());
            }
        });
    }

    private void CheckUserLogin(String username) {
        UserManager.CheckUserLogin(username,this, this);
    }

    private void updateUser(String username) {
        // Retrieve data;
        String alamat = etAlamat.getText().toString();
        String email = etEmail.getText().toString();
        String province = ProvinsiTujuan;
        String kota = KotaTujuan;
        String kode_pos = String.valueOf(postal_code_tujuan);
        String image = imgFileUploaded;

        UserService api = RetrofitAPI.API().create(UserService.class);
        Call<ResponseUpdateUser> call = api.updateUserProfile(
                username,
                alamat,
                email,
                province,
                kota,
                kode_pos,
                image
        );
        call.enqueue(new Callback<ResponseUpdateUser>() {
            @Override
            public void onResponse(Call<ResponseUpdateUser> call, Response<ResponseUpdateUser> response) {
                if (response.isSuccessful()) {
                    // Handle successful response here
                    // You can extract data from response.body() if needed
                    Toast.makeText(EditProfile.this, "Update successful!", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle error response
                    Toast.makeText(EditProfile.this, "Failed to update. Please try again."+response.body(), Toast.LENGTH_SHORT).show();
                }
                pd.dismiss(); // Dismiss progress dialog
            }

            @Override
            public void onFailure(Call<ResponseUpdateUser> call, Throwable t) {
                // Handle failure
                Toast.makeText(EditProfile.this, "Update failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss(); // Dismiss progress dialog
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        verifyStoragePermission(EditProfile.this);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALLERY){
                Uri uri = data.getData();
                File file = new File(uri.getPath());
                profileImage.setImageURI(uri);
            }
        }
    }

    private void verifyStoragePermission(EditProfile editProfile) {
        int permission = ActivityCompat.checkSelfPermission(editProfile, Manifest.permission.READ_EXTERNAL_STORAGE);
        Log.i("info Permission", "verifyStoragePermission: "+permission+" "+ PackageManager.PERMISSION_GRANTED);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("INFO FAILURE PERMISSION", "verifyStoragePermission: Tidak mendapatkan Ijin");
            ActivityCompat.requestPermissions(editProfile,PERMISSIONS_STORAGE,1);
        }
    }

    @Override
    public void onUserLoginSuccess(User user, Context context) {
        // Set initial Value
        etUsername.setText(user.getUsername());
        etAlamat.setText(user.getAlamat());
        etEmail.setText(user.getEmail());
        etPostCode.setText(user.getKode_pos());
        if (user.getImage() == null) {
            Glide.with(context)
                    .load(RetrofitAPI.BASE_URL+"images/user/default.jpg")
                    .centerCrop()
                    .transform(new RoundedCorners(150))
                    .into(profileImage);
        }else{
            Glide.with(context)
                    .load(RetrofitAPI.BASE_URL+"images/user/"+user.getImage())
                    .centerCrop()
                    .transform(new RoundedCorners(150))
                    .into(profileImage);
        }
    }

    @Override
    public void onUserLoginFailure(String errorMessage) {

    }
}