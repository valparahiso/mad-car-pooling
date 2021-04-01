package it.polito.mad.mad_car_pooling

import android.app.Activity
import android.app.backup.FullBackupDataOutput
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageButton

class EditProfileActivity : AppCompatActivity() {

    private lateinit var fullNameET: EditText
    private lateinit var nicknameET: EditText
    private lateinit var emailET: EditText
    private lateinit var locationET: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val imageButton = findViewById<ImageButton>(R.id.camera)
        registerForContextMenu(imageButton)

        fullNameET = findViewById<EditText>(R.id.edit_fullName)
        nicknameET = findViewById<EditText>(R.id.edit_nickName)
        locationET = findViewById<EditText>(R.id.edit_location)
        emailET = findViewById<EditText>(R.id.edit_email)

        setEditText()

    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_context_photo, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        //val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        return when (item.itemId) {
            R.id.gallery -> {
                Log.d("POLITOMAD","Gallery")
                true
            }
            R.id.camera -> {
                Log.d("POLITOMAD","Photo")
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_option_save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                saveContent()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setEditText() {
        val fullName: String? = intent.getStringExtra("group02.lab1.FULL_NAME")
        val nickName: String? = intent.getStringExtra("group02.lab1.NICK_NAME")
        val location: String? = intent.getStringExtra("group02.lab1.LOCATION")
        val email: String? = intent.getStringExtra("group02.lab1.EMAIL")

        fullNameET.setText(fullName)
        nicknameET.setText(nickName)
        locationET.setText(location)
        emailET.setText(email)
    }

    private fun saveContent() {
        setResult(Activity.RESULT_OK, Intent().also{
            it.putExtra("group02.lab1.FULL_NAME", fullNameET.text.toString())
            it.putExtra("group02.lab1.NICK_NAME", nicknameET.text.toString())
            it.putExtra("group02.lab1.EMAIL", emailET.text.toString())
            it.putExtra("group02.lab1.LOCATION", locationET.text.toString())
        })
        finish()
    }
}