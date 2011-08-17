# Enter the path to your own eclipse's plugins directory
export ECLIPSE_HOME=/Applications/eclipse3.6/plugins

function findbundle {
    find $ECLIPSE_HOME -name $1_*.jar
}

# Build the framework
cd ../framework
mvn -DskipTests=true install
cd ../net.luminis.clockreader

# Remember you need to build the three required bundles yourself, and export them to our bundle directory

# Startup the newly built framework
java -cp ../framework/target/de.kalpatec.pojosr.framework-0.1.2-SNAPSHOT.jar:bundle/org.apache.felix.gogo.command-0.10.0.jar:bundle/org.apache.felix.gogo.runtime-0.10.0.jar:bundle/org.apache.felix.gogo.shell-0.10.0.jar:bundle/plugins/a_saxactivator_1.0.0.jar:`findbundle org.eclipse.equinox.common`:`findbundle org.eclipse.equinox.registry`:bundle/org.eclipse.equinox.supplement-1.3.0.jar:`findbundle org.eclipse.equinox.util`:`findbundle org.eclipse.core.runtime`:bundle/plugins/net.luminis.futureclock_1.0.0.jar:bundle/plugins/net.luminis.clockreader_1.0.0.jar de.kalpatec.pojosr.framework.PojoSR "(&(Bundle-SymbolicName=*)(!(Bundle-SymbolicName=org.eclipse.core.runtime*)))"
