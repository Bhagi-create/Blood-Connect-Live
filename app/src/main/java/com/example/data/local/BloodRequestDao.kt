package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BloodRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BloodRequestDao {
    @Query("SELECT * FROM blood_requests ORDER BY createdAt DESC")
    fun getAllRequests(): Flow<List<BloodRequestEntity>>

    @Query("SELECT * FROM blood_requests WHERE requesterId = :requesterId ORDER BY createdAt DESC")
    fun getRequestsByRequester(requesterId: String): Flow<List<BloodRequestEntity>>

    @Query("SELECT * FROM blood_requests WHERE requestId = :requestId LIMIT 1")
    fun getRequestById(requestId: String): Flow<BloodRequestEntity?>

    @Query("SELECT * FROM blood_requests WHERE requestId = :requestId LIMIT 1")
    suspend fun getRequestByIdSync(requestId: String): BloodRequestEntity?

    @Query("SELECT * FROM blood_requests WHERE isEmergency = 1 AND status != 'Cancelled' ORDER BY createdAt DESC")
    fun getEmergencyRequests(): Flow<List<BloodRequestEntity>>

    @Query("SELECT * FROM blood_requests WHERE isEmergency = 1 AND LOWER(city) = LOWER(:city) AND status != 'Cancelled' AND status != 'Completed' ORDER BY createdAt DESC")
    fun getActiveEmergencyRequestsInCity(city: String): Flow<List<BloodRequestEntity>>

    @Query("SELECT * FROM blood_requests WHERE assignedDonorId = :donorId OR (bloodGroup = :donorBloodGroup AND status != 'Completed' AND status != 'Cancelled') ORDER BY createdAt DESC")
    fun getRequestsForDonor(donorId: String, donorBloodGroup: String): Flow<List<BloodRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: BloodRequestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequests(requests: List<BloodRequestEntity>)

    @Update
    suspend fun updateRequest(request: BloodRequestEntity)

    @Query("UPDATE blood_requests SET status = :status WHERE requestId = :requestId")
    suspend fun updateRequestStatus(requestId: String, status: String)

    @Query("UPDATE blood_requests SET unitsFulfilled = :unitsFulfilled, status = :status WHERE requestId = :requestId")
    suspend fun updateUnitsFulfilled(requestId: String, unitsFulfilled: Int, status: String)

    @Query("UPDATE blood_requests SET assignedDonorId = :donorId, assignedDonorName = :donorName, status = :status WHERE requestId = :requestId")
    suspend fun assignDonor(requestId: String, donorId: String, donorName: String, status: String)

    @Query("SELECT COUNT(*) FROM blood_requests")
    suspend fun countRequests(): Int
}
