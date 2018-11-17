package com.codebaum.lsdgpresentations.data

import com.google.firebase.auth.FirebaseUser

/**
 * Created on 10/5/18.
 */
data class Presentation(
        val uid: String,
        val name: String,
        val presenterUID: String,
        val state: String,
        val description: String
)

fun Presentation.isEditableBy(userUID: String) = presenterUID == userUID