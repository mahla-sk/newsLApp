package com.example.newslapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.newslapp.db.ArticleDatabase
import com.example.newslapp.repository.NewsRepository
import com.example.newslapp.repository.NewsViewmodelProviderfactory
import com.example.newslapp.ui.NewsViewmodel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


   // private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: NewsViewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val navView: BottomNavigationView = binding.navView
//
//        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.breaking_newsitem, R.id.saved_newsitem, R.id.search_newsitem,R.id.all_newsitem
//            )
//        )

        val navHostFragment= supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController= navHostFragment.navController
        nav_view.setupWithNavController(navController)
       // nav_view?.setupWithNavController(nav_host_fragment_activity_main.findNavController())
        val newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory= NewsViewmodelProviderfactory(application,newsRepository)
        viewModel= ViewModelProvider(this,viewModelProviderFactory).get(NewsViewmodel::class.java)


//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)




    }
}