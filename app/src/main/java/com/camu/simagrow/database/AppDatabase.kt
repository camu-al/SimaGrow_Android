package com.camu.simagrow.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.camu.simagrow.dao.IncidenciaDao
import com.camu.simagrow.dao.MensajeDao
import com.camu.simagrow.dao.UsuarioDao
import com.camu.simagrow.model.IncidenciaEntity
import com.camu.simagrow.model.MensajeEntity
import com.camu.simagrow.model.UsuarioEntity

@Database(
    entities = [UsuarioEntity::class, IncidenciaEntity::class, MensajeEntity::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun incidenciaDao(): IncidenciaDao
    abstract fun mensajeDao(): MensajeDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "simagrow_db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}