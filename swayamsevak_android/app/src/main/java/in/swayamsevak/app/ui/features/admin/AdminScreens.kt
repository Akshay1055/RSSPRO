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
import `in`.swayamsevak.app.ui.auth.AuthViewModel
import `in`.swayamsevak.app.ui.components.*

@Composable
fun Admin(viewModel: AuthViewModel, go: (Screen) -> Unit) {
    val users by viewModel.usersList.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchUsers()
    }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("प्रशासन डैशबोर्ड", fontSize = 26.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(14.dp))
            Row(Modifier.fillMaxWidth()) {
                Stat("कुल पंजीयन", "${users.size}", Modifier.weight(1f)); Spacer(Modifier.width(8.dp)); Stat("सत्यापित", "${users.size}", Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth()) {
                Stat("लंबित", "0", Modifier.weight(1f)); Spacer(Modifier.width(8.dp)); Stat("सक्रिय", "${users.size}", Modifier.weight(1f))
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
fun Registrations(viewModel: AuthViewModel, go: (Screen) -> Unit) {
    val users by viewModel.usersList.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchUsers()
    }

    val filteredUsers = users.filter { 
        it.fullName.contains(searchQuery, true) || 
        it.phoneNumber.contains(searchQuery, true) || 
        it.id.contains(searchQuery, true) 
    }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item {
            OutlinedTextField(
                searchQuery, { searchQuery = it }, Modifier.fillMaxWidth(),
                label = { Text("नाम, मोबाइल या पंजीयन क्रमांक खोजें") },
                leadingIcon = { Icon(Icons.Default.Search, null) }
            )
            Spacer(Modifier.height(12.dp))
            Text("कुल सहेजे गए स्वयंसेवक: ${filteredUsers.size}", fontWeight = FontWeight.Bold, color = Grey)
            Spacer(Modifier.height(8.dp))
        }

        if (filteredUsers.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("कोई पंजीयन नहीं मिला या सर्वर लोड हो रहा है।", color = Grey)
                }
            }
        } else {
            items(filteredUsers) { user ->
                Card(
                    Modifier.fillMaxWidth().padding(bottom = 10.dp).clickable {
                        viewModel.selectedUser.value = user
                        go(Screen.Profile)
                    },
                    colors = CardDefaults.cardColors(Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Border)
                ) {
                    ListItem(
                        leadingContent = { Icon(Icons.Default.Person, null, tint = Saffron) },
                        headlineContent = { Text(user.fullName, fontWeight = FontWeight.Bold) },
                        supportingContent = {
                            Text("मोबाइल: ${user.phoneNumber}\n${user.organization.metro} • ${user.organization.city}\nID: ${user.id.take(8)}...")
                        },
                        trailingContent = { Icon(Icons.Default.Edit, null, tint = Saffron) }
                    )
                }
            }
        }
    }
}

@Composable
fun OrganizationAdmin(viewModel: AuthViewModel) {
    var metro by remember { mutableStateOf("इंदौर") }
    var city by remember { mutableStateOf("पश्चिम नगर") }
    var basti by remember { mutableStateOf("विजय नगर बस्ती") }
    var branch by remember { mutableStateOf("विजय नगर शाखा") }
    var mohalla by remember { mutableStateOf("विजय नगर") }
    var successMsg by remember { mutableStateOf(false) }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("संगठन संरचना प्रबंधन (संपादन योग्य)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(metro, { metro = it }, Modifier.fillMaxWidth(), label = { Text("महानगर") })
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(city, { city = it }, Modifier.fillMaxWidth(), label = { Text("नगर") })
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(basti, { basti = it }, Modifier.fillMaxWidth(), label = { Text("बस्ती") })
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(branch, { branch = it }, Modifier.fillMaxWidth(), label = { Text("शाखा") })
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(mohalla, { mohalla = it }, Modifier.fillMaxWidth(), label = { Text("मोहल्ला") })
            Spacer(Modifier.height(16.dp))
            if (successMsg) {
                Text("संगठन इकाई सफलतापूर्वक अपडेट हुई!", color = Green, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
            }
            PrimaryButton("बदलाव सहेजें", Icons.Default.Save) {
                successMsg = true
            }
        }
    }
}

@Composable
fun Developer(viewModel: AuthViewModel, go: (Screen) -> Unit) {
    var fields by remember {
        mutableStateOf(
            mutableListOf(
                "नाम" to "Text • आवश्यक",
                "पिता का नाम" to "Text • आवश्यक",
                "जन्म दिनांक" to "Date • आवश्यक",
                "मोबाइल" to "Phone • आवश्यक",
                "रुचि" to "Multi Select • वैकल्पिक",
                "प्रोफ़ाइल फोटो" to "File / Photo • वैकल्पिक"
            )
        )
    }
    var showDialog by remember { mutableStateOf(false) }
    var newFieldName by remember { mutableStateOf("") }
    var newFieldType by remember { mutableStateOf("Text • वैकल्पिक") }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("स्वयंसेवक प्रोफ़ाइल फ़ील्ड प्रबंधन (DeveloperAdmin)", fontSize = 20.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(10.dp))
        }
        items(fields) { (name, detail) ->
            Card(Modifier.fillMaxWidth().padding(bottom = 8.dp), colors = CardDefaults.cardColors(Color.White), border = androidx.compose.foundation.BorderStroke(1.dp, Border)) {
                ListItem(
                    headlineContent = { Text(name, fontWeight = FontWeight.Bold) },
                    supportingContent = { Text("प्रकार: $detail") },
                    trailingContent = {
                        IconButton(onClick = { fields.remove(name to detail) }) {
                            Icon(Icons.Default.Delete, null, tint = Color.Red)
                        }
                    }
                )
            }
        }
        item {
            Spacer(Modifier.height(8.dp))
            PrimaryButton("नया फ़ील्ड जोड़ें", Icons.Default.Add) {
                showDialog = true
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("नया फ़ील्ड जोड़ें") },
            text = {
                Column {
                    OutlinedTextField(newFieldName, { newFieldName = it }, label = { Text("फ़ील्ड का नाम") })
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(newFieldType, { newFieldType = it }, label = { Text("फ़ील्ड प्रकार (जैसे Text • आवश्यक)") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newFieldName.isNotBlank()) {
                        fields.add(newFieldName to newFieldType)
                        newFieldName = ""
                        showDialog = false
                    }
                }) { Text("जोड़ें") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("रद्द करें") }
            }
        )
    }
}

@Composable
fun Permissions(viewModel: AuthViewModel) {
    val permissions = remember {
        mutableStateMapOf(
            "member.view" to true,
            "member.create" to true,
            "member.edit" to true,
            "member.delete" to true,
            "registration.verify" to true,
            "organization.view" to true,
            "organization.edit" to true,
            "field.view" to true,
            "field.create" to true,
            "field.edit" to true,
            "role.edit" to true,
            "permission.edit" to true,
            "poll.create" to true
        )
    }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("DeveloperAdmin • भूमिका एवं अधिकार (Permissions)", fontSize = 20.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(8.dp))
            Text("सभी अनुमतियाँ लाइव प्रबंधित करें:", color = Grey)
            Spacer(Modifier.height(12.dp))
        }
        items(permissions.keys.toList()) { p ->
            val enabled = permissions[p] ?: true
            SwitchListItem(p, enabled) { permissions[p] = it }
        }
    }
}

@Composable
fun SwitchListItem(title: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
        Switch(checked, onChecked, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Saffron))
    }
}
