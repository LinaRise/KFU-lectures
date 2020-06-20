package com.nikak.linadom.kfulectures.activities

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.nikak.linadom.kfulectures.ConnectivityReceiver
import com.nikak.linadom.kfulectures.JSONHandler
import com.nikak.linadom.kfulectures.R
import com.nikak.linadom.kfulectures.entities.Theme
import kotlinx.android.synthetic.main.activity_theme.*
import kotlinx.android.synthetic.main.template_for_sub_lv.view.*
import java.io.InputStream


class ThemeActivity : AppCompatActivity() {
    private var dbThemes: DatabaseReference? = null

    var receiver: ConnectivityReceiver? = null

    var themeAdapter: ThemeListAdapter? = null

    var themesList = ArrayList<Theme>()
    var allThemesList = ArrayList<Theme>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme)

        //загрузка тем предмета
        dbThemes =
            FirebaseDatabase.getInstance().getReference("themes")
                .child(intent.getStringExtra("SUBJECT_ID"))

        themeListView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            //получаем выбранную тему
            val theme = themesList[i]

            val subjectId = intent.getStringExtra("SUBJECT_ID")
            val subjectTitle = intent.getStringExtra("SUBJECT_TITLE")
            val subjectTeacher = intent.getStringExtra("SUBJECT_TEACHER")
            println("$subjectId $subjectTitle $subjectTeacher 678678")

            //создаем intent
            val intent = Intent(applicationContext, PDFActivity::class.java)

            Log.i("theme.themeId", theme.themeId)
            Log.i("theme.themeTitle", theme.themeTitle)
            Log.i("theme.themeNumber", theme.themeNumber)
            //передаем необходимые параметры в intent
            intent.putExtra("THEME_ID", theme.themeId)
            intent.putExtra("THEME_TITLE", theme.themeTitle)
            intent.putExtra("THEME_NUMBER", theme.themeNumber)
            intent.putExtra("SUBJECT_ID", subjectId)
            intent.putExtra("SUBJECT_TITLE", subjectTitle)
            intent.putExtra("SUBJECT_TEACHER", subjectTeacher)

            //запускаем новую activity
            startActivity(intent)
//            finish()

        }

        receiver = ConnectivityReceiver()

        registerReceiver(receiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))

    }

    //при нажатии кнопки нахать возвращаемся в список предметов
    override fun onBackPressed() {
        finish()
    }


    override fun onStart() {
        super.onStart()

        //загрузка значений тем из базы данных
        dbThemes!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                themesList.clear()
                allThemesList.clear()
                for (postSnapshot in dataSnapshot.children) {
                    val theme = postSnapshot.getValue<Theme>(Theme::class.java)
                    if (theme != null) {
                        themesList.add(theme)
                        allThemesList.add(theme)
                    }
                }
                themeAdapter = ThemeListAdapter(this@ThemeActivity, themesList)
                themeListView.adapter = themeAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    applicationContext,
                    "Ошибка загрузки!\nПроверьте подключению к интернету",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))

    }

    //добавление меню
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.subject, menu)
        val searchItem = menu.findItem(R.id.app_bar_search)

        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                var filteredList = ArrayList<Theme>()
                filteredList.clear()
                for (theme in allThemesList) {
                    if (theme.themeTitle.toLowerCase().contains(query!!.toLowerCase())) {
                        filteredList.add(theme)
                    }
                }
                themeAdapter = ThemeListAdapter(applicationContext, filteredList)
                themeListView.adapter = themeAdapter
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                var filteredList = ArrayList<Theme>()
                filteredList.clear()
                for (theme in allThemesList) {
                    if (theme.themeTitle.toLowerCase().contains(newText!!.toLowerCase())) {
                        filteredList.add(theme)
                    }
                }
                themeAdapter = ThemeListAdapter(applicationContext, filteredList)
                themeListView.adapter = themeAdapter


                return true
            }


        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_about_app -> {
                print("о приложении")
//
                val alertDialogBuilder = AlertDialog.Builder(this)

                alertDialogBuilder.setMessage(
                    R.string.about_app_message
                )

                    .setCancelable(false)
                    // positive button text and action
                    .setPositiveButton(R.string.Ok_button) { _, _ ->
                    }
                    // negative button text and action
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
        }
        return super.onOptionsItemSelected(item)
    }


    //добавление темы
    private fun addThemes(stream: InputStream) {
        //вызваем функцию загрузки файлов из json
        var themesFromJson = JSONHandler.loadJsonTheme(stream)
        //проходимся в цикле по каждой лекции
        for (item in themesFromJson) {
            //задаем id для объекта базы данных
            val id = dbThemes!!.push().key
            //вытаскиваем занчения из списка
            val theme = Theme(id!!, item.themeTitle, item.themeNumber)
            //отправляем данные в бд
            dbThemes!!.child(id).setValue(theme)
            Toast.makeText(this, getString(R.string.theme_is_saved), Toast.LENGTH_LONG).show()

        }
    }


    //    inner class SubjectAdapter для отображения списка предеметов
    inner class ThemeListAdapter(context: Context, var arrayList: ArrayList<Theme>) : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val theme = arrayList[position]
            var linearLayout = findViewById<LinearLayout>(R.id.linearTheme)
            val listViewItem = layoutInflater.inflate(R.layout.template_for_sub_lv, linearLayout, false)
            listViewItem.subjectTV.text = theme.themeTitle
            val res = resources
            listViewItem.teacherTV.text = String.format(res.getString(R.string.lectureWord), theme.themeNumber)
            themesList = arrayList
            return listViewItem
        }

        override fun getItem(position: Int): Any {
            return arrayList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return arrayList.size
        }

        var context: Context? = context


    }

}
