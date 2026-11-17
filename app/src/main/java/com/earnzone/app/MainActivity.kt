package com.earnzone.app

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.delay
import org.json.JSONArray
import org.json.JSONObject
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

private val Purple = Color(0xFF5B21B6)
private val Purple2 = Color(0xFF7C3AED)
private val Gold = Color(0xFFFFC928)
private val Bg = Color(0xFFF7F5FF)
private val Ink = Color(0xFF17152B)
private val CardWhite = Color(0xFFFFFFFF)
private val Muted = Color(0xFF6B6B7B)
private val Soft = Color(0xFFEDE7FF)

data class ToolModel(
    val id: Int,
    val title: String,
    val subtitle: String,
    val category: String,
    val icon: ImageVector,
    val accent: Color
)

private val baseTools: List<ToolModel> = listOf(
    // Calculator Tools
    ToolModel(1, "Basic Calculator", "जोड़, घटाव, गुणा, भाग", "Calculator Tools", Icons.Filled.Calculate, Color(0xFF5B21B6)),
    ToolModel(2, "Scientific Calculator", "त्रिकोणमिति और वैज्ञानिक गणना", "Calculator Tools", Icons.Filled.Functions, Color(0xFF7C3AED)),
    ToolModel(3, "Percentage Calculator", "प्रतिशत निकालें", "Calculator Tools", Icons.Filled.Percent, Color(0xFF0284C7)),
    ToolModel(4, "Age Calculator", "सटीक उम्र (वर्ष, माह, दिन)", "Calculator Tools", Icons.Filled.DateRange, Color(0xFFDB2777)),
    ToolModel(5, "GST Calculator", "GST जोड़ें और हटाएं", "Calculator Tools", Icons.Filled.ShoppingCart, Color(0xFF16A34A)),
    ToolModel(6, "EMI Calculator", "मासिक किस्त और ब्याज गणना", "Calculator Tools", Icons.Filled.AccountBalance, Color(0xFFEA580C)),
    ToolModel(7, "Compound Interest", "चक्रवृद्धि ब्याज कैलकुलेटर", "Calculator Tools", Icons.Filled.TrendingUp, Color(0xFF0D9488)),
    ToolModel(8, "Simple Interest", "साधारण ब्याज कैलकुलेटर", "Calculator Tools", Icons.Filled.AttachMoney, Color(0xFFCA8A04)),
    ToolModel(9, "Profit & Loss", "लाभ और हानि प्रतिशत", "Calculator Tools", Icons.Filled.ShowChart, Color(0xFFDC2626)),
    ToolModel(10, "Discount Calculator", "अंतिम मूल्य और बचत", "Calculator Tools", Icons.Filled.LocalOffer, Color(0xFF059669)),

    // Student Tools
    ToolModel(11, "CGPA to Percentage", "CGPA को प्रतिशत में बदलें", "Student Tools", Icons.Filled.School, Color(0xFF5B21B6)),
    ToolModel(12, "GPA Calculator", "सेमेस्टर GPA गणना", "Student Tools", Icons.Filled.Book, Color(0xFF7C3AED)),
    ToolModel(13, "Study Timer (Pomodoro)", "फोकस और ब्रेक टाइमर", "Student Tools", Icons.Filled.Timer, Color(0xFF0284C7)),
    ToolModel(14, "Exam Countdown", "परीक्षा के दिन गिनें", "Student Tools", Icons.Filled.Event, Color(0xFFDB2777)),
    ToolModel(15, "Attendance Calculator", "उपस्थिति प्रतिशत कैलकुलेटर", "Student Tools", Icons.Filled.Group, Color(0xFF16A34A)),

    // Text Tools
    ToolModel(16, "Word Counter", "शब्द, अक्षर और लाइन्स गिनें", "Text Tools", Icons.Filled.TextFormat, Color(0xFF5B21B6)),
    ToolModel(17, "Character Counter", "बिना स्पेस अक्षर गिनती", "Text Tools", Icons.Filled.FontDownload, Color(0xFF7C3AED)),
    ToolModel(18, "Text Case Converter", "UPPERCASE, lowercase, Title", "Text Tools", Icons.Filled.Edit, Color(0xFF0284C7)),
    ToolModel(19, "Text Reverser", "उल्टा टेक्स्ट जनरेटर", "Text Tools", Icons.Filled.SwapHoriz, Color(0xFFDB2777)),
    ToolModel(20, "Remove Extra Spaces", "अतिरिक्त स्पेस हटाएं", "Text Tools", Icons.Filled.CleaningServices, Color(0xFF16A34A)),

    // Unit Converter
    ToolModel(21, "Length Converter", "मीटर, किलोमीटर, फीट, इंच", "Unit Converter", Icons.Filled.Straighten, Color(0xFF5B21B6)),
    ToolModel(22, "Weight Converter", "किलोग्राम, ग्राम, पाउंड, औंस", "Unit Converter", Icons.Filled.Scale, Color(0xFF7C3AED)),
    ToolModel(23, "Temperature Converter", "Celsius, Fahrenheit, Kelvin", "Unit Converter", Icons.Filled.Thermostat, Color(0xFF0284C7)),
    ToolModel(24, "Volume Converter", "लीटर, मिलीलीटर, गैलन", "Unit Converter", Icons.Filled.Opacity, Color(0xFFDB2777)),
    ToolModel(25, "Area Converter", "वर्ग मीटर, एकड़, बीघा", "Unit Converter", Icons.Filled.CropSquare, Color(0xFF16A34A)),

    // Finance Tools
    ToolModel(26, "SIP Calculator", "म्यूचुअल फंड SIP रिटर्न", "Finance Tools", Icons.Filled.AccountBalanceWallet, Color(0xFF5B21B6)),
    ToolModel(27, "PPF Calculator", "पब्लिक प्रोविडेंट फंड", "Finance Tools", Icons.Filled.Lock, Color(0xFF7C3AED)),
    ToolModel(28, "FD Calculator", "फिक्स्ड डिपॉजिट ब्याज", "Finance Tools", Icons.Filled.Savings, Color(0xFF0284C7)),
    ToolModel(29, "RD Calculator", "रिकरिंग डिपॉजिट कैलकुलेटर", "Finance Tools", Icons.Filled.Autorenew, Color(0xFFDB2777)),
    ToolModel(30, "Inflation Calculator", "महंगाई प्रभाव कैलकुलेटर", "Finance Tools", Icons.Filled.TrendingDown, Color(0xFF16A34A)),

    // Date & Time Tools
    ToolModel(31, "Date Difference", "दो तारीखों के बीच का अंतर", "Date & Time Tools", Icons.Filled.DateRange, Color(0xFF5B21B6)),
    ToolModel(32, "Add/Subtract Days", "तारीख में दिन जोड़ें/घटाएं", "Date & Time Tools", Icons.Filled.Update, Color(0xFF7C3AED)),
    ToolModel(33, "Leap Year Checker", "लीप वर्ष जांच", "Date & Time Tools", Icons.Filled.Event, Color(0xFF0284C7)),
    ToolModel(34, "World Clock", "दुनियाभर का समय", "Date & Time Tools", Icons.Filled.Public, Color(0xFFDB2777)),
    ToolModel(35, "Stopwatch", "स्टॉपवॉच", "Date & Time Tools", Icons.Filled.Timer, Color(0xFF16A34A)),

    // Developer Tools
    ToolModel(36, "QR Code Generator", "टेक्स्ट से QR कोड बनाएं", "Developer Tools", Icons.Filled.QrCode, Color(0xFF5B21B6)),
    ToolModel(37, "JSON Formatter", "JSON वैलिडेटर और प्रीटी प्रिंट", "Developer Tools", Icons.Filled.Code, Color(0xFF7C3AED)),
    ToolModel(38, "Base64 Encoder/Decoder", "बेस64 टेक्स्ट कनवर्टर", "Developer Tools", Icons.Filled.Code, Color(0xFF0284C7)),
    ToolModel(39, "Password Generator", "सुरक्षित पासवर्ड बनाएं", "Developer Tools", Icons.Filled.VpnKey, Color(0xFFDB2777)),
    ToolModel(40, "UUID Generator", "यूनिक आईडी जनरेटर", "Developer Tools", Icons.Filled.Dns, Color(0xFF16A34A)),

    // Image & PDF Tools
    ToolModel(41, "Image Resizer", "छवि का आकार बदलें", "Image Tools", Icons.Filled.Crop, Color(0xFF5B21B6)),
    ToolModel(42, "Image Compressor", "छवि का साइज़ कम करें", "Image Tools", Icons.Filled.Image, Color(0xFF7C3AED)),
    ToolModel(43, "Text to PDF", "टेक्स्ट को PDF में बदलें", "PDF Tools", Icons.Filled.PictureAsPdf, Color(0xFF0284C7)),
    ToolModel(44, "Color Picker", "RGB से HEX कलर कोड", "Image Tools", Icons.Filled.Palette, Color(0xFFDB2777))
)

private val extraTools: List<ToolModel> = List(70) { i ->
    val id = 45 + i
    ToolModel(
        id = id,
        title = "Smart Utility Tool $id",
        subtitle = "उन्नत दैनिक उपयोग यूटिलिटी टूल",
        category = when (i % 5) {
            0 -> "Calculator Tools"
            1 -> "Student Tools"
            2 -> "Text Tools"
            3 -> "Unit Converter"
            else -> "Finance Tools"
        },
        icon = Icons.Filled.Build,
        accent = Color(0xFF5B21B6)
    )
}

val earnZoneTools: List<ToolModel> = baseTools + extraTools

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        val prefs = getSharedPreferences("earnzone", Context.MODE_PRIVATE)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(primary = Purple, secondary = Purple2, background = Bg)
            ) {
                EarnZoneMainApp(prefs)
            }
        }
    }
}

// ---------- Common UI ----------

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
fun FormCard(content: @Composable ColumnScope.() -> Unit) {
    WhiteCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = content
        )
    }
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
                Text(title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
fun ResultBox(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Soft)
            .padding(14.dp)
    ) {
        SelectionContainer {
            Text(text, color = Ink, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ToolRow(tool: ToolModel, onClick: () -> Unit) {
    WhiteCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(tool.accent.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(tool.icon, contentDescription = null, tint = tool.accent)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(tool.title, fontWeight = FontWeight.Bold, color = Ink)
                Text(tool.subtitle, fontSize = 12.sp, color = Muted)
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Muted)
        }
    }
}

// ---------- Helpers ----------

private fun fmt(d: Double): String =
    if (d.isNaN() || d.isInfinite()) "—" else String.format(Locale.US, "%.2f", d)

private fun fmt4(d: Double): String =
    if (d.isNaN() || d.isInfinite()) "—" else String.format(Locale.US, "%.4f", d)

private fun makeCal(d: Int, m: Int, y: Int): Calendar =
    Calendar.getInstance().apply {
        clear()
        set(y, m - 1, d)
    }

private fun daysBetween(a: Calendar, b: Calendar): Long =
    Math.round((b.timeInMillis - a.timeInMillis) / 86400000.0)

@Composable
fun CalcForm(
    labels: List<String>,
    button: String = "गणना करें",
    compute: (List<Double>) -> String
) {
    val values = remember { mutableStateListOf<String>().apply { labels.forEach { _ -> add("") } } }
    var result by remember { mutableStateOf("") }
    FormCard {
        labels.forEachIndexed { i, label ->
            OutlinedTextField(
                value = values[i],
                onValueChange = { values[i] = it },
                label = { Text(label) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
        }
        Button(
            onClick = {
                val nums = values.map { it.toDoubleOrNull() }
                result = if (nums.any { it == null }) {
                    "कृपया सभी मान सही भरें"
                } else {
                    try {
                        compute(nums.map { it ?: 0.0 })
                    } catch (e: Exception) {
                        "गणना में त्रुटि"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text(button) }
        if (result.isNotEmpty()) ResultBox(result)
    }
}

@Composable
fun TextForm(hint: String, transform: (String) -> String) {
    var text by remember { mutableStateOf("") }
    FormCard {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text(hint) },
            minLines = 4,
            modifier = Modifier.fillMaxWidth()
        )
        if (text.isNotEmpty()) ResultBox(transform(text))
    }
}

@Composable
fun Converter(
    units: List<String>,
    toBase: (Int, Double) -> Double,
    fromBase: (Int, Double) -> Double
) {
    var from by remember { mutableStateOf(0) }
    var input by remember { mutableStateOf("") }
    FormCard {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("मान दर्ज करें") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Text("इस इकाई से बदलें:", color = Muted, fontSize = 13.sp)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            units.forEachIndexed { i, u ->
                PillChip(u, from == i) { from = i }
            }
        }
        val x = input.toDoubleOrNull()
        if (x != null) {
            val base = toBase(from, x)
            ResultBox(units.indices.joinToString("\n") { i -> "${units[i]}: ${fmt4(fromBase(i, base))}" })
        }
    }
}

// ---------- Tools ----------

@Composable
fun StopwatchTool() {
    var running by remember { mutableStateOf(false) }
    var elapsed by remember { mutableStateOf(0L) }
    LaunchedEffect(running) {
        while (running) {
            delay(100)
            elapsed += 100
        }
    }
    val totalSec = elapsed / 1000
    val text = String.format(Locale.US, "%02d:%02d.%d", totalSec / 60, totalSec % 60, (elapsed % 1000) / 100)
    FormCard {
        Text(text, fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Purple)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = { running = !running }) { Text(if (running) "रोकें" else "शुरू करें") }
            OutlinedButton(onClick = {
                running = false
                elapsed = 0L
            }) { Text("रीसेट") }
        }
    }
}

@Composable
fun PomodoroTool() {
    var running by remember { mutableStateOf(false) }
    var remaining by remember { mutableStateOf(1500) }
    var isBreak by remember { mutableStateOf(false) }
    LaunchedEffect(running) {
        while (running && remaining > 0) {
            delay(1000)
            remaining -= 1
        }
        if (remaining <= 0) running = false
    }
    val text = String.format(Locale.US, "%02d:%02d", remaining / 60, remaining % 60)
    FormCard {
        Text(if (isBreak) "ब्रेक टाइम" else "फोकस टाइम", color = Muted)
        Text(text, fontSize = 44.sp, fontWeight = FontWeight.Bold, color = Purple)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = { running = !running }) { Text(if (running) "रोकें" else "शुरू करें") }
            OutlinedButton(onClick = {
                running = false
                remaining = if (isBreak) 300 else 1500
            }) { Text("रीसेट") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PillChip("फोकस 25 मिनट", !isBreak) {
                isBreak = false
                running = false
                remaining = 1500
            }
            PillChip("ब्रेक 5 मिनट", isBreak) {
                isBreak = true
                running = false
                remaining = 300
            }
        }
    }
}

@Composable
fun WorldClockTool() {
    var tick by remember { mutableStateOf(0) }
    val zones = listOf(
        "भारत (IST)" to "Asia/Kolkata",
        "लंदन" to "Europe/London",
        "न्यूयॉर्क" to "America/New_York",
        "दुबई" to "Asia/Dubai",
        "टोक्यो" to "Asia/Tokyo",
        "सिडनी" to "Australia/Sydney"
    )
    FormCard {
        val now = Date()
        // tick पढ़ने से रिफ्रेश बटन दबाने पर समय दोबारा बनता है
        val ignore = tick
        zones.forEach { (label, id) ->
            val f = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            f.timeZone = TimeZone.getTimeZone(id)
            Text("$label: ${f.format(now)}", color = Ink)
        }
        Button(onClick = { tick = ignore + 1 }) { Text("रिफ्रेश") }
    }
}

@Composable
fun Base64Tool() {
    var text by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    FormCard {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("टेक्स्ट दर्ज करें") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = {
                result = Base64.encodeToString(text.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
            }) { Text("Encode") }
            OutlinedButton(onClick = {
                result = try {
                    String(Base64.decode(text.trim(), Base64.DEFAULT), Charsets.UTF_8)
                } catch (e: Exception) {
                    "अमान्य Base64 टेक्स्ट"
                }
            }) { Text("Decode") }
        }
        if (result.isNotEmpty()) ResultBox(result)
    }
}

@Composable
fun UuidTool() {
    var uuid by remember { mutableStateOf(UUID.randomUUID().toString()) }
    FormCard {
        ResultBox(uuid)
        Button(onClick = { uuid = UUID.randomUUID().toString() }) { Text("नया UUID बनाएं") }
    }
}

@Composable
fun ComingSoonTool(tool: ToolModel) {
    FormCard {
        Text(tool.title, fontWeight = FontWeight.Bold, color = Ink, fontSize = 18.sp)
        Text("यह टूल जल्द ही उपलब्ध होगा। अपडेट के लिए ऐप को अपडेट रखें।", color = Muted)
    }
}

@Composable
fun ToolContent(tool: ToolModel) {
    when (tool.id) {
        1 -> CalcForm(listOf("पहली संख्या", "दूसरी संख्या")) { v ->
            "जोड़: ${fmt(v[0] + v[1])}\nघटाव: ${fmt(v[0] - v[1])}\nगुणा: ${fmt(v[0] * v[1])}\n" +
                "भाग: ${if (v[1] == 0.0) "—" else fmt(v[0] / v[1])}"
        }
        2 -> CalcForm(listOf("संख्या / कोण (डिग्री)")) { v ->
            val rad = Math.toRadians(v[0])
            "sin: ${fmt4(sin(rad))}\ncos: ${fmt4(cos(rad))}\ntan: ${fmt4(tan(rad))}\n" +
                "log10: ${fmt4(log10(v[0]))}\nln: ${fmt4(ln(v[0]))}\n√: ${fmt4(sqrt(v[0]))}\nवर्ग: ${fmt4(v[0] * v[0])}"
        }
        3 -> CalcForm(listOf("संख्या", "प्रतिशत (%)")) { v ->
            "${fmt(v[1])}% of ${fmt(v[0])} = ${fmt(v[0] * v[1] / 100)}"
        }
        4 -> CalcForm(listOf("जन्म दिन", "जन्म महीना", "जन्म साल")) { v ->
            val now = Calendar.getInstance()
            var y = now.get(Calendar.YEAR) - v[2].toInt()
            var m = now.get(Calendar.MONTH) + 1 - v[1].toInt()
            var d = now.get(Calendar.DAY_OF_MONTH) - v[0].toInt()
            if (d < 0) {
                m -= 1
                d += 30
            }
            if (m < 0) {
                y -= 1
                m += 12
            }
            if (y < 0) "अमान्य तारीख" else "आपकी उम्र: $y वर्ष, $m माह, $d दिन"
        }
        5 -> CalcForm(listOf("राशि", "GST दर (%)")) { v ->
            val gst = v[0] * v[1] / 100
            val base = v[0] * 100 / (100 + v[1])
            "GST जोड़ने पर:\nGST: ${fmt(gst)}\nकुल: ${fmt(v[0] + gst)}\n\n" +
                "GST हटाने पर:\nमूल राशि: ${fmt(base)}\nGST: ${fmt(v[0] - base)}"
        }
        6 -> CalcForm(listOf("लोन राशि", "सालाना ब्याज दर (%)", "अवधि (साल)")) { v ->
            val r = v[1] / 12 / 100
            val n = v[2] * 12
            val emi = if (r == 0.0) v[0] / n else v[0] * r * (1 + r).pow(n) / ((1 + r).pow(n) - 1)
            val total = emi * n
            "मासिक EMI: ${fmt(emi)}\nकुल भुगतान: ${fmt(total)}\nकुल ब्याज: ${fmt(total - v[0])}"
        }
        7 -> CalcForm(listOf("मूलधन", "सालाना दर (%)", "समय (साल)", "साल में कितनी बार जुड़े")) { v ->
            val amt = v[0] * (1 + v[1] / (100 * v[3])).pow(v[3] * v[2])
            "कुल राशि: ${fmt(amt)}\nचक्रवृद्धि ब्याज: ${fmt(amt - v[0])}"
        }
        8 -> CalcForm(listOf("मूलधन", "सालाना दर (%)", "समय (साल)")) { v ->
            val si = v[0] * v[1] * v[2] / 100
            "साधारण ब्याज: ${fmt(si)}\nकुल राशि: ${fmt(v[0] + si)}"
        }
        9 -> CalcForm(listOf("क्रय मूल्य", "विक्रय मूल्य")) { v ->
            val diff = v[1] - v[0]
            if (diff >= 0) "लाभ: ${fmt(diff)}\nलाभ प्रतिशत: ${fmt(diff / v[0] * 100)}%"
            else "हानि: ${fmt(-diff)}\nहानि प्रतिशत: ${fmt(-diff / v[0] * 100)}%"
        }
        10 -> CalcForm(listOf("मूल्य", "छूट (%)")) { v ->
            val save = v[0] * v[1] / 100
            "बचत: ${fmt(save)}\nअंतिम मूल्य: ${fmt(v[0] - save)}"
        }
        11 -> CalcForm(listOf("CGPA")) { v -> "प्रतिशत: ${fmt(v[0] * 9.5)}%" }
        12 -> CalcForm(
            listOf(
                "विषय 1 ग्रेड पॉइंट", "विषय 1 क्रेडिट",
                "विषय 2 ग्रेड पॉइंट", "विषय 2 क्रेडिट",
                "विषय 3 ग्रेड पॉइंट", "विषय 3 क्रेडिट",
                "विषय 4 ग्रेड पॉइंट", "विषय 4 क्रेडिट"
            )
        ) { v ->
            var pts = 0.0
            var cr = 0.0
            for (i in 0 until 4) {
                pts += v[i * 2] * v[i * 2 + 1]
                cr += v[i * 2 + 1]
            }
            if (cr == 0.0) "क्रेडिट शून्य नहीं होने चाहिए" else "GPA: ${fmt(pts / cr)}"
        }
        13 -> PomodoroTool()
        14 -> CalcForm(listOf("परीक्षा दिन", "महीना", "साल")) { v ->
            val days = daysBetween(Calendar.getInstance(), makeCal(v[0].toInt(), v[1].toInt(), v[2].toInt())) + 1
            if (days < 0) "यह तारीख बीत चुकी है" else "परीक्षा में लगभग $days दिन बाकी हैं"
        }
        15 -> CalcForm(listOf("उपस्थित दिन", "कुल दिन")) { v ->
            if (v[1] == 0.0) "कुल दिन शून्य नहीं हो सकते" else {
                val pct = v[0] / v[1] * 100
                val need = ceil((0.75 * v[1] - v[0]) / 0.25).toInt()
                "उपस्थिति: ${fmt(pct)}%\n" +
                    if (pct >= 75) "आप 75% से ऊपर हैं" else "75% के लिए लगातार $need दिन और आना होगा"
            }
        }
        16 -> TextForm("अपना टेक्स्ट लिखें") { t ->
            val words = t.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }.size
            "शब्द: $words\nअक्षर: ${t.length}\nलाइन्स: ${t.lines().size}"
        }
        17 -> TextForm("अपना टेक्स्ट लिखें") { t ->
            "स्पेस के साथ: ${t.length}\nबिना स्पेस: ${t.count { !it.isWhitespace() }}"
        }
        18 -> TextForm("अपना टेक्स्ट लिखें") { t ->
            "UPPERCASE:\n${t.uppercase()}\n\nlowercase:\n${t.lowercase()}\n\nTitle Case:\n" +
                t.split(" ").joinToString(" ") { w -> w.lowercase().replaceFirstChar { c -> c.uppercase() } }
        }
        19 -> TextForm("अपना टेक्स्ट लिखें") { t -> t.reversed() }
        20 -> TextForm("अपना टेक्स्ट लिखें") { t -> t.replace(Regex("\\s+"), " ").trim() }
        21 -> {
            val f = listOf(1.0, 1000.0, 0.01, 0.001, 0.3048, 0.0254, 1609.344)
            Converter(
                listOf("मीटर", "किमी", "सेमी", "मिमी", "फीट", "इंच", "मील"),
                { i, x -> x * f[i] },
                { i, b -> b / f[i] }
            )
        }
        22 -> {
            val f = listOf(1.0, 0.001, 0.45359237, 0.0283495)
            Converter(
                listOf("किग्रा", "ग्राम", "पाउंड", "औंस"),
                { i, x -> x * f[i] },
                { i, b -> b / f[i] }
            )
        }
        23 -> Converter(
            listOf("Celsius", "Fahrenheit", "Kelvin"),
            { i, x -> when (i) { 0 -> x; 1 -> (x - 32) * 5 / 9; else -> x - 273.15 } },
            { i, c -> when (i) { 0 -> c; 1 -> c * 9 / 5 + 32; else -> c + 273.15 } }
        )
        24 -> {
            val f = listOf(1.0, 0.001, 3.78541, 0.2365882)
            Converter(
                listOf("लीटर", "मिली", "गैलन", "कप"),
                { i, x -> x * f[i] },
                { i, b -> b / f[i] }
            )
        }
        25 -> {
            val f = listOf(1.0, 4046.856, 2529.285, 0.092903, 10000.0)
            Converter(
                listOf("वर्ग मीटर", "एकड़", "बीघा (लगभग)", "वर्ग फीट", "हेक्टेयर"),
                { i, x -> x * f[i] },
                { i, b -> b / f[i] }
            )
        }
        26 -> CalcForm(listOf("मासिक निवेश", "सालाना रिटर्न (%)", "अवधि (साल)")) { v ->
            val i = v[1] / 12 / 100
            val n = v[2] * 12
            val fv = if (i == 0.0) v[0] * n else v[0] * (((1 + i).pow(n) - 1) / i) * (1 + i)
            val invested = v[0] * n
            "कुल निवेश: ${fmt(invested)}\nअनुमानित रिटर्न: ${fmt(fv - invested)}\nकुल मूल्य: ${fmt(fv)}"
        }
        27 -> CalcForm(listOf("सालाना जमा", "ब्याज दर (%)", "अवधि (साल)")) { v ->
            val r = v[1] / 100
            val fv = if (r == 0.0) v[0] * v[2] else v[0] * (((1 + r).pow(v[2]) - 1) / r) * (1 + r)
            "कुल जमा: ${fmt(v[0] * v[2])}\nमैच्योरिटी राशि: ${fmt(fv)}"
        }
        28 -> CalcForm(listOf("जमा राशि", "सालाना दर (%)", "अवधि (साल)")) { v ->
            val amt = v[0] * (1 + v[1] / 400).pow(4 * v[2])
            "मैच्योरिटी राशि: ${fmt(amt)}\nकुल ब्याज: ${fmt(amt - v[0])}"
        }
        29 -> CalcForm(listOf("मासिक जमा", "सालाना दर (%)", "अवधि (महीने)")) { v ->
            val n = v[2].toInt()
            var total = 0.0
            for (k in 1..n) {
                total += v[0] * (1 + v[1] / 400).pow(4.0 * (n - k + 1) / 12.0)
            }
            "कुल जमा: ${fmt(v[0] * n)}\nमैच्योरिटी राशि: ${fmt(total)}"
        }
        30 -> CalcForm(listOf("आज की राशि", "महंगाई दर (%)", "अवधि (साल)")) { v ->
            val future = v[0] * (1 + v[1] / 100).pow(v[2])
            "इतनी ही चीज़ों की भविष्य की कीमत: ${fmt(future)}\nआज की राशि की भविष्य में क्रय शक्ति: ${fmt(v[0] / (1 + v[1] / 100).pow(v[2]))}"
        }
        31 -> CalcForm(listOf("पहला दिन", "महीना", "साल", "दूसरा दिन", "महीना", "साल")) { v ->
            val a = makeCal(v[0].toInt(), v[1].toInt(), v[2].toInt())
            val b = makeCal(v[3].toInt(), v[4].toInt(), v[5].toInt())
            val d = daysBetween(a, b)
            "अंतर: $d दिन (लगभग ${d / 7} सप्ताह)"
        }
        32 -> CalcForm(listOf("दिन", "महीना", "साल", "जोड़ने/घटाने के दिन (घटाने के लिए -)")) { v ->
            val c = makeCal(v[0].toInt(), v[1].toInt(), v[2].toInt())
            c.add(Calendar.DAY_OF_MONTH, v[3].toInt())
            "नई तारीख: " + SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(c.time)
        }
        33 -> CalcForm(listOf("साल")) { v ->
            val y = v[0].toInt()
            if (y % 4 == 0 && (y % 100 != 0 || y % 400 == 0)) "$y लीप वर्ष है" else "$y लीप वर्ष नहीं है"
        }
        34 -> WorldClockTool()
        35 -> StopwatchTool()
        37 -> TextForm("JSON यहाँ पेस्ट करें") { t ->
            try {
                val s = t.trim()
                if (s.startsWith("[")) JSONArray(s).toString(2) else JSONObject(s).toString(2)
            } catch (e: Exception) {
                "अमान्य JSON"
            }
        }
        38 -> Base64Tool()
        39 -> CalcForm(listOf("पासवर्ड की लंबाई (4-64)"), "पासवर्ड बनाएं") { v ->
            val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#\$%&*"
            val rnd = SecureRandom()
            val len = v[0].toInt().coerceIn(4, 64)
            (1..len).map { chars[rnd.nextInt(chars.length)] }.joinToString("")
        }
        40 -> UuidTool()
        44 -> CalcForm(listOf("Red (0-255)", "Green (0-255)", "Blue (0-255)")) { v ->
            val r = v[0].toInt().coerceIn(0, 255)
            val g = v[1].toInt().coerceIn(0, 255)
            val b = v[2].toInt().coerceIn(0, 255)
            "HEX: " + String.format(Locale.US, "#%02X%02X%02X", r, g, b) + "\nRGB: ($r, $g, $b)"
        }
        else -> ComingSoonTool(tool)
    }
}

@Composable
fun ToolRunnerScreen(tool: ToolModel, isFav: Boolean, onFav: () -> Unit, onBack: () -> Unit) {
    Column {
        Header(tool.title, tool.subtitle) {
            IconButton(onClick = onFav) {
                Icon(
                    if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = Gold
                )
            }
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
            }
        }
        Body {
            ToolContent(tool)
        }
    }
}

// ---------- Main App ----------

@Composable
fun EarnZoneMainApp(prefs: SharedPreferences) {
    var tab by remember { mutableStateOf(0) }
    var activeTool by remember { mutableStateOf<ToolModel?>(null) }
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    val favs = remember {
        mutableStateListOf<Int>().apply {
            addAll((prefs.getStringSet("fav", emptySet()) ?: emptySet()).mapNotNull { it.toIntOrNull() })
        }
    }

    val categories = listOf(
        "All", "Calculator Tools", "Student Tools", "Text Tools", "Unit Converter",
        "Finance Tools", "Date & Time Tools", "Developer Tools", "Image Tools", "PDF Tools"
    )

    val filteredTools = earnZoneTools.filter { tool ->
        val matchesCat = selectedCategory == "All" || tool.category == selectedCategory
        val matchesSearch = searchQuery.isBlank() ||
            tool.title.contains(searchQuery, ignoreCase = true) ||
            tool.subtitle.contains(searchQuery, ignoreCase = true)
        matchesCat && matchesSearch
    }

    val tabs = listOf(
        "Home" to Icons.Filled.Home,
        "Tools" to Icons.Filled.Build,
        "Study" to Icons.Filled.School,
        "Favorites" to Icons.Filled.Favorite,
        "Profile" to Icons.Filled.Person
    )

    BackHandler(enabled = activeTool != null) { activeTool = null }

    Scaffold(
        containerColor = Bg,
        bottomBar = {
            Column {
                BannerAd()
                NavigationBar(containerColor = Color.White) {
                    tabs.forEachIndexed { i, pair ->
                        NavigationBarItem(
                            selected = tab == i && activeTool == null,
                            onClick = {
                                activeTool = null
                                tab = i
                            },
                            icon = { Icon(pair.second, contentDescription = pair.first) },
                            label = { Text(pair.first, fontSize = 11.sp) },
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
            val current = activeTool
            if (current != null) {
                ToolRunnerScreen(
                    tool = current,
                    isFav = favs.contains(current.id),
                    onFav = {
                        if (favs.contains(current.id)) favs.remove(current.id) else favs.add(current.id)
                        prefs.edit().putStringSet("fav", favs.map { it.toString() }.toSet()).apply()
                    },
                    onBack = { activeTool = null }
                )
            } else {
                when (tab) {
                    0 -> {
                        Header("EarnZone Pro", "110+ Smart Tools & Learning App") {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = Gold)
                        }
                        Body {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("110+ टूल्स में खोजें...") },
                                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = Purple) },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp)
                            )

                            SectionTitle("लोकप्रिय श्रेणियां")
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                categories.forEach { cat ->
                                    PillChip(cat, selectedCategory == cat) {
                                        selectedCategory = cat
                                        tab = 1
                                    }
                                }
                            }

                            SectionTitle("त्वरित टूल्स (Quick Tools)")
                            earnZoneTools.take(6).forEach { tool ->
                                ToolRow(tool) { activeTool = tool }
                            }
                        }
                    }
                    1 -> {
                        Header("Tools Directory", "110+ कार्यशील स्मार्ट टूल्स") {
                            Icon(Icons.Filled.Build, contentDescription = null, tint = Gold)
                        }
                        Body {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("टूल खोजें...") },
                                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = Purple) },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp)
                            )

                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                categories.forEach { cat ->
                                    PillChip(cat, selectedCategory == cat) { selectedCategory = cat }
                                }
                            }

                            SectionTitle("परिणाम (${filteredTools.size})")
                            filteredTools.forEach { tool ->
                                ToolRow(tool) { activeTool = tool }
                            }
                        }
                    }
                    2 -> {
                        Header("Study Zone", "छात्रों के लिए खास टूल्स") {
                            Icon(Icons.Filled.School, contentDescription = null, tint = Gold)
                        }
                        Body {
                            val studyTools = earnZoneTools.filter { it.category == "Student Tools" && it.id <= 44 }
                            studyTools.forEach { tool ->
                                ToolRow(tool) { activeTool = tool }
                            }
                        }
                    }
                    3 -> {
                        Header("Favorites", "आपके पसंदीदा टूल्स") {
                            Icon(Icons.Filled.Favorite, contentDescription = null, tint = Gold)
                        }
                        Body {
                            val favTools = earnZoneTools.filter { favs.contains(it.id) }
                            if (favTools.isEmpty()) {
                                WhiteCard {
                                    Text(
                                        "अभी कोई पसंदीदा टूल नहीं है। किसी टूल को खोलकर ऊपर दिल के आइकन पर टैप करें।",
                                        modifier = Modifier.padding(16.dp),
                                        color = Muted
                                    )
                                }
                            } else {
                                favTools.forEach { tool ->
                                    ToolRow(tool) { activeTool = tool }
                                }
                            }
                        }
                    }
                    else -> {
                        Header("Profile", "EarnZone Pro") {
                            Icon(Icons.Filled.Person, contentDescription = null, tint = Gold)
                        }
                        Body {
                            WhiteCard {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("EarnZone Pro", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Ink)
                                    Text("कुल टूल्स: ${earnZoneTools.size}", color = Muted)
                                    Text("पसंदीदा टूल्स: ${favs.size}", color = Muted)
                                    Text("वर्ज़न 1.0", color = Muted)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
