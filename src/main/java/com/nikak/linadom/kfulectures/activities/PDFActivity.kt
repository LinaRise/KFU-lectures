package com.nikak.linadom.kfulectures.activities

import android.app.DownloadManager
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.nikak.linadom.kfulectures.ConnectivityReceiver
import com.nikak.linadom.kfulectures.R
import com.nikak.linadom.kfulectures.R.string.lecture
import java.io.File
import java.net.URLEncoder


class PDFActivity : AppCompatActivity() {

    private val gDriveUrl = "http://drive.google.com/viewerng/viewer?embedded=true&url="
    private var downloadID: Long = 0


    private var lectureNumber: String = ""
    private var themeID: String = ""
    private var subjectID: String = ""
    private var subjectTitle: String = ""
    private var subjectTeacher: String = ""
    private var uriMain: Uri? = null

    private var receiver: ConnectivityReceiver? = null

    lateinit var webView: WebView


    private val onDownloadComplete = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //Fetching the download id received with the broadcast
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                Toast.makeText(this@PDFActivity, getString(R.string.lectureIsDownloaded), Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf)

        //инициализируем webView
        webView = findViewById(R.id.webView)
        webView.webViewClient = WebViewClient()
        //поддержка зума
        webView.settings.setSupportZoom(true)
        //отображение кнопок зума
        webView.settings.builtInZoomControls
        webView.settings.javaScriptEnabled = true
        //доступы к доп функциям
        webView.settings.allowContentAccess = false
        webView.settings.allowFileAccess = false
        webView.settings.allowFileAccessFromFileURLs = false
        webView.settings.allowUniversalAccessFromFileURLs = false




        subjectID = intent.getStringExtra("SUBJECT_ID")
        subjectTitle = intent.getStringExtra("SUBJECT_TITLE")
        subjectTeacher = intent.getStringExtra("SUBJECT_TEACHER")
        lectureNumber = intent.getStringExtra("THEME_NUMBER")
        themeID = intent.getStringExtra("THEME_ID")
//        subject = intent.getStringExtra("SUBJECT_TITLE")
        println(subjectID)
        println(lecture)

//слушает окончание скачивания лекции
        registerReceiver(onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("$subjectID/$themeID.pdf").downloadUrl
            .addOnSuccessListener { uri ->
                Log.i("uri=", uri.toString())
                uriMain = uri
                Log.i("URL", uriMain.toString())
//                url of a supported doc should be urlencoded
                val url = URLEncoder.encode(uri.toString(), "UTF-8")
                webView.loadUrl("$gDriveUrl$url")
//
            }.addOnFailureListener {
                Toast.makeText(applicationContext, "Материалы по данной лекции не найдены", Toast.LENGTH_LONG).show()
            }

        receiver = ConnectivityReceiver()

        registerReceiver(receiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))

    }


    override fun onPause() {
        Log.i("onPause", "!!!!!")
        super.onPause()
        unregisterReceiver(receiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(onDownloadComplete)

    }

    override fun onResume() {
        Log.i("onResume", "!!!!!")
        super.onResume()
        registerReceiver(receiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))
    }

    private fun downloadFile(
        context: Context,
        fileName: String,
        fileExt: String,
        directory: File,
        url: String
    ) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)
        //VISIBILITY_VISIBLE_NOTIFY_COMPLETED - This download is visible
        // and shows in the notifications while in progress and after completion.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalFilesDir(context, directory.toString(), fileName + fileExt)
        downloadID = downloadManager.enqueue(request)


    }


    //при нажатии кнопки нахать возвращаемся в список предметов
    override fun onBackPressed() {
        finish()
    }


    //добавление меню

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //что происходит при клике на меню
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {

            R.id.action_about_app -> {
                print("о приложении")
                val alertDialogBuilder = AlertDialog.Builder(this)

                alertDialogBuilder.setMessage(R.string.about_app_message)

                    .setCancelable(false)
                    //кнопка "Ok"
                    .setPositiveButton(R.string.Ok_button) { _, _ ->
                    }
                    //кнопка "Написать"
                    .setNegativeButton(getString(R.string.write_button)) { _, _ ->
                        val emailIntent = Intent(Intent.ACTION_SEND)
                        val emailList = arrayOf("lin4rise@gmail.com")
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, emailList)
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Лекции КТП")
                        emailIntent.type = "text/plain"
                        if (emailIntent.resolveActivity(packageManager) != null) {
                            Log.i("Open E-mail app", "Handling this intent!")
                            startActivity(emailIntent)
                        } else {
                            Log.d("ImplicitIntents", "Can't handle this intent!")
                            alertDialogBuilder.setMessage(getString(R.string.NoEmailApp))
                                .setCancelable(false)
                                //кнопка "Ok"
                                .setPositiveButton(R.string.Ok_button) { _, _ ->
                                }
                            val alert = alertDialogBuilder.create()
                            // set title for alert dialog box
                            alert.setTitle(getString(R.string.attention))
                            // show alert dialog
                            alert.show()
                        }
                    }

                // create dialog box
                val alert = alertDialogBuilder.create()
                // set title for alert dialog box
                alert.setTitle(R.string.about_app)
                // show alert dialog
                alert.show()
                return true
            }
            R.id.saveLecture -> {
                downloadFile(
                    this,
                    "$subjectTitle - Лекция $lectureNumber",
                    ".pdf",
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    uriMain.toString()
                )
            }

        }
        return super.onOptionsItemSelected(item)
    }
}
