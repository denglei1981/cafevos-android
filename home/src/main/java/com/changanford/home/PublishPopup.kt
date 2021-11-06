package com.changanford.home

import android.content.Context
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.changanford.common.MyApp
import com.changanford.common.net.*
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast
import com.changanford.home.adapter.HomePublishAdapter
import com.changanford.home.api.HomeNetWork
import com.changanford.home.callback.ICallback
import com.changanford.home.data.PublishData
import com.changanford.home.data.ResultData
import com.changanford.home.util.AnimScaleInUtil
import com.changanford.home.util.launchWithCatch
import com.changanford.home.widget.BasePopup
import kotlin.also
import kotlin.onSuccess

class PublishPopup(
    var context: Context,
    var lifecycleOwner: LifecycleOwner,
    layoutView: View,
    width: Int,
    height: Int,
    var callback: ICallback
) : BasePopup(context, layoutView, width, height) {


    val acts: String = "activity_add_wonderful"

    val questiton: String = "add_questionnaire"

    var list: MutableList<PublishData> = arrayListOf()

    var homeViewPagerAdapter: HomePublishAdapter? = null

    init {
        initView(layoutView)
    }

    fun initView(layoutView: View) {
//        list.add(PublishData(1, context.getString(R.string.home_publish_acts),R.drawable.icon_home_publish_acts))
//        list.add(PublishData(2, context.getString(R.string.home_publish_answer),R.drawable.icon_home_answer))
        list.add(PublishData(
                3,
                context.getString(R.string.home_publish_scan),
                R.drawable.icon_home_scan
            )
        )
        homeViewPagerAdapter = HomePublishAdapter(list)
        val recyclerView = layoutView.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = homeViewPagerAdapter
        homeViewPagerAdapter?.setOnItemClickListener { adapter, view, position ->
            callback.onResult(ResultData(1, list[position]))
        }
        getData()
    }



    fun getData(){
        lifecycleOwner.launchWithCatch {
            val body = HashMap<String, Any>()
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .getIndexPerms(body.header(rkey), body.body(rkey))
                .onSuccess {
                    if (it != null) {
                        changPerm(data = it)
                    }
                }.onWithMsgFailure {

                }.onFailure {
//                    val updateUiState = UpdateUiState<String>(false, it)
//                    bannerLiveData.postValue(updateUiState)
                }
        }
    }


    fun changPerm(data: List<String>) {
        try {
            data.forEach { d ->
                when (d) {
                    acts -> {
                        list.add(
                            PublishData(
                                1,
                                context.getString(R.string.home_publish_acts),
                                R.drawable.icon_home_publish_acts
                            )
                        )
                    }
                    questiton -> {
                        list.add(
                            PublishData(
                                2,
                                context.getString(R.string.home_publish_answer),
                                R.drawable.icon_home_answer
                            )
                        )
                    }
                    else -> {

                    }
                }
            }
            homeViewPagerAdapter?.notifyDataSetChanged()
        }catch (e:Exception){
            e.printStackTrace()
        }

    }


}