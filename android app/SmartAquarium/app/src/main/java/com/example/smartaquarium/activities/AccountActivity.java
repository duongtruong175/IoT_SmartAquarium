package com.example.smartaquarium.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends AppCompatActivity {

    TextView tvUsername, tvName, tvEmail, tvGender, tvDate, tvChangePassword, tvLogout;
    ImageView ivBack;
    ImageButton btnUpdate;
    View loading;

    int statusCode;
    String api_url_base;
    UserModel user;

    // key for shared preferences
    private static final String SHARED_PREFS_KEY = "shared_prefs";
    private static final String API_URL_BASE_KEY = "api_url_base";
    private static final String USER_KEY = "user";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        sharedpreferences = getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        api_url_base = sharedpreferences.getString(API_URL_BASE_KEY, "");
        Gson gson = new Gson();
        String json = sharedpreferences.getString(USER_KEY, "");
        user = gson.fromJson(json, UserModel.class);

        tvUsername = findViewById(R.id.tv_value_username);
        tvName = findViewById(R.id.tv_value_name);
        tvEmail = findViewById(R.id.tv_value_email);
        tvGender = findViewById(R.id.tv_value_gender);
        tvDate = findViewById(R.id.tv_value_date);
        tvChangePassword = findViewById(R.id.tv_value_password);
        tvLogout = findViewById(R.id.tv_value_logout);
        loading = findViewById(R.id.pb_loading);

        ivBack = findViewById(R.id.iv_back);
        btnUpdate = findViewById(R.id.btn_update);
        btnUpdate.setEnabled(false);

        // khoi tao du lieu cho view
        tvUsername.setText(user.getUsername());
        tvName.setText(user.getName());
        tvEmail.setText(user.getEmail());
        tvGender.setText(user.getGender());
        tvDate.setText(user.getDate());

        // thay doi ten
        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeInformationDialog(tvName, "name");
            }
        });

        // thay doi email
        tvEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeInformationDialog(tvEmail, "email");
            }
        });

        // thay doi gioi tinh
        tvGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeGenderDialog(tvGender);
            }
        });

        // thay doi date
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDateDialog(tvDate);
            }
        });

        // thay doi mat khau
        tvChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePasswordDialog();
            }
        });

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // xoa user
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.remove(USER_KEY);
                editor.apply();

                Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnUpdate.isEnabled()) {
                    final Dialog customDialog = new Dialog(AccountActivity.this);
                    customDialog.setContentView(R.layout.dialog_confirm_back);
                    customDialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            customDialog.dismiss();
                        }
                    });
                    customDialog.findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            customDialog.dismiss();
                            finish();
                        }
                    });
                    customDialog.setCanceledOnTouchOutside(true);
                    customDialog.show();
                } else {
                    finish();
                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // update lại du lieu user
                updateInformation();
            }
        });
    }

    // ham show dialog thay doi thong tin
    public void changeInformationDialog(final TextView tv, String label) {
        final Dialog customDialog = new Dialog(AccountActivity.this);
        customDialog.setContentView(R.layout.dialog_change_information);
        EditText etNewValue = customDialog.findViewById(R.id.et_new_value);
        customDialog.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });
        customDialog.findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(customDialog.getWindow().getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // try catch de tranh luc ban phim khong bat
                }
                String value = etNewValue.getText().toString().trim();
                if (value.length() == 0) {
                    etNewValue.requestFocus();
                    etNewValue.setError("Dữ liệu không được để trống");
                } else {
                    tv.setText(value);
                    if ((label.equals("name") && !value.equals(user.getName())) || (label.equals("email") && !value.equals(user.getEmail()))) {
                        btnUpdate.setEnabled(true);
                    } else {
                        btnUpdate.setEnabled(false);
                    }
                    customDialog.dismiss();
                }
            }
        });
        customDialog.setCanceledOnTouchOutside(true);
        customDialog.show();
    }

    // ham show dialog thay doi gioi tinh
    public void changeGenderDialog(final TextView tv) {
        final Dialog customDialog = new Dialog(AccountActivity.this);
        customDialog.setContentView(R.layout.dialog_change_gender);
        customDialog.findViewById(R.id.txt_male).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText("Nam");
                if (!user.getGender().equals("Nam")) {
                    btnUpdate.setEnabled(true);
                } else {
                    btnUpdate.setEnabled(false);
                }
                customDialog.dismiss();
            }
        });
        customDialog.findViewById(R.id.txt_female).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText("Nữ");
                if (!user.getGender().equals("Nữ")) {
                    btnUpdate.setEnabled(true);
                } else {
                    btnUpdate.setEnabled(false);
                }
                customDialog.dismiss();
            }
        });
        customDialog.findViewById(R.id.txt_other).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText("Khác");
                if (!user.getGender().equals("Khác")) {
                    btnUpdate.setEnabled(true);
                } else {
                    btnUpdate.setEnabled(false);
                }
                customDialog.dismiss();
            }
        });
        customDialog.setCanceledOnTouchOutside(true);
        customDialog.show();
    }

    // ham show dialog thay doi date
    public void changeDateDialog(final TextView tv) {
        String date = tv.getText().toString().trim();
        String[] parts = date.split("/");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]) - 1;
        int year = Integer.parseInt(parts[2]);

        DatePickerDialog dpd = new DatePickerDialog(AccountActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int nYear, int nMonth, int nDay) {
                Calendar c = Calendar.getInstance();
                c.set(nYear, nMonth, nDay);
                Date date = c.getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                tv.setText(formatter.format(date));
                if (!user.getDate().equals(tv.getText())) {
                    btnUpdate.setEnabled(true);
                } else {
                    btnUpdate.setEnabled(false);
                }
            }
        }, year, month, day);
        dpd.show();
    }

    // ham show dialog thay doi mat khau
    public void changePasswordDialog() {
        final Dialog customDialog = new Dialog(AccountActivity.this);
        customDialog.setContentView(R.layout.dialog_change_password);
        final EditText etCurrentPassword = customDialog.findViewById(R.id.et_current_password);
        final EditText etNewPassword = customDialog.findViewById(R.id.et_new_password);
        final EditText etConformPassword = customDialog.findViewById(R.id.et_conform_password);
        customDialog.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });
        customDialog.findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(customDialog.getWindow().getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // try catch de tranh luc ban phim khong bat
                }
                String currentPassword = etCurrentPassword.getText().toString().trim();
                final String newPassword = etNewPassword.getText().toString().trim();
                String conformPassword = etConformPassword.getText().toString().trim();
                if (currentPassword.length() == 0) {
                    etCurrentPassword.requestFocus();
                    etCurrentPassword.setError("Dữ liệu không được để trống");
                } else if (newPassword.length() == 0) {
                    etNewPassword.requestFocus();
                    etNewPassword.setError("Dữ liệu không được để trống");
                } else if (conformPassword.length() == 0) {
                    etConformPassword.requestFocus();
                    etConformPassword.setError("Dữ liệu không được để trống");
                } else if (!currentPassword.equals(user.getPassword())) {
                    etCurrentPassword.requestFocus();
                    etCurrentPassword.setError("Mật khẩu không chính xác");
                } else if (!newPassword.equals(conformPassword)) {
                    etConformPassword.requestFocus();
                    etConformPassword.setError("Mật khẩu không khớp");
                } else if (newPassword.equals(currentPassword)) {
                    etNewPassword.requestFocus();
                    etNewPassword.setError("Mật khẩu chưa đổi");
                } else {
                    // goi API doi mat khau
                    try {
                        // tao object gui di
                        JSONObject infoBody = new JSONObject();
                        infoBody.put("newPassword", newPassword);
                        infoBody.put("userId", user.getId());

                        String url = api_url_base + "/api/user/updatePassword";
                        // goi API update
                        //khoi tao RequestQueue
                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        //tao json object request voi method POST
                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, infoBody,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        if (statusCode == 200) {
                                            // cap nhap lai thong tin user
                                            user.setPassword(newPassword);
                                            // luu user vao share preferences
                                            SharedPreferences.Editor editor = sharedpreferences.edit();
                                            Gson gson = new Gson();
                                            String json = gson.toJson(user);
                                            editor.putString(USER_KEY, json);
                                            editor.apply();
                                            Toast.makeText(getApplicationContext(), "Thay đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                            customDialog.dismiss();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // response loi 400 401 403 404
                                Toast.makeText(getApplicationContext(), "Thay đổi mặt khẩu thất bại, xin thử lại", Toast.LENGTH_SHORT).show();
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
        customDialog.setCanceledOnTouchOutside(true);
        customDialog.show();
    }

    // ham thay doi thong tin nguoi dung
    public void updateInformation() {
        String name = tvName.getText().toString().trim();
        String email = tvEmail.getText().toString().trim();
        String gender = tvGender.getText().toString().trim();
        String date = tvDate.getText().toString().trim();

        if (user != null && name != null && email != null && gender != null && date != null) {
            // view ProgressBar when loading API
            loading.setVisibility(View.VISIBLE);

            try {
                // tao object gui di
                JSONObject infoBody = new JSONObject();
                infoBody.put("name", name);
                infoBody.put("email", email);
                infoBody.put("gender", gender);
                infoBody.put("date", date);
                infoBody.put("userId", user.getId());

                String url = api_url_base + "/api/user/update";
                // goi API update
                //khoi tao RequestQueue
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                //tao json object request voi method POST
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, infoBody,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if (statusCode == 200) {
                                    // cap nhap lai thong tin user
                                    user.setName(name);
                                    user.setEmail(email);
                                    user.setGender(gender);
                                    user.setDate(date);
                                    // luu user vao share preferences
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    Gson gson = new Gson();
                                    String json = gson.toJson(user);
                                    editor.putString(USER_KEY, json);
                                    editor.apply();
                                    Toast.makeText(getApplicationContext(), "Thay đổi thông tin tài khoản thành công", Toast.LENGTH_SHORT).show();
                                } else {
                                    // neu loi thi sua lai view
                                    tvName.setText(user.getName());
                                    tvEmail.setText(user.getEmail());
                                    tvGender.setText(user.getGender());
                                    tvDate.setText(user.getDate());
                                    Toast.makeText(getApplicationContext(), "Thay đổi thông tin tài khoản thất bại, xin thử lại", Toast.LENGTH_SHORT).show();
                                }
                                btnUpdate.setEnabled(false);
                                // an ProgressBar khi goi API hoan thanh
                                loading.setVisibility(View.GONE);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // an ProgressBar khi goi API hoan thanh
                        loading.setVisibility(View.GONE);
                        // neu loi thi sua lai view
                        tvName.setText(user.getName());
                        tvEmail.setText(user.getEmail());
                        tvGender.setText(user.getGender());
                        tvDate.setText(user.getDate());
                        btnUpdate.setEnabled(false);
                        // response loi 400 401 403 404
                        Toast.makeText(getApplicationContext(), "Thay đổi thông tin tài khoản thất bại, xin thử lại", Toast.LENGTH_SHORT).show();
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
}