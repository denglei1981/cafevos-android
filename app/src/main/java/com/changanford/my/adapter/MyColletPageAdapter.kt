package com.changanford.my.adapter

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.*
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.CommonUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.SpannableStringUtils
import com.changanford.evos.R
import com.changanford.evos.databinding.ItemHomePageBinding
import com.changanford.my.activity.MyJoinCircleActivity
import com.changanford.my.activity.MyJoinTopicActivity
import com.changanford.my.activity.MyStarPostsActivity
import com.changanford.my.fragment.MyCollectFragment


class MyColletPageAdapter :
    BaseQuickAdapter<MyCollectBean, BaseDataBindingHolder<ItemHomePageBinding>>(R.layout.item_home_page) {
    var userIds = ""
    lateinit var activity: Activity
    lateinit var myFragment: MyCollectFragment
    val myColletNewsAdapter: MyColletNewsAdapter by lazy {
        MyColletNewsAdapter()
    }
    val myColletPostAdapter: MyColletPostAdapter by lazy {
        MyColletPostAdapter()
    }
    val myColletAccAdapter: MyColletAccAdapter by lazy {
        MyColletAccAdapter()
    }
    val myColletShopAdapter: MyColletShopAdapter by lazy {
        MyColletShopAdapter()
    }

    override fun convert(
        holder: BaseDataBindingHolder<ItemHomePageBinding>,
        item: MyCollectBean
    ) {
        holder.dataBinding?.let { t ->
            when (item.type) {
                0 -> { //资讯
                    t.tvTitle.text = "资讯"
                    if (item.infoList != null && item.infoList!!.dataList != null && item.infoList!!.dataList?.size!! > 0) {
                        t.rvMenu.adapter = myColletNewsAdapter
                        myColletNewsAdapter.setNewInstance(item.infoList!!.dataList as MutableList<InfoDataBean>?)
                        val lin =object :GridLayoutManager(activity, 3){
                            override fun canScrollHorizontally(): Boolean {
                                return false
                            }

                            override fun canScrollVertically(): Boolean {
                                return false
                            }
                        }
                        t.rvMenu.layoutManager = lin
                        t.rvMenu.visibility = View.VISIBLE
                        t.llEmpty.visibility = View.GONE
                        t.tvMore.visibility = View.VISIBLE
//                        val str = "资讯 ${item.infoList!!.total}"
//
//                        t.tvTitle.text =
//                            SpannableStringUtils.getSizeColor(str, "#999999", 14, 2, str.length)

                        myColletNewsAdapter.setOnItemClickListener { adapter, view, position ->
                            myFragment.isOut=true
                            val item = myColletNewsAdapter.getItem(position)
                            JumpUtils.instans?.jump(2, item.artId)
                        }
                        t.tvMore.setOnClickListener {
                            myFragment.isOut=true
                            JumpUtils.instans?.jump(27, "0")
                        }
                    } else {

                        t.rvMenu.visibility = View.GONE
                        t.llEmpty.visibility = View.VISIBLE
                        t.tvMore.visibility = View.GONE

                    }
                }
                1 -> {//帖子
                    t.tvTitle.text = "帖子"
                    if (item.postList != null && item.postList!!.dataList.size > 0) {
                        t.rvMenu.adapter = myColletPostAdapter
                        val lin =object :GridLayoutManager(activity, 3){
                            override fun canScrollHorizontally(): Boolean {
                                return false
                            }

                            override fun canScrollVertically(): Boolean {
                                return false
                            }
                        }
                        t.rvMenu.layoutManager = lin
                        myColletPostAdapter.setNewInstance(item.postList!!.dataList)
                        t.rvMenu.visibility = View.VISIBLE
                        t.llEmpty.visibility = View.GONE
                        t.tvMore.visibility = View.VISIBLE
//                        val str = "帖子 ${item.postList!!.total}"
//                        t.tvTitle.text =
//                            SpannableStringUtils.getSizeColor(str, "#999999", 14, 2, str.length)
                        myColletPostAdapter.setOnItemClickListener { adapter, view, position ->
                            myFragment.isOut=true
                            val bundle = Bundle()
                            bundle.putString(
                                "postsId",
                                myColletPostAdapter.getItem(position).postsId.toString()
                            )
                            startARouter(ARouterCirclePath.PostDetailsActivity, bundle)
                        }
                        t.tvMore.setOnClickListener {
                            myFragment.isOut=true
                            JumpUtils.instans?.jump(27, "1")
                        }
                    } else {

                        t.rvMenu.visibility = View.GONE
                        t.llEmpty.visibility = View.VISIBLE
                        t.tvMore.visibility = View.GONE
                    }
                }
                2 -> {//活动
                    t.tvTitle.text = "活动"
                    if (item.actDataBean != null && item.actDataBean!!.dataList != null && item.actDataBean!!.dataList?.size!! > 0) {
                        t.rvMenu.adapter = myColletAccAdapter
                        myColletAccAdapter.setNewInstance(item.actDataBean!!.dataList as MutableList<ActDataBean>?)
                        val lin =object :GridLayoutManager(activity, 3){
                            override fun canScrollHorizontally(): Boolean {
                                return false
                            }

                            override fun canScrollVertically(): Boolean {
                                return false
                            }
                        }
                        t.rvMenu.layoutManager = lin
                        t.rvMenu.visibility = View.VISIBLE
                        t.llEmpty.visibility = View.GONE
                        t.tvMore.visibility = View.VISIBLE
                        t.tvMore.setOnClickListener {
                            myFragment.isOut=true
                            JumpUtils.instans?.jump(27, "2")
                        }
//                        val str = "活动 ${item.actDataBean!!.total}"
//                        t.tvTitle.text =
//                            SpannableStringUtils.getSizeColor(str, "#999999", 14, 2, str.length)
                        myColletAccAdapter.setOnItemClickListener { adapter, view, position ->
                         myFragment.isOut=true
                            val item = myColletAccAdapter.getItem(position)
                            CommonUtils.jumpActDetail(item.jumpType, item.jumpVal)
                        }
                    } else {

                        t.rvMenu.visibility = View.GONE
                        t.llEmpty.visibility = View.VISIBLE
                        t.tvMore.visibility = View.GONE
                    }
                }
                3 -> {//商品
                    t.tvTitle.text = "商品"
                    if (item.shopList != null && item.shopList!!.dataList != null && item.shopList!!.dataList?.isNotEmpty()!!) {
                        t.rvMenu.adapter = myColletShopAdapter
                        myColletShopAdapter.setNewInstance(item.shopList!!.dataList as MutableList<MyShopBean>?)
                        val lin =object :GridLayoutManager(activity, 3){
                            override fun canScrollHorizontally(): Boolean {
                                return false
                            }

                            override fun canScrollVertically(): Boolean {
                                return false
                            }
                        }
                        t.rvMenu.layoutManager = lin
                        t.rvMenu.visibility = View.VISIBLE
                        t.llEmpty.visibility = View.GONE
                        t.tvMore.visibility = View.VISIBLE
//                        val str = "商品 ${item.shopList!!.total}"
//                        t.tvTitle.text =
//                            SpannableStringUtils.getSizeColor(str, "#999999", 14, 2, str.length)
                        t.tvMore.setOnClickListener {
                            myFragment.isOut=true
                            JumpUtils.instans?.jump(27, "3")
                        }
                        myColletShopAdapter.setOnItemClickListener { adapter, view, position ->
                            myFragment.isOut=true
                            val item = myColletShopAdapter.getItem(position)
                            JumpUtils.instans?.jump(3, item.mallMallSpuId)
                        }
                    } else {

                        t.rvMenu.visibility = View.GONE
                        t.llEmpty.visibility = View.VISIBLE
                        t.tvMore.visibility = View.GONE
                    }
                }
            }

        }
    }


}