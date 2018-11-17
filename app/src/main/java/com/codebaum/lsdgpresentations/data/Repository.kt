package com.codebaum.lsdgpresentations.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class Repository {

    private val firebaseAuth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

    private val database: FirebaseFirestore
        get() = FirebaseFirestore.getInstance()

    val currentFirebaseUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    val presentations = database.collection(PRESENTATIONS_COLLECTION_PATH)

    val users = database.collection(Companion.USERS_COLLECTION_PATH)

    fun signOut() = firebaseAuth.signOut()

    companion object {
        private const val USERS_COLLECTION_PATH = "users"
        private const val PRESENTATIONS_COLLECTION_PATH = "presentations"
    }
}