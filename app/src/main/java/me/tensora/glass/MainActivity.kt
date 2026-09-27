package me.tensora.glass

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setContent { GlassLauncher() }
    }
}

private enum class GlassTheme(val title: String, val bg: List<Color>) {
    Clear("Clear", listOf(Color(0xFF101216), Color(0xFF090A0D))),
    Aurora("Aurora", listOf(Color(0xFF102027), Color(0xFF080B10))),
    Violet("Violet", listOf(Color(0xFF21182A), Color(0xFF09090D)))
}

@Composable
private fun GlassLauncher() {
    val context = LocalContext.current
    var theme by remember {
        mutableStateOf(
            context.getSharedPreferences("glass", Context.MODE_PRIVATE).getInt("theme", 0)
        )
    }
    var drawer by remember { mutableStateOf(false) }
    var settings by remember { mutableStateOf(false) }
    var now by remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now()
            kotlinx.coroutines.delay(1000)
        }
    }

    val selected = GlassTheme.entries[theme.coerceIn(0, GlassTheme.entries.lastIndex)]
    val time = now.format(DateTimeFormatter.ofPattern("HH:mm"))
    val date = now.format(DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.getDefault()))

    Box(
        Modifier.fillMaxSize().background(Brush.linearGradient(selected.bg))
    ) {
        Box(
            Modifier.size(320.dp).align(Alignment.TopEnd).background(
                Brush.radialGradient(listOf(Color.White.copy(alpha = .06f), Color.Transparent))
            )
        )

        Column(
            Modifier.fillMaxSize().padding(horizontal = 20.dp).navigationBarsPadding()
        ) {
            Spacer(Modifier.height(44.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(time, color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Light)
                    Text(date, color = Color.White.copy(alpha = .55f), fontSize = 14.sp)
                }
                GlassButton("⋯") { settings = true }
            }

            Spacer(Modifier.height(28.dp))

            GlassPanel(Modifier.fillMaxWidth(), 30) {
                Column(Modifier.padding(18.dp)) {
                    Text("Glass", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    Text("Liquid home", color = Color.White.copy(alpha = .45f), fontSize = 12.sp)
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Action("Apps", "⌕") { drawer = true }
                        Action("Theme", "✦") { settings = true }
                        Action("Default", "⌂") { requestHome(context) }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            GlassPanel(Modifier.fillMaxWidth(), 28) {
                Row(
                    Modifier.fillMaxWidth().padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    DockItem("⌕", "Apps") { drawer = true }
                    DockItem("✦", "Themes") { settings = true }
                    DockItem("⌂", "Default") { requestHome(context) }
                }
            }
            Spacer(Modifier.height(14.dp))
        }

        if (drawer) AppDrawer(context) { drawer = false }

        if (settings) {
            SettingsPanel(
                context = context,
                current = theme,
                onTheme = {
                    theme = it
                    context.getSharedPreferences("glass", Context.MODE_PRIVATE)
                        .edit().putInt("theme", it).apply()
                },
                close = { settings = false }
            )
        }
    }
}

private fun requestHome(context: Context) {
    if (Build.VERSION.SDK_INT >= 29) {
        val roles = context.getSystemService(RoleManager::class.java)
        if (roles != null && roles.isRoleAvailable(RoleManager.ROLE_HOME)) {
            context.startActivity(roles.createRequestRoleIntent(RoleManager.ROLE_HOME))
            return
        }
    }
    context.startActivity(Intent(Settings.ACTION_HOME_SETTINGS))
}

@Composable
private fun RowScope.Action(title: String, icon: String, click: () -> Unit) {
    Box(
        Modifier.weight(1f).height(64.dp).clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = .06f))
            .border(1.dp, Color.White.copy(alpha = .10f), RoundedCornerShape(20.dp))
            .clickable(onClick = click),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, color = Color.White.copy(alpha = .9f), fontSize = 21.sp)
            Text(title, color = Color.White.copy(alpha = .52f), fontSize = 9.sp)
        }
    }
}

@Composable
private fun DockItem(icon: String, title: String, click: () -> Unit) {
    Column(
        Modifier.clip(RoundedCornerShape(18.dp)).clickable(onClick = click)
            .padding(horizontal = 24.dp, vertical = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, color = Color.White.copy(alpha = .88f), fontSize = 22.sp)
        Text(title, color = Color.White.copy(alpha = .42f), fontSize = 8.sp)
    }
}

@Composable
private fun GlassButton(icon: String, click: () -> Unit) {
    Box(
        Modifier.size(46.dp).clip(CircleShape)
            .background(Color.White.copy(alpha = .07f))
            .border(1.dp, Color.White.copy(alpha = .12f), CircleShape)
            .clickable(onClick = click),
        contentAlignment = Alignment.Center
    ) {
        Text(icon, color = Color.White.copy(alpha = .8f), fontSize = 22.sp)
    }
}

@Composable
private fun GlassPanel(modifier: Modifier, radius: Int, content: @Composable () -> Unit) {
    val shape = RoundedCornerShape(radius.dp)
    Box(
        modifier.clip(shape)
            .background(Color.White.copy(alpha = .075f))
            .border(1.dp, Color.White.copy(alpha = .13f), shape)
    ) { content() }
}

private data class AppEntry(val label: String, val component: android.content.ComponentName)

@Composable
private fun AppDrawer(context: Context, close: () -> Unit) {
    var apps by remember { mutableStateOf<List<AppEntry>>(emptyList()) }

    LaunchedEffect(Unit) {
        val launcher = context.getSystemService(android.content.pm.LauncherApps::class.java)
        apps = launcher.getActivityList(null, android.os.Process.myUserHandle())
            .distinctBy { it.componentName }
            .sortedBy { it.label.toString().lowercase(Locale.getDefault()) }
            .map { AppEntry(it.label.toString(), it.componentName) }
    }

    Box(
        Modifier.fillMaxSize().background(Color(0xC908090B))
            .padding(top = 42.dp, start = 14.dp, end = 14.dp, bottom = 20.dp)
    ) {
        GlassPanel(Modifier.fillMaxSize(), 34) {
            Column(Modifier.padding(18.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Applications", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.SemiBold)
                        Text("${apps.size} installed", color = Color.White.copy(alpha = .45f), fontSize = 12.sp)
                    }
                    GlassButton("×", close)
                }

                Spacer(Modifier.height(14.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(apps, key = { it.component.flattenToString() }) { app ->
                        val initial = app.label.firstOrNull()?.uppercase() ?: "?"
                        Column(
                            Modifier.clickable {
                                val intent = Intent().apply {
                                    component = app.component
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                                close()
                            },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                Modifier.size(52.dp).clip(RoundedCornerShape(17.dp))
                                    .background(Color.White.copy(alpha = .09f))
                                    .border(1.dp, Color.White.copy(alpha = .12f), RoundedCornerShape(17.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(initial, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                            }
                            Spacer(Modifier.height(5.dp))
                            Text(app.label, color = Color.White.copy(alpha = .75f), fontSize = 10.sp, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsPanel(
    context: Context,
    current: Int,
    onTheme: (Int) -> Unit,
    close: () -> Unit
) {
    Box(Modifier.fillMaxSize().background(Color(0xCC08090B)).padding(16.dp)) {
        GlassPanel(Modifier.fillMaxWidth(), 34) {
            Column(Modifier.padding(20.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Glass settings", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                    GlassButton("×", close)
                }

                Spacer(Modifier.height(22.dp))
                Text("Theme", color = Color.White.copy(alpha = .55f), fontSize = 12.sp)
                Spacer(Modifier.height(10.dp))

                GlassTheme.entries.forEachIndexed { index, item ->
                    val active = current == index
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
                            .background(if (active) Color.White.copy(alpha = .12f) else Color.White.copy(alpha = .045f))
                            .border(1.dp, Color.White.copy(alpha = if (active) .24f else .08f), RoundedCornerShape(18.dp))
                            .clickable { onTheme(index) }.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier.size(28.dp).clip(CircleShape)
                                .background(Brush.linearGradient(item.bg))
                        )
                        Spacer(Modifier.size(12.dp))
                        Text(item.title, color = Color.White, fontSize = 14.sp)
                        if (active) {
                            Spacer(Modifier.weight(1f))
                            Text("✓", color = Color.White)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }

                Spacer(Modifier.height(14.dp))
                Text("Default launcher", color = Color.White.copy(alpha = .55f), fontSize = 12.sp)
                Spacer(Modifier.height(10.dp))

                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = .07f))
                        .border(1.dp, Color.White.copy(alpha = .12f), RoundedCornerShape(20.dp))
                        .clickable { requestHome(context) }.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Set Glass as default", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Text("Android will ask you to confirm", color = Color.White.copy(alpha = .45f), fontSize = 11.sp)
                    }
                    Text("→", color = Color.White.copy(alpha = .75f), fontSize = 20.sp)
                }
            }
        }
    }
}
