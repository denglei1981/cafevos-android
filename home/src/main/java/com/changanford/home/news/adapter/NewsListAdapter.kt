package com.changanford.home.news.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.home.R
import com.changanford.home.adapter.RecommendAdapter
import com.changanford.home.news.data.NewsData


class NewsListAdapter: BaseQuickAdapter<NewsData, BaseViewHolder>(R.layout.item_news_items) {
    val  recommendAdapter: RecommendAdapter by lazy {
        RecommendAdapter()
    }
    override fun convert(holder: BaseViewHolder, item: NewsData) {

    }
}