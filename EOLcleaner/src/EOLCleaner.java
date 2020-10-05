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
 * @version 2020.10.04
 */

public class EOLCleaner {
	
	private static String replaceCharAt(String s, int i, char c) {
        StringBuffer buf = new StringBuffer(s);
        buf.setCharAt(i, c);
        return buf.toString();
    }		
	
	public static String replaceNestedCommas(String a, char replaceWith) {
		int quotaCnt = 0;
		
		int x=0;
		while (x< a.length()){
			if (a.charAt(x)=='"') {
				quotaCnt++;
			}
			if (quotaCnt%2==1 && a.charAt(x)==',') {
				a = replaceCharAt(a,x,replaceWith);
			}
			//System.out.println( a.charAt(x) + " " + quotaCnt%2);
			x++;
		}
		//System.out.println( a);
		return a;		
	}		
	
	private static FileReader fr;	
	private static void deleteEOLsfromCSV (String sourceFile, String destFile, String replaceNestedCommasFlag, String trucateCharsX, String tableName) throws IOException {
	    
		try {
			FileWriter writer = new FileWriter(destFile);
			
			fr = new FileReader(sourceFile);
			BufferedReader br = new BufferedReader(fr);
			String phisicalLine;
			String recordLine;
			Integer recordNo=0;
	        
			Boolean deleteNested =replaceNestedCommasFlag.compareTo("Y")==0;
			Boolean trucateChars = (trucateCharsX !=null) && (trucateCharsX.length()>0);
			int trucateNChars=0;
			if (trucateChars)
			    trucateNChars = Integer.parseInt(trucateCharsX)-3;
			
			int fuse = 1;
			while( (phisicalLine = br.readLine()) != null) {
				recordLine = phisicalLine;				
				while ( !((recordLine.length() - recordLine.replace("\"","").length()) % 2 == 0) || fuse==1000  ) { 
					phisicalLine = br.readLine();
					recordLine = recordLine +" "+ phisicalLine;
					fuse++;
				}
				if (fuse==1000) System.out.println("*** Error: The file structure is invalid");

			    if (recordLine.isEmpty()) {
				    System.out.println(Messages.getString("EOLCleaner.3")); 
			    	continue;
			    }				
				recordNo++;
				if (deleteNested) {
					recordLine = replaceNestedCommas(recordLine,' ');
				}
				
				if (recordNo % 1000 == 0)
					System.out.println(recordNo);
				
				//Generate DML
				if (recordNo==1) {
					FileWriter writerDML = new FileWriter(sourceFile+".dml");
					writerDML.append("create table "+tableName+" (");			 	 			 	 

					String separator = "";
					ArrayList<String> tokens = new ArrayList<String>();
					StringTokenizer st = new StringTokenizer(recordLine, ",");
					while (st.hasMoreElements()) {
						String currStr = (String) st.nextElement();
						writerDML.append(separator+currStr+" varchar2(4000)");
						separator = ",";
						tokens.add(currStr);
					}
					recordLine = "";
					Integer i=0;
					for (String s : tokens)
					{
						recordLine += (i==0?s:"," + s);
						i++;
					}
					
					writerDML.append(");");			 	 			 	 
				    writerDML.flush();
				    writerDML.close();									
				}
				
				
				if (trucateChars) {
					Boolean eliminateNestedCommas = (trucateChars && !deleteNested);
					if (eliminateNestedCommas) recordLine = replaceNestedCommas(recordLine,'`');
					ArrayList<String> tokens = new ArrayList<String>();
					StringTokenizer st = new StringTokenizer(recordLine, ",");
					while (st.hasMoreElements()) {
						String currStr = (String) st.nextElement();
						Boolean isQuotedFlag = isQuoted (currStr);
						if (isQuotedFlag) {
							currStr = unquote(currStr);
						}
						
						if (currStr.length()>trucateNChars)
							currStr = currStr.substring(0, trucateNChars)+"...";							
						
						currStr = currStr.replace("'", " ");
						currStr = currStr.replace("\"", " ");

						if (isQuotedFlag) {
							currStr = quote(currStr);
						}
						
						tokens.add(currStr);
					}
					recordLine = "";
					Integer i=0;
					for (String s : tokens)
					{
						recordLine += (i==0?s:"," + s);
						i++;
					}
					if (eliminateNestedCommas) recordLine = recordLine.replace("`", ",");
				}
				
			    writer.append(recordLine);			 	 
			    writer.append('\n');			 	 
												
			}
		    writer.flush();
		    writer.close();			
			fr.close();
			System.out.println("Lines processed: " + recordNo);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	    
	}	
	
	public static Boolean isQuoted(String s) {
		return (s != null) && (s.length()>1) && (s.charAt(0)=='\"') && (s.charAt(s.length()-1)=='\"');
	}
	
	public static String unquote(String s) {
		int len = s.length();
		return s.substring(1, len-1);
	}
	
	public static String quote(String s) {
		return "\"" + s + "\"";
	}

	
	public static void main(String[] args) throws Exception {
		
		System.out.println(Messages.getString("EOLCleaner.4")); //$NON-NLS-1$
		if (args.length==0) {
  		  System.out.println(Messages.getString("EOLCleaner.5")); //$NON-NLS-1$
		}
		else {
		  deleteEOLsfromCSV(args[0], args[1], args[2], args[3], args[4]);		
		  System.out.println(Messages.getString("EOLCleaner.6")); //$NON-NLS-1$
		}
	}		

}
