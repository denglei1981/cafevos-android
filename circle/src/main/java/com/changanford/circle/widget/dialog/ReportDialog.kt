package com.changanford.circle.widget.dialog

import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.ReportDislikeBody
import com.changanford.circle.utils.launchWithCatch
import com.changanford.common.MyApp
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.ui.dialog.BaseDialog
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast

/**
 * @Author: lcw
 * @Date: 2020/10/22
 * @Des: 举报
 */
class ReportDialog(private val activity: AppCompatActivity, private val body: ReportDislikeBody?) :
    BaseDialog(activity) {

    private val adapter = MyAdapter()

    private val configBean = MutableLiveData<ArrayList<String>>()

    override fun getLayoutId(): Int {
        return R.layout.dialog_report
    }

    init {
        window?.setGravity(Gravity.BOTTOM)
        setParamWidthMatch()
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        findViewById<TextView>(R.id.cancel_tv).setOnClickListener { dismiss() }
        findViewById<ImageView>(R.id.back_img).setOnClickListener { dismiss() }
        configBean.observe(activity, Observer {
            adapter.setList(it)
        })
        adapter.setOnItemClickListener { _, _, position ->
            reportApi(adapter.getItem(position))
        }
        getTipOffReason()
    }

    inner class MyAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_dialog_report) {

        override fun convert(holder: BaseViewHolder, item: String) {
            holder.setText(R.id.content_tv, item)
        }

    }

    override fun show() {
        if (configBean.value == null)
            getTipOffReason()
        super.show()
    }

    private fun reportApi(reportStr: String) {
        //类型 1 资讯 2 帖子 3 活动
        activity.launchWithCatch {
            val bodyPostSet = MyApp.mContext.createHashMap()
            bodyPostSet["type"] = 2
            bodyPostSet["linkId"] = body?.linkId ?: ""
            bodyPostSet["reason"] = reportStr
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .reportPost(bodyPostSet.header(rKey), bodyPostSet.body(rKey))
                .also {
                    "举报成功".toast()
                    dismiss()
                }
        }
    }

    private fun getTipOffReason() {
        activity.launchWithCatch {
            val bodyPostSet = MyApp.mContext.createHashMap()

            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .getTipOffReason(bodyPostSet.header(rKey), bodyPostSet.body(rKey))
                .also {
                    it.data?.let { list -> configBean.value = list }
                }
        }

    }
}