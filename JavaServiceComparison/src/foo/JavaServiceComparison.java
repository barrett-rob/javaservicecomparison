package foo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JavaServiceComparison {

	public static void main(String[] args) {
		if (args.length < 2) {
			throw new IllegalArgumentException("expected 2 arguments");
		}
		String fromPath = args[0];
		File from = new File(fromPath);
		if (!from.exists()) {
			throw new IllegalArgumentException(fromPath + " does not exist");
		}
		if (!from.isDirectory()) {
			throw new IllegalArgumentException(fromPath
					+ " must be a directory");
		}
		String toPath = args[1];
		File to = new File(toPath);
		if (!to.exists()) {
			throw new IllegalArgumentException(toPath + " does not exist");
		}
		if (!to.isDirectory()) {
			throw new IllegalArgumentException(toPath + " must be a directory");
		}
		long start = System.currentTimeMillis();
		try {
			new JavaServiceComparison(from, to);
		} catch (Error e) {
			e.printStackTrace();
			throw e;
		}
		long elapsed = System.currentTimeMillis() - start;
		System.out.println("elapsed time: " + elapsed + "ms");
	}

	public JavaServiceComparison(File from, File to) {
		List<String> fromJars = new EARJarLister(from).list();
		List<String> fromExtracted = runExtraction(from.getName(), fromJars);
		for (String s : fromExtracted) {
			System.out.println(s);
		}
		List<String> toJars = new EARJarLister(to).list();
		List<String> toExtracted = runExtraction(to.getName(), toJars);
		for (String s : toExtracted) {
			System.out.println(s);
		}
	}

	private List<String> runExtraction(String name, List<String> jars) {
		StringBuilder sb = new StringBuilder();
		for (String s : jars) {
			if (s.contains("logback")) {
				continue;
			}
			sb.append(s).append(":");
		}
		sb.append(ServiceFromClasspathExtractor.class.getProtectionDomain()
				.getCodeSource().getLocation().getPath());
		sb.append(":/Users/robertb/glassfish4/glassfish/lib/javaee.jar");
		String jvm = new File(new java.io.File(System.getProperty("java.home"),
				"bin"), "java").getAbsolutePath();
		ProcessBuilder pb = new ProcessBuilder(jvm, "-XX:MaxPermSize=1024M", "-Xmx1024M", "-classpath",
				sb.toString(), ServiceFromClasspathExtractor.class.getName());
		pb.inheritIO();
		try {
			File out = new File("/Users/robertb/Downloads/" + name + ".txt");
			pb.redirectOutput(out);
			Process p = pb.start();
			p.waitFor();
			List<String> results = new ArrayList<String>();
			getResultsFromFile(out, results);
			return results;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void getResultsFromFile(File out, List<String> results)
			throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(out));
		String line;
		while ((line = br.readLine()) != null) {
			results.add(line);
		}
		br.close();
	}
}
