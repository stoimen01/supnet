package com.supnet.indoor.friends

import android.os.Bundle
import com.supnet.R
import com.supnet.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_friends.*

class FriendsFragment : BaseFragment() {

    override fun provideViewId() = R.layout.fragment_friends

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbarFriends.inflateMenu(R.menu.menu_friends)

    }


}