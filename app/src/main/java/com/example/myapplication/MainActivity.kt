package com.example.myapplication

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import java.io.File
import androidx.fragment.app.FragmentTransaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity(), PasswordDialogFragment1.PasswordDialogListener1, PasswordDialogFragment.PasswordDialogListener, shareDialog.shareDialogListener, inshareDialog.passShare {

    private lateinit var password: String
    private lateinit var passwor2: String
    private lateinit var share: String
    private lateinit var sharepas: String
    private lateinit var con: String
    private lateinit var inf: String


    private lateinit var saveButton: Button
    private lateinit var noteEditText: EditText

    private fun hideSystemBars() {
        val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView)
        windowInsetsController?.let {
            it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            it.hide(WindowInsetsCompat.Type.systemBars())
        }
    }

    private fun showSystemBars() {
        val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView)
        windowInsetsController?.show(WindowInsetsCompat.Type.systemBars())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);


        // Инициализация элементов интерфейса
        saveButton = findViewById(R.id.saveButton)
        noteEditText = findViewById(R.id.noteEditText)
        val sharebutton: ImageButton = findViewById(R.id.button)
        this.inf = null.toString()

        // Handle incoming intents
        handleIncomingIntent(intent)

        if (inf != "1") {
            try {
                val fileName = "new_filik.txt"
                val file = File(filesDir, fileName)
                val noteContent = file.readText()
                if (noteContent.length > 5) {
                    // Вызов первого диалога для ввода пароля
                    showPasswordDialog()
                }
            } catch (_: Exception) {
                val dialog = WelcomeDialogFragment()
                dialog.show(supportFragmentManager, "WelcomeDialogFragment")
            }
        }

        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }

        sharebutton.setOnClickListener {
            shareFile()
        }

        saveButton.setOnClickListener {
            val noteContent = noteEditText.text.toString()
            if (noteContent.length>0) {
                val dialog = PasswordDialogFragment.newInstance(noteContent)
                dialog.setListener(this)
                dialog.show(supportFragmentManager, "PasswordDialogFragment")
            }
            else {
                Toast.makeText(this@MainActivity, "Нельзя сохранить ничего", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun shareFile() {

        val noteContent = noteEditText.text.toString()
        if (noteContent.length>0) {
            val dialog = shareDialog.newInstance(noteContent)
            dialog.setListener(this)
            dialog.show(supportFragmentManager, "shareDialog")
        }
        else {
            Toast.makeText(this@MainActivity, "Нельзя отправить ничего", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_VIEW) {
            val uri: Uri? = intent.data
            if (uri != null) {
                this.inf = "1"
                openFile(uri)
            }
            else{
                try {
                    val fileName = "new_filik.txt"
                    val file = File(filesDir, fileName)
                    val noteContent = file.readText()
                    if (noteContent.length > 5) {
                        // Вызов первого диалога для ввода пароля
                        showPasswordDialog()
                    }
                } catch (_: Exception) {}
            }
        }
    }

    private fun openFile(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        val content = inputStream?.bufferedReader().use { it?.readText() }
        if (content.toString().length > 10000) {
            this.con = content.toString()
            inshare()
        }
        else{
            try {
                Toast.makeText(this@MainActivity, "Ваш файл не поддерживается программой", Toast.LENGTH_SHORT).show()
                val fileName = "new_filik.txt"
                val file = File(filesDir, fileName)
                val noteContent = file.readText()
                if (noteContent.length > 5) {
                    // Вызов первого диалога для ввода пароля
                    showPasswordDialog()
                }
            } catch (_: Exception) {}
        }
    }

    private fun inshare() {
        val dialog = inshareDialog()
        dialog.setListener(this)
        dialog.isCancelable = false
        dialog.show(supportFragmentManager, "inshareDialog")
    }

    private fun showPasswordDialog() {
        val dialog = PasswordDialogFragment1()
        dialog.setListener(this)
        dialog.isCancelable = false
        dialog.show(supportFragmentManager, "PasswordDialogFragment1")
    }

    private fun showVideoFragment() {
        val fragment = VideoFragment()
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commitAllowingStateLoss()
    }

    private fun hideVideoFragment() {
        val fragment = supportFragmentManager.findFragmentById(android.R.id.content)
        fragment?.let {
            supportFragmentManager.beginTransaction()
                .remove(it)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commitAllowingStateLoss()
        }
    }

    private fun disableUserInteraction() {
        saveButton.isEnabled = false
        noteEditText.isEnabled = false
    }

    private fun enableUserInteraction() {
        saveButton.isEnabled = true
        noteEditText.isEnabled = true
    }

    override fun onPasswordEntered(password: String) {
        this.password = password
        disableUserInteraction()
        hideSystemBars()
        showVideoFragment()
        CoroutineScope(Dispatchers.Main).launch {
            val tex = perform()
            if (tex == "404") {
                showPasswordDialog()
            }
        }
    }

    override fun onPasswordEntered1(passwor: String) {
        this.passwor2 = passwor

        // Отключить взаимодействие с элементами интерфейса и показать видеофрагмент
        disableUserInteraction()
        hideSystemBars()
        showVideoFragment()


        // Запуск асинхронной задачи для шифрования
        CoroutineScope(Dispatchers.Main).launch {
            performEncryption()
        }
    }

    private suspend fun performEncryption() {
        delay(500)
        withContext(Dispatchers.IO) {
            if (!Python.isStarted()) {
                Python.start(AndroidPlatform(this@MainActivity))
            }

            val py = Python.getInstance()
            val module = py.getModule("main")
            val my = module["init"]

            val textView: EditText = findViewById(R.id.noteEditText)
            val fileName = "new_filik.txt"
            val file = File(filesDir, fileName)
            val noteContent = textView.text.toString()

            try {
                val encryptedContent = my?.call(passwor2, "None", 1, noteContent)
                file.writeText(encryptedContent.toString())
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Успешно!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Ошибка шифрования $e", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    // Скрыть видеофрагмент и включить взаимодействие после завершения шифрования
                    hideVideoFragment()
                    showSystemBars()
                    enableUserInteraction()
                }
            }
        }
    }

    private suspend fun perform(): String {
        delay(1000)
        return withContext(Dispatchers.IO) {
            if (!Python.isStarted()) {
                Python.start(AndroidPlatform(this@MainActivity))
            }

            val py = Python.getInstance()
            val module = py.getModule("main")
            val my = module["init"]

            val fileName = "new_filik.txt"
            val file = File(filesDir, fileName)
            val noteContent = file.readText()

            try {
                val newtext = my?.call(password, noteContent)
                if (newtext.toString() == "404") {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Введён неверный пароль", Toast.LENGTH_SHORT).show()
                    }
                    withContext(Dispatchers.Main) {
                        hideVideoFragment()
                        showSystemBars()
                        enableUserInteraction()
                    }
                    return@withContext "404"
                } else {
                    withContext(Dispatchers.Main) {
                        hideVideoFragment()
                        showSystemBars()
                        enableUserInteraction()
                    }
                    withContext(Dispatchers.Main) {
                        noteEditText.setText(newtext.toString())
                    }
                    return@withContext ""
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Произошла ошибка $e", Toast.LENGTH_SHORT).show()
                    hideVideoFragment()
                    showSystemBars()
                    enableUserInteraction()
                }
                return@withContext "404"
            }
        }
    }

    override fun onShareEntered(share: String) {
        this.share= share

        // Отключить взаимодействие с элементами интерфейса и показать видеофрагмент
        disableUserInteraction()
        hideSystemBars()
        showVideoFragment()


        // Запуск асинхронной задачи для шифрования
        CoroutineScope(Dispatchers.Main).launch {
            shareEncryption()
            share()
        }
    }

    private suspend fun shareEncryption() {
        delay(500)
        withContext(Dispatchers.IO) {
            if (!Python.isStarted()) {
                Python.start(AndroidPlatform(this@MainActivity))
            }

            val py = Python.getInstance()
            val module = py.getModule("main")
            val my = module["init"]

            val textView: EditText = findViewById(R.id.noteEditText)
            val fileName = "new_filik.txt"
            val file = File(filesDir, fileName)
            val noteContent = textView.text.toString()

            try {
                val encryptedContent = my?.call(share, "None", 1, noteContent)
                file.writeText(encryptedContent.toString())
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Успешно!", Toast.LENGTH_SHORT).show()
                }
                hideVideoFragment()
                showSystemBars()
                enableUserInteraction()

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Файл сохранён!", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    // Скрыть видеофрагмент и включить взаимодействие после завершения шифрования
                    hideVideoFragment()
                    showSystemBars()
                    enableUserInteraction()

                }
            }
        }
    }

    suspend fun share(){
        delay(500)
        val file = File(filesDir, "new_filik.txt")
        // Получите Uri для файла, используя FileProvider
        val fileUri: Uri = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.provider",
            file
        )

        // Создайте Intent для отправки файла
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, fileUri)
            type = "text/plain"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Запустите Intent
        startActivity(Intent.createChooser(shareIntent, "Поделиться файлом"))
    }

    override fun onPasswordShare(passwor: String) {
        this.sharepas = passwor

        // Отключить взаимодействие с элементами интерфейса и показать видеофрагмент
        disableUserInteraction()
        hideSystemBars()
        showVideoFragment()


        // Запуск асинхронной задачи для шифрования
        CoroutineScope(Dispatchers.Main).launch {
            val tex = performshare()
            if (tex == "404") {
                inshare()
            }
        }
    }

    private suspend fun performshare(): String {
        delay(1000)
        return withContext(Dispatchers.IO) {
            if (!Python.isStarted()) {
                Python.start(AndroidPlatform(this@MainActivity))
            }

            val py = Python.getInstance()
            val module = py.getModule("main")
            val my = module["init"]

            try {
                val newtext = my?.call(sharepas, con)
                if (newtext.toString() == "404") {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Введён неверный пароль", Toast.LENGTH_SHORT).show()
                    }
                    withContext(Dispatchers.Main) {
                        hideVideoFragment()
                        showSystemBars()
                        enableUserInteraction()
                    }
                    return@withContext "404"
                } else {
                    withContext(Dispatchers.Main) {
                        hideVideoFragment()
                        showSystemBars()
                        noteEditText.isEnabled = true
                        saveButton.text="Режим просмотра"
                    }
                    withContext(Dispatchers.Main) {
                        noteEditText.setText(newtext.toString())
                    }
                    return@withContext ""
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Произошла ошибка $e", Toast.LENGTH_SHORT).show()
                    hideVideoFragment()
                    showSystemBars()
                    enableUserInteraction()
                }
                return@withContext "404"
            }
        }
    }
}