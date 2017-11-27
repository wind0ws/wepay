package com.threshold.wepay

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class MainFragmentViewPageAdapter(private val fragments:Array<Fragment>, private val pageTitles: Array<String>, fragmentManager: FragmentManager):FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = fragments.size

    override fun getPageTitle(position: Int): CharSequence {
        return pageTitles[position]
    }
}