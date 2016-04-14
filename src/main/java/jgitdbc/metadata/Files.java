package jgitdbc.metadata;

import gristle.GitRepository;
import gristle.GitRepository.Commit;

import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class Files extends TableMetaData {
  public static final String TABLE_NAME = "files";
  private Files() {
    super(TABLE_NAME);
  }
  
  private static final ColumnMetaData[] COL_DEFS = {
    new ColumnMetaData("commit_id", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("name", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls)
  };
  
  @Override
  public ColumnMetaData[] getAllColumnDefsImpl(){
    return COL_DEFS;
  }
  @Override
  public List<ResultRow> getRows(GitRepository repo) throws IOException {
    List<ResultRow> rows = new ArrayList<ResultRow>();

    for (Commit commit : repo.listCommits()) {
      for (String path : commit.listFiles()) {
        rows.add(Files.createRow(commit.getObjectId().getName(), path));
      }
    }

    return rows;
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
