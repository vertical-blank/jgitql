package jgitdbc.metadata;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

public class Branches extends BaseMetaData {
  
  private Branches(){
    
  }
  public static final Branches INSTANCE = new Branches();
  
  static final String TABLE_NAME = "branches";
  
  private static final ColumnMetaData[] COL_DEFS = {
    new ColumnMetaData("name", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("full_name", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("hash", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls)};
  
  @Override
  public ColumnMetaData[] getColumnDefs(){
    return COL_DEFS;
  }

  @Override
  public String getTableName(int column) throws SQLException {
    return TABLE_NAME;
  }
  
  public static ResultRow createRow(
      String name, 
      String full_name, 
      String hash){
    return new ResultRow(name, full_name, hash);
  }
  
}
