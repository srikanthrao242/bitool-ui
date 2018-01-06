package com.pridictit.charts;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Row;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class PieChartGUI extends Charts{	
	final double SCALE_DELTA = 1.1;
	final StackPane zoomPane = new StackPane();
	final ScrollPane sp = new ScrollPane();
	
	public PieChartGUI(String ds_Name) {
		super(ds_Name);
	}
	
	class CreatePieChartData<T>{
		public PieChart.Data pieChartData(String category, T measure){			
			return new PieChart.Data(category, (double) measure);
		}
	}
    
	public void createPie(String category, String measure, String[] colNames,JavaRDD<Row> tableMap){
		List<String> columns = Arrays.asList(colNames);
		Stage stage = new Stage();
		
        int cIndex = columns.indexOf(category);
        int mIndex = columns.indexOf(measure);
 
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        
        tableMap.collect().forEach((Row row)->{
            	if(row.get(cIndex)!= null && row.get(mIndex) != null){
            		//if(!pieChartData.contains(new PieChart.Data((String)row.get(cIndex), (double)row.get(mIndex))))
            		String pa1 = String.valueOf(row.get(cIndex));
            		Number pa2 = (Number) row.get(mIndex);
            		pieChartData.add(new PieChart.Data(pa1, pa2.doubleValue()));
            }
		});
        final PieChart chart = new PieChart(pieChartData);
        chart.setTitle(ds_Name);
        chart.setLabelLineLength(10);
        chart.setLegendSide(Side.LEFT);
        chart.setPrefHeight(1000);
        chart.setPrefWidth(1000);
        chart.autosize();
       
        final Label caption = new Label("");
        caption.setTextFill(Color.DARKORANGE);
        caption.setStyle("-fx-font: 24 arial;");

        for (final PieChart.Data data : chart.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
                new EventHandler<MouseEvent>() {
                    @Override public void handle(MouseEvent e) {
                        caption.setTranslateX(e.getSceneX());
                        caption.setTranslateY(e.getSceneY());
                        caption.setText(String.valueOf(data.getPieValue()) + "%");
                     }
                });
        }
        zoomPane.getChildren().addAll(chart,caption);
        Scene scene = new Scene(zoomPane, 1000, 1000);
        stage.setScene(scene);
        stage.show();
	}
	
}
