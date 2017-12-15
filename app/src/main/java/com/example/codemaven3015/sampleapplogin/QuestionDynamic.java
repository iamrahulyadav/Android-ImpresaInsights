package com.example.codemaven3015.sampleapplogin;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Calendar;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class QuestionDynamic extends AppCompatActivity {
    LinearLayout LLQuestion;
    int marginBottomPxl,marginBottomDp,imageSize;
    String radioselect = "",survey_ID;
    EditText tvQuestion;
    TextView clientId,sectionNameId,sectionDescription;
    Button next;
    GlobalVariables gbl;
    CheckBox checkboxOptional;
    DataBaseHealper db;
    ScrollView sc;
    String encodedImageString = "";
    int yearX,monthX,dayX;
    int date = 0;
    public static final int DILOG_ID=0;
    public boolean isDone = false;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public boolean isList = false;
    int groupId = 0;
    public JSONArray optionsOption = null;
    //JSONArray optionNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_dynamic);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        final TextView helloTextView = (TextView) findViewById(R.id.action_text);
        final ImageView imageView = (ImageView)findViewById(R.id.imageButton);
        imageView.setVisibility(View.GONE);
        db = new DataBaseHealper(this);
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        sc= (ScrollView)findViewById(R.id.scrollViewQuestions);
        sc.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        sc.setFocusable(true);
        sc.setFocusableInTouchMode(true);
        sc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocusFromTouch();
                return false;
            }
        });
        LLQuestion = (LinearLayout) findViewById(R.id.Questions);
        checkboxOptional = (CheckBox)findViewById(R.id.checkboxOptional) ;
        gbl = (GlobalVariables)getApplicationContext();
        clientId = (TextView)findViewById(R.id.clientId) ;
        sectionNameId = (TextView)findViewById(R.id.sectionNameId);
        sectionDescription = (TextView)findViewById(R.id.sectionDescription);
        helloTextView.setText(getIntent().getStringExtra("SURVEY_NAME"));
        if(gbl.getClientId().equals("new")) {
            clientId.setText("");
            clientId.setVisibility(View.GONE);
        }else{
            clientId.setText(gbl.getClientId());
            clientId.setVisibility(View.VISIBLE);
        }
        sectionNameId.setText("SECTION "+getIntent().getStringExtra("SECTION_NO")+": "+getIntent().getStringExtra("SECTION_NAME"));
        survey_ID = getIntent().getStringExtra("SURVEY_ID");
        isDone = getIntent().getBooleanExtra("isDONE",false);
        sectionDescription.setVisibility(View.GONE);
        final Calendar cal = Calendar.getInstance();
        yearX=cal.get(Calendar.YEAR);
        monthX=cal.get(Calendar.MONTH);
        dayX=cal.get(Calendar.DAY_OF_MONTH);
        next = (Button)findViewById(R.id.button_next);
        marginBottomDp = 10;
        float scale = getResources().getDisplayMetrics().density;
        marginBottomPxl = (int) (marginBottomDp * scale + 0.5f);
        imageSize = (int)(200 * scale + 0.5f);
        checkboxOptional.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int nViews = LLQuestion.getChildCount();
                //String questionId = "";
                    for (int i = 0;i<nViews;i++){
                        View child = LLQuestion.getChildAt(i);

                       if (child instanceof EditText || child instanceof RadioGroup ){
                            if(isChecked) {
                                child.setFocusable(false);
                                child.setFocusableInTouchMode(false);
                                child.setBackgroundColor(getResources().getColor(R.color.lidgtGrey));

                            }else {
                                child.setFocusable(true);
                                child.setFocusableInTouchMode(true);
                                if(child instanceof RadioGroup){
                                    child.setBackgroundColor(Color.TRANSPARENT);
                                }else {
                                    child.setBackgroundColor(getResources().getColor(R.color.white));
                                }
                            }
                        }

                    }

            }
        });
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            //mUser = savedInstanceState.getString(STATE_USER);
            Cursor section = db.getSectionList(getIntent().getStringExtra("SURVEY_ID"));
            gbl.setsectionList(section);
            Cursor question = db.getQuestionList(getIntent().getStringExtra("SECTION_ID"));
            gbl.setGlobalCursor(question);
            //Log.e("Saved",savedInstanceState.getInt("QUESTION_NO"));
            Log.e("Saved",savedInstanceState.getString("ANSWER"));
            try {
                JSONArray jsonObject = new JSONArray(savedInstanceState.getString("ANSWER"));
                gbl.setAnswerFromSavedInstance(jsonObject);
                //gbl.setQuestionSavedCounter(savedInstanceState.getInt("QUESTION_NO"));
                gbl.setSectionCount(savedInstanceState.getInt("SECTION_NO"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //gbl.setAnswerFromSavedInstance();

        }
        showQuestion();
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
    protected Dialog onCreateDialog(int id){
        if(id == DILOG_ID){
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,dpickerListner,yearX,monthX,dayX);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            return datePickerDialog;

        }else{
            return null;
        }
    }
    public DatePickerDialog.OnDateSetListener dpickerListner=
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    yearX = year;
                    monthX = month+1;
                    dayX = dayOfMonth;
                    if(date>0){
                        EditText edt = (EditText)findViewById(date);
                    edt.setText(yearX+"/"+monthX+"/"+dayX);
                    }
                }
            };
    public boolean emptyFieldValidation(String s){
        if(s.isEmpty()){
            return false;
        }else{
            return true;
        }
    }
    public boolean specialCharValidation(String s){
        Pattern regex = Pattern.compile("[$&+:;=\\\\?@#|/<>^*()%!-]");

        if (regex.matcher(s).find()) {
            return false;
        }else {
            return true;
        }
    }
    public boolean emailValidation(String email){
        if(!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return true;
        }else {
            return false;
        }
    }
    public boolean phoneValidation(String phone){
        if(!(phone.length()==10)){
//            if(phone.matches("^[+]?[0-9]{10,13}$")) {
//
//                return PhoneNumberUtils.isGlobalPhoneNumber(phone);
//            }else {
                return false;
            //}
        }else {
            return true;
        }

    }
    public boolean radioButtonCheck(){
        if(radioselect.isEmpty()){
            return true;
        }else{
            return true;
        }
    }
    public void onclickBack(View view){
        isList = false;
        optionsOption = null;
        view.setEnabled(false);
        view.setClickable(false);
        Cursor questionBack = gbl.getQuestionCursor();
        int questionCount = gbl.getCounter();
        Log.e("BACK",gbl.getCounter()+"");
        if(gbl.getCounter() < 1){
            onClickBackIfFirstQuestion();
            view.setEnabled(true);
            view.setClickable(true);
            return;
        }
        gbl.countDecrement();
        questionBack.moveToPosition(gbl.getCounter());
        String groupid = questionBack.getString(questionBack.getColumnIndex("GROUP_ID"));
        Log.e("BACK",gbl.getCounter()+"");
        if(groupid.equals("0")){
            gbl.countDecrement();
            if(gbl.getCounter() < 0){
                onClickBackIfFirstQuestion();
                gbl.countIncrement();
                gbl.countIncrement();
                view.setEnabled(true);
                view.setClickable(true);
                return;
            }
            //setQuestionData(gbl.getCounter());
        }else{
            Cursor group = db.getQuestionByGroupId(groupid);
            String desc = db.getGroupDesc(groupid);
            if(!desc.equals("")){
                sectionDescription.setVisibility(View.VISIBLE);
                sectionDescription.setText(desc);
            }else {
                sectionDescription.setVisibility(View.GONE);
            }
            int groupQuestionNumbers = group.getCount();
            Log.e("Check","lolol");
            for(int i = 1;i<=groupQuestionNumbers;i++){
                gbl.countDecrement();
                if(gbl.getCounter() < 1){
                    //showMessage("Info","FirstQuestion");
                    onClickBackIfFirstQuestion();
                    while (gbl.getCounter() < questionCount){
                        gbl.countIncrement();
                    }
                    //group.close();
                    view.setEnabled(true);
                    view.setClickable(true);
                    return;
                }
            }
            //group.close();

        }
        questionBack.moveToPosition(gbl.getCounter());
        groupid = questionBack.getString(questionBack.getColumnIndex("GROUP_ID"));
        if(groupid.equals("0")){
            deleteTextView();
            setQuestionData(gbl.getCounter(),"notGroup");
        }else {
            Cursor group = db.getQuestionByGroupId(groupid);
            String desc = db.getGroupDesc(groupid);
            String canSkip = db.can_SkipGroup(groupid);

            if(!desc.equals("")){
                sectionDescription.setVisibility(View.VISIBLE);
                sectionDescription.setText(desc);
            }else {
                sectionDescription.setVisibility(View.GONE);
            }
            int groupQuestionNumbers = group.getCount();
            for(int i = 1;i<groupQuestionNumbers;i++){
                gbl.countDecrement();
            }
            deleteTextView();
            for(int i = 1;i<=groupQuestionNumbers;i++){
                setQuestionData(gbl.getCounter(),"group");
            }
            if(canSkip.equals("1")){
                checkboxOptional.setVisibility(View.VISIBLE);
                if(gbl.ifexistGroup(groupid)){
                    checkboxOptional.setChecked(true);
                }else{
                    checkboxOptional.setChecked(false);
                }

            }else{
                checkboxOptional.setVisibility(View.INVISIBLE);
                checkboxOptional.setChecked(false);
            }
            if(checkboxOptional.getVisibility()==View.VISIBLE){
                deleteAllCheckBoxChild();
            }
            //group.close();

        }
        view.setEnabled(true);
        view.setClickable(true);
    }
    public void onClickBackIfFirstQuestion() {
        Cursor section = gbl.getSectionList();

        if(section.getCount()>0) {
            section.moveToFirst();
            //gbl.incrementSectionCount();
            if (gbl.getSectionCount() < section.getCount()) {
                section.moveToPosition(gbl.getSectionCount());
                Intent i = new Intent(QuestionDynamic.this, SurveySection.class);
                i.putExtra("SURVEY_NAME", getIntent().getStringExtra("SURVEY_NAME"));
                i.putExtra("SURVEY_ID", getIntent().getStringExtra("SURVEY_ID"));
                i.putExtra("SECTION_NAME", section.getString(2));
                i.putExtra("SECTION_ID", section.getString(0));
                i.putExtra("SECTION_NO", gbl.getSectionCount() + 1 + "");
                i.putExtra("SECTION_DESC", section.getString(3));
                i.putExtra("isDONE",isDone);
                startActivity(i);
            }
        }
        //section.close();
    }
    public void onClickSaveAndExit(View view){
        Log.e("SAVE",gbl.getAnswer().toString());
        Cursor section = gbl.getSectionList();
        if(section.getCount()>0) {
            section.moveToFirst();
            //gbl.incrementSectionCount();
            if (!(gbl.getSectionCount() < section.getCount()) && gbl.getCounter() >= gbl.getCount()) {
                if (getIntent().getStringExtra("SURVEY_ID").equals("1")) {
                    showMessageWithNoAndYes(getResources().getString(R.string.info), getResources().getString(R.string.saveAndExit), true,isDone);
                } else {
                    showMessageWithNoAndYes(getResources().getString(R.string.info), getResources().getString(R.string.saveAndExit), true,isDone);
                }
            } else {
                if (getIntent().getStringExtra("SURVEY_ID").equals("1")) {
                    showMessageWithNoAndYes(getResources().getString(R.string.info), getResources().getString(R.string.dataLost), false,isDone);


                } else {
                    showMessageWithNoAndYes(getResources().getString(R.string.info), getResources().getString(R.string.saveAndExit), true,isDone);
                }
            }
        }
    }
    public void showMessageWithNoAndYes(String title, String message, final boolean flag,final boolean isdone){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(flag){
                    if(gbl.getClientId().equals("new")){
                        db.updateAnswerInTable(gbl.getAnswer(),false,survey_ID,gbl.getClientId(),isdone,sharedPreferences.getString("project_id", ""));
                    }else{
                        db.deleteAnswerIfUpdated(gbl.getClientId());
                        db.updateAnswerInTable(gbl.getAnswer(),true,survey_ID,gbl.getClientId(),isdone,sharedPreferences.getString("project_id", ""));
                    }

                }else{
                    db.deleteRegistrationDetails("1");
                }
                Intent i = new Intent(QuestionDynamic.this , welcome.class);
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
    public void onClickNext(View view) throws JSONException {
        //showMessage("info","Next question");
        //progressDialog.show();
        isList = false;
        optionsOption = null;
        view.setEnabled(false);
        view.setClickable(false);
        int nViews = LLQuestion.getChildCount();
        Boolean emptyFlag = true;
        Boolean phoneFlag = true;
        Boolean emailFlag = true;
        Boolean radioFlag = true;
        Boolean imageFlag = true;
        Boolean splCharFlag = true;
        Boolean dateFlag = true;
        Boolean numericFlag = true;
        Boolean compareFlag = true;
        if(!((checkboxOptional.getVisibility() == View.VISIBLE) && (checkboxOptional.isChecked()))) {
            for (int i = 0; i < nViews; i++) {
                View child = LLQuestion.getChildAt(i);
                View chidCheckBox;
                boolean checkBoxFlag = false;
                if(i<(nViews-1)){
                    chidCheckBox = LLQuestion.getChildAt(i+1);
                    if((chidCheckBox instanceof CheckBox )&& (chidCheckBox.getVisibility()==View.VISIBLE) && ((CheckBox) chidCheckBox).isChecked()){
                        checkBoxFlag = true;
                    }
                }
                if(!checkBoxFlag) {
                    if (child.getVisibility() == View.VISIBLE) {
                        if (child instanceof EditText) {
                            EditText edt = (EditText) child;
                            String CompareQuestionId= ((List<String>)edt.getTag()).get(2).toString();
                            if(!CompareQuestionId.equals("")) {
                                String compareValue = getAnswerIfsaved(CompareQuestionId).getString("answer");
                                if(!compareValue.equals("__123__")){
                                    int comparevalueWith = Integer.parseInt(compareValue);
                                    int compareValueTo = Integer.parseInt(edt.getText().toString());
                                    if(comparevalueWith < compareValueTo){
                                        compareFlag = false;
                                    }
                                }
                            }
                            if (edt.getVisibility() == View.VISIBLE) {
                                if (emptyFlag) {
                                    emptyFlag = emptyFieldValidation(edt.getText().toString());
                                    if(!emptyFieldValidation(edt.getText().toString())){
                                        edt.setError("Field cannot be empty!!");
                                    }
                                }

                                if (edt.getInputType() != InputType.TYPE_CLASS_TEXT) {
                                    if (edt.getInputType() == InputType.TYPE_CLASS_PHONE) {
                                        if (phoneFlag) {

                                            phoneFlag = phoneValidation(edt.getText().toString());
                                            if(!phoneValidation(edt.getText().toString())){
                                                edt.setError("Phone number must be 10 digits long!!");
                                            }
                                        }

                                    } else if (edt.getInputType() == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS) {
                                        if (emailFlag)
                                            emailFlag = emailValidation(edt.getText().toString());
                                        if(!emailValidation(edt.getText().toString())){
                                            edt.setError("Please enter valid  email!!");
                                        }

                                    }else if(edt.getInputType() == InputType.TYPE_CLASS_NUMBER){
                                        if(numericFlag){
                                            numericFlag = TextUtils.isDigitsOnly(edt.getText());
                                        }
                                        if(!TextUtils.isDigitsOnly(edt.getText())){
                                            edt.setError("Please enter valid value!!");
                                        }
                                    }
                                }else{
                                    if (splCharFlag)
                                    splCharFlag = specialCharValidation(edt.getText().toString());
                                    if(!specialCharValidation(edt.getText().toString())){
                                        edt.setError("Please enter valid text!!");
                                    }
                                }if(edt.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE)){
                                    if (splCharFlag)
                                        splCharFlag = specialCharValidation(edt.getText().toString());
                                    if(!specialCharValidation(edt.getText().toString())){
                                        edt.setError("Please enter valid text!!");
                                    }
                                }
                            }
                        } else if (child instanceof RadioGroup) {
                            int checkedId = ((RadioGroup) child).getCheckedRadioButtonId();
                            if(checkedId>0){
                                radioFlag = true;
                            }else{
                                //((RadioGroup) child).set
                                radioFlag = false;
                            }
                            //boolean flag = true;
                            for(int k = 0;k<((RadioGroup) child).getChildCount();k++){
                                View childOfRadio = ((RadioGroup) child).getChildAt(k);
//                                if(flag){
//                                    if(childOfRadio instanceof RadioButton){
//                                        if(!radioFlag){
//                                            ((RadioButton)childOfRadio).setError("Select one from the options!!");
//                                            flag = false;
//                                        }
//                                    }
//                                }

                                if(childOfRadio instanceof EditText){
                                    if(childOfRadio.getVisibility()==View.VISIBLE){
                                        emptyFlag = emptyFieldValidation(((EditText) childOfRadio).getText().toString());
                                        if(!emptyFieldValidation(((EditText) childOfRadio).getText().toString())){
                                            ((EditText) childOfRadio).setError("Field cannot be empty!!");
                                        }
                                        EditText edt = (EditText) childOfRadio;
                                        if (edt.getInputType() != InputType.TYPE_CLASS_TEXT) {
                                            if (edt.getInputType() == InputType.TYPE_CLASS_PHONE) {
                                                if (phoneFlag) {

                                                    phoneFlag = phoneValidation(edt.getText().toString());
                                                }
                                                if(!phoneValidation(edt.getText().toString())){
                                                    edt.setError("Phone number must be 10 digits long!!");
                                                }

                                            } else if (edt.getInputType() == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS) {
                                                if (emailFlag)
                                                    emailFlag = emailValidation(edt.getText().toString());
                                                if(!emailValidation(edt.getText().toString())){
                                                    edt.setError("Please enter valid email!!");
                                                }

                                            }else if(edt.getInputType() == InputType.TYPE_CLASS_NUMBER){
                                                if(numericFlag){
                                                    numericFlag = TextUtils.isDigitsOnly(edt.getText());
                                                }
                                                if(!TextUtils.isDigitsOnly(edt.getText())){
                                                    edt.setError("Please enter valid value!!");
                                                }
                                            }
                                        }else{
                                            splCharFlag = specialCharValidation(edt.getText().toString());
                                            if(!specialCharValidation(edt.getText().toString())){
                                                edt.setError("Please enter valid text!!");
                                            }
                                        }

                                    }
                                }
                            }
                        }else if (child instanceof ImageView){
                            ImageView imageView = (ImageView)child;
                            if(imageView.getDrawable() == null){
                                imageFlag = false;
                            }
                        }
                    }
                }
            }
        }
        if (!emptyFlag) {
            showMessage(getResources().getString(R.string.info), getResources().getString(R.string.emptyField));
        }else if (!phoneFlag) {
            showMessage(getResources().getString(R.string.info), getResources().getString(R.string.validNumber));
        }else if (!emailFlag) {
            showMessage(getResources().getString(R.string.info), getResources().getString(R.string.validEmail));
        }else  if (!radioFlag) {
            showMessage(getResources().getString(R.string.info), getResources().getString(R.string.validRadio));
        }else if(!imageFlag){
            showMessage(getResources().getString(R.string.info), getResources().getString(R.string.validImage));
        }else if(!splCharFlag){
            showMessage(getResources().getString(R.string.info),"Please enter valid text");
        }else if(!numericFlag){
            showMessage(getResources().getString(R.string.info),"Please enter valid value");
        }else if(!dateFlag){
            showMessage(getResources().getString(R.string.info),"Please enter valid number of months");
        }else if(!compareFlag){
            showMessage("Info","Profit cannot be greater than sales. Please verify the amount is correct");
        }
        if (emptyFlag && phoneFlag && emailFlag && radioFlag && imageFlag && splCharFlag && dateFlag && numericFlag && compareFlag) {
            String questionPreviousId = SaveAnswerInJsonArray();
            if (gbl.getCounter() < gbl.getCount()) {
                //setAnswerJsonArray();
                Cursor question = gbl.getQuestionCursor();
                //deleteTextView();
                question.moveToPosition(gbl.getCounter());
                String groupId = question.getString(question.getColumnIndex("GROUP_ID"));
                Log.e("GROUP ID",groupId);
                if(groupId.equals("0")) {
                    deleteTextView();
                    setQuestionData(gbl.getCounter(),"notGroup");
                }else{
                    deleteTextView();
                    String previousGroupId = db.getGroupIdbyQuestionId(questionPreviousId);
                    if(checkboxOptional.isChecked() && checkboxOptional.getVisibility()==View.VISIBLE){

                        if(!gbl.ifexistGroup(previousGroupId)) {
                            gbl.setGroup(previousGroupId);
                        }
                    }else{
                        if(gbl.ifexistGroup(previousGroupId)){
                            gbl.removeGroup(previousGroupId);
                        }
                    }
                    setGroupData(groupId);
                    if(checkboxOptional.getVisibility()==View.VISIBLE){
                        deleteAllCheckBoxChild();
                    }
                }
            } else {
                Cursor section = gbl.getSectionList();
                if(section.getCount()>0) {
                    section.moveToFirst();
                    gbl.incrementSectionCount();
                    if (gbl.getSectionCount() < section.getCount()) {
                        section.moveToPosition(gbl.getSectionCount());
                        Intent i = new Intent(QuestionDynamic.this, SurveySection.class);
                        i.putExtra("SURVEY_NAME", getIntent().getStringExtra("SURVEY_NAME"));
                        i.putExtra("SURVEY_ID", getIntent().getStringExtra("SURVEY_ID"));
                        i.putExtra("SECTION_NAME", section.getString(2));
                        i.putExtra("SECTION_ID", section.getString(0));
                        i.putExtra("SECTION_NO", gbl.getSectionCount() + 1 + "");
                        i.putExtra("SECTION_DESC", section.getString(3));
                        i.putExtra("isDONE",isDone);
                        startActivity(i);
                    } else {
                        gbl.decrementSectionCount();
                        isDone = true;
                        showMessageWithNoAndYes(getResources().getString(R.string.tankYou), "Thank You "+gbl.getName()+getResources().getString(R.string.surveyCompleted), true,isDone);
                    }
                }
            }

        }
        view.setEnabled(true);
        view.setClickable(true);
    }
    public int ifAnswerExist(String questionId){
        JSONArray answer = gbl.getAnswer();
        for(int i=0;i<answer.length();i++){
            try {
                JSONObject obj = answer.getJSONObject(i);
                if(questionId.equals(obj.getString("question_no"))){
                    return i;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return -1;

    }

    public String SaveAnswerInJsonArray(){
        String questionId = "";
        int nViews = LLQuestion.getChildCount();
        boolean isSkiped = false;

        for (int i = 0; i < nViews; i++) {
            View child = LLQuestion.getChildAt(i);
            if (child instanceof EditText) {
                EditText edt = (EditText) child;
                String ans = "";
                isSkiped = false;
                if(!(checkboxOptional.getVisibility() == View.VISIBLE) || ((checkboxOptional.getVisibility() == View.VISIBLE) && !(checkboxOptional.isChecked()))){
                    ans = edt.getText().toString();
                }else{
                    isSkiped = true;
                }
                if(child.getVisibility() == View.GONE){
                    ans = "";
                }
//                if((edt.getInputType() == InputType.TYPE_CLASS_PHONE)){
//                    String firstLetter = ans.substring(0,1);
//                    if(!(firstLetter.equals("+"))){
//                        ans = "+"+ans;
//                    }
//                }

                JSONObject obj = new JSONObject();
                questionId= ((List<String>)edt.getTag()).get(0).toString();
                String ifRadio = ((List<String>)edt.getTag()).get(1).toString();

                    try {
                        int position = ifAnswerExist(questionId);
                        Log.e("POSITION",position+"");
                        if(ifRadio.equals("not_radio")) {
                            if (position >= 0) {
                                gbl.updateAtAnswer(position, ans, questionId, "",true,isSkiped);
                            } else {
                                obj.put("question_no", questionId);
                                obj.put("answer", ans);
                                obj.put("radio", "");
                                Log.e("ANSWER", obj.toString());
                                gbl.addAnswerInJsonArray(obj);
                            }
                        }else{
                            if (position >= 0) {
                                gbl.updateAtAnswer(position,ans, questionId, "radio",true,isSkiped);
                            } else {
                                obj.put("question_no", questionId);
                                obj.put("answer", ans);
                                obj.put("radio", "radio");
                                Log.e("ANSWER", obj.toString());
                                gbl.addAnswerInJsonArray(obj);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



            }else if(child instanceof RadioGroup){
                RadioGroup rbg = (RadioGroup) child;
                String ans = "";
                int childCount = rbg.getChildCount();
                for(int j =0;j<childCount;j++) {
                    View child1 = rbg.getChildAt(j);
                    isSkiped = false;
                    if (child1 instanceof EditText) {
                        EditText edt = (EditText) child1;
                        if (edt.getVisibility() == View.VISIBLE && child.getVisibility() != View.GONE){
                            ans = edt.getText().toString();
                        }else {
                            isSkiped = true;
                        }

                    }

                }
                String selectedtext = "";
                if(!(checkboxOptional.getVisibility() == View.VISIBLE) || ((checkboxOptional.getVisibility() == View.VISIBLE) && !(checkboxOptional.isChecked()))){
                    int id = rbg.getCheckedRadioButtonId();
                    RadioButton r = (RadioButton) findViewById(id);
                    if(!(r == null))
                    selectedtext = r.getText().toString();
                }
                JSONObject obj = new JSONObject();
                int position = ifAnswerExist(rbg.getTag().toString());
                questionId =rbg.getTag().toString();
                if(position>=0){
                    gbl.updateAtAnswer(position,ans ,rbg.getTag().toString(),selectedtext,true,isSkiped);
                }else{
                    try {
                        obj.put("question_no",rbg.getTag());
                        obj.put("answer",ans);
                        obj.put("radio",selectedtext);
                        Log.e("ANSWER",obj.toString());
                        gbl.addAnswerInJsonArray(obj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }else if(child instanceof ImageView){
                ImageView imageView = (ImageView)child;
                Bitmap bitmap =((BitmapDrawable)imageView.getDrawable()).getBitmap();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] b = baos.toByteArray();
                try {
                    encodedImageString = URLEncoder.encode(Base64.encodeToString(b, Base64.DEFAULT),"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                ;

                //Log.e("IMAGE",encodedImageString);
                int position = ifAnswerExist(imageView.getTag().toString());
                JSONObject obj = new JSONObject();
                questionId = imageView.getTag().toString();
                if(position >= 0){
                    gbl.updateAtAnswer(position,encodedImageString,imageView.getTag().toString(),"image",true,isSkiped);
                }else{
                    try {
                        obj.put("question_no",imageView.getTag());
                        obj.put("answer",encodedImageString);
                        obj.put("radio","image");
                        Log.e("ANSWER",obj.toString());
                        gbl.addAnswerInJsonArray(obj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

        }

        return questionId;
    }

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }
    public void setQuestionData(int onQuestion, String  isGroup){
        sc.fullScroll(View.FOCUS_UP);
        Cursor question = gbl.getQuestionCursor();
        String questionId, questionType, questionText,questionOrder,QuestionSectionSuggestion,isOptional,compare_with = "";
        JSONArray options = null;
        boolean optionFlag = false;
        question.moveToPosition(onQuestion);
        questionId = question.getString(0);
        questionType=question.getString(4);
        Log.e("TYPE",questionType);
        questionText = question.getString(5);
        QuestionSectionSuggestion = question.getString(6);
        questionOrder =question.getString(9);
        isOptional = question.getString(7);
        compare_with = question.getString(11);

        if(question.getString(8).equals("")){
            optionFlag = false;
        }else {
            try {
                options = new JSONArray(question.getString(8));
                optionFlag = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        Log.e("Question Id", questionId);
        createQuestionPage(isGroup,questionId,questionType,questionText,QuestionSectionSuggestion,isOptional,optionFlag,questionOrder,options,compare_with);

        gbl.countIncrement();


    }
    public void showQuestion(){
        Cursor question = gbl.getQuestionCursor();
        gbl.resetquestioncounter();

        if(question.getCount()>0) {
            question.moveToFirst();
            Log.e("CHECKK ", "move to first" + question.getColumnCount());
            Log.e("CHECKKK@@ ", "check size" + question.getCount());
            int count = gbl.getCount();
            if(getIntent().getStringExtra("FIRST").equals("first")) {
                int onQuestion = 0;
                question.moveToPosition(onQuestion);
                String groupId = question.getString(question.getColumnIndex("GROUP_ID"));
                Log.e("GROUP ID", groupId);
                if (groupId.equals("0")) {
                    deleteTextView();
                    setQuestionData(onQuestion, "notGroup");
                } else {
                    deleteTextView();
                    setGroupData(groupId);
                    if (checkboxOptional.getVisibility() == View.VISIBLE) {
                        deleteAllCheckBoxChild();
                    }
                    //showMessage("GROUPID",groupId);
                }
            }else{
                gbl.setQuestionCounter();
                gbl.countDecrement();
                int onQuestion = gbl.getCounter();
                if(question.moveToFirst()){
                    question.moveToPosition(onQuestion);
                    String groupId = question.getString(question.getColumnIndex("GROUP_ID"));
                    Log.e("GROUP ID", groupId);
                    if (groupId.equals("0")) {
                        deleteTextView();
                        setQuestionData(onQuestion, "notGroup");
                    } else {
                        deleteTextView();
                        Cursor group = db.getQuestionByGroupId(groupId);
                        String desc = db.getGroupDesc(groupId);
                        String canSkip = db.can_SkipGroup(groupId);


                        if(!desc.equals("")){
                            sectionDescription.setVisibility(View.VISIBLE);
                            sectionDescription.setText(desc);
                        }else {
                            sectionDescription.setVisibility(View.GONE);
                        }
                        int groupQuestionNumbers = group.getCount();
                        for(int i = 1;i<groupQuestionNumbers;i++){
                            gbl.countDecrement();
                        }
                        deleteTextView();
                        for(int i = 1;i<=groupQuestionNumbers;i++){
                            setQuestionData(gbl.getCounter(),"group");
                        }
                        if(canSkip.equals("1")){
                            checkboxOptional.setVisibility(View.VISIBLE);
                            if(gbl.ifexistGroup(groupId)){
                                checkboxOptional.setChecked(true);
                            }else{
                                checkboxOptional.setChecked(false);
                            }
                        }else{
                            checkboxOptional.setVisibility(View.INVISIBLE);
                            checkboxOptional.setChecked(false);
                        }
                        if(checkboxOptional.getVisibility()==View.VISIBLE){
                            deleteAllCheckBoxChild();
                        }
                    }
                }

            }
        }

    }
    public void deleteAllCheckBoxChild(){
        int nViews = LLQuestion.getChildCount();
        for (int i = 0; i < nViews; i++) {
            View child = LLQuestion.getChildAt(i);
            if (child instanceof CheckBox) {
                child.setVisibility(View.GONE);

            }
        }

    }
    public void setGroupData(String groupId){
        Cursor groupQuestion =db.getQuestionByGroupId(groupId);
        String desc = db.getGroupDesc(groupId);
        String canSkip = db.can_SkipGroup(groupId);


        if(!desc.equals("")){
            sectionDescription.setVisibility(View.VISIBLE);
            sectionDescription.setText(desc);
            }else {
            sectionDescription.setVisibility(View.GONE);
         }
         if(groupQuestion.getCount()>0) {
             groupQuestion.moveToFirst();
             Log.e("groupQuestion", groupQuestion.getCount() + "");
             for (int j = 0; j < groupQuestion.getCount(); j++) {
                 if (gbl.getCounter() < gbl.getCount()) {
                     setQuestionData(gbl.getCounter(), "group");
                 }

             }
         }
        if(canSkip.equals("1")){
            checkboxOptional.setVisibility(View.VISIBLE);
            if(gbl.ifexistGroup(groupId)){
                checkboxOptional.setChecked(true);
            }else{
                checkboxOptional.setChecked(false);
            }
        }else{
            checkboxOptional.setVisibility(View.INVISIBLE);
            checkboxOptional.setChecked(false);
        }
        //groupQuestion.close();
    }
    public void createQuestionPage(String isGroup,String questionId,String type,String  questionText,String QuestionSectionSuggestion,String isOptional,boolean optionFlag,String questionOrder,JSONArray options,String compare_with){
        createTextQuestionNo(questionOrder);
        createTextViewQuestion(questionText);

        //showMessage("FLAG"," "+optionFlag);
        if(!QuestionSectionSuggestion.isEmpty()){
            createTextViewQuestionInfo(QuestionSectionSuggestion);
        }
        Log.e("isOptional::",isOptional+"");
//        if(isGroup.equals("group") && isOptional.equals("1") ){
//            checkboxOptional.setVisibility(View.VISIBLE);
//            checkboxOptional.setChecked(false);
//        }
        boolean isSkipFlag = false;
        if(isOptional.equals("1")){
            isSkipFlag = true;
        }
        switch(type){
            case ("text"):
                createEditTextViewEmail(questionId,isSkipFlag,"text",compare_with);
                break;
            case("textarea"):
                createEditTextViewEmail(questionId,isSkipFlag,"textarea",compare_with);
                break;
            case("phone"):
                createEditTextViewEmail(questionId,isSkipFlag,"phone",compare_with);
                break;
            case("email"):
                createEditTextViewEmail(questionId,isSkipFlag,"email",compare_with);
                break;
            case("date"):
                createEditTextViewEmail(questionId,isSkipFlag,"date",compare_with);
                break;
            case("radio"):
                if(optionFlag) {
                    try {
                        if(isList){
                            createGroup(questionId);
                        }else{
                            createRadioButtonGroup(questionId,options,isSkipFlag);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case("image"):
                createImageView(questionId);
                break;
            case("numeric"):
                createEditTextViewEmail(questionId,isSkipFlag,"numeric",compare_with);
                break;
            case("yearmonth"):
                createEditTextViewEmail(questionId,isSkipFlag,"yearmonth",compare_with);
                break;
            case("year"):
                createEditTextViewEmail(questionId,isSkipFlag,"year",compare_with);
                break;
            case("month"):
                createEditTextViewEmail(questionId,isSkipFlag,"month",compare_with);
                break;
            default:
                //showMessage("type","type erro");
                break;
        }

    }
    public JSONObject getAnswerIfsaved(String questionId){
        JSONArray answer = gbl.getAnswer();
        JSONObject obj = new JSONObject();
        JSONObject obj1 = new JSONObject();
        for(int i=0;i<answer.length();i++){
            try {
                obj = answer.getJSONObject(i);
                Log.e("1234!@#$",obj.toString());
                if(questionId.equals(obj.getString("question_no"))){
                    Log.e("ANSWERCHECK1",obj.toString());
                    return obj;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        try {

            obj1.put("answer","__123__");
            obj1.put("radio","__123__");
            obj1.put("question_no",questionId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("ANSWERCHECK",obj1.toString());
        return obj1 ;

    }
    public void createTextQuestionNo(String questionOrder){
        TextView tvQuestion = new TextView(this);
        //tvQuestion.setId(@+id/1);
        tvQuestion.setText(questionOrder+".");
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        tvQuestion.setTextColor(getResources().getColor(R.color.darkgrey));
        tvQuestion.setTypeface(Typeface.DEFAULT_BOLD);
        tvQuestion.setTextSize(18);
        layoutParams.setMargins(0,0,0,marginBottomPxl);
        tvQuestion.setLayoutParams(layoutParams);
        LLQuestion.addView(tvQuestion);

    }
    public void createTextViewQuestion(String questionText){
        TextView tvQuestion = new TextView(this);
        tvQuestion.setText(questionText.trim());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        tvQuestion.setTextColor(getResources().getColor(R.color.darkgrey));
        //tvQuestion.setTypeface(Typeface.DEFAULT_BOLD);
        tvQuestion.setTextSize(16);
        layoutParams.setMargins(0,0,0,marginBottomPxl);
        tvQuestion.setLayoutParams(layoutParams);
        LLQuestion.addView(tvQuestion);

    }
    public void createTextViewQuestionInfo(String QuestionSectionSuggestion){
        TextView tvQuestion = new TextView(this);
        //tvQuestion.setId(@+id/1);
        tvQuestion.setText(QuestionSectionSuggestion.trim());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        tvQuestion.setTextColor(getResources().getColor(R.color.blue));
        tvQuestion.setTypeface(null,Typeface.ITALIC);
        tvQuestion.setTextSize(16);
        layoutParams.setMargins(0,0,0,marginBottomPxl);
        tvQuestion.setLayoutParams(layoutParams);
        LLQuestion.addView(tvQuestion);

    }

    public void createEditTextViewEmail(String questionId ,boolean isSkipFlag,String inputType,String compare_with){
        JSONObject obj = new JSONObject();
        obj = getAnswerIfsaved(questionId);
        String answer = "";
        boolean flagChecked = false;

        try {
            answer = obj.getString("answer");
            if(answer.equals("__123__")){
                answer = "";
            }else if(answer.equals("")){
                flagChecked = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final EditText tvQuestion = new EditText(this);
        Random r = new Random();
        tvQuestion.setId(r.nextInt(9999999));
        tvQuestion.setHint(getResources().getString(R.string.placeholder));
        tvQuestion.setBackgroundColor(getResources().getColor(R.color.white));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        tvQuestion.setTextColor(getResources().getColor(R.color.darkgrey));
        tvQuestion.setTextSize(16);
        if(answer != ""){
            tvQuestion.setText(answer);
        }
        layoutParams.setMargins(0,0,0,marginBottomPxl);
        switch(inputType){
            case "email":
                tvQuestion.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            case "date":
                tvQuestion.setInputType(InputType.TYPE_DATETIME_VARIATION_NORMAL);
                tvQuestion.setFocusable(false);
                tvQuestion.setHint(getResources().getString(R.string.selectDate));
                tvQuestion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        date = v.getId();
                        showDialog(DILOG_ID);
                        //tvQuestion.setText(date);
                    }
                });
                break;
            case "text":
                tvQuestion.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case "phone":
                tvQuestion.setInputType(InputType.TYPE_CLASS_PHONE);
                //tvQuestion.setHint(getResources().getString(R.string.placeholderPhone));
                int maxLength = 10;
                tvQuestion.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
                break;
            case "numeric":
                tvQuestion.setInputType(InputType.TYPE_CLASS_NUMBER);
                int maxLengthNumeric = 13;
                tvQuestion.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthNumeric)});
                break;
            case "textarea":
                tvQuestion.setSingleLine(false);
                tvQuestion.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                tvQuestion.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                tvQuestion.setLines(2);
                tvQuestion.setMaxLines(3);
                break;
            case "yearmonth":
                tvQuestion.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL);
                tvQuestion.setHint("Please select");
                tvQuestion.setFocusable(false);
                tvQuestion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showYearMonth(v,"yearmonth");
                    }
                });
                break;
            case "month":
                tvQuestion.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL);
                tvQuestion.setHint("Please select");
                tvQuestion.setFocusable(false);
                tvQuestion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showYearMonth(v,"month");
                    }
                });
                break;
            case "year":
                tvQuestion.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL);
                tvQuestion.setHint("Please select");
                tvQuestion.setFocusable(false);
                tvQuestion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showYearMonth(v,"year");
                    }
                });
                break;
            default:
                tvQuestion.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
        }
        List<String> data = new ArrayList<String>();
        String qCompareWith = "";
        if(!compare_with.equals("")){
            Cursor csr =db.getQuestionByQuestionOrder(compare_with,getIntent().getStringExtra("SECTION_NO"));
            csr.moveToFirst();
            if(csr.getCount()>0){
                qCompareWith = csr.getString(0);
            }
        }
        data.add(questionId);
        data.add("not_radio");
        data.add(qCompareWith);
        tvQuestion.setTag(data);
        tvQuestion.setPadding(marginBottomPxl,marginBottomPxl,marginBottomPxl,marginBottomPxl);
        tvQuestion.setLayoutParams(layoutParams);
        LLQuestion.addView(tvQuestion);
        if(isSkipFlag){
            //create check box
            createCheckBox(tvQuestion.getId(), flagChecked);
        }
    }
    public void createCheckBox(int parentId, boolean flagChecked){

        CheckBox checkBox = new CheckBox(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        layoutParams.setMargins(marginBottomPxl,marginBottomPxl,marginBottomPxl,marginBottomPxl);
        checkBox.setTextColor(getResources().getColor(R.color.darkgrey));
        checkBox.setTextSize(16);
        checkBox.setText("Skip");
        checkBox.setLayoutParams(layoutParams);
        checkBox.setTag(parentId);
        //checkBox.setChecked(false);
        checkBox.setVisibility(View.VISIBLE);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                View v1 = (View) findViewById((Integer)buttonView.getTag());
                //v.getFocusable();
                if(v1 instanceof EditText) {
                    EditText v = (EditText)v1;
                    if (isChecked) {
                        v.setFocusable(false);
                        v.setFocusableInTouchMode(false);
                        v.setBackgroundColor(getResources().getColor(R.color.lidgtGrey));
                        v.setText("");
                    } else {
                        v.setFocusable(true);
                        v.setFocusableInTouchMode(true);
                        v.setBackgroundColor(getResources().getColor(R.color.white));
                    }
                }
            }
        });
        if(flagChecked){
            checkBox.setChecked(true);
        }
        LLQuestion.addView(checkBox);

    }
    public void createImageView(String questionId){
        JSONObject obj = new JSONObject();
        obj = getAnswerIfsaved(questionId);
        String answer = "";

        try {
            answer = obj.getString("answer");
            if(answer.equals("__123__")){
                answer = "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Button btn = new Button(this);
        btn.setText(getResources().getString(R.string.selectPhoto));
        Random r = new Random();
        int id = (r.nextInt(9999));
        btn.setTag(id);
        float scale = getResources().getDisplayMetrics().density;
        int heightButtonPxl = (int) (50 * scale + 0.5f);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, heightButtonPxl);
        layoutParams.height = heightButtonPxl;
        btn.setTextSize(20);
        layoutParams.setMargins(marginBottomPxl, 0, marginBottomPxl, marginBottomPxl);
        btn.setLayoutParams(layoutParams);
        btn.setBackgroundColor(getResources().getColor(R.color.blue));
        btn.setTextColor(getResources().getColor(R.color.white));

        ImageView imageView = new ImageView(this);
        imageView.setTag(questionId);
        imageView.setId(id);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(imageSize,imageSize);
        layoutParams1.gravity = Gravity.CENTER_HORIZONTAL;
        imageView.setLayoutParams(layoutParams1);
        LLQuestion.addView(btn);
        LLQuestion.addView(imageView);
        String temp = "";
        if(answer != ""){
            try {
                temp = URLDecoder.decode(answer,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            byte[] bytarray = Base64.decode(temp, Base64.DEFAULT);
            Bitmap bmimage = BitmapFactory.decodeByteArray(bytarray, 0,
                    bytarray.length);
            imageView.setImageBitmap(bmimage);
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent();//(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.putExtra("crop", "true");
                i.putExtra("outputX", 100);
                i.putExtra("outputY", 100);
                i.putExtra("scale", true);
                i.putExtra("return-data", true);
                i.putExtra("id",(int)v.getTag());
               // i.p
                startActivityForResult(Intent.createChooser(i,"Select Picture"),(int)v.getTag());
            }
        });




    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode >0 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            int id = requestCode;//data.getStringExtra()
            Bitmap bitmap = null;
                bitmap = getScaledBitmap(uri);
                if(id>0) {
                    ImageView img = (ImageView) findViewById(id);
                    img.setImageBitmap(bitmap);
                }


        }
    }
    private Bitmap getScaledBitmap(Uri uri){
        Bitmap thumb = null ;
        try {
            ContentResolver cr = getContentResolver();
            InputStream in = cr.openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize=4;
            thumb = BitmapFactory.decodeStream(in,null,options);
        } catch (FileNotFoundException e) {
        }
        return thumb ;
    }
    public void createRadioButtonGroup(String questionId,final JSONArray options,boolean isSkipFlag) throws JSONException {
        JSONObject obj = new JSONObject();
        obj = getAnswerIfsaved(questionId);
        String answer = "";
        String radioValue = "";

        try {
            answer = obj.getString("answer");
            radioValue = obj.getString("radio");
            if(answer.equals("__123__")){
                answer = "";
                radioValue = "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("Options",options.toString());
        RadioGroup group = new RadioGroup(this);
        group.setOrientation(RadioGroup.VERTICAL);
        group.setTag(questionId);
        Random r = new Random();
        group.setId(r.nextInt(9999999));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        layoutParams.setMargins(marginBottomPxl,marginBottomPxl,marginBottomPxl,marginBottomPxl);
        isList = false;
        optionsOption = null;
        for(int i=0;i<options.length();i++) {
            RadioButton btn1 = new RadioButton(this);
            JSONObject child = new JSONObject();
            child = options.getJSONObject(i);
            btn1.setId(r.nextInt(10000));
            Log.e("OPTIONS OBJECT",child.toString());
            btn1.setTextColor(getResources().getColor(R.color.darkgrey));
            btn1.setTextSize(16);
            group.setLayoutParams(layoutParams);
            btn1.setText(child.getString("option_text"));
            btn1.setTag(child.getString("response_type"));
            if(radioValue.equals(child.getString("option_text"))) {
                btn1.setChecked(true);
            }
            if(!btn1.getTag().equals("radio") && !( btn1.getTag().equals("radiolist"))){
                tvQuestion = new EditText(this);
                tvQuestion.setHint(getResources().getString(R.string.placeholder));
                tvQuestion.setBackgroundColor(getResources().getColor(R.color.white));
                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                tvQuestion.setTextColor(getResources().getColor(R.color.darkgrey));
                tvQuestion.setId(r.nextInt(10000));
                tvQuestion.setTextSize(16);
                List<String> data = new ArrayList<String>();
                data.add(questionId);
                data.add("yes_radio");
                tvQuestion.setTag(data);
                layoutParams1.setMargins(0,0,0,marginBottomPxl);
                //tvQuestion.setInputType(InputType.TYPE_CLASS_TEXT);
                switch(child.getString("response_type")){
                    case "email":
                        tvQuestion.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        break;
                    case "date":
                        tvQuestion.setInputType(InputType.TYPE_DATETIME_VARIATION_NORMAL);
                        tvQuestion.setFocusable(false);
                        tvQuestion.setHint(getResources().getString(R.string.selectDate));
                        tvQuestion.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                date = v.getId();
                                showDialog(DILOG_ID);
                                //tvQuestion.setText(date);
                            }
                        });
                        break;
                    case "text":
                        tvQuestion.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    case "phone":
                        tvQuestion.setInputType(InputType.TYPE_CLASS_PHONE);
                        int maxLength = 10;
                        //tvQuestion.setHint(getResources().getString(R.string.placeholderPhone));
                        tvQuestion.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
                        break;
                    case "numeric":
                        tvQuestion.setInputType(InputType.TYPE_CLASS_NUMBER);
                        int maxLengthNumeric = 13;
                        tvQuestion.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthNumeric)});
                        break;
                    case "textarea":
                        tvQuestion.setSingleLine(false);
                        tvQuestion.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                        tvQuestion.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        tvQuestion.setLines(2);
                        tvQuestion.setMaxLines(3);
                        break;
                    case "yearmonth":
                        tvQuestion.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL);
                        tvQuestion.setHint("Please select");
                        tvQuestion.setFocusable(false);
                        tvQuestion.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showYearMonth(v,"yearmonth");
                            }
                        });
                        break;
                    case "month":
                        tvQuestion.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL);
                        tvQuestion.setHint("Please select");
                        tvQuestion.setFocusable(false);
                        tvQuestion.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showYearMonth(v,"month");
                            }
                        });
                        break;
                    case "year":
                        tvQuestion.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL);
                        tvQuestion.setHint("Please select");
                        tvQuestion.setFocusable(false);
                        tvQuestion.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showYearMonth(v,"year");
                            }
                        });
                        break;
                    default:
                        tvQuestion.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                }
                tvQuestion.setPadding(marginBottomPxl,marginBottomPxl,marginBottomPxl,marginBottomPxl);
                tvQuestion.setLayoutParams(layoutParams1);
                btn1.setTag(tvQuestion.getId());
                group.addView(btn1);
                if(radioValue.equals(child.getString("option_text"))){
                    tvQuestion.setText(obj.getString("answer"));
                    tvQuestion.setVisibility(View.VISIBLE);
                }else {
                    tvQuestion.setVisibility(View.GONE);
                }
                group.addView(tvQuestion);
            }else{
                if((child.getString("response_type")).equals("radiolist")){
                    isList = true;

                }

                if(radioValue.equals(child.getString("option_text"))){
                    //btn1.setChecked(true);
                    if(isList) {
                        optionsOption = new JSONArray();
                        optionsOption =child.getJSONArray("sub_option");
                    }
                }
                group.addView(btn1);
            }
        }
        //radioselect = "";


        LLQuestion.addView(group);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton rb= (RadioButton) findViewById(checkedId);
                radioselect = rb.getText().toString();
//                if(survey_ID.equals("4")){
//                    //String ans = getAnswerIfsaved(questionPreviousId).getString("answer");
//                    if(!(radioselect.equalsIgnoreCase("yes"))){
//                        gbl.countIncrement();
//                    }
//                }
                //gbl.setRadioInputTextCheckToZero();
                if(rb.getTag().equals("radiolist")){
                    for(int i=0;i<options.length();i++) {
                        JSONObject child = new JSONObject();
                        try {
                            child = options.getJSONObject(i);
                            if(child.getString("option_text").equals(radioselect)){
                                setNextQuestionOptions(child.getJSONArray("sub_option"),groupId);
                                //setNextQuestionOptions(options,groupId);
                                break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }else if(!rb.getTag().equals("radio")){
                    int i = (Integer)rb.getTag();
                    View v = (View) findViewById(i);
                    if(v instanceof EditText) {
                        EditText  edt = (EditText)v;
                        //gbl.setRadioInputTextCheck(i);
                        edt.setVisibility(View.VISIBLE);
                        edt.setFocusable(true);
                    }
                }else{
                    int childCount = group.getChildCount();
                    for(int i =0;i<childCount;i++){
                        View child  = group.getChildAt(i);
                        if(child instanceof EditText){
                            EditText edt = (EditText)child;
                            edt.setVisibility(View.GONE);
                        }
                    }
                }

            }
        });
    }
    public void createGroup(String questionid ) throws JSONException {
        JSONObject obj = new JSONObject();
       //Log.e("Options",options.toString());
        RadioGroup group = new RadioGroup(this);
        group.setOrientation(RadioGroup.VERTICAL);
        group.setTag(questionid);
        Random r = new Random();
        groupId = r.nextInt(9999999);
        group.setId(groupId);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        layoutParams.setMargins(marginBottomPxl,marginBottomPxl,marginBottomPxl,marginBottomPxl);
        LLQuestion.addView(group);
        if(!(optionsOption == null)){
            setNextQuestionOptions(optionsOption,groupId);
        }
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton rb= (RadioButton) findViewById(checkedId);
                radioselect = rb.getText().toString();
            }
        });

    }
    public void setNextQuestionOptions(JSONArray options,int group_Id) throws JSONException {
        //question_id;
        RadioGroup group = (RadioGroup)findViewById(group_Id);
        group.removeAllViews();
        JSONObject obj = new JSONObject();
        obj = getAnswerIfsaved(group.getTag().toString());
        String answer = "";
        String radioValue = "";

        try {
            answer = obj.getString("answer");
            radioValue = obj.getString("radio");
            if(answer.equals("__123__")){
                answer = "";
                radioValue = "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(int i=0;i<options.length();i++) {
            RadioButton btn1 = new RadioButton(this);
            JSONObject child = new JSONObject();
            child = options.getJSONObject(i);
            //btn1.setId(r.nextInt(10000));
            //Log.e("OPTIONS OBJECT", child.toString());
            btn1.setTextColor(getResources().getColor(R.color.darkgrey));
            btn1.setTextSize(16);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            layoutParams.setMargins(marginBottomPxl,marginBottomPxl,marginBottomPxl,marginBottomPxl);
            group.setLayoutParams(layoutParams);
            btn1.setText(child.getString("option_text"));
            if(radioValue.equals(child.getString("option_text"))) {
                btn1.setChecked(true);
            }
            group.addView( btn1);
        }


    }
    public void deleteTextView(){
        int nViews = LLQuestion.getChildCount();
        for (int i = 0; i < nViews; i++) {
            View child = LLQuestion.getChildAt(i);
            if (child instanceof RadioGroup) {
                RadioGroup rbg = (RadioGroup)child;
                //rbg.clearCheck();
                 rbg.removeAllViews();

            }
        }

        LLQuestion.removeAllViews();
    }
    public void showMessage(String title, String Message){
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
                Window view = ((AlertDialog)dialog).getWindow();
                view.setBackgroundDrawableResource(R.color.alertBackGround);
            }
        });
        dialog1.show();

    }
    public void showYearMonth(final View v,String type){
        final android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(QuestionDynamic.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.number_picker_dialog, null);
        d.setTitle("");
        d.setMessage(" ");
        d.setView(dialogView);
        final NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.dialog_number_picker);
        final NumberPicker numberPicker2 = (NumberPicker) dialogView.findViewById(R.id.numberPicker2);
        final TextView textView2 = (TextView)dialogView.findViewById(R.id.textView2);
        switch (type){
            case "year":
                numberPicker.setVisibility(View.GONE);
                textView2.setVisibility(View.GONE);
                numberPicker2.setMaxValue(2018);
                numberPicker2.setMinValue(1900);
                break;
            case "month":
                numberPicker.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.VISIBLE);
                numberPicker.setMaxValue(12);
                numberPicker.setMinValue(1);
                numberPicker2.setMaxValue(2018);
                numberPicker2.setMinValue(1900);
                break;
            case "yearmonth":
                numberPicker.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.VISIBLE);
                numberPicker.setMaxValue(12);
                numberPicker.setMinValue(0);
                numberPicker2.setMaxValue(99);
                numberPicker2.setMinValue(0);
                break;
        }

        numberPicker.setWrapSelectorWheel(false);

        numberPicker2.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                Log.e("Check", "onValueChange: ");

            }
        });
        numberPicker2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                Log.e("Check", "onValueChange: ");

            }
        });
        d.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.e("Check", "onClick: " + numberPicker.getValue());
                String text = "";
                if(numberPicker.getVisibility() == View.VISIBLE){
                    text = numberPicker2.getValue()+" / "+numberPicker.getValue();
                }else {
                    text = numberPicker2.getValue()+"";
                }

                ((EditText) v).setText(text);
            }
        });
        d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        android.app.AlertDialog alertDialog = d.create();
        alertDialog.show();
    }
}
