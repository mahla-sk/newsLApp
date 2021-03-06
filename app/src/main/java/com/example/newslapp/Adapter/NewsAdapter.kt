package com.example.newslapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newslapp.Api.Article
import com.example.newslapp.R
import kotlinx.android.synthetic.main.item_article.view.*


class NewsAdapter :RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>(){

    inner class ArticleViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
    //to update what has changed in list and not to update everything when its not even changed
    private val differCallback=object :DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url==newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem==newItem
        }

    }
    val differ= AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val itemView= LayoutInflater.from(parent.context).inflate(R.layout.item_article,parent,false)
        return ArticleViewHolder(itemView)
    }


    override fun getItemCount(): Int {
       return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article= differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(ArticleImage)
            tvSource.text = article.source?.name
            tvTitle.text = article.title
            tvDescription.text = article.description
            tvPublishedAt.text = article.publishedAt
            setOnClickListener {
                onItemClickListener?.let {
                    it(article)
                }
            }
        }
    }

    private var onItemClickListener:((Article)->Unit)?=null
    fun setOnitemClicklistener(listener: (Article)->Unit){
        onItemClickListener=listener
    }

}

