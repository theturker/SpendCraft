package com.alperen.spendcraft.data.db.dao

import androidx.room.*
import com.alperen.spendcraft.data.db.entities.SharingMemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SharingMemberDao {
    
    @Query("SELECT * FROM sharing_members WHERE householdId = :householdId")
    fun getMembersByHousehold(householdId: String): Flow<List<SharingMemberEntity>>
    
    @Query("SELECT * FROM sharing_members WHERE userId = :userId")
    suspend fun getMemberByUserId(userId: String): SharingMemberEntity?
    
    @Query("SELECT * FROM sharing_members WHERE householdId = :householdId AND role = 'OWNER'")
    suspend fun getHouseholdOwner(householdId: String): SharingMemberEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: SharingMemberEntity): Long
    
    @Update
    suspend fun updateMember(member: SharingMemberEntity)
    
    @Query("UPDATE sharing_members SET role = :role WHERE id = :id")
    suspend fun updateMemberRole(id: Long, role: String)
    
    @Query("UPDATE sharing_members SET joinedAt = :joinedAt WHERE id = :id")
    suspend fun markAsJoined(id: Long, joinedAt: Long)
    
    @Delete
    suspend fun deleteMember(member: SharingMemberEntity)
    
    @Query("DELETE FROM sharing_members WHERE householdId = :householdId")
    suspend fun deleteAllMembersFromHousehold(householdId: String)
}
