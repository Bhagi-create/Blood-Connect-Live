package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.DonorRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DonorRequestDao {
    @Query("SELECT * FROM donor_requests WHERE donorId = :donorId ORDER BY createdAt DESC")
    fun getRequestsForDonor(donorId: String): Flow<List<DonorRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonorRequest(request: DonorRequestEntity)

    @Query("UPDATE donor_requests SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query("UPDATE donor_requests SET status = :status WHERE requestId = :requestId")
    suspend fun updateStatusByBloodRequestId(requestId: String, status: String)
}
