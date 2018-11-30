package com.supnet.indoor.friends.invitation

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.supnet.R
import com.supnet.common.hide
import com.supnet.common.observe
import com.supnet.common.show
import kotlinx.android.synthetic.main.dialog_invitation.*
import kotlinx.android.synthetic.main.dialog_invitation.view.*
import kotlinx.android.synthetic.main.dialog_room_creator.view.*
import java.lang.IllegalArgumentException

class InvitationDialogFragment : DialogFragment() {

    private val viewModel by lazy {
        ViewModelProviders
            .of(this)
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
                viewModel.sendInvitation()
            }

            return@let AlertDialog.Builder(it)
                .setView(view)
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observe(viewModel.getLiveState()) {
            if (it) {
                dialog.findViewById<View>(R.id.progressBar).show()
            } else {
                dialog.findViewById<View>(R.id.progressBar).hide()
            }
        }
    }

}