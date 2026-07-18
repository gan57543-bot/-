package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppConfigDao {
    @Query("SELECT * FROM app_config WHERE id = 1 LIMIT 1")
    fun getConfigFlow(): Flow<AppConfig?>

    @Query("SELECT * FROM app_config WHERE id = 1 LIMIT 1")
    suspend fun getConfig(): AppConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveConfig(config: AppConfig)
}

@Dao
interface MilestoneDao {
    @Query("SELECT * FROM milestones ORDER BY id ASC")
    fun getAllMilestones(): Flow<List<Milestone>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: Milestone)

    @Update
    suspend fun updateMilestone(milestone: Milestone)

    @Delete
    suspend fun deleteMilestone(milestone: Milestone)

    @Query("DELETE FROM milestones")
    suspend fun deleteAllMilestones()
}

@Dao
interface ProjectNoteDao {
    @Query("SELECT * FROM project_notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<ProjectNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: ProjectNote)

    @Delete
    suspend fun deleteNote(note: ProjectNote)
}

@Dao
interface MockAssetDao {
    @Query("SELECT * FROM mock_assets ORDER BY id DESC")
    fun getAllAssets(): Flow<List<MockAsset>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: MockAsset)

    @Update
    suspend fun updateAsset(asset: MockAsset)

    @Delete
    suspend fun deleteAsset(asset: MockAsset)
}

@Dao
interface MockInventoryDao {
    @Query("SELECT * FROM mock_inventories ORDER BY id DESC")
    fun getAllInventories(): Flow<List<MockInventory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventory(inventory: MockInventory)

    @Update
    suspend fun updateInventory(inventory: MockInventory)

    @Delete
    suspend fun deleteInventory(inventory: MockInventory)
}

@Dao
interface MockDocumentDao {
    @Query("SELECT * FROM mock_documents ORDER BY id DESC")
    fun getAllDocuments(): Flow<List<MockDocument>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: MockDocument)

    @Update
    suspend fun updateDocument(document: MockDocument)

    @Delete
    suspend fun deleteDocument(document: MockDocument)
}

@Dao
interface MockRepairDao {
    @Query("SELECT * FROM mock_repairs ORDER BY id DESC")
    fun getAllRepairs(): Flow<List<MockRepair>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepair(repair: MockRepair)

    @Update
    suspend fun updateRepair(repair: MockRepair)

    @Delete
    suspend fun deleteRepair(repair: MockRepair)
}
