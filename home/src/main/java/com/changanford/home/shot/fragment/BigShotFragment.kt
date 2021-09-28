package com.changanford.home.shot.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.EmptyViewModel
import com.changanford.home.databinding.FragmentBigShotBinding
import com.changanford.home.news.data.SpecialData
import com.changanford.home.shot.adapter.BigShotPostListAdapter
import com.changanford.home.shot.adapter.BigShotUserListAdapter

/**
 *  大咖
 * */
class BigShotFragment : BaseFragment<FragmentBigShotBinding, EmptyViewModel>() {


    var bigShotUserListAdapter: BigShotUserListAdapter? = null


    var bigShotPostListAdapter: BigShotPostListAdapter? = null


    companion object {
        fun newInstance(): BigShotFragment {
            val fg = BigShotFragment()
            val bundle = Bundle()
            fg.arguments = bundle
            return fg
        }
    }

    override fun initView() {

        bigShotUserListAdapter = BigShotUserListAdapter().apply {
            addData(SpecialData())
            addData(SpecialData())
            addData(SpecialData())
            addData(SpecialData())
            addData(SpecialData())
            addData(SpecialData())
            addData(SpecialData())
            setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(
                    adapter: BaseQuickAdapter<*, *>,
                    view: View,
                    position: Int
                ) {

                }
            })
        }
        binding.recyclerViewH.adapter = bigShotUserListAdapter
        binding.recyclerViewH.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        bigShotPostListAdapter = BigShotPostListAdapter().apply {

            addData(SpecialData())
            addData(SpecialData())
            addData(SpecialData())
            addData(SpecialData())
            addData(SpecialData())
            addData(SpecialData())
            addData(SpecialData())
        }
        binding.recyclerViewV.adapter = bigShotPostListAdapter
        binding.recyclerViewV.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

    }

    override fun initData() {

    }
}