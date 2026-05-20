package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.activity.compose.BackHandler
import com.example.viewmodel.LmsLanguage
import com.example.viewmodel.LmsTheme
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.viewmodel.LmsViewModel
import com.example.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FrostedMeshBackground() {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val glowBlue = if (isDark) Color(0x333B82F6) else Color(0x2E3B82F6)
    val glowPurple = if (isDark) Color(0x33D946EF) else Color(0x29C084FC)
    val glowMint = if (isDark) Color(0x2410B981) else Color(0x1F34D399)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // Glowing flow circles simulating the glassmorphism mesh gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(glowBlue, Color.Transparent),
                        center = Offset(0f, 0f),
                        radius = 1600f
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(glowPurple, Color.Transparent),
                        center = Offset(1200f, 2000f),
                        radius = 2000f
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(glowMint, Color.Transparent),
                        center = Offset(100f, 1200f),
                        radius = 1200f
                    )
                )
        )
    }
}

@Composable
fun Modifier.glassCard(
    cornerRadius: androidx.compose.ui.unit.Dp = 24.dp,
    borderAlpha: Float = 0.5f
): Modifier {
    val isDark = isSystemInDarkTheme()
    // Gentle white border for light mode, slightly brighter border for dark mode
    val borderColor = if (isDark) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = borderAlpha)
    return this
        .clip(RoundedCornerShape(cornerRadius))
        .border(1.dp, borderColor, RoundedCornerShape(cornerRadius))
}

@Composable
fun QrCodeView(value: String, modifier: Modifier = Modifier) {
    val hash = value.hashCode()
    val random = remember(value) { java.util.Random(hash.toLong()) }
    val size = 15
    val grid = remember(value) {
        Array(size) { r ->
            BooleanArray(size) { c ->
                val isFinder = (r < 4 && c < 4) || (r < 4 && c >= size - 4) || (r >= size - 4 && c < 4)
                if (isFinder) {
                    val ringR = if (r >= size - 4) r - (size - 4) else r
                    val ringC = if (c >= size - 4) c - (size - 4) else c
                    (ringR == 0 || ringR == 3 || ringC == 0 || ringC == 3) || (ringR == 1 || ringR == 2) && (ringC == 1 || ringC == 2)
                } else {
                    random.nextBoolean()
                }
            }
        }
    }

    val isDark = isSystemInDarkTheme()
    val qrColor = if (isDark) Color.White else Color(0xFF0F172A)
    val qrBg = if (isDark) Color(0xFF1E293B) else Color.White

    Box(
        modifier = modifier
            .background(qrBg, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val cellW = this.size.width / size
            val cellH = this.size.height / size
            for (r in 0 until size) {
                for (c in 0 until size) {
                    if (grid[r][c]) {
                        drawRect(
                            color = qrColor,
                            topLeft = Offset(c * cellW, r * cellH),
                            size = androidx.compose.ui.geometry.Size(cellW + 0.3f, cellH + 0.3f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainControlBar(viewModel: LmsViewModel) {
    val language by viewModel.language.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val isOnline by viewModel.onlineSyncIndicator.collectAsState()
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .glassCard(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.55f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Language selector
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(12.dp)).padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (language == LmsLanguage.English) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent)
                        .clickable { viewModel.setLanguage(LmsLanguage.English) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "EN",
                        fontWeight = if (language == LmsLanguage.English) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp,
                        color = if (language == LmsLanguage.English) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (language == LmsLanguage.Sinhala) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent)
                        .clickable { viewModel.setLanguage(LmsLanguage.Sinhala) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "සිංහල",
                        fontWeight = if (language == LmsLanguage.Sinhala) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp,
                        color = if (language == LmsLanguage.Sinhala) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            
            // Live status tag
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isOnline) Color(0x1F10B981) else Color(0x1FE11D48))
                    .clickable { viewModel.setOnlineSync(!isOnline) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isOnline) Color(0xFF10B981) else Color(0xFFE11D48))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isOnline) LmsTranslations.translate("live_sync_status", language) else LmsTranslations.translate("offline_sync_status", language),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isOnline) Color(0xFF10B981) else Color(0xFFE11D48)
                )
            }

            // Theme switch
            IconButton(
                onClick = {
                    val nextTheme = if (themeMode == LmsTheme.Light) LmsTheme.Dark else LmsTheme.Light
                    viewModel.setTheme(nextTheme)
                },
                modifier = Modifier
                    .size(32.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), CircleShape)
            ) {
                Icon(
                    imageVector = if (themeMode == LmsTheme.Light) Icons.Default.DarkMode else Icons.Default.LightMode,
                    contentDescription = "Toggle Theme",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LmsMainContent(viewModel: LmsViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val backStack by viewModel.backStack.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Intercept standard android system back gestures and back-presses
    BackHandler(enabled = backStack.size > 1 && currentScreen != Screen.Login) {
        viewModel.navigateBack()
    }

    LaunchedEffect(statusMessage) {
        statusMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            FrostedMeshBackground()
            Column(modifier = Modifier.fillMaxSize()) {
                // Floating Dynamic Settings Islands on top of the app workspace
                MainControlBar(viewModel = viewModel)
                
                Box(modifier = Modifier.weight(1f)) {
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = "ScreenTransition"
                    ) { targetScreen ->
                        when (targetScreen) {
                            is Screen.Login -> LoginScreen(viewModel)
                    
                    // Admin Screens
                    is Screen.AdminDashboard -> AdminDashboardScreen(viewModel)
                    is Screen.AdminManageStudents -> AdminManageStudentsScreen(viewModel)
                    is Screen.AdminAddStudent -> AdminAddStudentScreen(viewModel)
                    is Screen.StudentDetails -> StudentDetailsScreen(viewModel, targetScreen.studentId)
                    is Screen.AdminAttendance -> AdminAttendanceScreen(viewModel)
                    is Screen.AdminAddAttendance -> AdminAddAttendanceScreen(viewModel)
                    is Screen.AdminFees -> AdminFeesScreen(viewModel)
                    is Screen.AdminAddFee -> AdminAddFeeScreen(viewModel)
                    is Screen.AdminTutes -> AdminTutesScreen(viewModel)
                    is Screen.AdminAddTute -> AdminAddTuteScreen(viewModel)
                    is Screen.AdminResults -> AdminResultsScreen(viewModel)
                    is Screen.AdminAddResult -> AdminAddResultScreen(viewModel)
                    is Screen.AdminAddAnnouncement -> AdminAddAnnouncementScreen(viewModel)
                    
                    // Student Screens
                    is Screen.StudentDashboard -> StudentDashboardScreen(viewModel, targetScreen.studentId)
                    is Screen.StudentAttendance -> StudentAttendanceScreen(viewModel, targetScreen.studentId)
                    is Screen.StudentFees -> StudentFeesScreen(viewModel, targetScreen.studentId)
                    is Screen.StudentTutes -> StudentTutesScreen(viewModel, targetScreen.studentId)
                    is Screen.StudentTuteReader -> StudentTuteReaderScreen(viewModel, targetScreen.studentId, targetScreen.tuteId)
                    is Screen.StudentResults -> StudentResultsScreen(viewModel, targetScreen.studentId)
                    is Screen.StudentAiCompanion -> StudentAiCompanionScreen(viewModel, targetScreen.studentId)
                }
            }
        }
    }
}
}
}

// --- REUSABLE COMPONENTS ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LmsTopAppBar(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit)? = null
) {
    val isDark = isSystemInDarkTheme()
    val appBarBg = if (isDark) Color(0xD90F172A) else Color(0xD9FFFFFF)
    val strokeColor = if (isDark) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.6f)

    Surface(
        color = appBarBg,
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = strokeColor, shape = RoundedCornerShape(0.dp)),
        shadowElevation = 1.dp
    ) {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            navigationIcon = {
                if (onBack != null) {
                    IconButton(onClick = onBack, modifier = Modifier.minimumInteractiveComponentSize()) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            },
            actions = {
                actions?.invoke(this)
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

// --- 1. LOGIN SCREEN ---
@Composable
fun QrScanningSimulatorDialog(
    viewModel: LmsViewModel,
    onDismiss: () -> Unit,
    onScanned: (String) -> Unit
) {
    val language by viewModel.language.collectAsState()
    val students by viewModel.students.collectAsState()
    var selectedStudent by remember { mutableStateOf<Student?>(null) }
    var isScanning by remember { mutableStateOf(false) }

    LaunchedEffect(selectedStudent) {
        val s = selectedStudent
        if (s != null) {
            isScanning = true
            kotlinx.coroutines.delay(1200) // Simulated scan delay
            isScanning = false
            onScanned(s.studentId)
            selectedStudent = null
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(LmsTranslations.translate("close", language), fontWeight = FontWeight.Bold)
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(LmsTranslations.translate("qr_scanner_title", language), fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = LmsTranslations.translate("qr_scanner_desc", language),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Animated scan viewfinder window
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF0F172A))
                        .border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isScanning) {
                        val transition = rememberInfiniteTransition(label = "LaserMotion")
                        val lineY by transition.animateFloat(
                            initialValue = 10f,
                            targetValue = 170f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "LaserY"
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .offset(y = lineY.dp)
                                .background(Color(0xFFFF0055))
                        )
                        Text(
                            text = "SCANNING...",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Camera,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(52.dp)
                        )
                        Text(
                            text = "SIMULATED VIEWPORT ACTIVE",
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 70.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = LmsTranslations.translate("qr_select_student", language),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.Start).padding(bottom = 6.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        .padding(4.dp)
                ) {
                    items(students) { stu ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(enabled = !isScanning) {
                                    selectedStudent = stu
                                }
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(stu.name, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("ID: ${stu.studentId}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun LoginScreen(viewModel: LmsViewModel) {
    val language by viewModel.language.collectAsState()
    var idOrUsername by remember { mutableStateOf("") }
    var passwordOrPin by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showQrScanner by remember { mutableStateOf(false) }
    
    val focusManager = LocalFocusManager.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current

    if (showQrScanner) {
        QrScanningSimulatorDialog(
            viewModel = viewModel,
            onDismiss = { showQrScanner = false },
            onScanned = { scannedId ->
                showQrScanner = false
                viewModel.loginWithQr(scannedId)
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                // Header Logo
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "RN LMS Logo",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = LmsTranslations.translate("app_title", language),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                
                // ICT Subject Badge
                Card(
                    modifier = Modifier.padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Information & Communication Technology | Grades 6 - 11",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = "by Mr. Ravindu Nidushan",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // Input Fields Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .glassCard(24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = LmsTranslations.translate("unified_login_title", language),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = LmsTranslations.translate("unified_login_subtitle", language),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 14.dp)
                        )

                        // Username or Student ID (Unified)
                        OutlinedTextField(
                            value = idOrUsername,
                            onValueChange = { idOrUsername = it },
                            label = { Text(LmsTranslations.translate("unified_login_label", language)) },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "User", tint = MaterialTheme.colorScheme.primary) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_username"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Password Field
                        OutlinedTextField(
                            value = passwordOrPin,
                            onValueChange = { passwordOrPin = it },
                            label = { Text(LmsTranslations.translate("pass_label", language)) },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Lock", tint = MaterialTheme.colorScheme.primary) },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle password"
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Log In Button
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.unifiedLogin(idOrUsername, passwordOrPin)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("submit_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(
                                text = LmsTranslations.translate("sign_in_btn", language),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Unified Quick Scan Options
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { showQrScanner = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = LmsTranslations.translate("login_qr_btn", language),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Web Info details of rnlms.edu.lk requested by user
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Text(
                            text = "OFFICIAL INSTITUTION LINKS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Link 1
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        val url = "http://rnlms.edu.lk"
                                        val annStr = androidx.compose.ui.text.buildAnnotatedString { append(url) }
                                        clipboardManager.setText(annStr)
                                        viewModel.showMessage("Copied Main Site: $url to clipboard!")
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text("rnlms.edu.lk", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        Text("Official Web", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                            
                            // Link 2
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        val url = "http://lms.rnlms.edu.lk"
                                        val annStr = androidx.compose.ui.text.buildAnnotatedString { append(url) }
                                        clipboardManager.setText(annStr)
                                        viewModel.showMessage("Copied LMS Portal: $url to clipboard!")
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text("lms.rnlms.edu.lk", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                                        Text("LMS Hub Link", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Demo Credentials helper
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().glassCard(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Hint",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = LmsTranslations.translate("demo_helper", language),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Student Demo Item
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        idOrUsername = "STU001"
                                        passwordOrPin = "12345"
                                    },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(LmsTranslations.translate("student_mode", language), fontWeight = FontWeight.Bold, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                                    Text("ID: STU001", fontSize = 9.sp)
                                    Text("Pass: 12345", fontSize = 9.sp)
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(8.dp))

                            // Admin Demo Item
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        idOrUsername = "admin"
                                        passwordOrPin = "admin123"
                                    },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(LmsTranslations.translate("admin_mode", language), fontWeight = FontWeight.Bold, fontSize = 10.sp, color = MaterialTheme.colorScheme.error)
                                    Text("User: admin", fontSize = 9.sp)
                                    Text("Pass: admin123", fontSize = 9.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- 2. ADMIN DASHBOARD SCREEN ---
@Composable
fun AdminDashboardScreen(viewModel: LmsViewModel) {
    val studentsList by viewModel.students.collectAsState()
    val attendanceList by viewModel.attendanceRecords.collectAsState()
    val feesList by viewModel.feeRecords.collectAsState()
    val tutesList by viewModel.tutes.collectAsState()
    val announcementsList by viewModel.announcements.collectAsState()
    val isSyncingSheets by viewModel.isSyncingSheets.collectAsState()
    val syncStatusStage by viewModel.syncStatusStage.collectAsState()
    val lastSyncTime by viewModel.lastSyncTime.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        LmsTopAppBar(
            title = "RN LMS Admin Console",
            actions = {
                IconButton(
                    onClick = { viewModel.clearToLogin() },
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Log Out",
                        tint = Color.White
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AdminStatCard(
                        title = "Students",
                        value = "${studentsList.size}",
                        icon = Icons.Default.People,
                        color = DeepTeal,
                        modifier = Modifier.weight(1f)
                    )
                    
                    val collectedAmount = feesList.filter { it.status == "Paid" }.sumOf { it.amount }
                    AdminStatCard(
                        title = "Fees",
                        value = "Rs. ${collectedAmount.toInt()}",
                        icon = Icons.Default.Payments,
                        color = RichGold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val totalDays = attendanceList.size
                    val presentDays = attendanceList.count { it.status == "Present" }
                    val attendanceRate = if (totalDays > 0) (presentDays * 100) / totalDays else 100
                    
                    AdminStatCard(
                        title = "Attendance",
                        value = "$attendanceRate%",
                        icon = Icons.Default.EventNote,
                        color = MediumAqua,
                        modifier = Modifier.weight(1f)
                    )
                    
                    AdminStatCard(
                        title = "Tutes Issued",
                        value = "${tutesList.size}",
                        icon = Icons.Default.ImportContacts,
                        color = SoftCyan,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Notice Banner Section
            item {
                Text(
                    text = "Quick Admin Actions",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Quick Actions Actions Grid
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().glassCard(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            AdminActionIcon(
                                label = "Students",
                                icon = Icons.Default.School,
                                color = DeepTeal,
                                modifier = Modifier.weight(1f)
                            ) { viewModel.navigateTo(Screen.AdminManageStudents) }
                            
                            AdminActionIcon(
                                label = "Attendance",
                                icon = Icons.Default.DateRange,
                                color = MediumAqua,
                                modifier = Modifier.weight(1f)
                            ) { viewModel.navigateTo(Screen.AdminAttendance) }
                            
                            AdminActionIcon(
                                label = "Fees Ledger",
                                icon = Icons.Default.Receipt,
                                color = RichGold,
                                modifier = Modifier.weight(1f)
                            ) { viewModel.navigateTo(Screen.AdminFees) }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth()) {
                            AdminActionIcon(
                                label = "Issue Tute",
                                icon = Icons.Default.Book,
                                color = SoftCyan,
                                modifier = Modifier.weight(1f)
                            ) { viewModel.navigateTo(Screen.AdminTutes) }
                            
                            AdminActionIcon(
                                label = "Exam Marks",
                                icon = Icons.Default.Assessment,
                                color = EarthRed,
                                modifier = Modifier.weight(1f)
                            ) { viewModel.navigateTo(Screen.AdminResults) }
                            
                            AdminActionIcon(
                                label = "Broadcast Notice",
                                icon = Icons.Default.Campaign,
                                color = RichGold,
                                modifier = Modifier.weight(1f)
                            ) { viewModel.navigateTo(Screen.AdminAddAnnouncement) }
                        }
                    }
                }
            }

            // Google Sheets and Google Drive Cloud Sync Center
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().glassCard(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = "G-Suite Cloud Sync",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Google Drive & Google Sheets Sync Center",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "rnlms.edu.lk Automated Cloud Repository",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        Divider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Drive Folder:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text("My Drive > RN_LMS_Backups", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                                
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.GridOn, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.secondary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Active Sheet:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text("RN_LMS_Student_Ledger.xlsx", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                            
                            Column(modifier = Modifier.weight(1.2f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isSyncingSheets) Icons.Default.Autorenew else Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Status Matrix:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(
                                    text = syncStatusStage,
                                    fontSize = 10.sp,
                                    color = if (isSyncingSheets) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                                
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                Text("Last Synced:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (lastSyncTime > 0) {
                                    val formattedTime = SimpleDateFormat("HH:mm:ss da", Locale.getDefault()).format(Date(lastSyncTime))
                                    Text("Today at $formattedTime", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                                } else {
                                    Text("Never synced today", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(14.dp))
                        
                        Button(
                            onClick = { viewModel.syncToCloudSheet() },
                            enabled = !isSyncingSheets,
                            modifier = Modifier.fillMaxWidth().height(42.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            if (isSyncingSheets) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Synchronizing... Please Wait", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            } else {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sync Ledger to Google Sheet & Drive", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            // Notice board/recent announcements
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notice Board (Announcements)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            if (announcementsList.isEmpty()) {
                item {
                    EmptyStatePlaceholder(text = "No active notices published yet.")
                }
            } else {
                items(announcementsList) { ann ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = ann.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = DeepTeal
                                )
                                Text(
                                    text = ann.date,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = ann.message,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            IconButton(
                                onClick = { viewModel.deleteAnnouncement(ann) },
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete Announcement",
                                    tint = EarthRed.copy(alpha = 0.8f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.glassCard(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AdminActionIcon(
    label: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// --- 3. ADMIN MANAGE STUDENTS SCREEN ---
@Composable
fun AdminManageStudentsScreen(viewModel: LmsViewModel) {
    val studentsList by viewModel.students.collectAsState()
    var searchWord by remember { mutableStateOf("") }

    val filteredList = studentsList.filter {
        it.name.contains(searchWord, ignoreCase = true) || 
        it.studentId.contains(searchWord, ignoreCase = true)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            LmsTopAppBar(
                title = "Manage Students",
                onBack = { viewModel.navigateBack() }
            )

            // Search Bar
            OutlinedTextField(
                value = searchWord,
                onValueChange = { searchWord = it },
                placeholder = { Text("Search by name or Student ID...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DeepTeal,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            if (filteredList.isEmpty()) {
                EmptyStatePlaceholder(text = "No students found matching '$searchWord'")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredList) { student ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.navigateTo(Screen.StudentDetails(student.studentId))
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(DeepTeal.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = student.name.take(2).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = DeepTeal,
                                        fontSize = 16.sp
                                    )
                                }
                                
                                Spacer(modifier = Modifier.width(16.dp))
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = student.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = student.studentId,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Details",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    // Spacer for FAB padding
                    item { Spacer(modifier = Modifier.height(72.dp)) }
                }
            }
        }

        // Add Student FAB
        FloatingActionButton(
            onClick = { viewModel.navigateTo(Screen.AdminAddStudent) },
            containerColor = DeepTeal,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_student_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Student")
        }
    }
}

@Composable
fun StudentIdCardView(student: Student, viewModel: LmsViewModel) {
    val language by viewModel.language.collectAsState()
    var isDownloading by remember { mutableStateOf(false) }

    LaunchedEffect(isDownloading) {
        if (isDownloading) {
            kotlinx.coroutines.delay(1200) // Simulate card PDF construction and download file delay
            isDownloading = false
            viewModel.showMessage(LmsTranslations.translate("download_success", language))
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.03f)
                            )
                        )
                    )
                    .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                // Header of Physical Card with Hologram badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "RN LMS ACADEMY",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = LmsTranslations.translate("student_card_tag", language),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Light,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Cyber chip / Hologram simulator
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFFACC15),
                                        Color(0xFFFB923C),
                                        Color(0xFF38BDF8)
                                    )
                                )
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Card Main Details Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar & Meta Details Left
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1.3f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = student.name.take(2).uppercase(),
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = student.name,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                            Text(
                                text = "${LmsTranslations.translate("id_header", language)}: ${student.studentId}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "CLASS: standard",
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // QR Component on right
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(0.7f)
                    ) {
                        QrCodeView(
                            value = student.studentId,
                            modifier = Modifier
                                .size(72.dp)
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Card Bottom Footer Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = LmsTranslations.translate("valid_year", language),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = LmsTranslations.translate("auth_signature", language),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                        )
                    }

                    // Quick Download trigger button inside the Card UI
                    Button(
                        onClick = { isDownloading = true },
                        enabled = !isDownloading,
                        modifier = Modifier.height(34.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = LmsTranslations.translate("download_card", language),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Downloader Simulated Progress Indicator Mask
            if (isDownloading) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.7f))
                        .clip(RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("PREPARING PDF CARD...", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// --- 4. STUDENT DETAILS SCREEN ---
@Composable
fun StudentDetailsScreen(viewModel: LmsViewModel, studentId: String) {
    val studentsList by viewModel.students.collectAsState()
    val student = studentsList.find { it.studentId == studentId }

    if (student == null) {
        Column(modifier = Modifier.fillMaxSize()) {
            LmsTopAppBar(title = "Student Details", onBack = { viewModel.navigateBack() })
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Student database record not found.")
            }
        }
        return
    }

    val attRecords by viewModel.attendanceRecords.collectAsState()
    val feeRecords by viewModel.feeRecords.collectAsState()
    val examResults by viewModel.examResults.collectAsState()

    val studentAtt = attRecords.filter { it.studentId == studentId }
    val studentFees = feeRecords.filter { it.studentId == studentId }
    val studentResults = examResults.filter { it.studentId == studentId }

    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Student?") },
            text = { Text("Are you sure you want to permanently delete student '${student.name}' (${student.studentId})? This will also wipe all of their attendance, fee, and exam records!") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        viewModel.deleteStudent(student)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EarthRed)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LmsTopAppBar(
            title = student.name,
            onBack = { viewModel.navigateBack() },
            actions = {
                IconButton(onClick = { showDeleteConfirm = true }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Student Profile Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(DeepTeal),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = student.name.take(2).uppercase(),
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(student.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("Student ID: ${student.studentId}", fontSize = 13.sp, color = MediumAqua, fontWeight = FontWeight.Bold)
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 16.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.Phone, contentDescription = "Phone", tint = DeepTeal)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(student.phone, fontSize = 14.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.Email, contentDescription = "Email", tint = DeepTeal)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(student.email, fontSize = 14.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Date", tint = DeepTeal)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Joined Date: ${student.joiningDate}", fontSize = 14.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.LockOpen, contentDescription = "Password", tint = DeepTeal)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Portal Plain Password: ${student.password}", fontSize = 14.sp)
                        }
                    }
                }
            }

            // Digital ID Card representation
            item {
                StudentIdCardView(student = student, viewModel = viewModel)
            }

            // Quick Info Tabs (Attendance total, unpaid bills, average mark)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val days = studentAtt.size
                    val presentPct = if (days > 0) (studentAtt.count { it.status == "Present" } * 100) / days else 100
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Attendance", fontSize = 11.sp)
                            Text("$presentPct%", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DeepTeal)
                        }
                    }

                    val pendingFees = studentFees.filter { it.status == "Pending" }.size
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Pending Fees", fontSize = 11.sp)
                            Text("$pendingFees Month(s)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = RichGold)
                        }
                    }

                    val avgResult = if (studentResults.isNotEmpty()) studentResults.map { it.marks }.average().toInt() else 0
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Average Mark", fontSize = 11.sp)
                            Text("$avgResult / 100", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EarthRed)
                        }
                    }
                }
            }

            // Academic history headers
            item {
                Text("Recent Academic Records", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DeepTeal)
            }

            if (studentResults.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Box(Modifier.padding(16.dp)) {
                            Text("No recorded exams for this student.", fontSize = 13.sp)
                        }
                    }
                }
            } else {
                items(studentResults) { res ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(res.examName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${res.subject} • ${res.date}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (res.marks >= 50) DeepTeal.copy(alpha = 0.12f) else EarthRed.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = res.grade,
                                    fontWeight = FontWeight.Bold,
                                    color = if (res.marks >= 50) DeepTeal else EarthRed,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- 5. ADMIN ADD STUDENT SCREEN ---
@Composable
fun AdminAddStudentScreen(viewModel: LmsViewModel) {
    var studentId by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("12345") } // default pass

    Column(modifier = Modifier.fillMaxSize()) {
        LmsTopAppBar(title = "Register New Student", onBack = { viewModel.navigateBack() })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Apeksha Institute Student Profile Creation",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Ensure IDs are unique with format like STUxxx",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                OutlinedTextField(
                    value = studentId,
                    onValueChange = { studentId = it.uppercase() },
                    label = { Text("Student ID (e.g., STU005)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
            }

            item {
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Mobile/Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
            }

            item {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Student Login Password") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val trimmedId = studentId.trim()
                        val trimmedName = name.trim()
                        if (trimmedId.isEmpty() || trimmedName.isEmpty()) {
                            viewModel.showMessage("Student ID and Full Name are strictly required.")
                        } else {
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val currentFormattedDate = sdf.format(Date())
                            viewModel.addStudent(
                                Student(
                                    studentId = trimmedId,
                                    name = trimmedName,
                                    email = email.trim(),
                                    phone = phone.trim(),
                                    password = password.trim(),
                                    joiningDate = currentFormattedDate
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepTeal)
                ) {
                    Text("SAVE PROFILE", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// --- 6. ADMIN ATTENDANCE LOG SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAttendanceScreen(viewModel: LmsViewModel) {
    val attendanceList by viewModel.attendanceRecords.collectAsState()
    val studentsList by viewModel.students.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        LmsTopAppBar(
            title = "Attendance Logs",
            onBack = { viewModel.navigateBack() }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Button(
                    onClick = { viewModel.navigateTo(Screen.AdminAddAttendance) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepTeal),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Log", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("MARK NEW ATTENDANCE", fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (attendanceList.isEmpty()) {
                item {
                    EmptyStatePlaceholder(text = "No attendance sheets logged yet.")
                }
            } else {
                items(attendanceList) { att ->
                    val stud = studentsList.find { it.studentId == att.studentId }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(stud?.name ?: att.studentId, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${att.studentId} • Date: ${att.date}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (att.remarks.isNotEmpty()) {
                                    Text("Note: ${att.remarks}", fontSize = 11.sp, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                            
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(32.dp))
                                    .background(
                                        when (att.status) {
                                            "Present" -> SoftCyan.copy(alpha = 0.25f)
                                            "Absent" -> EarthRed.copy(alpha = 0.15f)
                                            else -> RichGold.copy(alpha = 0.15f)
                                        }
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = att.status,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (att.status) {
                                        "Present" -> DeepTeal
                                        "Absent" -> EarthRed
                                        else -> RichGold
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAddAttendanceScreen(viewModel: LmsViewModel) {
    val studentsList by viewModel.students.collectAsState()
    
    var selectedStudentIndex by remember { mutableIntStateOf(0) }
    var status by remember { mutableStateOf("Present") }
    var remarks by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    // Auto populate date
    LaunchedEffect(Unit) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        date = sdf.format(Date())
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LmsTopAppBar(title = "Log Attendance", onBack = { viewModel.navigateBack() })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Select Student record", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                if (studentsList.isEmpty()) {
                    Text("No students in system. Please register students first.", color = EarthRed)
                }
            }

            if (studentsList.isNotEmpty()) {
                item {
                    // Quick vertical picker
                    var expandedDropdown by remember { mutableStateOf(false) }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { expandedDropdown = !expandedDropdown }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val activeSt = studentsList[selectedStudentIndex]
                        Text("${activeSt.name} (${activeSt.studentId})", fontWeight = FontWeight.Bold)
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                    }

                    DropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    ) {
                        studentsList.forEachIndexed { i, student ->
                            DropdownMenuItem(
                                text = { Text("${student.name} (${student.studentId})") },
                                onClick = {
                                    selectedStudentIndex = i
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Log Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                Text("Log Session Status", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val states = listOf("Present", "Absent", "Late")
                    states.forEach { st ->
                        val isSel = status == st
                        Button(
                            onClick = { status = st },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSel) DeepTeal else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(st, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Remarks (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (studentsList.isEmpty()) {
                            viewModel.showMessage("No student chosen.")
                        } else {
                            viewModel.addAttendance(
                                Attendance(
                                    studentId = studentsList[selectedStudentIndex].studentId,
                                    date = date.trim(),
                                    status = status,
                                    remarks = remarks.trim()
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepTeal)
                ) {
                    Text("SAVE ATTENDANCE", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// --- 7. FEES RECORD SCREEN ---
@Composable
fun AdminFeesScreen(viewModel: LmsViewModel) {
    val feesList by viewModel.feeRecords.collectAsState()
    val studentsList by viewModel.students.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        LmsTopAppBar(title = "Fees Ledger", onBack = { viewModel.navigateBack() })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Button(
                    onClick = { viewModel.navigateTo(Screen.AdminAddFee) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepTeal),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Fee", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("RECORD NEW PAYMENT", fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (feesList.isEmpty()) {
                item {
                    EmptyStatePlaceholder(text = "No fee ledger entries compiled yet.")
                }
            } else {
                items(feesList) { fee ->
                    val stud = studentsList.find { it.studentId == fee.studentId }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(stud?.name ?: fee.studentId, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Student ID: ${fee.studentId} • Month: ${fee.month}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Amount: Rs. ${fee.amount.toInt()} /=", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DeepTeal)
                                if (fee.paymentDate.isNotEmpty()) {
                                    Text("Paid on: ${fee.paymentDate}", fontSize = 11.sp, color = MediumAqua)
                                }
                            }
                            
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        when (fee.status) {
                                            "Paid" -> SoftCyan.copy(alpha = 0.25f)
                                            "Pending" -> RichGold.copy(alpha = 0.15f)
                                            else -> EarthRed.copy(alpha = 0.15f)
                                        }
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = fee.status,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (fee.status) {
                                        "Paid" -> DeepTeal
                                        "Pending" -> RichGold
                                        else -> EarthRed
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAddFeeScreen(viewModel: LmsViewModel) {
    val studentsList by viewModel.students.collectAsState()
    
    var selectedStudentIndex by remember { mutableIntStateOf(0) }
    var amount by remember { mutableStateOf("3000") }
    var month by remember { mutableStateOf("May 2026") }
    var status by remember { mutableStateOf("Paid") }
    var remarks by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        LmsTopAppBar(title = "Record Fee Payment", onBack = { viewModel.navigateBack() })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Select Student record", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                if (studentsList.isEmpty()) {
                    Text("No students in system.", color = EarthRed)
                }
            }

            if (studentsList.isNotEmpty()) {
                item {
                    var expandedDropdown by remember { mutableStateOf(false) }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { expandedDropdown = !expandedDropdown }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val activeSt = studentsList[selectedStudentIndex]
                        Text("${activeSt.name} (${activeSt.studentId})", fontWeight = FontWeight.Bold)
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                    }

                    DropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    ) {
                        studentsList.forEachIndexed { i, student ->
                            DropdownMenuItem(
                                text = { Text("${student.name} (${student.studentId})") },
                                onClick = {
                                    selectedStudentIndex = i
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = month,
                    onValueChange = { month = it },
                    label = { Text("Billing Month/Year (e.g., May 2026)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Payment Fee Amount (LKR)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            item {
                Text("Ledger Status", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val statusOpts = listOf("Paid", "Pending")
                    statusOpts.forEach { st ->
                        val isSel = status == st
                        Button(
                            onClick = { status = st },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSel) DeepTeal else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(st, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Note / Receipt Number") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val amountVal = amount.trim().toDoubleOrNull()
                        val monthVal = month.trim()
                        if (studentsList.isEmpty()) {
                            viewModel.showMessage("No student chosen.")
                        } else if (amountVal == null || monthVal.isEmpty()) {
                            viewModel.showMessage("Please specify valid Amount and Month.")
                        } else {
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val currentFormattedDate = if (status == "Paid") sdf.format(Date()) else ""
                            viewModel.addFeeRecord(
                                FeeRecord(
                                    studentId = studentsList[selectedStudentIndex].studentId,
                                    month = monthVal,
                                    amount = amountVal,
                                    status = status,
                                    paymentDate = currentFormattedDate,
                                    remarks = remarks.trim()
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepTeal)
                ) {
                    Text("SAVE RECORD", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// --- 8. ADMIN TUTUES SCREEN ---
@Composable
fun AdminTutesScreen(viewModel: LmsViewModel) {
    val tutesList by viewModel.tutes.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        LmsTopAppBar(title = "Study Materials & Tutes", onBack = { viewModel.navigateBack() })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Button(
                    onClick = { viewModel.navigateTo(Screen.AdminAddTute) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepTeal),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tute", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PUBLISH NEW TUTE / STUDY NOTE", fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (tutesList.isEmpty()) {
                item {
                    EmptyStatePlaceholder(text = "No study books/tutes published yet.")
                }
            } else {
                items(tutesList) { tute ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SoftCyan.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(tute.topic, color = DeepTeal, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                                Text(tute.releaseDate, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(tute.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DeepTeal)
                            Text(tute.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Content length: ${tute.content.length} characters", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                IconButton(
                                    onClick = { viewModel.deleteTute(tute) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = EarthRed)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAddTuteScreen(viewModel: LmsViewModel) {
    var title by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("Physics") }
    var desc by remember { mutableStateOf("") }
    var contentText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        LmsTopAppBar(title = "Publish Study Note", onBack = { viewModel.navigateBack() })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tute Title (e.g. Unit 4: Modern Semiconductors)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                Text("Subject/Topic Track", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                val topics = listOf("Physics", "Chemistry", "Mathematics", "Other")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    topics.forEach { tp ->
                        val isSel = topic == tp
                        Button(
                            onClick = { topic = tp },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSel) DeepTeal else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Text(tp, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Brief Catchy Description") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = contentText,
                    onValueChange = { contentText = it },
                    label = { Text("Write/Paste Tute Study Content (Text/Markdown)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 15
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val trimmedTitle = title.trim()
                        val trimmedTxt = contentText.trim()
                        if (trimmedTitle.isEmpty() || trimmedTxt.isEmpty()) {
                            viewModel.showMessage("Please fill the study note Title and Content.")
                        } else {
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val releaseD = sdf.format(Date())
                            viewModel.addTute(
                                Tute(
                                    title = trimmedTitle,
                                    topic = topic,
                                    description = desc.trim(),
                                    content = trimmedTxt,
                                    releaseDate = releaseD
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepTeal)
                ) {
                    Text("PUBLISH TO STUDENT PORTAL", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// --- 9. ADMIN EXAM RESULTS SCREEN ---
@Composable
fun AdminResultsScreen(viewModel: LmsViewModel) {
    val resultsList by viewModel.examResults.collectAsState()
    val studentsList by viewModel.students.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        LmsTopAppBar(title = "Student Mock Exam Results", onBack = { viewModel.navigateBack() })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Button(
                    onClick = { viewModel.navigateTo(Screen.AdminAddResult) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepTeal),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Result", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("LOG STUDENTS EXAM RESULTS", fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (resultsList.isEmpty()) {
                item {
                    EmptyStatePlaceholder(text = "No results filed in system yet.")
                }
            } else {
                items(resultsList) { res ->
                    val stud = studentsList.find { it.studentId == res.studentId }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(stud?.name ?: res.studentId, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("ID: ${res.studentId} • Subject: ${res.subject}", fontSize = 11.sp)
                                Text("Exam: ${res.examName}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Marks: ${res.marks.toInt()} / ${res.maxMarks.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DeepTeal)
                            }
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(DeepTeal.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(res.grade, fontWeight = FontWeight.Bold, color = DeepTeal)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                IconButton(
                                    onClick = { viewModel.deleteResult(res) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete", tint = EarthRed)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAddResultScreen(viewModel: LmsViewModel) {
    val studentsList by viewModel.students.collectAsState()
    
    var selectedStudentIndex by remember { mutableIntStateOf(0) }
    var examName by remember { mutableStateOf("Mock Exam May") }
    var subject by remember { mutableStateOf("Physics") }
    var marks by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        date = sdf.format(Date())
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LmsTopAppBar(title = "Log Exam Result", onBack = { viewModel.navigateBack() })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Select Student record", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                if (studentsList.isEmpty()) {
                    Text("No students detected.", color = EarthRed)
                }
            }

            if (studentsList.isNotEmpty()) {
                item {
                    var expandedDropdown by remember { mutableStateOf(false) }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { expandedDropdown = !expandedDropdown }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val activeSt = studentsList[selectedStudentIndex]
                        Text("${activeSt.name} (${activeSt.studentId})", fontWeight = FontWeight.Bold)
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                    }

                    DropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    ) {
                        studentsList.forEachIndexed { i, student ->
                            DropdownMenuItem(
                                text = { Text("${student.name} (${student.studentId})") },
                                onClick = {
                                    selectedStudentIndex = i
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = examName,
                    onValueChange = { examName = it },
                    label = { Text("Exam Name / Assessment Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                Text("Subject Specialty", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                val statusOpts = listOf("Physics", "Chemistry", "Mathematics")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    statusOpts.forEach { st ->
                        val isSel = subject == st
                        Button(
                            onClick = { subject = st },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSel) DeepTeal else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(st, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = marks,
                    onValueChange = { marks = it },
                    label = { Text("Acquired Marks (0 - 100)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            item {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Assessment Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val marksVal = marks.trim().toDoubleOrNull()
                        val examVal = examName.trim()
                        if (studentsList.isEmpty()) {
                            viewModel.showMessage("No student chosen.")
                        } else if (marksVal == null || marksVal < 0 || marksVal > 100 || examVal.isEmpty()) {
                            viewModel.showMessage("Please input valid Assessment Title and Marks (0-100).")
                        } else {
                            // Grade logic
                            val grade = when {
                                marksVal >= 75.0 -> "A"
                                marksVal >= 65.0 -> "B"
                                marksVal >= 55.0 -> "C"
                                marksVal >= 40.0 -> "S"
                                else -> "W"
                            }
                            
                            viewModel.addExamResult(
                                ExamResult(
                                    studentId = studentsList[selectedStudentIndex].studentId,
                                    examName = examVal,
                                    subject = subject,
                                    marks = marksVal,
                                    maxMarks = 100.0,
                                    grade = grade,
                                    date = date.trim()
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepTeal)
                ) {
                    Text("SAVE MARKSHEET", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// --- BROADCAST NOTICE SCREEN ---
@Composable
fun AdminAddAnnouncementScreen(viewModel: LmsViewModel) {
    var title by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        LmsTopAppBar(title = "Publish Notification Notice", onBack = { viewModel.navigateBack() })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Broadcast live global bulletin for all students to view on login.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Bulletin/Notice Title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = msg,
                    onValueChange = { msg = it },
                    label = { Text("Notification Message details") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val tVal = title.trim()
                        val mVal = msg.trim()
                        if (tVal.isEmpty() || mVal.isEmpty()) {
                            viewModel.showMessage("Title and message detail must be filled.")
                        } else {
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val currentFormattedDate = sdf.format(Date())
                            viewModel.addAnnouncement(
                                Announcement(
                                    title = tVal,
                                    message = mVal,
                                    date = currentFormattedDate
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepTeal)
                ) {
                    Text("PUBLISH NOTICE", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// --- 10. STUDENT DASHBOARD SCREEN ---
@Composable
fun StudentDashboardScreen(viewModel: LmsViewModel, studentId: String) {
    val studentFlow = viewModel.loggedStudent.collectAsState()
    val student = studentFlow.value

    if (student == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val announcementsList by viewModel.announcements.collectAsState()
    val attRecordsFlow = viewModel.getStudentAttendanceFlow(studentId).collectAsState(emptyList())
    val feesFlow = viewModel.getStudentFeesFlow(studentId).collectAsState(emptyList())
    val resultsFlow = viewModel.getStudentResultsFlow(studentId).collectAsState(emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        LmsTopAppBar(
            title = "RN LMS Portal",
            actions = {
                IconButton(
                    onClick = { viewModel.clearToLogin() },
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Log out",
                        tint = Color.White
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Student Profile Welcome Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().glassCard(24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepTeal)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(RichGold),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = student.name.take(2).uppercase(),
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Welcome, ${student.name}!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "Student ID: ${student.studentId}",
                                    fontSize = 13.sp,
                                    color = SoftCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Downloadable student card with QR code for academic checkpoints & class login
            item {
                StudentIdCardView(student = student, viewModel = viewModel)
            }

            // High priority stats visual metrics (attendance, fees pending, average exam grade!)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val days = attRecordsFlow.value.size
                    val presentPct = if (days > 0) (attRecordsFlow.value.count { it.status == "Present" } * 100) / days else 100
                    Card(
                        modifier = Modifier.weight(1.5f).glassCard(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("ATTENDANCE", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(54.dp)) {
                                CircularProgressIndicator(
                                    progress = { presentPct.toFloat() / 100f },
                                    color = DeepTeal,
                                    strokeWidth = 5.dp
                                )
                                Text("$presentPct%", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1.5f).glassCard(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("FEES OVERVIEW", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(14.dp))
                            val unpaid = feesFlow.value.count { it.status == "Pending" }
                            if (unpaid > 0) {
                                Icon(Icons.Default.Warning, contentDescription = "Overdue", tint = RichGold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("$unpaid Overdue", fontSize = 11.sp, color = EarthRed, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Cleared", tint = MediumAqua)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Fees Cleared", fontSize = 11.sp, color = DeepTeal, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Quick actions menus grid
            item {
                Text(text = "Academia Dashboard", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().glassCard(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            AdminActionIcon(
                                label = "My Attendance",
                                icon = Icons.Default.Event,
                                color = DeepTeal,
                                modifier = Modifier.weight(1f)
                            ) { viewModel.navigateTo(Screen.StudentAttendance(studentId)) }
                            
                            AdminActionIcon(
                                label = "Fees History",
                                icon = Icons.Default.Receipt,
                                color = RichGold,
                                modifier = Modifier.weight(1f)
                            ) { viewModel.navigateTo(Screen.StudentFees(studentId)) }
                            
                            AdminActionIcon(
                                label = "Tute Library",
                                icon = Icons.Default.Description,
                                color = SoftCyan,
                                modifier = Modifier.weight(1f)
                            ) { viewModel.navigateTo(Screen.StudentTutes(studentId)) }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth()) {
                            AdminActionIcon(
                                label = "My Exam Marks",
                                icon = Icons.Default.Grade,
                                color = EarthRed,
                                modifier = Modifier.weight(1f)
                            ) { viewModel.navigateTo(Screen.StudentResults(studentId)) }
                            
                            AdminActionIcon(
                                label = "AI Study Guide",
                                icon = Icons.Default.SmartToy,
                                color = DeepTeal,
                                modifier = Modifier.weight(1f)
                            ) { viewModel.navigateTo(Screen.StudentAiCompanion(studentId)) }
                            
                            // Placeholder
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // Recent bulletins notice boards
            item {
                Text(text = "Latest Notices / Announcements", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            if (announcementsList.isEmpty()) {
                item {
                    EmptyStatePlaceholder(text = "Notice board is empty.")
                }
            } else {
                items(announcementsList) { ann ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(ann.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DeepTeal)
                                Text(ann.date, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(ann.message, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// --- 11. STUDENT ATTENDANCE VIEW SCREEN ---
@Composable
fun StudentAttendanceScreen(viewModel: LmsViewModel, studentId: String) {
    val attList by viewModel.getStudentAttendanceFlow(studentId).collectAsState(emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        LmsTopAppBar(title = "My Attendance Report", onBack = { viewModel.navigateBack() })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                // Overview stats tile
                val days = attList.size
                val presents = attList.count { it.status == "Present" }
                val lateInfo = attList.count { it.status == "Late" }
                val absents = attList.count { it.status == "Absent" }
                val rate = if (days > 0) (presents * 100) / days else 100
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepTeal)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Overall Attendance Percentage", color = Color.White, fontSize = 13.sp)
                        Text("$rate%", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Present", color = SoftCyan, fontSize = 11.sp)
                                Text("$presents", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Absent", color = SoftPeach, fontSize = 11.sp)
                                Text("$absents", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Late", color = SoftPeach, fontSize = 11.sp)
                                Text("$lateInfo", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (attList.isEmpty()) {
                item {
                    EmptyStatePlaceholder(text = "No recorded attendance days yet.")
                }
            } else {
                items(attList) { log ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(log.date, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                if (log.remarks.isNotEmpty()) {
                                    Text(log.remarks, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(32.dp))
                                    .background(
                                        when (log.status) {
                                            "Present" -> SoftCyan.copy(alpha = 0.25f)
                                            "Absent" -> EarthRed.copy(alpha = 0.15f)
                                            else -> RichGold.copy(alpha = 0.15f)
                                        }
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = log.status,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (log.status) {
                                        "Present" -> DeepTeal
                                        "Absent" -> EarthRed
                                        else -> RichGold
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- 12. STUDENT FEES VIEW SCREEN ---
@Composable
fun StudentFeesScreen(viewModel: LmsViewModel, studentId: String) {
    val feesList by viewModel.getStudentFeesFlow(studentId).collectAsState(emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        LmsTopAppBar(title = "My Fee Invoices", onBack = { viewModel.navigateBack() })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                val outstanding = feesList.count { it.status == "Pending" }
                val paid = feesList.filter { it.status == "Paid" }.sumOf { it.amount }
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Ledger Summary", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DeepTeal)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Paid amount:")
                            Text("Rs. ${paid.toInt()} /=", fontWeight = FontWeight.Bold, color = DeepTeal)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Outstanding Pending bills:")
                            Text("$outstanding Month(s)", fontWeight = FontWeight.Bold, color = if (outstanding > 0) EarthRed else DeepTeal)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (feesList.isEmpty()) {
                item {
                    EmptyStatePlaceholder(text = "No fee ledger transactions generated.")
                }
            } else {
                items(feesList) { fee ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(fee.month, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Amount due: Rs. ${fee.amount.toInt()} /=", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (fee.paymentDate.isNotEmpty()) {
                                    Text("Paid on: ${fee.paymentDate}", fontSize = 11.sp, color = MediumAqua)
                                }
                                if (fee.remarks.isNotEmpty()) {
                                    Text("Ref: ${fee.remarks}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (fee.status == "Paid") SoftCyan.copy(alpha = 0.3f) else EarthRed.copy(alpha = 0.15f)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = fee.status,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (fee.status == "Paid") DeepTeal else EarthRed
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- 13. STUDENT TUTES READERS SCREEN ---
@Composable
fun StudentTutesScreen(viewModel: LmsViewModel, studentId: String) {
    val tutesList by viewModel.tutes.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        LmsTopAppBar(title = "LMS Library & Tutes", onBack = { viewModel.navigateBack() })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Gain reading materials, exam handouts and tute issues shared by admins.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (tutesList.isEmpty()) {
                item {
                    EmptyStatePlaceholder(text = "No study handouts published here yet.")
                }
            } else {
                items(tutesList) { tute ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.navigateTo(Screen.StudentTuteReader(studentId, tute.id))
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DeepTeal.copy(alpha = 0.12f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = tute.topic,
                                        color = DeepTeal,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                                Text(tute.releaseDate, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(tute.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DeepTeal)
                            Text(tute.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MenuBook, contentDescription = "Read", tint = RichGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Click to start reading", fontWeight = FontWeight.Bold, color = RichGold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentTuteReaderScreen(viewModel: LmsViewModel, studentId: String, tuteId: Int) {
    val tutesList by viewModel.tutes.collectAsState()
    val tute = tutesList.find { it.id == tuteId }

    if (tute == null) {
        Column(modifier = Modifier.fillMaxSize()) {
            LmsTopAppBar(title = "Study Hub Handouts", onBack = { viewModel.navigateBack() })
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Study handout was deleted.")
            }
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LmsTopAppBar(
            title = tute.topic,
            onBack = { viewModel.navigateBack() }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(tute.title, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = DeepTeal)
                Text("Released: ${tute.releaseDate}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Text(tute.description, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MediumAqua)
                Divider(modifier = Modifier.padding(vertical = 12.dp))
            }

            // Book study body paper content text view
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Box(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = tute.content,
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Quick AI Tutor interaction suggestion
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = RichGold.copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .clickable {
                                viewModel.navigateTo(Screen.StudentAiCompanion(studentId))
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.SmartToy, contentDescription = "Robot", tint = DeepTeal, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Confused about this topic?", fontWeight = FontWeight.Bold, color = DeepTeal)
                            Text("Clear doubts immediately using the Gemini AI Tutor Companion!", fontSize = 12.sp)
                        }
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Go", tint = DeepTeal)
                    }
                }
            }
        }
    }
}

// --- 14. STUDENT RESULTS VIEW SCREEN ---
@Composable
fun StudentResultsScreen(viewModel: LmsViewModel, studentId: String) {
    val resultsList by viewModel.getStudentResultsFlow(studentId).collectAsState(emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        LmsTopAppBar(title = "My Academic Results", onBack = { viewModel.navigateBack() })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                val mockExams = resultsList.size
                val maxResult = if (resultsList.isNotEmpty()) resultsList.maxOf { it.marks }.toInt() else 0
                val average = if (resultsList.isNotEmpty()) resultsList.map { it.marks }.average().toInt() else 0
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DeepTeal),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Class Academic Performance Index", color = Color.White, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Assessed", color = SoftCyan, fontSize = 11.sp)
                                Text("$mockExams Times", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Best Score", color = SoftCyan, fontSize = 11.sp)
                                Text("$maxResult / 100", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Class Average", color = SoftCyan, fontSize = 11.sp)
                                Text("$average / 100", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (resultsList.isEmpty()) {
                item {
                    EmptyStatePlaceholder(text = "No graded assessments published in portal yet.")
                }
            } else {
                items(resultsList) { res ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SoftCyan.copy(alpha = 0.25f))
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(res.subject, color = DeepTeal, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(res.examName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Logged sheet date: ${res.date}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Scored: ${res.marks.toInt()} marks", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DeepTeal)
                            }
                            
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(DeepTeal.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = res.grade,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DeepTeal,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- 15. STUDENT AI COMPANION SCREEN ---
@Composable
fun StudentAiCompanionScreen(viewModel: LmsViewModel, studentId: String) {
    val messages by viewModel.aiMessages.collectAsState()
    val isFetching by viewModel.isAiLoading.collectAsState()
    
    var questionInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxSize()) {
        LmsTopAppBar(
            title = "Apeksha AI Tutor",
            onBack = {
                viewModel.clearAiCompanionChat()
                viewModel.navigateBack()
            }
        )

        // Intro tip row
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = RichGold.copy(alpha = 0.08f))
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Spark", tint = RichGold, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Ask our AI Tutor helper to clarify mirror formulas, chemistry equations, etc.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Messages list
        Box(modifier = Modifier.weight(1f)) {
            if (messages.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "Robot",
                        tint = DeepTeal.copy(alpha = 0.4f),
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your AI Study Companion is ready!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Send a question to begin chatting.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    reverseLayout = false
                ) {
                    items(messages) { msg ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Card(
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (msg.isUser) 16.dp else 4.dp,
                                    bottomEnd = if (msg.isUser) 4.dp else 16.dp
                                ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (msg.isUser) DeepTeal else MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                modifier = Modifier.widthIn(max = 280.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = msg.text,
                                        color = if (msg.isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                    if (isFetching) {
                        item {
                            Row(modifier = Modifier.fillMaxWidth().padding(4.dp), horizontalArrangement = Arrangement.Start) {
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("AI is explaining...", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Question input line bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = questionInput,
                onValueChange = { questionInput = it },
                placeholder = { Text("What is my Mirror Formula?") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input"),
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DeepTeal,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    val prompt = questionInput.trim()
                    if (prompt.isNotEmpty()) {
                        focusManager.clearFocus()
                        questionInput = ""
                        viewModel.sendAiQuestion(studentId, prompt)
                    }
                },
                enabled = !isFetching && questionInput.isNotBlank(),
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (questionInput.isNotBlank()) DeepTeal else MaterialTheme.colorScheme.surfaceVariant)
                    .minimumInteractiveComponentSize()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (questionInput.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// --- STANDARD EMPTY UX WRAPPERS ---
@Composable
fun EmptyStatePlaceholder(text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Icon(Icons.Default.Info, contentDescription = "Empty", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
