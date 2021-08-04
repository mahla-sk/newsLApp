package com.example.newslapp.repository

import com.example.newslapp.Api.Article
import com.example.newslapp.Api.RetrofitInstance
import com.example.newslapp.db.ArticleDatabase
//to directly query the API
//and to access the database and api

class NewsRepository(val db:ArticleDatabase) {
    suspend fun getBreakingNews(countryCode:String,pageNumber:Int)=
         RetrofitInstance.api.breakingNews(countryCode,pageNumber)

    suspend fun searchNews(searchQuery:String, pageNumber: Int)=
        RetrofitInstance.api.searchforNews(searchQuery,pageNumber)

    suspend fun upsert(article: Article)= db.getArticleDao().upsert(article)
    fun getSavednews()= db.getArticleDao().getAllarticles()
    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
}