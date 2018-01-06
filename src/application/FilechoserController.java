/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;


import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import com.pridictit.Core.GetJData;
import com.pridictit.Core.StaticVariables;
import com.pridictit.hive.Parquet;

import scala.concurrent.Future;
import akka.dispatch.*;
import scala.concurrent.ExecutionContext;

import application.Main;

/**
 * FXML Controller class
 *
 * @author srikanth
 */
public class FilechoserController implements Initializable 
{
	
    @FXML
    private TextField tablename;
    
    @FXML
    private HBox hbox;
    
    @FXML
    private TextField filepath;

    @FXML
    private Button filechoose;
    
    @FXML
    private Label uploadlabel;
    
    Main main = new Main();
    
    @FXML
    void extrachFile(ActionEvent event) {
    	
    	FileChooser fc = new FileChooser();    	
    	File selectedFile = fc.showOpenDialog(null);    	
    	if(selectedFile != null){
    		filepath.setText(selectedFile.toString());
        }
    }    
    @FXML
    private void saveParquet(ActionEvent event) throws InterruptedException, ExecutionException {    	
    	if(filepath.getText() == null || tablename.getText() == null){
    		if(filepath.getText() == null){
        		uploadlabel.setText("Please Choose A File");
        	}        	
        	if(tablename.getText() == null){
        		uploadlabel.setText("Please Give your Table Name");
        	}
    	}else if(StaticVariables.DatasetNames.contains(tablename.getText().toString())){
    		uploadlabel.setText("Table Name already exist");
    	}else{
    		String tableName = tablename.getText();    		
    		String filePath = filepath.getText();    		
    		((Node)(event.getSource())).getScene().getWindow().hide();    		
    		this.file_Upload(filePath,tableName);    		
    	}
    	
    }
	public void file_Upload(String filePath,String tableName) throws InterruptedException, ExecutionException
    {
    	HashMap<String, Object > req = new HashMap<String,Object>();    	
		req.put("tablename" , tableName);		
    	req.put("csvFile" ,filePath);    	
    	req.put("delimiter" ,",");    	
    	Parquet parquet = new Parquet();    	
    	Future<HashMap<String, Object>> response = parquet.createTable(req);    	
    	response.onComplete(new OnComplete<HashMap<String, Object>>(){
			@Override
			public void onComplete(Throwable arg0, HashMap<String, Object> result) throws Throwable {	    		
	    		if((result.get("msg").toString()).equals("Success"))
	    		{
	    			Platform.runLater(()->{
	    				Alert alert = new Alert(AlertType.INFORMATION);
	    				alert.setTitle("Pridict_It Information");
	    				alert.setHeaderText("Information Dialog");
	    				alert.setContentText(tableName+" is Successfully Uploaded ... ");
	    				HashMap<String, Object> data = new HashMap<String, Object>();
	    				data.put("db_id", Main.datasets.size()+1);
	    				data.put("db_name", tableName);
	    				data.put("user_id", 1);
	    				HashMap<String, Object> result1 = parquet.insertDatasource_info(data);
	    				if(result1.get("msg").toString().equals("Success")){
	    					Main.datasets.add(tableName);
	    					main.addTableMenuItem(tableName);
	    					StaticVariables.DatasetNames.add(tableName);
	    					GetJData gD = new GetJData();
	    					gD.getCategories(tableName);
		    				alert.showAndWait();
	    				}else{
	    					System.out.println(result1.get("error"));
	    				}
	    			});
	    		}
			}
        }, ExecutionContext.Implicits$.MODULE$.global());
    	
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    	//categories.getItems().addAll(fcitems);
    	
    }    
    
}
