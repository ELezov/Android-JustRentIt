package elezov.com.justrentit

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.microsoft.windowsazure.mobileservices.MobileServiceClient
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse
import com.microsoft.windowsazure.mobileservices.table.TableQueryCallback

import elezov.com.justrentit.model.Advert

/**
 * Created by USER on 14.03.2017.
 */

class AdvertListFragment : Fragment() {

    private var mClient: MobileServiceClient? = null
    var rv: RecyclerView?= null
    var progressDialog: ProgressDialog?=null

    internal interface OpenAdvertInfoClickListener {
        fun onOpenAdvertInfo(id: String)
    }

    private var clickCallBack: OpenAdvertInfoClickListener? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.advert_list_layout, container, false)
        progressDialog = ProgressDialog(context)
        progressDialog!!.setTitle("Loading...")
        progressDialog!!.setMessage("Please wait...")
        progressDialog!!.show()
        val utils = Utils.getInstance()
        rv = rootView.findViewById(R.id.advert_recycler) as RecyclerView
        val adapter = AdvertListAdapter()
        rv!!.layoutManager = LinearLayoutManager(context)

        try {
            mClient = MobileServiceClient(utils.urL_AZURE, context)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.v("AdvertListFragment", e.cause!!.message)
        }

        clickCallBack = activity as OpenAdvertInfoClickListener


        mClient!!.getTable(Advert::class.java).where().field("id_category").eq(utils.getListCategory()[utils.current_id].id).execute { result, count, exception, response ->
            if (result != null) {
                Log.v("COUNT", "" + result.size)
                val data = result
                adapter.add(result)
                adapter.notifyDataSetChanged()
                adapter.onButtonClickListener={position->
                    clickCallBack!!.onOpenAdvertInfo(data[position].id)
                }
                rv!!.adapter = adapter
            } else {
                Log.v("COUNT", "fail")
            }
            progressDialog!!.hide()
        }

        return rootView
    }
}
