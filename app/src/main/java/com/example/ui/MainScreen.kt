package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*

// Color Palette for Slate Tech Vibe (Avoid default grey slop)
val Slate900 = Color(0xFF0F172A)
val Slate800 = Color(0xFF1E293B)
val Slate700 = Color(0xFF334155)
val Slate600 = Color(0xFF475569)
val PrimaryBlue = Color(0xFF3B82F6)
val SoftBlue = Color(0xFF60A5FA)
val AccentPurple = Color(0xFF8B5CF6)
val EmeraldGreen = Color(0xFF10B981)
val WarmOrange = Color(0xFFF59E0B)
val CrimsonRed = Color(0xFFEF4444)
val OffWhite = Color(0xFFF8FAFC)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val config by viewModel.configState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Brush.linearGradient(listOf(PrimaryBlue, AccentPurple))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Business,
                                contentDescription = "Logo",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "SMART OFFICE SUITE",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate900,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "บริหารสำนักงานดิจิทัลโรงเรียน",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate600,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                actions = {
                    // Role Selector badge (Permissions)
                    RoleSelectorBadge(
                        currentRole = config.currentRole,
                        onRoleChange = { viewModel.updateRole(it) }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Slate900
                ),
                modifier = Modifier.shadow(1.dp)
            )
        },
        bottomBar = {
            BottomNavigationTabs(
                currentTab = currentTab,
                onTabSelected = { viewModel.setTab(it) }
            )
        },
        containerColor = OffWhite
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "TabContent"
            ) { targetTab ->
                when (targetTab) {
                    "overview" -> OverviewTab(viewModel)
                    "modules" -> ModulesTab(viewModel)
                    "architecture" -> ArchitectureTab()
                    "roadmap" -> RoadmapTab(viewModel)
                    "ai_assistant" -> AiAssistantTab(viewModel)
                }
            }
        }
    }
}

@Composable
fun RoleSelectorBadge(currentRole: String, onRoleChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Surface(
            onClick = { expanded = true },
            shape = RoundedCornerShape(20.dp),
            color = when (currentRole) {
                "ผู้ดูแลระบบ" -> PrimaryBlue.copy(alpha = 0.12f)
                "เจ้าหน้าที่งานพัสดุ" -> EmeraldGreen.copy(alpha = 0.12f)
                else -> WarmOrange.copy(alpha = 0.12f)
            },
            border = BorderStroke(
                width = 1.dp,
                color = when (currentRole) {
                    "ผู้ดูแลระบบ" -> PrimaryBlue
                    "เจ้าหน้าที่งานพัสดุ" -> EmeraldGreen
                    else -> WarmOrange
                }
            ),
            modifier = Modifier
                .padding(end = 12.dp)
                .testTag("role_badge")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = when (currentRole) {
                        "ผู้ดูแลระบบ" -> Icons.Default.AdminPanelSettings
                        "เจ้าหน้าที่งานพัสดุ" -> Icons.Default.LocalShipping
                        else -> Icons.Default.Person
                    },
                    contentDescription = null,
                    tint = when (currentRole) {
                        "ผู้ดูแลระบบ" -> PrimaryBlue
                        "เจ้าหน้าที่งานพัสดุ" -> EmeraldGreen
                        else -> WarmOrange
                    },
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = currentRole,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Slate900,
                    fontSize = 11.sp
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Slate700,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            DropdownMenuItem(
                text = { Text("ผู้ดูแลระบบ (Admin)", color = Slate900) },
                leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = PrimaryBlue) },
                onClick = {
                    onRoleChange("ผู้ดูแลระบบ")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("เจ้าหน้าที่งานพัสดุ (Officer)", color = Slate900) },
                leadingIcon = { Icon(Icons.Default.LocalShipping, contentDescription = null, tint = EmeraldGreen) },
                onClick = {
                    onRoleChange("เจ้าหน้าที่งานพัสดุ")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("ครูผู้ใช้งาน (Teacher)", color = Slate900) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = WarmOrange) },
                onClick = {
                    onRoleChange("ครูผู้ใช้งาน")
                    expanded = false
                }
            )
        }
    }
}

@Composable
fun BottomNavigationTabs(currentTab: String, onTabSelected: (String) -> Unit) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.shadow(12.dp)
    ) {
        val tabs = listOf(
            Triple("overview", "สรุป & สถิติ", Icons.Default.Dashboard),
            Triple("modules", "โมดูลระบบ", Icons.Default.ViewModule),
            Triple("architecture", "สถาปัตยกรรม", Icons.Default.AccountTree),
            Triple("roadmap", "แผนงาน", Icons.Default.Map),
            Triple("ai_assistant", "AI ผู้ช่วย", Icons.Default.AutoAwesome)
        )

        tabs.forEach { (route, label, icon) ->
            NavigationBarItem(
                selected = currentTab == route,
                onClick = { onTabSelected(route) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (currentTab == route) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue,
                    selectedTextColor = PrimaryBlue,
                    indicatorColor = PrimaryBlue.copy(alpha = 0.08f),
                    unselectedIconColor = Slate600,
                    unselectedTextColor = Slate600
                ),
                modifier = Modifier.testTag("tab_$route")
            )
        }
    }
}

// ==================== TABS IMPLEMENTATION ====================

// --- 1. OVERVIEW TAB ---
@Composable
fun OverviewTab(viewModel: MainViewModel) {
    val config by viewModel.configState.collectAsStateWithLifecycle()
    val assets by viewModel.assets.collectAsStateWithLifecycle()
    val inventories by viewModel.inventories.collectAsStateWithLifecycle()
    val documents by viewModel.documents.collectAsStateWithLifecycle()
    val repairs by viewModel.repairs.collectAsStateWithLifecycle()

    var showEditConfigDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Slate900, Slate800)
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = AccentPurple.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = " โครงการเด่น Google Apps Script ",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentPurple,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = "SMART OFFICE SUITE",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "ระบบบริหารจัดการสำนักงานดิจิทัลครบวงจรสำหรับโรงเรียน บูรณาการฐานข้อมูล Google Sheets และขับเคลื่อนด้วย Google Apps Script แบบไร้เอกสาร 100%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OffWhite.copy(alpha = 0.85f),
                        lineHeight = 22.sp
                    )
                    
                    Divider(color = Slate700, modifier = Modifier.padding(vertical = 4.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, tint = SoftBlue, modifier = Modifier.size(16.dp))
                        Text(
                            text = config.schoolName,
                            style = MaterialTheme.typography.bodySmall,
                            color = SoftBlue,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = SoftBlue, modifier = Modifier.size(14.dp))
                        Text(
                            text = "ปีงบประมาณ ${config.academicYear}",
                            style = MaterialTheme.typography.bodySmall,
                            color = SoftBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Live Statistics Cards Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "สถิติจำลองการประมวลผล",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "พัสดุรับเข้า",
                        value = assets.size.toString(),
                        subtitle = "รอดำเนินการ: ${assets.count { it.status == "รอดำเนินการ" }}",
                        color = PrimaryBlue,
                        icon = Icons.Default.LocalShipping,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "ทะเบียนครุภัณฑ์",
                        value = inventories.size.toString(),
                        subtitle = "ชำรุด: ${inventories.count { it.status == "ชำรุด" }} รายการ",
                        color = AccentPurple,
                        icon = Icons.Default.Inventory,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "หนังสือเข้า-ออก",
                        value = documents.size.toString(),
                        subtitle = "เรื่องด่วนที่สุด: ${documents.count { it.priority == "ด่วนที่สุด" }}",
                        color = WarmOrange,
                        icon = Icons.Default.Description,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "ใบงานแจ้งซ่อม",
                        value = repairs.size.toString(),
                        subtitle = "รอคิวซ่อม: ${repairs.count { it.status == "รอดำเนินการ" }} รายการ",
                        color = CrimsonRed,
                        icon = Icons.Default.Build,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // System Settings & Config Panel
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Slate700.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = Slate900)
                            Text(
                                text = "ตั้งค่าโครงสร้างระบบ (Config)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                        }
                        
                        TextButton(
                            onClick = { showEditConfigDialog = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = PrimaryBlue)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("แก้ไขค่ากำหนด", fontWeight = FontWeight.Bold)
                        }
                    }

                    ConfigItemRow(
                        title = "ชื่อโรงเรียนที่ติดตั้ง",
                        value = config.schoolName,
                        icon = Icons.Outlined.School
                    )
                    ConfigItemRow(
                        title = "ปีงบประมาณและเลขรหัสเอกสาร",
                        value = "ปี พ.ศ. ${config.academicYear} | คำนำหน้าพัสดุ: ${config.assetPrefix} | หนังสือ: ${config.docPrefix} | แจ้งซ่อม: ${config.repairPrefix}",
                        icon = Icons.Outlined.Pin
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Slate900.copy(alpha = 0.03f))
                        .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (config.enableLineNotifications) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                                contentDescription = null,
                                tint = if (config.enableLineNotifications) EmeraldGreen else Slate600
                            )
                            Column {
                                Text(
                                    text = "ระบบจำลองการส่งการแจ้งเตือน LINE Notify",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                                Text(
                                    text = "แจ้งเตือนอัตโนมัติเมื่อมีพัสดุใหม่หรือใบแจ้งซ่อมใหม่",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate600
                                )
                            }
                        }
                        Switch(
                            checked = config.enableLineNotifications,
                            onCheckedChange = { viewModel.updateLineNotificationSetting(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = EmeraldGreen
                            )
                        )
                    }
                }
            }
        }

        // About Description Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Slate900.copy(alpha = 0.03f)),
                border = BorderStroke(1.dp, Slate700.copy(alpha = 0.05f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "เกี่ยวกับโครงการ",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Text(
                        text = "SMART OFFICE SUITE ได้ถูกออกแบบด้วยแนวคิด Modular Architecture โดยใช้ Google Sheets ทำหน้าที่เป็นฐานข้อมูลเชิงสัมพันธ์ร่วมกับการเขียนสคริปต์กลาง (Common Core Utils, Sequence Generator, Router) ทุกโมดูลจะเข้าถึงและแชร์ฟังก์ชันร่วมกันเพื่อลดความซ้ำซ้อนและสามารถขยายระบบได้อย่างรวดเร็วในระยะยาว",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate700,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }

    // Config Edit Dialog
    if (showEditConfigDialog) {
        var schoolNameInput by remember { mutableStateOf(config.schoolName) }
        var academicYearInput by remember { mutableStateOf(config.academicYear) }

        Dialog(onDismissRequest = { showEditConfigDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "ตั้งค่าโครงสร้างระบบ",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )

                    OutlinedTextField(
                        value = schoolNameInput,
                        onValueChange = { schoolNameInput = it },
                        label = { Text("ชื่อโรงเรียน") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = academicYearInput,
                        onValueChange = { academicYearInput = it },
                        label = { Text("ปีงบประมาณ (พ.ศ.)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { showEditConfigDialog = false },
                            colors = ButtonDefaults.textButtonColors(contentColor = Slate600)
                        ) {
                            Text("ยกเลิก")
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Button(
                            onClick = {
                                viewModel.updateSchoolName(schoolNameInput)
                                viewModel.updateAcademicYear(academicYearInput)
                                showEditConfigDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Text("บันทึกข้อมูล", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Slate700.copy(alpha = 0.08f)),
        modifier = modifier.shadow(2.dp, shape = RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate600
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = Slate900
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Slate600,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ConfigItemRow(title: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryBlue,
            modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp)
        )
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Slate600
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = Slate900
            )
        }
    }
}


// --- 2. MODULES TAB (INTERACTIVE SIMULATOR) ---
@Composable
fun ModulesTab(viewModel: MainViewModel) {
    val moduleTab by viewModel.currentModuleTab.collectAsStateWithLifecycle()
    val config by viewModel.configState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        // Horizontal Scrollable Segmented Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ModuleTabItem(
                title = "ระบบรับพัสดุ (Asset)",
                isSelected = moduleTab == "asset",
                color = PrimaryBlue,
                onClick = { viewModel.setModuleTab("asset") }
            )
            ModuleTabItem(
                title = "ทะเบียนครุภัณฑ์ (Inventory)",
                isSelected = moduleTab == "inventory",
                color = AccentPurple,
                onClick = { viewModel.setModuleTab("inventory") }
            )
            ModuleTabItem(
                title = "หนังสือราชการ (Document)",
                isSelected = moduleTab == "document",
                color = WarmOrange,
                onClick = { viewModel.setModuleTab("document") }
            )
            ModuleTabItem(
                title = "แจ้งซ่อมบำรุง (Repair)",
                isSelected = moduleTab == "repair",
                color = CrimsonRed,
                onClick = { viewModel.setModuleTab("repair") }
            )
        }

        // Active Role Warning Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Slate900.copy(alpha = 0.04f))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Slate600, modifier = Modifier.size(14.dp))
                Text(
                    text = "สิทธิ์การจำลองปัจจุบัน: [${config.currentRole}] (เปลี่ยนบทบาทได้ที่ปุ่มขวาบนสุด)",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate700,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Module Screen Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (moduleTab) {
                "asset" -> AssetSimulatorScreen(viewModel)
                "inventory" -> InventorySimulatorScreen(viewModel)
                "document" -> DocumentSimulatorScreen(viewModel)
                "repair" -> RepairSimulatorScreen(viewModel)
            }
        }
    }
}

@Composable
fun ModuleTabItem(
    title: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) color else Color.White,
        border = BorderStroke(1.dp, if (isSelected) color else Slate700.copy(alpha = 0.15f)),
        shadowElevation = if (isSelected) 3.dp else 0.dp
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Slate700,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

// 2.1 ASSET SIMULATOR
@Composable
fun AssetSimulatorScreen(viewModel: MainViewModel) {
    val assets by viewModel.assets.collectAsStateWithLifecycle()
    val config by viewModel.configState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("ระบบจัดการพัสดุโรงเรียน", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Slate900)
                Text("จำลองระบบรับพัสดุเข้าส่วนกลางและแจกจ่ายไปยังครูผู้รับ", style = MaterialTheme.typography.bodySmall, color = Slate600)
            }
            
            // Permissions Check: Teachers shouldn't record parcels in standard school, but let's allow Admin or Officer roles
            val canAdd = config.currentRole == "ผู้ดูแลระบบ" || config.currentRole == "เจ้าหน้าที่งานพัสดุ"
            
            Button(
                onClick = { showAddDialog = true },
                enabled = canAdd,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("รับพัสดุ", fontWeight = FontWeight.Bold)
            }
        }

        if (assets.isEmpty()) {
            EmptyListPlaceholder(message = "ไม่มีข้อมูลพัสดุ")
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(assets) { asset ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Slate700.copy(alpha = 0.08f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = asset.trackingNo,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                                StatusBadge(
                                    status = asset.status,
                                    colors = when (asset.status) {
                                        "รับแล้ว" -> Pair(EmeraldGreen, EmeraldGreen.copy(alpha = 0.1f))
                                        "ตีกลับ" -> Pair(CrimsonRed, CrimsonRed.copy(alpha = 0.1f))
                                        else -> Pair(WarmOrange, WarmOrange.copy(alpha = 0.1f))
                                    }
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Slate700)) {
                                        append("ผู้จัดส่ง: ")
                                    }
                                    append("${asset.carrierName} (${asset.sender})")
                                }
                            )
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Slate700)) {
                                        append("ผู้รับปลายทาง: ")
                                    }
                                    append(asset.receiver)
                                }
                            )
                            Text(
                                text = "วันที่ลงทะเบียน: ${asset.receiveDate}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate600
                            )
                            
                            // Action Buttons
                            val isOfficerOrAdmin = config.currentRole == "ผู้ดูแลระบบ" || config.currentRole == "เจ้าหน้าที่งานพัสดุ"
                            if (asset.status == "รอดำเนินการ" && isOfficerOrAdmin) {
                                Divider(modifier = Modifier.padding(vertical = 8.dp), color = Slate900.copy(alpha = 0.05f))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(
                                        onClick = { viewModel.updateAssetStatus(asset, "ตีกลับ") },
                                        colors = ButtonDefaults.textButtonColors(contentColor = CrimsonRed)
                                    ) {
                                        Text("ตีกลับพัสดุ", fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { viewModel.updateAssetStatus(asset, "รับแล้ว") },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("เซ็นรับของเรียบร้อย", fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var carrierInput by remember { mutableStateOf("Flash Express") }
        var senderInput by remember { mutableStateOf("") }
        var receiverInput by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showAddDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "รับพัสดุใหม่ (บันทึกเข้าระบบ)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )

                    // Carrier Selection Row
                    Text("เลือกผู้จัดส่ง", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Slate700)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val carriers = listOf("Flash Express", "Kerry Express", "ไปรษณีย์ไทย", "J&T Express", "Grab/Lineman")
                        carriers.forEach { c ->
                            FilterChip(
                                selected = carrierInput == c,
                                onClick = { carrierInput = c },
                                label = { Text(c) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = senderInput,
                        onValueChange = { senderInput = it },
                        label = { Text("ชื่อร้านค้า / ผู้ส่งพัสดุ") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = receiverInput,
                        onValueChange = { receiverInput = it },
                        label = { Text("ชื่อครูผู้รับ (ระบุฝ่าย/วิชาด้วย)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showAddDialog = false }) {
                            Text("ยกเลิก")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (senderInput.isNotBlank() && receiverInput.isNotBlank()) {
                                    viewModel.addMockAsset(carrierInput, senderInput, receiverInput)
                                    showAddDialog = false
                                }
                            },
                            enabled = senderInput.isNotBlank() && receiverInput.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Text("บันทึกรับพัสดุ", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// 2.2 INVENTORY SIMULATOR
@Composable
fun InventorySimulatorScreen(viewModel: MainViewModel) {
    val inventories by viewModel.inventories.collectAsStateWithLifecycle()
    val config by viewModel.configState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("ทะเบียนคุมครุภัณฑ์โรงเรียน", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Slate900)
                Text("บันทึกทรัพย์สินที่มีมูลค่าทางบัญชี ตรวจจับรหัสครุภัณฑ์และสภาพความพร้อมใช้งาน", style = MaterialTheme.typography.bodySmall, color = Slate600)
            }
            
            val canAdd = config.currentRole == "ผู้ดูแลระบบ" || config.currentRole == "เจ้าหน้าที่งานพัสดุ"
            
            Button(
                onClick = { showAddDialog = true },
                enabled = canAdd,
                colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("เพิ่มครุภัณฑ์", fontWeight = FontWeight.Bold)
            }
        }

        if (inventories.isEmpty()) {
            EmptyListPlaceholder(message = "ไม่มีข้อมูลครุภัณฑ์")
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(inventories) { item ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Slate700.copy(alpha = 0.08f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "รหัส: ${item.itemCode}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentPurple
                                )
                                StatusBadge(
                                    status = item.status,
                                    colors = when (item.status) {
                                        "ปกติ" -> Pair(EmeraldGreen, EmeraldGreen.copy(alpha = 0.1f))
                                        "ชำรุด" -> Pair(CrimsonRed, CrimsonRed.copy(alpha = 0.1f))
                                        else -> Pair(Slate700, Slate700.copy(alpha = 0.1f))
                                    }
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            Text(text = item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Slate900)
                            Text(text = "S/N: ${item.serialNumber} | สถานที่ตั้ง: ${item.location}")
                            Text(
                                text = "มูลค่าครุภัณฑ์: ${String.format("%,.2f", item.costValue)} บาท",
                                fontWeight = FontWeight.Bold,
                                color = Slate700
                            )
                            
                            // Mock Barcode Render to make it look premium
                            Spacer(modifier = Modifier.height(8.dp))
                            BarcodeRenderer(code = item.itemCode)

                            // Quick actions: Send repair if broken
                            val isTeacherOrAdmin = config.currentRole == "ผู้ดูแลระบบ" || config.currentRole == "ครูผู้ใช้งาน"
                            if (item.status == "ปกติ" && isTeacherOrAdmin) {
                                Divider(modifier = Modifier.padding(vertical = 8.dp), color = Slate900.copy(alpha = 0.05f))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.updateInventoryStatus(item, "ชำรุด")
                                            // Automatically trigger a repair ticket! (Integration Core)
                                            viewModel.addMockRepair(
                                                itemName = item.name,
                                                problem = "ครุภัณฑ์ถูกรายงานชำรุดผ่านระบบพัสดุ",
                                                location = item.location,
                                                requester = "ครูผู้สแกนครุภัณฑ์ (${config.currentRole})"
                                            )
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonRed),
                                        border = BorderStroke(1.dp, CrimsonRed),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("รายงานชำรุด (ส่งซ่อม)", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var nameInput by remember { mutableStateOf("") }
        var serialInput by remember { mutableStateOf("") }
        var locationInput by remember { mutableStateOf("") }
        var costInput by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showAddDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "ขึ้นทะเบียนครุภัณฑ์ใหม่",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )

                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("ชื่อรายการครุภัณฑ์ (เช่น เก้าอี้เลคเชอร์, iPad)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = serialInput,
                        onValueChange = { serialInput = it },
                        label = { Text("เลขซีเรียลนัมเบอร์ (Serial Number)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = locationInput,
                        onValueChange = { locationInput = it },
                        label = { Text("สถานที่จัดวาง / ห้องเรียน") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = costInput,
                        onValueChange = { costInput = it },
                        label = { Text("ราคาครุภัณฑ์ต่อหน่วย (บาท)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showAddDialog = false }) {
                            Text("ยกเลิก")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val costVal = costInput.toDoubleOrNull() ?: 0.0
                                if (nameInput.isNotBlank() && locationInput.isNotBlank()) {
                                    viewModel.addMockInventory(nameInput, serialInput, locationInput, costVal)
                                    showAddDialog = false
                                }
                            },
                            enabled = nameInput.isNotBlank() && locationInput.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
                        ) {
                            Text("ขึ้นทะเบียน", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BarcodeRenderer(code: String) {
    // Generate a beautiful barcode visual directly with Canvas drawing!
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp)
            .background(Slate900.copy(alpha = 0.05f))
    ) {
        val linesCount = 40
        val lineSpace = size.width / linesCount
        // Hash code to produce pseudo-unique barcode lines
        val seed = code.hashCode()
        
        for (i in 0 until linesCount) {
            val isWhite = (seed xor (i * 25867)) % 3 == 0
            val strokeW = if (i % 5 == 0) 3f else 1.5f
            if (!isWhite) {
                drawLine(
                    color = Slate900,
                    start = Offset(x = i * lineSpace + (lineSpace/2), y = 6f),
                    end = Offset(x = i * lineSpace + (lineSpace/2), y = size.height - 6f),
                    strokeWidth = strokeW
                )
            }
        }
    }
}

// 2.3 DOCUMENT SIMULATOR
@Composable
fun DocumentSimulatorScreen(viewModel: MainViewModel) {
    val documents by viewModel.documents.collectAsStateWithLifecycle()
    val config by viewModel.configState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("งานรับ-ส่งหนังสือราชการ", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Slate900)
                Text("ลงทะเบียนรหัสเอกสารราชการภายนอก จัดส่งเรื่องเสนอแนะตามสายงาน", style = MaterialTheme.typography.bodySmall, color = Slate600)
            }
            
            val canAdd = config.currentRole == "ผู้ดูแลระบบ" || config.currentRole == "เจ้าหน้าที่งานพัสดุ"
            
            Button(
                onClick = { showAddDialog = true },
                enabled = canAdd,
                colors = ButtonDefaults.buttonColors(containerColor = WarmOrange),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("รับหนังสือ", fontWeight = FontWeight.Bold)
            }
        }

        if (documents.isEmpty()) {
            EmptyListPlaceholder(message = "ไม่มีประวัติการลงทะเบียนหนังสือเข้า")
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(documents) { doc ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Slate700.copy(alpha = 0.08f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = doc.docNo,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = WarmOrange
                                    )
                                    StatusBadge(
                                        status = doc.priority,
                                        colors = when (doc.priority) {
                                            "ด่วนที่สุด" -> Pair(CrimsonRed, CrimsonRed.copy(alpha = 0.1f))
                                            "ด่วน" -> Pair(WarmOrange, WarmOrange.copy(alpha = 0.1f))
                                            else -> Pair(Slate600, Slate600.copy(alpha = 0.1f))
                                        }
                                    )
                                }
                                StatusBadge(
                                    status = doc.status,
                                    colors = when (doc.status) {
                                        "เสร็จสิ้น" -> Pair(EmeraldGreen, EmeraldGreen.copy(alpha = 0.1f))
                                        "เสนอผู้บริหาร" -> Pair(PrimaryBlue, PrimaryBlue.copy(alpha = 0.1f))
                                        else -> Pair(WarmOrange, WarmOrange.copy(alpha = 0.1f))
                                    }
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            Text(text = doc.subject, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Slate900)
                            Text(text = "ต้นเรื่อง: ${doc.sender} ➔ ถึง: ${doc.receiver}")
                            Text(
                                text = "วันที่ออกเอกสาร: ${doc.docDate}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate600
                            )
                            
                            // Actions for Directors / Admins
                            val isAdminOrManager = config.currentRole == "ผู้ดูแลระบบ"
                            if (doc.status == "กำลังดำเนินการ" && isAdminOrManager) {
                                Divider(modifier = Modifier.padding(vertical = 8.dp), color = Slate900.copy(alpha = 0.05f))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(onClick = { viewModel.updateDocumentStatus(doc, "เสนอผู้บริหาร") }) {
                                        Text("ส่งเข้าเสนอผู้อำนวยการ", fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { viewModel.updateDocumentStatus(doc, "เสร็จสิ้น") },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("ลงนาม / เสร็จสิ้น", fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            } else if (doc.status == "เสนอผู้บริหาร" && isAdminOrManager) {
                                Divider(modifier = Modifier.padding(vertical = 8.dp), color = Slate900.copy(alpha = 0.05f))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Button(
                                        onClick = { viewModel.updateDocumentStatus(doc, "เสร็จสิ้น") },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("ผู้อำนวยการสั่งการ / ลงนามเสร็จสิ้น", fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var subjectInput by remember { mutableStateOf("") }
        var senderInput by remember { mutableStateOf("") }
        var receiverInput by remember { mutableStateOf("ผู้อำนวยการโรงเรียน") }
        var priorityInput by remember { mutableStateOf("ปกติ") }

        Dialog(onDismissRequest = { showAddDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "ลงทะเบียนหนังสือเข้าใหม่",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )

                    OutlinedTextField(
                        value = subjectInput,
                        onValueChange = { subjectInput = it },
                        label = { Text("เรื่อง / หัวข้อหนังสือราชการ") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = senderInput,
                        onValueChange = { senderInput = it },
                        label = { Text("หน่วยงานต้นเรื่อง (เช่น สพม., ฝ่ายปกครอง)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = receiverInput,
                        onValueChange = { receiverInput = it },
                        label = { Text("ผู้รับ / เสนอเรื่องต่อ") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("ความเร่งด่วน", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Slate700)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val priorities = listOf("ปกติ", "ด่วน", "ด่วนที่สุด")
                        priorities.forEach { p ->
                            FilterChip(
                                selected = priorityInput == p,
                                onClick = { priorityInput = p },
                                label = { Text(p) }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showAddDialog = false }) {
                            Text("ยกเลิก")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (subjectInput.isNotBlank() && senderInput.isNotBlank()) {
                                    viewModel.addMockDocument(subjectInput, senderInput, receiverInput, priorityInput)
                                    showAddDialog = false
                                }
                            },
                            enabled = subjectInput.isNotBlank() && senderInput.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = WarmOrange)
                        ) {
                            Text("ลงทะเบียนเลขหนังสือ", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// 2.4 REPAIR SIMULATOR (IN DEVELOPMENT MODULE)
@Composable
fun RepairSimulatorScreen(viewModel: MainViewModel) {
    val repairs by viewModel.repairs.collectAsStateWithLifecycle()
    val config by viewModel.configState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("ระบบแจ้งซ่อมบำรุงโรงเรียน", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Slate900)
                    Surface(
                        color = CrimsonRed.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = " กำลังพัฒนา ",
                            style = MaterialTheme.typography.labelSmall,
                            color = CrimsonRed,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
                Text("จำลองการส่งคำขอแจ้งซ่อมวัสดุอุปกรณ์ของครู และช่างบำรุงปรับสถานะ", style = MaterialTheme.typography.bodySmall, color = Slate600)
            }
            
            // Teachers, Admins, anyone can request repair! That is correct.
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Build, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("แจ้งซ่อม", fontWeight = FontWeight.Bold)
            }
        }

        if (repairs.isEmpty()) {
            EmptyListPlaceholder(message = "ไม่มีข้อมูลแจ้งซ่อม")
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(repairs) { repair ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Slate700.copy(alpha = 0.08f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "เลขคำขอ: ${repair.repairNo}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = CrimsonRed
                                )
                                StatusBadge(
                                    status = repair.status,
                                    colors = when (repair.status) {
                                        "เสร็จสิ้น" -> Pair(EmeraldGreen, EmeraldGreen.copy(alpha = 0.1f))
                                        "กำลังซ่อม" -> Pair(PrimaryBlue, PrimaryBlue.copy(alpha = 0.1f))
                                        else -> Pair(CrimsonRed, CrimsonRed.copy(alpha = 0.1f))
                                    }
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            Text(text = repair.itemName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Slate900)
                            Text(text = "อาการเสีย: ${repair.problem}")
                            Text(text = "สถานที่: ${repair.location} | ผู้แจ้ง: ${repair.requester}")
                            Text(
                                text = "แจ้งเมื่อวันที่: ${repair.date}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate600
                            )
                            
                            // Technicians (Officers/Admins) actions
                            val isTech = config.currentRole == "ผู้ดูแลระบบ" || config.currentRole == "เจ้าหน้าที่งานพัสดุ"
                            if (isTech && repair.status != "เสร็จสิ้น") {
                                Divider(modifier = Modifier.padding(vertical = 8.dp), color = Slate900.copy(alpha = 0.05f))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (repair.status == "รอดำเนินการ") {
                                        Button(
                                            onClick = { viewModel.updateRepairStatus(repair, "กำลังซ่อม") },
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text("รับงานซ่อมบำรุง", fontWeight = FontWeight.Bold)
                                        }
                                    } else if (repair.status == "กำลังซ่อม") {
                                        Button(
                                            onClick = { viewModel.updateRepairStatus(repair, "เสร็จสิ้น") },
                                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text("ปิดใบงานซ่อมสำเร็จ", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var itemInput by remember { mutableStateOf("") }
        var problemInput by remember { mutableStateOf("") }
        var locationInput by remember { mutableStateOf("") }
        var requesterInput by remember { mutableStateOf("ครูเวร") }

        Dialog(onDismissRequest = { showAddDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "ส่งใบแจ้งซ่อมบำรุงใหม่",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )

                    OutlinedTextField(
                        value = itemInput,
                        onValueChange = { itemInput = it },
                        label = { Text("ชื่อรายการอุปกรณ์ชำรุด (เช่น ลูกบิดประตู, หลอดไฟ)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = problemInput,
                        onValueChange = { problemInput = it },
                        label = { Text("ระบุอาการเสียโดยย่อ") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = locationInput,
                        onValueChange = { locationInput = it },
                        label = { Text("ระบุสถานที่ตั้ง / ห้องเรียน") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = requesterInput,
                        onValueChange = { requesterInput = it },
                        label = { Text("ชื่อครูผู้แจ้งซ่อม") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showAddDialog = false }) {
                            Text("ยกเลิก")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (itemInput.isNotBlank() && locationInput.isNotBlank()) {
                                    viewModel.addMockRepair(itemInput, problemInput, locationInput, requesterInput)
                                    showAddDialog = false
                                }
                            },
                            enabled = itemInput.isNotBlank() && locationInput.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                        ) {
                            Text("ส่งใบแจ้งงาน", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun StatusBadge(status: String, colors: Pair<Color, Color>) {
    Surface(
        color = colors.second,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, colors.first.copy(alpha = 0.5f))
    ) {
        Text(
            text = " $status ",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = colors.first,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun EmptyListPlaceholder(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Inbox,
                contentDescription = null,
                tint = Slate600.copy(alpha = 0.4f),
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Slate600.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold
            )
        }
    }
}


// --- 3. ARCHITECTURE TAB ---
@Composable
fun ArchitectureTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "โครงสร้างโมดูลระบบส่วนกลาง",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Slate900
        )
        Text(
            text = "SMART OFFICE SUITE ได้รับการเขียนโครงสร้างกลางบน Google Apps Script เพื่อให้ทุกระบบแชร์ไลบรารีเดียวกัน ลดปัญหาเขียนซ้ำซ้อน และเปลี่ยนหน้าประมวลผลได้อย่างลื่นไหล",
            style = MaterialTheme.typography.bodyMedium,
            color = Slate700,
            lineHeight = 22.sp
        )

        // Architectural Flow diagram block
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(BorderStroke(1.dp, Slate700.copy(alpha = 0.1f)))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Layer 1: Application Modules
                ArchitectureLayerCard(
                    title = "APPLICATION MODULES (ระบบงานสำนักงาน)",
                    description = "Dashboard / พัสดุ (Asset) / ครุภัณฑ์ (Inventory) / งานหนังสือราชการ (Document) / งานแจ้งซ่อมบำรุง (Repair)",
                    color = AccentPurple,
                    icon = Icons.Default.ViewModule
                )
                
                Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = Slate600)

                // Layer 2: Core Shared Framework
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate900.copy(alpha = 0.03f)),
                    border = BorderStroke(1.dp, Slate700.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "SHARED CORE SERVICES (สคริปต์สถาปัตยกรรมร่วม)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CoreServiceCard(title = "Config\n(ตั้งค่า)", icon = Icons.Default.Settings, modifier = Modifier.weight(1f))
                            CoreServiceCard(title = "Sequence\n(รันเลขเอกสาร)", icon = Icons.Default.Pin, modifier = Modifier.weight(1f))
                            CoreServiceCard(title = "Router\n(สลับหน้า)", icon = Icons.Default.Router, modifier = Modifier.weight(1f))
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CoreServiceCard(title = "Permissions\n(ระบบสิทธิ์)", icon = Icons.Default.Lock, modifier = Modifier.weight(1f))
                            CoreServiceCard(title = "Utils\n(เครื่องมือย่อย)", icon = Icons.Default.BuildCircle, modifier = Modifier.weight(1f))
                        }
                    }
                }

                Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = Slate600)

                // Layer 3: Database backend
                ArchitectureLayerCard(
                    title = "DATABASE BACKEND (ฐานข้อมูลกลาง)",
                    description = "Google Sheets API (แผ่นงานคุมข้อมูลพัสดุ ทะเบียนครุภัณฑ์ ทะเบียนประวัติ และพาราเมตอร์ระบบ)",
                    color = PrimaryBlue,
                    icon = Icons.Default.Storage
                )
            }
        }

        // Structural benefits summary list
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "ประโยชน์หลักของการสถาปัตยกรรมแบบนี้",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )

            BenefitRow(
                title = "ลดการทำซ้ำ (Zero Redundancy)",
                desc = "เช่น ฟังก์ชันการรันหมายเลขพัสดุ (Sequence) ใช้ฟังก์ชันศูนย์ร่วมกันทุกหน้า ไม่ต้องเขียนนับเลขแยกในแต่ละชีตเอง"
            )
            BenefitRow(
                title = "ความปลอดภัยระดับแถวข้อมูล (Role Protection)",
                desc = "ตั้งค่าสิทธิ์แยกแยะ เช่น ฝ่ายทั่วไปเขียนเฉพาะใบแจ้งซ่อมบำรุง, แอดมินจัดการอนุมัติเท่านั้น ด้วย Apps Script User Authentication"
            )
            BenefitRow(
                title = "เชื่อมต่อง่าย (Modular Addons)",
                desc = "ในอนาคต หากต้องการเปิดระบบขอใช้รถราชการ หรือระบบผู้มาติดต่อ ก็สามารถเรียกใช้ Database และ Sequence Module เดิมได้ภายใน 5 นาที"
            )
        }
    }
}

@Composable
fun ArchitectureLayerCard(
    title: String,
    description: String,
    color: Color,
    icon: ImageVector
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, color),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(text = title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Slate900)
                Text(text = description, style = MaterialTheme.typography.bodySmall, color = Slate700)
            }
        }
    }
}

@Composable
fun CoreServiceCard(title: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Slate700.copy(alpha = 0.1f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Slate900,
                textAlign = TextAlign.Center,
                fontSize = 10.sp,
                lineHeight = 12.sp
            )
        }
    }
}

@Composable
fun BenefitRow(title: String, desc: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(18.dp).padding(top = 2.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Slate900)
            Text(text = desc, style = MaterialTheme.typography.bodySmall, color = Slate700)
        }
    }
}


// --- 4. ROADMAP TAB ---
@Composable
fun RoadmapTab(viewModel: MainViewModel) {
    val milestones by viewModel.milestones.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("แผนพัฒนาเฟสถัดไป (Roadmap)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Slate900)
                Text("สถิติความก้าวหน้าและการเปิดโมดูลเพิ่มเติมในอนาคต บันทึกประวัติผ่านฐานข้อมูล", style = MaterialTheme.typography.bodySmall, color = Slate600)
            }
            
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("เพิ่มแผนงาน", fontWeight = FontWeight.Bold)
            }
        }

        // Percentage progress bar
        val completedCount = milestones.count { it.isCompleted }
        val totalCount = milestones.size
        val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
        
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Slate700.copy(alpha = 0.08f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("ความก้าวหน้าโครงการโดยรวม", fontWeight = FontWeight.Bold, color = Slate900)
                    Text("${(progress * 100).toInt()}% ($completedCount/$totalCount)", fontWeight = FontWeight.Bold, color = PrimaryBlue)
                }
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = PrimaryBlue,
                    trackColor = PrimaryBlue.copy(alpha = 0.1f)
                )
                Text(
                    text = "โครงการ SMART OFFICE SUITE คาดหวังเป็นแพลตฟอร์มบริหารสำนักงานครบวงจรในสถานศึกษา คุณสามารถทำเครื่องหมายขีดฆ่าเมื่อพัฒนาฟังก์ชันนั้นๆ สำเร็จ",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate600
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(milestones) { milestone ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (milestone.isCompleted) EmeraldGreen.copy(alpha = 0.03f) else Color.White
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (milestone.isCompleted) EmeraldGreen.copy(alpha = 0.3f) else Slate700.copy(alpha = 0.08f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Checkbox(
                            checked = milestone.isCompleted,
                            onCheckedChange = { viewModel.toggleMilestoneCompleted(milestone) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = EmeraldGreen,
                                uncheckedColor = Slate600
                            )
                        )
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = milestone.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (milestone.isCompleted) Slate600 else Slate900,
                                    textDecoration = if (milestone.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                                )
                                Surface(
                                    color = when (milestone.category) {
                                        "Core" -> AccentPurple.copy(alpha = 0.1f)
                                        "Feature" -> PrimaryBlue.copy(alpha = 0.1f)
                                        else -> WarmOrange.copy(alpha = 0.1f)
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = " ${milestone.category} ",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (milestone.category) {
                                            "Core" -> AccentPurple
                                            "Feature" -> PrimaryBlue
                                            else -> WarmOrange
                                        },
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = milestone.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate700,
                                lineHeight = 16.sp
                            )
                        }

                        IconButton(onClick = { viewModel.deleteMilestone(milestone) }) {
                            Icon(Icons.Default.Delete, contentDescription = "ลบ", tint = Slate600)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var titleInput by remember { mutableStateOf("") }
        var descInput by remember { mutableStateOf("") }
        var categoryInput by remember { mutableStateOf("Feature") }

        Dialog(onDismissRequest = { showAddDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "เพิ่มรายการแผนงานในอนาคต",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )

                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("ชื่อโมดูล / ฟังก์ชันเป้าหมาย") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = descInput,
                        onValueChange = { descInput = it },
                        label = { Text("คำอธิบายเป้าหมายฟังก์ชัน") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("หมวดหมู่แผนงาน", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Slate700)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val categories = listOf("Feature", "Core", "Integration")
                        categories.forEach { c ->
                            FilterChip(
                                selected = categoryInput == c,
                                onClick = { categoryInput = c },
                                label = { Text(c) }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showAddDialog = false }) {
                            Text("ยกเลิก")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (titleInput.isNotBlank()) {
                                    viewModel.addCustomMilestone(titleInput, descInput, categoryInput)
                                    showAddDialog = false
                                }
                            },
                            enabled = titleInput.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Text("บันทึกแผน", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}


// --- 5. AI ASSISTANT (GEMINI SANDBOX) ---
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiAssistantTab(viewModel: MainViewModel) {
    val aiResponse by viewModel.aiResponse.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val aiError by viewModel.aiError.collectAsStateWithLifecycle()

    var textInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("ผู้ช่วยสถาปัตยกรรม Apps Script", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Slate900)
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AccentPurple, modifier = Modifier.size(18.dp))
            }
            Text("ผู้ช่วยอัจฉริยะช่วยคุณตอบคำถามเกี่ยวกับการดีไซน์ โครงสร้างตาราง หรือดึงสคริปต์ Apps Script เพื่อเชื่อมต่อไปยังชีตโรงเรียนได้ทันที", style = MaterialTheme.typography.bodySmall, color = Slate600)
        }

        // Presets container
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900.copy(alpha = 0.03f)),
            border = BorderStroke(1.dp, Slate700.copy(alpha = 0.08f))
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("เทมเพลต Apps Script สำเร็จรูป (คลิกเพื่อขอโค้ดจาก AI)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Slate900)
                
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PresetButton(label = "ตัวรันเลขSequence", onClick = { viewModel.applyPresetPrompt("sequence") })
                    PresetButton(label = "สคริปต์ LINE Notify", onClick = { viewModel.applyPresetPrompt("line_notify") })
                    PresetButton(label = "ฐานข้อมูลชีตเริ่มแรก", onClick = { viewModel.applyPresetPrompt("database_gas") })
                    PresetButton(label = "สคริปต์แจ้งซ่อมเว็บ", onClick = { viewModel.applyPresetPrompt("repair_service") })
                }
            }
        }

        // Response box
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Slate700.copy(alpha = 0.1f)),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                if (isAiLoading) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = AccentPurple)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("กำลังออกแบบสถาปัตยกรรมและประมวลผลคำตอบจาก AI...", style = MaterialTheme.typography.bodySmall, color = Slate700)
                    }
                } else if (aiError != null) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = CrimsonRed, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(aiError!!, style = MaterialTheme.typography.bodyMedium, color = CrimsonRed, textAlign = TextAlign.Center)
                    }
                } else if (aiResponse.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
                    ) {
                        Icon(Icons.Default.Code, contentDescription = null, tint = Slate600.copy(alpha = 0.3f), modifier = Modifier.size(48.dp))
                        Text(
                            text = "ยินดีต้อนรับสู่สำนักงานดิจิทัล AI Assistant",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate600
                        )
                        Text(
                            text = "พิมพ์คำถามของคุณ หรือคลิกปุ่มเทมเพลตสีส้มด้านบน เพื่อให้ AI ร่าง Apps Script สวยงามให้คุณดาวน์โหลดทันที",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate600,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                } else {
                    // Render generated script beautifully with vertical scroll
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = aiResponse,
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Monospace,
                            color = Slate900,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }

        // Input row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text("ถามวิธีย้ายระบบลง Apps Script...") },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (textInput.isNotBlank()) {
                        viewModel.askGemini(textInput)
                        textInput = ""
                    }
                }),
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_input_field"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentPurple,
                    unfocusedBorderColor = Slate700.copy(alpha = 0.2f)
                )
            )

            FloatingActionButton(
                onClick = {
                    if (textInput.isNotBlank()) {
                        viewModel.askGemini(textInput)
                        textInput = ""
                    }
                },
                containerColor = AccentPurple,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("ai_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "ส่ง",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun PresetButton(label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = WarmOrange.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, WarmOrange.copy(alpha = 0.5f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Icon(Icons.Default.Code, contentDescription = null, tint = WarmOrange, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = WarmOrange
            )
        }
    }
}
