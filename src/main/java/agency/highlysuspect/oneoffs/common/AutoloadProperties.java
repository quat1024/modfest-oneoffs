package agency.highlysuspect.oneoffs.common;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.Util;
import org.slf4j.Logger;

public class AutoloadProperties<T> {
	public AutoloadProperties(Logger log, Path propsPath, Supplier<T> defaultState, BiConsumer<T, Properties> toProperties, Function<Properties, T> fromProperties, String... comments) {
		this.log = log;
		this.propsPath = propsPath;
		this.defaultState = defaultState;
		this.toProperties = toProperties;
		this.fromProperties = fromProperties;
		this.comments = String.join("\n", comments);
	}
	
	protected final Logger log;
	protected final Path propsPath;
	protected final Supplier<T> defaultState;
	protected final BiConsumer<T, Properties> toProperties;
	protected final Function<Properties, T> fromProperties;
	protected final String comments;

	//filewatcher debouncing
	protected long filewatcherDebounce = 0;
	protected static final int DEBOUNCE_MS = 300;

	//hard-save debouncing - we want to ignore filewatcher changes that
	//happen after saving the file
	protected volatile long lastIngameSave = 0;
	protected static final int SAVE_TIMEOUT_MS = 10000;

	public void saveNow(T state) {
		//create the default config file based on the current state
		Properties props = new Properties();
		toProperties.accept(state, props);
		
		//we're about to trigger the filewatcher by saving the file
		filewatcherDebounce = System.currentTimeMillis();
		lastIngameSave = System.currentTimeMillis();
		
		//save it
		log.info("Saving config to {}", propsPath);
		try(Writer writer = Files.newBufferedWriter(propsPath, StandardCharsets.UTF_8)) {
			props.store(writer, comments);
		} catch (Throwable e) {
			log.error("Failed to save config file to {}", propsPath, e);
		}
	}
	
	@SuppressWarnings("resource") //bro we are not closing this executor
	public void saveLater(T state) {
		Util.ioPool().submit(() -> saveNow(state));
	}
	
	public void load(Consumer<T> stateUpdater) {
		try {
			if(Files.notExists(propsPath)) {
				log.info("Creating default config file at {}", propsPath);
				saveNow(defaultState.get());
				return;
			}
			
			try(Reader reader = Files.newBufferedReader(propsPath, StandardCharsets.UTF_8)) {
				log.info("Parsing config file at {}", propsPath);
				//load the properties
				Properties props = new Properties();
				props.load(reader);
				
				//create the state
				T newState = fromProperties.apply(props);
				
				log.info("Looks good, loading it on-thread...");
				stateUpdater.accept(newState);
				
				//if it didn't load as the user intended, re-serialize it from scratch
				Properties savebackProps = new Properties();
				toProperties.accept(newState, savebackProps);
				if(!props.equals(savebackProps)) {
					log.info("Correcting config file at {}", propsPath);
					saveNow(newState);
				}
			}
		} catch (Throwable e) {
			log.error("Failed to load config file from {}", propsPath, e);
		}
	}
	
	public void watch(Consumer<T> stateUpdater) {
		SharedFileWatcher.registerWithWatcher(propsPath, () -> {
			long lastFilewatcherDebounce = filewatcherDebounce;
			long now = System.currentTimeMillis();
			filewatcherDebounce = now;

			if(now - lastFilewatcherDebounce < DEBOUNCE_MS) {
				log.info("only been {}ms since last filewatcher, ignoring", now - lastFilewatcherDebounce);
			} else if(now - lastIngameSave < SAVE_TIMEOUT_MS) {
				log.info("only been {}ms since last in-game save, ignoring", now - lastIngameSave);
			} else {
				load(stateUpdater);
			}
		});
	}
}
