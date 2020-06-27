package com.johnsproject.joo;

import java.util.List;

class Setting extends Settings {

	private final String name;
	private final byte byteCodeName;
	private final String type;
	
	public Setting(String name, byte byteCodeName, String type) {
		this.name = name;
		this.byteCodeName = byteCodeName;
		this.type = type;
	}
	
	public Setting(String name, byte byteCodeName, String type, List<Setting> settings) {
		super(settings);
		this.name = name;
		this.byteCodeName = byteCodeName;
		this.type = type;
	}

	public String getName() {
		return name;
	}
	
	public boolean hasName(String name) {
		return this.name.equals(name);
	}

	public byte getByteCodeName() {
		return byteCodeName;
	}
	
	public boolean hasName(byte name) {
		return byteCodeName == name;
	}

	public String getType() {
		return type;
	}

	public boolean hasType(String type) {
		return this.type.equals(type);
	}

	@Override
	public String toString() {
		return "Setting [name=" + name + ", byteCodeName=" + byteCodeName +
				", type=" + type + ", settings=" + getSettings() + "]";
	}
	
	@Override
	protected Setting clone() {
		final Settings clonedSettings = super.clone();
		return new Setting(name, byteCodeName, type, clonedSettings.getSettings());
	}
}
