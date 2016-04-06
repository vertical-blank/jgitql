package jgitdbc.metadata;

import java.text.SimpleDateFormat;

public class ResultRow {
  private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private Object[] colValues;
  
  ResultRow(Object ... colValues) {
    this.colValues = colValues;
  }

  private Object getColValue(int columnNumber) {
    return this.colValues[columnNumber - 1];
  }
  public String getString(int columnNumber) {
    Object val = getColValue(columnNumber);
    if (val instanceof java.sql.Date){
      return formatter.format(val);
    }
    return String.valueOf(val);
  }
}
