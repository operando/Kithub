package com.os.operando.kithub.entity

data class User(val id: Long,
                val login: String,
                val avatar_url: String,
                val email: String,
                val bio: String,
                val followers: Int,
                val following: Int) {
}