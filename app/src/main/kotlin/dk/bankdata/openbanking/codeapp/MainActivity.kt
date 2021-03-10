package dk.bankdata.openbanking.codeapp

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dk.bankdata.openbanking.codeapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), MainContract.View {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = intent.data ?: return
        val authId = data.getQueryParameter("auth_id") ?: return
        val updateUri = data.getQueryParameter("update_uri") ?: return
        val returnUri = data.getQueryParameter("return_uri") ?: return

        val httpClient = HttpClient.getInstance().getHttpClient()
        val presenter = MainPresenter(
            this,
            httpClient,
            WhitelistUrlValidator(),
            authId,
            updateUri,
            returnUri
        )

        binding.approve.setOnClickListener { presenter.selectStatus(Status.OK) }
        binding.cancel.setOnClickListener { presenter.selectStatus(Status.CANCEL) }
        binding.error.setOnClickListener { presenter.selectStatus(Status.ERROR) }

        presenter.start()
    }

    override fun onResume() {
        super.onResume()
        binding.versionName.text = getString(R.string.app_version, BuildConfig.VERSION_NAME)
    }

    override fun showStatusSelection(authId: String) {
        binding.requestTitle.text = getString(R.string.approve_request_title)
        binding.authId.text = getString(R.string.approve_hint, authId)
        binding.statusSelection.visibility = View.VISIBLE
    }

    override fun showErrorDialog(title: String, message: String) {
        Handler(mainLooper).post {
            MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }

    override fun showWaitIndicator() {
        Handler(mainLooper).post {
            window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            binding.waitOverlay.visibility = View.VISIBLE
        }
    }

    override fun hideWaitIndicator() {
        Handler(mainLooper).post {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            binding.waitOverlay.visibility = View.GONE
        }
    }

    override fun navigateToActivity(packageName: String) {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        launchIntent?.let { startActivity(it) }
        finishAndRemoveTask()
    }

}