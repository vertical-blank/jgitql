package jgitdbc.metadata;

import gristle.GitRepository;

import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SimpleSelectMetaData implements ResultSetMetaData {
  
  private ColumnMetaData[] columnDefs;
  private Map<String, ColumnMetaData> mapOfColumnDefByName;
  public final String tableName;
  
  public SimpleSelectMetaData(String tableName) {
    this.tableName = tableName;
  }
  
  public abstract ColumnMetaData[] getColumnDefsImpl();

  public abstract List<ResultRow> getRows(GitRepository repo) throws IOException;
  
  @Override
  public String getTableName(int i){
    return tableName;
  }

  public ColumnMetaData[] getColumnDefs() {
    if (this.columnDefs == null){
      this.columnDefs = getColumnDefsImpl();
    }
    return this.columnDefs;
  }
  
  public Map<String, ColumnMetaData> getMapOfColumnDefByName() {
    if (this.mapOfColumnDefByName == null){
      Map<String, ColumnMetaData> map = new HashMap<String, ColumnMetaData>();
      for (ColumnMetaData def : this.getColumnDefs()) {
        map.put(def.getName(), def);
      }
      this.mapOfColumnDefByName = map;
    }
    return this.mapOfColumnDefByName;
  }
  
  public ColumnMetaData getColumnDef(int column) {
    return this.getColumnDefs()[column - 1];
  }
  
  public int findColumn(String columnLabel) {
    ColumnMetaData[] columnDefs = getColumnDefs();
    for (int i = 0; i < columnDefs.length; i++) {
      if (columnDefs[i].getName().equals(columnLabel)){
        return i;
      }
    }
    return -1;
  }
  
  @Override
  public int getColumnCount() throws SQLException {
    return this.getColumnDefs().length;
  }

  @Override
  public String getColumnLabel(int column) throws SQLException {
    return this.getColumnDef(column).getName();
  }

  @Override
  public String getColumnName(int column) throws SQLException {
    return this.getColumnDef(column).getName();
  }
  
  @Override
  public int isNullable(int column) throws SQLException {
    return this.getColumnDef(column).getNullable();
  }

  @Override
  public int getColumnType(int column) throws SQLException {
    return this.getColumnDef(column).getType();
  }

  @Override
  public String getColumnTypeName(int column) throws SQLException {
    return this.getColumnDef(column).getTypeName();
  }

  @Override
  public String getColumnClassName(int column) throws SQLException {
    return this.getColumnDef(column).getClassName();
  }
  
  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return null;
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return false;
  }

  @Override
  public boolean isAutoIncrement(int column) throws SQLException {
    return false;
  }

  @Override
  public boolean isCaseSensitive(int column) throws SQLException {
    return false;
  }

  @Override
  public boolean isSearchable(int column) throws SQLException {
    return false;
  }

  @Override
  public boolean isCurrency(int column) throws SQLException {
    return false;
  }

  @Override
  public boolean isSigned(int column) throws SQLException {
    return false;
  }

  @Override
  public int getColumnDisplaySize(int column) throws SQLException {
    return 0;
  }

  @Override
  public String getSchemaName(int column) throws SQLException {
    return null;
  }

  @Override
  public int getPrecision(int column) throws SQLException {
    return 0;
  }

  @Override
  public int getScale(int column) throws SQLException {
    return 0;
  }

  @Override
  public String getCatalogName(int column) throws SQLException {
    return null;
  }

  @Override
  public boolean isReadOnly(int column) throws SQLException {
    return true;
  }

  @Override
  public boolean isWritable(int column) throws SQLException {
    return false;
  }

  @Override
  public boolean isDefinitelyWritable(int column) throws SQLException {
    return false;
  }
  
}
