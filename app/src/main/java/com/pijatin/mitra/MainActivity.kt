package com.pijatin.mitra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

data class Order(val id: String, val customerName: String, val service: String, val address: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MitraScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MitraScreen() {
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var ok by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(800)
        ok = true
        orders = listOf(
            Order("1", "Ibu Sari", "Pijat Full Body 90 Menit", "Gold Lotus Blok A No 12 Bekasi"),
            Order("2", "Kak Violet", "Pijat Refleksi + Totok Wajah", "Grand Galaxy City Bekasi")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "PijatIN Mitra - GOLD LOTUS",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFB8860B)
                )
            )
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFFF8E1))
                .padding(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (ok) Color(0xFF4CAF50) else Color(0xFFFF9800)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (ok) "● REALTIME ON - 2 Order Masuk!" else "● Menghubungkan...",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            if (orders.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFB8860B))
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(orders, key = { it.id }) { order ->
                        Card(
                            Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(order.customerName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text(order.service, color = Color(0xFFB8860B), fontWeight = FontWeight.SemiBold)
                                Text(order.address, fontSize = 12.sp, color = Color.Gray)
                                Spacer(Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { orders = orders.filter { it.id != order.id } },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB8860B))
                                    ) {
                                        Text("Terima")
                                    }
                                    OutlinedButton(
                                        onClick = { orders = orders.filter { it.id != order.id } }
                                    ) {
                                        Text("Tolak")
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
