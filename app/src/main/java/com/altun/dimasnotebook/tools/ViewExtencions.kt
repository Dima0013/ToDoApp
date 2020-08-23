package com.altun.dimasnotebook.tools

import android.animation.Animator
import android.view.View

fun View.visible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE
    else View.GONE
}

fun View.animShow() {
    alpha = 0f
    visible(true)
    animate()
        .alpha(1f)
        .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
        .setListener(null)
}

fun View.animHide() {
    animate()
        .alpha(0f)
        .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                visibility = View.GONE
            }
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}

        })

}