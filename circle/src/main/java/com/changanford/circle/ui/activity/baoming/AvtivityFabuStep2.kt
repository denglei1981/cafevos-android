package com.changanford.circle.ui.activity.baoming

import android.Manifest
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.media.ExifInterface
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.view.TimePickerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.adapter.LongPostV2Adapter
import com.changanford.circle.bean.LongPostBean
import com.changanford.circle.databinding.ActivityFabudeitalBinding
import com.changanford.circle.ui.release.MMapActivity
import com.changanford.circle.ui.release.ReleaseActivity
import com.changanford.circle.ui.release.widget.ActivityTypeDialog
import com.changanford.circle.viewmodel.PostViewModule
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.DtoBeanNew
import com.changanford.common.bean.MapReturnBean
import com.changanford.common.bean.STSBean
import com.changanford.common.net.onFailure
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.room.PostEntity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.router.startARouterForResult
import com.changanford.common.ui.dialog.AlertDialog
import com.changanford.common.ui.dialog.LoadDialog
import com.changanford.common.util.AliYunOssUploadOrDownFileConfig
import com.changanford.common.util.AppUtils
import com.changanford.common.util.PictureUtil
import com.changanford.common.util.TimeUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.image.ImageCompress
import com.changanford.common.utilext.logD
import com.changanford.common.utilext.toast
import com.changanford.common.utilext.toastShow
import com.changanford.common.widget.HomeBottomDialog
import com.gyf.immersionbar.ImmersionBar
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.luck.picture.lib.tools.DoubleUtils
import com.luck.picture.lib.tools.ToastUtils
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.callbcak.CheckRequestPermissionListener
import com.scwang.smart.refresh.layout.util.SmartUtil
import razerdp.basepopup.QuickPopupBuilder
import razerdp.basepopup.QuickPopupConfig
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


/**
 * 发布报名活动第二步
 */
@Route(path = ARouterCirclePath.ActivityFabuStep2)
class AvtivityFabuStep2 : BaseActivity<ActivityFabudeitalBinding, PostViewModule>() {

    private val longpostadapter by lazy {
        LongPostV2Adapter(binding.longpostrec.layoutManager as LinearLayoutManager)
    }

    private var baoMingViewModel: BaoMingViewModel? = null

    private val upedimgs = ArrayList<DtoBeanNew.ContentImg>()  //上传之后的图片集合地址
    private var nomalwith = 500
    private var nomalhight = 500
    private var selectList = ArrayList<LongPostBean>()


    private var locaPostEntity: PostEntity? = null
    private var editText: EditText? = null

    private var isTopPost = false
    private var isCirclePost: Boolean = false
    private var isH5Post: Boolean = false

    private var isunSave: Boolean = false  // 要不要保存的标志。
    private val dialog by lazy {
        LoadDialog(this).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            setLoadingText("图片上传中..")
            show()
        }
    }

    companion object {
        const val ITEM_SELECTPIC = 0x5564
    }

    override fun initView() {
        ImmersionBar.with(this)
            .keyboardEnable(true)
            .init()  //顶起页面底部
        AppUtils.setStatusBarPaddingTop(binding.title.commTitleBar, this)
        binding.title.barTvTitle.text = "发布报名活动"
        locaPostEntity = intent.getSerializableExtra("postEntity") as PostEntity?
        isH5Post = intent.extras?.getBoolean("isH5Post") ?: false
        isCirclePost = intent.extras?.getBoolean("isCirclePost") ?: false
        isTopPost = intent.extras?.getBoolean("isTopPost") ?: false
        baoMingViewModel = createViewModel(BaoMingViewModel::class.java)
    }

    private fun initlocaData() {
        initEtContent()
        binding.bottom.apply {
            ActivityFabuBaoming.dto?.let {
                time.text = if (it.endTimeShow.isNullOrEmpty())"" else "${it.beginTimeShow?:""}-${it.endTimeShow?:""}"
                leixing.text = if(it.wonderfulType == "1") "线下活动" else "线上活动"
                place.text = it.activityAddr?:""
                if (it.wonderfulType.isNullOrEmpty()){
                    ActivityFabuBaoming.dto.wonderfulType = "0"
                }
            }
        }
    }

    private fun initEtContent() {
        if (!ActivityFabuBaoming.dto.contentImgList.isNullOrEmpty()){
            for (i in ActivityFabuBaoming.dto.contentImgList){
                longpostadapter.addData(LongPostBean(i.contentDesc,i.localMedias))
            }
        }else {
            val default = LongPostBean(hintStr = "请输入活动详情")
            longpostadapter.addData(default)
        }
        if (ActivityFabuBaoming.dto.wonderfulType?:"0" == "0"){
            actType = "0"
            binding.bottom.placelayout.isVisible = false
        }else{
            actType = "1"
            binding.bottom.placelayout.isVisible = true
        }
    }

    override fun onBackPressed() {
        caogao()
    }
    fun caogao(){
        if(ActivityFabuBaoming.dto.contentImgList.isNullOrEmpty()) {
            ActivityFabuBaoming.dto.contentImgList = ArrayList()
        }else{
            ActivityFabuBaoming.dto.contentImgList.clear()
        }
        ActivityFabuBaoming.dto.contentImgList.addAll(longpostadapter.data.map {
            DtoBeanNew.ContentImg(it.content,it.localMedias)
        })
        finish()
    }

    override fun observe() {
        super.observe()
        ImmersionBar.with(this).setOnKeyboardListener { isPopup, keyboardHeight ->
            Log.d("ImmersionBar", keyboardHeight.toString())
            binding.ivPic.isVisible = isPopup

        }
        viewModel.stsBean.observe(this, Observer {
            it?.let {
                upedimgs.clear()
                selectList.clear()
                selectList.addAll(longpostadapter.data)
                var mediacount = 0
                selectList.forEach {
                    if (it.localMedias != null) {
                        mediacount++
                    }
                }

                val needCompressImg = ArrayList<String?>()
                selectList.forEach { bean ->
                    bean.localMedias?.let {
                        needCompressImg.add(
                            PictureUtil.getFinallyPath(
                                bean.localMedias!!
                            )
                        )
                    }
                }

                ImageCompress.compressImage(
                    this,
                    needCompressImg,
                    object : ImageCompress.ImageCompressResult {
                        override fun compressSuccess(list: List<File>) {
                            var index = 0
                            selectList.forEach {
                                it.localMedias?.let {
                                    it.myCompressPath = list[index].absolutePath
                                    index++
                                }
                            }
                            uploadImgs(it, 0, dialog, mediacount, 0)
                        }

                        override fun compressFailed() {
                            uploadImgs(it, 0, dialog, mediacount, 0)
                        }

                    })
            }
        })
        LiveDataBus.get().with(LiveDataBusKey.PICTURESEDITED).observe(this, Observer {
            isunSave = false
            val localMedias = it as List<LocalMedia>
            if (longpostadapter.selectionIndex > 0) {// 光标位置后追加图片大于0
                val selectionStr =
                    longpostadapter.getItem(longpostadapter.selectionPosition).content
                val gindex = longpostadapter.currentTxtView?.selectionStart // 光标位置
                val gItem = longpostadapter.selectionPosition
                if (gindex != null && selectionStr != null) {
                    if (gindex > selectionStr.length) {
                        toastShow("请更换文本点击位置")
                        return@Observer
                    }
                }

                selectionStr?.let { s ->
                    val startStr = gindex?.let { it1 -> s.substring(0, it1) }
                    val endStr = gindex?.let { it1 -> s.substring(it1, s.length) }
                    val starStrBean = LongPostBean(startStr)
                    val picPostBean: ArrayList<LongPostBean> = arrayListOf()
                    localMedias.forEach { m ->
                        picPostBean.add(LongPostBean("", m))
                        if (localMedias.indexOf(m) < localMedias.size - 1) {
                            picPostBean.add(LongPostBean(""))
                        }
                    }
                    longpostadapter.remove(longpostadapter.getItem(longpostadapter.selectionPosition)) //移除之前的
                    longpostadapter.addData(longpostadapter.selectionPosition, starStrBean)
                    longpostadapter.addData(longpostadapter.selectionPosition + 1, picPostBean)
                    val endStrBean = LongPostBean(endStr)
                    longpostadapter.addData(
                        longpostadapter.selectionPosition + picPostBean.size + 1,
                        endStrBean
                    )
                }
                longpostadapter.selectionIndex = -1
                longpostadapter.selectionPosition = -1
                longpostadapter.currentTxtView?.clearFocus()//清除光标
            } else {// 默认在最后加图片
                localMedias.forEach { m ->
                    longpostadapter.addData(LongPostBean("", m))
                    longpostadapter.addData(LongPostBean(""))
                }
            }
        })
    }


    fun showErrorWarn() {
        QuickPopupBuilder.with(this)
            .contentView(R.layout.dialog_post_error)
            .config(
                QuickPopupConfig()
                    .gravity(Gravity.CENTER)
                    .withClick(R.id.btn_comfir, View.OnClickListener {
                    }, true)

            ).show()
    }


    override fun initData() {
        viewModel.getPlate()
        viewModel.getTags() //标签
        val layoutManager = LinearLayoutManager(this)
        binding.longpostrec.layoutManager = layoutManager
        longpostadapter.draggableModule.isDragEnabled = true
        binding.longpostrec.adapter = longpostadapter
        onclick()
        initlocaData()
    }


    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            binding.title.barImgBack.callOnClick()
            return true
        }
        return super.onKeyUp(keyCode, event)
    }



    private fun onclick() {

        binding.title.barImgBack.setOnClickListener {
            caogao()
        }
        binding.bottom.timelayout.setOnClickListener {
            setTimePicker()
        }
        binding.bottom.leixinglayout.setOnClickListener {
            if (!DoubleUtils.isFastDoubleClick()) {
                ActivityTypeDialog(this) { integer: Int ->
                    actType = integer.toString() + ""
                    ActivityFabuBaoming.dto.wonderfulType = actType
                    if (integer == 0) {
                        binding.bottom.leixing.text = "线上活动"
                        binding.bottom.placelayout.isVisible = false
                    } else {
                        binding.bottom.leixing.text = "线下活动"
                        binding.bottom.placelayout.isVisible = true
                    }
                    null
                }.setDefault(Integer.valueOf(actType)).show()
            }
        }
        binding.bottom.placelayout.setOnClickListener {
            StartBaduMap()
        }
        binding.bottom.nickSave.setOnClickListener {
            ActivityFabuBaoming.dto.apply {
                if (longpostadapter.data!= null &&longpostadapter.data.size == 1 && longpostadapter.data[0].content.isNullOrEmpty()){
                    "请输入活动详情".toast()
                    return@setOnClickListener
                }
                if (beginTime.isNullOrEmpty() || endTime.isNullOrEmpty()){
                    "请选择活动时间".toast()
                    return@setOnClickListener
                }
                if (wonderfulType.isNullOrEmpty()){
                    "请选择活动类型".toast()
                    return@setOnClickListener
                }
                if (binding.bottom.placelayout.isVisible && activityAddr.isNullOrEmpty()){
                    "请选择活动地点".toast()
                    return@setOnClickListener
                }
                viewModel.getOSS()
            }

        }
        binding.ivPic.setOnClickListener {
            isunSave = true
            val meadiaList: ArrayList<LocalMedia> = arrayListOf()
            PictureUtil.openGallery(this, meadiaList,
                object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: MutableList<LocalMedia>?) {
                        if (result != null) {
                            meadiaList.clear()
                            meadiaList.addAll(result)
                        }
                        if (meadiaList.size > 0) {
                            val bundle = Bundle()
                            bundle.putParcelableArrayList("picList", meadiaList)
                            bundle.putInt("position", 0)
                            bundle.putInt("showEditType", -1)
                            startARouter(ARouterCirclePath.PictureeditlActivity, bundle)
                        }
                    }

                    override fun onCancel() {
                        isunSave = false
                    }

                })
        }



        longpostadapter.setOnItemChildClickListener(object : OnItemChildClickListener {
            override fun onItemChildClick(
                adapter: BaseQuickAdapter<*, *>,
                view: View,
                position: Int
            ) {
                if (view.id == R.id.iv_delete) {
                    longpostadapter.remove(position)
                } else if (view.id == R.id.iv_pic) {
                    isunSave = true
                    PictureUtil.openGalleryOnePic(this@AvtivityFabuStep2,
                        object : OnResultCallbackListener<LocalMedia> {
                            override fun onResult(result: MutableList<LocalMedia>?) {

                                val localMedia = result?.get(0)
                                localMedia?.let {
                                    val bundle = Bundle()
                                    bundle.putParcelableArrayList(
                                        "picList",
                                        arrayListOf(localMedia)
                                    )
                                    bundle.putInt("position", 0)
                                    bundle.putInt("showEditType", 0)
                                    bundle.putString(
                                        "itemcontent",
                                        longpostadapter.getItem(position).content
                                    )
                                    bundle.putInt("itemPosition", position)
                                    bundle.putBoolean("longpostItemSelect", true)
                                    startARouterForResult(
                                        this@AvtivityFabuStep2,
                                        ARouterCirclePath.PictureeditlActivity,
                                        bundle,
                                        ITEM_SELECTPIC
                                    )
                                }

                            }

                            override fun onCancel() {
                                isunSave = false
                            }

                        })
                } else if (view.id == R.id.ivFm) {
                    val array = ArrayList<String>()
                    array.add("编辑图片")
                    array.add("删除图片")
                    HomeBottomDialog(this@AvtivityFabuStep2, *array.toTypedArray())
                        .setOnClickItemListener(object :
                            HomeBottomDialog.OnClickItemListener {
                            override fun onClickItem(mposition: Int, str: String) {
                                when (str) {
                                    "编辑图片" -> {
                                        isunSave = true
                                        var bundle = Bundle()
                                        bundle.putParcelableArrayList(
                                            "picList",
                                            arrayListOf(longpostadapter.getItem(position).localMedias)
                                        )
                                        bundle.putInt("position", position)
                                        bundle.putInt("showEditType", 0)
                                        bundle.putString(
                                            "itemcontent",
                                            longpostadapter.getItem(position).content
                                        )
                                        bundle.putInt("itemPosition", position)
                                        bundle.putBoolean("longpostItemSelect", true)
                                        startARouterForResult(
                                            this@AvtivityFabuStep2,
                                            ARouterCirclePath.PictureeditlActivity,
                                            bundle,
                                            ITEM_SELECTPIC
                                        )
                                    }
                                    "删除图片" -> {
                                        longpostadapter.getItem(position).localMedias = null
                                        longpostadapter.notifyDataSetChanged()
                                    }
                                }
                            }
                        }).show()
                }
            }
        })

        longpostadapter.draggableModule.setOnItemDragListener(object : OnItemDragListener {
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
                longpostadapter.notifyDataSetChanged()
            }
        })
    }


    private fun uploadImgs(
        stsBean: STSBean,
        index: Int,
        dialog: LoadDialog,
        mediacount: Int,
        indexcount: Int
    ) {
        var path = ""
        var ytPath = ""
        val scount = index + 1

        AliYunOssUploadOrDownFileConfig.getInstance(this).initOss(
            stsBean.endpoint, stsBean.accessKeyId,
            stsBean.accessKeySecret, stsBean.securityToken
        )

        if (selectList[index].localMedias != null) {  //封面必不为空 index 0必有值
            val media = selectList[index].localMedias!!
            ytPath =  if (media.myCompressPath.isNullOrEmpty()) {
                PictureUtil.getFinallyPath(media)
            } else {
                media.myCompressPath
            }
            Log.d("=============", "${ytPath}")
            val type = ytPath.substring(ytPath.lastIndexOf(".") + 1, ytPath.length)
            val exifInterface = ExifInterface(ytPath);
            val rotation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            );
            path = stsBean.tempFilePath + System.currentTimeMillis() + "androidios${
                if (media.isCut) {
                    if (rotation == ExifInterface.ORIENTATION_ROTATE_90 || rotation == ExifInterface.ORIENTATION_ROTATE_270) {
                        exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 500)
                    } else {
                        exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 500);
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
                        exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 500);
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

        } else {
            if (index + 1 == selectList.size) {  //最后一个图片为空 开始post
                upedimgs.add(
                    DtoBeanNew.ContentImg(
                        "",
                        longpostadapter.getItem(index).content!!
                    )
                )
                addPost()
                dialog.dismiss()
                return
            } else {
                if (longpostadapter.getItem(index).content?.isNotEmpty() == true) {
                    upedimgs.add(
                        DtoBeanNew.ContentImg(
                            "",
                            longpostadapter.getItem(index).content!!
                        )
                    )
                }
                uploadImgs(stsBean, scount, dialog, mediacount, indexcount)
                return
            }
        }

        AliYunOssUploadOrDownFileConfig.getInstance(this)
            .uploadFile(stsBean.bucketName, path, ytPath, "", 0)
        AliYunOssUploadOrDownFileConfig.getInstance(this).setOnUploadFile(object :
            AliYunOssUploadOrDownFileConfig.OnUploadFile {
            override fun onUploadFileSuccess(info: String) {
                upedimgs.add(
                    DtoBeanNew.ContentImg(
                        path,
                        ""
                    )
                )
                val mindexpic = indexcount + 1
                runOnUiThread {
                    Log.d("mcount--", "${mindexpic}")
                    dialog.setTvprogress("${mindexpic}/${mediacount}")
                }
                if (scount == selectList.size) {
                    dialog.dismiss()
                    addPost()
                    return
                }
                uploadImgs(stsBean, scount, dialog, mediacount, mindexpic)
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

    fun addPost() {
        val last =
            upedimgs.filter {
                !TextUtils.isEmpty(
                    it.contentDesc
                ) || !TextUtils.isEmpty(it.contentImgUrl)
            }
        ActivityFabuBaoming.dto.contentImgList = last
        if (ActivityFabuBaoming.wonderfulId == 0) {
            baoMingViewModel?.CommitACT(
                ActivityFabuBaoming
                    .dto
            ) {
                it.onSuccess {
                    "发布完成".toast()
                    LiveDataBus.get().with(LiveDataBusKey.FABUBAOMINGFINISHI).postValue(true)
                    finish()
                }.onWithMsgFailure {
                    it?.toast()
                }
            }
        }else{
            baoMingViewModel?.updateActivity(ActivityFabuBaoming.wonderfulId,ActivityFabuBaoming
                .dto){
                it.onSuccess {
                    "发布完成".toast()
                    LiveDataBus.get().with(LiveDataBusKey.FABUBAOMINGFINISHI).postValue(true)
                    finish()
                }.onWithMsgFailure {
                    it?.toast()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        isunSave = false
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                ITEM_SELECTPIC -> {
                    val media = data!!.getParcelableArrayListExtra<LocalMedia>("itemMedia")
                    val itemposition = data!!.getIntExtra("position", 0)
                    longpostadapter.getItem(itemposition).apply {
                        localMedias = media!![0]
                        content = media!![0].contentDesc
                    }
                    longpostadapter.notifyDataSetChanged()
                }
                ReleaseActivity.ADDRESSBACK -> {
                    val poiInfo =
                        data!!.getBundleExtra("mbundaddress")!!.getParcelable<MapReturnBean>("poi")
                    if (poiInfo != null && poiInfo.poiInfo != null) {
                        binding.bottom.place.text = poiInfo.poiInfo.name
                        ActivityFabuBaoming.dto?.apply {
                            latitude = poiInfo.poiInfo.getLocation().latitude.toString() + ""
                            longitude = poiInfo.poiInfo.getLocation().longitude.toString() + ""
                            townName = poiInfo.poiInfo.area
                            provinceName = poiInfo.poiInfo.province
                            cityName = poiInfo.cityName
                            townId = poiInfo.qid
                            provinceId = poiInfo.sid
                            cityId = poiInfo.cid
                            activityAddr = poiInfo.poiInfo.name
                        }
                    }
                }
            }
        }
    }

    /****************************/
    private var pvActTime: TimePickerView? = null
    private var pvActEndTime: TimePickerView? = null
    var timebegin: Date = Date(System.currentTimeMillis())
    var datebegin = ""
    var dateend = ""
    var actType = "1" //活动类型


    fun setTimePicker() {
        hideKeyboard(binding.bottom.time.windowToken)
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
                datebegin = TimeUtils.MillisToStr1(date.time)
                ActivityFabuBaoming.dto.beginTime = datebegin
                ActivityFabuBaoming.dto.beginTimeShow = TimeUtils.MillisToStrO(date.time)
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
                .setBgColor(Color.WHITE) //滚轮背景颜色 Night mode
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
                dateend = TimeUtils.MillisToStr1(date.time)
                if (timebegin.time > date.time) {
                    ToastUtils.s(
                        BaseApplication.INSTANT.applicationContext,
                        "结束时间不能小于开始时间"
                    )
                    pvActTime!!.show()
                } else {
                    ActivityFabuBaoming.dto.endTime = dateend
                    ActivityFabuBaoming.dto.endTimeShow = TimeUtils.MillisToStrO(date.time)
                    binding.bottom.time.text = "${ActivityFabuBaoming.dto.beginTimeShow} - ${ActivityFabuBaoming.dto.endTimeShow}"
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
                .setBgColor(Color.WHITE) //滚轮背景颜色 Night mode
                .setType(booleanArrayOf(true, true, true, true, true, false))
                .setRangDate(startDate, endDate) //起始终止年月日设定
                .setLabel("年", "月", "日", "时", "分", "") //默认设置为年月日时分秒
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)
                .build()
        }
    }


    /**
     * 选择地址
     */
    private fun StartBaduMap() {
        SoulPermission.getInstance()
            .checkAndRequestPermission(
                Manifest.permission.ACCESS_FINE_LOCATION,  //if you want do noting or no need all the callbacks you may use SimplePermissionAdapter instead
                object : CheckRequestPermissionListener {
                    override fun onPermissionOk(permission: Permission) {
                        startActivityForResult(
                            Intent(
                                this@AvtivityFabuStep2,
                                MMapActivity::class.java
                            ), ReleaseActivity.ADDRESSBACK
                        )
                    }

                    override fun onPermissionDenied(permission: Permission) {
                        AlertDialog(this@AvtivityFabuStep2).builder()
                            .setTitle("提示")
                            .setMsg("您已禁止了定位权限，请到设置中心去打开")
                            .setNegativeButton("取消") { }.setPositiveButton(
                                "确定"
                            ) { SoulPermission.getInstance().goPermissionSettings() }.show()
                    }
                })
    }
}