package com.example.codemaven3015.sampleapplogin;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
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
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class QuestionDynamic extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
    LinearLayout LLQuestion;
    int marginBottomPxl,marginBottomDp,imageSize;
    String radioselect = "",survey_ID,section_id;
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
    Cursor question, section;
    boolean if_oquestionid_4c = false;
    boolean if_skip_5b = false;
    boolean if_skip_5c = false;
    boolean if_question_4a = false;
    boolean skip_4b_4c = false;
    boolean skip_4b = false;
    boolean if_question_5a = false;
    boolean if_survey_Products_2c = false;
    boolean if_sumQuestion_b = false;
    boolean if_sumQuestion_c = false;
    boolean if_question_6 = false, flagCreatedInputField = false;
    int value_b=0,value_c=0;
    String questionOrder = "";
    int SumOfAllQuestion = 0;
    String answer2a = "";
    boolean flagToswitchNextScreen = false;
    private DrawerLayout mDrawerLayout;
    ExpandableListAdapter mMenuAdapter;
    ExpandableListView expandableList;
    List<ExpandedMenuModel> listDataHeader;
    HashMap<ExpandedMenuModel, List<String>> listDataChild;
    //JSONArray optionNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_dynamic);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        final TextView helloTextView = (TextView) findViewById(R.id.action_text);
        final ImageView imageView = (ImageView)findViewById(R.id.imageButton);
        //imageView.setVisibility(View.GONE);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionMenu();
            }
        });
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        gbl.setClientId(getIntent().getStringExtra("CLIENT"));
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
        section_id = getIntent().getStringExtra("SECTION_ID");
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
            Log.e("Saved",savedInstanceState.getString("ANSWER"));
            try {
                JSONArray jsonObject = new JSONArray(savedInstanceState.getString("ANSWER"));
                gbl.setAnswerFromSavedInstance(jsonObject);
                //gbl.setQuestionSavedCounter(savedInstanceState.getInt("QUESTION_NO"));
                gbl.setSectionCount(savedInstanceState.getInt("SECTION_NO"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(!(gbl.popular_product_answer == null)) {
                gbl.popular_product_answer.clear();
            }
            gbl.popular_product_answer = savedInstanceState.getStringArrayList("LIST");

        }
        question = db.getQuestionList(section_id);
        section = db.getSectionList(getIntent().getStringExtra("SURVEY_ID"));

        showQuestion();
    }
    @Override
    protected void onResume() {
        question = db.getQuestionList(section_id);
        question.moveToFirst();
        section = db.getSectionList(getIntent().getStringExtra("SURVEY_ID"));
        section.moveToFirst();
        super.onResume();
    }
    @Override
    protected void onPause() {
        question.close();
        section.close();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("QUESTION_NO",gbl.getCounter());
        savedInstanceState.putString("ANSWER",gbl.getAnswer()+"");
        savedInstanceState.putInt("SECTION_NO",gbl.getSectionCount());
        savedInstanceState.putStringArrayList("LIST",gbl.popular_product_answer);
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
    public String setDescriptionValue(String desc){
        if(survey_ID.toLowerCase().equals("9")){
            desc = desc.replace("[2A]", answer2a);
            desc = desc.replace("[3A]", answer2a);
            desc = desc.replace("[4A]", answer2a);
        }
        return desc;
    }
    public void setDescriptionText(String desc){
        if (!desc.equals("")) {
            sectionDescription.setVisibility(View.VISIBLE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                sectionDescription.setText(Html.fromHtml(setDescriptionValue(desc),Html.FROM_HTML_MODE_LEGACY));
            } else {
                sectionDescription.setText(Html.fromHtml(setDescriptionValue(desc)));
            }
        } else {
            sectionDescription.setVisibility(View.GONE);
        }
    }
    public void onclickBack(View view){
        view.setEnabled(false);
        view.setClickable(false);
        if(survey_ID.equals("11")){
            if(questionOrder.toLowerCase().equals("9e")){
                if(getAnswervalue("9c").trim().toLowerCase().equals("no"))
                    gbl.countDecrement();
            }
            if(questionOrder.toLowerCase().equals("9c")){
                if(getAnswervalue("9a").trim().toLowerCase().equals("no"))
                gbl.countDecrement();
            }
        }
        if(flagCreatedInputField){
            flagCreatedInputField = false;
            deleteTextView();
            gbl.countDecrement();
            setQuestionData(gbl.getCounter(), "notGroup");
        }
        else {
            flagCreatedInputField = false;
            isList = false;
            optionsOption = null;
            if (if_question_5a) {
                if_question_5a = false;
                while (gbl.getCounter() > 1) {
                    gbl.countDecrement();
                }

            }

            //Cursor questionBack = gbl.getQuestionCursor();
            int questionCount = gbl.getCounter();
            Log.e("BACK", gbl.getCounter() + "");
            if (gbl.getCounter() < 1) {
                onClickBackIfFirstQuestion();
                view.setEnabled(true);
                view.setClickable(true);
                return;
            }
            gbl.countDecrement();
            question.moveToPosition(gbl.getCounter());
            String groupid = question.getString(question.getColumnIndex("GROUP_ID"));
            Log.e("BACK", gbl.getCounter() + "");
            if (groupid.equals("0")) {
                gbl.countDecrement();
                if (gbl.getCounter() < 0) {
                    onClickBackIfFirstQuestion();
                    gbl.countIncrement();
                    gbl.countIncrement();
                    view.setEnabled(true);
                    view.setClickable(true);
                    return;
                }
                //setQuestionData(gbl.getCounter());
            } else {
                Cursor group = db.getQuestionByGroupId(groupid);
                String desc = db.getGroupDesc(groupid);
                setDescriptionText(desc);
                int groupQuestionNumbers = group.getCount();
                Log.e("Check", "lolol");
                for (int i = 1; i <= groupQuestionNumbers; i++) {
                    gbl.countDecrement();
                    if (gbl.getCounter() < 1) {
                        //showMessage("Info","FirstQuestion");
                        onClickBackIfFirstQuestion();
                        while (gbl.getCounter() < questionCount) {
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
            question.moveToPosition(gbl.getCounter());
            groupid = question.getString(question.getColumnIndex("GROUP_ID"));
            if (groupid.equals("0")) {
                deleteTextView();
                setQuestionData(gbl.getCounter(), "notGroup");
            } else {
                Cursor group = db.getQuestionByGroupId(groupid);
                String desc = db.getGroupDesc(groupid);
                String canSkip = db.can_SkipGroup(groupid);

                setDescriptionText(desc);
                int groupQuestionNumbers = group.getCount();
                for (int i = 1; i < groupQuestionNumbers; i++) {
                    gbl.countDecrement();
                }
                deleteTextView();
                for (int i = 1; i <= groupQuestionNumbers; i++) {
                    setQuestionData(gbl.getCounter(), "group");
                }
                if (canSkip.equals("1")) {
                    checkboxOptional.setVisibility(View.VISIBLE);
                    if (gbl.ifexistGroup(groupid)) {
                        checkboxOptional.setChecked(true);
                    } else {
                        checkboxOptional.setChecked(false);
                    }

                } else {
                    checkboxOptional.setVisibility(View.INVISIBLE);
                    checkboxOptional.setChecked(false);
                }
                if (checkboxOptional.getVisibility() == View.VISIBLE) {
                    deleteAllCheckBoxChild();
                }
                //group.close();

            }

        }
        view.setEnabled(true);
        view.setClickable(true);
    }
    public void onClickBackIfFirstQuestion() {
        //Cursor section = gbl.getSectionList();

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
                i.putExtra("CLIENT",getIntent().getStringExtra("CLIENT"));
                startActivity(i);
            }
        }
        //section.close();
    }
    public void onClickSaveAndExit(View view){
        Log.e("SAVE",gbl.getAnswer().toString());
        //Cursor section = gbl.getSectionList();
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
                        db.deleteAnswerIfUpdated(gbl.getClientId(),survey_ID);
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
        if(if_question_4a) {
            if (skip_4b_4c) {
                gbl.countIncrement();
                gbl.countIncrement();
            }
            if (skip_4b) {
                gbl.countIncrement();
            }
        }
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
        String answerOFEditText = "";
        Boolean yearFlag = true;
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
                            answerOFEditText = edt.getText().toString();
                            if(survey_ID.equals("9")){
                                if(questionOrder.toLowerCase().equals("2c")){

                                }
                                if("2c 2d 3c 3d 4c 4d".toLowerCase().contains(questionOrder.toLowerCase())){
                                    int value = 0;
                                    try {
                                        value = Integer.parseInt(answerOFEditText);
                                    }catch(NumberFormatException e){}
                                    SumOfAllQuestion = value +SumOfAllQuestion;
                                }
                            }
                            String CompareQuestionId = "";
                            if(!flagCreatedInputField) {
                                CompareQuestionId = ((List<String>) edt.getTag()).get(2).toString();
                            }
                            if(!CompareQuestionId.equals("")) {
                                String compareValue = getAnswerIfsaved(CompareQuestionId).getString("answer");
                                if(!compareValue.equals("__123__")){
                                    int comparevalueWith = 0, compareValueTo = 0;
                                    try {
                                        comparevalueWith = Integer.parseInt(compareValue);
                                        compareValueTo = Integer.parseInt(edt.getText().toString());
                                    }catch(NumberFormatException e){}
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
                                    }else if (edt.getInputType() == InputType.TYPE_DATETIME_VARIATION_DATE){
                                        if(yearFlag){
                                            yearFlag = checkYearValidity(edt.getText().toString());
                                            if(!yearFlag){
                                                edt.setError("Please enter valid year!!");
                                            }
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
            showMessage(getResources().getString(R.string.Error), getResources().getString(R.string.emptyField));
        }else if (!phoneFlag) {
            showMessage(getResources().getString(R.string.Error), getResources().getString(R.string.validNumber));
        }else if (!emailFlag) {
            showMessage(getResources().getString(R.string.Error), getResources().getString(R.string.validEmail));
        }else  if (!radioFlag) {
            showMessage(getResources().getString(R.string.Error), getResources().getString(R.string.validRadio));
        }else if(!imageFlag){
            showMessage(getResources().getString(R.string.Error), getResources().getString(R.string.validImage));
        }else if(!splCharFlag){
            showMessage(getResources().getString(R.string.Error),"Please enter valid text");
        }else if(!numericFlag){
            showMessage(getResources().getString(R.string.Error),"Please enter valid value");
        }else if(!dateFlag){
            showMessage(getResources().getString(R.string.Error),"Please enter valid number of months");
        }else if(!compareFlag){
            showMessage("Error","Profit cannot be greater than sales. Please verify the amount is correct");
        }else if(!yearFlag){
            showMessage("Error","Please enter valid year!!");
        }
        if(survey_ID.equals("9")){
            if("2c 2d 3c 3d 4c 4d".toLowerCase().contains(questionOrder.toLowerCase())){
                if(!(SumOfAllQuestion == 100)) {
                    SumOfAllQuestion = 0;
                    showMessage("Error", "Total Should be 100%");
                    view.setEnabled(true);
                    view.setClickable(true);
                    return;
                }
            }
            SumOfAllQuestion = 0;
        }
        if (yearFlag && emptyFlag && phoneFlag && emailFlag && radioFlag && imageFlag && splCharFlag && dateFlag && numericFlag && compareFlag) {
            String questionPreviousId = "";
            flagToswitchNextScreen = false;
            questionPreviousId =SaveAnswerInJsonArray();
            if(flagToswitchNextScreen){
                flagToswitchNextScreen = false;
                return;
            }

            if(survey_ID.equals("10")&& !flagCreatedInputField && !(getIntent().getStringExtra("SECTION_NAME")).toLowerCase().equals("summary")){
                int value =0;
                try{
                    value = Integer.parseInt(answerOFEditText);
                }catch(NumberFormatException e){}

                if(getIntent().getStringExtra("SECTION_NO").equals("1"))
                    gbl.value_3d = value;
                if(getIntent().getStringExtra("SECTION_NO").equals("2"))
                    gbl.value_4d = value;
                if(getIntent().getStringExtra("SECTION_NO").equals("3"))
                    gbl.value_5d = value;
                if(value>0) {
                    flagCreatedInputField = true;
                    view.setEnabled(true);
                    view.setClickable(true);
                    //gbl.countIncrement();
                    createEmployeeInputFields(value);
                    return;
                }
            }if(flagCreatedInputField){
                flagCreatedInputField = false;
            }
            if (gbl.getCounter() < gbl.getCount()) {
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
                //Cursor section = gbl.getSectionList();
                //Cursor section = db.getSectionList(getIntent().getStringExtra("SURVEY_ID"));
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
                        i.putExtra("CLIENT",getIntent().getStringExtra("CLIENT"));
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
    public boolean checkYearValidity(String value){
        int year = 0;
        int current_year = Calendar.getInstance().get(Calendar.YEAR);
        try{
            year = Integer.parseInt(value);
        }catch (NumberFormatException e){
            return false;
        }
        if((current_year - year)<17)
            return false;
        if((current_year - year)>100)
            return false;
        return true;
    }
    public void createEmployeeInputFields(int number) throws JSONException {
        LLQuestion.removeAllViews();
        JSONArray checkAnswerIfExist = new JSONArray();
        JSONObject obj = getAnswerIfsaved(getIntent().getStringExtra("SECTION_NAME"));
        String answer = obj.getString("answer");
        if(answer.equals("__123__")) {
            answer = "";
        }
        if(!answer.equals("")) {
            checkAnswerIfExist = new JSONArray(answer);
        }
        for (int i = 0; i < number; i++) {
            String answerText = "";
            createQuestionTextAndInputField(i,checkAnswerIfExist);
        }
    }
    public void createQuestionTextAndInputField(int i,JSONArray  checkAnswerIfExist){
        int count = i;
        while (count>19){
            count = count - 19;
        }
        String[] numberString ={"FIRST","SECOND","THIRD","FOURTH","FIFTH","SIXTH","SEVENTH","EIGHTH","NINTH","TENTH","ELEVENTH","TWELFTH",
                "THIRTEEN","FOURTEENTH","FIFTEEN","SIXTEENTH","SEVENTEENTH","EIGHTEENTH","NINETEENTH","TWENTIETH"};
        String[] alfa = {"b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
        String QuestionText = "Please enter the names of your "+numberString[count] +" "+ getIntent().getStringExtra("SECTION_NAME")+" employee";
        String questionOrderTemp = getIntent().getStringExtra("SECTION_NO") +alfa[count];
        createTextQuestionNo(questionOrderTemp, QuestionText);
        try {
            createInputField("emp_"+getIntent().getStringExtra("SECTION_NAME")+"_name"+(i+1),checkAnswerIfExist);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void createInputField(String tagText,JSONArray checkAnswerIfExist) throws JSONException {
        String ans = "";
        for (int i = 0; i<checkAnswerIfExist.length();i++){
            try {
                ans = checkAnswerIfExist.getJSONObject(i).getString(tagText);
            }catch(JSONException e){
                Log.e("check","check");
            }
        }

        EditText tvQuestion = new EditText(this);
        tvQuestion.setHint(getResources().getString(R.string.placeholder));
        tvQuestion.setBackgroundColor(getResources().getColor(R.color.white));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        tvQuestion.setTextColor(getResources().getColor(R.color.darkgrey));
        tvQuestion.setTextSize(16);
        tvQuestion.setTag(tagText);
        tvQuestion.setText(ans);
        tvQuestion.setInputType(InputType.TYPE_CLASS_TEXT);
        layoutParams.setMargins(0,0,0,marginBottomPxl);
        tvQuestion.setPadding(marginBottomPxl,marginBottomPxl,marginBottomPxl,marginBottomPxl);
        tvQuestion.setLayoutParams(layoutParams);
        LLQuestion.addView(tvQuestion);
    }
    public void moveToFirstQuestion(){
        section.moveToFirst();
        while(gbl.getSectionCount()>=0) {
            gbl.decrementSectionCount();
        }
        Intent i = new Intent(QuestionDynamic.this, SurveySection.class);
        i.putExtra("SURVEY_NAME", getIntent().getStringExtra("SURVEY_NAME"));
        i.putExtra("SURVEY_ID", getIntent().getStringExtra("SURVEY_ID"));
        i.putExtra("SECTION_NAME", section.getString(2));
        i.putExtra("SECTION_ID", section.getString(0));
        i.putExtra("SECTION_NO", "1");
        i.putExtra("SECTION_DESC", section.getString(3));
        i.putExtra("isDONE",false);
        i.putExtra("CLIENT",getIntent().getStringExtra("CLIENT"));
        startActivity(i);

    }
    public void moveToNextSection(){
        flagToswitchNextScreen = true;
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
            i.putExtra("isDONE", isDone);
            i.putExtra("CLIENT", getIntent().getStringExtra("CLIENT"));
            startActivity(i);
        }
    }
    public String SaveAnswerInJsonArray(){
        String questionId = "";
        int nViews = LLQuestion.getChildCount();
        boolean isSkiped = false;
        JSONArray answerInputFields = new JSONArray();
        for (int i = 0; i < nViews; i++) {
            View child = LLQuestion.getChildAt(i);
            if (child instanceof EditText) {
                EditText edt = (EditText) child;
                if(flagCreatedInputField){
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put(edt.getTag().toString(),edt.getText());
                        answerInputFields.put(obj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                else {

                    String ans = "";
                    isSkiped = false;
                    if (!(checkboxOptional.getVisibility() == View.VISIBLE) || ((checkboxOptional.getVisibility() == View.VISIBLE) && !(checkboxOptional.isChecked()))) {
                        ans = edt.getText().toString();
                    } else {
                        isSkiped = true;
                    }
                    if (child.getVisibility() == View.GONE) {
                        ans = "";
                    }
                    if(edt.getInputType()==InputType.TYPE_NUMBER_FLAG_DECIMAL){
                        ans = ans.replace(",","");
                    }
                    if (if_survey_Products_2c) {
                        gbl.popular_product_answer.add(ans);
                    }
                    try{
                        if (if_sumQuestion_b) {
                            value_b = Integer.parseInt(ans);
                        }
                        if (if_sumQuestion_c) {
                            value_c = Integer.parseInt(ans);
                        }
                        if (survey_ID.equals("8") && questionOrder.equals("3d")) {
                            gbl.value_3d = Integer.parseInt(ans);
                        }
                        if (survey_ID.equals("8") && questionOrder.toLowerCase().equals("4d")) {
                            gbl.value_4d = Integer.parseInt(ans);
                        }
                        if (survey_ID.equals("8") && questionOrder.toLowerCase().equals("5d")) {
                            gbl.value_5d = Integer.parseInt(ans);
                        }
                    }catch(NumberFormatException e){}
                    if(survey_ID.equals("9")){
                        if("2a , 3a, 4a".contains(questionOrder.toLowerCase()))
                            answer2a = ans;
                        try {
                            if (questionOrder.toLowerCase().equals("2b"))
                                gbl.value_3d = Integer.parseInt(ans);
                            if (questionOrder.toLowerCase().equals("3b"))
                                gbl.value_4d = Integer.parseInt(ans);
                            if (questionOrder.toLowerCase().equals("4b"))
                                gbl.value_5d = Integer.parseInt(ans);
                        }catch(NumberFormatException e){}
                    }
                    JSONObject obj = new JSONObject();
                    questionId = ((List<String>) edt.getTag()).get(0).toString();
                    String ifRadio = ((List<String>) edt.getTag()).get(1).toString();

                    try {
                        int position = ifAnswerExist(questionId);
                        Log.e("POSITION", position + "");
                        if (ifRadio.equals("not_radio")) {
                            if (position >= 0) {
                                gbl.updateAtAnswer(position, ans, questionId, "", true, isSkiped);
                            } else {
                                obj.put("question_no", questionId);
                                obj.put("answer", ans);
                                obj.put("radio", "");
                                Log.e("ANSWER", obj.toString());
                                gbl.addAnswerInJsonArray(obj);
                            }
                        } else {
                            if (position >= 0) {
                                gbl.updateAtAnswer(position, ans, questionId, "radio", true, isSkiped);
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

                    if(survey_ID.equals("11") && ans.trim().toLowerCase().equals("0")){
                        if("2a 3a 4a 5a 6a 7a 8a ".contains(questionOrder.toLowerCase())){
                            moveToNextSection();
                        }

                    }
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
                            if(edt.getInputType()==InputType.TYPE_NUMBER_FLAG_DECIMAL){
                                ans = ans.replace(",","");
                            }
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
                if(if_question_6 && selectedtext.trim().toLowerCase().equals("no")){
                    moveToFirstQuestion();
                }
                if(survey_ID.equals("9") && questionOrder.equals("5") && selectedtext.trim().toLowerCase().equals("no")) {
                    moveToFirstQuestion();
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
                if(survey_ID.equals("11") && selectedtext.trim().toLowerCase().equals("no")){
                    if(questionOrder.toLowerCase().equals("10")){
                        moveToFirstQuestion();
                    }
                    if(questionOrder.toLowerCase().equals("1a")){
                        moveToNextSection();
                    }
                    if(questionOrder.toLowerCase().equals("9a")||questionOrder.toLowerCase().equals("9c")){
                        gbl.countIncrement();
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
        if(flagCreatedInputField){
            int position = ifAnswerExist(getIntent().getStringExtra("SECTION_NAME"));
            if(position>=0){
                gbl.updateAtAnswer(position,answerInputFields.toString(),getIntent().getStringExtra("SECTION_NAME"),"",true,isSkiped);
            }else {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("question_no", getIntent().getStringExtra("SECTION_NAME"));
                    obj.put("answer", answerInputFields.toString());
                    obj.put("radio", "");
                    Log.e("ANSWER", obj.toString());
                    gbl.addAnswerInJsonArray(obj);
                } catch (JSONException e) {

                }
            }
            questionId = getIntent().getStringExtra("SECTION_NAME");
        }
        return questionId;
    }

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }
    public void setQuestionData(int onQuestion, String  isGroup){
        sc.fullScroll(View.FOCUS_UP);
        //Cursor question = gbl.getQuestionCursor();
        String questionId, questionType, questionText,QuestionSectionSuggestion,isOptional,compare_with = "";
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
        //Cursor question = gbl.getQuestionCursor();
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
                        setDescriptionText(desc);
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
        setDescriptionText(desc);
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
        if((if_skip_5b && questionOrder.toLowerCase().equals("5b")) ||(if_skip_5c && questionOrder.toLowerCase().equals("5c"))){
            return;
        }else{
            createTextQuestionNo(questionOrder, questionText);
            if(survey_ID.equals("1")) {
                if (questionOrder.toLowerCase().equals("4c")) {
                    if_oquestionid_4c = true;
                } else {
                    if_oquestionid_4c = false;

                }
                if (questionOrder.toLowerCase().equals("4a")) {
                    if_question_4a = true;
                } else {
                    if_question_4a = false;
                }
                if ((questionOrder.toLowerCase().equals("5a") || questionOrder.toLowerCase().equals("5b") || questionOrder.toLowerCase().equals("5c"))) {
                        if_question_5a = true;
                } else {
                    if_question_5a = false;
                }
            }
            if(survey_ID.equals("8")||survey_ID.equals("10")) {
                if (questionOrder.toLowerCase().equals("6")) {
                    if_question_6 = true;
                } else {
                    if_question_6 = false;
                }
            }else
                if_question_6 = false;
             if(survey_ID.equals("8")&& questionOrder.toLowerCase().equals("2c")){
                 if_survey_Products_2c = true;
                 if(!(gbl.popular_product_answer == null))
                     gbl.popular_product_answer.clear();
             }else{
                 if_survey_Products_2c = false;
             }
            if(survey_ID.equals("8")){
                 if("3b 4b 5b ".contains(questionOrder.toLowerCase())){
                    if_sumQuestion_b = true;
                }else{
                    if_sumQuestion_b = false;
                }
                if("3c 4c 5c ".contains(questionOrder.toLowerCase())){
                    if_sumQuestion_c = true;
                }else{
                    if_sumQuestion_c = false;
                }
            }else {
                if_sumQuestion_b = false;
                if_sumQuestion_c = false;
            }
            if (!QuestionSectionSuggestion.isEmpty()) {
                createTextViewQuestionInfo(QuestionSectionSuggestion);
            }
            Log.e("isOptional::", isOptional + "");
            boolean isSkipFlag = false;
            if (isOptional.equals("1")) {
                isSkipFlag = true;
            }
            switch (type) {
                case("gps"):
                    createEditTextViewEmail(questionId, isSkipFlag, "gps", compare_with);
                    break;
                case ("text"):
                    createEditTextViewEmail(questionId, isSkipFlag, "text", compare_with);
                    break;
                case ("textarea"):
                    createEditTextViewEmail(questionId, isSkipFlag, "textarea", compare_with);
                    break;
                case ("phone"):
                    createEditTextViewEmail(questionId, isSkipFlag, "phone", compare_with);
                    break;
                case ("email"):
                    createEditTextViewEmail(questionId, isSkipFlag, "email", compare_with);
                    break;
                case ("date"):
                    createEditTextViewEmail(questionId, isSkipFlag, "date", compare_with);
                    break;
                case ("radio"):
                    if (optionFlag) {
                        try {
                            if (isList) {
                                createGroup(questionId);
                            } else {
                                createRadioButtonGroup(questionId, options, isSkipFlag);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case ("image"):
                    createImageView(questionId);
                    break;
                case ("numeric"):
                    createEditTextViewEmail(questionId, isSkipFlag, "numeric", compare_with);
                    break;
                case ("yearmonth"):
                    createEditTextViewEmail(questionId, isSkipFlag, "yearmonth", compare_with);
                    break;
                case ("year"):
                    createEditTextViewEmail(questionId, isSkipFlag, "year", compare_with);
                    break;
                case ("month"):
                    createEditTextViewEmail(questionId, isSkipFlag, "month", compare_with);
                    break;
                default:
                    //showMessage("type","type erro");
                    createEditTextViewEmail(questionId, isSkipFlag, type, compare_with);
                    break;
            }
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

    public String setTotalInSummary(String questionText){
        //int answer2c = getQuestionByQuestionOrder()
        int land = getAnswerFromQuestionOrder("1c");
        int building = getAnswerFromQuestionOrder("2c");
        building = building + getAnswerFromQuestionOrder("2e");
        building = building + getAnswerFromQuestionOrder("2g");
        building = building + getAnswerFromQuestionOrder("2h");
        int largerVehicel = getAnswerFromQuestionOrder("3c");
        largerVehicel = largerVehicel + getAnswerFromQuestionOrder("3e");
        largerVehicel = largerVehicel + getAnswerFromQuestionOrder("3g");
        largerVehicel = largerVehicel + getAnswerFromQuestionOrder("3h");
        int smallVehicel = getAnswerFromQuestionOrder("4c");
        smallVehicel = smallVehicel + getAnswerFromQuestionOrder("4e");
        smallVehicel = smallVehicel + getAnswerFromQuestionOrder("4g");
        smallVehicel = smallVehicel + getAnswerFromQuestionOrder("4h");
        int machine = getAnswerFromQuestionOrder("5c");
        machine = machine + getAnswerFromQuestionOrder("5e");
        machine = machine + getAnswerFromQuestionOrder("5g");
        machine = machine + getAnswerFromQuestionOrder("5h");
        int tools = getAnswerFromQuestionOrder("6c");
        tools = tools + getAnswerFromQuestionOrder("6e");
        tools = tools + getAnswerFromQuestionOrder("6g");
        tools = tools + getAnswerFromQuestionOrder("6h");
        int it = getAnswerFromQuestionOrder("7c");
        it = it + getAnswerFromQuestionOrder("7e");
        it = it + getAnswerFromQuestionOrder("7g");
        it = it + getAnswerFromQuestionOrder("7h");
        int fe = getAnswerFromQuestionOrder("8c");
        fe = fe + getAnswerFromQuestionOrder("8e");
        fe = fe + getAnswerFromQuestionOrder("8g");
        fe = fe + getAnswerFromQuestionOrder("8h");
        int workingCapital = getAnswerFromQuestionOrder("9b");
        workingCapital = workingCapital + getAnswerFromQuestionOrder("9d");
        workingCapital = workingCapital + getAnswerFromQuestionOrder("9e");

        questionText = questionText.replace("[LAND]", land+"");
        questionText = questionText.replace("[BUILDINGS]", building+"");
        questionText = questionText.replace("[LARGE VEHICLES]", largerVehicel+"");
        questionText = questionText.replace("[SMALL VEHICLES]", smallVehicel+"");
        questionText = questionText.replace("[MACHINES]", machine+"");
        questionText = questionText.replace("[TOOLS]", tools+"");
        questionText = questionText.replace("[IT EQUIPMENT]", it+"");
        questionText = questionText.replace("[FURNITURE & ELECTRONICS]", fe+"");
        questionText = questionText.replace("[WORKING CAPITAL]", workingCapital+"");
        int total = land+building+largerVehicel+smallVehicel+machine+tools+it+fe+workingCapital;
        questionText = questionText.replace("[sum of all 9 asset categories]",total+"");
        return questionText;
    }
    public int getAnswerFromQuestionOrder(String order){
        int value = 0;

        String questionNo = db.getQuestionByQuestionOrderandSurveyId(order,getIntent().getStringExtra("SURVEY_ID"));
        if(!questionNo.equals("0")){
            if(!(getAnswervalue(questionNo).equals(""))) {
                try {
                    value = Integer.parseInt(getAnswervalue(questionNo));
                }catch(NumberFormatException e){}
            }
        }
        return value;
    }
    public String getAnswervalue(String questionNo){
        JSONObject obj = new JSONObject();
        obj = getAnswerIfsaved(questionNo);
        String  answer = "";
        try {
            answer = obj.getString("answer");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(answer.equals("__123__")) {
            answer = "";
        }
        return answer;

    }
    public String replaceQuestionTextValue(String questionText){
        if(survey_ID.equals("11")){
            if (questionOrder.trim().equals("10")){
                questionText = setTotalInSummary(questionText);
            }
        }
        if(survey_ID.equals("9")){
            if(!answer2a.equals("")) {
                questionText = questionText.replace("[2A]", answer2a);
                questionText = questionText.replace("[3A]", answer2a);
                questionText = questionText.replace("[4A]", answer2a);
            }
            if(questionOrder.equals("5")){
                int avg =(gbl.value_3d+gbl.value_4d+gbl.value_4d)/3;
                questionText = questionText.replace("[2B]", gbl.value_3d + "");
                questionText = questionText.replace("[3B]", gbl.value_4d + "");
                questionText = questionText.replace("[4B]", gbl.value_5d + "");
                questionText = questionText.replace("[average of (2B, 3B, 4B)]", avg + "");
            }
        }
        if(survey_ID.equals("10")){
            int val = gbl.value_3d+(gbl.value_4d/2)+gbl.value_4d;
            questionText = questionText.replace("[emp_fulltime]", gbl.value_3d + "");
            questionText = questionText.replace("[emp_parttime]", gbl.value_4d + "");
            questionText = questionText.replace("[emp_partners]", gbl.value_5d + "");
            questionText = questionText.replace("[1.0*(emp_fulltime) + 0.5*(emp_parttime) + 1.0*(emp_partners)]", val+ "");
        }else {
            if (!(gbl.popular_product_answer == null)) {
                if (!(gbl.popular_product_answer.isEmpty()) && survey_ID.equals("8")) {
                    questionText = questionText.replace("[2A]", gbl.popular_product_answer.get(0));
                    questionText = questionText.replace("[2B]", gbl.popular_product_answer.get(1));
                    questionText = questionText.replace("[2C]", gbl.popular_product_answer.get(2));
                    questionText = questionText.replace("#1", gbl.popular_product_answer.get(0));
                    questionText = questionText.replace("#2", gbl.popular_product_answer.get(1));
                    questionText = questionText.replace("#3", gbl.popular_product_answer.get(2));
                }
                //if(questionText.)

                int sum = 0;
                if (questionText.toLowerCase().contains("[(3B) - (3C)]".toLowerCase())) {
                    sum = value_b - value_c;
                    //gbl.value_3d = sum;
                    questionText = questionText.replace("[(3B) - (3C)]", sum + "");
                }
                if (questionText.toLowerCase().contains("[(4B) - (4C)]".toLowerCase())) {
                    sum = value_b - value_c;
                    //gbl.value_4d = sum;
                    questionText = questionText.replace("[(4B) - (4C)]", sum + "");
                }
                if (questionText.toLowerCase().contains("[(5B) - (5C)]".toLowerCase())) {
                    sum = value_b - value_c;
                    //gbl.value_5d = sum;
                    questionText = questionText.replace("[(5B) - (5C)]", sum + "");
                }
                if (questionText.contains("[average of (3D, 4D, 5D)]")) {
                    int avg = (gbl.value_3d + gbl.value_4d + gbl.value_5d) / 3;
                    questionText = questionText.replace("[average of (3D, 4D, 5D)]", avg + "");
                    questionText = questionText.replace("[3D]", gbl.value_3d + "");
                    questionText = questionText.replace("[4D]", gbl.value_4d + "");
                    questionText = questionText.replace("[5D]", gbl.value_5d + "");
                }


            }
        }
        return questionText;
    }
    public void createTextQuestionNo(String questionOrder, String questionText){
        TextView tvQuestion = new TextView(this);
        //tvQuestion.setId(@+id/1);
        if(survey_ID.equals("8") || survey_ID.equals("10")|| survey_ID.equals("9")||survey_ID.equals("11")) {
            questionText = replaceQuestionTextValue(questionText);
        }
        String setText = "<b>"+questionOrder+"</b>. "+questionText;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tvQuestion.setText(Html.fromHtml(setText,Html.FROM_HTML_MODE_LEGACY));
        } else {
            tvQuestion.setText(Html.fromHtml(setText));
        }
        //tvQuestion.setText(questionOrder+". ");
        //tvQuestion.append(questionText);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        tvQuestion.setTextColor(getResources().getColor(R.color.darkgrey));
        //tvQuestion.setTypeface(Typeface.DEFAULT_BOLD);
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
            case "gps":
                tvQuestion.setFocusable(false);
                tvQuestion.setHint("Record Location");
                tvQuestion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setGpsLocationValue(v);
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
                tvQuestion.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
                tvQuestion.setHint("Please select");
                int maxLen = 4;
                tvQuestion.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLen)});
//                tvQuestion.setFocusable(false);
//                tvQuestion.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        showYearMonth(v,"year");
//                    }
//                });
                break;
            case "money":
                tvQuestion.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                tvQuestion.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        TextView editText = (TextView)v;
                        if(hasFocus){
                            //editText.setText("Has focus");
                            String text = editText.getText().toString();
                            text = text.replace(",","");
                            editText.setText(text);
                        }else{
                            //editText.setText("Has no focus");
                            String text = editText.getText().toString();
                            NumberFormat formatter = new DecimalFormat("#,###");
                            //double myNumber = 1000000;
                            double myNumber = 0;
                            try{
                                myNumber = Double.parseDouble(text);
                            String formattedNumber = formatter.format(myNumber);
                            editText.setText(formattedNumber);
                            }catch (NumberFormatException e){

                            }
                        }
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
            Cursor csr =db.getQuestionByQuestionOrder(compare_with,section_id);
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
        int id = -1;
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
                //btn1.setChecked(true);
                id = btn1.getId();
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
                    case "money":
                        tvQuestion.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        tvQuestion.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                TextView editText = (TextView)v;
                                if(hasFocus){
                                    //editText.setText("Has focus");
                                    String text = editText.getText().toString();
                                    text = text.replace(",","");
                                    editText.setText(text);
                                }else{
                                    //editText.setText("Has no focus");
                                    String text = editText.getText().toString();
                                    NumberFormat formatter = new DecimalFormat("#,###");
                                    //double myNumber = 1000000;
                                    double myNumber = 0;
                                    try{
                                        myNumber = Double.parseDouble(text);
                                        String formattedNumber = formatter.format(myNumber);
                                        editText.setText(formattedNumber);
                                    }catch (NumberFormatException e){

                                    }
                                }
                            }
                        });
                        break;
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
                    case "gps":
                        tvQuestion.setFocusable(false);
                        tvQuestion.setHint("Record Location");
                        tvQuestion.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setGpsLocationValue(v);
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
                RadioButton rb = (RadioButton) findViewById(checkedId);
                if (rb != null) {
                    radioselect = rb.getText().toString();
                    int idx = group.indexOfChild(rb);
                    if (if_oquestionid_4c) {

                        if (idx == 0) {
                            if_skip_5b = true;
                            if_skip_5c = false;
                        } else if (idx == 1) {
                            if_skip_5b = false;
                            if_skip_5c = true;
                        } else {
                            if_skip_5b = false;
                            if_skip_5c = false;
                        }
                    }
                    if (if_question_4a) {
                        if (idx == 0 || idx == 1) {
                            if_skip_5b = true;
                            if_skip_5c = true;
                            skip_4b_4c = true;
                        } else if (idx == 2) {
                            if_skip_5b = false;
                            if_skip_5c = false;
                            skip_4b_4c = false;
                            skip_4b = true;
                        } else if (idx == 3) {
                            if_skip_5b = false;
                            if_skip_5c = false;
                            skip_4b_4c = false;
                            skip_4b = false;
                        }

                    }
                    if (rb.getTag().equals("radiolist")) {
                        for (int i = 0; i < options.length(); i++) {
                            JSONObject child = new JSONObject();
                            try {
                                child = options.getJSONObject(i);
                                if (child.getString("option_text").equals(radioselect)) {
                                    setNextQuestionOptions(child.getJSONArray("sub_option"), groupId);
                                    //setNextQuestionOptions(options,groupId);
                                    break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    } else if (!rb.getTag().equals("radio")) {
                        int i = (Integer) rb.getTag();
                        View v = (View) findViewById(i);
                        if (v instanceof EditText) {
                            EditText edt = (EditText) v;
                            //gbl.setRadioInputTextCheck(i);
                            edt.setVisibility(View.VISIBLE);
                            edt.setFocusable(true);
                        }
                    } else {
                        int childCount = group.getChildCount();
                        for (int i = 0; i < childCount; i++) {
                            View child = group.getChildAt(i);
                            if (child instanceof EditText) {
                                EditText edt = (EditText) child;
                                edt.setVisibility(View.GONE);
                            }
                        }
                    }

                }
            }
        });

        if(id >=0) {
            //group.check(id);
            RadioButton checkBtn = (RadioButton)findViewById(id);
            if(checkBtn != null){
                checkBtn.setChecked(true );
            }
        }
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
        if(group != null) {
            group.removeAllViews();
            JSONObject obj = new JSONObject();
            obj = getAnswerIfsaved(group.getTag().toString());
            String answer = "";
            String radioValue = "";

            try {
                answer = obj.getString("answer");
                radioValue = obj.getString("radio");
                if (answer.equals("__123__")) {
                    answer = "";
                    radioValue = "";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //String child[] = null;
            for (int i = 0; i < options.length(); i++) {
                if ((options.getString(i).toUpperCase().equals(options.getString(i)))) {

                    TextView tvQuestion = new TextView(this);
                    tvQuestion.setText(options.getString(i).trim());
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                    tvQuestion.setTextColor(getResources().getColor(R.color.darkgrey));
                    tvQuestion.setTypeface(Typeface.DEFAULT_BOLD);
                    tvQuestion.setTextSize(16);
                    layoutParams.setMargins(0, marginBottomPxl, 0, 0);
                    tvQuestion.setLayoutParams(layoutParams);
                    group.addView(tvQuestion);

                } else {
                    RadioButton btn1 = new RadioButton(this);
                    //child[i] = options.getString(i);
                    //btn1.setId(r.nextInt(10000));
                    //Log.e("OPTIONS OBJECT", child.toString());
                    btn1.setTextColor(getResources().getColor(R.color.darkgrey));
                    btn1.setTextSize(16);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                    layoutParams.setMargins(marginBottomPxl, marginBottomPxl, marginBottomPxl, marginBottomPxl);
                    group.setLayoutParams(layoutParams);
                    btn1.setText(options.getString(i));
//            if(radioValue.equals(options.getString(i))) {
//                btn1.setChecked(true);
//            }
                    group.addView(btn1);
                }
            }

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
        int current_year = Calendar.getInstance().get(Calendar.YEAR);
        int current_month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        switch (type){
            case "year":
                numberPicker.setVisibility(View.GONE);
                textView2.setVisibility(View.GONE);
                 numberPicker2.setMaxValue(current_year);
                numberPicker2.setMinValue(1900);
                numberPicker2.setValue(current_year);
                break;
            case "month":
                numberPicker.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.VISIBLE);
                numberPicker.setMaxValue(12);
                numberPicker.setMinValue(1);
                numberPicker.setValue(current_month);
                numberPicker2.setMaxValue(current_year);
                numberPicker2.setMinValue(1900);
                numberPicker2.setValue(current_year);
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

    public void setGpsLocationValue(View v){
        TextView tv = (TextView)v;
        date = tv.getId();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },10
            );
        }else {
            getLocation(date);
            date = 0;

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getLocation(date);
                    date = 0;
                }
                break;
        }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    public void getLocation(int id) {
        final TextView tv = (TextView) findViewById(id);
        if (tv != null) {
            SingleShotLocationProvider.requestSingleUpdate(this,
                    new SingleShotLocationProvider.LocationCallback() {
                        @Override
                        public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                            tv.setText("Long: " + location.longitude + " Lat: " + location.latitude);
                            //Log.d("Location", "my location is " + location.toString());
                        }
                    });
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    public void questionMenu(){
            if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                mDrawerLayout.closeDrawer(GravityCompat.END);
            } else {
                mDrawerLayout.openDrawer(GravityCompat.END);
            }
        //addMenuItem();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        expandableList = (ExpandableListView) findViewById(R.id.navigationmenu);
        prepareListData();
        mMenuAdapter = new DynamicAdapter(this, listDataHeader, listDataChild, expandableList);

        // setting list adapter
        expandableList.setAdapter(mMenuAdapter);

        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                Log.d("DEBUG", "submenu item clicked");
                gbl.setSectionCount(i);
                gbl.setQuestionSavedCounter(i1);
                setSelectedQuestionData();

                return false;
            }
        });
        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                Log.d("DEBUG", "heading clicked");
                return false;
            }
        });
    }

     public void setSelectedQuestionData(){
        Cursor sectionname = db.getSectionList(survey_ID);
        if(sectionname.getCount()>0){
            sectionname.moveToPosition(gbl.getSectionCount());
            section_id = sectionname.getString(0);
            question = db.getQuestionList(section_id);
            if(question.getCount()>0) {
                deleteTextView();
                setQuestionData(gbl.getCounter(),"notGroup");
            }
            if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                mDrawerLayout.closeDrawer(GravityCompat.END);
            } else {
                mDrawerLayout.openDrawer(GravityCompat.END);
            }

        }
         sectionname.close();
     }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }
    private void prepareListData() {
        listDataHeader = new ArrayList<ExpandedMenuModel>();
        listDataChild = new HashMap<ExpandedMenuModel, List<String>>();
        Cursor csr = db.getSectionList(survey_ID);
        csr.moveToFirst();
        do{
            ExpandedMenuModel item1 = new ExpandedMenuModel();
            item1.setIconName(csr.getString(2));
            listDataHeader.add(item1);
            List<String> heading1 = new ArrayList<String>();
            Cursor que = db.getQuestionList(csr.getString(0));
            que.moveToFirst();
            do{
                heading1.add(que.getString(13));
            }while(que.moveToNext());
            que.close();
            listDataChild.put(item1, heading1);
        }while(csr.moveToNext());
        csr.close();
    }
    private void setupDrawerContent(NavigationView navigationView) {
        //revision: this don't works, use setOnChildClickListener() and setOnGroupClickListener() above instead
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

//    public void addMenuItem(){
//
//        Menu menu = navigationView.getMenu();
//        Cursor sectionMenu = db.getSectionList(survey_ID);
//        sectionMenu.moveToFirst();
//        int count = 0;
//        do{
//
//        }while (sectionMenu.moveToNext());
//        SubMenu sm = menu.addSubMenu("Options...");
//        sm.add("Theme");
//        sm.add("Settings");
//    }

}
