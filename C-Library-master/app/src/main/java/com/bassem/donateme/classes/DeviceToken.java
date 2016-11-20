package com.bassem.donateme.classes;
        import android.util.Log;
        import android.widget.Toast;

        import com.google.firebase.iid.FirebaseInstanceId;
        import com.google.firebase.iid.FirebaseInstanceIdService;


public class DeviceToken extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIDService";
    private String deviceToken ="";

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    /**

     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        this.deviceToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + this.deviceToken);
       sendRegistrationToServer(this.deviceToken);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }
}