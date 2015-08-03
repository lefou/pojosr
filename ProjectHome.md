A service registry that enables OSGi style service registry programs without using an OSGi framework.

The idea is to create something that would make the service and parts of the life cycle layer of OSGi available in environments where it typically isn't.

The current implementation is based in parts on Apache Felix and is a work in progress. Feel free to play with it and let me know what is missing and if it is useful for something - if this is going somewhere I'll try to merge it back to felix (probably as a new subproject). The reason that it is here in the first place is that I'm not sure it will go anywhere.

That said, what is there is a service registry that you can use from normal java (including access to a bundle context so you can continue using e.g., ServiceTracker) and a framework that installs and starts bundles found on the classpath  and still provides global visibility to the classes. However, it is not a full OSGi framework and doesn't provide all of the features of one.

It works rather well with well-designed bundles including getting scr, dependencymanager, and ipojo based services to work. People used it on the Google App Engine (GAE), Amazon Elastic Beanstalk, and inside JEE containers (it has a mode to run without creating threads and doesn't create a bundle cache -- hence, works on the GAE alas, a lot of bundles don't because they in turn create threads).

Finally, this is not intended to be a replacement for osgi but rather as a way to enable bundles in environments where they otherwise couldn't run and as a start to OSGi development. Use at your own risk.

Updates:

**2012/11/07 - Download the new bare version 0.2.1 from maven: com.googlecode.pojosr:de.kalpatec.pojosr.framework.bare:0.2.1 or from the downloads section.**

**2012/11/07 - Download version 0.2.1 from maven: com.googlecode.pojosr:de.kalpatec.pojosr.framework:0.2.1 or from the downloads section.**

**2012/11/04 - Download version 0.2.0 from maven: com.googlecode.pojosr:de.kalpatec.pojosr.framework:0.2.0 or from the downloads section.**

**2011/10/12 - Download version 0.1.8 from maven: com.googlecode.pojosr:de.kalpatec.pojosr.framework:0.1.8 or from the downloads section.**

**2011/10/12 - Download version 0.1.6 from maven: com.googlecode.pojosr:de.kalpatec.pojosr.framework:0.1.6 or from the downloads section.**

**2011/08/30 - Download version 0.1.4 from maven: com.googlecode.pojosr:de.kalpatec.pojosr.framework:0.1.4.**

