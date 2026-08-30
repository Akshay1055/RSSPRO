package `in`.swayamsevak.app.ui.features.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.swayamsevak.app.Screen
import `in`.swayamsevak.app.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dashboard(go: (Screen) -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text("होम", fontWeight = FontWeight.Bold) }, actions = { IconButton({}) { Icon(Icons.Default.NotificationsNone, null) } }) }) { pad ->
        LazyColumn(Modifier.padding(pad).padding(16.dp)) {
            item {
                Text("नमस्ते, अमित शर्मा", fontSize = 24.sp, fontWeight = FontWeight.Black)
                Text("स्वयंसेवक • SVY-2026-001245", color = Grey)
                Spacer(Modifier.height(16.dp))
                SectionCard("पंजीयन स्थिति", Icons.Default.Verified) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.HourglassTop, null, tint = Saffron)
                        Spacer(Modifier.width(10.dp))
                        Text("सत्यापन लंबित", fontWeight = FontWeight.Bold)
                    }
                }
                val tiles = listOf(
                    "मेरा प्रोफ़ाइल" to Icons.Default.Person,
                    "उपस्थिति" to Icons.Default.EventAvailable,
                    "घोषणाएँ" to Icons.Default.Campaign,
                    "गतिविधियाँ" to Icons.Default.DirectionsRun,
                    "मतदान" to Icons.Default.HowToVote,
                    "सुझाव" to Icons.Default.Lightbulb
                )
                tiles.chunked(2).forEach { row ->
                    Row(Modifier.fillMaxWidth()) {
                        row.forEach { (title, icon) ->
                            Card(
                                Modifier.weight(1f).padding(5.dp).height(105.dp).clickable {
                                    when (title) {
                                        "मेरा प्रोफ़ाइल" -> go(Screen.Profile)
                                        "उपस्थिति" -> go(Screen.Attendance)
                                        "मतदान" -> go(Screen.Poll)
                                    }
                                },
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Border)
                            ) {
                                Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
                                    Icon(icon, null, tint = Saffron)
                                    Spacer(Modifier.height(6.dp))
                                    Text(title, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                SectionCard("आज का विचार", Icons.Default.FormatQuote) {
                    Text("निरंतर प्रयास ही सफलता का आधार है।")
                }
            }
        }
    }
}

@Composable
fun Profile() {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item {
            SectionCard("व्यक्तिगत जानकारी", Icons.Default.Person) {
                Info("नाम", "अमित शर्मा"); Info("मोबाइल", "9876543210"); Info("जन्म दिनांक", "15/08/1995")
            }
            SectionCard("संगठन जानकारी", Icons.Default.AccountTree) {
                Info("महानगर", "इंदौर"); Info("नगर", "पश्चिम नगर"); Info("बस्ती", "विजय नगर बस्ती")
                Info("शाखा", "विजय नगर शाखा"); Info("मोहल्ला", "विजय नगर"); Info("भूमिका", "स्वयंसेवक")
            }
        }
    }
}

@Composable
fun Attendance() {
    val names = listOf("राहुल", "अमित", "विवेक", "सुरेश")
    val selected = remember { mutableStateListOf("राहुल", "अमित", "सुरेश") }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item { Text("विजय नगर शाखा", fontSize = 22.sp, fontWeight = FontWeight.Black) }
        items(names) { n ->
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(Color.White)) {
                Row(Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(selected.contains(n), { if (it) selected.add(n) else selected.remove(n) }, colors = CheckboxDefaults.colors(checkedColor = Saffron))
                    Text(n)
                }
            }
        }
        item { Text("उपस्थित: ${selected.size} / ${names.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
    }
}

@Composable
fun Poll() {
    var choice by remember { mutableStateOf("प्रातः 6:30") }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        SectionCard("शाखा समय संबंधी सुझाव", Icons.Default.HowToVote) {
            Text("आपकी शाखा के लिए कौन सा समय उचित रहेगा?", fontWeight = FontWeight.Bold)
            listOf("प्रातः 6:00", "प्रातः 6:30", "प्रातः 7:00", "सायं 6:00").forEach {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(choice == it, { choice = it }, colors = RadioButtonDefaults.colors(selectedColor = Saffron))
                    Text(it)
                }
            }
        }
        PrimaryButton("मत सुरक्षित करें", Icons.Default.Check) {}
    }
}
