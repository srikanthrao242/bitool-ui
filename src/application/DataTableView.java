package application;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Row;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DataTableView{
	
	 private TableView table  = new TableView();
	 private ObservableList<Object> data;
	   
  public ObservableList t_model(String[] colNames) throws Exception{
        data = FXCollections.observableArrayList();
        try{   
            for(int i=0 ; i<colNames.length; i++){
                data.add(colNames[i]);
            }
        }catch(Exception e){
           System.out.println("problem in t_model"+e.getMessage());
        }
        return data;
   } 
   public ObservableList t_row(JavaRDD<Row> tableMap) throws Exception{
       ObservableList data1 = FXCollections.observableArrayList();
       tableMap.collect().forEach((Row row)->{        	
            ObservableList obsrow = FXCollections.observableArrayList();
            for(int i=0 ; i<row.size(); i++){                    
            	obsrow.add(row.get(i));
            }
            data1.add(obsrow);
		});
        return data1;
	}
	
	public void createTable(String[] colNames,JavaRDD<Row> tableMap, String ds_Name) throws Exception{
		DataTableView _this = this;
		Stage stage = new Stage();		
		Scene scene = new Scene(new Group());
        stage.setTitle(ds_Name);
        table.setEditable(true);
        
        ObservableList tl = _this.t_model(colNames);
        ObservableList<ObservableList> rl = _this.t_row(tableMap);
        
        for (Object val : tl){
            TableColumn col = new TableColumn(String.valueOf(val));
            col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>(){
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param){
                	int index = tl.indexOf(val);
                	ObservableList oList = param.getValue();
                	if(index != -1){
                		if(oList.get(index) != null)
                			return new SimpleStringProperty(oList.get(index).toString());
                		else
                			return new SimpleStringProperty("");
                	}else{
                		return null;
                	}                    
                }
            });
            table.getColumns().addAll(col);
        }
        table.setItems(rl);
        table.setVisible(true);
        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll( table);
        ScrollPane sp = new ScrollPane();
        sp.setContent(vbox);
 
        ((Group) scene.getRoot()).getChildren().addAll(sp);
        stage.setScene(scene);
        stage.show();
	}	
	
	
}
