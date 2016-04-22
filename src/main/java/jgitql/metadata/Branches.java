package jgitql.metadata;

import glitch.GitRepository;
import glitch.GitRepository.Branch;

import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import jgitql.parser.Parser.Expression;
import net.sf.jsqlparser.statement.select.OrderByElement;

public class Branches extends TableMetaData {
  static final String TABLE_NAME = "branches";
  
  private static final TableMetaData ALL = new Branches();
  
  public Branches() {
    super(TABLE_NAME);
  }

  private static final ColumnMetaData[] COL_DEFS = {
      new ColumnMetaData("name", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
      new ColumnMetaData("full_name", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
      new ColumnMetaData("hash", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls) };

  @Override
  public ColumnMetaData[] getAllColumnDefs() {
    return COL_DEFS;
  }

  @Override
  public List<ResultRow> getRows(GitRepository repo, Expression expression, List<OrderByElement> orderByElements) throws IOException, SQLException {
    List<ResultRow> temp = new ArrayList<ResultRow>();

    List<Branch> listBranches = repo.listBranches();
    for (Branch branch : listBranches) {
      Object[] allVals = new Object[] { branch.name, "refs/tags/" + branch.name, branch.head().getObjectId().getName() };
      temp.add(new ResultRow(ALL, allVals));
    }
    
    temp.sort(new RowComparator(orderByElements));
    return filterRowsAndCols(temp, expression);
  }

}
