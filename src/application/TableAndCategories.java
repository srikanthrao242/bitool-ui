package application;


import java.io.IOException;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Row;

import com.pridictit.charts.Charts;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

class TableAndCategories {
	AddCategories addCat;
	JavaRDD<Row> tableMap;
	String[] colNames;
	TableAndCategories _this = this;
	String ds_Name ;
	public TableAndCategories(AddCategories addCat1, JavaRDD<Row> tableMap1,String[] colNames1,String ds_name){
		_this.addCat = addCat1;
		_this.tableMap = tableMap1;
		_this.colNames = colNames1;
		_this.ds_Name = ds_name;
	}
	public void createTable(String type) throws IOException{
		System.out.println("createTable invoked");
		try{				
			Platform.runLater(()->{
				DataTableView dataTable = new DataTableView();
				try {
					if(tableMap.collect().size() > 0){
						if(type.equals("table"))
							dataTable.createTable(colNames,tableMap,ds_Name);
						else{
							Charts chart = new Charts(ds_Name);
							chart.createTableView(colNames, tableMap);
						}
					}else{
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("PRIDICT_IT");
						alert.setHeaderText(null);
						alert.setContentText("NO DATA FOUND.... ");
						alert.showAndWait();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});				
		}catch(Exception e){
			System.out.println(e.toString());
		}
	}
}

