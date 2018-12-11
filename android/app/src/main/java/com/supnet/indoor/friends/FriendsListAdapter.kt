package com.supnet.indoor.friends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.supnet.R
import com.supnet.indoor.friends.FriendsListItem.FriendListItem
import com.supnet.indoor.friends.FriendsListItem.InvitationListItem
import kotlinx.android.synthetic.main.list_item_friend.view.*
import kotlinx.android.synthetic.main.list_item_invitation.view.*

class FriendsListAdapter(
    private val listener: Listener
) : RecyclerView.Adapter<FriendsListAdapter.MyViewHolder>() {

    interface Listener {
        fun onAcceptInvitation(id: Int)
        fun onRejectInvitation(id: Int)
        fun onConnect(id: Int)
    }

    private var items = listOf<FriendsListItem>()

    fun update(friendsListItems: List<FriendsListItem>) {
        items = friendsListItems
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = when (items[position]) {
        is InvitationListItem -> R.layout.list_item_invitation
        is FriendListItem -> R.layout.list_item_friend
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(viewType, parent, false)
    )

    override fun onBindViewHolder(holder: MyViewHolder, i: Int) = holder.bindCard(items[i])

    inner class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bindCard(friendsListItem: FriendsListItem) = when (friendsListItem) {
            is InvitationListItem -> with(view) {
                txtInitiatorName.text = friendsListItem.initiatorName
                txtMsg.text = friendsListItem.message
                btnAccept.setOnClickListener {
                    listener.onAcceptInvitation(friendsListItem.id)
                }
                btnReject.setOnClickListener {
                    listener.onRejectInvitation(friendsListItem.id)
                }
            }
            is FriendListItem -> with(view) {
                txtFriendName.text = friendsListItem.friendName
                btnConnect.setOnClickListener {
                    listener.onConnect(friendsListItem.id)
                }
            }
        }
    }
}