package jgitdbc.metadata;

public class ResultRow {
  private Object[] colValues;
  
  ResultRow(Object ... colValues) {
    this.colValues = colValues;
  }
  
  public String getString(int columnIndex) {
    return String.valueOf(colValues[columnIndex]);
  }
}
