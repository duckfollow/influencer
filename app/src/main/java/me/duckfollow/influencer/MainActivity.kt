package me.duckfollow.influencer

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import me.duckfollow.influencer.activity.ExchangeActivity
import me.duckfollow.influencer.adapter.AdsAdapter
import me.duckfollow.influencer.models.AdsModel
import me.duckfollow.influencer.user.UserProfile
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {
    lateinit var myRefUser: DatabaseReference
    lateinit var myRefUserSetting: DatabaseReference
    private lateinit var myRef_point:DatabaseReference
    private lateinit var myRef_ads:DatabaseReference
    lateinit var UserSettingListener:ValueEventListener
    lateinit var adsAdapter: AdsAdapter
    val data_ads = ArrayList<AdsModel>()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val android_id = Settings.Secure.getString(this.getContentResolver(),
            Settings.Secure.ANDROID_ID);
        text_id.text = android_id
        val current = LocalDateTime.now()
        val database = FirebaseDatabase.getInstance().reference
        myRefUser = database.child("user/"+android_id)
        myRefUserSetting = database.child("usersetting/"+android_id)
        myRef_point = database.child("point/"+android_id)
        myRef_ads = database.child("ads/")
        val map = HashMap<String, Any>()
        map.put("id",android_id)
        map.put("date",current.toString())
        map.put("time",UserProfile(this).getTimerDefault())
        myRefUser.updateChildren(map)

        MobileAds.initialize(this, "ca-app-pub-2582707291059118~8882306426");
        val adLoader = AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
            .forUnifiedNativeAd(object : UnifiedNativeAd.OnUnifiedNativeAdLoadedListener{
                override fun onUnifiedNativeAdLoaded(p0: UnifiedNativeAd?) {
                    val colorDrawable = ColorDrawable(getResources().getColor(R.color.colorWhite))
                    val styles =
                        NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build()

                    val template: TemplateView = findViewById(R.id.my_template)
                    template.setStyles(styles)
                    template.setNativeAd(p0)

                    val template2: TemplateView = findViewById(R.id.my_template2)
                    template2.setStyles(styles)
                    template2.setNativeAd(p0)
                }
            }).withAdListener(object : AdListener(){
                override fun onAdClicked() {
                    super.onAdClicked()
                    Log.d("keyUser",UserProfile(this@MainActivity).getAdOpen())
                    if (!UserProfile(this@MainActivity).getAdOpen().toBoolean()) {
                        UserProfile(this@MainActivity).setTimer(UserProfile(this@MainActivity).getTimerDefault())
                        val key = myRef_point.push().getKey().toString()
                        Log.d("keyUser",key)
                        val map = HashMap<String, Any>()
                        map.put("point", 0)
                        map.put("date",current.toString())
                        myRef_point.child(key).updateChildren(map)
                    }
                }
                override fun onAdOpened() {
                    super.onAdOpened()
                    UserProfile(this@MainActivity).setAdOpen(true.toString())
                }
            })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())

        val PointListener = object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                var point_text = 0.0f
                p0.children.forEach {mdataSnapshot: DataSnapshot? ->
                    val c = mdataSnapshot
                    val date = c?.child("date")?.getValue().toString()
                    val point = c?.child("point")?.getValue().toString()

                    try {
                        point_text += point.toFloat()
                    }catch (e:Exception) {

                    }

                    Log.d("date_test",date)
                }

                txt_point.text =  String.format("%,.2f", point_text) +"฿"
            }
        }
        myRef_point.addValueEventListener(PointListener)

        val UserListener = object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                try {
                    val timerDefault = p0.child("time").getValue().toString().toLong()
                    UserProfile(this@MainActivity).setTimerDefault(timerDefault)
                }catch (e:java.lang.Exception) {

                }
            }
        }

        myRefUser.addValueEventListener(UserListener)

        UserSettingListener = object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                try {
                    val timeEnd = p0.child("timeEnd").getValue().toString().toLong()
                    val time = p0.child("time").getValue().toString().toLong()
                    val adopen = p0.child("adopen").getValue().toString()
                    val reward = p0.child("reward").getValue().toString()
                    UserProfile(this@MainActivity).setTimeEnd(timeEnd)
                    UserProfile(this@MainActivity).setAdOpen(adopen)
                    val timeNew= time + ( timeEnd - System.currentTimeMillis())
                    UserProfile(this@MainActivity).setTimer(timeNew)

                    val view_timer = findViewById<LinearLayout>(R.id.view_timer)
                    if (UserProfile(this@MainActivity).getAdOpen().toBoolean()) {
                        view_timer.visibility = View.VISIBLE
                        countDown()
                    } else {
                        view_timer.visibility = View.GONE
                    }
                    UserProfile(this@MainActivity).setReward(reward)
                    val view_reward = findViewById<LinearLayout>(R.id.view_reward)
                    if (reward.toBoolean()) {
                        view_reward.visibility = View.VISIBLE
                    } else {
                        view_reward.visibility = View.GONE
                    }
                }catch (e:Exception) {

                }
            }
        }

        myRefUserSetting.addValueEventListener(UserSettingListener)

        btn_exchange.setOnClickListener {
            val i_exchange = Intent(this,ExchangeActivity::class.java)
            startActivity(i_exchange)
        }

        val AdsListener = object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                data_ads.clear()
                p0.children.forEach {mdataSnapshot: DataSnapshot? ->
                    val c = mdataSnapshot
                    val text = c?.child("text")?.getValue().toString()
                    val img = c?.child("img")?.getValue().toString()
                    val url = c?.child("url")?.getValue().toString()
                    val isShow = c?.child("isShow")?.getValue().toString()

                    data_ads.add(AdsModel(text,img,url))

                }
                Collections.shuffle(data_ads)
                adsAdapter.notifyDataSetChanged()
            }
        }

        myRef_ads.addValueEventListener(AdsListener)

        list_ads.layoutManager = LinearLayoutManager(this)
        list_ads.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        adsAdapter = AdsAdapter(data_ads,this)
        list_ads.adapter = adsAdapter
//        list_view_user.setScrollingTouchSlop(RecyclerView.TOUCH_SLOP_PAGING)
//        val snapHelper = LinearSnapHelper() // Or PagerSnapHelper
//        snapHelper.attachToRecyclerView(list_view_user)
    }

    override fun onStart() {
        super.onStart()
        val time= System.currentTimeMillis()
        UserProfile(this).setTimeEnd(time)
//        val map = HashMap<String, Any>()
//        map.put("adopen",UserProfile(this).getAdOpen())
//        map.put("timeEnd",time)
//        map.put("time",UserProfile(this).getTimer())
//        myRefUserSetting.updateChildren(map)
    }

    override fun onDestroy() {
        super.onDestroy()
        val time= System.currentTimeMillis()
        UserProfile(this).setTimeEnd(time)
        val map = HashMap<String, Any>()
        map.put("adopen",UserProfile(this).getAdOpen())
        map.put("timeEnd",time)
        map.put("time",UserProfile(this).getTimer())
        map.put("reward",UserProfile(this).getReward())
        myRefUserSetting.updateChildren(map)
    }

    override fun onStop() {
        super.onStop()
        val time= System.currentTimeMillis()
        UserProfile(this).setTimeEnd(time)
        val map = HashMap<String, Any>()
        map.put("adopen",UserProfile(this).getAdOpen())
        map.put("timeEnd",time)
        map.put("time",UserProfile(this).getTimer())
        map.put("reward",UserProfile(this).getReward())
        myRefUserSetting.updateChildren(map)
    }

    override fun onResume() {
        super.onResume()
//        val txt_timer = findViewById<TextView>(R.id.txt_timer)
        val view_timer = findViewById<LinearLayout>(R.id.view_timer)
        if (UserProfile(this).getAdOpen().toBoolean()) {
            view_timer.visibility = View.VISIBLE
            countDown()
        } else {
            view_timer.visibility = View.GONE
        }
//        myRefUserSetting.addValueEventListener(UserSettingListener)
    }

    fun countDown() {
        val txt_timer = findViewById<TextView>(R.id.txt_timer)
        val view_timer = findViewById<LinearLayout>(R.id.view_timer)
        val c = object:CountDownTimer(UserProfile(this).getTimer(),1000) {
            override fun onFinish() {
                UserProfile(this@MainActivity).setAdOpen(false.toString())
                view_timer.visibility = View.GONE
            }

            override fun onTick(millisUntilFinished: Long) {
                UserProfile(this@MainActivity).setTimer(millisUntilFinished)
                val seconds = ((millisUntilFinished / 1000) % 60).toInt()
                val minutes = (millisUntilFinished / (1000 * 60) % 60).toInt()
                val hours = (millisUntilFinished / (1000 * 60 * 60) % 24).toInt()
                txt_timer.setText(String.format("%02d",hours)+":"+String.format("%02d",minutes)+":"+ String.format("%02d",seconds))
            }
        }
        c.start()
    }

}
