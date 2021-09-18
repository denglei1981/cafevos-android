package com.changanford.common.ui

import android.Manifest
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.adapter.PhotoImageAdapter
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.bean.MediaListBean
import com.changanford.common.databinding.PhotoviewActivityBinding
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.GifUtils
import com.changanford.common.util.MConstant

import com.changanford.common.util.PictureUtil
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.common.utilext.toast
import com.qw.soul.permission.SoulPermission
import com.qw.soul.permission.bean.Permission
import com.qw.soul.permission.bean.Permissions
import com.qw.soul.permission.callbcak.CheckRequestPermissionsListener
import java.lang.Exception
@Route(path = ARouterCirclePath.PhotoViewActivity)
class PhotoViewActivity :BaseActivity<PhotoviewActivityBinding,EmptyViewModel>() {
    var currentPosition =0;
    lateinit var mbundle:Bundle;
    lateinit var pics:ArrayList<MediaListBean>
    lateinit var adapter:PhotoImageAdapter
    override fun initView() {
        StatusBarUtil.setStatusBarPaddingTop(binding.toolbar,this)
        mbundle = intent?.extras!!
        pics = mbundle.getSerializable("imgList") as ArrayList<MediaListBean>
        currentPosition = mbundle.getInt("count")
        if (currentPosition == 0 && !TextUtils.isEmpty(pics[0].videoUrl)) {
            binding.tvSaveImagePhoto.visibility = View.GONE
        }
        adapter = PhotoImageAdapter(pics,this)
        binding.viewPagerPhoto.adapter = adapter
        binding.viewPagerPhoto.setCurrentItem(currentPosition,false)
        when (pics.size) {
            1 -> {
                binding.tvImageCount.visibility = View.GONE
            }
            else -> {
                binding.tvImageCount.visibility = View.VISIBLE
                binding.tvImageCount.text = "${currentPosition+1}/${pics.size}"
            }
        }

        binding.viewPagerPhoto.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                currentPosition = position
                binding.tvImageCount.text = "${currentPosition+1}/${pics.size}"
                if (!TextUtils.isEmpty(pics[position].videoUrl)) {
                    binding.tvSaveImagePhoto.visibility = View.GONE
//                    val myJzvdStd: IjkVideoView = adapter.getfirstview()
//                    if (myJzvdStd != null) {
//                        myJzvdStd.start()
//                    }
                } else {
                    binding.tvSaveImagePhoto.visibility = View.VISIBLE
//                    val myJzvdStd: IjkVideoView = adapter.getfirstview()
//                    if (myJzvdStd != null) {
//                        myJzvdStd.pause()
//                    }
                }
            }

            override fun onPageSelected(position: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })


    }

    override fun initData() {

        binding.llPhotoView.setOnClickListener {
            finish()
        }
        binding.toolbar.setOnClickListener {
            finish()
        }

        binding.tvSaveImagePhoto.setOnClickListener {
            var permissions = Permissions.build(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
            SoulPermission.getInstance().checkAndRequestPermissions(permissions,object :CheckRequestPermissionsListener{
                override fun onAllPermissionOk(allPermissions: Array<out Permission>?) {
                    try {
                        if (pics[currentPosition].img_url.contains(".gif")) {
                            GifUtils.saveGif(
                                pics[currentPosition].img_url,
                                this@PhotoViewActivity,
                                MConstant.rootPath + "/" + System.currentTimeMillis() + ".gif"
                            )
                        } else {
                            PictureUtil.saveBitmapPhoto(pics[currentPosition].bitmap)
                        }
                       "保存成功".toast()
                    } catch (e: Exception) {
                        "保存失败".toast()
                    }
                }

                override fun onPermissionDenied(refusedPermissions: Array<out Permission>?) {
                }

            })

        }
    }
}