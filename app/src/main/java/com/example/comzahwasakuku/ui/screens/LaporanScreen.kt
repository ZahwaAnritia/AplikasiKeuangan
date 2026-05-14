package com.example.comzahwasakuku.ui.screens

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import com.example.comzahwasakuku.data.local.entity.TransactionEntity
import com.example.comzahwasakuku.ui.viewmodel.AuthViewModel
import com.example.comzahwasakuku.ui.viewmodel.LaporanViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaporanScreen(
    navController: NavController,
    viewModel: LaporanViewModel,
    authViewModel: AuthViewModel
) {
    // DEFINISI WARNA AKSESIBILITAS (Navy Dark & High Contrast)
    val NavyDark = Color(0xFF0D3B4E)
    val NavyBackground = Color(0xFF071F2A)
    val HighContrastRed = Color(0xFFD32F2F) // Merah lebih gelap untuk Low Vision
    val HighContrastGreen = Color(0xFF2E7D32) // Hijau lebih gelap untuk Low Vision
    val AccessibleGray = Color(0xFF424242)

    val userId by authViewModel.userId.collectAsState()
    val transactionList by viewModel.getTransactions(userId).collectAsState(initial = emptyList())

    var selectedFilter by remember { mutableStateOf("Semua") }
    val filters = listOf("Mingguan", "Bulanan", "Semua")

    val filteredList = viewModel.filterTransactions(transactionList, selectedFilter)
        .sortedWith(compareByDescending<TransactionEntity> { it.date }.thenByDescending { it.id })

    val expenseList = filteredList.filter { it.type == "OUT" }
    val totalPengeluaran = expenseList.sumOf { it.amount }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<TransactionEntity?>(null) }

    val chartData = expenseList
        .groupBy { it.category }
        .mapValues { entry -> entry.value.sumOf { it.amount } }

    // PALET WARNA GRAFIK DENGAN KONTRAS TINGGI UNTUK CVD
    val chartColors = listOf(
        Color(0xFF0D3B4E), Color(0xFFFF5252), Color(0xFFFFEB3B),
        Color(0xFF4CAF50), Color(0xFF2196F3), Color(0xFFFF9800),
        Color(0xFF9C27B0), Color(0xFF795548)
    )

    fun formatRupiah(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        format.maximumFractionDigits = 0
        return format.format(amount).replace("Rp", "Rp ")
    }

    val formatDate: (Long) -> String = { timestamp ->
        SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(Date(timestamp))
    }

    // Sync Status Bar ke Navy Dark
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = NavyDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    // Dialog Hapus Inklusif
    if (showDeleteDialog && itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = Color.White,
            title = { Text("Hapus Transaksi?", fontWeight = FontWeight.Black, color = NavyDark) },
            text = { Text("Data '${itemToDelete?.category}' akan dihapus permanen.", color = Color.Black) },
            confirmButton = {
                Button(
                    onClick = {
                        itemToDelete?.let { viewModel.deleteTransaction(it) }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HighContrastRed)
                ) { Text("Ya, Hapus", color = Color.White, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal", color = AccessibleGray) }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF1F4F7))) {
        // HEADER GRADIENT NAVY DARK
        Box(
            modifier = Modifier.fillMaxWidth().height(220.dp)
                .background(Brush.verticalGradient(colors = listOf(NavyDark, NavyBackground)))
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.padding(top = 44.dp, bottom = 16.dp, start = 20.dp, end = 20.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Laporan & Riwayat", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            }

            // FILTER CHIPS (Teks Black untuk Low Vision)
            Surface(
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(26.dp),
                color = Color.White.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.4f))
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    filters.forEach { filter ->
                        val isSelected = selectedFilter == filter
                        val chipBg by animateColorAsState(if (isSelected) Color.White else Color.Transparent)
                        val chipText by animateColorAsState(if (isSelected) NavyDark else Color.White)

                        Box(
                            modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(22.dp))
                                .background(chipBg).clickable { selectedFilter = filter },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(filter, color = chipText, fontWeight = FontWeight.Black, fontSize = 14.sp)
                        }
                    }
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    // CARD LAPORAN DENGAN KONTRAST TINGGI
                    Card(
                        modifier = Modifier.fillMaxWidth().shadow(12.dp, RoundedCornerShape(28.dp)),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total Pengeluaran", fontSize = 14.sp, color = AccessibleGray, fontWeight = FontWeight.Bold)
                            Text(formatRupiah(totalPengeluaran), color = HighContrastRed, fontWeight = FontWeight.Black, fontSize = 30.sp)

                            Spacer(modifier = Modifier.height(32.dp))

                            // PIE CHART DENGAN STROKE LEBIH TEBAL (LOW VISION FRIENDLY)
                            Box(contentAlignment = Alignment.Center) {
                                SimplePieChart(data = chartData, colors = chartColors, radius = 80.dp, stroke = 50f)
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Status", fontSize = 11.sp, color = AccessibleGray)
                                    Text("Aman", fontWeight = FontWeight.Black, fontSize = 18.sp, color = HighContrastGreen)
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            // LEGENDA DENGAN TEKS LEBIH TEGAS
                            if (chartData.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                    chartData.keys.forEachIndexed { index, category ->
                                        val amount = chartData[category] ?: 0.0
                                        val percentage = if (totalPengeluaran > 0) (amount / totalPengeluaran * 100).toInt() else 0

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier.size(12.dp).background(chartColors.getOrElse(index % chartColors.size) { Color.Gray }, CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(category, modifier = Modifier.weight(1f), fontSize = 14.sp, fontWeight = FontWeight.Black, color = NavyDark)
                                            Text("$percentage%", fontSize = 13.sp, color = AccessibleGray, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 12.dp))
                                            Text(formatRupiah(amount), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Text("Riwayat Transaksi", fontWeight = FontWeight.Black, fontSize = 20.sp, color = NavyDark, modifier = Modifier.padding(top = 8.dp))
                }

                // RIWAYAT TRANSAKSI DENGAN DOUBLE-CODING (IKON PANAH)
                items(filteredList) { item ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF1F4F7))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            // Ikon Kategori (Teks Kapital Besar)
                            Box(modifier = Modifier.size(48.dp).background(if (item.type == "IN") Color(0xFFE8F5E9) else Color(0xFFFFEBEE), CircleShape), contentAlignment = Alignment.Center) {
                                Text(item.category.take(1).uppercase(), fontWeight = FontWeight.Black, color = if (item.type == "IN") HighContrastGreen else HighContrastRed, fontSize = 20.sp)
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.category, fontWeight = FontWeight.Black, fontSize = 16.sp, color = NavyDark)
                                Text(formatDate(item.date), fontSize = 12.sp, color = AccessibleGray, fontWeight = FontWeight.Medium)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                // IMPLEMENTASI DOUBLE CODING: Ikon Panah + Warna
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (item.type == "IN") Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                        contentDescription = null,
                                        tint = if (item.type == "IN") HighContrastGreen else HighContrastRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = formatRupiah(item.amount),
                                        color = if (item.type == "IN") HighContrastGreen else HighContrastRed,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Icon(
                                    Icons.Default.Delete,
                                    "Hapus",
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(20.dp).clickable { itemToDelete = item; showDeleteDialog = true }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimplePieChart(data: Map<String, Double>, colors: List<Color>, radius: Dp, stroke: Float) {
    val total = data.values.sum()
    Canvas(modifier = Modifier.size(radius * 2)) {
        if (total == 0.0) {
            drawCircle(color = Color(0xFFE0E0E0), style = Stroke(width = stroke))
        } else {
            var startAngle = -90f
            data.entries.forEachIndexed { index, entry ->
                val sweepAngle = (entry.value / total * 360).toFloat()
                drawArc(
                    color = colors.getOrElse(index % colors.size) { Color.Gray },
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = stroke, cap = StrokeCap.Round) // Stroke tebal untuk Low Vision
                )
                startAngle += sweepAngle
            }
        }
    }
}