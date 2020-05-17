package com.example.coplanning.communication

import android.util.Log
import okhttp3.OkHttpClient

import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitClient {

   constructor() {
        val okHttpClientBuilder: OkHttpClient.Builder = okhttp3.OkHttpClient.Builder()
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        okHttpClientBuilder.addInterceptor(logging)
        Log.d("MyLog", "Connecting with localhost:")
        val builder: Retrofit.Builder = Retrofit.Builder()
           // .baseUrl("http://10.0.2.2:3000/")
            .baseUrl("https://co-planning.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClientBuilder.build())
        Log.d("MyLog", "Connected")
        retrofit = builder.build()
    }

    private lateinit var retrofit: Retrofit

    fun GetRetrofitEntity(): Retrofit {
        return retrofit
    }
}