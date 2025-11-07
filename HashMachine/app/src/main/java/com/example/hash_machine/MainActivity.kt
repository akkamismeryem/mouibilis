package com.example.hash_machine


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import java.security.MessageDigest
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                HashMachineApp()
            }
        }
    }
}

@Composable
fun HashMachineApp() {
    var inputText by remember { mutableStateOf("") }
    var selectedAlgo by remember { mutableStateOf("MD5") }
    var result by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val algorithms = listOf("MD5", "SHA-1", "SHA-256", "SHA-512")

    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope() // ✅ Burada coroutine scope oluşturduk

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Metin girin") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                maxLines = 5
            )

            Box {
                Button(onClick = { expanded = true }) {
                    Text("Algoritma: $selectedAlgo")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    algorithms.forEach { algo ->
                        DropdownMenuItem(
                            text = { Text(algo) },
                            onClick = {
                                selectedAlgo = algo
                                expanded = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    result = hashText(selectedAlgo, inputText)
                },
                enabled = inputText.isNotBlank()
            ) {
                Text("Hashle")
            }

            if (result.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Sonuç:\n$result")

                    Button(onClick = {
                        clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(result))

                        // ✅ Artık coroutine scope ile snackbar çalıştırıyoruz
                        scope.launch {
                            snackbarHostState.showSnackbar("Hash panoya kopyalandı ✅")
                        }
                    }) {
                        Text("Kopyala")
                    }
                }
            }
        }
    }
}


fun hashText(algorithm: String, input: String): String {
    val bytes = MessageDigest
        .getInstance(algorithm)
        .digest(input.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}
