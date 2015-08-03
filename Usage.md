# Introduction #

There are two ways to use pojosr:
  * As a service registry
  * As a OSGi "light" framework

# As a Service registry #

You can get a service registry like this:
```
ServiceLoader<PojoServiceRegistryFactory> loader = ServiceLoader.load(PojoServiceRegistryFactory.class);

PojoServiceRegistry registry = loader.iterator().next().newPojoServiceRegistry(new HashMap());
```
the interface looks like:
```
public interface PojoServiceRegistry {
	public BundleContext getBundleContext();

	public void addServiceListener(ServiceListener listener, String filter)
			throws InvalidSyntaxException;

	public void addServiceListener(ServiceListener listener);

	public void removeServiceListener(ServiceListener listener);

	public ServiceRegistration registerService(String[] clazzes,
			Object service, @SuppressWarnings("rawtypes") Dictionary properties);

	public ServiceRegistration registerService(String clazz, Object service,
			@SuppressWarnings("rawtypes") Dictionary properties);

	public ServiceReference[] getServiceReferences(String clazz, String filter)
			throws InvalidSyntaxException;

	public ServiceReference getServiceReference(String clazz);

	public Object getService(ServiceReference reference);

	public boolean ungetService(ServiceReference reference);
}
```
# As a OSGi "light" framework #

Either use the PojoServiceRegistry with bundles form the classpath installed and started by using the provided classpathscanner like this (or standalone):

```
Map config = new HashMap();
config.put(PojoServiceRegistryFactory.BUNDLE_DESCRIPTORS, new ClasspathScanner().scanForBundles());

ServiceLoader<PojoServiceRegistryFactory> loader = ServiceLoader.load(PojoServiceRegistryFactory.class);

PojoServiceRegistry registry = loader.iterator().next().newPojoServiceRegistry(config);
```

# Standalone #

A main method is provided as well e.g. the following gives us a working gogo shell by just putting the gogo bundles on the classpath and running the framework,

```
java -cp target/de.kalpatec.pojosr.framework-0.1.0-SNAPSHOT.jar:org.apache.felix.gogo.runtime-0.8.0.jar:org.apache.felix.gogo.shell-0.8.0.jar:org.apache.felix.gogo.command-0.8.0.jar de.kalpatec.pojosr.framework.PojoSR
...
Starting: org.apache.felix.gogo.runtime
Starting: org.apache.felix.gogo.shell
Starting: org.apache.felix.gogo.command
g! lb
START LEVEL 1
   ID|State      |Level|Name
    0|Active     |    1|System Bundle (0.0.1)
    1|Active     |    1|jar:file:/System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Classes/classes.jar!/ (0.0.0)
    2|Active     |    1|jar:file:/System/Library/Frameworks/JavaVM.framework/Versions/A/Frameworks/JavaRuntimeSupport.framework/Versions/A/Resources/Java/JavaRuntimeSupport.jar!/ (0.0.0)
    3|Active     |    1|jar:file:/System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Classes/ui.jar!/ (0.0.0)
    4|Active     |    1|jar:file:/System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Classes/laf.jar!/ (0.0.0)
    5|Active     |    1|jar:file:/System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Classes/jsse.jar!/ (0.0.0)
    6|Active     |    1|jar:file:/System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Classes/jce.jar!/ (0.0.0)
    7|Active     |    1|jar:file:/System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Classes/charsets.jar!/ (0.0.0)
    8|Active     |    1|jar:file:/Library/Java/Extensions/RXTXcomm.jar!/ (0.0.0)
    9|Active     |    1|jar:file:/System/Library/Java/Extensions/AppleScriptEngine.jar!/ (0.0.0)
   10|Active     |    1|jar:file:/System/Library/Java/Extensions/CoreAudio.jar!/ (0.0.0)
   11|Active     |    1|jar:file:/System/Library/Java/Extensions/dns_sd.jar!/ (0.0.0)
   12|Active     |    1|jar:file:/System/Library/Java/Extensions/j3daudio.jar!/ (0.0.0)
   13|Active     |    1|jar:file:/System/Library/Java/Extensions/j3dcore.jar!/ (0.0.0)
   14|Active     |    1|jar:file:/System/Library/Java/Extensions/j3dutils.jar!/ (0.0.0)
   15|Active     |    1|jar:file:/System/Library/Java/Extensions/jai_codec.jar!/ (0.0.0)
   16|Active     |    1|jar:file:/System/Library/Java/Extensions/jai_core.jar!/ (0.0.0)
   17|Active     |    1|jar:file:/System/Library/Java/Extensions/mlibwrapper_jai.jar!/ (0.0.0)
   18|Active     |    1|jar:file:/System/Library/Java/Extensions/MRJToolkit.jar!/ (0.0.0)
   19|Active     |    1|jar:file:/System/Library/Java/Extensions/QTJava.zip!/ (0.0.0)
   20|Active     |    1|jar:file:/System/Library/Java/Extensions/vecmath.jar!/ (0.0.0)
   21|Active     |    1|jar:file:/System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Home/lib/ext/apple_provider.jar!/ (0.0.0)
   22|Active     |    1|jar:file:/System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Home/lib/ext/dnsns.jar!/ (0.0.0)
   23|Active     |    1|jar:file:/System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Home/lib/ext/js-engine.jar!/ (0.0.0)
   24|Active     |    1|jar:file:/System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Home/lib/ext/js.jar!/ (0.0.0)
   25|Active     |    1|jar:file:/System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Home/lib/ext/localedata.jar!/ (0.0.0)
   26|Active     |    1|jar:file:/System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Home/lib/ext/sunjce_provider.jar!/ (0.0.0)
   27|Active     |    1|jar:file:/System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Home/lib/ext/sunpkcs11.jar!/ (0.0.0)
   28|Active     |    1|Apache Felix Gogo Runtime (0.8.0)
   29|Active     |    1|Apache Felix Gogo Shell (0.8.0)
   30|Active     |    1|Apache Felix Gogo Command (0.8.0)
g! 
```

# GAE and other "no user thread" frameworks #

The GAE doesn't allow threads so there is a property to deliver all events synchronous:

-Dde.kalpatec.pojosr.framework.events.sync=true

# Services #

There is a PackageAdmin and a StartLevel service but they don't do anything (just published because quite some interesting bundles have dependencies on them).