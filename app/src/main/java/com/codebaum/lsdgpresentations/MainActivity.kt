package com.codebaum.lsdgpresentations

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebaum.lsdgpresentations.data.Presentation
import com.codebaum.lsdgpresentations.data.PresentationMapper
import com.codebaum.lsdgpresentations.data.Repository
import com.codebaum.lsdgpresentations.utils.RESULT_SIGN_OUT
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val repository = Repository()

    private val presentationMapper = PresentationMapper()

    private lateinit var viewAdapter: MyAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buildNavigation()

        buildListView()

        if (repository.currentFirebaseUser == null) {
            startLoginFlow()
            return
        }

        updateView()

        fab_submit.setOnClickListener {
            val intent = SubmitActivity.getStartIntent(this)
            startActivityForResult(intent, REQUEST_CODE_SUBMIT_PRESENTATION)
        }
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

        bottom_navigation.selectedItemId = R.id.action_suggested

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            var wasNavigationItemSelectionHandled = true
            when (item.itemId) {
                R.id.action_upcoming -> {
                    viewAdapter.filter("upcoming")
                    fab_submit.hide()
                }
                R.id.action_suggested -> {
                    viewAdapter.filter("suggested")
                    fab_submit.show()
                }
                R.id.action_past -> {
                    viewAdapter.filter("completed")
                    fab_submit.hide()
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

        repository.presentations.addSnapshotListener { querySnapshot, _ ->
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
        menuInflater.inflate(R.menu.menu_main, menu)
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
            REQUEST_CODE_SIGN_IN -> handleSignInResult(resultCode, data)
            REQUEST_CODE_VIEW_PROFILE -> handleViewProfileResult(resultCode)
        }
    }

    private fun handleSignInResult(resultCode: Int, data: Intent?) {
        val response = IdpResponse.fromResultIntent(data)
        val isNewUser = response?.isNewUser ?: false
        when (resultCode) {
            Activity.RESULT_OK -> handleSuccessfulSignIn(isNewUser)
            else -> finish()
        }
    }

    private fun handleSuccessfulSignIn(isNewUser: Boolean) {
        if (isNewUser) {
            createFirestoreUser()
        }
        updateView()
    }

    private fun createFirestoreUser() {
        val newUser = repository.currentFirebaseUser ?: return

        val userPOJO = hashMapOf<String, Any>()
        userPOJO["email"] = newUser.email ?: ""
        userPOJO["starred_presentations"] = ArrayList<String>()

        val newUserDocument = repository.users.document(newUser.uid)
        newUserDocument.set(userPOJO, SetOptions.merge())
    }

    private fun handleViewProfileResult(resultCode: Int) {
        when (resultCode) {
            RESULT_SIGN_OUT -> finish()
            else -> {
                // do nothing
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_SIGN_IN: Int = 1
        private const val REQUEST_CODE_VIEW_PROFILE: Int = 2
        private const val REQUEST_CODE_SUBMIT_PRESENTATION: Int = 3
    }
}