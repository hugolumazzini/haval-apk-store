package br.com.hugolumazzini.havalapkstore.system

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

class TelnetClient {
    private var socket: Socket? = null
    private var writer: OutputStreamWriter? = null
    private var reader: BufferedReader? = null

    fun connect(host: String, port: Int, timeoutMs: Int = 1000) {
        socket = Socket(host, port).apply {
            soTimeout = timeoutMs
        }
        writer = OutputStreamWriter(socket!!.getOutputStream())
        reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))
    }

    fun executeCommand(command: String, timeoutMs: Int = 5000): String {
        if (socket == null) throw IllegalStateException("Not connected")

        writer?.write("$command\n")
        writer?.flush()

        val output = StringBuilder()
        val deadline = System.currentTimeMillis() + timeoutMs

        try {
            while (System.currentTimeMillis() < deadline) {
                if (reader!!.ready()) {
                    val line = reader!!.readLine() ?: break
                    output.append(line).append("\n")
                    if (line.contains(":/ #") || line.contains("$")) break
                } else {
                    Thread.sleep(10)
                }
            }
        } catch (e: Exception) {
            // Timeout ok, retorna o que já temos
        }

        return output.toString().trim()
    }

    fun disconnect() {
        try {
            writer?.close()
            reader?.close()
            socket?.close()
        } catch (e: Exception) {
            // ignore
        }
        socket = null
        writer = null
        reader = null
    }
}
