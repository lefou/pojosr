package de.kalpatec.pojosr.examples.gae;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.UUID;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.felix.cm.PersistenceManager;
import org.apache.felix.cm.file.ConfigurationHandler;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.log.LogService;

import de.kalpatec.pojosr.framework.launch.ClasspathScanner;
import de.kalpatec.pojosr.framework.launch.PojoServiceRegistry;
import de.kalpatec.pojosr.framework.launch.PojoServiceRegistryFactory;

@SuppressWarnings("serial")
public class PojoSRDemo extends HttpServlet {
	private static Map m_config = new HashMap();
	static {
		try {
			m_config.put(
					PojoServiceRegistryFactory.BUNDLE_DESCRIPTORS,
					new ClasspathScanner().scanForBundles());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void service(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		try {
			PojoServiceRegistry registry = getRegistryForSession(req
					.getSession());
			synchronized (registry) {
				final Map<String, String> configs = (Map<String, String>) ((req
						.getSession().getAttribute("config-state") != null) ? req
						.getSession().getAttribute("config-state")
						: new HashMap<String, String>());
				ServiceRegistration reg = registry.registerService(
						PersistenceManager.class.getName(),
						new PersistenceManager() {

							@Override
							public void store(String arg0, Dictionary arg1)
									throws IOException {
								ByteArrayOutputStream output = new ByteArrayOutputStream();
								ConfigurationHandler.write(output, arg1);
								configs.put(arg0, output.toString("UTF-8"));
							}

							@Override
							public Dictionary load(String arg0)
									throws IOException {
								return ConfigurationHandler
										.read(new ByteArrayInputStream(configs
												.get(arg0).getBytes("UTF-8")));
							}

							@Override
							public Enumeration getDictionaries()
									throws IOException {
								return new Enumeration<Dictionary>() {
									Iterator<String> iter = configs.values()
											.iterator();

									@Override
									public boolean hasMoreElements() {
										return iter.hasNext();
									}

									@Override
									public Dictionary nextElement() {
										try {
											return ConfigurationHandler
													.read(new ByteArrayInputStream(
															iter.next()
																	.getBytes(
																			"UTF-8")));
										} catch (Exception e) {
											throw new RuntimeException(e);
										}
									}
								};
							}

							@Override
							public boolean exists(String arg0) {
								return configs.containsKey(arg0);
							}

							@Override
							public void delete(String arg0) throws IOException {
								configs.remove(arg0);
							}
						}, new Properties() {
							{
								put(Constants.SERVICE_RANKING,
										Integer.MAX_VALUE);
							}
						});
				for (Bundle b : registry.getBundleContext().getBundles()) {
					if ((b.getSymbolicName() != null)
							&& (b.getSymbolicName()
									.equals("org.apache.felix.configadmin"))) {
						b.start();
					}
				}

				HttpServlet service = (HttpServlet) registry
						.getService(registry.getServiceReferences(
								HttpServlet.class.getName(),
								"(http.felix.dispatcher=org.apache.felix.http.base.internal.DispatcherServlet)")[0]);
				service.service(req, resp);
				HttpSession session = req.getSession();
				Map<Long, Integer> state = new HashMap<Long, Integer>();
				for (Bundle b : registry.getBundleContext().getBundles()) {
					if (b.getBundleId() != 0) {
						state.put(b.getBundleId(), b.getState());
					}
				}
				for (Bundle b : registry.getBundleContext().getBundles()) {
					if ((b.getSymbolicName() != null)
							&& (b.getSymbolicName()
									.equals("org.apache.felix.configadmin"))) {
						b.stop();
					}
				}
				session.setAttribute("pojosr-state", state);
				reg.unregister();
				session.setAttribute("config-state", configs);
			}
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	private static final Map<UUID, PojoServiceRegistry> m_sessionRegistry = new HashMap<UUID, PojoServiceRegistry>();

	private PojoServiceRegistry getRegistryForSession(HttpSession session)
			throws Exception {
		synchronized (m_sessionRegistry) {
			UUID id = (UUID) session.getAttribute("pojosr-id");
			if (id == null) {
				id = UUID.randomUUID();
				session.setAttribute("pojosr-id", id);
			}
			PojoServiceRegistry result = m_sessionRegistry.get(id);
			if (result == null) {
				result = ServiceLoader.load(PojoServiceRegistryFactory.class)
						.iterator().next().newPojoServiceRegistry(m_config);

				HttpServlet service = (HttpServlet) result
						.getService(result.getServiceReferences(
								HttpServlet.class.getName(),
								"(http.felix.dispatcher=org.apache.felix.http.base.internal.DispatcherServlet)")[0]);
				service.init(getServletConfig());

				Map<Long, Integer> state = (Map<Long, Integer>) session
						.getAttribute("pojosr-state");
				if (state != null) {
					for (Entry<Long, Integer> entry : state.entrySet()) {
						Bundle b = result.getBundleContext().getBundle(
								entry.getKey());
						if (b != null) {
							if ((b.getSymbolicName() != null)
									&& (b.getSymbolicName()
											.equals("org.apache.felix.configadmin"))) {

							} else {
								if (b.getState() != entry.getValue()) {
									switch (entry.getValue()) {
									case Bundle.ACTIVE:
										b.start();
									case Bundle.RESOLVED:
										b.stop();
									}
								}
							}
						}
					}
				}
				m_sessionRegistry.put(id, result);
			}
			return result;
		}

	}
}
