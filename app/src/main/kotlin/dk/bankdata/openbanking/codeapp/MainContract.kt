package dk.bankdata.openbanking.codeapp

interface MainContract {
    interface View {
        fun showStatusSelection(authId: String)
        fun showErrorDialog(title: String, message: String)
        fun showWaitIndicator()
        fun hideWaitIndicator()
        fun navigateToActivity(packageName: String)
    }
    interface Presenter {
        fun start()
        fun selectStatus(status: Status)
    }
}
