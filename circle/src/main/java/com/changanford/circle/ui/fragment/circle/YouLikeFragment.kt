package com.changanford.circle.ui.fragment.circle

import android.os.Bundle
import com.changanford.circle.adapter.circle.YouLikeAdapter
import com.changanford.circle.databinding.FragmentYoulikeBinding
import com.changanford.circle.ui.activity.CircleDetailsActivity
import com.changanford.circle.viewmodel.circle.NewCircleViewModel
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.NewCircleBean
import com.changanford.common.buried.WBuriedUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * @Author : wenke
 * @Time : 2022/1/5
 * @Description : 猜你喜欢
 */
class YouLikeFragment:BaseFragment<FragmentYoulikeBinding, NewCircleViewModel>() {
    companion object{
        fun newInstance(page:Int,jsonStr:String): YouLikeFragment {
            val bundle = Bundle()
            bundle.putInt("page", page)
            bundle.putString("jsonStr", jsonStr)
            val fragment= YouLikeFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
    private val myAdapter by lazy { YouLikeAdapter() }
    override fun initView() {
        binding.recyclerView.adapter=myAdapter
        arguments?.getString("jsonStr",null)?.apply {
            val dataList: List<NewCircleBean> = Gson().fromJson(this, object : TypeToken<List<NewCircleBean?>?>() {}.type)
            myAdapter.setList(dataList)
            myAdapter.setOnItemClickListener { _, _, position ->
                myAdapter.data[position].apply {
                    WBuriedUtil.clickCircleYouLike(name)
                    CircleDetailsActivity.start(circleId)
                }
            }
        }
    }
    override fun initData() {}
}