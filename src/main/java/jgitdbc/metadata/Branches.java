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
  public List<ResultRow> getRows(GitRepository repo) throws IOException {
    List<ResultRow> rows = new ArrayList<ResultRow>();

    List<Branch> listBranches = repo.listBranches();
    for (Branch branch : listBranches) {
      Object[] allVals = new Object[] { branch.name, "refs/tags/" + branch.name, branch.head().getObjectId().getName() };
      rows.add(new ResultRow(this, filterColumns(allVals)));
    }

    return rows;
  }

}
