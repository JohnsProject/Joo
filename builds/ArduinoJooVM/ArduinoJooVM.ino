  #define TYPE_INT 127
  #define TYPE_FIXED 126
  #define TYPE_BOOL 125
  #define TYPE_CHAR 124
  
  #define TYPE_ARRAY_INT 123
  #define TYPE_ARRAY_FIXED 122
  #define TYPE_ARRAY_BOOL 121
  #define TYPE_ARRAY_CHAR 120
  #define TYPE_FUNCTION 119
  
  #define KEYWORD_IF 118
  #define KEYWORD_ELSE_IF 117
  #define KEYWORD_ELSE 116
  #define KEYWORD_FUNCTION 115
  #define KEYWORD_FUNCTION_CALL 114
  #define KEYWORD_FUNCTION_REPEAT 113
  #define KEYWORD_PARAMETER 112
  
  #define LINE_BREAK 101
  
  #define NUMBER_9 100
  #define NUMBER_8 99
  #define NUMBER_7 98
  #define NUMBER_6 97
  #define NUMBER_5 96
  #define NUMBER_4 95
  #define NUMBER_3 94
  #define NUMBER_2 93
  #define NUMBER_1 92
  #define NUMBER_0 91
  
  #define COMPONENTS_START 1
  #define COMPONENTS_END 65
  #define PARAMETERS_START 66
  #define PARAMETERS_END 72
  #define ARRAY_INDEXES_START 73
  #define ARRAY_INDEXES_END 127
  #define TYPES_START 119
  #define TYPES_END 127
  #define OPERATORS_START 1
  #define OPERATORS_END 127
  #define NATIVE_FUNCTIONS_START 1
  #define NATIVE_FUNCTIONS_END 127
  
  #define FIXED_POINT 8 

  int codeSize = 0;
  byte componentIndexes[9];
  int components[COMPONENTS_END - COMPONENTS_START];
  /* The array memory can have up to int.maxValue length. 
   * The default lenght is for compatibility with the arduino */
  int arrays[(ARRAY_INDEXES_END - ARRAY_INDEXES_START) * 2];
  int parameters[PARAMETERS_END - PARAMETERS_START];
  bool ifs[6];
  /* The code can have up to int.maxValue length. 
   * The default lenght is for compatibility with the arduino */
  char code[490];


#define SOURCE_SERIAL_MONITOR 0 // load byte code from serial port
#define SOURCE_SD 1 // load byte code from sd card

#define BYTE_CODE_SOURCE SOURCE_SD

#if BYTE_CODE_SOURCE == SOURCE_SD
  #include <SPI.h>
  #include <SD.h>
  File file;
  int programStart = 0;
#endif

void setup() {
  Serial.begin(9600);
  while(!Serial);
    
  #if BYTE_CODE_SOURCE == SOURCE_SERIAL_MONITOR
    Serial.println(F("Starting Joo VM... Hello World! XD"));  
    Serial.println(F("Send me some delicious byte code and i'll execute it! :P"));
  #elif BYTE_CODE_SOURCE == SOURCE_SD
    SD.begin(4);
    file = SD.open("bytecode.txt");
    while(programStart >= 0) {
      start();
    }
  #endif
}

void loop() {
  #if BYTE_CODE_SOURCE == SOURCE_SERIAL_MONITOR
    if(Serial.available() > 2) {
      codeSize = 0;
      codeSize |= (Serial.read() >> 8) & 255;  
      codeSize |= Serial.read() & 255;    
      for(int i = 0; i < codeSize;) {
        if(Serial.available() > 0) {
          code[i++] = Serial.read();
        }
      }
      Serial.print(F("Executing code, size: "));
      Serial.println(codeSize);
      start();
    }
  #endif
}
  
  void start() {
    #if BYTE_CODE_SOURCE == SOURCE_SD
      file.seek(programStart);
      codeSize = 0;
      codeSize |= (file.read() >> 8) & 255;  
      codeSize |= file.read() & 255;   
      for(int i = 0; i < codeSize;) {
        code[i++] = file.read();
      } 
      programStart = -1;
    #endif  
    
    initialize();
    interpretFunction((char)componentIndexes[TYPE_FUNCTION - TYPES_START]);
  }

  void initialize() {
    resetVM();
    int codeIndex = 0;
    bool functionFound = false;
    for (int i = 0; i < codeSize; i++) {
      if(code[i] == LINE_BREAK) {
        codeIndex = i + 1;
      }
      else if(code[i] == KEYWORD_FUNCTION) {
        functionFound = true;
      }
      else if (i != 0) {
        continue;
      }
      bool isTypeDeclaration = parseTypeDeclaration(codeIndex);
      if(!isTypeDeclaration) {
        bool isFunctionDeclaration = parseFunctionDeclaration(codeIndex);
        if(!isFunctionDeclaration && !functionFound) {
          parseVariableDeclaration(codeIndex);
        }
      }
    }
  } 
  
  void resetVM() {
    for (int i = 0; i < 9; i++) {
      componentIndexes[i] = 0;
    }
    for (int i = 0; i < COMPONENTS_END - COMPONENTS_START; i++) {
      components[i] = 0;
    }
    for (int i = 0; i < (ARRAY_INDEXES_END - ARRAY_INDEXES_START) * 2; i++) {
      arrays[i] = 0;
    }
    for (int i = 0; i < PARAMETERS_END - PARAMETERS_START; i++) {
      parameters[i] = 0;
    }
    for (int i = 0; i < 6; i++) {
      ifs[i] = false;
    }
  }
    
  bool parseTypeDeclaration(int codeIndex) {
    for (int j = TYPES_END; j >= TYPES_START; j--) {
      if(code[codeIndex] == j) {
        int typeIndex = j - TYPES_START;
        byte typeCount = (byte) (code[codeIndex + 1] - COMPONENTS_START);
        typeCount += componentIndexes[typeIndex];
        // assign typeCount to typeIndex - 1 to make type count directly
        // accessible through componentIndexes[TYPE_ - TYPES_START]
        if(typeIndex > 0) {
          componentIndexes[typeIndex - 1] = typeCount;
        }
        return true;
      }
    }
    return false;
  }
  
  bool parseFunctionDeclaration(int codeIndex) {
    if(code[codeIndex] == KEYWORD_FUNCTION) {
      // - 1 because the compiler adds 1 to avoid names with character 0
      int componentIndex = (int)code[codeIndex + 1] - COMPONENTS_START;
      // jooCodeIndex + 3 because KEYWORD_FUNCTION + name + lineBreak
      components[componentIndex] = codeIndex + 3;
      return true;
    }
    return false;
  } 
  
  bool parseVariableDeclaration(int codeIndex) {
    int componentIndex = (int)code[codeIndex] - COMPONENTS_START;
    char componentType = interpretComponentType(componentIndex);
    char componentValue = code[codeIndex + 1];
    // if value is declared
    if(componentValue != LINE_BREAK) {
      // if variable is char or array there is no number behind the variable name
      // as the array sizes are expressed as a char to make code smaller
      if(componentType == TYPE_CHAR) {
        components[componentIndex] = componentValue;
      }
      // if is array
      else if(componentType <= TYPE_ARRAY_INT) {
        // assign array size to componentIndex + 1 to make arrayStartIndex be directly
        // accessible through jooComponents[array variable index]
        int arrayStartIndex = componentValue + components[componentIndex];
        components[componentIndex + 1] = arrayStartIndex;
      } else {
        components[componentIndex] = parseNumber(codeIndex + 1);
      }
      return true;
    }
    return false;
  }
  
  int parseNumber(int codeIndex) {
    if((code[codeIndex] >= NUMBER_0) && (code[codeIndex] <= NUMBER_9)) {
      int result = 0;
      int index = 100000;
      for (int i = codeIndex; code[i] != LINE_BREAK; i++) {
        index /= 10;
        result += (code[i] - NUMBER_0) * index;
      }
      return result / index;
    }
    return -1;
  }
  
  void interpretFunction(char functionIndex) {
    bool if0 = ifs[0];
    bool if1 = ifs[1];
    bool if2 = ifs[2];
    bool if3 = ifs[3];
    bool if4 = ifs[4];
    bool if5 = ifs[5];
    int startCodeIndex = components[functionIndex];
    int codeIndex = startCodeIndex;
    byte allIfs = -1;
    bool canInterpretCode = true;
    for (int i = codeIndex; i < codeSize; i++) {
      if((code[i] == LINE_BREAK) && (i + 1 < codeSize)) {
        codeIndex = i + 1;
      } 
      else if (i != startCodeIndex) {
        continue;
      }
      else if(code[codeIndex] == KEYWORD_FUNCTION) {
        break;
      }
      switch (code[codeIndex]) {
      case KEYWORD_IF:
        allIfs = interpretIfOperation(codeIndex, allIfs);
        canInterpretCode = ifs[allIfs];
        break;
      case KEYWORD_ELSE_IF:
        canInterpretCode = interpretElseIfOperation(codeIndex, allIfs);
        break;
      case KEYWORD_ELSE:
        canInterpretCode = interpretElseOperation(allIfs, canInterpretCode);
        break;
      case KEYWORD_FUNCTION:        
        return;
      case KEYWORD_FUNCTION_CALL:
        if(canInterpretCode) {
          interpretFunctionCall(codeIndex, true);
        }
        break;
      case KEYWORD_FUNCTION_REPEAT:
        if(canInterpretCode) {
          interpretFunction(functionIndex);
          return;
        }
        break;
      default:
        if(canInterpretCode) {
          if(interpretComponentType(code[codeIndex] - COMPONENTS_START) == TYPE_FUNCTION) {
            interpretFunctionCall(codeIndex, false);
          } else {
            interpretVariableOperation(codeIndex);
          }
        }
        break;
      }
    }
    ifs[0] = if0;
    ifs[1] = if1;
    ifs[2] = if2;
    ifs[3] = if3;
    ifs[4] = if4;
    ifs[5] = if5;
  }
  
  byte interpretIfOperation(int codeIndex, byte allIfs) {
    if(code[codeIndex + 1] == LINE_BREAK) {
      if(allIfs > 0) {
        allIfs--;           
      } else {
        ifs[allIfs] = true; 
      }
    } else {
      allIfs++;
      ifs[allIfs] = interpretVariableOperation(codeIndex + 1);
    }
    return allIfs;
  }
  
  bool interpretElseIfOperation(int codeIndex, byte allIfs) {
    if((allIfs > 0) && ifs[allIfs - 1]) {
      return interpretVariableOperation(codeIndex + 1);
    } else { // if there is only 1 if
      return interpretVariableOperation(codeIndex + 1);
    }
  }
  
  bool interpretElseOperation(byte allIfs, bool canInterpretCode) {
    if((allIfs > 0) && ifs[allIfs - 1]) {
      return !canInterpretCode;
    } else { // if there is only 1 if
      return !canInterpretCode;
    }
  }
  
  void interpretFunctionCall(int codeIndex, bool nativeFunction) {
    if(nativeFunction) {
      codeIndex++;
    }
    int parameter0 = parameters[0];
    int parameter1 = parameters[1];
    int parameter2 = parameters[2];
    int parameter3 = parameters[3];
    int parameter4 = parameters[4];
    int parameter5 = parameters[5];
    byte currentParameter = 0;
    for (int i = codeIndex; code[i] != LINE_BREAK; i++) {
      if(code[i] == KEYWORD_PARAMETER) {
        int parameterIndex = code[i + 1] - COMPONENTS_START;
        char arrayIndex = code[i + 2];
        if((arrayIndex != KEYWORD_PARAMETER) && (arrayIndex != LINE_BREAK)) {
          // don't subtract ARRAY_INDICES_START because its used to know if it's an array index
          parameterIndex = components[parameterIndex] + arrayIndex;
        }
        parameters[currentParameter++] = parameterIndex;
      } else {
        continue;
      }
    }
    if(nativeFunction) {
      callNativeFunction(code[codeIndex]);
    } else {
      interpretFunction((char) (code[codeIndex] - COMPONENTS_START));
    }
    parameters[0] = parameter0;
    parameters[1] = parameter1;
    parameters[2] = parameter2;
    parameters[3] = parameter3;
    parameters[4] = parameter4;
    parameters[5] = parameter5;
  }
  
  bool interpretVariableOperation(int codeIndex) {
    int variable0Index = interpretVariableIndex(code[codeIndex]);   
    char variable0Type = interpretComponentType(variable0Index);
    // if is array
    if(variable0Type <= TYPE_ARRAY_INT) {
      // if variable0 is an array there is a array index behind variable0Index
      codeIndex++;
    }
    char variableOperator = code[codeIndex + 1];
    int variable1Value = interpretOperationValue(codeIndex);
    // if is not array
    if(variable0Type > TYPE_ARRAY_INT) {
      return interpretVariableOperation(components, variable0Index, variable0Type, variableOperator, variable1Value);
    } else {
      int variable0ArrayIndex = interpretArrayIndex(code[codeIndex]);
      // get index in arrays array
      variable0Index = components[variable0Index] + variable0ArrayIndex;
      // convert array type to primitive type
      variable0Type = (char)(variable0Type + (TYPE_INT - TYPE_ARRAY_INT));
      return interpretVariableOperation(arrays, variable0Index, variable0Type, variableOperator, variable1Value);
    }
  }
  
  int interpretOperationValue(int codeIndex) {
    int variable1Value = parseNumber(codeIndex + 2);
    // if is not number get variable value
    if(variable1Value == -1) {
      int variable1Index = interpretVariableIndex(code[codeIndex + 2]);
      // if TYPE_CHAR after the operator then it's a char value
      if((variable1Index + 1) == TYPE_CHAR) {
        variable1Value = code[codeIndex + 3];
      } else {
        char variable1Type = interpretComponentType(variable1Index);
        // if is not array
        if(variable1Type > TYPE_ARRAY_INT) {
          variable1Value = components[variable1Index];
        } else {
          int variable1ArrayIndex = interpretArrayIndex(code[codeIndex + 3]);
          variable1Value = arrays[components[variable1Index] + variable1ArrayIndex];
        }
      }
    }
    return variable1Value;
  }
  
  int interpretVariableIndex(int variableIndex) {
    variableIndex -= COMPONENTS_START;
    if((variableIndex >= PARAMETERS_START - COMPONENTS_START) && (variableIndex < ARRAY_INDEXES_START)) {
      variableIndex = parameters[(variableIndex - PARAMETERS_START) + COMPONENTS_START];
    }
    return variableIndex;
  }
  
  int interpretArrayIndex(int variableArrayIndex) {
    // if is number index
    if(variableArrayIndex >= ARRAY_INDEXES_START) {
      variableArrayIndex -= ARRAY_INDEXES_START;
    }
    // if is parameter index
    else if(variableArrayIndex >= PARAMETERS_START) {
      variableArrayIndex = parameters[variableArrayIndex - PARAMETERS_START];
    } else { // if is variable index
      variableArrayIndex = components[variableArrayIndex - COMPONENTS_START];
    }
    return variableArrayIndex;
  }
  
  char interpretComponentType(int componentIndex) {
    if(componentIndex < componentIndexes[TYPE_FIXED - TYPES_START]) {
      return TYPE_INT;
    }
    else if(componentIndex < componentIndexes[TYPE_BOOL - TYPES_START]) {
      return TYPE_FIXED;
    }
    else if(componentIndex < componentIndexes[TYPE_CHAR - TYPES_START]) {
      return TYPE_BOOL;
    }
    else if(componentIndex < componentIndexes[TYPE_ARRAY_INT - TYPES_START]) {
      return TYPE_CHAR;
    }
    else if(componentIndex < componentIndexes[TYPE_ARRAY_FIXED - TYPES_START]) {
      return TYPE_ARRAY_INT;
    }
    else if(componentIndex < componentIndexes[TYPE_ARRAY_BOOL - TYPES_START]) {
      return TYPE_ARRAY_FIXED;
    } 
    else if(componentIndex < componentIndexes[TYPE_ARRAY_CHAR - TYPES_START]) {
      return TYPE_ARRAY_BOOL;
    } 
    else if(componentIndex < componentIndexes[TYPE_FUNCTION - TYPES_START]) {
      return TYPE_ARRAY_CHAR;
    }
    else if (componentIndex < COMPONENTS_END){
      return TYPE_FUNCTION;
    }
    return 0;
  }

  #define STANDART_OPERATOR_START 0 + OPERATORS_START
  
  #define STANDART_COMPARATOR_SMALLER_EQUALS 0 + STANDART_OPERATOR_START
  #define STANDART_COMPARATOR_BIGGER_EQUALS 1 + STANDART_OPERATOR_START
  #define STANDART_COMPARATOR_SMALLER 2 + STANDART_OPERATOR_START
  #define STANDART_COMPARATOR_BIGGER 3 + STANDART_OPERATOR_START
  #define STANDART_COMPARATOR_EQUALS 4 + STANDART_OPERATOR_START
  #define STANDART_COMPARATOR_NOT_EQUALS 5 + STANDART_OPERATOR_START

  #define STANDART_OPERATOR_ASSIGN 6 + STANDART_OPERATOR_START
  #define STANDART_OPERATOR_ASSIGN_POSITIVE 7 + STANDART_OPERATOR_START
  #define STANDART_OPERATOR_ASSIGN_NEGATIVE 8 + STANDART_OPERATOR_START
  #define STANDART_OPERATOR_ASSIGN_INVERSE 9 + STANDART_OPERATOR_START
  #define STANDART_OPERATOR_ADD 10 + STANDART_OPERATOR_START
  #define STANDART_OPERATOR_SUBTRACT 11 + STANDART_OPERATOR_START
  #define STANDART_OPERATOR_MULTIPLY 12 + STANDART_OPERATOR_START
  #define STANDART_OPERATOR_DIVIDE 13 + STANDART_OPERATOR_START
  #define STANDART_OPERATOR_REMAINDER 14 + STANDART_OPERATOR_START
  #define STANDART_OPERATOR_BITWISE_AND 15 + STANDART_OPERATOR_START
  #define STANDART_OPERATOR_BITWISE_XOR 16 + STANDART_OPERATOR_START
  #define STANDART_OPERATOR_BITWISE_OR 17 + STANDART_OPERATOR_START
  #define STANDART_OPERATOR_BITWISE_NOT 18 + STANDART_OPERATOR_START
  #define STANDART_OPERATOR_BITSHIFT_LEFT 19 + STANDART_OPERATOR_START
  #define STANDART_OPERATOR_BITSHIFT_RIGHT 20 + STANDART_OPERATOR_START
  
  bool interpretVariableOperation(int values[], int variable0Index, char variable0Type, char variableOperator, int variable1Value) {
    if (variableOperator == STANDART_COMPARATOR_SMALLER_EQUALS) {
      return values[variable0Index] <= variable1Value;
    }
    else if (variableOperator == STANDART_COMPARATOR_BIGGER_EQUALS) {
      return values[variable0Index] >= variable1Value;
    }
    else if (variableOperator == STANDART_COMPARATOR_SMALLER) {
      return values[variable0Index] < variable1Value;
    }
    else if (variableOperator == STANDART_COMPARATOR_BIGGER) {
      return values[variable0Index] > variable1Value;
    }
    else if (variableOperator == STANDART_COMPARATOR_EQUALS) {
      return values[variable0Index] == variable1Value;
    }
    else if (variableOperator == STANDART_COMPARATOR_NOT_EQUALS) {
      return values[variable0Index] != variable1Value;
    }
    else if (variableOperator == STANDART_OPERATOR_ASSIGN) {
      values[variable0Index] = variable1Value;
    }
    else if (variableOperator == STANDART_OPERATOR_ASSIGN_POSITIVE) {
      if(variable0Type == TYPE_BOOL) {
        values[variable0Index] = 1;
      } else {
        if(variable1Value < 0) {
          values[variable0Index] = -variable1Value;
        } else {
          values[variable0Index] = variable1Value;
        }
      }
    }
    else if (variableOperator == STANDART_OPERATOR_ASSIGN_NEGATIVE) {
      if(variable0Type == TYPE_BOOL) {
        values[variable0Index] = 0;
      } else {
        if(variable1Value < 0) {
          values[variable0Index] = variable1Value;
        } else {
          values[variable0Index] = -variable1Value;
        }
      }
    }
    else if (variableOperator == STANDART_OPERATOR_ASSIGN_INVERSE) {
      if(variable0Type == TYPE_BOOL) {
        if(variable1Value == 0) {
          values[variable0Index] = 1;
        } else {
          values[variable0Index] = 0;
        }
      } else {
        values[variable0Index] = -variable1Value;
      }
    }
    else if (variableOperator == STANDART_OPERATOR_ADD) {
      values[variable0Index] += variable1Value;
    }
    else if (variableOperator == STANDART_OPERATOR_SUBTRACT) {
      values[variable0Index] -= variable1Value;
    }
    else if (variableOperator == STANDART_OPERATOR_MULTIPLY) {
      if(variable0Type == TYPE_FIXED) {
        long result = (long)values[variable0Index] * (long)variable1Value;
        values[variable0Index] = (int)(result >> FIXED_POINT);
      } else {
        values[variable0Index] *= variable1Value;
      }
    }
    else if (variableOperator == STANDART_OPERATOR_DIVIDE) {
      if(variable0Type == TYPE_FIXED) {
        long result = (long)values[variable0Index] << FIXED_POINT;
        values[variable0Index] = (int)(result / variable1Value);
      } else {
        values[variable0Index] /= variable1Value;
      }
    }
    else if (variableOperator == STANDART_OPERATOR_REMAINDER) {
      values[variable0Index] %= variable1Value;
    }
    else if (variableOperator == STANDART_OPERATOR_BITWISE_AND) {
      values[variable0Index] &= variable1Value;
    }
    else if (variableOperator == STANDART_OPERATOR_BITWISE_XOR) {
      values[variable0Index] ^= variable1Value;
    }
    else if (variableOperator == STANDART_OPERATOR_BITWISE_OR) {
      values[variable0Index] |= variable1Value;
    }
    else if (variableOperator == STANDART_OPERATOR_BITWISE_NOT) {
      values[variable0Index] = ~variable1Value;
    }
    else if (variableOperator == STANDART_OPERATOR_BITSHIFT_LEFT) {
      values[variable0Index] <<= variable1Value;
    }
    else if (variableOperator == STANDART_OPERATOR_BITSHIFT_RIGHT) {
      values[variable0Index] >>= variable1Value;
    }
    return false;
  }

  #define STANDART_NATIVE_START 0 + NATIVE_FUNCTIONS_START
  
  #define STANDART_NATIVE_EXECUTE 0 + STANDART_NATIVE_START
  #define STANDART_NATIVE_PRINT 1 + STANDART_NATIVE_START


  #define ARDUINO_NATIVE_START 2 + NATIVE_FUNCTIONS_START

  #define ARDUINO_NATIVE_DIGITAL_READ 0 + ARDUINO_NATIVE_START
  #define ARDUINO_NATIVE_DIGITAL_WRITE 1 + ARDUINO_NATIVE_START
  #define ARDUINO_NATIVE_PIN_MODE 2 + ARDUINO_NATIVE_START
  
  #define ARDUINO_NATIVE_ANALOG_READ 3 + ARDUINO_NATIVE_START
  #define ARDUINO_NATIVE_ANALOG_REFERENCE 4 + ARDUINO_NATIVE_START
  #define ARDUINO_NATIVE_ANALOG_WRITE 5 + ARDUINO_NATIVE_START
  
  #define ARDUINO_NATIVE_NO_TONE 6 + ARDUINO_NATIVE_START
  #define ARDUINO_NATIVE_PULSE_IN 7 + ARDUINO_NATIVE_START
  #define ARDUINO_NATIVE_PULSE_IN_LONG 8 + ARDUINO_NATIVE_START
  #define ARDUINO_NATIVE_SHIFT_IN 9 + ARDUINO_NATIVE_START
  #define ARDUINO_NATIVE_SHIFT_OUT 10 + ARDUINO_NATIVE_START
  #define ARDUINO_NATIVE_TONE 11 + ARDUINO_NATIVE_START
  
  #define ARDUINO_NATIVE_DELAY 12 + ARDUINO_NATIVE_START
  #define ARDUINO_NATIVE_DELAY_MICROSECONDS 13 + ARDUINO_NATIVE_START
  #define ARDUINO_NATIVE_MICROS 14 + ARDUINO_NATIVE_START
  #define ARDUINO_NATIVE_MILLIS 15 + ARDUINO_NATIVE_START
  
  void callNativeFunction(char functionIndex) {    
    if (functionIndex == STANDART_NATIVE_EXECUTE) {
      #if BYTE_CODE_SOURCE == SOURCE_SD
        int programIndex = components[parameters[0]];
        programStart = 0;
        for (int i = 0; i < programIndex; i++) {
          file.seek(programStart);
          codeSize = 0;
          codeSize |= (file.read() << 8) & 255;
          codeSize |= file.read() & 255;
          programStart += codeSize + 2;
        }
      #endif
    }
    else if (functionIndex == STANDART_NATIVE_PRINT) {
      Serial.println(components[parameters[0]]);
    }
    else if (functionIndex == ARDUINO_NATIVE_DIGITAL_READ) {
      int pin = components[parameters[0]];
      components[parameters[1]] = digitalRead(pin == 256 ? LED_BUILTIN : pin);
    }
    else if (functionIndex == ARDUINO_NATIVE_DIGITAL_WRITE) {
      int pin = components[parameters[0]];
      int value = -1;
      if (components[parameters[1]] == 1) value = LOW;
      if (components[parameters[1]] == 2) value = HIGH;
      digitalWrite(pin == 256 ? LED_BUILTIN : pin, value);
    }
    else if (functionIndex == ARDUINO_NATIVE_PIN_MODE) {
      int pin = components[parameters[0]];
      int mode = -1;
      if (components[parameters[1]] == 1) mode = LOW;
      if (components[parameters[1]] == 2) mode = HIGH;
      if (components[parameters[1]] == 3) mode = INPUT_PULLUP;
      pinMode(pin == 256 ? LED_BUILTIN : pin, mode);
    }
    else if (functionIndex == ARDUINO_NATIVE_ANALOG_READ) {
      components[parameters[1]] = analogRead(components[parameters[0]]);
    }
    else if (functionIndex == ARDUINO_NATIVE_ANALOG_REFERENCE) {
      analogReference(components[parameters[0]]);
    }
    else if (functionIndex == ARDUINO_NATIVE_ANALOG_WRITE) {
      analogWrite(components[parameters[0]], components[parameters[1]]);
    }
    else if (functionIndex == ARDUINO_NATIVE_NO_TONE) {
      noTone(components[parameters[0]]);
    }
    else if (functionIndex == ARDUINO_NATIVE_PULSE_IN) {
      if(components[parameters[2]] == 0) {
        components[parameters[3]] = pulseIn(components[parameters[0]], components[parameters[1]]);
    } else {
        components[parameters[3]] = pulseIn(components[parameters[0]], components[parameters[1]], components[parameters[2]]);
    }
    }
    else if (functionIndex == ARDUINO_NATIVE_PULSE_IN_LONG) {
      if(components[parameters[2]] == 0) {
        components[parameters[3]] = pulseInLong(components[parameters[0]], components[parameters[1]]);
      } else {
        components[parameters[3]] = pulseInLong(components[parameters[0]], components[parameters[1]], components[parameters[2]]);
      }
    }
    else if (functionIndex == ARDUINO_NATIVE_SHIFT_IN) {
      components[parameters[3]] = shiftIn(components[parameters[0]], components[parameters[1]], components[parameters[2]]);
    }
    else if (functionIndex == ARDUINO_NATIVE_SHIFT_OUT) {
      shiftOut(components[parameters[0]], components[parameters[1]], components[parameters[2]], components[parameters[3]]);
    }
    else if (functionIndex == ARDUINO_NATIVE_TONE) {
      if(components[parameters[2]] == 0) {
        tone(components[parameters[0]], components[parameters[1]]);
      } else {
        tone(components[parameters[0]], components[parameters[1]], components[parameters[2]]);
      }
    }
    else if (functionIndex == ARDUINO_NATIVE_DELAY) {
      delay(components[parameters[0]]);
    } 
    else if (functionIndex == ARDUINO_NATIVE_DELAY_MICROSECONDS) {
      delayMicroseconds(components[parameters[0]]);
    }
    else if (functionIndex == ARDUINO_NATIVE_MICROS) {
      components[parameters[0]] = micros();
    }
    else if (functionIndex == ARDUINO_NATIVE_MILLIS) {
      components[parameters[0]] = millis();
    }
  }
