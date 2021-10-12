package com.changanford.circle.ui.activity

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSON
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.baidu.mapapi.search.core.PoiInfo
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.adapter.ButtomTypeAdapter
import com.changanford.circle.adapter.ButtomlabelAdapter
import com.changanford.circle.adapter.LongPostAdapter
import com.changanford.circle.adapter.PostPicAdapter
import com.changanford.circle.bean.*
import com.changanford.circle.databinding.LongpostactivityBinding
import com.changanford.circle.databinding.LongpostheadBinding
import com.changanford.circle.ext.loadImageNoOther
import com.changanford.circle.viewmodel.PostViewModule
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.bean.ImageUrlBean
import com.changanford.common.bean.STSBean
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.router.startARouterForResult
import com.changanford.common.ui.dialog.LoadDialog
import com.changanford.common.util.AliYunOssUploadOrDownFileConfig
import com.changanford.common.util.AppUtils
import com.changanford.common.util.PictureUtil
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.logD
import com.changanford.common.utilext.toast
import com.changanford.common.widget.HomeBottomDialog
import com.gyf.immersionbar.ImmersionBar
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.luck.picture.lib.tools.ScreenUtils
import com.yalantis.ucrop.UCrop
import java.util.zip.Inflater

@Route(path = ARouterCirclePath.LongPostAvtivity)
class LongPostAvtivity: BaseActivity<LongpostactivityBinding, PostViewModule>() {

    private lateinit var headBinding: LongpostheadBinding
    private val headview by lazy {
        layoutInflater.inflate(R.layout.longposthead,null)
    }
    private val longpostadapter by lazy {
        LongPostAdapter(binding.longpostrec.layoutManager as LinearLayoutManager)
    }

    private lateinit var plateBean: PlateBean
    private var platename: String=""
    private var circlename: String=""
    private var address: String = ""
    private val upedimgs = ArrayList<ImageUrlBean>()  //上传之后的图片集合地址
    private var nomalwith = 500;
    private var nomalhight = 500;
    private var selectList = ArrayList<LongPostBean>()
    private var type = 0
    private var params = hashMapOf<String, Any>()
    private  var FMMeadia:LocalMedia? = null
    private val dialog by lazy{
        LoadDialog(this).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            setLoadingText("图片上传中..")
            show()
        }
    }

    private val buttomTypeAdapter by lazy {
        ButtomTypeAdapter()
    }
    private val buttomlabelAdapter by lazy {
        ButtomlabelAdapter()
    }
    companion object{
        const val ITEM_SELECTPIC= 0x5564
    }

    override fun initView() {
        ImmersionBar.with(this).keyboardEnable(true).init()  //顶起页面底部
        AppUtils.setStatusBarPaddingTop(binding.title.commTitleBar, this)
        binding.title.barTvTitle.text = "发帖"
        binding.title.barTvOther.visibility = View.VISIBLE
        binding.title.barTvOther.text = "发布"
        binding.title.barTvOther.setTextColor(resources.getColor(R.color.white))
        binding.title.barTvOther.textSize = 12f
        binding.title.barTvOther.background = resources.getDrawable(R.drawable.post_btn_bg)
        headBinding =DataBindingUtil.bind(headview)!!
    }

    override fun observe() {
        super.observe()
        LiveDataBus.get().with(LiveDataBusKey.LONGPOSTFM).observe(this, Observer {
            FMMeadia = it as LocalMedia
            headBinding.ivFm.visibility=View.VISIBLE

            GlideUtils.loadRoundFilePath(PictureUtil.getFinallyPath(FMMeadia!!),headBinding.ivFm)
            headBinding.ivAddfm.visibility = View.GONE
            headBinding.tvFm.visibility = View.GONE
        })
        viewModel.postsuccess.observe(this, Observer {
            if (dialog.isShowing){
                dialog.dismiss()
            }
            "发布成功".toast()
            finish()
        })
        viewModel.stsBean.observe(this, Observer {
            it?.let {
                upedimgs.clear()
                selectList.add(LongPostBean("",FMMeadia))
                selectList.addAll(longpostadapter.data)
                var mediacount = 0
                selectList.forEach {
                    if (it.localMedias!=null){
                        mediacount++
                    }
                }
                uploadImgs(it,0,dialog,mediacount,0)
            }
        })
        viewModel.cityCode.observe(this, Observer {
            params["cityCode"] = it.cityCode ?: ""
            params["city"] = it.cityName
        })
        LiveDataBus.get().with(LiveDataBusKey.Conversation, HotPicItemBean::class.java)
            .observe(this,
                Observer {
                    buttomTypeAdapter.setData(2, ButtomTypeBean(it.name, 1, 2))
                    params["topicId"] = it.topicId
                })


        LiveDataBus.get().with(LiveDataBusKey.CHOOSELOCATION, PoiInfo::class.java).observe(this,
            {
                it.location.latitude.toString().toast()
                address = it.address
                params["address"] = address
                params["lat"] = it.location.latitude
                params["lon"] = it.location.longitude
                params["province"] = it.province ?: address
                viewModel.getCityDetailBylngAndlat(it.location.latitude, it.location.longitude)
                buttomTypeAdapter.setData(4, ButtomTypeBean(it.name, 1, 4))

            })

        viewModel.plateBean.observe(this, Observer {
            plateBean = it

        })
        LiveDataBus.get().with(LiveDataBusKey.CHOOSELOCATIONNOTHING, String::class.java)
            .observe(this,
                {
                    it.toString().toast()
                    buttomTypeAdapter.setData(4,ButtomTypeBean("",0,4))
                })

        LiveDataBus.get().with(LiveDataBusKey.PICTURESEDITED).observe(this, Observer {
//            selectList.clear()
//            selectList.addAll(it as Collection<LocalMedia>)
            var localMedias = it as List<LocalMedia>
            longpostadapter.addData(LongPostBean( localMedias[0].contentDesc?:"",localMedias[0]))
//            postPicAdapter.setList(selectList)
        })
    }

    override fun initData() {
        initandonclickhead()
        binding.longpostrec.layoutManager = LinearLayoutManager(this)
        longpostadapter.draggableModule.isDragEnabled=true
        binding.longpostrec.adapter = longpostadapter
        longpostadapter.addHeaderView(headview)
        params["type"] = 4
        initbuttom()
        onclick()

    }

    private fun initbuttom() {
        binding.bottom.typerec.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
        binding.bottom.typerec.adapter = buttomTypeAdapter
        buttomTypeAdapter.addData(
            arrayListOf(
                ButtomTypeBean("选择模块", 1, 0),
                ButtomTypeBean("", 0, 1),
                ButtomTypeBean("", 0, 2),
                ButtomTypeBean("", 0, 3),
                ButtomTypeBean("", 0, 4)
            )
        )
        binding.bottom.labelrec.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
        binding.bottom.labelrec.adapter = buttomlabelAdapter
        buttomlabelAdapter.addData(
            arrayListOf(
                ButtomlabelBean("最美重庆1"),
                ButtomlabelBean("最美重庆111"),
                ButtomlabelBean("最美重庆12222"),
                ButtomlabelBean("最美重庆13333"),
                ButtomlabelBean("最美重庆144444")
            )
        )
        buttomlabelAdapter.setOnItemClickListener { adapter, view, position ->
            buttomlabelAdapter.getItem(position).isselect = true
            buttomlabelAdapter.data.forEachIndexed { index, buttomlabelBean ->
                if (index != position) {
                    buttomlabelBean.isselect = false
                }
            }
            buttomlabelAdapter.notifyDataSetChanged()
            buttomlabelAdapter.getItem(position).content.toast()
        }
    }

    private fun onclick() {
        binding.bottom.ivEmoj.setOnClickListener {
            "表情未开发".toast()
        }
        binding.title.barTvOther.setOnClickListener {
            ispost()
        }

        buttomTypeAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.buttom_iv_close) {
                buttomTypeAdapter.setData(
                    position,
                    ButtomTypeBean("", 0, buttomTypeAdapter.getItem(position).itemType)
                )
            }

        }

        buttomTypeAdapter.setOnItemClickListener { adapter, view, position ->
            if (buttomTypeAdapter.getItem(position).itemType == 0 || buttomTypeAdapter.getItem(
                    position
                ).itemType == 1
            ) {
                if (::plateBean.isInitialized && plateBean.plate.isNotEmpty()) {
                    val sList = mutableListOf<String>()
                    for (bean in plateBean.plate) {
                        sList.add(bean.name)
                    }
                    HomeBottomDialog(this, *sList.toTypedArray()).setOnClickItemListener(object :
                        HomeBottomDialog.OnClickItemListener {
                        override fun onClickItem(position: Int, str: String) {
                            buttomTypeAdapter.setData(0, ButtomTypeBean("", 0, 0))
                            buttomTypeAdapter.setData(1, ButtomTypeBean(str, 1, 1))
                            platename = str
                            params["plate"] = plateBean.plate[position].plate
                            params["actionCode"] = plateBean.plate[position].actionCode
                        }
                    }).show()
                } else {
                    viewModel.getPlate()
                }
            }
        }


        binding.bottom.ivQuanzi.setOnClickListener {
            startARouterForResult(
                this,
                ARouterCirclePath.ChoseCircleActivity,
                PostActivity.REQUEST_CIRCLE
            )
        }

        binding.bottom.ivLoc.setOnClickListener {
            startARouter(ARouterCirclePath.ChooseLocationActivity)
        }
        binding.bottom.ivEmoj.setOnClickListener {
            "表情未开发".toast()
        }
        binding.bottom.ivHuati.setOnClickListener {
            startARouter(ARouterCirclePath.ChooseConversationActivity)
        }
        binding.bottom.ivPic.setOnClickListener {
            PictureUtil.openGalleryOnePic(this,
                object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: MutableList<LocalMedia>?) {
                        val localMedia = result?.get(0)
                        localMedia?.let {
                            var bundle = Bundle()
                            bundle.putParcelableArrayList("picList", arrayListOf(localMedia))
                            bundle.putInt("position", 0)
                            bundle.putInt("showEditType", 1)
                            startARouter(ARouterCirclePath.PictureeditlActivity, bundle)
                        }

                    }

                    override fun onCancel() {

                    }

                })
        }



        longpostadapter.setOnItemChildClickListener(object :OnItemChildClickListener{
            override fun onItemChildClick(
                adapter: BaseQuickAdapter<*, *>,
                view: View,
                position: Int
            ) {
                if (view.id == R.id.iv_delete){
                    longpostadapter.getItem(position).localMedias =null
                    longpostadapter.notifyDataSetChanged()
                }else if(view.id == R.id.iv_addfm){
                    PictureUtil.openGalleryOnePic(this@LongPostAvtivity,
                        object : OnResultCallbackListener<LocalMedia> {
                            override fun onResult(result: MutableList<LocalMedia>?) {
                                val localMedia = result?.get(0)
                                localMedia?.let {
                                    var bundle = Bundle()
                                    bundle.putParcelableArrayList("picList", arrayListOf(localMedia))
                                    bundle.putInt("position", 0)
                                    bundle.putInt("showEditType", 1)
                                    bundle.putString("itemcontent",longpostadapter.getItem(position).content)
                                    bundle.putInt("itemPosition",position)
                                    bundle.putBoolean("longpostItemSelect",true)
                                    startARouterForResult(this@LongPostAvtivity,ARouterCirclePath.PictureeditlActivity, bundle,ITEM_SELECTPIC)
                                }

                            }

                            override fun onCancel() {

                            }

                        })
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

    private fun ispost() {
        var biaoti = headBinding.etBiaoti.text.toString()
        var content = headBinding.etContent.text.toString()
        when {
            FMMeadia==null  -> {
                "请选择封面".toast()
                return
            }
            biaoti.isNullOrEmpty() || biaoti.length < 6 || biaoti.length > 20 -> {
                "请输入6-20字的帖子标题".toast()
                return
            }
            content.isNullOrEmpty() -> {
                "请输入正文内容".toast()
            }
            !params.containsKey("plate") -> {
                "请选择模块".toast()
            }
            else -> {
                params["content"] = content
                params["title"] = biaoti
                viewModel.getOSS()
            }
        }
    }

    private fun initandonclickhead() {
        var bthinttxt = "标题 (6-20字之间)"
        var spannableString = SpannableString(bthinttxt)
        var intstart = bthinttxt.indexOf('(')
        var intend = bthinttxt.length
        spannableString.setSpan(
            AbsoluteSizeSpan(60),
            0,
            intstart,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        spannableString.setSpan(
            AbsoluteSizeSpan(40),
            intstart,
            intend,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        headBinding.etBiaoti.hint =spannableString
        headBinding.ivAddfm.setOnClickListener {
            PictureUtil.openGalleryOnePic(this,object :OnResultCallbackListener<LocalMedia>{
                override fun onResult(result: MutableList<LocalMedia>?) {
                    val localMedia = result?.get(0)
                    localMedia?.let {
                        var bundle = Bundle()
                        bundle.putParcelableArrayList("picList", arrayListOf(localMedia))
                        bundle.putInt("position", 0)
                        bundle.putInt("showEditType", -1)
                        bundle.putBoolean("longPostFM",true)
                        startARouter(ARouterCirclePath.PictureeditlActivity, bundle)
                    }
                }

                override fun onCancel() {

                }

            })
        }
        headBinding.ivFm.setOnClickListener {
            val array = ArrayList<String>()
            array.add("重选封面")
            array.add("编辑封面")
            array.add("删除封面")
            HomeBottomDialog(this, *array.toTypedArray())
                .setOnClickItemListener(object :
                    HomeBottomDialog.OnClickItemListener {
                    override fun onClickItem(position: Int, str: String) {
                        when (str) {
                            "重选封面" -> {
                                PictureUtil.openGalleryOnePic(this@LongPostAvtivity,object :OnResultCallbackListener<LocalMedia>{
                                    override fun onResult(result: MutableList<LocalMedia>?) {
                                        val localMedia = result?.get(0)
                                        localMedia?.let {
                                            var bundle = Bundle()
                                            bundle.putParcelableArrayList("picList", arrayListOf(localMedia))
                                            bundle.putInt("position", 0)
                                            bundle.putInt("showEditType", -1)
                                            bundle.putBoolean("longPostFM",true)
                                            startARouter(ARouterCirclePath.PictureeditlActivity, bundle)
                                        }
                                    }

                                    override fun onCancel() {

                                    }

                                })

                            }
                            "编辑封面" -> {
                                var bundle = Bundle()
                                bundle.putParcelableArrayList("picList", arrayListOf(FMMeadia))
                                bundle.putInt("position", 0)
                                bundle.putInt("showEditType", -1)
                                bundle.putBoolean("longPostFM",true)
                                startARouter(ARouterCirclePath.PictureeditlActivity, bundle)
                            }
                            "删除封面" -> {
                                FMMeadia = null
                                headBinding.ivAddfm.visibility = View.VISIBLE
                                headBinding.tvFm.visibility = View.VISIBLE
                                headBinding.ivFm.visibility = View.GONE

                            }
                        }
                    }
                }).show()
        }

        headBinding.etContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                headBinding.tvEtcount.text = "${headBinding.etContent.length()}/200"
            }

        })
    }

    private fun uploadImgs(stsBean: STSBean, index: Int, dialog: LoadDialog,mediacount:Int,indexcount:Int) {
        var path=""
        var ytPath = ""
        val scount = index + 1

        AliYunOssUploadOrDownFileConfig.getInstance(this).initOss(
            stsBean.endpoint, stsBean.accessKeyId,
            stsBean.accessKeySecret, stsBean.securityToken
        )

        if (selectList[index].localMedias!=null){  //封面必不为空 index 0必有值
            val media = selectList[index].localMedias
             ytPath = PictureUtil.getFinallyPath(media!!)
            Log.d("=============", "${ytPath}")
            var type = ytPath.substring(ytPath.lastIndexOf(".") + 1, ytPath.length)

             path =
                stsBean.tempFilePath + System.currentTimeMillis() + "androidios${
                    if (media.width == 0) {
                        nomalwith
                    } else {
                        media.width
                    }
                }_${
                    if (media.height == 0) {
                        nomalhight
                    } else {
                        media.height
                    }
                }." + type
            if (index==0){
                params["pics"] = path
            }

        }else {
            if (index==0){
                params["pics"] = path
            }else if(index+1 == selectList.size){  //最后一个图片为空 开始post
                upedimgs.add(ImageUrlBean("",longpostadapter.getItem(index-1).content))
                addPost()
                return
            }else{
                upedimgs.add(ImageUrlBean("",longpostadapter.getItem(index-1).content))
                uploadImgs(stsBean, scount, dialog,mediacount,indexcount)
                return
            }


        }

        AliYunOssUploadOrDownFileConfig.getInstance(this)
            .uploadFile(stsBean.bucketName, path, ytPath, "", 0)
        AliYunOssUploadOrDownFileConfig.getInstance(this).setOnUploadFile(object :
            AliYunOssUploadOrDownFileConfig.OnUploadFile {
            override fun onUploadFileSuccess(info: String) {
                if (index!=0){
                    upedimgs.add(ImageUrlBean(path,longpostadapter.getItem(index-1).content))
                }
                var mindexpic = indexcount+1
                runOnUiThread {
                    Log.d("mcount--","${mindexpic}")
                    dialog.setTvprogress("${mindexpic}/${mediacount}")
                }
                if (scount == selectList.size) {
                    addPost()
                    return
                }
                uploadImgs(stsBean, scount, dialog,mediacount,mindexpic)
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
        params["imgUrl"] = upedimgs
        params["isPublish"]= 2
        JSON.toJSONString(params).logD()
        viewModel.postEdit(params)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {

                PostActivity.REQUEST_CIRCLE -> {
                    if (data != null) {
                        params["circleId"] = data.getStringExtra("circleId") ?: "0"
                        circlename = data.getStringExtra("name").toString()
                        buttomTypeAdapter.setData(3, ButtomTypeBean(circlename, 1, 3))
                    }
                }
                ITEM_SELECTPIC ->{
                    var media= data!!.getParcelableArrayListExtra<LocalMedia>("itemMedia")
                    var itemposition = data!!.getIntExtra("position",0)
                    longpostadapter.getItem(itemposition).apply {
                        localMedias = media!![0]
                        content =media!![0].contentDesc
                    }
                    longpostadapter.notifyDataSetChanged()
                }
            }
        }
    }
}