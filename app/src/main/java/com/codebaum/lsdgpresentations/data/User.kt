package com.codebaum.lsdgpresentations.data

/**
 * Created on 10/5/18.
 */
data class User(
        val id: String,
        val email: String?,
        val starredPresentations: List<String>
)