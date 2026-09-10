package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.GameItem
import kotlinx.coroutines.flow.Flow

@Dao
interface GameVaultDao {

  @Query("SELECT * FROM games ORDER BY lastPlayed DESC, title ASC")
  fun getAllGames(): Flow<List<GameItem>>

  @Query("SELECT * FROM games WHERE id = :id LIMIT 1")
  suspend fun getGameById(id: String): GameItem?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertGames(games: List<GameItem>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertGame(game: GameItem)

  @Update
  suspend fun updateGame(game: GameItem)

  @Query("UPDATE games SET highScore = :score, totalPlays = totalPlays + 1, lastPlayed = :timestamp WHERE id = :id AND :score > highScore")
  suspend fun updateHighScore(id: String, score: Int, timestamp: Long)

  @Query("UPDATE games SET totalPlays = totalPlays + 1, lastPlayed = :timestamp WHERE id = :id")
  suspend fun recordPlay(id: String, timestamp: Long)

  @Query("UPDATE games SET isFavorite = :isFavorite WHERE id = :id")
  suspend fun setFavorite(id: String, isFavorite: Boolean)
}
