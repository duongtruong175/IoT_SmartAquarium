package com.example.smartaquarium.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.smartaquarium.R;
import com.example.smartaquarium.models.UserModel;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    TextView tvRegister;
    Button btnLogin;

    boolean doubleBackToExitPressedOnce = false;
    int statusCode;
    String api_url_base;

    // key for shared preferences
    private static final String SHARED_PREFS_KEY = "shared_prefs";
    private static final String API_URL_BASE_KEY = "api_url_base";
    private static final String USER_KEY = "user";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedpreferences = getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        api_url_base = sharedpreferences.getString(API_URL_BASE_KEY, "");

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        tvRegister = findViewById(R.id.tv_register);
        btnLogin = findViewById(R.id.btn_login);

        // chuyen sang dang ky
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // kiem tra dang nhap
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // tat ban phim
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // try catch de tranh luc ban phim khong bat
                }
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                if (username.length() == 0) {
                    etUsername.requestFocus();
                    etUsername.setError("Dữ liệu không được để trống");
                } else if (password.length() == 0) {
                    etPassword.requestFocus();
                    etPassword.setError("Dữ liệu không được để trống");
                } else { // lay duoc du lieu
                    // hien thi ProgressBar khi goi API
                    findViewById(R.id.pb_loading).setVisibility(View.VISIBLE);

                    try {
                        // tao object gui di
                        JSONObject infoBody = new JSONObject();
                        infoBody.put("username", username);
                        infoBody.put("password", password);

                        String url = api_url_base + "/api/user/login";
                        // goi API dang nhap
                        //khoi tao RequestQueue
                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        //tao json object request voi method POST
                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, infoBody,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        if (statusCode == 200) {
                                            try {
                                                JSONArray results = response.getJSONArray("results");
                                                JSONObject temp = results.getJSONObject(0);
                                                // lay ket qua user
                                                int userID = temp.getInt("id");
                                                String userName = temp.getString("username");
                                                String pass = temp.getString("password");
                                                String name = temp.getString("name");
                                                String email = temp.getString("email");
                                                String gender = temp.getString("gender");
                                                String date = temp.getString("date");
                                                UserModel user = new UserModel(userID, userName, pass, name, email, gender, date);
                                                // luu user vao share preferences
                                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                                Gson gson = new Gson();
                                                String json = gson.toJson(user);
                                                editor.putString(USER_KEY, json);
                                                editor.apply();

                                                // chuyen huong trang
                                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        // an ProgressBar khi goi API hoan thanh
                                        findViewById(R.id.pb_loading).setVisibility(View.GONE);
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // an ProgressBar khi goi API hoan thanh
                                findViewById(R.id.pb_loading).setVisibility(View.GONE);
                                // response loi 400 401 403 404 (neu muon lay response -> giai ma error.networkResponse.data)
                                Toast.makeText(getApplicationContext(), "Tên đăng nhập hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            @Override
                            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                                statusCode = response.statusCode;
                                return super.parseNetworkResponse(response);
                            }

                            @Override
                            public Map getHeaders() throws AuthFailureError {
                                HashMap headers = new HashMap();
                                headers.put("Content-Type", "application/json");
                                return headers;
                            }
                        };
                        //them request vao RequestQueue , no se tu dong duoc chay
                        requestQueue.add(request);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // an 2 lan back lien tiep de thoat
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Nhấn BACK một lần nữa để thoát", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}