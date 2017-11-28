package com.example.codemaven3015.sampleapplogin;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class SurveyList extends AppCompatActivity {
    DataBaseHealper myDB;
    RequestQueue requestQueueLogin;
    isNetworkAvaliable nb;
    ProgressBar mProgressView;
    String fromWhere;
    GlobalVariables gbl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myDB = new DataBaseHealper(this);
        nb = new isNetworkAvaliable(this);
        requestQueueLogin = Volley.newRequestQueue(this);
        gbl = (GlobalVariables)getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_list);
        mProgressView = (ProgressBar)findViewById(R.id.login_progress);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        final TextView helloTextView1 = (TextView) findViewById(R.id.action_text);
        helloTextView1.setText("Surveys");
       // mProgressView.setVisibility(View.VISIBLE);
        //getSurveyListFromAPI();
        //fromWhere = getIntent().getStringExtra("CLIENT");
        populateButtons();

    }

    public void onClickLogout(View view){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //builder.setCancelable(true);
        builder.setTitle(R.string.logout_title);
        builder.setMessage(R.string.logout_message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(SurveyList.this , MainActivity.class);
                    startActivity(i);
                }
            });
        builder.setNegativeButton("No", null);
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
    public void populateButtons(){

        if(!myDB.ifSurveyListIsEmpty()) {
            final Cursor res = myDB.getSurveyList();
            LinearLayout ll = (LinearLayout) findViewById(R.id.buttons);
            int heightButtonDp = 55;
            int marginLeftDp = 20;
            int marginBottomDp = 15;
            float scale = getResources().getDisplayMetrics().density;
            int heightButtonPxl = (int) (heightButtonDp * scale + 0.5f);
            int marginLeftPxl = (int) (marginLeftDp * scale + 0.5f);
            int marginBottomPxl = (int) (marginBottomDp * scale + 0.5f);
            boolean flagBtnColor = true;
            if (res.getCount() == 0) {

            } else {
                //StringBuffer buffer = new StringBuffer()
                while (res.moveToNext()) {
                    Button btn = new Button(this);
                    final String str = res.getString(0);
                    btn.setText(res.getString(1));
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, heightButtonPxl);
                    layoutParams.height = heightButtonPxl;
                    btn.setTextSize(20);
                    layoutParams.setMargins(marginLeftPxl, 0, marginLeftPxl, marginBottomPxl);
                    // btn.setPadding(marginPx,marginPx,marginPx,marginPx);
                    btn.setLayoutParams(layoutParams);
                    if (flagBtnColor) {
                        btn.setBackgroundColor(getResources().getColor(R.color.blue));
                        btn.setTextColor(getResources().getColor(R.color.white));
                        btn.setTag("blue");
                        flagBtnColor = false;
                    } else {
                        btn.setBackgroundColor(getResources().getColor(R.color.white));
                        btn.setTextColor(getResources().getColor(R.color.blue));
                        flagBtnColor = true;
                        btn.setTag("white");
                    }
                    if(gbl.getClientId().equals("new") && !str.equals("1")){
                        btn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        btn.setTextColor(getResources().getColor(R.color.white));
                        btn.setTag("red");
                    }
                    if(!gbl.getClientId().equals("new")) {
                        Cursor list = myDB.returnSurveyDone(gbl.getClientId());
                        Log.e("CURSOR",list.toString());
                        if (list.getCount() > 0) {
                            list.moveToFirst();
                            do{
                                if(str.equals(list.getString(1))){
                                    btn.setTag("red");
                                    btn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                    btn.setTextColor(getResources().getColor(R.color.white));
                                }
                        }while(list.moveToNext());
                    }
                    }

                    ll.addView(btn);
                    btn.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(gbl.getClientId().equals("new") && !str.equals("1")){
                                        showMessage(getResources().getString(R.string.info),getResources().getString(R.string.newClientMessage));
                                    }else{
                                        if(v.getTag().equals("red")){
                                            showMessage(getResources().getString(R.string.info),getResources().getString(R.string.surveyTaken));
                                        }else {
                                            Intent intent = new Intent(SurveyList.this, SelectLanguage.class);
                                            intent.putExtra("SURVEY_ID", str);
                                            startActivity(intent);
                                        }
                                    }

                                }
                            }
                    );


                }

            }
        }else {
            showMessage(getResources().getString(R.string.info),getResources().getString(R.string.emptySurveyTable));
        }
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
