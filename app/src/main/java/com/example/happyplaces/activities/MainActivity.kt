package com.example.happyplaces.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.happyplaces.database.DatabaseHandler
import com.example.happyplaces.databinding.ActivityMainBinding
import com.example.happyplaces.models.HappyPlaceModel
import com.happyplaces.adapters.HappyPlacesAdapter

class MainActivity : AppCompatActivity() {
    private var viewBinding : ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding?.root)

        viewBinding?.fabAddHappyPlace?.setOnClickListener {
            val intent = Intent(this, AddHappyPlaceActivity::class.java)
            startActivityForResult(intent,ADD_PLACE_ACTIVITY_REQUEST_CODE)
        }
        getHappyPlacesListFromLocalDB()
    }
    private fun getHappyPlacesListFromLocalDB(){
        val dbHandler = DatabaseHandler(this)
        val HappyPlaceList = dbHandler.getHappyPlacesList()

        if(HappyPlaceList.size > 0){
            viewBinding?.rvHappyPlacesList?.visibility = View.VISIBLE
            viewBinding?.tvNoRecordsAvailable?.visibility = View.GONE
            setUpHappyPlaceRecyclerView(HappyPlaceList)
        }else{
            viewBinding?.rvHappyPlacesList?.visibility = View.GONE
            viewBinding?.tvNoRecordsAvailable?.visibility = View.VISIBLE
        }
    }

    private fun setUpHappyPlaceRecyclerView(happyPlaceList : ArrayList<HappyPlaceModel>){
        viewBinding?.rvHappyPlacesList?.layoutManager = LinearLayoutManager(this)

        viewBinding?.rvHappyPlacesList?.setHasFixedSize(true)
        val placesAdapter = HappyPlacesAdapter(happyPlaceList)
        viewBinding?.rvHappyPlacesList?.adapter = placesAdapter

        //4
        placesAdapter.setOnClickListener(object : HappyPlacesAdapter.onclickListener{
            override fun onClick(position: Int, model: HappyPlaceModel) {
                val intent = Intent(this@MainActivity,HappyPlaceDetailsActivity::class.java)

                intent.putExtra(EXTRA_PLACE_DETAILS,model)
                startActivity(intent)
            }

        })
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                getHappyPlacesListFromLocalDB()
            }
        }else{
            Log.i( "onActivityResult: ","cancelled")
        }
    }

    companion object{
        private const val ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
        const val EXTRA_PLACE_DETAILS = "EXTRA_PLACE_DETAILS"
    }
}