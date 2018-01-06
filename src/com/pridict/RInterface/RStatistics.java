package com.pridict.RInterface;

import java.util.HashMap;
import java.util.Map;

import rcaller.RCaller;

public class RStatistics {
	
	public RStatistics(){		
		
	}
	public  enum RStats{
		addRStatsGUI,getDataFrame
		
	}
	public Map<String, Object>  msgexec(Map<String, Object>  msg){
		System.out.println("Executing R functions............");
		String msgType=(String) msg.get("mtype");
		RCaller caller = new RCaller();
		caller.setRscriptExecutable("./R/bin/Rscript");
		caller.cleanRCode();
		Map<String, Object>  resp=new HashMap<String, Object> ();
		try{
			switch (RStats.valueOf(msgType)){
			case addRStatsGUI:RAddRFunctions rfun = new RAddRFunctions();
			rfun.addGUIButtons();
			break;
			case getDataFrame:RAddRFunctions rfun1 = new RAddRFunctions();
			rfun1.getDataFrame(msg);
			break;
			}
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resp;
		
	}
	public void listOfStatistics(){
		
	}

}
