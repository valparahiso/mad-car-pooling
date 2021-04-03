package it.polito.mad.mad_car_pooling

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.InputStream


class ShowProfileActivity : AppCompatActivity() {
    private lateinit var fullName: TextView
    private lateinit var nickName: TextView
    private lateinit var location: TextView
    private lateinit var email: TextView
    private lateinit var photo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)
        fullName = findViewById<TextView>(R.id.fullName)
        nickName = findViewById(R.id.nickname)
        email = findViewById(R.id.email)
        location = findViewById(R.id.location)
        photo = findViewById(R.id.photo)

        fullName.text =  "Mario Rossi"
        nickName.text =  "mariored89"
        email.text =  "mario.rossi@polito.it"
        location.text =  "Lombardia"
        /*var uri: Uri = Uri.parse("android.resource://${getApplicationContext().getPackageName()}/drawable/download.png")
        val stream: InputStream? = contentResolver.openInputStream(uri)
        val myBitmap = BitmapFactory.decodeStream(stream)
        photo.setImageBitmap(myBitmap)*/
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("fullname", fullName.text.toString())
        outState.putString("nickname", nickName.text.toString())
        outState.putString("email", email.text.toString())
        outState.putString("location", location.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        fullName.text = savedInstanceState.getString("fullname")
        nickName.text = savedInstanceState.getString("nickname")
        email.text = savedInstanceState.getString("email")
        location.text = savedInstanceState.getString("location")
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
        intent.putExtra("group02.lab1.EMAIL", email.text)
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
        email.text = data?.getStringExtra("group02.lab1.EMAIL")
        location.text = data?.getStringExtra("group02.lab1.LOCATION")
    }
}