package com.pridictit.charts;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Row;

import com.pridictit.Core.StaticVariables;

import application.DataTableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Charts {
	Stage stage = new Stage();		
	Scene scene = new Scene(new Group());
	final VBox vbox = new VBox();
	final GridPane grid = new GridPane();
	String ds_Name ;
	Charts _this = this;
	
	public Charts(String ds_Name){
		this.ds_Name = ds_Name;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void createTableView(String[] colNames,JavaRDD<Row> tableMap) throws Exception{
		DataTableView tbView = new DataTableView();
		TableView table  = new TableView();
        table.setEditable(true);
        ObservableList tl = tbView.t_model(colNames);
        ObservableList<ObservableList> rl = tbView.t_row(tableMap);
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
        vbox.setSpacing(2);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        grid.setVgap(8);
    	grid.setHgap(10);
    	grid.setPadding(new Insets(5, 5, 5, 5));
        vbox.getChildren().addAll( table,grid);
        ScrollPane sp = new ScrollPane();
        sp.setContent(vbox);
        this.createChartsMeasuresAndCat(colNames,tableMap);
        ((Group) scene.getRoot()).getChildren().addAll(sp);
        stage.setTitle(ds_Name);
        stage.setScene(scene);
        stage.show();
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void createChartsMeasuresAndCat(String[] colNames,JavaRDD<Row> tableMap){
    	grid.add(new Label("CATEGORIES: "), 0, 0);
    	grid.add(new Label("MEASURES : "), 2, 0);
		final ComboBox categories = new ComboBox();
		final ComboBox measures = new ComboBox();
		final ComboBox charts = new ComboBox();
		charts.getItems().addAll("Pie","Line","Area","Bubble","Scatter","Bar");
		HashMap<String,String[]> catAndDt = StaticVariables.CategoriesAndDataTypes.get(ds_Name);
		Iterator<String> keys = catAndDt.keySet().iterator();
		while(keys.hasNext()){
			String type = keys.next();
			if(catAndDt.containsKey(type)){
				String[] values = catAndDt.get(type);
				String mC = this.getMeasureOrCat(type);
				if(mC.equals("category")){
					for(int i=0;i<values.length;i++){
						categories.getItems().add(values[i]);
					}
				}else{
					for(int i=0;i<values.length;i++){
						measures.getItems().add(values[i]);
					}
				}
			}			
		}
		grid.add(categories, 1, 0);
		grid.add(measures, 3, 0);
		Button chartView = new Button("ChartView");
		grid.add(new Label("CHART : "), 5, 0);
		grid.add(charts, 6, 0);
		grid.add(chartView, 8, 0);
		chartView.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				String cat = (String) categories.getValue();
				String mes = (String) measures.getValue();
				String chartType = (String) charts.getValue();
				_this.callChartView(cat,mes,chartType,colNames,tableMap);
			}			
		});
	}
	public void callChartView(String category, String measure, String chart,String[] colNames,JavaRDD<Row> tableMap){		
		if(chart.equals("Pie")){
			PieChartGUI pie = new PieChartGUI(ds_Name);
			pie.createPie(category,measure,colNames,tableMap);
		}else if(chart.equals("Line")){
			LineChartGUI line = new LineChartGUI(ds_Name);
			line.createLineChart(category, measure, colNames, tableMap);
		}else if(chart.equals("Area")){
			AreaChartGUI area = new AreaChartGUI(ds_Name);
			area.createAreaChartGUI(category, measure, colNames, tableMap);
		}else if(chart.equals("Bubble")){
			BubbleChartGUI bubble = new BubbleChartGUI(ds_Name);
			bubble.createBubbleChartGUI(category, measure, colNames, tableMap);
		}else if(chart.equals("Scatter")){
			ScatterChartGUI scatter = new ScatterChartGUI(ds_Name);
			scatter.createScatterChartGUI(category, measure, colNames, tableMap);
		}else{
			BarChartGUI bar = new BarChartGUI(ds_Name);
			bar.createBarChartGUI(category, measure, colNames, tableMap);
		}
		
	}
	public String getMeasureOrCat(String type){
		String result = "";
		if(type.toLowerCase().equals("int") 
	            ||type.toLowerCase().equals("float") 
	            ||type.toLowerCase().equals("double")
	            ||type.toLowerCase().equals("decimal")
	            ||type.toLowerCase().equals("tinyint")
	            ||type.toLowerCase().equals("smallint")
	            ||type.toLowerCase().equals("bigint")
	            ||type.toLowerCase().equals("numerics")){
				result = "measure";
			}else{
				result = "category";
			}
		return result;
	}
}