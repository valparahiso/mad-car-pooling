package it.polito.mad.mad_car_pooling

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView

class ShowProfileActivity : AppCompatActivity() {
    private var fullName: String = "Mario Rossi"
    private var nickName: String = "mariorossi"
    private var location: String = "ovunque"
    private var emain: String = "mario.rossi@polito.it"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)
        findViewById<TextView>(R.id.fullName).text =  fullName
        findViewById<TextView>(R.id.nickname).text =  nickName
        findViewById<TextView>(R.id.email).text =  emain
        findViewById<TextView>(R.id.location).text =  location
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.file_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Log.d("POLITOMAD","onOptionsItemSelected()")
        editProfile()
        return super.onOptionsItemSelected(item)
    }

    private fun editProfile() {
        val intent = Intent(this, EditProfileActivity::class.java)
        intent.putExtra("group02.lab1.FULL_NAME", fullName)
        intent.putExtra("group02.lab1.NICK_NAME", nickName)
        intent.putExtra("group02.lab1.EMAIL", emain)
        intent.putExtra("group02.lab1.LOCATION", location)
        startActivityForResult(intent, 1)
    }
}