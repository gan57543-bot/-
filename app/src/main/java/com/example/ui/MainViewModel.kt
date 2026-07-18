package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.api.GeminiClient
import com.example.api.GeminiContent
import com.example.api.GeminiPart
import com.example.api.GeminiRequest
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = Repository(database)

    // Exposed flows from Room database
    val configState: StateFlow<AppConfig> = repository.configFlow
        .map { it ?: AppConfig() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppConfig())

    val milestones: StateFlow<List<Milestone>> = repository.milestonesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<ProjectNote>> = repository.notesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val assets: StateFlow<List<MockAsset>> = repository.assetsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inventories: StateFlow<List<MockInventory>> = repository.inventoriesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val documents: StateFlow<List<MockDocument>> = repository.documentsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val repairs: StateFlow<List<MockRepair>> = repository.repairsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI State Management (Navigation & Tab states)
    private val _currentTab = MutableStateFlow("overview") // "overview", "modules", "architecture", "roadmap", "ai_assistant"
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    private val _currentModuleTab = MutableStateFlow("asset") // "asset", "inventory", "document", "repair"
    val currentModuleTab: StateFlow<String> = _currentModuleTab.asStateFlow()

    // AI Assistant States
    private val _aiResponse = MutableStateFlow<String>("")
    val aiResponse: StateFlow<String> = _aiResponse.asStateFlow()

    private val _isAiLoading = MutableStateFlow<Boolean>(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _aiError = MutableStateFlow<String?>(null)
    val aiError: StateFlow<String?> = _aiError.asStateFlow()

    init {
        // Initialize sample records and milestones if empty
        viewModelScope.launch {
            repository.initializeDatabaseIfEmpty()
        }
    }

    // Tab Navigation
    fun setTab(tab: String) {
        _currentTab.value = tab
    }

    fun setModuleTab(subTab: String) {
        _currentModuleTab.value = subTab
    }

    // Config Actions
    fun updateSchoolName(newName: String) {
        viewModelScope.launch {
            val config = configState.value.copy(schoolName = newName)
            repository.saveConfig(config)
        }
    }

    fun updateAcademicYear(year: String) {
        viewModelScope.launch {
            val config = configState.value.copy(academicYear = year)
            repository.saveConfig(config)
        }
    }

    fun updateLineNotificationSetting(enabled: Boolean) {
        viewModelScope.launch {
            val config = configState.value.copy(enableLineNotifications = enabled)
            repository.saveConfig(config)
        }
    }

    fun updateRole(role: String) {
        viewModelScope.launch {
            repository.updateRole(role)
        }
    }

    // Milestone Actions (Roadmap)
    fun toggleMilestoneCompleted(milestone: Milestone) {
        viewModelScope.launch {
            repository.updateMilestone(milestone.copy(isCompleted = !milestone.isCompleted))
        }
    }

    fun addCustomMilestone(title: String, description: String, category: String) {
        viewModelScope.launch {
            repository.insertMilestone(Milestone(title = title, description = description, category = category))
        }
    }

    fun deleteMilestone(milestone: Milestone) {
        viewModelScope.launch {
            repository.deleteMilestone(milestone)
        }
    }

    // Notes Actions (Dev Notes)
    fun addNote(title: String, content: String) {
        viewModelScope.launch {
            repository.insertNote(ProjectNote(title = title, content = content))
        }
    }

    fun deleteNote(note: ProjectNote) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    // --- Interactive Modules Simulators & Auto Sequence Generators ---

    fun addMockAsset(carrier: String, sender: String, receiver: String) {
        viewModelScope.launch {
            val currentAssets = assets.value
            val config = configState.value
            // Auto Sequence (ระบบสร้างเลขพัสดุอัตโนมัติ)
            val nextNo = currentAssets.size + 1
            val trackingNo = "${config.assetPrefix}-${config.academicYear}-${String.format("%03d", nextNo)}"
            
            val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            val asset = MockAsset(
                trackingNo = trackingNo,
                carrierName = carrier,
                sender = sender,
                receiver = receiver,
                receiveDate = today,
                status = "รอดำเนินการ"
            )
            repository.insertAsset(asset)
        }
    }

    fun updateAssetStatus(asset: MockAsset, status: String) {
        viewModelScope.launch {
            repository.updateAsset(asset.copy(status = status))
        }
    }

    fun deleteAsset(asset: MockAsset) {
        viewModelScope.launch {
            repository.deleteAsset(asset)
        }
    }

    fun addMockInventory(name: String, serial: String, location: String, cost: Double) {
        viewModelScope.launch {
            val currentInventories = inventories.value
            val config = configState.value
            // Auto Sequence (ระบบสร้างเลขครุภัณฑ์)
            val nextNo = currentInventories.size + 1
            // เช่น "7110-001-2569"
            val itemCode = "7110-${String.format("%03d", nextNo)}-${config.academicYear}"
            
            val item = MockInventory(
                itemCode = itemCode,
                name = name,
                serialNumber = serial.ifEmpty { "N/A" },
                location = location,
                costValue = cost,
                status = "ปกติ"
            )
            repository.insertInventory(item)
        }
    }

    fun updateInventoryStatus(item: MockInventory, status: String) {
        viewModelScope.launch {
            repository.updateInventory(item.copy(status = status))
        }
    }

    fun deleteInventory(item: MockInventory) {
        viewModelScope.launch {
            repository.deleteInventory(item)
        }
    }

    fun addMockDocument(subject: String, sender: String, receiver: String, priority: String) {
        viewModelScope.launch {
            val currentDocs = documents.value
            val config = configState.value
            // Auto Sequence (เลขรับหนังสือเข้า)
            val nextNo = currentDocs.size + 1
            val docNo = "ศธ 04002/${config.docPrefix}-${String.format("%03d", nextNo)}"
            
            val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            val doc = MockDocument(
                docNo = docNo,
                subject = subject,
                sender = sender,
                receiver = receiver,
                priority = priority,
                docDate = today,
                status = "กำลังดำเนินการ"
            )
            repository.insertDocument(doc)
        }
    }

    fun updateDocumentStatus(doc: MockDocument, status: String) {
        viewModelScope.launch {
            repository.updateDocument(doc.copy(status = status))
        }
    }

    fun deleteDocument(doc: MockDocument) {
        viewModelScope.launch {
            repository.deleteDocument(doc)
        }
    }

    fun addMockRepair(itemName: String, problem: String, location: String, requester: String) {
        viewModelScope.launch {
            val currentRepairs = repairs.value
            val config = configState.value
            // Auto Sequence (เลขแจ้งซ่อม)
            val nextNo = currentRepairs.size + 1
            val repairNo = "${config.repairPrefix}-${config.academicYear}-${String.format("%03d", nextNo)}"
            
            val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            val repair = MockRepair(
                repairNo = repairNo,
                itemName = itemName,
                problem = problem,
                location = location,
                requester = requester,
                date = today,
                status = "รอดำเนินการ"
            )
            repository.insertRepair(repair)
        }
    }

    fun updateRepairStatus(repair: MockRepair, status: String) {
        viewModelScope.launch {
            repository.updateRepair(repair.copy(status = status))
        }
    }

    fun deleteRepair(repair: MockRepair) {
        viewModelScope.launch {
            repository.deleteRepair(repair)
        }
    }

    // --- Gemini AI Assistant Actions ---

    fun askGemini(prompt: String) {
        if (prompt.isBlank()) return
        _isAiLoading.value = true
        _aiError.value = null
        _aiResponse.value = ""

        viewModelScope.launch {
            try {
                val config = configState.value
                val systemPrompt = """
                    คุณคือ "ผู้เชี่ยวชาญสถาปัตยกรรม Google Apps Script และระบบ SMART OFFICE SUITE โรงเรียน"
                    หน้าที่หลักของคุณคือช่วยผู้ใช้ทำความเข้าใจระบบ SMART OFFICE SUITE (โรงเรียน ${config.schoolName} ประจำปีงบประมาณ พ.ศ. ${config.academicYear}) 
                    คุณสามารถสร้างโค้ด Google Apps Script (GAS) สำหรับโมดูลต่างๆ เช่น:
                    1. ระบบบันทึกพัสดุ (Asset) ที่เชื่อมโยงเข้ากับ Google Sheets
                    2. ระบบครุภัณฑ์ (Inventory) การสร้างรหัสอัตโนมัติและตรวจสอบสถานะ
                    3. ระบบรับส่งหนังสือราชการ (Document) และจัดลำดับความสำคัญ
                    4. ระบบใบแจ้งซ่อมบำรุง (Repair) สำหรับส่งใบสั่งซ่อมหาผู้บำรุงรักษา
                    5. ระบบส่งการแจ้งเตือนพัสดุหรือสถานะซ่อมผ่าน LINE Notify
                    6. ระบบเลขรับเอกสารอัตโนมัติ (Sequence Generator) ด้วย Apps Script
                    
                    กรุณาตอบคำถามอย่างเป็นทางการ สุภาพ และอธิบายเชิงเทคนิคพร้อมโค้ด Apps Script ตัวอย่างที่สามารถนำไปประยุกต์ใช้งานบน Google Sheets และ Google Apps Script Editor ได้ทันที โดยเขียนอธิบายเป็นภาษาไทยอย่างสวยงามและเข้าใจง่าย
                """.trimIndent()

                val request = GeminiRequest(
                    contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))),
                    systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt)))
                )

                // Key check
                val key = BuildConfig.GEMINI_API_KEY
                if (key.isEmpty() || key == "MY_GEMINI_API_KEY") {
                    _aiResponse.value = """
                        ⚠️ [ระบบจำลอง]: คีย์ Gemini API ยังไม่ได้ตั้งค่าผ่าน Secrets Panel ของ AI Studio.
                        
                        เพื่อให้คุณเห็นตัวอย่างคำตอบแบบเป็นระบบ นี่คือโครงสร้างตัวอย่าง Google Apps Script สำหรับระบบจัดทำลำดับเลขที่เอกสารอัตโนมัติ (Sequence Generator):
                        
                        ```javascript
                        // ฟังก์ชันดึงเลขที่ถัดไปตามประเภทโมดูล
                        function getNextSequence(moduleName) {
                          var sheet = SpreadsheetApp.getActiveSpreadsheet().getSheetByName("Sequence");
                          var data = sheet.getDataRange().getValues();
                          
                          for (var i = 1; i < data.length; i++) {
                            if (data[i][0] === moduleName) {
                              var currentCounter = data[i][1];
                              var prefix = data[i][2];
                              var year = data[i][3];
                              
                              var nextCounter = currentCounter + 1;
                              // บันทึกค่าใหม่ลงชีตเพื่อป้องกันการทำซ้ำ
                              sheet.getRange(i + 1, 2).setValue(nextCounter);
                              
                              // จัดรูปแบบรหัส เช่น AST-2569-001
                              var paddedCounter = ("000" + nextCounter).slice(-3);
                              return prefix + "-" + year + "-" + paddedCounter;
                            }
                          }
                          return "ERR-SEQ";
                        }
                        ```
                        
                        💡 กรุณาเพิ่ม `GEMINI_API_KEY` ใน Secrets panel มุมซ้ายล่างของ AI Studio เพื่อเปิดใช้งานฟังก์ชันพูดคุยและสร้างโค้ดด้วย AI แบบเต็มระบบ!
                    """.trimIndent()
                } else {
                    val response = GeminiClient.service.generateContent(key, request)
                    val reply = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (reply != null) {
                        _aiResponse.value = reply
                    } else {
                        _aiError.value = "ไม่สามารถอ่านคำตอบของ AI ได้ กรุณาลองใหม่อีกครั้ง"
                    }
                }
            } catch (e: Exception) {
                _aiError.value = "เกิดข้อผิดพลาดในการเชื่อมต่อ AI: ${e.localizedMessage ?: e.message}"
            } finally {
                _isAiLoading.value = false
            }
        }
    }

    fun applyPresetPrompt(presetName: String) {
        val config = configState.value
        val promptText = when (presetName) {
            "sequence" -> "เขียนโค้ด Google Apps Script ฟังก์ชันทำ Sequence Generator อัตโนมัติ ป้องกันชนกันแบบ Lock Service สำหรับปีงบประมาณ ${config.academicYear}"
            "line_notify" -> "เขียนโค้ด Google Apps Script แจ้งเตือนผ่าน LINE Notify สำหรับระบบรับพัสดุ (Asset) มีชื่อผู้รับและผู้ส่งแบบข้อความสั้น"
            "database_gas" -> "ขอโค้ด Google Apps Script เริ่มต้นสร้างแผ่นงาน Google Sheets ทั้งหมด และฟังก์ชันบันทึกข้อมูลแบบ Modular สำหรับระบบ Smart Office"
            "repair_service" -> "เขียนโค้ด Google Apps Script สร้างหน้ากากแจ้งซ่อม (Repair Form) ด้วย HTML Service แสดงสถานะงานซ่อมปัจจุบัน"
            else -> ""
        }
        askGemini(promptText)
    }
}
