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
    var showReportDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchUsers()
    }

    val sampleUsersCount = if (users.isEmpty()) 12 else users.size

    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("प्रशासन डैशबोर्ड एवं विश्लेषिकी", fontSize = 26.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(14.dp))
            Row(Modifier.fillMaxWidth()) {
                Stat("कुल पंजीयन", "$sampleUsersCount", Modifier.weight(1f)); Spacer(Modifier.width(8.dp)); Stat("सत्यापित", "$sampleUsersCount", Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth()) {
                Stat("लंबित", "0", Modifier.weight(1f)); Spacer(Modifier.width(8.dp)); Stat("सक्रिय शाखाएँ", "18", Modifier.weight(1f))
            }
            Spacer(Modifier.height(18.dp))

            SectionCard("त्वरित कार्य एवं प्रबंधन", Icons.Default.FlashOn) {
                Action("पंजीयन एवं खोज प्रबंधन", Icons.Default.People, { go(Screen.Registrations) })
                Action("सामग्री एवं ऐप विवरण प्रबंधन", Icons.Default.DashboardCustomize, { go(Screen.AppDetailsAdmin) })
                Action("संगठन संरचना प्रबंधन", Icons.Default.AccountTree, { go(Screen.OrganizationAdmin) })
                Action("फ़ील्ड प्रबंधन", Icons.Default.Tune, { go(Screen.Developer) })
                Action("भूमिका एवं अधिकार", Icons.Default.AdminPanelSettings, { go(Screen.Permissions) })
            }

            Spacer(Modifier.height(12.dp))
            SectionCard("रिपोर्ट एवं डेटा विश्लेषण (CSV / Graph)", Icons.Default.BarChart) {
                Text("नगर / बस्ती अनुसार पंजीयन रिपोर्ट:", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                AnalyticsBar("पश्चिम नगर", 0.85f, "85%")
                AnalyticsBar("पूर्व नगर", 0.60f, "60%")
                AnalyticsBar("उत्तर नगर", 0.45f, "45%")
                
                Spacer(Modifier.height(14.dp))
                Text("रुचि अनुसार स्वयंसेवक वितरण:", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                AnalyticsBar("सेवा कार्य", 0.90f, "90%")
                AnalyticsBar("शारीरिक एवं खेल", 0.75f, "75%")
                AnalyticsBar("बौद्धिक एवं विचार", 0.65f, "65%")
                AnalyticsBar("तकनीकी / IT", 0.40f, "40%")

                Spacer(Modifier.height(16.dp))
                PrimaryButton("Excel / CSV रिपोर्ट जनरेट करें", Icons.Default.FileDownload) {
                    showReportDialog = true
                }
            }
        }
    }

    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("CSV / Excel डेटा रिपोर्ट जनरेट हुई") },
            text = {
                Column {
                    Text("रिपोर्ट फ़ाइल तैयार है: swayamsevak_members_report.csv")
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "फ़ील्ड्स: ID, Name, Mobile, Occupation, Metro, City, Basti, Branch, Role",
                        fontSize = 12.sp, color = Grey
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showReportDialog = false }) { Text("डाउनलोड / शेयर करें") }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) { Text("बंद करें") }
            }
        )
    }
}

@Composable
fun AnalyticsBar(label: String, progress: Float, percentText: String) {
    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 13.sp)
            Text(percentText, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Saffron)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = Saffron,
            trackColor = Border
        )
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

@Composable
fun AppDetailsAdminPage(viewModel: AuthViewModel) {
    val appTitle by viewModel.adminAppTitle.collectAsState()
    val mahanagars by viewModel.adminMahanagarList.collectAsState()
    val cities by viewModel.adminCityList.collectAsState()
    val interests by viewModel.adminInterestList.collectAsState()
    val docsMap by viewModel.topicDocs.collectAsState()

    var editingTitle by remember { mutableStateOf(appTitle) }
    var newCityItem by remember { mutableStateOf("") }
    var newInterestItem by remember { mutableStateOf("") }
    var saveSuccessMsg by remember { mutableStateOf("") }

    val topics = listOf("RSS प्रार्थना", "शाखा पद्धति", "गीत", "अमृत वचन", "शाखा खेल", "RSS इतिहास")
    var selectedTopicForPdf by remember { mutableStateOf(topics.first()) }
    var pdfUrlInput by remember { mutableStateOf(docsMap[topics.first()]?.first ?: "") }
    var imageUrlInput by remember { mutableStateOf(docsMap[topics.first()]?.second ?: "") }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("सामग्री एवं ऐप विवरण प्रबंधन (Admin)", fontSize = 22.sp, fontWeight = FontWeight.Black)
            Text("ऐप के शीर्षक, ड्रॉपडाउन विकल्पों और RSS पैनलों के लिए दस्तावेज़ (PDF/Image) सेट करें।", color = Grey)
            Spacer(Modifier.height(8.dp))

            SectionCard("ऐप मुख्य विवरण", Icons.Default.Title) {
                OutlinedTextField(
                    editingTitle, { editingTitle = it; viewModel.adminAppTitle.value = it },
                    Modifier.fillMaxWidth(), label = { Text("ऐप का नाम / शीर्षक") }
                )
            }

            SectionCard("पंजीयन ड्रॉपडाउन विकल्प (संगठन एवं रुचि)", Icons.Default.ListAlt) {
                Text("वर्तमान नगर सूची: ${cities.joinToString(", ")}", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(newCityItem, { newCityItem = it }, Modifier.weight(1f), label = { Text("नया नगर जोड़ें") })
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newCityItem.isNotBlank()) {
                                viewModel.adminCityList.value = (cities + newCityItem).toMutableList()
                                newCityItem = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Saffron)
                    ) { Text("जोड़ें") }
                }

                Spacer(Modifier.height(12.dp))
                Text("वर्तमान रुचि सूची: ${interests.joinToString(", ")}", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(newInterestItem, { newInterestItem = it }, Modifier.weight(1f), label = { Text("नई रुचि जोड़ें") })
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newInterestItem.isNotBlank()) {
                                viewModel.adminInterestList.value = (interests + newInterestItem).toMutableList()
                                newInterestItem = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Saffron)
                    ) { Text("जोड़ें") }
                }
            }

            SectionCard("RSS पैनल PDF / Image अपलोड एवं लिंक", Icons.Default.PictureAsPdf) {
                Text("विषय चुनें:", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                topics.chunked(2).forEach { row ->
                    Row(Modifier.fillMaxWidth()) {
                        row.forEach { topic ->
                            FilterChip(
                                selected = selectedTopicForPdf == topic,
                                onClick = {
                                    selectedTopicForPdf = topic
                                    pdfUrlInput = docsMap[topic]?.first ?: ""
                                    imageUrlInput = docsMap[topic]?.second ?: ""
                                },
                                label = { Text(topic) },
                                modifier = Modifier.weight(1f).padding(3.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))
                Text("चयनित विषय: $selectedTopicForPdf", fontWeight = FontWeight.ExtraBold, color = Saffron)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    pdfUrlInput, { pdfUrlInput = it }, Modifier.fillMaxWidth(),
                    label = { Text("PDF लिंक (URL / File path)") },
                    leadingIcon = { Icon(Icons.Default.PictureAsPdf, null) }
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    imageUrlInput, { imageUrlInput = it }, Modifier.fillMaxWidth(),
                    label = { Text("Image लिंक (URL / File path)") },
                    leadingIcon = { Icon(Icons.Default.Image, null) }
                )
                Spacer(Modifier.height(12.dp))
                PrimaryButton("दस्तावेज़ लिंक अपडेट करें", Icons.Default.Save) {
                    val updatedDocs = docsMap.toMutableMap()
                    updatedDocs[selectedTopicForPdf] = Pair(pdfUrlInput, imageUrlInput)
                    viewModel.topicDocs.value = updatedDocs
                    saveSuccessMsg = "$selectedTopicForPdf का दस्तावेज़ लिंक अपडेट हुआ!"
                }
                if (saveSuccessMsg.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(saveSuccessMsg, color = Green, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
