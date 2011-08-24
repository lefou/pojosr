package net.luminis.clockreader;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.adaptor.LocationManager;
import org.eclipse.core.runtime.internal.adaptor.EclipseAdaptorHook;
import org.eclipse.core.runtime.internal.adaptor.EclipseAppLauncher;
import org.eclipse.core.runtime.internal.adaptor.EclipseCommandProvider;
import org.eclipse.core.runtime.internal.adaptor.EclipseLogHook;
import org.eclipse.core.runtime.internal.adaptor.PluginConverterImpl;
import org.eclipse.osgi.baseadaptor.BaseAdaptor;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.eclipse.osgi.internal.baseadaptor.AdaptorUtil;
import org.eclipse.osgi.internal.module.ResolverImpl;
import org.eclipse.osgi.internal.resolver.StateHelperImpl;
import org.eclipse.osgi.internal.resolver.StateImpl;
import org.eclipse.osgi.service.environment.EnvironmentInfo;
import org.eclipse.osgi.service.localization.BundleLocalization;
import org.eclipse.osgi.service.pluginconversion.PluginConverter;
import org.eclipse.osgi.service.resolver.DisabledInfo;
import org.eclipse.osgi.service.resolver.PlatformAdmin;
import org.eclipse.osgi.service.resolver.Resolver;
import org.eclipse.osgi.service.resolver.State;
import org.eclipse.osgi.service.resolver.StateDelta;
import org.eclipse.osgi.service.resolver.StateHelper;
import org.eclipse.osgi.service.resolver.StateObjectFactory;
import org.eclipse.osgi.service.runnable.ApplicationLauncher;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
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
		
		final PojoServiceRegistry reg = new PojoServiceRegistryFactoryImpl().newPojoServiceRegistry(new HashMap());
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
		AdaptorUtil.register(PlatformAdmin.class.getName(), new PlatformAdmin() {
			
			public void removeDisabledInfo(DisabledInfo disabledInfo) {
				// TODO Auto-generated method stub
				
			}
			
			public StateHelper getStateHelper() {
				// TODO Auto-generated method stub
				return new StateHelperImpl();
			}
			
			public State getState(boolean mutable) {
				// TODO Auto-generated method stub
				return new StateImpl() {
					
					public StateDelta compare(State baseState) throws BundleException {
						// TODO Auto-generated method stub
						return null;
					}
				};
			}
			
			public State getState() {
				// TODO Auto-generated method stub
				return getFactory().createState();
			}
			
			public Resolver getResolver() {
				// TODO Auto-generated method stub
				return new ResolverImpl(reg.getBundleContext(), false);
			}
			
			public StateObjectFactory getFactory() {
				// TODO Auto-generated method stub
				return StateObjectFactory.defaultFactory;
			}
			
			public Resolver createResolver() {
				// TODO Auto-generated method stub
				return new ResolverImpl(reg.getBundleContext(), false);
			}
			
			public void commit(State state) throws BundleException {
				// TODO Auto-generated method stub
				
			}
			
			public void addDisabledInfo(DisabledInfo disabledInfo) {
				// TODO Auto-generated method stub
				
			}
		}, context);
		PluginConverter converter = PluginConverterImpl.getDefault();
		if (converter == null)
			converter = new PluginConverterImpl(adaptor, context);
		AdaptorUtil.register(PluginConverter.class.getName(), converter, context);
		AdaptorUtil.register(CommandProvider.class.getName(), new EclipseCommandProvider(context), context);
		AdaptorUtil.register(org.eclipse.osgi.service.localization.BundleLocalization.class.getName(), new BundleLocalization() {
			private final Map<String, PropertyResourceBundle> m_cache = new ConcurrentHashMap<String, PropertyResourceBundle>();
			public ResourceBundle getLocalization(final Bundle arg0, final String arg1) {
				// TODO Auto-generated method stub
				return new ResourceBundle() {
					protected Object handleGetObject(String key) {
						Object result = arg0.getHeaders(arg1).get(key);
						if (result == null) {
							try {
								PropertyResourceBundle prb = m_cache.get(arg0.getSymbolicName());
								if (prb == null) {
								InputStream in = arg0.getEntry("plugin.properties").openStream();
								 prb = new PropertyResourceBundle(in);
								 m_cache.put(arg0.getSymbolicName(), prb);
								 in.close();
								}
								result = prb.handleGetObject(key);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						//System.out.println("GetHeader: " + key + "=" + result + " for: " + arg0.getSymbolicName() + " at " + arg0.getLocation());
						
						return result;
						
						}

					@Override
					public Enumeration<String> getKeys() {
						return arg0.getHeaders(arg1).keys();
					}
					};
			}}, context);
	

		logH.frameworkStart(context);
		BundleDescriptor equinox = null;
		for (Iterator<BundleDescriptor> iter = scan.iterator();iter.hasNext();) {
			BundleDescriptor bd = iter.next();
			String sn = bd.getHeaders().get(Constants.BUNDLE_SYMBOLICNAME);
			if ((sn != null) && (sn.trim().startsWith("org.eclipse.osgi;"))) {
				iter.remove();
				Map headers = bd.getHeaders();
				headers.remove(Constants.BUNDLE_ACTIVATOR);
				equinox = new BundleDescriptor(bd.getClassLoader(), bd.getUrl(), headers);
			}
		}
		if (equinox != null) {
			scan.add(0, equinox);
		}
		reg.startBundles(scan);
		EclipseAppLauncher appLauncher = new EclipseAppLauncher(context, false,
				true, null);
		context.registerService(ApplicationLauncher.class.getName(),
				appLauncher, null);
		new Thread() {public void run() {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (Bundle b : reg.getBundleContext().getBundles()) {
			try {
			 if (b.getState() != Bundle.ACTIVE) {
				 b.start();
			 }
			} catch (Exception ex) {
				
			}
		}
		}
		}.start();
		appLauncher.start(null);
	}
}
