package com.harmonicplayer.app.util

import android.Manifest
import android.os.Build
import org.junit.Assert.assertEquals
import org.junit.Test

class PermissionStateTest {

    @Test
    fun testPermissionStateVariants() {
        val granted: PermissionState = PermissionState.Granted
        val denied: PermissionState = PermissionState.Denied
        val rationale: PermissionState = PermissionState.ShowRationale("android.permission.READ_MEDIA_AUDIO")
        val permanent: PermissionState = PermissionState.PermanentlyDenied("android.permission.READ_MEDIA_AUDIO")

        assertEquals(PermissionState.Granted, granted)
        assertEquals(PermissionState.Denied, denied)
        assertEquals("android.permission.READ_MEDIA_AUDIO", (rationale as PermissionState.ShowRationale).requiredPermission)
        assertEquals("android.permission.READ_MEDIA_AUDIO", (permanent as PermissionState.PermanentlyDenied).requiredPermission)
    }
}
