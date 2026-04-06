package com.example.posapp.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.posapp.data.local.AppDatabase // Kiểm tra đúng package
import com.example.posapp.data.remote.PosApi    // Kiểm tra đúng package
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val api: PosApi,
    private val db: AppDatabase
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = Result.success()
}