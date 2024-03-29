package com.johnsproject.joo;

import java.util.ArrayList;
import java.util.List;

/**
 * The Instruction class contains the data of a joo instruction (like if it's an operation or function call).
 * 
 * @author John Ferraz Salomon
 *
 */
public class Instruction {
	
	private boolean isCondition;
	private String conditionType;
	
	private boolean hasVariable0;
	private String variable0Name;
	private boolean hasVariable0ArrayIndex;
	private String variable0ArrayIndex;
	
	private char operator;
	
	private boolean hasVariable1;
	private String variable1Name;
	private boolean hasVariable1ArrayIndex;
	private String variable1ArrayIndex;

	private boolean hasValue;
	private String value;
	private String valueType;

	private boolean isFunctionCall;
	private String functionName;
	private List<String> parameters;

	public Instruction() {
		isCondition = false;
		conditionType = "";
		hasVariable0 = false;
		variable0Name = "";
		hasVariable0ArrayIndex = false;
		variable0ArrayIndex = "";
		operator = 0;
		hasVariable1 = false;
		variable1Name = "";
		hasVariable1ArrayIndex = false;
		variable1ArrayIndex = "";
		hasValue = false;
		value = "";
		valueType = "";
		isFunctionCall = false;
		functionName = "";
		parameters = new ArrayList<>();
	}
	
	public boolean isCondition() {
		return isCondition;
	}

	public void isCondition(boolean isCondition) {
		this.isCondition = isCondition;
	}

	public String getConditionType() {
		return conditionType;
	}

	public void setConditionType(String conditionType) {
		this.conditionType = conditionType;
	}

	public boolean hasVariable0() {
		return hasVariable0;
	}

	public void hasVariable0(boolean hasVariable0) {
		this.hasVariable0 = hasVariable0;
	}

	public String getVariable0Name() {
		return variable0Name;
	}

	public void setVariable0Name(String variable0Name) {
		this.variable0Name = variable0Name;
	}

	public boolean hasVariable0ArrayIndex() {
		return hasVariable0ArrayIndex;
	}

	public void hasVariable0ArrayIndex(boolean hasVariable0ArrayIndex) {
		this.hasVariable0ArrayIndex = hasVariable0ArrayIndex;
	}

	public String getVariable0ArrayIndex() {
		return variable0ArrayIndex;
	}

	public void setVariable0ArrayIndex(String variable0ArrayIndex) {
		this.variable0ArrayIndex = variable0ArrayIndex;
	}

	public char getOperator() {
		return operator;
	}

	public void setOperator(char operator) {
		this.operator = operator;
	}

	public boolean hasVariable1() {
		return hasVariable1;
	}

	public void hasVariable1(boolean hasVariable1) {
		this.hasVariable1 = hasVariable1;
	}

	public String getVariable1Name() {
		return variable1Name;
	}

	public void setVariable1Name(String variable1Name) {
		this.variable1Name = variable1Name;
	}

	public boolean hasVariable1ArrayIndex() {
		return hasVariable1ArrayIndex;
	}

	public void hasVariable1ArrayIndex(boolean hasVariable1ArrayIndex) {
		this.hasVariable1ArrayIndex = hasVariable1ArrayIndex;
	}

	public String getVariable1ArrayIndex() {
		return variable1ArrayIndex;
	}

	public void setVariable1ArrayIndex(String variable1ArrayIndex) {
		this.variable1ArrayIndex = variable1ArrayIndex;
	}

	public boolean hasValue() {
		return hasValue;
	}

	public void hasValue(boolean hasValue) {
		this.hasValue = hasValue;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	public boolean isFunctionCall() {
		return isFunctionCall;
	}

	public void isFunctionCall(boolean isFunctionCall) {
		this.isFunctionCall = isFunctionCall;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}

	public void addParameter(String parameter) {
		parameters.add(parameter);
	}	

	public List<String> getParameters() {
		return parameters;
	}		
}
