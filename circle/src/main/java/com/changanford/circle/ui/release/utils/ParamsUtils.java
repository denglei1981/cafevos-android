package com.changanford.circle.ui.release.utils;

import android.text.TextUtils;

import com.changanford.common.basic.BaseApplication;
import com.changanford.common.bean.DtoBean;
import com.changanford.common.bean.QueryInfo;
import com.luck.picture.lib.tools.ToastUtils;

public class ParamsUtils {
    /**
     * 判断发布线下活动
     * @param dtoBean
     */
    public static boolean isactupCommit(DtoBean dtoBean){
        if (TextUtils.isEmpty(dtoBean.getCoverImgUrl())){
            ToastUtils.s(BaseApplication.INSTANT,"请上传封面");
            return false;
        }
        if (TextUtils.isEmpty(dtoBean.getTitle())){
            ToastUtils.s(BaseApplication.INSTANT,"请填写活动标题");
            return false;
        }
//        if (TextUtils.isEmpty(dtoBean.getContent())){
//            ToastUtils.s(BaseApplication.INSTANT,"请填写活动描述");
//            return false;
//        }
//        if (dtoBean.getContentImgList().size()==0){
//            ToastUtils.s(BaseApplication.INSTANT,"请添加图片");
//            return false;
//        }

        if (TextUtils.isEmpty(dtoBean.getDeadLineTime())){
            ToastUtils.s(BaseApplication.INSTANT,"请选择报名截止时间");
            return false;
        }
        if (TextUtils.isEmpty(dtoBean.getBeginTime())){
            ToastUtils.s(BaseApplication.INSTANT,"请选择活动时间");
            return false;
        }
        if (TextUtils.isEmpty(dtoBean.getWonderfulType())){
            ToastUtils.s(BaseApplication.INSTANT,"请选择活动类型");
            return false;
        }
        if (dtoBean.getWonderfulType().equals("1")&&TextUtils.isEmpty(dtoBean.getActivityAddr())){
            ToastUtils.s(BaseApplication.INSTANT,"请选择活动地点");
            return false;
        }
//        if (dtoBean.getAttributes().size()==0){
//            ToastUtils.s(BaseApplication.INSTANT,"请选择填写资料");
//            return false;
//        }
        if (dtoBean.getTitle().length()<4||dtoBean.getTitle().length()>20){
            ToastUtils.s(BaseApplication.INSTANT,"活动标题请输入4-20个文字");
            return false;
        }
        return true;
//        if (TextUtils.isEmpty(dtoBean.getActivityTotalCount())
    }


    //判断单选多选问题填写
    public static boolean isquessionOk(QueryInfo.QuessionBean quessionBean){
        if (TextUtils.isEmpty(quessionBean.getQuestionInfo())){
            ToastUtils.s(BaseApplication.INSTANT,"请输入题目标题");
            return false;
        }
        for (int i = 0; i < quessionBean.getOptionList().size() ; i++) {
            if (TextUtils.isEmpty(quessionBean.getOptionList().get(i).getOptionName())){
                ToastUtils.s(BaseApplication.INSTANT,"请输入选项名");
                return  false;
            }
        }
        return true;
    }

    public static boolean isWENQUANOK(QueryInfo queryInfo) {
        if (TextUtils.isEmpty(queryInfo.getCoverImgUrl())){
            ToastUtils.s(BaseApplication.INSTANT,"请上传封面图");
            return false;
        }
        if (TextUtils.isEmpty(queryInfo.getTitle())){
            ToastUtils.s(BaseApplication.INSTANT,"请输入调查标题");
            return false;
        }
        if (queryInfo.getQuestionList().size()==0){
            ToastUtils.s(BaseApplication.INSTANT,"请至少添加一个问题");
            return false;
        }
        if (TextUtils.isEmpty(queryInfo.getDeadlineTime())){
            ToastUtils.s(BaseApplication.INSTANT,"请选择报名截止时间");
            return false;
        }
        if (queryInfo.getTitle().length()<4||queryInfo.getTitle().length()>20){
            ToastUtils.s(BaseApplication.INSTANT,"调查标题请输入(4-20)个字");
            return false;
        }
        return true;
    }

    //发布调查 文本
    public static boolean isWBOk(QueryInfo.QuessionBean quessionBean){
        if (TextUtils.isEmpty(quessionBean.getQuestionInfo())){
            ToastUtils.s(BaseApplication.INSTANT,"请输入问题题目");
            return false;
        }
        return true;
    }
}
