package com.example.codemaven3015.sampleapplogin;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TableUpdate extends Activity {
    DataBaseHealper myDB;
    RequestQueue requestQueueLogin;
    isNetworkAvaliable nb;
    String status;
    Context context;
    String url ;//url Launguage

    public TableUpdate(Context context ) {
        this.context = context;
    }

    public void getSurveyListFromAPI(){
            //service call
            url = "http://104.238.125.119/~codedev/api/surveylist.php";
            if (nb.isConnected()) {
                StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
                        url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {

                                    JSONObject obj = new JSONObject(response);
                                    Log.e("my app", response);
                                    status = obj.getString("status");
                                    if(status.equals("Success")){
                                        JSONArray jArray = obj.getJSONArray("data");
                                        myDB.insertDataSurveyList(jArray);
                                    }else{
                                        //showMessage("Error",obj.getString("message"));

                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.e("my app", "could not be parse");

                                }


                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

                    @Override
                    public String getBodyContentType() {
                        return "application/x-www-form-urlencoded; charset=UTF-8";
                    }

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("surveylist", "1");
                        return params;
                    }

                };

                requestQueueLogin.add(jsonObjRequest);


            } else {

            }
    }


    public void getLaunguageListFromAPI(){
        //if(myDB.ifSurveyListIsEmpty()) {
            //service call
            if (nb.isConnected()) {
                StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
                        url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //mProgressView.setVisibility(View.INVISIBLE);

                                try {

                                    JSONObject obj = new JSONObject(response);
                                    Log.e("my app", response);
                                    status = obj.getString("status");
                                    if(status.equals("Success")){
                                        JSONArray jArray = obj.getJSONArray("data");
                                        myDB.insertDataLanguage(jArray);
                                        //update language db;
                                    }else{
                                        //showMessage("Error",obj.getString("message"));

                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.e("my app", "could not be parse");
                                }

                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

                    @Override
                    public String getBodyContentType() {
                        return "application/x-www-form-urlencoded; charset=UTF-8";
                    }

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("surveylist", "1");
                        return params;
                    }

                };

                requestQueueLogin.add(jsonObjRequest);


            } else {
                //showMessage("Error","no internetConnection");

            }
    }



}


