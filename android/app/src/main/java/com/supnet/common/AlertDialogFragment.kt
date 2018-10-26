package com.supnet.common

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.supnet.R

class AlertDialogFragment : DialogFragment() {

    interface Listener {
        fun onConfirm()
        fun onCancel() {
        }
    }

    private val listener by lazy {
        (parentFragment as? Listener) ?:
        throw IllegalArgumentException("Parent fragment must implement RoomCreator listener.")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val message = arguments?.getString(TITLE_KEY) ?:
            throw IllegalArgumentException("Title argument must be provided !")

            return@let AlertDialog.Builder(it)
                .setMessage(message)
                .setPositiveButton(R.string.ok) { _, _ -> listener.onConfirm() }
                .setNegativeButton(R.string.cancel) { _, _ -> listener.onCancel() }
                .create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {
        private const val TITLE_KEY = "TITLE_KEY"

        fun newInstance(message: String): AlertDialogFragment {
            val bundle = Bundle()
            bundle.putString(TITLE_KEY, message)
            val fragment = AlertDialogFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}