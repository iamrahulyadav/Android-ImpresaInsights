package com.example.codemaven3015.sampleapplogin;

import android.app.Application;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by CodeMaven3015 on 11/6/2017.
 */

public class GlobalVariables extends Application {
    private Cursor Question;
    //private int radioInputTextCheck = 0;
    private Cursor section;
    private int sectionCount = 0;
    private int questionCount;
    private int questionCounter = 0;
    private String name = "";
    private String clientId = "new";
    private ArrayList<String> groupList = new ArrayList<String>();
    private JSONArray answer = new JSONArray() ;

    public void setGroup(String  groupid){
        groupList.add(groupid);
    }
    public boolean ifexistGroup(String groupId){
        return groupList.contains(groupId);
    }
    public void removeGroup(String groupId){
        groupList.remove(groupId);
    }
    public void setName(String setname){
        name = setname;
    }
    public String getName(){
        return name;
    }
    public void resetAllGlobalVariable(){
        sectionCount = 0;
        questionCounter = 0;
        name = "";
       //radioInputTextCheck=0;
        answer = new JSONArray();
        groupList = new ArrayList<String>();
        //Question = null;
    }
    public void resetquestioncounter(){
        questionCounter = 0;
        //radioInputTextCheck = 0;
    }
    public void setClientId(String id){
        clientId = id;
    }
    public String getClientId(){
        return clientId;
    }
    public void updateAtAnswer(int i,String ans,String queId,String radio,boolean flag,boolean isskiped){
        JSONObject obj = new JSONObject();

        try {
            if(ans.equals("") &&(!isskiped)){
                ans = answer.getJSONObject(i).getString("answer");
            }
            obj.put("answer",ans);
            obj.put("question_no",queId);
            if(flag) {
                obj.put("radio", radio);
            }else{
                obj.put("order", radio);
            }

            answer.put(i,obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public JSONArray getAnswer(){
        return answer;
    }
    public String getAnswerByQuestionOrder(String order) throws JSONException {
        for(int i =0;i<answer.length();i++){
            JSONObject obj = answer.getJSONObject(i);
            if(order.equals(obj.getString("order"))){
                return (obj.getString("answer"));
            }

        }
        return "";
    }
    public void setAnswerFromSavedInstance(JSONArray obj){
        answer = new JSONArray();
        answer = obj;
    }
    public void addAnswerInJsonArray(JSONObject obj){
        answer.put(obj);
    }
    public GlobalVariables(){
        Log.e("INSIDE","CLASS constructor");
    }
    public void setsectionList(Cursor sectionList){
        section = sectionList;
    }
    public Cursor getSectionList(){
        return section;
    }
    public int incrementSectionCount(){
        return sectionCount++;
    }
    public int decrementSectionCount(){
        return sectionCount--;
    }
    public int getSectionCount(){
        return sectionCount;
    }
    public void setSectionCount(int count){
        sectionCount = count;
    }

    public void setGlobalCursor(Cursor csr){
        Question = csr;
        questionCount = csr.getCount();
        Log.e("Inside","global setData"+questionCount);

    }
    public int getCount(){
        return questionCount;
    }
    public int getCounter(){
        Log.e("GET_COUNT",questionCounter+"");
        return questionCounter;
    }
    public void setQuestionCounter(){
        questionCounter = questionCount;
    }
    public int countIncrement(){
        questionCounter++;
        Log.e("GLOBAL INCREMENT",questionCounter+"");
        return questionCounter;
    }
    public void setQuestionSavedCounter(int count){
        questionCounter = count;
    }
    public int countDecrement(){
        questionCounter--;
        Log.e("GLOBAL DECREMENT",questionCounter+"");
        return questionCounter;
    }
    public Cursor getQuestionCursor(){
        Log.e("Inside","getData");
        return Question;
    }
}


