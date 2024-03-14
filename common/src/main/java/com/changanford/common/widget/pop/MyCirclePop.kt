package com.changanford.common.widget.pop

import android.content.Context
import android.view.Gravity
import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import com.changanford.common.R
import com.changanford.common.adapter.MyCirclePopAdapter
import com.changanford.common.bean.NewCircleBean
import com.changanford.common.databinding.PopMyCircleBinding
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.Direction
import razerdp.util.animation.TranslationConfig

/**
 * @author: niubobo
 * @date: 2024/3/13
 * @description：
 */
class MyCirclePop(private val context: Context) : BasePopupWindow(context) {

    private var binding: PopMyCircleBinding =
        DataBindingUtil.bind(createPopupById(R.layout.pop_my_circle))!!

    private val adapter by lazy { MyCirclePopAdapter() }

    init {
        contentView = binding.root
        popupGravity = Gravity.BOTTOM or Gravity.END
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
            .withTranslation(TranslationConfig.TO_TOP)
            .toDismiss()
    }

    fun initPopData(list: ArrayList<NewCircleBean>, circleId: String?) {
        binding.ryCircle.adapter = adapter
        list.forEachIndexed { index, s ->
            if (circleId == s.circleId) {
                adapter.selectPosition = index
            }
        }
        adapter.setList(list)
        adapter.setOnItemClickListener { _, view, position ->
            val bean = adapter.getItem(position)
            LiveDataBus.get().with(LiveDataBusKey.HOME_CIRCLE_CHECK_ID).postValue(bean.circleId)
            dismiss()
        }
    }
}