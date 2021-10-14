package com.changanford.circle.widget.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.changanford.circle.R
import com.changanford.circle.adapter.CircleMemberDialogAdapter
import com.changanford.circle.adapter.MemberDialogAdapter
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.CircleMemberBean
import com.changanford.circle.utils.launchWithCatch
import com.changanford.common.MyApp
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.ui.dialog.BaseDialog
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast

/**
 * @Author: lcw
 * @Date: 2020/11/17
 * @Des:
 */
@SuppressLint("SetTextI18n")
class CircleMemberManageDialog(
    context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val size: Int,
    private val circleId: String,
    private val list: ArrayList<CircleMemberBean>,
    private val mSureListener: SureListener
) :
    BaseDialog(context) {

    private val mRoleAdapter by lazy {
        MemberDialogAdapter(context, size, list)
    }

    private var rlContent: RecyclerView? = null
    private var rlRoleRecycler: RecyclerView? = null
    private var tvCancel: TextView? = null
    private var tvConfirm: TextView? = null
    private var tvAllNum: TextView? = null

    override fun getLayoutId() = R.layout.member_manage_set_pop


    private val mAdapter by lazy {
        CircleMemberDialogAdapter(context)
    }

    init {
        window?.setGravity(Gravity.BOTTOM)
        setParamWidthMatch()
        initView()
        rlContent?.adapter = mAdapter
        mAdapter.setItems(list)
        mAdapter.notifyDataSetChanged()
        tvCancel?.setOnClickListener {
            dismiss()
        }
        tvConfirm?.setOnClickListener {
            val newList = ArrayList<CircleMemberBean>()
            var circleStarRoleId = ""
            list.forEachIndexed { _, cirBean ->
                mRoleAdapter.getItems()?.forEach { bean ->
                    if (bean.isCheck) {
                        circleStarRoleId = bean.circleStarRoleId
                        if (bean.starName != cirBean.starOrderNumStr) {
                            newList.add(cirBean)
                        }
                    }
                }
            }
            if (circleStarRoleId.isNotEmpty()) {
                mSureListener.sure(circleStarRoleId, newList)
                dismiss()
            }
        }
        if (list.size == 1) {
            tvAllNum?.text = "当前设置"
        } else {
            tvAllNum?.text = "等${list.size}人"
        }

        getCircleUsers()
    }

    private fun initView() {
        rlContent = findViewById(R.id.rl_content)
        rlRoleRecycler = findViewById(R.id.rl_role_recycler)
        tvCancel = findViewById(R.id.tv_cancel)
        tvConfirm = findViewById(R.id.tv_confirm)
        tvAllNum = findViewById(R.id.tv_all_num)
    }

    private fun getCircleUsers() {
        lifecycleOwner.launchWithCatch {
            val body = MyApp.mContext.createHashMap()
            body["circleId"] = circleId

            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .getStarsRole(body.header(rKey), body.body(rKey)).also {
                    if (it.code == 0) {
                        mRoleAdapter.setItems(it.data)
                        rlRoleRecycler?.adapter = mRoleAdapter
                    } else {
                        it.msg.toast()
                    }
                }
        }
    }

    interface SureListener {
        fun sure(circleStarRoleId: String, list: ArrayList<CircleMemberBean>)
    }
}