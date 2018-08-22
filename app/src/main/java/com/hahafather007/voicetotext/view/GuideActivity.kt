package com.hahafather007.voicetotext.view

import android.app.Activity
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import com.hahafather007.voicetotext.R
import com.hahafather007.voicetotext.databinding.ActivityGuideBinding
import com.hahafather007.voicetotext.model.pref.VoicePref

class GuideActivity : Activity() {
    private lateinit var binding: ActivityGuideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //去除状态栏
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_guide)
        binding.activity = this

        initPager()

        //TODO 这里执行设置是否初始化操作
        VoicePref.isFirst = false
    }

    private fun initPager() {
        val pagers = (0..2).map {
            val img = ImageView(binding.viewPager.context)
            img.setImageResource(R.drawable.back)
            img.scaleType = ImageView.ScaleType.CENTER_CROP

            img
        }

        binding.viewPager.adapter = object : PagerAdapter() {
            override fun isViewFromObject(p0: View, p1: Any): Boolean {
                return p0 == p1
            }

            override fun getCount(): Int {
                return pagers.size
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                container.addView(pagers[position])

                return pagers[position]
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                container.removeView(pagers[position])
            }
        }
    }
}