package it.polito.mad.mad_car_pooling

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker
import java.io.File
import java.util.*
import org.joda.time.DateTime


class EditProfileActivity : AppCompatActivity() {
    //code of request (camera or gallery)
    val REQUEST_IMAGE_CAPTURE = 1       //camera
    val PICK_IMAGE = 2                  //gallery

    private lateinit var fullNameET: EditText
    private lateinit var nicknameET: EditText
    private lateinit var emailET: EditText
    private lateinit var locationET: EditText
    private lateinit var photoIV: ImageView
    private lateinit var birthET: EditText

    private lateinit var statusBitmap: Bitmap
    private lateinit var imagePath: String
    private var flagPhotoModified: Boolean = false

    @RequiresApi(Build.VERSION_CODES.O)
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
        birthET = findViewById(R.id.edit_birthDate)

        val mcalendar: Calendar = Calendar.getInstance()

        var day = mcalendar.get(Calendar.DAY_OF_MONTH);
        var year = mcalendar.get(Calendar.YEAR);
        var month = mcalendar.get(Calendar.MONTH);
        var minYear = year - 18
        var minMonth = month
        var minDay = day

        birthET.setOnFocusChangeListener { v, hasFocus -> run {
            if(hasFocus)
                openCalendarDialog(year, month, day)
        } }

        imagePath = intent.getStringExtra("group02.lab1.IMAGE_PATH").toString()   //get path of profile picture
        setEditText()

        //load photo and save status bitmap
        var file = File(imagePath)
        if(file.exists()) {
            statusBitmap = BitmapFactory.decodeFile(imagePath);
            photoIV.setImageResource(R.drawable.user_image)
            photoIV.setImageURI(file.toUri())
        }else{
            val options = BitmapFactory.Options()
            options.inSampleSize = 2
            statusBitmap = BitmapFactory.decodeResource(resources, R.drawable.user_image, options)
            photoIV.setImageResource(R.drawable.user_image)
        }
    }

    //open Calendar Dialog for Birth Date and remove focus form the EditText
    @RequiresApi(Build.VERSION_CODES.O)
    private fun openCalendarDialog(year: Int, month: Int, day: Int){
        birthET.setInputType(InputType.TYPE_NULL);
        val listener = OnDateSetListener { view, year, monthOfYear, dayOfMonth -> birthET.setText("" + dayOfMonth + "/" + monthOfYear + "/" + year + "") }
        val dpDialog = DatePickerDialog(this, listener, year, month, day)
        dpDialog.datePicker.maxDate = DateTime().minusYears(18).millis    //set the maximum date (at least 18 years old)
        dpDialog.show()
        birthET.clearFocus()
    }

    //save the state in order to restore it on recreation of the activity
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("BitmapImage", statusBitmap)
        outState.putBoolean("BitmapModified", flagPhotoModified)
    }

    //restore the photo after the destruction and the creation of the activity (change of orientation of the device)
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        flagPhotoModified =savedInstanceState.getBoolean("BitmapModified")
        photoIV.setImageBitmap(savedInstanceState.getParcelable("BitmapImage"))
        statusBitmap = savedInstanceState.getParcelable("BitmapImage")!!

    }

    //permits to create the floating context menu
    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_context_photo, menu)
    }

    //behaviour of item in the floating context menu
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.gallery -> {
                openGallery()
                true
            }
            R.id.camera -> {
                dispatchTakePictureIntent()   //open camera
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    //function to open the camera
    @RequiresApi(Build.VERSION_CODES.N)
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
            Log.e("POLITOMAD", "ActivityNotFoundException - Camera")
        }

    }

    //create an intent for the gallery activity
    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        intent.type = "image/*"
        try {
            startActivityForResult(gallery, PICK_IMAGE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
            Log.e("POLITOMAD", "ActivityNotFoundException - Gallery")
        }
    }

    //permits to receive the photo from the camera or gallery
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        flagPhotoModified = true
        when (requestCode) {
            //return from camera
            REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == RESULT_OK) {
                    try {
                        statusBitmap = data?.extras?.get("data") as Bitmap
                        photoIV.setImageBitmap(statusBitmap)
                    } catch (e: kotlin.TypeCastException) {
                        Log.e("POLITOMAD", "TypeCastException - Camera")
                    }
                }
            }
            //return from gallery
            PICK_IMAGE -> {
                if (resultCode == RESULT_OK) {
                    try {
                        val imageUri = data?.data
                        val source: ImageDecoder.Source = ImageDecoder.createSource(this.contentResolver, imageUri!!)
                        statusBitmap = ImageDecoder.decodeBitmap(source)
                        photoIV.setImageBitmap(statusBitmap)
                    } catch (e: kotlin.TypeCastException) {
                        Log.e("POLITOMAD", "TypeCastException - Gallery")
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
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                saveContent()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //retrieve data from intent of ShowActivityProfile
    private fun setEditText() {
        val fullName: String? = intent.getStringExtra("group02.lab1.FULL_NAME")
        val nickName: String? = intent.getStringExtra("group02.lab1.NICK_NAME")
        val location: String? = intent.getStringExtra("group02.lab1.LOCATION")
        val email: String? = intent.getStringExtra("group02.lab1.EMAIL")
        val birthDate: String? = intent.getStringExtra("group02.lab1.BIRTH")

        fullNameET.setText(fullName)
        nicknameET.setText(nickName)
        locationET.setText(location)
        emailET.setText(email)
        birthET.setText(birthDate)
    }

    //set result for ShowProfileActivity (check if all EditText contain some characters)
    @RequiresApi(Build.VERSION_CODES.N)
    private fun saveContent() {
        var flagPresentValue = true

        if(TextUtils.isEmpty(fullNameET.text.toString())) {
            fullNameET.error = "Full name is required!"
            flagPresentValue = false
        }
        if (TextUtils.isEmpty(nicknameET.text.toString())) {
            nicknameET.error = "Nick name is required!"
            flagPresentValue = false
        }
        if (TextUtils.isEmpty(emailET.text.toString())) {
            emailET.error = "Email is required!"
            flagPresentValue = false
        }
        if (TextUtils.isEmpty(locationET.text.toString())) {
            locationET.error = "Location is required!"
            flagPresentValue = false
        }

        if (TextUtils.isEmpty(birthET.text.toString())) {
            birthET.error = "Date of birth is required!"
            flagPresentValue = false
        }

        if(flagPresentValue) {
            setResult(Activity.RESULT_OK, Intent().also {
                it.putExtra("group02.lab1.FULL_NAME", fullNameET.text.toString())
                it.putExtra("group02.lab1.NICK_NAME", nicknameET.text.toString())
                it.putExtra("group02.lab1.EMAIL", emailET.text.toString())
                it.putExtra("group02.lab1.LOCATION", locationET.text.toString())
                it.putExtra("group02.lab1.BIRTH", birthET.text.toString())

                //check if photo is changed
                if (flagPhotoModified) {
                    Log.d("POLIMAD", "New photo saved in: $imagePath")
                    File(imagePath).writeBitmap(statusBitmap, Bitmap.CompressFormat.PNG, 100)
                }
            })
            finish()
        }
    }

    //save the bitmap (photo) on file
    private fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
        outputStream().use { out ->
            bitmap.compress(format, quality, out)
            out.flush()
        }
    }

}