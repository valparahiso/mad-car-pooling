package it.polito.mad.mad_car_pooling

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity


class EditProfileActivity : AppCompatActivity() {

    private lateinit var fullNameET: EditText
    private lateinit var nicknameET: EditText
    private lateinit var emailET: EditText
    private lateinit var locationET: EditText
    private lateinit var photoIV: ImageView
    private lateinit var imageBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val imageButton = findViewById<ImageButton>(R.id.camera)
        registerForContextMenu(imageButton)

        fullNameET = findViewById<EditText>(R.id.edit_fullName)
        nicknameET = findViewById<EditText>(R.id.edit_nickName)
        locationET = findViewById<EditText>(R.id.edit_location)
        emailET = findViewById<EditText>(R.id.edit_email)
        photoIV = findViewById(R.id.edit_photo)

        imageBitmap  = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888) // temp initialization

        setEditText()
    }

    //save the state in order to restore it on recreation of the activity
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable("BitmapImage", imageBitmap)
    }

    //restore the photo after the destruction and the creation of the activity (change of orientation of the device)
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        photoIV.setImageBitmap(savedInstanceState.getParcelable("BitmapImage"))
        imageBitmap = savedInstanceState.getParcelable("BitmapImage")!!
    }

    //permits to create the floating context menu
    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_context_photo, menu)
    }

    //behaviour of item in the floating context menu
    override fun onContextItemSelected(item: MenuItem): Boolean {
        //val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        return when (item.itemId) {
            R.id.gallery -> {
                //Log.d("POLITOMAD", "Gallery")
                openGallery()
                true
            }
            R.id.camera -> {
                //Log.d("POLITOMAD","Photo")
                dispatchTakePictureIntent()   //open camera
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    //function to open the camera
    val REQUEST_IMAGE_CAPTURE = 1
    val PICK_IMAGE = 2
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
            Log.d("POLITOMAD", "ActivityNotFoundException")
        }
    }

    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(gallery, PICK_IMAGE)
    }

    //permits to recieve the photo from the camera
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == RESULT_OK) {
                    imageBitmap = data?.extras?.get("data") as Bitmap
                    photoIV.setImageBitmap(imageBitmap)
                }
            }
            PICK_IMAGE -> {
                if (resultCode == RESULT_OK) {
                    try {
                        val imageUri = data?.data
                        val source: ImageDecoder.Source = ImageDecoder.createSource(this.contentResolver, imageUri!!)
                        imageBitmap = ImageDecoder.decodeBitmap(source)
                        photoIV.setImageBitmap(imageBitmap)
                    } catch (e: kotlin.TypeCastException) {
                        Log.d("POLITOMAD", "TypeCastException")
                    }
                }
            }
        }

    }

    //option menu for saving
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_option_save, menu)
        return true
    }

    //items of save option menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                saveContent()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //retrive data from intent of ShowActivityProfile
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

    //set result for ShowProfileAcitivty (check if all EditText contain some characters)
    private fun saveContent() {
        var flag = true

        if(TextUtils.isEmpty(fullNameET.text.toString())) {
            fullNameET.setError("Full name is required!")
            flag = false
        }
        if (TextUtils.isEmpty(nicknameET.text.toString())) {
            nicknameET.setError("Nick name is required!")
            flag = false
        }
        if (TextUtils.isEmpty(emailET.text.toString())) {
            emailET.setError("Email is required!")
            flag = false
        }
        if (TextUtils.isEmpty(locationET.text.toString())) {
            locationET.setError("Location is required!")
            flag = false
        }

        if(flag) {
            setResult(Activity.RESULT_OK, Intent().also {
                it.putExtra("group02.lab1.FULL_NAME", fullNameET.text.toString())
                it.putExtra("group02.lab1.NICK_NAME", nicknameET.text.toString())
                it.putExtra("group02.lab1.EMAIL", emailET.text.toString())
                it.putExtra("group02.lab1.LOCATION", locationET.text.toString())
            })
            finish()
        }
    }
}