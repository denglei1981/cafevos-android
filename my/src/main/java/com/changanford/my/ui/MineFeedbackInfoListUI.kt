package com.changanford.my.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.bean.FeedbackInfoItem
import com.changanford.common.bean.FeedbackMemberBean
import com.changanford.common.chat.adapter.BottomMenuAdapter
import com.changanford.common.chat.adapter.ChatAdapter
import com.changanford.common.chat.bean.BottomMenuBean
import com.changanford.common.chat.bean.MessageBean
import com.changanford.common.chat.bean.MessageImageBody
import com.changanford.common.chat.bean.MessageStatus
import com.changanford.common.chat.bean.MessageTextBody
import com.changanford.common.chat.bean.MessageType
import com.changanford.common.chat.utils.ChatUiHelper
import com.changanford.common.chat.utils.LogUtil
import com.changanford.common.chat.utils.Utils
import com.changanford.common.net.CommonResponse
import com.changanford.common.net.onFailure
import com.changanford.common.net.onSuccess
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouterForResult
import com.changanford.common.util.ConfirmTwoBtnPop
import com.changanford.common.util.MConstant
import com.changanford.common.util.MineUtils
import com.changanford.common.util.PictureUtil
import com.changanford.common.util.PictureUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.toastShow
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.UiFeedbackInfoBinding
import com.changanford.my.interf.UploadPicCallback
import com.changanford.my.viewmodel.SignViewModel
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import java.util.UUID

/**
 *  文件名：MineFeedbackInfoListUI1
 *  创建者: zcy
 *  创建日期：2020/10/15 18:29
 *  描述: TODO
 *  修改描述：TODO
 */

@Route(path = ARouterMyPath.MineFeedbackInfoListUI)
class MineFeedbackInfoListUI : BaseMineUI<UiFeedbackInfoBinding, SignViewModel>(),
    View.OnClickListener {


    var userFeedbackId: String = ""

    var feedbackMember: FeedbackMemberBean? = null

    lateinit var questOff: TextView

    lateinit var bottomView: View

    var mReceiveMsgList: ArrayList<MessageBean> = ArrayList()

    var startTime: Long = 0L //记录时间
    var headUrl: String = ""//用户自己头像地址

    lateinit var feedbackHint: TextView

    var feedbackParam = HashMap<String, Any>()

    val adapter: ChatAdapter by lazy {
        ChatAdapter(this)
    }

    val mUiHelper: ChatUiHelper by lazy {
        ChatUiHelper.with(this)
    }

    val menuAdapter: BottomMenuAdapter by lazy {
        BottomMenuAdapter()
    }

    companion object {
        private const val REQUEST_PIC = 0x5432//图片
    }

    override fun back() {
        //刷新列表
        LiveDataBus.get().with(LiveDataBusKey.MINE_SUBMIT_FEEDBACK_SUCCESS, Boolean::class.java)
            .postValue(true)
        super.back()
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
//        SoftHideKeyBoardUtil.assistActivity(this)

        binding.mineToolbar.toolbarTitle.text = "我要反馈"
        binding.mineToolbar.toolbar.setNavigationOnClickListener { back() }

        intent?.let {
            it.extras?.let {
                userFeedbackId = it.getString("value", "")
            }
        }

        var layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        layoutManager.stackFromEnd = true

        binding.chatRv.layoutManager = layoutManager
        binding.chatRv.adapter = adapter

        mUiHelper.bindBottomLayout(binding.bottomLayout)
            .bindInputEditText(binding.input)
            .bindAddView(binding.add)
            .bindSendView(binding.btnSend)
            .bindContentLayout(binding.contentLayout)

        binding.btnSend.setOnClickListener(this)

        viewModel.feedbackInfo.observe(this, {
            it.let { feedbackInfo ->
                //反馈状态,0 待回复 ，1 已回复, 2 已关闭
                if (feedbackInfo.feedbackStatus == 2) {
                    feedbackHint.visibility = View.VISIBLE
                    binding.bottomSendLayout.visibility = View.GONE
                    binding.bottomLayout.visibility = View.GONE
                    feedbackHint.text = "${feedbackInfo.closeReason}"

                    binding.input.isEnabled = false
                    binding.add.isEnabled = false
                    mUiHelper.hideSoftInput()

                    questOff.visibility = View.GONE
                } else {
                    questOff.visibility = View.VISIBLE
                    binding.input.isEnabled = true
                    binding.add.isEnabled = true
                    questOff.setOnClickListener(this)
                }

                mReceiveMsgList.clear()
                adapter.data.clear()

                it.item.forEach { item ->
                    //内容类型 1：反馈，2：回复	 messageType
                    if (!item.messageContent.isNullOrEmpty()) {
                        headUrl = feedbackInfo.avatar
                        var message =
                            getBaseSendMessage(MessageType.TEXT, item.messageType, item.createTime)
                        message.messageStatus = MessageStatus.MESSAGE_SUCCESS

                        var textMsgBody = MessageTextBody(item.messageContent ?: "")
                        message.messageBody = textMsgBody
                        mReceiveMsgList.add(message)
                    }

                    if (!item.messageImg.isNullOrEmpty()) {
                        //多图
                        var imgs = item.messageImg.split(",")
                        imgs.let {
                            it.forEachIndexed { index, s ->
                                if (!s.isNullOrEmpty()) {
                                    var img = item.copy(
                                        messageImg = s
                                    )
                                    buildImageBody(img, feedbackInfo.avatar)
                                }
                            }
                        }
                    }
                }

                adapter.addData(mReceiveMsgList)
                chatRvPost()
            }
        })

        //底部布局弹出,聊天列表上滑
        binding.chatRv.addOnLayoutChangeListener(View.OnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom) {
                chatRvPost()
            }
        })
        //点击列表 隐藏底部或者键盘
        binding.chatRv.setOnTouchListener(OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                mUiHelper.hideBottomLayout(100)
                mUiHelper.hideSoftInput()
            }
            false
        })

        bottomView = layoutInflater.inflate(R.layout.view_bottom_feedback, null)
        questOff = bottomView.findViewById(R.id.quest_off)
        feedbackHint = bottomView.findViewById(R.id.feedback_hint)
        adapter.addFooterView(bottomView)

        binding.bottomMenuRv.layoutManager = GridLayoutManager(this, 2)
        binding.bottomMenuRv.adapter = menuAdapter

        menuAdapter.addData(BottomMenuBean("相机", R.mipmap.icon_feedback_take))
        menuAdapter.addData(BottomMenuBean("相册", R.mipmap.icon_feedback_photo))

        menuAdapter.setOnItemClickListener { adapter, view, position ->
            when (position) {
                0 -> {
                    takePhoto()
                }

                1 -> {
                    pic()
                }
            }
        }
    }

    override fun initData() {
        viewModel.queryMemberNickName {
            it.onSuccess {
                feedbackMember = it
                viewModel.queryFeedbackInfoList(1, userFeedbackId)
            }.onFailure {
                viewModel.queryFeedbackInfoList(1, userFeedbackId)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_send -> {
                sendTextMsg(binding.input.text.toString())
                binding.input.setText("")
            }

            R.id.quest_off -> {
                val cannotUnbindPop = ConfirmTwoBtnPop(this)
                cannotUnbindPop.apply {
                    contentText.text = "你即将关闭该问题反馈，如后续有其他新问题再次提交意见反馈"
                    contentText.textSize=14f
                    contentText.setTextColor(ContextCompat.getColor(this@MineFeedbackInfoListUI,R.color.color_9916))
                    btnCancel.text = "暂不关闭"
                    btnConfirm.text = "继续关闭"
                    title.text = "即将结束问题反馈"
                    title.isVisible = true
                    title.setTextColor(
                        ContextCompat.getColor(
                            this@MineFeedbackInfoListUI,
                            R.color.color_16
                        )
                    )
                    btnCancel.setOnClickListener {
                        dismiss()
                    }
                    btnConfirm.setOnClickListener {
                        questOff()
                        dismiss()
                    }
                    showPopupWindow()
                }
//                var pop = ConfirmPop(this)
//                pop.contentText.text = "你即将关闭该问题反馈，如后续有其他新问题再次提交意见反馈"
//                pop.cancelBtn.text = "暂不关闭"
//                pop.submitBtn.text = "确认关闭"
//                pop.submitBtn.setOnClickListener {
//                    pop.dismiss()
//                    questOff()
//                }
//                pop.showPopupWindow()
            }
        }
    }

    /**
     * 滚动到列表底部
     */
    private fun chatRvPost(isAdd: Boolean = false) {
        binding.chatRv.postDelayed(Runnable {
            if (isAdd) {
                try {
                    adapter.addFooterView(bottomView)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            if (adapter.itemCount > 0) {
                binding.chatRv.smoothScrollToPosition(adapter.itemCount - 1)
            }
        }, 500)
    }


    /**
     * 创建文本消息
     */
    private fun getBaseSendMessage(
        msgType: MessageType,
        messageType: Int = 1,
        messageTime: Long = System.currentTimeMillis()
    ): MessageBean {
        val mMessgae = MessageBean()
        mMessgae.uuid = "${UUID.randomUUID().toString()}+${System.currentTimeMillis()}"
        mMessgae.isOneselfSend = messageType == 1

        if (MineUtils.timeFiveS(startTime, messageTime)) {
            //相差5分钟 显示时间
            startTime = messageTime
            mMessgae.sendTime = startTime
        } else {
            mMessgae.sendTime = 0L //不显示时间
        }

        mMessgae.headIcon = if (messageType == 1) headUrl else feedbackMember?.avatar
        mMessgae.nickName = feedbackMember?.nickname
        mMessgae.messageStatus = MessageStatus.MESSAGE_SUCCESS
        mMessgae.messageType = msgType
        return mMessgae
    }


    /**
     * 拍照
     */
    fun takePhoto() {
        PictureUtils.opencarcme(
            this@MineFeedbackInfoListUI,
            object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: List<LocalMedia?>) {
                    uploadFile(result as List<LocalMedia>)
                }

                override fun onCancel() {
                    // 取消
                }
            })
    }


    /**
     * 选择图片
     */
    fun pic() {
        PictureUtils.openGarlly5(this@MineFeedbackInfoListUI, object :
            OnResultCallbackListener<LocalMedia?> {
            override fun onResult(result: List<LocalMedia?>) {
                val bundle = Bundle()
                bundle.putInt("position", 0)
                bundle.putInt("showEditType", 312)
                bundle.putParcelableArrayList(
                    "picList",
                    arrayListOf(*result.toTypedArray())
                )
                startARouterForResult(
                    this@MineFeedbackInfoListUI,
                    ARouterCirclePath.PictureeditlActivity,
                    bundle,
                    MineFeedbackInfoListUI.REQUEST_PIC
                )
            }

            override fun onCancel() {

            }
        })
    }

    /**
     * 创建图片数据
     */
    fun buildImageBody(itemInfo: FeedbackInfoItem, avatar: String) {
        var message =
            getBaseSendMessage(MessageType.IMAGE, itemInfo.messageType, itemInfo.createTime)
        message.headIcon = if (itemInfo.messageType == 1) avatar else feedbackMember?.avatar
        message.nickName = feedbackMember?.nickname
        message.messageStatus = MessageStatus.MESSAGE_SUCCESS
        var imageU =
            if (Utils.isHttpOrHttps(itemInfo.messageImg)) itemInfo.messageImg else "${MConstant.imgcdn}${itemInfo.messageImg}"
        LogUtil.d("$imageU")
        var imageMsgBody = MessageImageBody(imageU)
        message.messageBody = imageMsgBody
        mReceiveMsgList.add(message)
    }


    /**
     * 用户主动关闭问题
     */
    fun questOff() {
        viewModel.closeFeedback(userFeedbackId) {
            it.onSuccess {
                startTime = 0L
                viewModel.queryFeedbackInfoList(1, userFeedbackId)
            }.onFailure {
                toastShow(it ?: "")

            }
        }
    }

    /**
     * 发送文本消息
     */
    private fun sendTextMsg(hello: String) {
        val mMessgae: MessageBean = getBaseSendMessage(MessageType.TEXT)
        mMessgae.headIcon = headUrl
        val mTextMsgBody = MessageTextBody("$hello")
        mMessgae.messageBody = mTextMsgBody
        mMessgae.messageStatus = MessageStatus.MESSAGE_LOADING
        //开始发送
        adapter.addData(mMessgae)
        //发送成功
        updateMsg(mMessgae)
    }


    /**
     * 上传文本消息
     */
    private fun updateMsg(mMessgae: MessageBean) {
        feedbackParam["userFeedbackId"] = userFeedbackId
        feedbackParam["messageContent"] = (mMessgae.messageBody as MessageTextBody).messageText
        feedbackParam["messageType"] = 1
        feedbackParam.remove("messageImg")
        uploadFeedback() {
            it.onSuccess {
                sendMessage(mMessgae, true)
            }.onFailure {
                toastShow(it ?: "")
                sendMessage(mMessgae, false)
            }
        }
    }

    /**
     * 更新反馈
     */
    fun uploadFeedback(callback: (CommonResponse<String>) -> Unit) {
        viewModel.addFeedbackInfo(feedbackParam, callback)
    }

    /**
     * 提交数据后更新
     */
    fun sendMessage(mMessgae: MessageBean, isSuccess: Boolean) {
        chatRvPost()
        mMessgae.messageStatus =
            (if (isSuccess) MessageStatus.MESSAGE_SUCCESS else MessageStatus.MESSAGE_FAILED)
        //更新单个子条目
        for (i in 0 until adapter.data.size) {
            val mAdapterMessage: MessageBean = adapter.data[i]
            if (mMessgae.uuid == mAdapterMessage.uuid) {
                if (isSuccess) {
                    adapter.notifyItemChanged(i)
                } else {
                    adapter.remove(i)
                }
            }
        }
    }


    /**
     * 上传图片
     */
    fun uploadFile(media: List<LocalMedia>) {
        mUiHelper.hideBottomLayout(100)
        buildImageMessage(media)
        media.let { medias ->
            viewModel.uploadFileWithWH(medias, object : UploadPicCallback {
                override fun onUploadSuccess(files: ArrayList<String>) {
                    var images: String = ""
                    files.forEach {
                        images += "${it},"
                    }
                    updateImageMsg(images)
                }

                override fun onUploadFailed(errCode: String) {
                    toastShow(errCode)
                    //上传失败删除数据
                    try {
                        updateList.forEach { item ->
                            adapter.remove(item)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    chatRvPost(true)
                }

                override fun onuploadFileprogress(progress: Long) {

                }
            })
        }
    }


    /**
     * 发送图片 上传成功
     */
    var updateList = ArrayList<MessageBean>() //临时存储上传数据

    fun buildImageMessage(files: List<LocalMedia>) {
        var startIndex = adapter.data.size
        adapter.removeAllFooterView()

        updateList.clear()
        files.forEach {
            var mMessage: MessageBean = getBaseSendMessage(MessageType.IMAGE)
            mMessage.messageId = "$startIndex"
            var mImageMsgBody: MessageImageBody = MessageImageBody(PictureUtil.getFinallyPath(it))
            mMessage.messageBody = mImageMsgBody
            adapter.addData(mMessage)
            updateList.add(mMessage)
        }
    }

    /**
     * 更新图片消息
     */
//    fun sendImageMessage(isSuccess: Boolean) {
//        //更新单个子条目
//        for (i in startIndex until adapter.data.size) {
//            val mAdapterMessage: MessageBean = adapter.data[i]
//            mAdapterMessage.messageStatus =
//                if (isSuccess) MessageStatus.MESSAGE_SUCCESS else MessageStatus.MESSAGE_FAILED
//            adapter.notifyItemChanged(i)
//        }
//        chatRvPost(true)
//    }


    /**
     * 更新图片消息
     */
    fun updateImageMsg(pathUrl: String) {
        feedbackParam["userFeedbackId"] = userFeedbackId
        feedbackParam["messageType"] = 1
        feedbackParam["messageImg"] = pathUrl
        feedbackParam.remove("messageContent")
        viewModel.addFeedbackInfo(feedbackParam) {
            it.onSuccess {
                chatRvPost(true)
            }.onFailure {
                toastShow(it ?: "")
                //上传失败删除数据
                try {
                    updateList.forEach { item ->
                        adapter.remove(item)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                chatRvPost(true)
            }
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        back()
        return super.onKeyDown(keyCode, event)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_PIC -> {//选择图片
                    var selectList =
                        data!!.getSerializableExtra("picList") as ArrayList<LocalMedia>
                    uploadFile(selectList)
                }
            }
        }
    }
}