package com.changanford.my.ui

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import com.changanford.common.util.MineUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.toast.ToastUtils
import com.changanford.my.BaseMineUI
import com.changanford.my.databinding.UiEditNicknameBinding
import com.changanford.my.viewmodel.SignViewModel

/**
 *  文件名：EditNickNameUI
 *  创建者: zcy
 *  创建日期：2020/5/7 17:37
 *  描述: TODO
 *  修改描述：1、6.22 产品说昵称长度8 群里发的623版本 公告
 */

class EditNickNameUI : BaseMineUI<UiEditNicknameBinding, SignViewModel>() {

    var max: Int = 8

    var inputValue: String = ""

    override fun initView() {
        binding.mineToolbar.toolbarTitle.text = "修改昵称"
        binding.mineToolbar.toolbar.setNavigationOnClickListener {
            back()
        }

        intent.getStringExtra("nickName")?.let {
            binding.nickInput.setText(it)
            binding.inputHint.text = "${if (it.length > max) max else it.length}/${max}"
            inputValue = it
            binding.nickSave.isEnabled = inputValue.isNotEmpty()
        }

        binding.nickInput.setFilters(
            arrayOf<InputFilter>(
                MineUtils.EmojiInputFilter(),
                InputFilter.LengthFilter(max)
            )
        )

        binding.nickInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 禁止EditText输入空格
                try {
                    if (p0.toString().contains(" ")) {
                        var str = p0?.toString()?.split(" ")
                        var sb = StringBuffer()
                        for (i in 0 until str?.size!!) {
                            sb?.append(str[i])
                        }
                        binding.nickInput?.setText(sb.toString())
                        binding.nickInput?.setSelection(p1)
                    }
                    var inputText = binding.nickInput.text.toString()
                    binding.nickSave.isEnabled = inputText.isNotEmpty() && inputValue != inputText
                    binding.inputHint.text = "${binding.nickInput.text.toString().length}/${max}"
                } catch (e: Exception) {
                    binding.nickInput?.setText("")
                    binding.nickInput?.setSelection(0)
                }
            }
        })

        binding.nickSave.setOnClickListener {
            var nickName: String = binding.nickInput.text.toString()
            if (nickName.isNullOrEmpty()) {
                ToastUtils.showLongToast("请输入昵称", this)
                return@setOnClickListener
            }
//            if (MineUtils.compileExChar(nickName)) {
//                ToastUtils.showLongToast("不能输入特殊字符",this)
//                return@setOnClickListener
//            }
            viewModel.nameNick(nickName) {
                LiveDataBus.get().with("MineNickName").postValue(nickName)
                finish()
            }
        }
    }

    override fun initData() {
    }


}