package com.changanford.evos.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.changanford.common.util.ext.setOnFastClickListener
import com.changanford.common.utilext.GlideUtils
import com.changanford.evos.R
import me.majiajie.pagerbottomtabstrip.internal.RoundMessageView
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem

/**
 * Created by mjj on 2017/6/3
 * 注意：此文件只能使用在首页底部导航栏，因为修改了底部布局，适用于
 */
class SpecialJsonTab @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseTabItem(
    context!!, attrs, defStyleAttr
) {
    private val mIcon: LottieAnimationView
    private val mTitle: TextView
    private val mMessages: RoundMessageView
    private val mMessage: ImageView
    private var mDefaultDrawable: Bitmap? = null
    private var mCheckedDrawable: String? = null
    private val mDefaultBitmap: Bitmap? = null
    private var mCheckedBitmap: Bitmap? = null
    private var myuanshu = 0x56000001
    private var mDefaultTextColor = 0x56000000
    private var mCheckedTextColor = getContext().resources.getColor(R.color.black)
    var mChecked = false
    override fun setOnClickListener(l: OnClickListener?) {
        val view = getChildAt(0)
        view?.setOnClickListener(l)
    }

    fun setmsgGone() {
        mMessages.visibility = GONE
        mMessage.visibility = GONE
    }

    fun setmsgVisible() {
        mMessage.visibility = VISIBLE
    }

    /**
     * 方便初始化的方法
     *
     * @param drawableRes        默认状态的图标
     * @param checkedDrawableRes 选中状态的图标
     * @param title              标题
     */
    fun initialize(drawableRes: Bitmap?, checkedDrawableRes: String?, title: String?) {
        mDefaultDrawable = drawableRes
        mCheckedDrawable = checkedDrawableRes
        mTitle.text = title
    }

    /**
     * 方便初始化的方法
     *
     * @param mDefaultBitmap 默认状态的图标
     * @param mCheckedBitmap 选中状态的图标
     * @param title          标题
     */
    fun initialize(mDefaultBitmap: Bitmap?, mCheckedBitmap: Bitmap?, title: String?) {
        this.mCheckedBitmap = mDefaultBitmap
        this.mCheckedBitmap = mCheckedBitmap
        mTitle.text = title
    }

    override fun setChecked(checked: Boolean) {
        if (checked) {
            try {
                mIcon.setAnimationFromUrl(GlideUtils.handleImgUrl(mCheckedDrawable));
                mIcon.playAnimation();
                mIcon.setRepeatCount(0);
            } catch (exception: IllegalStateException) {
                mIcon.setImageResource(R.mipmap.image_h_one_default)
            }
//            mIcon.setColorFilter(Color.parseColor("#1700f4"))
            mTitle.setTextColor(mCheckedTextColor)
        } else {
//            mIcon.load(GlideUtils.handleImgUrl(mDefaultDrawable))
            mIcon.setImageBitmap(mDefaultDrawable)
            //            mIcon.setImageResource(mDefaultDrawable);
//            mIcon.setColorFilter(Color.parseColor("#9FA5B0"))
            mTitle.setTextColor(mDefaultTextColor)
        }
        mChecked = checked
    }

    override fun setMessageNumber(number: Int) {
        mMessages.messageNumber = number
    }

    override fun setHasMessage(hasMessage: Boolean) {
        mMessages.setHasMessage(hasMessage)
    }

    override fun setTitle(title: String) {}
    override fun setDefaultDrawable(drawable: Drawable) {}
    override fun setSelectedDrawable(drawable: Drawable) {}
    override fun getTitle(): String {
        return mTitle.text.toString()
    }

    fun setTextDefaultColor(color: Int) {
        mDefaultTextColor = color
    }

    fun setTextDefaultColor(color: String?) {
        mDefaultTextColor = Color.parseColor(color)
    }

    fun setTextCheckedColor(color: Int) {
        mCheckedTextColor = color
    }

    fun setIvyuanshu(color: Int) {
        myuanshu = color
    }

    fun setTextCheckedColor(color: String?) {
        mCheckedTextColor = Color.parseColor(color)
    }

    var jl = 0f

    init {
        LayoutInflater.from(context).inflate(R.layout.special_json_tab, this, true)
        mIcon = findViewById<View>(R.id.icon) as LottieAnimationView
        mTitle = findViewById<View>(R.id.title) as TextView
        mMessages = findViewById<View>(R.id.messages) as RoundMessageView
        mMessage = findViewById(R.id.icon_msg)
        mMessage.setOnFastClickListener {  }
    }
}