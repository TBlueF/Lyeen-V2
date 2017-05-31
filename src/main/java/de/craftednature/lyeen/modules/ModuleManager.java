package de.craftednature.lyeen.modules;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import de.craftednature.lyeen.LyeenPlugin;

public class ModuleManager {

	public Map<Class<? extends Module>, ModuleContainer> modules;
	
	public ModuleManager() {
		modules = new ConcurrentHashMap<>();
	}
	
	/**
	 * Tries to start all modules in an order so that every module has its dependencies already loaded and injected.<br>
	 * {@link Module}s are only loaded if all their dependencies are available and able to load.<br>
	 * <br>
	 * {@link Module}s that are already initialized will not be initialized again.<br>
	 * {@link Module}s that are already started will not be started again.<br>
	 * <br>
	 * Modules that cannot be started <i>(due to an error)</i>, will be removed.
	 */
	public void startAll(){
		
		//init all modules
		Set<ModuleContainer> stack = new HashSet<>();
		stack.addAll(modules.values());
		
		stackLoop:
		while(!stack.isEmpty()){
			Iterator<ModuleContainer> stackIterator = stack.iterator();
			
			while (stackIterator.hasNext()){
				ModuleContainer mc = stackIterator.next();
				if (mc.hasAllDependenciesSet()){
					
					try {
						if (mc.getState() == ModuleState.CONSTRUCTED) mc.initModule();
						stack.forEach(c -> c.offerDependency(mc.getModule()));
					} catch (Throwable t){
						modules.remove(mc.getType());
						LyeenPlugin.getLogger().error("Failed to initialize module: " + mc.getModule().getName(), t);
					}

					stackIterator.remove();
					continue stackLoop;
				}
			}
			
			//if the iterator finished without processing any module
			for (ModuleContainer mc : stack){
				LyeenPlugin.getLogger().warn("Could not initialize module: " + mc.getModule().getName() + ", because it is missing the following modules: ", listMissingModules(mc));
				modules.remove(mc.getType());
			}
			
			stack.clear();
		}
		
		//inject all @Uses fields
		modules.values().forEach(mc -> modules.values().forEach(o -> mc.offerToUse(o.getModule())));
		
		//start all modules
		modules.values().forEach(mc -> {
			if (mc.getState() == ModuleState.INITIALIZED) mc.startModule();
		});
	}
	
	private String listMissingModules(ModuleContainer mc){
		Collection<Class<? extends Module>> deps = mc.getDependencies();
		deps.removeAll(modules.keySet());
		
		return StringUtils.join(deps.stream().map(d -> d.getName()).iterator(), ",");
	}
	
	/**
	 * Tries to start the {@link Module} <i>(initialize, load and start)</i>.<br>
	 * The {@link Module} is only loaded if all dependencies are available.<br>
	 * <br>
	 * If the {@link Module} cannot be started <i>(due to an error)</i>, it will be removed.
	 * 
	 * @throws IllegalStateException If there is no {@link Module} present with this type
	 * @return <code>true</code> if the module could be started and <code>false</code> otherwise!
	 */
	public boolean start(Class<? extends Module> moduleType){
		ModuleContainer mc = modules.get(moduleType);
		if (mc == null) throw new IllegalStateException("Failed to start module! The module is not present: " + moduleType.getCanonicalName());

		try {
			modules.values().forEach(c -> mc.offerDependency(c.getModule()));
			if (!mc.hasAllDependenciesSet()) throw new IllegalStateException("Could not initialize module: " + mc.getModule().getName() + "! Dependencies missing: " + listMissingModules(mc));
		
			mc.initModule();

			modules.values().forEach(c -> mc.offerToUse(c.getModule()));
			modules.values().forEach(c -> c.offerToUse(mc.getModule()));
			
			mc.startModule();
			return true;
			
		} catch (Throwable t){
			modules.remove(mc.getType());
			LyeenPlugin.getLogger().error("Failed to initialize module: " + mc.getModule().getName(), t);
			
			return false;
		}
	}
	
	/**
	 * Tries to stop all modules in an order so that every module that is beeing stopped has all its dependencies still loaded.
	 */
	public void stopAll(){
		Set<ModuleContainer> toBeStopped = new HashSet<>(modules.values());
		while (!toBeStopped.isEmpty()){
			Set<ModuleContainer> stoppable = new HashSet<>(toBeStopped);
			for (ModuleContainer mc : toBeStopped){
				stoppable.removeIf(rem -> mc.getDependencies().contains(rem.getType()));
			}

			if (stoppable.isEmpty()){
				LyeenPlugin.getLogger().warn("Can't stop all modules, without stopping a dependency of a still loaded module! (" + StringUtils.join(toBeStopped.stream().map(d -> d.getModule().getName()).iterator(), ",") + ")");
				for (ModuleContainer mc : toBeStopped) mc.stopModule();
				toBeStopped.clear();
			}
			
			for (ModuleContainer mc : stoppable){
				mc.stopModule();
				toBeStopped.remove(mc);
			}
		}
	}
	
	/**
	 * Tries to load every module that is {@link ModuleState#INITIALIZED} or {@link ModuleState#STARTED}
	 */
	public void loadAll(){
		modules.values().forEach(m -> {
			try {
				if (m.getState() == ModuleState.INITIALIZED || m.getState() == ModuleState.STARTED) m.getModule().load();
			} catch (IOException ex){
				LyeenPlugin.getLogger().error("Exception trying to load module: " + m.getModule().getName(), ex);
			}
		});
	}

	/**
	 * Tries to load every module that is {@link ModuleState#INITIALIZED} or {@link ModuleState#STARTED}
	 */
	public void saveAll(){
		modules.values().forEach(m -> {
			try {
				if (m.getState() == ModuleState.INITIALIZED || m.getState() == ModuleState.STARTED) m.getModule().save();
			} catch (IOException ex){
				LyeenPlugin.getLogger().error("Exception trying to save module: " + m.getModule().getName(), ex);
			}
		});
		
	}
	
	/**
	 * Adds a {@link Module} to this manager.<br>
	 * <i>(The module will not get initialized, loaded or started)</i>
	 * 
	 * @throws IllegalStateException If there is already a {@link Module} with this type present.
	 */
	public void addModule(Module module){
		ModuleContainer container = new ModuleContainer(module);
		if (modules.containsKey(container.getType())) throw new IllegalStateException("This module is already present: " + module.getName());
		
		modules.put(container.getType(), container);
	}

	/**
	 * Removes a {@link Module} from this manager.<br>
	 * <i>(If the module is started, it will be saved and stopped)</i>
	 * 
	 * @throws IllegalStateException If there is no {@link Module} with that type.
	 */
	public void removeModule(Class<? extends Module> moduleType){
		ModuleContainer mc = modules.remove(moduleType);
		if (mc == null) throw new IllegalStateException("Failed to remove module! The module is not present: " + moduleType.getCanonicalName());
		
		if (mc.getState() == ModuleState.STARTED) mc.stopModule();
	}
	
	/**
	 * Returns an {@link Optional} with the {@link Module}-instance of this module type, or an absent {@link Optional} if there is no {@link Module} with that type.
	 */
	public Optional<Module> getModule(Class<? extends Module> moduleType){
		ModuleContainer mc = modules.get(moduleType);
		if (mc == null) return Optional.empty();
		return Optional.of(modules.get(moduleType).getModule());
	}
	
}
