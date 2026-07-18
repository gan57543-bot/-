package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class Repository(private val db: AppDatabase) {

    // DAOs
    private val configDao = db.appConfigDao()
    private val milestoneDao = db.milestoneDao()
    private val noteDao = db.projectNoteDao()
    private val assetDao = db.mockAssetDao()
    private val inventoryDao = db.mockInventoryDao()
    private val documentDao = db.mockDocumentDao()
    private val repairDao = db.mockRepairDao()

    // Exposed Flows
    val configFlow: Flow<AppConfig?> = configDao.getConfigFlow()
    val milestonesFlow: Flow<List<Milestone>> = milestoneDao.getAllMilestones()
    val notesFlow: Flow<List<ProjectNote>> = noteDao.getAllNotes()
    val assetsFlow: Flow<List<MockAsset>> = assetDao.getAllAssets()
    val inventoriesFlow: Flow<List<MockInventory>> = inventoryDao.getAllInventories()
    val documentsFlow: Flow<List<MockDocument>> = documentDao.getAllDocuments()
    val repairsFlow: Flow<List<MockRepair>> = repairDao.getAllRepairs()

    // Initialize with default values if empty
    suspend fun initializeDatabaseIfEmpty() {
        // 1. Config Setup
        val currentConfig = configDao.getConfig()
        if (currentConfig == null) {
            configDao.saveConfig(AppConfig())
        }

        // 2. Milestones (Future Plans) Setup
        val milestones = milestoneDao.getAllMilestones().firstOrNull()
        if (milestones.isNullOrEmpty()) {
            milestoneDao.deleteAllMilestones()
            val defaults = listOf(
                Milestone(title = "ระบบประชุมดิจิทัล (Meeting System)", description = "จัดการวาระการประชุม มติที่ประชุม และลงลายมือชื่อดิจิทัลสำหรับครูและบุคลากร", category = "Feature"),
                Milestone(title = "ระบบขอใช้รถราชการ (Vehicle Request)", description = "จัดทำใบคำขอใช้รถยนต์ส่วนกลางของโรงเรียน บริหารตารางเวลาคนขับ และคำนวณเส้นทาง", category = "Feature"),
                Milestone(title = "ระบบบันทึกผู้มาติดต่อ (Visitor System)", description = "ลงทะเบียนสแกนบัตรผู้ปกครองหรือแขกภายนอก พร้อมรหัส QR ตรวจสอบเวลาเข้าออก", category = "Feature"),
                Milestone(title = "ระบบจองห้องประชุมและอุปกรณ์ (Room Reservation)", description = "ปฏิทินกลางสำหรับตรวจเช็คตารางห้องประชุม จองใช้ห้องสัมมนา หรืออุปกรณ์โสตทัศนูปกรณ์", category = "Feature"),
                Milestone(title = "ระบบสถิติสรุป Dashboard กราฟ (Charts & Stats)", description = "วิเคราะห์ข้อมูลเชิงลึกเป็นแผนภูมิแท่ง แผนภูมิวงกลม แสดงปริมาณงานและงบประมาณสะสม", category = "Integration"),
                Milestone(title = "การส่งออก PDF และ Excel (Export PDF/Excel)", description = "พัฒนาระบบดาวน์โหลดทะเบียนพัสดุ ใบเสนอซ่อม และหนังสือราชการนำส่งในรูปแบบ PDF/Excel", category = "Integration"),
                Milestone(title = "ระบบสแกน QR Code / Barcode (Asset Scanning)", description = "สร้างและสแกนรหัสแท่งประจำคุรุภัณฑ์ ตรวจสอบประวัติบำรุงรักษาผ่านกล้องสมาร์ทโฟน", category = "Core"),
                Milestone(title = "ระบบแจ้งเตือนผ่าน LINE Notify (LINE Notifications)", description = "แจ้งเตือนเหตุการสำคัญ เช่น พัสดุมาถึง, อนุมัติการซ่อมบำรุง, ส่งตรงไปยังกลุ่มไลน์ครู", category = "Integration")
            )
            for (m in defaults) {
                milestoneDao.insertMilestone(m)
            }
        }

        // 3. Populate Sample Assets
        val assets = assetDao.getAllAssets().firstOrNull()
        if (assets.isNullOrEmpty()) {
            assetDao.insertAsset(MockAsset(trackingNo = "AST-2569-001", carrierName = "Flash Express", sender = "ร้านหนังสือศึกษิตสยาม", receiver = "ครูสมชาย (กลุ่มสาระภาษาไทย)", receiveDate = "18/07/2569", status = "รับแล้ว"))
            assetDao.insertAsset(MockAsset(trackingNo = "AST-2569-002", carrierName = "Kerry Express", sender = "บจก. เทคโนโลยีล้ำยุค", receiver = "ครูวิมล (หัวหน้าฝ่ายคอมพิวเตอร์)", receiveDate = "18/07/2569", status = "รอดำเนินการ"))
            assetDao.insertAsset(MockAsset(trackingNo = "AST-2569-003", carrierName = "ไปรษณีย์ไทย (EMS)", sender = "สำนักงานศึกษาธิการจังหวัด", receiver = "ฝ่ายอำนวยการ/ธุรการ", receiveDate = "17/07/2569", status = "รับแล้ว"))
        }

        // 4. Populate Sample Inventories
        val inventories = inventoryDao.getAllInventories().firstOrNull()
        if (inventories.isNullOrEmpty()) {
            inventoryDao.insertInventory(MockInventory(itemCode = "7110-001-2569", name = "เครื่องประมวลผลข้อมูล (Computer Laptop)", serialNumber = "LP-DELL-5520A", location = "ห้องสมุดอิเล็กทรอนิกส์", costValue = 29500.0, status = "ปกติ"))
            inventoryDao.insertInventory(MockInventory(itemCode = "7110-002-2569", name = "โปรเจคเตอร์ภาพ 4K (Projector)", serialNumber = "PJ-EPSON-X9", location = "ห้องประชุมราชพฤกษ์", costValue = 35000.0, status = "ปกติ"))
            inventoryDao.insertInventory(MockInventory(itemCode = "7110-003-2569", name = "เครื่องปรับอากาศ 24,000 BTU", serialNumber = "AC-DAIKIN-102", location = "ห้องวิชาการ", costValue = 18900.0, status = "ชำรุด"))
        }

        // 5. Populate Sample Documents
        val documents = documentDao.getAllDocuments().firstOrNull()
        if (documents.isNullOrEmpty()) {
            documentDao.insertDocument(MockDocument(docNo = "ศธ 04002/ว 228", subject = "แจ้งมาตรการรักษาความปลอดภัยในสถานศึกษาช่วงวันหยุดราชการ", sender = "สำนักงานเขตพื้นที่การศึกษามัธยมศึกษา", receiver = "ผู้อำนวยการโรงเรียน / งานปกครอง", priority = "ด่วนที่สุด", docDate = "16/07/2569", status = "เสนอผู้บริหาร"))
            documentDao.insertDocument(MockDocument(docNo = "ศธ 04002/1049", subject = "ขอเชิญร่วมจัดแสดงนิทรรศการผลงานทางวิชาการนวัตกรรมสิ่งประดิษฐ์", sender = "มหาวิทยาลัยเทคโนโลยีราชมงคล", receiver = "กลุ่มบริหารงานวิชาการ", priority = "ด่วน", docDate = "18/07/2569", status = "กำลังดำเนินการ"))
            documentDao.insertDocument(MockDocument(docNo = "โรงเรียน/112/2569", subject = "คำสั่งแต่งตั้งคณะกรรมการการดำเนินการสอบคัดเลือกกลางภาค", sender = "ฝ่ายธุรการโรงเรียน", receiver = "ครูและบุคลากรทุกท่าน", priority = "ปกติ", docDate = "15/07/2569", status = "เสร็จสิ้น"))
        }

        // 6. Populate Sample Repairs
        val repairs = repairDao.getAllRepairs().firstOrNull()
        if (repairs.isNullOrEmpty()) {
            repairDao.insertRepair(MockRepair(repairNo = "REP-2569-001", itemName = "เครื่องซ่อมบำรุงพัดลมติดผนัง", problem = "ใบพัดไม่หมุน มีเสียงดังครางเบาๆ เสี่ยงไฟช็อต", location = "ห้องเรียน ม.5/2", requester = "ครูนิตยา", date = "18/07/2569", status = "รอดำเนินการ"))
            repairDao.insertRepair(MockRepair(repairNo = "REP-2569-002", itemName = "ก๊อกน้ำและท่อระบายอ่างล้างมือ", problem = "เกลียวก๊อกหวานปิดไม่สนิท น้ำรั่วไหลตลอดเวลา", location = "ห้องน้ำนักเรียนหญิง ชั้น 2", requester = "ครูสมพร (งานเวรประจำวัน)", date = "18/07/2569", status = "กำลังซ่อม"))
        }
    }

    // Config Actions
    suspend fun saveConfig(config: AppConfig) = configDao.saveConfig(config)
    suspend fun updateRole(role: String) {
        val current = configDao.getConfig() ?: AppConfig()
        configDao.saveConfig(current.copy(currentRole = role))
    }

    // Milestones Actions
    suspend fun insertMilestone(milestone: Milestone) = milestoneDao.insertMilestone(milestone)
    suspend fun updateMilestone(milestone: Milestone) = milestoneDao.updateMilestone(milestone)
    suspend fun deleteMilestone(milestone: Milestone) = milestoneDao.deleteMilestone(milestone)

    // Notes Actions
    suspend fun insertNote(note: ProjectNote) = noteDao.insertNote(note)
    suspend fun deleteNote(note: ProjectNote) = noteDao.deleteNote(note)

    // Asset Actions
    suspend fun insertAsset(asset: MockAsset) = assetDao.insertAsset(asset)
    suspend fun updateAsset(asset: MockAsset) = assetDao.updateAsset(asset)
    suspend fun deleteAsset(asset: MockAsset) = assetDao.deleteAsset(asset)

    // Inventory Actions
    suspend fun insertInventory(inventory: MockInventory) = inventoryDao.insertInventory(inventory)
    suspend fun updateInventory(inventory: MockInventory) = inventoryDao.updateInventory(inventory)
    suspend fun deleteInventory(inventory: MockInventory) = inventoryDao.deleteInventory(inventory)

    // Document Actions
    suspend fun insertDocument(document: MockDocument) = documentDao.insertDocument(document)
    suspend fun updateDocument(document: MockDocument) = documentDao.updateDocument(document)
    suspend fun deleteDocument(document: MockDocument) = documentDao.deleteDocument(document)

    // Repair Actions
    suspend fun insertRepair(repair: MockRepair) = repairDao.insertRepair(repair)
    suspend fun updateRepair(repair: MockRepair) = repairDao.updateRepair(repair)
    suspend fun deleteRepair(repair: MockRepair) = repairDao.deleteRepair(repair)
}
