package com.johnsproject.joo;

import java.util.List;

class CodeComponent extends Code {

	private final String name;
	private final byte byteCodeName;
	private final String type;
	private final byte byteCodeType;
	private final int lineIndex;
	
	public CodeComponent(String name, byte byteCodeName, String type, byte byteCodeType, int lineIndex) {
		this.name = name;
		this.byteCodeName = byteCodeName;
		this.type = type;
		this.byteCodeType = byteCodeType;
		this.lineIndex = lineIndex;
	}
	
	private CodeComponent(String name, byte byteCodeName, String type, byte byteCodeType, int lineIndex, List<CodeComponent> components) {
		super(components);
		this.name = name;
		this.byteCodeName = byteCodeName;
		this.type = type;
		this.byteCodeType = byteCodeType;
		this.lineIndex = lineIndex;
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

	public int getLineIndex() {
		return lineIndex;
	}

	@Override
	public String toString() {
		return "CodeComponent [name=" + name + ", byteCodeName=" + byteCodeName + ", type=" + type +
				", byteCodeType=" + byteCodeType + ", components=" + getComponents() + "]";
	}

	@Override
	protected CodeComponent clone() {
		final Code clonedCode = super.clone();
		return new CodeComponent(name, byteCodeName, type, byteCodeType, lineIndex, clonedCode.getComponents());
	}
}
