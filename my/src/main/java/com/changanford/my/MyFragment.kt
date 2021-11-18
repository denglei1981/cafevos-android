package com.changanford.my

import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.MenuBeanItem
import com.changanford.common.bean.UserInfoBean
import com.changanford.common.manger.UserManger
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.load
import com.changanford.common.utilext.logE
import com.changanford.common.utilext.setDrawableLeft
import com.changanford.my.adapter.LabelAdapter
import com.changanford.my.adapter.MedalAdapter
import com.changanford.my.adapter.MenuAdapter
import com.changanford.my.databinding.FragmentMyBinding
import com.changanford.my.viewmodel.SignViewModel
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.util.*

class MyFragment : BaseFragment<FragmentMyBinding, SignViewModel>() {
    var menuBean: ArrayList<MenuBeanItem> = ArrayList<MenuBeanItem>()
    private var menuAdapter = MenuAdapter()
    private var medalAdapter = MedalAdapter()
    val labelAdapter: LabelAdapter by lazy {
        LabelAdapter(22)
    }
    private var loginState: MutableLiveData<Boolean> = MutableLiveData()
    private var authState: MutableLiveData<Boolean> = MutableLiveData()

    private var isRefreshUserInfo: Boolean = true //是否刷新用户信息

    override fun initView() {
        binding.daySign.setDrawableLeft(R.mipmap.my_daysign, R.dimen.dp_20)
        binding.memberEnter.setDrawableLeft(R.mipmap.my_member, R.dimen.dp_20)
        binding.medalRec.isSaveEnabled = false
        binding.menuRec.isSaveEnabled = false
        binding.menuRec.adapter = menuAdapter
        binding.medalRec.adapter = medalAdapter
        observeLoginAndAuthState()
        initClick()
        binding.refreshLayout.setOnRefreshListener {
            initData()
            it.finishRefresh()
        }
    }

    /**
     * 监听登录和认证状态
     */
    private fun observeLoginAndAuthState() {
        loginState.observe(this, {
            when (it) {
                false -> {//未登录
                    binding.myScoreLayout.isVisible = false
                    binding.myCarAuthLayout.include.root.isVisible = false
                    binding.myHeadvipimg.isVisible = false
                }
                true -> {//已登录
                    binding.myScoreLayout.isVisible = true
                    binding.myCarAuthLayout.include.root.isVisible = true
                    binding.myHeadvipimg.isVisible = false
                }
            }
        })

        authState.observe(this, {
            if (it) {//已认证
                binding.myCarAuthLayout.include2.myCarauthstate.text =
                    resources.getText(R.string.my_authed)
                binding.myCarAuthLayout.include2.myMylovecar.apply {
                    text = resources.getText(R.string.my_lovecar)
                    setOnClickListener { JumpUtils.instans?.jump(41) }
                }
            } else {//未认证
                binding.myCarAuthLayout.include2.myCarauthstate.text =
                    resources.getText(R.string.my_unauth)
                binding.myCarAuthLayout.include2.myMylovecar.apply {
                    text = resources.getText(R.string.my_goauth)
                    setOnClickListener { JumpUtils.instans?.jump(17) }
                }

            }
        })
        LiveDataBus.get().with(MConstant.REFRESH_USER_INFO, Boolean::class.java)
            .observe(this, {
                if (it) {
                    viewModel.getUserInfo()
                }
            })
    }

    override fun initData() {
        getUserInfo()
        viewModel.getMenuList()
        viewModel.menuBean.observe(this, {
            menuBean.clear()
            menuBean.addAll(it)
            menuAdapter.data = menuBean
            menuAdapter.notifyDataSetChanged()
        })
        lifecycleScope.launch {
            if (MConstant.token.isNotEmpty()) {
                viewModel.mineMedal()
            }
        }
        viewModel.allMedal.observe(this, {
            if (it == null) {
                medalAdapter.data?.clear()
                medalAdapter.notifyDataSetChanged()
            } else {
                medalAdapter.data = it
                medalAdapter.notifyDataSetChanged()
            }
        })
    }

    /**
     * 处理点击事件
     */
    private fun initClick() {
        binding.daySign.setOnClickListener { JumpUtils.instans?.jump(37) }
        binding.mySet.setOnClickListener { JumpUtils.instans?.jump(21) }
        binding.myMsg.setOnClickListener { JumpUtils.instans?.jump(24) }

//        binding.daySign.setOnClickListener { JumpUtils.instans?.jump(39) }
//        binding.mySet.setOnClickListener { JumpUtils.instans?.jump(42) }
//        binding.myMsg.setOnClickListener { JumpUtils.instans?.jump(71) }

        binding.memberEnter.setOnClickListener { JumpUtils.instans?.jump(22) }
        binding.myHead.setOnClickListener {
            JumpUtils.instans?.jump(34)
        }
        binding.myScore.setOnClickListener { JumpUtils.instans?.jump(30) }
        binding.myScoreIc.setOnClickListener { JumpUtils.instans?.jump(30) }
        binding.myScoreLayout.setOnClickListener { JumpUtils.instans?.jump(30) }
        binding.myStateLayout.apply {
            myStateFabu.setOnClickListener { JumpUtils.instans?.jump(23) }
            myStateFensi.setOnClickListener { JumpUtils.instans?.jump(40) }
            myStateGuanzhu.setOnClickListener { JumpUtils.instans?.jump(25) }
            myStateShoucang.setOnClickListener { JumpUtils.instans?.jump(27) }
        }
        binding.myCarAuthLayout.apply {
            include.root.setOnClickListener {
                JumpUtils.instans?.jump(32)
            }
            include.textView10.setOnClickListener {
                JumpUtils.instans?.jump(16)
            }
            include.textView11.setOnClickListener {
                JumpUtils.instans?.jump(32)
            }
//            include2.myMylovecar.setOnClickListener {
//                RouterManger.startARouter(ARouterMyPath.MineLoveCarListUI)
//            }
        }
    }

    /**
     * 设置值
     */
    private fun setData(userInfoBean: UserInfoBean?) {
        if (!isRefreshUserInfo) {
            return
        }
        if (userInfoBean == null) {
            loginState.postValue(false)
            authState.postValue(false)
        } else {
            isRefreshUserInfo = false
            loginState.postValue(true)
        }
        GlideUtils.loadCircle(userInfoBean?.avatar,binding.myHead,R.mipmap.head_default)
        binding.myHeadvipimg.load(userInfoBean?.ext?.memberIcon)
        binding.messageStatus.isVisible = userInfoBean?.isUnread == 1
        LiveDataBus.get().with(LiveDataBusKey.SHOULD_SHOW_MY_MSG_DOT).postValue(userInfoBean?.isUnread == 1)
        binding.daySign.text = if (userInfoBean?.isSignIn == 1) "已签到" else "签到"
        binding.daySign.isClickable = userInfoBean?.isSignIn != 1
        binding.myName.text = userInfoBean?.nickname
            ?: if (UserManger.isLogin()) "" else resources.getString(R.string.my_loginTips)
        binding.myContent.visibility = if (UserManger.isLogin()) View.GONE else View.VISIBLE
//        binding.myContent.text =
//            userInfoBean?.brief
//                ?: if (UserManger.isLogin()) "" else resources.getString(R.string.my_loginSubTips)
        binding.myScore.text = "${userInfoBean?.ext?.totalIntegral ?: "0"}"//积分
        binding.myScoreAcc.text = "${getAcc(userInfoBean?.ext?.multiple)} 倍加速"
        binding.myStateLayout.apply {
            myStateFabu.text = "${userInfoBean?.count?.releases ?: "0"}"
            myStateFensi.text = "${userInfoBean?.count?.fans ?: "0"}"
            myStateGuanzhu.text = "${userInfoBean?.count?.follows ?: "0"}"
            myStateShoucang.text = "${userInfoBean?.count?.collections ?: "0"}"
        }
        binding.myCarAuthLayout.apply {
            include.apply {
                textView9.text = userInfoBean?.ext?.growSeriesName
                textView11.text =
                    "${userInfoBean?.ext?.totalGrowth}/${userInfoBean?.ext?.nextSeriesMinGrow}"
//                myScorelevel.setProgressWithAnimation(
//                    ((userInfoBean?.ext?.totalGrowth
//                        ?: 0) * 100 / (userInfoBean?.ext?.nextSeriesMinGrow ?: 1)).toFloat()
//                )
                myScorelevel.progress = ((userInfoBean?.ext?.totalGrowth
                    ?: 0) * 100 / (userInfoBean?.ext?.nextSeriesMinGrow ?: 1)).toInt()
            }
        }
        binding.myIconRv.isVisible = false
        userInfoBean?.ext?.let {
            //用户图标
            it.imags?.let {
                binding.myIconRv.visibility = View.VISIBLE
                binding.myIconRv.layoutManager =
                    LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                binding.myIconRv.adapter = labelAdapter
                labelAdapter.data.clear()
                labelAdapter.addData(it)
            }
        }
    }
    private fun getAcc(multiple:Double?):String{
        if (multiple==null || multiple.isNaN()){
            return "1"
        }
        return if ((multiple*10).toInt()%10==0){
            "${multiple.toInt()}"
        } else
            "$multiple"
    }

    /**
     * 获取用户信息
     */
    private fun getUserInfo() {
        viewModel.userDatabase.getUniUserInfoDao().getUser().observe(this, {
            it?.toString()?.logE()
            if (MConstant.token.isNullOrEmpty() || null == it || it.userJson.isNullOrEmpty()) {
                setData(null)
            } else {
                var userInfoBean: UserInfoBean =
                    Gson().fromJson(it.userJson, UserInfoBean::class.java)
                setData(userInfoBean)
            }
        })
        viewModel.userInfo.observe(this,{
            setData(it)
        })
    }

    override fun onPause() {
        super.onPause()
        isRefreshUserInfo = true
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUserInfo()
        if (MConstant.token.isNotEmpty()) {
            viewModel.mineMedal()
        } else {
            medalAdapter.data?.clear()
            medalAdapter.notifyDataSetChanged()
        }
        viewModel.getMenuList()
    }
}

