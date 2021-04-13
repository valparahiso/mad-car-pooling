package it.polito.mad.mad_car_pooling

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.graphics.BitmapFactory
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
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.theartofdev.edmodo.cropper.CropImage
import org.joda.time.DateTime
import java.io.File
import java.util.*


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

    private var imageTempModified: Boolean = false
    private lateinit var imageTemp: String
    private lateinit var imagePath: String

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

        birthET.setOnFocusChangeListener { _, hasFocus -> run {
            if(hasFocus)
                openCalendarDialog(year, month, day)
        } }

        imagePath = intent.getStringExtra("group02.lab1.IMAGE_PATH").toString()   //get path of profile picture
        imageTemp = externalCacheDir.toString() + "/tmp.png"
        setEditText()

        //load photo and save status bitmap
        loadImage(photoIV, imagePath)
    }

    //open Calendar Dialog for Birth Date and remove focus form the EditText
    @RequiresApi(Build.VERSION_CODES.O)
    private fun openCalendarDialog(year: Int, month: Int, day: Int){
        birthET.inputType = InputType.TYPE_NULL;
        val listener = OnDateSetListener { _, dayOfMonth, monthOfYear, Year-> birthET.setText("${dayOfMonth}/${monthOfYear}/${Year}") }
        val dpDialog = DatePickerDialog(this, listener, year, month, day)
        dpDialog.datePicker.maxDate = DateTime().minusYears(18).millis    //set the maximum date (at least 18 years old)
        dpDialog.show()
        birthET.clearFocus()
    }

    //save the state in order to restore it on recreation of the activity
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("imageTempModified", imageTempModified)
    }

    //restore the photo after the destruction and the creation of the activity (change of orientation of the device)
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        imageTempModified = savedInstanceState.getBoolean("imageTempModified")
        if (imageTempModified){
            photoIV.setImageResource(R.drawable.user_image)
            photoIV.setImageURI(imageTemp.toUri())
        } else {
            loadImage(photoIV, imagePath)
        }

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

    //create an intent for the gallery activity
    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(gallery, PICK_IMAGE)
    }
    //function to open the camera
    @RequiresApi(Build.VERSION_CODES.N)
    private fun dispatchTakePictureIntent() {
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        val photoUri = FileProvider.getUriForFile(
            this,
            "$packageName.provider",
            File(imageTemp)
        )

        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        startActivityForResult(takePhotoIntent, REQUEST_IMAGE_CAPTURE)
    }

    //permits to receive the photo from the camera or gallery
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            //return from camera
            REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == RESULT_OK) {
                    try {
                        var file = File(imageTemp)
                        if(file.exists()) {
                            CropImage.activity(file.toUri())
                                .setAspectRatio(1,1)
                                .start(this);
                        }
                    } catch (e: kotlin.TypeCastException) {
                        Log.e("POLITOMAD", "Camera Exception")
                    }
                }
            }
            //return from gallery
            PICK_IMAGE -> {
                if (resultCode == RESULT_OK) {
                    try {
                        val imageUri = data?.data
                        CropImage.activity(imageUri)
                                .setAspectRatio(1,1)
                                .start(this);
                    } catch (e: kotlin.TypeCastException) {
                        Log.e("POLITOMAD", "Gallery Exception")
                    }
                }
            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {
                    try {
                        File(CropImage.getActivityResult(data).uri.path).copyTo(
                            File(imageTemp),
                            overwrite = true
                        )
                        imageTempModified = true
                        photoIV.setImageResource(R.drawable.user_image)
                        photoIV.setImageURI(imageTemp.toUri())
                    } catch (e: kotlin.TypeCastException) {
                        Log.e("POLITOMAD", "Crop Exception")
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
            R.id.clear -> {
                clearFields()
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

                //check if photo is changed -> save photo and delete tmp cached file
                if (imageTempModified) {
                    Log.d("POLIMAD", "New photo saved in: $imagePath")
                    val tmpFile = File(imageTemp)
                    tmpFile.copyTo(File(imagePath), overwrite = true)
                    tmpFile.delete()
                }
            })
            finish()
        }
    }

    //function to reset textEdit
    private fun clearFields(){
        fullNameET.setText("");
        nicknameET.setText("");
        emailET.setText("");
        locationET.setText("");
        birthET.setText("");

    }

    //function to load the picture if exist (icon default)
    private fun loadImage(image:ImageView, path:String){
        var file = File(path)
        if(file.exists()) {
            image.setImageResource(R.drawable.user_image)
            image.setImageURI(path.toUri())
        }else{
            val options = BitmapFactory.Options()
            options.inScaled = false;
            image.setImageResource(R.drawable.user_image)
        }
    }

}