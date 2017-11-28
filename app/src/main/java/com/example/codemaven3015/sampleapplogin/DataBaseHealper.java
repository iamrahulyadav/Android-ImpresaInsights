package com.example.codemaven3015.sampleapplogin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import static com.example.codemaven3015.sampleapplogin.R.id.clientId;

public class DataBaseHealper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SURVEY.db";
    public static final String TABLE_NAME = "Login_table";
    public static final String TABLE_LAUNGUAGE = "Language_Table";
    public static final String TABLE_SURVEY = "Survey_table";
    public static final String TABLE_SURVEY_SECTION = "Survey_Section_Table";
    public static final String TABLE_SECTION_GROUP = "Section_Group_Table";
    public static final String TABLE_SURVEY_QUESTION = "Survey_Question_Table";
    public static final String TABLE_SURVEY_ANSWER = "Survey_Answer_Table";
    public static final String TABLE_CLIENT_INFO = "Client_Info_Table";
    //public static final String TABLE_GROWTH_ORITENTAION_SURVEY_ANSWER = "Survey_Answer_Table";

    public static final String SurveySectionCOL1 = "SECTION_ID";
    public static final String SurveySectionCOL2 = "SURVEY_ID";
    public static final String SurveySectionCOL3 = "SECTION_TITLE";
    public static final String SurveySectionCOL4 = "SECTION_DESC";
    public static final String SurveySectionCOL5 = "SECTION_SUGGESTION";

    public static final String SectionGroupCOL1 = "GROUP_ID";
    public static final String SectionGroupCOL2 = "SECTION_ID";
    public static final String SectionGroupCOL3 = "GROUP_TITLE";
    public static final String SectionGroupCOL4 = "GROUP_DESC";
    public static final String SectionGroupCOL5 = "GROUP_SUGGESTION";

    public static final String QuestionCOL1 = "QUESTION_ID";
    public static final String QuestionCOL2 = "SECTION_ID";
    public static final String QuestionCOL3 = "GROUP_ID";
    public static final String QuestionCOL4 = "QUESTION_TYPE";
    public static final String QuestionCOL5 = "QUESTION_TEXT";
    public static final String QuestionCOL6 = "QUESTION_SECTION_SUGGESTION";
    public static final String QuestionCOL7 = "IS_OPTIONAL";

    public static final String AnswerCOL1 = "ANSWER_ID";
    public static final String AnswerCOL2 = "QUESTION_ID";
    public static final String AnswerCOL3 = "CLIENT_ID";
    public static final String AnswerCOL4 = "CLIENT_ID_TEMP";
    public static final String AnswerCOL5 = "ANSWER_TEXT";
    public static final String AnswerCOL6 = "FLAG";

    public static final String LanguageCOL_1 = "ID";
    public static final String LanguageCOL_2 = "LAUNGUAGE_ID";
    public static final String LanguageCOL_3 = "LAUNGUAGE_NAME";

    public static final String SurveyCOL_1 = "SURVEY_ID";
    public static final String SurveyCOL_2 = "SURVEY_NAME";
    public static final String SurveyCOL_3 = "SURVEY_DETAILS";


    public static final String COL_1 = "ID";
    public static final String COL_2 = "USERNAME";
    public static final String COL_3 = "PASSWORD";

    public DataBaseHealper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON");
        db.execSQL("create table "+ TABLE_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT, USERNAME TEXT, PASSWORD TEXT)");
        db.execSQL("create table "+ TABLE_SURVEY+" (SURVEY_ID INTEGER, SURVEY_NAME TEXT, SURVEY_DETAILS TEXT )");
        db.execSQL("create table "+ TABLE_LAUNGUAGE+" (ID INTEGER PRIMARY KEY AUTOINCREMENT, LAUNGUAGE_ID TEXT, LAUNGUAGE_NAME TEXT)");
        db.execSQL("create table "+ TABLE_SURVEY_SECTION+" (SECTION_ID INTEGER, SURVEY_ID INTEGER, SECTION_TITLE TEXT, SECTION_DESC TEXT, SECTION_SUGGESTION TEXT)");
        db.execSQL("create table "+ TABLE_SECTION_GROUP+" (GROUP_ID INTEGER, SECTION_ID INTEGER, SURVEY_ID INTEGER, GROUP_TITLE TEXT, GROUP_DESC TEXT, GROUP_SUGGESTION)");
        db.execSQL("create table "+ TABLE_SURVEY_QUESTION+" (QUESTION_ID INTEGER, SECTION_ID INTEGER, GROUP_ID INTEGER, SURVEY_ID INTEGER, QUESTION_TYPE TEXT, QUESTION_TEXT TEXT, QUESTION_SECTION_SUGGESTION TEXT,IS_OPTIONAL INTEGER, QUESTION_OPTION TEXT, QUESTION_ORDER TEXT, QUESTION_NO INTEGER)");
        //db.execSQL("create table "+ TABLE_SURVEY_QUESTION_OPTION+" (OPTION_ID INTEGER, QUESTION_ID INTEGER, OPTION_DESC TEXT)");
        db.execSQL("create table "+ TABLE_SURVEY_ANSWER+" (ANSWER_ID INTEGER PRIMARY KEY AUTOINCREMENT, QUESTION_ID INTEGER, CLIENT_ID INTEGER, CLIENT_ID_TEMP INTEGER,ANSWER_TEXT TEXT, FLAG INTEGER,SURVEY_ID INTEGER,TYPE TEXT)");
        db.execSQL("create table "+TABLE_CLIENT_INFO+" (CLIENT_ID INTEGER, SURVEY_ID INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_SURVEY);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_LAUNGUAGE);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_SURVEY_SECTION);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_SECTION_GROUP);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_SURVEY_QUESTION);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_SURVEY_ANSWER);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_CLIENT_INFO);
        onCreate(db);
    }
    public void updateClientIdTable(String clientId,String surveyId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValue = new ContentValues() ;
        contentValue.put("CLIENT_ID",clientId);
        contentValue.put("SURVEY_ID",surveyId);
        db.insert(TABLE_CLIENT_INFO,null,contentValue);
    }
    public boolean ifClientExist(String clientId){
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_CLIENT_INFO + " WHERE CLIENT_ID =?";
        Cursor res = db.rawQuery(Query,new String[] {clientId}, null);
        if(res.getCount()>0){
            return true;
        }else{
            return false;
        }
    }
    public Cursor returnSurveyDone(String clientId){
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_CLIENT_INFO + " WHERE CLIENT_ID =?";
        Cursor res = db.rawQuery(Query,new String[] {clientId}, null);
        return res;
    }
    public void updateAnswerInTable( JSONArray answer,boolean isOldClient,String survey_id,String client_id){
        SQLiteDatabase db = this.getWritableDatabase();
        int clientId = 0;
        if(!isOldClient){
            Random r = new Random();
            clientId = (r.nextInt(9999999));
        }else{
            clientId = 0;
        }
        for(int i =0;i<answer.length();i++) {
            JSONObject obj = new JSONObject();
            try {
                obj = answer.getJSONObject(i);
                ContentValues contentValue = new ContentValues();
                contentValue.put("QUESTION_ID", obj.getString("question_no"));
                contentValue.put("SURVEY_ID",survey_id);
                if (isOldClient) {
                    contentValue.put("CLIENT_ID", client_id);
                } else {
                    contentValue.put("CLIENT_ID_TEMP", clientId);
                }
                String ans = obj.getString("answer");
                String radio = obj.getString("radio");
                if(ans.equals("")){
                    if(radio.equals("")){
                        contentValue.put("ANSWER_TEXT", "");
                    }else{
                        contentValue.put("ANSWER_TEXT", radio);
                    }
                }else{
                    contentValue.put("ANSWER_TEXT", ans);
                }
                if(radio.equals("image")){
                    contentValue.put("TYPE","image");
                }else{
                    contentValue.put("TYPE",radio);
                }
                contentValue.put("FLAG", "0");
                db.insert(TABLE_SURVEY_ANSWER,null,contentValue);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        //getAnswerFromDB();

    }
    public void deleteAnswerIfUpdated(String clientId){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SURVEY_ANSWER, "CLIENT_ID_TEMP =?", new String[]{clientId});
        db.delete(TABLE_SURVEY_ANSWER, "CLIENT_ID =?", new String[]{clientId});
    }
    public Boolean checkAnswerToupdate(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_SURVEY_ANSWER+" WHERE FLAG = 0", null);
        if(res.getCount()>0){
            //res.close();
            return true;
        }else{
            //res.close();
            return false;
        }
    }
    public Cursor getAnswerFromDB(String clientId,boolean flag){
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "";
        if(flag){
            Query = "SELECT * FROM " + TABLE_SURVEY_ANSWER + " WHERE CLIENT_ID_TEMP =?";
        }else{
            Query = "SELECT * FROM " + TABLE_SURVEY_ANSWER + " WHERE CLIENT_ID =?";
        }
        Cursor res = db.rawQuery(Query,new String[] {clientId}, null);
        //Cursor res = db.rawQuery("select * from "+TABLE_SURVEY_ANSWER+" WHERE FLAG = 0", null,);
        return res;
    }
    public Cursor ifSurveyDone(String clientId,String surveyId){
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_SURVEY_ANSWER + " WHERE CLIENT_ID =? AND SURVEY_ID =?";
        Cursor res = db.rawQuery(Query,new String[] {clientId,surveyId}, null);
        return res;
    }
    public Cursor selectNewDistictClient(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select DISTINCT CLIENT_ID_TEMP from "+TABLE_SURVEY_ANSWER+" WHERE FLAG = 0 AND CLIENT_ID_TEMP IS NOT NULL", null);
        return res;
    }
    public Cursor selectExistingDistinctClient(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select DISTINCT CLIENT_ID from "+TABLE_SURVEY_ANSWER+" WHERE FLAG = 0 AND CLIENT_ID IS NOT NULL", null);
        return res;
    }
    public Cursor getLanguageList(){

        //insertDataSurveyList();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_LAUNGUAGE, null);
        return res;
    }

    public void insertDataLanguage(JSONArray jArray) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        for(int i = 0; i < jArray.length(); i++){
            ContentValues contentValue = new ContentValues();
            JSONObject json_data = jArray.getJSONObject(i);

            contentValue.put(LanguageCOL_2,json_data.getString("id"));
            contentValue.put(LanguageCOL_3,json_data.getString("survey_title"));
            //contentValue.put(SurveyCOL_4,json_data.getString("survey_description"));
            db.insert(TABLE_LAUNGUAGE,null,contentValue );
        }

    }
    public Cursor getSurveyList(){

        //insertDataSurveyList();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_SURVEY+" ORDER BY SURVEY_ID ASC", null);
        return res;
    }

    public void insertDataSurveyList(JSONArray jArray) throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();
        if(!ifSurveyListIsEmpty()){db.delete(TABLE_SURVEY, null, null);}

        for(int i = 0; i < jArray.length(); i++){
            ContentValues contentValue = new ContentValues();
            JSONObject json_data = jArray.getJSONObject(i);

            contentValue.put(SurveyCOL_1,json_data.getString("id"));
            contentValue.put(SurveyCOL_2,json_data.getString("survey_title"));
            contentValue.put(SurveyCOL_3,json_data.getString("survey_description"));
            db.insert(TABLE_SURVEY,null,contentValue );
        }
    }

    public void insertDataSectionList(JSONArray jArray) throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();
        if(!ifSurveyListIsEmpty()){db.delete(TABLE_SURVEY_SECTION, null, null);}

        for(int i = 0; i < jArray.length(); i++){
            ContentValues contentValue = new ContentValues();
            JSONObject json_data = jArray.getJSONObject(i);

            contentValue.put("SECTION_ID",json_data.getString("id"));
            Log.e("SECTION_ID",json_data.getString("id"));
            contentValue.put("SURVEY_ID",json_data.getString("survey_type"));
            contentValue.put("SECTION_TITLE",json_data.getString("section_title"));
            contentValue.put("SECTION_DESC",json_data.getString("section_description"));
            contentValue.put("SECTION_SUGGESTION",json_data.getString("section_suggestion"));
            db.insert(TABLE_SURVEY_SECTION,null,contentValue );
        }
    }
    public void insertDataGroupList(JSONArray jArray) throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();
        if(!ifSurveyListIsEmpty()){db.delete(TABLE_SECTION_GROUP, null, null);}

        for(int i = 0; i < jArray.length(); i++){
            ContentValues contentValue = new ContentValues();
            JSONObject json_data = jArray.getJSONObject(i);

            contentValue.put("GROUP_ID",json_data.getString("id"));
            contentValue.put("SECTION_ID",json_data.getString("section_type"));
            contentValue.put("SURVEY_ID",json_data.getString("survey_type"));
            contentValue.put("GROUP_TITLE",json_data.getString("group_title"));
            contentValue.put("GROUP_DESC",json_data.getString("group_description"));
            contentValue.put("GROUP_SUGGESTION",json_data.getString("group_suggestion"));
            db.insert(TABLE_SECTION_GROUP,null,contentValue );
        }
    }
    public void insertDataQuestion(JSONArray jArray) throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();
        if(!ifSurveyListIsEmpty()){db.delete(TABLE_SURVEY_QUESTION, null, null);}

        for(int i = 0; i < jArray.length(); i++){
            ContentValues contentValue = new ContentValues();
            JSONObject json_data = jArray.getJSONObject(i);

            contentValue.put("QUESTION_ID",json_data.getString("id"));
            contentValue.put("SECTION_ID",json_data.getString("section_type"));
            contentValue.put("GROUP_ID",json_data.getString("group_type"));
            contentValue.put("SURVEY_ID",json_data.getString("survey_type"));
            contentValue.put("QUESTION_TYPE",json_data.getString("question_type"));
            contentValue.put("QUESTION_TEXT",json_data.getString("question"));
            contentValue.put("QUESTION_SECTION_SUGGESTION",json_data.getString("question_suggestion"));
            contentValue.put("IS_OPTIONAL",json_data.getString("can_skip"));
            contentValue.put("QUESTION_OPTION",json_data.getString("question_options"));
            contentValue.put("QUESTION_ORDER",json_data.getString("question_order"));
            contentValue.put("QUESTION_NO",json_data.getString("question_no"));

            db.insert(TABLE_SURVEY_QUESTION,null,contentValue );
        }
    }
    public boolean insertDataLogin(String username,String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(COL_2,username);
        contentValue.put(COL_3,password);
        long result = db.insert(TABLE_NAME,null,contentValue );
        if(result == -1){
            return false;
        }else
        {
            return true;
        }
    }
    public boolean updateDataLogin(String username,String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(COL_2,username);
        contentValue.put(COL_3,password);

        db.update(TABLE_NAME,contentValue,"USERNAME = ?",new String[] { username });
        return true;
    }
    public boolean CheckIsDataAlreadyInDBorNotLogin(String fieldValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_NAME + " WHERE "+ COL_2+" =?";
        Cursor cursor = db.rawQuery(Query,new String[] {fieldValue}, null);
        if(cursor.getCount() <= 0){
            //cursor.close();
            return false;
        }
        //cursor.close();
        return true;
    }
    public boolean passwordMatchDBLogin(String fieldValue1 , String fieldValue2){
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_NAME + " WHERE "+ COL_2+" =?  AND "+COL_3+" =?";
        Cursor cursor = db.rawQuery(Query,new String[] {fieldValue1,fieldValue2}, null);
        if(cursor.getCount() <= 0){
            //cursor.close();
            return false;
        }
        //cursor.close();
        return true;
    }
    public Cursor getSectionList(String surveyId){
        Cursor cursor;
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_SURVEY_SECTION + " WHERE SURVEY_ID =? ";
        cursor = db.rawQuery(Query,new String[] {surveyId}, null);
        return cursor;
    }
    public Cursor getQuestionByGroupId(String groupId){
        Cursor csr;
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_SURVEY_QUESTION + " WHERE GROUP_ID =? ORDER BY QUESTION_ORDER ASC";
        csr = db.rawQuery(Query,new String[] {groupId}, null);
        return csr;

    }
    public Cursor getQuestionList(String sectionId){
        Cursor cursor;
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_SURVEY_QUESTION + " WHERE SECTION_ID =? ORDER BY QUESTION_NO,QUESTION_ORDER ASC";
        cursor = db.rawQuery(Query,new String[] {sectionId}, null);
        return cursor;

    }
    public String getGroupDesc(String groupid){
        Cursor cursor;
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_SECTION_GROUP + " WHERE GROUP_ID =? ";
        cursor = db.rawQuery(Query,new String[] {groupid}, null);
        if(cursor.getCount()>0) {
            cursor.moveToFirst();
            return cursor.getString(4);
        }else{
            return "";
        }
    }
    public String[] getSurveyDetails( String surveyId){
        String[] surveyDetails= new String[3];
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_SURVEY + " WHERE "+ SurveyCOL_1+" =? ";
        Cursor cursor = db.rawQuery(Query,new String[] {surveyId}, null);
        if(cursor.getCount() >0){
            while (cursor.moveToNext()){
                surveyDetails[0]=cursor.getString(0);
                surveyDetails[1]=cursor.getString(1);
                surveyDetails[2]=cursor.getString(2);

            }
            //cursor.close();
            return surveyDetails;

        }else{
            //cursor.close();
            return surveyDetails;
        }

    }
    public boolean ifSurveyListIsEmpty(){
        String count = "SELECT count(*) FROM "+TABLE_SURVEY;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if(icount>0){
            //mcursor.close();
            return false;
        }else{
            //mcursor.close();
            return true;
        }
//populate table
    }
}
