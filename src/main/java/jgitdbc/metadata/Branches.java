package jgitdbc.metadata;

import java.sql.SQLException;
import java.sql.Types;


public class Branches extends BaseMetaData {

  @Override
  protected String[] getColNmaes() {
    return new String[]{"name", "full_name", "hash"};
  }

  @Override
  public int isNullable(int column) throws SQLException {
    return 0;
  }

  @Override
  public String getTableName(int column) throws SQLException {
    return "branches";
  }

  @Override
  public int getColumnType(int column) throws SQLException {
    return Types.VARCHAR;
  }

  @Override
  public String getColumnTypeName(int column) throws SQLException {
    return "VARCHAR";
  }

  @Override
  public String getColumnClassName(int column) throws SQLException {
    return String.class.getName();
  }
  
}
