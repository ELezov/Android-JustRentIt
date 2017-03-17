package elezov.com.justrentit

import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import elezov.com.justrentit.model.Advert
import elezov.com.justrentit.model.Category
import java.util.*

/**
 * Created by USER on 09.03.2017.
 */

class AdvertListAdapter : RecyclerView.Adapter<AdvertListAdapter.ViewHolder>() {
    internal var data: List<Advert> = ArrayList()

    lateinit var onButtonClickListener:((Int)->Unit)

    fun add(items:List<Advert>){
        this.data=items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.advert_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameText.text=data[position].name;
        holder.priceDayText.text=data[position].pricePerDay.toString()
        holder.priceWeekText.text=data[position].pricePerWeek.toString()
        holder.priceMonthText.text=data[position].pricePerMonth.toString()
        if (data[position].stringPhoto!="")
        {
            var decodedByte = Base64.decode(data[position].stringPhoto,0)
            holder.imageView.setImageBitmap(BitmapFactory.decodeByteArray(decodedByte,0,decodedByte.size))
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }



    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameText:TextView
        var priceDayText:TextView
        var priceWeekText:TextView
        var priceMonthText:TextView
        var imageView:ImageView


        init {
            nameText=itemView.findViewById(R.id.card_advert_name) as TextView
            priceDayText=itemView.findViewById(R.id.card_advert_price_day) as TextView
            priceWeekText=itemView.findViewById(R.id.card_advert_price_week) as TextView
            priceMonthText=itemView.findViewById(R.id.card_advert_price_month) as TextView
            imageView=itemView.findViewById(R.id.advert_item_image) as ImageView

            itemView.setOnClickListener{
                onButtonClickListener.invoke(adapterPosition)
            }
        }
    }
}
