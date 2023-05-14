package com.example.happyplaces.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.happyplaces.R
import com.example.happyplaces.databinding.ActivityHappyPlaceDetailsBinding
import com.example.happyplaces.models.HappyPlaceModel

class HappyPlaceDetailsActivity : AppCompatActivity() {
    private var viewBinding : ActivityHappyPlaceDetailsBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityHappyPlaceDetailsBinding.inflate(layoutInflater)
        setContentView(viewBinding?.root)


        var happyPlaceModel : HappyPlaceModel? = null
        if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){
            happyPlaceModel = intent.getSerializableExtra(MainActivity.EXTRA_PLACE_DETAILS) as HappyPlaceModel
        }

        if(happyPlaceModel != null){
            setSupportActionBar(viewBinding?.toolbarHappyPlaceDetail)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = happyPlaceModel.title

            viewBinding?.toolbarHappyPlaceDetail?.setNavigationOnClickListener {
                onBackPressed()
            }

            viewBinding?.ivPlaceImage?.setImageURI(
                Uri.parse(happyPlaceModel.image)
            )

            viewBinding?.tvDescription?.text = happyPlaceModel.description

            viewBinding?.tvLocation?.text = happyPlaceModel.location
        }
    }
}