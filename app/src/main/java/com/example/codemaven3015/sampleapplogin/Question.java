package com.example.codemaven3015.sampleapplogin;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class Question extends AppCompatActivity {
    String questiontype = "name";
    DataBaseHealper db;
    TextView textViewDetails ,textViewQuestion1,textViewQuestion2,textViewQuestionNumber1,textViewQuestionInfo,textViewQuestionNumber2;
    RadioButton  radio1,radio2;
    EditText editTextName1,editTextName2,editTextPhone,editTextEmail;

    public void OnBackClick(View v) {
        Intent i = new Intent(Question.this, welcome.class);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
//        db = new DataBaseHealper(this);
//        Cursor res = db.getQuestionList("1");
//        showMessage("QuestionData", res.getCount());
////        if(res.getCount()>0) {
////            showMessage("QuestionData", "data inserted");
////        }else{
////            showMessage("QuestionData", "data not inserted");
////        }

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        //Toolbar parent= (Toolbar)(R.layout.action_bar).getParent();
        final TextView helloTextView = (TextView) findViewById(R.id.action_text);
        helloTextView.setText(getIntent().getStringExtra("SURVEY_NAME"));
        textViewDetails = (TextView)findViewById(R.id.textViewDetails);
        textViewQuestionNumber1 = (TextView)findViewById(R.id.textViewQuestionNumber1);
        textViewQuestionNumber2 = (TextView)findViewById(R.id.textViewQuestionNumber2);
        textViewQuestionInfo = (TextView)findViewById(R.id.textViewQuestionInfo);
        textViewQuestion1 = (TextView)findViewById(R.id.textViewQuestion1);
        textViewQuestion2 = (TextView)findViewById(R.id.textViewQuestion2);
        radio1 = (RadioButton)findViewById(R.id.radio1);
        radio2 = (RadioButton)findViewById(R.id.radio2);
        editTextName1=(EditText)findViewById(R.id.editTextName1);
        editTextName2=(EditText)findViewById(R.id.editTextName2);
        editTextPhone=(EditText)findViewById(R.id.editTextPhone);
        editTextEmail=(EditText)findViewById(R.id.editTextemail);
        setQuestionWigets(questiontype);
        editTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    validateEmail(editTextEmail.getText().toString());
                }
            }
        });

    }
    public void validateEmail(String email){
        if(!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()){

        }else{
            showMessage("Info","Enter Valid email");
        }

    }
    public void nextQuestion(View view){
        setQuestionWigets(questiontype);

    }
    public void setQuestionWigets(String  type){
        textViewDetails.setVisibility(textViewDetails.GONE);
        textViewQuestionInfo.setVisibility(textViewQuestionInfo.GONE);
        textViewQuestionNumber1.setVisibility(textViewQuestionNumber1.GONE);
        textViewQuestionNumber2.setVisibility(textViewQuestionNumber2.GONE);
        textViewQuestion1.setVisibility(textViewQuestion1.GONE);
        textViewQuestion2.setVisibility(textViewQuestion2.GONE);
        radio1.setVisibility(radio1.GONE);
        radio2.setVisibility(radio2.GONE);
        editTextName1.setVisibility(editTextName1.GONE);
        editTextName2.setVisibility(editTextName2.GONE);
        editTextPhone.setVisibility(editTextPhone.GONE);
        editTextEmail.setVisibility(editTextEmail.GONE);
        if(type.equals("name")){
            textViewDetails.setVisibility(textViewDetails.GONE);
            textViewQuestionInfo.setVisibility(textViewQuestionInfo.GONE);
            textViewQuestionNumber1.setVisibility(textViewQuestionNumber1.VISIBLE);
            textViewQuestionNumber2.setVisibility(textViewQuestionNumber2.VISIBLE);
            textViewQuestion1.setVisibility(textViewQuestion1.VISIBLE);
            textViewQuestion2.setVisibility(textViewQuestion2.VISIBLE);
            radio1.setVisibility(radio1.GONE);
            radio2.setVisibility(radio2.GONE);
            editTextName1.setVisibility(editTextName1.VISIBLE);
            editTextName1.setText("");
            editTextName2.setVisibility(editTextName2.VISIBLE);
            editTextName2.setText("");
            editTextPhone.setVisibility(editTextPhone.GONE);
            editTextEmail.setVisibility(editTextEmail.GONE);
            questiontype = "text";

        }else if(type.equals("phone")){
            textViewDetails.setVisibility(textViewDetails.GONE);
            textViewQuestionInfo.setVisibility(textViewQuestionInfo.VISIBLE);
            textViewQuestionNumber1.setVisibility(textViewQuestionNumber1.VISIBLE);
            textViewQuestionNumber1.setText("Question 3A.");
            textViewQuestionNumber2.setVisibility(textViewQuestionNumber2.GONE);
            textViewQuestion1.setVisibility(textViewQuestion1.VISIBLE);
            textViewQuestion1.setText("What is your mobile phone number?");
            textViewQuestionInfo.setText("Please make sure the client provides a ten-digit phone number.");
            textViewQuestion2.setVisibility(textViewQuestion2.GONE);
            radio1.setVisibility(radio1.VISIBLE);
            radio2.setVisibility(radio2.VISIBLE);
            radio2.setText("Do not have");
            radio1.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editTextPhone.setVisibility(editTextPhone.VISIBLE);
                            editTextPhone.setText("");
                            radio1.setChecked(true);
                            radio2.setChecked(false);
                        }
                    }
            );
            radio2.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editTextPhone.setVisibility(editTextPhone.GONE);
                            radio1.setChecked(false);
                        }
                    }
            );
            editTextName1.setVisibility(editTextName1.GONE);
            editTextName2.setVisibility(editTextName2.GONE);
            editTextPhone.setVisibility(editTextPhone.GONE);
            editTextEmail.setVisibility(editTextEmail.GONE);
            questiontype = "email";

        }else if(type.equals("email")){
            textViewDetails.setVisibility(textViewDetails.GONE);
            textViewQuestionInfo.setVisibility(textViewQuestionInfo.VISIBLE);
            textViewQuestionNumber1.setVisibility(textViewQuestionNumber1.VISIBLE);
            textViewQuestionNumber2.setVisibility(textViewQuestionNumber2.GONE);
            textViewQuestion1.setVisibility(textViewQuestion1.VISIBLE);
            textViewQuestion2.setVisibility(textViewQuestion2.GONE);
            textViewQuestion1.setText("What is your email adress?");
            textViewQuestionInfo.setText("Please make sure the client provides a valid email address.");
            radio1.setVisibility(radio1.VISIBLE);
            radio2.setVisibility(radio2.VISIBLE);
            radio1.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editTextEmail.setVisibility(editTextEmail.VISIBLE);
                            editTextEmail.setText("");
                            radio1.setChecked(true);
                            radio2.setChecked(false);
                        }
                    }
            );
            radio2.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editTextEmail.setVisibility(editTextEmail.GONE);
                            radio1.setChecked(false);
                        }
                    }
            );
            editTextName1.setVisibility(editTextName1.GONE);
            editTextName2.setVisibility(editTextName2.GONE);
            editTextPhone.setVisibility(editTextPhone.GONE);
            editTextEmail.setVisibility(editTextEmail.GONE);


        }else if(type.equals("text")){
            textViewDetails.setVisibility(textViewDetails.GONE);
            textViewQuestionInfo.setVisibility(textViewQuestionInfo.GONE);
            textViewQuestionNumber1.setVisibility(textViewQuestionNumber1.VISIBLE);
            textViewQuestionNumber1.setText("Question 2.");
            textViewQuestionNumber2.setVisibility(textViewQuestionNumber2.GONE);
            textViewQuestion1.setVisibility(textViewQuestion1.VISIBLE);
            textViewQuestion1.setText("What is the name of your business?");
            textViewQuestion2.setVisibility(textViewQuestion2.GONE);
            radio1.setVisibility(radio1.GONE);
            radio2.setVisibility(radio2.GONE);
            editTextName1.setVisibility(editTextName1.VISIBLE);
            editTextName1.setText("");
            editTextName2.setVisibility(editTextName2.GONE);
            editTextPhone.setVisibility(editTextPhone.GONE);
            editTextEmail.setVisibility(editTextEmail.GONE);
            questiontype = "phone";

        }
    }
    public void showMessage(String title, String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();

    }
    public void showMessage(String title, Integer Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();

    }
}
