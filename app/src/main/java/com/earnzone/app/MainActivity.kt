                            )
                            ToolButton("अतिरिक्त स्पेस हटाएं") {
                                resultText = input1.trim().replace("\\s+".toRegex(), " ")
                            }
                        }
                        126 -> { // Length Converter
                            ToolField(input1, { input1 = it }, "लंबाई मीटर में")
                            ToolButton("बदलें") {
                                resultText = input1.toDoubleOrNull()?.let { m ->
                                    "किलोमीटर: %.6f\nसेंटीमीटर: %.2f\nफीट: %.4f\nइंच: %.4f\nमील: %.6f"
                                        .format(m / 1000, m * 100, m * 3.28084, m * 39.3701, m / 1609.344)
                                } ?: "त्रुटि: सही संख्या दर्ज करें।"
                            }
                        }
                        127 -> { // Weight Converter
                            ToolField(input1, { input1 = it }, "वजन किलोग्राम में")
                            ToolButton("बदलें") {
                                resultText = input1.toDoubleOrNull()?.let { kg ->
                                    "ग्राम: %.2f\nक्विंटल: %.4f\nटन: %.5f\nपाउंड: %.4f\nऔंस: %.3f"
                                        .format(kg * 1000, kg / 100, kg / 1000, kg * 2.20462, kg * 35.274)
                                } ?: "त्रुटि: सही संख्या दर्ज करें।"
                            }
                        }
                        128 -> { // Temperature Converter
                            ToolField(input1, { input1 = it }, "तापमान Celsius में")
                            ToolButton("बदलें") {
                                resultText = input1.toDoubleOrNull()?.let { c ->
                                    "Fahrenheit: %.2f\nKelvin: %.2f".format(c * 9 / 5 + 32, c + 273.15)
                                } ?: "त्रुटि: सही संख्या दर्ज करें।"
                            }
                        }
                        129 -> { // Volume Converter
                            ToolField(input1, { input1 = it }, "आयतन लीटर में")
                            ToolButton("बदलें") {
                                resultText = input1.toDoubleOrNull()?.let { l ->
                                    "मिलीलीटर: %.2f\nगैलन (US): %.4f\nकप (US): %.3f".format(l * 1000, l / 3.78541, l * 4.22675)
                                } ?: "त्रुटि: सही संख्या दर्ज करें।"
                            }
                        }
                        151 -> { // Date Difference
                            ToolField(input1, { input1 = it }, "दो तारीखें (YYYY-MM-DD, YYYY-MM-DD)")
                            ToolButton("अंतर निकालें") {
                                resultText = try {
                                    val f = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                                    f.isLenient = false
                                    f.timeZone = java.util.TimeZone.getTimeZone("UTC")
                                    val parts = input1.split(",")
                                    val a = f.parse(parts[0].trim())!!.time
                                    val b = f.parse(parts[1].trim())!!.time
                                    val days = Math.abs(b - a) / 86400000L
                                    "कुल दिन: $days\nसप्ताह: ${days / 7} और ${days % 7} दिन\nलगभग माह: %.1f\nलगभग वर्ष: %.2f"
                                        .format(days / 30.44, days / 365.25)
                                } catch (e: Exception) {
                                    "त्रुटि: प्रारूप दें -> 2024-01-15, 2026-11-17"
                                }
                            }
                        }
                        167 -> { // JSON Formatter
                            ToolField(input1, { input1 = it }, "JSON यहाँ पेस्ट करें", false)
                            ToolButton("फॉर्मेट करें") {
                                resultText = try {
                                    val t = input1.trim()
                                    if (t.startsWith("[")) org.json.JSONArray(t).toString(2) else org.json.JSONObject(t).toString(2)
                                } catch (e: Exception) {
                                    "त्रुटि: अमान्य JSON"
                                }
                            }
                        }
                        168 -> { // Password Generator
                            ToolField(input1, { input1 = it }, "पासवर्ड की लंबाई (4 से 64, जैसे 12)")
                            ToolButton("पासवर्ड बनाएं") {
                                val n = input1.trim().toIntOrNull()
                                resultText = if (n == null || n < 4 || n > 64) {
                                    "त्रुटि: लंबाई 4 से 64 के बीच दें।"
                                } else {
                                    val chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789@#\$%&*!?"
                                    val rnd = java.security.SecureRandom()
                                    (1..n).map { chars[rnd.nextInt(chars.length)] }.joinToString("")
                                }
                            }
                        }
                        182 -> { // Fuel Cost
                            ToolField(input1, { input1 = it }, "दूरी (किमी), माइलेज (किमी/लीटर), दर (₹/लीटर) (जैसे: 250, 18, 100)")
                            ToolButton("खर्च निकालें") {
                                resultText = try {
                                    val p = input1.split(",").map { it.trim().toDouble() }
                                    if (p[1] <= 0.0) throw ArithmeticException()
                                    val litres = p[0] / p[1]
                                    "ईंधन: %.2f लीटर\nकुल खर्च: ₹%.2f".format(litres, litres * p[2])
                                } catch (e: Exception) {
                                    "त्रुटि: प्रारूप दें -> 250, 18, 100"
                                }
                            }
                        }
                        49 -> { // Exam Countdown
                            ToolField(input1, { input1 = it }, "परीक्षा की तारीख (YYYY-MM-DD)")
                            ToolButton("दिन गिनें") {
                                resultText = try {
                                    val f = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                                    f.isLenient = false
                                    val exam = f.parse(input1.trim())!!.time
                                    val today = f.parse(f.format(java.util.Date()))!!.time
                                    val d = Math.round((exam - today) / 86400000.0)
                                    when {
                                        d > 0 -> "परीक्षा में $d दिन बाकी हैं।"
                                        d == 0L -> "परीक्षा आज है। शुभकामनाएँ!"
                                        else -> "परीक्षा को ${-d} दिन हो चुके हैं।"
                                    }
                                } catch (e: Exception) {
                                    "त्रुटि: प्रारूप दें -> 2026-12-15"
                                }
                            }
                        }
                        50 -> { // Attendance Tracker
                            ToolField(input1, { input1 = it }, "उपस्थित कक्षाएँ, कुल कक्षाएँ (जैसे: 30, 40)")
                            ToolButton("प्रतिशत निकालें") {
                                resultText = try {
                                    val p = input1.split(",").map { it.trim().toDouble() }
                                    val a = p[0]
                                    val t = p[1]
                                    if (t <= 0.0 || a < 0.0 || a > t) throw ArithmeticException()
                                    val pct = a / t * 100
                                    val extra = if (pct < 75.0) {
                                        "75%% के लिए लगातार %d कक्षाएँ और चाहिए।".format(Math.ceil((0.75 * t - a) / 0.25).toInt())
                                    } else {
                                        "75%% बनाए रखते हुए आप %d कक्षाएँ छोड़ सकते हैं।".format(Math.floor(a / 0.75 - t).toInt())
                                    }
                                    "उपस्थिति: %.2f%%\n".format(pct) + extra
                                } catch (e: Exception) {
                                    "त्रुटि: प्रारूप दें -> 30, 40"
                                }
                            }
                        }
                        196 -> MathPuzzleGame()
                        197 -> MemoryGame()
                        206 -> NotesTool()
                        207 -> TodoTool()
                        else -> {
                            Text(
                                "इस टूल पर अभी काम चल रहा है, यह अभी उपलब्ध नहीं है।",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        }
                    }

                    val context = LocalContext.current
                    if (resultText.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SoftPurple)
                                .padding(14.dp)
                        ) {
                            Text(resultText, color = TextDark, fontSize = 15.sp)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                    cm.setPrimaryClip(android.content.ClipData.newPlainText("result", resultText))
                                    android.widget.Toast.makeText(context, "कॉपी हो गया", android.widget.Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Filled.ContentCopy, null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("कॉपी")
                            }
                            OutlinedButton(
                                onClick = {
                                    val send = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, resultText)
                                    }
                                    context.startActivity(Intent.createChooser(send, "शेयर करें"))
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Filled.Share, null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("शेयर")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ToolField(value: String, onChange: (String) -> Unit, label: String, singleLine: Boolean = true) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun ToolButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun NotesTool() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("earnzone_pro", Context.MODE_PRIVATE) }
    val sep = "\u241F"
    val notes = remember {
        mutableStateListOf<String>().apply {
            addAll((prefs.getString("notes_list", "") ?: "").split(sep).filter { it.isNotBlank() })
        }
    }
    var draft by remember { mutableStateOf("") }
    fun persist() {
        prefs.edit().putString("notes_list", notes.joinToString(sep)).apply()
    }

    ToolField(draft, { draft = it }, "नया नोट लिखें", false)
    ToolButton("नोट सेव करें") {
        if (draft.isNotBlank()) {
            notes.add(0, draft.trim())
            draft = ""
            persist()
        }
    }
    if (notes.isEmpty()) {
        Text("कोई नोट सेव नहीं है।", color = TextMuted, fontSize = 13.sp)
    }
    notes.forEachIndexed { i, n ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SoftPurple)
                .padding(start = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(n, modifier = Modifier.weight(1f), color = TextDark, fontSize = 14.sp)
            IconButton(onClick = {
                notes.removeAt(i)
                persist()
            }) {
                Icon(Icons.Filled.Delete, contentDescription = "हटाएं", tint = TextMuted)
            }
        }
    }
}

@Composable
fun TodoTool() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("earnzone_pro", Context.MODE_PRIVATE) }
    val sep = "\u241F"
    val tasks = remember {
        mutableStateListOf<Pair<Boolean, String>>().apply {
            (prefs.getString("todo_list", "") ?: "").split(sep).filter { it.length > 2 }.forEach {
                add((it[0] == '1') to it.substring(2))
            }
        }
    }
    var draft by remember { mutableStateOf("") }
    fun persist() {
        prefs.edit().putString(
            "todo_list",
            tasks.joinToString(sep) { (if (it.first) "1" else "0") + "|" + it.second }
        ).apply()
    }

    ToolField(draft, { draft = it }, "नया काम लिखें")
    ToolButton("काम जोड़ें") {
        if (draft.isNotBlank()) {
            tasks.add(0, false to draft.trim().replace("\n", " "))
            draft = ""
            persist()
        }
    }
    if (tasks.isEmpty()) {
        Text("कोई काम नहीं जोड़ा गया है।", color = TextMuted, fontSize = 13.sp)
    }
    tasks.forEachIndexed { i, t ->
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = t.first,
                onCheckedChange = { checked ->
                    tasks[i] = checked to t.second
                    persist()
                }
            )
            Text(
                t.second,
                modifier = Modifier.weight(1f),
                color = if (t.first) TextMuted else TextDark,
                textDecoration = if (t.first) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
            )
            IconButton(onClick = {
                tasks.removeAt(i)
                persist()
            }) {
                Icon(Icons.Filled.Delete, contentDescription = "हटाएं", tint = TextMuted)
            }
        }
    }
}

@Composable
fun MathPuzzleGame() {
    var a by remember { mutableStateOf((2..20).random()) }
    var b by remember { mutableStateOf((2..20).random()) }
    var op by remember { mutableStateOf(listOf("+", "-", "×").random()) }
    var answer by remember { mutableStateOf("") }
    var score by remember { mutableStateOf(0) }
    var total by remember { mutableStateOf(0) }
    var feedback by remember { mutableStateOf("") }
    val correct = when (op) {
        "+" -> a + b
        "-" -> a - b
        else -> a * b
    }

    Text("$a $op $b = ?", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = PrimaryPurple)
    ToolField(answer, { answer = it }, "आपका उत्तर")
    ToolButton("जाँचें") {
        val g = answer.trim().toIntOrNull()
        if (g == null) {
            feedback = "कृपया संख्या दर्ज करें।"
        } else {
            total += 1
            if (g == correct) {
                score += 1
                feedback = "सही!"
            } else {
                feedback = "गलत। सही उत्तर: $correct"
            }
            a = (2..20).random()
            b = (2..20).random()
            op = listOf("+", "-", "×").random()
            answer = ""
        }
    }
    Text("स्कोर: $score / $total", fontWeight = FontWeight.Bold, color = TextDark)
    if (feedback.isNotEmpty()) {
        Text(feedback, color = TextMuted, fontSize = 13.sp)
    }
}

@Composable
fun MemoryGame() {
    var round by remember { mutableStateOf(0) }
    var level by remember { mutableStateOf(3) }
    var number by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }
    var answer by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("शुरू करने के लिए बटन दबाएँ। संख्या कुछ सेकंड दिखेगी, फिर छिप जाएगी।") }

    LaunchedEffect(round) {
        if (round > 0) {
            visible = true
            kotlinx.coroutines.delay(1500L + level * 400L)
            visible = false
        }
    }

    fun start() {
        number = (1..level).map { (0..9).random() }.joinToString("")
        answer = ""
        message = ""
        round += 1
    }

    if (round > 0) {
        Text(
            if (visible) number else "?".repeat(level),
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryPurple,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
    if (round > 0 && !visible) {
        ToolField(answer, { answer = it }, "संख्या याद करके लिखें")
        ToolButton("जाँचें") {
            if (answer.trim() == number) {
                level += 1
                message = "सही! अगला स्तर: $level अंक।"
            } else {
                message = "गलत। संख्या थी $number"
                level = maxOf(3, level - 1)
            }
            round = 0
            number = ""
        }
    } else if (round == 0) {
        ToolButton(if (number.isEmpty()) "खेल शुरू करें" else "नई संख्या") { start() }
    }
    Text("स्तर: $level अंक", fontWeight = FontWeight.Bold, color = TextDark)
    if (message.isNotEmpty()) {
        Text(message, color = TextMuted, fontSize = 13.sp)
    }
}

@Composable
fun StudyAndGamesScreen() {
    Column {
        ProHeader("Study & Games", "पढ़ाई और दिमागी खेल") {
            Icon(Icons.Filled.School, null, tint = AccentYellow)
        }
        ProBody {
            SectionHeading("गणित पहेली")
            ProCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    MathPuzzleGame()
                }
            }
            SectionHeading("याददाश्त ट्रेनर")
            ProCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    MemoryGame()
                }
            }
        }
    }
}

@Composable
fun FavoritesAndHistoryScreen(
    favoriteIds: Set<Int>,
    recentIds: List<Int>,
    onToggleFavorite: (Int) -> Unit,
    onOpenService: (ProService) -> Unit
) {
    val favs = earnZoneProServices.filter { favoriteIds.contains(it.id) }
    val recents = recentIds.mapNotNull { id -> earnZoneProServices.firstOrNull { it.id == id } }

    Column {
        ProHeader("Favorites", "पसंदीदा और हाल की सर्विसेज") {
            Icon(Icons.Filled.Favorite, null, tint = AccentYellow)
        }
        ProBody {
            SectionHeading("पसंदीदा (${favs.size})")
            if (favs.isEmpty()) {
                ProCard {
                    Text("अभी कोई पसंदीदा सर्विस नहीं है। किसी सर्विस पर दिल का आइकन दबाएँ।", modifier = Modifier.padding(16.dp), color = TextMuted, fontSize = 13.sp)
                }
            } else {
                favs.forEach { s ->
                    ServiceItemRow(s, true, { onToggleFavorite(s.id) }) { onOpenService(s) }
                }
            }

            SectionHeading("हाल ही में उपयोग किए गए (${recents.size})")
            if (recents.isEmpty()) {
                ProCard {
                    Text("अभी कोई सर्विस नहीं खोली गई।", modifier = Modifier.padding(16.dp), color = TextMuted, fontSize = 13.sp)
                }
            } else {
                recents.forEach { s ->
                    ServiceItemRow(s, favoriteIds.contains(s.id), { onToggleFavorite(s.id) }) { onOpenService(s) }
                }
            }
        }
    }
}

@Composable
fun ProfileSettingsScreen() {
    val categoryCount = earnZoneProServices.map { it.category }.distinct().size
    Column {
        ProHeader("Profile", "EarnZone Pro के बारे में") {
            Icon(Icons.Filled.Person, null, tint = AccentYellow)
        }
        ProBody {
            ProCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("EarnZone Pro", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text("संस्करण 1.0", color = TextMuted, fontSize = 13.sp)
                    Text("कुल सर्विसेज: ${earnZoneProServices.size}", color = TextDark)
                    Text("श्रेणियाँ: $categoryCount", color = TextDark)
                }
            }
            ProCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionHeading("आपका डेटा")
                    Text(
                        "पसंदीदा, हाल की सर्विसेज, नोट्स और To-Do सूची सिर्फ़ आपके फोन में सेव होती हैं। ज़्यादातर टूल बिना इंटरनेट के चलते हैं।",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
