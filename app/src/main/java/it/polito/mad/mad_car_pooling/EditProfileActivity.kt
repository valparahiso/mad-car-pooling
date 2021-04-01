package it.polito.mad.mad_car_pooling

import android.app.backup.FullBackupDataOutput
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.ContextMenu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageButton

class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val imageButton = findViewById<ImageButton>(R.id.camera)
        registerForContextMenu(imageButton)

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

    private fun setEditText() {
        val fullName: String? = intent.getStringExtra("group02.lab1.FULL_NAME")
        val nickName: String? = intent.getStringExtra("group02.lab1.NICK_NAME")
        val location: String? = intent.getStringExtra("group02.lab1.LOCATION")
        val email: String? = intent.getStringExtra("group02.lab1.EMAIL")

        val fullNameET = findViewById<EditText>(R.id.edit_fullName)
        fullNameET.setText(fullName)
        val nicknameET = findViewById<EditText>(R.id.edit_nickName)
        nicknameET.setText(nickName)
        val locationET = findViewById<EditText>(R.id.edit_location)
        locationET.setText(location)
        val emailET = findViewById<EditText>(R.id.edit_email)
        emailET.setText(email)
    }
}