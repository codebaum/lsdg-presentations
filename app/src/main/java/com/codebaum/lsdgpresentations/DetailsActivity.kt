package com.codebaum.lsdgpresentations

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.codebaum.lsdgpresentations.data.PresentationMapper
import com.codebaum.lsdgpresentations.data.Repository
import com.codebaum.lsdgpresentations.data.User
import com.codebaum.lsdgpresentations.data.UserMapper
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity() {

    /// name
    /// presenter
    /// state
    /// description
    /// starred

    private val repository = Repository()

    private val presentationMapper = PresentationMapper()
    private val userMapper = UserMapper()

    private val db = FirebaseFirestore.getInstance()

    private lateinit var presentationId: String
    private lateinit var user: User

    private var userDocumentReference: DocumentReference? = null

    private var isStarred = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        presentationId = intent.getStringExtra(KEY_PRESENTATION_ID)

        repository.currentUser?.apply {
            userDocumentReference = repository.users.document(uid)
        }

        updateContent(presentationId)

        updateFAB()
    }

    private fun updateContent(docId: String) {
        repository.presentations.document(docId).addSnapshotListener { documentSnapshot, _ ->

            documentSnapshot?.apply {
                val presentation = presentationMapper.from(documentSnapshot)
                tv_description_value.text = presentation.description

                title = presentation.name
            }
        }
    }

    private fun updateFAB() {

        if (userDocumentReference == null) {
            fab_starred.isEnabled = false
            return
        }

        userDocumentReference?.apply {
            addSnapshotListener { documentSnapshot, _ ->
                documentSnapshot?.apply {

                    user = userMapper.from(documentSnapshot)

                    isStarred = user.starredPresentations.contains(presentationId)

                    updateFABImage()
                }
            }
            setupFABListener(this)
        }
    }

    private fun updateFABImage() {
        val imageRes = if (isStarred) {
            android.R.drawable.btn_star_big_on
        } else {
            android.R.drawable.btn_star_big_off
        }

        fab_starred.setImageResource(imageRes)
    }

    private fun setupFABListener(documentReference: DocumentReference) {
        fab_starred.setOnClickListener {

            isStarred = !isStarred

            updateFABImage()

            showSnackbarMessage(it)

            val updatedStarredPresentations = ArrayList(user.starredPresentations)
            if (isStarred) {
                updatedStarredPresentations.add(presentationId)
            } else {
                updatedStarredPresentations.remove(presentationId)
            }

            documentReference.update("starred_presentations", updatedStarredPresentations)
        }
    }

    private fun showSnackbarMessage(it: View) {
        val message = if (isStarred) {
            "Added to favorites!"
        } else {
            "Removed from favorites."
        }
        Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
    }

    companion object {

        const val KEY_PRESENTATION_ID = "KEY_PRESENTATION_ID"

        fun getStartIntent(context: Context, docId: String): Intent {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra(KEY_PRESENTATION_ID, docId)
            return intent
        }
    }
}
