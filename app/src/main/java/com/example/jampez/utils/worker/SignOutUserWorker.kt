package com.example.jampez.utils.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.jampez.data.repositories.TodoRepository
import com.example.jampez.data.repositories.UserRepository
import com.example.jampez.utils.constants.USERID
import com.example.jampez.utils.constants.userImage
import okhttp3.internal.format
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

class SignOutUserWorker(
    private val appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params), KoinComponent {

    private val userRepository: UserRepository by inject()
    private val todoRepository: TodoRepository by inject()

    override suspend fun doWork(): Result {

        return try {
            val userId = inputData.getLong(USERID, 0)
            if (deleteUser(userId)) {
                appContext.cacheDir?.let { cacheDir ->
                    if(cacheDir.exists()){

                        val file = File(cacheDir, format(userImage, userId))

                        if (file.exists()) {
                            file.delete()
                        }

                        cacheDir.listFiles()?.forEach {
                            if (it.extension == "tmp") {
                                it.delete()
                            }
                        }
                    }
                }
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun deleteUser(userId: Long) : Boolean {
        todoRepository.deleteTodos(userId)
        return userRepository.deleteUser()
    }

}