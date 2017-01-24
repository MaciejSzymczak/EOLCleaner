import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.FileWriter;

/**
 * This program deletes EOLs from any CSV file / deletes nested commas
 * 
 * @author Maciej Szymczak
 * @version 2016.11.10
 */

public class EOLCleaner {
	
	private static String replaceCharAt(String s, int i, char c) {
        StringBuffer buf = new StringBuffer(s);
        buf.setCharAt(i, c);
        return buf.toString();
    }		
	
	public static String replaceNestedCommas(String a) {
		int quotaCnt = 0;
		
		int x=0;
		while (x< a.length()){
			if (a.charAt(x)=='"') {
				quotaCnt++;
			}
			if (quotaCnt%2==1 && a.charAt(x)==',') {
				a = replaceCharAt(a,x,' ');
			}
			//System.out.println( a.charAt(x) + " " + quotaCnt%2);
			x++;
		}
		//System.out.println( a);
		return a;		
	}		
	
	private static FileReader fr;	
	private static void deleteEOLsfromCSV (String sourceFile, String destFile, String replaceNestedCommasFlag, String truncateMax255chars) throws IOException {
	    
		try {
			FileWriter writer = new FileWriter(destFile);
			
			fr = new FileReader(sourceFile);
			BufferedReader br = new BufferedReader(fr);
			String phisicalLine;
			String recordLine;
			Integer recordNo=0;
	        
			Boolean deleteNested =replaceNestedCommasFlag.compareTo("Y")==0;
			Boolean trucate255   =truncateMax255chars.equalsIgnoreCase("Y");
			
			while( (phisicalLine = br.readLine()) != null) {
				recordLine = phisicalLine;				
				while ( !((recordLine.length() - recordLine.replace("\"","").length()) % 2 == 0) ) { 
					phisicalLine = br.readLine();
					recordLine = recordLine +" "+ phisicalLine;						 
				}

			    if (recordLine.isEmpty()) {
				    System.out.println(Messages.getString("EOLCleaner.3")); 
			    	continue;
			    }				
				recordNo++;
				if (deleteNested) {
					recordLine = replaceNestedCommas(recordLine);
				}
				//this function is experimental, do not use it
				if (trucate255) {
					ArrayList<String> tokens = new ArrayList<String>();
					StringTokenizer st = new StringTokenizer(recordLine, ",");
					while (st.hasMoreElements()) {
						String currStr = (String) st.nextElement();
						currStr = currStr.replace("\"", "");
						currStr = currStr.substring(0, (currStr.length()<254?currStr.length():254) );							
						tokens.add(currStr);
					}
					recordLine = "";
					Integer i=0;
					for (String s : tokens)
					{
						recordLine += (i==0?s:"," + s);
						i++;
					}
				}
			    writer.append(recordLine);			 	 
			    writer.append('\n');			 	 
												
			}
		    writer.flush();
		    writer.close();			
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	    
	}	
	
	public static void main(String[] args) throws Exception {
		System.out.println(Messages.getString("EOLCleaner.4")); //$NON-NLS-1$
		if (args.length==0) {
  		  System.out.println(Messages.getString("EOLCleaner.5")); //$NON-NLS-1$
		}
		else {
		  deleteEOLsfromCSV(args[0], args[1], args[2], "N");		
		  System.out.println(Messages.getString("EOLCleaner.6")); //$NON-NLS-1$
		}
	}		

}
