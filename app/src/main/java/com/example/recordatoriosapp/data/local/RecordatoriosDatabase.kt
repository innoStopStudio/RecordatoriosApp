
package com.example.recordatoriosapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [RecordatorioEntity::class],
    version = 3,
    exportSchema = true
)
abstract class RecordatoriosDatabase : RoomDatabase() {

    abstract fun recordatorioDao(): RecordatorioDao

    companion object {

        @Volatile
        private var INSTANCE: RecordatoriosDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE recordatorios " +
                            "ADD COLUMN usuarioId TEXT DEFAULT NULL"
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL(
                    "ALTER TABLE recordatorios " +
                            "ADD COLUMN estadoSincronizacion TEXT NOT NULL " +
                            "DEFAULT 'PENDIENTE_CREAR'"
                )

                db.execSQL(
                    "ALTER TABLE recordatorios " +
                            "ADD COLUMN versionLocal INTEGER NOT NULL DEFAULT 1"
                )

                db.execSQL(
                    "ALTER TABLE recordatorios " +
                            "ADD COLUMN eliminado INTEGER NOT NULL DEFAULT 0"
                )

                db.execSQL(
                    "UPDATE recordatorios " +
                            "SET estadoSincronizacion = " +
                            "CASE WHEN pendienteSincronizacion = 1 " +
                            "THEN 'PENDIENTE_CREAR' " +
                            "ELSE 'SINCRONIZADO' END"
                )
            }
        }

        fun obtenerInstancia(context: Context): RecordatoriosDatabase {
            return INSTANCE ?: synchronized(this) {

                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    RecordatoriosDatabase::class.java,
                    "recordatorios_database"
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3
                    )
                    .build()

                INSTANCE = instancia
                instancia
            }
        }
    }
}