package com.camu.simagrow.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetroFitInstance {

    private const val BASE_URL = "http://20.111.17.43/simagrow/"

    val api: SimaGrowApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SimaGrowApi::class.java)
    }
}
