package com.os.operando.kithub.api

import android.net.Uri
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody


class GitHubPaginationInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.isSuccessful && response.peekBody(1).string() == "[") {
            var json = "{"

            val link = response.header("link")
            if (link != null) {
                val links = link.split(",".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()
                for (link1 in links) {
                    val pageLink = link1.split(";".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()
                    val page = Uri.parse(pageLink[0].replace("[<>]".toRegex(), "")).getQueryParameter("page")
                    val rel = pageLink[1].replace("\"".toRegex(), "").replace("rel=", "")

                    if (page != null)
                        json += String.format("\"%s\":\"%s\",", rel.trim { it <= ' ' }, page)
                }
            }

            json += String.format("\"items\":%s}", response.body().string())
            return response.newBuilder().body(ResponseBody.create(response.body().contentType(), json)).build()
        }
        return response
    }
}