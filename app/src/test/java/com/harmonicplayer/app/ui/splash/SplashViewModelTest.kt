package com.harmonicplayer.app.ui.splash

import android.content.Context
import com.harmonicplayer.app.util.PermissionManager
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FakePermissionManager(
    private var granted: Boolean = false
) : PermissionManager(FakeContext()) {
    fun setGranted(isGranted: Boolean) {
        granted = isGranted
    }

    override fun isPermissionGranted(): Boolean = granted
    override fun getRequiredPermission(): String = "android.permission.READ_MEDIA_AUDIO"
}

class FakeContext : Context() {
    override fun getAssets(): android.content.res.AssetManager = throw UnsupportedOperationException()
    override fun getResources(): android.content.res.Resources = throw UnsupportedOperationException()
    override fun getPackageManager(): android.content.pm.PackageManager = throw UnsupportedOperationException()
    override fun getContentResolver(): android.content.ContentResolver = throw UnsupportedOperationException()
    override fun getMainLooper(): android.os.Looper = throw UnsupportedOperationException()
    override fun getApplicationContext(): Context = this
    override fun setTheme(resid: Int) {}
    override fun getTheme(): android.content.res.Resources.Theme = throw UnsupportedOperationException()
    override fun getClassLoader(): ClassLoader = throw UnsupportedOperationException()
    override fun getPackageName(): String = "com.harmonicplayer.app"
    override fun getApplicationInfo(): android.content.pm.ApplicationInfo = throw UnsupportedOperationException()
    override fun getPackageResourcePath(): String = ""
    override fun getPackageCodePath(): String = ""
    override fun getSharedPreferences(name: String?, mode: Int): android.content.SharedPreferences = throw UnsupportedOperationException()
    override fun openFileInput(name: String?): java.io.FileInputStream = throw UnsupportedOperationException()
    override fun openFileOutput(name: String?, mode: Int): java.io.FileOutputStream = throw UnsupportedOperationException()
    override fun deleteFile(name: String?): Boolean = false
    override fun getFileStreamPath(name: String?): java.io.File = throw UnsupportedOperationException()
    override fun getFilesDir(): java.io.File = throw UnsupportedOperationException()
    override fun getNoBackupFilesDir(): java.io.File = throw UnsupportedOperationException()
    override fun getExternalFilesDir(type: String?): java.io.File? = null
    override fun getExternalFilesDirs(type: String?): Array<java.io.File> = emptyArray()
    override fun getObbDir(): java.io.File = throw UnsupportedOperationException()
    override fun getObbDirs(): Array<java.io.File> = emptyArray()
    override fun getCacheDir(): java.io.File = throw UnsupportedOperationException()
    override fun getCodeCacheDir(): java.io.File = throw UnsupportedOperationException()
    override fun getExternalCacheDir(): java.io.File? = null
    override fun getExternalCacheDirs(): Array<java.io.File> = emptyArray()
    override fun getExternalMediaDirs(): Array<java.io.File> = emptyArray()
    override fun fileList(): Array<String> = emptyArray()
    override fun getDir(name: String?, mode: Int): java.io.File = throw UnsupportedOperationException()
    override fun openOrCreateDatabase(name: String?, mode: Int, factory: android.database.sqlite.SQLiteDatabase.CursorFactory?): android.database.sqlite.SQLiteDatabase = throw UnsupportedOperationException()
    override fun openOrCreateDatabase(name: String?, mode: Int, factory: android.database.sqlite.SQLiteDatabase.CursorFactory?, errorHandler: android.database.DatabaseErrorHandler?): android.database.sqlite.SQLiteDatabase = throw UnsupportedOperationException()
    override fun moveDatabaseFrom(name: Context?, name2: String?): Boolean = false
    override fun deleteDatabase(name: String?): Boolean = false
    override fun getDatabasePath(name: String?): java.io.File = throw UnsupportedOperationException()
    override fun databaseList(): Array<String> = emptyArray()
    override fun WallpaperManager(): Any = throw UnsupportedOperationException()
    override fun peekWallpaper(): android.graphics.drawable.Drawable = throw UnsupportedOperationException()
    override fun getWallpaperDesiredMinimumWidth(): Int = 0
    override fun getWallpaperDesiredMinimumHeight(): Int = 0
    override fun setWallpaper(bitmap: android.graphics.Bitmap?) {}
    override fun setWallpaper(snapshot: java.io.InputStream?) {}
    override fun clearWallpaper() {}
    override fun startActivity(intent: android.content.Intent?) {}
    override fun startActivity(intent: android.content.Intent?, options: android.os.Bundle?) {}
    override fun startActivities(intents: Array<out android.content.Intent>?) {}
    override fun startActivities(intents: Array<out android.content.Intent>?, options: android.os.Bundle?) {}
    override fun startIntentSender(intent: android.content.IntentSender?, fillInIntent: android.content.Intent?, flagsMask: Int, flagsValues: Int, extraFlags: Int) {}
    override fun startIntentSender(intent: android.content.IntentSender?, fillInIntent: android.content.Intent?, flagsMask: Int, flagsValues: Int, extraFlags: Int, options: android.os.Bundle?) {}
    override fun sendBroadcast(intent: android.content.Intent?) {}
    override fun sendBroadcast(intent: android.content.Intent?, receiverPermission: String?) {}
    override fun sendOrderedBroadcast(intent: android.content.Intent?, receiverPermission: String?) {}
    override fun sendOrderedBroadcast(intent: android.content.Intent?, receiverPermission: String?, resultReceiver: android.content.BroadcastReceiver?, scheduler: android.os.Handler?, initialCode: Int, initialData: String?, initialExtras: android.os.Bundle?) {}
    override fun sendBroadcastAsUser(intent: android.content.Intent?, user: android.os.UserHandle?) {}
    override fun sendBroadcastAsUser(intent: android.content.Intent?, user: android.os.UserHandle?, receiverPermission: String?) {}
    override fun sendOrderedBroadcastAsUser(intent: android.content.Intent?, user: android.os.UserHandle?, receiverPermission: String?, resultReceiver: android.content.BroadcastReceiver?, scheduler: android.os.Handler?, initialCode: Int, initialData: String?, initialExtras: android.os.Bundle?) {}
    override fun registerReceiver(receiver: android.content.BroadcastReceiver?, filter: android.content.IntentFilter?): android.content.Intent? = null
    override fun registerReceiver(receiver: android.content.BroadcastReceiver?, filter: android.content.IntentFilter?, flags: Int): android.content.Intent? = null
    override fun registerReceiver(receiver: android.content.BroadcastReceiver?, filter: android.content.IntentFilter?, broadcastPermission: String?, scheduler: android.os.Handler?): android.content.Intent? = null
    override fun registerReceiver(receiver: android.content.BroadcastReceiver?, filter: android.content.IntentFilter?, broadcastPermission: String?, scheduler: android.os.Handler?, flags: Int): android.content.Intent? = null
    override fun unregisterReceiver(receiver: android.content.BroadcastReceiver?) {}
    override fun startService(service: android.content.Intent?): android.content.ComponentName? = null
    override fun startForegroundService(service: android.content.Intent?): android.content.ComponentName? = null
    override fun stopService(service: android.content.Intent?): Boolean = false
    override fun bindService(service: android.content.Intent?, conn: android.content.ServiceConnection, flags: Int): Boolean = false
    override fun unbindService(conn: android.content.ServiceConnection) {}
    override fun startInstrumentation(className: android.content.ComponentName, profileFile: String?, arguments: android.os.Bundle?): Boolean = false
    override fun getSystemService(name: String): Any = throw UnsupportedOperationException()
    override fun getSystemServiceName(serviceClass: Class<*>): String? = null
    override fun checkPermission(permission: String, pid: Int, uid: Int): Int = 0
    override fun checkCallingPermission(permission: String): Int = 0
    override fun checkCallingOrSelfPermission(permission: String): Int = 0
    override fun checkSelfPermission(permission: String): Int = 0
    override fun enforcePermission(permission: String, pid: Int, uid: Int, message: String?) {}
    override fun enforceCallingPermission(permission: String, message: String?) {}
    override fun enforceCallingOrSelfPermission(permission: String, message: String?) {}
    override fun grantUriPermission(toPackage: String?, uri: android.net.Uri?, modeFlags: Int) {}
    override fun revokeUriPermission(uri: android.net.Uri?, modeFlags: Int) {}
    override fun revokeUriPermission(toPackage: String?, uri: android.net.Uri?, modeFlags: Int) {}
    override fun checkUriPermission(uri: android.net.Uri?, pid: Int, uid: Int, modeFlags: Int): Int = 0
    override fun checkCallingUriPermission(uri: android.net.Uri?, modeFlags: Int): Int = 0
    override fun checkCallingOrSelfUriPermission(uri: android.net.Uri?, modeFlags: Int): Int = 0
    override fun checkUriPermission(uri: android.net.Uri?, readPermission: String?, writePermission: String?, pid: Int, uid: Int, modeFlags: Int): Int = 0
    override fun enforceUriPermission(uri: android.net.Uri?, pid: Int, uid: Int, modeFlags: Int, message: String?) {}
    override fun enforceCallingUriPermission(uri: android.net.Uri?, modeFlags: Int, message: String?) {}
    override fun enforceCallingOrSelfUriPermission(uri: android.net.Uri?, modeFlags: Int, message: String?) {}
    override fun enforceUriPermission(uri: android.net.Uri?, readPermission: String?, writePermission: String?, pid: Int, uid: Int, modeFlags: Int, message: String?) {}
    override fun createPackageContext(packageName: String?, flags: Int): Context = this
    override fun createContextForSplit(splitName: String?): Context = this
    override fun createConfigurationContext(overrideConfiguration: android.content.res.Configuration): Context = this
    override fun createDisplayContext(display: android.view.Display): Context = this
    override fun createDeviceProtectedStorageContext(): Context = this
    override fun isDeviceProtectedStorage(): Boolean = false
}

class SplashViewModelTest {

    private lateinit var fakePermissionManager: FakePermissionManager
    private lateinit var viewModel: SplashViewModel

    @Before
    fun setUp() {
        fakePermissionManager = FakePermissionManager(granted = false)
        viewModel = SplashViewModel(fakePermissionManager)
    }

    @Test
    fun initialCheck_whenNotGranted_setsStateToPermissionRequired() {
        assertEquals(SplashUiState.PermissionRequired, viewModel.uiState.value)
    }

    @Test
    fun initialCheck_whenGranted_setsStateToSuccess() {
        fakePermissionManager.setGranted(true)
        val vm = SplashViewModel(fakePermissionManager)
        assertEquals(SplashUiState.Success, vm.uiState.value)
    }

    @Test
    fun onPermissionResult_whenGranted_setsStateToSuccess() {
        viewModel.onPermissionResult(isGranted = true, shouldShowRationale = false)
        assertEquals(SplashUiState.Success, viewModel.uiState.value)
    }

    @Test
    fun onPermissionResult_whenDeniedWithRationale_setsStateToShowRationale() {
        viewModel.onPermissionResult(isGranted = false, shouldShowRationale = true)
        assertEquals(SplashUiState.ShowRationale, viewModel.uiState.value)
    }

    @Test
    fun onPermissionResult_whenPermanentlyDenied_setsStateToShowSettingsRedirect() {
        viewModel.onPermissionResult(isGranted = false, shouldShowRationale = false)
        assertEquals(SplashUiState.ShowSettingsRedirect, viewModel.uiState.value)
    }

    @Test
    fun onPermissionRevoked_resetsStateToPermissionRequired() {
        viewModel.onPermissionResult(isGranted = true, shouldShowRationale = false)
        assertEquals(SplashUiState.Success, viewModel.uiState.value)

        viewModel.onPermissionRevoked()
        assertEquals(SplashUiState.PermissionRequired, viewModel.uiState.value)
    }
}
