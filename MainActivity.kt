package com.pijatin.mitra
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
val Green = Color(0xFF2D4A3E)
val Orange = Color(0xFFFF7A00)
data class OrderMitra(
val id: String,
val customer_id: String,
val layanan: String,
val total: Int,
val alamat: String,
val status: String
)
class MainActivity : ComponentActivity() {
override fun onCreate(b: Bundle?) {
super.onCreate(b)
setContent {
val ctx = LocalContext.current
val prefs = ctx.getSharedPreferences("PijatIN_Mitra", 0)
val scope = rememberCoroutineScope()
var isLoggedIn by remember { mutableStateOf(prefs.getBoolean("isLoggedIn", false)) }
var therapistId by remember { mutableStateOf(prefs.getString("therapist_id", "SR") ?: "SR") }
var therapistName by remember { mutableStateOf(prefs.getString("therapist_name", "Siti Rahayu") ?: "Siti Rahayu") }
var saldoMitra by remember { mutableStateOf(prefs.getInt("saldo_mitra", 0)) }
var online by remember { mutableStateOf(prefs.getBoolean("online", true)) }
var currentOrder by remember { mutableStateOf<OrderMitra?>(null) }
var orderStatus by remember { mutableStateOf("idle") } // idle, menunggu_mitra, dalam_perjalanan, sudah_sampai, berlangsung
var progress by remember { mutableStateOf(0f) }
// SIMULASI REALTIME: listen orders where therapist_id = SR and status menunggu_mitra
LaunchedEffect(online) {
if (online) {
// TODO REAL: supabase.channel("orders:therapist_id=eq.$therapistId").on("postgres_changes", event=INSERT, filter status=menunggu_mitra) { order ->
// currentOrder = order; orderStatus = "menunggu_mitra"
Toast.makeText(ctx, "Realtime ON: Menunggu order untuk $therapistId...", Toast.LENGTH_SHORT).show()
// Simulasi order masuk setelah 5 detik jika online
delay(5000)
if (currentOrder == null) {
currentOrder = OrderMitra("ORD-${(10000..99999).random()}", "CUST-083893330346", "Tradisional 60' - Rp120k", 140000, "Jl. Ciledug Raya No.10, Tangerang", "menunggu_mitra")
orderStatus = "menunggu_mitra"
}
}
}
LaunchedEffect(orderStatus) {
if (orderStatus == "dalam_perjalanan") {
while (progress < 100f) {
delay(5000)
progress += 20f
// TODO REAL: supabase.from("orders").update(mapOf("mitra_lat" to -6.2, "mitra_lng" to 106.8, "progress" to progress)).eq("id", currentOrder?.id)
}
orderStatus = "sudah_sampai"
}
}
if (!isLoggedIn) {
var idInput by remember { mutableStateOf("SR") }
var nameInput by remember { mutableStateOf("Siti Rahayu") }
Box(Modifier.fillMaxSize().background(Color.White).padding(16.dp), contentAlignment = Alignment.Center) {
Column {
Text("Login Mitra PijatIN", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Green)
Spacer(Modifier.height(16.dp))
OutlinedTextField(value = idInput, onValueChange = { idInput = it }, label = { Text("ID Therapist (SR/BS)") }, modifier = Modifier.fillMaxWidth())
Spacer(Modifier.height(8.dp))
OutlinedTextField(value = nameInput, onValueChange = { nameInput = it }, label = { Text("Nama") }, modifier = Modifier.fillMaxWidth())
Spacer(Modifier.height(16.dp))
Button(onClick = {
prefs.edit().putBoolean("isLoggedIn", true).putString("therapist_id", idInput).putString("therapist_name", nameInput).apply()
therapistId = idInput; therapistName = nameInput; isLoggedIn = true
}, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(Green)) { Text("Login Mitra", color = Color.White) }
}
}
} else {
Box(Modifier.fillMaxSize().background(Color(0xFFF6F3EE)).padding(16.dp)) {
Column {
Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
Text("Mitra: $therapistName ($therapistId)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Green)
Button(onClick = { online = !online; prefs.edit().putBoolean("online", online).apply() }, colors = ButtonDefaults.buttonColors(if (online) Color(0xFF4CAF50) else Color.Gray)) { Text(if (online) "ONLINE" else "OFFLINE", fontSize = 11.sp, color = Color.White) }
}
Spacer(Modifier.height(8.dp))
Box(Modifier.fillMaxWidth().background(Green, RoundedCornerShape(12.dp)).padding(12.dp)) {
Column {
Text("Saldo Mitra", fontSize = 11.sp, color = Color.White)
Text("Rp $saldoMitra", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
Text("Fee 20% - Customer bayar total, kamu dapat 80%", fontSize = 9.sp, color = Color(0xFFB0B0B0))
}
}
Spacer(Modifier.height(16.dp))
when (orderStatus) {
"idle" -> {
Box(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(12.dp)).padding(16.dp), contentAlignment = Alignment.Center) {
Column(horizontalAlignment = Alignment.CenterHorizontally) {
Text(if (online) "Menunggu Order REAL..." else "Kamu OFFLINE", fontWeight = FontWeight.Bold)
Spacer(Modifier.height(8.dp))
Text("Realtime channel: orders where therapist_id=$therapistId", fontSize = 10.sp, color = Color.Gray)
Text("Supabase realtime ON", fontSize = 10.sp, color = Color.Gray)
Spacer(Modifier.height(12.dp))
if (currentOrder == null && online) {
Button(onClick = {
currentOrder = OrderMitra("ORD-${(10000..99999).random()}", "CUST-083893330346", "Tradisional 60' - Rp120k", 140000, "Jl. Ciledug Raya No.10", "menunggu_mitra")
orderStatus = "menunggu_mitra"
}, colors = ButtonDefaults.buttonColors(Orange)) { Text("Simulasi Order Masuk", color = Color.White) }
}
}
}
}
"menunggu_mitra" -> {
Text("Order Baru Masuk! 🔔", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Orange)
Spacer(Modifier.height(12.dp))
Box(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(12.dp)).padding(12.dp)) {
Column {
Text(currentOrder?.id ?: "", fontWeight = FontWeight.Bold, fontSize = 14.sp)
Text("Customer: ${currentOrder?.customer_id}", fontSize = 12.sp)
Text("Layanan: ${currentOrder?.layanan}", fontSize = 12.sp)
Text("Total: Rp${currentOrder?.total} (Kamu dapat Rp${((currentOrder?.total ?: 0) * 0.8).toInt()})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Green)
Text("Alamat: ${currentOrder?.alamat}", fontSize = 11.sp, color = Color.Gray)
}
}
Spacer(Modifier.height(12.dp))
Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
Button(onClick = {
orderStatus = "dalam_perjalanan"
scope.launch {
// TODO REAL: supabase.from("orders").update(mapOf("status" to "dalam_perjalanan")).eq("id", currentOrder?.id)
Toast.makeText(ctx, "Order diterima! Dalam perjalanan...", Toast.LENGTH_SHORT).show()
}
}, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(Green)) { Text("Terima", color = Color.White) }
Button(onClick = {
currentOrder = null; orderStatus = "idle"
// TODO REAL: supabase.from("orders").update(mapOf("status" to "ditolak")).eq("id", currentOrder?.id)
}, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(Color.Red)) { Text("Tolak", color = Color.White) }
}
}
"dalam_perjalanan" -> {
Text("Dalam Perjalanan ke Customer", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Green)
Spacer(Modifier.height(12.dp))
Box(Modifier.fillMaxWidth().height(250.dp).background(Color(0xFFE0E0E0), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
Column(horizontalAlignment = Alignment.CenterHorizontally) {
Text("MAPS 250px 2 pin + motor 🛵", fontWeight = FontWeight.Bold)
Text("${progress.toInt()}% • Update lokasi 5 detik ke Supabase", fontSize = 12.sp)
LinearProgressIndicator(progress = { progress / 100f }, modifier = Modifier.fillMaxWidth().padding(16.dp), color = Orange)
}
}
Spacer(Modifier.height(12.dp))
Text("Order ${currentOrder?.id} - Update lat/lng every 5s", fontSize = 11.sp, color = Color.Gray)
}
"sudah_sampai" -> {
Text("Sudah Sampai di Lokasi!", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Green)
Spacer(Modifier.height(12.dp))
Text("Customer: ${currentOrder?.customer_id} - ${currentOrder?.alamat}", fontSize = 12.sp)
Spacer(Modifier.height(16.dp))
Button(onClick = {
orderStatus = "berlangsung"
// TODO REAL: supabase.from("orders").update(mapOf("status" to "sudah_sampai")).eq("id", currentOrder?.id)
Toast.makeText(ctx, "Mulai pijat! Timer jalan...", Toast.LENGTH_SHORT).show()
}, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(Orange)) { Text("Mulai Pijat Sekarang", color = Color.White, fontWeight = FontWeight.Bold) }
}
"berlangsung" -> {
Text("Pijat Berlangsung - Timer", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Green)
Spacer(Modifier.height(12.dp))
Box(Modifier.fillMaxWidth().height(150.dp).background(Green, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
Text("TIMER 60:00 FULLSCREEN #2D4A3E", color = Color.White, fontWeight = FontWeight.Bold)
}
Spacer(Modifier.height(12.dp))
Button(onClick = {
// Selesai → tambah saldo mitra
val earning = ((currentOrder?.total ?: 0) * 0.8).toInt()
saldoMitra += earning
prefs.edit().putInt("saldo_mitra", saldoMitra).apply()
Toast.makeText(ctx, "Selesai! Saldo +Rp$earning (total 80% dari Rp${currentOrder?.total})", Toast.LENGTH_LONG).show()
// TODO REAL: supabase.from("therapists").update(mapOf("saldo" to saldoMitra)).eq("id", therapistId)
// TODO REAL: supabase.from("orders").update(mapOf("status" to "selesai")).eq("id", currentOrder?.id)
currentOrder = null
orderStatus = "idle"
progress = 0f
}, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(Green)) { Text("Selesaikan & Terima Saldo", color = Color.White) }
}
}
Spacer(Modifier.height(20.dp))
Button(onClick = { prefs.edit().putBoolean("isLoggedIn", false).apply(); isLoggedIn = false }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(Color.Red)) { Text("Logout Mitra", color = Color.White) }
}
}
}
}
}
}
