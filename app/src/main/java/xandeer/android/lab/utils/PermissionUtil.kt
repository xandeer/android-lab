package xandeer.android.lab.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtil {
  private val EXTERNAL_STORAGE_PERMISSIONS =
    Permission(
      arrayListOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
      ),
      0
    )

  data class Permission(val permissions: List<String>, val code: Int)

  fun requestExternalStorageIfNeed(activity: Activity): Boolean =
    requestPermissionIfNeed(activity, EXTERNAL_STORAGE_PERMISSIONS)

  private fun requestPermissionIfNeed(
    activity: Activity, permission: Permission
  ): Boolean {
    return if (!isGranted(activity, permission)
    ) {
      request(activity, permission)
      true
    } else {
      false
    }
  }

  private fun isGranted(context: Context, permission: Permission): Boolean {
    var hasPermissions = true
    permission.permissions.forEach {
      if (ContextCompat.checkSelfPermission(context, it)
        != PackageManager.PERMISSION_GRANTED
      ) {
        hasPermissions = false
        return@forEach
      }
    }
    return hasPermissions
  }

  private fun request(activity: Activity, permission: Permission) {
    ActivityCompat.requestPermissions(
      activity, permission.permissions.toTypedArray(), permission.code
    )
  }
}