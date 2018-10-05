package com.codebaum.lsdgpresentations

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val viewAdapter = MyAdapter(arrayListOf())
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buildNavigation()

        buildListView()

        updateListView()

    }

    private fun buildNavigation() {
        val bottomNavigationView = findViewById<View>(R.id.bottom_navigation) as BottomNavigationView

        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_favorites ->
                    // do something here
                    return@OnNavigationItemSelectedListener true
                R.id.action_schedules ->
                    // do something here
                    return@OnNavigationItemSelectedListener true
                R.id.action_music ->
                    // do something here
                    return@OnNavigationItemSelectedListener true
            }
            false
        })
    }

    private fun updateListView() {
        val db = FirebaseFirestore.getInstance()

        db.collection("presentations").get().addOnCompleteListener {
            if (it.isSuccessful) {
                val presentationList = ArrayList<String>()
                it.result?.forEach { presentation ->
                    val name = presentation.get("name") as String
                    presentationList.add(name)
                    name.logDebug()
                }
                viewAdapter.update(presentationList)
            } else {
                val message = "Error occurred."
                toast(message)
                message.logDebug()
            }
        }
    }

    private fun buildListView() {
        viewManager = LinearLayoutManager(this)

        recyclerView = findViewById<RecyclerView>(R.id.rv_presentations).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }
    }
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun String.logDebug() {
    Log.d("DEBUG", this)
}