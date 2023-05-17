package com.tosspayments.paymentsdk.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.widget.FrameLayout
import com.tosspayments.paymentsdk.R
import com.tosspayments.paymentsdk.extension.startSchemeIntent
import com.tosspayments.paymentsdk.interfaces.PaymentWidgetJavascriptInterface

sealed class PaymentWidgetContainer(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {
    private val paymentWebView: PaymentWebView

    protected val redirectOption: (redirectUrl: String?) -> String? = {
        it?.let { redirectUrl -> "{'brandpay':{'redirectUrl':'$redirectUrl'}}" }
    }

    protected var methodRenderCalled = false

    companion object {
        private const val INTERFACE_NAME_WIDGET = "PaymentWidgetAndroidSDK"

        internal const val EVENT_NAME = "name"
        internal const val EVENT_PARAMS = "params"

        internal const val EVENT_NAME_UPDATE_HEIGHT = "updateHeight"

        internal const val EVENT_PARAM_PAYMENT_METHOD_KEY = "paymentMethodKey"
        internal const val EVENT_PARAM_HEIGHT = "height"
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_payment_widget, this, true).run {
            paymentWebView = findViewById<PaymentWebView>(R.id.webview_payment).apply {
                this@apply.layoutParams = this@apply.layoutParams.apply {
                    this.height = ViewGroup.LayoutParams.WRAP_CONTENT
                }

                isVerticalScrollBarEnabled = false
            }
        }
    }

    private fun handleOverrideUrl(requestedUri: Uri?): Boolean {
        return requestedUri?.let { uri ->
            val requestedUrl = uri.toString()

            return when {
                URLUtil.isNetworkUrl(requestedUrl) -> {
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    true
                }
                !URLUtil.isJavaScriptUrl(requestedUrl) -> {
                    if ("intent".equals(uri.scheme, true)) {
                        context.startSchemeIntent(requestedUrl)
                    } else {
                        try {
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                            true
                        } catch (e: java.lang.Exception) {
                            false
                        }
                    }
                }
                else -> false
            }
        } ?: false
    }

    protected fun renderWidget(
        clientKey: String,
        customerKey: String,
        domain: String? = null,
        redirectUrl: String? = null,
        appendWidgetRenderScript: StringBuilder.() -> StringBuilder
    ) {
        val paymentWidgetConstructor =
            "PaymentWidget('$clientKey', '$customerKey', {'brandpay':{'redirectUrl':'${redirectUrl.orEmpty()}'}})"

        val renderMethodScript = StringBuilder()
            .appendLine("var paymentWidget = $paymentWidgetConstructor;")
            .appendWidgetRenderScript()
            .toString()

        paymentWebView.loadHtml(
            domain,
            "tosspayment_widget.html",
            {
                evaluateJavascript(renderMethodScript)
                methodRenderCalled = true
            },
            {
                handleOverrideUrl(this)
            }
        )
    }

    fun evaluateJavascript(script: String, resultCallback: ValueCallback<String>? = null) {
        paymentWebView.evaluateJavascript("javascript:$script", resultCallback)
    }

    fun addJavascriptInterface(javascriptInterface: PaymentWidgetJavascriptInterface) {
        paymentWebView.addJavascriptInterface(
            javascriptInterface,
            INTERFACE_NAME_WIDGET
        )
    }

    internal fun updateHeight(heightPx: Float?) {
        paymentWebView.setHeight(heightPx)
    }
}