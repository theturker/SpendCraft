package com.alperen.spendcraft.core.achievements

import com.alperen.spendcraft.data.db.dao.AchievementDao
import com.alperen.spendcraft.data.db.dao.TransactionDao
import com.alperen.spendcraft.data.db.entities.AchievementEntity
import com.alperen.spendcraft.data.db.entities.AchievementCategory
import com.alperen.spendcraft.data.db.entities.TransactionEntity
import com.alperen.spendcraft.data.db.entities.TransactionType
import com.alperen.spendcraft.core.notifications.NotificationManager
import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementManager @Inject constructor(
    private val achievementDao: AchievementDao,
    private val transactionDao: TransactionDao,
    private val notificationManager: NotificationManager
) {
    
    suspend fun checkAchievements() {
        val achievements = achievementDao.getAllAchievements().first()
        
        achievements.forEach { achievement ->
            if (!achievement.isUnlocked) {
                val progress = calculateProgress(achievement)
                if (progress >= achievement.maxProgress) {
                    unlockAchievement(achievement)
                } else {
                    achievementDao.updateProgress(achievement.id, progress)
                }
            }
        }
    }
    
    private suspend fun calculateProgress(achievement: AchievementEntity): Int {
        return when (achievement.name) {
            "İlk Adım" -> checkFirstTransaction()
            "Hafta Savaşçısı" -> checkWeeklyStreak()
            "Bütçe Planlayıcısı" -> checkBudgetCreated()
            "Tasarruf Ustası" -> checkSavingsGoal()
            "Kategori Uzmanı" -> checkCategoryMaster()
            "Günlük Kayıt" -> checkDailyRecording()
            "Aylık Hedef" -> checkMonthlyGoal()
            "Yıllık Planlayıcı" -> checkYearlyPlanning()
            else -> 0
        }
    }
    
    private suspend fun checkFirstTransaction(): Int {
        val transactionCount = transactionDao.getTransactionCount()
        return if (transactionCount > 0) 1 else 0
    }
    
    private suspend fun checkWeeklyStreak(): Int {
        val transactions = transactionDao.getAllTransactions().first()
        val calendar = Calendar.getInstance()
        var streak = 0
        
        for (i in 0..6) {
            calendar.timeInMillis = System.currentTimeMillis() - (i * 24 * 60 * 60 * 1000L)
            val startOfDay = getStartOfDay(calendar.timeInMillis)
            val endOfDay = getEndOfDay(calendar.timeInMillis)
            
            val hasTransaction = transactions.any { transaction ->
                transaction.timestamp >= startOfDay && transaction.timestamp <= endOfDay
            }
            
            if (hasTransaction) {
                streak++
            } else {
                break
            }
        }
        
        return streak
    }
    
    private suspend fun checkBudgetCreated(): Int {
        // Bu fonksiyon BudgetRepository'den gelecek
        // Şimdilik mock
        return 1
    }
    
    private suspend fun checkSavingsGoal(): Int {
        val transactions = transactionDao.getAllTransactions().first()
        val totalIncome = transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount.amount }
        val totalExpense = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount.amount }
        
        val savings = totalIncome - totalExpense
        return if (savings > 0) 1 else 0
    }
    
    private suspend fun checkCategoryMaster(): Int {
        val transactions = transactionDao.getAllTransactions().first()
        val categoryCount = transactions.map { it.categoryId }.distinct().size
        return if (categoryCount >= 5) 1 else 0
    }
    
    private suspend fun checkDailyRecording(): Int {
        val transactions = transactionDao.getAllTransactions().first()
        val today = getStartOfDay(System.currentTimeMillis())
        val hasTodayTransaction = transactions.any { it.timestamp >= today }
        return if (hasTodayTransaction) 1 else 0
    }
    
    private suspend fun checkMonthlyGoal(): Int {
        val transactions = transactionDao.getAllTransactions().first()
        val thisMonth = getStartOfMonth(System.currentTimeMillis())
        val monthlyTransactions = transactions.filter { it.timestamp >= thisMonth }
        return if (monthlyTransactions.size >= 30) 1 else 0
    }
    
    private suspend fun checkYearlyPlanning(): Int {
        val transactions = transactionDao.getAllTransactions().first()
        val thisYear = getStartOfYear(System.currentTimeMillis())
        val yearlyTransactions = transactions.filter { it.timestamp >= thisYear }
        return if (yearlyTransactions.size >= 100) 1 else 0
    }
    
    private fun unlockAchievement(achievement: AchievementEntity) {
        val updatedAchievement = achievement.copy(
            isUnlocked = true,
            unlockedAt = System.currentTimeMillis()
        )
        achievementDao.updateAchievement(updatedAchievement)
        notificationManager.showAchievementNotification(
            achievement.name,
            achievement.description
        )
    }
    
    private fun getStartOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    private fun getEndOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
    
    private fun getStartOfMonth(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    private fun getStartOfYear(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.DAY_OF_YEAR, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
