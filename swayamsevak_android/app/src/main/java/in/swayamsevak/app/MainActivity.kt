package `in`.swayamsevak.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import `in`.swayamsevak.app.ui.auth.AuthViewModel
import `in`.swayamsevak.app.ui.components.AppScaffold
import `in`.swayamsevak.app.ui.components.Cream
import `in`.swayamsevak.app.ui.components.Saffron
import `in`.swayamsevak.app.ui.components.SaffronDark
import `in`.swayamsevak.app.ui.features.admin.*
import `in`.swayamsevak.app.ui.features.auth.*
import `in`.swayamsevak.app.ui.features.dashboard.*

enum class Screen {
    Welcome, Mobile, Personal, Organization, Interest, Review, Success,
    Dashboard, Profile, Attendance, Poll,
    Admin, Registrations, OrganizationAdmin, Developer, Permissions
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SwayamsevakApp() }
    }
}

@Composable
fun SwayamsevakApp() {
    val authViewModel: AuthViewModel = viewModel()
    
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Saffron,
            secondary = SaffronDark,
            background = Cream,
            surface = androidx.compose.ui.graphics.Color.White
        )
    ) {
        var screen by remember { mutableStateOf(Screen.Welcome) }
        var backStack by remember { mutableStateOf(listOf<Screen>()) }

        val go = { next: Screen ->
            backStack = backStack + screen
            screen = next
        }
        val back = {
            if (backStack.isNotEmpty()) {
                screen = backStack.last()
                backStack = backStack.dropLast(1)
            }
        }

        Surface(modifier = Modifier.fillMaxSize(), color = Cream) {
            when (screen) {
                Screen.Welcome -> Welcome(
                    onRegister = { go(Screen.Mobile) },
                    onAdmin = { go(Screen.Admin) },
                    onDeveloper = { go(Screen.Developer) }
                )
                Screen.Mobile -> AppScaffold("मोबाइल सत्यापन", true, { back() }) { Mobile(authViewModel, go) }
                Screen.Personal -> AppScaffold("व्यक्तिगत जानकारी", true, { back() }) { Personal(authViewModel, go) }
                Screen.Organization -> AppScaffold("संगठन जानकारी", true, { back() }) { Organization(authViewModel, go) }
                Screen.Interest -> AppScaffold("रुचि / कार्यक्षेत्र", true, { back() }) { Interest(authViewModel, go) }
                Screen.Review -> AppScaffold("पंजीयन समीक्षा", true, { back() }) { Review(authViewModel, go) }
                Screen.Success -> AppScaffold("पंजीयन सफल", false, {}) { Success(authViewModel) { screen = Screen.Dashboard; backStack = emptyList() } }
                Screen.Dashboard -> Dashboard(go)
                Screen.Profile -> AppScaffold("मेरा प्रोफ़ाइल", true, { back() }) { Profile() }
                Screen.Attendance -> AppScaffold("आज की उपस्थिति", true, { back() }) { Attendance() }
                Screen.Poll -> AppScaffold("मतदान / सुझाव", true, { back() }) { Poll() }
                Screen.Admin -> AppScaffold("प्रशासन", true, { back() }) { Admin(go) }
                Screen.Registrations -> AppScaffold("पंजीयन प्रबंधन", true, { back() }) { Registrations(go) }
                Screen.OrganizationAdmin -> AppScaffold("संगठन प्रबंधन", true, { back() }) { OrganizationAdmin() }
                Screen.Developer -> AppScaffold("फ़ील्ड प्रबंधन", true, { back() }) { Developer(go) }
                Screen.Permissions -> AppScaffold("भूमिका एवं अधिकार", true, { back() }) { Permissions() }
            }
        }
    }
}
