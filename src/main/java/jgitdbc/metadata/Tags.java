package jgitdbc.metadata;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

public class Tags extends BaseMetaData {
  
  private Tags(){
    
  }
  public static final Tags INSTANCE = new Tags();

  private static final String TABLE_NAME = "commits";
  
  private static final ColumnMetaData[] COL_DEFS = {
    new ColumnMetaData("author", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("author_email", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("committer", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("committer_email", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("hash", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("message", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("full_message", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls)};
  
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
