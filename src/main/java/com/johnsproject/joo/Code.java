package com.johnsproject.joo;

import java.util.ArrayList;
import java.util.List;

class Code {

	private final List<CodeComponent> components;
	
	public Code() {
		components = new ArrayList<CodeComponent>();
	}
	
	protected Code(List<CodeComponent> components) {
		this.components = components;
	}

	public List<CodeComponent> getComponents() {
		return components;
	}

	public CodeComponent getComponent(int index) {
		return components.get(index);
	}
	
	public void addComponent(CodeComponent component) {
		components.add(component);
	}
	
	public void removeComponent(CodeComponent component) {
		components.remove(component);
	}
	
	/**
	 * Returns the {@link CodeCompoent} with the specified name, 
	 * if it's found in the components list of this CodeComponent, or null.
	 * <br><br>
	 * This method does not search in the component lists of this CodeComponent's components.
	 * 
	 * @param name of the component.
	 * @return The component with the specified name.
	 */
	public CodeComponent getComponentWithName(String name) {
		for (CodeComponent component : components) {
			if(component.hasName(name))
				return component;
		}
		return null;
	}
	/**
	 * Does this {@link CodeCompoent} have a component with the specified name?
	 * <br><br>
	 * This method does not search in the component lists of this CodeComponent's components.
	 * 
	 * @param name of the component.
	 * @return If there is a component with the specified name.
	 */
	public boolean hasComponentWithName(String name) {
		return getComponentWithName(name) != null;
	}
	
	/**
	 * Returns the {@link CodeCompoent} with the specified byte code name, 
	 * if it's found in the components list of this CodeComponent, or null.
	 * <br><br>
	 * This method does not search in the component lists of this CodeComponent's components.
	 * 
	 * @param name Byte code name of the component.
	 * @return The component with the specified name.
	 */
	public CodeComponent getComponentWithName(byte name) {
		for (CodeComponent component : components) {
			if(component.hasName(name))
				return component;
		}
		return null;
	}
	
	/**
	 * Does this {@link CodeCompoent} have a component with the specified byte code name?
	 * <br><br>
	 * This method does not search in the component lists of this CodeComponent's components.
	 * 
	 * @param name Byte code name of the component.
	 * @return If there is a component with the specified name.
	 */
	public boolean hasComponentWithName(byte name) {
		return getComponentWithName(name) != null;
	}

	@Override
	public String toString() {
		return "Code [components=" + components + "]";
	}

	@Override
	protected Code clone() {
		final List<CodeComponent> clonedComponents = new ArrayList<CodeComponent>();
		for (CodeComponent codeComponent : components) {
			clonedComponents.add(codeComponent.clone());
		}
		return new Code(clonedComponents);
	}
}
