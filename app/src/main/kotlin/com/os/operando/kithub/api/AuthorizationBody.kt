package com.os.operando.kithub.api

import com.os.operando.kithub.BuildConfig

class AuthorizationBody(val clientSecret: String = BuildConfig.GITHUB_CLIENT_SECRET,
                        val scopes: List<String> = listOf("repo"),
                        val note: String = "",
                        val noteUrl: String = "") {
}