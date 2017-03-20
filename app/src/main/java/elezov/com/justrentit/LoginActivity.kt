
package elezov.com.justrentit

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.microsoft.windowsazure.mobileservices.MobileServiceClient
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback
import com.microsoft.windowsazure.mobileservices.table.TableQueryCallback
import elezov.com.justrentit.model.User
import java.net.MalformedURLException

class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener, View.OnClickListener  {

    companion object{
        public val USER_MAIL="user_mail"
        public val USER_NAME="user_name"
        public val USER_ID="user_id"
        public val SHARED_KEY="shared_key"
    }

    private var mProgressDialog: ProgressDialog? = null
    private val TAG = "SignInActivity"
    var utils:Utils= Utils.getInstance()

    //Facebook
    var loginButton: LoginButton? = null
    var callbackManager: CallbackManager? = null
    private var graphRequest:GraphRequest? = null

    //Google
    private val RC_SIGN_IN = 9001
    private var mGoogleApiClient: GoogleApiClient? = null
    //Azure
    private var mClient: MobileServiceClient? = null



    override fun onConnectionFailed(p0: ConnectionResult) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Google
        findViewById(R.id.signIn).setOnClickListener(this)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
        val signInButton = findViewById(R.id.signIn) as SignInButton
        signInButton.setSize(SignInButton.SIZE_STANDARD)

        //Facebook
        callbackManager = CallbackManager.Factory.create()
        loginButton = findViewById(R.id.fb_login_button) as LoginButton
        loginButton!!.setReadPermissions("email")

        //Azure
        try
        {
            mClient = MobileServiceClient(utils.URL_AZURE, this)
        }
        catch (e: MalformedURLException)
        {
            e.printStackTrace()
        }

        if (AccessToken.getCurrentAccessToken() == null)
            loginButton!!.registerCallback(callbackManager, object : FacebookCallback<LoginResult>
        {
            override fun onSuccess(loginResult: LoginResult)
            {
                showProgressDialog()
                var mail=""
                var name=""
                graphRequest=GraphRequest.newMeRequest(loginResult.accessToken,
                        GraphRequest.GraphJSONObjectCallback {
                            jsonObject, graphResponse ->
                            try{
                                mail=jsonObject.getString("email")
                                name=jsonObject.getString("name")
                                utils.setUserInfoPreferences(name,mail,this@LoginActivity)
                                CheckAzureUserByMail(name,mail)
                            }catch (e:Exception){
                                Log.v("Fail",jsonObject.toString())
                            }
                        })
                val parameters = Bundle()
                parameters.putString("fields", "name,email")
                graphRequest!!.parameters=parameters
                graphRequest!!.executeAsync()
            }

            override fun onCancel() {
                // Toast.makeText(getApplicationContext(), R.string.cancel_login, Toast.LENGTH_SHORT).show();
            }

            override fun onError(error: FacebookException) {
                //  Toast.makeText(getApplicationContext(), R.string.error_login, Toast.LENGTH_SHORT).show();
                if (!isOnline()) {
                    Toast.makeText(applicationContext,
                            "Нет соединения с интернетом!", Toast.LENGTH_LONG).show()
                }

            }
        })
        else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }



    }

    public override fun onStart() {
        super.onStart()
        val opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient)
        if (opr.isDone) {
            Log.d(TAG, "Got cached sign-in")
            val result = opr.get()
            handleSignInResult(result)
        } else {
            showProgressDialog()
            opr.setResultCallback { googleSignInResult ->
                hideProgressDialog()
                handleSignInResult(googleSignInResult)
            }
        }
    }

    private fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this)
            mProgressDialog!!.setTitle("Loading...")
            mProgressDialog!!.setMessage("Please wait...")
            mProgressDialog!!.setCancelable(false)

            mProgressDialog!!.setIndeterminate(true)
        }

        mProgressDialog!!.show()
    }


    private fun hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing()) {
            mProgressDialog!!.hide()
        }
    }

    public fun isOnline(): Boolean
    {
        var cs = Context.CONNECTIVITY_SERVICE
        var cm = getSystemService(cs) as ConnectivityManager
        if (cm.activeNetworkInfo == null) {
            return false
        }
        else
        {
            return true
        }
    }

    private fun signIn()
    {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result)
        }

        callbackManager!!.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        //Log.d("Log", "handleSignInResult:" + result.isSuccess)
        if (result.isSuccess) {
            showProgressDialog()
            val acct = result.signInAccount

            var mail=acct!!.email
            var name=acct!!.displayName
            utils.setUserInfoPreferences(name,mail,this)
            Log.v(utils.getUserMail(this),utils.getUserName(this))
            CheckAzureUserByMail(name!!,mail!!)
        }
    }

    override fun onClick(v: View?)
    {
        if (!isOnline()) {
            Toast.makeText(applicationContext,
                    "Нет соединения с интернетом!", Toast.LENGTH_LONG).show()
            return
        } else {
            when (v!!.getId()) {
                R.id.signIn -> signIn()
            }
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }

    public fun CheckAzureUserByMail(name:String,mail:String)
    {
        mClient!!.getTable(User::class.java).where().field("mail").eq(mail).execute(TableQueryCallback {
            mutableList, i, exception, serviceFilterResponse ->
            if (exception==null)
            {
                if (mutableList.size==0)
                {
                    var user=User()
                    user.mail=mail
                    user.name=name
                    InsertUserToAzure(user)
                }
                else
                {
                    Log.v("AZURE","INSERT This user exits yet")
                    mClient!!.getTable(User::class.java).execute(TableQueryCallback {
                        result, i, exception, serviceFilterResponse ->
                        for (i in result!!.indices)
                            Log.v("AZURE",result!![i].name+" " + result!![i].mail + " "+result!![i].id)
                        hideProgressDialog()
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                    })
                }
            }
            else
            {

                hideProgressDialog()
                Log.v("AZURE",exception.message)
                Toast.makeText(applicationContext,"Повторите попытку позже",Toast.LENGTH_LONG).show()
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback { }
                //fb
                LoginManager.getInstance().logOut()
            }

        })
    }

    public fun InsertUserToAzure(user:User){
        mClient!!.getTable(User::class.java).insert(user, TableOperationCallback { entity, exception, serviceFilterResponse ->
            if (exception == null) {
                Log.v("AZURE","INSERT  "+ entity.name)
                hideProgressDialog()
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            } else {
                hideProgressDialog()
                Toast.makeText(applicationContext, "Произошла ошибка, свяжитесь со службой поддержки", Toast.LENGTH_SHORT).show()
                Log.v("AZURE", "INSERT false")
            }
        })
    }

}
