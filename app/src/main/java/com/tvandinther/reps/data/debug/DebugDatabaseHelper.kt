package com.tvandinther.reps.data.debug

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Process
import com.tvandinther.reps.data.db.AppDatabase
import java.io.File

object DebugDatabaseHelper {

    fun exportDatabase(context: Context, database: AppDatabase, uri: Uri) {
        database.openHelper.readableDatabase.rawQuery("PRAGMA wal_checkpoint(FULL)", null).close()
        val dbFile = context.getDatabasePath("reps.db")
        context.contentResolver.openOutputStream(uri)?.use { output ->
            dbFile.inputStream().use { input -> input.copyTo(output) }
        }
    }

    fun importDatabase(context: Context, database: AppDatabase, uri: Uri) {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return
        val dbFile = context.getDatabasePath("reps.db")

        database.close()

        inputStream.use { input ->
            dbFile.outputStream().use { output -> input.copyTo(output) }
        }
        File("${dbFile.absolutePath}-wal").delete()
        File("${dbFile.absolutePath}-shm").delete()

        val intent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) }
        context.startActivity(intent)
        Process.killProcess(Process.myPid())
    }
}
