package ca.nkrishnaswamy.picshare.data.db.roomDbs

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_10_11: Migration = object : Migration(10,11) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE UNIQUE INDEX index_signedInAccount_user_email ON signedInAccount (user_email)")
    }
}