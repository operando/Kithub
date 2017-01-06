package com.os.operando.kithub.entity

data class AuthToken(val id: Long,
                     val token: String,
                     val hashedToken: String,
                     val scopes: List<String>,
                     val fingerprint: String)