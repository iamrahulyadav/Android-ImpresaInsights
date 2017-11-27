package com.example.codemaven3015.sampleapplogin;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.R.attr.id;
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
    //ProgressDialog progressDialog;


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
        clientId.setText("");
        clientId.setVisibility(View.GONE);
        sectionNameId.setText("SECTION "+getIntent().getStringExtra("SECTION_NO")+": "+getIntent().getStringExtra("SECTION_NAME"));
        survey_ID = getIntent().getStringExtra("SURVEY_ID");
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
        showQuestion();
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
    public boolean emailValidation(String email){
        if(!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return true;
        }else {
            return false;
        }
    }
    public boolean phoneValidation(String phone){
        if(phone.length()>8){
            return true;
        }else {
            return false;
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
        Cursor questionBack = gbl.getQuestionCursor();
        int questionCount = gbl.getCounter();
        Log.e("BACK",gbl.getCounter()+"");
        if(gbl.getCounter() < 1){
            onClickBackIfFirstQuestion();
            return;
        }
        gbl.countDecrement();
        questionBack.moveToPosition(gbl.getCounter());
        String groupid = questionBack.getString(questionBack.getColumnIndex("GROUP_ID"));
        Log.e("BACK",gbl.getCounter()+"");
        if(groupid.equals("0")){
            gbl.countDecrement();
            if(gbl.getCounter() < 1){
                onClickBackIfFirstQuestion();
                gbl.countIncrement();
                gbl.countIncrement();
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
            if(checkboxOptional.getVisibility()==View.VISIBLE){
                deleteAllCheckBoxChild();
            }
            //group.close();

        }
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
                    showMessageWithNoAndYes("Info", "Are you sure you want to Save and Exit?", true);
                } else {
                    showMessageWithNoAndYes("Info", "Are you sure you want to Save and Exit?", true);
                }
            } else {
                if (getIntent().getStringExtra("SURVEY_ID").equals("1")) {
                    showMessageWithNoAndYes("Info", "Are you sure you want to exit? All the data will be lost", false);


                } else {
                    showMessageWithNoAndYes("Info", "Are you sure you want to Save and Exit?", true);
                }
            }
        }
    }
    public void showMessageWithNoAndYes(String title, String message, final boolean flag){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(flag){
                    db.updateAnswerInTable(gbl.getAnswer(),false,survey_ID);
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
    public void onClickNext(View view) {
        //showMessage("info","Next question");
        //progressDialog.show();
        int nViews = LLQuestion.getChildCount();
        Boolean emptyFlag = true;
        Boolean phoneFlag = true;
        Boolean emailFlag = true;
        Boolean radioFlag = true;
        Boolean imageFlag = true;
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
                            if (edt.getVisibility() == View.VISIBLE) {
                                if (emptyFlag)
                                    emptyFlag = emptyFieldValidation(edt.getText().toString());
                                if (edt.getInputType() != InputType.TYPE_CLASS_TEXT) {
                                    if (edt.getInputType() == InputType.TYPE_CLASS_PHONE) {
                                        if (phoneFlag) {

                                            phoneFlag = phoneValidation(edt.getText().toString());
                                        }

                                    } else if (edt.getInputType() == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS) {
                                        if (emailFlag)
                                            emailFlag = emailValidation(edt.getText().toString());

                                    }
                                }
                            }
                        } else if (child instanceof RadioGroup) {
                            int checkedId = ((RadioGroup) child).getCheckedRadioButtonId();
                            if(checkedId>0){
                                radioFlag = true;
                            }else{
                                radioFlag = false;
                            }
                            for(int k = 0;k<((RadioGroup) child).getChildCount();k++){
                                View childOfRadio = ((RadioGroup) child).getChildAt(k);
                                if(childOfRadio instanceof EditText){
                                    if(childOfRadio.getVisibility()==View.VISIBLE){
                                        emptyFlag = emptyFieldValidation(((EditText) childOfRadio).getText().toString());

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
            showMessage("Info", "Field cannot be empty!!");
        }
        if (!phoneFlag) {
            showMessage("Info", "Enter valid  phone number!!");
        }
        if (!emailFlag) {
            showMessage("Info", "Enter valid email!!");
        }
        if (!radioFlag) {
            showMessage("Info", "Select one from the options");
        }
        if(!imageFlag){
            showMessage("Info", "Select photo from the gallery");
        }
        if (emptyFlag && phoneFlag && emailFlag && radioFlag && imageFlag) {
            SaveAnswerInJsonArray();
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
                        startActivity(i);
                    } else {
                        //gbl.countIncrement();
                        gbl.decrementSectionCount();
                        showMessageWithNoAndYes("ThankYou", "Survey is completed. Do you want to Save And Exit?", true);
                        //showMessage("Info","Survey is completed");
                    }
                }
                //section.close();
            }

        }
        //progressDialog.hide();
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

    public void SaveAnswerInJsonArray(){
        int nViews = LLQuestion.getChildCount();
        for (int i = 0; i < nViews; i++) {
            View child = LLQuestion.getChildAt(i);
            if (child instanceof EditText) {
                EditText edt = (EditText) child;
                String ans = "";
                if(!(checkboxOptional.getVisibility() == View.VISIBLE) && !(checkboxOptional.isChecked())){
                    ans = edt.getText().toString();
                }
                if(child.getVisibility() == View.GONE){
                    ans = "";
                }
                if((edt.getInputType() == InputType.TYPE_CLASS_PHONE)){
                    String firstLetter = ans.substring(0,1);
                    if(!(firstLetter.equals("+"))){
                        ans = "+"+ans;
                    }
                }

                JSONObject obj = new JSONObject();
                String questionId= ((List<String>)edt.getTag()).get(0).toString();
                String ifRadio = ((List<String>)edt.getTag()).get(1).toString();

                    try {
                        int position = ifAnswerExist(questionId);
                        Log.e("POSITION",position+"");
                        if(ifRadio.equals("not_radio")) {
                            if (position >= 0) {
                                gbl.updateAtAnswer(position, ans, questionId, "");
                            } else {
                                obj.put("question_no", questionId);
                                obj.put("answer", ans);
                                obj.put("radio", "");
                                Log.e("ANSWER", obj.toString());
                                gbl.addAnswerInJsonArray(obj);
                            }
                        }else{
                            if (position >= 0) {
                                gbl.updateAtAnswer(position,ans, questionId, "radio");
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
                    if (child1 instanceof EditText) {
                        EditText edt = (EditText) child1;
                        if (edt.getVisibility() == View.VISIBLE && child.getVisibility() != View.GONE){
                            ans = edt.getText().toString();
                        }
                    }

                }
                String selectedtext = "";
                if(!(checkboxOptional.getVisibility() == View.VISIBLE) && !(checkboxOptional.isChecked())) {
                    int id = rbg.getCheckedRadioButtonId();
                    RadioButton r = (RadioButton) findViewById(id);
                    //RadioButton r = (RadioButton) rbg.getChildAt(id);

                    selectedtext = r.getText().toString();
                }
                JSONObject obj = new JSONObject();
                int position = ifAnswerExist(rbg.getTag().toString());
                if(position>=0){
                    gbl.updateAtAnswer(position,ans ,rbg.getTag().toString(),selectedtext);
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
                if(position >= 0){
                    gbl.updateAtAnswer(position,encodedImageString,imageView.getTag().toString(),"image");
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

    }

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }
    public void setQuestionData(int onQuestion, String  isGroup){
        sc.fullScroll(View.FOCUS_UP);
        Cursor question = gbl.getQuestionCursor();
        String questionId, questionType, questionText,questionOrder,QuestionSectionSuggestion,isOptional;
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
        createQuestionPage(isGroup,questionId,questionType,questionText,QuestionSectionSuggestion,isOptional,optionFlag,questionOrder,options);

        gbl.countIncrement();


    }
    public void showQuestion(){
        Cursor question = gbl.getQuestionCursor();
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
        //groupQuestion.close();
    }
    public void createQuestionPage(String isGroup,String questionId,String type,String  questionText,String QuestionSectionSuggestion,String isOptional,boolean optionFlag,String questionOrder,JSONArray options){
        createTextQuestionNo(questionOrder);
        createTextViewQuestion(questionText);
        checkboxOptional.setVisibility(View.GONE);
        checkboxOptional.setChecked(false);
        //showMessage("FLAG"," "+optionFlag);
        if(!QuestionSectionSuggestion.isEmpty()){
            createTextViewQuestionInfo(QuestionSectionSuggestion);
        }
        Log.e("isOptional::",isOptional+"");
        if(isGroup.equals("group") && isOptional.equals("1") ){
            checkboxOptional.setVisibility(View.VISIBLE);
            checkboxOptional.setChecked(false);
        }
        boolean isSkipFlag = false;
//        if(isOptional.equals("1") ){
//            isSkipFlag = true;
//        }
        switch(type){
            case ("text"):
                createEditTextViewEmail(questionId,isSkipFlag,"text");
                break;
            case("textarea"):
                createEditTextViewEmail(questionId,isSkipFlag,"textarea");
                break;
            case("phone"):
                createEditTextViewEmail(questionId,isSkipFlag,"phone");
                break;
            case("email"):
                createEditTextViewEmail(questionId,isSkipFlag,"email");
                break;
            case("date"):
                createEditTextViewEmail(questionId,isSkipFlag,"date");
                break;
            case("radio"):
                if(optionFlag) {
                    try {
                        createRadioButtonGroup(questionId,options);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case("image"):
                createImageView(questionId);
                break;
            case("numeric"):
                createEditTextViewEmail(questionId,isSkipFlag,"numeric");
                break;
            case("yearmonth"):
                createEditTextViewEmail(questionId,isSkipFlag,"yearmonth");
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
    public void createEditTextViewPlain(String questionId,Boolean isSkipFlag){
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

        tvQuestion = new EditText(this);
        //tvQuestion.setId(@+id/1);
        tvQuestion.setHint("Please specify");
        tvQuestion.setBackgroundColor(getResources().getColor(R.color.white));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        tvQuestion.setTextColor(getResources().getColor(R.color.darkgrey));
        tvQuestion.setTextSize(16);
        List<String> data = new ArrayList<String>();
        data.add(questionId);
        data.add("not_radio");
        tvQuestion.setTag(data);
        if(answer != ""){
            tvQuestion.setText(answer);
        }
        layoutParams.setMargins(0,0,0,marginBottomPxl);
        tvQuestion.setInputType(InputType.TYPE_CLASS_TEXT);
        tvQuestion.setPadding(marginBottomPxl,marginBottomPxl,marginBottomPxl,marginBottomPxl);
        tvQuestion.setLayoutParams(layoutParams);
        Random r = new Random();
        tvQuestion.setId(r.nextInt(9999999));
        LLQuestion.addView(tvQuestion);
        if(isSkipFlag){
            //create check box
            createCheckBox(tvQuestion.getId(),flagChecked);
        }
    }
    public void createEditTextViewPhone(String questionId ,boolean isSkipFlag){
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
        EditText tvQuestion = new EditText(this);
        //tvQuestion.setId(@+id/1);
        tvQuestion.setHint("Please specify");
        tvQuestion.setBackgroundColor(getResources().getColor(R.color.white));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        tvQuestion.setTextColor(getResources().getColor(R.color.darkgrey));
        //tvQuestion.setTypeface(Typeface.DEFAULT_BOLD);
        tvQuestion.setTextSize(16);
        if(answer != ""){
            tvQuestion.setText(answer);
        }
        layoutParams.setMargins(0,0,0,marginBottomPxl);
        tvQuestion.setInputType(InputType.TYPE_CLASS_PHONE);
        List<String> data = new ArrayList<String>();
        data.add(questionId);
        data.add("not_radio");
        tvQuestion.setTag(data);
        tvQuestion.setPadding(marginBottomPxl,marginBottomPxl,marginBottomPxl,marginBottomPxl);
        tvQuestion.setLayoutParams(layoutParams);
        Random r = new Random();
        tvQuestion.setId(r.nextInt(9999999));
        LLQuestion.addView(tvQuestion);
        if(isSkipFlag){
            //create check box
            createCheckBox(tvQuestion.getId(),flagChecked);
        }
    }
    public void createEditTextViewEmail(String questionId ,boolean isSkipFlag,String inputType){
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
        //tvQuestion.setId(@+id/1);
        tvQuestion.setHint("Please specify");
        tvQuestion.setBackgroundColor(getResources().getColor(R.color.white));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        tvQuestion.setTextColor(getResources().getColor(R.color.darkgrey));
        //tvQuestion.setTypeface(Typeface.DEFAULT_BOLD);
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
                tvQuestion.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
                tvQuestion.setFocusable(false);
                tvQuestion.setHint("Select Date");
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
                int maxLength = 14;
                tvQuestion.setHint("Please enter phone number with country code");
                tvQuestion.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
                break;
            case "numeric":
                tvQuestion.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case "textarea":
                tvQuestion.setSingleLine(false);
                tvQuestion.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                tvQuestion.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                tvQuestion.setLines(2);
                tvQuestion.setMaxLines(3);
                break;
            case "yearmonth":
                tvQuestion.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
                tvQuestion.setHint("YY/MM");
                break;
            default:
                tvQuestion.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
        }
        List<String> data = new ArrayList<String>();
        data.add(questionId);
        data.add("not_radio");
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
        btn.setText("Select Photo");
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
           // try {
                //bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                //int countChild = LLQuestion.getChildCount();
                bitmap = getScaledBitmap(uri);
                if(id>0) {
                    ImageView img = (ImageView) findViewById(id);
                    img.setImageBitmap(bitmap);
                }

//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        }
    }
    private Bitmap getScaledBitmap(Uri uri){
        Bitmap thumb = null ;
        try {
            ContentResolver cr = getContentResolver();
            InputStream in = cr.openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize=7;
            thumb = BitmapFactory.decodeStream(in,null,options);
        } catch (FileNotFoundException e) {
        }
        return thumb ;
    }
    public void createRadioButtonGroup(String questionId,JSONArray options) throws JSONException {
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
            //if(child.getString("response_type").equals("text")){
            btn1.setTag(child.getString("response_type"));
            //}else{
               // btn1.setTag("radio");
            //}

            if(radioValue.equals(child.getString("option_text"))){
                btn1.setChecked(true);
            }

            if(!btn1.getTag().equals("radio")){
                tvQuestion = new EditText(this);
                tvQuestion.setHint("Please specify");
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
                        tvQuestion.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
                        tvQuestion.setFocusable(false);
                        tvQuestion.setHint("Select Date");
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
                        int maxLength = 14;
                        tvQuestion.setHint("Please enter phone number with country code");
                        tvQuestion.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
                        break;
                    case "numeric":
                        tvQuestion.setInputType(InputType.TYPE_CLASS_NUMBER);
                        break;
                    case "textarea":
                        tvQuestion.setSingleLine(false);
                        tvQuestion.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                        tvQuestion.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        tvQuestion.setLines(2);
                        tvQuestion.setMaxLines(3);
                        break;
                    case "yearmonth":
                        tvQuestion.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
                        tvQuestion.setHint("YY/MM");
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
                //gbl.setRadioInputTextCheckToZero();
                if(!rb.getTag().equals("radio")){
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
                    //gbl.setRadioInputTextCheckToZero();
                }
//                if(gbl.getRadioInputTextCheck()>0 && !rb.getTag().equals(gbl.getRadioInputTextCheck())){
//                    EditText edt = (EditText)findViewById(gbl.getRadioInputTextCheck());
//                    edt.setVisibility(View.GONE);
//                    gbl.setRadioInputTextCheckToZero();
//                }
            }
        });
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

}
