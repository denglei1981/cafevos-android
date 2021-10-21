package com.changanford.circle.ui.release;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CompoundButton;

import com.changanford.common.basic.BaseActivity;
import com.changanford.common.basic.EmptyViewModel;
import com.changanford.common.bean.QueryInfo;
import com.changanford.common.util.AppUtils;
import com.changanford.circle.databinding.PerfectquessionwbBinding;
import com.changanford.circle.ui.release.utils.ParamsUtils;

/**
 * 文本问题添加
 */
public class PerfectQuessionWBActivity extends BaseActivity<PerfectquessionwbBinding, EmptyViewModel> {
    int quessionType;
    QueryInfo.QuessionBean quessionBean = new QueryInfo.QuessionBean();

    int position;

    @Override
    public void initView() {
        AppUtils.setStatusBarHeight(binding.title.barTitleView, this);
        if (getIntent().getExtras().getSerializable("quessionBean") != null) {
            position = getIntent().getIntExtra("position", 0);
            quessionBean = (QueryInfo.QuessionBean) getIntent().getExtras().getSerializable("quessionBean");
        } else {
            quessionType = getIntent().getIntExtra("quessionType", 0);
            quessionBean.setQuestionType(quessionType);
        }

        binding.title.barTvTitle.setText("填空题");

        binding.etTimu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.tvNum.setText(s.length() + "/20");
            }
        });
        quessionBean.setIsQuestionNecessary(0);
        binding.mcb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    quessionBean.setIsQuestionNecessary(0);
                } else {
                    quessionBean.setIsQuestionNecessary(1);
                }
            }
        });
    }

    @Override
    public void initData() {
        if (getIntent().getExtras().getSerializable("quessionBean") != null) {
            binding.etTimu.setText(quessionBean.getQuestionInfo());
            if (quessionBean.getIsQuestionNecessary() == 1) {
                binding.mcb.setChecked(false);
            } else {
                binding.mcb.setChecked(true);
            }
        }

        binding.commit.setOnClickListener(v -> {
            quessionBean.setQuestionInfo(binding.etTimu.getText().toString().trim());
            if (ParamsUtils.isWBOk(quessionBean)) {

                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("quession", quessionBean);
                bundle.putInt("position", position);
                intent.putExtra("mbund", bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
