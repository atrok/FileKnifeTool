package garbagecleaner;

import java.io.File;

public interface Strategy {
	void process(File file);
}
