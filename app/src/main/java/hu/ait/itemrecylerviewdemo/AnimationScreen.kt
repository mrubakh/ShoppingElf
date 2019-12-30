package hu.ait.itemrecylerviewdemo

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import hu.ait.todorecylerviewdemo.R
import kotlinx.android.synthetic.main.animation_screen.*

class AnimationScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.animation_screen)

        var rotate: Animation = AnimationUtils.loadAnimation(
            this@AnimationScreen,
            R.anim.loading_scroll)

        var fade_in: Animation = AnimationUtils.loadAnimation(
            this@AnimationScreen,
            R.anim.fade_in)

        loading.startAnimation(rotate)
        logo.startAnimation(fade_in)
        rotateGear(rotate)
    }

    fun rotateGear(rotate: Animation) {
        rotate.setAnimationListener(
            object : Animation.AnimationListener {
                override fun onAnimationRepeat(p0: Animation?) {

                }

                override fun onAnimationEnd(p0: Animation?) {
                    var intentDetails = Intent()
                    intentDetails.setClass(
                        this@AnimationScreen,
                        ScrollingActivity::class.java)

                    startActivity(intentDetails)
                    finish()
                }
                override fun onAnimationStart(p0: Animation?) {

                }
            }
        )
    }


}

