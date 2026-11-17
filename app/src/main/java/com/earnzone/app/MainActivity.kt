package com.earnzone.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val Purple = Color(0xFF5428D8)
private val LightPurple = Color(0xFF7C4DFF)
private val Bg = Color(0xFFF7F5FF)
private val CardWhite = Color(0xFFFFFFFF)
private val Gold = Color(0xFFF5B82E)
private val Green = Color(0xFF16A34A)
private val Red = Color(0xFFDC2626)

private fun todayNumber(): Int {
    val now = System.currentTimeMillis()
    return ((now + TimeZone.getDefault().getOffset(now)) / 86400000L).toInt()
}

data class RewardItem(val id: String, val name: String, val cost: Int)

private val rewardItems = listOf(
    RewardItem("purple_theme", "Purple Theme", 500),
    RewardItem("silver_badge", "Silver Badge", 1000),
    RewardItem("golden_avatar", "Golden Avatar", 2000),
    RewardItem("profile_frame", "Special Profile Frame", 3000),
    RewardItem("exclusive_theme", "Exclusive Theme", 5000)
)

private val tips = listOf(
    "Open EarnZone every day to build your streak.",
    "Use two fingers in Tap Rush for a higher score.",
    "Finish all daily tasks to level up faster.",
    "Rewards you buy are yours forever.",
    "Spin the wheel once every day.",
    "Keep a 7 day streak to earn a special badge."
)

class Store(private val prefs: SharedPreferences) {
    var points by mutableStateOf(prefs.getInt("points", 0))
        private set
    var totalEarned by mutableStateOf(prefs.getInt("total_earned", 0))
        private set
    var history by mutableStateOf(prefs.getString("history", "") ?: "")
        private set
    var owned by mutableStateOf(prefs.getString("owned", "") ?: "")
        private set
    var streak by mutableStateOf(prefs.getInt("streak", 0))
        private set
    var bestStreak by mutableStateOf(prefs.getInt("best_streak", 0))
        private set
    var streakClaimDay by mutableStateOf(prefs.getInt("streak_claim", -1))
        private set
    var spinCount by mutableStateOf(prefs.getInt("spin_count", 0))
        private set
    var bestTaps by mutableStateOf(prefs.getInt("best_taps", 0))
        private set
    var tick by mutableStateOf(0)
        private set
    val userId: String

    init {
        val saved = prefs.getString("user_id", null)
        if (saved == null) {
            val n = "EZ" + Random.nextInt(100000, 999999)
            prefs.edit().putString("user_id", n).apply()
            userId = n
        } else {
            userId = saved
        }
        val today = todayNumber()
        val last = prefs.getInt("last_open", -1)
        if (today > last) {
            streak = if (last == today - 1) streak + 1 else 1
            if (streak > bestStreak) bestStreak = streak
            prefs.edit()
                .putInt("last_open", today)
                .putInt("streak", streak)
                .putInt("best_streak", bestStreak)
                .apply()
        }
        if (streak < 1) streak = 1
    }

    val level: Int
        get() = minOf(20, 1 + totalEarned / 150)

    val levelName: String
        get() = if (level <= 5) "Bronze" else if (level <= 10) "Silver" else "Gold"

    fun badges(): List<Pair<String, Boolean>> = listOf(
        "New Member" to true,
        "Spin Master" to (spinCount >= 7),
        "7 Days Streak" to (bestStreak >= 7),
        "Game Champion" to (bestTaps >= 80),
        "Quiz Master" to false,
        "Gold Member" to (level >= 11)
    )

    fun currentBadge(): String = badges().filter { it.second }.last().first

    private fun addHistory(reason: String, n: Int) {
        val counter = prefs.getInt("tx_counter", 0) + 1
        val id = "TX" + counter.toString().padStart(5, '0')
        val time = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
        val line = "$id|$time|$reason|$n"
        val lines = (listOf(line) + history.lines().filter { it.isNotEmpty() }).take(50)
        history = lines.joinToString("\n")
        prefs.edit().putInt("tx_counter", counter).putString("history", history).apply()
    }

    fun earn(n: Int, reason: String) {
        if (n <= 0) return
        points += n
        totalEarned += n
        prefs.edit().putInt("points", points).putInt("total_earned", totalEarned).apply()
        addHistory(reason, n)
        tick += 1
    }

    fun isOwned(id: String): Boolean = owned.split(",").contains(id)

    fun buy(item: RewardItem): String {
        if (isOwned(item.id)) return "Already owned."
        if (points < item.cost) return "Not enough points."
        points -= item.cost
        owned = if (owned.isEmpty()) item.id else owned + "," + item.id
        prefs.edit().putInt("points", points).putString("owned", owned).apply()
        addHistory("Bought " + item.name, -item.cost)
        tick += 1
        return "You got " + item.name + "!"
    }

    fun streakReward(): Int {
        val day = ((streak - 1) % 7) + 1
        return if (day == 7) 30 else day * 5
    }

    fun claimStreak(): Int {
        val today = todayNumber()
        if (streakClaimDay == today) return 0
        val day = ((streak - 1) % 7) + 1
        val reward = streakReward()
        streakClaimDay = today
        prefs.edit().putInt("streak_claim", today).apply()
        earn(reward, "Streak Day $day")
        return reward
    }

    fun recordSpin() {
        spinCount += 1
        prefs.edit().putInt("spin_count", spinCount).apply()
    }

    fun recordTaps(t: Int) {
        if (t > bestTaps) {
            bestTaps = t
            prefs.edit().putInt("best_taps", t).apply()
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
                colorScheme = lightColorScheme(primary = Purple, background = Bg)
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
fun WhiteCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        shape = RoundedCornerShape(16.dp)
    ) {
        content()
    }
}

@Composable
fun Title(text: String) {
    Text(text, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Purple)
}

@Composable
fun EarnZoneApp(prefs: SharedPreferences) {
    var resetKey by remember { mutableStateOf(0) }
    val store = remember(resetKey) { Store(prefs) }
    var tab by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = Bg,
        bottomBar = {
            Column {
                BannerAd()
                NavigationBar {
                    NavigationBarItem(
                        selected = tab == 0,
                        onClick = { tab = 0 },
                        icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = tab == 1,
                        onClick = { tab = 1 },
                        icon = { Icon(Icons.Filled.Star, contentDescription = "Tasks") },
                        label = { Text("Tasks") }
                    )
                    NavigationBarItem(
                        selected = tab == 2,
                        onClick = { tab = 2 },
                        icon = { Icon(Icons.Filled.PlayArrow, contentDescription = "Games") },
                        label = { Text("Games") }
                    )
                    NavigationBarItem(
                        selected = tab == 3,
                        onClick = { tab = 3 },
                        icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Rewards") },
                        label = { Text("Rewards") }
                    )
                    NavigationBarItem(
                        selected = tab == 4,
                        onClick = { tab = 4 },
                        icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                        label = { Text("Profile") }
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            key(resetKey) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (tab) {
                        0 -> HomeScreen(prefs, store) { tab = it }
                        1 -> TasksScreen(prefs, store)
                        2 -> GameScreen(prefs, store)
                        3 -> RewardsScreen(store)
                        else -> ProfileScreen(store) {
                            prefs.edit().clear().apply()
                            resetKey += 1
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(prefs: SharedPreferences, store: Store, onGo: (Int) -> Unit) {
    val today = todayNumber()
    var tasksDone = 0
    for (i in 0..2) {
        if (prefs.getInt("task_$i", -1) == today) tasksDone += 1
    }
    var actions = tasksDone
    if (prefs.getInt("spin_day", -1) == today) actions += 1
    if (prefs.getInt("game_day", -1) == today) actions += prefs.getInt("game_plays", 0)
    val fraction = if (store.tick >= 0) (actions / 7f).coerceAtMost(1f) else 0f

    var streakMsg by remember { mutableStateOf("") }
    val streakDay = ((store.streak - 1) % 7) + 1
    val claimed = store.streakClaimDay == today

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Purple),
            contentAlignment = Alignment.Center
        ) {
            Text("E", color = Gold, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text("EarnZone", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Purple)
            Text("Welcome back, Player!")
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Purple),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Total points", color = Color.White)
            Text("${store.points}", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Gold)
            Text("Level ${store.level} (${store.levelName})", color = Color.White)
            Text("Badge: ${store.currentBadge()}", color = Color.White)
        }
    }

    WhiteCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Today's progress", fontWeight = FontWeight.Bold)
            Text("$actions of 7 activities done")
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xFFE6E0FA))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction)
                        .fillMaxHeight()
                        .background(Purple)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Daily Challenge: finish all 3 tasks ($tasksDone/3)", color = LightPurple)
        }
    }

    WhiteCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Daily Streak: ${store.streak} day(s)", fontWeight = FontWeight.Bold)
            Text("Today is streak day $streakDay of 7. Bonus: ${store.streakReward()} points")
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                enabled = !claimed,
                onClick = {
                    val r = store.claimStreak()
                    streakMsg = "You got $r points!"
                }
            ) {
                Text(if (claimed) "Claimed today" else "Claim streak bonus")
            }
            if (streakMsg.isNotEmpty()) {
                Text(streakMsg, fontWeight = FontWeight.Bold, color = Purple)
            }
        }
    }

    SpinWheel(prefs, store)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(modifier = Modifier.weight(1f), onClick = { onGo(1) }) { Text("Tasks") }
        Button(modifier = Modifier.weight(1f), onClick = { onGo(2) }) { Text("Games") }
        Button(modifier = Modifier.weight(1f), onClick = { onGo(3) }) { Text("Rewards") }
    }
}

@Composable
fun SpinWheel(prefs: SharedPreferences, store: Store) {
    val today = todayNumber()
    val prizes = listOf(5, 10, 20, 50, 10, 5)
    val colors = listOf(
        Color(0xFF7C4DFF), Color(0xFFFF5A9E), Color(0xFF22D3C5),
        Color(0xFFFFA000), Color(0xFFEF5350), Color(0xFF43A047)
    )
    val sweep = 360f / prizes.size
    val sweepD = 360.0 / prizes.size
    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var spinning by remember { mutableStateOf(false) }
    var spunToday by remember { mutableStateOf(prefs.getInt("spin_day", -1) == today) }
    var message by remember { mutableStateOf("") }

    WhiteCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Daily Spin", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Purple)
            Text("▼", fontSize = 28.sp, color = Purple)
            Box(modifier = Modifier.size(240.dp).rotate(rotation.value)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    prizes.forEachIndexed { i, _ ->
                        drawArc(
                            color = colors[i],
                            startAngle = -90f + i * sweep,
                            sweepAngle = sweep,
                            useCenter = true
                        )
                    }
                }
                prizes.forEachIndexed { i, prize ->
                    val a = Math.toRadians(-90.0 + i * sweepD + sweepD / 2)
                    Text(
                        "$prize",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(x = (80 * cos(a)).dp, y = (80 * sin(a)).dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                enabled = !spinning && !spunToday,
                onClick = {
                    spinning = true
                    spunToday = true
                    message = ""
                    prefs.edit().putInt("spin_day", today).apply()
                    val idx = Random.nextInt(prizes.size)
                    val base = rotation.value
                    val desired = 360f - (idx * sweep + sweep / 2f)
                    val extra = (((desired - (base % 360f)) % 360f) + 360f) % 360f
                    val target = base + 1800f + extra
                    scope.launch {
                        try {
                            rotation.animateTo(target, tween(3500, easing = FastOutSlowInEasing))
                        } finally {
                            spinning = false
                            message = "You won ${prizes[idx]} points!"
                            store.recordSpin()
                            store.earn(prizes[idx], "Daily Spin")
                        }
                    }
                }
            ) {
                Text(
                    if (spinning) "Spinning..."
                    else if (spunToday) "Come back tomorrow"
                    else "SPIN"
                )
            }
            if (message.isNotEmpty()) {
                Text(message, fontWeight = FontWeight.Bold, color = Purple)
            }
        }
    }
}

@Composable
fun TasksScreen(prefs: SharedPreferences, store: Store) {
    val context = LocalContext.current
    val today = todayNumber()
    val tasks = listOf("Daily check-in" to 10, "Read today's tip" to 20, "Share EarnZone" to 30)
    val done = remember {
        mutableStateListOf(
            prefs.getInt("task_0", -1) == today,
            prefs.getInt("task_1", -1) == today,
            prefs.getInt("task_2", -1) == today
        )
    }
    Title("Tasks")
    tasks.forEachIndexed { index, task ->
        WhiteCard {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${task.first} (+${task.second})", modifier = Modifier.weight(1f))
                    Button(
                        enabled = !done[index],
                        onClick = {
                            done[index] = true
                            prefs.edit().putInt("task_$index", today).apply()
                            if (index == 2) {
                                val send = Intent(Intent.ACTION_SEND)
                                send.type = "text/plain"
                                send.putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Try EarnZone! Play games and win rewards. My code: ${store.userId}"
                                )
                                context.startActivity(Intent.createChooser(send, "Share EarnZone"))
                            }
                            store.earn(task.second, task.first)
                        }
                    ) {
                        Text(if (done[index]) "Done" else "Claim")
                    }
                }
                if (index == 1) {
                    Text(tips[today % tips.size], modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
    }
    Text("Tasks reset every day.")
}

@Composable
fun GameScreen(prefs: SharedPreferences, store: Store) {
    val today = todayNumber()
    var plays by remember {
        mutableStateOf(if (prefs.getInt("game_day", -1) == today) prefs.getInt("game_plays", 0) else 0)
    }
    var running by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableStateOf(10) }
    var taps by remember { mutableStateOf(0) }
    var message by remember { mutableStateOf("") }

    LaunchedEffect(running) {
        if (running) {
            timeLeft = 10
            try {
                while (timeLeft > 0) {
                    delay(1000)
                    timeLeft -= 1
                }
            } finally {
                running = false
                val reward = minOf(taps / 10, 20)
                store.recordTaps(taps)
                store.earn(reward, "Tap Rush")
                message = "Time up! Taps: $taps. You got $reward points."
            }
        }
    }

    Title("Games")
    WhiteCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Tap Rush", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Tap as fast as you can for 10 seconds. Every 10 taps = 1 point (max 20).")
            Text("Plays left today: ${3 - plays}")
            if (running) {
                Text("Time left: $timeLeft", fontSize = 18.sp)
                Text("Taps: $taps", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Purple)
                Button(
                    onClick = { taps += 1 },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {
                    Text("TAP!", fontSize = 32.sp)
                }
            } else {
                Button(
                    enabled = plays < 3,
                    onClick = {
                        taps = 0
                        message = ""
                        plays += 1
                        prefs.edit().putInt("game_day", today).putInt("game_plays", plays).apply()
                        running = true
                    }
                ) {
                    Text(if (plays < 3) "Start game" else "Come back tomorrow")
                }
            }
            if (message.isNotEmpty()) {
                Text(message, fontWeight = FontWeight.Bold, color = Purple)
            }
        }
    }
    Text("Daily Quiz and more games are coming in the next update.")
}

@Composable
fun RewardsScreen(store: Store) {
    var pending by remember { mutableStateOf<RewardItem?>(null) }
    var message by remember { mutableStateOf("") }

    Title("Rewards Store")
    WhiteCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Your points")
            Text("${store.points}", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Purple)
            Text("Rewards work only inside EarnZone. They cannot be exchanged for money or transferred to others.")
        }
    }
    rewardItems.forEach { item ->
        val isOwned = store.isOwned(item.id)
        WhiteCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.name, fontWeight = FontWeight.Bold)
                    Text("${item.cost} points", color = Gold)
                }
                Button(
                    enabled = !isOwned,
                    onClick = { pending = item }
                ) {
                    Text(if (isOwned) "Owned" else "Buy")
                }
            }
        }
    }
    if (message.isNotEmpty()) {
        Text(message, fontWeight = FontWeight.Bold, color = Purple)
    }

    val p = pending
    if (p != null) {
        AlertDialog(
            onDismissRequest = { pending = null },
            title = { Text("Confirm") },
            text = { Text("Buy ${p.name} for ${p.cost} points?") },
            confirmButton = {
                TextButton(onClick = {
                    message = store.buy(p)
                    pending = null
                }) { Text("Buy") }
            },
            dismissButton = {
                TextButton(onClick = { pending = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun ProfileScreen(store: Store, onReset: () -> Unit) {
    val context = LocalContext.current
    val debuggable = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    var dialog by remember { mutableStateOf("") }

    Title("Profile")
    WhiteCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Purple),
                contentAlignment = Alignment.Center
            ) {
                Text("P", color = Gold, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("EarnZone Player", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("User ID: ${store.userId}")
                Text("Level ${store.level} (${store.levelName})")
                Text("Total points: ${store.points}")
            }
        }
    }

    WhiteCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Achievements", fontWeight = FontWeight.Bold)
            store.badges().forEach { b ->
                Text(if (b.second) "🏅 ${b.first}" else "🔒 ${b.first}")
            }
        }
    }

    WhiteCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("My rewards", fontWeight = FontWeight.Bold)
            val mine = rewardItems.filter { store.isOwned(it.id) }
            if (mine.isEmpty()) {
                Text("None yet. Visit the Rewards tab.")
            } else {
                mine.forEach { Text("✔ ${it.name}") }
            }
        }
    }

    WhiteCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Invite friends", fontWeight = FontWeight.Bold)
            Text("Your referral code: ${store.userId}")
            Text("Referral rewards will be added in a later update.")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                val send = Intent(Intent.ACTION_SEND)
                send.type = "text/plain"
                send.putExtra(
                    Intent.EXTRA_TEXT,
                    "Try EarnZone! Play games and win rewards. My code: ${store.userId}"
                )
                context.startActivity(Intent.createChooser(send, "Share EarnZone"))
            }) {
                Text("Share EarnZone")
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(modifier = Modifier.weight(1f), onClick = { dialog = "privacy" }) { Text("Privacy") }
        Button(modifier = Modifier.weight(1f), onClick = { dialog = "terms" }) { Text("Terms") }
        Button(modifier = Modifier.weight(1f), onClick = { dialog = "help" }) { Text("Help") }
    }

    Text("EarnZone version 1.2")

    if (debuggable) {
        Button(onClick = onReset) {
            Text("Reset Testing Data")
        }
    }

    Title("Points History")
    val rows = store.history.lines().filter { it.isNotEmpty() }.take(20)
    if (rows.isEmpty()) {
        Text("No activity yet.")
    }
    rows.forEach { line ->
        val p = line.split("|")
        if (p.size == 4) {
            val n = p[3].toIntOrNull() ?: 0
            WhiteCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(p[2], fontWeight = FontWeight.Bold)
                        Text("${p[1]}  |  ${p[0]}", fontSize = 12.sp)
                    }
                    Text(
                        if (n > 0) "+$n" else "$n",
                        fontWeight = FontWeight.Bold,
                        color = if (n > 0) Green else Red
                    )
                }
            }
        }
    }

    if (dialog.isNotEmpty()) {
        val dialogTitle = when (dialog) {
            "privacy" -> "Privacy Policy"
            "terms" -> "Terms & Conditions"
            else -> "Help & Support"
        }
        val dialogText = when (dialog) {
            "privacy" -> "EarnZone keeps your game data only on this phone. A full privacy policy will be added before public release."
            "terms" -> "Points are in-app rewards only. They have no cash value and cannot be transferred. Full terms will be added before public release."
            else -> "Need help? A support option will be added in a later update."
        }
        AlertDialog(
            onDismissRequest = { dialog = "" },
            title = { Text(dialogTitle) },
            text = { Text(dialogText) },
            confirmButton = {
                TextButton(onClick = { dialog = "" }) { Text("OK") }
            }
        )
    }
}
