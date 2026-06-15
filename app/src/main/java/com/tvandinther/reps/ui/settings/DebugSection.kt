package com.tvandinther.reps.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tvandinther.reps.data.db.AppDatabase
import com.tvandinther.reps.data.debug.DebugDatabaseHelper
import com.tvandinther.reps.ui.theme.ColorInk6
import com.tvandinther.reps.ui.theme.ColorSignal
import com.tvandinther.reps.ui.theme.StyleEyebrow
import com.tvandinther.reps.ui.theme.StyleH3
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
internal fun DebugSection() {
    val context = LocalContext.current
    val database: AppDatabase = koinInject()
    val scope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/octet-stream"),
    ) { uri ->
        uri?.let {
            scope.launch(Dispatchers.IO) {
                DebugDatabaseHelper.exportDatabase(context, database, it)
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri ->
        uri?.let {
            scope.launch(Dispatchers.IO) {
                DebugDatabaseHelper.importDatabase(context, database, it)
            }
        }
    }

    // Accordion header
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = "DEBUG", style = StyleEyebrow, color = ColorInk6)
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Color(0xFF1A1A1A)),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = if (expanded) "▴" else "▾",
            style = StyleEyebrow,
            color = ColorInk6,
        )
    }

    AnimatedVisibility(visible = expanded) {
        Column {
            DebugActionRow(label = "DOWNLOAD DATABASE") {
                exportLauncher.launch("reps-debug.db")
            }
            DebugActionRow(label = "LOAD DATABASE FROM FILE") {
                importLauncher.launch(arrayOf("application/octet-stream", "*/*"))
            }
        }
    }
}

@Composable
private fun DebugActionRow(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = StyleH3,
            color = ColorSignal,
            modifier = Modifier.weight(1f),
        )
        Text(text = "→", style = StyleH3, color = ColorSignal)
    }
}
