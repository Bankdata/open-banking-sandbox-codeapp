package dk.bankdata.openbanking.codeapp

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class HttpClient {

    fun getHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        }
                    )
                }
            }
            .build()
    }

    companion object {
        private var instance: HttpClient? = null

        fun getInstance(): HttpClient {
            if (instance == null) {
                instance = HttpClient()
            }
            return instance!!
        }
    }
}