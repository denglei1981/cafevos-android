package com.changanford.my.ui

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import com.changanford.my.databinding.*
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.bean.InputBean
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.MineUtils
import com.changanford.common.utilext.toast
import com.changanford.my.BaseMineUI

/**
 *  文件名：EditNickNameUI
 *  创建者: zcy
 *  创建日期：2020/5/7 17:37
 *  描述: TODO
 *  修改描述：TODO
 */

class InputUI : BaseMineUI<UiEditInputBinding, EmptyViewModel>() {

    var type: Int = 0
    var max: Int = 30

    override fun initView() {

        type = intent.getIntExtra("type", 1)
        intent.getStringExtra("content")?.let {
            binding.nickInput.setText("${it}")
            binding.inputHint.text = "${if (it.length > max) max else it.length}/${max}"
        }

        binding.nickNameTitle.visibility = View.VISIBLE
        when (type) {
            1 -> {
                binding.mineToolbar.toolbarTitle.text = "修改个性签名"
                binding.nickInput.hint = "请输入个性签名"
                binding.nickNameTitle.text = "个性签名"
            }
            2 -> {
                binding.mineToolbar.toolbarTitle.text = "修改邮箱"
                binding.nickInput.hint = "请输入邮箱"
                binding.nickNameTitle.text = "邮箱"
            }
        }

        binding.mineToolbar.toolbar.setNavigationOnClickListener {
            back()
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
                if (p0.toString().contains(" ")) {
                    var str = p0?.toString()?.split(" ")
                    var sb = StringBuffer()
                    for (i in 0 until str?.size!!) {
                        sb?.append(str[i])
                    }
                    binding.nickInput?.setText(sb.toString())
                    binding.nickInput?.setSelection(p1)
                }
                binding.inputHint.text = "${binding.nickInput.text.toString().length}/${max}"
            }
        })

        binding.nickSave.setOnClickListener {
            var nickName: String = binding.nickInput.text.toString()

            when (type) {
                1 -> {
                    if (nickName.isNullOrEmpty()) {
                        "请输入个性签名".toast()
                        return@setOnClickListener
                    }
                }
                2 -> {
                    if (!MineUtils.isEmail(nickName)) {
                        "请输入正确的邮箱".toast()
                        return@setOnClickListener
                    }
                }
            }
            LiveDataBus.get().with("MineEditInput", InputBean::class.java)
                .postValue(InputBean(type, nickName))
            finish()
        }
    }

    override fun initData() {
    }


}