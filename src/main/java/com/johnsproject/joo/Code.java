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
	 * Returns the number of {@link CodeCompoent} with the specified name.
	 * <br><br>
	 * This method does not search in the component lists of this CodeComponent's components.
	 * 
	 * @param name of the component.
	 * @return The number of components with the specified name.
	 */
	public int getComponentWithNameCount(String name) {
		int count = 0;
		for (CodeComponent component : components) {
			if(component.hasName(name))
				count++;
		}
		return count;
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
	
	/**
	 * Returns the {@link CodeCompoent} with the specified type, 
	 * if it's found in the components list of this CodeComponent, or null.
	 * <br><br>
	 * This method does not search in the component lists of this CodeComponent's components.
	 * 
	 * @param type of the component.
	 * @return The component with the specified name.
	 */
	public CodeComponent getComponentWithType(String type) {
		for (CodeComponent component : components) {
			if(component.hasType(type))
				return component;
		}
		return null;
	}	
	
	/**
	 * Returns the {@link CodeCompoent} of the type with the specified index, 
	 * if it's found in the components list of this CodeComponent, or null.
	 * 
	 * @param type of the component.
	 * @param index of the component.
	 * @return The component with the specified name.
	 */
	public CodeComponent getComponentWithType(String type, int index) {
		int i = 0;
		for (CodeComponent component : components) {
			if(component.hasType(type) && (i++ == index))
				return component;
		}
		return null;
	}
	
	/**
	 * Returns the number of {@link CodeCompoent} with the specified type.
	 * <br><br>
	 * This method does not search in the component lists of this CodeComponent's components.
	 * 
	 * @param type of the component.
	 * @return The number of components with the specified type.
	 */
	public int getComponentWithTypeCount(String type) {
		int count = 0;
		for (CodeComponent component : components) {
			if(component.hasType(type))
				count++;
		}
		return count;
	}
	
	/**
	 * Does this {@link CodeCompoent} have a component with the specified type?
	 * <br><br>
	 * This method does not search in the component lists of this CodeComponent's components.
	 * 
	 * @param type of the component.
	 * @return If there is a component with the specified type.
	 */
	public boolean hasComponentWithType(String type) {
		return getComponentWithType(type) != null;
	}
	
	/**
	 * Returns the {@link CodeCompoent} with the specified name and type.
	 * <br><br>
	 * This method does not search in the component lists of this CodeComponent's components.
	 * 
	 * @param name of the component.
	 * @param type of the component.
	 * @return The component with the specified name and type.
	 */
	public CodeComponent getComponent(String name, String type) {
		for (CodeComponent component : components) {
			if(component.hasName(name) && component.hasType(type))
				return component;
		}
		return null;
	}
	
	/**
	 * Returns the number of {@link CodeCompoent} with the specified name and type.
	 * <br><br>
	 * This method does not search in the component lists of this CodeComponent's components.
	 * 
	 * @param name of the component.
	 * @param type of the component.
	 * @return The number of components with the specified name and type.
	 */
	public int getComponentCount(String name, String type) {
		int count = 0;
		for (CodeComponent component : components) {
			if(component.hasName(name) && component.hasType(type))
				count++;
		}
		return count;
	}
	
	/**
	 * Does this {@link CodeCompoent} have a component with the specified name and type?
	 * <br><br>
	 * This method does not search in the component lists of this CodeComponent's components.
	 * 
	 * @param name of the component.
	 * @param type of the component.
	 * @return If there is a component with the specified name and type.
	 */
	public boolean hasComponent(String name, String type) {
		return getComponent(name, type) != null;
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
