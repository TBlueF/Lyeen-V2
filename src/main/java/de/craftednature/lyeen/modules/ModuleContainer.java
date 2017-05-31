/*
 * The MIT License (MIT)
 * 
 * Copyright (c) Blue <https://www.bluecolored.de>
 * Copyright (c) CraftedNature <https://www.craftednature.de>
 * Copyright (c) contributors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.craftednature.lyeen.modules;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import de.craftednature.lyeen.LyeenPlugin;

/**
 * Wrapper class to keep track of the state of a {@link Module} and manage it.<br>
 * Used by the {@link ModuleManager}.
 */
public class ModuleContainer {
	
	private Module module;
	private ModuleState state;
	
	public ModuleContainer(Module module) {
		this.module = module;
		this.state = ModuleState.CONSTRUCTED;
	}
	
	public Module getModule(){
		return module;
	}
	
	public Class<? extends Module> getType(){
		return module.getClass();
	}
	
	public ModuleState getState(){
		return state;
	}
	
	/**
	 * Initializes this module and loads it.
	 * 
	 * @throws IllegalStateException If the modules state is not {@link ModuleState#CONSTRUCTED}
	 * @throws Throwable If anything goes wrong during initialisation. This is the exception thrown by {@link Module#init()}.
	 */
	public void initModule() throws IllegalStateException, Throwable {
		Preconditions.checkState(state == ModuleState.CONSTRUCTED, "Module is already initialized! Expected state CONSTRUCTED but is " + state);
		
		module.init();
		
		try {
			module.load();
		} catch (IOException ex){
			LyeenPlugin.getLogger().error("Exception trying to load module: " + module.getName(), ex);
		}
		
		state = ModuleState.INITIALIZED;
	}

	/**
	 * Starts this module.
	 * 
	 * @throws IllegalStateException If the modules state is not {@link ModuleState#INITIALIZED}
	 */
	public void startModule() throws IllegalStateException {
		Preconditions.checkState(state == ModuleState.INITIALIZED, "Module is either not initialized, or already started! Expected state INITIALIZED but is " + state);
		
		module.start();
		
		state = ModuleState.STARTED;
	}
	
	/**
	 * Stops this module.
	 * 
	 * @throws IllegalStateException If the modules state is not {@link ModuleState#STARTED}
	 */
	public void stopModule() throws IllegalStateException {
		Preconditions.checkState(state == ModuleState.STARTED, "Module is either not started, or already stopped! Expected state INITIALIZED but is " + state);
		
		try {
			module.save();
		} catch (IOException ex){
			LyeenPlugin.getLogger().error("Exception trying to save module: " + module.getName(), ex);
		}
		
		module.stop();
		
		state = ModuleState.STOPPED;
	}
	
	/**
	 * Reads all fields of the {@link Module} and returns the types of those fields that are annotated with {@link Depends}.
	 *  
	 * @return a collection of all module-types this module depends on
	 */
	@SuppressWarnings("unchecked")
	public Collection<Class<? extends Module>> getDependencies(){
		return getModuleFields(module.getClass(), Depends.class).stream()
				.map(f -> (Class<? extends Module>) f.getType())
				.collect(Collectors.toSet());
	}

	/**
	 * Reads all fields of the {@link Module} and returns the types of those fields that are annotated with {@link Uses}.
	 *  
	 * @return a collection of all module-types this module uses
	 */
	@SuppressWarnings("unchecked")
	public Collection<Class<? extends Module>> getUsedModules(){
		return getModuleFields(module.getClass(), Uses.class).stream()
				.map(f -> (Class<? extends Module>) f.getType())
				.collect(Collectors.toSet());
	}
	
	/**
	 * Tests if every depenency field of this {@link Module} is set (is not <code>null</code>).
	 * @return <code>true</code> if all dependencies are set and false otherwise
	 */
	public boolean hasAllDependenciesSet(){
		return getModuleFields(module.getClass(), Depends.class).stream()
			.allMatch(f -> {
				try {
					f.setAccessible(true);
					return f.get(module) != null;
				} catch (SecurityException | IllegalArgumentException | IllegalAccessException ex){
					LyeenPlugin.getLogger().error("Could not access dependency field! (Field " + f.getName() + " of " + f.getType().getName() + ") ", ex);
					return false;
				}
			});
	}
	
	/**
	 * Tests if every with {@link Uses} annotated field of this {@link Module} is set (is not <code>null</code>).
	 * @return <code>true</code> if all fields are set and false otherwise
	 */
	public boolean hasAllUsedModulesSet(){
		return getModuleFields(module.getClass(), Uses.class).stream()
			.allMatch(f -> {
				try {
					f.setAccessible(true);
					return f.get(module) != null;
				} catch (SecurityException | IllegalArgumentException | IllegalAccessException ex){
					LyeenPlugin.getLogger().error("Could not access field! (Field " + f.getName() + " of " + f.getType().getName() + ") ", ex);
					return false;
				}
			});
	}
	
	/**
	 * Checks if this {@link Module} needs the parameter {@link Module} as an dependency and if it does, injects the value.
	 * 
	 * @param offer the {@link Module} instance to offer
	 */
	public void offerDependency(Module offer){
		for(Field f : getModuleFields(module.getClass(), Depends.class)){
			try {
				f.setAccessible(true);
				if (f.getType().isInstance(offer)){
					if (f.get(module) == null){
						f.set(module, offer);
					}
				}
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException ex){
				LyeenPlugin.getLogger().error("Could not inject module! (Field " + f.getName() + " of " + f.getType().getName() + ") ", ex);
			}
		}
	}
	
	/**
	 * Checks if this {@link Module} uses the parameter {@link Module} and if it does, injects the value.
	 * 
	 * @param offer the {@link Module} instance to offer
	 */
	public void offerToUse(Module offer){
		for(Field f : getModuleFields(module.getClass(), Uses.class)){
			try {
				f.setAccessible(true);
				if (f.getType().isInstance(offer)){
					if (f.get(module) == null){
						f.set(module, offer);
					}
				}
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException ex){
				LyeenPlugin.getLogger().error("Could not inject module! (Field " + f.getName() + " of " + f.getType().getName() + ") ", ex);
			}
		}
	}
	
	public void removeDependencyOrUse(Module module){
		
	}
	
	private Collection<Field> getModuleFields(Class<?> type, Class<?> annotation){
		ArrayList<Field> depList = new ArrayList<>();
		
		Class<?> parent = type.getSuperclass();
		if (parent != null) depList.addAll(getModuleFields(parent, annotation));
		
		for (Field f : type.getDeclaredFields()){
			try {
				f.setAccessible(true);
				if (f.getAnnotation(Depends.class) != null){
					if (Module.class.isAssignableFrom(f.getType())) depList.add(f);
				}
			} catch (SecurityException ex){
				LyeenPlugin.getLogger().warn("Could not access field " + f.getName() + " of " + type.getName() + ": " + ex);
			}
		}
		
		return depList;
	}
	
}