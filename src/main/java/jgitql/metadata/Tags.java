package jgitql.metadata;

import glitch.GitRepository;
import glitch.GitRepository.Tag;

import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import jgitql.parser.Parser.Expression;
import net.sf.jsqlparser.statement.select.OrderByElement;

public class Tags extends TableMetaData {
  public static final String TABLE_NAME = "tags";
  
  private static final TableMetaData ALL = new Tags();
  
  public Tags() {
    super(TABLE_NAME);
  }
  
  private static final ColumnMetaData[] COL_DEFS = {
    new ColumnMetaData("author", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("author_email", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("committer", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("committer_email", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("hash", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("message", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("full_message", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls)};
  
  @Override
  public ColumnMetaData[] getAllColumnDefs(){
    return COL_DEFS;
  }
  @Override
  public List<ResultRow> getRows(GitRepository repo, Expression expression, List<OrderByElement> orderByElements) throws IOException, SQLException {

    List<ResultRow> temp = new ArrayList<ResultRow>();
    List<Tag> listTags = repo.listTags();
    for (Tag tag : listTags) {
      Object[] allVals = new Object[] {
        tag.name,
        "refs/tags/" + tag.name,
        tag.getCommit().getObjectId().getName()
      };
      temp.add(new ResultRow(ALL, allVals));
    }
    
    temp.sort(new RowComparator(orderByElements));
    return filterRowsAndCols(temp, expression);
  }
  
}
