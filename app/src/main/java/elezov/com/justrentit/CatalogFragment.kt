package elezov.com.justrentit


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import elezov.com.justrentit.model.Category

import java.util.Arrays

/**
 * Created by USER on 08.03.2017.
 */

class CatalogFragment : Fragment() {
    lateinit var rv: RecyclerView
    lateinit var adapter: CatalogAdapter
    lateinit var utils: Utils

    private lateinit var clickCallbackLevel2 : CatalogClickListener


    interface CatalogClickListener{
        fun onFragmentClick()
    }


    private lateinit var clickCallback: OpenAdvertListClickListener

    interface OpenAdvertListClickListener{
        fun onOpenAdvertList(id:Int)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        utils = Utils.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.catalog_fragment_layout, container, false)
        rv = rootView.findViewById(R.id.catalog_list) as RecyclerView

        val data = utils.getListCategory()

        rv.layoutManager = LinearLayoutManager(rv.context)
        adapter = CatalogAdapter()
        clickCallback = activity as OpenAdvertListClickListener
        adapter.onButtonClickListener = {position->
            clickCallback.onOpenAdvertList(position)
        }

        adapter.add(data)
        rv.adapter = adapter


        return rootView
    }
}
