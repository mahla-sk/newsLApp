package com.example.newslapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.newslapp.Api.Article

@Dao
interface DAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long

    @Query("SELECT * FROM articles")
    fun getAllarticles():LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)
}