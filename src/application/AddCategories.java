package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Row;

import com.pridictit.Core.GetJData;
import com.pridictit.Core.StaticVariables;
import com.pridictit.hive.GetData;


import scala.concurrent.Future;
import scala.concurrent.ExecutionContext;


import akka.dispatch.OnComplete;


import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class AddCategories{
	
	AddCategories _this = this;   
    private static int INSTANCE_ID = 0;
    public  Map<String,Object> SelCategories;
    public String dsName ;
    public Button tableView,reset,chartView;
    public HBox hbox1;
    public SplitPane splitPane1;
    public SplitPane splitPane2;
    public SplitPane splitPane3;
    public SplitPane splitPaneR;
    public TreeItem<Object> catrootNode;
    public VBox vSelected;
	Main main = new Main();
	public AddCategories(String dsName1){
		System.out.println("AddCategories invoked");
		SelCategories = new HashMap<String,Object>();
		dsName = dsName1;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addCategories(){
		try{	
			HBox hbox = new HBox();
	        //hbox.setTranslateX(20);	
			hbox.setId(dsName+"_"+INSTANCE_ID);
	        hbox.setTranslateY(20);
	        _this.splitPane1 = new SplitPane();
	        _this.splitPane1.setOrientation(Orientation.VERTICAL);
	        _this.splitPane1.setPrefSize(200, 300);
	        hbox.getChildren().add(_this.splitPane1);
	        
	        _this.splitPane2 = new SplitPane();
	        _this.splitPane2.setOrientation(Orientation.VERTICAL);
	        _this.splitPane2.setPrefSize(200, 300);
	        hbox.getChildren().add(_this.splitPane2);
	        
	        _this.splitPaneR = new SplitPane();
	        _this.splitPaneR.setOrientation(Orientation.VERTICAL);
	        _this.splitPaneR.setPrefSize(700, 300);
	        hbox.getChildren().add(_this.splitPaneR);
	        
	        _this.addRColumn();
	        
	        _this.splitPane3 = new SplitPane();
	        _this.splitPane3.setOrientation(Orientation.VERTICAL);
	        _this.splitPane3.setPrefSize(200, 300);
	        hbox.getChildren().add(_this.splitPane3);
	        
	        _this.vSelected = new VBox();
	        _this.vSelected.prefHeight(280);
	        HBox submitReset = new HBox();
	        _this.tableView = new Button("TableView");
	        _this.chartView = new Button("ChartView");
	        _this.reset = new Button("Reset");
	        submitReset.getChildren().add(_this.reset);
	        submitReset.getChildren().add(_this.tableView);
	        submitReset.getChildren().add(_this.chartView);
	        _this.splitPane3.getItems().addAll(_this.vSelected,submitReset);
	        
	        _this.chartView.setOnAction(new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent event) {
					// TODO Auto-generated method stub
					_this.submitCategories("chart");
				}	        	
	        });
	        _this.tableView.setOnAction(new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent event) {
					// TODO Auto-generated method stub
					_this.submitCategories("table");
				}	        	
	        });
	        _this.hbox1 = new HBox();
	        _this.splitPane2.getItems().addAll(_this.hbox1);
	        Set<String> dsNames = StaticVariables.CategoriesAndDataTypes.keySet();
	        _this.catrootNode =  new TreeItem<Object>("PREDICATES");
	        TreeItem selected =  new TreeItem("SELECTED PREDICATES");
	        TreeView selectedTree = new TreeView(selected);
	        _this.vSelected.getChildren().add(selectedTree);
	        selected.setExpanded(true);
	        _this.catrootNode.setExpanded(true);
	        if(dsNames.contains(_this.dsName.toString())){
	        	HashMap<String,String[]> category = StaticVariables.CategoriesAndDataTypes.get(_this.dsName);
	        	Set<String> noOfCat = category.keySet();
	        	noOfCat.forEach(key ->{
	        		TreeItem<Object> rootItem = new TreeItem<Object> (key);
		            rootItem.setExpanded(true);
	        		Object[] categories = category.get(key);
	        		for(Object cat : categories){
	        			TreeItem<Object> item = new TreeItem<Object> (cat);  
	                    rootItem.getChildren().add(item);
	        		}
	        		_this.catrootNode.getChildren().add(rootItem);
	        	});

    	        TreeView<Object> tree = new TreeView<Object> (_this.catrootNode);
    	        tree.getSelectionModel()
    	        	.selectedItemProperty()
    	        	.addListener((observable, oldValue, newValue) -> {
    	        		if(newValue.isLeaf()){
    	        			_this.hbox1.getChildren().clear();
    	    	        	System.out.println("Selected Text : " + newValue.getValue()+"        "+newValue.getParent().getValue());    	    	        	
    	    	        	AddSubCategories sub = new AddSubCategories(this);
    						sub.addingSubCategories(newValue,selected);
    	        		}	    	        	    	        
    	        });
    	        _this.splitPane1.getItems().addAll(tree);	        	
	        }			
			main.addNodetoTab(hbox);
			
		}catch(Exception e){
			
		}
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void submitCategories(String type){
		HashMap<String, ArrayList<HashMap<String, Object>>> sendresp = new HashMap<String, ArrayList<HashMap<String,Object>>>();
		try{
			ObservableList<Node> list = _this.vSelected.getChildren();
			list.forEach(node->{
				TreeView node1 = (TreeView) node;
				TreeItem seleTree = node1.getRoot();
				ObservableList chil = seleTree.getChildren();
				chil.forEach(child->{
					TreeItem child1 = (TreeItem) child;
					if(!child1.isLeaf()){
						_this.isNumericDate(child1,sendresp);
					}
				});
			});
			GetData gd1 = new GetData();
			Future<HashMap<String, Object>> response = gd1.getTableData(sendresp,_this.dsName);
			response.onComplete(new OnComplete<HashMap<String, Object>>(){
				@Override
				public void onComplete(Throwable arg0, HashMap<String, Object> result) throws Throwable {
					// TODO Auto-generated method stub
					if((result.get("msg").toString()).equals("success")){
						String[] columns = (String[])result.get("colNames");						
						TableAndCategories tc = new TableAndCategories(_this,(JavaRDD<Row>)result.get("data"),columns,_this.dsName);
						tc.createTable(type);						
					}					
				}				
			},ExecutionContext.Implicits$.MODULE$.global());	
					
		}catch(Exception e){
			
		}
	}
	public void addRColumn(){
		try{
			VBox vBox = new VBox();
			HBox hBox = new HBox();
			Text addcolText = new Text("ADD COLUMNS:=============>  ");
			vBox.getChildren().addAll(addcolText,hBox);
			
			SplitPane splitPaneR1 = new SplitPane();
	        splitPaneR1.setOrientation(Orientation.VERTICAL);
	        splitPaneR1.setPrefSize(300, 300);
	        hBox.getChildren().add(splitPaneR1);
	        
	        SplitPane splitPaneR2 = new SplitPane();
	        splitPaneR2.setOrientation(Orientation.VERTICAL);
	        splitPaneR2.setPrefSize(700, 300);
	        hBox.getChildren().add(splitPaneR2);
	        _this.splitPaneR.getItems().add(hBox); 
			
		}catch(Exception e){
			
		}
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void isNumericDate(TreeItem child1, HashMap<String, ArrayList<HashMap<String, Object>>> sendresp) {
		//HashMap<String, ArrayList<HashMap<String, Object>>> sendresp = new HashMap<String, ArrayList<HashMap<String,Object>>>();
		try{
			ArrayList<HashMap<String,Object>> sendresparr = new ArrayList<HashMap<String,Object>>();
			HashMap<String,Object> sendresp1 = new HashMap<String,Object>();
			//sendresp.put(child1.getValue().toString(), sendresp1);
			_this.catrootNode.getChildren().contains(child1);
			GetJData gd = new GetJData();
			String type = _this.getType(child1.getValue().toString());
			String aType = gd.getDataType(type);
			if(!sendresp1.containsKey(child1.getValue().toString())){
				sendresp1.put(child1.getValue().toString(), new ArrayList());
				if(aType.equals("numeric") || aType.equals("date") || aType.equals("timestamp")){				
					child1.getChildren().forEach(child2->{
						TreeItem child3 = (TreeItem) child2;
						ArrayList x = (ArrayList)sendresp1.get(child1.getValue().toString());
						String nchild = child3.getValue().toString();
						String[] y = nchild.split(" to ");					
						x.add(y[0]);
						x.add(y[1]);
					});						
				}else{
					child1.getChildren().forEach(child2->{
						TreeItem child3 = (TreeItem) child2;
						ArrayList x = (ArrayList)sendresp1.get(child1.getValue().toString());
						x.add(child3.getValue().toString());									
					});
				}
				if(sendresp.containsKey(aType)){
					sendresp.get(aType).add(sendresp1);						
				}else{
					sendresparr.add(sendresp1);
					sendresp.put(aType, sendresparr);
				}
			}
			System.out.println(sendresp.toString());
				
		}catch(Exception e){
			
		}
		
	} 
	public String getType(String sele){
		String type = "";
		try{
			HashMap<String,String[]> category = StaticVariables.CategoriesAndDataTypes.get(_this.dsName);
			Set<String> noOfCat = category.keySet();
			for(String k : noOfCat){
				Object[] categories = category.get(k);
				for(Object cat : categories){
					if(sele.equals(cat.toString())){
						type = k;
						break;
					}
				}
				if(!type.equals("")){
					break;
				}
			}
		}catch(Exception e){
			
		}
		return type;
		
	}
	

}

