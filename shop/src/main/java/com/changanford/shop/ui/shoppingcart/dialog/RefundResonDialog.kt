package com.changanford.shop.ui.shoppingcart.dialog

import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.MyApp
import com.changanford.common.bean.BackEnumBean
import com.changanford.common.databinding.DialogLayoutOneSelectItemBinding
import com.changanford.common.net.ApiClient
import com.changanford.common.net.NetWorkApi
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.ui.dialog.BaseDialog
import com.changanford.common.util.launchWithCatch
import com.changanford.common.utilext.createHashMap
import com.changanford.shop.R

/**
 * @Author: lcw
 * @Date: 2020/10/22
 * @Des: 举报
 */
class RefundResonDialog(private val activity: AppCompatActivity, val callMessage: CallMessage) :
    BaseDialog(activity) {

    private val adapter = MyAdapter()
    private var selectData: BackEnumBean? = null

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
        findViewById<TextView>(R.id.tv_sure).setOnClickListener {
            val selectBean = adapter.data.find { item -> item.isSelect }
            selectBean?.let { it1 -> callMessage.message(it1) }
            dismiss()
        }

//        adapter.setOnItemClickListener { _, _, position ->
//            val item = adapter.getItem(position)
//            selectData = item
//            adapter.data.forEachIndexed { index, backEnumBean ->
//                backEnumBean.isSelect = index == position
//                adapter.notifyDataSetChanged()
//            }
////            callMessage.message(item)
////            this.dismiss()
//        }
        getRefundReson()
//        getTipOffReason()
    }

    inner class MyAdapter :
        BaseQuickAdapter<BackEnumBean, BaseDataBindingHolder<DialogLayoutOneSelectItemBinding>>(R.layout.dialog_layout_one_select_item) {

        override fun convert(
            holder: BaseDataBindingHolder<DialogLayoutOneSelectItemBinding>,
            item: BackEnumBean
        ) {
            holder.dataBinding?.apply {
                checkbox.isChecked = item.isSelect
                checkbox.text = item.message
                checkbox.setOnClickListener {
                    data.forEachIndexed { index, backEnumBean ->
                        if (index == holder.layoutPosition) {
                            backEnumBean.isSelect = checkbox.isChecked
                        } else {
                            backEnumBean.isSelect = false
                        }
                    }
                    notifyDataSetChanged()
                }
            }
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

    interface CallMessage {
        fun message(reson: BackEnumBean)
    }

}