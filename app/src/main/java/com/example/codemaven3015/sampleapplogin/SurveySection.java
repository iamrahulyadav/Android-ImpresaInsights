package com.example.codemaven3015.sampleapplogin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

public class SurveySection extends AppCompatActivity {
    String surveyName, sectionTittle, sectionNo,surveyId,sectionId,sectionDesc;
    TextView sectionNumber,sectionName,sectionDesc1,textView10;
    DataBaseHealper db;
    GlobalVariables gbl;
    boolean isDone = false;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Button button_back;
    Cursor section;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_section);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        final TextView helloTextView = (TextView) findViewById(R.id.action_text);
        surveyName = getIntent().getStringExtra("SURVEY_NAME");
        helloTextView.setText(surveyName);
        final ImageView imageView = (ImageView)findViewById(R.id.imageButton);
        imageView.setVisibility(View.GONE);
        sectionDesc1 = (TextView)findViewById(R.id.sectionDescription);
        db = new DataBaseHealper(this);
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gbl = (GlobalVariables)getApplicationContext();
        textView10 = (TextView)findViewById(R.id.textView10);
        sectionNumber = (TextView)findViewById(R.id.sectionNumber);
        sectionName = (TextView)findViewById(R.id.sectionName);
        sectionTittle = getIntent().getStringExtra("SECTION_NAME");
        sectionNo = getIntent().getStringExtra("SECTION_NO");
        surveyId = getIntent().getStringExtra("SURVEY_ID");
        sectionId = getIntent().getStringExtra("SECTION_ID");
        sectionDesc = getIntent().getStringExtra("SECTION_DESC");
        isDone = getIntent().getBooleanExtra("isDONE",false);
        sectionNumber.setText("Section "+sectionNo + " :");
        if(surveyId.equals("8")){
            if(gbl.popular_product_answer.size()>2) {
                sectionTittle = sectionTittle.replace("#1", gbl.popular_product_answer.get(0));
                sectionTittle = sectionTittle.replace("#2", gbl.popular_product_answer.get(1));
                sectionTittle = sectionTittle.replace("#3", gbl.popular_product_answer.get(2));
            }
        }
        button_back = (Button)findViewById(R.id.button_back);
        sectionName.setText(sectionTittle);
        textView10.setText(surveyName);
        gbl.setClientId(getIntent().getStringExtra("CLIENT"));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            sectionDesc1.setText(Html.fromHtml(sectionDesc,Html.FROM_HTML_MODE_LEGACY));
        } else {
            sectionDesc1.setText(Html.fromHtml(sectionDesc));
        }
        if (savedInstanceState != null) {
            Log.e("Saved",savedInstanceState.getString("ANSWER"));
            try {
                JSONArray jsonObject = new JSONArray(savedInstanceState.getString("ANSWER"));
                gbl.setAnswerFromSavedInstance(jsonObject);
                gbl.setSectionCount(savedInstanceState.getInt("SECTION_NO"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //gbl.setAnswerFromSavedInstance();

        }
        section = db.getSectionList(getIntent().getStringExtra("SURVEY_ID"));
        gbl.setsectionList(section);
        if(surveyId.equals("5")){
            abilitySurveyDetails();
        }
        //sectionDesc1.setMovementMethod(new ScrollingMovementMethod());

    }
    @Override
    protected void onPause() {
        //question.close();
        section.close();
        super.onPause();
    }
    @Override
    protected void onResume() {
        section = db.getSectionList(getIntent().getStringExtra("SURVEY_ID"));
        //section.moveToFirst();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        section.close();
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("ANSWER",gbl.getAnswer()+"");
        savedInstanceState.putInt("SECTION_NO",gbl.getSectionCount());
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }

    public void nextQuestionActivity(View view) {
        Cursor question = db.getQuestionList(sectionId);
        //question.moveToFirst();
        gbl.resetquestioncounter();
        gbl.setGlobalCursor(question);
        if(question.getCount()>0) {
            question.moveToFirst();
            while (question.moveToNext()){
                Log.e("Option",question.getString(8));
            }
//            if(surveyId.equals("2")||surveyId.equals("3")){
//                Intent i = new Intent(SurveySection.this, Question.class);
//                i.putExtra("from","surveySection");
//                i.putExtra("SURVEY_NAME", surveyName);
//                i.putExtra("FIRST", "first");
//                i.putExtra("SURVEY_ID", surveyId);
//                i.putExtra("SECTION_NAME", sectionTittle);
//                i.putExtra("SECTION_ID", sectionId);
//                i.putExtra("SECTION_NO", sectionNo);
//                i.putExtra("SECTION_DESC", sectionDesc);
//                startActivity(i);
//            }else {
                Intent i;
            if(surveyId.equals("5")){
                i = new Intent(SurveySection.this, TimerSurvey.class);
            }else{
                i = new Intent(SurveySection.this, QuestionDynamic.class);
            }
                i.putExtra("CLIENT",getIntent().getStringExtra("CLIENT"));
                i.putExtra("SURVEY_NAME", surveyName);
                i.putExtra("FIRST", "first");
                i.putExtra("SURVEY_ID", surveyId);
                i.putExtra("SECTION_NAME", sectionTittle);
                i.putExtra("SECTION_ID", sectionId);
                i.putExtra("SECTION_NO", sectionNo);
                i.putExtra("SECTION_DESC", sectionDesc);
                i.putExtra("isDONE",isDone);
                startActivity(i);
            //}
        }else{
            showMessage("Info","No Question");
        }
        //ques tion.close();

    }
    public void abilitySurveyDetails(){
        button_back.setClickable(false);
        button_back.setBackgroundColor(getResources().getColor(R.color.fadeRed));
    }
    public void onClickSaveAndExit(View view){
        Log.e("SAVE",gbl.getAnswer().toString());
        //Cursor section = gbl.getSectionList();
        if(section.getCount()>0) {
            section.moveToFirst();
            //gbl.incrementSectionCount();
            if (!(gbl.getSectionCount() < section.getCount()) && gbl.getCounter() >= gbl.getCount()) {
                if (getIntent().getStringExtra("SURVEY_ID").equals("1")) {
                    showMessageWithNoAndYes(getResources().getString(R.string.info), getResources().getString(R.string.saveAndExit),true,isDone);
                } else {
                    showMessageWithNoAndYes(getResources().getString(R.string.info), getResources().getString(R.string.saveAndExit),true,isDone);
                }
            } else {
                if (getIntent().getStringExtra("SURVEY_ID").equals("1")) {
                    showMessageWithNoAndYes(getResources().getString(R.string.info), getResources().getString(R.string.dataLost),false,isDone);
                } else {
                    showMessageWithNoAndYes(getResources().getString(R.string.info), getResources().getString(R.string.saveAndExit),true,isDone);
                }
            }
        }
        //section.close();
    }
    public void showMessageWithNoAndYes(String title,String message,final boolean flag,final boolean isdone){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(flag){
                    if(gbl.getClientId().equals("new")){
                        db.updateAnswerInTable(gbl.getAnswer(),false,surveyId,gbl.getClientId(),isdone,sharedPreferences.getString("project_id", ""));
                    }else{
                        db.deleteAnswerIfUpdated(gbl.getClientId(),surveyId);
                        db.updateAnswerInTable(gbl.getAnswer(),true,surveyId,gbl.getClientId(),isdone,sharedPreferences.getString("project_id", ""));
                    }
                }else{
                    db.deleteRegistrationDetails("1");
                }
                Intent i = new Intent(SurveySection.this , welcome.class);
                i.putExtra("from","not_main");
                startActivity(i);
            }
        });
        builder.setNegativeButton(R.string.no, null);

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
    public void OnBackClick(View view){
        //Cursor section = gbl.getSectionList();
        gbl.decrementSectionCount();
        if(section.moveToFirst()) {
            section.moveToPosition(gbl.getSectionCount());
            int sectionCount = gbl.getSectionCount();//   jkfk
            if (sectionCount > -1) {
                if (gbl.getSectionCount() < section.getCount()) {
                    Cursor question = db.getQuestionList(section.getString(0));
                    gbl.resetquestioncounter();
                    gbl.setGlobalCursor(question);

                    if (question.getCount() > 0) {
                        question.moveToFirst();

                        Intent i ;
                        if(surveyId.equals("5")){
                            i = new Intent(SurveySection.this, TimerSurvey.class);
                        }else{
                            i = new Intent(SurveySection.this, QuestionDynamic.class);
                        }
                        String first = "notFirst";
                        if(surveyId.equals("11")){
                            first = "first";
                        }
                        i.putExtra("CLIENT",getIntent().getStringExtra("CLIENT"));
                        i.putExtra("SURVEY_NAME", surveyName);
                        i.putExtra("SURVEY_ID", surveyId);
                        i.putExtra("FIRST",first);
                        i.putExtra("SECTION_NAME", section.getString(2));
                        i.putExtra("SECTION_ID", section.getString(0));
                        i.putExtra("SECTION_NO", gbl.getSectionCount() + 1 + "");
                        i.putExtra("SECTION_DESC", section.getString(3));
                        i.putExtra("isDONE",isDone);
                        startActivity(i);
                    }
                    //question.close();
                }
            } else {
                db.deleteRegistrationDetails("1");

                Intent i = new Intent(SurveySection.this, welcome.class);
                i.putExtra("from", "not_main");
                startActivity(i);
            }
        }
        //section.close();

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
