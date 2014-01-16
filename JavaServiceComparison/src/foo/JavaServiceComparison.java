package foo;

import java.io.File;
import java.util.List;

public class JavaServiceComparison {

	private final File from;
	private final File to;

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

		new JavaServiceComparison(from, to).run();

	}

	public JavaServiceComparison(File from, File to) {
		this.from = from;
		this.to = to;
		List<String> fromJars = new EARJarLister(from).list();
		System.out.println(fromJars);
		List<String> toJars = new EARJarLister(to).list();
		System.out.println(toJars);
	}

	private void run() {
	}

}
