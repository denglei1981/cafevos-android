package com.changanford.my.adapter

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.*
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.SpannableStringUtils
import com.changanford.evos.R
import com.changanford.evos.databinding.ItemHomePageBinding
import com.changanford.my.activity.MyJoinCircleActivity
import com.changanford.my.activity.MyJoinTopicActivity
import com.changanford.my.activity.MyStarPostsActivity


class MyHomePageAdapter :
    BaseQuickAdapter<HomePageBean, BaseDataBindingHolder<ItemHomePageBinding>>(R.layout.item_home_page) {
    var userIds = ""
    lateinit var activity: Activity
    val myJoinCircleAdapter: MyJoinCircleAdapter by lazy {
        MyJoinCircleAdapter()
    }

    val myStarAdapter: MyStarPostAdapter by lazy {
        MyStarPostAdapter()
    }

    val myJoinTopicAdapter: MyJoinTopicAdapter by lazy {
        MyJoinTopicAdapter()
    }

    override fun convert(
        holder: BaseDataBindingHolder<ItemHomePageBinding>,
        item: HomePageBean
    ) {
        holder.dataBinding?.let { t ->
            when (item.type) {
                0 -> { //圈子
                    if (item.circleList != null && item.circleList!!.dataList != null && item.circleList!!.dataList!!.size > 0) {
                        val linearLayoutManager =object : LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false) {
                            override fun canScrollVertically(): Boolean {
                                return false
                            }

                            override fun canScrollHorizontally(): Boolean {
                                return false
                            }
                        }
                        t.rvMenu.layoutManager = linearLayoutManager
                        t.rvMenu.adapter = myJoinCircleAdapter
                        myJoinCircleAdapter.setNewInstance(item.circleList!!.dataList)
                        t.rvMenu.visibility = View.VISIBLE
                        t.llEmpty.visibility = View.GONE
                        t.tvMore.visibility = View.VISIBLE
                        val str="加入的圈子 ${item.circleList!!.total}"
                        t.tvTitle.text =    SpannableStringUtils.getSizeColor(str,"#999999",14,5,str.length)
                        myJoinCircleAdapter.setOnItemClickListener { adapter, view, position ->
                            JumpUtils.instans?.jump(
                                6,
                                myJoinCircleAdapter.getItem(position).circleId.toString()
                            )
                        }
                        t.tvMore.setOnClickListener {
                            MyJoinCircleActivity.start(userId = userIds, activity)
                        }
                    } else {
                        t.tvTitle.text = "加入的圈子"
                        t.rvMenu.visibility = View.GONE
                        t.llEmpty.visibility = View.VISIBLE
                        t.tvMore.visibility = View.GONE
                    }
                }
                1 -> {//话题
                    if (item.topicList != null && item.topicList!!.dataList != null && item.topicList!!.dataList!!.size > 0) {
                        val linearLayoutManager =object : LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false) {
                            override fun canScrollVertically(): Boolean {
                                return false
                            }

                            override fun canScrollHorizontally(): Boolean {
                                return false
                            }
                        }
                        t.rvMenu.layoutManager = linearLayoutManager
                        t.rvMenu.adapter = myJoinTopicAdapter

                        myJoinTopicAdapter.setNewInstance(item.topicList!!.dataList)
                        t.rvMenu.visibility = View.VISIBLE
                        t.llEmpty.visibility = View.GONE
                        t.tvMore.visibility = View.VISIBLE
                        val str="参与的话题 ${item.topicList!!.total}"
                        t.tvTitle.text =    SpannableStringUtils.getSizeColor(str,"#999999",14,5,str.length)
                        t.tvMore.setOnClickListener {
                            MyJoinTopicActivity.start(userIds, activity)
                        }
                        myJoinTopicAdapter.setOnItemClickListener { adapter, view, position ->
                            val item = myJoinTopicAdapter.getItem(position)
                            val bundle = Bundle()
                            bundle.putString("topicId", item.topicId.toString())
                            startARouter(ARouterCirclePath.TopicDetailsActivity, bundle)
                        }
                    } else {
                        t.tvTitle.text = "参与的话题"
                        t.rvMenu.visibility = View.GONE
                        t.llEmpty.visibility = View.VISIBLE
                        t.tvMore.visibility = View.GONE
                    }
                }
                2 -> {//帖子
                    if (item.postList != null && item.postList!!.dataList != null && item.postList!!.dataList!!.size > 0) {
                        val linearLayoutManager =object : LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false) {
                            override fun canScrollVertically(): Boolean {
                                return false
                            }

                            override fun canScrollHorizontally(): Boolean {
                                return false
                            }
                        }
                        t.rvMenu.layoutManager = linearLayoutManager
                        t.rvMenu.adapter = myStarAdapter
                        myStarAdapter.setNewInstance(item.postList!!.dataList)
                        t.rvMenu.visibility = View.VISIBLE
                        t.llEmpty.visibility = View.GONE
                        t.tvMore.visibility = View.VISIBLE
                        val str="点赞的帖子 ${item.postList!!.total}"
                        t.tvTitle.text =    SpannableStringUtils.getSizeColor(str,"#999999",14,5,str.length)
                        myStarAdapter.setOnItemClickListener { adapter, view, position ->
                            val bundle = Bundle()
                            bundle.putString(
                                "postsId",
                                myStarAdapter.getItem(position).postsId.toString()
                            )
                            startARouter(ARouterCirclePath.PostDetailsActivity, bundle)
                        }
                        t.tvMore.setOnClickListener {
                            MyStarPostsActivity.start(userIds, activity)
                        }
                    } else {
                        t.tvTitle.text = "点赞的帖子"
                        t.rvMenu.visibility = View.GONE
                        t.llEmpty.visibility = View.VISIBLE
                        t.tvMore.visibility = View.GONE
                    }
                }

            }

        }
    }


}