package com.supnet.indoor.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.supnet.R
import com.supnet.common.BaseFragment
import com.supnet.indoor.friends.FriendsFragment.Card.FriendCard
import com.supnet.indoor.friends.FriendsFragment.Card.InvitationCard
import com.supnet.indoor.friends.FriendsFragment.CardsAdapter.CardViewHolder.FriendHolder
import com.supnet.indoor.friends.FriendsFragment.CardsAdapter.CardViewHolder.InvitationHolder
import com.supnet.indoor.friends.invitation.InvitationDialogFragment
import kotlinx.android.synthetic.main.fragment_friends.*
import java.lang.IllegalArgumentException

class FriendsFragment : BaseFragment(), Toolbar.OnMenuItemClickListener {

    override fun provideViewId() = R.layout.fragment_friends

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbarFriends.inflateMenu(R.menu.menu_friends)
        toolbarFriends.setOnMenuItemClickListener(this)

        val adapter = CardsAdapter()
        listFriends.adapter = adapter
        listFriends.layoutManager = LinearLayoutManager(context)

        adapter.update(listOf<Card>(
            InvitationCard(10, "bob", "hi"),
            InvitationCard(10, "bob", "hi"),
            InvitationCard(10, "bob", "hi"),
            InvitationCard(10, "bob", "hi"),
            InvitationCard(10, "bob", "hi"),
            InvitationCard(10, "bob", "hi"),
            FriendCard(10, "Bob"),
            FriendCard(10, "Bob"),
            FriendCard(10, "Bob"),
            FriendCard(10, "Bob"),
            FriendCard(10, "Bob")
        ))

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


    sealed class Card {

        data class InvitationCard(
            val id: Int,
            val initiatorName: String,
            val message: String
        ): Card()

        data class FriendCard(
            val id: Int,
            val friendName: String
        ): Card()

    }

    class CardsAdapter : RecyclerView.Adapter<CardsAdapter.CardViewHolder>() {

        private var cards = listOf<Card>()

        fun update(cards: List<Card>) {
            this.cards = cards
            notifyDataSetChanged()
        }

        override fun getItemCount() = cards.size

        override fun getItemViewType(position: Int) = when (cards[position]){
            is InvitationCard -> CARD_INVITATION
            is FriendCard -> CARD_FRIEND
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
            val li = LayoutInflater.from(parent.context)
            return when (viewType) {
                CARD_INVITATION ->
                    InvitationHolder(li.inflate(R.layout.list_item_invitation, parent, false))

                CARD_FRIEND ->
                    FriendHolder(li.inflate(R.layout.list_item_friend, parent, false))

                else -> throw IllegalArgumentException("Unsupported view type !")
            }
        }

        override fun onBindViewHolder(holder: CardViewHolder, i: Int) = when (holder) {
            is InvitationHolder -> {
                val card = cards[i]
                if (card is InvitationCard) {
                    holder.txtInitiatorName.text = card.initiatorName
                    holder.txtMessage.text = card.message
                    holder.btnAccept.setOnClickListener {

                    }
                    holder.btnReject.setOnClickListener {

                    }
                }
                Unit
            }
            is FriendHolder -> {
                val card = cards[i]
                if (card is FriendCard) {
                    holder.txtFriendName.text = card.friendName
                    holder.btnConnect.setOnClickListener {

                    }
                }
                Unit
            }
        }

        sealed class CardViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

            class InvitationHolder(view: View): CardViewHolder(view) {
                val txtInitiatorName: TextView = view.findViewById(R.id.txtInitiatorName)
                val txtMessage: TextView = view.findViewById(R.id.txtMsg)
                val btnAccept: ImageView = view.findViewById(R.id.btnAccept)
                val btnReject: ImageView = view.findViewById(R.id.btnReject)
            }

            class FriendHolder(view: View): CardViewHolder(view) {
                val txtFriendName: TextView = view.findViewById(R.id.txtFriendName)
                val btnConnect: Button = view.findViewById(R.id.btnConnect)
            }

        }

        companion object {
            private const val CARD_INVITATION = 100
            private const val CARD_FRIEND = 101
        }
    }

}