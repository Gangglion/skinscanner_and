package com.glion.skinscanner_and.data.datastore.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.glion.skinscanner_and.data.datastore.PreferencesKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("check_skin_cancer")
class DataStoreRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : DataStoreRepository {
    override val gender: Flow<String?>
        get() = context.dataStore.data
            .map { pref ->
                pref[PreferencesKeys.GENDER]
            }
    override val age: Flow<Int?>
        get() = context.dataStore.data
            .map { pref ->
                pref[PreferencesKeys.AGE]
            }

    override suspend fun setGender(gender: String) {
        context.dataStore.edit { pref ->
            pref[PreferencesKeys.GENDER] = gender
        }
    }

    override suspend fun setAge(age: Int) {
        context.dataStore.edit { pref ->
            pref[PreferencesKeys.AGE] = age
        }
    }
}