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
import androidx.compose.foundation.horizontalScroll
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.CancellationException
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

data class NewsItem(val title: String, val link: String)

data class Cat(val name: String, val icon: ImageVector, val color: Color)

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

private val trendFilters = listOf("सभी", "राष्ट्रीय समाचार", "खेल", "टेक्नोलॉजी", "शिक्षा")

private fun encode(list: List<NewsItem>): String =
    list.joinToString("\n") { it.title + SEP + it.link }

private fun decode(text: String): List<NewsItem> =
    text.lines().mapNotNull { line ->
        val p = line.split(SEP)
        if (p.size == 2 && p[1].startsWith("https://")) NewsItem(p[0], p[1]) else null
    }

private fun fetchNews(): List<NewsItem> {
    val conn = URL(NEWS_URL).openConnection() as HttpURLConnection
    try {
        conn.connectTimeout = 12000
        conn.readTimeout = 12000
        conn.requestMethod = "GET"
        conn.setRequestProperty("User-Agent", "EarnZone-Android")
        if (conn.responseCode != 200) throw IOException("HTTP " + conn.responseCode)
        val text = conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            .trimStart('\uFEFF')
        val parser = Xml.newPullParser()
        parser.setInput(StringReader(text))
        val out = ArrayList<NewsItem>()
        var inItem = false
        var title = ""
        var link = ""
        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            if (event == XmlPullParser.START_TAG) {
                val name = parser.name
                if (name == "item") {
                    inItem = true
                    title = ""
                    link = ""
                } else if (inItem && name == "title") {
                    title = parser.nextText().trim().replace(Regex("\\s+"), " ")
                } else if (inItem && name == "link") {
                    link = parser.nextText().trim()
                }
            } else if (event == XmlPullParser.END_TAG && parser.name == "item") {
                if (title.isNotEmpty() && link.startsWith("https://")) {
                    out.add(NewsItem(title, link))
                }
                inItem = false
                if (out.size >= 20) break
            }
            event = parser.next()
        }
        return out
    } finally {
        conn.disconnect()
    }
}

private fun openLink(context: Context, url: String) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } catch (e: Exception) {
        // browser not found
    }
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
        saved = if (isSaved(item.link)) {
            saved.filter { it.link != item.link }
        } else {
            listOf(item) + saved
        }
        prefs.edit().putString("saved_news", encode(saved)).apply()
    }

    suspend fun refresh() {
        if (loading) return
        loading = true
        error = ""
        try {
            val list = withContext(Dispatchers.IO) { fetchNews() }
            if (list.isEmpty()) {
                error = "अभी कोई खबर नहीं मिली। थोड़ी देर बाद फिर कोशिश करें।"
            } else {
                val time = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date())
                items = list
                updated = time
                prefs.edit()
                    .putString("news_cache", encode(list))
                    .putString("news_time", time)
                    .apply()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            error = "इंटरनेट नहीं है या सर्वर से जुड़ नहीं पाए। कनेक्शन जाँचकर फिर कोशिश करें।"
        } catch (e: Exception) {
            error = "खबरें इस समय नहीं मिल पाईं। थोड़ी देर बाद फिर कोशिश करें।"
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
                    Text("स्रोत: PIB", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Purple)
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
            Text(
                item.title,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = Ink,
                modifier = Modifier.padding(end = 8.dp)
            )
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
fun FeaturedCard(item: NewsItem, state: AppState) {
    val context = LocalContext.current
    val isSaved = state.isSaved(item.link)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(Purple2, Purple)))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Gold)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("ताज़ा खबर", color = Purple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text("स्रोत: PIB", color = Color(0xFFE9DDFF), fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                item.title,
                color = Color.White,
                fontSize = 20.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { openLink(context, item.link) },
                    colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Purple)
                ) {
                    Text("मूल लेख खोलें", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { state.toggleSave(item) }) {
                    Icon(
                        if (isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = "सेव",
                        tint = if (isSaved) Gold else Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun FeedStatus(state: AppState, onRefresh: () -> Unit) {
    if (state.error.isNotEmpty()) {
        WhiteCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("⚠️ " + state.error, color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                if (state.items.isNotEmpty()) {
                    Text(
                        "नीचे फ़ोन में सेव की हुई पिछली खबरें दिख रही हैं।",
                        fontSize = 12.sp,
                        color = Muted
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(enabled = !state.loading, onClick = onRefresh) {
                    Text("फिर कोशिश करें (Retry)")
                }
            }
        }
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            if (state.updated.isEmpty()) "अभी ताज़ा नहीं हुआ"
            else "आख़िरी बार ताज़ा किया: ${state.updated}",
            fontSize = 12.sp,
            color = Muted,
            modifier = Modifier.weight(1f)
        )
        if (state.loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = Purple
            )
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
    EmptyState(
        Icons.Filled.Info,
        "अभी कोई खबर नहीं",
        "इंटरनेट चालू करके \"ताज़ा करें\" दबाइए।"
    )
}

@Composable
fun NoSourceState(icon: ImageVector) {
    EmptyState(
        icon,
        "अभी कोई स्रोत नहीं जुड़ा",
        "इस श्रेणी के लिए अभी कोई भरोसेमंद खबर-स्रोत नहीं जुड़ा है। हम नकली खबरें नहीं दिखाते।"
    )
}

@Composable
fun EarnZoneApp(prefs: SharedPreferences) {
    val state = remember { AppState(prefs) }
    val scope = rememberCoroutineScope()
    val refresh: () -> Unit = { scope.launch { state.refresh() } }
    var tab by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) { state.refresh() }

    val tabs = listOf(
        "Home" to Icons.Filled.Home,
        "Categories" to Icons.Filled.Category,
        "Trending" to Icons.Filled.Whatshot,
        "Saved" to Icons.Filled.Bookmark,
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
                            selected = tab == i,
                            onClick = { tab = i },
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
            when (tab) {
                0 -> HomeScreen(state, refresh) { tab = it }
                1 -> CategoriesScreen(state, refresh)
                2 -> TrendingScreen(state, refresh)
                3 -> SavedScreen(state)
                else -> ProfileScreen()
            }
        }
    }
}

@Composable
fun HomeScreen(state: AppState, onRefresh: () -> Unit, onGo: (Int) -> Unit) {
    var searching by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var showBell by remember { mutableStateOf(false) }

    Column {
        Header("EarnZone", "सरकारी खबरें, एक जगह") {
            IconButton(onClick = {
                searching = !searching
                if (!searching) query = ""
            }) {
                Icon(
                    if (searching) Icons.Filled.Close else Icons.Filled.Search,
                    contentDescription = "खोजें",
                    tint = Color.White
                )
            }
            IconButton(onClick = { showBell = true }) {
                Icon(Icons.Filled.Notifications, contentDescription = "सूचनाएँ", tint = Color.White)
            }
        }
        Body {
            if (searching) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("खबरों के शीर्षक में खोजें") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Filled.Close, contentDescription = "मिटाएँ")
                            }
                        }
                    }
                )
                val q = query.trim()
                if (q.isEmpty()) {
                    Text("ऊपर खोज शब्द लिखिए।", color = Muted, fontSize = 13.sp)
                } else {
                    val res = state.items.filter { it.title.contains(q, ignoreCase = true) }
                    if (res.isEmpty()) {
                        EmptyState(
                            Icons.Filled.Search,
                            "कोई नतीजा नहीं",
                            "\"$q\" से मेल खाती कोई खबर नहीं मिली।"
                        )
                    } else {
                        Text("${res.size} नतीजे मिले", color = Muted, fontSize = 13.sp)
                        NewsList(res, state)
                    }
                }
            } else {
                FeedStatus(state, onRefresh)
                if (state.items.isEmpty()) {
                    if (!state.loading) NoNewsState()
                } else {
                    FeaturedCard(state.items[0], state)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { c ->
                            PillChip(c.name, false) { onGo(1) }
                        }
                    }
                    SectionTitle("टॉप हेडलाइन्स")
                    NewsList(state.items.drop(1).take(5), state)
                    if (state.items.size > 6) {
                        SectionTitle("ताज़ा समाचार")
                        NewsList(state.items.drop(6), state)
                    }
                }
            }
        }
    }

    if (showBell) {
        AlertDialog(
            onDismissRequest = { showBell = false },
            title = { Text("सूचनाएँ") },
            text = { Text("रोज़ की सूचनाएँ अभी उपलब्ध नहीं हैं। बाद के अपडेट में जोड़ी जाएँगी।") },
            confirmButton = {
                TextButton(onClick = { showBell = false }) { Text("ठीक है") }
            }
        )
    }
}

@Composable
fun CatTile(cat: Cat, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CardWhite)
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(cat.color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(cat.icon, null, tint = cat.color)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            cat.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Ink,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CategoriesScreen(state: AppState, onRefresh: () -> Unit) {
    var open by remember { mutableStateOf(-1) }

    if (open >= 0) {
        val cat = categories[open]
        Column {
            Header(cat.name, "श्रेणी") {
                IconButton(onClick = { open = -1 }) {
                    Icon(Icons.Filled.Close, contentDescription = "वापस", tint = Color.White)
                }
            }
            Body {
                if (open == 0) {
                    FeedStatus(state, onRefresh)
                    if (state.items.isEmpty()) {
                        if (!state.loading) NoNewsState()
                    } else {
                        NewsList(state.items, state)
                    }
                } else {
                    NoSourceState(cat.icon)
                }
            }
        }
    } else {
        Column {
            Header("श्रेणियाँ", "अपनी पसंद चुनिए")
            Body {
                categories.chunked(3).forEachIndexed { r, row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        row.forEachIndexed { c, cat ->
                            CatTile(cat, Modifier.weight(1f)) { open = r * 3 + c }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrendingScreen(state: AppState, onRefresh: () -> Unit) {
    var sel by remember { mutableStateOf(0) }
    Column {
        Header("ट्रेंडिंग", "ताज़ा अपडेट") {
            Icon(Icons.Filled.Whatshot, null, modifier = Modifier.size(28.dp), tint = Gold)
        }
        Body {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                trendFilters.forEachIndexed { i, t ->
                    PillChip(t, sel == i) { sel = i }
                }
            }
            Text(
                "यह सूची ताज़ा खबरों की है। फ़ीड में लोकप्रियता का डेटा नहीं है, इसलिए कोई रैंकिंग नहीं दिखाई जाती।",
                fontSize = 12.sp,
                color = Muted
            )
            FeedStatus(state, onRefresh)
            if (sel <= 1) {
                if (state.items.isEmpty()) {
                    if (!state.loading) NoNewsState()
                } else {
                    NewsList(state.items, state)
                }
            } else {
                NoSourceState(Icons.Filled.Whatshot)
            }
        }
    }
}

@Composable
fun SavedScreen(state: AppState) {
    Column {
        Header("सेव की गई खबरें", "${state.saved.size} खबरें") {
            Icon(Icons.Filled.Bookmark, null, modifier = Modifier.size(28.dp), tint = Gold)
        }
        Body {
            if (state.saved.isEmpty()) {
                EmptyState(
                    Icons.Filled.BookmarkBorder,
                    "अभी कुछ सेव नहीं है",
                    "किसी खबर के बुकमार्क आइकन को दबाइए, वह यहाँ सेव हो जाएगी।"
                )
            } else {
                NewsList(state.saved, state)
            }
        }
    }
}

@Composable
fun SettingRow(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardWhite)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Soft),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = Purple)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, color = Ink)
            Text(subtitle, fontSize = 12.sp, color = Muted)
        }
        Icon(Icons.Filled.ChevronRight, null, tint = Muted)
    }
}

@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    var dlg by remember { mutableStateOf<Pair<String, String>?>(null) }

    Column {
        Header("EarnZone", "वर्ज़न 1.3")
        Body {
            SettingRow(Icons.Filled.Language, "भाषा", "हिंदी") {
                dlg = Pair(
                    "भाषा",
                    "अभी सिर्फ़ हिंदी उपलब्ध है, क्योंकि खबरों का स्रोत हिंदी है। दूसरी भाषाएँ बाद में जोड़ी जाएँगी।"
                )
            }
            SettingRow(Icons.Filled.Notifications, "सूचनाएँ", "अभी उपलब्ध नहीं") {
                dlg = Pair(
                    "सूचनाएँ",
                    "रोज़ की सूचनाएँ अभी उपलब्ध नहीं हैं। बाद के अपडेट में जोड़ी जाएँगी।"
                )
            }
            SettingRow(Icons.Filled.Info, "EarnZone के बारे में", "यह ऐप क्या है") {
                dlg = Pair(
                    "EarnZone के बारे में",
                    "EarnZone एक खबर ऐप है। अभी इसमें पत्र सूचना कार्यालय (PIB) की हिंदी प्रेस विज्ञप्तियों के शीर्षक और मूल लेख के लिंक दिखते हैं। यह भारत सरकार का ऐप नहीं है।"
                )
            }
            SettingRow(Icons.Filled.Lock, "गोपनीयता नीति", "Privacy Policy") {
                dlg = Pair(
                    "गोपनीयता नीति",
                    "ऐप आपकी कोई निजी जानकारी इकट्ठा नहीं करता। सेव की गई खबरें सिर्फ़ आपके फ़ोन में रहती हैं। विज्ञापन Google AdMob दिखाता है, जो अपने नियमों के अनुसार विज्ञापन के लिए डेटा इस्तेमाल कर सकता है। पूरी गोपनीयता नीति सार्वजनिक रिलीज़ से पहले जोड़ी जाएगी।"
                )
            }
            SettingRow(Icons.Filled.Description, "नियम और शर्तें", "Terms & Conditions") {
                dlg = Pair(
                    "नियम और शर्तें",
                    "खबरें PIB की RSS फ़ीड से बिना बदले दिखाई जाती हैं और स्रोत साफ़ लिखा जाता है। पूरा लेख PIB की वेबसाइट पर खुलता है। पूरे नियम सार्वजनिक रिलीज़ से पहले जोड़े जाएँगे।"
                )
            }
            SettingRow(Icons.Filled.Share, "ऐप शेयर करें", "दोस्तों को बताइए") {
                val send = Intent(Intent.ACTION_SEND)
                send.type = "text/plain"
                send.putExtra(Intent.EXTRA_TEXT, "EarnZone: सरकारी खबरें, एक जगह।")
                context.startActivity(Intent.createChooser(send, "EarnZone शेयर करें"))
            }
            SettingRow(Icons.Filled.Info, "ऐप वर्ज़न", "1.3") { }
        }
    }

    val d = dlg
    if (d != null) {
        AlertDialog(
            onDismissRequest = { dlg = null },
            title = { Text(d.first) },
            text = { Text(d.second) },
            confirmButton = {
                TextButton(onClick = { dlg = null }) { Text("ठीक है") }
            }
        )
    }
}
