package com.example.comzahwasakuku.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
// Import warna tema
import com.example.comzahwasakuku.ui.theme.CyanPrimary

@Composable
fun SplashScreen(onTimeout: () -> Unit) {


    LaunchedEffect(true) {
        delay(10000)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(

                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0D3B4E), Color(0xFF071F2A))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = "Logo Sakuku",
                    tint = Color(0xFF0D3B4E),
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))


            Text(
                text = "SAKUKU",
                color = Color(0xFFE8F8FC),
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Cerdas Kelola Keuangan Mahasiswa",
                color = Color(0xFFA8D8E8),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }


        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Smart Financial Assistant",
                color = Color(0xFF7BBCCE),
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "v1.0.0 by Zahwa",
                color = Color(0xFF5A9BAD),
                fontSize = 11.sp
            )
        }
    }
}