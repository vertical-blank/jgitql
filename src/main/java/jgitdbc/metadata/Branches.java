package jgitdbc.metadata;

import gristle.GitRepository;
import gristle.GitRepository.Branch;

import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class Branches extends TableMetaData {
  static final String TABLE_NAME = "branches";
  private Branches() {
    super(TABLE_NAME);
  }
  
  private static final ColumnMetaData[] COL_DEFS = {
    new ColumnMetaData("name", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("full_name", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls),
    new ColumnMetaData("hash", Types.VARCHAR, String.class, ResultSetMetaData.columnNoNulls)};
  
  @Override
  public ColumnMetaData[] getAllColumnDefsImpl(){
    return COL_DEFS;
  }

  @Override
  public List<ResultRow> getRows(GitRepository repo) throws IOException {
    List<ResultRow> rows = new ArrayList<ResultRow>();

    List<Branch> listBranches = repo.listBranches();
    for (Branch branch : listBranches) {
      rows.add(Branches.createRow(
        branch.name,
        "refs/tags/" + branch.name,
        branch.head().getObjectId().getName())
      );
    }

    return rows;
  }
  
  public static ResultRow createRow(
      String name, 
      String full_name, 
      String hash){
    return new ResultRow(name, full_name, hash);
  }
  
}
