package `in`.swayamsevak.app.ui.features.admin

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import `in`.swayamsevak.app.Screen
import `in`.swayamsevak.app.ui.components.*

@Composable
fun Admin(go: (Screen) -> Unit) {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("प्रशासन डैशबोर्ड", fontSize = 26.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(14.dp))
            Row(Modifier.fillMaxWidth()) {
                Stat("कुल पंजीयन", "12,458", Modifier.weight(1f)); Spacer(Modifier.width(8.dp)); Stat("सत्यापित", "11,920", Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth()) {
                Stat("लंबित", "183", Modifier.weight(1f)); Spacer(Modifier.width(8.dp)); Stat("निष्क्रिय", "355", Modifier.weight(1f))
            }
            Spacer(Modifier.height(18.dp))
            SectionCard("त्वरित कार्य", Icons.Default.FlashOn) {
                Action("पंजीयन प्रबंधन", Icons.Default.People, { go(Screen.Registrations) })
                Action("संगठन प्रबंधन", Icons.Default.AccountTree, { go(Screen.OrganizationAdmin) })
                Action("फ़ील्ड प्रबंधन", Icons.Default.Tune, { go(Screen.Developer) })
                Action("भूमिका एवं अधिकार", Icons.Default.AdminPanelSettings, { go(Screen.Permissions) })
            }
        }
    }
}

@Composable
fun Stat(title: String, value: String, modifier: Modifier) {
    Card(modifier, colors = CardDefaults.cardColors(Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, Border)) {
        Column(Modifier.padding(14.dp)) {
            Text(title, color = Grey); Text(value, fontSize = 24.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun Action(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        leadingContent = { Icon(icon, null, tint = Saffron) },
        headlineContent = { Text(title, fontWeight = FontWeight.Bold) },
        trailingContent = { Icon(Icons.Default.ChevronRight, null) }
    )
}

@Composable
fun Registrations(go: (Screen) -> Unit) {
    val members = listOf(
        Triple("अमित शर्मा", "SVY-2026-001245", "सत्यापन लंबित"),
        Triple("राहुल वर्मा", "SVY-2026-001246", "सत्यापित"),
        Triple("विवेक जोशी", "SVY-2026-001247", "सत्यापित")
    )
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item { OutlinedTextField("", {}, Modifier.fillMaxWidth(), label = { Text("नाम, मोबाइल या पंजीयन क्रमांक खोजें") }, leadingIcon = { Icon(Icons.Default.Search, null) }); Spacer(Modifier.height(12.dp)) }
        items(members) { m ->
            Card(Modifier.fillMaxWidth().padding(bottom = 10.dp).clickable { go(Screen.Profile) }, colors = CardDefaults.cardColors(Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, Border)) {
                ListItem(
                    leadingContent = { Icon(Icons.Default.Person, null, tint = Saffron) },
                    headlineContent = { Text(m.first, fontWeight = FontWeight.Bold) },
                    supportingContent = { Text("${m.second}\nइंदौर • पश्चिम नगर\n${m.third}") }
                )
            }
        }
    }
}

@Composable
fun OrganizationAdmin() {
    val rows = listOf("महानगर" to "इंदौर", "नगर" to "पश्चिम नगर", "बस्ती" to "विजय नगर बस्ती", "शाखा" to "विजय नगर शाखा", "मोहल्ला" to "विजय नगर")
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item { Text("महानगर → नगर → बस्ती → शाखा → मोहल्ला", fontWeight = FontWeight.Bold); Spacer(Modifier.height(12.dp)) }
        items(rows) { (a, b) ->
            Card(Modifier.fillMaxWidth().padding(bottom = 8.dp), colors = CardDefaults.cardColors(Color.White)) {
                ListItem(headlineContent = { Text(a, color = Grey) }, supportingContent = { Text(b, fontWeight = FontWeight.Bold) }, trailingContent = { Icon(Icons.Default.Edit, null) })
            }
        }
    }
}

@Composable
fun Developer(go: (Screen) -> Unit) {
    val fields = listOf(
        "नाम" to "Text • आवश्यक",
        "जन्म दिनांक" to "Date • आवश्यक",
        "रुचि" to "Multi Select • वैकल्पिक",
        "प्रोफ़ाइल फोटो" to "File / Photo • वैकल्पिक",
        "ईमेल" to "Email • वैकल्पिक"
    )
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item { Text("स्वयंसेवक प्रोफ़ाइल", fontSize = 22.sp, fontWeight = FontWeight.Black); Spacer(Modifier.height(10.dp)) }
        items(fields) { (name, detail) ->
            Card(Modifier.fillMaxWidth().padding(bottom = 8.dp), colors = CardDefaults.cardColors(Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, Border)) {
                ListItem(headlineContent = { Text(name, fontWeight = FontWeight.Bold) }, supportingContent = { Text("प्रकार: $detail") }, trailingContent = { Icon(Icons.Default.Edit, null) })
            }
        }
        item { Spacer(Modifier.height(8.dp)); PrimaryButton("नया फ़ील्ड जोड़ें", Icons.Default.Add) {} }
    }
}

@Composable
fun Permissions() {
    val permissions = listOf("member.view", "member.create", "member.edit", "member.delete", "registration.verify", "organization.view", "organization.edit", "field.view", "field.create", "field.edit", "role.edit", "permission.edit", "poll.create")
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item { Text("DeveloperAdmin", fontSize = 22.sp, fontWeight = FontWeight.Black); Spacer(Modifier.height(8.dp)) }
        items(permissions) { p ->
            var enabled by remember { mutableStateOf(true) }
            SwitchListItem(p, enabled) { enabled = it }
        }
    }
}

@Composable
fun SwitchListItem(title: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
        Switch(checked, onChecked, colors = SwitchDefaults.colors(checkedThumbColor = androidx.compose.ui.graphics.Color.White, checkedTrackColor = Saffron))
    }
}
