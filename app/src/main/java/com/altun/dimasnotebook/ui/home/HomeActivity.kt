package com.altun.dimasnotebook.ui.home

import android.app.Activity
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.altun.dimasnotebook.App
import com.altun.dimasnotebook.R
import com.altun.dimasnotebook.tools.animHide
import com.altun.dimasnotebook.tools.animShow
import com.altun.dimasnotebook.room.DatabaseBuilder
import com.altun.dimasnotebook.room.NoteModel
import com.altun.dimasnotebook.ui.new_note.NewNoteActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.*
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var adapter: NoteRecyclerViewAdapter
    private val noteList = mutableListOf<NoteModel>()
    var menuMode = true

    companion object {
        const val REQUEST_CODE = 1
        val checkedItems = mutableListOf<NoteModel>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
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
            val intent = Intent(this, NewNoteActivity::class.java)
            menuHide()
            startActivityForResult(intent, REQUEST_CODE)
        }
        deleteButton.setOnClickListener {
            checkedItems.forEach {
                CoroutineScope(Dispatchers.IO).launch {
                    DatabaseBuilder.getInstance().noteDao().deleteAll(it)
                    noteList.remove(it)
                    withContext(Dispatchers.Main) {
                        adapter.notifyDataSetChanged()
                        menuHide()
                    }
                }
            }

        }
    }

    private fun initRecyclerView() {
        adapter =
            NoteRecyclerViewAdapter(noteList)
        noteRecyclerView.layoutManager = LinearLayoutManager(this)
        noteRecyclerView.adapter = adapter

        GlobalScope.launch(Dispatchers.IO) {
            noteList.addAll(DatabaseBuilder.getInstance().noteDao().getAll())
            withContext(Dispatchers.Main) { adapter.notifyDataSetChanged() }
        }
    }


    private fun menuShow() {
        menuButton.setImageResource(R.drawable.ic_clear)
        addButton.animShow()
        deleteButton.animShow()
        menuMode = false
    }

    private fun menuHide() {
        menuButton.setImageResource(R.drawable.ic_menu)
        addButton.animHide()
        deleteButton.animHide()
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
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                noteList.removeAt(viewHolder.adapterPosition)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
            }

        })
        touchHelper.attachToRecyclerView(noteRecyclerView)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val note = data!!.extras!!.getParcelable("note") as NoteModel?
            noteList.add(note!!)
            adapter.notifyItemInserted(noteList.size - 1)
            noteRecyclerView.scrollToPosition(noteList.size - 1)
        }
    }
}