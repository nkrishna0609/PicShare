package ca.nkrishnaswamy.picshare.data.db.roomDbs

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3: Migration = object : Migration(2,3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE UNIQUE INDEX index_signedInAccount_user_email ON signedInAccount (user_email)")
    }
}