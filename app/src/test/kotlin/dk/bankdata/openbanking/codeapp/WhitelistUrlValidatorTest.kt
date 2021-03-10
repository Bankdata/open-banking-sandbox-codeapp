package dk.bankdata.openbanking.codeapp

import com.google.common.truth.Truth.assertThat
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.Test

internal class WhitelistUrlValidatorTest {

    @Test
    fun isWhitelisted_NonHttps_ReturnsFalse() {
        val validator = WhitelistUrlValidator()
        val result = validator.isWhitelisted("http://sandbox-auth.jyskebank.dk/".toHttpUrl())
        assertThat(result).isFalse()
    }

    @Test
    fun isWhitelisted_HttpsInvalidHost_ReturnsFalse() {
        val validator = WhitelistUrlValidator()
        val result = validator.isWhitelisted("https://jyskebank.dk/".toHttpUrl())
        assertThat(result).isFalse()
    }

    @Test
    fun isWhitelisted_HttpsValidHost_ReturnsTrue() {
        val validator = WhitelistUrlValidator()
        val result = validator.isWhitelisted("https://sandbox-auth.jyskebank.dk/".toHttpUrl())
        assertThat(result).isTrue()
    }

}