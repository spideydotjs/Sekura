package com.sonusid.sekura.data.remote

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.google.api.client.http.ByteArrayContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.Collections

class GoogleDriveManager(private val context: Context) {

    fun getDriveService(account: GoogleSignInAccount): Drive {
        val credential = GoogleAccountCredential.usingOAuth2(
            context, Collections.singleton(DriveScopes.DRIVE_APPDATA)
        )
        credential.selectedAccount = account.account
        return Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory(),
            credential
        ).setApplicationName("Sekura").build()
    }

    fun getLastSignedInAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    suspend fun uploadBackup(account: GoogleSignInAccount, jsonData: String) = withContext(Dispatchers.IO) {
        val driveService = getDriveService(account)
        
        // Check if file exists
        val files = driveService.files().list()
            .setSpaces("appDataFolder")
            .setQ("name = 'sekura_backup.json'")
            .execute()

        val metadata = File()
            .setName("sekura_backup.json")
            .setParents(Collections.singletonList("appDataFolder"))

        val content = ByteArrayContent.fromString("application/json", jsonData)

        if (files.files.isEmpty()) {
            driveService.files().create(metadata, content).execute()
        } else {
            val fileId = files.files[0].id
            driveService.files().update(fileId, null, content).execute()
        }
    }

    suspend fun downloadBackup(account: GoogleSignInAccount): String? = withContext(Dispatchers.IO) {
        val driveService = getDriveService(account)
        
        val files = driveService.files().list()
            .setSpaces("appDataFolder")
            .setQ("name = 'sekura_backup.json'")
            .execute()

        if (files.files.isEmpty()) return@withContext null

        val fileId = files.files[0].id
        val outputStream = ByteArrayOutputStream()
        driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream)
        outputStream.toString()
    }
}
