package xandeer.android.lab.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi


object PermissionUtil {
  private val WRITE_EXTERNAL_STORAGE = Permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 0)

  data class Permission(val permission: String, val code: Int)

  fun requestWriteExternalStorageIfNeed(activity: Activity): Boolean = requestPermissionIfNeed(activity, WRITE_EXTERNAL_STORAGE)

  private fun requestPermissionIfNeed(activity: Activity, permission: Permission): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        && !isGranted(activity, permission)) {
      request(activity, permission)
      true
    } else {
      false
    }
  }

  @RequiresApi(Build.VERSION_CODES.M)
  private fun isGranted(context: Context, permission: Permission): Boolean {
    return context.checkSelfPermission(permission.permission) == PackageManager.PERMISSION_GRANTED
  }

  @RequiresApi(Build.VERSION_CODES.M)
  private fun request(activity: Activity, permission: Permission) {
    activity.requestPermissions(arrayOf(permission.permission), permission.code)
  }
}