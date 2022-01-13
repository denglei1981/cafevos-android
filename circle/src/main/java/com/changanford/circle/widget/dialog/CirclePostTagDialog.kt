package com.changanford.circle.widget.dialog


import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.changanford.circle.R
import com.changanford.common.ui.dialog.BaseDialog

class CirclePostTagDialog(private val activity: AppCompatActivity,private val lifecycleOwner: LifecycleOwner) :BaseDialog(activity) {

    override fun getLayoutId()= R.layout.dialog_circle_post_tag

    init {
        window?.setGravity(Gravity.BOTTOM)
        setParamWidthMatch()
    }
}