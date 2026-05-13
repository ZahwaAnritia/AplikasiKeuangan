package com.example.comzahwasakuku.ui.screens

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.comzahwasakuku.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: AuthViewModel,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onCategoryClick: () -> Unit
) {
    // Definisi Warna Aksesibilitas
    val NavyDark = Color(0xFF0D3B4E)
    val NavyBackground = Color(0xFF071F2A)
    val AccessibleLightBlue = Color(0xFFA8D8E8)
    val HighContrastGray = Color(0xFF424242) // Untuk teks sekunder

    val userName by viewModel.userName.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()
    val savedLimit by viewModel.currentLimit.collectAsState()
    val savedPrice by viewModel.currentIndomiePrice.collectAsState()

    var inputLimit by remember { mutableStateOf("") }
    var inputPrice by remember { mutableStateOf("") }

    LaunchedEffect(savedLimit, savedPrice) {
        if (inputLimit.isEmpty()) inputLimit = savedLimit
        if (inputPrice.isEmpty()) inputPrice = savedPrice
    }

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showEditSuccessDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // Status Bar Sync
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = NavyDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF1F4F7))) {

        // Header Background
        Box(
            modifier = Modifier.fillMaxWidth().height(300.dp)
                .background(Brush.verticalGradient(listOf(NavyDark, NavyBackground)))
        )

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
        ) {
            Text(
                text = "Profil Saya",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(top = 44.dp, start = 20.dp, bottom = 16.dp)
            )

            // Area Foto Profil
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(110.dp).shadow(15.dp, CircleShape).clip(CircleShape).background(Color.White).padding(4.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(Color(0xFFF1F4F7)), contentAlignment = Alignment.Center) {
                        Text("🎓", fontSize = 54.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = userName, fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.White)
                Text(text = userEmail, fontSize = 14.sp, color = AccessibleLightBlue)
            }

            Column(
                modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // REDESAIN: Menu Kelola Kategori (Kontras Tinggi)
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().clickable { onCategoryClick() }.shadow(8.dp, RoundedCornerShape(24.dp))
                ) {
                    Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(48.dp).background(Color(0xFFE8F5E9), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                            Icon(Icons.AutoMirrored.Filled.List, null, tint = NavyDark)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Kelola Kategori", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NavyDark)
                            Text("Atur kategori pemasukan & pengeluaran", fontSize = 12.sp, color = HighContrastGray)
                        }
                        // PERBAIKAN: Kontras Ikon Navigasi ditingkatkan ke NavyDark
                        Icon(Icons.Default.ChevronRight, null, tint = NavyDark)
                    }
                }

                // Card Pengaturan (Fokus ke Aksesibilitas Input)
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(24.dp))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Pengaturan Keuangan", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = NavyDark)
                        Spacer(modifier = Modifier.height(20.dp))

                        // PERBAIKAN: Input Limit (Double Coding & Focus Border)
                        // Input Limit Bulanan
                        OutlinedTextField(
                            value = inputLimit,
                            onValueChange = { if (it.all { c -> c.isDigit() }) inputLimit = it },
                            label = { Text("Limit Anggaran Bulanan", fontWeight = FontWeight.Bold) },
                            leadingIcon = {
                                Icon(Icons.Default.AccountBalanceWallet, null, tint = NavyDark)
                            },
                            prefix = {
                                Text("Rp ", fontWeight = FontWeight.ExtraBold, color = Color.Black)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NavyDark,
                                focusedLabelColor = NavyDark,
                                unfocusedBorderColor = HighContrastGray
                                // BARIS focusedStrokeWidth DIHAPUS DARI SINI
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

// Input Harga Indomie
                        OutlinedTextField(
                            value = inputPrice,
                            onValueChange = { if (it.all { c -> c.isDigit() }) inputPrice = it },
                            label = { Text("Harga Satuan Indomie", fontWeight = FontWeight.Bold) },
                            leadingIcon = {
                                Icon(Icons.Default.Restaurant, null, tint = NavyDark)
                            },
                            prefix = {
                                Text("Rp ", fontWeight = FontWeight.ExtraBold, color = Color.Black)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NavyDark,
                                focusedLabelColor = NavyDark,
                                unfocusedBorderColor = HighContrastGray
                                // BARIS focusedStrokeWidth DIHAPUS DARI SINI
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                viewModel.updateProfile(inputLimit, inputPrice)
                                showEditSuccessDialog = true
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyDark),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Simpan Perubahan", fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                    }
                }

                // Logout Akun (Warning High Contrast)
                Surface(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(2.dp, Color(0xFFFF5252)) // Border merah lebih tegas
                ) {
                    Row(modifier = Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Icon(Icons.Default.Logout, null, tint = Color(0xFFFF5252), modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Logout Akun", fontWeight = FontWeight.Black, color = Color(0xFFFF5252))
                    }
                }
            }
        }

        // Dialog Sukses Inklusif
        if (showEditSuccessDialog) {
            androidx.compose.ui.window.Dialog(onDismissRequest = { showEditSuccessDialog = false }) {
                Surface(modifier = Modifier.fillMaxWidth(0.85f).wrapContentHeight(), shape = RoundedCornerShape(28.dp), color = Color.White) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.size(72.dp).background(NavyDark, CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CheckCircle, null, tint = Color.White, modifier = Modifier.size(44.dp))
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(text = "Berhasil!", fontSize = 22.sp, fontWeight = FontWeight.Black, color = NavyDark)
                        Text(text = "Profil kamu sudah diperbarui.", fontSize = 14.sp, color = HighContrastGray)
                        Spacer(modifier = Modifier.height(28.dp))
                        Button(
                            onClick = { showEditSuccessDialog = false },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyDark),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Siap!", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        // Alert Dialog Logout
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                containerColor = Color.White,
                title = { Text("Logout?", fontWeight = FontWeight.ExtraBold, color = NavyDark) },
                text = { Text("Yakin ingin keluar dari akun SAKUKU?", color = Color.Black) },
                confirmButton = {
                    TextButton(onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                        onLogoutClick()
                    }) { Text("Ya, Keluar", color = Color(0xFFFF5252), fontWeight = FontWeight.ExtraBold) }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) { Text("Batal", color = HighContrastGray) }
                }
            )
        }
    }
}