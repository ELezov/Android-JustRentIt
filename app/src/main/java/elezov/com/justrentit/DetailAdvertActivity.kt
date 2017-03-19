
package elezov.com.justrentit

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.Toolbar
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.microsoft.windowsazure.mobileservices.MobileServiceClient
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse
import com.microsoft.windowsazure.mobileservices.table.TableDeleteCallback
import com.microsoft.windowsazure.mobileservices.table.TableQueryCallback
import elezov.com.justrentit.model.Advert
import elezov.com.justrentit.model.User
import java.net.MalformedURLException

class DetailAdvertActivity : AppCompatActivity() {

    var imgInfo:ImageView?=null
    var nameText:TextView?=null
    var pricePerDayText: TextView?=null
    var pricePerWeekText: TextView?=null
    var pricePerMonthText: TextView?=null
    var descriptionText: TextView?=null
    var phoneText: TextView?=null
    var ownerText: TextView?=null

    private var progressDialog: ProgressDialog?=null

    lateinit var fab: FloatingActionButton

    var delete: MenuItem?=null

    //private lateinit var callBackAfterDeleteAdvert:BackAfterDeleteAdvertListener

    /*interface BackAfterDeleteAdvertListener{
        fun onBackAfterDeleteAdvert()
    }*/



    private var mobileServiceClient: MobileServiceClient? = null
    private var utils: Utils? = null
    private var advert:Advert?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_advert)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Loading...")
        progressDialog!!.setMessage("Please wait...")
       // progressDialog!!.setCancelable(false)
        progressDialog!!.show()

        //callBackAfterDeleteAdvert=this as BackAfterDeleteAdvertListener

        imgInfo=findViewById(R.id.image_backdrap_info) as ImageView
        nameText=findViewById(R.id.advert_info_name_text) as TextView
        descriptionText = findViewById(R.id.description) as TextView
        pricePerDayText = findViewById(R.id.advert_info_price_per_day) as TextView
        pricePerWeekText = findViewById(R.id.advert_info_price_per_week) as TextView
        pricePerMonthText = findViewById(R.id.advert_info_price_per_month) as TextView
        phoneText = findViewById(R.id.phone_number) as TextView
        ownerText=findViewById(R.id.name_owner) as TextView


        val collapsingToolbar = findViewById(R.id.collapsing_toolbar) as CollapsingToolbarLayout
        collapsingToolbar.title = "  "
        fab = findViewById(R.id.call_fab) as FloatingActionButton


        utils= Utils.getInstance()

        try {
            mobileServiceClient = MobileServiceClient(utils!!.URL_AZURE, this)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            Log.v("MainActivity", "Mailformed FAIL")
        }

        mobileServiceClient!!.getTable(Advert::class.java).where().field("id").eq(Utils.getInstance().currentAdvert)
                .execute { result, count, exception, response ->
                    if (exception == null) {
                        advert=result[0]
                        Log.v("GetAdvertById", result[0].name)
                        //collapsingToolbar.title=result[0].name
                        if (result[0].stringPhoto !== "") {
                            val decodedByte = Base64.decode(result[0].stringPhoto, 0)
                            imgInfo!!.setImageBitmap(BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size))
                        }
                        nameText!!.text=result[0].name
                        descriptionText!!.text=result[0].description
                        pricePerDayText!!.text=result[0].pricePerDay.toString()
                        pricePerWeekText!!.text=result[0].pricePerWeek.toString()
                        pricePerMonthText!!.text=result[0].pricePerMonth.toString()
                        descriptionText!!.text=result[0].description
                        phoneText!!.text=result[0].phoneNumber

                        mobileServiceClient!!.getTable(User::class.java).where().field("mail").eq(result[0].id_user)
                                .execute { users, count, exception, response ->
                                    if (exception == null) {
                                        Log.v("GetUserByMail", users[0].name)
                                        ownerText!!.text=users[0].name
                                       if (result[0].id_user==utils!!.getUser_Mail()){
                                           delete!!.setVisible(true)
                                       }

                                        progressDialog!!.hide()
                                        fab.setOnClickListener(View.OnClickListener {
                                            val callIntent = Intent(Intent.ACTION_CALL)
                                            callIntent.data = Uri.parse("tel:" + phoneText!!.text)
                                            startActivity(callIntent)
                                        })

                                    } else {
                                        Log.v("GetUserByMail", exception.message)
                                        progressDialog!!.hide()
                                    }
                                }

                    } else {
                        progressDialog!!.hide()
                        Log.v("GetAdvertById", exception.message)
                    }
                }



    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId)
        {
            R.id.action_delete->
                mobileServiceClient!!.getTable(Advert::class.java)
                        .delete(advert, TableDeleteCallback {
                            exception, serviceFilterResponse ->
                            if (exception==null){
                                Toast.makeText(applicationContext,"Ваше объявление удалено",Toast.LENGTH_LONG).show()
                                var intent=Intent(this,MainActivity::class.java)
                                intent.putExtra("DeleteFlag",true)
                                startActivity(intent)
                                //callBackAfterDeleteAdvert.onBackAfterDeleteAdvert()
                            }
                            else
                            {
                                Toast.makeText(applicationContext,"Произошла ошибка, повторите попытку позднее",
                                        Toast.LENGTH_LONG).show()
                            }
                        })
            else -> onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        var inflater = menuInflater
                inflater.inflate(R.menu.activity_detail_menu, menu)

        delete= menu!!.findItem(R.id.action_delete)



        return true
    }


}
