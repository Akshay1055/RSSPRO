package `in`.swayamsevak.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import `in`.swayamsevak.app.ui.auth.AuthViewModel
import `in`.swayamsevak.app.ui.components.AppScaffold
import `in`.swayamsevak.app.ui.components.Cream
import `in`.swayamsevak.app.ui.components.Saffron
import `in`.swayamsevak.app.ui.components.SaffronDark
import `in`.swayamsevak.app.ui.components.SurfaceDark
import `in`.swayamsevak.app.ui.features.admin.*
import `in`.swayamsevak.app.ui.features.auth.*
import `in`.swayamsevak.app.ui.features.dashboard.*

enum class Screen {
    Index, Library, Directory, MyShakha, ProfileAccess, AdminLogin, AdminConsole,
    Mobile, Personal, Organization, Interest, Review, Success,
    Dashboard, Profile, Attendance, Poll,
    Admin, Registrations, OrganizationAdmin, Developer, Permissions, AppDetailsAdmin
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
        colorScheme = darkColorScheme(
            primary = Saffron,
            secondary = SaffronDark,
            background = Cream,
            surface = SurfaceDark
        )
    ) {
        var screen by remember { mutableStateOf(Screen.Index) }
        var backStack by remember { mutableStateOf(listOf<Screen>()) }
        var selectedTopic by remember { mutableStateOf<RssTopic?>(null) }

        val go = { next: Screen ->
            backStack = backStack + screen
            screen = next
        }
        val back = {
            if (backStack.isNotEmpty()) {
                screen = backStack.last()
                backStack = backStack.dropLast(1)
            } else {
                screen = Screen.Index
            }
        }
        val goHome = {
            backStack = emptyList()
            screen = Screen.Index
        }

        BackHandler {
            if (backStack.isNotEmpty()) {
                screen = backStack.last()
                backStack = backStack.dropLast(1)
            } else if (screen != Screen.Index) {
                screen = Screen.Index
            }
        }

        Surface(modifier = Modifier.fillMaxSize(), color = Cream) {
            when (screen) {
                Screen.Index -> RssIndex(
                    onRegister = { go(Screen.Mobile) },
                    onMyShakha = { go(Screen.ProfileAccess) },
                    onAdmin = { authViewModel.resetAdminState(); go(Screen.AdminLogin) },
                    onTopic = { topic ->
                        selectedTopic = topic
                        go(Screen.Library)
                    }
                )
                Screen.Library -> AppScaffold(selectedTopic?.title ?: "RSS", true, { back() }, goHome) {
                    RssTopicPage(selectedTopic ?: rssTopics.first(), authViewModel)
                }
                Screen.Directory -> AppScaffold("सदस्य खोजें (प्रशासन)", true, { back() }, goHome) {
                    MemberSearchPage(authViewModel)
                }
                Screen.MyShakha -> AppScaffold("मेरी शाखा", true, { back() }, goHome) {
                    MyShakhaTabbedPage(authViewModel)
                }
                Screen.ProfileAccess -> AppScaffold("मेरी शाखा प्रवेश", true, { back() }, goHome) {
                    ProfileAccessPage(authViewModel) { screen = Screen.MyShakha; backStack = listOf(Screen.Index) }
                }
                Screen.AdminLogin -> AppScaffold("Admin प्रवेश", true, { back() }, goHome) {
                    AdminLoginPage(authViewModel) { go(Screen.Admin) }
                }
                Screen.AdminConsole -> AppScaffold("शाखा प्रबंधन", true, { back() }, goHome) {
                    AdminConsolePage()
                }
                Screen.Mobile -> AppScaffold("स्वयंसेवक पंजीयन", true, { back() }, goHome) { Mobile(authViewModel, go) }
                Screen.Personal -> AppScaffold("व्यक्तिगत जानकारी", true, { back() }, goHome) { Personal(authViewModel, go) }
                Screen.Organization -> AppScaffold("संगठन जानकारी", true, { back() }, goHome) { Organization(authViewModel, go) }
                Screen.Interest -> AppScaffold("रुचि / कार्यक्षेत्र", true, { back() }, goHome) { Interest(authViewModel, go) }
                Screen.Review -> AppScaffold("पंजीयन समीक्षा", true, { back() }, goHome) { Review(authViewModel, go) }
                Screen.Success -> AppScaffold("पंजीयन सफल", false, {}, goHome) {
                    Success(authViewModel) {
                        screen = Screen.MyShakha
                        backStack = listOf(Screen.Index)
                    }
                }
                Screen.Dashboard -> Dashboard(authViewModel, go)
                Screen.Profile -> AppScaffold("मेरी शाखा", true, { back() }, goHome) { MyShakhaTabbedPage(authViewModel) }
                Screen.Attendance -> AppScaffold("आज की उपस्थिति", true, { back() }, goHome) { Attendance() }
                Screen.Poll -> AppScaffold("मतदान / सुझाव", true, { back() }, goHome) { Poll() }
                Screen.Admin -> AppScaffold("प्रशासन", true, { back() }, goHome) { Admin(authViewModel, go) }
                Screen.Registrations -> AppScaffold("पंजीयन एवं खोज प्रबंधन", true, { back() }, goHome) { Registrations(authViewModel, go) }
                Screen.OrganizationAdmin -> AppScaffold("संगठन संरचना प्रबंधन", true, { back() }, goHome) { OrganizationAdmin(authViewModel) }
                Screen.Developer -> AppScaffold("ऐप विवरण एवं विकल्प प्रबंधन", true, { back() }, goHome) { AppDetailsAdminPage(authViewModel) }
                Screen.Permissions -> AppScaffold("भूमिका एवं अधिकार", true, { back() }, goHome) { Permissions(authViewModel) }
                Screen.AppDetailsAdmin -> AppScaffold("सामग्री एवं विवरण प्रबंधन", true, { back() }, goHome) { AppDetailsAdminPage(authViewModel) }
            }
        }
    }
}
