package com.nikak.linadom.kfulectures

import com.nikak.linadom.kfulectures.entities.Subject
import com.nikak.linadom.kfulectures.entities.Theme
import org.json.JSONException
import org.json.JSONObject
import java.io.*

class JSONHandler {


    companion object {

        private var subjectsFromJson = ArrayList<Subject>()
        private const val JSON_KEY_ITEMS_SUBJECT = "subjects"
        private const val JSON_KEY_SUBJECT_TEACHER = "subject_teacher"
        private const val JSON_KEY_SUBJECT_TITLE = "subject_title"
        private const val JSON_KEY_SUBJECT_ID = "subject_id"

        //переменные для работы с json файлом, где находятся темы
        private var themesFromJson = ArrayList<Theme>()
        private const val JSON_KEY_ITEMS_THEME = "themes"
        private const val JSON_KEY_THEME_NUMBER = "theme_number"
        private const val JSON_KEY_THEME_TITLE = "theme_title"

        //parse json файл с предметами
        private fun parseJsonSubject(jsonData: String) {
            try {
                val jsonObject = JSONObject(jsonData)
                val jsonArray = jsonObject.getJSONArray(JSON_KEY_ITEMS_SUBJECT)
                var id = 0
                //очистка списка предметов
                subjectsFromJson.clear()
                //в цикле добавляем темы в список
                for (i in 0 until jsonArray.length()) {
                    val `object` = jsonArray.getJSONObject(i)
                    val subjectId = `object`.getString(JSON_KEY_SUBJECT_ID)
                    val subjectTitle = `object`.getString(JSON_KEY_SUBJECT_TITLE)
                    val subjectTeacher = `object`.getString(JSON_KEY_SUBJECT_TEACHER)
                    id++
                    subjectsFromJson.add(
                        Subject(
                            subjectId,
                            subjectTitle,
                            subjectTeacher
                        )
                    )
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }


        //загрузка json файла с предметами
        fun loadJsonSubjects(stream: InputStream): ArrayList<Subject> {
            val buffer = StringBuffer()
            var reader: BufferedReader? = null
            try {
                reader = BufferedReader(InputStreamReader(stream) as Reader?)
                var temp = reader.readLine()
                while (temp != null) {
                    buffer.append(temp)
                    temp = reader.readLine()
                }

            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    reader!!.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            subjectsFromJson.clear()
            //parse json файл с предметами
            parseJsonSubject(buffer.toString())
            return subjectsFromJson
        }

        //загрузка json файла с темами
        fun loadJsonTheme(stream: InputStream): ArrayList<Theme> {
            val buffer = StringBuffer()
            var reader: BufferedReader? = null
            try {
                reader = BufferedReader(InputStreamReader(stream) as Reader?)
                var temp = reader.readLine()
                while (temp != null) {
                    buffer.append(temp)
                    temp = reader.readLine()
                }

            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    reader!!.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            themesFromJson.clear()
            //parse json файл с темами
            parseJsonTheme(buffer.toString())
            return themesFromJson

        }


        //parse json файл с темами
        private fun parseJsonTheme(jsonData: String) {
            try {
                val jsonObject = JSONObject(jsonData)
                val jsonArray = jsonObject.getJSONArray(JSON_KEY_ITEMS_THEME)
                var id = 0
                //очистка списка тем
                themesFromJson.clear()
                //в цикле добавляем темы в список
                for (i in 0 until jsonArray.length()) {
                    val `object` = jsonArray.getJSONObject(i)

                    val themeNumber = `object`.getString(JSON_KEY_THEME_NUMBER)
                    val themeTitle = `object`.getString(JSON_KEY_THEME_TITLE)
                    id++
                    themesFromJson.add(Theme(id.toString(), themeTitle, themeNumber))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

    }


}