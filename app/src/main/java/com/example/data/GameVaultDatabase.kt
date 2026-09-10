package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.model.GameItem

@Database(entities = [GameItem::class], version = 1, exportSchema = false)
abstract class GameVaultDatabase : RoomDatabase() {

  abstract fun gameDao(): GameVaultDao

  companion object {
    @Volatile
    private var INSTANCE: GameVaultDatabase? = null

    fun getDatabase(context: Context): GameVaultDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          GameVaultDatabase::class.java,
          "game_vault_database"
        ).build()
        INSTANCE = instance
        instance
      }
    }
  }
}
