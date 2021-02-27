package cf.phoenixfirewingz.remapper.utils;

import java.io.*;
import java.util.Map;

public class CommonUtils
{
	protected static String remapMethodDescriptor(String method, Map<String, String> classMappings) {
		try {
			Reader r = new StringReader(method);
			StringBuilder result = new StringBuilder();
			boolean started = false;
			boolean insideClassName = false;
			StringBuilder className = new StringBuilder();
			while (true) {
				int c = r.read();
				if (c == -1) {
					break;
				}

				if (c == ';') {
					insideClassName = false;
					result.append(classMappings.getOrDefault(
							className.toString(),
							className.toString()
					));
				}

				if (insideClassName) {
					className.append((char) c);
				}
				else {
					result.append((char) c);
				}

				if (c == '(') {
					started = true;
				}

				//qouteall changed
				if (started && c == 'L' && !insideClassName) {
					insideClassName = true;
					className.setLength(0);
				}
			}

			return result.toString();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}
}
