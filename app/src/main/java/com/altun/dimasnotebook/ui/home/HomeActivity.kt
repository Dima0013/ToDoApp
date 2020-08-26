package com.altun.dimasnotebook.ui.home

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log.d
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.altun.dimasnotebook.App
import com.altun.dimasnotebook.R
import com.altun.dimasnotebook.tools.animHide
import com.altun.dimasnotebook.tools.animShow
import com.altun.dimasnotebook.room.DatabaseBuilder
import com.altun.dimasnotebook.room.NoteModel
import com.altun.dimasnotebook.tools.Tools
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.add_item_layout.*
import kotlinx.coroutines.*
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var adapter: NoteRecyclerViewAdapter
    private val noteList = mutableListOf<NoteModel>()
    private lateinit var viewModel: HomeViewModel
    var menuMode = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        init()
        initRecyclerView()
        swapItems()
    }

    private fun init() {

        menuButton.setOnClickListener {
            if (menuMode)
                menuShow()
            else
                menuHide()

        }
        addButton.setOnClickListener {
            addItemDialog()
            menuHide()


        }
        searchButton.setOnClickListener {

        }
    }

    private fun initRecyclerView() {

        adapter =
            NoteRecyclerViewAdapter(noteList)
        noteRecyclerView.layoutManager = LinearLayoutManager(this@HomeActivity)
        noteRecyclerView.adapter = adapter
        GlobalScope.launch(Dispatchers.IO) {
            noteList.addAll(DatabaseBuilder.getInstance().noteDao().getAll())
            withContext(Dispatchers.Main) { adapter.notifyDataSetChanged() }
        }
    }


    private fun menuShow() {
        menuButton.setImageResource(R.drawable.ic_clear)
        addButton.animShow()
        searchButton.animShow()
        menuMode = false
    }

    private fun menuHide() {
        menuButton.setImageResource(R.drawable.ic_menu)
        addButton.animHide()
        searchButton.animHide()
        menuMode = true
    }


    private fun swapItems() {
        val touchHelper = ItemTouchHelper(object :
            ItemTouchHelper.Callback() {

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                return makeMovementFlags(dragFlags, swipeFlags)
            }


            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val sourcePosition = viewHolder.adapterPosition
                val targetPosition = target.adapterPosition
                Collections.swap(noteList, sourcePosition, targetPosition)
                adapter.notifyItemMoved(sourcePosition, targetPosition)
                adapter.notifyItemChanged(sourcePosition)
                adapter.notifyItemChanged(targetPosition)

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {


                CoroutineScope(Dispatchers.IO).launch {
                    DatabaseBuilder.getInstance().noteDao()
                        .deleteAll(noteList[viewHolder.adapterPosition])
                    noteList.removeAt(viewHolder.adapterPosition)
                    withContext(Dispatchers.Main) {
                        adapter.notifyDataSetChanged()
                    }
                }

            }

        })
        touchHelper.attachToRecyclerView(noteRecyclerView)
    }

    private fun addItemDialog() {
        val dialog = Dialog(this)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.add_item_layout)

        val params: ViewGroup.LayoutParams = dialog.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = params as WindowManager.LayoutParams
        dialog.saveButton.setOnClickListener {
            if (dialog.titleEditText.text.toString().isNotEmpty()) {
                val title = dialog.titleEditText.text.toString()
                val description = dialog.descriptionEditText.text.toString()
                val note = NoteModel(null, title, description)
                addItemInDatabase(note)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun addItemInDatabase(noteModel: NoteModel) {
        GlobalScope.launch(Dispatchers.IO) {
            DatabaseBuilder.getInstance().noteDao().insert(noteModel)
            withContext(Dispatchers.Main){
                noteList.add(noteModel)
                adapter.notifyItemInserted(noteList.size - 1)
                noteRecyclerView.scrollToPosition(noteList.size - 1)
            }
        }
    }


}