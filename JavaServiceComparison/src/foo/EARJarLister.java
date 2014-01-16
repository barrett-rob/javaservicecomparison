package foo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EARJarLister {

	private final File ear;

	public EARJarLister(File ear) {
		this.ear = ear;
		System.out.println("extracting jars from: " + ear);
	}

	public List<String> list() {
		return list(ear);
	}

	private List<String> list(File dir) {
		List<String> jars = new ArrayList<String>();
		File[] fs = dir.listFiles();
		for (File f : fs) {
			if (f.isDirectory()) {
				jars.addAll(list(f));
			} else {
				if (f.getName().endsWith("jar")) {
					jars.add(f.getAbsolutePath());
				}
			}
		}
		return jars;
	}

}
