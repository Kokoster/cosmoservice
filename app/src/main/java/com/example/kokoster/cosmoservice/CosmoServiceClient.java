package com.example.kokoster.cosmoservice;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;

import java.io.File;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kokoster on 13.05.16.
 */
public class CosmoServiceClient {
    public enum METER_DATA {
        COLD_WATER("25046|-1"), HOT_WATER("25047|-1"), DAY_LIGHT("26678|-1"), NIGHT_LIGHT("26679|-1");

        private String dataid;

        METER_DATA(String dataid) {
            this.dataid = dataid;
        }

        public String getDataid() {
            return dataid;
        }
    };

    private RequestQueue mRequestQueue;
    private String mToken;

    // TODO: Activity or cacheDir ??
    public CosmoServiceClient(File cacheDir) {
        Cache cache = new DiskBasedCache(cacheDir, 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
    }

    public CosmoServiceClient(File cacheDir, String token) {
        Cache cache = new DiskBasedCache(cacheDir, 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);

        this.mToken = token;
    }

    private String parseResponse(String response) {
        String[] params = response.split("\\|", -1);

        try {
            Integer errorCode = Integer.parseInt(params[0]);

            if (errorCode != 0) {
                return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }

        return params[1];
    }

    public void login(String username, String password, final ResponseListener responseListener) {
        mRequestQueue.start();
        String url = "http://cosmoservice.spb.ru/privoff/OfficeHelper.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("success");
                        mToken = parseResponse(response);

                        if (mToken == null) {
                            if (responseListener != null) {
                                responseListener.onError(-1);
                            }
                        } else {
                            if (responseListener != null) {
                                responseListener.onSuccess();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("error");
                        if (responseListener != null && error != null) {
                            responseListener.onError(error.networkResponse.statusCode);
                        }
                    }
                }
        ) {
            @Override
            protected Map<String,String> getParams(){
                System.out.println("In getParams");
                Map<String,String> params = new HashMap<>();
                params.put("login", "172");
                params.put("password", "tM3kpRUX7G2b");
                params.put("tsgcode", "s406");
                params.put("method", "login");

                return params;
            }

//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String,String> params = new HashMap<>();
//                params.put("Cookie", "token=" + mToken);
//                return params;
//            }
        };

        mRequestQueue.add(stringRequest);
    }

    public String getMeterHistory(final METER_DATA meter) {
        mRequestQueue.start();
        String url = "http://cosmoservice.spb.ru/privoff/requestCntrDataHistory.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("success");
                        System.out.println(response);

//                        if (mToken == null) {
//                            if (responseListener != null) {
//                                responseListener.onError(-1);
//                            }
//                        } else {
//                            if (responseListener != null) {
//                                responseListener.onSuccess();
//                            }
//                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("error");
//                        if (responseListener != null && error != null) {
//                            responseListener.onError(error.networkResponse.statusCode);
//                        }
                    }
                }
        ) {
            @Override
            protected Map<String,String> getParams(){
                System.out.println("In getParams");
                Map<String,String> params = new HashMap<>();
                System.out.println(meter.getDataid());
                params.put("dataId", meter.getDataid());

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                System.out.println("In MainActivity in headers token = " + mToken);
//                headers.put("Cookie", "tsg=s406; token=" + mToken);
                headers.put("Cookie", "token=" + mToken);

                return headers;
            }
        };

        mRequestQueue.add(stringRequest);

        return null;
    }

    public String getToken() {
        return mToken;
    }
}
