package com.codebaum.lsdgpresentations.data

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot

/**
 * Created on 10/5/18.
 */
class PresentationMapper {

    fun from(documentSnapshot: DocumentSnapshot): Presentation {
        val id = documentSnapshot.id
        val name = documentSnapshot.get("name") as String
        val presenterReference = documentSnapshot.get("presenter") as DocumentReference
        val presenter = presenterReference.id
        val state = documentSnapshot.get("state") as String
        val description = documentSnapshot.get("description") as String
        return Presentation(id, name, presenter, state, description)
    }
}