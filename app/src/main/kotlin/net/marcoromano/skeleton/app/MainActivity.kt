package net.marcoromano.skeleton.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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
    var screen by remember { mutableStateOf("home") }
    when (screen) {
        "home" -> HomeScreen(
            onBmi = { screen = "bmi" },
            onIbw = { screen = "ibw" },
            onEnergy = { screen = "energy" },
            onMacro = { screen = "macro" },
            onPregnancy = { screen = "pregnancy" },
            onLactation = { screen = "lactation" }
        )
        "bmi" -> BmiScreen(onBack = { screen = "home" })
        "ibw" -> IbwScreen(onBack = { screen = "home" })
        "energy" -> EnergyScreen(onBack = { screen = "home" })
        "macro" -> MacroScreen(onBack = { screen = "home" })
        "pregnancy" -> PregnancyScreen(onBack = { screen = "home" })
        "lactation" -> LactationScreen(onBack = { screen = "home" })
    }
}

// ==================== رنگ‌ها ====================
val GreenMain = Color(0xFF2E7D32)

// ==================== صفحه اصلی ====================
@Composable
fun HomeScreen(
    onBmi: () -> Unit, onIbw: () -> Unit, onEnergy: () -> Unit,
    onMacro: () -> Unit, onPregnancy: () -> Unit, onLactation: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))
        Text("دستیار تغذیه", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = GreenMain)
        Text("نسخه ۲ - بر اساس جزوه تغذیه بالینی", fontSize = 13.sp, color = Color.Gray)
        Spacer(Modifier.height(6.dp))
        Text("دانشگاه علوم پزشکی شهید بهشتی", fontSize = 11.sp, color = Color.Gray)
        Spacer(Modifier.height(30.dp))

        MenuButton("۱. محاسبه BMI") { onBmi() }
        MenuButton("۲. وزن ایده‌آل (IBW + AIBW)") { onIbw() }
        MenuButton("۳. انرژی مورد نیاز (BMR + TEE)") { onEnergy() }
        MenuButton("۴. درشت‌مغذی‌ها (پروتئین، کربوهیدرات، چربی)") { onMacro() }
        MenuButton("۵. انرژی بارداری") { onPregnancy() }
        MenuButton("۶. انرژی شیردهی") { onLactation() }

        Spacer(Modifier.height(20.dp))
        Text(
            "⚠ این نرم‌افزار ابزار محاسباتی برای متخصص تغذیه است و نتایج باید توسط کارشناس تأیید شود.",
            fontSize = 11.sp, color = Color.Red, modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun MenuButton(title: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).height(60.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = GreenMain)
    ) {
        Text(title, fontSize = 15.sp, color = Color.White, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun BackButton(onBack: () -> Unit) {
    TextButton(onClick = onBack) { Text("← بازگشت به منو", color = GreenMain, fontSize = 14.sp) }
}

@Composable
fun Field(
    value: String, onChange: (String) -> Unit, label: String
) {
    OutlinedTextField(
        value = value, onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true
    )
}

@Composable
fun ResultCard(title: String, value: String, color: Color = GreenMain, subtitle: String = "") {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontSize = 13.sp, color = Color.DarkGray)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = color)
            if (subtitle.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(subtitle, fontSize = 14.sp, color = color, fontWeight = FontWeight.Medium)
            }
        }
    }
}

// ==================== BMI ====================
@Composable
fun BmiScreen(onBack: () -> Unit) {
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var bmi by remember { mutableStateOf<Double?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackButton(onBack)
        Text("محاسبه BMI", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = GreenMain)
        Text("فرمول: BMI = وزن (kg) ÷ (قد (m))²", fontSize = 12.sp, color = Color.Gray)
        Spacer(Modifier.height(12.dp))

        Field(weight, { weight = it }, "وزن (کیلوگرم)")
        Field(height, { height = it }, "قد (سانتی‌متر)")

        Button(
            onClick = {
                val w = weight.toDoubleOrNull(); val h = height.toDoubleOrNull()
                if (w != null && h != null && w > 0 && h > 0) {
                    val hM = h / 100.0; bmi = w / (hM * hM)
                }
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenMain)
        ) { Text("محاسبه", color = Color.White, fontSize = 16.sp) }

        bmi?.let { b ->
            val (status, color) = when {
                b < 18.5 -> "کمبود وزن / لاغر" to Color(0xFFFF9800)
                b in 18.5..24.9 -> "وزن طبیعی" to Color(0xFF4CAF50)
                b in 25.0..29.9 -> "اضافه وزن" to Color(0xFFFFEB3B)
                else -> "چاقی" to Color(0xFFF44336)
            }
            Spacer(Modifier.height(16.dp))
            ResultCard("شاخص توده بدنی", String.format("%.1f", b), color, "وضعیت: $status")
        }
    }
}

// ==================== IBW + AIBW ====================
@Composable
fun IbwScreen(onBack: () -> Unit) {
    var gender by remember { mutableStateOf("male") }
    var height by remember { mutableStateOf("") }
    var currentWeight by remember { mutableStateOf("") }
    var ibw by remember { mutableStateOf<Double?>(null) }
    var aibw by remember { mutableStateOf<Double?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackButton(onBack)
        Text("وزن ایده‌آل (Hamwi)", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = GreenMain)
        Text("منبع: جزوه تغذیه بالینی، جلد اول، صفحه ۹", fontSize = 11.sp, color = Color.Gray)
        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = gender == "male", onClick = { gender = "male" })
            Text("مرد", modifier = Modifier.padding(end = 16.dp))
            RadioButton(selected = gender == "female", onClick = { gender = "female" })
            Text("زن")
        }

        Field(height, { height = it }, "قد (سانتی‌متر)")
        Field(currentWeight, { currentWeight = it }, "وزن فعلی (کیلوگرم) - برای AIBW")

        Text(
            "فرمول Hamwi:\n" +
            "مرد: IBW = 48.1 + 1.1 × (قد - 152)\n" +
            "زن: IBW = 45.5 + 0.9 × (قد - 152)\n" +
            "AIBW = IBW + [(وزن فعلی - IBW) × 0.25]",
            fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(8.dp)
        )

        Button(
            onClick = {
                val h = height.toDoubleOrNull()
                val w = currentWeight.toDoubleOrNull()
                if (h != null && h > 0) {
                    val ideal = if (gender == "male") 48.1 + 1.1 * (h - 152)
                                else 45.5 + 0.9 * (h - 152)
                    ibw = ideal
                    if (w != null && w > ideal) {
                        aibw = ideal + (w - ideal) * 0.25
                    } else { aibw = null }
                }
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenMain)
        ) { Text("محاسبه", color = Color.White, fontSize = 16.sp) }

        ibw?.let {
            Spacer(Modifier.height(16.dp))
            ResultCard("وزن ایده‌آل (IBW)", String.format("%.1f", it), GreenMain, "کیلوگرم")
            aibw?.let { a ->
                ResultCard("وزن ایده‌آل تطبیق‌یافته (AIBW)", String.format("%.1f", a), Color(0xFF1565C0),
                    "مورد استفاده در بیماران دارای اضافه وزن/چاقی")
            }
        }
    }
}

// ==================== انرژی (BMR + TEE) ====================
@Composable
fun EnergyScreen(onBack: () -> Unit) {
    var gender by remember { mutableStateOf("male") }
    var method by remember { mutableStateOf("simple") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var activity by remember { mutableStateOf(0.30) }
    var bmr by remember { mutableStateOf<Double?>(null) }
    var activityKcal by remember { mutableStateOf<Double?>(null) }
    var tef by remember { mutableStateOf<Double?>(null) }
    var total by remember { mutableStateOf<Double?>(null) }
    var forLoss by remember { mutableStateOf<Double?>(null) }

    val activities = listOf(
        0.30 to "خیلی سبک (کم‌تحرک، اداری) - ۳۰٪",
        0.50 to "سبک - ۵۰٪",
        0.80 to "متوسط - ۸۰٪",
        1.00 to "سنگین - ۱۰۰٪",
        2.50 to "خیلی سنگین (ورزشکار حرفه‌ای) - ۲۵۰٪"
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackButton(onBack)
        Text("انرژی مورد نیاز", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = GreenMain)
        Text("منبع: جلد اول، صفحات ۱۰-۱۴", fontSize = 11.sp, color = Color.Gray)
        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = gender == "male", onClick = { gender = "male" })
            Text("مرد", modifier = Modifier.padding(end = 16.dp))
            RadioButton(selected = gender == "female", onClick = { gender = "female" })
            Text("زن")
        }

        Text("روش محاسبه BMR:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
        listOf(
            "simple" to "فرمول ساده (وزن × ۲۴)",
            "mifflin" to "میفلین (Mifflin)",
            "harris" to "هریس-بندیکت"
        ).forEach { (key, label) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = method == key, onClick = { method = key })
                Text(label, fontSize = 13.sp)
            }
        }

        Field(weight, { weight = it }, "وزن (کیلوگرم)")
        if (method != "simple") {
            Field(height, { height = it }, "قد (سانتی‌متر)")
            Field(age, { age = it }, "سن (سال)")
        }

        Spacer(Modifier.height(8.dp))
        Text("سطح فعالیت:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
        activities.forEach { (factor, label) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = activity == factor, onClick = { activity = factor })
                Text(label, fontSize = 12.sp)
            }
        }

        Button(
            onClick = {
                val w = weight.toDoubleOrNull() ?: return@Button
                val h = height.toDoubleOrNull() ?: 0.0
                val a = age.toDoubleOrNull() ?: 0.0
                val b = when (method) {
                    "simple" -> if (gender == "male") w * 24 else w * 0.95 * 24
                    "mifflin" -> if (gender == "male") 10 * w + 6.25 * h - 5 * a + 5
                                else 10 * w + 6.25 * h - 5 * a - 161
                    else -> if (gender == "male") 66 + 13.7 * w + 5 * h - 6.8 * a
                            else 655 + 9.6 * w + 1.8 * h - 4.7 * a
                }
                bmr = b
                activityKcal = b * activity
                val t = (b + (b * activity)) * 0.10
                tef = t
                total = b + (b * activity) + t
                forLoss = total!! - 500
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenMain)
        ) { Text("محاسبه انرژی", color = Color.White, fontSize = 16.sp) }

        bmr?.let {
            Spacer(Modifier.height(16.dp))
            ResultCard("BMR (متابولیسم پایه)", "${it.roundToInt()} kcal/day", Color(0xFF1565C0))
            activityKcal?.let { a ->
                ResultCard("انرژی فعالیت بدنی", "${a.roundToInt()} kcal/day", Color(0xFF6A1B9A))
            }
            tef?.let { t ->
                ResultCard("اثر گرمازایی غذا (TEF)", "${t.roundToInt()} kcal/day", Color(0xFFFF9800))
            }
            total?.let { tot ->
                ResultCard("کل انرژی مورد نیاز", "${tot.roundToInt()} kcal/day", GreenMain)
                forLoss?.let { fl ->
                    ResultCard("برای کاهش وزن (۵۰۰ kcal کمتر)", "${fl.roundToInt()} kcal/day", Color(0xFFF44336))
                }
            }
        }
    }
}

// ==================== ماکرومغذی‌ها ====================
@Composable
fun MacroScreen(onBack: () -> Unit) {
    var totalCal by remember { mutableStateOf("") }
    var proteinPct by remember { mutableStateOf("15") }
    var carbPct by remember { mutableStateOf("55") }
    var fatPct by remember { mutableStateOf("30") }

    var p by remember { mutableStateOf<Double?>(null) }
    var c by remember { mutableStateOf<Double?>(null) }
    var f by remember { mutableStateOf<Double?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackButton(onBack)
        Text("درشت‌مغذی‌ها", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = GreenMain)
        Text("منبع: جلد اول، صفحه ۲۷", fontSize = 11.sp, color = Color.Gray)
        Spacer(Modifier.height(8.dp))

        Field(totalCal, { totalCal = it }, "کل انرژی (کیلوکالری)")
        Field(proteinPct, { proteinPct = it }, "درصد پروتئین (مثلاً ۱۵)")
        Field(carbPct, { carbPct = it }, "درصد کربوهیدرات (مثلاً ۵۵)")
        Field(fatPct, { fatPct = it }, "درصد چربی (مثلاً ۳۰)")

        Text("تبدیل: پروتئین و کربوهیدرات ÷ ۴ ، چربی ÷ ۹",
            fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(8.dp))

        Button(
            onClick = {
                val cal = totalCal.toDoubleOrNull() ?: return@Button
                val pp = proteinPct.toDoubleOrNull() ?: 15.0
                val cp = carbPct.toDoubleOrNull() ?: 55.0
                val fp = fatPct.toDoubleOrNull() ?: 30.0
                p = cal * pp / 100.0 / 4.0
                c = cal * cp / 100.0 / 4.0
                f = cal * fp / 100.0 / 9.0
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenMain)
        ) { Text("محاسبه", color = Color.White, fontSize = 16.sp) }

        p?.let {
            Spacer(Modifier.height(16.dp))
            ResultCard("پروتئین", "${it.roundToInt()} g/day", Color(0xFF1565C0))
            ResultCard("کربوهیدرات", "${c!!.roundToInt()} g/day", Color(0xFF6A1B9A))
            ResultCard("چربی", "${f!!.roundToInt()} g/day", Color(0xFFFF9800))
        }
    }
}

// ==================== بارداری ====================
@Composable
fun PregnancyScreen(onBack: () -> Unit) {
    var baseCal by remember { mutableStateOf("") }
    var trimester by remember { mutableStateOf(1) }
    var fetuses by remember { mutableStateOf(1) }
    var result by remember { mutableStateOf<Double?>(null) }
    var proteinExtra by remember { mutableStateOf(0) }

    val extraCal = mapOf(1 to 0, 2 to 340, 3 to 452)

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackButton(onBack)
        Text("انرژی بارداری", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = GreenMain)
        Text("منبع: جلد اول، صفحه ۴۰", fontSize = 11.sp, color = Color.Gray)
        Spacer(Modifier.height(8.dp))

        Field(baseCal, { baseCal = it }, "انرژی پایه (بر اساس وزن قبل از بارداری)")

        Text("سه ماهه:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
        listOf(1 to "اول (بدون افزایش)", 2 to "دوم (+۳۴۰)", 3 to "سوم (+۴۵۲)").forEach { (t, label) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = trimester == t, onClick = { trimester = t })
                Text(label, fontSize = 13.sp)
            }
        }

        Text("تعداد جنین:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
        listOf(1, 2, 3).forEach { n ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = fetuses == n, onClick = { fetuses = n })
                Text("$n جنین", fontSize = 13.sp)
            }
        }

        Button(
            onClick = {
                val base = baseCal.toDoubleOrNull() ?: return@Button
                val add = extraCal[trimester] ?: 0
                result = base + add
                proteinExtra = 25 * fetuses
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenMain)
        ) { Text("محاسبه", color = Color.White, fontSize = 16.sp) }

        result?.let {
            Spacer(Modifier.height(16.dp))
            ResultCard("کل انرژی مورد نیاز", "${it.roundToInt()} kcal/day", GreenMain,
                "پروتئین اضافه: $proteinExtra گرم در روز")
        }
    }
}

// ==================== شیردهی ====================
@Composable
fun LactationScreen(onBack: () -> Unit) {
    var baseCal by remember { mutableStateOf("") }
    var period by remember { mutableStateOf(1) }
    var babies by remember { mutableStateOf(1) }
    var result by remember { mutableStateOf<Double?>(null) }

    val extraCal = mapOf(1 to 330, 2 to 400)

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackButton(onBack)
        Text("انرژی شیردهی", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = GreenMain)
        Text("منبع: جلد اول، صفحه ۱۲۶", fontSize = 11.sp, color = Color.Gray)
        Spacer(Modifier.height(8.dp))

        Field(baseCal, { baseCal = it }, "انرژی پایه (بر اساس وزن فعلی)")

        Text("دوره شیردهی:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
        listOf(1 to "شش ماهه اول (+۳۳۰)", 2 to "شش ماهه دوم (+۴۰۰)").forEach { (p, label) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = period == p, onClick = { period = p })
                Text(label, fontSize = 13.sp)
            }
        }

        Text("تعداد نوزاد:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
        listOf(1, 2, 3).forEach { n ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = babies == n, onClick = { babies = n })
                Text("$n نوزاد", fontSize = 13.sp)
            }
        }

        Button(
            onClick = {
                val base = baseCal.toDoubleOrNull() ?: return@Button
                val add = extraCal[period] ?: 0
                // در جزوه: برای هر کودک اضافی ۲۵ گرم پروتئین و مقدار بیشتری انرژی
                val babyFactor = if (babies > 2) 1.2 else 1.0
                result = base + add * babyFactor
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenMain)
        ) { Text("محاسبه", color = Color.White, fontSize = 16.sp) }

        result?.let {
            Spacer(Modifier.height(16.dp))
            ResultCard("کل انرژی مورد نیاز", "${it.roundToInt()} kcal/day", GreenMain,
                "پروتئین اضافه: ${25 * babies} گرم در روز")
        }
    }
}
