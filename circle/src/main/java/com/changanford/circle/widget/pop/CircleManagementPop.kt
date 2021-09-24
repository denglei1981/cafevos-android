package com.changanford.circle.widget.pop

import android.content.Context
import android.view.View
import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import com.changanford.circle.R
import com.changanford.circle.adapter.PopManagementAdapter
import com.changanford.circle.databinding.PopCircleManagementBinding
import com.changanford.common.basic.adapter.OnRecyclerViewItemClickListener
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.Direction
import razerdp.util.animation.ScaleConfig
import razerdp.util.animation.TranslationConfig

/**
 *Author lcw
 *Time on 2021/9/23
 *Purpose 圈子详情申请管理pop
 */
class CircleManagementPop(context: Context,private val listener:ClickListener) : BasePopupWindow(context) {

    private lateinit var binding: PopCircleManagementBinding

    private val adapter by lazy {
        PopManagementAdapter(context)
    }

    override fun onCreateContentView(): View {
        binding = DataBindingUtil.bind(createPopupById(R.layout.pop_circle_management))!!
        return binding.root
    }

    override fun onCreateShowAnimation(): Animation {
        return AnimationHelper.asAnimation()
            .withTranslation(
                TranslationConfig()
                    .from(Direction.TOP)
            )
            .toShow()
    }

    override fun onCreateDismissAnimation(): Animation {
        return AnimationHelper.asAnimation()
            .withScale(ScaleConfig.BOTTOM_TO_TOP)
            .toDismiss()
    }


    fun setData(list: ArrayList<String>) {
        adapter.setItems(list)
        binding.ryManagement.adapter = adapter
        adapter.setOnItemClickListener(object :OnRecyclerViewItemClickListener{
            override fun onItemClick(view: View?, position: Int) {
                adapter.getItem(position)?.let { listener.checkPosition(it) }
                dismiss()
            }

        })
    }

    interface ClickListener {
        fun checkPosition(bean: String)
    }
}