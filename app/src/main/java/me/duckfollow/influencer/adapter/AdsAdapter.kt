package me.duckfollow.influencer.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.custom_recycler_view_ads.view.*
import me.duckfollow.influencer.R
import me.duckfollow.influencer.models.AdsModel
import me.duckfollow.influencer.util.ConvertImagetoBase64

class AdsAdapter(val items : ArrayList<AdsModel>, val context: Context) : RecyclerView.Adapter<ViewHolderAds>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolderAds {
        return ViewHolderAds(LayoutInflater.from(context).inflate(R.layout.custom_recycler_view_ads, p0, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: ViewHolderAds, p1: Int) {

        p0.btn_share.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                putExtra(Intent.EXTRA_TEXT, items[p1].url)
                putExtra(Intent.EXTRA_TITLE, items[p1].text)
                type = "text/*"
            }

            val shareIntent = Intent.createChooser(sendIntent, items[p1].text)
            context.startActivity(shareIntent)
        }

        try {
            p0.img_ads.setImageBitmap(ConvertImagetoBase64().base64ToBitmap(items[p1].img))
        }catch (e:Exception){

        }

    }
}

class ViewHolderAds (view: View) : RecyclerView.ViewHolder(view) {
    val btn_share = view.btn_share
    val img_ads = view.img_ads
}