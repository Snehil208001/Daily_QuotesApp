package com.BrewApp.dailyquoteapp.data.auth

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "https://grgvxfbfkcitalbcwvkv.supabase.co",
        supabaseKey = "sb_publishable_1wAs3sgB13ifWYmsmFS-PQ_Qe_2WNuT"
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
    }
}