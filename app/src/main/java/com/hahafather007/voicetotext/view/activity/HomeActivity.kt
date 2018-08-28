package com.hahafather007.voicetotext.view.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.hahafather007.voicetotext.R
import com.hahafather007.voicetotext.databinding.ActivityHomeBinding
import com.hahafather007.voicetotext.databinding.ItemTabNewsBinding
import com.hahafather007.voicetotext.databinding.ItemTabNoteBinding
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

    override fun onDestroy() {
        super.onDestroy()

        binding.viewPager.clearOnPageChangeListeners()
    }

    override fun onBackPressed() {
        moveTaskToBack(false)
    }

    private fun initView() {
        val pagers = listOf<Fragment>(NewsFragment(), NoteFragment())

        binding.viewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(p0: Int): Fragment {
                return pagers[p0]
            }

            override fun getCount(): Int {
                return pagers.size
            }
        }

        var tabNews: ItemTabNewsBinding? = null
        var tabNote: ItemTabNoteBinding? = null

        binding.tabLayout.apply {
            setupWithViewPager(binding.viewPager)
            removeAllTabs()

            addTab(newTab().setCustomView(R.layout.item_tab_news))
            addTab(newTab().setCustomView(R.layout.item_tab_note))

            tabNews = DataBindingUtil.bind(getTabAt(0)?.customView!!)
            tabNote = DataBindingUtil.bind(getTabAt(1)?.customView!!)
        }

        tabNews?.selected = true

        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {}

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

            override fun onPageSelected(p0: Int) {
                when (p0) {
                    0 -> {
                        tabNews?.selected = true
                        tabNote?.selected = false
                    }
                    1 -> {
                        tabNews?.selected = false
                        tabNote?.selected = true
                    }
                }
            }
        })
    }
}