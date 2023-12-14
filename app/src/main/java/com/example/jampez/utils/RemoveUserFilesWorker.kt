package com.example.jampez.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.jampez.utils.constants.USERID
import com.example.jampez.utils.constants.userImage
import okhttp3.internal.format
import org.koin.core.component.KoinComponent
import java.io.File

class RemoveUserFilesWorker(
    private val appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params), KoinComponent {

    override suspend fun doWork(): Result {

        return try {
            val userId = inputData.getLong(USERID, 0)

            appContext.filesDir?.let { filesDir ->
                if(filesDir.exists()){

                    val file = File(filesDir, format(userImage, userId))

                    if (file.exists()) {
                        file.delete()
                    }

                    filesDir.listFiles()?.forEach {
                        if (it.extension == "tmp") {
                            it.delete()
                        }
                    }
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

}