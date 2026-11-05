package com.findme.app

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findme.app.presentation.ui.theme.FindMeTheme
import com.findme.app.service.AlarmManagerHelper
import com.findme.app.service.SoundDetectionService

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestRequiredPermissions()

        setContent {
            FindMeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainDashboardScreen(
                        onStartService = { startSoundService() },
                        onStopService = { stopSoundService() },
                        onTestAlarm = { testAlarm() }
                    )
                }
            }
        }
    }

    private fun requestRequiredPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        requestPermissionLauncher.launch(permissions.toTypedArray())
    }

    private fun startSoundService() {
        val intent = Intent(this, SoundDetectionService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun stopSoundService() {
        val intent = Intent(this, SoundDetectionService::class.java)
        stopService(intent)
    }

    private fun testAlarm() {
        AlarmManagerHelper.playAlarm(this)
    }
}

@Composable
fun MainDashboardScreen(
    onStartService: () -> Unit,
    onStopService: () -> Unit,
    onTestAlarm: () -> Unit
) {
    var isMonitoring by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "FindMe Security",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00E5FF)
        )
        Text(
            text = "نظام استعادة وحماية الهاتف المفقود",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isMonitoring) "🟢 الحماية نشطة في الخلفية" else "🔴 الحماية متوقفة",
                    color = if (isMonitoring) Color.Green else Color.Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "التطبيق يراقب أصوات التصفيق والاتصالات عن بُعد بأمان.",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            }
        }

        Button(
            onClick = {
                if (isMonitoring) {
                    onStopService()
                    isMonitoring = false
                } else {
                    onStartService()
                    isMonitoring = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isMonitoring) Color.Red else Color(0xFF00E5FF)
            )
        ) {
            Text(
                text = if (isMonitoring) "إيقاف المراقبة الحية" else "تفعيل الحماية الشاملة",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { onTestAlarm() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E5FF))
        ) {
            Text(text = "اختبار صفارة الإنذار")
        }
    }
}
