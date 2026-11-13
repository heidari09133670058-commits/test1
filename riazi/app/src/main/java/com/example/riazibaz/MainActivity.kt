package com.example.riazibaz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { RiaziBazApp() }
    }
}

@Composable
fun RiaziBazApp() {
    var topic by remember { mutableStateOf("خانه") }
    var score by remember { mutableIntStateOf(0) }
    MaterialTheme {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            CompositionLocalProvider(LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
                if (topic == "خانه") {
                    Column(
                        Modifier.fillMaxSize().padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("🌟 ریاضی‌باز 🌟", fontSize = 32.sp, fontWeight = FontWeight.Bold)
                        Text("بازی و یادگیری ریاضی برای کوچولوها", fontSize = 17.sp)
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                            Text("⭐ امتیاز شما: $score", Modifier.padding(16.dp), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }
                        val topics = listOf("شمارش", "جمع", "تفریق", "شکل‌ها")
                        LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(topics) { item ->
                                Button(onClick = { topic = item }, modifier = Modifier.height(100.dp), shape = RoundedCornerShape(20.dp)) {
                                    Text(item, fontSize = 23.sp)
                                }
                            }
                        }
                    }
                } else {
                    GameScreen(topic = topic, onBack = { topic = "خانه" }, onCorrect = { score++ })
                }
            }
        }
    }
}

@Composable
fun GameScreen(topic: String, onBack: () -> Unit, onCorrect: () -> Unit) {
    var a by remember { mutableIntStateOf(Random.nextInt(1, 6)) }
    var b by remember { mutableIntStateOf(Random.nextInt(1, 6)) }
    var selected by remember { mutableStateOf<Int?>(null) }
    var answered by remember { mutableStateOf(false) }
    val answer = when (topic) {
        "جمع" -> a + b
        "تفریق" -> maxOf(a, b) - minOf(a, b)
        else -> a
    }
    val symbol = if (topic == "جمع") "+" else "−"
    val prompt = when (topic) {
        "شمارش" -> "تعداد ستاره‌ها چندتاست؟"
        "شکل‌ها" -> "کدام شکل را می‌بینی؟"
        "جمع", "تفریق" -> "$a $symbol $b = ؟"
        else -> "جواب را انتخاب کن"
    }
    val options = remember(a, b, topic) {
        if (topic == "شکل‌ها") listOf("دایره", "مربع", "مثلث")
        else if (topic == "شمارش") listOf(a, a + 1, maxOf(1, a - 1)).shuffled()
        else (listOf(answer, answer + 1, maxOf(0, answer - 1))).distinct().shuffled()
    }
    Column(Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            TextButton(onClick = onBack) { Text("← بازگشت") }
        }
        Text(topic, fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Text(prompt, fontSize = 24.sp, textAlign = TextAlign.Center)
        if (topic == "شمارش") Text("⭐ ".repeat(a), fontSize = 34.sp)
        if (topic == "شکل‌ها") Text("🔵   🟦   🔺", fontSize = 38.sp)
        options.forEach { option ->
            Button(
                onClick = {
                    if (!answered) {
                        selected = if (option is Int) option else null
                        answered = true
                        val correct = if (topic == "شکل‌ها") option == "دایره" else option == answer
                        if (correct) onCorrect()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(58.dp)
            ) { Text(option.toString(), fontSize = 22.sp) }
        }
        if (answered) {
            val correct = if (topic == "شکل‌ها") selected == null else selected == answer
            Text(if (correct) "آفرین! عالی بود 🎉" else "اشکالی نداره، دوباره تلاش کن 💛", fontSize = 21.sp)
            Button(onClick = {
                a = Random.nextInt(1, 6); b = Random.nextInt(1, 6)
                selected = null; answered = false
            }) { Text("سؤال بعدی") }
        }
    }
}
