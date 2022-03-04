package com.changanford.circle.ui.activity

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.adapter.circle.CircleTagAdapter
import com.changanford.circle.databinding.ActivityCreateCircleBinding
import com.changanford.circle.ext.ImageOptions
import com.changanford.circle.ext.loadImage
import com.changanford.circle.ext.setCircular
import com.changanford.circle.viewmodel.CreateCircleViewModel
import com.changanford.circle.widget.pop.CircleSelectTypePop
import com.changanford.circle.widget.pop.OnSelectedBackListener
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.CircleItemBean
import com.changanford.common.bean.NewCirceTagBean
import com.changanford.common.buried.WBuriedUtil
import com.changanford.common.helper.OSSHelper
import com.changanford.common.listener.OnPerformListener
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.AppUtils
import com.changanford.common.util.PictureUtil
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.utilext.toast
import com.changanford.common.wutil.FlowLayoutManager
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener

/**
 *Author lcw
 *Time on 2021/10/11
 *Purpose 创建圈子
 */
@Route(path = ARouterCirclePath.CreateCircleActivity)
class CreateCircleActivity : BaseActivity<ActivityCreateCircleBinding, CreateCircleViewModel>() {
    private var picUrl = ""
    private val mAdapter by lazy { CircleTagAdapter(listener=listener) }
    private var circleItemBean:CircleItemBean?=null
    private var pop:CircleSelectTypePop?=null
    private var circleTypeArr:List<NewCirceTagBean>?=null
    private var typeId:String?=""
    @SuppressLint("SetTextI18n")
    override fun initView() {
        binding.title.apply {
            AppUtils.setStatusBarMarginTop(rlTitle, this@CreateCircleActivity)
            ivBack.setOnClickListener { finish() }
            tvTitle.setText(R.string.str_createCircle)
            wtvCreate.visibility= View.VISIBLE
            wtvCreate.setOnClickListener { submit() }
        }

        binding.run {
            ivFengmian.setCircular(5)
            etBiaoti.addTextChangedListener {
                binding.tvNum.text = it?.length.toString() + "/15"
                btnIsClick()
            }
            etContent.addTextChangedListener {
                binding.tvNum1.text = it?.length.toString() + "/50"
                btnIsClick()
            }
            edtCircleTypeValue.setOnClickListener {
                createPop()
            }
        }
        val flowLayoutManager1 =FlowLayoutManager(this@CreateCircleActivity,true)
        val flowLayoutManager0 =FlowLayoutManager(this@CreateCircleActivity,2) {
            binding.cbMore.visibility=if(it>2)View.VISIBLE else View.GONE
        }
        binding.recyclerView.apply {
            this.layoutManager=flowLayoutManager0
            adapter=mAdapter
        }
        binding.cbMore.apply {
            setOnClickListener {
                binding.recyclerView.layoutManager=if(isChecked)flowLayoutManager1 else flowLayoutManager0
            }
        }
        initListener()
    }

    private fun initListener() {
        binding.run {
            ivFengmian.setOnClickListener {
                PictureUtil.openGalleryOnePic(this@CreateCircleActivity, object :
                    OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: MutableList<LocalMedia>?) {
                        val bean = result?.get(0)
                        val path = bean?.let { it1 -> PictureUtil.getFinallyPath(it1) }
                        path?.let { it1 ->
                            OSSHelper.init(this@CreateCircleActivity).getOSSToImage(this@CreateCircleActivity,it1, object : OSSHelper.OSSImageListener {
                                        override fun getPicUrl(url: String) {
                                            picUrl = url
                                            ivFengmian.post {
                                                ivFengmian.loadImage(picUrl, ImageOptions().apply {
                                                    placeholder = R.mipmap.add_image
                                                })
                                            }
                                            btnIsClick()
                                        }
                                    })
                        }
                    }
                    override fun onCancel() {}
                })
            }
        }
    }
    override fun initData() {
        circleItemBean = intent.getSerializableExtra(RouterManger.KEY_TO_ITEM) as CircleItemBean?
        circleItemBean?.let {//编辑圈子
            typeId=it.type
            picUrl = it.pic
            binding.apply {
                ivFengmian.loadImage(picUrl)
                etBiaoti.setText(it.name)
                etContent.setText(it.description)
                checkBox.isChecked=it.needAudit=="YES"
            }
        }
        //获取圈子标签信息
        viewModel.getTagInfo()
        btnIsClick()
    }
    /**
     * 创建按钮是否可以点击
     * */
    private fun btnIsClick(){
        binding.title.wtvCreate.apply {
            val titleLength=binding.etBiaoti.text.length
            if(picUrl.isEmpty()||titleLength<1||binding.etContent.text.isEmpty()||TextUtils.isEmpty(binding.edtCircleTypeValue.text)
                || mAdapter.data.none { it.isCheck == true }){
                isEnabled=false
                setBackgroundResource(R.drawable.shadow_dd_12dp)
            }else{
                isEnabled=true
                setBackgroundResource(R.drawable.shadow_00095b_12dp)
            }
        }
    }
    private fun submit(){
        binding.apply {
            val title = etBiaoti.text.toString()
            val content = etContent.text.toString()
            val isAudit= checkBox.isChecked
            val typeName=edtCircleTypeValue.text.toString()
            val tagIds= arrayListOf<Int>()
            var tagName =""
            //被选中的标签集合
            mAdapter.data.filter { it.isCheck==true }.apply {
                forEach {
                    tagName+="${it.tagName}、"
                    it.tagId?.apply { tagIds.add(this) }
                }
            }
            WBuriedUtil.clickCircleCreate(title,content,tagName.substring(0,tagName.length-1),if(isAudit)"是" else "否",typeName)
            if(null==circleItemBean) viewModel.createCircle(title,content, picUrl,tagIds,isAudit,typeId)
            else viewModel.editCircle(circleItemBean?.circleId,title,content,picUrl,tagIds,isAudit,typeId)
        }
    }
    private val listener=object : OnPerformListener {
        override fun onFinish(code: Int) {
            btnIsClick()
        }
    }
    override fun observe() {
        super.observe()
        viewModel.upLoadBean.observe(this) {
            it.msg.toast()
            if (it.code == 0) {
                LiveDataBus.get().with(CircleLiveBusKey.REFRESH_MANAGEMENT_CIRCLE).postValue(false)
                finish()
            }
        }
        viewModel.tagInfoData.observe(this) { tagInfo ->
            tagInfo?.apply {
                val typeIndex:Int = circleTypes?.indexOfFirst { item -> typeId == item.id }?:-1
                if(typeIndex>-1){
                    circleTypes?.get(typeIndex)?.apply {
                        isCheck =true
                        binding.edtCircleTypeValue.setText(name)
                    }
                }
                circleTypeArr=circleTypes
                mAdapter.tagMaxCount = tagMaxCount ?: 0
                circleItemBean?.tagIds?.forEach { tagId ->
                    tags?.let { tagItem ->
                        val index = tagItem.indexOfFirst { item -> tagId == item.tagId }
                        if (index >= 0) tagItem[index].isCheck = true
                    }
                }
                mAdapter.setList(tags)
                btnIsClick()
            }
        }
    }
    /**
     * 创建分类弹窗
    * */
    private fun createPop(){
        circleTypeArr?.let {
            if(pop==null){
                pop=CircleSelectTypePop(this,it,object : OnSelectedBackListener{
                    override fun onSelectedBackListener(itemBean: NewCirceTagBean?) {
                        typeId=itemBean?.id
                        binding.edtCircleTypeValue.setText(itemBean?.name)
                        btnIsClick()
                    }
                })
            }
            pop?.showPopupWindow()
        }
    }
}