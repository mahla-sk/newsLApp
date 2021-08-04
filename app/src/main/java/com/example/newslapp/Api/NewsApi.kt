package com.example.newslapp.Api

import com.example.newslapp.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("v2/top-headlines")
    suspend fun breakingNews(
    @Query("country")
    countrycode:String="us",
    @Query("page")
    page:Int=1,
    @Query("Apikey")
    Apikey:String=API_KEY
    ):Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchforNews(
        @Query("q")
        searchquery:String,
        @Query("page")
        page:Int=1,
        @Query("Apikey")
        Apikey:String=API_KEY
    ):Response<NewsResponse>


}