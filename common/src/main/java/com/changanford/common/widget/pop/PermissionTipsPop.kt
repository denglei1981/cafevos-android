package com.changanford.common.widget.pop

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import com.changanford.common.R
import com.changanford.common.basic.adapter.OnRecyclerViewItemClickListener
import com.changanford.common.databinding.DialogPermissionTipsBinding
import com.changanford.common.util.SPUtils
import com.changanford.common.util.gio.updateMainGio
import com.luck.picture.lib.config.PictureSelectionConfig.listener
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.Direction
import razerdp.util.animation.TranslationConfig

/**
 *Author lcw
 *Time on 2023/12/6
 *Purpose
 */
class PermissionTipsPop(private val context: Context) : BasePopupWindow(context) {
    private var binding: DialogPermissionTipsBinding =
        DataBindingUtil.bind(createPopupById(R.layout.dialog_permission_tips))!!

    init {
        contentView = binding.root
        popupGravity = Gravity.BOTTOM or Gravity.END
    }

    fun setContent(content: String) {
        binding.tvContent.text = content
    }

    fun setTitle(title: String) {
        binding.tvTitle.text = title
    }
}