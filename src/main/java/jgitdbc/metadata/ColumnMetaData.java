package jgitdbc.metadata;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class ColumnMetaData {
  
  private static final Map<Integer, String> typeNames;
  static {
    typeNames = new HashMap<Integer, String>();
    for (Field field : Types.class.getFields()) {
      try {
        typeNames.put((Integer)field.get(null), field.getName());
      } catch (IllegalArgumentException | IllegalAccessException e) {
      }
    }
  }
  
  private String name;
  
  private int type;
  
  private Class<?> clazz;
  
  private int nullable; 
  
  public ColumnMetaData(String name, int type, Class<?> clazz, int nullable) {
    super();
    this.name = name;
    this.type = type;
    this.clazz = clazz;
    this.nullable = nullable;
  }
  
  public String getClassName() {
    return this.clazz.getName();
  }
  
  public String getTypeName() {
    return typeNames.get(this.type);
  }

  public int getNullable() {
    return nullable;
  }

  public String getName() {
    return name;
  }

  public int getType() {
    return type;
  }

  public Class<?> getClazz() {
    return clazz;
  }
  
}
