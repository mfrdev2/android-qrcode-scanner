package com.ba.qrc_scanner.utils

interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): String
}

class CameraPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "It seems you permanently declined camera permission. You can go to the app settings to grant it."
        } else {
            "This app needs access to your camera so that your friends can see you in a call."
        }
    }
}

class RecordAudioPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "It seems you permanently declined microphone permission. You can go to the app settings to grant it."
        } else {
            "This app needs access to your microphone so that your friends can hear you in a call."
        }
    }
}

class PhoneCallPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "It seems you permanently declined phone calling permission. You can go to the app settings to grant it."
        } else {
            "This app needs phone calling permission so that you can talk to your friends."
        }
    }
}


class NotificationPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            return "It seems you permanently declined notification permission. " +
                    "You can go to the app settings to grant it."
        } else {
            return "This app needs notification permission so that you can get " +
                    "important notification from server."
        }
    }
}
class LocationPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            return "It seems you permanently declined location permission. " +
                    "You can go to the app settings to grant it."
        } else {
            return "You have to give permission so that we can find nearest branch for you."
        }
    }
}
class MediaWritePermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            return "It seems you permanently declined storage permission. " +
                    "You can go to the app settings to grant it.";
        } else {
            return "This app needs access to your storage to save and manage your media files.";
        }
    }
}

class DefaultPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            return "It seems you permanently declined permission that need this app. " +
                    "You can go to the app settings to grant it."
        } else {
            return "This app needs to your permission so that you " +
                    "enjoying the full experience of this app."
        }
    }
}
