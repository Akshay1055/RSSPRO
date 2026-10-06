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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import `in`.swayamsevak.app.ui.components.Green
import `in`.swayamsevak.app.ui.components.Navy
import `in`.swayamsevak.app.ui.components.PrimaryButton
import `in`.swayamsevak.app.ui.components.Saffron
import `in`.swayamsevak.app.ui.components.SectionCard
import `in`.swayamsevak.app.ui.components.Info
import `in`.swayamsevak.app.ui.components.SurfaceDark
import `in`.swayamsevak.app.ui.auth.AdminState
import `in`.swayamsevak.app.ui.auth.AuthViewModel
import `in`.swayamsevak.app.ui.auth.DirectoryState

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
    onDirectory: () -> Unit,
    onMyShakha: () -> Unit,
    onAdmin: () -> Unit,
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
            Spacer(Modifier.height(14.dp))
            Row(Modifier.fillMaxWidth()) {
                HomeAction("सदस्य खोजें", Icons.Default.Search, Modifier.weight(1f), onDirectory)
                HomeAction("मेरी शाखा", Icons.Default.Groups, Modifier.weight(1f), onMyShakha)
            }
            HomeAction("Admin प्रवेश", Icons.Default.AdminPanelSettings, Modifier.fillMaxWidth(), onAdmin)
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
                colors = CardDefaults.cardColors(containerColor = SurfaceDark, contentColor = Navy),
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
        colors = CardDefaults.cardColors(containerColor = SurfaceDark, contentColor = Navy),
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
                colors = CardDefaults.cardColors(containerColor = SurfaceDark, contentColor = Navy),
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

@Composable
private fun HomeAction(title: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.padding(5.dp).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark, contentColor = Navy),
        border = BorderStroke(1.dp, Border),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    ) {
        Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Saffron, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(8.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
fun MemberSearchPage(viewModel: AuthViewModel) {
    var query by remember { mutableStateOf("") }
    val results by viewModel.memberResults.collectAsState()
    val state by viewModel.directoryState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("नाम या शाखा से खोजें", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Navy)
            Spacer(Modifier.height(6.dp))
            Text("कम-से-कम 2 अक्षर लिखें। परिणाम अधिकतम 50 तक सीमित हैं।", color = Grey)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("नाम या शाखा") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true
            )
            Spacer(Modifier.height(10.dp))
            PrimaryButton("खोजें", Icons.Default.Search) { viewModel.searchMembers(query) }
        }
        if (state is DirectoryState.Loading) {
            item { CircularProgressIndicator(modifier = Modifier.padding(16.dp), color = Saffron) }
        }
        if (state is DirectoryState.Error) {
            item { Text((state as DirectoryState.Error).message, color = Color(0xFFFCA5A5)) }
        }
        if (state is DirectoryState.Ready && results.isEmpty()) {
            item { Text("कोई सदस्य नहीं मिला।", color = Grey) }
        }
        items(results) { member ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark, contentColor = Navy),
                border = BorderStroke(1.dp, Border)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(member.fullName, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("शाखा: " + member.shakha, color = Grey)
                }
            }
        }
    }
}

@Composable
fun MyShakhaPage() {
    var activity by remember { mutableStateOf("मतदान") }
    var title by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var posted by remember { mutableStateOf(false) }
    val choices = listOf(
        "मतदान" to Icons.Default.HowToVote,
        "कार्यक्रम" to Icons.Default.Event,
        "स्मरण" to Icons.Default.Notifications,
        "रिपोर्ट" to Icons.Default.Assignment
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("विजय नगर शाखा", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Navy)
            Text("गतिविधि बनाएँ और अपनी शाखा से जुड़ें।", color = Grey)
            Spacer(Modifier.height(14.dp))
            SectionCard("नई गतिविधि", Icons.Default.Add) {
                choices.chunked(2).forEach { row ->
                    Row(Modifier.fillMaxWidth()) {
                        row.forEach { choice ->
                            FilterChip(
                                selected = activity == choice.first,
                                onClick = { activity = choice.first },
                                label = { Text(choice.first) },
                                leadingIcon = { Icon(choice.second, null, modifier = Modifier.size(18.dp)) },
                                modifier = Modifier.weight(1f).padding(3.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(title, { title = it }, Modifier.fillMaxWidth(), label = { Text("शीर्षक") })
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    details,
                    { details = it },
                    Modifier.fillMaxWidth().height(110.dp),
                    label = { Text("विवरण, समय या प्रश्न") }
                )
                Spacer(Modifier.height(12.dp))
                PrimaryButton("गतिविधि तैयार करें", Icons.Default.Publish) {
                    posted = title.isNotBlank()
                }
                if (posted) {
                    Text("यह " + activity + " तैयार है। इसे सभी को भेजने के लिए अगला सर्वर चरण जोड़ा जाएगा।", color = Green, modifier = Modifier.padding(top = 10.dp))
                }
            }
        }
    }
}

@Composable
fun AdminLoginPage(viewModel: AuthViewModel, onAuthenticated: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    val state by viewModel.adminState.collectAsState()

    LaunchedEffect(state) {
        if (state is AdminState.Authenticated) onAuthenticated()
    }

    Column(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.AdminPanelSettings, null, tint = Saffron, modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(14.dp))
        Text("Admin प्रवेश", fontSize = 25.sp, fontWeight = FontWeight.Black, color = Navy)
        Text("अपना उपयोगकर्ता नाम और सुरक्षित PIN दर्ज करें।", color = Grey)
        Spacer(Modifier.height(20.dp))
        OutlinedTextField(username, { username = it }, Modifier.fillMaxWidth(), label = { Text("उपयोगकर्ता नाम") }, singleLine = true)
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(pin, { pin = it }, Modifier.fillMaxWidth(), label = { Text("PIN / पासवर्ड") }, singleLine = true)
        Spacer(Modifier.height(14.dp))
        if (state is AdminState.Error) Text((state as AdminState.Error).message, color = Color(0xFFFCA5A5))
        if (state is AdminState.Loading) CircularProgressIndicator(color = Saffron)
        else PrimaryButton("प्रवेश करें", Icons.Default.LockOpen) { viewModel.adminLogin(username, pin) }
    }
}

@Composable
fun AdminConsolePage() {
    val tools = listOf(
        "मतदान बनाएँ" to Icons.Default.HowToVote,
        "कार्यक्रम पोस्ट करें" to Icons.Default.Event,
        "स्मरण भेजें" to Icons.Default.Notifications,
        "शाखा रिपोर्ट बनाएँ" to Icons.Default.Assignment
    )

    LazyColumn(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("शाखा प्रबंधन", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Navy)
            Text("अपनी शाखा के लिए गतिविधियाँ प्रबंधित करें।", color = Grey)
        }
        items(tools) { tool ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark, contentColor = Navy),
                border = BorderStroke(1.dp, Border)
            ) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(tool.second, null, tint = Saffron)
                    Spacer(Modifier.width(14.dp))
                    Text(tool.first, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.ArrowForward, null, tint = Saffron)
                }
            }
        }
    }
}
