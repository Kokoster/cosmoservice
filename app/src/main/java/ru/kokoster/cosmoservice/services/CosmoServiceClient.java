package ru.kokoster.cosmoservice.services;

import android.util.Log;

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
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.kokoster.cosmoservice.model.MonthData;

/**
 * Created by kokoster on 13.05.16.
 */

// TODO: разобраться с Parcelable
public class CosmoServiceClient implements Serializable {
    private static final String TAG = "CosmoServiceClient";

    public enum METER_DATAID {
        COLD_WATER("25046"),
        HOT_WATER("25047"),
        DAY_LIGHT("26678"),
        NIGHT_LIGHT("26679");

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

    public CosmoServiceClient(File cacheDir) {
        Cache cache = new DiskBasedCache(cacheDir, 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);

        // if something crashes try to find bug here :)
        mRequestQueue.start();
    }

    public CosmoServiceClient(File cacheDir, String token) {
        this(cacheDir);

        this.mToken = token;
    }

    public void checkToken(final String token, final ResponseListener responseListener) {
        String url = "http://cosmoservice.spb.ru/privoff/office.php";
        final String tokenNotFoundString = "Token is not found";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains(tokenNotFoundString)) {
                    responseListener.onError(-1);
                } else {
                    responseListener.onSuccess();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    responseListener.onError(error.networkResponse.statusCode);
                } else {
                    responseListener.onError(-1);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                Log.d(TAG, "getHeaders. Token = " + token);
                headers.put("Cookie", "token=" + token);

                return headers;
            }
        };

        mRequestQueue.add(stringRequest);
    }

    public void login(String username, String password, ResponseListener responseListener) {
        login(username, password, responseListener, 0);
    }

    private void login(final String username, final String password, final ResponseListener responseListener, final int failsCount) {
        String url = "http://cosmoservice.spb.ru/privoff/OfficeHelper.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mToken = parseResponse(response);

                        if (mToken == null) {
                            if (responseListener != null) {
                                responseListener.onError(-1);
                            }

                            return;
                        }

                        getMeterHistory(CosmoServiceClient.METER_DATAID.COLD_WATER, new MeterDataHistoryResponseListener() {
                            @Override
                            public void onSuccess(METER_DATAID dataId, ArrayList<MonthData> allMonthsData) {
                                if (responseListener != null) {
                                    responseListener.onSuccess();
                                }
                            }

                            @Override
                            public void onError(METER_DATAID dataId, int errorCode) {
                                if (failsCount < 3)  {
                                    Log.d(TAG, "fails count = " + Integer.toString(failsCount));
                                    login(username, password, responseListener, failsCount + 1);
                                } else {
                                    if (responseListener != null) {
                                        responseListener.onError(errorCode);
                                    }
                                }
                            }
                        });
                    }
                 },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (responseListener != null) {
                            responseListener.onError(error.networkResponse != null ?
                                                     error.networkResponse.statusCode : -1);
                        }
                    }
                }
        ) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("method", "login");
                params.put("tsgcode", "s406");
                params.put("login", username);
                params.put("password", password);

                return params;
            }
        };

        mRequestQueue.add(stringRequest);
    }

    public void getMeterHistory(final METER_DATAID meter, final MeterDataHistoryResponseListener responseListener) {
        mRequestQueue.start();
        String url = "http://cosmoservice.spb.ru/privoff/requestCntrDataHistory.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "getMeterHistory. Succeeded to get data");
                        ArrayList<MonthData> allMonthsData = parseMeterHistoryResponse(response);

                        if (allMonthsData.size() == 0) {
                            Log.d(TAG, "getMeterHistory. data size = 0");
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
                        if (responseListener != null) {
                            responseListener.onError(meter, error.networkResponse != null ?
                                    error.networkResponse.statusCode : -1);
                        }
                    }
                }
        ) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("dataId", meter.getDataid() + "|-1");

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                Log.d(TAG, "getHeaders. Token = " + mToken);
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

    private ArrayList<MonthData> parseMeterHistoryResponse(String response) {
        Document doc = Jsoup.parse(response);

        Elements data = doc.select("tr");

        ArrayList<MonthData> allMonthsData = new ArrayList<>();

        for (Element el : data) {
            Elements monthData = el.getElementsByTag("td");
            String date = monthData.get(0).text();
            String valueString = monthData.get(1).text();
            valueString = valueString.split(" ")[0];

            try {
                allMonthsData.add(new MonthData(date, new BigDecimal(valueString.replaceAll(",", ""))));
            } catch(NumberFormatException e) {
                Log.d(TAG, "Number format exception. String is " + valueString);
            }
        }

        return allMonthsData;
    }

    private void saveCurrentMetersData(final HashMap<METER_DATAID, String> meterIdMap,
                                       final HashMap<METER_DATAID, BigDecimal> metersDataMap,
                                       final SaveDataListener listener) {
        mRequestQueue.start();
        String url = "http://cosmoservice.spb.ru/privoff/obr.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.onSuccess();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (listener != null) {
                    listener.onError(error.networkResponse != null ?
                            error.networkResponse.statusCode : -1);
                }
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String, String> params = new HashMap<>();
                final String sumpostStr = "sumpost";
                final String markSaveStr = "markSave";
                final String markDeleteStr = "markDelete";
                final String counterDataIdStr = "CounterDataId";

                String meterNumberStr = "";
                for (METER_DATAID key : metersDataMap.keySet()) {
                    meterNumberStr = detectMeterNumber(key);

                    BigDecimal value = metersDataMap.get(key);

//                    TODO: заменить BigDecimal(0) на null
                    if (value.equals(new BigDecimal(0))) {
                        params.put(sumpostStr + meterNumberStr, "");
                        params.put(markSaveStr + meterNumberStr, "0");
                        params.put(markDeleteStr + meterNumberStr, "1");
                    } else {
                        params.put(sumpostStr + meterNumberStr, value.toString());
                        params.put(markSaveStr + meterNumberStr, "1");
                        params.put(markDeleteStr + meterNumberStr, "0");
                    }

                    params.put(counterDataIdStr + meterNumberStr, meterIdMap.get(key));
                }

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                Log.d(TAG, "getHeaders. Token = " + mToken);
                headers.put("Cookie", "token=" + mToken);

                return headers;
            }
        };

        mRequestQueue.add(stringRequest);
    }

    private String detectMeterNumber(METER_DATAID key) {
        switch (key) {
            case COLD_WATER:
                return "0";

            case HOT_WATER:
                return "1";

            case DAY_LIGHT:
                return "2";

            case NIGHT_LIGHT:
                return "3";
        }

        return null;
    }

    public void saveCurrentMetersData(final HashMap<METER_DATAID, BigDecimal> metersData, final SaveDataListener listener) {
        retrieveMeterDataId(new MeterDataIdResponseListener() {
            @Override
            public void onSuccess(HashMap<METER_DATAID, String> meterId) {
                saveCurrentMetersData(meterId, metersData, listener);
            }

            @Override
            public void onError(int errorCode) {

            }
        });
    }

    private HashMap<METER_DATAID, BigDecimal> extractMetersCurrentData(String response) {
        Document doc = Jsoup.parse(response);

        HashMap<METER_DATAID, BigDecimal> currentMetersData = new HashMap<>();

//        TODO: merge
        Elements data = doc.getElementsByClass("sumpost0");
        for (Element el : data) {
            String valueStr = el.attr("value");
            BigDecimal value = new BigDecimal(valueStr.replaceAll(",", "")).setScale(0, BigDecimal.ROUND_HALF_UP);
            currentMetersData.put(METER_DATAID.COLD_WATER,value);
        }

        data = doc.getElementsByClass("sumpost1");
        for (Element el : data) {
            String valueStr = el.attr("value");
            BigDecimal value = new BigDecimal(valueStr.replaceAll(",", "")).setScale(0, BigDecimal.ROUND_HALF_UP);
            currentMetersData.put(METER_DATAID.HOT_WATER,value);
        }

        data = doc.getElementsByClass("sumpost2");
        for (Element el : data) {
            String valueStr = el.attr("value");
            BigDecimal value = new BigDecimal(valueStr.replaceAll(",", "")).setScale(0, BigDecimal.ROUND_HALF_UP);
            currentMetersData.put(METER_DATAID.DAY_LIGHT,value);
        }

        data = doc.getElementsByClass("sumpost3");
        for (Element el : data) {
            String valueStr = el.attr("value");
            BigDecimal value = new BigDecimal(valueStr.replaceAll(",", "")).setScale(0, BigDecimal.ROUND_HALF_UP);
            currentMetersData.put(METER_DATAID.NIGHT_LIGHT,value);
        }

        return currentMetersData;
    }

    private String extractCurrentMonth(String response) {
        Document doc = Jsoup.parse(response);
        Elements data = doc.getElementsByClass("zzag");
        for (Element el : data) {
            // TODO: split -> 4th word
            if (el.text().contains("Ввод показаний за")) {
                return el.text().substring(18);
            }
        }

        return null;
    }

    public void retrieveCurrentMonth(final MonthRequestListener listener) {
        String url = "http://cosmoservice.spb.ru/privoff/office.php";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.onSuccess(extractCurrentMonth(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    listener.onError(error.networkResponse.statusCode);
                } else {
                    listener.onError(-1);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                Log.d(TAG, "getHeaders. Token = " + mToken);
                headers.put("Cookie", "token=" + mToken);

                return headers;
            }
        };

        mRequestQueue.add(stringRequest);
    }

    public void retrieveCurrentMetersData(final MetersCurrentDataListener listener) {
//        mRequestQueue.start();
        String url = "http://cosmoservice.spb.ru/privoff/office.php";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.onSuccess(extractMetersCurrentData(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error.networkResponse.statusCode);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                Log.d(TAG, "getHeaders. Token = " + mToken);
                headers.put("Cookie", "token=" + mToken);

                return headers;
            }
        };

        mRequestQueue.add(stringRequest);
    }

    private void retrieveMeterDataId(final MeterDataIdResponseListener listener) {
        mRequestQueue.start();
        String url = "http://cosmoservice.spb.ru/privoff/office.php";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.onSuccess(extractMeterDataId(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error.networkResponse.statusCode);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                Log.d(TAG, "getHeaders. Token = " + mToken);
                headers.put("Cookie", "token=" + mToken);

                return headers;
            }
        };


        mRequestQueue.add(stringRequest);
    }

    private HashMap<METER_DATAID, String> extractMeterDataId(String response) {
        Document doc = Jsoup.parse(response);

        HashMap<METER_DATAID, String> meterDataId = new HashMap<>();

        Elements data = doc.getElementsByClass("CounterDataId0");
        for (Element el : data) {
            meterDataId.put(METER_DATAID.COLD_WATER, el.attr("value"));
        }

        data = doc.getElementsByClass("CounterDataId1");
        for (Element el : data) {
            meterDataId.put(METER_DATAID.HOT_WATER, el.attr("value"));
        }

        data = doc.getElementsByClass("CounterDataId2");
        for (Element el : data) {
            meterDataId.put(METER_DATAID.DAY_LIGHT, el.attr("value"));
        }

        data = doc.getElementsByClass("CounterDataId3");
        for (Element el : data) {
            meterDataId.put(METER_DATAID.NIGHT_LIGHT, el.attr("value"));
        }

        return meterDataId;
    }

    private interface MeterDataIdResponseListener {
        public void onSuccess(HashMap<METER_DATAID, String> meterId);
        public void onError(int errorCode);
    }
}
