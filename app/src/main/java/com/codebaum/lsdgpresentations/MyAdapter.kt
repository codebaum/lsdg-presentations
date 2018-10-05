package com.codebaum.lsdgpresentations

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.codebaum.lsdgpresentations.data.Presentation

typealias OnItemClicked = (String) -> Unit

/**
 * Created on 10/5/18.
 */
class MyAdapter(private val context: Context, private val listener: OnItemClicked) :
        RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    private val myDataset: ArrayList<Presentation> = arrayListOf()

    private var filteredState = "upcoming" // "completed", "suggested"
    private var filteredDataset: ArrayList<Presentation> = myDataset

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyAdapter.MyViewHolder {
        // create a new view
        val rootView = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
        val textView = rootView as TextView
        // set the view's size, margins, paddings and layout parameters

        return MyViewHolder(textView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val presentation = filteredDataset[position]
        holder.textView.text = presentation.name

        holder.textView.setOnClickListener {
            listener.invoke(presentation.id)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = filteredDataset.size

    fun update(newDataset: ArrayList<Presentation>) {
        myDataset.clear()
        myDataset.addAll(newDataset)
        filter(filteredState)
        notifyDataSetChanged()
    }

    fun filter(state: String) {
        filteredState = state
        filteredDataset = arrayListOf()

        for (presentation in myDataset) {
            if (presentation.state == filteredState) {
                filteredDataset.add(presentation)
            }
        }

        notifyDataSetChanged()
    }
}