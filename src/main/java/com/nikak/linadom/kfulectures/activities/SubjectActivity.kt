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
import com.nikak.linadom.kfulectures.entities.Subject
import kotlinx.android.synthetic.main.activity_subject.*
import kotlinx.android.synthetic.main.template_for_sub_lv.view.*
import java.io.InputStream

class SubjectActivity : AppCompatActivity() {

    var subjectsList = ArrayList<Subject>()
    var allSubjectsList = ArrayList<Subject>()
    private var subjectDb: DatabaseReference? = null
    var subjectAdapter: SubjectListAdapter? = null
    private var receiver: ConnectivityReceiver? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject)

        //обращаемся к базе данных
        subjectDb = FirebaseDatabase.getInstance().getReference("subject")

        subjectListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            //получаем выбранный предемт
            val subject = subjectsList[i]
            //создаем intent
            val intent = Intent(applicationContext, ThemeActivity::class.java)

            //передаем id, название предмета и ФИО преподавателя intent
            intent.putExtra("SUBJECT_ID", subject.subjectId)
            Log.i("", subject.subjectId)
            intent.putExtra("SUBJECT_TITLE", subject.subjectTitle)
            Log.i("SUBJECT_TITLE", subject.subjectTitle)
            intent.putExtra("SUBJECT_TEACHER", subject.subjectTeacher)
            Log.i("ATTENTION", "${subject.subjectId} ${subject.subjectTeacher} ${subject.subjectTitle})")

            //запускаем новую activity
            startActivity(intent)
        }
//        val stream:InputStream  = resources.openRawResource(R.raw.subjectslist)
//        addSubjects(stream)
        receiver = ConnectivityReceiver()
        //метод прослушивающий состояние интернет подключения устройства
        registerReceiver(receiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))
    }


    override fun onStart() {
        super.onStart()
        //присоединение event listener
        subjectDb!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //отчистка предудщего списка предметов
                subjectsList.clear()
                allSubjectsList.clear()

                //проход по всем узлам
                for (postSnapshot in dataSnapshot.children) {
                    //получение предмета
                    val subject = postSnapshot.getValue(Subject::class.java)
                    //добавления предемета в список
                    subjectsList.add(subject!!)
                    allSubjectsList.add(subject)
                }
                //сортировка списка предметов по названию
                subjectsList = ArrayList(subjectsList.sortedWith(compareBy(Subject::subjectTitle)))
                //создание адаптера
                subjectAdapter = SubjectListAdapter(this@SubjectActivity, subjectsList)
                //присоединенеи адаптера к ListView
                subjectListView.adapter = subjectAdapter
            }

            //Данный метод задействуется, если произошли ошибки на стороне сервера,
            // либо есть запреты в правилах Firebase Database
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    applicationContext,
                    R.string.download_error,
                    Toast.LENGTH_LONG
                ).show()

            }
        })
    }


    override fun onPause() {
        Log.i("onPause", "!!!!!")
        super.onPause()
        unregisterReceiver(receiver)
    }

    override fun onResume() {
        Log.i("onResume", "!!!!!")
        super.onResume()
        registerReceiver(receiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))
    }


    //функция добавления предмета
    private fun addSubject() {
        val id = subjectDb!!.push().key
        val subject =
            Subject(id!!, "Спецификация программных систем", "Медведва О.А.")
        subjectDb!!.child(id).setValue(subject)

    }


    //добавление предеметов
//this.resources.openRawResource(R.raw.subjectsListJSON)
    private fun addSubjects(stream: InputStream) {
        //вызваем функцию загрузки файлов из json
        var subjectsFromJson = JSONHandler.loadJsonSubjects(stream)

//        loadJson(stream)
        //проходимся в цикле по каждой лекции
        for (item in subjectsFromJson) {
            //задаем id для объекта базы данных
            val id = subjectDb!!.push().key
            //вытаскиваем занчения из списка
            val subject = Subject(id!!, item.subjectTitle, item.subjectTeacher)

            //отправляем данные в бд
            subjectDb!!.child(id).setValue(subject)
            Toast.makeText(this, getString(R.string.subject_is_saved), Toast.LENGTH_LONG).show()

        }
    }


    //добавление меню
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // добавляет пункты в action bar, если он есть
        menuInflater.inflate(R.menu.subject, menu)
        val searchItem = menu.findItem(R.id.app_bar_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                var filteredList = ArrayList<Subject>()
                filteredList.clear()
                for (subject in allSubjectsList) {
                    if (subject.subjectTitle.toLowerCase().contains(query!!.toLowerCase())) {
                        filteredList.add(subject)
                    }

                }
                subjectAdapter = SubjectListAdapter(applicationContext, filteredList)
                Log.i("filteredList", filteredList.toString())
                Log.i("subjectsList", subjectsList.toString())
                subjectListView.adapter = subjectAdapter

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                var filteredList = ArrayList<Subject>()
                filteredList.clear()
                for (subject in allSubjectsList) {
                    if (subject.subjectTitle.toLowerCase().contains(newText!!.toLowerCase())) {
                        filteredList.add(subject)
                    }

                }
                subjectAdapter = SubjectListAdapter(applicationContext, filteredList)
                Log.i("filteredList", filteredList.toString())
                Log.i("subjectsList", subjectsList.toString())
                subjectListView.adapter = subjectAdapter


                return true
            }


        })
        return true
    }


    //что происходит при клике на меню
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_about_app -> {

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
        }


        return super.onOptionsItemSelected(item)
    }


    //    inner class SubjectListAdapter для отображения списка предеметов
    inner class SubjectListAdapter(context: Context, private var arrayList: ArrayList<Subject>) : BaseAdapter() {
        var context: Context? = context

        //получение view, который отображает данные в выбранном формате
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            val subject = arrayList[position]
            val linearLayout = findViewById<LinearLayout>(R.id.linearSubject)
            val listViewItem = layoutInflater.inflate(R.layout.template_for_sub_lv, linearLayout, false)
            listViewItem.subjectTV.text = subject.subjectTitle
            listViewItem.teacherTV.text = subject.subjectTeacher
            subjectsList = arrayList
            return listViewItem
        }

        //функция получения позиции
        override fun getItem(position: Int): Any {
            return arrayList[position]
        }

        //функция получения id позиции
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        //функция получения количества позиций
        override fun getCount(): Int {
            return arrayList.size
        }

    }

}
