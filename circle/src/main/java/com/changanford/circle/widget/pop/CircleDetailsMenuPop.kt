package com.changanford.circle.widget.pop

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import com.changanford.circle.R
import com.changanford.circle.adapter.CircleMainMenuAdapter
import com.changanford.circle.bean.CircleMainMenuBean
import com.changanford.circle.databinding.PopCircleDetailsMenuBinding
import com.changanford.circle.databinding.PopCircleMainMenuBinding
import com.changanford.common.basic.adapter.OnRecyclerViewItemClickListener
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.SPUtils
import com.luck.picture.lib.config.PictureSelectionConfig.listener
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.Direction
import razerdp.util.animation.TranslationConfig

/**
 *Author lcw
 *Time on 2021/9/24
 *Purpose
 */
class CircleDetailsMenuPop(private val context: Context) :
    BasePopupWindow(context) {

    private var binding: PopCircleDetailsMenuBinding =
        DataBindingUtil.bind(createPopupById(R.layout.pop_circle_details_menu))!!

    init {
        contentView = binding.root
        popupGravity = Gravity.BOTTOM or Gravity.END
    }

    private val adapter by lazy {
        CircleMainMenuAdapter(context)
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

    fun initData() {
        val param = SPUtils.getParam(context, "identityType", "").toString()
        val list = arrayListOf(
            CircleMainMenuBean(content = "发长帖"),
            CircleMainMenuBean(content = "发长帖"),
            CircleMainMenuBean(content = "发长帖"),
            CircleMainMenuBean(content = "发长帖"),
        )
//        if(param!="TECHNICIAN"){
//
//        }
        adapter.setItems(list)
        binding.ryManagement.adapter = adapter

        adapter.setOnItemClickListener(object : OnRecyclerViewItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                startARouter(ARouterCirclePath.CreateNoticeActivity)
                dismiss()
            }

        })
    }

}