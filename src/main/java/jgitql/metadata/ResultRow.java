package jgitql.metadata;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class ResultRow {
  private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private TableMetaData meta;
  private Object[] colValues;

  
  ResultRow(TableMetaData meta, Object ... colValues) {
    this.meta = meta;
    this.setColValues(colValues);
  }

  private Object getColValue(int columnNumber) {
    return this.getColValues()[columnNumber - 1];
  }
  
  public String getString(String columnName) throws SQLException {
    return this.getString(this.meta.findColumn(columnName));
  }
  
  public String getString(int columnNumber) {
    Object val = getColValue(columnNumber);
    
    if (val instanceof java.sql.Date){
      return formatter.format(val);
    }
    return String.valueOf(val);
  }

  public Object[] getColValues() {
    return colValues;
  }

  public void setColValues(Object[] colValues) {
    this.colValues = colValues;
  }
}
