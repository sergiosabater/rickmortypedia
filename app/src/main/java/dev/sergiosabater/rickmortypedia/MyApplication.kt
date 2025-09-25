package dev.sergiosabater.rickmortypedia

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import dev.sergiosabater.rickmortypedia.data.repository.CharacterRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {

    @Inject
    lateinit var repository: CharacterRepository

    override fun onCreate() {
        super.onCreate()

        // Solo para testearlo, lo quitaré después
        CoroutineScope(Dispatchers.IO).launch {
            testRepository()
        }
    }

    private suspend fun testRepository() {
        Log.d("AppTest", "Starting repository test...")

        try {
            val result = repository.getCharacters(page = 1)

            if (result.isSuccess) {
                val characters = result.getOrNull()!!
                Log.d("AppTest", "SUCCESS: Loaded ${characters.size} characters")
                Log.d("AppTest", "Sample: ${characters.first().name}")
            } else {
                Log.e("AppTest", "ERROR: ${result.exceptionOrNull()?.message}")
            }
        } catch (e: Exception) {
            Log.e("AppTest", "EXCEPTION: ${e.message}")
        }
    }

}