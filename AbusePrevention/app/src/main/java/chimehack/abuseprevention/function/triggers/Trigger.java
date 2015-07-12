package chimehack.abuseprevention.function.triggers;

import android.content.Context;

import chimehack.abuseprevention.function.actions.Action;
import chimehack.abuseprevention.service.ChimeService;

/**
 * An event that is considered important and can activate one or more {@link Action}s.
 */
public interface Trigger {
    public boolean isActivated(ChimeService service);
}
