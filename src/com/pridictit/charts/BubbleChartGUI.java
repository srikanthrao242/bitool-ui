package com.pridictit.charts;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Row;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class BubbleChartGUI  extends Charts {
	
	final CategoryAxis xAxis = new CategoryAxis();
    final NumberAxis yAxis = new NumberAxis();
    final StackPane zoomPane = new StackPane();
    final ScrollPane sp = new ScrollPane();
    final double SCALE_DELTA = 1.1;

	public BubbleChartGUI(String ds_Name) {
		super(ds_Name);
		// TODO Auto-generated constructor stub
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void createBubbleChartGUI(String category, String measure, String[] colNames,JavaRDD<Row> tableMap){
		List<String> columns = Arrays.asList(colNames);
		Stage stage = new Stage();
		Group group = new Group();
		Scene scene = new Scene(group);
		xAxis.setLabel(category);
		yAxis.setLabel(measure);
		yAxis.setAutoRanging(false);
		yAxis.setMinWidth(1);
		//yAxis.setUpperBound(50);
		XYChart.Series series = new XYChart.Series();
		
		final BubbleChart<String,Number> lineChart = 
                new BubbleChart<String,Number>(xAxis,yAxis);
		
        int cIndex = columns.indexOf(category);
        int mIndex = columns.indexOf(measure);
        
        tableMap.collect().forEach((Row row)->{
        	if(row.get(cIndex)!= null && row.get(mIndex) != null){
        		series.getData().add(new XYChart.Data(String.valueOf(row.get(cIndex)), row.get(mIndex)));
	        }
		});
        
        lineChart.getData().add(series);
        lineChart.setPrefHeight(1000);
        lineChart.setPrefWidth(1000);
        zoomPane.getChildren().add(lineChart);
        sp.setContent(zoomPane);
        ((Group) scene.getRoot()).getChildren().add(sp);
        zoomPane.setOnScroll(new EventHandler<ScrollEvent>() {
    	  @Override 
    	  public void handle(ScrollEvent event) {
    	    event.consume();
    	    if (event.getDeltaY() == 0) {
    	      return;
    	    }
    	    double scaleFactor =
    	      (event.getDeltaY() > 0)
    	        ? SCALE_DELTA
    	        : 1/SCALE_DELTA;

    	    group.setScaleX(group.getScaleX() * scaleFactor);
    	    group.setScaleY(group.getScaleY() * scaleFactor);
    	  }
    	});
        stage.setScene(scene);
        stage.show();		

		
	}

}
