package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.chaquo.python.Python
import java.io.File

class inshareDialog : DialogFragment() {

    interface passShare {
        fun onPasswordShare(password: String)
    }

    private var listener: passShare? = null

    fun setListener(listener: passShare) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_password_dialog1, container, false)

        val passwordEditText = view.findViewById<EditText>(R.id.tex1)
        val confirmButton = view.findViewById<Button>(R.id.but1)
        try {
            confirmButton.setOnClickListener {
                val password = passwordEditText.text.toString()
                if (password.length > 6) {
                    listener?.onPasswordShare(password)
                    dismiss()

                } else {
                    Toast.makeText(
                        activity,
                        "Введите пароль длиной от 7 символов",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        catch (e:Exception){
            dismiss()
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)
    }
}
