package com.changanford.circle.ui.release.widget

import android.content.Context
import android.view.Gravity
import com.changanford.circle.databinding.DialogActivitytypeBinding
import com.changanford.circle.utils.setDialogParams
import com.changanford.common.basic.BaseDialog

class ActivityTypeDialog(context: Context, var checked: (Int) -> Unit) :
    BaseDialog<DialogActivitytypeBinding>(context) {
    var value = 0;//0：线上，1：线下
    fun setDefault(value: Int): ActivityTypeDialog {
        this.value = value
        return this
    }

    override fun initView() {
        setDialogParams(context, this, Gravity.BOTTOM)
        binding.offline.setOnClickListener {
            changeCheck(false)

        }
        binding.online.setOnClickListener {
            changeCheck(true)
        }
        binding.ok.setOnClickListener {
            if (binding.online.isChecked){
                checked(0)
            }else{
                checked(1)
            }
            dismiss()
        }
        if (value == 0) {
            changeCheck(true)
        } else {
            changeCheck(false)
        }
    }

    override fun initData() {

    }

    private fun changeCheck(online: Boolean) {
        binding.online.isChecked = online
        binding.offline.isChecked = !online
    }
}