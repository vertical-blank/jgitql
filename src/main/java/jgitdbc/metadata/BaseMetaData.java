package jgitdbc.metadata;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public abstract class BaseMetaData implements ResultSetMetaData {
  
  protected abstract String[] getColNmaes(); 

  @Override
  public int getColumnCount() throws SQLException {
    return this.getColNmaes().length;
  }

  @Override
  public String getColumnLabel(int column) throws SQLException {
    return this.getColNmaes()[column];
  }

  @Override
  public String getColumnName(int column) throws SQLException {
    return this.getColNmaes()[column];
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
