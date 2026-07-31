package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GhostDao {
    @Query("SELECT * FROM ghost_detections ORDER BY timestamp DESC")
    fun getAllDetections(): Flow<List<GhostDetectionEntity>>

    @Query("SELECT * FROM ghost_detections WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteDetections(): Flow<List<GhostDetectionEntity>>

    @Query("SELECT * FROM ghost_detections WHERE dangerLevel >= :minDanger ORDER BY dangerLevel DESC")
    fun getHighDangerDetections(minDanger: Int): Flow<List<GhostDetectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ghost: GhostDetectionEntity): Long

    @Update
    suspend fun update(ghost: GhostDetectionEntity)

    @Delete
    suspend fun delete(ghost: GhostDetectionEntity)

    @Query("DELETE FROM ghost_detections")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM ghost_detections")
    suspend fun getCount(): Int
}
