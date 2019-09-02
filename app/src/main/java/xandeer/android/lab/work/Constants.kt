package xandeer.android.lab.work

object Constants {
  // Notification Channel constants

  // Name of Notification Channel for verbose notifications of background work
  const val VERBOSE_NOTIFICATION_CHANNEL_NAME =
    "Verbose WorkManager Notifications"
  const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION =
    "Shows notifications whenever work starts"
  const val NOTIFICATION_TITLE = "Lab Work Manager"
  const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
  const val NOTIFICATION_ID = 1

  const val DELAY_MILLIS = 3000L

  const val PICTURE_URI_KEY = "PICTURE_URI_KEY"
  const val OUTPUT_PATH = "blur_outputs"
  const val TAG_OUTPUT = "OUTPUT"

  // The name of the picture manipulation work
  const val PICTURE_MANIPULATION_WORK_NAME = "picture_manipulation_work"
}