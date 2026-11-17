========== FILE: settings.gradle.kts ==========
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = java.net.URI("https://jitpack.io") }
    }
}

rootProject.name = "EarnZone"
include(":app")

========== FILE: build.gradle.kts ==========
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}

========== FILE: app/build.gradle.kts ==========
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.earnzone.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.earnzone.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
    implementation("com.google.android.gms:play-services-ads:22.6.0")
    
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

========== FILE: app/src/main/AndroidManifest.xml ==========
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="EarnZone"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.EarnZone">

        <meta-data
            android:name="com.google.android.gms.ads.APPLICATION_ID"
            android:value="ca-app-pub-3940256099942544~3343011703" />

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.EarnZone">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>

========== FILE: app/src/main/java/com/earnzone/app/SmartTools.kt ==========
package com.earnzone.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.pow

private val ToolPurple = Color(0xFF5B21B6)
private val ToolPurple2 = Color(0xFF7C3AED)
private val ToolGold = Color(0xFFFFC928)
private val ToolInk = Color(0xFF17152B)
private val ToolCardWhite = Color(0xFFFFFFFF)
private val ToolMuted = Color(0xFF6B6B7B)
private val ToolSoft = Color(0xFFEDE7FF)

data class SmartToolItem(
    val id: Int,
    val title: String,
    val subtitle: String,
    val category: String,
    val icon: ImageVector,
    val accent: Color
)

val smartToolsList = listOf(
    SmartToolItem(1, "Basic Calculator", "जोड़, घटाव, गुणा और भाग", "Calculators", Icons.Filled.Build, Color(0xFF5B21B6)),
    SmartToolItem(2, "Percentage Calculator", "प्रतिशत और डिस्काउंट निकालें", "Calculators", Icons.Filled.Info, Color(0xFF0284C7)),
    SmartToolItem(3, "Age Calculator", "सटीक उम्र (वर्ष, माह, दिन)", "Calculators", Icons.Filled.DateRange, Color(0xFFDB2777)),
    SmartToolItem(4, "GST Calculator", "GST जोड़ें या हटाएं (5%-28%)", "Calculators", Icons.Filled.ShoppingCart, Color(0xFF16A34A)),
    SmartToolItem(5, "EMI Calculator", "लोन की मासिक किस्त और ब्याज", "Calculators", Icons.Filled.AccountBalance, Color(0xFFEA580C)),
    SmartToolItem(6, "Unit Converter", "लंबाई, वजन, तापमान और आयतन (Volume)", "Converters", Icons.Filled.Refresh, Color(0xFF7C3AED)),
    SmartToolItem(7, "Date Difference Calculator", "दो तारीखों के बीच का अंतर", "Calculators", Icons.Filled.DateRange, Color(0xFF0D9488)),
    SmartToolItem(8, "Word Counter", "शब्द, अक्षर और पढ़ने का समय", "Text Tools", Icons.Filled.List, Color(0xFFCA8A04)),
    SmartToolItem(9, "Text Case Converter", "UPPERCASE, lowercase बदलें", "Text Tools", Icons.Filled.Edit, Color(0xFF475569))
)

private val df = DecimalFormat("#,##0.##")

@Composable
fun ToolDetailScreen(
    toolId: Int,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBack: () -> Unit
) {
    val tool = smartToolsList.firstOrNull { it.id == toolId } ?: return
    Column {
        Header(tool.title, tool.subtitle) {
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) ToolGold else Color.White
                )
            }
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "वापस", tint = Color.White)
            }
        }
        Body {
            when (toolId) {
                1 -> BasicCalculatorUI()
                2 -> PercentageCalculatorUI()
                3 -> AgeCalculatorUI()
                4 -> GstCalculatorUI()
                5 -> EmiCalculatorUI()
                6 -> UnitConverterUI()
                7 -> DateCalculatorUI()
                8 -> WordCounterUI()
                9 -> TextCaseConverterUI()
            }
        }
    }
}

@Composable
fun ErrorBanner(msg: String) {
    if (msg.isNotEmpty()) {
        Text(
            text = "⚠️ $msg",
            color = Color(0xFFDC2626),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun ResultBox(title: String, value: String, extra: String = "") {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ToolSoft)
            .padding(16.dp)
    ) {
        Column {
            Text(title, fontSize = 12.sp, color = ToolPurple, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 20.sp, color = ToolInk, fontWeight = FontWeight.Bold)
            if (extra.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(extra, fontSize = 13.sp, color = ToolMuted)
            }
        }
    }
}

@Composable
fun BasicCalculatorUI() {
    var display by remember { mutableStateOf("0") }
    var firstOperand by remember { mutableStateOf<Double?>(null) }
    var pendingOp by remember { mutableStateOf("") }
    var clearNext by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    fun calculate(a: Double, b: Double, op: String): Double? {
        return when (op) {
            "+" -> a + b
            "-" -> a - b
            "×" -> a * b
            "÷" -> if (b == 0.0) null else a / b
            else -> b
        }
    }

    fun onButtonPress(label: String) {
        errorMsg = ""
        when (label) {
            "C" -> {
                display = "0"
                firstOperand = null
                pendingOp = ""
                clearNext = false
            }
            "DEL" -> {
                display = if (display.length > 1) display.dropLast(1) else "0"
            }
            "+", "-", "×", "÷" -> {
                val current = display.toDoubleOrNull() ?: 0.0
                if (firstOperand != null && pendingOp.isNotEmpty() && !clearNext) {
                    val res = calculate(firstOperand!!, current, pendingOp)
                    if (res == null) {
                        errorMsg = "शून्य (0) से भाग नहीं दिया जा सकता।"
                        display = "0"
                        firstOperand = null
                        pendingOp = ""
                        return
                    }
                    firstOperand = res
                    display = df.format(res).replace(",", "")
                } else {
                    firstOperand = current
                }
                pendingOp = label
                clearNext = true
            }
            "=" -> {
                val current = display.toDoubleOrNull() ?: return
                if (firstOperand != null && pendingOp.isNotEmpty()) {
                    val res = calculate(firstOperand!!, current, pendingOp)
                    if (res == null) {
                        errorMsg = "शून्य (0) से भाग नहीं दिया जा सकता।"
                        display = "0"
                    } else {
                        display = df.format(res).replace(",", "")
                    }
                    firstOperand = null
                    pendingOp = ""
                    clearNext = true
                }
            }
            "." -> {
                if (clearNext) {
                    display = "0."
                    clearNext = false
                } else if (!display.contains(".")) {
                    display += "."
                }
            }
            else -> {
                if (clearNext || display == "0") {
                    display = label
                    clearNext = false
                } else if (display.length < 15) {
                    display += label
                }
            }
        }
    }

    WhiteCard {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ErrorBanner(errorMsg)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ToolSoft)
                    .padding(16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    if (pendingOp.isNotEmpty()) {
                        Text("${firstOperand ?: ""} $pendingOp", fontSize = 14.sp, color = ToolMuted)
                    }
                    Text(display, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = ToolInk)
                }
            }

            val rows = listOf(
                listOf("C", "DEL", "÷", "×"),
                listOf("7", "8", "9", "-"),
                listOf("4", "5", "6", "+"),
                listOf("1", "2", "3", "="),
                listOf("0", ".")
            )

            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { btn ->
                        Button(
                            onClick = { onButtonPress(btn) },
                            modifier = Modifier
                                .weight(if (btn == "0") 2f else 1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when (btn) {
                                    "=" -> ToolGold
                                    "C" -> Color(0xFFDC2626)
                                    in listOf("+", "-", "×", "÷", "DEL") -> ToolPurple
                                    else -> ToolSoft
                                },
                                contentColor = when (btn) {
                                    "=" -> ToolPurple
                                    "C", "+", "-", "×", "÷", "DEL" -> Color.White
                                    else -> ToolInk
                                }
                            )
                        ) {
                            Text(btn, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PercentageCalculatorUI() {
    var mode by remember { mutableStateOf(0) }
    var val1 by remember { mutableStateOf("") }
    var val2 by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    WhiteCard {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PillChip("कुल का % निकालें", mode == 0) { mode = 0; result = ""; errorMsg = "" }
                PillChip("कितने % हुआ?", mode == 1) { mode = 1; result = ""; errorMsg = "" }
            }
            OutlinedTextField(
                value = val1,
                onValueChange = { val1 = it },
                label = { Text(if (mode == 0) "प्रतिशत (%)" else "प्राप्तांक / हिस्सा") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = val2,
                onValueChange = { val2 = it },
                label = { Text(if (mode == 0) "कुल राशि / संख्या" else "कुल पूर्णांक / राशि") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            ErrorBanner(errorMsg)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val a = val1.toDoubleOrNull()
                        val b = val2.toDoubleOrNull()
                        if (a == null || b == null) {
                            errorMsg = "कृपया दोनों खानों में सही संख्या दर्ज करें।"
                            result = ""
                        } else if (mode == 1 && b == 0.0) {
                            errorMsg = "कुल संख्या शून्य (0) नहीं हो सकती।"
                            result = ""
                        } else {
                            errorMsg = ""
                            result = if (mode == 0) {
                                val ans = (a / 100.0) * b
                                "${df.format(b)} का ${df.format(a)}% = ${df.format(ans)}"
                            } else {
                                val ans = (a / b) * 100.0
                                "${df.format(ans)}%"
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = ToolPurple)
                ) {
                    Text("गणना करें", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(onClick = { val1 = ""; val2 = ""; result = ""; errorMsg = "" }) {
                    Text("Reset")
                }
            }
            if (result.isNotEmpty()) {
                ResultBox("परिणाम (Result)", result)
            }
        }
    }
}

@Composable
fun AgeCalculatorUI() {
    var dobText by remember { mutableStateOf("") }
    var ageResult by remember { mutableStateOf("") }
    var extraInfo by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    WhiteCard {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("जन्मतिथि दर्ज करें (DD-MM-YYYY)", fontWeight = FontWeight.Bold, color = ToolInk)
            OutlinedTextField(
                value = dobText,
                onValueChange = { dobText = it },
                placeholder = { Text("उदाहरण: 15-08-1998") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            ErrorBanner(errorMsg)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val parts = dobText.trim().split("-", "/", ".")
                        if (parts.size != 3) {
                            errorMsg = "कृपया तारीख DD-MM-YYYY प्रारूप में लिखें (जैसे 15-08-1998)।"
                            ageResult = ""
                            return@Button
                        }
                        val d = parts[0].toIntOrNull()
                        val m = parts[1].toIntOrNull()
                        val y = parts[2].toIntOrNull()
                        if (d == null || m == null || y == null || d !in 1..31 || m !in 1..12 || y < 1900) {
                            errorMsg = "अमान्य जन्मतिथि। कृपया सही दिन, माह और वर्ष दर्ज करें।"
                            ageResult = ""
                            return@Button
                        }
                        val dob = Calendar.getInstance().apply {
                            isLenient = false
                            set(y, m - 1, d, 0, 0, 0)
                        }
                        val today = Calendar.getInstance()
                        try {
                            if (dob.after(today)) {
                                errorMsg = "जन्मतिथि आज की तारीख से आगे नहीं हो सकती।"
                                ageResult = ""
                                return@Button
                            }
                            var years = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
                            var months = today.get(Calendar.MONTH) - dob.get(Calendar.MONTH)
                            var days = today.get(Calendar.DAY_OF_MONTH) - dob.get(Calendar.DAY_OF_MONTH)

                            if (days < 0) {
                                months -= 1
                                val prevMonth = today.clone() as Calendar
                                prevMonth.add(Calendar.MONTH, -1)
                                days += prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
                            }
                            if (months < 0) {
                                years -= 1
                                months += 12
                            }
                            val diffMillis = today.timeInMillis - dob.timeInMillis
                            val totalDays = TimeUnit.MILLISECONDS.toDays(diffMillis)

                            errorMsg = ""
                            ageResult = "$years वर्ष, $months महीने, $days दिन"
                            extraInfo = "कुल जीवित दिन: ${df.format(totalDays)} दिन"
                        } catch (e: Exception) {
                            errorMsg = "यह तारीख कैलेंडर में मौजूद नहीं है।"
                            ageResult = ""
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = ToolPurple)
                ) {
                    Text("उम्र निकालें", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(onClick = { dobText = ""; ageResult = ""; extraInfo = ""; errorMsg = "" }) {
                    Text("Reset")
                }
            }
            if (ageResult.isNotEmpty()) {
                ResultBox("आपकी सटीक उम्र", ageResult, extraInfo)
            }
        }
    }
}

@Composable
fun GstCalculatorUI() {
    var amount by remember { mutableStateOf("") }
    var selectedRate by remember { mutableStateOf(18.0) }
    var isAddGst by remember { mutableStateOf(true) }
    var resultMain by remember { mutableStateOf("") }
    var resultDetail by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    val rates = listOf(5.0, 12.0, 18.0, 28.0)

    WhiteCard {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PillChip("+ GST जोड़ें", isAddGst) { isAddGst = true }
                PillChip("- GST हटाएं", !isAddGst) { isAddGst = false }
            }
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("मूल राशि (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Text("GST दर चुनें:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ToolInk)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rates.forEach { r ->
                    PillChip("${r.toInt()}%", selectedRate == r) { selectedRate = r }
                }
            }
            ErrorBanner(errorMsg)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val amt = amount.toDoubleOrNull()
                        if (amt == null || amt <= 0) {
                            errorMsg = "कृपया शून्य से बड़ी सही राशि दर्ज करें।"
                            resultMain = ""
                        } else {
                            errorMsg = ""
                            if (isAddGst) {
                                val gstAmt = amt * (selectedRate / 100.0)
                                val netAmt = amt + gstAmt
                                resultMain = "कुल राशि: ₹${df.format(netAmt)}"
                                resultDetail = "GST (${selectedRate.toInt()}%): ₹${df.format(gstAmt)} (CGST: ₹${df.format(gstAmt / 2)} + SGST: ₹${df.format(gstAmt / 2)})"
                            } else {
                                val baseAmt = amt / (1.0 + (selectedRate / 100.0))
                                val gstAmt = amt - baseAmt
                                resultMain = "बिना GST मूल राशि: ₹${df.format(baseAmt)}"
                                resultDetail = "शामिल GST (${selectedRate.toInt()}%): ₹${df.format(gstAmt)}"
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = ToolPurple)
                ) {
                    Text("GST गणना करें", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(onClick = { amount = ""; resultMain = ""; resultDetail = ""; errorMsg = "" }) {
                    Text("Reset")
                }
            }
            if (resultMain.isNotEmpty()) {
                ResultBox("GST विवरण", resultMain, resultDetail)
            }
        }
    }
}

@Composable
fun EmiCalculatorUI() {
    var principal by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf("") }
    var months by remember { mutableStateOf("") }
    var emiMain by remember { mutableStateOf("") }
    var emiDetail by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    WhiteCard {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = principal,
                onValueChange = { principal = it },
                label = { Text("लोन की राशि (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = rate,
                onValueChange = { rate = it },
                label = { Text("वार्षिक ब्याज दर (% प्रति वर्ष)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = months,
                onValueChange = { months = it },
                label = { Text("अवधि (महीनों में)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            ErrorBanner(errorMsg)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val p = principal.toDoubleOrNull()
                        val r = rate.toDoubleOrNull()
                        val n = months.toIntOrNull()
                        if (p == null || p <= 0 || r == null || r < 0 || n == null || n <= 0) {
                            errorMsg = "कृपया सही लोन राशि, ब्याज दर और महीने दर्ज करें।"
                            emiMain = ""
                        } else {
                            errorMsg = ""
                            val emi = if (r == 0.0) {
                                p / n
                            } else {
                                val monthlyRate = r / 12.0 / 100.0
                                val factor = (1 + monthlyRate).pow(n)
                                (p * monthlyRate * factor) / (factor - 1)
                            }
                            val totalPayment = emi * n
                            val totalInterest = totalPayment - p
                            emiMain = "मासिक EMI: ₹${df.format(emi)}"
                            emiDetail = "कुल ब्याज: ₹${df.format(totalInterest)} | कुल भुगतान: ₹${df.format(totalPayment)}"
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = ToolPurple)
                ) {
                    Text("EMI निकालें", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(onClick = { principal = ""; rate = ""; months = ""; emiMain = ""; emiDetail = ""; errorMsg = "" }) {
                    Text("Reset")
                }
            }
            if (emiMain.isNotEmpty()) {
                ResultBox("लोन EMI सारांश", emiMain, emiDetail)
            }
        }
    }
}

@Composable
fun UnitConverterUI() {
    var category by remember { mutableStateOf(0) }
    var inputVal by remember { mutableStateOf("") }
    var outputVal by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    val cats = listOf(
        "लंबाई (Km ↔ Miles)",
        "वजन (Kg ↔ Pounds)",
        "तापमान (°C ↔ °F)",
        "आयतन / Volume (Liter ↔ Gallon)"
    )

    WhiteCard {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                cats.forEachIndexed { idx, name ->
                    PillChip(name, category == idx) { category = idx; outputVal = ""; errorMsg = "" }
                }
            }
            OutlinedTextField(
                value = inputVal,
                onValueChange = { inputVal = it },
                label = {
                    Text(
                        when (category) {
                            0 -> "मान दर्ज करें (Km या Miles)"
                            1 -> "मान दर्ज करें (Kg या Pounds)"
                            2 -> "तापमान दर्ज करें (°C या °F)"
                            else -> "आयतन दर्ज करें (Liters या US Gallons)"
                        }
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            ErrorBanner(errorMsg)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val v = inputVal.toDoubleOrNull()
                        if (v == null) {
                            errorMsg = "कृपया सही संख्यात्मक मान दर्ज करें।"
                            outputVal = ""
                        } else {
                            errorMsg = ""
                            outputVal = when (category) {
                                0 -> "${df.format(v)} Km = ${df.format(v * 0.621371)} Miles\n${df.format(v)} Miles = ${df.format(v * 1.60934)} Km"
                                1 -> "${df.format(v)} Kg = ${df.format(v * 2.20462)} Pounds\n${df.format(v)} Pounds = ${df.format(v * 0.453592)} Kg"
                                2 -> "${df.format(v)} °C = ${df.format((v * 9 / 5) + 32)} °F\n${df.format(v)} °F = ${df.format((v - 32) * 5 / 9)} °C"
                                else -> "${df.format(v)} Liters = ${df.format(v * 0.264172)} US Gallons (${df.format(v * 1000)} ml)\n${df.format(v)} US Gallons = ${df.format(v * 3.78541)} Liters"
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = ToolPurple)
                ) {
                    Text("कन्वर्ट करें", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(onClick = { inputVal = ""; outputVal = ""; errorMsg = "" }) {
                    Text("Reset")
                }
            }
            if (outputVal.isNotEmpty()) {
                ResultBox("रूपांतरण परिणाम", outputVal)
            }
        }
    }
}

@Composable
fun DateCalculatorUI() {
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var diffResult by remember { mutableStateOf("") }
    var diffExtra by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    WhiteCard {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = startDate,
                onValueChange = { startDate = it },
                label = { Text("प्रारंभ तारीख (DD-MM-YYYY)") },
                placeholder = { Text("01-01-2026") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = endDate,
                onValueChange = { endDate = it },
                label = { Text("अंतिम तारीख (DD-MM-YYYY)") },
                placeholder = { Text("17-11-2026") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            ErrorBanner(errorMsg)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).apply { isLenient = false }
                        try {
                            val d1 = sdf.parse(startDate.trim())
                            val d2 = sdf.parse(endDate.trim())
                            if (d1 == null || d2 == null) {
                                errorMsg = "कृपया दोनों तारीखें DD-MM-YYYY प्रारूप में लिखें।"
                                diffResult = ""
                                return@Button
                            }
                            val diffMillis = kotlin.math.abs(d2.time - d1.time)
                            val totalDays = TimeUnit.MILLISECONDS.toDays(diffMillis)
                            val weeks = totalDays / 7
                            val remDays = totalDays % 7
                            errorMsg = ""
                            diffResult = "$totalDays दिन"
                            diffExtra = "$weeks सप्ताह और $remDays दिन"
                        } catch (e: Exception) {
                            errorMsg = "अमान्य तारीख! कृपया सही DD-MM-YYYY प्रारूप का उपयोग करें।"
                            diffResult = ""
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = ToolPurple)
                ) {
                    Text("अंतर निकालें", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(onClick = { startDate = ""; endDate = ""; diffResult = ""; diffExtra = ""; errorMsg = "" }) {
                    Text("Reset")
                }
            }
            if (diffResult.isNotEmpty()) {
                ResultBox("दोनों तारीखों का अंतर", diffResult, diffExtra)
            }
        }
    }
}

@Composable
fun WordCounterUI() {
    var text by remember { mutableStateOf("") }
    val trimmed = text.trim()
    val words = if (trimmed.isEmpty()) 0 else trimmed.split(Regex("\\s+")).size
    val charsWithSpaces = text.length
    val charsNoSpaces = text.count { !it.isWhitespace() }
    val lines = if (text.isEmpty()) 0 else text.lines().size
    val readTimeSec = (words * 60) / 200

    WhiteCard {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("यहाँ अपना टेक्स्ट लिखें या पेस्ट करें") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )
            OutlinedButton(onClick = { text = "" }, enabled = text.isNotEmpty()) {
                Text("साफ़ करें (Reset)")
            }
            ResultBox(
                "टेक्स्ट विश्लेषण",
                "$words शब्द (Words)",
                "अक्षर (Spaces सहित): $charsWithSpaces | बिना Space: $charsNoSpaces\nलाइनें: $lines | पढ़ने का समय: ~${readTimeSec} सेकंड"
            )
        }
    }
}

@Composable
fun TextCaseConverterUI() {
    var inputText by remember { mutableStateOf("") }
    var convertedText by remember { mutableStateOf("") }
    val clipboard = LocalClipboardManager.current
    var copiedMsg by remember { mutableStateOf("") }

    WhiteCard {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it; copiedMsg = "" },
                label = { Text("टेक्स्ट दर्ज करें (English / Hindi)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { convertedText = inputText.uppercase(Locale.getDefault()) },
                    colors = ButtonDefaults.buttonColors(containerColor = ToolPurple)
                ) { Text("UPPERCASE") }

                Button(
                    onClick = { convertedText = inputText.lowercase(Locale.getDefault()) },
                    colors = ButtonDefaults.buttonColors(containerColor = ToolPurple2)
                ) { Text("lowercase") }

                Button(
                    onClick = {
                        convertedText = inputText.lowercase(Locale.getDefault())
                            .split(" ")
                            .joinToString(" ") { w -> w.replaceFirstChar { it.titlecase(Locale.getDefault()) } }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ToolGold, contentColor = ToolPurple)
                ) { Text("Title Case", fontWeight = FontWeight.Bold) }

                OutlinedButton(onClick = { inputText = ""; convertedText = ""; copiedMsg = "" }) {
                    Text("Reset")
                }
            }
            if (convertedText.isNotEmpty()) {
                ResultBox("परिवर्तित टेक्स्ट", convertedText)
                Button(
                    onClick = {
                        clipboard.setText(AnnotatedString(convertedText))
                        copiedMsg = "क्लिपबोर्ड पर कॉपी हो गया!"
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("कॉपी करें (Copy)")
                }
                if (copiedMsg.isNotEmpty()) {
                    Text(copiedMsg, color = Color(0xFF16A34A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

========== FILE: app/src/main/java/com/earnzone/app/LearningModule.kt ==========
package com.earnzone.app

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

private val LearnPurple = Color(0xFF5B21B6)
private val LearnGold = Color(0xFFFFC928)
private val LearnInk = Color(0xFF17152B)
private val LearnCardWhite = Color(0xFFFFFFFF)
private val LearnMuted = Color(0xFF6B6B7B)
private val LearnSoft = Color(0xFFEDE7FF)

data class QuizQuestion(
    val subject: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

val curatedQuizBank = listOf(
    QuizQuestion("General Knowledge", "भारत का राष्ट्रीय पशु कौन सा है?", listOf("शेर", "बाघ (Royal Bengal Tiger)", "हाथी", "मोर"), 1, "बाघ (Panthera tigris) भारत का राष्ट्रीय पशु है।"),
    QuizQuestion("General Knowledge", "भारत का संविधान कब लागू हुआ था?", listOf("15 अगस्त 1947", "26 जनवरी 1950", "26 नवंबर 1949", "2 अक्टूबर 1950"), 1, "भारतीय संविधान 26 जनवरी 1950 को पूर्ण रूप से लागू हुआ।"),
    QuizQuestion("Mathematics", "यदि 15 × 12 = X है, तो X का मान क्या होगा?", listOf("160", "170", "180", "190"), 2, "15 × 12 = 180 होता है।"),
    QuizQuestion("Mathematics", "एक समकोण त्रिभुज में सबसे बड़ी भुजा क्या कहलाती है?", listOf("आधार", "लंब", "कर्ण (Hypotenuse)", "परिधि"), 2, "समकोण के सामने वाली सबसे बड़ी भुजा को कर्ण कहते हैं।"),
    QuizQuestion("Science", "पौधे प्रकाश संश्लेषण (Photosynthesis) में किस गैस का उपयोग करते हैं?", listOf("ऑक्सीजन", "कार्बन डाइऑक्साइड", "नाइट्रोजन", "हीलियम"), 1, "पौधे सूर्य के प्रकाश में कार्बन डाइऑक्साइड और जल से भोजन बनाते हैं।"),
    QuizQuestion("Science", "मनुष्य के शरीर की सबसे बड़ी ग्रंथि कौन सी है?", listOf("अग्न्याशय", "यकृत (Liver)", "पीयूष ग्रंथि", "थायरॉयड"), 1, "यकृत (Liver) मानव शरीर की सबसे बड़ी ग्रंथि है।"),
    QuizQuestion("History", "पानीपत का प्रथम युद्ध किस वर्ष लड़ा गया था?", listOf("1526", "1556", "1761", "1857"), 0, "पानीपत का प्रथम युद्ध 1526 में बाबर और इब्राहिम लोदी के बीच हुआ था।"),
    QuizQuestion("History", "अशोक किस राजवंश के प्रसिद्ध सम्राट थे?", listOf("गुप्त वंश", "मौर्य वंश", "मुगल वंश", "चोल वंश"), 1, "सम्राट अशोक मौर्य राजवंश के महान शासक थे।"),
    QuizQuestion("Geography", "भारत की सबसे लंबी नदी कौन सी है?", listOf("यमुना", "गोदावरी", "गंगा", "ब्रह्मपुत्र"), 2, "गंगा नदी भारत में बहने वाली सबसे लंबी नदी है (लगभग 2525 किमी)।"),
    QuizQuestion("Geography", "कर्क रेखा (Tropic of Cancer) भारत के कितने राज्यों से होकर गुजरती है?", listOf("6", "7", "8", "9"), 2, "कर्क रेखा भारत के 8 राज्यों से गुजरती है।"),
    QuizQuestion("Computer Knowledge", "CPU का पूर्ण रूप क्या है?", listOf("Central Process Unit", "Central Processing Unit", "Computer Personal Unit", "Central Power Unit"), 1, "CPU का अर्थ Central Processing Unit होता है।"),
    QuizQuestion("Computer Knowledge", "1 बाइट (Byte) में कितने बिट्स (Bits) होते हैं?", listOf("4", "8", "16", "32"), 1, "1 Byte = 8 Bits होता है।"),
    QuizQuestion("Hindi", "'कमल' शब्द का पर्यायवाची निम्न में से कौन सा है?", listOf("जलज", "अनल", "पवन", "व्योम"), 0, "जलज, पंकज, नीरज आदि कमल के पर्यायवाची हैं।"),
    QuizQuestion("Hindi", "'जो कभी न मरे' वाक्यांश के लिए एक शब्द क्या है?", listOf("अजर", "अमर", "अटल", "अगम"), 1, "जो कभी न मरे उसे 'अमर' कहते हैं।"),
    QuizQuestion("English", "Choose the correct synonym of 'Brave':", listOf("Coward", "Courageous", "Weak", "Timid"), 1, "'Courageous' means brave or fearless."),
    QuizQuestion("English", "Identify the plural form of 'Child':", listOf("Childs", "Childrens", "Children", "Childes"), 2, "The correct irregular plural of Child is Children.")
)

val studySubjects = listOf(
    "General Knowledge" to Icons.Filled.Public,
    "Mathematics" to Icons.Filled.Calculate,
    "Science" to Icons.Filled.Science,
    "History" to Icons.Filled.AccountBalance,
    "Geography" to Icons.Filled.Place,
    "Computer Knowledge" to Icons.Filled.Computer,
    "Hindi" to Icons.Filled.MenuBook,
    "English" to Icons.Filled.Spellcheck
)

@Composable
fun StudyAndGamesScreen(
    prefs: SharedPreferences,
    initialMode: String = "MENU"
) {
    var currentView by remember { mutableStateOf(initialMode) }
    var selectedSubject by remember { mutableStateOf("") }
    var selectedGame by remember { mutableStateOf("") }

    when {
        selectedSubject.isNotEmpty() -> {
            QuizRunnerScreen(
                subject = selectedSubject,
                prefs = prefs,
                onBack = { selectedSubject = "" }
            )
        }
        currentView == "DAILY" -> {
            DailyChallengeScreen(prefs = prefs, onBack = { currentView = "MENU" })
        }
        selectedGame.isNotEmpty() -> {
            BrainGameDetailScreen(
                gameName = selectedGame,
                prefs = prefs,
                onBack = { selectedGame = "" }
            )
        }
        currentView == "GAMES" -> {
            BrainGamesMenuScreen(
                prefs = prefs,
                onSelectGame = { selectedGame = it },
                onBack = { currentView = "MENU" }
            )
        }
        else -> {
            Column {
                Header("Study & Learning Zone", "ऑफलाइन क्विज़, डेली चैलेंज और ब्रेन गेम्स") {
                    Icon(Icons.Filled.School, null, modifier = Modifier.size(28.dp), tint = LearnGold)
                }
                Body {
                    WhiteCard {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { currentView = "DAILY" }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(LearnGold),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Star, null, tint = LearnPurple)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Daily Challenge (आज का प्रश्न)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LearnInk)
                                val dailyScore = prefs.getInt("daily_total_score", 0)
                                Text("कुल डेली स्कोर: $dailyScore अंक • रोज़ नया सवाल", fontSize = 12.sp, color = LearnMuted)
                            }
                            Icon(Icons.Filled.ChevronRight, null, tint = LearnPurple)
                        }
                    }

                    WhiteCard {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { currentView = "GAMES" }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(LearnSoft),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Extension, null, tint = LearnPurple)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Brain Games (4 दिमागी खेल)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LearnInk)
                                Text("Math Puzzle, Memory, Number Guess & Word Puzzle", fontSize = 12.sp, color = LearnMuted)
                            }
                            Icon(Icons.Filled.ChevronRight, null, tint = LearnPurple)
                        }
                    }

                    SectionTitle("विषय चुनें (Offline Quizzes)")
                    studySubjects.chunked(2).forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            pair.forEach { (subj, icon) ->
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(LearnCardWhite)
                                        .clickable { selectedSubject = subj }
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(LearnSoft),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(icon, null, tint = LearnPurple)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(subj, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LearnInk, textAlign = TextAlign.Center)
                                    val best = prefs.getInt("quiz_best_$subj", 0)
                                    Text("Best: $best", fontSize = 11.sp, color = LearnMuted)
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
fun QuizRunnerScreen(subject: String, prefs: SharedPreferences, onBack: () -> Unit) {
    val questions = remember(subject) { curatedQuizBank.filter { it.subject == subject } }
    var currentIndex by remember { mutableStateOf(0) }
    var selectedOption by remember { mutableStateOf(-1) }
    var score by remember { mutableStateOf(0) }
    var finished by remember { mutableStateOf(false) }

    Column {
        Header(subject, "प्रश्न ${currentIndex + 1} / ${questions.size}") {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.Close, contentDescription = "बंद करें", tint = Color.White)
            }
        }
        Body {
            if (questions.isEmpty()) {
                EmptyState(Icons.Filled.Info, "कोई प्रश्न उपलब्ध नहीं", "इस विषय के प्रश्न जल्द जोड़े जाएंगे।")
            } else if (finished) {
                val prevBest = prefs.getInt("quiz_best_$subject", 0)
                if (score > prevBest) {
                    prefs.edit().putInt("quiz_best_$subject", score).apply()
                }
                WhiteCard {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("🎉 क्विज़ पूरा हुआ!", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = LearnPurple)
                        Text("आपका स्कोर: $score / ${questions.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = LearnInk)
                        Button(
                            onClick = {
                                currentIndex = 0
                                selectedOption = -1
                                score = 0
                                finished = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LearnPurple)
                        ) {
                            Text("पुनः प्रारंभ करें (Restart Quiz)")
                        }
                        OutlinedButton(onClick = onBack) {
                            Text("विषय सूची पर लौटें")
                        }
                    }
                }
            } else {
                val q = questions[currentIndex]
                WhiteCard {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(q.question, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = LearnInk)
                        q.options.forEachIndexed { idx, opt ->
                            val bgColor = when {
                                selectedOption == -1 -> LearnSoft
                                idx == q.correctIndex -> Color(0xFFDCFCE7)
                                idx == selectedOption -> Color(0xFFFEE2E2)
                                else -> LearnSoft
                            }
                            val borderColor = when {
                                selectedOption != -1 && idx == q.correctIndex -> Color(0xFF16A34A)
                                selectedOption == idx -> Color(0xFFDC2626)
                                else -> Color.Transparent
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(bgColor)
                                    .clickable(enabled = selectedOption == -1) {
                                        selectedOption = idx
                                        if (idx == q.correctIndex) score++
                                    }
                                    .padding(14.dp)
                            ) {
                                Text(
                                    opt,
                                    color = if (borderColor != Color.Transparent) borderColor else LearnInk,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        if (selectedOption != -1) {
                            ResultBox(
                                if (selectedOption == q.correctIndex) "✅ सही उत्तर!" else "❌ गलत उत्तर",
                                q.explanation
                            )
                            Button(
                                onClick = {
                                    if (currentIndex + 1 < questions.size) {
                                        currentIndex++
                                        selectedOption = -1
                                    } else {
                                        finished = true
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = LearnPurple)
                            ) {
                                Text(if (currentIndex + 1 < questions.size) "अगला प्रश्न →" else "परिणाम देखें")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DailyChallengeScreen(prefs: SharedPreferences, onBack: () -> Unit) {
    val todayKey = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())
    val dayIndex = kotlin.math.abs(todayKey.hashCode()) % curatedQuizBank.size
    val todayQuestion = curatedQuizBank[dayIndex]

    var alreadyCompleted by remember { mutableStateOf(prefs.getBoolean("daily_done_$todayKey", false)) }
    var selectedIdx by remember { mutableStateOf(prefs.getInt("daily_choice_$todayKey", -1)) }
    var totalScore by remember { mutableStateOf(prefs.getInt("daily_total_score", 0)) }

    Column {
        Header("Daily Challenge", "तारीख: $todayKey") {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "वापस", tint = Color.White)
            }
        }
        Body {
            WhiteCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("विषय: ${todayQuestion.subject}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LearnPurple)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("कुल स्कोर: $totalScore", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LearnInk)
                    }
                    Text(todayQuestion.question, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = LearnInk)

                    todayQuestion.options.forEachIndexed { idx, opt ->
                        val bg = when {
                            !alreadyCompleted -> LearnSoft
                            idx == todayQuestion.correctIndex -> Color(0xFFDCFCE7)
                            idx == selectedIdx -> Color(0xFFFEE2E2)
                            else -> LearnSoft
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(bg)
                                .clickable(enabled = !alreadyCompleted) {
                                    selectedIdx = idx
                                    alreadyCompleted = true
                                    val earned = if (idx == todayQuestion.correctIndex) 10 else 0
                                    totalScore += earned
                                    prefs.edit()
                                        .putBoolean("daily_done_$todayKey", true)
                                        .putInt("daily_choice_$todayKey", idx)
                                        .putInt("daily_total_score", totalScore)
                                        .apply()
                                }
                                .padding(14.dp)
                        ) {
                            Text(opt, color = LearnInk, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    if (alreadyCompleted) {
                        ResultBox(
                            if (selectedIdx == todayQuestion.correctIndex) "🎉 शानदार! +10 अंक अर्जित" else "आज का उत्तर दर्ज हो गया",
                            todayQuestion.explanation,
                            "कल फिर नए प्रश्न के लिए वापस आएं!"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BrainGamesMenuScreen(prefs: SharedPreferences, onSelectGame: (String) -> Unit, onBack: () -> Unit) {
    val games = listOf(
        Triple("Math Puzzle", "तेज़ गणितीय पहेलियाँ हल करें", Icons.Filled.Calculate),
        Triple("Memory Game", "नंबर सीक्वेंस याद रखकर दोहराएं", Icons.Filled.Visibility),
        Triple("Number Guessing Game", "1 से 100 के बीच छिपा नंबर पहचानें", Icons.Filled.Help),
        Triple("Word Puzzle", "उलझे हुए अक्षरों से सही शब्द बनाएं", Icons.Filled.Spellcheck)
    )

    Column {
        Header("Brain Games", "दिमागी कसरत और मनोरंजन") {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "वापस", tint = Color.White)
            }
        }
        Body {
            games.forEach { (name, desc, icon) ->
                WhiteCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectGame(name) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(LearnSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, null, tint = LearnPurple)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LearnInk)
                            Text(desc, fontSize = 12.sp, color = LearnMuted)
                            val high = prefs.getInt("game_score_$name", 0)
                            Text("उच्चतम स्कोर: $high", fontSize = 11.sp, color = LearnPurple, fontWeight = FontWeight.Bold)
                        }
                        Icon(Icons.Filled.PlayArrow, null, tint = LearnPurple)
                    }
                }
            }
        }
    }
}

@Composable
fun BrainGameDetailScreen(gameName: String, prefs: SharedPreferences, onBack: () -> Unit) {
    Column {
        Header(gameName, "Brain Training") {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "वापस", tint = Color.White)
            }
        }
        Body {
            when (gameName) {
                "Math Puzzle" -> MathPuzzleGame(prefs)
                "Memory Game" -> MemorySequenceGame(prefs)
                "Number Guessing Game" -> NumberGuessingGame(prefs)
                "Word Puzzle" -> WordScrambleGame(prefs)
            }
        }
    }
}

@Composable
fun MathPuzzleGame(prefs: SharedPreferences) {
    var difficulty by remember { mutableStateOf("Easy") }
    var a by remember { mutableStateOf(Random.nextInt(5, 20)) }
    var b by remember { mutableStateOf(Random.nextInt(2, 15)) }
    var answerInput by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf("") }
    var score by remember { mutableStateOf(0) }

    fun nextProblem() {
        val maxVal = if (difficulty == "Easy") 20 else 60
        a = Random.nextInt(5, maxVal)
        b = Random.nextInt(3, maxVal)
        answerInput = ""
    }

    WhiteCard {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PillChip("Easy", difficulty == "Easy") { difficulty = "Easy"; nextProblem() }
                PillChip("Hard", difficulty == "Hard") { difficulty = "Hard"; nextProblem() }
            }
            Text("स्कोर: $score", fontWeight = FontWeight.Bold, color = LearnPurple)
            Text("हल करें:  $a + $b = ?", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = LearnInk)
            OutlinedTextField(
                value = answerInput,
                onValueChange = { answerInput = it },
                label = { Text("आपका उत्तर") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Button(
                onClick = {
                    val userAns = answerInput.toIntOrNull()
                    if (userAns == a + b) {
                        score += 5
                        feedback = "✅ बिल्कुल सही! (+5 अंक)"
                        val best = prefs.getInt("game_score_Math Puzzle", 0)
                        if (score > best) prefs.edit().putInt("game_score_Math Puzzle", score).apply()
                        nextProblem()
                    } else {
                        feedback = "❌ गलत उत्तर! सही जवाब ${a + b} था।"
                        nextProblem()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = LearnPurple)
            ) { Text("उत्तर जांचें") }
            if (feedback.isNotEmpty()) Text(feedback, fontWeight = FontWeight.Bold, color = LearnInk)
        }
    }
}

@Composable
fun MemorySequenceGame(prefs: SharedPreferences) {
    var sequence by remember { mutableStateOf(List(4) { Random.nextInt(1, 10) }.joinToString("")) }
    var showSeq by remember { mutableStateOf(true) }
    var userGuess by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("इस नंबर को याद रखें, फिर 'छिपाएं' दबाएं") }
    var score by remember { mutableStateOf(0) }

    WhiteCard {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("स्कोर: $score", fontWeight = FontWeight.Bold, color = LearnPurple)
            if (showSeq) {
                ResultBox("याद रखें:", sequence)
                Button(
                    onClick = { showSeq = false; message = "अब वह नंबर नीचे लिखें:" },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = LearnPurple)
                ) { Text("नंबर छिपाएं (Hide)") }
            } else {
                OutlinedTextField(
                    value = userGuess,
                    onValueChange = { userGuess = it },
                    label = { Text("नंबर दर्ज करें") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Button(
                    onClick = {
                        if (userGuess.trim() == sequence) {
                            score += 10
                            message = "✅ शानदार याददाश्त! नया नंबर तैयार है।"
                            val best = prefs.getInt("game_score_Memory Game", 0)
                            if (score > best) prefs.edit().putInt("game_score_Memory Game", score).apply()
                        } else {
                            message = "❌ गलत! सही नंबर $sequence था।"
                        }
                        val len = 4 + (score / 20)
                        sequence = List(len) { Random.nextInt(1, 10) }.joinToString("")
                        userGuess = ""
                        showSeq = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = LearnPurple)
                ) { Text("जांचें") }
            }
            Text(message, fontSize = 13.sp, color = LearnMuted)
        }
    }
}

@Composable
fun NumberGuessingGame(prefs: SharedPreferences) {
    var target by remember { mutableStateOf(Random.nextInt(1, 101)) }
    var guess by remember { mutableStateOf("") }
    var hint by remember { mutableStateOf("मैंने 1 से 100 के बीच एक संख्या सोची है।") }
    var attempts by remember { mutableStateOf(0) }

    WhiteCard {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("प्रयास (Attempts): $attempts", fontWeight = FontWeight.Bold, color = LearnPurple)
            Text(hint, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = LearnInk)
            OutlinedTextField(
                value = guess,
                onValueChange = { guess = it },
                label = { Text("1-100 के बीच अनुमान लिखें") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val g = guess.toIntOrNull()
                        if (g == null || g !in 1..100) {
                            hint = "⚠️ कृपया 1 से 100 के बीच ही संख्या डालें।"
                        } else {
                            attempts++
                            when {
                                g < target -> hint = "⬆️ इससे बड़ी संख्या सोचें!"
                                g > target -> hint = "⬇️ इससे छोटी संख्या सोचें!"
                                else -> {
                                    val pts = kotlin.math.max(10, 100 - (attempts * 5))
                                    hint = "🎉 बधाई हो! आपने $attempts प्रयास में सही संख्या ($target) ढूंढ ली! स्कोर: $pts"
                                    val best = prefs.getInt("game_score_Number Guessing Game", 0)
                                    if (pts > best) prefs.edit().putInt("game_score_Number Guessing Game", pts).apply()
                                }
                            }
                        }
                        guess = ""
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = LearnPurple)
                ) { Text("अनुमान लगाएं") }

                OutlinedButton(onClick = {
                    target = Random.nextInt(1, 101)
                    attempts = 0
                    guess = ""
                    hint = "नया खेल शुरू! 1 से 100 के बीच संख्या पहचानें।"
                }) { Text("Restart") }
            }
        }
    }
}

@Composable
fun WordScrambleGame(prefs: SharedPreferences) {
    val wordsList = listOf("ANDROID", "KNOWLEDGE", "SCIENCE", "PLANET", "HISTORY", "COMPUTER", "STUDENT", "VICTORY")
    var idx by remember { mutableStateOf(0) }
    val currentWord = wordsList[idx % wordsList.size]
    val scrambled = remember(currentWord) { currentWord.toList().shuffled().joinToString(" ") }
    var answer by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var score by remember { mutableStateOf(0) }

    WhiteCard {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("स्कोर: $score", fontWeight = FontWeight.Bold, color = LearnPurple)
            ResultBox("उलझा हुआ शब्द (Unscramble):", scrambled)
            OutlinedTextField(
                value = answer,
                onValueChange = { answer = it },
                label = { Text("सही अंग्रेजी शब्द लिखें") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Button(
                onClick = {
                    if (answer.trim().equals(currentWord, ignoreCase = true)) {
                        score += 10
                        status = "✅ सही शब्द: $currentWord (+10 अंक)"
                        val best = prefs.getInt("game_score_Word Puzzle", 0)
                        if (score > best) prefs.edit().putInt("game_score_Word Puzzle", score).apply()
                        idx++
                        answer = ""
                    } else {
                        status = "❌ पुनः प्रयास करें!"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = LearnPurple)
            ) { Text("शब्द जांचें") }
            if (status.isNotEmpty()) Text(status, fontWeight = FontWeight.Bold, color = LearnInk)
        }
    }
}

========== FILE: app/src/main/java/com/earnzone/app/NotesAndFavorites.kt ==========
package com.earnzone.app

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val NotePurple = Color(0xFF5B21B6)
private val NoteGold = Color(0xFFFFC928)
private val NoteInk = Color(0xFF17152B)
private val NoteMuted = Color(0xFF6B6B7B)

data class UserNote(
    val id: Long,
    val title: String,
    val content: String,
    val updatedAt: String
)

private const val NOTE_SEP = "\u001E"

fun loadNotes(prefs: SharedPreferences): List<UserNote> {
    val raw = prefs.getString("user_notes_v1", "") ?: ""
    if (raw.isBlank()) return emptyList()
    return raw.lines().mapNotNull { line ->
        val parts = line.split(NOTE_SEP)
        if (parts.size >= 4) {
            UserNote(
                id = parts[0].toLongOrNull() ?: System.currentTimeMillis(),
                title = parts[1],
                content = parts[2].replace("\\n", "\n"),
                updatedAt = parts[3]
            )
        } else null
    }
}

fun saveNotes(prefs: SharedPreferences, list: List<UserNote>) {
    val encoded = list.joinToString("\n") { note ->
        val cleanTitle = note.title.replace(NOTE_SEP, " ").replace("\n", " ")
        val cleanBody = note.content.replace(NOTE_SEP, " ").replace("\n", "\\n")
        "${note.id}$NOTE_SEP$cleanTitle$NOTE_SEP$cleanBody$NOTE_SEP${note.updatedAt}"
    }
    prefs.edit().putString("user_notes_v1", encoded).apply()
}

@Composable
fun FavoritesAndNotesScreen(
    prefs: SharedPreferences,
    favoriteToolIds: Set<Int>,
    onToggleFavoriteTool: (Int) -> Unit,
    onOpenTool: (Int) -> Unit,
    newsState: AppState
) {
    var subTab by remember { mutableStateOf(0) }
    var notes by remember { mutableStateOf(loadNotes(prefs)) }
    var searchNoteQuery by remember { mutableStateOf("") }
    var editingNote by remember { mutableStateOf<UserNote?>(null) }
    var showNoteDialog by remember { mutableStateOf(false) }

    Column {
        Header("Favorites & Notes", "पसंदीदा टूल्स और व्यक्तिगत नोट्स") {
            Icon(Icons.Filled.Bookmark, null, modifier = Modifier.size(28.dp), tint = NoteGold)
        }
        Body {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PillChip("Favorites (${favoriteToolIds.size + newsState.saved.size})", subTab == 0) { subTab = 0 }
                PillChip("My Notes (${notes.size})", subTab == 1) { subTab = 1 }
            }

            if (subTab == 0) {
                SectionTitle("पसंदीदा Smart Tools")
                val favTools = smartToolsList.filter { favoriteToolIds.contains(it.id) }
                if (favTools.isEmpty()) {
                    EmptyState(
                        Icons.Filled.FavoriteBorder,
                        "कोई पसंदीदा टूल नहीं",
                        "किसी भी Smart Tool के ऊपर दिल (Heart) आइकन दबाकर यहाँ जोड़ें।"
                    )
                } else {
                    favTools.forEach { tool ->
                        WhiteCard {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onOpenTool(tool.id) }
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(tool.accent.copy(alpha = 0.14f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(tool.icon, null, tint = tool.accent)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(tool.title, fontWeight = FontWeight.Bold, color = NoteInk)
                                    Text(tool.subtitle, fontSize = 12.sp, color = NoteMuted)
                                }
                                IconButton(onClick = { onToggleFavoriteTool(tool.id) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "हटाएं", tint = Color(0xFFDC2626))
                                }
                            }
                        }
                    }
                }

                if (newsState.saved.isNotEmpty()) {
                    SectionTitle("सेव किए गए समाचार (${newsState.saved.size})")
                    NewsList(newsState.saved, newsState)
                }
            } else {
                Button(
                    onClick = {
                        editingNote = null
                        showNoteDialog = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NotePurple)
                ) {
                    Icon(Icons.Filled.Add, null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("नया नोट लिखें (Create Note)", fontWeight = FontWeight.Bold)
                }

                OutlinedTextField(
                    value = searchNoteQuery,
                    onValueChange = { searchNoteQuery = it },
                    placeholder = { Text("नोट्स में खोजें...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                val filteredNotes = if (searchNoteQuery.isBlank()) notes else {
                    notes.filter {
                        it.title.contains(searchNoteQuery, ignoreCase = true) ||
                                it.content.contains(searchNoteQuery, ignoreCase = true)
                    }
                }

                if (filteredNotes.isEmpty()) {
                    EmptyState(
                        Icons.Filled.Edit,
                        "कोई नोट नहीं मिला",
                        "ऊपर 'नया नोट लिखें' बटन दबाकर अपने महत्वपूर्ण नोट्स सहेजें।"
                    )
                } else {
                    filteredNotes.forEach { note ->
                        WhiteCard {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        note.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = NoteInk,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(onClick = {
                                        editingNote = note
                                        showNoteDialog = true
                                    }) {
                                        Icon(Icons.Filled.Edit, contentDescription = "संपादित करें", tint = NotePurple)
                                    }
                                    IconButton(onClick = {
                                        notes = notes.filter { it.id != note.id }
                                        saveNotes(prefs, notes)
                                    }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "हटाएं", tint = Color(0xFFDC2626))
                                    }
                                }
                                Text(note.content, fontSize = 14.sp, color = NoteInk)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("अपडेट: ${note.updatedAt}", fontSize = 11.sp, color = NoteMuted)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showNoteDialog) {
        var titleInput by remember { mutableStateOf(editingNote?.title ?: "") }
        var bodyInput by remember { mutableStateOf(editingNote?.content ?: "") }
        var err by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showNoteDialog = false },
            title = { Text(if (editingNote == null) "नया नोट बनाएं" else "नोट संपादित करें") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("शीर्षक (Title)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = bodyInput,
                        onValueChange = { bodyInput = it },
                        label = { Text("विवरण (Content)") },
                        modifier = Modifier.height(120.dp)
                    )
                    if (err.isNotEmpty()) Text(err, color = Color(0xFFDC2626), fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (titleInput.isBlank() || bodyInput.isBlank()) {
                        err = "कृपया शीर्षक और विवरण दोनों भरें।"
                        return@Button
                    }
                    val now = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
                    notes = if (editingNote == null) {
                        listOf(UserNote(System.currentTimeMillis(), titleInput.trim(), bodyInput.trim(), now)) + notes
                    } else {
                        notes.map {
                            if (it.id == editingNote!!.id) it.copy(title = titleInput.trim(), content = bodyInput.trim(), updatedAt = now)
                            else it
                        }
                    }
                    saveNotes(prefs, notes)
                    showNoteDialog = false
                }) {
                    Text("सहेजें (Save)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNoteDialog = false }) { Text("रद्द करें") }
            }
        )
    }
}

========== FILE: app/src/main/java/com/earnzone/app/MainActivity.kt ==========
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
import java.text.Normalizer
import java.text.ParseException
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

private val sources = listOf(Source("PIB", NEWS_URL, true))
private data class Source(val name: String, val url: String, val isNational: Boolean)

private fun cleanField(s: String): String = s.replace(SEP, " ").replace("\n", " ").replace("\r", " ")
private fun encode(list: List<NewsItem>): String = list.joinToString("\n") { cleanField(it.title) + SEP + it.link + SEP + cleanField(it.source) + SEP + it.pubDate }
private fun decode(text: String): List<NewsItem> = text.lines().mapNotNull { line ->
    val p = line.split(SEP)
    if (p.size < 2 || p[0].isEmpty() || !p[1].startsWith("https://")) null
    else if (p.size == 2) NewsItem(p[0], p[1])
    else NewsItem(p[0], p[1], p[2].ifBlank { "अज्ञात स्रोत" }, if (p.size >= 4) (p[3].toLongOrNull() ?: 0L) else 0L)
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
        } catch (e: ParseException) { }
    }
    return 0L
}

private fun formatDate(millis: Long): String = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(millis))

private fun fetchSource(src: Source): List<NewsItem> {
    val conn = URL(src.url).openConnection() as HttpURLConnection
    try {
        conn.connectTimeout = 12000
        conn.readTimeout = 12000
        conn.requestMethod = "GET"
        conn.setRequestProperty("User-Agent", "EarnZone-Android")
        if (conn.responseCode != 200) throw IOException("HTTP " + conn.responseCode)
        val text = conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }.trimStart('﻿')
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
                if (name == "item" || name == "entry") { inItem = true; title = ""; link = ""; date = "" }
                else if (inItem && name == "title") { title = parser.nextText().trim().replace(Regex("\\s+"), " ") }
                else if (inItem && name == "link") {
                    val href = parser.getAttributeValue(null, "href")
                    val t = parser.nextText().trim()
                    link = href?.trim() ?: if (t.isNotEmpty()) t else ""
                }
                else if (inItem && (name == "pubDate" || name == "published" || name == "updated")) { date = parser.nextText().trim() }
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
    } finally { conn.disconnect() }
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
    try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) } catch (e: Exception) { }
}

private fun appVersion(context: Context): String = try {
    context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0"
} catch (e: Exception) { "1.0" }

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
                        onOpenTool = { openTool(it) },
                        onNavigateStudy = { mode ->
                            studyInitialMode = mode
                            tab = 1
                        }
                    )
                    3 -> FavoritesAndNotesScreen(
                        prefs = prefs,
                        favoriteToolIds = favoriteToolIds,
                        onToggleFavoriteTool = { toggleFavTool(it) },
                        onOpenTool = { openTool(it) },
                        newsState = state
                    )
                    else -> ProfileScreen()
                }
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
    var searchQuery by remember { mutableStateOf("") }
    var showLegacyNews by remember { mutableStateOf(false) }

    Column {
        Header("EarnZone", "All-in-One Smart Tools & Learning") {
            IconButton(onClick = { showLegacyNews = !showLegacyNews }) {
                Icon(
                    if (showLegacyNews) Icons.Filled.Close else Icons.Filled.Notifications,
                    contentDescription = "News Updates",
                    tint = Color.White
                )
            }
        }
        Body {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Tools, Quizzes, Brain Games या Notes खोजें...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = Purple) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Filled.Close, contentDescription = "साफ़ करें")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            val q = searchQuery.trim()
            if (q.isNotEmpty()) {
                val matchedTools = smartToolsList.filter {
                    it.title.contains(q, ignoreCase = true) || it.subtitle.contains(q, ignoreCase = true)
                }
                val matchedSubjects = studySubjects.filter { it.first.contains(q, ignoreCase = true) }
                val gamesList = listOf("Math Puzzle", "Memory Game", "Number Guessing Game", "Word Puzzle")
                val matchedGames = gamesList.filter { it.contains(q, ignoreCase = true) }
                val matchedNotes = loadNotes(prefs).filter {
                    it.title.contains(q, ignoreCase = true) || it.content.contains(q, ignoreCase = true)
                }

                if (matchedTools.isEmpty() && matchedSubjects.isEmpty() && matchedGames.isEmpty() && matchedNotes.isEmpty()) {
                    EmptyState(Icons.Filled.Search, "कोई परिणाम नहीं मिला", "\"$q\" से संबंधित कोई टूल या क्विज़ नहीं मिला।")
                } else {
                    if (matchedTools.isNotEmpty()) {
                        SectionTitle("Smart Tools (${matchedTools.size})")
                        matchedTools.forEach { t -> SettingRow(t.icon, t.title, t.subtitle) { onOpenTool(t.id) } }
                    }
                    if (matchedSubjects.isNotEmpty()) {
                        SectionTitle("Study Quizzes (${matchedSubjects.size})")
                        matchedSubjects.forEach { (subj, icon) -> SettingRow(icon, "$subj Quiz", "ऑफलाइन अभ्यास प्रश्न") { onNavigateTab(1, "MENU") } }
                    }
                    if (matchedGames.isNotEmpty()) {
                        SectionTitle("Brain Games (${matchedGames.size})")
                        matchedGames.forEach { g -> SettingRow(Icons.Filled.Extension, g, "दिमागी खेल खेलें") { onNavigateTab(1, "GAMES") } }
                    }
                    if (matchedNotes.isNotEmpty()) {
                        SectionTitle("Saved Notes (${matchedNotes.size})")
                        matchedNotes.forEach { n -> SettingRow(Icons.Filled.Edit, n.title, n.content.take(40)) { onNavigateTab(3, "MENU") } }
                    }
                }
            } else if (showLegacyNews) {
                SectionTitle("सरकारी समाचार अपडेट्स")
                FeedStatus(newsState, onRefreshNews)
                if (newsState.items.isEmpty()) {
                    if (!newsState.loading) NoNewsState()
                } else {
                    NewsList(newsState.items.take(10), newsState)
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.linearGradient(listOf(Purple2, Purple)))
                        .padding(20.dp)
                ) {
                    Column {
                        Text("स्वागत है! EarnZone Smart Hub", color = Gold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "9 स्मार्ट कैलकुलेटर, ऑफलाइन क्विज़, डेली चैलेंज और ब्रेन गेम्स एक ही ऐप में",
                            color = Color.White,
                            fontSize = 18.sp,
                            lineHeight = 25.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { onNavigateTab(2, "MENU") },
                                colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Purple)
                            ) {
                                Text("सभी Tools देखें", fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { onNavigateTab(1, "DAILY") },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) {
                                Text("Daily Challenge")
                            }
                        }
                    }
                }

                if (recentToolIds.isNotEmpty()) {
                    SectionTitle("हाल ही में उपयोग किए गए")
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        recentToolIds.mapNotNull { id -> smartToolsList.firstOrNull { it.id == id } }.forEach { tool ->
                            PillChip(tool.title, true) { onOpenTool(tool.id) }
                        }
                    }
                }

                SectionTitle("Quick Smart Tools")
                smartToolsList.take(4).chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        pair.forEach { tool ->
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(CardWhite)
                                    .clickable { onOpenTool(tool.id) }
                                    .padding(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(tool.accent.copy(alpha = 0.14f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(tool.icon, null, tint = tool.accent)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(tool.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Ink)
                                Text(tool.subtitle, fontSize = 11.sp, color = Muted, maxLines = 1)
                            }
                        }
                    }
                }

                SectionTitle("Study Zone & Brain Games")
                SettingRow(Icons.Filled.Star, "Daily Challenge", "आज का प्रश्न हल करें और स्कोर बढ़ाएं") {
                    onNavigateTab(1, "DAILY")
                }
                SettingRow(Icons.Filled.School, "Offline Quizzes (8 विषय)", "GK, Math, Science, History, Computer, Hindi & English") {
                    onNavigateTab(1, "MENU")
                }
                SettingRow(Icons.Filled.Extension, "Brain Games (4 गेम्स)", "Math Puzzle, Memory, Number Guess & Word Puzzle") {
                    onNavigateTab(1, "GAMES")
                }
                SettingRow(Icons.Filled.Edit, "Personal Notes & Favorites", "अपने महत्वपूर्ण नोट्स और पसंदीदा टूल्स देखें") {
                    onNavigateTab(3, "MENU")
                }
            }
        }
    }
}

@Composable
fun PopularToolsDirectoryScreen(
    favoriteToolIds: Set<Int>,
    onToggleFavorite: (Int) -> Unit,
    onOpenTool: (Int) -> Unit,
    onNavigateStudy: (String) -> Unit
) {
    var search by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf("All") }
    val filterCats = listOf("All", "Calculators", "Converters", "Text Tools", "Study Tools", "Brain Games")

    Column {
        Header("Tools Directory", "सभी स्मार्ट टूल्स और लर्निंग यूटिलिटीज़") {
            Icon(Icons.Filled.Build, null, modifier = Modifier.size(26.dp), tint = Gold)
        }
        Body {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("टूल का नाम खोजें...") },
                leadingIcon = { Icon(Icons.Filled.Search, null, tint = Purple) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterCats.forEach { cat ->
                    PillChip(cat, selectedCat == cat) { selectedCat = cat }
                }
            }

            val filteredTools = smartToolsList.filter { tool ->
                val matchesCat = selectedCat == "All" || tool.category == selectedCat
                val matchesSearch = search.isBlank() || tool.title.contains(search, ignoreCase = true) || tool.subtitle.contains(search, ignoreCase = true)
                matchesCat && matchesSearch
            }

            if (filteredTools.isNotEmpty()) {
                SectionTitle("Smart Tools (${filteredTools.size})")
                filteredTools.forEach { tool ->
                    WhiteCard {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenTool(tool.id) }
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
                                Icon(tool.icon, null, tint = tool.accent)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(tool.title, fontWeight = FontWeight.Bold, color = Ink)
                                Text(tool.subtitle, fontSize = 12.sp, color = Muted)
                            }
                            IconButton(onClick = { onToggleFavorite(tool.id) }) {
                                Icon(
                                    if (favoriteToolIds.contains(tool.id)) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (favoriteToolIds.contains(tool.id)) Gold else Muted
                                )
                            }
                        }
                    }
                }
            }

            if (selectedCat == "All" || selectedCat == "Study Tools") {
                SectionTitle("Study Tools")
                SettingRow(Icons.Filled.School, "Subject Quizzes", "8 विषयों के अभ्यास प्रश्न") { onNavigateStudy("MENU") }
                SettingRow(Icons.Filled.Star, "Daily Challenge", "प्रतिदिन एक नया प्रश्न") { onNavigateStudy("DAILY") }
            }

            if (selectedCat == "All" || selectedCat == "Brain Games") {
                SectionTitle("Brain Games")
                SettingRow(Icons.Filled.Extension, "Play Brain Games", "4 ऑफलाइन दिमागी खेल") { onNavigateStudy("GAMES") }
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
    val version = appVersion(context)

    Column {
        Header("EarnZone", if (version.isEmpty()) "All-in-One App" else "वर्ज़न $version")
        Body {
            SettingRow(Icons.Filled.Language, "भाषा (Language)", "हिंदी / English") {
                dlg = Pair("भाषा सेटिंग", "EarnZone में टूल्स और क्विज़ हिंदी और अंग्रेजी दोनों भाषाओं के अनुकूल तैयार किए गए हैं।")
            }
            SettingRow(Icons.Filled.Info, "EarnZone के बारे में", "All-in-One Smart Tools & Learning App") {
                dlg = Pair("EarnZone के बारे में", "EarnZone एक संपूर्ण स्मार्ट टूल्स और ऑफलाइन लर्निंग ऐप है जिसमें 9 दैनिक कैलकुलेटर/कन्वर्टर, 8 विषयों के क्विज़, डेली चैलेंज, 4 ब्रेन गेम्स और व्यक्तिगत नोट्स की सुविधा है।")
            }
            SettingRow(Icons.Filled.Lock, "गोपनीयता नीति", "Privacy Policy") {
                dlg = Pair("गोपनीयता नीति", "आपके सभी नोट्स, पसंदीदा टूल्स और क्विज़ स्कोर आपके फ़ोन में ही सुरक्षित रहते हैं। विज्ञापन Google AdMob द्वारा दिखाए जाते हैं।")
            }
            SettingRow(Icons.Filled.Description, "नियम और शर्तें", "Terms & Conditions") {
                dlg = Pair("नियम और शर्तें", "यह ऐप शैक्षणिक और दैनिक गणना सहायता के लिए बनाया गया है। इसमें कोई सट्टेबाजी या पेड एंट्री शामिल नहीं है।")
            }
            SettingRow(Icons.Filled.Share, "ऐप शेयर करें (Share App)", "दोस्तों को भेजें") {
                val send = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "EarnZone – All-in-One Smart Tools & Learning App आज़माएं!")
                }
                context.startActivity(Intent.createChooser(send, "EarnZone शेयर करें"))
            }
            SettingRow(Icons.Filled.Star, "ऐप रेट करें (Rate App)", "अपनी प्रतिक्रिया दें") {
                dlg = Pair("Rate EarnZone", "Play Store पर जल्द उपलब्ध होगा।")
            }
            SettingRow(Icons.Filled.Info, "ऐप वर्ज़न", version.ifEmpty { "1.0" }) { }
        }
    }

    val d = dlg
    if (d != null) {
        AlertDialog(
            onDismissRequest = { dlg = null },
            title = { Text(d.first) },
            text = { Text(d.second) },
            confirmButton = { TextButton(onClick = { dlg = null }) { Text("ठीक है") } }
        )
    }
}
