package com.supnet.indoor.friends.invitation

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.supnet.R
import com.supnet.common.*
import com.supnet.indoor.friends.invitation.InvitationCommand.*
import kotlinx.android.synthetic.main.dialog_invitation.view.*

class InvitationDialogFragment : DialogFragment() {

    private val viewModel by lazy {
        ViewModelProviders
            .of(this, InvitationViewModelFactory())
            .get(InvitationViewModel::class.java)
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val view = LayoutInflater.from(it).inflate(R.layout.dialog_invitation, null)

            view.btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            view.btnSend.setOnClickListener {
                viewModel.sendInvitation(view.txtFriend.toString(), view.txtMessage.toString())
            }

            return@let AlertDialog.Builder(it)
                .setView(view)
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observe(viewModel.getLiveState()) { it.render() }
        observeCommands(viewModel.getLiveCommand(), this::onCommand)
    }

    private fun InvitationState.render() {
        if (isLoading) {
            dialog.findViewById<View>(R.id.progressBar).show()
        } else {
            dialog.findViewById<View>(R.id.progressBar).hide()
        }
    }

    private fun onCommand(cmd: InvitationCommand) = when (cmd) {
        SHOW_ERROR -> {
            showToast("Sending invitation failed")
        }
        DISMISS -> {
            dialog.dismiss()
        }
    }


}