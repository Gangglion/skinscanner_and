package com.glion.skinscanner_and.data.datastore

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * DataStore Keys
 */
object PreferencesKeys {
    val GENDER = stringPreferencesKey("gender")
    val AGE = intPreferencesKey("age")
}