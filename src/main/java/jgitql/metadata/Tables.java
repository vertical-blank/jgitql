package jgitql.metadata;

import glitch.GitRepository;

import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import jgitql.parser.Parser.Expression;
import net.sf.jsqlparser.statement.select.OrderByElement;

public class Tables extends TableMetaData {
  
  public static final Tables instance = new Tables();

  private static final String TABLE_NAME = "Tables";

  private Tables() {
    super(TABLE_NAME);
  }

  @Override
  public ColumnMetaData[] getAllColumnDefs() {
    return new ColumnMetaData[]{
        new ColumnMetaData("table_cat", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
        new ColumnMetaData("table_schem", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
        new ColumnMetaData("table_name", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
        new ColumnMetaData("table_type", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
        new ColumnMetaData("remarks", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls)
    };
  }

  public List<ResultRow> getRows() {
    List<ResultRow> rows = new ArrayList<ResultRow>();
    
    for (String tableName : TableMetaDatas.getAllNames()) {
      rows.add(new ResultRow(this, null, null, tableName, "TABLE", null));
    }
    
    return rows;
  }

  @Override
  public List<ResultRow> getRows(GitRepository repo, Expression expression, List<OrderByElement> orderByElements)
      throws IOException, SQLException {
    return getRows();
  }
  
}
