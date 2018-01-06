package application;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.pridictit.Core.GetJData;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.TreeItem;
import javafx.util.Callback;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddSubCategories {
	AddSubCategories _this = this;
	public Map<String,Object> SelCategories;
	public String dsName;
	public HBox hbox1;
	public AddSubCategories(AddCategories addCat){
		SelCategories = addCat.SelCategories;
		dsName = addCat.dsName;
		hbox1 = addCat.hbox1;
	}
	//public static Map<String,Object> SelCategories = new HashMap<String,Object>();
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addingSubCategories(TreeItem<Object>  category,TreeItem selected){
		try{
			VBox vbox = new VBox();	
			String selCat = category.getValue().toString();
			String type = category.getParent().getValue().toString();
			vbox.getChildren().add(new Text(selCat));
			GetJData gd = new GetJData();
			String aType = gd.getDataType(type);
			if(aType.equals("numeric")){				
				if(!SelCategories.containsKey(selCat)){
					this.addNumeric(dsName, hbox1, selected, selCat, vbox);		
				}else{
					vbox.getChildren().add((Pane)SelCategories.get(selCat));
				}						
			}else if(aType.equals("date")){
				if(!SelCategories.containsKey(selCat)){
					this.addDates(dsName, hbox1, selected, selCat, vbox);		
				}else{
					vbox.getChildren().add((Pane)SelCategories.get(selCat));
				}
			}else if(aType.equals("timestamp")){
				if(!SelCategories.containsKey(selCat)){
					this.addTimeStamps(dsName, hbox1, selected, selCat, vbox);		
				}else{
					vbox.getChildren().add((Pane)SelCategories.get(selCat));
				}
			}else{		
				if(!SelCategories.containsKey(selCat)){
					vbox.setSpacing(2);
					TreeItem rootItem = new TreeItem(selCat);
					rootItem.setExpanded(true);
					selected.setExpanded(true);
					selected.getChildren().add(rootItem);
					ListView<Object> list = new ListView<Object>();
					vbox.getChildren().add(list);
					Object[] result = gd.getSubCategories(dsName, selCat);
				    for(int i=0;i<result.length;i++){
				    	if(result[i] != null){	    		
				    		list.getItems().add(result[i]);
				    	}
				    }
				    list.setCellFactory(CheckBoxListCell.forListView(new Callback<Object, ObservableValue<Boolean>>() {
				        @Override
				        public ObservableValue<Boolean> call(Object item) {
				            BooleanProperty observable = new SimpleBooleanProperty();
				            observable.addListener((obs, wasSelected, isNowSelected) -> {
				            	System.out.println("Check box for "+item+" changed from "+wasSelected+" to "+isNowSelected);	            	 
				            	if(isNowSelected){	
				            		TreeItem sub = new TreeItem (item);
				                    rootItem.getChildren().add(sub);
				            	}else{	
				            		ObservableList<TreeItem> a = rootItem.getChildren();
				            		for(TreeItem sub : a){
				            			String v = sub.getValue().toString();
				            			System.out.println(v);
				            			if(v.equals(item)){
				            				int b = a.indexOf(sub);
				    	            		rootItem.getChildren().remove(b);
				            			}
				            		}	            		
				            	}
				            });
				            return observable ;
				        }
				    }));
				    SelCategories.put(selCat, list);
				}else{
					vbox.getChildren().add((ListView<Object>)SelCategories.get(selCat));
				}
			}
		    hbox1.getChildren().add(vbox);
		}catch(Exception e){
			System.out.println(e.toString());
		}
		
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addNumeric(String dsName,HBox hbox1,TreeItem selected,String selCat,VBox vbox){
		try{
			Pane pane = new Pane();
			GridPane grid = new GridPane();
			grid.setPadding(new Insets(10, 10, 10, 10));
			grid.setMinSize(200, 300);
			grid.setMaxWidth(300);
			grid.setHgap(10);
			grid.setVgap(10);
			//vbox.setPrefWidth(300);
			vbox.setSpacing(5);
			GetJData gd = new GetJData();
			
			HashMap<String,Object> result = gd.getMaxMinValues(dsName, selCat);
			if(result.get("msg").equals("success")){
				Object max = result.get("max");
				Object min = result.get("min");
				
				System.out.println(max.toString());
				Text maximum = new Text("MAXIMUM: ( "+max+" ) : ");
				grid.add(maximum,0, 0, 1, 1);
				Spinner<Double> ispinner1 = new Spinner<>((Double)min, (Double)max, 0.5);
				grid.add(ispinner1,0, 1, 1, 1);
				
				
				System.out.println(min.toString());
				Text minimum = new Text("MINIMUM: ( "+min+" ) : ");
				grid.add(minimum,0, 2, 1, 1);
				Spinner<Double> ispinner2 = new Spinner<>((Double)min, (Double)max, 0.5);
				grid.add(ispinner2,0, 3, 1, 1);
							
				
				Button submit = new Button(" submit ");
				Button reset = new Button("reset");
				
				HBox subRe = new HBox(5);
				subRe.getChildren().addAll(submit,reset);
				grid.add(subRe,0, 4, 1, 1);
				pane.getChildren().add(grid);
				vbox.getChildren().add(pane);
				if(!SelCategories.containsKey(selCat)){
					TreeItem rootItem = new TreeItem(selCat);
					rootItem.setExpanded(true);
					selected.setExpanded(true);
					selected.getChildren().add(rootItem);
					//vbox.getChildren().add(grid);
					submit.setOnAction(new EventHandler<ActionEvent>(){						
						public void handle(ActionEvent event) {
							rootItem.getChildren().clear();
							TreeItem sub = new TreeItem (ispinner2.getValue()+" to "+ispinner1.getValue());
		                    rootItem.getChildren().add(sub);
			            }
					});
					reset.setOnAction(new EventHandler<ActionEvent>(){
						public void handle(ActionEvent event) {
							rootItem.getChildren().clear();							
			            }
					});						
				}
			}
			SelCategories.put(selCat, grid);
		}catch(Exception e){
			
		}
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addDates(String dsName,HBox hbox1,TreeItem selected,String selCat,VBox vbox){
		try{
			Pane pane = new Pane();
			GridPane grid = new GridPane();
			grid.setPadding(new Insets(10, 10, 10, 10));
			grid.setMinSize(200, 300);
			grid.setMaxWidth(300);
			grid.setHgap(10);
			grid.setVgap(10);
			vbox.setSpacing(1);
			GetJData gd = new GetJData();	
			String pattern = "yyyy-MM-dd";
			HashMap<String,Object> result = gd.getMaxMinValues(dsName, selCat);
			if(result.get("msg").equals("success")){
				Object max = result.get("max");				
				Text maximum = new Text("MAXIMUM: ( "+max+" ) : ");
				grid.add(maximum,0, 0, 1, 1);
				
				DatePicker datePicker1 = new DatePicker();
				datePicker1.setPromptText(pattern.toLowerCase());
				grid.add(datePicker1,0, 1, 1, 1);
				datePicker1.setConverter(new StringConverter<LocalDate>() {
				     DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
				     @Override 
				     public String toString(LocalDate date) {
				         if (date != null) {
				             return dateFormatter.format(date);
				         } else {
				             return "";
				         }
				     }
				     @Override 
				     public LocalDate fromString(String string) {
				         if (string != null && !string.isEmpty()) {
				             return LocalDate.parse(string, dateFormatter);
				         } else {
				             return null;
				         }
				     }
				 });				
				
			    Object min = result.get("min");
			    Text minimum = new Text("MINIMUM: ( "+min+" ) : ");
				grid.add(minimum,0, 2, 1, 1);
				
				DatePicker datePicker2 = new DatePicker();
				datePicker2.setPromptText(pattern.toLowerCase());
				grid.add(datePicker2,0, 3, 1, 1);
				
				datePicker2.setConverter(new StringConverter<LocalDate>() {
				     DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
				     @Override 
				     public String toString(LocalDate date) {
				         if (date != null) {
				             return dateFormatter.format(date);
				         } else {
				             return "";
				         }
				     }
				     @Override 
				     public LocalDate fromString(String string) {
				         if (string != null && !string.isEmpty()) {
				             return LocalDate.parse(string, dateFormatter);
				         } else {
				             return null;
				         }
				     }
				 });				
			    Button submit = new Button(" submit ");
				Button reset = new Button("reset");
				
				HBox subRe = new HBox(5);
				subRe.getChildren().addAll(submit,reset);
				grid.add(subRe,0, 4, 1, 1);
				pane.getChildren().add(grid);
				vbox.getChildren().add(pane);
				if(!SelCategories.containsKey(selCat)){
					TreeItem rootItem = new TreeItem(selCat);
					rootItem.setExpanded(true);
					selected.setExpanded(true);
					selected.getChildren().add(rootItem);
					//vbox.getChildren().add(grid);
					submit.setOnAction(new EventHandler<ActionEvent>(){
						public void handle(ActionEvent event) {
							rootItem.getChildren().clear();
							TreeItem sub = new TreeItem (datePicker2.getValue().toString()+
									" to "+datePicker1.getValue().toString());
		                    rootItem.getChildren().add(sub);
			            }
					});
					reset.setOnAction(new EventHandler<ActionEvent>(){
						public void handle(ActionEvent event) {
							rootItem.getChildren().clear();
							datePicker2.setValue(null);
							datePicker1.setValue(null);								
			            }
					});				
					
				}
			}			
			SelCategories.put(selCat, grid);
			
		}catch(Exception e){
			
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addTimeStamps(String dsName,HBox hbox1,TreeItem selected,String selCat,VBox vbox){
		try{
			Pane pane = new Pane();
			GridPane grid = new GridPane();
			grid.setPadding(new Insets(10, 10, 10, 10));
			grid.setMinSize(200, 300);
			grid.setMaxWidth(300);
			grid.setHgap(10);
			grid.setVgap(10);
			vbox.setSpacing(1);
			GetJData gd = new GetJData();	
			String pattern = "yyyy-MM-dd";
			HashMap<String,Object> result = gd.getMaxMinValues(dsName, selCat);
			if(result.get("msg").equals("success")){
				Object max = result.get("max");	
				Object min = result.get("min");
				Text maximum = new Text("MAXIMUM: ( "+max+" ) : ");
				grid.add(maximum,0, 0, 1, 1);
				DatePicker datePicker1 = new DatePicker();
				datePicker1.setPromptText(pattern.toLowerCase());
				grid.add(datePicker1,0, 1, 1, 1);
				datePicker1.setConverter(new StringConverter<LocalDate>() {
				     DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
				     @Override 
				     public String toString(LocalDate date) {
				         if (date != null) {
				             return dateFormatter.format(date);
				         } else {
				             return "";
				         }
				     }
				     @Override 
				     public LocalDate fromString(String string) {
				         if (string != null && !string.isEmpty()) {
				             return LocalDate.parse(string, dateFormatter);
				         } else {
				             return null;
				         }
				     }
				 });
				Text time1 = new Text("TIME:");
				grid.add(time1,0, 2, 1, 1);
				HBox timehb1 = new HBox(2);
				Spinner<Double> maxpinner1 = new Spinner<>(1,24,00);
				maxpinner1.setPrefWidth(60);
				Spinner<Double> maxpinner2 = new Spinner<>(1,60,00);
				maxpinner2.setPrefWidth(60);
				Spinner<Double> maxpinner3 = new Spinner<>(1,60,00);
				maxpinner3.setPrefWidth(60);
				timehb1.getChildren().addAll(maxpinner1,maxpinner2,maxpinner3);
				grid.add(timehb1,0, 3, 1, 1);
				
			    
			    Text minimum = new Text("MINIMUM: ( "+min+" ) : ");
				grid.add(minimum,0, 4, 1, 1);
				
				DatePicker datePicker2 = new DatePicker();
				datePicker2.setPromptText(pattern.toLowerCase());
				grid.add(datePicker2,0, 5, 1, 1);
				
				datePicker2.setConverter(new StringConverter<LocalDate>() {
				     DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
				     @Override 
				     public String toString(LocalDate date) {
				         if (date != null) {
				             return dateFormatter.format(date);
				         } else {
				             return "";
				         }
				     }
				     @Override 
				     public LocalDate fromString(String string) {
				         if (string != null && !string.isEmpty()) {
				             return LocalDate.parse(string, dateFormatter);
				         } else {
				             return null;
				         }
				     }
				 });
				Text time2 = new Text("TIME:");
				grid.add(time2,0, 6, 1, 1);
				
				HBox timehb2 = new HBox(2);
				Spinner<Double> minpinner1 = new Spinner<>(1,24,00);
				minpinner1.setPrefWidth(60);
				Spinner<Double> minpinner2 = new Spinner<>(1,60,00);
				minpinner2.setPrefWidth(60);
				Spinner<Double> minpinner3 = new Spinner<>(1,60,00);
				minpinner3.setPrefWidth(60);
				timehb2.getChildren().addAll(minpinner1,minpinner2,minpinner3);
				grid.add(timehb2,0, 7, 1, 1);
				
			    Button submit = new Button(" submit ");
				Button reset = new Button("reset");
				
				HBox subRe = new HBox(5);
				subRe.getChildren().addAll(submit,reset);
				grid.add(subRe,0, 8, 1, 1);
				pane.getChildren().add(grid);
				vbox.getChildren().add(pane);
				if(!SelCategories.containsKey(selCat)){
					TreeItem rootItem = new TreeItem(selCat);
					rootItem.setExpanded(true);
					selected.setExpanded(true);
					selected.getChildren().add(rootItem);
					//vbox.getChildren().add(grid);
					submit.setOnAction(new EventHandler<ActionEvent>(){
						public void handle(ActionEvent event) {
							rootItem.getChildren().clear();
							TreeItem sub = new TreeItem (datePicker2.getValue().toString()+"T"+
									maxpinner1.getValue()+":"+maxpinner2.getValue()+":"+maxpinner3.getValue()+"Z"+
									" to "+datePicker1.getValue().toString()+"T"+
									minpinner1.getValue()+":"+minpinner2.getValue()+":"+minpinner3.getValue()+"Z"
									);
		                    rootItem.getChildren().add(sub);
			            }
					});
					reset.setOnAction(new EventHandler<ActionEvent>(){
						public void handle(ActionEvent event) {
							rootItem.getChildren().clear();
							datePicker2.setValue(null);
							datePicker1.setValue(null);	
							
			            }
					});				
					
				}
			}			
			SelCategories.put(selCat, grid);
			
		}catch(Exception e){
			
		}
		
	}
	public String fromCalendar(String timestamp) {
        @SuppressWarnings("deprecation")
		Date date = new Date(timestamp);
        String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .format(date);
        return formatted.substring(0, 22) + ":" + formatted.substring(22);
    }
	
}
