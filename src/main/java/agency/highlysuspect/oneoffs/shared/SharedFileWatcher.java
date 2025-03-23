package agency.highlysuspect.oneoffs.shared;

import java.nio.file.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SharedFileWatcher {
	static final WatchService WATCHER;
	static final Set<Path> watchedDirectories = new HashSet<>();
	static final Map<String, Runnable> actions = new HashMap<>();
	
	static Thread watcherThread;
	
	public static void registerWithWatcher(Path configFile, Runnable action) {
		synchronized(watchedDirectories) { //arbitrary object as a mutex
			Path configDir = configFile.getParent();
			
			if(!watchedDirectories.contains(configDir)) {
				try {
					//in general, you can only register directories with the watch service, not files
					configFile.getParent().register(WATCHER, StandardWatchEventKinds.ENTRY_MODIFY);
				} catch (Exception e) {
					throw new RuntimeException("failed to register " + configDir + " to watcher");
				}
				
				watchedDirectories.add(configDir);
			}
			
			actions.put(configFile.getFileName().toString(), action);
			startWatcherThreadIfNeeded();
		}
	}
	
	public static void startWatcherThreadIfNeeded() {
		if(watcherThread == null)  {
			watcherThread = new Thread("modfest-oneoffs config filewatcher") {
				@Override
				public void run() {
					try {
						while(!interrupted()) {
							//block until there's a new event in this directory
							WatchKey key = WATCHER.take();
							if(!key.isValid()) continue;
							
							//look through the events
							for(WatchEvent<?> event : key.pollEvents()) {
								//did it modify a file?
								if(event.kind() == StandardWatchEventKinds.ENTRY_MODIFY && event.context() instanceof Path path) {
									//do we care about this file?
									Runnable action = actions.get(path.getFileName().toString());
									if(action != null) action.run();
								}
							}
							
							//all done
							key.reset();
						}
					} catch (Throwable e) {
						System.err.println("modfest-oneoffs filewatcher failed!");
						e.printStackTrace();
					}
				}
			};
			watcherThread.setPriority(Thread.MIN_PRIORITY); //be polite
			watcherThread.setDaemon(true); //Don't block JVM shutdown
			watcherThread.start();
		}
	}
	
	static {
		try {
			WATCHER = FileSystems.getDefault().newWatchService();
		} catch (Exception e) {
			throw new RuntimeException("failed to make watchservice", e);
		}
	}
}
