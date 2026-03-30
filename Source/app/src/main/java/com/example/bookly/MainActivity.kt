package com.example.bookly

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.bookly.ui.theme.BooklyTheme
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import com.example.bookly.models.*

suspend fun clearUsers() {
    try {
        supabase.from("users").delete {
            filter {
                gt("id", 0)
            }
        }
        Log.d("SUPABASE_TEST", "--- Таблиця 'users' очищена ---")
    } catch (e: Exception) {
        Log.e("SUPABASE_TEST", "Помилка очищення: ${e.localizedMessage}")
    }
}

suspend fun seedUsers() {
    try {
        val dummyUsers = (1..10).map { i ->
            User(
                name = "Користувач $i",
                email = "user$i@example.com",
                password = "password$i",
                city = "Місто $i",
                role = UserRole.user,
                about = "Я люблю книги і програмування, я юзер №$i"
            )
        }

        supabase.from("users").insert(dummyUsers)
        Log.d("SUPABASE_TEST", "--- 10 користувачів успішно додано ---")
    } catch (e: Exception) {
        Log.e("SUPABASE_TEST", "Помилка заповнення: ${e.localizedMessage}")
    }
}

suspend fun testConnection() {
    try {
        val users = supabase.from("users").select().decodeList<User>()

        if (users.isNotEmpty()) {
            Log.d("SUPABASE_TEST", "Зв'язок ОК! У базі знайдено юзерів: ${users.size}")
            users.take(3).forEach { user ->
                Log.d("SUPABASE_TEST", "Приклад: ${user.name} - ${user.email}")
            }
        } else {
            Log.d("SUPABASE_TEST", "Зв'язок є, але база чомусь порожня.")
        }
    } catch (e: Exception) {
        Log.e("SUPABASE_TEST", "Помилка запиту: ${e.localizedMessage}")
        e.printStackTrace()
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            clearUsers()
            seedUsers()
            testConnection()
        }

        setContent {
            BooklyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "User",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello, $name! \n" + "The database is being updated...",
        modifier = modifier
    )
}