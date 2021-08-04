package com.example.newslapp.ui.Breaking

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newslapp.Adapter.NewsAdapter
import com.example.newslapp.Api.NewsResponse
import com.example.newslapp.MainActivity
import com.example.newslapp.R
import com.example.newslapp.ui.NewsViewmodel
import com.example.newslapp.util.Constants.Companion.QUERY_PAGE_SIZE
import com.example.newslapp.util.Resource
import kotlinx.android.synthetic.main.fragment_breaking.*

class BreakingFragment : Fragment() {

    lateinit var viewModel: NewsViewmodel

    lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_breaking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       viewModel = (activity as MainActivity).viewModel
        setupRecyclerview()


        newsAdapter.setOnitemClicklistener {
            val bundle=Bundle().apply {
                putSerializable("article_data",it)
            }
            findNavController().navigate(R.id.action_breaking_newsfrag_to_all_newsfrag,bundle)
        }


        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { resp ->
                        newsAdapter.differ.submitList(resp.articles.toList())
                        val totalPages= resp.totalResults/ QUERY_PAGE_SIZE+2
                        isLastpage=viewModel.breakingNewspage== totalPages
                        if (isLastpage){
                            breaking_rv.setPadding(0,0,0,0)
                        }

                    }

                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { msg ->
                        Toast.makeText(activity,"an error occurred:$msg",Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar() {
        pb_breaking.visibility = View.INVISIBLE
        isLoading=false
    }

    private fun showProgressBar() {
        pb_breaking.visibility = View.VISIBLE
        isLoading=true
    }
    
    var isLoading=false
    var isLastpage=false
    var isScrolling=false

    val scrollListener= object :RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState==AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
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
            val isTotalmoreThanvisible= totalItemcount>= QUERY_PAGE_SIZE
            val shouldPaginate= isAtlastItem && isNotatBeginning && isTotalmoreThanvisible && isNotloadingOrlastPage
                    && isScrolling
            if (shouldPaginate){
                viewModel.getBreakingNews("us")
                isScrolling=false
            }
        }
    }

    private fun setupRecyclerview() {
        newsAdapter = NewsAdapter()
        breaking_rv.apply {
            adapter = newsAdapter
            addOnScrollListener(this@BreakingFragment.scrollListener)
        }
    }

}