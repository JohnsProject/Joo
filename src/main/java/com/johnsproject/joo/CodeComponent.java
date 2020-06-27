package com.johnsproject.joo;

import java.util.List;

class CodeComponent extends Code {

	private final String name;
	private final byte byteCodeName;
	private final String type;
	private final byte byteCodeType;
	
	public CodeComponent(String name, byte byteCodeName, String type, byte byteCodeType) {
		this.name = name;
		this.byteCodeName = byteCodeName;
		this.type = type;
		this.byteCodeType = byteCodeType;
	}
	
	public CodeComponent(String name, byte byteCodeName, String type, byte byteCodeType, List<CodeComponent> components) {
		super(components);
		this.name = name;
		this.byteCodeName = byteCodeName;
		this.type = type;
		this.byteCodeType = byteCodeType;
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

	public byte getByteCodeType() {
		return byteCodeType;
	}
	
	public boolean hasType(byte type) {
		return byteCodeType == type;
	}

	@Override
	public String toString() {
		return "CodeComponent [name=" + name + ", byteCodeName=" + byteCodeName + ", type=" + type +
				", byteCodeType=" + byteCodeType + ", components=" + getComponents() + "]";
	}

	@Override
	protected CodeComponent clone() {
		final Code clonedCode = super.clone();
		return new CodeComponent(name, byteCodeName, type, byteCodeType, clonedCode.getComponents());
	}
}
