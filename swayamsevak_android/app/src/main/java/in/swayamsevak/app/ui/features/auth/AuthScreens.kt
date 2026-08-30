package `in`.swayamsevak.app.ui.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.swayamsevak.app.Screen
import `in`.swayamsevak.app.ui.auth.AuthUiState
import `in`.swayamsevak.app.ui.auth.AuthViewModel
import `in`.swayamsevak.app.ui.components.*

@Composable
fun Welcome(onRegister: () -> Unit, onAdmin: () -> Unit, onDeveloper: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))
        Box(
            Modifier.size(100.dp).background(Saffron, RoundedCornerShape(28.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.AccountBalance, null, tint = Color.White, modifier = Modifier.size(54.dp))
        }
        Spacer(Modifier.height(22.dp))
        Text("स्वयंसेवक", fontSize = 34.sp, fontWeight = FontWeight.Black, color = Navy)
        Text("पंजीयन एवं संगठन प्रबंधन", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(10.dp))
        Text(
            "पंजीयन → सत्यापन → संगठन से जोड़ना → जानकारी प्रबंधन → सहभागिता",
            textAlign = TextAlign.Center, color = Grey
        )
        Spacer(Modifier.weight(1f))
        PrimaryButton("स्वयंसेवक पंजीयन", Icons.Default.PersonAdd, onRegister)
        Spacer(Modifier.height(10.dp))
        OutlinedButton(onClick = onAdmin) { Icon(Icons.Default.AdminPanelSettings, null); Spacer(Modifier.width(6.dp)); Text("Admin Demo") }
        TextButton(onClick = onDeveloper) { Text("DeveloperAdmin Demo") }
        Text("V1.0 • Native Android MVP", color = Grey)
    }
}

@Composable
fun Mobile(viewModel: AuthViewModel, go: (Screen) -> Unit) {
    val phone by viewModel.phone.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    var otpSent by remember { mutableStateOf(false) }
    var otp by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(18.dp)) {
        Text("स्वयंसेवक पंजीयन", fontSize = 25.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(8.dp))
        Text("पहले अपना मोबाइल नंबर सत्यापित करें।")
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(phone, { viewModel.phone.value = it }, Modifier.fillMaxWidth(), label = { Text("मोबाइल नंबर") }, prefix = { Text("+91  ") })
        
        if (otpSent) {
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(otp, { otp = it }, Modifier.fillMaxWidth(), label = { Text("OTP") })
        }

        if (uiState is AuthUiState.Error) {
            Text((uiState as AuthUiState.Error).message, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(Modifier.weight(1f))
        
        if (uiState is AuthUiState.Loading) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            PrimaryButton(
                if (otpSent) "OTP सत्यापित करें" else "OTP भेजें",
                if (otpSent) Icons.Default.CheckCircle else Icons.Default.Sms
            ) {
                if (!otpSent) {
                    otpSent = true
                } else {
                    go(Screen.Personal)
                }
            }
        }
    }
}

@Composable
fun Personal(viewModel: AuthViewModel, go: (Screen) -> Unit) {
    val name by viewModel.name.collectAsState()
    val fatherName by viewModel.fatherName.collectAsState()
    val dob by viewModel.dob.collectAsState()

    Column(Modifier.fillMaxSize().padding(18.dp)) {
        Step(2, 5)
        Spacer(Modifier.height(18.dp))
        OutlinedTextField(name, { viewModel.name.value = it }, Modifier.fillMaxWidth(), label = { Text("नाम") })
        Spacer(Modifier.height(12.dp))
        OutlinedTextField("", {}, Modifier.fillMaxWidth(), label = { Text("नाम (अंग्रेज़ी में)") })
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(fatherName, { viewModel.fatherName.value = it }, Modifier.fillMaxWidth(), label = { Text("पिता / अभिभावक का नाम") })
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(dob, { viewModel.dob.value = it }, Modifier.fillMaxWidth(), label = { Text("जन्म दिनांक") })
        Spacer(Modifier.weight(1f))
        PrimaryButton("आगे बढ़ें") { go(Screen.Organization) }
    }
}

@Composable
fun Step(step: Int, total: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("$step/$total", fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(8.dp))
        LinearProgressIndicator({ step.toFloat() / total }, Modifier.fillMaxWidth().height(6.dp))
    }
}

@Composable
fun Organization(viewModel: AuthViewModel, go: (Screen) -> Unit) {
    val metro by viewModel.metro.collectAsState()
    val city by viewModel.city.collectAsState()
    val basti by viewModel.basti.collectAsState()
    val branch by viewModel.branch.collectAsState()
    val mohalla by viewModel.mohalla.collectAsState()

    val options = listOf(
        "महानगर" to listOf("इंदौर", "भोपाल", "उज्जैन"),
        "नगर" to listOf("पश्चिम नगर", "पूर्व नगर", "उत्तर नगर"),
        "बस्ती" to listOf("विजय नगर बस्ती", "मालवीय नगर बस्ती"),
        "शाखा" to listOf("विजय नगर शाखा", "मुख्य शाखा"),
        "मोहल्ला" to listOf("विजय नगर", "स्कीम नंबर 54", "सुखलिया")
    )
    val values = listOf(metro, city, basti, branch, mohalla)
    val setters = listOf<(String) -> Unit>(
        { viewModel.metro.value = it }, { viewModel.city.value = it }, 
        { viewModel.basti.value = it }, { viewModel.branch.value = it }, 
        { viewModel.mohalla.value = it }
    )

    LazyColumn(Modifier.fillMaxSize().padding(18.dp)) {
        item { Step(3, 5); Spacer(Modifier.height(18.dp)) }
        items(options.indices.toList()) { i ->
            Dropdown(options[i].first, values[i], options[i].second, setters[i])
            Spacer(Modifier.height(12.dp))
        }
        item { PrimaryButton("आगे बढ़ें") { go(Screen.Interest) } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dropdown(label: String, value: String, items: List<String>, onValue: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded, { expanded = !expanded }) {
        OutlinedTextField(
            value, {}, Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
            label = { Text(label) }, readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
        )
        ExposedDropdownMenu(expanded, { expanded = false }) {
            items.forEach { item ->
                DropdownMenuItem(text = { Text(item) }, onClick = { onValue(item); expanded = false })
            }
        }
    }
}

@Composable
fun Interest(viewModel: AuthViewModel, go: (Screen) -> Unit) {
    val options = listOf("शारीरिक", "गीत / संगीत", "बौद्धिक", "खेल", "योग", "सेवा कार्य", "संपर्क", "प्रचार", "तकनीकी / IT", "लेखन", "आयोजन व्यवस्था", "अन्य")
    val selected = viewModel.interests

    Column(Modifier.fillMaxSize().padding(18.dp)) {
        Step(4, 5)
        Spacer(Modifier.height(16.dp))
        Text("अपनी रुचि चुनें", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        LazyColumn(Modifier.weight(1f)) {
            items(options) { item ->
                Row(
                    Modifier.fillMaxWidth().clickable {
                        if (selected.contains(item)) selected.remove(item) else selected.add(item)
                    }.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(selected.contains(item), { checked ->
                        if (checked) selected.add(item) else selected.remove(item)
                    }, colors = CheckboxDefaults.colors(checkedColor = Saffron))
                    Text(item)
                }
            }
        }
        PrimaryButton("पंजीयन देखें") { go(Screen.Review) }
    }
}

@Composable
fun Review(viewModel: AuthViewModel, go: (Screen) -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val name by viewModel.name.collectAsState()
    val father by viewModel.fatherName.collectAsState()
    val dob by viewModel.dob.collectAsState()
    val phone by viewModel.phone.collectAsState()
    
    val metro by viewModel.metro.collectAsState()
    val city by viewModel.city.collectAsState()
    val basti by viewModel.basti.collectAsState()
    val branch by viewModel.branch.collectAsState()
    val mohalla by viewModel.mohalla.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Registered) {
            go(Screen.Success)
        }
    }

    LazyColumn(Modifier.fillMaxSize().padding(18.dp)) {
        item {
            SectionCard("व्यक्तिगत जानकारी", Icons.Default.Person) {
                Info("नाम", name); Info("पिता / अभिभावक", father)
                Info("जन्म दिनांक", dob); Info("मोबाइल", phone)
            }
            SectionCard("संगठन", Icons.Default.AccountTree) {
                Info("महानगर", metro); Info("नगर", city); Info("बस्ती", basti)
                Info("शाखा", branch); Info("मोहल्ला", mohalla); Info("भूमिका", "स्वयंसेवक")
            }
            SectionCard("रुचि / कार्यक्षेत्र", Icons.Default.Interests) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    viewModel.interests.forEach {
                        AssistChip(onClick = {}, label = { Text(it) })
                    }
                }
            }
            
            if (uiState is AuthUiState.Error) {
                Text((uiState as AuthUiState.Error).message, color = Color.Red, modifier = Modifier.padding(bottom = 8.dp))
            }

            if (uiState is AuthUiState.Loading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                PrimaryButton("पंजीयन जमा करें", Icons.Default.CheckCircle) {
                    viewModel.submitRegistration()
                }
            }
        }
    }
}

@Composable
fun Success(viewModel: AuthViewModel, done: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val regId = (uiState as? AuthUiState.Registered)?.regId ?: "SVY-2026-PENDING"

    Column(Modifier.fillMaxSize().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.weight(1f))
        Icon(Icons.Default.CheckCircle, null, tint = Green, modifier = Modifier.size(80.dp))
        Spacer(Modifier.height(18.dp))
        Text("आपका पंजीयन सफल हुआ!", fontSize = 25.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(12.dp))
        Text("आपका पंजीयन क्रमांक", color = Grey)
        Text(regId, fontSize = 28.sp, fontWeight = FontWeight.Black, color = Navy)
        Spacer(Modifier.height(12.dp))
        Text("आपकी जानकारी सत्यापन के लिए भेजी गई है।", textAlign = TextAlign.Center)
        Spacer(Modifier.weight(1f))
        PrimaryButton("होम पर जाएँ", Icons.Default.Home) {
            viewModel.resetState()
            done()
        }
    }
}
