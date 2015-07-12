package chimehack.abuseprevention.function.actions;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

import chimehack.abuseprevention.function.triggers.Trigger;

/**
 * Something that can be done as a result of a {@link Trigger}.
 */
public interface Action {
    public void execute(Context context, SharedPreferences prefs);
}
