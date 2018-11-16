package com.codebaum.lsdgpresentations

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.codebaum.lsdgpresentations.data.Presentation
import com.codebaum.lsdgpresentations.data.PresentationMapper
import com.codebaum.lsdgpresentations.data.Repository
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val presentationMapper = PresentationMapper()

    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var viewAdapter: MyAdapter
    private lateinit var viewManager: androidx.recyclerview.widget.RecyclerView.LayoutManager

    private var user: FirebaseUser? = null

    private val REQUEST_CODE_SIGN_IN: Int = 1
    private val REQUEST_CODE_VIEW_PROFILE: Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buildNavigation()

        buildListView()

        user = Repository.getStoredUser()
        if (user == null) {
            startLoginFlow()
            return
        }

        updateView()
    }

    private fun startLoginFlow() {
        val providers = arrayListOf(
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.PhoneBuilder().build(),
                AuthUI.IdpConfig.AnonymousBuilder().build())

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                REQUEST_CODE_SIGN_IN)
    }

    private fun buildNavigation() {

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            var wasNavigationItemSelectionHandled = true
            when (item.itemId) {
                R.id.action_upcoming -> {
                    viewAdapter.filter("upcoming")
                }
                R.id.action_suggested -> {
                    viewAdapter.filter("suggested")
                }
                R.id.action_past -> {
                    viewAdapter.filter("completed")
                }
                else -> {
                    wasNavigationItemSelectionHandled = false
                }
            }
            wasNavigationItemSelectionHandled
        }
    }

    private fun updateView() {
        val db = FirebaseFirestore.getInstance()

        db.collection("presentations").addSnapshotListener { querySnapshot, _ ->
            val presentationList = ArrayList<Presentation>()
            querySnapshot?.documents?.forEach {
                val presentation = presentationMapper.from(it)
                presentationList.add(presentation)
            }
            viewAdapter.update(presentationList)
        }
    }

    private fun buildListView() {
        viewManager = LinearLayoutManager(this)

        viewAdapter = MyAdapter(object : OnItemClickListener {
            override fun onItemClicked(id: String) {
                val intent = DetailsActivity.getStartIntent(this@MainActivity, id)
                startActivity(intent)
            }
        })

        rv_presentations.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                val intent = ProfileActivity.getStartIntent(this)
                startActivityForResult(intent, REQUEST_CODE_VIEW_PROFILE)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_SIGN_IN -> handleSignInResult(resultCode)
            REQUEST_CODE_VIEW_PROFILE -> handleViewProfileResult(resultCode)
        }
    }

    private fun handleSignInResult(resultCode: Int) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val user = FirebaseAuth.getInstance().currentUser
                this.user = user
                updateView()
            }
            else -> finish()
        }
    }

    private fun handleViewProfileResult(resultCode: Int) {
        when (resultCode) {
            Activity.RESULT_CANCELED -> finish()
            else -> {
                // do nothing
            }
        }
    }
}