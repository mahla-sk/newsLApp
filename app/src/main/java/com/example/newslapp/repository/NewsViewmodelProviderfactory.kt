@file:Suppress("UNCHECKED_CAST")

package com.example.newslapp.repository

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newslapp.ui.NewsViewmodel

class NewsViewmodelProviderfactory(val app:Application,val newsRepository: NewsRepository) :ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsViewmodel(app,newsRepository) as T
    }

}