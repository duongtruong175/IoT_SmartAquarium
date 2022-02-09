package com.example.smartaquarium.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.example.smartaquarium.adapters.UserAquariumAdapter;
import com.example.smartaquarium.models.AquariumModel;
import com.example.smartaquarium.models.UserAquariumModel;
import com.example.smartaquarium.models.UserModel;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AquariumActivity extends AppCompatActivity {

    List<UserAquariumModel> userAquariums;
    ImageView addUserAquarium, ivBack;
    ListView listUserAquarium;
    View pbLoading;

    long mLastClickTimeListViewItem = 0; // bien tranh an nhieu lan listview item
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
        setContentView(R.layout.activity_aquarium);

        sharedpreferences = getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        api_url_base = sharedpreferences.getString(API_URL_BASE_KEY, "");
        Gson gson = new Gson();
        String json = sharedpreferences.getString(USER_KEY, "");
        user = gson.fromJson(json, UserModel.class);
        
        listUserAquarium = findViewById(R.id.list_user_aquarium);
        addUserAquarium = findViewById(R.id.add_user_aquarium);
        pbLoading = findViewById(R.id.pb_loading);
        ivBack = findViewById(R.id.iv_back);

        // lay du lieu cho list view
        getListUserAquarium();

        // chon phan tu trong list
        listUserAquarium.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                // tranh an nhieu lan listview item
                if (SystemClock.elapsedRealtime() - mLastClickTimeListViewItem < 1000){
                    return ;
                }
                mLastClickTimeListViewItem = SystemClock.elapsedRealtime();

                UserAquariumModel userAquarium = userAquariums.get(i);
                // lay thong tin tu be ca roi chuyen sang man hinh dieu khien
                // goi API lay ve danh sach be ca ma nguoi dung quan ly
                try {
                    // tao object gui di
                    JSONObject infoBody = new JSONObject();
                    infoBody.put("deviceId", userAquarium.getDeviceId());

                    String url = api_url_base + "/api/aquarium";
                    // goi API get
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
                                            int id = temp.getInt("id");
                                            String deviceType = temp.getString("device_type");
                                            String deviceId = temp.getString("device_id");
                                            AquariumModel aquarium = new AquariumModel(id, deviceType, deviceId);

                                            Intent intent = new Intent(AquariumActivity.this, ControlActivity.class);
                                            intent.putExtra("aquariumName", userAquarium.getAquariumName());
                                            intent.putExtra("aquarium", aquarium);
                                            startActivity(intent);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // response loi 400 401 403 404
                            Toast.makeText(getApplicationContext(), "Lấy thông tin thiết bị thất bại, xin thử lại", Toast.LENGTH_SHORT).show();
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
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // an vao them be ca
        addUserAquarium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog customDialog = new Dialog(AquariumActivity.this);
                customDialog.setContentView(R.layout.dialog_add_user_aquarium);
                EditText etAquariumName = customDialog.findViewById(R.id.et_aquarium_name);
                EditText etDeviceId = customDialog.findViewById(R.id.et_device_id);
                customDialog.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        customDialog.dismiss();
                    }
                });
                customDialog.findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(customDialog.getWindow().getCurrentFocus().getWindowToken(), 0);
                        } catch (Exception e) {
                            // try catch de tranh luc ban phim khong bat
                        }
                        String aquariumName = etAquariumName.getText().toString().trim();
                        String deviceId = etDeviceId.getText().toString().trim();
                        if (aquariumName.length() == 0) {
                            etAquariumName.requestFocus();
                            etAquariumName.setError("Dữ liệu không được để trống");
                        } else if (deviceId.length() == 0) {
                            etDeviceId.requestFocus();
                            etDeviceId.setError("Dữ liệu không được để trống");
                        } else {
                            // Kiem tra xem id be ca co hop le hay khong?
                            // goi API lay ve danh sach be ca ma nguoi dung quan ly
                            try {
                                // tao object gui di
                                JSONObject infoBody = new JSONObject();
                                infoBody.put("deviceId", deviceId);

                                String url = api_url_base + "/api/aquarium";
                                // goi API get
                                //khoi tao RequestQueue
                                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                //tao json object request voi method POST
                                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, infoBody,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                if (statusCode == 200) {
                                                    // ID thiet bi co ton tai
                                                    // goi API them be ca cho nguoi dung quan ly
                                                    try {
                                                        // tao object gui di
                                                        JSONObject infoBody2 = new JSONObject();
                                                        infoBody2.put("aquariumName", aquariumName);
                                                        infoBody2.put("userId", user.getId());
                                                        infoBody2.put("deviceId", deviceId);

                                                        String url2 = api_url_base + "/api/user/aquarium/add";
                                                        // goi API add
                                                        //khoi tao RequestQueue
                                                        RequestQueue requestQueue2 = Volley.newRequestQueue(getApplicationContext());
                                                        //tao json object request voi method POST
                                                        JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.POST, url2, infoBody2,
                                                                new Response.Listener<JSONObject>() {
                                                                    @Override
                                                                    public void onResponse(JSONObject response) {
                                                                        if (statusCode == 201) {
                                                                            etAquariumName.setText("");
                                                                            etDeviceId.setText("");
                                                                            Toast.makeText(AquariumActivity.this, "Thêm bể cá thành công", Toast.LENGTH_SHORT).show();
                                                                            customDialog.dismiss();
                                                                            getListUserAquarium();
                                                                        }
                                                                    }
                                                                }, new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                // response loi 400 401 403 404
                                                                Toast.makeText(getApplicationContext(), "Bể cá đã tồn tại, xin thử lại", Toast.LENGTH_SHORT).show();
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
                                                        requestQueue2.add(request2);

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // response loi 400 401 403 404
                                        Toast.makeText(getApplicationContext(), "ID thiết bị không tồn tại, xin kiểm tra lại", Toast.LENGTH_SHORT).show();
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
                customDialog.setCanceledOnTouchOutside(false);
                customDialog.show();
            }
        });
    }

    public void getListUserAquarium() {
        pbLoading.setVisibility(View.VISIBLE);
        // goi API lay ve danh sach be ca ma nguoi dung quan ly
        try {
            // tao object gui di
            JSONObject infoBody = new JSONObject();
            infoBody.put("userId", user.getId());

            String url = api_url_base + "/api/user/aquarium";
            // goi API get
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
                                    userAquariums = new ArrayList<>();
                                    for (int i = 0; i < results.length(); i++) {
                                        JSONObject temp = results.getJSONObject(i);
                                        int id = temp.getInt("id");
                                        String aquariumName = temp.getString("aquarium_name");
                                        int userId = temp.getInt("user_id");
                                        String deviceId = temp.getString("device_id");
                                        userAquariums.add(new UserAquariumModel(id, aquariumName, userId, deviceId));
                                    }
                                    // set adapter
                                    UserAquariumAdapter adapter = new UserAquariumAdapter(userAquariums, getApplicationContext());
                                    listUserAquarium.setAdapter(adapter);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            // an ProgressBar khi goi API hoan thanh
                            pbLoading.setVisibility(View.GONE);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // an ProgressBar khi goi API hoan thanh
                    pbLoading.setVisibility(View.GONE);
                    // response loi 400 401 403 404
                    Toast.makeText(getApplicationContext(), "Có lỗi xảy ra, xin thử lại", Toast.LENGTH_SHORT).show();
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