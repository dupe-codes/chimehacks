package chimehack.abuseprevention.function.triggers;

import android.content.Context;

import chimehack.abuseprevention.function.actions.Action;

/**
 * An event that is considered important and can activate one or more {@link Action}s.
 */
public interface Trigger {
    public boolean isActivated(Context context);
}
