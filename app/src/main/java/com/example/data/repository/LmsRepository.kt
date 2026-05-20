package com.example.data.repository

import com.example.data.dao.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class LmsRepository(
    private val studentDao: StudentDao,
    private val attendanceDao: AttendanceDao,
    private val feeRecordDao: FeeRecordDao,
    private val tuteDao: TuteDao,
    private val examResultDao: ExamResultDao,
    private val announcementDao: AnnouncementDao
) {
    // Flow getters
    val allStudents: Flow<List<Student>> = studentDao.getAllStudents()
    val allAttendance: Flow<List<Attendance>> = attendanceDao.getAllAttendance()
    val allFees: Flow<List<FeeRecord>> = feeRecordDao.getAllFees()
    val allTutes: Flow<List<Tute>> = tuteDao.getAllTutes()
    val allResults: Flow<List<ExamResult>> = examResultDao.getAllResults()
    val allAnnouncements: Flow<List<Announcement>> = announcementDao.getAllAnnouncements()

    // Student operations
    fun getStudent(studentId: String): Flow<Student?> = studentDao.getStudentById(studentId)
    suspend fun getStudentDirect(studentId: String): Student? = studentDao.getStudentByIdDirect(studentId)
    suspend fun insertStudent(student: Student) = studentDao.insertStudent(student)
    suspend fun updateStudent(student: Student) = studentDao.updateStudent(student)
    suspend fun deleteStudent(student: Student) {
        studentDao.deleteStudent(student)
        // Clean up related data of the deleted student
        attendanceDao.clearAttendanceForStudent(student.studentId)
        feeRecordDao.clearFeesForStudent(student.studentId)
        examResultDao.clearResultsForStudent(student.studentId)
    }

    // Attendance operations
    fun getAttendanceForStudent(studentId: String): Flow<List<Attendance>> = attendanceDao.getAttendanceForStudent(studentId)
    suspend fun insertAttendance(attendance: Attendance) = attendanceDao.insertAttendance(attendance)
    suspend fun deleteAttendance(attendance: Attendance) = attendanceDao.deleteAttendance(attendance)

    // Fees operations
    fun getFeesForStudent(studentId: String): Flow<List<FeeRecord>> = feeRecordDao.getFeesForStudent(studentId)
    suspend fun insertFeeRecord(feeRecord: FeeRecord) = feeRecordDao.insertFeeRecord(feeRecord)
    suspend fun deleteFeeRecord(feeRecord: FeeRecord) = feeRecordDao.deleteFeeRecord(feeRecord)

    // Tutes operations
    fun getTuteById(id: Int): Flow<Tute?> = tuteDao.getTuteById(id)
    suspend fun insertTute(tute: Tute) = tuteDao.insertTute(tute)
    suspend fun deleteTute(tute: Tute) = tuteDao.deleteTute(tute)

    // Results operations
    fun getResultsForStudent(studentId: String): Flow<List<ExamResult>> = examResultDao.getResultsForStudent(studentId)
    suspend fun insertResult(result: ExamResult) = examResultDao.insertResult(result)
    suspend fun deleteResult(result: ExamResult) = examResultDao.deleteResult(result)

    // Announcements operations
    suspend fun insertAnnouncement(announcement: Announcement) = announcementDao.insertAnnouncement(announcement)
    suspend fun deleteAnnouncement(announcement: Announcement) = announcementDao.deleteAnnouncement(announcement)

    // Prepopulate database with default initial data for demo/evaluation
    suspend fun prepopulateDatabaseIfEmpty() {
        val count = studentDao.getStudentByIdDirect("STU001")
        if (count == null) {
            // Add Students
            val students = listOf(
                Student("STU001", "Amal Perera", "amal@gmail.com", "0771234567", "12345", "2026-01-10"),
                Student("STU002", "Ruwan Gamage", "ruwan@gmail.com", "0719876543", "12345", "2026-02-15"),
                Student("STU003", "Nishi Silva", "nishi@gmail.com", "0751112223", "12345", "2026-03-01"),
                Student("STU004", "Kasun Jayasekara", "kasun@gmail.com", "0785556667", "12345", "2026-03-20")
            )
            for (st in students) {
                studentDao.insertStudent(st)
            }

            // Add Announcements
            val announcements = listOf(
                Announcement(title = "Welcome to RN LMS!", message = "Hi Students! Welcome to the official RN LMS Student Portal. Track your ICT marks, lessons, download digital ID cards, and access lms.rnlms.edu.lk study materials.", date = "2026-05-18"),
                Announcement(title = "ICT Grade 11 Monthly Test", message = "Grade 11 ICT monthly paper on HTML and Algorithms will be held next Tuesday. Prepare with HTML coding exercises.", date = "2026-05-19"),
                Announcement(title = "Logic Gates Tute Issued", message = "Logic Gates and Truth Tables comprehensive study tute has been released. Grade 10 students can collect print outs or download digital versions.", date = "2026-05-20")
            )
            for (ann in announcements) {
                announcementDao.insertAnnouncement(ann)
            }
 
            // Add Tutes
            val tutes = listOf(
                Tute(
                    title = "Grade 10: Logic Gates & Truth Tables",
                    topic = "ICT Grade 10",
                    description = "Mastering AND, OR, NOT, NAND, NOR logic systems and Boolean expressions.",
                    content = "1. Introduction to Logic Gates\nLogic gates are the basic building blocks of any digital system. They process binary input values (0 and 1) to produce a single output value.\n\n2. Primary Logic Gates\n- AND Gate: Output is 1 only if all inputs are 1. Formula: Y = A • B\n- OR Gate: Output is 1 if at least one input is 1. Formula: Y = A + B\n- NOT Gate: Inverts the input signal. Formula: Y = A'\n\n3. Truth Tables\nA truth table displays all possible input configurations and their resulting outputs. For a 2-input gate, there are 2² = 4 input rows:\n(A=0,B=0), (A=0,B=1), (A=1,B=0), (A=1,B=1).",
                    releaseDate = "2026-05-10"
                ),
                Tute(
                    title = "Grade 11: HTML & CSS Programming",
                    topic = "ICT Grade 11",
                    description = "Learn how to build static web pages with headings, lists, tables, links, and clean CSS styling.",
                    content = "1. HTML Structure\nHTML is the standard mockup markup language for creating web pages. The basic hierarchy is:\n\n<html>\n  <head>\n    <title>My Web Page</title>\n  </head>\n  <body>\n    <h1>Welcome to RN LMS Academy</h1>\n    <p>We are learning CSS styling today.</p>\n  </body>\n</html>\n\n2. Key Elements\n- <a> for links (e.g., href=\"http://rnlms.edu.lk\")\n- <img> for inserting photos\n- <ul> / <li> for bullet lists.\n\n3. CSS (Cascading Style Sheets)\nUsed for styling. Can be inline, internal, or external. Example:\nh1 { color: #1E40AF; font-size: 24px; }",
                    releaseDate = "2026-05-12"
                ),
                Tute(
                    title = "Grade 8: Algorithms & Scratch Coding",
                    topic = "ICT Grade 8",
                    description = "Introduction to visual sequence flowcharts and basic sprite animations using Scratch programming blocks.",
                    content = "1. What is an Algorithm?\nAn algorithm is a step-by-step procedure or sequence of instructions to solve a particular problem in a finite amount of time.\n\n2. Flowchart Symbols\n- Oval: Start / End\n- Parallelogram: Input / Output\n- Rectangle: Process block\n- Diamond: Decision point\n\n3. Coding in Scratch\nScratch is a visual drag-and-drop language. We use coding sprites, event listeners ('when green flag clicked'), control loops ('repeat 10 times'), and variable math variables.",
                    releaseDate = "2026-05-15"
                )
            )
            for (t in tutes) {
                tuteDao.insertTute(t)
            }
 
            // Add Attendance Records
            val attendanceList = listOf(
                Attendance(studentId = "STU001", date = "2026-05-10", status = "Present", remarks = "Arrived on time"),
                Attendance(studentId = "STU001", date = "2026-05-12", status = "Present", remarks = "Arrived on time"),
                Attendance(studentId = "STU001", date = "2026-05-15", status = "Absent", remarks = "Sick leave"),
                Attendance(studentId = "STU001", date = "2026-05-18", status = "Present", remarks = "Syllabus tute checked"),
                Attendance(studentId = "STU002", date = "2026-05-10", status = "Present"),
                Attendance(studentId = "STU002", date = "2026-05-12", status = "Absent"),
                Attendance(studentId = "STU002", date = "2026-05-15", status = "Present"),
                Attendance(studentId = "STU003", date = "2026-05-10", status = "Present"),
                Attendance(studentId = "STU003", date = "2026-05-12", status = "Present")
            )
            for (att in attendanceList) {
                attendanceDao.insertAttendance(att)
            }
 
            // Add Fees Records
            val feesList = listOf(
                FeeRecord(studentId = "STU001", month = "April 2026", amount = 3000.0, status = "Paid", paymentDate = "2026-04-05", remarks = "LMS tutes authorized"),
                FeeRecord(studentId = "STU001", month = "May 2026", amount = 3000.0, status = "Paid", paymentDate = "2026-05-02", remarks = "Card payment receipt"),
                FeeRecord(studentId = "STU002", month = "April 2026", amount = 3000.0, status = "Paid", paymentDate = "2026-04-10"),
                FeeRecord(studentId = "STU002", month = "May 2026", amount = 3000.0, status = "Pending", remarks = "Tute printouts withheld until fee cleared"),
                FeeRecord(studentId = "STU003", month = "May 2026", amount = 3000.0, status = "Paid", paymentDate = "2026-05-01")
            )
            for (fee in feesList) {
                feeRecordDao.insertFeeRecord(fee)
            }
 
            // Add Exam Results
            val resultsList = listOf(
                ExamResult(studentId = "STU001", examName = "HTML Unit Test 1", subject = "ICT Grade 11", marks = 85.0, grade = "A", date = "2026-05-05"),
                ExamResult(studentId = "STU001", examName = "Binary Arithmetic Quiz", subject = "ICT Grade 10", marks = 62.0, grade = "C", date = "2026-05-15"),
                ExamResult(studentId = "STU002", examName = "HTML Unit Test 1", subject = "ICT Grade 11", marks = 45.0, grade = "W", date = "2026-05-05"),
                ExamResult(studentId = "STU002", examName = "Binary Arithmetic Quiz", subject = "ICT Grade 10", marks = 78.0, grade = "B", date = "2026-05-15"),
                ExamResult(studentId = "STU003", examName = "HTML Unit Test 1", subject = "ICT Grade 11", marks = 92.0, grade = "A", date = "2026-05-05")
            )
            for (res in resultsList) {
                examResultDao.insertResult(res)
            }
        }
    }
}
