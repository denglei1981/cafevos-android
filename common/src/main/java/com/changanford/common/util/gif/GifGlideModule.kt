package com.changanford.common.util.gif

import android.content.Context
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.Glide
import android.support.rastermill.FrameSequenceDrawable
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import java.io.InputStream

@GlideModule
class GifGlideModule : AppGlideModule() {
    override fun registerComponents(
        context: Context,
        glide: Glide, registry: Registry
    ) {
        super.registerComponents(context, glide, registry)
        registry.append(
            Registry.BUCKET_GIF, InputStream::class.java,
            FrameSequenceDrawable::class.java, GifDecoder(glide.bitmapPool)
        )
    }
}