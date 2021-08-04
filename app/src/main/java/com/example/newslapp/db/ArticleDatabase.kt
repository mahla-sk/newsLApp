package com.example.newslapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.newslapp.Api.Article

@Database(entities = [Article::class], version = 1)
@TypeConverters(Converters::class)

//data base classes for room always need to be abstract
abstract class ArticleDatabase:RoomDatabase(){

//the function is abstract so i dont have to implement it, room will do it
    abstract fun getArticleDao():DAO

    companion object{
        //this means other threads can see when a thread changes the instance below
        @Volatile
        private var instance:ArticleDatabase?=null
        //used to synchronize setting the instance and make sure there is only one instance at once
        private var Lock= Any()

        //everything happens in the block can not be accessed by other threads
        //when invoke it returns the instance but if its null then it will set the instance in synchronize block
        operator fun invoke(context:Context) = instance?: synchronized(Lock){
            instance?: createDatabase(context).also{ instance=it}

        }

        private fun createDatabase(context: Context)= Room.databaseBuilder(
            context.applicationContext,ArticleDatabase::class.java,"article_db.db"
        ).build()

    }
}