package com.pridictit.Core;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import com.pridictit.Core.StaticVariables;
import com.pridictit.hive.GetData;
import application.Main;

public class GetJData {
	
	GetData gd = new GetData();
	Main main = new Main();
	public GetJData(){
		System.out.println("GetJData invoked");
	}
	public void getDatasources(){
		try{
			HashMap<String,Object> dsMap = gd.getDataSources();
			if((dsMap.get("msg").toString()).equals("Success")){
				@SuppressWarnings("unchecked")
				HashMap<String, Object> data = (HashMap<String, Object>) dsMap.get("data");
				for(String key : data.keySet()){
					String[] obj = (String[]) data.get(key);
					for(String tn : obj){
						Main.datasets.add(tn);
						StaticVariables.DatasetNames.add(tn);
						main.addTableMenuItem(tn);
					}
				}
			}			
		}catch(Exception e){
			System.out.println(e.toString());
		}		
	}
	public void getCategories(String datasource){
	   try{
			//GetData gd = new GetData();
			HashMap<String, Object> result = gd.getCategories(datasource);
			if(result.get("msg").toString().equals("Success")){
				@SuppressWarnings("unchecked")
				HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
				HashMap<String,String[]> cat = new HashMap<String,String[]>();
				data.forEach((k,v)->{						
					String[] arr = (String[]) v;
					if(arr.length > 0){
						cat.put(k, arr);
					}					
				});
				StaticVariables.CategoriesAndDataTypes.put(datasource, cat);
			}else{
				System.out.println("error");
			}
			
		}catch(Exception e){
			System.out.println(e.toString());
		}
	}
	@SuppressWarnings("unchecked")
	public void getCategories(){
		try{
			StaticVariables.DatasetNames.forEach(dsName->{
				GetData gd = new GetData();
				HashMap<String, Object> result = gd.getCategories(dsName);
				if(result.get("msg").toString().equals("Success")){
					HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
					HashMap<String,String[]> cat = new HashMap<String,String[]>();
					data.forEach((k,v)->{	
						String[] arr = (String[]) v;
						System.out.println(k+"     "+v.toString());
						if(arr.length > 0){
							cat.put(k, (String[]) v);
						}						
					});
					StaticVariables.CategoriesAndDataTypes.put(dsName, cat);
				}else{
					System.out.println("error");
				}
				
			});
			
		}catch(Exception e){
			System.out.println(e.toString());
		}
	}
	@SuppressWarnings("unchecked")
	public Object[] getSubCategories(String dsName,String predicate){
		//ArrayList<Object> result = new ArrayList<Object>();
		try{
			HashMap<String,Object> input = new HashMap<String,Object>();
			input.put("dsName", dsName);
			input.put("predicate", predicate);
			HashMap<String,Object> output = gd.getSubCategories(input);
			if(output.get("msg").toString().equals("Success")){
				System.out.println(output.get("data").toString());
				HashMap<String,Object> output1 =  (HashMap<String, Object>) output.get("data");
				if(output1.containsKey(predicate)){
					System.out.println(output1.toString());
					Object[] result1 = (Object[]) output1.get(predicate);
					return result1;
					//result = result1;
				}else
					return null;
				//System.out.println(result.size());
				//return result;
			}else
			return null;
			
		}catch(Exception e){
			System.out.println(e.toString());
		}
		//return result;
		return null;
	}
	public HashMap<String,Object> getMaxMinValues(String dsName,String predicate){
		HashMap<String,Object> result = new HashMap<String,Object>();
		try{
			HashMap<String,Object> input = new HashMap<String,Object>();
			input.put("dsName", dsName);
			input.put("predicate", predicate);
			HashMap<String,Object> output = gd.getMaxMin(input);
			if(output.get("msg").toString().equals("Success")){
				Object Maximum = output.get("max");
				Object Minimum = output.get("min");
				result.put("msg", "success");
				result.put("max", Maximum);
				result.put("min", Minimum);
				return result;
			}else{
				result.put("msg", "error");
				return result;
			}
			
		}catch(Exception e){
			result.put("msg", "error");
			return result;
		}
	}
	public String getDataType(String type){
		try{
			if(type.toLowerCase().equals("int") 
	            ||type.toLowerCase().equals("float") 
	            ||type.toLowerCase().equals("double")
	            ||type.toLowerCase().equals("decimal")
	            ||type.toLowerCase().equals("tinyint")
	            ||type.toLowerCase().equals("smallint")
	            ||type.toLowerCase().equals("bigint")
	            ||type.toLowerCase().equals("numerics")){
				return "numeric";
			}else if(type.toLowerCase().equals("timestamp")){
				return "timestamp";
			}else if(type.toLowerCase().equals("date")){
				return "date";
			}else{
				return "string";
			}
		}catch(Exception e){
			return "error";
		}
	}
   public HashMap<String,Object> getTableMap(HashMap<String,Object> input){
	   HashMap<String,Object> result = new HashMap<String,Object>();
	   try{
		   
		   
	   }catch(Exception e){
			//return "error";
	   }
	   return result;
   }
   public  String isDateValid(Object max)
    {
        String returnVal1 = "";
        /*
         * Set the permissible formats.
         * A better approach here would be to define all formats in a .properties file
         * and load the file during execution.
         */
        String[] permissFormats = new String[]{"MM/dd/yyyy hh:mm:ss.sss","MM/dd/yyyy hh:mm:ss","yyyy-MM-dd hh:mm:ss.sss","yyyy-MM-dd hh:mm","dd/MM/yyyy",
                "dd-MM-yyyy","MM-dd-yyyy","ddMMyyyy"};
        /*
         * Loop through array of formats and validate using JAVA API.
         */
        SimpleDateFormat sdfObj = new SimpleDateFormat();
        sdfObj.setLenient(false);
        ParsePosition position = new ParsePosition(0);
        for (int i = 0; i < permissFormats.length; i++) 
        {
            sdfObj.applyPattern(permissFormats[i]);
            position.setIndex(0);
            position.setErrorIndex(-1);
            sdfObj.parse(max.toString(), position);
            if (position.getErrorIndex() == -1)
            {
                returnVal1 = permissFormats[i];
                System.out.println("Looks like a valid date for Date Value :"+max.toString()+": For Format:"+permissFormats[i]);
                break;
            }
            else
            {
                System.out.println("Parse Failed Occured for Date Value :"+max.toString()+":And Format:"+permissFormats[i]);
            }
        }
        return returnVal1;
    }
	
}
