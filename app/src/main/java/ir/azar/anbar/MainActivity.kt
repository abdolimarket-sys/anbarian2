package ir.azar.anbar

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.webkit.*
import android.webkit.JavascriptInterface
import android.widget.Toast
import java.nio.charset.StandardCharsets
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.webkit.WebViewAssetLoader

class MainActivity : ComponentActivity() {
    private lateinit var webView: WebView
    private var fileCallback: ValueCallback<Array<Uri>>? = null
    private var pendingWebPermission: PermissionRequest? = null
    private var pendingExportBytes: ByteArray? = null
    private var pendingExportMimeType: String = "application/octet-stream"

    private val createExportFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val uri = if (result.resultCode == Activity.RESULT_OK) result.data?.data else null
        val bytes = pendingExportBytes
        pendingExportBytes = null
        if (uri == null || bytes == null) {
            Toast.makeText(this, "ذخیره فایل لغو شد", Toast.LENGTH_SHORT).show()
            return@registerForActivityResult
        }
        try {
            contentResolver.openOutputStream(uri, "w")?.use { it.write(bytes) }
                ?: throw IllegalStateException("Output stream unavailable")
            Toast.makeText(this, "فایل با موفقیت ذخیره شد", Toast.LENGTH_LONG).show()
            webView.evaluateJavascript("window.onNativeFileSaved && window.onNativeFileSaved(true)", null)
        } catch (e: Exception) {
            Toast.makeText(this, "ذخیره فایل ناموفق بود", Toast.LENGTH_LONG).show()
            webView.evaluateJavascript("window.onNativeFileSaved && window.onNativeFileSaved(false)", null)
        }
    }

    private inner class AndroidFileBridge {
        @JavascriptInterface
        fun saveTextFile(filename: String, mimeType: String, content: String) {
            val safeMime = mimeType.substringBefore(";").trim().takeIf { it.contains("/") } ?: "text/plain"
            saveBytes(filename, safeMime, content.toByteArray(StandardCharsets.UTF_8))
        }

        @JavascriptInterface
        fun saveBase64File(filename: String, base64Content: String) {
            try {
                val bytes = android.util.Base64.decode(base64Content, android.util.Base64.DEFAULT)
                val mime = when {
                    filename.endsWith(".xlsx", true) -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                    filename.endsWith(".csv", true) -> "text/csv"
                    filename.endsWith(".json", true) -> "application/json"
                    else -> "application/octet-stream"
                }
                saveBytes(filename, mime, bytes)
            } catch (_: IllegalArgumentException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "ساخت فایل اکسل ناموفق بود", Toast.LENGTH_LONG).show()
                    webView.evaluateJavascript("window.onNativeFileSaved && window.onNativeFileSaved(false)", null)
                }
            }
        }

        private fun saveBytes(filename: String, mimeType: String, bytes: ByteArray) {
            runOnUiThread {
                if (pendingExportBytes != null) {
                    Toast.makeText(this@MainActivity, "لطفاً ابتدا ذخیره فایل قبلی را تمام کنید", Toast.LENGTH_SHORT).show()
                    return@runOnUiThread
                }
                pendingExportBytes = bytes
                pendingExportMimeType = mimeType
                val safeName = filename.substringAfterLast('/').substringAfterLast('\\').ifBlank { "azar-anbar-export.txt" }
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = pendingExportMimeType
                    putExtra(Intent.EXTRA_TITLE, safeName)
                }
                createExportFile.launch(intent)
            }
        }
    }

    private val cameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        val request = pendingWebPermission
        pendingWebPermission = null
        if (granted && request != null) {
            val allowed = request.resources.filter { it == PermissionRequest.RESOURCE_VIDEO_CAPTURE }.toTypedArray()
            if (allowed.isNotEmpty()) request.grant(allowed) else request.deny()
        } else request?.deny()
    }

    private val pickFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val callback = fileCallback
        fileCallback = null
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val uris = when {
                data?.clipData != null -> Array(data.clipData!!.itemCount) { i -> data.clipData!!.getItemAt(i).uri }
                data?.data != null -> arrayOf(data.data!!)
                else -> null
            }
            callback?.onReceiveValue(uris)
        } else callback?.onReceiveValue(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WebView.setWebContentsDebuggingEnabled(false)
        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()
        webView = WebView(this)
        webView.addJavascriptInterface(AndroidFileBridge(), "AzarAndroid")
        setContentView(webView)
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            allowFileAccess = false
            allowContentAccess = true
            javaScriptCanOpenWindowsAutomatically = false
            setSupportMultipleWindows(false)
            mediaPlaybackRequiresUserGesture = true
        }
        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): android.webkit.WebResourceResponse? =
                assetLoader.shouldInterceptRequest(request.url)
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val uri = request.url
                return uri.host != "appassets.androidplatform.net"
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                runOnUiThread {
                    if (request.resources.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE)) {
                        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            request.grant(arrayOf(PermissionRequest.RESOURCE_VIDEO_CAPTURE))
                        } else {
                            pendingWebPermission = request
                            cameraPermission.launch(Manifest.permission.CAMERA)
                        }
                    } else request.deny()
                }
            }
            override fun onShowFileChooser(view: WebView, callback: ValueCallback<Array<Uri>>, params: FileChooserParams): Boolean {
                fileCallback?.onReceiveValue(null)
                fileCallback = callback
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/*"
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
                        "application/json", "text/csv", "text/plain",
                        "application/vnd.ms-excel",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                    ))
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                }
                pickFile.launch(intent)
                return true
            }
        }
        webView.setDownloadListener { url, _, contentDisposition, mimeType, _ ->
            try {
                val uri = Uri.parse(url)
                if (uri.scheme == "blob" || uri.scheme == "data") {
                    Toast.makeText(this, "برای خروجی فایل، از گزینه اشتراک‌گذاری/ذخیره داخل برنامه استفاده کنید؛ دانلود مستقیم این نوع پیوند در نسخه اولیه پشتیبانی نمی‌شود.", Toast.LENGTH_LONG).show()
                } else {
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }
            } catch (_: Exception) {
                Toast.makeText(this, "بازکردن فایل انجام نشد", Toast.LENGTH_SHORT).show()
            }
        }
        if (savedInstanceState == null) webView.loadUrl("https://appassets.androidplatform.net/assets/index.html")
        else webView.restoreState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }

    override fun onDestroy() {
        fileCallback?.onReceiveValue(null)
        fileCallback = null
        pendingWebPermission?.deny()
        pendingWebPermission = null
        if (::webView.isInitialized) webView.destroy()
        super.onDestroy()
    }
}
