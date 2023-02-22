package com.changanford.common.widget.webview

import android.app.Activity
import android.os.Bundle
import android.webkit.JavascriptInterface
import com.alibaba.android.arouter.launcher.ARouter
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.MediaListBean
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.gio.updateGoodsDetails
import java.io.Serializable

/**
 * Created by Administrator on 2019/1/16.
 */
class MJavascriptInterface(private val activity: Activity) {
    private val list_imgs: List<String> = ArrayList()
    private val index = 0
    @JavascriptInterface
    fun openImage(img: String, imageUrls: Array<String>) {
        val bundle = Bundle()
        bundle.putStringArray("imageUrls", imageUrls)
        val mediaListBeans: MutableList<MediaListBean> = ArrayList()
        var count = 0
        if (imageUrls.size > 0) {
            for (i in imageUrls.indices) {
                val mediaListBean = MediaListBean()
                mediaListBean.img_url = imageUrls[i]
                mediaListBeans.add(mediaListBean)
                if (img == imageUrls[i]) {
                    count = i
                }
            }
        }
        bundle.putSerializable("imgList", mediaListBeans as Serializable)
        bundle.putString("curImageUrl", img)
        bundle.putInt("count", count)
        if (BaseApplication.curActivity?.javaClass?.name == "com.changanford.shop.ui.goods.GoodsDetailsActivity") {
            updateGoodsDetails("图片查看页", "图片查看页")
        }
        ARouter.getInstance().build(ARouterCirclePath.PhotoViewActivity).with(bundle).navigation()
    }
}