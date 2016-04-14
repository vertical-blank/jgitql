package jgitdbc.metadata;

import gristle.GitRepository;
import gristle.GitRepository.Tag;

import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class Tags extends TableMetaData {
  public static final String TABLE_NAME = "tags";
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
  public List<ResultRow> getRows(GitRepository repo) throws IOException {
    List<ResultRow> rows = new ArrayList<ResultRow>();

    List<Tag> listTags = repo.listTags();
    for (Tag tag : listTags) {
      Object[] allVals = new Object[] {
        tag.name,
        "refs/tags/" + tag.name,
        tag.getCommit().getObjectId().getName()
      };
      rows.add(new ResultRow(this, filterColumns(allVals)));
    }

    return rows;
  }
  
}
