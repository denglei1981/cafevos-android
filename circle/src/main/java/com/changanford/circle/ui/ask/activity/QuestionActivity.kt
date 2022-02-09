package com.changanford.circle.ui.ask.activity

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.media.ExifInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSON
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.adapter.PostPicAdapter
import com.changanford.circle.bean.ButtomTypeBean
import com.changanford.circle.databinding.ActivityQuestionBinding
import com.changanford.circle.ui.activity.PostActivity
import com.changanford.circle.ui.ask.adapter.AskPicAdapter
import com.changanford.circle.ui.ask.pop.QuestionTipsPop
import com.changanford.circle.ui.ask.request.QuestionViewModel
import com.changanford.circle.widget.pop.CircleMainMenuPop
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.CancelReasonBeanItem
import com.changanford.common.bean.ImageUrlBean
import com.changanford.common.bean.QuestionData
import com.changanford.common.bean.STSBean
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.ui.dialog.LoadDialog
import com.changanford.common.util.AliYunOssUploadOrDownFileConfig
import com.changanford.common.util.PictureUtil
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.common.utilext.logD
import com.changanford.common.utilext.logE
import com.changanford.common.utilext.toast
import com.gyf.immersionbar.ImmersionBar
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.luck.picture.lib.tools.ScreenUtils
import com.yalantis.ucrop.UCrop

@Route(path = ARouterCirclePath.QuestionActivity)
class QuestionActivity : BaseActivity<ActivityQuestionBinding, QuestionViewModel>() {


    lateinit var postPicAdapter: AskPicAdapter
    private var selectList = ArrayList<LocalMedia>()
    private var type = 0
    private val upedimgs = ArrayList<ImageUrlBean>()  //上传之后的图片集合地址
    private var params = hashMapOf<String, Any>()
    private var nomalwith = 500;
    private val dialog by lazy {
        LoadDialog(this).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            setLoadingText("图片上传中..")
            show()
        }
    }

    override fun initView() {
        StatusBarUtil.setStatusBarMarginTop(binding.layoutTitle.conTitle, this)
        binding.layoutTitle.tvTitle.text = "提问"
        binding.layoutTitle.barTvOther.text = "发布"
        binding.layoutTitle.barTvOther.visibility = View.VISIBLE
        binding.layoutTitle.barTvOther.background =
            resources.getDrawable(R.drawable.question_btn_can_release)
        binding.layoutTitle.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.ivQuestion.setOnClickListener {
            showMenuPop()
        }
        binding.layoutTitle.barTvOther.setOnClickListener {
            isPublish()
        }
    }

    override fun initData() {
        initPicAdapter()
        viewModel.getQuestionType()
        viewModel.getFordReward()
        viewModel.getInitQuestion()

    }

    fun initPicAdapter() {
        postPicAdapter = AskPicAdapter(type)
        postPicAdapter.draggableModule.isDragEnabled = true
        binding.rvImg.adapter = postPicAdapter
        postPicAdapter.setList(selectList)

        postPicAdapter.setOnItemClickListener { adapter, view, position ->
            val holder = binding.rvImg.findViewHolderForLayoutPosition(position)
            if (holder != null && holder.itemViewType == 0x9843) {//添加
                "actionbarheight--${ImmersionBar.getActionBarHeight(this)}".logD()
                "NavigationBarHeight--${ImmersionBar.getNavigationBarHeight(this)}".logD()
                "ScreenHeight--${ScreenUtils.getScreenHeight(this)}".logD()

                PictureUtil.openGallery(
                    this,
                    selectList,
                    object : OnResultCallbackListener<LocalMedia> {
                        override fun onResult(result: MutableList<LocalMedia>?) {
                            if (result != null) {
                                selectList.clear()
                                selectList.addAll(result)
                            }
                            val bundle = Bundle()
                            bundle.putParcelableArrayList("picList", selectList)
                            bundle.putInt("position", 0)
                            bundle.putInt("showEditType", -1)
                            startARouter(ARouterCirclePath.PictureeditlActivity, bundle)

                        }

                        override fun onCancel() {

                        }

                    })
            } else {
                val bundle = Bundle()
                bundle.putParcelableArrayList("picList", selectList)
                bundle.putInt("position", position)
                bundle.putInt("showEditType", -1)
                startARouter(ARouterCirclePath.PictureeditlActivity, bundle)
            }
        }
        postPicAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.iv_delete) {
                selectList.remove(postPicAdapter.getItem(position))
                postPicAdapter.remove(postPicAdapter.getItem(position))
                postPicAdapter.notifyDataSetChanged()

            }
        }
        postPicAdapter.draggableModule.setOnItemDragListener(object : OnItemDragListener {
            override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {

                "drag start".logD()
                val holder = viewHolder as BaseViewHolder
                // 开始时，item背景色变化，demo这里使用了一个动画渐变，使得自然
                val startColor = Color.WHITE
                val endColor = Color.rgb(245, 245, 245)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val v = ValueAnimator.ofArgb(startColor, endColor)
                    v.addUpdateListener { animation -> holder.itemView.setBackgroundColor(animation.animatedValue as Int) }
                    v.duration = 300
                    v.start()
                }
                holder.itemView.alpha = 0.7f
            }

            override fun onItemDragMoving(
                source: RecyclerView.ViewHolder?,
                from: Int,
                target: RecyclerView.ViewHolder?,
                to: Int
            ) {
                """"move from: " + source.getAdapterPosition() + " to: " + target.getAdapterPosition() """.logD()
            }

            override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                "drag end".logD()
                val holder = viewHolder as BaseViewHolder
                // 结束时，item背景色变化，demo这里使用了一个动画渐变，使得自然
                val startColor = Color.rgb(245, 245, 245)
                val endColor = Color.WHITE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val v = ValueAnimator.ofArgb(startColor, endColor)
                    v.addUpdateListener { animation -> holder.itemView.setBackgroundColor(animation.animatedValue as Int) }
                    v.duration = 300
                    v.start()
                }
                holder.itemView.alpha = 1f
                postPicAdapter.notifyDataSetChanged()
            }

        })
    }

    override fun observe() {
        super.observe()
        viewModel.questTypeList.observe(this, Observer {
            showQuestionType(it)
        })
        viewModel.fordRewardList.observe(this, Observer {
            showFordType(it)

        })

        LiveDataBus.get().with(LiveDataBusKey.PICTURESEDITED).observe(this, Observer {

            selectList.clear()
            selectList.addAll(it as Collection<LocalMedia>)
            postPicAdapter.setList(selectList)
        })

        viewModel.stsBean.observe(this, Observer {
            it?.let {
                upedimgs.clear()
                uploadImgs(it, 0, dialog)
            }
        })
        viewModel.createQuestionLiveData.observe(this, Observer {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
            if ("upsuccess".equals(it)) {
                "发布成功".toast()
                //TODO 通知刷新。
                this.finish()
            }
        })

    }

    fun showFordType(datas: ArrayList<QuestionData>) {
        binding.labelsReward.setLabels(
            datas
        ) { label, position, data ->
            label?.let {
                it.text = data?.dictLabel
            }
            data?.dictLabel
        }
    }

    fun showQuestionType(datas: ArrayList<QuestionData>) {
        binding.labelsType.setLabels(
            datas
        ) { label, position, data ->
            label?.let {
                it.text = data?.dictLabel
            }
            data?.dictLabel
        }
    }


    private fun showMenuPop() {
        QuestionTipsPop(
            this,
            object : QuestionTipsPop.CheckPostType {
                override fun checkLongBar() {

                }

                override fun checkPic() {

                }

                override fun checkVideo() {

                }

            }).run {
            setBlurBackgroundEnable(false)
            setOverlayMask(false)
            showPopupWindow(binding.ivQuestion)
            initData()
        }
    }

    private fun isPublish() {
        val biaoti = binding.etQuestionTitle.text.toString()
        val content = binding.etQuestion.text.toString()
        val labelsReaward = binding.labelsReward.getSelectLabelDatas<QuestionData>()
        val questionTypes = binding.labelsType.getSelectLabelDatas<QuestionData>()
        when {
            selectList.size == 0 -> {
                "请选择图片".toast()
                return
            }
            biaoti.isNullOrEmpty() || biaoti.isEmpty() || biaoti.length > 20 -> {
                "请输入5-20字的标题".toast()
                return
            }
            content.isNullOrEmpty() -> {
                "请输入正文内容".toast()
            }
            questionTypes.isEmpty() || questionTypes.size <= 0 -> {
                "请选择问题类型".toast()
            }
            labelsReaward.isEmpty() || labelsReaward.size <= 0 -> {
                "请选择打赏福币".toast()
            }
            else -> {
                params["content"] = content
                params["title"] = biaoti
                params["fbReward"] = labelsReaward[0].dictValue.toInt()
                params["questionType"] = questionTypes[0].dictValue
                if (selectList.size == 0) {
                    viewModel.createQuestion(params)
                } else if (selectList.size > 0) {
                    viewModel.getOSS()
                }
            }
        }

    }


    private fun uploadImgs(stsBean: STSBean, index: Int, dialog: LoadDialog) {
        AliYunOssUploadOrDownFileConfig.getInstance(this).initOss(
            stsBean.endpoint, stsBean.accessKeyId,
            stsBean.accessKeySecret, stsBean.securityToken
        )

        val media = selectList[index]
        val ytPath = PictureUtil.getFinallyPath(media)
        Log.d("=============", "${ytPath}")
        var type = ytPath.substring(ytPath.lastIndexOf(".") + 1, ytPath.length)
        var exifInterface = ExifInterface(ytPath);
        var rotation = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        );
        val path =
            stsBean.tempFilePath + System.currentTimeMillis() + "androidios${
                if (media.isCut) {
                    if (rotation == ExifInterface.ORIENTATION_ROTATE_90 || rotation == ExifInterface.ORIENTATION_ROTATE_270) {
                        exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 500)
                    } else {
                        exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 500)
                    }
                } else {
                    if (media.width == 0) {
                        nomalwith
                    } else {
                        media.width
                    }
                }

            }_${
                if (media.isCut) {
                    if (rotation == ExifInterface.ORIENTATION_ROTATE_90 || rotation == ExifInterface.ORIENTATION_ROTATE_270) {
                        exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 500)
                    } else {
                        exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 500)
                    }
                } else {
                    if (media.height == 0) {
                        nomalwith
                    } else {
                        media.height
                    }
                }
            }." + type

        AliYunOssUploadOrDownFileConfig.getInstance(this)
            .uploadFile(stsBean.bucketName, path, ytPath, "", 0)
        AliYunOssUploadOrDownFileConfig.getInstance(this).setOnUploadFile(object :
            AliYunOssUploadOrDownFileConfig.OnUploadFile {
            override fun onUploadFileSuccess(info: String) {

                upedimgs.add(ImageUrlBean(path, ""))
                val scount = index + 1
                runOnUiThread {
                    dialog.setTvprogress("${scount}/${selectList.size}")
                }
                ("上传了几张图呢").plus(scount).logE()
                if (scount == selectList.size) {
                    var imgUrls = ""
                    upedimgs.forEach {
                        imgUrls += it.imgUrl + ","
                    }
                    params["imgUrls"] = imgUrls
//                    params["pics"] = upedimgs[0]?.imgUrl
                    JSON.toJSONString(params).logE()
                    addPost(dialog)
                    return
                }

                uploadImgs(stsBean, scount, dialog)
            }

            override fun onUploadFileFailed(errCode: String) {
                errCode.toast()

                dialog.dismiss()
            }

            override fun onuploadFileprogress(
                request: PutObjectRequest,
                currentSize: Long,
                totalSize: Long
            ) {
            }
        })
    }


    fun addPost(dialog: LoadDialog) {
        viewModel.createQuestion(params)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                UCrop.REQUEST_CROP -> {
                    val resultUri = UCrop.getOutput(data!!)
                    selectList[0].isCut = true
                    selectList[0].cutPath = resultUri?.path
                    postPicAdapter.setList(selectList)
                    postPicAdapter.notifyDataSetChanged()
                }
                UCrop.RESULT_ERROR -> {
                    val cropError = UCrop.getError(data!!)
                }
            }
        }

    }
}