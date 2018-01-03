package com.example.codemaven3015.sampleapplogin;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

public class EnterPID extends AppCompatActivity {
    DataBaseHealper db;
    EditText editText ;
    GlobalVariables gbl;
    public void openSurveyList(View v) {
        String client_id = editText.getText().toString().trim().toUpperCase();
        if(db.ifClientExist(client_id)){
            gbl.setClientId(client_id);
            Intent i = new Intent(EnterPID.this, SurveyList.class);
            i.putExtra("CLIENT",client_id);
            startActivity(i);
        }else{
            showMessage("INFO","Wrong Client Id");
        }
//        Intent i = new Intent(EnterPID.this, SurveyList.class);
//        startActivity(i);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pid);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        //Toolbar parent= (Toolbar)(R.layout.action_bar).getParent();
        final TextView helloTextView = (TextView) findViewById(R.id.action_text);
        helloTextView.setText("Enter PID");
        db = new DataBaseHealper(this);
        gbl = (GlobalVariables)getApplicationContext();
        editText = (EditText)findViewById(R.id.editTextPID);
    }
    public void onClickLogout(View view){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setCancelable(true);
        builder.setTitle(R.string.logout_title);
        builder.setMessage(R.string.logout_message);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(EnterPID.this , MainActivity.class);
                startActivity(i);
            }
        });
        builder.setNegativeButton(R.string.no, null);
        builder.show();
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
