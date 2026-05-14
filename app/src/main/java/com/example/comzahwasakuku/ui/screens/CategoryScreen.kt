package com.example.comzahwasakuku.ui.screens

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.comzahwasakuku.data.local.entity.CategoryEntity
import com.example.comzahwasakuku.ui.viewmodel.AuthViewModel
import com.example.comzahwasakuku.ui.viewmodel.CategoryViewModel

val categoryIcons = mapOf(
    "Makan" to Icons.Default.Restaurant,
    "Belanja" to Icons.Default.ShoppingCart,
    "Transport" to Icons.Default.DirectionsCar,
    "Tagihan" to Icons.Default.Receipt,
    "Uang" to Icons.Default.AttachMoney,
    "Kampus" to Icons.Default.School,
    "Kesehatan" to Icons.Default.LocalHospital,
    "Hiburan" to Icons.Default.Movie,
    "Lainnya" to Icons.Default.Category
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel,
    authViewModel: AuthViewModel,
    onBackClick: () -> Unit
) {
    // DEFINISI WARNA AKSESIBILITAS
    val NavyDark = Color(0xFF0D3B4E)
    val NavyBackground = Color(0xFF071F2A)
    val AccessibleLightBlue = Color(0xFFA8D8E8)
    val HighContrastGray = Color(0xFF424242)

    val userId by authViewModel.userId.collectAsState()
    LaunchedEffect(userId) {
        if (userId != -1) viewModel.setUserId(userId)
    }

    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var categoryToDelete by remember { mutableStateOf<CategoryEntity?>(null) }
    var showAddSuccessDialog by remember { mutableStateOf(false) }

    var newCategoryName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("OUT") }
    var selectedIconName by remember { mutableStateOf("Lainnya") }

    val isInputValid = newCategoryName.isNotBlank()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = NavyDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    // --- DIALOG HAPUS (Ditingkatkan Kontrasnya) ---
    if (showDeleteDialog && categoryToDelete != null) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showDeleteDialog = false }) {
            Surface(
                modifier = Modifier.fillMaxWidth(0.8f).wrapContentHeight(),
                shape = RoundedCornerShape(32.dp),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(64.dp).background(Color(0xFFFFEBEE), CircleShape), contentAlignment = Alignment.Center) {
                        // Mempertahankan merah gelap untuk aksesibilitas
                        Icon(Icons.Default.DeleteForever, null, tint = Color(0xFFB3261E), modifier = Modifier.size(36.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Hapus Kategori", fontSize = 20.sp, fontWeight = FontWeight.Black, color = NavyDark)
                    Text("Hapus '${categoryToDelete?.name}'?", fontSize = 14.sp, color = HighContrastGray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            categoryToDelete?.let { viewModel.deleteCategory(it) }
                            showDeleteDialog = false
                            categoryToDelete = null
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3261E)), // Merah Gelap
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Ya, Hapus", fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                    TextButton(onClick = { showDeleteDialog = false }, modifier = Modifier.fillMaxWidth()) {
                        Text("Batal", color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // --- DIALOG SUKSES (Tema Navy Dark) ---
    if (showAddSuccessDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showAddSuccessDialog = false }) {
            Surface(modifier = Modifier.fillMaxWidth(0.8f).wrapContentHeight(), shape = RoundedCornerShape(32.dp), color = Color.White) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(64.dp).background(NavyDark, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text("Berhasil!", fontSize = 20.sp, fontWeight = FontWeight.Black, color = NavyDark)
                    Text("Kategori baru ditambahkan.", fontSize = 14.sp, color = HighContrastGray)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { showAddSuccessDialog = false },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyDark),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Siap!", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF1F4F7))) {
        // HEADER GRADIENT NAVY
        Box(modifier = Modifier.fillMaxWidth().height(260.dp).background(Brush.verticalGradient(listOf(NavyDark, NavyBackground))))

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.padding(top = 44.dp, bottom = 16.dp, start = 8.dp, end = 16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                }
                Text("Kelola Kategori", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(start = 8.dp))
            }

            // DOUBLE-CODING PADA SELEKTOR (Teks + Ikon Arrow dari GitHub)
            Surface(
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                color = Color.White.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.4f))
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    val types = listOf("OUT" to "Pengeluaran", "IN" to "Pemasukan")
                    types.forEach { (type, label) ->
                        val isSelected = selectedType == type
                        val chipBg by animateColorAsState(if (isSelected) Color.White else Color.Transparent)

                        // Menggabungkan Arrow double-coding dengan warna dark red/green
                        val chipText = if (isSelected) (if (type == "OUT") Color(0xFFB3261E) else Color(0xFF2E7D32)) else Color.White
                        val iconVector = if (type == "OUT") Icons.Default.ArrowDownward else Icons.Default.ArrowUpward

                        Box(
                            modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(24.dp))
                                .background(chipBg).clickable { selectedType = type },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(iconVector, null, tint = chipText, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(label, color = chipText, fontWeight = FontWeight.Black, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // FORM INPUT (Fokus Aksesibilitas)
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).shadow(12.dp, RoundedCornerShape(28.dp)),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Tambah Kategori Kustom", fontSize = 16.sp, fontWeight = FontWeight.Black, color = NavyDark)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Pilih Visual Ikon:", fontSize = 12.sp, color = HighContrastGray, fontWeight = FontWeight.ExtraBold)
                    LazyRow(
                        modifier = Modifier.padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(categoryIcons.toList()) { (name, iconVector) ->
                            val isSelected = selectedIconName == name
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(54.dp).clip(CircleShape)
                                    .background(if (isSelected) NavyDark else Color(0xFFF1F4F7))
                                    .clickable { selectedIconName = name }
                            ) {
                                Icon(iconVector, name, tint = if (isSelected) Color.White else Color.DarkGray, modifier = Modifier.size(28.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = newCategoryName,
                            onValueChange = { newCategoryName = it },
                            placeholder = { Text("Nama kategori baru...", color = Color.Gray) },
                            leadingIcon = { Icon(Icons.Default.Edit, null, tint = NavyDark) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NavyDark,
                                unfocusedBorderColor = HighContrastGray,
                                focusedLabelColor = NavyDark
                            )
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = {
                                if (newCategoryName.isNotEmpty() && userId != -1) {
                                    viewModel.addCategory(userId, newCategoryName, selectedType, selectedIconName)
                                    newCategoryName = ""; selectedIconName = "Lainnya"; showAddSuccessDialog = true
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isInputValid) NavyDark else Color.LightGray,
                                disabledContainerColor = Color.LightGray.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.size(56.dp),
                            enabled = !isLoading && isInputValid
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = if (isInputValid) Color.White else Color.DarkGray
                                )
                            }
                        }
                    }
                }
            }

            Text(
                "Daftar Kategori ${if (selectedType == "OUT") "Pengeluaran" else "Pemasukan"}",
                modifier = Modifier.padding(start = 24.dp, top = 28.dp, bottom = 12.dp),
                fontWeight = FontWeight.Black, color = NavyDark, fontSize = 18.sp
            )

            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                val filteredList = categories.filter { it.type == selectedType }
                if (filteredList.isEmpty()) {
                    item {
                        Column(modifier = Modifier.fillMaxWidth().padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Category, null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                            Text("Kategori belum tersedia", color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                } else {
                    items(filteredList) { cat ->
                        Surface(
                            shape = RoundedCornerShape(20.dp), color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF1F4F7)),
                            modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp))
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                val iconVector = categoryIcons[cat.icon] ?: Icons.Default.Category
                                Box(
                                    modifier = Modifier.size(48.dp).background(
                                        if (cat.type == "OUT") Color(0xFFFFEBEE) else Color(0xFFE8F5E9), CircleShape
                                    ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = iconVector,
                                        contentDescription = null,
                                        tint = if (cat.type == "OUT") Color(0xFFB3261E) else Color(0xFF2E7D32),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Text(text = cat.name, fontSize = 16.sp, fontWeight = FontWeight.Black, color = NavyDark, modifier = Modifier.weight(1f))

                                if (cat.userId != 0) {
                                    IconButton(onClick = {
                                        categoryToDelete = cat
                                        showDeleteDialog = true
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Hapus",
                                            tint = Color(0xFFB3261E),
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                } else {
                                    // Mempertahankan tag DEFAULT dengan NavyDark (kontras tinggi)
                                    Surface(color = NavyDark, shape = RoundedCornerShape(8.dp)) {
                                        Text(
                                            "DEFAULT",
                                            fontSize = 10.sp,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}