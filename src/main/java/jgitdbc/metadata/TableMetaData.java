package jgitdbc.metadata;

import gristle.GitRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.jsqlparser.statement.select.SelectItem;

public abstract class TableMetaData extends SimpleSelectMetaData {
  
  private List<SelectItem> selectItems;

  public TableMetaData(String tableName) {
    super(tableName);
  }
  
  public void setSelectItems(List<SelectItem> selectItems){
    this.selectItems = selectItems;
  }
  
  public abstract ColumnMetaData[] getAllColumnDefsImpl();
  
  public ColumnMetaData[] getColumnDefsImpl(){
    boolean allCols = (selectItems.size() == 1 && selectItems.get(0).toString().equals("*"));
    if (allCols) {
      return this.getAllColumnDefsImpl();
    }

    Map<String, ColumnMetaData> mapOfColumnDefByName = this.getMapOfColumnDefByName();

    List<ColumnMetaData> cols = new ArrayList<ColumnMetaData>(selectItems.size());
    for (SelectItem selectItem : selectItems) {
      cols.add(mapOfColumnDefByName.get(selectItem.toString()));
    }
    
    return (ColumnMetaData[]) cols.toArray();
  }

  public abstract List<ResultRow> getRows(GitRepository repo) throws IOException;

  public SimpleSelectMetaData columnsOf(List<SelectItem> selectItems2) {
    return null;
  }
  
}
