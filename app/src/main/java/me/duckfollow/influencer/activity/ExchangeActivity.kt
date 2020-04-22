package me.duckfollow.influencer.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_exchange.*
import kotlinx.android.synthetic.main.activity_main.*
import me.duckfollow.influencer.R
import java.time.LocalDateTime

class ExchangeActivity : AppCompatActivity() {
    private lateinit var myRef_point: DatabaseReference
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exchange)
        val android_id = Settings.Secure.getString(this.getContentResolver(),
            Settings.Secure.ANDROID_ID);
        val current = LocalDateTime.now()
        val database = FirebaseDatabase.getInstance().reference
        myRef_point = database.child("point/"+android_id)
        val i = input.text
        btn_ok.setOnClickListener {
            val key = myRef_point.push().getKey().toString()
            Log.d("keyUser",key)
            val map = HashMap<String, Any>()
            map.put("point", i.toString().toInt())
            map.put("description", "exchange")
            map.put("date",current.toString())
            myRef_point.child(key).updateChildren(map).addOnSuccessListener {
                input.setText("")
            }
        }

        val PointListener = object : ValueEventListener {
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

                text_point.text =  String.format("%,.2f", point_text) +"฿"
            }
        }
        myRef_point.addValueEventListener(PointListener)
    }
}
