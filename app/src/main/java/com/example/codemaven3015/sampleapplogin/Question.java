package com.example.codemaven3015.sampleapplogin;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class Question extends AppCompatActivity {
    DataBaseHealper db;
    TextView textViewDetails ,textViewQuestion1,textViewQuestionNumber1,textViewQuestionInfo,sectionName,clientId;
    RadioButton  radio1,radio2;
    EditText editTextName1;
    GlobalVariables gbl;
    String sectionNameId,survey_ID;
    String questionOrder;
    RadioGroup radioGroup;
    RelativeLayout footerButtons;
    EditText firstname, lastname,phone;
    CountryCodePicker cpp;
    Button register;

    public void OnBackClick(View v) {
        Intent i = new Intent(Question.this, welcome.class);
        i.putExtra("from","not_main");
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        //Toolbar parent= (Toolbar)(R.layout.action_bar).getParent();
        final TextView helloTextView = (TextView) findViewById(R.id.action_text);
        gbl = (GlobalVariables)getApplicationContext();
        helloTextView.setText(getIntent().getStringExtra("SURVEY_NAME"));
        final ImageView imageView = (ImageView)findViewById(R.id.imageButton);
        imageView.setVisibility(View.GONE);
        sectionNameId = ("SECTION "+getIntent().getStringExtra("SECTION_NO")+": "+getIntent().getStringExtra("SECTION_NAME"));
        sectionName = (TextView)findViewById(R.id.textView3);
        gbl.setClientId(getIntent().getStringExtra("CLIENT"));
        clientId = (TextView)findViewById(R.id.textView2);
        survey_ID = getIntent().getStringExtra("SURVEY_ID");
        db = new DataBaseHealper(this);
        textViewDetails = (TextView)findViewById(R.id.textViewDetails);
        textViewQuestionNumber1 = (TextView)findViewById(R.id.textViewQuestionNumber1);
        textViewQuestionInfo = (TextView)findViewById(R.id.textViewQuestionInfo);
        textViewQuestion1 = (TextView)findViewById(R.id.textViewQuestion1);
        editTextName1=(EditText)findViewById(R.id.editTextName1);
        textViewDetails.setVisibility(textViewDetails.GONE);
        textViewQuestionInfo.setVisibility(textViewQuestionInfo.GONE);
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        radioGroup.setVisibility(View.GONE);
        footerButtons = (RelativeLayout)findViewById(R.id.footerButtons);
        footerButtons.setVisibility(View.VISIBLE);
        sectionName.setVisibility(View.VISIBLE);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton btn = (RadioButton)findViewById(checkedId);
                if(btn.getText().equals("yes")){

                }else {

                }
            }
        });

        if(getIntent().getStringExtra("from").equals("iagree")){
            textViewDetails.setVisibility(View.GONE);
            textViewQuestion1.setVisibility(View.GONE);
            textViewQuestionNumber1.setVisibility(View.GONE);
            editTextName1.setVisibility(View.GONE);
            footerButtons.setVisibility(View.GONE);
            sectionName.setVisibility(View.GONE);
            clientId.setText("REGISTRATION");
            setRegistrationView();
        }else{
            sectionName.setText(sectionNameId);
            clientId.setText(gbl.getClientId());
            setSurveyQuestion();
        }

    }
    public void setRegistrationView(){

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.registration,null);
        LinearLayout LL = (LinearLayout) findViewById(R.id.Questions);
        LL.addView(view);
        cpp = (CountryCodePicker)view.findViewById(R.id.ccp);
        //cpp.setDefaultCountryUsingNameCode("AF");
        register = (Button)view.findViewById(R.id.register);
        firstname = (EditText)view.findViewById(R.id.editTextName);
        lastname = (EditText)view.findViewById(R.id.editTextLastName);
        phone = (EditText)view.findViewById(R.id.editTextPhone);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String first,last,phoneNo;
                first = firstname.getText().toString();
                last = lastname.getText().toString();
                phoneNo = phone.getText().toString();
                if(!specialCharValidation(first) || !emptyFieldValidation(first)){
                    firstname.setError("Please enter valid name");
                    return;
                }
                if(!specialCharValidation(last) || !emptyFieldValidation(last)){
                    lastname.setError("Please enter valid name");
                    return;
                }
                if( !emptyFieldValidation(phoneNo)){
                    phone.setError("Please enter valid phone number!!");
                    return;
                }
                if(!(phoneNo.length()==10)) {
                    phone.setError("Phone number must be 10 digits long!!");
                    return;
                }
                phoneNo = cpp.getSelectedCountryCodeWithPlus()+phoneNo;
                //showMessage("",phoneNo);
                db.addRegistrationDetails(first,last,phoneNo);
                gbl.setName(first+" "+last);
                Intent i = new Intent(Question.this, SurveySection.class);
                i.putExtra("SURVEY_NAME", getIntent().getStringExtra("SURVEY_NAME"));
                i.putExtra("SURVEY_ID", getIntent().getStringExtra("SURVEY_ID"));
                i.putExtra("SECTION_NAME", getIntent().getStringExtra("SECTION_NAME"));
                i.putExtra("SECTION_ID", getIntent().getStringExtra("SECTION_ID"));
                i.putExtra("SECTION_NO", "1");
                i.putExtra("SECTION_DESC", getIntent().getStringExtra("SECTION_DESC"));
                i.putExtra("isDONE",false);
                i.putExtra("CLIENT",getIntent().getStringExtra("CLIENT"));
                startActivity(i);
            }
        });
    }
    public boolean specialCharValidation(String s){
        Pattern regex = Pattern.compile("[$&+:;=\\\\?@#|/<>^*()%!-1234567890,'{}]");

        if (regex.matcher(s).find()) {
            return false;
        }else {
            return true;
        }
    }
    public boolean emptyFieldValidation(String s){
        if(s.isEmpty()){
            return false;
        }else{
            return true;
        }
    }
    public void setSurveyQuestion(){
        //Cursor csr = db.getQuestionByQuestionOrder("1a",getIntent().getStringExtra("SECTION_NO").toString());
        Cursor question = gbl.getQuestionCursor();
        questionOrder = "1a";
        if(question.getCount()>0){
            question.moveToFirst();
            textViewQuestionNumber1.setText(question.getString(9));
            textViewQuestion1.setText(question.getString(5));
            editTextName1.setTag(question.getString(0));
            editTextName1.setVisibility(View.VISIBLE);

        }
    }
    public void nextQuestion(View v) throws JSONException {
        Integer answer = Integer.parseInt(editTextName1.getText().toString());
        String order =questionOrder;
        if(order.equals("2b")){
            order = "2a";
        }
        switch (order){
            case "1a":
                if(answer == 0) {
                    showMessageWithNoAndYes("Info", "Are you sure you had 0 sales in the past seven days? This means you did not sell a single product/service in the last week.", "1a");
                }else if (answer >= 0){
                    showMessageWithNoAndYes("Info","Thanks! If we take your FIRST GUESS (of sales in the past seven days), the estimate for your total sales last week =" + answer +" . Reply 'yes' if correct or 'no' to re-answer.","1a");
                    //showNextQuestion();
                }
                break;
            case "2a":
                if(answer == 0) {
                    showMessageWithNoAndYes("Info", "Are you sure you had 0 sales in the past FOUR WEEKS? This means you did not sell a single product/service in the last month.", "1a");
                }else if (answer > 0){
                    if(questionOrder.equals("2b")){
                        int val = (Integer.parseInt(gbl.getAnswerByQuestionOrder("2a"))+answer)/2;
                        showMessageWithNoAndYes("info","Thanks! If we calculate the AVERAGE OF BUSY AND SLOW WEEKS, the estimate for your total sales last week = "+ val+". Reply 'yes' if correct or 'no' to re-answer.","2b");
                    }else {
                        showNextQuestion();
                    }
                }
                break;
            case "3a":
                if(answer == 0) {
                    showMessageWithNoAndYes("Info", "Are you sure your business was open for 0 days last week? This means your business was not open to customers at all in the past seven days.", "3a");
                }else if (answer > 0 && answer < 8){
                    showNextQuestion();
                }else{
                    editTextName1.setError("Please enter valid number  of days");
                }
                break;
            case "3b":
                if(answer >= 0){
                    showNextQuestion();
                }
                break;
            case "3c":
                if(answer < Integer.parseInt(gbl.getAnswerByQuestionOrder("3b"))){
                    showMessage("Info","Your Highest sales day should NOT BE LESS than what you collected Yesterday. Please reenter a number");
                    editTextName1.setError("Reenter the number");
                }else {
                  showNextQuestion();
                }
                break;
            case "3d":
                if(answer > Integer.parseInt(gbl.getAnswerByQuestionOrder("3b"))){
                    showMessage("Info","Your Lowest sales day should NOT BE MORE than what you collected Yesterday. Please reenter a number");
                    editTextName1.setError("Reenter the number");
                }else {
                    //showNextQuestion();
                    showMessageWithNoAndYes("Info","Thank you. Let’s review the information you provided about daily sales:\n" +
                            "Highest (busiest) day = "+Integer.parseInt(gbl.getAnswerByQuestionOrder("3c"))+"\n" +
                            "Yesterday =  "+Integer.parseInt(gbl.getAnswerByQuestionOrder("3b"))+"\n" +
                            "Lowest (slowest) day = "+ answer+"\n" +
                            "Reply 'yes' if correct or 'no' to re-answer.","3d");
                }
                break;
            case "3e":
                if(answer <= Integer.parseInt(gbl.getAnswerByQuestionOrder("3c")) && answer >= Integer.parseInt(gbl.getAnswerByQuestionOrder("3d")) ){
                    //showNextQuestion();
                    int val = Integer.parseInt(gbl.getAnswerByQuestionOrder("3c"))* answer;
                    showMessageWithNoAndYes("Info","Thanks! If we calculate the SUM OF TYPICAL DAILY SALES, the estimate for your total sales last week = "+ val+". Reply 'yes' if correct or 'no' to re-answer.","2b");
                }else{
                    showMessage("Info","Your TYPICAL sales day should be less than your Highest sales day and also more than your Lowest sales day. Please reenter a number.");
                    editTextName1.setError("Reenter the number");
                }
                break;
            case "4a":
                if(answer >=0){
                    showMessageWithNoAndYes("Info","Thank you. We have recorded "+answer+" as your Best Guess for total SALES in the PAST SEVEN DAYS. Reply 'yes' if correct or 'no' to re-answer","4a");
                }else{
                    editTextName1.setError("Reenter the correct number");
                }
                break;
            case "5a":
                if(answer >= 0 && answer< 8){
                    showNextQuestion();
                }else{
                    editTextName1.setError("Reenter the correct number");
                }
                break;
            case "5b":
                if(answer >= 0 && answer< 8){
                    showNextQuestion();
                }else{
                    editTextName1.setError("Reenter the correct number");
                }
                break;
            case "5c":
                if(answer >= 0 && answer< 8){
                    String message = "Thanks for completing Impresa Insights this week! You are 25%  of the way to receiving this month's full report for your business! BASED ON TODAY'S RESPONSES, here is your weekly SALES estimate:Weekly Sales = "+gbl.getAnswerByQuestionOrder("3c");
                    //showMessage("Info",message);
                    textViewQuestion1.setText(message);
                    textViewQuestionNumber1.setVisibility(View.GONE);
                    editTextName1.setVisibility(View.GONE);
                    JSONObject obj = new JSONObject();
                    obj.put("answer",editTextName1.getText().toString());
                    obj.put("question_no",editTextName1.getTag().toString());
                    obj.put("order",questionOrder);
                    gbl.addAnswerInJsonArray(obj);
                    //db.updateAnswerInTable(gbl.getAnswer(),true,survey_ID,gbl.getClientId());
                    db.updateWeeklyInfo(gbl.getClientId(),survey_ID,"1");
                    questionOrder = "last";

                    //showNextQuestion();
                }else{
                    editTextName1.setError("Reenter the correct number");
                }
                break;
            case "last":
                Intent i = new Intent(Question.this, welcome.class);
                i.putExtra("from","not_main");
                startActivity(i);
                break;

        }
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
    public void showNextQuestion() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("answer",editTextName1.getText().toString());
        obj.put("question_no",editTextName1.getTag().toString());
        obj.put("order",questionOrder);
        int position = ifAnswerExist(editTextName1.getTag().toString());
        if(position >0){
            gbl.updateAtAnswer(position,editTextName1.getText().toString(),editTextName1.getTag().toString(),questionOrder,false,false);
        }else {
            gbl.addAnswerInJsonArray(obj);
        }
        gbl.countIncrement();
        editTextName1.setText("");
        String type;
        Cursor question = gbl.getQuestionCursor();
        if(gbl.getCounter() < question.getCount()) {
            question.moveToPosition(gbl.getCounter());
            questionOrder = question.getString(9);

            textViewQuestionNumber1.setText(question.getString(9));
            type = question.getString(4);
            textViewQuestion1.setText((question.getString(5)));
            if(questionOrder.equals("4a")){
                textViewQuestion1.setText(checkQuestionText(question.getString(5)));
            }
            if(type.equals("radio")){
                editTextName1.setVisibility(View.GONE);
                radioGroup.setVisibility(View.VISIBLE);
                radioGroup.setTag(question.getString(0));

            }else {
                editTextName1.setTag(question.getString(0));
                editTextName1.setVisibility(View.VISIBLE);
                radioGroup.setVisibility(View.GONE);
            }


        }

    }
    public String checkQuestionText(String text) throws JSONException {
        //String substr=text.substring(text.indexOf("["),text.indexOf("["));
        //String subString = substr.substring(text.indexOf("[")+1,text.indexOf("[")-1);
        switch (questionOrder){
            case "4a":
                Integer val1 = Integer.parseInt(gbl.getAnswerByQuestionOrder("2a"));
                Integer val2 =(Integer.parseInt(gbl.getAnswerByQuestionOrder("2a"))+Integer.parseInt(gbl.getAnswerByQuestionOrder("2b")))/2;
                Integer val3 =Integer.parseInt(gbl.getAnswerByQuestionOrder("3c"))* Integer.parseInt(gbl.getAnswerByQuestionOrder("3e"));
                text = text.replace("[1a]",val1.toString());
                text = text.replace("[2c]",val2.toString());
                text = text.replace("[3g]",val3.toString());
                break;

        }
        return text;
    }
    public void showMessageWithNoAndYes(String title, String message, final String whereTo){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (whereTo){
                    case "1a":
                        try {
                            showNextQuestion();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "3a":
                        gbl.countIncrement();
                        gbl.countIncrement();
                        gbl.countIncrement();
                        try {
                            showNextQuestion();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;

                    default:
                        try {
                            showNextQuestion();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (whereTo){
                    case "1a":
                        //showNextQuestion();
                        dialog.dismiss();
                        editTextName1.setError("Please enter correct  value");
                        break;
                    case "2b":
                        dialog.dismiss();
                        gbl.countDecrement();
                        try {
                            showNextQuestion();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "3d":
                        dialog.dismiss();
                        gbl.countDecrement();
                        gbl.countDecrement();
                        gbl.countDecrement();
                        try {
                            showNextQuestion();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        dialog.dismiss();
                        editTextName1.setError("Please reenter correct  value");
                        break;

                }
            }
        });
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
    public void validateEmail(String email){
        if(!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()){

        }else{
            showMessage("Info","Enter Valid email");
        }

    }

    public void showMessage(String title, String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();

    }
}
