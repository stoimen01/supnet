package com.supnet.indoor.friends

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.supnet.R
import com.supnet.common.BaseFragment
import com.supnet.common.observe
import com.supnet.indoor.friends.FriendsEvent.FriendsViewEvent.*
import com.supnet.indoor.friends.invitation.InvitationDialogFragment
import kotlinx.android.synthetic.main.fragment_friends.*

class FriendsFragment : BaseFragment(), Toolbar.OnMenuItemClickListener, FriendsListAdapter.Listener {

    private val viewModel by lazy {
        ViewModelProviders
            .of(this, FriendsViewModelFactory())
            .get(FriendsViewModel::class.java)
    }

    private val adapter by lazy { FriendsListAdapter(this) }

    override fun provideViewId() = R.layout.fragment_friends

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbarFriends.inflateMenu(R.menu.menu_friends)
        toolbarFriends.setOnMenuItemClickListener(this)

        listFriends.adapter = adapter
        listFriends.layoutManager = LinearLayoutManager(context)

        observe(viewModel.liveState, this::onState)
    }

    private fun onState(state: FriendsState) = when (state) {
        FriendsState.Idle -> {

        }
        is FriendsState.Ready -> {
            adapter.update(state.listItems)
        }
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

    override fun onAcceptInvitation(id: Int) = viewModel.onViewEvent(OnAcceptInvitation(id))

    override fun onRejectInvitation(id: Int) = viewModel.onViewEvent(OnRejectInvitation(id))

    override fun onConnect(id: Int) = viewModel.onViewEvent(OnConnect(id))

}