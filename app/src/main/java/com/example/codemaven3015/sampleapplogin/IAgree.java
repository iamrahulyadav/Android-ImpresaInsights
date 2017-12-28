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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class IAgree extends AppCompatActivity {
    String SurveyId;
    int Iagree = 0;
    String[] surveyDetails;
    DataBaseHealper db;
    TextView textViewSurveyName;
    TextView textView_desc;
    GlobalVariables gbl;
    RadioGroup radioIagreeActivity;
    TextView textViewtitle;


    public void OnBack(View v) {
        Intent i = new Intent(IAgree.this, welcome.class);
        i.putExtra("from","not_main");
        startActivity(i);
    }
    public void openQuestion(View v) {
        if(Iagree ==1){
            Cursor res = getSectionList();
            gbl.setsectionList(res);
            //gbl.incrementSectionCount();
            if(res.getCount()>0) {
                String sectionName, sectionId ;
                res.moveToFirst();
                sectionName = res.getString(2);
                sectionId = res.getString(0);
                String sectionDesc = res.getString(3);
                if(!SurveyId.equals("1")) {
                    Intent i = new Intent(IAgree.this, SurveySection.class);
                    i.putExtra("SURVEY_NAME", surveyDetails[1]);
                    i.putExtra("SURVEY_ID", SurveyId);
                    i.putExtra("SECTION_NAME", sectionName);
                    i.putExtra("SECTION_ID", sectionId);
                    i.putExtra("SECTION_NO", "1");
                    i.putExtra("SECTION_DESC", sectionDesc);
                    i.putExtra("isDONE",false);
                    startActivity(i);
                }else{
                    Intent i = new Intent(IAgree.this, Question.class);
                    i.putExtra("from","iagree");
                    i.putExtra("SURVEY_NAME", surveyDetails[1]);
                    i.putExtra("SURVEY_ID", SurveyId);
                    i.putExtra("SECTION_NAME", sectionName);
                    i.putExtra("SECTION_ID", sectionId);
                    i.putExtra("SECTION_NO", "1");
                    i.putExtra("SECTION_DESC", sectionDesc);
                    startActivity(i);
                }
            }else{
                showMessage("Info","No Section");
            }
        }
        else if(Iagree == 0){
            //do noting
            showMessage("Info","Please Select One Option");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DataBaseHealper(this);
        gbl = (GlobalVariables)getApplicationContext();
        gbl.resetAllGlobalVariable();
        setContentView(R.layout.activity_iagree);
        textViewSurveyName = (TextView)findViewById(R.id.textViewSurveyName);
        radioIagreeActivity = (RadioGroup)findViewById(R.id.radioIagreeActivity);
        textView_desc = (TextView)findViewById(R.id.textView_desc);
        textViewtitle = (TextView)findViewById(R.id.textView4);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        //Toolbar parent= (Toolbar)(R.layout.action_bar).getParent();
        final TextView helloTextView = (TextView) findViewById(R.id.action_text);
        final ImageView imageView = (ImageView)findViewById(R.id.imageButton);
        imageView.setVisibility(View.GONE);
        SurveyId = getIntent().getStringExtra("SURVEY_ID");
        surveyDetails = db.getSurveyDetails(SurveyId);
        if(!gbl.getClientId().equals("new")){
            Cursor ifSurveyDone = db.ifSurveyDone(gbl.getClientId(),SurveyId);
            if(ifSurveyDone.getCount()>0){

                ifSurveyDone.moveToFirst();
                do{
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("question_no", ifSurveyDone.getString(1));
                        obj.put("answer", ifSurveyDone.getString(4));
                        obj.put("radio", ifSurveyDone.getString(7));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    gbl.addAnswerInJsonArray(obj);
                }while (ifSurveyDone.moveToNext());
            }
        }
        //db.insertDataQuestion();
        if(surveyDetails.length >0 ) {
            helloTextView.setText(surveyDetails[1]);
            textViewSurveyName.setText(surveyDetails[1]);
            if(!(SurveyId.equals("4"))){
                textView_desc.setText(surveyDetails[2]);
                textViewtitle.setVisibility(View.VISIBLE);
                radioIagreeActivity.setVisibility(View.VISIBLE);
            }else{
                textView_desc.setText(surveyDetails[2]);
               radioIagreeActivity.setVisibility(View.INVISIBLE);
                textViewtitle.setVisibility(View.GONE);
                Iagree = 1;
                //textView_desc = ;
            }

        }else{
            helloTextView.setText("Survey");
        }

    }
    public Cursor getSectionList(){
        Log.e("Survey id",SurveyId);
        Cursor sectionList = db.getSectionList(SurveyId);
        return sectionList;
    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioIAgree:
                if (checked)
                    Iagree = 1;
                    // Pirates are the best
                    break;
            case R.id.radioIDontAgree:
                if (checked)
                    Iagree=2;
                    // Ninjas rule
                    break;
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
