package com.example.newslapp.Api

data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)