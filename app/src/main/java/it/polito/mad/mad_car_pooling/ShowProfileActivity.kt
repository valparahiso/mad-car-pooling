package it.polito.mad.mad_car_pooling

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import org.w3c.dom.Text

class ShowProfileActivity : AppCompatActivity() {
    private lateinit var fullName: TextView
    private lateinit var nickName: TextView
    private lateinit var location: TextView
    private lateinit var emain: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)
        fullName = findViewById<TextView>(R.id.fullName)
        nickName = findViewById(R.id.nickname)
        emain = findViewById(R.id.email)
        location = findViewById(R.id.location)

        fullName.text =  "Mario Rossi"
        nickName.text =  "mariored89"
        emain.text =  "mario.rossi@polito.it"
        location.text =  "Lombardia"
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
        intent.putExtra("group02.lab1.FULL_NAME", fullName.text)
        intent.putExtra("group02.lab1.NICK_NAME", nickName.text)
        intent.putExtra("group02.lab1.EMAIL", emain.text)
        intent.putExtra("group02.lab1.LOCATION", location.text)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            //Log.d("POLITOMAD", data?.getStringExtra("group02.lab1.FULL_NAME").toString())
            showProfile(data)
        }
    }

    private fun showProfile(data: Intent?) {
        fullName.text = data?.getStringExtra("group02.lab1.FULL_NAME")
        nickName.text = data?.getStringExtra("group02.lab1.NICK_NAME")
        emain.text = data?.getStringExtra("group02.lab1.EMAIL")
        location.text = data?.getStringExtra("group02.lab1.LOCATION")
    }
}