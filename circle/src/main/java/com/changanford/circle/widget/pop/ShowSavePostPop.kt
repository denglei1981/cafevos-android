package com.changanford.circle.widget.pop

import android.content.Context
import android.view.Gravity
import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import com.changanford.circle.R
import com.changanford.circle.databinding.PopCircleDetailsBinding
import com.changanford.circle.databinding.SavepostBinding
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.Direction
import razerdp.util.animation.ScaleConfig
import razerdp.util.animation.TranslationConfig
import com.google.gson.reflect.TypeToken





class ShowSavePostPop(val context: Context,val postBackListener:PostBackListener)
    :BasePopupWindow(context) {
    private var binding: SavepostBinding =
        DataBindingUtil.bind(createPopupById(R.layout.savepost))!!
    init {
        contentView = binding.root
        popupGravity = Gravity.CENTER
        initView()
    }


    private fun initView() {
        binding.tvSave.setOnClickListener {
            postBackListener.save()
        }
        binding.tvUnsave.setOnClickListener {
            postBackListener.unsave()
        }
        binding.con.setOnClickListener {
            dismiss()
        }


//        val ps: List<Person> = gson.fromJson(str, object : TypeToken<List<Person?>?>() {}.type)
//        for (i in ps.indices) {
//            val p: Person = ps[i]
//            System.out.println(p.toString())
//        }
    }


//    override fun onCreateShowAnimation(): Animation {
//        return AnimationHelper.asAnimation()
//            .withTranslation(
//                TranslationConfig()
//                    .from(Direction.CENTER)
//            )
//            .toShow()
//    }
//
//    override fun onCreateDismissAnimation(): Animation {
//        return AnimationHelper.asAnimation()
//            .withScale(ScaleConfig.CENTER)
//            .toDismiss()
//    }

    interface PostBackListener {
        fun save()
        fun unsave()
    }
}