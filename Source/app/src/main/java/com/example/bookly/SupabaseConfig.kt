package com.example.bookly

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

val supabase = createSupabaseClient(
    supabaseUrl = "https://nmpizxdmoyjszttpyaya.supabase.co",
    supabaseKey = "sb_publishable_LSmzpn7LYf3bQjc1O6hHsQ_-gZ6R7BM"
) {
    install(Postgrest)
}