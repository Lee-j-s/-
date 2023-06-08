package com.tosspayments.paymentsdk.sample.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.tosspayments.paymentsdk.PaymentWidget
import com.tosspayments.paymentsdk.model.*
import com.tosspayments.paymentsdk.sample.R
import com.tosspayments.paymentsdk.sample.extension.toast
import com.tosspayments.paymentsdk.view.Agreement
import com.tosspayments.paymentsdk.view.PaymentMethod

class PaymentWidgetActivity : AppCompatActivity() {
    private lateinit var methodWidget: PaymentMethod
    private lateinit var agreementWidget: Agreement
    private lateinit var paymentCta: Button
    private lateinit var updateAmountCta: Button

    private val paymentEventListener
        get() = object : PaymentMethodEventListener() {
            override fun onCustomRequested(paymentMethodKey: String) {
                val message = "onCustomRequested : $paymentMethodKey"
                Log.d(TAG, message)

                toast(message)
            }

            override fun onCustomPaymentMethodSelected(paymentMethodKey: String) {
                val message = "onCustomPaymentMethodSelected : $paymentMethodKey"
                Log.d(TAG, message)

                toast(message)
            }

            override fun onCustomPaymentMethodUnselected(paymentMethodKey: String) {
                val message = "onCustomPaymentMethodUnselected : $paymentMethodKey"
                Log.d(TAG, message)

                toast(message)
            }
        }

    private val agreementStatusListener
        get() = object : AgreementStatusListener {
            override fun onAgreementStatusChanged(agreementStatus: AgreementStatus) {
                Log.d(TAG, "onAgreementStatusChanged : ${agreementStatus.agreedRequiredTerms}")

                runOnUiThread {
                    paymentCta.isEnabled = agreementStatus.agreedRequiredTerms
                }
            }
        }

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
        updateAmountCta = findViewById(R.id.change_amount_cta)
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

        paymentWidget.run {
            renderPaymentMethods(methodWidget, amount)
            renderAgreement(agreementWidget)

            addPaymentMethodEventListener(paymentEventListener)
            addAgreementStatusListener(agreementStatusListener)
        }

        paymentCta.setOnClickListener {
            paymentWidget.requestPayment(
                paymentInfo = PaymentMethod.PaymentInfo(orderId = orderId, orderName = orderName),
                paymentCallback = object : PaymentCallback {
                    override fun onPaymentSuccess(success: TossPaymentResult.Success) {
                        handlePaymentSuccessResult(success)
                    }

                    override fun onPaymentFailed(fail: TossPaymentResult.Fail) {
                        handlePaymentFailResult(fail)
                    }
                }
            )
        }

        updateAmountCta.setOnClickListener {
            showUpdateAmountDialog { inputAmount ->
                paymentWidget.updateAmount(inputAmount)
            }
        }
    }

    private fun handlePaymentSuccessResult(success: TossPaymentResult.Success) {
        val paymentType: String? = success.additionalParameters["paymentType"]
        if ("BRANDPAY".equals(paymentType, true)) {
            // TODO: 브랜드페이 승인
        } else {
            // TODO: 일반결제 승인 -> 추후 일반결제/브랜드페이 승인으로 Migration 예정되어있음
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

    private fun showUpdateAmountDialog(amountCallback: (Long) -> Unit) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_input_amount, null)
        val inputAmount = view.findViewById<EditText>(R.id.input_amount)

        AlertDialog.Builder(this)
            .setView(view)
            .setPositiveButton("확인") { dialog, _ ->
                try {
                    amountCallback.invoke(inputAmount.text.toString().toLong())
                } catch (ignore: Exception) {
                }

                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }
}