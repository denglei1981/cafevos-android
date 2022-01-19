package com.changanford.evos.adapter

import android.app.Activity
import android.view.View
import com.zhpan.bannerview.BaseViewHolder

class LandingViewHolder(
    val activity: Activity,
    itemView: View,
    roundCorner: Int
) : BaseViewHolder<Int>(itemView) {
//    var imageView: ImageView = findView(R.id.img_land)
//    var textView: TextView = findView(R.id.tv_go);
//    override fun bindData(data: Int, position: Int, pageSize: Int) {
//        Glide.with(imageView).load(data).into(imageView)
////        Glide.with(activity).asBitmap().load(data).into(object :
////            SimpleTarget<Bitmap>(SIZE_ORIGINAL, SIZE_ORIGINAL){
////            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
////                var imageWidth = resource.width;
////                var imageHeight = resource.height;
////                var height = ScreenUtils.getScreenWidth(BaseApplication.INSTANT) * imageHeight / imageWidth;
////                var para = imageView.layoutParams;
////                para.height = height;
////                para.width = ScreenUtils.getScreenWidth(BaseApplication.INSTANT);
////                imageView.setImageBitmap(resource);
////
////            }
////
////        });
//
//        if (pageSize == position + 1) {
//            textView.visibility = View.VISIBLE
//            textView.setOnClickListener {
//                startARouterFinish(activity, ARouterHomePath.MainActivity)
//            }
//        } else {
//            textView.visibility = View.GONE
//        }
//    }
}