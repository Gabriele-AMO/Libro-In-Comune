package com.example.progetto.RicercaLibri

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.progetto.databinding.CardViewDesigneBinding
import com.example.progetto.RicercaLibri.ItemsVMRicercaLibro as ItemsVMRicercaLibro


class CustomAdapterRicercaLibro(private val mList: List<ItemsVMRicercaLibro>) : RecyclerView.Adapter<CustomAdapterRicercaLibro.ViewHolder>() {

    private val TAG = "CustomAdapter"
    private var onClickListener: OnClickListener? = null
    class ViewHolder(binding: CardViewDesigneBinding) : RecyclerView.ViewHolder(binding.root) {

        private val TAG = "ViewHolder"

        val text_1View = binding.Campo1
        val text_2View = binding.Campo2
        val text_3View = binding.Campo3
        init {
            Log.i(TAG, "ViewHolder created")
        }
    }
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view that is used to hold list item
        val view = CardViewDesigneBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        Log.i(TAG, "Invoke onCreateViewHolder")
        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val items = mList[position]

        // sets the image to the imageview from our itemHolder class
        holder.text_1View.text = items.text_1

        // sets the text to the textview from our itemHolder class
        holder.text_2View.text = items.text_2

        holder.text_3View.text = items.text_3

        holder.itemView.setOnClickListener {
            onClickListener?.onClick(position,items)
        }

        Log.i(TAG, "Invoke onBindViewHolder for position ${position+1}")

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        Log.i(TAG, "Invoke getItemCount")
        return mList.size
    }

    interface OnClickListener {
        fun onClick(position: Int, model: ItemsVMRicercaLibro)
    }
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

}
// Holds the views for adding it to image and text
