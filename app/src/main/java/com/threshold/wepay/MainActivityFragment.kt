package com.threshold.wepay

import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_main.*

class MainActivityFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.offscreenPageLimit=2
        val fragments = arrayOf<Fragment>(AlipayFragment(),WechatPayFragment())
        val titles = arrayOf("支付宝","微信")
        viewPager.adapter = MainFragmentViewPageAdapter(fragments,titles,fragmentManager)
        tabLayout.setupWithViewPager(viewPager)
    }

}
