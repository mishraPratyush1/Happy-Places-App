package com.example.happyplaces


import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.support.v4.os.IResultReceiver._Parcel
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOError
import java.io.IOException
import java.io.OutputStream
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {
    private var viewBinding : ActivityAddHappyPlaceBinding? = null
    private var calender = Calendar.getInstance()
    private lateinit var dateSetListener: OnDateSetListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(viewBinding?.root)

        setSupportActionBar(viewBinding?.toolbarAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        viewBinding?.toolbarAddPlace?.setNavigationOnClickListener{
            onBackPressed()
        }

        dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calender.set(Calendar.YEAR,year)
            calender.set(Calendar.MONTH,month)
            calender.set(Calendar.DAY_OF_MONTH,dayOfMonth)

            UpdateDateInView()
        }
        viewBinding?.etDate?.setOnClickListener(this)
        viewBinding?.tvAddImage?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.et_date -> {
                DatePickerDialog(
                    this@AddHappyPlaceActivity,
                    dateSetListener,
                    calender.get(Calendar.YEAR),calender.get(Calendar.MONTH),calender.get(Calendar.DAY_OF_MONTH
                    )).show()
            }
            R.id.tv_add_image -> {
                val pictureDialog = AlertDialog.Builder(
                    this)
                pictureDialog.setTitle("Select Action")
                val pictureDIalogItems = arrayOf("Select Photos From Gallery","capture Photos From camera"
                )

                pictureDialog.setItems(pictureDIalogItems){
                        _, which ->
                    when(which){
                        0-> choosePhotosFromGallery()
                        1->takePhotosFromCamera()
                    }
                }
                pictureDialog.show()
            }
        }
    }
    private fun takePhotosFromCamera(){
        Dexter.withActivity(this).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
        ).withListener(object:MultiplePermissionsListener{
            override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                if(p0!!.areAllPermissionsGranted()){
                    val galleryIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(galleryIntent,CAMERA)
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: MutableList<PermissionRequest>?,
                p1: PermissionToken?
            ) {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check()
    }
    private fun choosePhotosFromGallery(){
        Dexter.withActivity(this).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object:MultiplePermissionsListener{
            override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                if(p0!!.areAllPermissionsGranted()){
                    val galleryIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent,GALLERY)
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: MutableList<PermissionRequest>?,
                p1: PermissionToken?
            ) {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if (requestCode == GALLERY){
                    if(data != null){
                        var contentURI = data.data
                        try {
                            val selectedImageBitMap = MediaStore.Images.Media.getBitmap(this.contentResolver,contentURI)
                            val saveImageToInternalStorage = saveImageToInternalStorage(selectedImageBitMap)
                            Log.e("", "Path : $saveImageToInternalStorage")
                            viewBinding?.ivPlaceImage!!.setImageBitmap(selectedImageBitMap)
                        }catch (e : IOException){
                            e.printStackTrace()
                            Toast.makeText(this@AddHappyPlaceActivity,"Failed to load image from gallery",Toast.LENGTH_LONG).show()

                        }
                    }
            }else if(requestCode == CAMERA){
                val thumbnail : Bitmap = data!!.extras!!.get("data") as Bitmap
                val saveImageToInternalStorage = saveImageToInternalStorage(thumbnail)
                Log.e("", "Path : $saveImageToInternalStorage")
                viewBinding?.ivPlaceImage!!.setImageBitmap(thumbnail)

            }
        }
    }
    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this).setMessage("looks like permissions are turned off for this feature").setPositiveButton("go to settings")
        {
            _, _ ->
            run {
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
        }.setNegativeButton("cancel"){
            dialog,_ ->
            run {
                dialog.dismiss()
            }
        }.show()
    }

    private fun UpdateDateInView(){
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        viewBinding?.etDate?.setText(sdf.format(calender.time).toString())
    }
    private fun saveImageToInternalStorage(bitmap : Bitmap) : Uri{
        val wrapper = ContextWrapper(applicationContext)
        var file : File = wrapper.getDir(IMAGE_DIRECTORY,Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")

        try {
            var stream : OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        }catch (e : IOException){
            e.printStackTrace()
        }

        return Uri.parse(file.absolutePath)

    }
    companion object{
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val IMAGE_DIRECTORY = "HappyPlacesImages"
    }
}