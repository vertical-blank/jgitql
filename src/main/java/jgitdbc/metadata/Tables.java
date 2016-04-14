package jgitdbc.metadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jsqlparser.statement.select.SelectItem;

public class Tables {
  private static final Map<String, Class<? extends TableMetaData>> TABLES = new HashMap<String, Class<? extends TableMetaData>>();
  static {
    TABLES.put(Branches.TABLE_NAME, Branches.class);
    TABLES.put(Commits.TABLE_NAME,  Commits.class);
    TABLES.put(Files.TABLE_NAME,    Files.class);
    TABLES.put(Tags.TABLE_NAME,     Tags.class);
  }
  
  public static SimpleSelectMetaData get(String name, List<SelectItem> selectItems){
    try {
      TableMetaData newInstance = TABLES.get(name).newInstance();
      newInstance.setSelectItems(selectItems);
      return newInstance;
    } catch (InstantiationException | IllegalAccessException e) {
      return null;
    }
  }
  
  private Tables() {
    
  }
}
