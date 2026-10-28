package com.smartgenius.tapotv

import android.content.Context
import android.net.Uri
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class Camera(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val host: String,
    val port: Int = 554,
    val user: String,
    val password: String,
    val path: String = "stream1",
    val subPath: String = "stream2",
) {
    fun rtspUrl(stream: String = path): String =
        "rtsp://${Uri.encode(user)}:${Uri.encode(password)}@$host:$port/${stream.trimStart('/')}"
}

/** Guarda la lista de cámaras cifrada en el propio TV; nada sale de la red local. */
class CameraStore(context: Context) {
    private val prefs = EncryptedSharedPreferences.create(
        context,
        "cameras",
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    fun load(): List<Camera> {
        val raw = prefs.getString(KEY, null) ?: return emptyList()
        val arr = JSONArray(raw)
        return (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            Camera(
                id = o.getString("id"),
                name = o.getString("name"),
                host = o.getString("host"),
                port = o.getInt("port"),
                user = o.getString("user"),
                password = o.getString("password"),
                path = o.getString("path"),
                subPath = o.optString("subPath", "stream2"),
            )
        }
    }

    fun save(cameras: List<Camera>) {
        val arr = JSONArray()
        cameras.forEach {
            arr.put(
                JSONObject()
                    .put("id", it.id)
                    .put("name", it.name)
                    .put("host", it.host)
                    .put("port", it.port)
                    .put("user", it.user)
                    .put("password", it.password)
                    .put("path", it.path)
                    .put("subPath", it.subPath)
            )
        }
        prefs.edit().putString(KEY, arr.toString()).apply()
    }

    private companion object {
        const val KEY = "list"
    }
}
