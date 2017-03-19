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
import android.widget.Toast
import com.microsoft.windowsazure.mobileservices.MobileServiceClient
import com.microsoft.windowsazure.mobileservices.table.TableQueryCallback
import elezov.com.justrentit.model.Advert

/**
 * Created by USER on 12.03.2017.
 */

class MyAdvertsFragment : Fragment() {


    internal var rv: RecyclerView? = null
    internal var adapter: AdvertListAdapter? = null
    private var mClient: MobileServiceClient? = null
    var progressDialog: ProgressDialog?=null

    lateinit var utils:Utils

    private var clickCallBack: AdvertListFragment.OpenAdvertInfoClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.my_adverts_fragment_layout, container, false)

        progressDialog = ProgressDialog(context)
        progressDialog!!.setTitle("Loading...")
        progressDialog!!.setMessage("Please wait...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()

        rv=rootView.findViewById(R.id.my_adverts_list) as RecyclerView

        rv!!.layoutManager=LinearLayoutManager(context)
        adapter= AdvertListAdapter()

        utils= Utils.getInstance()

        try {
            mClient = MobileServiceClient(utils.urL_AZURE, context)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.v("MyAdvertsFragment", e.cause!!.message)
        }

        clickCallBack = activity as AdvertListFragment.OpenAdvertInfoClickListener

        mClient!!.getTable(Advert::class.java).where().field("id_user")
                .eq(utils.user_Mail).execute(TableQueryCallback
        {
            mutableList, i, exception, serviceFilterResponse ->
            if (exception==null){
                Log.v("GetMyAdverts","Count "+mutableList.size)
                var data=mutableList
                adapter!!.add(data)
                adapter!!.notifyDataSetChanged()
                adapter!!.onButtonClickListener={position->
                    clickCallBack!!.onOpenAdvertInfo(
                            data[position].id)
                }
                rv!!.adapter=adapter
                progressDialog!!.hide()

            }
            else
            {
                progressDialog!!.hide()
                Toast.makeText(context, "Произошла ошибка, свяжитесь со службой поддержки", Toast.LENGTH_SHORT).show()
                Log.v("GetMyAdverts",exception.message)
            }
        })

        return rootView
    }
}
