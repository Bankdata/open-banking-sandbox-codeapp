package dk.bankdata.openbanking.codeapp

import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class MainPresenter(
    private val view: MainContract.View,
    private val okHttpClient: OkHttpClient,
    private val whitelistUrlValidator: IWhitelistUrlValidator,
    private val authId: String,
    private val updateUri: String,
    private val returnUri: String
) : MainContract.Presenter {

    override fun start() {
        val updateUri = updateUri.toHttpUrlOrNull()
        if (updateUri == null || !whitelistUrlValidator.isWhitelisted(updateUri)) {
            view.showErrorDialog("Error", "The update_uri is not allowed: $updateUri")
            return
        }

        view.showStatusSelection(authId)
    }

    override fun selectStatus(status: Status) {
        view.showWaitIndicator()

        val url = updateUri.toHttpUrl().newBuilder()
            .addQueryParameter("auth_id", authId)
            .addQueryParameter("status", status.value)
            .build()

        val request = Request.Builder().url(url).build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                view.hideWaitIndicator()
                view.showErrorDialog("Error", "Connectivity problem or timeout")
            }

            override fun onResponse(call: Call, response: Response) {
                view.hideWaitIndicator()
                if (response.isSuccessful) {
                    view.navigateToActivity(returnUri)
                } else {
                    view.showErrorDialog("Error", "Request failed")
                }
            }
        })
    }

}