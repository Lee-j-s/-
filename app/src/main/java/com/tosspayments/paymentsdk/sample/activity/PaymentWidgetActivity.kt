package com.tosspayments.paymentsdk.sample.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.tosspayments.paymentsdk.PaymentWidget
import com.tosspayments.paymentsdk.model.*
import com.tosspayments.paymentsdk.sample.R
import com.tosspayments.paymentsdk.view.Agreement
import com.tosspayments.paymentsdk.view.PaymentMethod

class PaymentWidgetActivity : AppCompatActivity() {
    private lateinit var methodWidget: PaymentMethod
    private lateinit var agreementWidget: Agreement
    private lateinit var paymentCta: Button

    companion object {
        private const val TAG = "PaymentWidgetActivity"

        private const val EXTRA_KEY_AMOUNT = "extraKeyAmount"
        private const val EXTRA_KEY_CLIENT_KEY = "extraKeyClientKey"
        private const val EXTRA_KEY_CUSTOMER_KEY = "extraKeyCustomerKey"
        private const val EXTRA_KEY_ORDER_ID = "extraKeyOrderId"
        private const val EXTRA_KEY_ORDER_NAME = "extraKeyOrderName"
        private const val EXTRA_KEY_REDIRECT_URL = "extraKeyRedirectUrl"

        fun getIntent(
            context: Context,
            amount: Long,
            clientKey: String,
            customerKey: String,
            orderId: String,
            orderName: String,
            redirectUrl: String? = null
        ): Intent {
            return Intent(context, PaymentWidgetActivity::class.java)
                .putExtra(EXTRA_KEY_AMOUNT, amount)
                .putExtra(EXTRA_KEY_CLIENT_KEY, clientKey)
                .putExtra(EXTRA_KEY_CUSTOMER_KEY, customerKey)
                .putExtra(EXTRA_KEY_ORDER_ID, orderId)
                .putExtra(EXTRA_KEY_ORDER_NAME, orderName)
                .putExtra(EXTRA_KEY_REDIRECT_URL, redirectUrl)
        }
    }

    private val tossPaymentActivityResult: ActivityResultLauncher<Intent> =
        PaymentWidget.getPaymentResultLauncher(
            this,
            { success ->
                handlePaymentSuccessResult(success)
            },
            { fail ->
                handlePaymentFailResult(fail)
            })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_widget)

        initViews()

        intent?.run {
            initPaymentWidget(
                getLongExtra(EXTRA_KEY_AMOUNT, 0),
                getStringExtra(EXTRA_KEY_CLIENT_KEY).orEmpty(),
                getStringExtra(EXTRA_KEY_CUSTOMER_KEY).orEmpty(),
                getStringExtra(EXTRA_KEY_ORDER_ID).orEmpty(),
                getStringExtra(EXTRA_KEY_ORDER_NAME).orEmpty(),
                getStringExtra(EXTRA_KEY_REDIRECT_URL),
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        methodWidget = findViewById(R.id.payment_widget)
        agreementWidget = findViewById(R.id.agreement_widget)
        paymentCta = findViewById(R.id.request_payment_cta)
    }

    private fun initPaymentWidget(
        amount: Long,
        clientKey: String,
        customerKey: String,
        orderId: String,
        orderName: String,
        redirectUrl: String?
    ) {
        val paymentWidget = PaymentWidget(
            activity = this@PaymentWidgetActivity,
            clientKey = clientKey,
            customerKey = customerKey,
            redirectUrl?.let {
                PaymentWidgetOptions.Builder()
                    .brandPayOption(redirectUrl = it)
                    .build()
            }
        )

        paymentCta.setOnClickListener {
            paymentWidget.requestPayment(
                paymentResultLauncher = tossPaymentActivityResult,
                orderId = orderId,
                orderName = orderName
            )
        }

        paymentWidget.run {
            renderPaymentMethods(methodWidget, amount)
            renderAgreement(agreementWidget)

            addMethodWidgetEventListener(object : PaymentMethodCallback() {
                override fun onCustomRequested(paymentMethodKey: String) {
                    Log.d(TAG, "onCustomRequested : $paymentMethodKey")
                }

                override fun onCustomPaymentMethodSelected(paymentMethodKey: String) {
                    Log.d(TAG, "onCustomPaymentMethodSelected : $paymentMethodKey")
                }

                override fun onCustomPaymentMethodUnselected(paymentMethodKey: String) {
                    Log.d(TAG, "onCustomPaymentMethodUnselected : $paymentMethodKey")
                }
            })

            onAgreementStatusChanged(object : AgreementCallback {
                override fun onAgreementStatusChanged(agreementStatus: AgreementStatus) {
                    Log.d(TAG, "onAgreementStatusChanged : ${agreementStatus.agreedRequiredTerms}")

                    runOnUiThread {
                        paymentCta.isEnabled = agreementStatus.agreedRequiredTerms
                    }
                }
            })
        }
    }

    private fun handlePaymentSuccessResult(success: TossPaymentResult.Success) {
        val paymentType: String? = success.additionalParameters["paymentType"]
        if ("BRANDPAY".equals(paymentType, true)) {
            // 브랜드페이 승인
        } else {
            // 일반결제 승인 -> 추후 일반결제/브랜드페이 승인으로 Migration 예정되어있음
        }

        startActivity(
            PaymentResultActivity.getIntent(
                this@PaymentWidgetActivity,
                true,
                arrayListOf(
                    "PaymentKey|${success.paymentKey}",
                    "OrderId|${success.orderId}",
                    "Amount|${success.amount}",
                    "AdditionalParams|${success.additionalParameters}"
                )
            )
        )
    }

    private fun handlePaymentFailResult(fail: TossPaymentResult.Fail) {
        startActivity(
            PaymentResultActivity.getIntent(
                this@PaymentWidgetActivity,
                false,
                arrayListOf(
                    "ErrorCode|${fail.errorCode}",
                    "ErrorMessage|${fail.errorMessage}",
                    "OrderId|${fail.orderId}"
                )
            )
        )
    }
}