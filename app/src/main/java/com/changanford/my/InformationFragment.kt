package com.changanford.my

import android.os.Bundle
import android.view.View
import com.changanford.common.databinding.ViewEmptyTopBinding
import com.changanford.common.manger.RouterManger
import com.changanford.common.manger.UserManger
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.toastShow
import com.changanford.home.R
import com.changanford.home.news.adapter.NewsListAdapter
import com.changanford.my.databinding.FragmentInfomationBinding
import com.changanford.my.viewmodel.ActViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 *  文件名：InformationFragment
 *  创建者: zcy
 *  创建日期：2021/9/26 18:12
 *  描述: TODO
 *  修改描述：TODO
 */
class InformationFragment : BaseMineFM<FragmentInfomationBinding, ActViewModel>() {
    var type: String = ""
    var userId: String = ""

    var searchKeys: String = ""

    private val infoAdapter: NewsListAdapter by lazy {
        NewsListAdapter(this)
    }

    companion object {
        fun newInstance(value: String, userId: String = ""): InformationFragment {
            var bundle: Bundle = Bundle()
            bundle.putString(RouterManger.KEY_TO_OBJ, value)
            bundle.putString(RouterManger.KEY_TO_ID, userId)
            var medalFragment = InformationFragment()
            medalFragment.arguments = bundle
            return medalFragment
        }
    }

    var isRefresh: Boolean = false


    override fun onPause() {
        super.onPause()
        isRefresh = true
    }

    override fun onResume() {
        super.onResume()
        if (isRefresh) {
            isRefresh = false
            initRefreshData(1)
        }
    }

    override fun initView() {
        arguments?.getString(RouterManger.KEY_TO_OBJ)?.let {
            type = it
            if (it == "footInformation") {
                infoAdapter.type = "我的足迹-资讯"
            } else if (it == "collectInformation") {
                infoAdapter.type = "我的收藏-资讯"
            }
        }
        userId = UserManger.getSysUserInfo()?.uid ?: ""
        arguments?.getString(RouterManger.KEY_TO_ID)?.let {
            userId = it
        }
        if (type == "centerInformation") {
            infoAdapter.isShowFollow = false
        }
        binding.rcyAct.rcyCommonView.adapter = infoAdapter

        infoAdapter.setOnItemChildClickListener { adapter, view, position ->
            val item = infoAdapter.getItem(position)
            when (view.id) {
                R.id.iv_header, R.id.tv_author_name, R.id.tv_sub_title -> {// 去用户主页？
                    if (infoAdapter.isManage) {
                        item.isCheck = !item.isCheck
                        infoAdapter.notifyItemChanged(position)
                        infoAdapter.checkIsAllCheck()
                        return@setOnItemChildClickListener
                    }
                    JumpUtils.instans!!.jump(35, item.authors?.authorId)
                }

                R.id.layout_content, R.id.tv_time_look_count, R.id.tv_comment_count -> {// 去资讯详情。
                    if (infoAdapter.isManage) {
                        item.isCheck = !item.isCheck
                        infoAdapter.notifyItemChanged(position)
                        infoAdapter.checkIsAllCheck()
                        return@setOnItemChildClickListener
                    }
                    if (item.authors != null) {
//                        var newsValueData = NewsValueData(item.artId, item.type)
//                        var values = Gson().toJson(newsValueData)
//                        JumpUtils.instans?.jump(2, values)
                        JumpUtils.instans?.jump(2, item.artId)
                    } else {
                        toastShow("没有作者")
                    }
                }
            }
        }
        LiveDataBus.get().withs<Boolean>(LiveDataBusKey.REFRESH_INFORMATION_FRAGMENT)
            .observe(this) {
                infoAdapter.isManage = it
                infoAdapter.notifyDataSetChanged()
            }
        LiveDataBus.get().withs<Boolean>(LiveDataBusKey.REFRESH_INFORMATION_DATA).observe(this) {
            infoAdapter.data.forEach { data ->
                data.isCheck = it
            }
            infoAdapter.notifyDataSetChanged()
        }
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyAct.smartCommonLayout
    }

    override fun showEmpty(): View? {
        return when (type) {
            "centerInformation" -> {
                ViewEmptyTopBinding.inflate(layoutInflater).root
            }

            else -> {
                super.showEmpty()
            }
        }
    }

    fun myCollectInfo(pageSize: Int) {
        var total: Int = 0
        viewModel.queryMineCollectInfo(pageSize, searchKeys) { reponse ->
            LiveDataBus.get().with(LiveDataBusKey.BUS_SHOW_LOAD_CONTENT).postValue(true)
            reponse?.data?.total?.let {
                total = it
            }
            completeRefresh(reponse.data?.dataList, infoAdapter, total)
        }
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        var total: Int = 0

        when (type) {
            "collectInformation" -> {
                infoAdapter.isShowTag = true
                myCollectInfo(pageSize)
            }

            "footInformation" -> {
                infoAdapter.isShowTag = true
                viewModel.queryMineFootInfo(pageSize) { reponse ->
                    LiveDataBus.get().with(LiveDataBusKey.BUS_SHOW_LOAD_CONTENT).postValue(true)
                    reponse?.data?.total?.let {
                        total = it
                    }
                    completeRefresh(reponse?.data?.dataList, infoAdapter, total)
                    if (pageSize>1){
                        infoAdapter.checkIsAllCheck()
                    }
                }
            }

            "centerInformation" -> {
                viewModel.queryMineSendInfoList(userId, pageSize) { reponse ->
                    reponse?.data?.total?.let {
                        total = it
                    }
                    completeRefresh(reponse?.data?.dataList, infoAdapter, total)
                }
            }
        }
    }
}