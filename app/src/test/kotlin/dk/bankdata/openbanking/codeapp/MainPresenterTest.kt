package dk.bankdata.openbanking.codeapp

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner.StrictStubs::class)
internal class MainPresenterTest {

    @Mock
    private lateinit var mockView: MainContract.View

    @Mock
    private lateinit var mockOkHttpClient: OkHttpClient

    @Mock
    private lateinit var mockWhitelistUrlValidator: WhitelistUrlValidator

    @Test
    fun start_UpdateUriNotWellFormedHttp_ShowError() {
        val mainPresenter = MainPresenter(
            view = mockView,
            okHttpClient = mockOkHttpClient,
            whitelistUrlValidator = mockWhitelistUrlValidator,
            authId = "some-auth-id",
            updateUri = "not://well-formed-http",
            returnUri = "some-return-uri"
        )

        mainPresenter.start()

        verify(mockView).showErrorDialog(anyString(), anyString())
        verifyNoMoreInteractions(mockView)
    }

    @Test
    fun start_UpdateUriNotWhitelisted_ShowError() {
        whenever(mockWhitelistUrlValidator.isWhitelisted(any())).thenReturn(false)
        val mainPresenter = MainPresenter(
            view = mockView,
            okHttpClient = mockOkHttpClient,
            whitelistUrlValidator = mockWhitelistUrlValidator,
            authId = "some-auth-id",
            updateUri = "https://bankdata.dk",
            returnUri = "some-return-uri"
        )

        mainPresenter.start()

        verify(mockView).showErrorDialog(anyString(), anyString())
        verifyNoMoreInteractions(mockView)
    }

    @Test
    fun start_UpdateUriWhitelisted_ShowStatusSelection() {
        whenever(mockWhitelistUrlValidator.isWhitelisted(any())).thenReturn(true)
        val mainPresenter = MainPresenter(
            view = mockView,
            okHttpClient = mockOkHttpClient,
            whitelistUrlValidator = mockWhitelistUrlValidator,
            authId = "some-auth-id",
            updateUri = "https://bankdata.dk",
            returnUri = "some-return-uri"
        )

        mainPresenter.start()

        verify(mockView).showStatusSelection("some-auth-id")
        verifyNoMoreInteractions(mockView)
    }

    @Test
    fun selectStatus_ShouldConstructCorrectUpdateUrl() {
        val mockCall: Call = mock()
        whenever(mockOkHttpClient.newCall(any())).thenReturn(mockCall)

        val mainPresenter = MainPresenter(
            view = mockView,
            okHttpClient = mockOkHttpClient,
            whitelistUrlValidator = mockWhitelistUrlValidator,
            authId = "some-auth-id",
            updateUri = "https://bankdata.dk",
            returnUri = "some-return-uri"
        )

        mainPresenter.selectStatus(Status.OK)

        val argumentCaptor = argumentCaptor<Request>()
        verify(mockOkHttpClient).newCall(argumentCaptor.capture())

        val url = argumentCaptor.firstValue.url
        assertThat(url.scheme).isEqualTo("https")
        assertThat(url.host).isEqualTo("bankdata.dk")
        assertThat(url.queryParameter("auth_id")).isEqualTo("some-auth-id")
        assertThat(url.queryParameter("status")).isEqualTo("ok")
    }

    @Test
    fun selectStatus_ResponseWithSuccessfulHttpStatusCode_NavigateToReturnUri() {
        val mockCall: Call = mock()
        whenever(mockOkHttpClient.newCall(any())).thenReturn(mockCall)

        val mainPresenter = MainPresenter(
            view = mockView,
            okHttpClient = mockOkHttpClient,
            whitelistUrlValidator = mockWhitelistUrlValidator,
            authId = "some-auth-id",
            updateUri = "https://bankdata.dk",
            returnUri = "some-return-uri"
        )

        mainPresenter.selectStatus(Status.OK)

        val argumentCaptor = argumentCaptor<Callback>()
        verify(mockCall).enqueue(argumentCaptor.capture())

        val response: Response = mock {
            whenever(it.isSuccessful).thenReturn(true)
        }
        argumentCaptor.firstValue.onResponse(mock(), response)

        val order = inOrder(mockView)
        order.verify(mockView).showWaitIndicator()
        order.verify(mockView).hideWaitIndicator()
        order.verify(mockView).navigateToActivity("some-return-uri")
        order.verifyNoMoreInteractions()
    }

    @Test
    fun selectStatus_ResponseWithNotSuccessfulHttpStatusCode_ShowError() {
        val mockCall: Call = mock()
        whenever(mockOkHttpClient.newCall(any())).thenReturn(mockCall)

        val mainPresenter = MainPresenter(
            view = mockView,
            okHttpClient = mockOkHttpClient,
            whitelistUrlValidator = mockWhitelistUrlValidator,
            authId = "some-auth-id",
            updateUri = "https://bankdata.dk",
            returnUri = "some-return-uri"
        )

        mainPresenter.selectStatus(Status.OK)

        val argumentCaptor = argumentCaptor<Callback>()
        verify(mockCall).enqueue(argumentCaptor.capture())

        val response: Response = mock {
            whenever(it.isSuccessful).thenReturn(false)
        }
        argumentCaptor.firstValue.onResponse(mock(), response)

        val order = inOrder(mockView)
        order.verify(mockView).showWaitIndicator()
        order.verify(mockView).hideWaitIndicator()
        order.verify(mockView).showErrorDialog("Error", "Request failed")
        order.verifyNoMoreInteractions()
    }

    @Test
    fun selectStatus_NetworkError_ShowNetworkError() {
        val mockCall: Call = mock()
        whenever(mockOkHttpClient.newCall(any())).thenReturn(mockCall)

        val mainPresenter = MainPresenter(
            view = mockView,
            okHttpClient = mockOkHttpClient,
            whitelistUrlValidator = mockWhitelistUrlValidator,
            authId = "some-auth-id",
            updateUri = "https://bankdata.dk",
            returnUri = "some-return-uri"
        )

        mainPresenter.selectStatus(Status.OK)

        val argumentCaptor = argumentCaptor<Callback>()
        verify(mockCall).enqueue(argumentCaptor.capture())

        argumentCaptor.firstValue.onFailure(mock(), mock())

        val order = inOrder(mockView)
        order.verify(mockView).showWaitIndicator()
        order.verify(mockView).hideWaitIndicator()
        order.verify(mockView).showErrorDialog("Error", "Connectivity problem or timeout")
        order.verifyNoMoreInteractions()
    }

}