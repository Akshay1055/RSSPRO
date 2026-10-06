package `in`.swayamsevak.app.ui.features.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.swayamsevak.app.ui.components.Border
import `in`.swayamsevak.app.ui.components.Cream
import `in`.swayamsevak.app.ui.components.Grey
import `in`.swayamsevak.app.ui.components.Navy
import `in`.swayamsevak.app.ui.components.PrimaryButton
import `in`.swayamsevak.app.ui.components.Saffron
import `in`.swayamsevak.app.ui.components.SectionCard
import `in`.swayamsevak.app.ui.components.Info

data class RssTopic(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val overview: String,
    val highlights: List<String>
)

val rssTopics = listOf(
    RssTopic(
        "RSS प्रार्थना",
        "दैनिक शाखा की प्रेरक प्रार्थना",
        Icons.Default.AccountBalance,
        "प्रार्थना शाखा के समापन पर सामूहिक भाव और राष्ट्र के प्रति समर्पण को अभिव्यक्त करती है।",
        listOf("शांत, स्पष्ट उच्चारण", "सामूहिक सहभागिता", "अर्थ समझकर स्मरण")
    ),
    RssTopic(
        "शाखा पद्धति",
        "दैनिक शाखा का सरल क्रम",
        Icons.Default.MenuBook,
        "शाखा में समयबद्ध शारीरिक, बौद्धिक और सामूहिक गतिविधियों का संतुलित क्रम होता है।",
        listOf("एकत्रीकरण", "व्यायाम व खेल", "बौद्धिक चर्चा", "प्रार्थना")
    ),
    RssTopic(
        "गीत",
        "प्रेरक और सामूहिक गीत",
        Icons.Default.MusicNote,
        "गीतों के माध्यम से प्रेरणा, संस्कार और सामूहिकता का अनुभव साझा किया जाता है।",
        listOf("गीत का भाव समझें", "लय के साथ गाएँ", "नए गीत सीखें")
    ),
    RssTopic(
        "अमृत वचन",
        "विचार और प्रेरक कथन",
        Icons.Default.FormatQuote,
        "छोटे विचार, प्रसंग और सूत्र जो सेवा, अनुशासन और समाज-भाव को मजबूत करते हैं।",
        listOf("दिन का विचार", "चर्चा के प्रश्न", "जीवन में प्रयोग")
    ),
    RssTopic(
        "शाखा खेल",
        "सहयोग, कौशल और आनंद",
        Icons.Default.EmojiEvents,
        "खेल शाखा को जीवंत बनाते हैं और सहयोग, नेतृत्व व अनुशासन का अभ्यास कराते हैं।",
        listOf("आयु के अनुसार खेल", "सुरक्षा का ध्यान", "सबकी सहभागिता")
    ),
    RssTopic(
        "RSS इतिहास",
        "यात्रा और प्रेरक प्रसंग",
        Icons.Default.History,
        "इतिहास को घटनाओं की सूची नहीं, संगठन की सीख और समाज-सेवा की यात्रा के रूप में जानें।",
        listOf("प्रमुख पड़ाव", "प्रेरक व्यक्तित्व", "सेवा के उदाहरण")
    )
)

@Composable
fun RssIndex(
    onRegister: () -> Unit,
    onHierarchy: () -> Unit,
    onTopic: (RssTopic) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("राष्ट्रीय स्वयंसेवक संघ", fontSize = 27.sp, fontWeight = FontWeight.Black, color = Navy)
            Spacer(Modifier.height(5.dp))
            Text("जानें • जुड़ें • सहभागी बनें", color = Grey, fontSize = 16.sp)
            Spacer(Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Saffron),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text("आज से अपनी यात्रा शुरू करें", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(6.dp))
                    Text("RSS की गतिविधियों, विचारों और संगठन से एक ही स्थान पर जुड़ें।", color = Color.White)
                    Spacer(Modifier.height(14.dp))
                    PrimaryButton("स्वयंसेवक पंजीयन", Icons.Default.PersonAdd, onRegister)
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("RSS को जानें", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Navy)
            Text("किसी भी विषय को चुनकर उसका संक्षिप्त परिचय पढ़ें।", color = Grey)
        }
        items(rssTopics.chunked(2)) { row ->
            Row(Modifier.fillMaxWidth()) {
                row.forEach { topic ->
                    TopicTile(topic, Modifier.weight(1f), { onTopic(topic) })
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onHierarchy),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Border),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp)
            ) {
                Row(
                    Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AccountTree, null, tint = Saffron, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text("RSS संगठन संरचना", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Text("क्षेत्र से शाखा तक संगठन का दृश्य क्रम", color = Grey)
                    }
                    Icon(Icons.Default.ArrowForward, null, tint = Saffron)
                }
            }
        }
    }
}

@Composable
private fun TopicTile(topic: RssTopic, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.padding(5.dp).height(164.dp).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Border),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp)
    ) {
        Column(
            Modifier.fillMaxSize().padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(topic.icon, null, tint = Saffron, modifier = Modifier.size(30.dp))
            Column {
                Text(topic.title, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                Spacer(Modifier.height(4.dp))
                Text(topic.subtitle, color = Grey, fontSize = 12.sp, lineHeight = 16.sp)
            }
        }
    }
}

@Composable
fun RssTopicPage(topic: RssTopic) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Cream),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(topic.icon, null, tint = Saffron, modifier = Modifier.size(52.dp))
                    Spacer(Modifier.height(12.dp))
                    Text(topic.title, fontSize = 25.sp, fontWeight = FontWeight.Black, color = Navy)
                    Spacer(Modifier.height(8.dp))
                    Text(topic.overview, textAlign = TextAlign.Center, color = Grey)
                }
            }
        }
        item {
            SectionCard("मुख्य बिंदु", topic.icon) {
                topic.highlights.forEach { highlight ->
                    Row(Modifier.padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ArrowForward, null, tint = Saffron, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(highlight, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun RssHierarchyPage() {
    val levels = listOf("क्षेत्र", "प्रांत", "विभाग", "जिला", "खंड", "मंडल", "शाखा")
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text("संगठन का क्रम", fontSize = 23.sp, fontWeight = FontWeight.Black, color = Navy)
            Spacer(Modifier.height(6.dp))
            Text("स्थानीय सहभागिता शाखा से शुरू होकर व्यापक संगठन से जुड़ती है।", textAlign = TextAlign.Center, color = Grey)
            Spacer(Modifier.height(20.dp))
        }
        items(levels) { level ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Border),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp)
            ) {
                Row(
                    Modifier.padding(15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AccountTree, null, tint = Saffron)
                    Spacer(Modifier.width(12.dp))
                    Text(level, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                }
            }
            if (level != levels.last()) {
                Icon(Icons.Default.ArrowForward, null, tint = Saffron, modifier = Modifier.padding(vertical = 5.dp).size(22.dp))
            }
        }
    }
}

@Composable
fun Profile() {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item {
            SectionCard("व्यक्तिगत जानकारी", Icons.Default.PersonAdd) {
                Info("नाम", "अमित शर्मा")
                Info("मोबाइल", "9876543210")
                Info("जन्म दिनांक", "15/08/1995")
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
        items(names) { name ->
            Text(name, Modifier.padding(vertical = 10.dp), fontWeight = FontWeight.SemiBold)
        }
        item { Text("उपस्थित: " + selected.size + " / " + names.size, fontSize = 18.sp, fontWeight = FontWeight.Bold) }
    }
}

@Composable
fun Poll() {
    var choice by remember { mutableStateOf("प्रातः 6:30") }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        SectionCard("शाखा समय संबंधी सुझाव", Icons.Default.FormatQuote) {
            Text("आपकी शाखा के लिए कौन सा समय उचित रहेगा?")
            Text(choice, color = Saffron, fontWeight = FontWeight.Bold)
        }
    }
}
