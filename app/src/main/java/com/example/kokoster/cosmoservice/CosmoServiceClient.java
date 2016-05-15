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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kokoster on 13.05.16.
 */
public class CosmoServiceClient {
    public enum METER_DATAID {
        COLD_WATER("25046|-1"),
        HOT_WATER("25047|-1"),
        DAY_LIGHT("26678|-1"),
        NIGHT_LIGHT("26679|-1");

        private String dataid;

        METER_DATAID(String dataid) {
            this.dataid = dataid;
        }

        public String getDataid() {
            return dataid;
        }
    }

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
        };

        mRequestQueue.add(stringRequest);
    }

    public void getMeterHistory(final METER_DATAID meter, final MeterDataResponseListener responseListener) {
        mRequestQueue.start();
        String url = "http://cosmoservice.spb.ru/privoff/requestCntrDataHistory.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("success");
                        ArrayList<MonthData> allMonthsData = parseHTMLResponse(response);

                        if (allMonthsData.size() == 0) {
                            if (responseListener != null) {
                                responseListener.onError(meter, -1);
                            }
                        } else {
                            if (responseListener != null) {
                                responseListener.onSuccess(meter, allMonthsData);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("error");
                        if (responseListener != null && error != null) {
                            responseListener.onError(meter, error.networkResponse.statusCode);
                        }
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
    }

    public String getToken() {
        return mToken;
    }

    private ArrayList<MonthData> parseHTMLResponse(String response) {
        Document doc = Jsoup.parse(response);

        Elements data = doc.select("tr");

        ArrayList<MonthData> allMonthsData = new ArrayList<>();

        for (Element el : data) {
            Elements monthData = el.getElementsByTag("td");
            String date = monthData.get(0).text();
            String value = monthData.get(1).text();

            allMonthsData.add(new MonthData(date, value));
        }

        return allMonthsData;
    }
}
