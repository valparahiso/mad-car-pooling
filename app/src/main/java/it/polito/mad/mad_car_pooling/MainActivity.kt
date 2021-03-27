package it.polito.mad.mad_car_pooling

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_show_profile)

        val intent = Intent(this, ShowProfileActivity::class.java)   //NON SI TOCCA!!!!
        startActivity(intent)
    }

}