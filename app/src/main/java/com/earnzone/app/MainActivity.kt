package com.earnzone.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Xml
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import java.io.IOException
import java.io.StringReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser

private val Purple = Color(0xFF5B21B6)
private val Purple2 = Color(0xFF7C3AED)
private val Gold = Color(0xFFFFC928)
private val Bg = Color(0xFFF7F5FF)
private val Ink = Color(0xFF17152B)
private val CardWhite = Color(0xFFFFFFFF)
private val Muted = Color(0xFF6B6B7B)
private val Soft = Color(0xFFEDE7FF)

private const val NEWS_URL = "https://www.pib.gov.in/RssMain.aspx?ModId=6&Lang=2&Regid=3"
private const val SEP = "\u001F"
private const val MAX_PER_SOURCE = 20

data class NewsItem(
    val title: String,
    val link: String,
    val source: String = "PIB",
    val pubDate: Long = 0L
)

data class Cat(
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val keywords: List<String> = emptyList()
)

data class Tool(val id: Int, val name: String, val desc: String, val icon: ImageVector)

private val categories = listOf(
    Cat("राष्ट्रीय समाचार", Icons.Filled.Flag, Color(0xFF5B21B6)),
    Cat("अंतरराष्ट्रीय", Icons.Filled.Public, Color(0xFF0284C7)),
    Cat("टेक्नोलॉजी", Icons.Filled.Computer, Color(0xFF7C3AED)),
    Cat("सरकारी नौकरी", Icons.Filled.Work, Color(0xFFEA580C)),
    Cat("सरकारी योजनाएँ", Icons.Filled.AccountBalance, Color(0xFF16A34A)),
    Cat("शिक्षा", Icons.Filled.School, Color(0xFFDB2777)),
    Cat("खेल", Icons.Filled.SportsSoccer, Color(0xFF059669)),
    Cat("मनोरंजन", Icons.Filled.Movie, Color(0xFFD946EF)),
    Cat("व्यापार", Icons.Filled.TrendingUp, Color(0xFFCA8A04)),
    Cat("विज्ञान", Icons.Filled.Science, Color(0xFF0D9488)),
    Cat("स्वास्थ्य", Icons.Filled.HealthAndSafety, Color(0xFFDC2626)),
    Cat("ऑटोमोबाइल", Icons.Filled.DirectionsCar, Color(0xFF475569))
)

private val allTools = listOf(
    Tool(1, "कैलकुलेटर", "जोड़, घटाव, गुणा, भाग", Icons.Filled.Calculate),
    Tool(2, "EMI कैलकुलेटर", "लोन की किस्त निकालें", Icons.Filled.AccountBalance),
    Tool(3, "यूनिट कन्वर्टर", "लंबाई, वज़न, तापमान", Icons.Filled.Build),
    Tool(4, "उम्र कैलकुलेटर", "जन्मतिथि से उम्र निकालें", Icons.Filled.Person),
    Tool(5, "QR जनरेटर", "टेक्स्ट से QR बनाएँ", Icons.Filled.Info)
)

private data class Source(val name: String, val url: String, val isNational: Boolean)

private val sources = listOf(Source("PIB", NEWS_URL, true))

private fun cleanField(s: String): String = s.replace(SEP, " ").replace("\n", " ").replace("\r", " ")

private fun encode(list: List<NewsItem>): String = list.joinToString("\n") {
    cleanField(it.title) + SEP + it.link + SEP + cleanField(it.source) + SEP + it.pubDate
}

private fun decode(text: String): List<NewsItem> = text.lines().mapNotNull { line ->
    val p = line.split(SEP)
    if (p.size < 2 || p[0].isEmpty() || !p[1].startsWith("https://")) null
    else if (p.size == 2) NewsItem(p[0], p[1])
    else NewsItem(
        p[0], p[1],
        p[2].ifBlank { "अज्ञात स्रोत" },
        if (p.size >= 4) (p[3].toLongOrNull() ?: 0L) else 0L
    )
}

private fun parseDate(raw: String): Long {
    val s = raw.trim()
    if (s.isEmpty()) return 0L
    val formats = listOf("EEE, dd MMM yyyy HH:mm:ss Z", "yyyy-MM-dd'T'HH:mm:ssXXX")
    for (f in formats) {
        try {
            val sdf = SimpleDateFormat(f, Locale.ENGLISH).apply { isLenient = false }
            val t = sdf.parse(s)?.time ?: continue
            return if (t > 0L) t else 0L
        } catch (e: ParseException) {
        }
    }
    return 0L
}

private fun formatDate(millis: Long): String =
    SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(millis))

private fun fetchSource(src: Source): List<NewsItem> {
    val conn = URL(src.url).openConnection() as HttpURLConnection
    try {
        conn.connectTimeout = 12000
        conn.readTimeout = 12000
        conn.requestMethod = "GET"
        conn.setRequestProperty("User-Agent", "EarnZone-Android")
        if (conn.responseCode != 200) throw IOException("HTTP " + conn.responseCode)
        val text = conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }.trimStart('\uFEFF')
        val parser = Xml.newPullParser().apply { setInput(StringReader(text)) }
        val out = ArrayList<NewsItem>()
        var inItem = false
        var title = ""
        var link = ""
        var date = ""
        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            if (event == XmlPullParser.START_TAG) {
                val name = parser.name
                if (name == "item" || name == "entry") {
                    inItem = true; title = ""; link = ""; date = ""
                } else if (inItem && name == "title") {
                    title = parser.nextText().trim().replace(Regex("\\s+"), " ")
                } else if (inItem && name == "link") {
                    val href = parser.getAttributeValue(null, "href")
                    val t = parser.nextText().trim()
                    link = href?.trim() ?: if (t.isNotEmpty()) t else ""
                } else if (inItem && (name == "pubDate" || name == "published" || name == "updated")) {
                    date = parser.nextText().trim()
                }
            } else if (event == XmlPullParser.END_TAG && (parser.name == "item" || parser.name == "entry")) {
                if (title.isNotEmpty() && link.startsWith("https://")) {
                    out.add(NewsItem(title, link, src.name, parseDate(date)))
                }
                inItem = false
                if (out.size >= MAX_PER_SOURCE) break
            }
            event = parser.next()
        }
        return out
    } finally {
        conn.disconnect()
    }
}

private fun mergeItems(lists: List<List<NewsItem>>): List<NewsItem> {
    val out = ArrayList<NewsItem>()
    val seenLinks = HashSet<String>()
    for (l in lists) {
        for (item in l) {
            if (seenLinks.add(item.link)) out.add(item)
        }
    }
    return out.sortedByDescending { it.pubDate }
}

private fun openLink(context: Context, url: String) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } catch (e: Exception) {
    }
}

private fun appVersion(context: Context): String = try {
    context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0"
} catch (e: Exception) {
    "1.0"
}

class AppState(private val prefs: SharedPreferences) {
    var items by mutableStateOf(decode(prefs.getString("news_cache", "") ?: ""))
        private set
    var saved by mutableStateOf(decode(prefs.getString("saved_news", "") ?: ""))
        private set
    var updated by mutableStateOf(prefs.getString("news_time", "") ?: "")
        private set
    var loading by mutableStateOf(false)
        private set
    var error by mutableStateOf("")
        private set

    fun isSaved(link: String): Boolean = saved.any { it.link == link }

    fun toggleSave(item: NewsItem) {
        saved = if (isSaved(item.link)) saved.filter { it.link != item.link } else listOf(item) + saved
        prefs.edit().putString("saved_news", encode(saved)).apply()
    }

    suspend fun refresh() {
        if (loading) return
        loading = true
        error = ""
        try {
            val results = withContext(Dispatchers.IO) {
                sources.map { src -> try { fetchSource(src) } catch (e: Exception) { emptyList() } }
            }
            val merged = mergeItems(results)
            if (merged.isNotEmpty()) {
                val time = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date())
                items = merged
                updated = time
                prefs.edit().putString("news_cache", encode(merged)).putString("news_time", time).apply()
            }
        } catch (e: Exception) {
            error = "खबरें लोड करने में विफल।"
        } finally {
            loading = false
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        val prefs = getSharedPreferences("earnzone", Context.MODE_PRIVATE)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(primary = Purple, secondary = Purple2, background = Bg)
            ) {
                EarnZoneApp(prefs)
            }
        }
    }
}

@Composable
fun BannerAd() {
    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { ctx ->
            AdView(ctx).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = "ca-app-pub-3940256099942544/6300978111"
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}

@Composable
fun WhiteCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        content = content
    )
}

@Composable
fun Header(title: String, subtitle: String = "", actions: @Composable RowScope.() -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(Brush.verticalGradient(listOf(Purple, Purple2)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Gold),
                contentAlignment = Alignment.Center
            ) {
                Text("E", color = Purple, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                if (subtitle.isNotEmpty()) {
                    Text(subtitle, color = Color(0xFFE9DDFF), fontSize = 13.sp)
                }
            }
            actions()
        }
    }
}

@Composable
fun Body(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content
    )
}

@Composable
fun SectionTitle(text: String) {
    Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Ink)
}

@Composable
fun PillChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) Purple else Soft)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text,
            color = if (selected) Color.White else Purple,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EmptyState(icon: ImageVector, title: String, text: String) {
    WhiteCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Soft),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, modifier = Modifier.size(32.dp), tint = Purple)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Ink)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text, fontSize = 13.sp, color = Muted, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun NewsCard(item: NewsItem, state: AppState) {
    val context = LocalContext.current
    val isSaved = state.isSaved(item.link)
    WhiteCard {
        Column(modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Soft)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("स्रोत: " + item.source, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Purple)
                }
                if (item.pubDate > 0L) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(formatDate(item.pubDate), fontSize = 12.sp, color = Muted)
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { state.toggleSave(item) }) {
                    Icon(
                        if (isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = "सेव",
                        tint = if (isSaved) Gold else Purple
                    )
                }
            }
            Text(item.title, fontSize = 16.sp, lineHeight = 24.sp, color = Ink, modifier = Modifier.padding(end = 8.dp))
            TextButton(onClick = { openLink(context, item.link) }) {
                Text("मूल लेख खोलें", color = Purple, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun NewsList(list: List<NewsItem>, state: AppState) {
    list.forEach { NewsCard(it, state) }
}

@Composable
fun FeedStatus(state: AppState, onRefresh: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            if (state.updated.isEmpty()) "अभी ताज़ा नहीं हुआ" else "आख़िरी बार ताज़ा किया: ${state.updated}",
            fontSize = 12.sp,
            color = Muted,
            modifier = Modifier.weight(1f)
        )
        if (state.loading) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Purple)
        } else {
            TextButton(onClick = onRefresh) {
                Icon(Icons.Filled.Refresh, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("ताज़ा करें")
            }
        }
    }
}

@Composable
fun NoNewsState() {
    EmptyState(Icons.Filled.Info, "अभी कोई खबर नहीं", "इंटरनेट चालू करके \"ताज़ा करें\" दबाइए।")
}

@Composable
fun ToolRow(tool: Tool, isFavorite: Boolean, onToggleFavorite: () -> Unit, onOpen: () -> Unit) {
    WhiteCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpen() }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Soft),
                contentAlignment = Alignment.Center
            ) {
                Icon(tool.icon, null, tint = Purple)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(tool.name, fontWeight = FontWeight.Bold, color = Ink, fontSize = 16.sp)
                Text(tool.desc, color = Muted, fontSize = 13.sp)
            }
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    if (isFavorite) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                    contentDescription = "फेवरेट",
                    tint = if (isFavorite) Gold else Purple
                )
            }
        }
    }
}

@Composable
fun HomeDashboardScreen(
    prefs: SharedPreferences,
    newsState: AppState,
    onRefreshNews: () -> Unit,
    recentToolIds: List<Int>,
    onOpenTool: (Int) -> Unit,
    onNavigateTab: (Int, String) -> Unit
) {
    Header("EarnZone", "आज की ताज़ा खबरें और टूल्स")
    Body {
        val recent = recentToolIds.mapNotNull { id -> allTools.find { it.id == id } }
        if (recent.isNotEmpty()) {
            SectionTitle("हाल के टूल्स")
            recent.forEach { t ->
                ToolRow(t, false, onToggleFavorite = {}, onOpen = { onOpenTool(t.id) })
            }
        }
        SectionTitle("ताज़ा खबरें")
        FeedStatus(newsState, onRefreshNews)
        if (newsState.error.isNotEmpty()) {
            Text(newsState.error, color = Color(0xFFDC2626), fontSize = 13.sp)
        }
        if (newsState.items.isEmpty()) NoNewsState() else NewsList(newsState.items, newsState)
    }
}

@Composable
fun StudyAndGamesScreen(prefs: SharedPreferences, initialMode: String) {
    Header("स्टडी", "पढ़ाई और गेम्स")
    Body {
        EmptyState(Icons.Filled.School, "जल्द आ रहा है", "स्टडी और गेम्स सेक्शन जल्द उपलब्ध होगा।")
    }
}

@Composable
fun PopularToolsDirectoryScreen(
    favoriteToolIds: Set<Int>,
    onToggleFavorite: (Int) -> Unit,
    onOpenTool: (Int) -> Unit
) {
    Header("पॉपुलर टूल्स", "काम के टूल्स एक जगह")
    Body {
        allTools.forEach { t ->
            ToolRow(
                t,
                favoriteToolIds.contains(t.id),
                onToggleFavorite = { onToggleFavorite(t.id) },
                onOpen = { onOpenTool(t.id) }
            )
        }
    }
}

@Composable
fun FavoritesScreen(
    favoriteToolIds: Set<Int>,
    newsState: AppState,
    onToggleFavorite: (Int) -> Unit,
    onOpenTool: (Int) -> Unit
) {
    Header("फेवरेट्स", "आपके सेव किए हुए")
    Body {
        val favTools = allTools.filter { favoriteToolIds.contains(it.id) }
        SectionTitle("टूल्स")
        if (favTools.isEmpty()) {
            EmptyState(Icons.Filled.Bookmark, "कोई फेवरेट टूल नहीं", "किसी टूल पर बुकमार्क दबाइए।")
        } else {
            favTools.forEach { t ->
                ToolRow(t, true, onToggleFavorite = { onToggleFavorite(t.id) }, onOpen = { onOpenTool(t.id) })
            }
        }
        SectionTitle("सेव की हुई खबरें")
        if (newsState.saved.isEmpty()) {
            EmptyState(Icons.Filled.BookmarkBorder, "कोई सेव खबर नहीं", "खबर पर बुकमार्क दबाकर सेव करें।")
        } else {
            NewsList(newsState.saved, newsState)
        }
    }
}

@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    Header("प्रोफ़ाइल", "EarnZone")
    Body {
        WhiteCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("EarnZone", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Ink)
                Spacer(modifier = Modifier.height(4.dp))
                Text("वर्ज़न " + appVersion(context), color = Muted, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun ToolDetailScreen(
    toolId: Int,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBack: () -> Unit
) {
    val tool = allTools.find { it.id == toolId }
    Header(tool?.name ?: "टूल", tool?.desc ?: "") {
        IconButton(onClick = onToggleFavorite) {
            Icon(
                if (isFavorite) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                contentDescription = "फेवरेट",
                tint = Gold
            )
        }
    }
    Body {
        TextButton(onClick = onBack) {
            Icon(Icons.Filled.ArrowBack, null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("वापस जाएँ")
        }
        EmptyState(tool?.icon ?: Icons.Filled.Info, tool?.name ?: "टूल", "यह टूल जल्द उपलब्ध होगा।")
    }
}

@Composable
fun EarnZoneApp(prefs: SharedPreferences) {
    val state = remember { AppState(prefs) }
    val scope = rememberCoroutineScope()
    val refresh: () -> Unit = { scope.launch { state.refresh() } }
    var tab by remember { mutableStateOf(0) }
    var activeToolId by remember { mutableStateOf(-1) }
    var studyInitialMode by remember { mutableStateOf("MENU") }

    var favoriteToolIds by remember {
        val savedSet = prefs.getStringSet("fav_tool_ids", emptySet()) ?: emptySet()
        mutableStateOf(savedSet.mapNotNull { it.toIntOrNull() }.toSet())
    }
    var recentToolIds by remember {
        val raw = prefs.getString("recent_tool_ids", "") ?: ""
        mutableStateOf(raw.split(",").mapNotNull { it.toIntOrNull() })
    }

    fun openTool(id: Int) {
        activeToolId = id
        val updatedRecent = (listOf(id) + recentToolIds.filter { it != id }).take(4)
        recentToolIds = updatedRecent
        prefs.edit().putString("recent_tool_ids", updatedRecent.joinToString(",")).apply()
    }

    fun toggleFavTool(id: Int) {
        val next = if (favoriteToolIds.contains(id)) favoriteToolIds - id else favoriteToolIds + id
        favoriteToolIds = next
        prefs.edit().putStringSet("fav_tool_ids", next.map { it.toString() }.toSet()).apply()
    }

    LaunchedEffect(Unit) { state.refresh() }

    val tabs = listOf(
        "Home" to Icons.Filled.Home,
        "Study" to Icons.Filled.School,
        "Popular" to Icons.Filled.Build,
        "Favorites" to Icons.Filled.Bookmark,
        "Profile" to Icons.Filled.Person
    )

    Scaffold(
        containerColor = Bg,
        bottomBar = {
            Column {
                BannerAd()
                NavigationBar(containerColor = Color.White) {
                    tabs.forEachIndexed { i, (label, icon) ->
                        NavigationBarItem(
                            selected = tab == i && activeToolId == -1,
                            onClick = {
                                activeToolId = -1
                                if (i == 1) studyInitialMode = "MENU"
                                tab = i
                            },
                            icon = { Icon(icon, label) },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = Purple,
                                indicatorColor = Purple,
                                unselectedIconColor = Muted,
                                unselectedTextColor = Muted
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            if (activeToolId > 0) {
                ToolDetailScreen(
                    toolId = activeToolId,
                    isFavorite = favoriteToolIds.contains(activeToolId),
                    onToggleFavorite = { toggleFavTool(activeToolId) },
                    onBack = { activeToolId = -1 }
                )
            } else {
                when (tab) {
                    0 -> HomeDashboardScreen(
                        prefs = prefs,
                        newsState = state,
                        onRefreshNews = refresh,
                        recentToolIds = recentToolIds,
                        onOpenTool = { openTool(it) },
                        onNavigateTab = { targetTab, mode ->
                            studyInitialMode = mode
                            tab = targetTab
                        }
                    )
                    1 -> StudyAndGamesScreen(prefs = prefs, initialMode = studyInitialMode)
                    2 -> PopularToolsDirectoryScreen(
                        favoriteToolIds = favoriteToolIds,
                        onToggleFavorite = { toggleFavTool(it) },
                        onOpenTool = { openTool(it) }
                    )
                    3 -> FavoritesScreen(
                        favoriteToolIds = favoriteToolIds,
                        newsState = state,
                        onToggleFavorite = { toggleFavTool(it) },
                        onOpenTool = { openTool(it) }
                    )
                    else -> ProfileScreen()
                }
            }
        }
    }
}
