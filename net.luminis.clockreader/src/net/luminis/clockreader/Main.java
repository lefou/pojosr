package net.luminis.clockreader;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.runtime.adaptor.LocationManager;
import org.eclipse.core.runtime.internal.adaptor.EclipseAdaptorHook;
import org.eclipse.core.runtime.internal.adaptor.EclipseAppLauncher;
import org.eclipse.core.runtime.internal.adaptor.EclipseLogHook;
import org.eclipse.osgi.baseadaptor.BaseAdaptor;
import org.eclipse.osgi.service.environment.EnvironmentInfo;
import org.eclipse.osgi.service.runnable.ApplicationLauncher;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;

import de.kalpatec.pojosr.framework.PojoServiceRegistryFactoryImpl;
import de.kalpatec.pojosr.framework.launch.BundleDescriptor;
import de.kalpatec.pojosr.framework.launch.ClasspathScanner;
import de.kalpatec.pojosr.framework.launch.PojoServiceRegistry;

public class Main {
	public static void main(final String[] args) throws Exception {
		List<BundleDescriptor> scan = new ClasspathScanner()
				.scanForBundles("(Bundle-SymbolicName=*)");
		
		PojoServiceRegistry reg = new PojoServiceRegistryFactoryImpl().newPojoServiceRegistry(new HashMap());
		BundleContext context = reg.getBundleContext();
		context.registerService(EnvironmentInfo.class.getName(),
				new EnvironmentInfo() {

					
					public String setProperty(String key, String value) {
						return System.setProperty(key, value);
					}

					
					public boolean inDevelopmentMode() {
						// TODO Auto-generated method stub
						return false;
					}

					
					public boolean inDebugMode() {
						// TODO Auto-generated method stub
						return false;
					}

					
					public String getWS() {
						return System.getProperty("osgi.ws");
					}

					
					public String getProperty(String key) {
						// TODO Auto-generated method stub
						return System.getProperty(key);
					}

					
					public String getOSArch() {
						return System.getProperty("osgi.arch");
					}

					
					public String getOS() {
						return System.getProperty("osgi.os");
					}

					
					public String[] getNonFrameworkArgs() {
						// TODO Auto-generated method stub
						return args;
					}

					
					public String getNL() {
						return System.getProperty("osgi.nl");
					}

					
					public String[] getFrameworkArgs() {
						return args;
					}

					
					public String[] getCommandLineArgs() {
						return args;
					}
				}, null);
		reg.registerService(LogListener.class.getName(), new LogListener() {
			
			public void logged(LogEntry arg0) {
				System.out.println(arg0.getMessage());
				if (arg0.getException() != null) {
					arg0.getException().printStackTrace();
				}
			}
		}, null);
		LocationManager.initializeLocations();
		EclipseLogHook logH = new EclipseLogHook();
		EclipseAdaptorHook hook = new EclipseAdaptorHook();
		BaseAdaptor adaptor = new BaseAdaptor(new String[0]);
		adaptor.frameworkStart(reg.getBundleContext());
		adaptor.initializeStorage();
		hook.initialize(adaptor);
		logH.initialize(adaptor);
		try {
		hook.frameworkStart(reg.getBundleContext());
		} catch (Exception ex) {
			// this is good enough
		}

		logH.frameworkStart(context);
		reg.startBundles(scan);
		EclipseAppLauncher appLauncher = new EclipseAppLauncher(context, false,
				true, null);
		context.registerService(ApplicationLauncher.class.getName(),
				appLauncher, null);
		appLauncher.start(null);
	}
}
