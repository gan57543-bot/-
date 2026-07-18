package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// --- Settings and System Configuration ---
@Entity(tableName = "app_config")
data class AppConfig(
    @PrimaryKey val id: Int = 1,
    val schoolName: String = "โรงเรียนเตรียมอุดมศึกษาดิจิทัล",
    val academicYear: String = "2569",
    val enableLineNotifications: Boolean = false,
    val assetPrefix: String = "AST",
    val docPrefix: String = "DOC",
    val repairPrefix: String = "REP",
    val currentRole: String = "ผู้ดูแลระบบ" // "ผู้ดูแลระบบ" (Admin), "เจ้าหน้าที่งานพัสดุ" (Officer), "ครูผู้ใช้งาน" (Teacher)
)

// --- Future Project Milestones (Roadmap) ---
@Entity(tableName = "milestones")
data class Milestone(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val category: String // "Core", "Feature", "Integration"
)

// --- Project Notes & Developer Logs ---
@Entity(tableName = "project_notes")
data class ProjectNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

// --- Mock Module Data: Asset Management (ระบบรับพัสดุ) ---
@Entity(tableName = "mock_assets")
data class MockAsset(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val trackingNo: String,
    val carrierName: String, // เช่น "Kerry", "Flash", "ไปรษณีย์ไทย"
    val sender: String,
    val receiver: String, // ชื่อครูผู้รับปลายทาง
    val receiveDate: String, // Format: DD/MM/YYYY
    val status: String // "รอดำเนินการ", "รับแล้ว", "ตีกลับ"
)

// --- Mock Module Data: Inventory Management (ระบบครุภัณฑ์) ---
@Entity(tableName = "mock_inventories")
data class MockInventory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val itemCode: String, // เช่น ครุภัณฑ์เลขที่ "7110-001-2569"
    val name: String,
    val serialNumber: String,
    val location: String, // เช่น "ห้องประชุม 1", "ห้องคอมพิวเตอร์"
    val costValue: Double,
    val status: String // "ปกติ", "ชำรุด", "แทงจำหน่าย"
)

// --- Mock Module Data: Document Management (ระบบหนังสือราชการ) ---
@Entity(tableName = "mock_documents")
data class MockDocument(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val docNo: String, // เช่น "ศธ 04002/123"
    val subject: String,
    val sender: String, // หน่วยงานเจ้าของเรื่อง
    val receiver: String, // ผู้รับ/เสนอ
    val priority: String, // "ปกติ", "ด่วน", "ด่วนที่สุด"
    val docDate: String,
    val status: String // "กำลังดำเนินการ", "เสร็จสิ้น", "เสนอผู้บริหาร"
)

// --- Mock Module Data: Repair System (ระบบแจ้งซ่อม - กำลังพัฒนา) ---
@Entity(tableName = "mock_repairs")
data class MockRepair(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val repairNo: String, // เช่น "REP-2569-001"
    val itemName: String,
    val problem: String,
    val location: String,
    val requester: String,
    val date: String,
    val status: String // "รอดำเนินการ", "กำลังซ่อม", "เสร็จสิ้น"
)
