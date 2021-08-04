package com.example.newslapp.ui.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newslapp.Adapter.NewsAdapter
import com.example.newslapp.MainActivity
import com.example.newslapp.R
import com.example.newslapp.ui.NewsViewmodel
import com.example.newslapp.util.Constants
import com.example.newslapp.util.Resource
import kotlinx.android.synthetic.main.fragment_breaking.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchFragment : Fragment() {

lateinit var viewModel:NewsViewmodel
lateinit var newsAdapter:NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= (activity as MainActivity).viewModel
        setupRecyclerview()

        newsAdapter.setOnitemClicklistener {
            val bundle=Bundle().apply {
                putSerializable("article_data",it)
            }
            findNavController().navigate(R.id.action_search_newsfrag_to_all_newsfrag,bundle)
        }


        var job: Job?=null
        searchtv.addTextChangedListener {editable->
            job?.cancel()
            job= MainScope().launch {
                delay(500L)
                editable?.let {
                    if (editable.toString().isNotEmpty()){
                        viewModel.searchNews(editable.toString())
                    }
                }
            }
        }


        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resp ->
                        newsAdapter.differ.submitList(resp.articles.toList())
                        val totalPages= resp.totalResults/ Constants.QUERY_PAGE_SIZE +2
                        isLastpage=viewModel.searchNewspage== totalPages

                    }

                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { msg ->
                        Toast.makeText(activity,"an error occurred:$msg", Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar() {
        pb_search.visibility = View.INVISIBLE
        isLoading=false
    }

    private fun showProgressBar() {
        pb_search.visibility = View.VISIBLE
        isLoading=true
    }

    var isLoading=false
    var isLastpage=false
    var isScrolling=false

    val scrollListener= object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling=true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager= recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemposition= layoutManager.findFirstVisibleItemPosition()
            val visibleItemcount= layoutManager.childCount
            val totalItemcount= layoutManager.itemCount

            val isNotloadingOrlastPage= !isLoading && !isLastpage
            val isAtlastItem= firstVisibleItemposition+visibleItemcount >= totalItemcount
            val isNotatBeginning= firstVisibleItemposition>=0
            val isTotalmoreThanvisible= totalItemcount>= Constants.QUERY_PAGE_SIZE
            val shouldPaginate= isAtlastItem && isNotatBeginning && isTotalmoreThanvisible && isNotloadingOrlastPage
                    && isScrolling
            if (shouldPaginate){
                viewModel.searchNews(srch_rv.toString())
                isScrolling=false
            }else{
                srch_rv.setPadding(0,0,0,0)
            }
        }
    }

    private fun setupRecyclerview() {
        newsAdapter = NewsAdapter()
        srch_rv.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchFragment.scrollListener)
        }
    }
    }

