package com.example.smartaquarium.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.example.smartaquarium.models.UserAquariumModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAquariumAdapter extends BaseAdapter {

    List<UserAquariumModel> userAquariums;
    Context context;
    int statusCode;

    // key for shared preferences
    private static final String SHARED_PREFS_KEY = "shared_prefs";
    private static final String API_URL_BASE_KEY = "api_url_base";

    public UserAquariumAdapter(List<UserAquariumModel> userAquariums, Context context) {
        this.userAquariums = userAquariums;
        this.context = context;
    }

    @Override
    public int getCount() {
        return userAquariums.size();
    }

    @Override
    public Object getItem(int position) {
        return userAquariums.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null)
        {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_aquarium_item , parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvAquariumName = convertView.findViewById(R.id.tv_aquarium_name);
            viewHolder.tvDeviceId = convertView.findViewById(R.id.tv_device_id);
            viewHolder.tvDelete = convertView.findViewById(R.id.tv_delete);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        UserAquariumModel userAquarium = userAquariums.get(position);
        viewHolder.tvAquariumName.setText(userAquarium.getAquariumName());
        viewHolder.tvDeviceId.setText(userAquarium.getDeviceId());
        viewHolder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog customDialog = new Dialog(parent.getContext());
                customDialog.setContentView(R.layout.dialog_confirm_delete);
                customDialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        customDialog.dismiss();
                    }
                });
                customDialog.findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            // tao object gui di
                            JSONObject infoBody = new JSONObject();
                            infoBody.put("id", userAquarium.getId());
                            SharedPreferences sharedpreferences = context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
                            String api_url_base = sharedpreferences.getString(API_URL_BASE_KEY, "");
                            String url = api_url_base + "/api/user/aquarium/delete";
                            // goi API get
                            //khoi tao RequestQueue
                            RequestQueue requestQueue = Volley.newRequestQueue(context);
                            //tao json object request voi method POST
                            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, infoBody,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            if (statusCode == 200) {
                                                userAquariums.remove(position);
                                                notifyDataSetChanged();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(context, "Xóa bể cá thất bại, xin thử lại", Toast.LENGTH_LONG).show();
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
                        customDialog.dismiss();
                    }
                });
                customDialog.setCanceledOnTouchOutside(true);
                customDialog.show();
            }
        });

        return convertView;
    }

    class ViewHolder {
        TextView tvAquariumName;
        TextView tvDeviceId;
        TextView tvDelete;
    }
}
