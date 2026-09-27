package me.tensora.glass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GlassHome() }
    }
}

@Composable
private fun GlassHome() {
    val background = Brush.verticalGradient(
        listOf(Color(0xFF0F0F14), Color(0xFF17151F), Color(0xFF0B0B10))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(22.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(34.dp))

            Text(
                text = "10:53",
                color = Color.White,
                fontSize = 52.sp,
                fontWeight = FontWeight.Light
            )
            Text(
                text = "Sunday, 27 September",
                color = Color(0xFFBDB7C9),
                fontSize = 15.sp
            )

            Spacer(Modifier.height(34.dp))

            // First-pass circular photo orb. Photo picker + persistence comes next.
            Box(
                modifier = Modifier
                    .size(148.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFF8C6BFF), Color(0xFF33265A), Color(0xFF17151F))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("ADD\nPHOTO", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(28.dp))

            GlassCard()
        }
    }
}

@Composable
private fun GlassCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0x332F2B3A))
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Glass", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                Text("Liquid UI • v0.1", color = Color(0xFFAFA8BA), fontSize = 13.sp)
            }
            Text("🫧", fontSize = 28.sp)
        }
    }
}
