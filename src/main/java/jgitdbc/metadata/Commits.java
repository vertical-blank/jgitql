package jgitdbc.metadata;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.Date;

public class Commits extends BaseMetaData {
  
  private Commits(){
    
  }
  public static final Commits INSTANCE = new Commits();

  private static final String TABLE_NAME = "commits";
  
  private static final ColumnMetaData[] COL_DEFS = {
    new ColumnMetaData("author", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("author_email", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("committer", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("committer_email", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("hash", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("message", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("full_message", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("date", Types.TIMESTAMP, java.util.Date.class, ResultSetMetaData.columnNoNulls)
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
      String author, 
      String author_email, 
      String committer, 
      String committer_email, 
      String hash, 
      String message, 
      String full_message,
      long time){
    return new ResultRow(
      author,
      author_email,
      committer,
      committer_email,
      hash,
      message,
      full_message,
      new Date(time)
    );
  }
  
}
