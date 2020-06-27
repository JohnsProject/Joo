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
	
	public Setting getSettingWithName(String name) {
		for (Setting setting : settings) {
			if(setting.hasName(name))
				return setting;
		}
		return null;
	}
	
	public boolean hasSettingWithName(String name) {
		return getSettingWithName(name) != null;
	}
	
	public Setting getSettingWithName(byte name) {
		for (Setting setting : settings) {
			if(setting.hasName(name))
				return setting;
		}
		return null;
	}
	
	public boolean hasSettingWithName(byte name) {
		return getSettingWithName(name) != null;
	}
	
	public Setting getSettingWithType(String type) {
		for (Setting setting : settings) {
			if(setting.hasType(type))
				return setting;
		}
		return null;
	}
	
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
