package com.akobusinska.letsplay.utils

import android.content.Context
import android.content.ContextWrapper
import android.os.Environment
import com.akobusinska.letsplay.data.entities.MyGame
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter


class Backup {

    companion object {

        fun writeJSON(games: List<MyGame>, context: Context) {

            val mapper = jacksonObjectMapper()
            val json = mapper.writeValueAsString(games)

            val contextWrapper = ContextWrapper(context)
            val directory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val txtFile = File(directory, "myGamesBackup.json")
            val fos: FileOutputStream
            val osw: OutputStreamWriter

            try {
                fos = FileOutputStream(txtFile)
                osw = OutputStreamWriter(fos)
                osw.write(json)
                osw.flush()
                osw.close()
                fos.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}