package `in`.swayamsevak.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val Saffron = Color(0xFFF57C00)
val SaffronDark = Color(0xFFE65100)
val Cream = Color(0xFFFFF8F1)
val Navy = Color(0xFF172B4D)
val Grey = Color(0xFF667085)
val Border = Color(0xFFE4E7EC)
val Green = Color(0xFF2E7D32)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(title: String, back: Boolean, onBack: () -> Unit, content: @Composable () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (back) IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        }
    ) { pad -> Box(Modifier.padding(pad)) { content() } }
}

@Composable
fun PrimaryButton(text: String, icon: ImageVector = Icons.Default.ArrowForward, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Saffron),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        Icon(icon, null)
        Spacer(Modifier.width(8.dp))
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SectionCard(title: String, icon: ImageVector? = null, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon?.let {
                    Icon(it, null, tint = Saffron)
                    Spacer(Modifier.width(8.dp))
                }
                Text(title, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun Info(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
        Text(label, Modifier.width(125.dp), color = Grey)
        Text(value, fontWeight = FontWeight.Bold)
    }
}
