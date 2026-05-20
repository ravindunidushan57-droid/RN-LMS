package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "students")
data class Student(
    @PrimaryKey val studentId: String, // String ID like "STU001", "STU002"
    val name: String,
    val email: String,
    val phone: String,
    val password: String, // Password for Student Login
    val joiningDate: String,
    val imageUrl: String = "" // Optional profile image url or placeholder
) : Serializable

@Entity(tableName = "attendance")
data class Attendance(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentId: String,
    val date: String, // "YYYY-MM-DD"
    val status: String, // "Present", "Absent", "Late"
    val remarks: String = ""
) : Serializable

@Entity(tableName = "fee_records")
data class FeeRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentId: String,
    val month: String, // e.g. "May 2026"
    val amount: Double,
    val status: String, // "Paid", "Pending", "Overdue"
    val paymentDate: String = "", // Date of payment
    val remarks: String = ""
) : Serializable

@Entity(tableName = "tutes")
data class Tute(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val topic: String, // Subject/Topic
    val description: String,
    val content: String, // The full article/notes text so students can "read" it inside the app
    val releaseDate: String
) : Serializable

@Entity(tableName = "exam_results")
data class ExamResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentId: String,
    val examName: String, // e.g. "Mock Exam 1"
    val subject: String,
    val marks: Double,
    val maxMarks: Double = 100.0,
    val grade: String, // "A", "B", "C", "F"
    val date: String
) : Serializable

@Entity(tableName = "announcements")
data class Announcement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val message: String,
    val date: String
) : Serializable
