package com.happyplaces.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.R
import com.example.happyplaces.activities.AddHappyPlaceActivity
import com.example.happyplaces.activities.MainActivity
import com.example.happyplaces.databinding.ItemHappyPlaceBinding
import com.example.happyplaces.models.HappyPlaceModel
//import kotlinx.android.synthetic.main.item_happy_place.view.*

// TODO (Step 6: Creating an adapter class for binding it to the recyclerview in the new package which is adapters.)
// START
class HappyPlacesAdapter(
    private var list: ArrayList<HappyPlaceModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //2
    private var onClickListener : onclickListener? = null

    inner class MainViewHolder(private val itemBinding : ItemHappyPlaceBinding)
        : RecyclerView.ViewHolder(itemBinding.root)
    {
        val tvTitle = itemBinding.tvTitle
        val tvDescription = itemBinding.tvDescription
        val tvImage = itemBinding.ivPlaceImage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MainViewHolder(
            ItemHappyPlaceBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MainViewHolder) {
            holder.tvTitle.text = model.title
            holder.tvDescription.text = model.description
            holder.tvImage.setImageURI(Uri.parse(model.image))

            //5
            holder.itemView.setOnClickListener{
                if(onClickListener != null){
                    onClickListener!!.onClick(position,model)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    //3
    fun setOnClickListener(onclickListener: onclickListener){
        this.onClickListener = onclickListener
    }

    fun notifyEditItem(activity : Activity,position: Int,requestCode : Int){
        val intent = Intent(activity.applicationContext,AddHappyPlaceActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS,list[position])

        activity.startActivityForResult(intent,requestCode)
        notifyItemChanged(position)
    }
    //1
    interface onclickListener{
        fun onClick(position: Int,model: HappyPlaceModel)
    }

}
// END