package com.example.codemaven3015.sampleapplogin;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
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
    public static final String TABLE_REGISTRATION = "New_Client_Registration";
    public static final String TABLE_WEEK = "Weekly_Money_Market_Update";
    public static final String TABLE_PROJECT = "Project_Table";
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
        db.execSQL("create table "+ TABLE_SECTION_GROUP+" (GROUP_ID INTEGER, SECTION_ID INTEGER, SURVEY_ID INTEGER, GROUP_TITLE TEXT, GROUP_DESC TEXT, GROUP_SUGGESTION TEXT ,FLAG TEXT)");
        db.execSQL("create table "+ TABLE_SURVEY_QUESTION+" (QUESTION_ID INTEGER, SECTION_ID INTEGER, GROUP_ID INTEGER, SURVEY_ID INTEGER, QUESTION_TYPE TEXT, QUESTION_TEXT TEXT, QUESTION_SECTION_SUGGESTION TEXT,IS_OPTIONAL INTEGER, QUESTION_OPTION TEXT, QUESTION_ORDER TEXT, QUESTION_NO INTEGER,COMPARE_WITH TEXT, QUESTION_TIMER TEXT, QUESTION_TITLE TEXT)");
        //db.execSQL("create table "+ TABLE_SURVEY_QUESTION_OPTION+" (OPTION_ID INTEGER, QUESTION_ID INTEGER, OPTION_DESC TEXT)");
        db.execSQL("create table "+ TABLE_SURVEY_ANSWER+" (ANSWER_ID INTEGER PRIMARY KEY AUTOINCREMENT, QUESTION_ID INTEGER, CLIENT_ID INTEGER, CLIENT_ID_TEMP INTEGER,ANSWER_TEXT TEXT, FLAG INTEGER,SURVEY_ID INTEGER,TYPE TEXT,PROJECT_CODE TEXT)");
        db.execSQL("create table "+TABLE_CLIENT_INFO+" (CLIENT_ID INTEGER, SURVEY_ID INTEGER)");
        db.execSQL("create table "+TABLE_REGISTRATION+" (CLIENT_ID INTEGER, FIRST_NAME TEXT, LAST_NAME TEXT, PHONE_NUMBER TEXT)");
        db.execSQL("create table "+TABLE_WEEK+" (CLIENT_ID INTEGER, SURVEY_ID INTEGER, WEEK TEXT)");
        db.execSQL("create table "+TABLE_PROJECT+" (PROJECT_ID INTEGER, PROJECT_CODE TEXT, PROJECT_TITLE TEXT)");
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
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_REGISTRATION);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_WEEK);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_PROJECT);
        onCreate(db);
    }
    public void updateWeeklyInfo(String client_id,String survey_id,String week ){
        SQLiteDatabase db = this.getWritableDatabase();

        String Query = "SELECT * FROM " + TABLE_WEEK + " WHERE CLIENT_ID =? AND SURVEY_ID =?";
        Cursor ifExist = db.rawQuery(Query,new String[] {client_id,survey_id}, null);
        if(ifExist.getCount()>0){
//            switch(week){
//                case "1":
//                    db.execSQL("UPDATE "+TABLE_WEEK+" SET WEEK_1= 1 WHERE CLIENT_ID = "+client_id+" AND SURVEY_ID = "+survey_id);
//                    break;
//                case "2":
//                    db.execSQL("UPDATE "+TABLE_WEEK+" SET WEEK_2= 1 WHERE CLIENT_ID = "+client_id+" AND SURVEY_ID = "+survey_id);
//                    break;
//                case "3":
//                    db.execSQL("UPDATE "+TABLE_WEEK+" SET WEEK_3= 1 WHERE CLIENT_ID = "+client_id+" AND SURVEY_ID = "+survey_id);
//                    break;
//                case "4":
                    db.execSQL("UPDATE "+TABLE_WEEK+" SET WEEK = "+week+" WHERE CLIENT_ID = "+client_id+" AND SURVEY_ID = "+survey_id);
                   // break;
           // }
        }else{
            ContentValues contentValue = new ContentValues();
            contentValue.put("CLIENT_ID", client_id);
            contentValue.put("SURVEY_ID", survey_id);
            contentValue.put("WEEK", "1");
//            contentValue.put("WEEK_2", "0");
//            contentValue.put("WEEK_3", "0");
//            contentValue.put("WEEK_4", "0");
            db.insert(TABLE_WEEK, null, contentValue);
        }

    }
    public void addRegistrationDetails(String first,String last,String phone){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put("CLIENT_ID", "1");
        contentValue.put("FIRST_NAME", first);
        contentValue.put("LAST_NAME", last);
        contentValue.put("PHONE_NUMBER", phone);
        db.insert(TABLE_REGISTRATION, null, contentValue);
    }
    public void updateClientId(int updateClientId){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE "+TABLE_REGISTRATION+" SET CLIENT_ID= "+updateClientId +" WHERE CLIENT_ID = 1");
       //db.update(TABLE_REGISTRATION,contentValue,"USERNAME = ?",new String[] { username });
    }
    public void deleteRegistrationDetails(String clientId){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REGISTRATION, "CLIENT_ID =?", new String[]{clientId});
    }
    public void updateClientIdTable(String clientId,String surveyId){
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_CLIENT_INFO + " WHERE CLIENT_ID =? AND SURVEY_ID =?";
        Cursor res = db.rawQuery(Query,new String[] {clientId,surveyId}, null);
        if(res.getCount() < 1) {
            ContentValues contentValue = new ContentValues();
            contentValue.put("CLIENT_ID", clientId);
            contentValue.put("SURVEY_ID", surveyId);
            db.insert(TABLE_CLIENT_INFO, null, contentValue);
        }
    }
    public void updateClientTablefromAPI(JSONArray data) throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_CLIENT_INFO;
        Cursor res = db.rawQuery(Query, null);
        if(res.getCount()>0){
            db.delete(TABLE_CLIENT_INFO, null, null);
        }
        for(int i = 0;i<data.length();i++){
            ContentValues contentValue = new ContentValues();
            contentValue.put("CLIENT_ID", data.getJSONObject(i).getString("participant_code"));
            contentValue.put("SURVEY_ID", data.getJSONObject(i).getString("survey_type"));
            db.insert(TABLE_CLIENT_INFO, null, contentValue);
        }
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
    public Cursor getRegistrationDetails(String clientId){

        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_REGISTRATION + " WHERE CLIENT_ID =?";
        Cursor res = db.rawQuery(Query,new String[] {clientId}, null);
        return res;

    }
    public void updateAnswerInTable( JSONArray answer,boolean isOldClient,String survey_id,String client_id,boolean isDone,String project){
        SQLiteDatabase db = this.getWritableDatabase();
        int clientId = 0;
        if(!isOldClient){
            Random r = new Random();
            clientId = (r.nextInt(9999999));
            updateClientId(clientId);
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
                    if(!isDone){
                        contentValue.put("FLAG", "2");
                    }else {
                        contentValue.put("FLAG", "0");
                    }
                } else {
                    contentValue.put("CLIENT_ID_TEMP", clientId);
                    contentValue.put("FLAG", "0");
                }
                String ans = obj.getString("answer");
                String radio = "";
                if(!(survey_id.equals("2")&& survey_id.equals("3"))){
                    radio = obj.getString("radio");
                }

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
                //contentValue.put("FLAG", "0");
                contentValue.put("PROJECT_CODE",project);
                db.insert(TABLE_SURVEY_ANSWER,null,contentValue);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        //getAnswerFromDB();

    }
    public void deleteAnswerIfUpdated(String clientId,String survey_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SURVEY_ANSWER, "CLIENT_ID_TEMP =?", new String[]{clientId});
        if (survey_id.equals("")){
            db.delete(TABLE_SURVEY_ANSWER, "CLIENT_ID =? ", new String[]{clientId});
        }else{
            db.delete(TABLE_SURVEY_ANSWER, "CLIENT_ID =? AND SURVEY_ID =?", new String[]{clientId, survey_id});
        }
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
    public Cursor getAnswerFromDB(String surveyId,String clientId,boolean flag){
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "";
        Cursor res;
        if(flag){
            Query = "SELECT * FROM " + TABLE_SURVEY_ANSWER + " WHERE CLIENT_ID_TEMP =?";
            res = db.rawQuery(Query,new String[] {clientId}, null);
        }else{
            Query = "SELECT * FROM " + TABLE_SURVEY_ANSWER + " WHERE CLIENT_ID =? AND SURVEY_ID =? ";
            res = db.rawQuery(Query,new String[] {clientId,surveyId}, null);
        }
        //Cursor res = db.rawQuery("select * from "+TABLE_SURVEY_ANSWER+" WHERE FLAG = 0", null,);
        return res;
    }
    public boolean ifFlageisNottwo(String clientId){
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "";
        Query = "SELECT * FROM " + TABLE_SURVEY_ANSWER + " WHERE CLIENT_ID =? AND FLAG = 2";
        Cursor res = db.rawQuery(Query,new String[] {clientId}, null);
        if(res.getCount()>0){
            return false;
        }else {
            return true;
        }
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
    public ArrayList<String> selectExistingDistinctSameClientSurvey(String client_id){
        SQLiteDatabase db = this.getWritableDatabase();

        String Query = //"select * from "+TABLE_SURVEY_ANSWER+" WHERE FLAG = 0 AND CLIENT_ID =?";
                "select DISTINCT SURVEY_ID from "+TABLE_SURVEY_ANSWER+" WHERE FLAG = 0 AND CLIENT_ID =?";
        Cursor res1 = db.rawQuery(Query,new String[] {client_id}, null);
        ArrayList<String> diffrentSurveyId = new ArrayList<String>();
        res1.moveToFirst();
        do{
            diffrentSurveyId.add(res1.getString(0));
        }while(res1.moveToNext());
//        for(int i = 0;i<res1.getCount();i++){
//            String value = res1.getString(6);
//            value = value+res1.getString(3);
//            Log.e("fvd",value);
//        }
        res1.close();
        return diffrentSurveyId;
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
            contentValue.put("FLAG",json_data.getString("can_skip"));
            db.insert(TABLE_SECTION_GROUP,null,contentValue );
        }
    }
    public void insertDataProject(JSONArray jArray) throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();
        if(!ifSurveyListIsEmpty()){db.delete(TABLE_PROJECT, null, null);}

        for(int i = 0; i < jArray.length(); i++){
            ContentValues contentValue = new ContentValues();
            JSONObject json_data = jArray.getJSONObject(i);
            contentValue.put("PROJECT_ID",json_data.getString("id"));
            contentValue.put("PROJECT_CODE",json_data.getString("project_code"));
            contentValue.put("PROJECT_TITLE",json_data.getString("project_title"));

            db.insert(TABLE_PROJECT,null,contentValue );
        }
    }
    public Cursor getProjectData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_PROJECT ;
        Cursor cursor = db.rawQuery(Query, null);
        return cursor;
    }
    public String getProjectCodeByProjectTitle(String title){
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_PROJECT +" WHERE PROJECT_TITLE =?";
        Cursor cursor = db.rawQuery(Query,new String[] {title}, null);
        cursor.moveToFirst();
        if(cursor.getCount()>0){
            return cursor.getString(1);
        }else{
            return "";
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
            contentValue.put("COMPARE_WITH",json_data.getString("compare_with"));
            contentValue.put("QUESTION_TIMER",json_data.getString("question_timer"));
            contentValue.put("QUESTION_TITLE",json_data.getString("question_title"));

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
    public String getGroupIdbyQuestionId(String questionId){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor csr;
        String Query = "SELECT * FROM " + TABLE_SURVEY_QUESTION + " WHERE QUESTION_ID = ?";
        csr = db.rawQuery(Query,new String[] {questionId}, null);
        csr.moveToFirst();
        if(csr.getCount()>0){
            return csr.getString(2);
        }else{
            return "";
        }
    }
    public String getQuestionByQuestionOrderandSurveyId(String question_order,String surveyId){
        Cursor csr1;
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_SURVEY_QUESTION + " WHERE QUESTION_ORDER =? AND SURVEY_ID =? ";
        csr1 = db.rawQuery(Query,new String[] {question_order,surveyId}, null);
        if(csr1.getCount()>0) {
            csr1.moveToFirst();
            return csr1.getString(0);
        }else{
            return "0";
        }
    }

    public Cursor getQuestionByQuestionOrder(String question_order,String section_id){
        Cursor csr;
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_SURVEY_QUESTION + " WHERE QUESTION_ORDER =? AND SECTION_ID =? ";
        csr = db.rawQuery(Query,new String[] {question_order,section_id}, null);
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
    public String can_SkipGroup(String groupid){
        Cursor cursor;
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_SECTION_GROUP + " WHERE GROUP_ID =? ";
        cursor = db.rawQuery(Query,new String[] {groupid}, null);
        if(cursor.getCount()>0) {
            cursor.moveToFirst();
            return cursor.getString(6);
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
