package com.altun.dimasnotebook.ui.home

import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.altun.dimasnotebook.R
import com.altun.dimasnotebook.room.NoteModel
import com.altun.dimasnotebook.tools.Tools
import kotlinx.android.synthetic.main.note_recycler_view_item_layout.view.*

class NoteRecyclerViewAdapter(private val items: MutableList<NoteModel>) :
    RecyclerView.Adapter<NoteRecyclerViewAdapter.ViewHolder>() {

    var currentPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.note_recycler_view_item_layout, parent, false
        )
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var model: NoteModel
        fun onBind() {
            model = items[adapterPosition]
            currentPosition = adapterPosition + 1
            itemView.titleTextView.text = model.title
            itemView.setOnClickListener {
                Tools.showDialog(itemView.context, model.title!!, model.description!!)
            }
        }
    }

}