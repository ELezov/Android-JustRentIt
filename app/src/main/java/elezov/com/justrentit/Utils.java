package elezov.com.justrentit;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import elezov.com.justrentit.model.Category;

import static android.R.attr.key;

/**
 * Created by USER on 08.03.2017.
 */

public  class Utils {

    public String URL_AZURE="https://justrentit.azurewebsites.net";
    public static String SHARED_KEY="shared_key";
    public static String USER_MAIL="user_mail";
    public static String USER_NAME="user_name";
    public static String USER_ID="user_id";
    public String user_Mail;
    public String user_Name;
    public String currentAdvert;
    public Class currFrgmClass;

    public Class getCurrFrgmClass() {
        return currFrgmClass;
    }

    public void setCurrFrgmClass(Class currFrgmClass) {
        this.currFrgmClass = currFrgmClass;
    }

    public String getCurrentAdvert() {
        return currentAdvert;
    }

    public void setCurrentAdvert(String currentAdvert) {
        this.currentAdvert = currentAdvert;
    }

    public String getUser_Name() {
        return user_Name;
    }

    public void setUser_Name(String user_Name) {
        this.user_Name = user_Name;
    }

    public String getUser_Mail() {
        return user_Mail;
    }

    public void setUser_Mail(String user_Maii) {
        this.user_Mail = user_Maii;
    }

    private int current_id=0;


    public List<Category>  listCategory=new ArrayList<Category>();


    private static Utils utils;

    private Utils(){

    }

    public static Utils getInstance(){
        if(utils==null){
            utils=new Utils();
        }
        return utils;
    }

    public String getURL_AZURE() {
        return URL_AZURE;
    }

    public void setURL_AZURE(String URL_AZURE) {
        this.URL_AZURE = URL_AZURE;
    }

    public int getCurrent_id() {
        return current_id;
    }

    public void setCurrent_id(int current_id) {
        this.current_id = current_id;
    }

    public List<Category> getListCategory() {
        return listCategory;
    }

    public void setListCategory(List<Category> listCategory) {
        this.listCategory = listCategory;
    }

    public void setUserInfoPreferences(String name,String mail, Activity activity){
        Context context= activity;
        SharedPreferences sh= context.getSharedPreferences(SHARED_KEY,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sh.edit();
        editor.putString(USER_MAIL,mail);
        editor.putString(USER_NAME,name);
        editor.commit();
    }

    public String getUserMail(Activity activity){

        SharedPreferences sh=activity.getSharedPreferences(SHARED_KEY,Context.MODE_PRIVATE);
        return sh.getString(USER_MAIL,"-10");
    }

    public String getUserName(Activity activity) {
        SharedPreferences sh=activity.getSharedPreferences(SHARED_KEY,Context.MODE_PRIVATE);
        return sh.getString(USER_NAME,"-10");
    }

    public String getUserId(Activity activity) {
        SharedPreferences sh=activity.getSharedPreferences(SHARED_KEY,Context.MODE_PRIVATE);
        return sh.getString(USER_ID,"-10");
    }
}
