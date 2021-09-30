package com.changanford.common.util.paging

import android.widget.EdgeEffect
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.util.paging.PagingViewHolder
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/25 20:55
 * @Description: 　viewHolder，在Adapter构造时，传入bind函数处理绑定数据
 * *********************************************************************************
 */
class SingleViewHolder<V : ViewBinding, T>(
    private val v: V,
    private val bind: (V, Int) -> Unit,
) :
    RecyclerView.ViewHolder(v.root) {
    fun bind(position: Int) {
        bind.invoke(v, position)
    }

    /**
     * 滑动时边界的移动
     */
    var transition_X = SpringAnimation(itemView, SpringAnimation.TRANSLATION_X)
        .setSpring(
            SpringForce(0f)
                .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY)
                .setStiffness(SpringForce.STIFFNESS_LOW)
        )

    /**
     * 卡片效果
     */
    var rotation_Y = SpringAnimation(itemView, SpringAnimation.ROTATION_Y)
        .setSpring(
            SpringForce(0f)
                .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY)
                .setStiffness(SpringForce.STIFFNESS_LOW)
        ).addUpdateListener { animation, value, velocity ->
            currentVelocity = velocity
        }
    var currentVelocity: Float = 0f

    /**
     * 滑动过程中的效果
     */
    inner class OnScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            recyclerView.forEachVisibleHolder<SingleViewHolder<V, T>> {
                it.rotation_Y
                    .setStartVelocity(it.currentVelocity - dx * 0.25f)
                    .start()
            }
        }
    }

    inner class OverScrollEffect : RecyclerView.EdgeEffectFactory() {
        override fun createEdgeEffect(recyclerView: RecyclerView, direction: Int): EdgeEffect {
            return object : EdgeEffect(recyclerView.context) {
                override fun onPull(deltaDistance: Float) {
                    super.onPull(deltaDistance)
                    handlePull(deltaDistance)
                }

                override fun onPull(deltaDistance: Float, displacement: Float) {
                    super.onPull(deltaDistance, displacement)
                    handlePull(deltaDistance)
                }

                override fun onRelease() {
                    super.onRelease()
                    recyclerView.forEachVisibleHolder<SingleViewHolder<V, T>> { holder ->
                        holder.transition_X.start()
                        holder.rotation_Y.start()
                    }
                }

                fun handlePull(deltaDistance: Float) {
                    var sign = if (direction == DIRECTION_RIGHT) -1 else 1
                    recyclerView.forEachVisibleHolder<SingleViewHolder<V, T>> { holder ->
                        holder.transition_X.cancel()
                        holder.rotation_Y.cancel()
                        holder.itemView.translationX += sign * deltaDistance * 0.4f * recyclerView.width
                        holder.itemView.rotationY += sign * deltaDistance * 30
                    }
                }

                override fun onAbsorb(velocity: Int) {
                    super.onAbsorb(velocity)
                    var sign = if (direction == DIRECTION_RIGHT) -1 else 1
                    var startVelocity = sign * velocity * 0.5f
                    recyclerView.forEachVisibleHolder<SingleViewHolder<V, T>> { holder ->
                        holder.transition_X.setStartVelocity(startVelocity).start()
                    }
                }
            }
        }
    }
}

private inline fun <reified T : RecyclerView.ViewHolder> RecyclerView.forEachVisibleHolder(
    action: (T) -> Unit
) {
    for (i in 0 until childCount) {
        action(getChildViewHolder(getChildAt(i)) as T)
    }
}