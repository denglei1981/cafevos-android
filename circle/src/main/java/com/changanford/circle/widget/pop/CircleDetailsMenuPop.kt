package com.changanford.circle.widget.pop

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import com.changanford.circle.R
import com.changanford.circle.bean.CirclePermissionsData
import com.changanford.circle.databinding.PopCircleDetailsMenuBinding
import com.changanford.common.adapter.CircleMainMenuAdapter
import com.changanford.common.basic.adapter.OnRecyclerViewItemClickListener
import com.changanford.common.bean.CircleMainMenuBean
import com.changanford.common.constant.IntentKey
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.utilext.toIntPx
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.Direction
import razerdp.util.animation.TranslationConfig

/**
 *Author lcw
 *Time on 2021/9/24
 *Purpose
 */
class CircleDetailsMenuPop(
    private val context: Context,
    private val circleId: String,
    private val permissions: ArrayList<CirclePermissionsData>
) :
    BasePopupWindow(context) {

    private var binding: PopCircleDetailsMenuBinding =
        DataBindingUtil.bind(createPopupById(R.layout.pop_circle_details_menu))!!

    init {
        contentView = binding.root
        popupGravity = Gravity.BOTTOM or Gravity.END
        initView()
    }

    private val adapter by lazy {
        CircleMainMenuAdapter(context,true)
    }

    private fun initView() {
        val layoutParams = binding.llContent.layoutParams
        layoutParams.width = 100.toIntPx()
        binding.llContent.layoutParams = layoutParams
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
        val list = ArrayList<CircleMainMenuBean>()
        permissions.forEach {
            when (it.dictValue) {
                "ANNOUNCEMENT" -> {//发布公告
                    list.add(CircleMainMenuBean(1, it.dictLabel))
                }

                "PUBLISH_REGISTRATION_ACTIVITIES" -> {//报名活动
                    list.add(CircleMainMenuBean(2, it.dictLabel))
                }

                "PUBLISH_VOTING_ACTIVITIES" -> {//投票活动
                    list.add(CircleMainMenuBean(3, it.dictLabel))
                }

                "INITIATE_TOPIC" -> {//发布话题
                    list.add(CircleMainMenuBean(4, it.dictLabel))
                }

            }
        }
        adapter.setItems(list)
        binding.ryManagement.adapter = adapter

        adapter.setOnItemClickListener(object : OnRecyclerViewItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val itemBean = adapter.getItem(position)
                when (itemBean?.pic) {
                    1 -> {//发布公告
                        val bundle = Bundle()
                        bundle.putString(IntentKey.CREATE_NOTICE_CIRCLE_ID, circleId)
                        startARouter(ARouterCirclePath.CreateNoticeActivity, bundle)
                    }

                    2 -> {//报名活动
                        val bundle = Bundle()
                        bundle.putString(IntentKey.CREATE_NOTICE_CIRCLE_ID, circleId)
                        startARouter(ARouterCirclePath.ActivityFabuBaoming, bundle)
                    }

                    3 -> {//投票活动
                        val bundle = Bundle()
                        bundle.putString(IntentKey.CREATE_NOTICE_CIRCLE_ID, circleId)
                        startARouter(ARouterCirclePath.ActivityFabuToupiao, bundle)
                    }

                    4 -> {//发布话题
                        val bundle = Bundle()
                        bundle.putString(IntentKey.CREATE_NOTICE_CIRCLE_ID, circleId)
                        startARouter(ARouterCirclePath.CreateCircleTopicActivity, bundle)
                    }
                }
                dismiss()
            }

        })
    }

}