package com.changanford.shop.ui.shoppingcart.dialog

import android.os.Bundle
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
import com.changanford.common.MyApp
import com.changanford.common.bean.BackEnumBean
import com.changanford.common.net.*
import com.changanford.common.ui.dialog.BaseDialog
import com.changanford.common.util.launchWithCatch
import com.changanford.common.utilext.createHashMap
import com.changanford.shop.R

/**
 * @Author: lcw
 * @Date: 2020/10/22
 * @Des: 举报
 */
class RefundResonDialog(private val activity: AppCompatActivity,val callMessage:CallMessage) :
    BaseDialog(activity) {

    private val adapter = MyAdapter()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        requestWindowFeature(Window.FEATURE_NO_TITLE);//需要在设置内容之前定义
//        window?.setBackgroundDrawableResource(android.R.color.transparent)

    }
    override fun getLayoutId(): Int {
        return R.layout.dialog_refund_reson
    }

    init {

        window?.setGravity(Gravity.BOTTOM)
        setParamWidthMatch()
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        findViewById<TextView>(R.id.cancel_tv).setOnClickListener { dismiss() }
        findViewById<ImageView>(R.id.back_img).setOnClickListener { dismiss() }

        adapter.setOnItemClickListener { _, _, position ->
            val item = adapter.getItem(position)
            callMessage.message(item)
            this.dismiss()
        }
        getRefundReson()
//        getTipOffReason()
    }

    inner class MyAdapter : BaseQuickAdapter<BackEnumBean, BaseViewHolder>(R.layout.item_refund_reson) {

        override fun convert(holder: BaseViewHolder, item: BackEnumBean) {
            holder.setText(R.id.content_tv, item.message)
        }

    }


    private fun getRefundReson() {
        // 查询
        activity.launchWithCatch {
            val bodyPostSet = MyApp.mContext.createHashMap()
            bodyPostSet["className"] = "MallRefundReasonEnum"

            val rKey = getRandomKey()
            ApiClient.createApi<NetWorkApi>()
                .dictGetEnum(bodyPostSet.header(rKey), bodyPostSet.body(rKey))
                .also {
                    adapter.setList(it.data)
                }
        }
    }

    interface  CallMessage{
        fun message(reson:BackEnumBean)
    }

}