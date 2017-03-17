package elezov.com.justrentit

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import java.util.*
import kotlin.concurrent.timerTask

class StartActivity : AppCompatActivity() {

    var time=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        if (time==0)
        {
            var timer= Timer()
            timer.schedule(timerTask {
                time++
                if (time==10) {
                    var intent = Intent(applicationContext,LoginActivity::class.java)
                    startActivity(intent)
                    timer.cancel()

                }
            }, 1100,500)
        }
    }
}
