package com.example.newslapp.ui.Saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.newslapp.Adapter.NewsAdapter
import com.example.newslapp.MainActivity
import com.example.newslapp.R
import com.example.newslapp.ui.NewsViewmodel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_breaking.*
import kotlinx.android.synthetic.main.fragment_saved.*

class SavedFragment : Fragment() {

lateinit var newsAdapter:NewsAdapter
lateinit var viewModel:NewsViewmodel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_saved, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= (activity as MainActivity).viewModel
        setupRecyclerview()

        newsAdapter.setOnitemClicklistener {
            val bundle=Bundle().apply {
                putSerializable("article_data",it)
            }
            findNavController().navigate(R.id.action_saved_newsfrag_to_all_newsfrag,bundle)
        }

        val itemTouchHelperCallback= object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val position= viewHolder.adapterPosition
                val article= newsAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)
                Snackbar.make(view,"Article deleted",Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        viewModel.savedArticle(article)
                    }
                    show()
                }
            }

        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(saved_rv)
        }


        viewModel.getSavednews().observe(viewLifecycleOwner, Observer {articles->
            newsAdapter.differ.submitList(articles)
        })

    }
    private fun setupRecyclerview() {
        newsAdapter = NewsAdapter()
        saved_rv.apply {
            adapter = newsAdapter
        }
    }
}