package com.example.happyplaces


import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.happyplaces.databinding.ActivityAddHappyPlaceBinding
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
        }
    }
    private fun UpdateDateInView(){
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        viewBinding?.etDate?.setText(sdf.format(calender.time).toString())
    }
}