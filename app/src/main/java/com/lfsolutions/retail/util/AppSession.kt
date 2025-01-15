package com.lfsolutions.retail.util

import android.content.Context
import com.lfsolutions.retail.Main

/**
 * Created by Taimur on 5/11/2017.
 */
class AppSession {
    fun putTaskList(key: String?, Taskmap: HashMap<Int, Boolean?>) {
        require(!(key == null || key == "")) { WRONG_PAIR }
        val editor = Main.app
            .getSharedPreferences(SHARED_PREFERENCE_NAME + "HashMap", Context.MODE_PRIVATE).edit()
        for (s in Taskmap.keys) {
            editor.putBoolean(key + s, Taskmap[s]!!)
        }
        editor.commit()
    }

    fun getTaskList(key: String): HashMap<String, Boolean> {
        val pref = Main.app
            .getSharedPreferences(SHARED_PREFERENCE_NAME + "HashMap", Context.MODE_PRIVATE)
        val map = pref.all as HashMap<String, Boolean?>
        val Taskap = HashMap<String, Boolean>()
        for (s in map.keys) {
            val value = map[s]!!
            val KEY = s.substring(key.length)
            Taskap[KEY] = value
        }
        return Taskap
    }

    companion object {
        const val BLANK_STRING_KEY = "N/A"
        const val WRONG_PAIR = "Key-Value pair cannot be blank or null"

        // private static final String SHARED_PREFERENCE_NAME = Main.app.getPackageName() + "_" + DateTime.getTimeStampForKey();
        private const val SHARED_PREFERENCE_NAME = "SMRT"
        fun put(key: String?, value: String?): Boolean {
            require(!(key == null || value == null || key == "")) { WRONG_PAIR }
            val editor = Main.app
                .getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).edit()
            editor.putString(key, value)
            return editor.commit()
        }

        fun clearSharedPref(): Boolean {
            val editor = Main.app
                .getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).edit()
            val printerWidth = AppSession[Constants.PRINTER_WIDTH]
            val characters = getInt(Constants.CHARACTER_PER_LINE, 32)
            val bluetooth = AppSession[Constants.SELECTED_BLUETOOTH]
            editor.clear()
            val cleared = editor.commit()
            put(Constants.PRINTER_WIDTH, printerWidth)
            put(Constants.CHARACTER_PER_LINE, characters)
            put(Constants.SELECTED_BLUETOOTH, bluetooth)
            return cleared
        }

        fun put(key: String?, value: Int): Boolean {
            require(!(key == null || key == "")) { WRONG_PAIR }
            val editor = Main.app
                .getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).edit()
            editor.putInt(key, value)
            return editor.commit()
        }

        fun put(key: String?, value: Boolean): Boolean {
            require(!(key == null || key == "")) { WRONG_PAIR }
            val editor = Main.app
                .getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).edit()
            editor.putBoolean(key, value)
            return editor.commit()
        }

        fun put(key: String?, value: Long): Boolean {
            require(!(key == null || key == "")) { WRONG_PAIR }
            val editor = Main.app
                .getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).edit()
            editor.putLong(key, value)
            return editor.commit()
        }

        operator fun get(key: String?): String {
            val savedSession = Main.app
                .getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
            return savedSession.getString(key, BLANK_STRING_KEY)!!
        }

        @JvmStatic
        operator fun get(key: String?, defaultValue: String?): String? {
            val savedSession = Main.app
                .getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
            return savedSession.getString(key, defaultValue)
        }

        fun getInt(key: String?): Int {
            val savedSession = Main.app
                .getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
            return savedSession.getInt(key, 0)
        }

        fun getLong(key: String?): Long {
            val savedSession = Main.app
                .getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
            return savedSession.getLong(key, 0)
        }

        fun getBoolean(key: String?): Boolean {
            val savedSession = Main.app
                .getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
            return savedSession.getBoolean(key, false)
        }

        fun getBoolean(key: String?, defaultValue: Boolean): Boolean {
            val savedSession = Main.app
                .getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
            return savedSession.getBoolean(key, defaultValue)
        }

        @JvmStatic
        fun remove(key: String?) {
            val editor = Main.app
                .getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).edit()
            editor.remove(key)
            editor.commit()
        }

        fun getInt(key: String?, defaultValue: Int): Int {
            val savedSession = Main.app
                .getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
            return savedSession.getInt(key, defaultValue)
        }

        fun hasKey(key: String?): Boolean {
            val savedSession = Main.app
                .getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
            return savedSession.contains(key)
        }

        fun put(key: String?, value: Double): Boolean {
            require(!(key == null || key == "")) { WRONG_PAIR }
            val editor = Main.app
                .getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).edit()
            editor.putString(key, value.toString())
            return editor.commit()
        }

        fun getDouble(key: String?): Double {
            val savedSession = Main.app
                .getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
            return savedSession.getString(key, "0.0")!!.toDouble()
        }
    }
}