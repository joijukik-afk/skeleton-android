package net.marcoromano.skeleton.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // فعال‌سازی راست‌چین (RTL) برای زبان فارسی
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                MaterialTheme {
                    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF5F5F5)) {
                        AppNavigation()
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf("home") }
    when (currentScreen) {
        "home" -> HomeScreen(onNavigateToBmi = { currentScreen = "bmi" })
        "bmi" -> BmiScreen(onBack = { currentScreen = "home" })
    }
}

// ---------- صفحه اصلی (منو) ----------
@Composable
fun HomeScreen(onNavigateToBmi: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "دستیار تغذیه",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "نسخه اولیه (MVP)",
            fontSize = 16.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onNavigateToBmi,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
        ) {
            Text("محاسبه BMI", fontSize = 18.sp, color = Color.White)
        }
    }
}

// ---------- صفحه محاسبه BMI ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BmiScreen(onBack: () -> Unit) {
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var bmiResult by remember { mutableStateOf<Double?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("محاسبه BMI") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("بازگشت", fontSize = 14.sp)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it; errorMessage = null },
                label = { Text("وزن (کیلوگرم)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = height,
                onValueChange = { height = it; errorMessage = null },
                label = { Text("قد (سانتی‌متر)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorMessage!!, color = Color.Red, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val w = weight.toDoubleOrNull()
                    val h = height.toDoubleOrNull()
                    if (w == null || h == null || w <= 0 || h <= 0) {
                        errorMessage = "لطفاً وزن و قد معتبر وارد کنید."
                        bmiResult = null
                    } else {
                        val hM = h / 100.0
                        bmiResult = w / (hM * hM)
                        errorMessage = null
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text("محاسبه کن", fontSize = 18.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // نمایش نتیجه به صورت کارت رنگی
            bmiResult?.let { bmi ->
                val (status, color) = when {
                    bmi < 18.5 -> "کمبود وزن / لاغر" to Color(0xFFFF9800) // نارنجی
                    bmi in 18.5..24.9 -> "وزن طبیعی" to Color(0xFF4CAF50) // سبز
                    bmi in 25.0..29.9 -> "اضافه وزن" to Color(0xFFFFEB3B) // زرد
                    else -> "چاقی" to Color(0xFFF44336) // قرمز
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.2f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "شاخص توده بدنی (BMI)",
                            fontSize = 16.sp,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = String.format("%.1f", bmi),
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = color
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "وضعیت: $status",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = color
                        )
                    }
                }
            }
        }
    }
}
