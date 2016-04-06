package jgitdbc.metadata;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

public class Files extends BaseMetaData {
  
  private Files(){
    
  }
  public static final Files INSTANCE = new Files();

  private static final String TABLE_NAME = "files";
  
  private static final ColumnMetaData[] COL_DEFS = {
    new ColumnMetaData("commit_id", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("name", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls)
  };
  
  @Override
  public ColumnMetaData[] getColumnDefs(){
    return COL_DEFS;
  }

  @Override
  public String getTableName(int column) throws SQLException {
    return TABLE_NAME;
  }
  
  public static ResultRow createRow(
      String commit_id, 
      String name){
    return new ResultRow(
      commit_id,
      name
    );
  }
  
}
