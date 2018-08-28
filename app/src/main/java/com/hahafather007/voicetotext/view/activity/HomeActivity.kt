package com.hahafather007.voicetotext.view.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import com.hahafather007.voicetotext.R
import com.hahafather007.voicetotext.databinding.ActivityHomeBinding
import com.hahafather007.voicetotext.view.fragment.NewsFragment
import com.hahafather007.voicetotext.view.fragment.NoteFragment

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        setSupportActionBar(binding.toolbar)

        initView()
    }

    override fun onBackPressed() {
        moveTaskToBack(false)
    }

    private fun initView() {
        val pagers = listOf(NewsFragment(), NoteFragment())

        binding.viewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(p0: Int): Fragment {
                return pagers[p0]
            }

            override fun getCount(): Int {
                return pagers.size
            }
        }
    }
}