package chimehack.abuseprevention.ui;

import android.app.Application;
import android.content.Intent;

import chimehack.abuseprevention.service.ChimeService;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this, ChimeService.class));
    }
}
