package com.changanford.shop.ui.sale

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.media.ExifInterface
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.ImageUrlBean
import com.changanford.common.bean.STSBean
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.ui.dialog.LoadDialog
import com.changanford.common.util.AliYunOssUploadOrDownFileConfig
import com.changanford.common.util.PictureUtil
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.logD
import com.changanford.common.utilext.logE
import com.changanford.common.utilext.toast
import com.changanford.shop.R
import com.changanford.shop.databinding.ActivityRefundLogisticsBinding
import com.changanford.shop.databinding.BaseRecyclerViewBinding
import com.changanford.shop.ui.sale.adapter.RefundApplyPicAdapter
import com.changanford.shop.ui.sale.request.RefundViewModel
import com.changanford.shop.view.TopBar
import com.gyf.immersionbar.ImmersionBar
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.luck.picture.lib.tools.ScreenUtils
import com.yalantis.ucrop.UCrop


/**
 *  退货填写物流信息
 * */
class RefundLogisticsActivity : BaseActivity<ActivityRefundLogisticsBinding, RefundViewModel>() {

    lateinit var refundApplyPicAdapter: RefundApplyPicAdapter
    private val upedimgs = ArrayList<ImageUrlBean>()  //上传之后的图片集合地址
    private var nomalwith = 500
    private var params = hashMapOf<String, Any>()
    private var type = 0
    private var selectList = ArrayList<LocalMedia>()

    private var ossImageList: MutableList<String> = mutableListOf()
    private val dialog by lazy {
        LoadDialog(this).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            setLoadingText("图片上传中..")
            show()
        }
    }

    fun initPicAdapter() {
        refundApplyPicAdapter = RefundApplyPicAdapter(type)
        refundApplyPicAdapter.draggableModule.isDragEnabled = true
        binding.llRefundNotes.rvImg.adapter = refundApplyPicAdapter
        refundApplyPicAdapter.setList(selectList)
        refundApplyPicAdapter.setOnItemClickListener { adapter, view, position ->
            val holder = binding.llRefundNotes.rvImg.findViewHolderForLayoutPosition(position)
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
                    }, maxNum = 3
                )
            } else {
                val bundle = Bundle()
                bundle.putParcelableArrayList("picList", selectList)
                bundle.putInt("position", position)
                bundle.putInt("showEditType", -1)
                startARouter(ARouterCirclePath.PictureeditlActivity, bundle)
            }
        }
        refundApplyPicAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.iv_delete) {
                selectList.remove(refundApplyPicAdapter.getItem(position))
                refundApplyPicAdapter.remove(refundApplyPicAdapter.getItem(position))
                refundApplyPicAdapter.notifyDataSetChanged()

            }
        }
        refundApplyPicAdapter.draggableModule.setOnItemDragListener(object : OnItemDragListener {
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
                refundApplyPicAdapter.notifyDataSetChanged()
            }

        })
    }

    override fun initView() {
        initPicAdapter()
        binding.tvHandle.setOnClickListener {
            if (canHandle()) {
                addApply()
            }
        }
        binding.layoutTop.setOnBackClickListener(object :TopBar.OnBackClickListener{
            override fun onBackClick() {
                onBackPressed()
            }
        })
    }

    private fun canHandle(): Boolean {
        val logisticsCompany = binding.etLogisticsCompany.text.toString()

        val logisticsNo = binding.etLogisticsNum.text.toString()

        if (TextUtils.isEmpty(logisticsCompany)) {
            "请填写物流公司".toast()
            return false
        }
        if (TextUtils.isEmpty(logisticsNo)) {
            "请填写物流单号".toast()
            return false
        }

        return true
    }

    override fun observe() {
        super.observe()
        LiveDataBus.get().with(LiveDataBusKey.PICTURESEDITED).observe(this, Observer {

            selectList.clear()
            selectList.addAll(it as Collection<LocalMedia>)
            refundApplyPicAdapter.setList(selectList)
        })

        viewModel.stsBean.observe(this, Observer {
            it?.let {
                upedimgs.clear()
                ossImageList.clear()
                uploadImgs(it, 0, dialog)
            }
        })
        viewModel.fillInLogisticsLiveData.observe(this, Observer {
            "物流信息填写成功".toast()
            LiveDataBus.get().with(LiveDataBusKey.FILL_IN_LOGISTICS).postValue("success")
            this.finish()
        })
    }

    var mallMallRefundId: String? = ""
    override fun initData() {
        mallMallRefundId = intent.getStringExtra("value")


    }

    private fun uploadImgs(stsBean: STSBean, index: Int, dialog: LoadDialog) {
        AliYunOssUploadOrDownFileConfig.getInstance(this).initOss(
            stsBean.endpoint, stsBean.accessKeyId,
            stsBean.accessKeySecret, stsBean.securityToken
        )

        val media = selectList[index]
        val ytPath = PictureUtil.getFinallyPath(media)
        Log.d("=============", "${ytPath}")
        val type = ytPath.substring(ytPath.lastIndexOf(".") + 1, ytPath.length)
        val exifInterface = ExifInterface(ytPath);
        val rotation = exifInterface.getAttributeInt(
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
                        ossImageList.add(it.imgUrl)
                    }
                    params["imgUrls"] = imgUrls
                    JSON.toJSONString(params).logE()
                    addApply()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                UCrop.REQUEST_CROP -> {
                    val resultUri = UCrop.getOutput(data!!)
                    selectList[0].isCut = true
                    selectList[0].cutPath = resultUri?.path
                    refundApplyPicAdapter.setList(selectList)
                    refundApplyPicAdapter.notifyDataSetChanged()
                }
                UCrop.RESULT_ERROR -> {
                    val cropError = UCrop.getError(data!!)
                }
            }
        }

    }

    fun addApply() {

        val logisticsCompany = binding.etLogisticsCompany.text.toString()
        val refundDescText = binding.llRefundNotes.etContent.text.toString()
        val logisticsNo = binding.etLogisticsNum.text.toString()
        viewModel.fillInLogistics(
            mallMallRefundId,
            logisticsCompany,
            logisticsNo,
            ossImageList,
            refundDescText
        )


    }

}