package ca.nkrishnaswamy.picshare.data.db.roomDbs

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ca.nkrishnaswamy.picshare.data.db.DAOs.UserAccountDAO
import ca.nkrishnaswamy.picshare.data.models.UserModel
import ca.nkrishnaswamy.picshare.data.models.UserPost

@Database(entities= [UserModel::class, UserPost::class], version = 10, exportSchema = false)
abstract class CurrentLoggedInUserCache : RoomDatabase() {
    abstract fun userAccountDAO() : UserAccountDAO

    companion object{
        @Volatile
        private var INSTANCE: CurrentLoggedInUserCache ?= null

        fun getInstance(context: Context) : CurrentLoggedInUserCache {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null){
                    instance = Room.databaseBuilder(context.applicationContext, CurrentLoggedInUserCache::class.java, "currentLoggedInUserDbv10").addMigrations(
                        MIGRATION_9_10).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}