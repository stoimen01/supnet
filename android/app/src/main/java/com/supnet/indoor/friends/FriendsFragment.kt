package com.supnet.indoor.friends

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.supnet.R
import com.supnet.common.BaseFragment
import com.supnet.indoor.friends.invitation.InvitationDialogFragment
import kotlinx.android.synthetic.main.fragment_friends.*

class FriendsFragment : BaseFragment(), Toolbar.OnMenuItemClickListener {

    override fun provideViewId() = R.layout.fragment_friends

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbarFriends.inflateMenu(R.menu.menu_friends)
        toolbarFriends.setOnMenuItemClickListener(this)
    }

    override fun onMenuItemClick(item: MenuItem) = when (item.itemId) {
        R.id.action_search -> {
            true
        }
        R.id.action_add_friend -> {
            InvitationDialogFragment().show(childFragmentManager, "")
            true
        }
        else -> false
    }

}