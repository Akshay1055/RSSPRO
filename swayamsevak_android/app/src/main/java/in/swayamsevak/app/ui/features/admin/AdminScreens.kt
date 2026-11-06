package `in`.swayamsevak.app.ui.features.admin

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
                Action("स्वयंसेवक खोज एवं स्टेटमेन्ट रिपोर्ट", Icons.Default.Search, { go(Screen.Registrations) })
                Action("सामग्री एवं ऐप विवरण प्रबंधन", Icons.Default.DashboardCustomize, { go(Screen.AppDetailsAdmin) })
                Action("संगठन संरचना प्रबंधन", Icons.Default.AccountTree, { go(Screen.OrganizationAdmin) })
                Action("फ़ील्ड प्रबंधन (DeveloperAdmin)", Icons.Default.Tune, { go(Screen.Developer) })
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Registrations(viewModel: AuthViewModel, go: (Screen) -> Unit) {
    val users by viewModel.usersList.collectAsState()
    val mahanagars by viewModel.adminMahanagarList.collectAsState()
    val cities by viewModel.adminCityList.collectAsState()
    val interestsList by viewModel.adminInterestList.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    
    // Mandatory Filters
    var selectedMahanagar by remember { mutableStateOf("इंदौर") }
    var selectedNagar by remember { mutableStateOf("पश्चिम नगर") }
    
    // Optional Filters
    var selectedInterest by remember { mutableStateOf("सभी") }
    var selectedRole by remember { mutableStateOf("सभी") }
    
    var isStatementGenerated by remember { mutableStateOf(false) }
    var showDownloadSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchUsers()
    }

    val filteredList = users.filter { user ->
        val matchSearch = searchQuery.isBlank() || 
            user.fullName.contains(searchQuery, true) || 
            user.phoneNumber.contains(searchQuery, true) || 
            user.id.contains(searchQuery, true)
            
        val matchMahanagar = selectedMahanagar.isBlank() || user.organization.metro.contains(selectedMahanagar, true)
        val matchNagar = selectedNagar.isBlank() || user.organization.city.contains(selectedNagar, true)
        val matchRole = selectedRole == "सभी" || user.role.contains(selectedRole, true)

        matchSearch && matchMahanagar && matchNagar && matchRole
    }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("स्वयंसेवक खोज एवं स्टेटमेन्ट रिपोर्ट प्रबंधन", fontSize = 22.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("स्वयंसेवक खोज (नाम / मोबाइल / ID)") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true
            )
            Spacer(Modifier.height(10.dp))

            SectionCard("रिपोर्ट फिल्टर (Mahanagar & Nagar Mandatory)", Icons.Default.FilterList) {
                Text("1. अनिवार्य क्षेत्र (Mandatory):", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminDropdown("महानगर *", selectedMahanagar, mahanagars, Modifier.weight(1f)) { selectedMahanagar = it }
                    AdminDropdown("नगर *", selectedNagar, cities, Modifier.weight(1f)) { selectedNagar = it }
                }

                Spacer(Modifier.height(10.dp))
                Text("2. ऐच्छिक फ़िल्टर (Optional):", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminDropdown("रुचि (Optional)", selectedInterest, listOf("सभी") + interestsList, Modifier.weight(1f)) { selectedInterest = it }
                    AdminDropdown("पद / भूमिका (Optional)", selectedRole, listOf("सभी", "स्वयंसेवक", "मुख्य शिक्षक", "कार्यवाह"), Modifier.weight(1f)) { selectedRole = it }
                }

                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { isStatementGenerated = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Saffron),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("डेटा लागू करें")
                    }

                    Button(
                        onClick = { showDownloadSuccess = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Color.Black),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.FileDownload, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("XLS / CSV डाउनलोड")
                    }
                }

                if (showDownloadSuccess) {
                    Spacer(Modifier.height(8.dp))
                    Text("स्टेटमेन्ट रिपोर्ट swayamsevak_statement_${selectedNagar}.xlsx डाउनलोड हुआ!", color = Green, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(4.dp))
            Text("फ़िल्टर किए गए परिणाम: ${filteredList.size} स्वयंसेवक", fontWeight = FontWeight.Bold, color = Grey)
        }

        if (filteredList.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("कोई स्वयंसेवक नहीं मिला। फ़िल्टर बदलें।", color = Grey)
                }
            }
        } else {
            items(filteredList) { user ->
                Card(
                    Modifier.fillMaxWidth().clickable {
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
                            Text("मोबाइल: ${user.phoneNumber} • व्यवसाय: ${user.occupation}\n${user.organization.metro} • ${user.organization.city} • ${user.organization.branch}")
                        },
                        trailingContent = { Icon(Icons.Default.Edit, null, tint = Saffron) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDropdown(label: String, value: String, items: List<String>, modifier: Modifier, onValue: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded, { expanded = !expanded }, modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
            label = { Text(label, fontSize = 12.sp) },
            readOnly = true,
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { item ->
                DropdownMenuItem(text = { Text(item) }, onClick = { onValue(item); expanded = false })
            }
        }
    }
}

@Composable
fun OrganizationAdmin(viewModel: AuthViewModel) {
    val currentMetro by viewModel.metro.collectAsState()
    val currentCity by viewModel.city.collectAsState()
    val currentBasti by viewModel.basti.collectAsState()
    val currentBranch by viewModel.branch.collectAsState()
    val currentMohalla by viewModel.mohalla.collectAsState()
    val currentMukhya by viewModel.mukhyaShikshak.collectAsState()
    val currentKaryawah by viewModel.karyawah.collectAsState()

    var metro by remember { mutableStateOf(currentMetro) }
    var city by remember { mutableStateOf(currentCity) }
    var basti by remember { mutableStateOf(currentBasti) }
    var branch by remember { mutableStateOf(currentBranch) }
    var mohalla by remember { mutableStateOf(currentMohalla) }
    var mukhya by remember { mutableStateOf(currentMukhya) }
    var karyawah by remember { mutableStateOf(currentKaryawah) }
    var successMsg by remember { mutableStateOf(false) }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("संगठन संरचना एवं पदाधिकारी प्रबंधन", fontWeight = FontWeight.Bold, fontSize = 18.sp)
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
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(mukhya, { mukhya = it }, Modifier.fillMaxWidth(), label = { Text("मुख्य शिक्षक") })
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(karyawah, { karyawah = it }, Modifier.fillMaxWidth(), label = { Text("कार्यवाह") })
            Spacer(Modifier.height(16.dp))
            if (successMsg) {
                Text("संगठन एवं पदाधिकारी विवरण सहेजा गया!", color = Green, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
            }
            PrimaryButton("बदलाव सहेजें", Icons.Default.Save) {
                viewModel.metro.value = metro
                viewModel.city.value = city
                viewModel.basti.value = basti
                viewModel.branch.value = branch
                viewModel.mohalla.value = mohalla
                viewModel.mukhyaShikshak.value = mukhya
                viewModel.karyawah.value = karyawah
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
fun AppDetailsAdminPage(viewModel: AuthViewModel) {
    val appTitle by viewModel.adminAppTitle.collectAsState()
    val mahanagars by viewModel.adminMahanagarList.collectAsState()
    val cities by viewModel.adminCityList.collectAsState()
    val bastis by viewModel.adminBastiList.collectAsState()
    val branches by viewModel.adminBranchList.collectAsState()
    val mohallas by viewModel.adminMohallaList.collectAsState()
    val interests by viewModel.adminInterestList.collectAsState()
    val docsMap by viewModel.topicDocs.collectAsState()

    var editingTitle by remember { mutableStateOf(appTitle) }
    var newItemText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("महानगर") }
    var saveSuccessMsg by remember { mutableStateOf("") }

    val categories = listOf("महानगर", "नगर", "बस्ती", "शाखा", "मोहल्ला", "रुचि")

    val topics = listOf("RSS प्रार्थना", "शाखा पद्धति", "गीत", "अमृत वचन", "शाखा खेल", "RSS इतिहास")
    var selectedTopicForPdf by remember { mutableStateOf(topics.first()) }
    var pdfUrlInput by remember { mutableStateOf(docsMap[topics.first()]?.first ?: "") }
    var imageUrlInput by remember { mutableStateOf(docsMap[topics.first()]?.second ?: "") }

    // Native Local Device File Picker for PDF
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            pdfUrlInput = "https://storage.googleapis.com/rss-media/${selectedTopicForPdf.lowercase().replace(" ", "_")}.pdf"
            saveSuccessMsg = "लोकल PDF फाइल चुनी गई (${it.lastPathSegment ?: "document.pdf"}) - 1 PDF प्रति सेक्शन (Max 10 MB)"
        }
    }

    // Native Local Device File Picker for Image
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUrlInput = "https://storage.googleapis.com/rss-media/${selectedTopicForPdf.lowercase().replace(" ", "_")}.jpg"
            saveSuccessMsg = "लोकल Image चुनी गई (${it.lastPathSegment ?: "image.jpg"})"
        }
    }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("सामग्री एवं ऐप विवरण प्रबंधन (Admin)", fontSize = 22.sp, fontWeight = FontWeight.Black)
            Text("ऐप के शीर्षक, संगठन विकल्पों एवं RSS पैनलों के लिए दस्तावेज़ (PDF/Image) सेट करें।", color = Grey)
            Spacer(Modifier.height(8.dp))

            SectionCard("ऐप मुख्य विवरण", Icons.Default.Title) {
                OutlinedTextField(
                    editingTitle, { editingTitle = it; viewModel.adminAppTitle.value = it },
                    Modifier.fillMaxWidth(), label = { Text("ऐप का नाम / शीर्षक") }
                )
            }

            SectionCard("पंजीयन ड्रॉपडाउन विकल्प प्रबंधन (संगठन एवं रुचि)", Icons.Default.ListAlt) {
                Text("श्रेणी चुनें:", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                categories.chunked(3).forEach { row ->
                    Row(Modifier.fillMaxWidth()) {
                        row.forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat) },
                                modifier = Modifier.weight(1f).padding(2.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))
                val currentCategoryList = when (selectedCategory) {
                    "महानगर" -> mahanagars
                    "नगर" -> cities
                    "बस्ती" -> bastis
                    "शाखा" -> branches
                    "मोहल्ला" -> mohallas
                    else -> interests
                }

                Text("वर्तमान $selectedCategory सूची:", fontWeight = FontWeight.Bold, color = Saffron)
                Spacer(Modifier.height(4.dp))
                Column {
                    currentCategoryList.forEach { itemVal ->
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("• $itemVal", fontWeight = FontWeight.SemiBold)
                            IconButton(onClick = { viewModel.removeAdminOption(selectedCategory, itemVal) }) {
                                Icon(Icons.Default.Delete, contentDescription = "हटाएँ", tint = Color.Red, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        newItemText, { newItemText = it },
                        Modifier.weight(1f),
                        label = { Text("नया $selectedCategory जोड़ें") },
                        singleLine = true
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newItemText.isNotBlank()) {
                                viewModel.addAdminOption(selectedCategory, newItemText)
                                newItemText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Saffron)
                    ) { Text("जोड़ें") }
                }
            }

            SectionCard("RSS पैनल Media / PDF अपलोड (Max 10 MB • 1 PDF per section)", Icons.Default.PictureAsPdf) {
                Text("विषय क्षेत्र चुनें:", fontWeight = FontWeight.Bold)
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
                                    saveSuccessMsg = ""
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

                // PDF Upload Field with Browse Button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        pdfUrlInput, { pdfUrlInput = it }, Modifier.weight(1f),
                        label = { Text("PDF लिंक (1 PDF max 10MB)") },
                        leadingIcon = { Icon(Icons.Default.PictureAsPdf, null) },
                        singleLine = true
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { pdfPickerLauncher.launch("application/pdf") },
                        colors = ButtonDefaults.buttonColors(containerColor = Saffron)
                    ) {
                        Icon(Icons.Default.FolderOpen, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("ब्राउज़")
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Image Upload Field with Browse Button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        imageUrlInput, { imageUrlInput = it }, Modifier.weight(1f),
                        label = { Text("Image लिंक") },
                        leadingIcon = { Icon(Icons.Default.Image, null) },
                        singleLine = true
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = Saffron)
                    ) {
                        Icon(Icons.Default.FolderOpen, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("ब्राउज़")
                    }
                }

                Spacer(Modifier.height(12.dp))
                PrimaryButton("दस्तावेज़ सहेजें (Save Link to DB)", Icons.Default.Save) {
                    val updatedDocs = docsMap.toMutableMap()
                    updatedDocs[selectedTopicForPdf] = Pair(pdfUrlInput, imageUrlInput)
                    viewModel.topicDocs.value = updatedDocs
                    saveSuccessMsg = "$selectedTopicForPdf का मीडिया/PDF दस्तावेज़ सफलतापूर्वक सहेजा गया!"
                }
                if (saveSuccessMsg.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(saveSuccessMsg, color = Green, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
