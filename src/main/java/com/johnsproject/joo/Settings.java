package com.johnsproject.joo;

import java.util.ArrayList;
import java.util.List;

class Settings {

	private final List<Setting> settings;
	
	public Settings() {
		settings = new ArrayList<Setting>();
	}
	
	protected Settings(List<Setting> settings) {
		this.settings = settings;
	}

	public List<Setting> getSettings() {
		return settings;
	}
	
	public Setting getSetting(int index) {
		return settings.get(index);
	}
	
	public void addSetting(Setting setting) {
		settings.add(setting);
	}
	
	public void removeSetting(Setting setting) {
		settings.remove(setting);
	}
	
	/**
	 * Returns the {@link Setting} with the specified name, 
	 * if it's found in the settings list of this Setting, or null.
	 * <br><br>
	 * This method does not search in the settings lists of this Setting's settings.
	 * 
	 * @param name of the setting.
	 * @return The setting with the specified name.
	 */
	public Setting getSettingWithName(String name) {
		for (Setting setting : settings) {
			if(setting.hasName(name))
				return setting;
		}
		return null;
	}
	
	/**
	 * Does this {@link Setting} have a Setting with the specified name?
	 * <br><br>
	 * This method does not search in the settings lists of this Setting's settings.
	 * 
	 * @param name of the setting.
	 * @return If there is a setting with the specified name.
	 */
	public boolean hasSettingWithName(String name) {
		return getSettingWithName(name) != null;
	}
	
	/**
	 * Returns the {@link Setting} with the specified byte code name, 
	 * if it's found in the settings list of this Setting, or null.
	 * <br><br>
	 * This method does not search in the settings lists of this Setting's settings.
	 * 
	 * @param name Byte code name of the setting.
	 * @return The setting with the specified name.
	 */
	public Setting getSettingWithName(byte name) {
		for (Setting setting : settings) {
			if(setting.hasName(name))
				return setting;
		}
		return null;
	}
	
	/**
	 * Does this {@link Setting} have a Setting with the specified byte code name?
	 * <br><br>
	 * This method does not search in the settings lists of this Setting's settings.
	 * 
	 * @param name byte code name of the setting.
	 * @return If there is a setting with the specified name.
	 */
	public boolean hasSettingWithName(byte name) {
		return getSettingWithName(name) != null;
	}
	
	/**
	 * Returns the {@link Setting} with the specified type, 
	 * if it's found in the settings list of this Setting, or null.
	 * <br><br>
	 * This method does not search in the settings lists of this Setting's settings.
	 * 
	 * @param type of the setting.
	 * @return The setting with the specified type.
	 */
	public Setting getSettingWithType(String type) {
		for (Setting setting : settings) {
			if(setting.hasType(type))
				return setting;
		}
		return null;
	}
	
	/**
	 * Does this {@link Setting} have a Setting with the specified type?
	 * <br><br>
	 * This method does not search in the settings lists of this Setting's settings.
	 * 
	 * @param type of the setting.
	 * @return If there is a setting with the specified type.
	 */
	public boolean hasSettingWithType(String type) {
		return getSettingWithType(type) != null;
	}

	@Override
	public String toString() {
		return "Settings [settings=" + settings + "]";
	}
	
	@Override
	protected Settings clone() {
		final List<Setting> clonedSettings = new ArrayList<Setting>();
		for (Setting setting : settings) {
			clonedSettings.add(setting.clone());
		}
		return new Settings(clonedSettings);
	}
}
