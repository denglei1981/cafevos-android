package com.changanford.home.adapter

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.ui.release.LocationMMapActivity
import com.changanford.common.MyApp
import com.changanford.common.adapter.CarHomeHistoryAdapter
import com.changanford.common.adapter.LabelAdapter
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.AuthorBaseVo
import com.changanford.common.bean.InfoFlowTopicVoBean
import com.changanford.common.bean.PostDataBean
import com.changanford.common.bean.RecommendData
import com.changanford.common.bean.SpecialListMainBean
import com.changanford.common.buried.BuriedUtil
import com.changanford.common.constant.preLoadNumber
import com.changanford.common.databinding.HeaderCarHistoryBinding
import com.changanford.common.databinding.ItemHomeActsBinding
import com.changanford.common.net.ApiClient
import com.changanford.common.net.body
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.router.startARouter
import com.changanford.common.ui.dialog.AlertDialog
import com.changanford.common.ui.dialog.AlertThreeFilletDialog
import com.changanford.common.util.CountUtils
import com.changanford.common.util.DisplayUtil
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.ext.setCircular
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.util.gio.updatePersonalData
import com.changanford.common.util.image.ItemCommonPics
import com.changanford.common.util.imageAndTextView
import com.changanford.common.util.launchWithCatch
import com.changanford.common.util.request.actionLike
import com.changanford.common.utilext.GlideUtils.loadCompress
import com.changanford.common.utilext.PermissionPopUtil
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.load
import com.changanford.common.utilext.setDrawableLeft
import com.changanford.common.utilext.setDrawableNull
import com.changanford.common.utilext.setDrawableRight
import com.changanford.common.utilext.toast
import com.changanford.common.utilext.toastShow
import com.changanford.home.R
import com.changanford.home.api.HomeNetWork
import com.changanford.home.databinding.ItemHomeRecommendAdsBinding
import com.changanford.home.databinding.ItemHomeRecommendItemsOneBinding
import com.changanford.home.databinding.ItemRecommendHomeSpecialBinding
import com.changanford.home.util.LoginUtil
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permissions


class RecommendAdapter(var lifecycleOwner: LifecycleOwner) :
    BaseMultiItemQuickAdapter<RecommendData, BaseViewHolder>(), LoadMoreModule {
    init {
        addItemType(0, R.layout.item_home_recommend_items_one)
        addItemType(1, R.layout.item_home_recommend_items_one)
        addItemType(2, R.layout.item_home_recommend_items_one)
//        addItemType(3, R.layout.item_home_recommend_acts)
        addItemType(3, com.changanford.common.R.layout.item_home_acts)
        //话题
        addItemType(4, R.layout.header_car_history)
        //专题
        addItemType(5, R.layout.item_recommend_home_special)
        //广告位
        addItemType(6, R.layout.item_home_recommend_ads)
        loadMoreModule.preLoadNumber = preLoadNumber
    }


    override fun convert(holder: BaseViewHolder, item: RecommendData) {
        val picLists = item.getPicLists()
        when (item.itemType) {
            1, 2 -> {//1张图
                showPics(holder, item)
                val binding =
                    DataBindingUtil.bind<ItemHomeRecommendItemsOneBinding>(holder.itemView)
                binding?.let {
                    ItemCommonPics.setItemCommonPics(binding.layoutContent.layoutPics, picLists)
                }

            }

            3 -> { // 活动
//                showActs(holder, item)
                try {
                    showActsNew(holder, item)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            4 -> {//提车日记
                val binding = DataBindingUtil.bind<HeaderCarHistoryBinding>(holder.itemView)
                binding?.ivBg?.setBackgroundResource(R.mipmap.ic_car_history_bg_two)
                binding?.let { item.infoFlowTopicVo?.let { it1 -> showCarHistory(it, it1) } }
            }

            5 -> {//专题
                val binding = DataBindingUtil.bind<ItemRecommendHomeSpecialBinding>(holder.itemView)
                binding?.let { setSpecial(it, item.specialList) }
            }

            6 -> {//广告位
                val binding = DataBindingUtil.bind<ItemHomeRecommendAdsBinding>(holder.itemView)
                binding?.run {
                    ivAds.setCircular(12)
                    ivAds.load(item.adBean?.adImg)
                    ivAds.setOnClickListener {
                        JumpUtils.instans?.jump(
                            item.adBean?.jumpDataType,
                            item.adBean?.jumpDataValue
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showActsNew(holder: BaseViewHolder, recdate: RecommendData) { //活动
        val item = recdate.wonderful
        val binding = DataBindingUtil.bind<ItemHomeActsBinding>(holder.itemView)
        binding?.let {
            it.ivActs.loadCompress(item.coverImg)
//            it.root.setOnClickListener {
//                JumpUtils.instans?.jump(item.jumpDto.jumpCode,item.jumpDto.jumpVal)
//            }
            it.root.setPadding(
                DisplayUtil.dip2px(BaseApplication.INSTANT, 12f),
                0,
                DisplayUtil.dip2px(BaseApplication.INSTANT, 12f),
                DisplayUtil.dip2px(BaseApplication.INSTANT, 12f)
            )
            it.root.background = null
            it.tvTips.text = item.title
            it.tvHomeActTimes.text = item.getActTimeS()
            it.btnState.isVisible = !item.activityTag.isNullOrEmpty()
            it.btnState.text = item.showTag()
            it.tvHomeActAddress.isVisible = !item.activityAddr.isNullOrEmpty()
            it.tvHomeActAddress.text = item.getAddress()
            it.tvSignpeople.isVisible = !item.activityTotalCount.isNullOrEmpty()
//            it.tvSignpeopleImg.isVisible = !item.activityTotalCount.isNullOrEmpty()
            it.tvSignpeople.text = "${item.activityJoinCount}人参与"

            val constraintSet = ConstraintSet()
            constraintSet.clone(it.clContent)
            constraintSet.setDimensionRatio(R.id.iv_acts, "h,343:193")
            constraintSet.applyTo(it.clContent)

            it.bt.isVisible = item.showButton()
            if (item.showButton()) {
                it.bt.text = item.showButtonText()
            }
            val stateBg = it.btnState.background as GradientDrawable
            when (item.activityTag) {
                "NOT_BEGIN" -> stateBg.setColor(
                    ContextCompat.getColor(
                        context,
                        com.changanford.common.R.color.color_E67400
                    )
                )

                "ON_GOING" -> stateBg.setColor(
                    ContextCompat.getColor(
                        context,
                        com.changanford.common.R.color.color_1700f4
                    )
                )

                "SIGN_ING" -> stateBg.setColor(
                    ContextCompat.getColor(
                        context,
                        com.changanford.common.R.color.color_009987
                    )
                )

                else -> stateBg.setColor(
                    ContextCompat.getColor(
                        context,
                        com.changanford.common.R.color.color_a5adb1
                    )
                )
            }
            if (item.buttonBgEnable()) {
                it.bt.background = ContextCompat.getDrawable(
                    context,
                    com.changanford.common.R.drawable.bg_1700f4_18
                )
                it.bt.setTextColor(
                    ContextCompat.getColor(
                        context,
                        com.changanford.common.R.color.white
                    )
                )
            } else {
                it.bt.background =
                    ContextCompat.getDrawable(context, com.changanford.common.R.drawable.bg_80a6_18)
                it.bt.setTextColor(
                    ContextCompat.getColor(
                        context,
                        com.changanford.common.R.color.color_4d16
                    )
                )
            }
            it.bt.setOnClickListener {
                if (item.isFinish()) {
                    AlertThreeFilletDialog(BaseApplication.curActivity).builder()
                        .setMsg(
                            "一旦结束将无法恢复，确定结束吗？"
                        )
                        .setCancelable(true)
                        .setPositiveButton("确定", com.changanford.common.R.color.color_01025C) {
                        }
                        .setNegativeButton("取消", com.changanford.common.R.color.color_99) {

                        }.show()

                } else {
                    JumpUtils.instans?.jump(item.jumpDto.jumpCode, item.jumpDto.jumpVal)
                }
            }
            it.butongguo.isVisible = !item.reason.isNullOrEmpty()
            it.reason.text = item.reason ?: ""
            it.reedit.setOnClickListener {
            }
            it.reedit.isVisible = item.showReedit()
        }
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun showPics(holder: BaseViewHolder, item: RecommendData) { // 图片
        val binding = DataBindingUtil.bind<ItemHomeRecommendItemsOneBinding>(holder.itemView)
        binding?.let {
            val ivHeader = it.layoutHeader.ivHeader
            val ivVip = it.layoutHeader.ivVip
            val tvAuthorName = it.layoutHeader.tvAuthorName
            val tvSubtitle = it.layoutHeader.tvSubTitle
            val tvContent = it.layoutContent.tvContent
            val tvTopic = it.layoutContent.tvTopic
            val btnFollow = it.layoutHeader.btnFollow
//            val tvNewsTag = it.layoutContent.tvNewsTag
            val tvVideoTime = it.layoutContent.tvVideoTimes
            val ivPlay = it.layoutContent.ivPlay
            val tvLikeCount = it.layoutCount.tvLikeCount
            val rvUserTag = it.layoutHeader.rvUserTag
            val tvCommentCount = it.layoutCount.tvComments
            val tvViewCount = it.layoutCount.tvViewCount
            val tvPostTime = it.layoutCount.tvPostTime
            val city = it.layoutCount.tvLocation
            val tvPostTopic = it.layoutContent.tvPostTopic
            val tvCircle = it.layoutContent.tvCircle

            ivHeader.loadCompress(item.authors?.avatar)
            ivVip.isVisible = !item.authors?.memberIcon.isNullOrEmpty()
            ivVip.load(item.authors?.memberIcon)

            tvAuthorName.text = item.authors?.nickname
            if (TextUtils.isEmpty(item.authors?.getMemberNames())) {
                tvSubtitle.visibility = View.GONE
            } else {
                tvSubtitle.visibility = View.VISIBLE
            }
            tvSubtitle.text = item.authors?.getMemberNames()
            ivHeader.setOnClickListener {
                toUserHomePage(item)
            }
            tvAuthorName.setOnClickListener {
                toUserHomePage(item)
            }

            if (item.postsIsGood != null && item.postsIsGood == 1) {
                if (TextUtils.isEmpty(item.getTopic())) {
                    tvContent.visibility = View.GONE
                } else {
                    tvContent.visibility = View.VISIBLE
                    tvContent.imageAndTextView(
                        item.getTopic(),
                        R.mipmap.ic_home_refined_item
                    )
//                    tvContent.text = item.getTopic()
                }
                if (TextUtils.isEmpty(item.getContent())) {
                    tvTopic.text = ""
                    tvTopic.visibility = View.GONE
                } else {
                    tvTopic.visibility = View.VISIBLE
                    if (TextUtils.isEmpty(item.getTopic())) {
                        tvTopic.imageAndTextView(
                            item.getContent(),
                            R.mipmap.ic_home_refined_item
                        )
                    } else {
                        tvTopic.text = item.getContent()
                    }
                }
            } else {
                if (TextUtils.isEmpty(item.getTopic())) {
                    tvContent.visibility = View.GONE
                } else {
                    tvContent.visibility = View.VISIBLE
                    tvContent.text = item.getTopic()
                }
                if (TextUtils.isEmpty(item.getContent())) {
                    tvTopic.text = ""
                    tvTopic.visibility = View.GONE
                } else {
                    tvTopic.visibility = View.VISIBLE
                    tvTopic.text = item.getContent()
                }
            }
            setLikeState(tvLikeCount, item.isLike, false) // 设置是否喜欢。
            tvLikeCount.setOnClickListener {
                when (item.rtype) {
                    1 -> { // 点赞资讯。
                        if (LoginUtil.isLongAndBindPhone()) {
                            if (item.authors != null) {
                                actionLike(lifecycleOwner, item.artId) {
                                    if (item.isLike == 0) {
                                        item.isLike = 1
                                        val likesCount = item.likeCount.plus(1)
                                        item.likeCount = likesCount
                                        tvLikeCount.text =
                                            CountUtils.formatNum(likesCount.toString(), false)
                                                .toString()
                                        "点赞成功".toast()
                                        GIOUtils.infoLickClick(
                                            "发现-推荐",
                                            item.artSpecialTopicTitle,
                                            item.artId,
                                            item.artTitle
                                        )
                                    } else {
                                        item.isLike = 0
                                        val likesCount = item.likeCount.minus(1)
                                        item.likeCount = likesCount
                                        tvLikeCount.text = (
                                                CountUtils.formatNum(
                                                    likesCount.toString(),
                                                    false
                                                ).toString()
                                                )
                                        "取消点赞".toast()
                                        GIOUtils.cancelInfoLickClick(
                                            "发现-推荐",
                                            item.artSpecialTopicTitle,
                                            item.artId,
                                            item.artTitle
                                        )
                                    }
                                    setLikeState(tvLikeCount, item.isLike, true)
                                }
                            }
                        }
                    }

                    2 -> {// 点赞帖子
                        if (LoginUtil.isLongAndBindPhone()) {
                            likePost(tvLikeCount, item)
                        }
                    }
                }
            }
            tvLikeCount.text = (item.getLikeCount())
            item.authors?.let {
                setFollowState(btnFollow, it)
            }

            if (item.authors?.authorId == MConstant.userId) {
                btnFollow.visibility = View.GONE
            } else {
                btnFollow.visibility = View.VISIBLE
            }

            btnFollow.setOnClickListener {
                // 判断是否登录。
                if (LoginUtil.isLongAndBindPhone()) {
                    if (item.authors != null) {
                        followAction(btnFollow, item.authors!!, holder.adapterPosition)
                    }
                }
            }
            when (item.rtype) {
                1 -> {// 资讯
//                    tvNewsTag.visibility = View.VISIBLE
                    if (!TextUtils.isEmpty(item.artVideoTime)) {
                        tvVideoTime.text = item.artVideoTime
                    }

                    tvVideoTime.visibility = View.VISIBLE
//                    tvNewsTag.text = "资讯"
                    ivPlay.visibility = if (item.isArtVideoType()) View.VISIBLE else View.GONE
                    tvVideoTime.visibility = if (item.isArtVideoType()) View.VISIBLE else View.GONE
                    tvPostTopic.isVisible = false
                    tvCircle.isVisible = false
                }

                2 -> {// 帖子
                    if (item.postsTopicName.isNullOrEmpty()) {
                        tvPostTopic.isVisible = false
                    } else {
                        tvPostTopic.isVisible = true
                        tvPostTopic.text = item.postsTopicName
                        tvPostTopic.setOnClickListener {
                            val bundle = Bundle()
                            bundle.putString("topicId", item.postsTopicId)
                            GioPageConstant.topicEntrance = "推荐"
                            updatePersonalData("推荐", "推荐页")
                            startARouter(ARouterCirclePath.TopicDetailsActivity, bundle)
                        }
                    }
                    if (item.postsCircleName.isNullOrEmpty()) {
                        tvCircle.isVisible = false
                    } else {
                        tvCircle.isVisible = true
                        tvCircle.text = item.postsCircleName
                        tvCircle.setOnClickListener {
                            val bundle = Bundle()
                            bundle.putString("circleId", item.postsCircleId)
                            startARouter(ARouterCirclePath.CircleDetailsActivity, bundle)
                        }
                    }
                    ivPlay.visibility = if (item.postsType == 3) View.VISIBLE else View.GONE
                    if (item.postsType == 3) {
                        tvVideoTime.isVisible = true
                        tvVideoTime.text = item.postsVideoTime
                    } else if (item.pisList != null) {
                        if (item.pisList!!.size > 4) {
                            tvVideoTime.isVisible = true
                            tvVideoTime.text = "+${item.pisList!!.size - 4}"
                        } else {
                            tvVideoTime.isVisible = false
                        }
                    } else {
                        tvVideoTime.isVisible = false
                    }
                }

                else -> {
//                    tvNewsTag.visibility = View.GONE
                    tvVideoTime.visibility = View.GONE
                    ivPlay.visibility = View.GONE
                }

            }
            if (item.authors != null) {
                val labelAdapter = LabelAdapter(16)
                rvUserTag.adapter = labelAdapter
                labelAdapter.setNewInstance(item.authors?.imags)
            }
            if (!item.addrName.isNullOrEmpty()) {
                city.setDrawableLeft(R.mipmap.icon_circle_location)
                city.text = item.addrName
                city.isVisible = true
            } else if (!item.city.isNullOrEmpty()) {
                city.setDrawableNull()
                city.text = item.city
                city.isVisible = true
            } else {
                city.isVisible = false
            }
            tvCommentCount.text = (item.getCommentCount())
            tvPostTime.text = item.timeStr
            tvViewCount.text = item.getViewCount()
            tvCommentCount.setOnTouchListener { v, event ->
                when (item.rtype) {
                    1 -> {//资讯
                        GIOUtils.clickCommentInfo(
                            "发现-推荐",
                            item.artSpecialTopicTitle,
                            item.artId,
                            item.artTitle
                        )
                    }

                    2 -> {//帖子
                        GIOUtils.clickCommentPost(
                            "发现-推荐",
                            item.postsTopicId,
                            item.postsTopicName,
                            item.authors?.authorId,
                            item.postsId,
                            item.title,
                            item.postsCircleId,
                            item.postsCircleName
                        )
                    }
                }
                false
            }
            if (tvContent.isGone && tvTopic.isVisible) {
                tvTopic.setTextColor(
                    ContextCompat.getColor(
                        context,
                        com.changanford.circle.R.color.color_d916
                    )
                )
            } else {
                tvTopic.setTextColor(
                    ContextCompat.getColor(
                        context,
                        com.changanford.circle.R.color.color_8016
                    )
                )
            }
        }
//        GlideUtils.loadBD(item.authors?.avatar, ivHeader)
//        val tvTimeAndViewCount = holder.getView<TextView>(R.id.tv_time_look_count)
//        tvTimeAndViewCount.text = item.getTimeAdnViewCount()
    }

    private fun startBaduMap(mData: PostDataBean) {

        val permissions = Permissions.build(
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
        val success = {
            val intent = Intent()
            intent.putExtra("lat", mData.lat)
            intent.putExtra("lon", mData.lon)
            intent.putExtra("address", mData.address)
            intent.putExtra("addrName", mData.showCity())
            intent.setClass(MyApp.mContext, LocationMMapActivity::class.java)
            context.startActivity(intent)
        }
        val fail = {
            AlertDialog(MyApp.mContext).builder()
                .setTitle("提示")
                .setMsg("您已禁止了定位权限，请到设置中心去打开")
                .setNegativeButton("取消") { }.setPositiveButton(
                    "确定"
                ) { SoulPermission.getInstance().goPermissionSettings() }.show()
        }
        PermissionPopUtil.checkPermissionAndPop(permissions, success, fail)
    }

    private fun showCarHistory(
        hCarHistoryBinding: HeaderCarHistoryBinding,
        bean: InfoFlowTopicVoBean
    ) {
        hCarHistoryBinding.let {
            it.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.color_16))
            it.tvContent.setTextColor(ContextCompat.getColor(context, R.color.color_16))
            it.tvMore.setTextColor(ContextCompat.getColor(context, R.color.color_16))
            it.tvMore.setDrawableRight(R.mipmap.circle_right_black_small)
            it.ivIcon.setCircular(5)
            it.ivIcon.setBackgroundResource(R.mipmap.ic_car_history_bg_two)
            it.ivIcon.loadCompress(bean.pic)
            it.tvTitle.text = bean.name
            it.tvContent.text = bean.description
            it.tvMore.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("topicId", bean.topicId)
//                bundle.putString("carModelId", MConstant.carBannerCarModelId)
                startARouter(ARouterCirclePath.TopicDetailsActivity, bundle)
            }
            val adapter = CarHomeHistoryAdapter()
            adapter.setOnItemClickListener { _, view, position ->
                JumpUtils.instans?.jump(4, adapter.data[position].postsId.toString())
            }
            it.ryPost.adapter = adapter
            adapter.setList(bean.postsList)
        }
    }

    private fun setSpecial(
        binding: ItemRecommendHomeSpecialBinding,
        specialList: SpecialListMainBean?
    ) {
        specialList?.let {
            val specialAdapter = RecommendSpecialAdapter()
            binding.rySpecial.layoutManager =
                GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
            binding.rySpecial.adapter = specialAdapter
            specialAdapter.setOnItemClickListener { adapter, view, position ->
                val bean = specialAdapter.getItem(position)
                JumpUtils.instans?.jump(8, bean.artId)
            }
            specialAdapter.data.clear()
            specialAdapter.setNewInstance(specialList.dataList)
//            val size = specialList.dataList.size / 4
//            val remainder = specialList.dataList.size % 4
//            val pageSize = if (remainder == 0) size else size + 1
//            binding.drIndicator.setPageSize(pageSize)
//            binding.drIndicator.isVisible = pageSize > 1

//            binding.rySpecial.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                    super.onScrolled(recyclerView, dx, dy)
//                    val layoutManager = recyclerView.layoutManager as GridLayoutManager
//                    val fistVisibilityPosition = layoutManager.findLastVisibleItemPosition()
//                    val current = fistVisibilityPosition / 4
//                    binding.drIndicator.post {
//                        binding.drIndicator.onPageSelected(current)
//                    }
//                }
//            })
//            setIndicator(binding)

            binding.tvMore.setOnClickListener {
                startARouter(ARouterHomePath.SpecialListActivity)
            }
        }

    }

    private fun setIndicator(binding: ItemRecommendHomeSpecialBinding) {
        val dp6 = context.resources.getDimensionPixelOffset(R.dimen.dp_6)
        binding.drIndicator.setIndicatorDrawable(
            R.drawable.shape_home_banner_normal,
            R.drawable.shape_home_banner_focus
        ).setIndicatorSize(
            dp6,
            dp6,
            context.resources.getDimensionPixelOffset(R.dimen.dp_20),
            dp6
        )
            .setIndicatorGap(context.resources.getDimensionPixelOffset(R.dimen.dp_5))
    }

    private fun toUserHomePage(item: RecommendData) {
        JumpUtils.instans!!.jump(35, item.authors?.authorId.toString())
    }

    /**
     *  设置关注状态。
     * */
    fun setFollowState(btnFollow: TextView, authors: AuthorBaseVo) {
        val setFollowState = com.changanford.common.util.SetFollowState(context)
        authors.let {
            setFollowState.setFollowState(btnFollow, it, true)
        }
    }


    // 关注或者取消
    private fun followAction(btnFollow: TextView, authorBaseVo: AuthorBaseVo, position: Int) {

        var followType = authorBaseVo.isFollow
        followType = if (followType == 1) 2 else 1
        getFollow(authorBaseVo.authorId, followType)
        if (followType == 1) {
            // 埋点 关注
            BuriedUtil.instant?.discoverFollow(authorBaseVo.nickname)
            GIOUtils.followClick(authorBaseVo.authorId, authorBaseVo.nickname, "发现-推荐")
        } else {
            GIOUtils.cancelFollowClick(authorBaseVo.authorId, authorBaseVo.nickname, "发现-推荐")
        }

    }

    // 关注。
    private fun getFollow(followId: String, type: Int) {
        lifecycleOwner.launchWithCatch {
            val requestBody = HashMap<String, Any>()
            requestBody["followId"] = followId
            requestBody["type"] = type
            val rkey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .followOrCancelUser(requestBody.header(rkey), requestBody.body(rkey))
                .onSuccess {
                    if (type == 1) {
                        "已关注".toast()
                    } else {
                        "取消关注".toast()
                    }
                    notifyAtt(followId, type)
                }.onWithMsgFailure {
                    it?.let { it1 -> toastShow(it1) }
                }
        }
    }

    //关注
    fun notifyAtt(userId: String, isFollow: Int) {
        for (data in this.data) {
            if (data.authors?.authorId == userId) {
                data.authors?.isFollow = isFollow
            }
        }
        this.notifyDataSetChanged()
    }

    private fun setLikeState(tvLikeView: TextView, isLike: Int, isAnim: Boolean) {
        if (isLike == 0) {
            tvLikeView.setDrawableLeft(R.mipmap.item_good_count_ic)
        } else {
            tvLikeView.setDrawableLeft(R.mipmap.item_good_count_light_ic)
        }
    }


    private fun likePost(tvLikeView: TextView, item: RecommendData) {
        val activity = BaseApplication.curActivity as AppCompatActivity
        activity.launchWithCatch {
            val body = MyApp.mContext.createHashMap()
            body["postsId"] = item.postsId
            val rKey = getRandomKey()
            ApiClient.createApi<HomeNetWork>()
                .actionPostLike(body.header(rKey), body.body(rKey)).also {
                    if (it.code == 0) {
                        it.msg.toast()
                        if (item.isLike == 0) {
                            GIOUtils.postLickClick(
                                "发现-推荐",
                                item.postsTopicId,
                                item.postsTopicName,
                                item.authors?.authorId,
                                item.postsId,
                                item.title,
                                item.postsCircleId,
                                item.postsCircleName
                            )
                            "点赞成功".toast()
                            item.isLike = 1
                            tvLikeView.setDrawableLeft(R.mipmap.item_good_count_light_ic)
                            item.postsLikesCount++
                        } else {
                            GIOUtils.cancelPostLickClick(
                                "发现-推荐",
                                item.postsTopicId,
                                item.postsTopicName,
                                item.authors?.authorId,
                                item.postsId,
                                item.title,
                                item.postsCircleId,
                                item.postsCircleName
                            )
                            "取消点赞".toast()
                            item.isLike = 0
                            item.postsLikesCount--
                            tvLikeView.setDrawableLeft(R.mipmap.item_good_count_ic)
                        }
                        tvLikeView.text = item.getLikeCount()
                    } else {
                        it.msg.toast()
                    }
                }
        }
    }


}
