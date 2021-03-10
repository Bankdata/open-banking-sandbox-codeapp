package dk.bankdata.openbanking.codeapp

import okhttp3.HttpUrl

interface IWhitelistUrlValidator {
    fun isWhitelisted(url: HttpUrl): Boolean
}

class WhitelistUrlValidator : IWhitelistUrlValidator {
    private val whitelistedHosts = listOf(
        "sandbox-dev-auth.jyskebank.dk",
        "sandbox-dev-auth.sydbank.dk",
        "sandbox-dev-auth.landbobanken.dk",
        "sandbox-dev-auth-gw.almbrand.dk",
        "sandbox-dev-auth.djurslandsbank.dk",
        "sandbox-dev-auth.kreditbanken.dk",
        "sandbox-dev-auth.nordfynsbank.dk",
        "sandbox-dev-auth.skjernbank.dk",
        "sandbox-dev-auth.spks.dk",
        "sandbox-test-auth.jyskebank.dk",
        "sandbox-test-auth.sydbank.dk",
        "sandbox-test-auth.landbobanken.dk",
        "sandbox-test-auth-gw.almbrand.dk",
        "sandbox-test-auth.djurslandsbank.dk",
        "sandbox-test-auth.kreditbanken.dk",
        "sandbox-test-auth.nordfynsbank.dk",
        "sandbox-test-auth.skjernbank.dk",
        "sandbox-test-auth.spks.dk",
        "sandbox-auth.jyskebank.dk",
        "sandbox-auth.sydbank.dk",
        "sandbox-auth.landbobanken.dk",
        "sandbox-auth-gw.almbrand.dk",
        "sandbox-auth.djurslandsbank.dk",
        "sandbox-auth.kreditbanken.dk",
        "sandbox-auth.nordfynsbank.dk",
        "sandbox-auth.skjernbank.dk",
        "sandbox-auth.spks.dk"
    )

    override fun isWhitelisted(url: HttpUrl) =
        if (url.isHttps) whitelistedHosts.contains(url.host) else false
}