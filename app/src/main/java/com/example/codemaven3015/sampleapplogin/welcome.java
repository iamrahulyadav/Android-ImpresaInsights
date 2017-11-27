package com.example.codemaven3015.sampleapplogin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class welcome extends AppCompatActivity {

    TableUpdate tableUpdate;
    isNetworkAvaliable nb;
    DataBaseHealper myDB;
    RequestQueue requestQueueLogin;
    GlobalVariables gbl;
    String status;
    int noOfServiceCall = 0;
    ProgressDialog dialog,dialog1;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    public void openSurveyList(View v){
        Intent i = new Intent(welcome.this , SurveyList.class);
        //i.putExtra("CLIENT", "new");
        gbl.setClientId("new");
        startActivity(i);

    }
    public void openEnterPID(View v){
        Intent i = new Intent(welcome.this , EnterPID.class);
        startActivity(i);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myDB = new DataBaseHealper(this);
        nb = new isNetworkAvaliable(this);
        tableUpdate = new TableUpdate(this);
        requestQueueLogin = Volley.newRequestQueue(this);
        gbl = (GlobalVariables)getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //Toolbar parent= (Toolbar)(R.layout.action_bar).getParent();
        final TextView helloTextView = (TextView) findViewById(R.id.action_text);
        //final ImageView imageView= (ImageView)findViewById(R.id.imageButton) ;
        helloTextView.setText("Welcome");
        if((getIntent().getStringExtra("from").equals("main"))) {
            getQuestionListFromAPI();
            try {
                uploadAnswerFromDB();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //updateLanguageAndSurveyListDB();

    }
    public void uploadAnswerFromDB() throws JSONException {
        if(myDB.checkAnswerToupdate()){
            //
            JSONArray answerAllUser = new JSONArray();
            Cursor newClient = myDB.selectNewDistictClient();
            newClient.moveToNext();
            do{
                Log.e("CLIENT ID",newClient.getString(0));
                Cursor ans = myDB.getAnswerFromDB(newClient.getString(0));
                String phone = "";
                String surveyID = "";
                JSONArray response = new JSONArray();
                JSONObject data = new JSONObject();
                JSONArray imageQuestion = new JSONArray();
                JSONArray image = new JSONArray();
                if(ans.getCount()>0){
                    ans.moveToFirst();
                    do{
                        JSONObject obj = new JSONObject();
                        JSONObject imageObj = new JSONObject();
                        if(ans.getString(7).equals("image")){
                            imageQuestion.put(ans.getString(1));
                            imageObj.put("questionId",ans.getString(1));
                            imageObj.put("image",ans.getString(4));
                            image.put(imageObj);
                        }else {
                            obj.put("questionId", ans.getString(1));
                            obj.put("answer", ans.getString(4));
                            response.put(obj);
                        }
                        surveyID = ans.getString(6);
                        if(ans.getString(1).equals("3")) {
                            //phone = ans.getString(4);
                            phone = "10101010101";
                        }
                    }while(ans.moveToNext());
                    ans.close();
                    data.put("phone",phone);
                    data.put("survey_id",surveyID);
                    data.put("surveyor_id",sharedPreferences.getString("SurveyorId",""));
                    data.put("response",response);
                    data.put("image_question",imageQuestion);
                    //data.put("image",image);
                    JSONObject alldata = new JSONObject();
                    alldata.put("ClientId",newClient.getString(0));
                    alldata.put("data",data);
                    alldata.put("image",image);
                    answerAllUser.put(alldata);
                }
            }while(newClient.moveToNext());
            newClient.close();
            Log.e("ALLDATA",answerAllUser.toString());
            sendAnswerToServer(answerAllUser);

        }
    }
    public void sendAnswerToServer(JSONArray answerAllUser) throws JSONException {
        noOfServiceCall = answerAllUser.length()-1;
        for(int i = 0; i<answerAllUser.length();i++){
            //String clientId = ;
            JSONObject obj = new JSONObject();
            obj = answerAllUser.getJSONObject(i);
            String clientId = obj.getString("ClientId");
            JSONObject data = new JSONObject();
            //String surveyID = obj.getString("survey_id");
            JSONArray image = new JSONArray();
            //image.put(obj.getJSONArray("image"));
            data.put("data",obj.getJSONObject("data"));
            String surveyID = obj.getJSONObject("data").getString("survey_id");
            apiToUpdateanswerAtServer(data,clientId,i,surveyID,obj.getJSONArray("image"));
        }

    }
    public void apiToUpdateanswerAtServer(JSONObject data, String clientId,int count,String surveyID,final JSONArray image){

        final JSONObject dataToPass = data;
        Log.e("DATAPASSING",data.toString());
        final String clientIdToPass = clientId;
        final String survey_id = surveyID;
        String url = "http://104.238.125.119/~codedev/api/sinkresponse.php";
        if (nb.isConnected()) {
            StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("my app", "123"+response);
                            //dialog.hide();
                            dialog1.hide();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                Log.e("updateResponse",jsonObject.toString());
                                String status = jsonObject.getString("status");
                                if(status.equals("Success")){
                                    deleteUpgatedEntriesInAnswerTable(clientIdToPass);
                                    updateClientDB(jsonObject.getJSONObject("data"),survey_id);
                                    //showMessage(status,jsonObject.getString("message"));
                                    //questionDataFormating(jsonObject);
                                }else{
                                    showMessage(status,jsonObject.getString("message"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    //dialog.hide();
                    dialog1.hide();

                }
            }) {

                @Override
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded; charset=UTF-8";
                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("sinkresponse", "1");
                    params.put("responsedata",dataToPass.toString());
                    for(int i = 0; i<image.length();i++){
                        JSONObject obj = new JSONObject();
                        try {
                            obj=image.getJSONObject(i);
                            params.put(obj.getString("questionId"),obj.getString("image"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("Exception",e.getMessage());
                        }

                    }
                    return params;
                }

            };
            int socketTimeout = 20000;//30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjRequest.setRetryPolicy(policy);
            requestQueueLogin.add(jsonObjRequest);
            if(count == noOfServiceCall){
            dialog1 = new ProgressDialog(this);
            dialog1.setMessage("Uploading answers");
            dialog1.setCanceledOnTouchOutside(false);
            //dialog.setCancelable(true);
            dialog1.show();
        }


        }
    }
    public void updateClientDB(JSONObject obj,String survey_id){
        String participant_id = "";
        try {
            participant_id = obj.getString("participant_id");
            myDB.updateClientIdTable(participant_id,survey_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void deleteUpgatedEntriesInAnswerTable(String clientId){
        myDB.deleteAnswerIfUpdated(clientId);
    }
    public void onClickLogout(View view){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
       //builder.setCancelable(true);
        builder.setTitle(R.string.logout_title);
        builder.setMessage(R.string.logout_message);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(welcome.this , MainActivity.class);
                startActivity(i);
            }
        });
        builder.setNegativeButton(R.string.no, null);
       //builder.show();
        AlertDialog dialog1 = builder.create();
        dialog1.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Window view = ((AlertDialog)dialog).getWindow();
                view.setBackgroundDrawableResource(R.color.alertBackGround);
            }
        });
        dialog1.show();
    }

    public void questionDataFormating(JSONObject jsonObject) throws JSONException {
        Log.e("Inside1",jsonObject.toString());
        JSONObject jObj = jsonObject.getJSONObject("data");
        Log.e("Inside2",jObj.toString());
        JSONArray surveyList = jObj.getJSONArray("survey_list");
        JSONArray sectionList = jObj.getJSONArray("section_list");
        Log.e("Inside4",sectionList.toString());
        JSONArray groupList = jObj.getJSONArray("group_list");
        JSONArray questionList = jObj.getJSONArray("question_list");
        myDB.insertDataSurveyList(surveyList);
        myDB.insertDataSectionList(sectionList);
        myDB.insertDataGroupList(groupList);
        myDB.insertDataQuestion(questionList);
        showMessage("Info","Survey Table Updated");
        Log.e("Inside3",surveyList.toString());

    }
public void getQuestionListFromAPI(){
    String url = "http://104.238.125.119/~codedev/api/surveydata.php ";
    if (nb.isConnected()) {
        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("my app", "123"+response);
                        dialog.hide();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.e("my app",jsonObject.toString());
                            String status = jsonObject.getString("status");
                            if(status.equals("Success")){
                               //showMessage(status,jsonObject.getString("message"));
                                questionDataFormating(jsonObject);
                            }else{
                                showMessage(status,jsonObject.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();

            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("surveydata", "1");
                return params;
            }

        };
        int socketTimeout = 10000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjRequest.setRetryPolicy(policy);
        requestQueueLogin.add(jsonObjRequest);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Updating Data");
        dialog.setCanceledOnTouchOutside(false);
        //dialog.setCancelable(true);
        dialog.show();


    }

}


    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }
    public void showMessage(String title, String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        //builder.show();
        AlertDialog dialog1 = builder.create();
        dialog1.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Window view = ((AlertDialog)dialog).getWindow();
                view.setBackgroundDrawableResource(R.color.alertBackGround);
            }
        });
        dialog1.show();

    }
//    public void updateLanguageAndSurveyListDB(){
//        if(nb.isConnected()){
//            if(myDB.ifSurveyListIsEmpty()) {
//                getSurveyListFromAPI();
//            }
//            //tableUpdate.getLaunguageListFromAPI();
//        }
//
//    }
//    public void getSurveyListFromAPI(){
//        //service call
//        String url = "http://104.238.125.119/~codedev/api/surveylist.php";
//        if (nb.isConnected()) {
//            StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
//                    url,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            //showMessage("data",response);
//                            dialog.hide();
//
//                            try {
//
//                                JSONObject obj = new JSONObject(response);
//                                Log.e("my app", response);
//                                status = obj.getString("status");
//                                if(status.equals("Success")){
//                                    JSONArray jArray = obj.getJSONArray("data");
//                                    myDB.insertDataSurveyList(jArray);
//                                    showMessage("Info","Survey Table Updated");
//                                }else{
//                                    //showMessage("Error",obj.getString("message"));
//
//                                }
//
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                                Log.e("my app", "could not be parse");
//
//                            }
//
//
//                        }
//                    }, new Response.ErrorListener() {
//
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    dialog.hide();
//
//                }
//            }) {
//
//                @Override
//                public String getBodyContentType() {
//                    return "application/x-www-form-urlencoded; charset=UTF-8";
//                }
//
//                @Override
//                protected Map<String, String> getParams() {
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put("surveylist", "1");
//                    return params;
//                }
//
//            };
//
//            requestQueueLogin.add(jsonObjRequest);
//            dialog = new ProgressDialog(this);
//            dialog.setMessage("Updating Data");
//            // dialog.setCancelable(true);
//            dialog.show();
//
//
//        } else {
//
//        }
//    }
}
