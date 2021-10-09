package com.changanford.home

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.changanford.home.adapter.HomePublishAdapter
import com.changanford.home.callback.ICallback
import com.changanford.home.data.PublishData
import com.changanford.home.data.ResultData
import com.changanford.home.widget.BasePopup

class PublishPopup(
    var context: Context,
    layoutView: View,
    width: Int,
    height: Int,
    var callback: ICallback
) : BasePopup(context, layoutView, width, height) {


    var list: MutableList<PublishData> = arrayListOf()

    var homeViewPagerAdapter: HomePublishAdapter? = null

    init {
        initView(layoutView)
    }

    fun initView(layoutView: View) {
        list.add(PublishData(1, context.getString(R.string.home_publish_acts),R.drawable.icon_home_publish_acts))
        list.add(PublishData(2, context.getString(R.string.home_publish_answer),R.drawable.icon_home_answer))
        list.add(PublishData(3, context.getString(R.string.home_publish_scan),R.drawable.icon_home_scan))
        homeViewPagerAdapter = HomePublishAdapter(list)
        var recyclerView = layoutView.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = homeViewPagerAdapter
        homeViewPagerAdapter?.setOnItemClickListener { adapter, view, position ->
            callback.onResult(ResultData(1, list[position]))

        }
    }


}