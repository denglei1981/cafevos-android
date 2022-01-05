package com.changanford.circle.ui.fragment.circle

import android.os.Bundle
import com.changanford.circle.BuildConfig
import com.changanford.circle.adapter.circle.YouLikeAdapter
import com.changanford.circle.databinding.FragmentYoulikeBinding
import com.changanford.circle.viewmodel.circle.NewCircleViewModel
import com.changanford.common.basic.BaseFragment

/**
 * @Author : wenke
 * @Time : 2022/1/5
 * @Description : 猜你喜欢
 */
class YouLikeFragment:BaseFragment<FragmentYoulikeBinding, NewCircleViewModel>() {
    companion object{
        fun newInstance(itemId:String): YouLikeFragment {
            val bundle = Bundle()
            bundle.putString("typeId", itemId)
            val fragment= YouLikeFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
    private val myAdapter by lazy { YouLikeAdapter() }
    override fun initView() {
        binding.recyclerView.adapter=myAdapter
    }

    override fun initData() {
        if(BuildConfig.DEBUG){
            val dataList=ArrayList<String>()
            for (i in 0..3){
                dataList.add("$i")
            }
            myAdapter.setList(dataList)
        }
    }
}