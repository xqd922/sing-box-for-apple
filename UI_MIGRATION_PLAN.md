# sing-box Android UI 完全复制方案

## 🎯 目标

**完全复制** iOS/macOS 版本的界面，保持：
- ✅ 相同的视觉设计
- ✅ 相同的交互逻辑
- ✅ 相同的布局结构
- ✅ 相同的动画效果

---

## 📱 iOS 界面结构分析

### 1. 主界面架构 (MainView.swift)

**iOS 结构**:
```
TabView
├── Dashboard (主页)
├── Logs (日志)
├── Tools (工具)
└── Settings (设置)
```

**Android 复制**:
```kotlin
// 使用 Bottom Navigation + Navigation Compose
Scaffold(
    bottomBar = { BottomNavigationBar() }
) {
    NavHost(navController, startDestination = "dashboard") {
        composable("dashboard") { DashboardScreen() }
        composable("logs") { LogScreen() }
        composable("tools") { ToolsScreen() }
        composable("settings") { SettingsScreen() }
    }
}
```

### 2. 底部状态栏 (Bottom Accessory)

**iOS 特点**:
- 状态栏药丸形状 (Status Bar Pill)
- 显示连接状态、运行时间
- FAB 开始按钮（断开状态时显示）
- 圆角: 22px
- 毛玻璃效果 (iOS 26+) 或 `.bar` 背景

**Android 复制**:
```kotlin
@Composable
fun BottomAccessoryBar(
    vpnState: VpnState,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        if (vpnState == VpnState.DISCONNECTED) {
            // FAB 开始按钮
            FloatingActionButton(
                onClick = onStartClick,
                modifier = Modifier.align(Alignment.CenterEnd),
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Icon(Icons.Default.PlayArrow, "Start")
            }
        } else {
            // 状态栏药丸
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 状态文本
                    StatusText(vpnState)
                    Spacer(modifier = Modifier.weight(1f))

                    // 导航按钮组
                    NavigationButtons(
                        groupsCount = groupsCount,
                        connectionsCount = connectionsCount
                    )

                    Divider(modifier = Modifier.height(24.dp))

                    // 开始/停止按钮
                    StartStopButton(
                        vpnState = vpnState,
                        onClick = if (vpnState == VpnState.CONNECTED) onStopClick else onStartClick
                    )
                }
            }
        }
    }
}
```

---

## 🏠 Dashboard 界面

### iOS Dashboard 结构分析

**主要组件**:
1. **卡片系统** (Dashboard Cards)
   - Status Card - 显示 VPN 状态
   - Profile Card - 当前配置文件
   - Connections Card - 连接数
   - Traffic Cards - 上传/下载流量
   - Clash Mode Card - 模式切换
   - HTTP Proxy Card - HTTP 代理状态

2. **卡片管理** (Card Management Sheet)
   - 可拖拽排序
   - 可显示/隐藏卡片

3. **远程控制菜单** (Remote Control Menu)
   - 显示可用的远程服务器

### Android Dashboard 复制

```kotlin
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCardManagement by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    // 右上角菜单
                    IconButton(onClick = { showCardManagement = true }) {
                        Icon(Icons.Default.MoreVert, "Others")
                    }
                }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Status Card
            item(span = { GridItemSpan(2) }) {
                StatusCard(
                    vpnState = uiState.vpnState,
                    uptime = uiState.uptime
                )
            }

            // Profile Card
            item {
                ProfileCard(
                    profileName = uiState.profileName,
                    onClick = { /* 打开配置选择 */ }
                )
            }

            // Connections Card
            item {
                ConnectionsCard(
                    count = uiState.connectionsCount,
                    onClick = { /* 打开连接列表 */ }
                )
            }

            // Upload Traffic Card
            item {
                UploadTrafficCard(
                    speed = uiState.uploadSpeed,
                    total = uiState.uploadTotal
                )
            }

            // Download Traffic Card
            item {
                DownloadTrafficCard(
                    speed = uiState.downloadSpeed,
                    total = uiState.downloadTotal
                )
            }

            // Clash Mode Card (if applicable)
            if (uiState.showClashMode) {
                item {
                    ClashModeCard(
                        currentMode = uiState.clashMode,
                        onModeChange = { viewModel.setClashMode(it) }
                    )
                }
            }

            // HTTP Proxy Card
            item {
                HTTPProxyCard(
                    enabled = uiState.httpProxyEnabled,
                    port = uiState.httpProxyPort
                )
            }
        }
    }

    // Card Management Sheet
    if (showCardManagement) {
        CardManagementSheet(
            visibleCards = uiState.visibleCards,
            onDismiss = { showCardManagement = false },
            onCardsChanged = { viewModel.updateVisibleCards(it) }
        )
    }
}
```

### 卡片组件复制

#### Status Card

```kotlin
@Composable
fun StatusCard(
    vpnState: VpnState,
    uptime: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (vpnState) {
                VpnState.CONNECTED -> MaterialTheme.colorScheme.primaryContainer
                VpnState.CONNECTING -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 状态图标
                Icon(
                    imageVector = when (vpnState) {
                        VpnState.CONNECTED -> Icons.Default.CheckCircle
                        VpnState.CONNECTING -> Icons.Default.Sync
                        else -> Icons.Default.StopCircle
                    },
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = when (vpnState) {
                        VpnState.CONNECTED -> MaterialTheme.colorScheme.primary
                        VpnState.CONNECTING -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.error
                    }
                )

                // 状态文本
                Text(
                    text = when (vpnState) {
                        VpnState.CONNECTED -> "Started"
                        VpnState.CONNECTING -> "Starting"
                        VpnState.DISCONNECTED -> "Stopped"
                        VpnState.DISCONNECTING -> "Stopping"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // 运行时间（仅连接时显示）
            if (vpnState == VpnState.CONNECTED && uptime.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uptime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
```

#### Profile Card

```kotlin
@Composable
fun ProfileCard(
    profileName: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Profile",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = profileName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
```

#### Traffic Cards (Upload/Download)

```kotlin
@Composable
fun TrafficCard(
    type: TrafficType,
    speed: Long,
    total: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (type == TrafficType.UPLOAD) 
                        Icons.Default.ArrowUpward 
                    else 
                        Icons.Default.ArrowDownward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (type == TrafficType.UPLOAD) 
                        MaterialTheme.colorScheme.tertiary 
                    else 
                        MaterialTheme.colorScheme.primary
                )

                Text(
                    text = formatSpeed(speed),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 流量图表
            TrafficLineChart(
                data = if (type == TrafficType.UPLOAD) 
                    uploadTrafficHistory 
                else 
                    downloadTrafficHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Total: ${formatBytes(total)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

enum class TrafficType {
    UPLOAD, DOWNLOAD
}
```

---

## 📋 Logs 界面

### iOS Logs 结构分析

**主要功能**:
1. **日志显示** - 等宽字体，支持 ANSI 颜色
2. **搜索功能** - 实时搜索日志内容
3. **暂停/恢复** - 暂停自动滚动
4. **导出功能** - 复制到剪贴板、保存文件、分享
5. **日志级别过滤** - 按级别筛选日志
6. **远程设备切换** - 切换到远程服务器日志

### Android Logs 复制

```kotlin
@Composable
fun LogScreen(
    viewModel: LogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSearch by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Logs") },
                actions = {
                    // 搜索按钮
                    IconButton(onClick = { showSearch = !showSearch }) {
                        Icon(Icons.Default.Search, "Search")
                    }

                    // 暂停/恢复按钮
                    IconButton(onClick = { viewModel.togglePause() }) {
                        Icon(
                            imageVector = if (uiState.isPaused) 
                                Icons.Default.PlayArrow 
                            else 
                                Icons.Default.Pause,
                            contentDescription = if (uiState.isPaused) "Resume" else "Pause"
                        )
                    }

                    // 菜单按钮
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, "Others")
                    }

                    // 下拉菜单
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        // Log Level 子菜单
                        SubMenu(
                            label = "Log Level",
                            icon = Icons.Default.Tune
                        ) {
                            DropdownMenuItem(
                                text = { Text("Default") },
                                onClick = {
                                    viewModel.setLogLevel(null)
                                    showMenu = false
                                }
                            )
                            LogLevel.values().forEach { level ->
                                DropdownMenuItem(
                                    text = { Text(level.name) },
                                    onClick = {
                                        viewModel.setLogLevel(level)
                                        showMenu = false
                                    }
                                )
                            }
                        }

                        // Save 子菜单
                        SubMenu(
                            label = "Save",
                            icon = Icons.Default.Save
                        ) {
                            DropdownMenuItem(
                                text = { Text("To Clipboard") },
                                onClick = {
                                    viewModel.copyToClipboard()
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("To File") },
                                onClick = {
                                    viewModel.saveToFile()
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Share") },
                                onClick = {
                                    viewModel.shareLogs()
                                    showMenu = false
                                }
                            )
                        }

                        // Clear Logs
                        DropdownMenuItem(
                            text = { Text("Clear Logs") },
                            onClick = {
                                viewModel.clearLogs()
                                showMenu = false
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 搜索栏
            if (showSearch) {
                SearchBar(
                    query = searchText,
                    onQueryChange = { 
                        searchText = it
                        viewModel.setSearchText(it)
                    },
                    onSearch = { /* 搜索 */ },
                    active = false,
                    onActiveChange = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search logs") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = {
                        IconButton(onClick = { 
                            searchText = ""
                            viewModel.setSearchText("")
                        }) {
                            Icon(Icons.Default.Clear, "Clear")
                        }
                    }
                ) { }
            }

            // 日志内容
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                if (uiState.logs.isEmpty()) {
                    // 空状态
                    Text(
                        text = if (uiState.isConnected) "Empty logs" else "Service not started",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
                    )
                } else {
                    // 日志列表
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        items(uiState.visibleLogs) { logEntry ->
                            LogEntryItem(
                                logEntry = logEntry,
                                searchText = searchText
                            )
                        }
                    }

                    // 自动滚动到底部
                    LaunchedEffect(uiState.visibleLogs.size) {
                        if (!uiState.isPaused) {
                            listState.animateScrollToItem(uiState.visibleLogs.size - 1)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LogEntryItem(
    logEntry: LogEntry,
    searchText: String
) {
    val text = remember(logEntry) {
        parseAnsiColors(logEntry.message)
    }

    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp
        ),
        color = Color.White,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}
```

---

## 🔧 Tools 界面

### iOS Tools 结构分析

**主要功能**:
1. **Endpoints** - Tailscale 端点
2. **Services** - USB/IP 服务
3. **Network** - 网络质量测试、STUN 测试
4. **Debug** - 崩溃报告、OOM 报告

### Android Tools 复制

```kotlin
@Composable
fun ToolsScreen(
    viewModel: ToolsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Endpoints Section
        if (uiState.tailscaleEndpoints.isNotEmpty()) {
            item {
                SectionHeader("Endpoints")
            }
            items(uiState.tailscaleEndpoints) { endpoint ->
                NavigationItem(
                    icon = Icons.Default.DeviceHub,
                    title = if (uiState.tailscaleEndpoints.size == 1) 
                        "Tailscale" 
                    else 
                        "Tailscale: ${endpoint.endpointTag}",
                    onClick = { /* 打开端点详情 */ }
                )
            }
        }

        // Services Section
        if (uiState.usbipServers.isNotEmpty()) {
            item {
                SectionHeader("Services")
            }
            items(uiState.usbipServers) { server ->
                NavigationItem(
                    icon = Icons.Default.Usb,
                    title = if (uiState.usbipServers.size == 1) 
                        "USB/IP" 
                    else 
                        "USB/IP: ${server.serverTag}",
                    onClick = { /* 打开 USB/IP 详情 */ }
                )
            }
        }

        // Network Section
        item {
            SectionHeader("Network")
        }
        item {
            NavigationItem(
                icon = Icons.Default.NetworkCheck,
                title = "Network Quality",
                onClick = { /* 打开网络质量测试 */ }
            )
        }
        item {
            NavigationItem(
                icon = Icons.Default.SwapHoriz,
                title = "STUN Test",
                onClick = { /* 打开 STUN 测试 */ }
            )
        }

        // Debug Section (only for local device)
        if (uiState.remoteServer == null) {
            item {
                SectionHeader("Debug")
            }
            item {
                NavigationItem(
                    icon = Icons.Default.BugReport,
                    title = "Crash Report",
                    badge = uiState.crashReportCount,
                    onClick = { /* 打开崩溃报告 */ }
                )
            }
            item {
                NavigationItem(
                    icon = Icons.Default.Memory,
                    title = "OOM Report",
                    badge = uiState.oomReportCount,
                    onClick = { /* 打开 OOM 报告 */ }
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun NavigationItem(
    icon: ImageVector,
    title: String,
    badge: Int = 0,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        if (badge > 0) {
            Badge(
                containerColor = MaterialTheme.colorScheme.error
            ) {
                Text(
                    text = badge.toString(),
                    color = MaterialTheme.colorScheme.onError
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
```

---

## ⚙️ Settings 界面

### iOS Settings 结构分析

**主要功能**:
1. **App** - 应用设置
2. **Core** - 核心设置
3. **Packet Tunnel** - 隧道设置
4. **On Demand Rules** - 按需规则
5. **Profile Override** - 配置覆盖
6. **Remote Control** - 远程控制
7. **About** - 关于信息

### Android Settings 复制

```kotlin
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "settings_main"
    ) {
        composable("settings_main") {
            SettingsMainScreen(
                onNavigate = { route -> navController.navigate(route) }
            )
        }
        composable("app") { AppSettingsScreen() }
        composable("core") { CoreSettingsScreen() }
        composable("packet_tunnel") { PacketTunnelSettingsScreen() }
        composable("on_demand_rules") { OnDemandRulesScreen() }
        composable("profile_override") { ProfileOverrideScreen() }
        composable("remote_control") { RemoteControlScreen() }
    }
}

@Composable
fun SettingsMainScreen(
    onNavigate: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Settings items
        item {
            SettingsSection {
                SettingsNavigationItem(
                    icon = Icons.Default.Apps,
                    title = "App",
                    onClick = { onNavigate("app") }
                )
                SettingsNavigationItem(
                    icon = Icons.Default.Inventory2,
                    title = "Core",
                    onClick = { onNavigate("core") }
                )
                SettingsNavigationItem(
                    icon = Icons.Default.AspectRatio,
                    title = "Packet Tunnel",
                    onClick = { onNavigate("packet_tunnel") }
                )
                SettingsNavigationItem(
                    icon = Icons.Default.MenuOpen,
                    title = "On Demand Rules",
                    onClick = { onNavigate("on_demand_rules") }
                )
                SettingsNavigationItem(
                    icon = Icons.Default.Dashboard,
                    title = "Profile Override",
                    onClick = { onNavigate("profile_override") }
                )
                SettingsNavigationItem(
                    icon = Icons.Default.SettingsRemote,
                    title = "Remote Control",
                    onClick = { onNavigate("remote_control") }
                )
            }
        }

        // About Section
        item {
            SettingsSection(title = "About") {
                // Documentation
                ClickableSettingsItem(
                    icon = Icons.Default.Description,
                    title = "Documentation",
                    onClick = { /* 打开文档链接 */ }
                )

                // Source Code
                ClickableSettingsItem(
                    icon = Icons.Default.Code,
                    title = "Source Code",
                    onClick = { /* 打开源码链接 */ }
                )

                // Rate on App Store
                ClickableSettingsItem(
                    icon = Icons.Default.Star,
                    title = "Rate on the App Store",
                    onClick = { /* 打开评分 */ }
                )
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String? = null,
    content: @Composable () -> Unit
) {
    Column {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 8.dp
                )
            )
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun SettingsNavigationItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
```

---

## 🎨 主题和颜色复制

### iOS 颜色映射

```kotlin
// Color.kt
object SingBoxColors {
    // Primary colors
    val Primary = Color(0xFF007AFF) // iOS Blue
    val Secondary = Color(0xFF5856D6) // iOS Purple
    val Accent = Color(0xFFFF9500) // iOS Orange

    // Status colors
    val Success = Color(0xFF34C759) // iOS Green
    val Warning = Color(0xFFFF9500) // iOS Orange
    val Error = Color(0xFFFF3B30) // iOS Red

    // Background colors
    val Background = Color(0xFFF2F2F7) // iOS Grouped Background
    val Surface = Color(0xFFFFFFFF) // iOS Card Background
    val SurfaceVariant = Color(0xFFF2F2F7) // iOS Secondary Background

    // Text colors
    val TextPrimary = Color(0xFF000000)
    val TextSecondary = Color(0xFF8E8E93)
    val TextTertiary = Color(0xFFC7C7CC)

    // Dark mode colors
    val DarkBackground = Color(0xFF000000)
    val DarkSurface = Color(0xFF1C1C1E)
    val DarkSurfaceVariant = Color(0xFF2C2C2E)
}

// Theme.kt
@Composable
fun SingBoxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = SingBoxColors.Primary,
            secondary = SingBoxColors.Secondary,
            tertiary = SingBoxColors.Accent,
            background = SingBoxColors.DarkBackground,
            surface = SingBoxColors.DarkSurface,
            surfaceVariant = SingBoxColors.DarkSurfaceVariant,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = Color.White,
            onSurface = Color.White,
            onSurfaceVariant = Color(0xFF8E8E93)
        )
    } else {
        lightColorScheme(
            primary = SingBoxColors.Primary,
            secondary = SingBoxColors.Secondary,
            tertiary = SingBoxColors.Accent,
            background = SingBoxColors.Background,
            surface = SingBoxColors.Surface,
            surfaceVariant = SingBoxColors.SurfaceVariant,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = Color.Black,
            onSurface = Color.Black,
            onSurfaceVariant = Color(0xFF8E8E93)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SingBoxTypography,
        content = content
    )
}
```

---

## 📐 布局和间距规范

### iOS 设计规范

```kotlin
// Dimensions.kt
object SingBoxDimensions {
    // Card dimensions
    val CardCornerRadius = 16.dp
    val CardPadding = 16.dp
    val CardSpacing = 12.dp

    // Status bar pill
    val PillHeight = 44.dp
    val PillCornerRadius = 22.dp
    val PillHorizontalPadding = 20.dp
    val PillVerticalPadding = 8.dp

    // FAB button
    val FabSize = 56.dp
    val FabCornerRadius = 16.dp
    val FabIconSize = 22.dp

    // List items
    val ListItemHeight = 44.dp
    val ListItemPadding = 16.dp
    val ListItemSpacing = 12.dp

    // Section headers
    val SectionHeaderPadding = PaddingValues(
        start = 16.dp,
        end = 16.dp,
        top = 16.dp,
        bottom = 8.dp
    )

    // Navigation
    val NavigationIconSize = 24.dp
    val NavigationSpacing = 16.dp

    // Typography sizes
    val TitleFontSize = 17.sp
    val BodyFontSize = 17.sp
    val CaptionFontSize = 12.sp
    val FootnoteFontSize = 13.sp
}
```

---

## 🔄 动画效果复制

### iOS 动画规范

```kotlin
// Animations.kt
object SingBoxAnimations {
    // Spring animation (used for status bar transitions)
    val StatusBarSpring = spring<Float>(
        dampingRatio = 0.8f,
        stiffness = Spring.StiffnessMedium
    )

    // Ease out animation (used for button press)
    val ButtonPress = tween<Float>(
        durationMillis = 120,
        easing = FastOutSlowInEasing
    )

    // Standard transition
    val StandardTransition = tween<Float>(
        durationMillis = 300,
        easing = FastOutSlowInEasing
    )

    // Slide in from bottom
    val SlideInFromBottom = slideInVertically(
        initialOffsetY = { it },
        animationSpec = tween(300)
    ) + fadeIn(animationSpec = tween(300))

    // Slide out to bottom
    val SlideOutToBottom = slideOutVertically(
        targetOffsetY = { it },
        animationSpec = tween(300)
    ) + fadeOut(animationSpec = tween(300))
}
```

---

## 📋 迁移检查清单

### 主界面
- [ ] TabView 底部导航栏
- [ ] 状态栏药丸组件
- [ ] FAB 开始按钮
- [ ] 导航按钮组
- [ ] 开始/停止按钮

### Dashboard
- [ ] Status Card
- [ ] Profile Card
- [ ] Connections Card
- [ ] Upload Traffic Card
- [ ] Download Traffic Card
- [ ] Clash Mode Card
- [ ] HTTP Proxy Card
- [ ] 卡片管理 Sheet
- [ ] 流量图表

### Logs
- [ ] 日志显示（等宽字体）
- [ ] ANSI 颜色支持
- [ ] 搜索功能
- [ ] 暂停/恢复按钮
- [ ] 日志级别过滤
- [ ] 导出功能（剪贴板、文件、分享）
- [ ] 远程设备切换

### Tools
- [ ] Endpoints 列表
- [ ] Services 列表
- [ ] Network Quality
- [ ] STUN Test
- [ ] Crash Report
- [ ] OOM Report

### Settings
- [ ] App 设置
- [ ] Core 设置
- [ ] Packet Tunnel 设置
- [ ] On Demand Rules
- [ ] Profile Override
- [ ] Remote Control
- [ ] About 页面

### 通用组件
- [ ] NavigationItem
- [ ] SectionHeader
- [ ] FormItem
- [ ] Alert 对话框
- [ ] Loading 指示器
- [ ] Badge 角标

---

## 🎯 关键注意事项

### 1. 颜色一致性
- 使用 iOS 系统颜色值
- 支持 Light/Dark 模式
- 保持对比度一致

### 2. 字体和排版
- 使用 SF Pro 字体（或系统默认字体）
- 保持字体大小一致
- 保持行高和间距一致

### 3. 交互反馈
- 按钮按下效果
- 列表项点击反馈
- 滑动返回手势

### 4. 动画效果
- 使用相同的动画参数
- 保持动画时长一致
- 保持缓动曲线一致

### 5. 布局适配
- 支持不同屏幕尺寸
- 支持横竖屏切换
- 支持折叠屏设备

---

## 📚 参考资源

1. [Material Design 3](https://m3.material.io/)
2. [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
3. [iOS Design Guidelines](https://developer.apple.com/design/human-interface-guidelines/)
4. [Compose Animation](https://developer.android.com/jetpack/compose/animation)

---

## ✅ 总结

通过以上方案，你可以：

1. ✅ **完全复制** iOS 界面设计
2. ✅ **保持** 所有交互逻辑
3. ✅ **实现** 相同的视觉效果
4. ✅ **支持** Light/Dark 模式
5. ✅ **适配** 不同 Android 设备

**关键点**:
- 使用 Material Design 3 组件
- 精确匹配 iOS 颜色和间距
- 复制所有动画效果
- 保持相同的信息架构

祝你迁移顺利！🎉
