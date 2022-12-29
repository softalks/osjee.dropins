package com.softalks.osjee.dropins;

import static java.util.Arrays.asList;
import static org.osgi.framework.BundleEvent.STARTED;
import static org.osgi.framework.Constants.FRAMEWORK_BEGINNING_STARTLEVEL;
import static org.osgi.framework.wiring.BundleWiring.LISTRESOURCES_LOCAL;
import static org.osgi.framework.wiring.BundleWiring.LISTRESOURCES_RECURSE;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.annotation.WebServlet;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.framework.wiring.BundleWiring;

import com.softalks.dsu.Features;
import com.softalks.dsu.Server;

public class OsjeeDropins implements Features, SynchronousBundleListener, ServletContextAttributeListener {

	protected static final String START = "felix.fileinstall.start.level";
	protected final int level = 5;

	private BundleContext osgi;
	String classes;

	@Override
	public final void accept(BundleContext context) {
		osgi = context;
		String classes = osgi.getProperty("com.softalks.osjee.classes");
		if (classes != null) {
			context.addBundleListener(this);
			if (!classes.equals("/")) {
				if (!classes.endsWith("/")) {
					classes = classes + "/";
				}
				if (!classes.startsWith("/")) {
					classes = "/" + classes;
				}
			}
			this.classes = classes;
		}
	}

	private void register(Class<?> servlet, String pattern) {
		try {
			Dictionary<String, Object> properties = new Hashtable<>();
			properties.put("osgi.http.whiteboard.servlet.pattern", pattern);
			this.osgi.registerService(Servlet.class, (Servlet) servlet.newInstance(), properties);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	protected BundleContext getBundleContext() {
		return osgi;
	}

	@Override
	public final void bundleChanged(BundleEvent event) {
		int type = event.getType();
		Bundle bundle = event.getBundle();
		if (!bundle.getLocation().startsWith("file:/")) {
			return;
		}
		if (type == STARTED) {
			int options = LISTRESOURCES_RECURSE | LISTRESOURCES_LOCAL;
			BundleWiring wiring = bundle.adapt(BundleWiring.class);
			Collection<String> classes = wiring.listResources(this.classes, "*.class", options);
			ClassLoader loader = wiring.getClassLoader();
			for (String resource : classes) {
				try {
					resource = resource.substring(this.classes.length() - 1);
					resource = resource.replace('/', '.');
					resource = resource.substring(0, resource.length() - 6);
					Class<?> candidate = loader.loadClass(resource);
					WebServlet annotation = candidate.getAnnotation(WebServlet.class);
					if (annotation != null) {
						register(candidate, annotation.value()[0]);
					}
				} catch (ClassNotFoundException e) {
					throw new IllegalStateException(e);
				}
			}
		}
	}

	@Override
	public List<String> apply(Map<String, Object> configuration) {
		if (!configuration.containsKey(START)) {
			configuration.put(START, level);
		}
		configuration.put(FRAMEWORK_BEGINNING_STARTLEVEL, level);
		return asList("telnet-console", "web-console", "directory-watcher");
	}

	@Override
	public void attributeAdded(ServletContextAttributeEvent event) {
		if (event.getName().equals(Server.class.getName())) {
			Server server = (Server) event.getValue();
			server.accept(this, OsjeeDropins.class);
		}
	}

}