package com.os.operando.kithub.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.os.operando.kithub.BuildConfig
import com.os.operando.kithub.R
import com.os.operando.kithub.api.AuthorizationBody
import com.os.operando.kithub.api.GitHubApiClient
import com.os.operando.kithub.api.GitHubPaginationInterceptor
import com.os.operando.kithub.databinding.ActivityMainBinding
import com.os.operando.kithub.extension.addTo
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.HttpException
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import timber.log.Timber
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private val subscriptions = CompositeSubscription()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttp = OkHttpClient.Builder()
                .addNetworkInterceptor(StethoInterceptor())
                .addInterceptor(loggingInterceptor)
                .addInterceptor(GitHubPaginationInterceptor())
                .build()

        val retrofit = Retrofit.Builder()
                .client(okHttp)
                .baseUrl("https://api.github.com")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        retrofit.create(GitHubApiClient::class.java)
                .getUser("operando")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Timber.d(it.toString())
                    binding.user = it
                })
                .addTo(subscriptions)

        val header = "token " + BuildConfig.GITHUB_TEST_TOKEN

        retrofit.create(GitHubApiClient::class.java)
                .getUserRepos(header, 1, 2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Timber.d(it.toString())
                })
                .addTo(subscriptions)

        binding.login.setOnClickListener({
            val header = getBasicHeader(binding.userName.text.toString(), binding.pass.text.toString())
            val fingerprint = UUID.randomUUID().toString()
            val body = AuthorizationBody()
            retrofit.create(GitHubApiClient::class.java)
                    .putAuthorization(header, clientId = BuildConfig.GITHUB_CLIENT_ID, fingerprint = fingerprint, body = body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        Timber.d(it.toString())
                    }, {
                        Timber.e(it)
                        if (it is HttpException) {
                            val twoFactor: String? = it.response().headers().get("X-GitHub-OTP")
                            twoFactor?.let {
                                val code = binding.code.text.toString()
                                retrofit.create(GitHubApiClient::class.java).putAuthorization(header, code, BuildConfig.GITHUB_CLIENT_ID, fingerprint, body)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({
                                            Timber.d(it.toString())
                                        }, {
                                            Timber.e(it)
                                        })
                                        .addTo(subscriptions)
                            }
                        }
                    })
                    .addTo(subscriptions)
        })
    }

    fun getBasicHeader(name: String, password: String): String =
            "Basic " + Base64.encodeToString((name + ":" + password).toByteArray(), Base64.NO_WRAP)

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.unsubscribe()
    }
}