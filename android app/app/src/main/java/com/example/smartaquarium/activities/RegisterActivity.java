package com.example.smartaquarium.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etUsername, etPassword, etEmail;
    TextView tvDate;
    Button btnRegister;
    RadioGroup rgGender;
    RadioButton rbSelected;
    TextView tvLogin;

    Calendar c;
    DatePickerDialog dpd;

    boolean doubleBackToExitPressedOnce = false;
    int statusCode;
    String api_url_base;

    // key for shared preferences
    private static final String SHARED_PREFS_KEY = "shared_prefs";
    private static final String API_URL_BASE_KEY = "api_url_base";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sharedpreferences = getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        api_url_base = sharedpreferences.getString(API_URL_BASE_KEY, "");

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        tvDate = findViewById(R.id.tv_date);
        rgGender = findViewById(R.id.rg_gender);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // try catch de tranh luc ban phim khong bat
                }
                c = Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_MONTH);
                int month = c.get(Calendar.MONTH);
                int year = c.get(Calendar.YEAR);

                dpd = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int nYear, int nMonth, int nDay) {
                        c.set(nYear, nMonth, nDay);
                        Date date = c.getTime();
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        tvDate.setText(formatter.format(date));
                    }
                }, year, month, day);
                dpd.show();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // dang ky
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // try catch de tranh luc ban phim khong bat
                }
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String name = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String date = tvDate.getText().toString().trim();
                // lay radio button duoc chon
                int selectedId = rgGender.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                rbSelected = findViewById(selectedId);
                String gender = rbSelected.getText().toString().trim();
                if (username.length() == 0) {
                    etUsername.requestFocus();
                    etUsername.setError("Dữ liệu không được để trống");
                } else if (password.length() == 0) {
                    etPassword.requestFocus();
                    etPassword.setError("Dữ liệu không được để trống");
                } else if (name.length() == 0) {
                    etName.requestFocus();
                    etName.setError("Dữ liệu không được để trống");
                } else if (email.length() == 0) {
                    etEmail.requestFocus();
                    etEmail.setError("Dữ liệu không được để trống");
                } else if (date.length() == 0) {
                    tvDate.requestFocus();
                    tvDate.setError("Dữ liệu không được để trống");
                } else {
                    // view ProgressBar when loading API
                    findViewById(R.id.pb_loading).setVisibility(View.VISIBLE);

                    try {
                        // tao object gui di
                        JSONObject infoBody = new JSONObject();
                        infoBody.put("username", username);
                        infoBody.put("password", password);
                        infoBody.put("name", name);
                        infoBody.put("email", email);
                        infoBody.put("gender", gender);
                        infoBody.put("date", date);

                        String url = api_url_base + "/api/user/register";
                        // goi API dang ky
                        //khoi tao RequestQueue
                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        //tao json object request voi method POST
                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, infoBody,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        if (statusCode == 201) {
                                            etUsername.setText("");
                                            etPassword.setText("");
                                            etName.setText("");
                                            etEmail.setText("");
                                            tvDate.setText("");
                                            rgGender.check(R.id.radio_male);
                                            Toast.makeText(getApplicationContext(), "Đăng ký tài khoản thành công", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getApplicationContext(), "Tên tài khoản đã tồn tại hoặc xảy ra lỗi, xin thử lại", Toast.LENGTH_SHORT).show();
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