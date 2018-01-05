package com.example.codemaven3015.sampleapplogin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class TimerSurvey extends AppCompatActivity {
    GlobalVariables gbl;
    DataBaseHealper db;
    TextView clientId, textViewQuestionNumber1, textViewQuestion1,textViewQuestionInfo;
    ProgressBar progressBar;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean message = false;
    boolean isImage = false;
    CountDownTimer countDownTimer;
    RadioGroup radioGroup;
    int marginBottomPxl,sizeOfImage;
    LinearLayout imageLayout;
    String selectedImage = "";
    boolean isAptitude = false;
    boolean isComplete = false;
    String jumpto = "";
    boolean jumptoFlag = false;
    boolean question6a = false;
    Cursor question;
    ImageView questionImage ,answerImage1 ,answerImage2 ,answerImage3 ,answerImage4 ,answerImage5, answerImage6,answerImage7,answerImage8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_survey);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        final TextView helloTextView = (TextView) findViewById(R.id.action_text);
        final ImageView imageView = (ImageView)findViewById(R.id.imageButton);
        imageView.setVisibility(View.GONE);
        helloTextView.setText(getIntent().getStringExtra("SURVEY_NAME"));
        db = new DataBaseHealper(this);
        gbl = (GlobalVariables)getApplicationContext();
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        textViewQuestionNumber1 = (TextView)findViewById(R.id.textViewQuestionNumber1);
        textViewQuestion1 = (TextView)findViewById(R.id.textViewQuestion1);
        textViewQuestionInfo = (TextView)findViewById(R.id.textViewQuestionInfo);
        clientId = (TextView)findViewById(R.id.textView2);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        imageLayout = (LinearLayout)findViewById(R.id.imageLayout);
        questionImage = (ImageView)findViewById(R.id.questionImage);
        answerImage1 = (ImageView)findViewById(R.id.answerImage1);
        answerImage2 = (ImageView)findViewById(R.id.answerImage2);
        answerImage3 = (ImageView)findViewById(R.id.answerImage3);
        answerImage4 = (ImageView)findViewById(R.id.answerImage4);
        answerImage5 = (ImageView)findViewById(R.id.answerImage5);
        answerImage6 = (ImageView)findViewById(R.id.answerImage6);
        answerImage7 = (ImageView)findViewById(R.id.answerImage7);
        answerImage8 = (ImageView)findViewById(R.id.answerImage8);
        textViewQuestionNumber1.setTag("notDone");
        Drawable progressDrawable = progressBar.getProgressDrawable().mutate();
        progressDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.setProgressDrawable(progressDrawable);
        gbl.setClientId(getIntent().getStringExtra("CLIENT"));
        clientId.setText(gbl.getClientId());
        int marginBottomDp = 10;
        float scale = getResources().getDisplayMetrics().density;
        marginBottomPxl = (int) (marginBottomDp * scale + 0.5f);
        sizeOfImage = (int) (100 * scale + 0.5f);
        question = db.getQuestionList(getIntent().getStringExtra("SECTION_ID"));
        question.moveToFirst();
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            Log.e("Saved", savedInstanceState.getString("ANSWER"));
            try {
                JSONArray jsonObject = new JSONArray(savedInstanceState.getString("ANSWER"));
                gbl.setAnswerFromSavedInstance(jsonObject);
                //gbl.setQuestionSavedCounter(savedInstanceState.getInt("QUESTION_NO"));
                gbl.setSectionCount(savedInstanceState.getInt("SECTION_NO"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        setQuestion();

    }
    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("QUESTION_NO",gbl.getCounter());
        savedInstanceState.putString("ANSWER",gbl.getAnswer()+"");
        savedInstanceState.putInt("SECTION_NO",gbl.getSectionCount());
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    protected void onResume() {
        question = db.getQuestionList(getIntent().getStringExtra("SECTION_ID"));
        question.moveToFirst();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        question.close();
    }

    public void onSaveandExit(View view){
        //if(isComplete)
        showMessageWithNoAndYes(getResources().getString(R.string.info), getResources().getString(R.string.saveAndExit), isComplete);

    }
    public void nextQuestion(View view) throws JSONException {
        try {
            countDownTimer.cancel();
        }catch(NullPointerException e){
       }
        progressBar.setVisibility(View.GONE);
        if(isImage){
            isImage = false;
            questionImage.setVisibility(View.GONE);
            textViewQuestion1.setVisibility(View.GONE);
            textViewQuestionInfo.setVisibility(View.GONE);
            setClickable(true);
            //imageLayout.setVisibility(View.GONE);
            //radioGroup.setVisibility(View.VISIBLE);
            return;
        }
        if(isAptitude){
            isAptitude = false;
            textViewQuestion1.setVisibility(View.GONE);
            textViewQuestionInfo.setVisibility(View.GONE);
            radioGroup.setVisibility(View.VISIBLE);
            return;
        }
        if(imageLayout.getVisibility() == View.VISIBLE){
            if(selectedImage.equals("")){
                showMessage("Error","Please select one image");
                return;
            }else{
                String question = textViewQuestionNumber1.getTag().toString();;
                JSONObject obj = new JSONObject();
                obj.put("question_no", question);
                obj.put("answer", selectedImage);
                obj.put("radio", "");
                gbl.addAnswerInJsonArray(obj);
            }
        }
        if(radioGroup.getVisibility() == View.VISIBLE){
            int selectedIndex = radioGroup.getCheckedRadioButtonId();
            if(selectedIndex>0){
                RadioButton r = (RadioButton) findViewById(selectedIndex);
                String question = textViewQuestionNumber1.getTag().toString();
                if(!(r == null)) {
                    String answer = r.getText().toString();
                    JSONObject obj = new JSONObject();
                    obj.put("question_no", question);
                    obj.put("answer", answer);
                    obj.put("radio", "");
                    gbl.addAnswerInJsonArray(obj);

                    if(!jumpto.equals("")){
                        if(answer.toLowerCase().equals("no")){
                            jumptoFlag = true;
                        }else {
                            jumptoFlag = false;
                        }
                    }else {
                        jumptoFlag = false;
                    }
                }else{
                    showMessage("Error","Please select one option");
                    return;
                }
            }else{
                showMessage("Error","Please select one option");
                return;
            }
        }
        if(!message) {
            gbl.countIncrement();
        }
        setQuestion();
        progressBar.setProgress(0);
    }
    public void showmessageQuestion(){
        textViewQuestionInfo.setVisibility(View.GONE);
        textViewQuestionNumber1.setVisibility(View.GONE);
        textViewQuestion1.setText("Please return the tablet to the surveyor before moving on.");

    }
    public boolean ifAnswerExist(String questionId){
        JSONArray answer = gbl.getAnswer();
        for(int i=0;i<answer.length();i++){
            try {
                JSONObject obj = answer.getJSONObject(i);
                if(questionId.equals(obj.getString("question_no"))){
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }
    public void setQuestion() {
        isComplete = false;
        //progressBar.setVisibility(View.GONE);
        if (message) {
            showmessageQuestion();
            message = false;
        } else {
            //Cursor question = gbl.getQuestionCursor();

            //question.moveToFirst();
            if (question.getCount() > 0) {
                if (gbl.getCounter() < question.getCount()) {

                    question.moveToPosition(gbl.getCounter());
                    if(jumptoFlag && (!jumpto.equals(""))){
                        while(!question.getString(9).equals(jumpto)){
                            if(gbl.getCounter()<=question.getCount()) {
                                gbl.countIncrement();
                                question.moveToPosition(gbl.getCounter());
                            }else {
                                break;
                            }
                        }
                    }
                    jumptoFlag = false;
                    String compare,questionId, questionOrder, QuestionSectionSuggestion, questionText, timer, isMessage, type,imageText;
                    JSONArray options = null;
                    questionId = question.getString(0);
                    type = question.getString(4);
                    questionText = question.getString(5);
                    QuestionSectionSuggestion = question.getString(6);
                    questionOrder = question.getString(9);
                    isMessage = question.getString(7);
                    imageText = "q"+questionOrder.trim();
                    compare = question.getString(11);
                    timer = question.getString(12);
                    try {
                        options = new JSONArray(question.getString(8));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //timer
                    if(ifAnswerExist(questionId)){
                        gbl.countIncrement();
                        setQuestion();
                        return;
                    }else {
                        try {
                            setQuestionData(compare, questionId, questionOrder, QuestionSectionSuggestion, questionText, timer, isMessage, type, options, imageText);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    //Cursor section = gbl.getSectionList();
                    Cursor section = db.getSectionList(getIntent().getStringExtra("SURVEY_ID"));
                    if (section.getCount() > 0) {
                        section.moveToFirst();
                        gbl.incrementSectionCount();
                        if (gbl.getSectionCount() < section.getCount()) {
                            section.moveToPosition(gbl.getSectionCount());
                            Intent i = new Intent(TimerSurvey.this, SurveySection.class);
                            i.putExtra("SURVEY_NAME", getIntent().getStringExtra("SURVEY_NAME"));
                            i.putExtra("SURVEY_ID", getIntent().getStringExtra("SURVEY_ID"));
                            i.putExtra("SECTION_NAME", section.getString(2));
                            i.putExtra("SECTION_ID", section.getString(0));
                            i.putExtra("SECTION_NO", gbl.getSectionCount() + 1 + "");
                            i.putExtra("SECTION_DESC", section.getString(3));
                            i.putExtra("CLIENT",getIntent().getStringExtra("CLIENT"));
                            i.putExtra("isDONE", false);
                            startActivity(i);
                        } else {
                            gbl.decrementSectionCount();//isDone = true;
                            isComplete = true;
                            showMessageWithNoAndYes(getResources().getString(R.string.tankYou), "Thank You "+gbl.getName()+getResources().getString(R.string.surveyCompleted),true);

                            Log.e("Answer*",gbl.getAnswer().toString());
                        }
                    }
                }
            }
        }
    }
    public void showMessageWithNoAndYes(String title, String message,final boolean isdone){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteAnswerIfUpdated(gbl.getClientId());
                if(!isdone){
                    editor.putString(gbl.getClientId(),getIntent().getStringExtra("SECTION_ID"));
                    editor.apply();
                }
                db.updateAnswerInTable(gbl.getAnswer(),true,getIntent().getStringExtra("SURVEY_ID"),gbl.getClientId(),isdone,sharedPreferences.getString("project_id", ""));
                Intent i = new Intent(TimerSurvey.this , welcome.class);
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
    public void setQuestionData(String compare, String questionId,String questionOrder,String QuestionSectionSuggestion,String questionText,String timer,String isMessage,String type,JSONArray options,String imageText) throws JSONException {
        //textViewQuestionInfo.setVisibility(View.VISIBLE);
        if(!question6a) {
            if (questionOrder.toLowerCase().equals("6a")) {
                question6a = true;
            }else{
                question6a = false;
            }
        }
                if (question6a) {
                    if(textViewQuestionNumber1.getVisibility() == View.VISIBLE) {
                        textViewQuestion1.setVisibility(View.VISIBLE);
                        textViewQuestion1.setText(getResources().getString(R.string.recallNumber));
                        textViewQuestionNumber1.setVisibility(View.GONE);
                        textViewQuestionNumber1.setTag("done");
                        radioGroup.setVisibility(View.GONE);
                        gbl.countDecrement();
                        question6a = false;
                        return;
                    }
                }
        question6a = false;
        textViewQuestion1.setVisibility(View.VISIBLE);
        textViewQuestionNumber1.setVisibility(View.VISIBLE);
        textViewQuestionNumber1.setText(questionOrder);
        textViewQuestionNumber1.setTag(questionId);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            textViewQuestion1.setText(Html.fromHtml(questionText,Html.FROM_HTML_MODE_LEGACY));
        } else {
            textViewQuestion1.setText(Html.fromHtml(questionText));
        }
        progressBar.setVisibility(View.GONE);
        progressBar.setProgress(100);
        if(QuestionSectionSuggestion.equals("")){
            textViewQuestionInfo.setVisibility(View.GONE);
        }else {
            textViewQuestionInfo.setVisibility(View.VISIBLE);
            textViewQuestionInfo.setText(QuestionSectionSuggestion);
        }
        if(isMessage.equals("1")){
            message = true;
        }else{
            message = false;
        }
        isImage = false;
        isAptitude = false;
        imageLayout.setVisibility(View.GONE);
        radioGroup.setVisibility(View.GONE);
        int seconds =0;
        if(!timer.equals("")){
            seconds = Integer.parseInt(timer);
        }
        //String sectionName = getIntent().getStringExtra("SECTION_NAME").trim();
        if(type.equals("radio") && seconds>0 ) {
            type = "aptitude";
        }
        switch (type){
            case "readonly":

                JSONObject obj = new JSONObject();
                obj.put("question_no", questionId);
                obj.put("answer", "");
                obj.put("radio", "");
                gbl.addAnswerInJsonArray(obj);

                progressBar.setVisibility(View.VISIBLE);
                if(seconds>0) {
                    setTimer(seconds);
                }

                break;
            case "radio":
                progressBar.setVisibility(View.GONE);
                radioGroup.setVisibility(View.VISIBLE);
                //jumpto = compare;
                if(!compare.equals("")){
                    //setImage(imageText);
                    jumpto = compare;
                }else{
                    jumpto = "";
                }
                try {
                    setRadioButtons(options);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "picquestion":
                isImage = true;
                imageLayout.setVisibility(View.VISIBLE);
                if(!imageText.equals("")){
                    setImage(imageText);
                }
                progressBar.setVisibility(View.VISIBLE);
                if(seconds>0) {
                    setTimer(seconds);
                }
                break;
            case "aptitude":
                isAptitude = true;
                try {
                    setRadioButtons(options);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressBar.setVisibility(View.VISIBLE);
                if(seconds>0) {
                    setTimer(seconds);
                }
                break;
        }
    }
    public void imageClick(View v){
        setimageBackGroundTransparent();
        v.setBackgroundColor(getResources().getColor(R.color.lidgtGrey));
        selectedImage = v.getTag()+"";
    }

    public void setImage(String imageName){
        //questionImage.setImageResource(R.drawable.dotimage);
        //radioGroup.removeAllViews();
        questionImage.setVisibility(View.VISIBLE);
        Drawable image ;
        try {
            questionImage.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/" + imageName, "drawable", getPackageName())));
            answerImage1.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/" + imageName + "o1", "drawable", getPackageName())));
            answerImage2.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/" + imageName + "o2", "drawable", getPackageName())));
            answerImage3.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/" + imageName + "o3", "drawable", getPackageName())));
            answerImage5.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/" + imageName + "o5", "drawable", getPackageName())));
            answerImage6.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/" + imageName + "o6", "drawable", getPackageName())));
            answerImage4.setImageDrawable(getResources().getDrawable(getResources().getIdentifier("@drawable/" + imageName + "o4", "drawable", getPackageName())));
        }catch (Resources.NotFoundException e){
        }
        try{
            image = (getResources().getDrawable(getResources().getIdentifier("@drawable/"+imageName+"o7", "drawable", getPackageName())));
            answerImage7.setVisibility(View.VISIBLE);
            answerImage7.setImageDrawable(image);
        }catch(Resources.NotFoundException e){
            answerImage7.setVisibility(View.INVISIBLE);
            }
        try{
            image = (getResources().getDrawable(getResources().getIdentifier("@drawable/"+imageName+"o8", "drawable", getPackageName())));
            answerImage8.setVisibility(View.VISIBLE);
            answerImage8.setImageDrawable(image);
        }catch(Resources.NotFoundException e){
            answerImage8.setVisibility(View.INVISIBLE);
        }
        selectedImage = "";
        setimageBackGroundTransparent();
        setClickable(false);

    }
    public void setClickable(Boolean flag){
        answerImage1.setClickable(flag);
        answerImage2.setClickable(flag);
        answerImage3.setClickable(flag);
        answerImage4.setClickable(flag);
        answerImage5.setClickable(flag);
        answerImage6.setClickable(flag);
        answerImage7.setClickable(flag);
        answerImage8.setClickable(flag);
    }
    public void setimageBackGroundTransparent(){
        answerImage1.setBackgroundColor(getResources().getColor(R.color.transparent));
        answerImage2.setBackgroundColor(getResources().getColor(R.color.transparent));
        answerImage3.setBackgroundColor(getResources().getColor(R.color.transparent));
        answerImage4.setBackgroundColor(getResources().getColor(R.color.transparent));
        answerImage5.setBackgroundColor(getResources().getColor(R.color.transparent));
        answerImage6.setBackgroundColor(getResources().getColor(R.color.transparent));
        answerImage7.setBackgroundColor(getResources().getColor(R.color.transparent));
        answerImage8.setBackgroundColor(getResources().getColor(R.color.transparent));
    }
    public void setRadioButtons(JSONArray options) throws JSONException {
        radioGroup.removeAllViews();
        for(int i = 0;i<options.length();i++){
            JSONObject child = new JSONObject();
            child = options.getJSONObject(i);
            RadioButton btn1 = new RadioButton(this);
            btn1.setTextColor(getResources().getColor(R.color.darkgrey));
            btn1.setTextSize(16);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            layoutParams.setMargins(marginBottomPxl,marginBottomPxl,marginBottomPxl,marginBottomPxl);
            btn1.setLayoutParams(layoutParams);
            btn1.setText(child.getString("option_text"));
            radioGroup.addView( btn1);
        }
    }
    public void setTimer(final int seconds){
        countDownTimer = new CountDownTimer(seconds*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                progressBar.setProgress((int)((100*(millisUntilFinished / 1000))/seconds));
                //textView.setText(millisUntilFinished / 1000+"");
            }

            public void onFinish() {
                countDownTimer.cancel();
                progressBar.setVisibility(View.GONE);
                if(isAptitude){
                    isAptitude = false;
                    textViewQuestion1.setVisibility(View.GONE);
                    textViewQuestionInfo.setVisibility(View.GONE);
                    radioGroup.setVisibility(View.VISIBLE);
                    return;
                }
                if (isImage) {
                    isImage = false;
                    textViewQuestion1.setVisibility(View.GONE);
                    textViewQuestionInfo.setVisibility(View.GONE);
                    questionImage.setVisibility(View.GONE);
                    setClickable(true);
                    return;
                    //radioGroup.setVisibility(View.VISIBLE);
                }
                if (!message) {
                    gbl.countIncrement();
                }
                setQuestion();
                progressBar.setProgress(0);
            }
        }.start();

    }
    public void showMessage(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        //builder.set
        builder.setMessage(Message);
        //builder.show();
        AlertDialog dialog1 = builder.create();
        dialog1.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Window view = ((AlertDialog) dialog).getWindow();
                view.setBackgroundDrawableResource(R.color.alertBackGround);
            }
        });
        dialog1.show();
    }
}
