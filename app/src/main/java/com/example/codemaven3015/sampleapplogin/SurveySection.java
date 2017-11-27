package com.example.codemaven3015.sampleapplogin;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class SurveySection extends AppCompatActivity {
    String surveyName, sectionTittle, sectionNo,surveyId,sectionId,sectionDesc;
    TextView sectionNumber,sectionName,sectionDesc1;
    DataBaseHealper db;
    GlobalVariables gbl;

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
        gbl = (GlobalVariables)getApplicationContext();
        sectionNumber = (TextView)findViewById(R.id.sectionNumber);
        sectionName = (TextView)findViewById(R.id.sectionName);
        sectionTittle = getIntent().getStringExtra("SECTION_NAME");
        sectionNo = getIntent().getStringExtra("SECTION_NO");
        surveyId = getIntent().getStringExtra("SURVEY_ID");
        sectionId = getIntent().getStringExtra("SECTION_ID");
        sectionDesc = getIntent().getStringExtra("SECTION_DESC");
        sectionNumber.setText("Section "+sectionNo + " :");
        sectionName.setText(sectionTittle);
        sectionDesc1.setText(sectionDesc);
        //sectionDesc1.setMovementMethod(new ScrollingMovementMethod());

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
            Intent i = new Intent(SurveySection.this, QuestionDynamic.class);
            i.putExtra("SURVEY_NAME",surveyName);
            i.putExtra("FIRST","first");
            i.putExtra("SURVEY_ID", surveyId);
            i.putExtra("SECTION_NAME", sectionTittle);
            i.putExtra("SECTION_ID", sectionId);
            i.putExtra("SECTION_NO",sectionNo);
            i.putExtra("SECTION_DESC",sectionDesc);
            startActivity(i);
        }else{
            showMessage("Info","No Question");
        }
        //question.close();

    }
    public void onClickSaveAndExit(View view){
        Log.e("SAVE",gbl.getAnswer().toString());
        Cursor section = gbl.getSectionList();
        if(section.getCount()>0) {
            section.moveToFirst();
            //gbl.incrementSectionCount();
            if (!(gbl.getSectionCount() < section.getCount()) && gbl.getCounter() >= gbl.getCount()) {
                if (getIntent().getStringExtra("SURVEY_ID").equals("1")) {
                    showMessageWithNoAndYes("Info", "Are you sure you want to Save and Exit?",true);
                } else {
                    showMessageWithNoAndYes("Info", "Are you sure you want to Save and Exit?",true);
                }
            } else {
                if (getIntent().getStringExtra("SURVEY_ID").equals("1")) {
                    showMessageWithNoAndYes("Info", "Are you sure you want to exit? All the data will be lost",false);
                } else {
                    showMessageWithNoAndYes("Info", "Are you sure you want to Save and Exit?",true);
                }
            }
        }
        //section.close();
    }
    public void showMessageWithNoAndYes(String title,String message,final boolean flag){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(flag){
                    db.updateAnswerInTable(gbl.getAnswer(),false,surveyId);
                }
                Intent i = new Intent(SurveySection.this , welcome.class);
                i.putExtra("from","not_main");
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
    public void OnBackClick(View view){
        Cursor section = gbl.getSectionList();
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

                        Intent i = new Intent(SurveySection.this, QuestionDynamic.class);
                        i.putExtra("SURVEY_NAME", surveyName);
                        i.putExtra("SURVEY_ID", surveyId);
                        i.putExtra("FIRST","notFirst");
                        i.putExtra("SECTION_NAME", section.getString(2));
                        i.putExtra("SECTION_ID", section.getString(0));
                        i.putExtra("SECTION_NO", gbl.getSectionCount() + 1 + "");
                        i.putExtra("SECTION_DESC", section.getString(3));
                        startActivity(i);
                    }
                    //question.close();
                }
            } else {
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
