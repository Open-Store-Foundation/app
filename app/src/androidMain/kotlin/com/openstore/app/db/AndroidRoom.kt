package com.openstore.app.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.openstore.app.core.async.Async
import com.openstore.app.data.db.AppDatabase

fun getAppDatabase(ctx: Context): AppDatabase {
    return getAppDatabaseBuilder(ctx).run {
        addMigrations(*getMigrations())
        fallbackToDestructiveMigrationOnDowngrade(true)
        setDriver(AndroidSQLiteDriver())
        setQueryCoroutineContext(Async.Io)
        build()
    }
}

private fun getAppDatabaseBuilder(ctx: Context): RoomDatabase.Builder<AppDatabase> {
    val appContext = ctx.applicationContext

    val dbFile = appContext.getDatabasePath("open-store.db")

    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}

private fun getMigrations(): Array<Migration> {
    return emptyArray()
}
