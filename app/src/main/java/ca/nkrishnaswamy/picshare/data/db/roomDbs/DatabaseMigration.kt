package ca.nkrishnaswamy.picshare.data.db.roomDbs

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2: Migration = object : Migration(1,2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE UNIQUE INDEX index_signedInAccount_user_email ON signedInAccount (user_email)")
    }
}