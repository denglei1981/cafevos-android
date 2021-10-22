package com.changanford.circle.widget.dialog

import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
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
 * @Date: 2021/10/22
 * @Des: 不喜欢
 */
class DislikeDialog(private val activity: AppCompatActivity, private val body: ReportDislikeBody?) :
    BaseDialog(activity) {

    private val adapter = MyAdapter()
    private val configBean = MutableLiveData<ArrayList<DislikeBean>>()
    private var selectSize = 0

    override fun getLayoutId(): Int {
        return R.layout.dialog_dislike
    }

    init {
        window?.setGravity(Gravity.BOTTOM)
        setParamWidthMatch()
        val titleTv = findViewById<TextView>(R.id.chose_title_tv)
        val btTv = findViewById<TextView>(R.id.bt_tv)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener { _, _, position ->
            val dislikeBean = adapter.getItem(position)
            dislikeBean.isSelect = !dislikeBean.isSelect
            if (dislikeBean.isSelect) {
                selectSize++
            } else {
                selectSize--
            }
            titleTv.text = if (selectSize > 0) "已选择 $selectSize 个理由" else "请选择不喜欢理由"
            btTv.isSelected = selectSize > 0
            btTv.text = if (btTv.isSelected) "提交" else {
                "直接屏蔽"
            }
            adapter.notifyItemChanged(position)
        }
        findViewById<TextView>(R.id.cancel_tv).setOnClickListener { dismiss() }
        getDislikeReason()
        configBean.observe(activity, {
            adapter.setList(it)
        })
        btTv.setOnClickListener {
            if (btTv.isSelected) {//提交
                var str = ""
                for (bean in configBean.value!!) {
                    if (bean.isSelect) str += "," + bean.content
                }
                if (str.isNotEmpty()) {
                    str = str.substring(1)
                }
                dislikeApi(str)
            } else {//直接屏蔽
                dislikeApi("")
            }
        }
    }

    inner class MyAdapter :
        BaseQuickAdapter<DislikeBean, BaseViewHolder>(R.layout.item_dialog_dislike) {

        override fun convert(holder: BaseViewHolder, item: DislikeBean) {
            holder.getView<TextView>(R.id.content_tv).isSelected = item.isSelect
            holder.setText(R.id.content_tv, item.content)
        }

    }

    data class DislikeBean(var isSelect: Boolean, val content: String)

    private fun dislikeApi(dislikeStr: String) {
        //类型 1 资讯 2 帖子 3 活动
        activity.launchWithCatch {
            val bodyPostSet = MyApp.mContext.createHashMap()
            bodyPostSet["type"] = 2
            bodyPostSet["linkId"] = body?.linkId ?: ""
            bodyPostSet["reason"] = dislikeStr
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .dislikePost(bodyPostSet.header(rKey), bodyPostSet.body(rKey))
                .also {
                    "提交成功".toast()
                    dismiss()
                    LiveDataBus.get().with(CircleLiveBusKey.DELETE_CIRCLE_POST)
                        .postValue(false)
                }
        }

    }

    override fun show() {
        if (configBean.value == null)
            getDislikeReason()
        super.show()
    }

    /**
     *不喜欢原因
     */
    private fun getDislikeReason() {
        activity.launchWithCatch {
            val bodyPostSet = MyApp.mContext.createHashMap()

            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .getDislikeReason(bodyPostSet.header(rKey), bodyPostSet.body(rKey))
                .also {
                    val list = ArrayList<DislikeBean>()
                    it.data?.forEach { str ->
                        list.add(DislikeBean(false, str))
                    }
                    configBean.value = list
                }
        }

    }
}