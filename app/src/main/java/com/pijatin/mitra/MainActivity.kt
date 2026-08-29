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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

data class Order(
    val id: String = "",
    val customerName: String = "",
    val service: String = "",
    val address: String = "",
    val status: String = "new"
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MitraRealtimeScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MitraRealtimeScreen() {
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var isListening by remember { mutableStateOf(false) }

    // REALTIME LISTENER FIRESTORE
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("orders")
            .whereEqualTo("status", "waiting_mitra")
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        Order(
                            id = doc.id,
                            customerName = doc.getString("customerName") ?: "Customer",
                            service = doc.getString("service") ?: "Pijat Full Body",
                            address = doc.getString("address") ?: "-",
                            status = doc.getString("status") ?: "new"
                        )
                    }
                    orders = list
                    isListening = true
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PijatIN Mitra - GOLD LOTUS", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFB8860B))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFFF8E1))
                .padding(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = if(isListening) Color(0xFF4CAF50) else Color.Red),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        if(isListening) "● REALTIME ON - Menunggu Order..." else "● Menghubungkan...",
                        color = Color.White, fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            if (orders.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada order masuk\n\nOrder akan muncul OTOMATIS disini tanpa refresh!", fontSize = 16.sp)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(orders) { order ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(order.customerName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text(order.service, color = Color(0xFFB8860B))
                                Text(order.address, fontSize = 12.sp, color = Color.Gray)
                                Spacer(Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = {
                                        FirebaseFirestore.getInstance().collection("orders").document(order.id).update("status", "accepted")
                                    }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB8860B))) {
                                        Text("Terima Order")
                                    }
                                    OutlinedButton(onClick = {}) {
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
