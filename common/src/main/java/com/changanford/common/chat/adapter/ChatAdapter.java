package com.changanford.common.chat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.changanford.common.R;
import com.changanford.common.chat.bean.MessageBean;
import com.changanford.common.chat.bean.MessageImageBody;
import com.changanford.common.chat.bean.MessageStatus;
import com.changanford.common.chat.bean.MessageTextBody;
import com.changanford.common.chat.bean.MessageType;
import com.changanford.common.chat.utils.Utils;
import com.changanford.common.utilext.GlideUtils;

/**
 * 文件名：ChatAdapter
 * 创建者: zcy
 * 创建日期：2020/10/13 10:23
 * 描述: TODO
 * 修改描述：TODO
 */
public class ChatAdapter extends BaseMultiItemQuickAdapter<MessageBean, BaseViewHolder> {

    public static final int TYPE_SEND_TEXT = 1;
    public static final int TYPE_RECEIVE_TEXT = 2;
    public static final int TYPE_SEND_IMAGE = 3;
    public static final int TYPE_RECEIVE_IMAGE = 4;
    public static final int TYPE_SEND_VIDEO = 5;
    public static final int TYPE_RECEIVE_VIDEO = 6;
    public static final int TYPE_SEND_FILE = 7;
    public static final int TYPE_RECEIVE_FILE = 8;
    public static final int TYPE_SEND_AUDIO = 9;
    public static final int TYPE_RECEIVE_AUDIO = 10;

    private int w = 0;


    public ChatAdapter(Context context) {
        super();
        addItemType(TYPE_SEND_TEXT, R.layout.item_msg_send_text);
        addItemType(TYPE_RECEIVE_TEXT, R.layout.item_msg_receive_text);
        addItemType(TYPE_SEND_IMAGE, R.layout.item_msg_send_image);
        addItemType(TYPE_RECEIVE_IMAGE, R.layout.item_msg_receive_image);
        w = Utils.dip2Px(context, 160);

    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, MessageBean messageBean) {
        setHeadIcon((ImageView) baseViewHolder.getView(R.id.msg_head_icon), messageBean.getHeadIcon(), messageBean.isOneselfSend());
        String time = Utils.InputTimeAll(messageBean.getSendTime(), "");
        TextView timeText = baseViewHolder.getView(R.id.msg_time);
        if (Utils.isNotNull(time)) {
            timeText.setVisibility(View.VISIBLE);
            timeText.setText(time);
        } else {
            timeText.setVisibility(View.GONE);
        }
        if (messageBean.isOneselfSend()) {
            ProgressBar progressBar = baseViewHolder.getView(R.id.msg_progressbar);
            if (messageBean.getMessageStatus() == MessageStatus.MESSAGE_LOADING) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        }
        if (messageBean.getMessageType() == MessageType.TEXT) {//文本
            MessageTextBody textBody = (MessageTextBody) messageBean.getMessageBody();
            setMsgText((TextView) baseViewHolder.getView(R.id.msg_text), textBody.getMessageText());
            if (!messageBean.isOneselfSend()) {//别人发的消息
                baseViewHolder.setText(R.id.msg_nickName, messageBean.getNickName());
            }
        } else if (messageBean.getMessageType() == MessageType.IMAGE) {//图片
            final MessageImageBody imageBody = (MessageImageBody) messageBean.getMessageBody();
            final ImageView imageView = baseViewHolder.getView(R.id.msg_image);

            if (Utils.isHttpOrHttps(imageBody.getImageUrl())) {
                Glide.with(imageView.getContext()).asBitmap().load(imageBody.getImageUrl())
                        .apply(RequestOptions.errorOf(R.mipmap.ic_def_square_img)).listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(final Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        if (imageView == null) {
                            return false;
                        }
                        ViewGroup.LayoutParams params = imageView.getLayoutParams();
                        params.width = w;
                        params.height = w * resource.getHeight() / resource.getWidth();

//                    int vw = imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
//                    //float scale = (float) vw / (float) resource.getIntrinsicWidth();
//                    int vh = (int) ((float) vw / (float) 1.78);
//                    params.height = vh + imageView.getPaddingTop() + imageView.getPaddingBottom();
//                    imageView.setLayoutParams(params);
                        imageView.post(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(resource);
                            }
                        });
                        return false;
                    }
                }).submit();
            } else {
                Bitmap img = BitmapFactory.decodeFile(imageBody.getImageUrl());

                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                params.width = w;
                params.height = w * img.getHeight() / img.getWidth();

                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(imageView.getContext())
                                .load(imageBody.getImageUrl())
                                .apply(RequestOptions.errorOf(R.mipmap.ic_def_square_img))
                                .into(imageView);
                    }
                });
            }

            if (!messageBean.isOneselfSend()) {//别人发的消息
                baseViewHolder.setText(R.id.msg_nickName, messageBean.getNickName());
            }
        }
    }

    /**
     * 设置头像
     *
     * @param headIcon
     * @param headUrl
     */
    private void setHeadIcon(ImageView headIcon, String headUrl, boolean isOneselfSend) {
        if (null != headIcon) {
            GlideUtils.INSTANCE.loadCircle(headUrl, headIcon, isOneselfSend ?
                    R.mipmap.ic_def_square_img : R.mipmap.ic_def_square_img);
        }
    }

    /**
     * 设置文本内容
     *
     * @param msgText
     * @param messageContent
     */
    private void setMsgText(TextView msgText, String messageContent) {
        if (msgText != null) {
            msgText.setText(messageContent);
        }
    }

}
