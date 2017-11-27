package com.example.codemaven3015.sampleapplogin;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class SelectLanguage extends AppCompatActivity {
    String SurveyId;
    public void openIAgree(View v){
        Intent intent = new Intent(SelectLanguage.this , IAgree.class);
        intent.putExtra("SURVEY_ID", SurveyId);
        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        final TextView helloTextView2 = (TextView) findViewById(R.id.action_text);
        helloTextView2.setText("Language");
        SurveyId = getIntent().getStringExtra("SURVEY_ID");
        //Toast.makeText(SelectLanguage.this, s, Toast.LENGTH_LONG).show();
    }
    public void onClickLogout(View view){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setCancelable(true);
        builder.setTitle(R.string.logout_title);
        builder.setMessage(R.string.logout_message);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(SelectLanguage.this , MainActivity.class);
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
}
