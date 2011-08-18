package net.luminis.clockreader;

import java.util.HashMap;
import java.util.List;
import java.util.ServiceLoader;

import org.eclipse.core.runtime.internal.adaptor.EclipseAppLauncher;
import org.eclipse.osgi.service.environment.EnvironmentInfo;
import org.eclipse.osgi.service.runnable.ApplicationLauncher;
import org.osgi.framework.BundleContext;

import de.kalpatec.pojosr.framework.launch.BundleDescriptor;
import de.kalpatec.pojosr.framework.launch.ClasspathScanner;
import de.kalpatec.pojosr.framework.launch.PojoServiceRegistry;
import de.kalpatec.pojosr.framework.launch.PojoServiceRegistryFactory;

public class Main {
	public static void main(String[] args) throws Exception {
		List<BundleDescriptor> scan = new ClasspathScanner()
				.scanForBundles("(&(Bundle-SymbolicName=*)(!(|(Bundle-SymbolicName=org.eclipse.osgi*)(Bundle-SymbolicName=org.eclipse.update*))))");
		ServiceLoader<PojoServiceRegistryFactory> loader = ServiceLoader.load(PojoServiceRegistryFactory.class);

		PojoServiceRegistry reg = loader.iterator().next().newPojoServiceRegistry(new HashMap());
		BundleContext context = reg.getBundleContext();
		context.registerService(EnvironmentInfo.class.getName(),
				new EnvironmentInfo() {

					@Override
					public String setProperty(String key, String value) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public boolean inDevelopmentMode() {
						// TODO Auto-generated method stub
						return true;
					}

					@Override
					public boolean inDebugMode() {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public String getWS() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public String getProperty(String key) {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public String getOSArch() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public String getOS() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public String[] getNonFrameworkArgs() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public String getNL() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public String[] getFrameworkArgs() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public String[] getCommandLineArgs() {
						// TODO Auto-generated method stub
						return null;
					}
				}, null);
		reg.startBundles(scan);
		EclipseAppLauncher appLauncher = new EclipseAppLauncher(context, true,
				true, null);
		context.registerService(ApplicationLauncher.class.getName(),
				appLauncher, null);
		appLauncher.start(null);
	}
}
