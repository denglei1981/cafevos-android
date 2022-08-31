package com.changanford.circle.ui.activity.toupiao

import android.content.Intent
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSON
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.view.TimePickerView
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.chad.library.adapter.base.listener.OnItemSwipeListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ActivityToupiaoBinding
import com.changanford.circle.ui.activity.baoming.ActivityFabuBaoming
import com.changanford.circle.ui.activity.baoming.BaoMingViewModel
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.*
import com.changanford.common.constant.IntentKey
import com.changanford.common.helper.OSSHelper
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.room.PostDatabase
import com.changanford.common.room.PostEntity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterCommonPath
import com.changanford.common.router.startARouter
import com.changanford.common.ui.dialog.AlertThreeFilletDialog
import com.changanford.common.ui.dialog.BottomSelectDialog
import com.changanford.common.ui.dialog.SelectPicDialog
import com.changanford.common.util.AppUtils
import com.changanford.common.util.PictureUtil
import com.changanford.common.util.PictureUtils
import com.changanford.common.util.TimeUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.toast
import com.google.gson.Gson
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.luck.picture.lib.tools.ToastUtils
import com.scwang.smart.refresh.layout.util.SmartUtil
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@Route(path = ARouterCirclePath.ActivityFabuToupiao)
class ActivityFabuToupiao : BaseActivity<ActivityToupiaoBinding, BaoMingViewModel>() {
    var fengmianurl = ""
    lateinit var dragAdapter: FabuToupiaoDragAdapter
    val TAG = "TOUPIAO"
    var circleId = ""
    var position = -1
    var voteBean: VoteBean = VoteBean()
    var draftBean: PostEntity? = null
    var updateVoteReq: UpdateVoteReq? = null

    override fun initView() {
        binding.title.barTvTitle.text = "发布投票活动"
        AppUtils.setStatusBarPaddingTop(binding.title.commTitleBar, this)
        binding.title.barImgBack.setOnClickListener {
            caogao()
        }
        circleId = intent.getStringExtra(IntentKey.CREATE_NOTICE_CIRCLE_ID) ?: ""
        voteBean.circleId = circleId
        voteBean.voteType = "TEXT"
        clickInit()
        liveDataInit()
        initAdapter()
    }

    override fun onBackPressed() {
        caogao()
    }

    private val insertPostId by lazy {
        System.currentTimeMillis()
    }

    fun caogao() {
        if (updateVoteReq != null) {
            AlertThreeFilletDialog(BaseApplication.curActivity).builder()
                .setMsg(
                    "您正在编辑活动，是否确认离开"
                )
                .setCancelable(true)
                .setNegativeButton("放弃编辑", R.color.color_7174) {
                    finish()
                }
                .setPositiveButton("继续编辑", R.color.color_01025C) {

                }.show()
        } else {
            voteBean.apply {
                if (title.isNullOrEmpty() && coverImg.isNullOrEmpty() && endTime.isNullOrEmpty() && voteDesc.isNullOrEmpty()) {
                    var finishImmediately = true
                    optionList.forEach {
                        if (!it.optionDesc.isNullOrEmpty() || !it.optionImg.isNullOrEmpty()) {
                            finishImmediately = false
                            return@forEach
                        }
                    }
                    if (finishImmediately) {
                        finish()
                        return
                    }
                }
            }
            BottomSelectDialog(this, {
                voteBean.coverImg = fengmianurl
                var title = binding.etBiaoti.text.toString()
                voteBean.title = title
                voteBean.voteDesc = binding.etShuoming.text.toString()
                voteBean.allowMultipleChoice = if (binding.multeorsignle.isChecked) "YES" else "NO"
                voteBean.allowViewResult = if (binding.mcb.isChecked) "YES" else "NO"
                var voteDB = PostEntity(
                    postsId = draftBean?.postsId ?: insertPostId,
                    type = "6",
                    creattime = System.currentTimeMillis().toString(),
                    toupiao = JSON.toJSONString(voteBean)
                )
                lifecycleScope.launch(Dispatchers.IO) {
                    PostDatabase.getInstance(MyApp.mContext).getPostDao()
                        .insert(voteDB)
                    withContext(Dispatchers.Main) {
                        finish()
                    }
                }
            }) {
                finish()
            }.show()
        }
    }

    private fun initAdapter() {
        dragAdapter = FabuToupiaoDragAdapter()
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.recycler.setLayoutManager(layoutManager)
        dragAdapter.draggableModule.isSwipeEnabled = false
        dragAdapter.draggableModule.isDragEnabled = true
        dragAdapter.draggableModule.setOnItemDragListener(onItemDragListener)
        dragAdapter.draggableModule.setOnItemSwipeListener(onItemSwipeListener)
        dragAdapter.draggableModule.itemTouchHelperCallback.setSwipeMoveFlags(
            ItemTouchHelper.START or ItemTouchHelper.END
        )
        binding.recycler.setAdapter(dragAdapter)
        dragAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.iv_img) {
                openxiangche(position)
            } else if (view.id == R.id.iv_delete) {
                if (dragAdapter.data.size == 2) {
                    return@setOnItemChildClickListener
                }
                dragAdapter.remove(position)
                voteBean.optionList.removeAt(position)
            } else if (view.id == R.id.iv_del) {
//                dragAdapter.getItem(position).setBdoptionImgUrl("")
//                dragAdapter.getItem(position).setOptionImgUrl("")
//                dragAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun openxiangche(position: Int) {
        this.position = position
        PictureUtils.openGarlly(
            this@ActivityFabuToupiao,
            1,
            object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: List<LocalMedia?>?) {
                    val bean = result?.get(0)
                    val path = bean?.let { it1 -> PictureUtil.getFinallyPath(it1) }
                    path?.let { it1 ->
                        OSSHelper.init(this@ActivityFabuToupiao)
                            .getOSSToImage(
                                this@ActivityFabuToupiao,
                                it1,
                                object : OSSHelper.OSSImageListener {
                                    override fun getPicUrl(url: String) {
                                        lifecycleScope.launch(Dispatchers.Main) {
                                            voteBean.optionList[position].optionImg = url
                                            dragAdapter.notifyItemChanged(position)
                                        }
                                    }
                                })
                    }
                }

                override fun onCancel() {}
            },
            400,
            400
        )
    }

    var onItemDragListener: OnItemDragListener = object : OnItemDragListener {
        override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder, pos: Int) {
//            Log.d(TAG, "drag start");
//            final BaseViewHolder holder = ((BaseViewHolder) viewHolder);
//
//            // 开始时，item背景色变化，demo这里使用了一个动画渐变，使得自然
//            int startColor = Color.WHITE;
//            int endColor = Color.rgb(245, 245, 245);
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                ValueAnimator v = ValueAnimator.ofArgb(startColor, endColor);
//                v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                    @Override
//                    public void onAnimationUpdate(ValueAnimator animation) {
//                        holder.itemView.setBackgroundColor((int)animation.getAnimatedValue());
//                    }
//                });
//                v.setDuration(300);
//                v.start();
//            }
        }

        override fun onItemDragMoving(
            source: RecyclerView.ViewHolder,
            from: Int,
            target: RecyclerView.ViewHolder,
            to: Int
        ) {
        }

        override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder, pos: Int) {
//            Log.d(TAG, "drag end");
//            final BaseViewHolder holder = ((BaseViewHolder) viewHolder);
//            // 结束时，item背景色变化，demo这里使用了一个动画渐变，使得自然
//            int startColor = Color.rgb(245, 245, 245);
//            int endColor = Color.WHITE;
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                ValueAnimator v = ValueAnimator.ofArgb(startColor, endColor);
//                v.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                    @Override
//                    public void onAnimationUpdate(ValueAnimator animation) {
//                        holder.itemView.setBackgroundColor((int)animation.getAnimatedValue());
//                    }
//                });
//                v.setDuration(300);
//                v.start();
//            }
        }
    }

    // 侧滑监听
    var onItemSwipeListener: OnItemSwipeListener = object : OnItemSwipeListener {
        override fun onItemSwipeStart(viewHolder: RecyclerView.ViewHolder, pos: Int) {
            Log.d(TAG, "view swiped start: $pos")
            val holder = viewHolder as BaseViewHolder
        }

        override fun clearView(viewHolder: RecyclerView.ViewHolder, pos: Int) {
            Log.d(TAG, "View reset: $pos")
            val holder = viewHolder as BaseViewHolder
        }

        override fun onItemSwiped(viewHolder: RecyclerView.ViewHolder, pos: Int) {
            Log.d(TAG, "View Swiped: $pos")
        }

        override fun onItemSwipeMoving(
            canvas: Canvas,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            isCurrentlyActive: Boolean
        ) {
            canvas.drawColor(ContextCompat.getColor(this@ActivityFabuToupiao, R.color.white))
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== RESULT_OK && requestCode == UCrop.REQUEST_CROP){
            val resultUri = data?.let { UCrop.getOutput(it) }
            var cutPath = resultUri?.path
            cutPath?.let { it1 ->
                OSSHelper.init(this@ActivityFabuToupiao)
                    .getOSSToImage(
                        this@ActivityFabuToupiao,
                        it1,
                        object : OSSHelper.OSSImageListener {
                            override fun getPicUrl(url: String) {
                                showFengMian(url)
                            }
                        })
            }
        }
    }
    private fun liveDataInit() {
        LiveDataBus.get().with(LiveDataBusKey.FORD_ALBUM_RESULT).observe(this) {
            if (position == -1) {
                var conten = DtoBeanNew.ContentImg(it as String,"")
                var list = ArrayList<DtoBeanNew.ContentImg>()
                list.add(conten)
                viewModel.downGlideImg(list){
                    PictureUtil.startUCrop(
                        this,
                        PictureUtil.getFinallyPath(it),
                        UCrop.REQUEST_CROP,
                        16f,
                        9f
                    )
                }
            } else {
                voteBean.optionList[position].optionImg = it as String
                dragAdapter.notifyItemChanged(position)
            }
        }
        binding.etShuoming.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                binding.tvShuomingnum.text = "${p0?.length}/500"
            }

        })
        binding.etBiaoti.addTextChangedListener {
            binding.tvNum.text = "${it?.length}/20"
        }
    }

    private fun showFengMian(s: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            fengmianurl = s
            GlideUtils.loadRoundLocal(GlideUtils.handleImgUrl(fengmianurl), binding.ivFengmian, 5f);
            binding.tvFm.isVisible = true
            binding.tvFmHint.isVisible = false
        }
    }

    private fun clickInit() {
        binding.ivFengmian.setOnClickListener {
            position = -1
            SelectPicDialog(this, object : SelectPicDialog.ChoosePicListener {
                override fun chooseByPhone() {
                    PictureUtils.openGarlly(
                        this@ActivityFabuToupiao,
                        1,
                        object : OnResultCallbackListener<LocalMedia?> {
                            override fun onResult(result: List<LocalMedia?>?) {
                                val bean = result?.get(0)
                                val path = bean?.let { it1 -> PictureUtil.getFinallyPath(it1) }
                                path?.let { it1 ->
                                    OSSHelper.init(this@ActivityFabuToupiao)
                                        .getOSSToImage(
                                            this@ActivityFabuToupiao,
                                            it1,
                                            object : OSSHelper.OSSImageListener {
                                                override fun getPicUrl(url: String) {
                                                    showFengMian(url)
                                                }
                                            })
                                }
                            }

                            override fun onCancel() {}
                        },
                        670,
                        400
                    )
                }

                override fun chooseByDefault() {
                    startARouter(ARouterCommonPath.FordAlbumActivity)
                }

            }).show()
        }
        binding.rlActtime.setOnClickListener {
            setTimePicker()
        }
        binding.togwenzi.setOnClickListener {
            changeStyle(1)
        }
        binding.togtuwen.setOnClickListener {
            changeStyle(2)
        }
        binding.llAdd.setOnClickListener {
            if (voteBean.optionList.size < 20) {
                var voteoption = VoteOptionBean("")
                voteBean.optionList.add(voteoption)
                dragAdapter.addData(voteBean.optionList.size-1,voteoption)
            }
        }
        binding.question.setOnClickListener {
            AlertThreeFilletDialog(BaseApplication.curActivity).builder()
                .setTitle("查看投票明细")
                .setMsg(
                    "开启按钮，用户投票后可查看每一个选项具体投票的人员列表；关闭按钮，仅活动发起人可查看人员列表"
                )
                .setCancelable(true)
                .setPositiveButton("我知道了", R.color.color_01025C) {

                }.show()
        }
        binding.nickSave.setOnClickListener {
            if (fengmianurl.isNullOrEmpty()) {
                "请选择封面".toast()
                return@setOnClickListener
            }
            voteBean.coverImg = fengmianurl
            var title = binding.etBiaoti.text.toString()
            if (title.isNullOrEmpty()) {
                "请输入标题".toast()
                return@setOnClickListener
            }
            voteBean.title = title

            if (voteBean.endTime.isNullOrEmpty()) {
                "请选择时间".toast()
                return@setOnClickListener
            }
            voteBean.voteDesc = binding.etShuoming.text.toString()
            voteBean.optionList.forEach {
                if (it.optionDesc.isNullOrEmpty()){
                    "请输入选项内容".toast()
                    return@setOnClickListener
                }
                if (voteBean.voteType == "IMG" && it.optionImg.isNullOrEmpty()){
                    "请选择选项图片".toast()
                    return@setOnClickListener
                }
            }

            voteBean.allowMultipleChoice = if (binding.multeorsignle.isChecked) "YES" else "NO"
            voteBean.allowViewResult = if (binding.mcb.isChecked) "YES" else "NO"
            if (updateVoteReq == null) {
                viewModel.AddVote(voteBean) {
                    it.onSuccess {
                        "发布成功".toast()
                        if (draftBean != null) {
                            lifecycleScope.launch(Dispatchers.IO) {
                                draftBean?.postsId?.let { it1 ->
                                    PostDatabase.getInstance(MyApp.mContext).getPostDao()
                                        .delete(it1)
                                }
                                withContext(Dispatchers.Main) {
                                    finish()
                                }
                            }
                        } else {
                            finish()
                        }
                    }.onWithMsgFailure {
                        it?.toast()
                    }
                }
            } else {
                viewModel.updateVote(updateVoteReq?.wonderfulId ?: 0, voteBean) {
                    it.onSuccess {
                        "修改成功".toast()
                        if (draftBean != null) {
                            lifecycleScope.launch(Dispatchers.IO) {
                                draftBean?.postsId?.let { it1 ->
                                    PostDatabase.getInstance(MyApp.mContext).getPostDao()
                                        .delete(it1)
                                }
                                withContext(Dispatchers.Main) {
                                    finish()
                                }
                            }
                        } else {
                            finish()
                        }
                    }.onWithMsgFailure {
                        it?.toast()
                    }
                }
            }
        }

    }

    var list: ArrayList<VoteOptionBean> = ArrayList<VoteOptionBean>()

    override fun initData() {
        draftBean = intent.getSerializableExtra("postEntity") as PostEntity?
        updateVoteReq = intent.getSerializableExtra("voteBean") as UpdateVoteReq?
        if (draftBean != null) {
            voteBean = Gson().fromJson(draftBean?.toupiao, VoteBean::class.java)
            showDefaultData()
        } else if (updateVoteReq != null) {
            updateVoteReq?.addVoteDto?.let {
                voteBean = it
                try {
                    voteBean.beginTimeShow = TimeUtils.MillisToStrO(voteBean.beginTime.toLong())
                    voteBean.beginTime = TimeUtils.MillisToStr1(voteBean.beginTime.toLong())
                    voteBean.endTimeShow = TimeUtils.MillisToStrO(voteBean.endTime.toLong())
                    voteBean.endTime = TimeUtils.MillisToStr1(voteBean.endTime.toLong())
                }catch (e:Exception){
                    e.printStackTrace()
                }
                showDefaultData()
            }
        } else {
            list?.add(VoteOptionBean(""))
            list?.add(VoteOptionBean(""))
            voteBean.optionList.addAll(list)
            dragAdapter.addData(list!!)
        }

    }

    fun showDefaultData() {
        dragAdapter.addData(voteBean.optionList)
        binding.apply {
            showFengMian(voteBean.coverImg)
            etBiaoti.setText(voteBean.title)
            tvTime.text =
                "${voteBean.beginTimeShow}-${voteBean.endTimeShow}"
            etShuoming.setText(voteBean.voteDesc)
            multeorsignle.isChecked = voteBean.allowMultipleChoice == "YES"
            mcb.isChecked = voteBean.allowViewResult == "YES"
        }
    }


    fun changeStyle(int: Int) {
        when (int) {
            1 -> {
                binding.togwenzi.setTextColor(resources.getColor(R.color.color_33))
                binding.togtuwen.setTextColor(resources.getColor(R.color.color_cc))
                binding.togwenzi.background = resources.getDrawable(R.drawable.bg_white_12)
                binding.togtuwen.background = resources.getDrawable(R.drawable.bg_f6_cor14)
                dragAdapter.setStyle(false)
                voteBean.voteType = "TEXT"
            }
            2 -> {
                binding.togwenzi.setTextColor(resources.getColor(R.color.color_cc))
                binding.togtuwen.setTextColor(resources.getColor(R.color.color_33))
                binding.togwenzi.background = resources.getDrawable(R.drawable.bg_f6_cor14)
                binding.togtuwen.background = resources.getDrawable(R.drawable.bg_white_12)
                dragAdapter.setStyle(true)
                voteBean.voteType = "IMG"
            }
        }
    }


    private var pvActTime: TimePickerView? = null
    private var pvActEndTime: TimePickerView? = null
    var timebegin: Date = Date(System.currentTimeMillis())

    fun setTimePicker() {
        hideKeyboard(binding.tvTime.windowToken)
        initTimePick1()
        initTimePickEND()
        pvActTime?.show()
    }

    /**
     * 选择活动时间
     */
    private fun initTimePick1() {
        //时间选择器
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
        endDate[2099, 11] = 31
        //正确设置方式 原因：注意事项有说明
        if (
            pvActTime == null
        ) {
            pvActTime = TimePickerBuilder(
                this
            ) { date, v ->
                voteBean.beginTime = TimeUtils.MillisToStr1(date.time)
                voteBean.beginTimeShow = TimeUtils.MillisToStrO(date.time)
                timebegin = date
                pvActEndTime?.show()
            }
                .setCancelText("取消") //取消按钮文字
                .setSubmitText("确定") //确认按钮文字
                .setTitleText("开始时间")
                .setTitleSize(SmartUtil.dp2px(6f)) //标题文字大小
                .setOutSideCancelable(true) //点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true) //是否循环滚动
                .setSubmitColor(resources.getColor(R.color.black)) //确定按钮文字颜色
                .setCancelColor(resources.getColor(R.color.textgray)) //取消按钮文字颜色
                .setTitleBgColor(resources.getColor(R.color.color_withe)) //标题背景颜色 Night mode
                .setBgColor(android.graphics.Color.WHITE) //滚轮背景颜色 Night mode
                .setType(booleanArrayOf(true, true, true, true, true, false))
                .setRangDate(startDate, endDate) //起始终止年月日设定
                .setLabel("年", "月", "日", "时", "分", "") //默认设置为年月日时分秒
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)
                .build()
        }
    }

    /**
     * 选择活动时间
     */
    private fun initTimePickEND() {
        //时间选择器
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
        endDate[2099, 11] = 31
        //正确设置方式 原因：注意事项有说明
        if (pvActEndTime == null) {
            pvActEndTime = TimePickerBuilder(
                this
            ) { date, v ->
                if (timebegin.time > date.time) {
                    ToastUtils.s(
                        BaseApplication.INSTANT.applicationContext,
                        "结束时间不能小于开始时间"
                    )
                    pvActTime!!.show()
                } else {
                    voteBean.endTime = TimeUtils.MillisToStr1(date.time)
                    voteBean.endTimeShow = TimeUtils.MillisToStrO(date.time)
                    binding.tvTime.text =
                        "${voteBean.beginTimeShow}-${voteBean.endTimeShow}"
                }
            }
                .setCancelText("取消") //取消按钮文字
                .setSubmitText("确定") //确认按钮文字
                .setTitleText("结束时间")
                .setTitleSize(SmartUtil.dp2px(6f)) //标题文字大小
                .setOutSideCancelable(true) //点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true) //是否循环滚动
                .setSubmitColor(resources.getColor(R.color.black)) //确定按钮文字颜色
                .setCancelColor(resources.getColor(R.color.textgray)) //取消按钮文字颜色
                .setTitleBgColor(resources.getColor(R.color.color_withe)) //标题背景颜色 Night mode
                .setBgColor(android.graphics.Color.WHITE) //滚轮背景颜色 Night mode
                .setType(booleanArrayOf(true, true, true, true, true, false))
                .setRangDate(startDate, endDate) //起始终止年月日设定
                .setLabel("年", "月", "日", "时", "分", "") //默认设置为年月日时分秒
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)
                .build()
        }
    }
}