package com.example.happyplaces.models

data class HappyPlaceModel(
    val id : Int,
    val title : String,
    val image : String,
    val description : String,
    val date : String,
    val location : String,
    val latitude : Double,
    val longitude : Double
) : java.io.Serializable

//Serializable will bring it to format which we can pass from one class to other in intent