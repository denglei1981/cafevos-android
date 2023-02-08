package com.changanford.common.ui

import android.animation.Animator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.Nullable


/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.ui.MyView
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/8/11 10:50
 * @Description: 　
 * *********************************************************************************
 */
class MyView(context: Context?) : View(context) {
    var mProgress by Delegates.notNull<Float>()
    lateinit var mBluePaint : Paint
    lateinit var mLoadingPathMeasure:PathMeasure
    lateinit var mDisPath :Path
    lateinit var mLoadingAnimator:ValueAnimator
    init {
        setLayerType(LAYER_TYPE_SOFTWARE,null);//取消硬件加速
        mBluePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val loadingPath = Path()
        loadingPath.addCircle(100F, 100F, 60F,Path.Direction.CW)
        mLoadingPathMeasure = PathMeasure(loadingPath,false)
        mDisPath = Path()
        mLoadingAnimator = ValueAnimator.ofFloat(0F, 1F)
        mLoadingAnimator.apply {
            duration = 1500
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                mProgress = it.animatedValue as Float
                invalidate()
            }
        }
        mLoadingAnimator.start()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mDisPath.reset()
        val stop = mLoadingPathMeasure.length*mProgress
        mLoadingPathMeasure.getSegment(0F,stop,mDisPath,true)
        canvas?.drawPath(mDisPath,mBluePaint)
    }
}


class PayAnimatorView : View {
    /**
     * 当前动画的状态
     */
    private var curStatus = 0

    /**
     * loading 动画变量
     */
    private var mPathMeasure: PathMeasure? = null
    private var mDstPath: Path? = null
    private var mCurRotate = 0
    private var mProgress = 0f
    private var hasCanvasSaved = false
    private var hasCanvasRestored = false

    /**
     * success / Fail 动画变量
     */
    private var mSuccessPathMeasure: PathMeasure? = null
    private var mSuccessDstPath: Path? = null
    private var mFailPathMeasure: PathMeasure? = null
    private var mFailDstPath: Path? = null

    /**
     * 动画
     */
    private var mLoadingAnimator: ValueAnimator? = null
    private var mSuccessAnimator: ValueAnimator? = null
    private var mFailAnimator: ValueAnimator? = null
    private var mBluePaint: Paint? = null
    private var mRedPaint: Paint? = null
    private var mCenterX = 0
    private var mCenterY = 0

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, @Nullable attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, @Nullable attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCenterX = w / 2
        mCenterY = h / 2
        val radius = (Math.min(w, h) * 0.3).toInt()
        // 在获取宽高之后设置加载框的位置和大小
        val circlePath = Path()
        circlePath.addCircle(
            mCenterX.toFloat(),
            mCenterY.toFloat(),
            radius.toFloat(),
            Path.Direction.CW
        )
        mPathMeasure = PathMeasure(circlePath, true)
        mDstPath = Path()
        // 设置 success 动画的 path
        val successPath = Path()
        successPath.addCircle(
            mCenterX.toFloat(),
            mCenterY.toFloat(),
            radius.toFloat(),
            Path.Direction.CW
        )
        successPath.moveTo(mCenterX - radius * 0.5f, mCenterY - radius * 0.2f)
        successPath.lineTo(mCenterX - radius * 0.1f, mCenterY + radius * 0.4f)
        successPath.lineTo(mCenterX + radius * 0.6f, mCenterY - radius * 0.5f)
        mSuccessPathMeasure = PathMeasure(successPath, false)
        mSuccessDstPath = Path()
        // 设置 fail 动画的 path
        val failPath = Path()
        failPath.addCircle(
            mCenterX.toFloat(),
            mCenterY.toFloat(),
            radius.toFloat(),
            Path.Direction.CW
        )
        failPath.moveTo((mCenterX - radius / 3).toFloat(), (mCenterY - radius / 3).toFloat())
        failPath.lineTo((mCenterX + radius / 3).toFloat(), (mCenterY + radius / 3).toFloat())
        failPath.moveTo((mCenterX + radius / 3).toFloat(), (mCenterY - radius / 3).toFloat())
        failPath.lineTo((mCenterX - radius / 3).toFloat(), (mCenterY + radius / 3).toFloat())
        mFailPathMeasure = PathMeasure(failPath, false)
        mFailDstPath = Path()
    }

    private fun init() {
        // 取消硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        // 初始化画笔
        mBluePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBluePaint!!.color = Color.BLUE
        mBluePaint!!.style = Paint.Style.STROKE
        mBluePaint!!.strokeCap = Paint.Cap.ROUND
        mBluePaint!!.strokeWidth = 10f
        mRedPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mRedPaint!!.color = Color.RED
        mRedPaint!!.style = Paint.Style.STROKE
        mRedPaint!!.strokeCap = Paint.Cap.ROUND
        mRedPaint!!.strokeWidth = 10f

        // 初始化时, 动画为加载状态
        curStatus = STATUS_LOADING

        // 新建 Loading 动画并 start
        mLoadingAnimator = ValueAnimator.ofFloat(0f, 1f)
        mLoadingAnimator?.setDuration(2000)
        mLoadingAnimator?.addUpdateListener(AnimatorUpdateListener { animation ->
            mProgress = animation.animatedValue as Float
            invalidate()
        })
        mLoadingAnimator?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
            }

            override fun onAnimationEnd(p0: Animator) {
                if (curStatus == STATUS_SUCCESS) {
                    mSuccessAnimator!!.start()
                } else if (curStatus == STATUS_FAIL) {
                    mFailAnimator!!.start()
                }
            }

            override fun onAnimationCancel(p0: Animator) {
            }

            override fun onAnimationRepeat(p0: Animator) {
            }

        })
        mLoadingAnimator?.setInterpolator(AccelerateDecelerateInterpolator())
        mLoadingAnimator?.setRepeatCount(ValueAnimator.INFINITE)
        mLoadingAnimator?.setRepeatMode(ValueAnimator.RESTART)
        mLoadingAnimator?.start()
        // 新建 success 动画
        mSuccessAnimator = ValueAnimator.ofFloat(0f, 2f)
        mSuccessAnimator?.setDuration(1600)
        mSuccessAnimator?.addUpdateListener(AnimatorUpdateListener { animation ->
            mProgress = animation.animatedValue as Float
            invalidate()
        })
        // 新建 fail 动画
        mFailAnimator = ValueAnimator.ofFloat(0f, 3f)
        mFailAnimator?.setDuration(2100)
        mFailAnimator?.addUpdateListener(AnimatorUpdateListener { animation ->
            mProgress = animation.animatedValue as Float
            invalidate()
        })
    }

    /**
     * 将动画的状态从 Loading 变为 success 或 fail
     */
    fun setStatus(status: Int) {
        if (curStatus == STATUS_LOADING && status != STATUS_LOADING) {
            curStatus = status
            mLoadingAnimator!!.end()
        }
    }

    private var mSuccessIndex = 1
    private var mFailIndex = 1
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 在 Loading 状态下 Canvas 会被旋转, 需要在第一次进入时保存
        if (!hasCanvasSaved) {
            canvas.save()
            hasCanvasSaved = true
        }
        // 判断当前动画的状态并绘制相应动画
        if (curStatus == STATUS_LOADING) {
            mDstPath!!.reset()
            val length = mPathMeasure!!.length
            val stop = mProgress * length
            val start = (stop - (0.5 - Math.abs(mProgress - 0.5)) * length).toFloat()
            mPathMeasure!!.getSegment(start, stop, mDstPath, true)
            // 旋转画布
            mCurRotate = (mCurRotate + 2) % 360
            canvas.rotate(mCurRotate.toFloat(), mCenterX.toFloat(), mCenterY.toFloat())
            canvas.drawPath(mDstPath!!, mBluePaint!!)
        } else if (curStatus == STATUS_SUCCESS) {
            if (!hasCanvasRestored) {
                canvas.restore()
                hasCanvasRestored = true
            }
            if (mProgress < 1) {
                val stop = mSuccessPathMeasure!!.length * mProgress
                mSuccessPathMeasure!!.getSegment(0f, stop, mSuccessDstPath, true)
            } else {
                if (mSuccessIndex == 1) {
                    mSuccessIndex = 2
                    mSuccessPathMeasure!!.getSegment(
                        0f, mSuccessPathMeasure!!.length,
                        mSuccessDstPath, true
                    )
                    mSuccessPathMeasure!!.nextContour()
                }
                val stop = mSuccessPathMeasure!!.length * (mProgress - 1)
                mSuccessPathMeasure!!.getSegment(0f, stop, mSuccessDstPath, true)
            }
            canvas.drawPath(mSuccessDstPath!!, mBluePaint!!)
        } else if (curStatus == STATUS_FAIL) {
            if (!hasCanvasRestored) {
                canvas.restore()
                hasCanvasRestored = true
            }
            if (mProgress < 1) {
                val stop = mFailPathMeasure!!.length * mProgress
                mFailPathMeasure!!.getSegment(0f, stop, mFailDstPath, true)
            } else if (mProgress < 2) {
                if (mFailIndex == 1) {
                    mFailIndex = 2
                    mFailPathMeasure!!.getSegment(
                        0f, mFailPathMeasure!!.length,
                        mFailDstPath, true
                    )
                    mFailPathMeasure!!.nextContour()
                }
                val stop = mFailPathMeasure!!.length * (mProgress - 1)
                mFailPathMeasure!!.getSegment(0f, stop, mFailDstPath, true)
            } else {
                if (mFailIndex == 2) {
                    mFailIndex = 3
                    mFailPathMeasure!!.getSegment(
                        0f, mFailPathMeasure!!.length,
                        mFailDstPath, true
                    )
                    mFailPathMeasure!!.nextContour()
                }
                val stop = mFailPathMeasure!!.length * (mProgress - 2)
                mFailPathMeasure!!.getSegment(0f, stop, mFailDstPath, true)
            }
            canvas.drawPath(mFailDstPath!!, mRedPaint!!)
        }
    }

    companion object {
        /**
         * 动画状态：加载中、成功、失败
         */
        const val STATUS_LOADING = 1
        const val STATUS_SUCCESS = 2
        const val STATUS_FAIL = 3
    }
}