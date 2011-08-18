package net.luminis.clockreader;

import net.luminis.clock.Clock;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
	public void start(final BundleContext bundleContext) throws Exception {
		
		for (IConfigurationElement e : Platform.getExtensionRegistry().getConfigurationElementsFor("net.luminis.clockreader.clock")) {
			Object clock = e.createExecutableExtension("class");
			if (clock instanceof Clock) {
				System.out.println("From Activator: According to " + clock + ", it is " + ((Clock) clock).time());
			}
		}
	}

	public void stop(BundleContext bundleContext) throws Exception {
	}
}
