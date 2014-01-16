package foo;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class ServiceFromClasspathExtractor {
	private class AttributeContainer {
		String n;
		Class<?> c;
	}

	private class MethodContainer {
		Method m;
		List<AttributeContainer> acs = new ArrayList<>();

		public MethodContainer(Method m) {
			this.m = m;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(m.getName());
			for (AttributeContainer ac : acs) {
				sb.append("\n").append(ac.toString());
			}
			return sb.toString();
		}
	}

	private class ServiceContainer {
		Class<?> c;
		List<MethodContainer> mcs = new ArrayList<>();

		public ServiceContainer(Class<?> c) {
			this.c = c;
			for (Method m : c.getDeclaredMethods()) {
				if (m.getName().startsWith("multiple")) {
					continue;
				}
				mcs.add(new MethodContainer(m));
			}
			Collections.sort(mcs, new Comparator<MethodContainer>() {
				@Override
				public int compare(MethodContainer o1, MethodContainer o2) {
					return o1.m.getName().compareTo(o2.m.getName());
				}
			});
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(c.getName());
			sb.append(" { ");
			for (MethodContainer mc : mcs) {
				sb.append(" ").append(mc.toString()).append(", ");
			}
			sb.append(" } ");
			return sb.toString();
		}
	}

	public static void main(String[] args) {
		System.out.println("extracting services from classpath");
		new ServiceFromClasspathExtractor();
	}

	public ServiceFromClasspathExtractor() {
		extract();
	}

	public void extract() {
		List<ServiceContainer> serviceContainers = new ArrayList<>();
		try {
			for (Resource r : new PathMatchingResourcePatternResolver()
					.getResources("classpath*:/com/mincom/enterpriseservice/ellipse/*/*Service.class")) {
				Class<?> c = getClass(r.getURL());
				if (!c.isInterface()) {
					continue;
				}
				serviceContainers.add(new ServiceContainer(c));
			}
			for (Resource r : new PathMatchingResourcePatternResolver()
					.getResources("classpath*:/com/mincom/ellipse/service/m*/*/*Service.class")) {
				Class<?> c = getClass(r.getURL());
				if (!c.isInterface()) {
					continue;
				}
				serviceContainers.add(new ServiceContainer(c));
			}
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		Collections.sort(serviceContainers, new Comparator<ServiceContainer>() {
			@Override
			public int compare(ServiceContainer o1, ServiceContainer o2) {
				return o1.c.getName().compareTo(o2.c.getName());
			}
		});
		print(serviceContainers);
	}

	private Class<?> getClass(URL u) throws ClassNotFoundException {
		String s = u.toString();
		s = s.substring(s.indexOf("!/") + 2);
		s = s.substring(0, s.indexOf(".class"));
		s = s.replaceAll("\\/", ".");
		Class<?> c = Class.forName(s);
		return c;
	}

	private void print(List<ServiceContainer> serviceContainers) {
		for (ServiceContainer sc : serviceContainers) {
			System.out.println(sc);
		}
	}

}
