package com.example.newslapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.core.content.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newslapp.Api.Article
import com.example.newslapp.Api.NewsResponse
import com.example.newslapp.NewsApplication
import com.example.newslapp.repository.NewsRepository
import com.example.newslapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

//to handle the responses of the request
class NewsViewmodel(app: Application, val newsrepository: NewsRepository) : AndroidViewModel(app) {

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewspage = 1
    var breakinNewsResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewspage = 1
    var searchNewsResponse: NewsResponse? = null

    init {
        getBreakingNews(countryCode = "us")
    }

    //viewmodelscope means the coroutine is alive as long as the viewmodel is alive
    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        safeBreakingnewsCall(countryCode)
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        safeSearchnewsCall(searchQuery)

    }

    private fun handleBreakingnewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                breakingNewspage++

                //if its thr first response
                if (breakinNewsResponse == null) {
                    breakinNewsResponse = resultResponse
                } else {
                    val oldArticles = breakinNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakinNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchnewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchNewspage++

                //if its thr first response
                if (searchNewsResponse == null) {
                    searchNewsResponse = resultResponse
                } else {
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun savedArticle(article: Article) = viewModelScope.launch {
        newsrepository.upsert(article)
    }

    fun getSavednews() = newsrepository.getSavednews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsrepository.deleteArticle(article)
    }

    private suspend fun safeBreakingnewsCall(countryCode: String) {
        breakingNews.postValue(Resource.Loading())
        try {
            if (netConnection()) {
                val response = newsrepository.getBreakingNews(countryCode, breakingNewspage)
                breakingNews.postValue(handleBreakingnewsResponse(response))
            } else {
                breakingNews.postValue(Resource.Error(" no internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> breakingNews.postValue(Resource.Error("network failure"))
                else -> breakingNews.postValue(Resource.Error(" conversion error"))
            }

        }
    }

    private suspend fun safeSearchnewsCall(searchQuery: String) {
        searchNews.postValue(Resource.Loading())
        try {
            if (netConnection()) {
                val response = newsrepository.searchNews(searchQuery, searchNewspage)
                searchNews.postValue(handleSearchnewsResponse(response))
            } else {
                searchNews.postValue(Resource.Error("no internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchNews.postValue(Resource.Error("network failure"))
                else -> searchNews.postValue(Resource.Error("conversion error"))
            }

        }
    }


    fun netConnection(): Boolean {
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val activeNetwork = connectivityManager.activeNetwork ?: return false
            //checking if its null then it returns fall means no net connection
            //with capabilities there is access to check if there is any kinda network
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {//if android version is bellow marshmello
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

}