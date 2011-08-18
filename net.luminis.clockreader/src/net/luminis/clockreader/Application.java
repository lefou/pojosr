package net.luminis.clockreader;

import net.luminis.clock.Clock;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {
	public Object start(IApplicationContext context) throws Exception {
		for (IConfigurationElement e : Platform.getExtensionRegistry().getConfigurationElementsFor("net.luminis.clockreader.clock")) {
			Object clock = e.createExecutableExtension("class");
			if (clock instanceof Clock) {
				System.out.println("From Application: According to " + clock + ", it is " + ((Clock) clock).time());
			}
		}
		return IApplication.EXIT_OK;
	}

	public void stop() {
	}
}
