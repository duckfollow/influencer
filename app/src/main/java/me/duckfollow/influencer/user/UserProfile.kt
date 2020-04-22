package me.duckfollow.influencer.user

import android.content.Context
import android.content.SharedPreferences

class UserProfile {
    var User: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    private val USER_PREFS = "USER_PREFS"

    constructor(context: Context) {
        User = context!!.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
    }

    fun setAdOpen(isAdOpen:String){
        this.setUserData("AdOpen", isAdOpen)
    }

    fun getAdOpen():String{
        return User.getString("AdOpen","false")
    }

    fun setReward(isAdOpen:String){
        this.setUserData("reward", isAdOpen)
    }

    fun getReward():String{
        return User.getString("reward","false")
    }

    fun setTimer(time:Long) {
        editor = User.edit()
        editor.putLong("timer",time)
        editor.commit()
    }

    fun getTimer():Long {
        return User.getLong("timer",1800000)
    }

    fun setTimerDefault(time:Long) {
        editor = User.edit()
        editor.putLong("timerDefault",time)
        editor.commit()
    }

    fun getTimerDefault():Long {
        return User.getLong("timerDefault",1800000)
    }

    fun setTimeStart (time: Long) {
        editor = User.edit()
        editor.putLong("timerStart",time)
        editor.commit()
    }

    fun getTimeStart():Long {
        return User.getLong("timerStart",0)
    }

    fun setTimeEnd (time: Long) {
        editor = User.edit()
        editor.putLong("timerEnd",time)
        editor.commit()
    }

    fun getTimeEnd():Long {
        return User.getLong("timerEnd",0)
    }

    fun setUserData(key:String,value:String){
        editor = User.edit()
        editor.putString(key, value)
        editor.commit()
    }


    fun getUserData(key:String):String{
        return User.getString(key,"")
    }
}