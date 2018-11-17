package com.codebaum.lsdgpresentations.data

import com.google.firebase.firestore.DocumentSnapshot

/**
 * Created on 10/5/18.
 */
class UserMapper {

    fun from(documentSnapshot: DocumentSnapshot): User {
        val id = documentSnapshot.id
        val email = documentSnapshot.get("email") as String?
        val starredPresentations = documentSnapshot.get("starred_presentations") as List<String>
        return User(id, email, starredPresentations)
    }
}