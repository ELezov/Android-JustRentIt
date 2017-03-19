
package elezov.com.justrentit

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.microsoft.windowsazure.mobileservices.*
import elezov.com.justrentit.model.Category
import java.net.MalformedURLException


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
        CatalogFragment.OpenAdvertListClickListener, AddAdvertFragment.nextAfterAddAdvertClickListener,
        AdvertListFragment.OpenAdvertInfoClickListener,  SearchView.OnQueryTextListener{
    override fun onQueryTextSubmit(query: String?): Boolean {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    lateinit var currFrgmClass: Class<*>
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mClient: MobileServiceClient? = null
    lateinit var fab: FloatingActionButton
    lateinit var progressDialog: ProgressDialog
    lateinit var alertDialog: AlertDialog.Builder
    var utils=Utils.getInstance()


    override fun onOpenAdvertList(position:Int) {
        var utils=Utils.getInstance()
        utils.current_id=position
        FragmentManager(AdvertListFragment::class.java,true)
    }



    override fun nextAfterAddAdvert(){
        if (currFrgmClass==AddAdvertFragment::class.java)
            fab.visibility=View.VISIBLE
       FragmentManager(CatalogFragment::class.java,false)
    }

    override fun onOpenAdvertInfo(id: String) {
        Utils.getInstance().currentAdvert=id
        var intent=Intent(applicationContext, DetailAdvertActivity::class.java)
        startActivity(intent);
        //FragmentManager(AdvertInfoFragment::class.java,true)

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        //Check permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.CALL_PHONE),
                    1)
        } else {
            Log.e("DB", "PERMISSION GRANTED")
        }

        alertDialog=AlertDialog.Builder(this)
        alertDialog.setCancelable(false)
        alertDialog.setMessage("Вы уверены, что хотите выйте из приложения")

        alertDialog.setNegativeButton("Отменить",DialogInterface.OnClickListener {
            dialogInterface, i ->

        })
        alertDialog.setPositiveButton("Выйти", DialogInterface.OnClickListener {
            dialogInterface, i ->
            finishAffinity()
        })


        progressDialog= ProgressDialog(this)
        progressDialog.setTitle("Loading...")
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        utils.setUser_Mail(utils.getUserMail(this))
        utils.setUser_Name(utils.getUserName(this))


        fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
                FragmentManager(AddAdvertFragment::class.java,true)
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)




        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */
                // methods go here
                ) { connectionResult -> Log.d("LOG", "onConnectionFailed:" + connectionResult) }
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        try {
            mClient = MobileServiceClient(utils.URL_AZURE, this)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            Log.v("MainActivity", "Mailformed FAIL")
        }


        mClient!!.getTable(Category::class.java).execute { result, count, exception, response ->
            if (exception==null) {
                Log.v("URA", "" + result!!.size)
                utils.setListCategory(result)
            }
            else {

            }
            progressDialog.hide()

            var flag=intent!!.getBooleanExtra("DeleteFlag",false)
            if (flag)
                {
                    FragmentManager(MyAdvertsFragment::class.java,false)
                }
            else{
                    FragmentManager(CatalogFragment::class.java,false)
                }

        }


    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            if (currFrgmClass==AddAdvertFragment::class.java)
                fab.visibility=View.VISIBLE
            if (supportFragmentManager.backStackEntryCount>0){
                super.onBackPressed()
            }
            else {

                alertDialog.show()

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.logout) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback { }
            //fb
            LoginManager.getInstance().logOut()
            //vk
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }


        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.nav_catalog) {
            if (currFrgmClass==AddAdvertFragment::class.java)
                fab.visibility=View.VISIBLE
            FragmentManager(CatalogFragment::class.java,false)

            // Handle the camera action
        } else if (id == R.id.nav_my_advert) {
            if (currFrgmClass == AddAdvertFragment::class.java)
                fab.visibility = View.VISIBLE
            FragmentManager(MyAdvertsFragment::class.java, false)

//        } else if (id == R.id.nav_favorites) {
//            if (currFrgmClass==AddAdvertFragment::class.java)
//                fab.visibility=View.VISIBLE
//            FragmentManager(FavoriteAdvertsFragment::class.java,false)
//        } else if (id == R.id.nav_manage) {
//
//
//        } else if (id == R.id.nav_share) {

        } else if (id == R.id.exit) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback { }
            //fb
            LoginManager.getInstance().logOut()
            //vk
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    fun FragmentManager(fragmClass: Class<*>,flag:Boolean) {
        currFrgmClass=fragmClass
        utils.setCurrFrgmClass(currFrgmClass)
        /*if (currFrgmClass==MyAdvertsFragment::class.java)
        {
            supportActionBar!!.title="Ваши объявления"
        }
        else{
            supportActionBar!!.title="JustRentIt"
        }*/
        var fragment: Fragment? = null
        val fragmentClass = fragmClass
        try {
                fragment = fragmentClass.newInstance() as Fragment
            }
        catch (e: Exception) { }
        if (currFrgmClass==AddAdvertFragment::class.java)
            fab.visibility=View.INVISIBLE
        var tr=supportFragmentManager.beginTransaction()
        tr.replace(R.id.fragment_container,fragment)
        if (flag==true)
            tr.addToBackStack(null)
        tr.commit()
    }



}
