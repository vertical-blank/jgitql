package jgitql.metadata;

import glitch.GitRepository;
import glitch.GitRepository.Commit;

import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import jgitql.core.parser.Parser.Expression;
import net.sf.jsqlparser.statement.select.OrderByElement;

public class Files extends TableMetaData {
  public static final String TABLE_NAME = "files";
  
  private static final TableMetaData ALL = new Files();
  
  public Files() {
    super(TABLE_NAME);
  }
  
  private static final ColumnMetaData[] COL_DEFS = {
    new ColumnMetaData("commit_id", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("name", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls)
  };
  
  @Override
  public ColumnMetaData[] getAllColumnDefs(){
    return COL_DEFS;
  }
  @Override
  public List<ResultRow> getRows(GitRepository repo, Expression expression, List<OrderByElement> orderByElements) throws IOException, SQLException {
    List<ResultRow> temp = new ArrayList<ResultRow>();

    for (Commit commit : repo.listCommits()) {
      for (String path : commit.listFiles()) {
        Object[] allVals = new Object[] {commit.getObjectId().getName(), path};
        temp.add(new ResultRow(ALL, allVals));
      }
    }
    
    temp.sort(new RowComparator(orderByElements));
    return filterRowsAndCols(temp, expression);
  }
  
}
