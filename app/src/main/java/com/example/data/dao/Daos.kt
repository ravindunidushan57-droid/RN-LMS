package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM students ORDER BY name ASC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE studentId = :studentId LIMIT 1")
    fun getStudentById(studentId: String): Flow<Student?>

    @Query("SELECT * FROM students WHERE studentId = :studentId LIMIT 1")
    suspend fun getStudentByIdDirect(studentId: String): Student?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)
}

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance ORDER BY date DESC")
    fun getAllAttendance(): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE studentId = :studentId ORDER BY date DESC")
    fun getAttendanceForStudent(studentId: String): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE date = :date")
    fun getAttendanceForDate(date: String): Flow<List<Attendance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: Attendance)

    @Delete
    suspend fun deleteAttendance(attendance: Attendance)

    @Query("DELETE FROM attendance WHERE studentId = :studentId")
    suspend fun clearAttendanceForStudent(studentId: String)
}

@Dao
interface FeeRecordDao {
    @Query("SELECT * FROM fee_records ORDER BY id DESC")
    fun getAllFees(): Flow<List<FeeRecord>>

    @Query("SELECT * FROM fee_records WHERE studentId = :studentId ORDER BY id DESC")
    fun getFeesForStudent(studentId: String): Flow<List<FeeRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeeRecord(feeRecord: FeeRecord)

    @Delete
    suspend fun deleteFeeRecord(feeRecord: FeeRecord)

    @Query("DELETE FROM fee_records WHERE studentId = :studentId")
    suspend fun clearFeesForStudent(studentId: String)
}

@Dao
interface TuteDao {
    @Query("SELECT * FROM tutes ORDER BY id DESC")
    fun getAllTutes(): Flow<List<Tute>>

    @Query("SELECT * FROM tutes WHERE id = :id LIMIT 1")
    fun getTuteById(id: Int): Flow<Tute?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTute(tute: Tute)

    @Delete
    suspend fun deleteTute(tute: Tute)
}

@Dao
interface ExamResultDao {
    @Query("SELECT * FROM exam_results ORDER BY id DESC")
    fun getAllResults(): Flow<List<ExamResult>>

    @Query("SELECT * FROM exam_results WHERE studentId = :studentId ORDER BY id DESC")
    fun getResultsForStudent(studentId: String): Flow<List<ExamResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: ExamResult)

    @Delete
    suspend fun deleteResult(result: ExamResult)

    @Query("DELETE FROM exam_results WHERE studentId = :studentId")
    suspend fun clearResultsForStudent(studentId: String)
}

@Dao
interface AnnouncementDao {
    @Query("SELECT * FROM announcements ORDER BY id DESC")
    fun getAllAnnouncements(): Flow<List<Announcement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: Announcement)

    @Delete
    suspend fun deleteAnnouncement(announcement: Announcement)
}
