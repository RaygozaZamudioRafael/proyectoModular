package com.example.detectorenfermedadesdeltomateapp;

import android.content.Context;
import android.content.SharedPreferences;

public class UsuarioLocalAlmacenado {
    public static final String SP_NAME = "userDetails";
    SharedPreferences userLocalDatabase;

    public UsuarioLocalAlmacenado(Context context){
        userLocalDatabase = context.getSharedPreferences(SP_NAME,0);

    }

    public void storeUserData(Usuario user){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putString("username", user.username);
        spEditor.putString("password", user.password);
        spEditor.putString("email", user.email);
        spEditor.commit();

    }

    public Usuario getLoggInUser(){
        String username = userLocalDatabase.getString("username","");
        String password = userLocalDatabase.getString("password","");
        String email = userLocalDatabase.getString("email","");

        Usuario usuarioAlmacenado = new Usuario(username,password,email);
        return usuarioAlmacenado;
    }

    public void setLoggInUser(boolean loggedIn){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean("loggedIn",loggedIn);
        spEditor.commit();
    }

    public boolean getAuthLogInUser(){
        if (userLocalDatabase.getBoolean("loggedIn",false)){
            return true;
        }
        else{
            return false;
        }
    }

    public void clearUserData(){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.clear();
        spEditor.commit();
    }


}
