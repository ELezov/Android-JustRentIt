package elezov.com.justrentit

import android.graphics.BitmapFactory
import android.provider.ContactsContract
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import elezov.com.justrentit.model.Category

import java.util.ArrayList

/**
 * Created by USER on 08.03.2017.
 */

class CatalogAdapter internal constructor() : RecyclerView.Adapter<CatalogAdapter.ViewHolder>() {

    internal var data: List<Category> = ArrayList()

    lateinit var onButtonClickListener:((Int)->Unit)

    fun add(items: List<Category>) {
        this.data = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.catalog_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.catalogText.text = data[position].name
        if (data[position].stringPhoto!=""&&data[position].stringPhoto!=null)
        {
            var decodedByte = Base64.decode(data[position].stringPhoto,0)
            holder.imageCatalogItem.setImageBitmap(BitmapFactory.decodeByteArray(decodedByte,0,decodedByte.size))
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(var v: View) : RecyclerView.ViewHolder(v) {
        var catalogText: TextView
        var imageCatalogItem:ImageView

        init {
            imageCatalogItem=v.findViewById(R.id.img_item_catalog) as ImageView
            catalogText = v.findViewById(R.id.catalog_text) as TextView
            v.setOnClickListener {
                onButtonClickListener.invoke(adapterPosition)
            }
        }
    }
}
