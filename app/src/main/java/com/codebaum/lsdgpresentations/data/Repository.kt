package com.codebaum.lsdgpresentations.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object Repository {

    @JvmStatic
    fun getStoredUser() = FirebaseAuth.getInstance().currentUser

    @JvmStatic
    fun signOut() = FirebaseAuth.getInstance().signOut()
}