package jgitql.metadata;

import glitch.GitRepository;

import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import jgitql.parser.Parser.Expression;
import net.sf.jsqlparser.statement.select.OrderByElement;

public class Columns extends TableMetaData {
  
  public static final Columns instance = new Columns();
  
  public static final String TABLE_NAME = "columns";
  
  protected Columns() {
    super(TABLE_NAME);
  }
  
  @Override
  public ColumnMetaData[] getAllColumnDefs() {
    return new ColumnMetaData[]{
        new ColumnMetaData("table_cat", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
        new ColumnMetaData("table_schem", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
        new ColumnMetaData("table_name", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
        new ColumnMetaData("column_name", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
        new ColumnMetaData("data_type", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
        new ColumnMetaData("type_name", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
        new ColumnMetaData("columns_size", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
        new ColumnMetaData("buffer_length", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
        new ColumnMetaData("decimal_digits", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
        new ColumnMetaData("num_prec_radix", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
        new ColumnMetaData("nullable", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
        new ColumnMetaData("remarks", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
        new ColumnMetaData("column_def", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls)
    };
  }

  @Override
  public List<ResultRow> getRows(GitRepository repo, Expression expression, List<OrderByElement> orderByElements)
      throws IOException, SQLException {
    throw new UnsupportedOperationException();
  }
  
}
