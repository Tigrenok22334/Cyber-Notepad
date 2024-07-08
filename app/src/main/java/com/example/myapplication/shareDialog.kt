package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class shareDialog : DialogFragment() {

    interface shareDialogListener {
        fun onShareEntered(password: String)
    }

    private lateinit var listener: shareDialogListener
    private lateinit var noteContent: String

    companion object {
        private const val ARG_NOTE_CONTENT = "note_content"

        fun newInstance(noteContent: String): shareDialog {
            val args = Bundle()
            args.putString(ARG_NOTE_CONTENT, noteContent)
            val fragment = shareDialog()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            noteContent = it.getString(ARG_NOTE_CONTENT, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_password_dialog1, container, false)

        val passwordEditText = view.findViewById<EditText>(R.id.tex1)
        val confirmButton = view.findViewById<Button>(R.id.but1)

        confirmButton.setOnClickListener {
            val password1 = passwordEditText.text.toString()
            if (password1.length > 6) {
                listener.onShareEntered(password1)
                dismiss()
            } else {
                Toast.makeText(activity, "Введите пароль длиной от 7 символов", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
    }

    fun setListener(listener: shareDialogListener) {
        this.listener = listener
    }
}
