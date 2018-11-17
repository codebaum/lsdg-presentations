package com.codebaum.lsdgpresentations.data

/**
 * Created on 10/5/18.
 */
data class User(
        val uid: String,
        val email: String?,
        val starredPresentations: List<String>
)