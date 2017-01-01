package com.os.operando.kithub.entity

data class Repo(val id: Long,
                val name: String,
                val fullName: String,
                val description: String,
                val stargazersCount: Int,
                val htmlUrl: String,
                val owner: User)