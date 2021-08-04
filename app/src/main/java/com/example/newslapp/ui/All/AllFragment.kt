package com.example.newslapp.ui.All

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.newslapp.MainActivity
import com.example.newslapp.R
import com.example.newslapp.ui.NewsViewmodel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_all.*

//class AllFragment : Fragment() {
//
//    lateinit var viewModel: NewsViewmodel
//    val args:AllFragmentArgs by navArgs()
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//
//        viewModel = (activity as MainActivity).viewModel
//        val article= args.articleData
//        web_View.apply {
//            loadUrl(article.url)
//        }
//
//        return inflater.inflate(R.layout.fragment_all, container, false)
//
//    }
//}


class AllFragment : Fragment(R.layout.fragment_all) {

    lateinit var viewModel: NewsViewmodel
    val args:  AllFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        val article = args.articleData
        web_View.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url!!)
        }

        fab.setOnClickListener {
            viewModel.savedArticle(article)
            Snackbar.make(view,"Article saved",Snackbar.LENGTH_SHORT).show()
        }

    }
}

