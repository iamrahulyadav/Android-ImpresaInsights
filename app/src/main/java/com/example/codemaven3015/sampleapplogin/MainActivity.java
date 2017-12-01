package com.example.codemaven3015.sampleapplogin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    isNetworkAvaliable nb;
    DataBaseHealper db;
    RequestQueue requestQueueLogin;
    String url = "http://104.238.125.119/~codedev/api/login.php";
    EditText userName;
    EditText password;
    ProgressBar mProgressView;
    CheckBox checkBox;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String usernameString , passwordString , status;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        nb = new isNetworkAvaliable(this);
        db = new DataBaseHealper(this);
        requestQueueLogin = Volley.newRequestQueue(this);
        userName = (EditText)findViewById(R.id.editText_username);
        password = (EditText)findViewById(R.id.editText_password);
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        checkBox = (CheckBox)findViewById(R.id.rememberMeCheckBox);
        checkBox.setChecked(true);
        registerReceiver(new CheckConnectivityStateChange(this),new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        mProgressView = (ProgressBar)findViewById(R.id.login_progress);
        //isNetworkAvaliableToast();
        userName.setText(sharedPreferences.getString("userName",""));
        password.setText(sharedPreferences.getString("password",""));

    }
    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }

    public void sendStringRequest(){
        {
            StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            dialog.hide();
                            Log.e("my app",response);
                            try {
                                JSONObject obj = new JSONObject(response);

                                status = obj.getString("status").trim();

                                onStatusLogin(status,"Valid Login");

                            } catch (JSONException e) {
                                e.printStackTrace();
                                onStatusLogin(getResources().getString(R.string.Error),getResources().getString(R.string.wrong_password));
                                Log.e("my app","could not be parse");
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.hide();
                    showMessage(getResources().getString(R.string.info),"Check your Network Connection");

                }
            }) {

                @Override
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded; charset=UTF-8";
                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email", usernameString);
                    params.put("password", passwordString);
                    return params;
                }

            };
            int socketTimeout = 10000;//30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjRequest.setRetryPolicy(policy);

            requestQueueLogin.add(jsonObjRequest);
            dialog = new ProgressDialog(this);
            dialog.setMessage(getResources().getString(R.string.LoginMessage));
            dialog.setCancelable(true);
            dialog.show();



        }

    }

//    public void isNetworkAvaliableToast(){
//        boolean isConnect = nb.isConnected();
//        if(isConnect){
//            //Toast.makeText(MainActivity.this,"Network Connected",Toast.LENGTH_LONG).show();
//        }
//        else{
//            Toast.makeText(MainActivity.this,"No Network Connected",Toast.LENGTH_LONG).show();
//        }
//    }
    public void onLoginSucess(){
        editor.putString("SurveyorId",usernameString);
        editor.apply();
        Intent i = new Intent(MainActivity.this , welcome.class);
        i.putExtra("from","main");
        startActivity(i);

    }
    public boolean LoginInputValidation(){
        if(usernameString.isEmpty()||passwordString.isEmpty()){
            return false;
        }else {
            return true;

        }

    }

    public void onStatusLogin(String status1, String message){

        if(status1.equals("success")) {
            Log.e("my app",status1);
            boolean isExist = db.CheckIsDataAlreadyInDBorNotLogin(usernameString);
            if (isExist) {
                db.updateDataLogin(usernameString, passwordString);

            } else {
                db.insertDataLogin(usernameString, passwordString);

            }
            this.onLoginSucess();
        }else{
            showMessage(getResources().getString(R.string.Error),message);
        }


    }
    public void onLoginClick(View v){
        v.setEnabled(false);
        v.setClickable(false);
        usernameString = userName.getText().toString().trim();
        passwordString = password.getText().toString().trim();
        boolean isConnect = nb.isConnected();
        if(LoginInputValidation()) {
            if(checkBox.isChecked()){
                editor.putString("userName",usernameString);
                editor.putString("password",passwordString);
                editor.apply();
            }
            if (isConnect) {
                sendStringRequest();

            } else {
                boolean isExist = db.CheckIsDataAlreadyInDBorNotLogin(usernameString);
                if (isExist) {
                    boolean isMatched = db.passwordMatchDBLogin(usernameString, passwordString);
                    if (isMatched) {
                        //Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_LONG).show();
                        this.onLoginSucess();
                        //showProgress(false);
                    } else {
                        showMessage(getResources().getString(R.string.Error),getResources().getString(R.string.wrong_password));
                        //showProgress(false);
                    }
                } else {
                    showMessage(getResources().getString(R.string.Error),"Check your network connection");
                    //showProgress(false);
                }
            }
        }else
        {
            showMessage(getResources().getString(R.string.Error),getResources().getString(R.string.LoginEmptyFieldMessage));
            //showProgress(false);
        }

        v.setEnabled(true);
        v.setClickable(true);
        //showProgress(false);
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
}
