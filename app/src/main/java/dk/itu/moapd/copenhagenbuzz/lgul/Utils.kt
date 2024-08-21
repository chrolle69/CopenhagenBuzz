package dk.itu.moapd.copenhagenbuzz.lgul

import android.content.Context
import androidx.core.content.edit
import java.text.SimpleDateFormat
import java.util.Locale

internal object SharedPreferenceUtil {
    const val KEY_FOREGROUND_ENABLED = "tracking_foreground_location"
    const val KEY_AUTH_ENABLED = "face_authentication"
    const val KEY_MOUTH_AUTH = "mouth_mad_smile_authentication"
    const val KEY_LEFT_EYE_AUTH = "left_eye_closed_open_authentication"
    const val KEY_RIGHT_EYE_AUTH = "right_eye_closed_open_authentication"




    fun saveLocationTrackingPref(context: Context, requestingLocationUpdates: Boolean) =
        context.getSharedPreferences(
            context.getString(R.string.preference_file_key), Context.MODE_PRIVATE).edit {
            putBoolean(KEY_FOREGROUND_ENABLED, requestingLocationUpdates)
            }

    fun saveFaceAuth(context: Context, faceAuthStatus: Boolean) =
        context.getSharedPreferences(
            context.getString(R.string.preference_file_key), Context.MODE_PRIVATE).edit {
            putBoolean(KEY_AUTH_ENABLED, faceAuthStatus)
        }

    fun saveLeftEyeAuth(context: Context, faceAuthStatus: Boolean) =
        context.getSharedPreferences(
            context.getString(R.string.preference_file_key), Context.MODE_PRIVATE).edit {
            putBoolean(KEY_LEFT_EYE_AUTH, faceAuthStatus)
        }
    fun saveRightEyeAuth(context: Context, faceAuthStatus: Boolean) =
        context.getSharedPreferences(
            context.getString(R.string.preference_file_key), Context.MODE_PRIVATE).edit {
            putBoolean(KEY_RIGHT_EYE_AUTH, faceAuthStatus)
        }
    fun saveMouthAuth(context: Context, faceAuthStatus: Boolean) =
        context.getSharedPreferences(
            context.getString(R.string.preference_file_key), Context.MODE_PRIVATE).edit {
            putBoolean(KEY_MOUTH_AUTH, faceAuthStatus)
        }

    fun Long.toSimpleDateFormat(): String {
        val dateFormat = SimpleDateFormat("E, MMM dd yyyy hh:mm:ss a", Locale.US)
        return dateFormat.format(this)
    }
}