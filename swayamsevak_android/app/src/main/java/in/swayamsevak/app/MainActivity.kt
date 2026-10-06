package `in`.swayamsevak.app

import android.os.Bundle
import androidx.activity.ComponentActivity
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
import `in`.swayamsevak.app.ui.features.auth.*
import `in`.swayamsevak.app.ui.features.dashboard.*

enum class Screen {
    Index, Library, Hierarchy, Directory, MyShakha, AdminLogin, AdminConsole,
    Mobile, Personal, Organization, Interest, Review, Success,
    Profile, Attendance, Poll
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
            }
        }

        Surface(modifier = Modifier.fillMaxSize(), color = Cream) {
            when (screen) {
                Screen.Index -> RssIndex(
                    onRegister = { go(Screen.Mobile) },
                    onDirectory = { go(Screen.Directory) },
                    onMyShakha = { go(Screen.MyShakha) },
                    onAdmin = { authViewModel.resetAdminState(); go(Screen.AdminLogin) },
                    onHierarchy = { go(Screen.Hierarchy) },
                    onTopic = { topic ->
                        selectedTopic = topic
                        go(Screen.Library)
                    }
                )
                Screen.Library -> AppScaffold(selectedTopic?.title ?: "RSS", true, { back() }) {
                    RssTopicPage(selectedTopic ?: rssTopics.first())
                }
                Screen.Hierarchy -> AppScaffold("RSS संगठन संरचना", true, { back() }) {
                    RssHierarchyPage()
                }
                Screen.Directory -> AppScaffold("सदस्य खोजें", true, { back() }) {
                    MemberSearchPage(authViewModel)
                }
                Screen.MyShakha -> AppScaffold("मेरी शाखा", true, { back() }) {
                    MyShakhaPage()
                }
                Screen.AdminLogin -> AppScaffold("Admin प्रवेश", true, { back() }) {
                    AdminLoginPage(authViewModel) { go(Screen.AdminConsole) }
                }
                Screen.AdminConsole -> AppScaffold("शाखा प्रबंधन", true, { back() }) {
                    AdminConsolePage()
                }
                Screen.Mobile -> AppScaffold("स्वयंसेवक पंजीयन", true, { back() }) { Mobile(authViewModel, go) }
                Screen.Personal -> AppScaffold("व्यक्तिगत जानकारी", true, { back() }) { Personal(authViewModel, go) }
                Screen.Organization -> AppScaffold("संगठन जानकारी", true, { back() }) { Organization(authViewModel, go) }
                Screen.Interest -> AppScaffold("रुचि / कार्यक्षेत्र", true, { back() }) { Interest(authViewModel, go) }
                Screen.Review -> AppScaffold("पंजीयन समीक्षा", true, { back() }) { Review(authViewModel, go) }
                Screen.Success -> AppScaffold("पंजीयन सफल", false, {}) {
                    Success(authViewModel) {
                        screen = Screen.Index
                        backStack = emptyList()
                    }
                }
                Screen.Profile -> AppScaffold("मेरा प्रोफ़ाइल", true, { back() }) { Profile() }
                Screen.Attendance -> AppScaffold("आज की उपस्थिति", true, { back() }) { Attendance() }
                Screen.Poll -> AppScaffold("मतदान / सुझाव", true, { back() }) { Poll() }
            }
        }
    }
}
