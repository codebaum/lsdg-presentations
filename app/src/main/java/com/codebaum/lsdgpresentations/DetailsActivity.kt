package com.codebaum.lsdgpresentations

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.codebaum.lsdgpresentations.data.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
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

    private lateinit var presentationUID: String
    private lateinit var userUID: String

    private lateinit var presentationDocumentReference: DocumentReference
    private lateinit var userDocumentReference: DocumentReference

    private var isStarred = false

    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        presentationUID = intent.getStringExtra(KEY_PRESENTATION_ID)
        userUID = repository.currentFirebaseUser?.uid ?: ""

        presentationDocumentReference = repository.presentations.document(presentationUID)
        userDocumentReference = repository.users.document(userUID)

        presentationDocumentReference.addSnapshotListener { documentSnapshot, _ ->
            if (documentSnapshot != null) {
                onPresentationSnapshot(documentSnapshot)
            }
        }

        userDocumentReference.addSnapshotListener { documentSnapshot, _ ->
            if (documentSnapshot != null) {
                onUserSnapshot(documentSnapshot)
            }
        }

        fab_starred.setOnClickListener {

            isStarred = !isStarred

            updateFABImage()

            showSnackbarMessage(it)

            val updatedStarredPresentations = ArrayList(user.starredPresentations)
            if (isStarred) {
                updatedStarredPresentations.add(presentationUID)
            } else {
                updatedStarredPresentations.remove(presentationUID)
            }

            userDocumentReference.update("starred_presentations", updatedStarredPresentations)
        }
    }

    private fun onPresentationSnapshot(snapshot: DocumentSnapshot) {

        val presentation = presentationMapper.from(snapshot)

        if (presentation.isEditableBy(userUID)) {
            details_coordinator_layout.setBackgroundColor(ContextCompat.getColor(this@DetailsActivity, R.color.primary_dark))
        }

        tv_description_value.text = presentation.description

        title = presentation.name
    }

    private fun onUserSnapshot(snapshot: DocumentSnapshot) {

        user = userMapper.from(snapshot)

        isStarred = user.starredPresentations.contains(presentationUID)

        updateFABImage()
    }

    private fun updateFABImage() {
        val imageRes = if (isStarred) {
            android.R.drawable.btn_star_big_on
        } else {
            android.R.drawable.btn_star_big_off
        }

        fab_starred.setImageResource(imageRes)
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
