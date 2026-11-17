package com.earnzone.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

private val Purple = Color(0xFF5428D8)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        val prefs = getSharedPreferences("earnzone", Context.MODE_PRIVATE)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(primary = Purple)) {
                EarnZoneApp(
                    initialPoints = prefs.getInt("points", 0),
                    onSave = { prefs.edit().putInt("points", it).apply() }
                )
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
fun EarnZoneApp(initialPoints: Int, onSave: (Int) -> Unit) {
    var points by remember { mutableStateOf(initialPoints) }
    var tab by remember { mutableStateOf(0) }
    val done = remember { mutableStateListOf(false, false, false) }
    val tasks = listOf("Daily check-in" to 10, "Read today's tip" to 20, "Share the app" to 30)

    Scaffold(
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
                        icon = { Icon(Icons.Filled.AccountBalanceWallet, contentDescription = "Wallet") },
                        label = { Text("Wallet") }
                    )
                    NavigationBarItem(
                        selected = tab == 3,
                        onClick = { tab = 3 },
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
            when (tab) {
                0 -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Welcome to EarnZone", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Purple)
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Your points")
                            Text("$points", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Purple)
                        }
                    }
                    Text("Open the Tasks tab to collect points.")
                }

                1 -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Tasks", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Purple)
                    tasks.forEachIndexed { index, task ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${task.first} (+${task.second})")
                                Button(
                                    enabled = !done[index],
                                    onClick = {
                                        done[index] = true
                                        points += task.second
                                        onSave(points)
                                    }
                                ) {
                                    Text(if (done[index]) "Done" else "Claim")
                                }
                            }
                        }
                    }
                }

                2 -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Wallet", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Purple)
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Total points")
                            Text("$points", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Purple)
                        }
                    }
                    Text("Test mode: these points are not real money.")
                }

                else -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Purple)
                    Text("EarnZone version 1.0")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        points = 0
                        onSave(0)
                        for (i in done.indices) done[i] = false
                    }) {
                        Text("Reset points")
                    }
                }
            }
        }
    }
}
