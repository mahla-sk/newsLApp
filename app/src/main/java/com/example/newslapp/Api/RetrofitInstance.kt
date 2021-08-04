package com.example.newslapp.Api

import com.example.newslapp.util.Constants.Companion.Base_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitInstance {
    companion object{
        private val retrofit by lazy {
            //to log response of retrofit(for debugging)
            val logging=HttpLoggingInterceptor()
            //attach to retrofit to see what request is sent..with Body i can see the actual response
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client= OkHttpClient.Builder().addInterceptor(logging).build()

            Retrofit.Builder().baseUrl(Base_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client).build()
        }
        val api by lazy {
            retrofit.create(NewsApi::class.java)
        }
    }
}