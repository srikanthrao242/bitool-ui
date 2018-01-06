package com.pridict.RInterface;

import java.util.HashMap;
import java.util.Map;
import com.pridictit.hive.*;

import rcaller.RCaller;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;


public class RAddRFunctions {
	public RAddRFunctions(){
		
	}
	public void addGUIButtons(){
		
	}
	@SuppressWarnings("deprecation")
	public void getDataFrame(Map<String, Object>  msg){
		String dataset = (String) msg.get("dataset");
		HashMap<String,Object> input = new HashMap<String,Object>();
		try{
			input.put("dsName", dataset);
			System.out.println(dataset);
			GetData gd = new GetData();
			HashMap<String, Object> res = gd.getDataFrame(input);
			System.out.println(res.get("msg").toString());
			
			/*Dataset<Row> dataframe = (Dataset<Row>) res.get("df");
			dataframe.show();
			RCaller caller = new RCaller();
			caller.setRscriptExecutable("./R/bin/Rscript");
			caller.addJavaObject("datafram", dataframe);*/
		/*	RCode rCode = RCode.create();
			caller.cleanRCode();*/
			
			
		}catch(Exception e){
			System.out.println(e.toString());
		}

		

		
		
	}

}
