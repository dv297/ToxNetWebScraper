import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.net.*;
import java.sql.*;

import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

public class URLReader {
//    public static void main(String[] args) throws Exception {
//
//        URL oracle = new URL("http://toxgate.nlm.nih.gov/cgi-bin/sis/search2/g?./temp/~zatH9T:20");
//        BufferedReader in = new BufferedReader(
//        new InputStreamReader(oracle.openStream()));
//
//        String inputLine;
//        while ((inputLine = in.readLine()) != null)
//            System.out.println(inputLine);
//        in.close();
//    }
    
    private static String[] types = {"Neurological", "Ears", "Eyes", "Gastro/Urinary", "Skin"};
    // 427 - Removed - Furfuryl Alcohol
    // 447 - Removed - Crude Oil
    // 448 - Removed - Corexit 9500
    // 449 - Removed - Corexit 9527
	private static int[] idList = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254, 255, 256, 257, 258, 259, 260, 261, 262, 263, 264, 265, 266, 267, 268, 269, 270, 271, 272, 273, 274, 275, 276, 277, 278, 279, 280, 281, 282, 283, 284, 285, 286, 287, 288, 289, 291, 292, 293, 294, 295, 296, 297, 298, 299, 300, 301, 302, 303, 304, 305, 306, 307, 308, 309, 310, 311, 312, 313, 314, 315, 316, 317, 318, 319, 320, 321, 322, 323, 324, 325, 326, 327, 328, 329, 330, 331, 332, 333, 334, 335, 336, 337, 338, 339, 340, 341, 342, 343, 344, 345, 346, 347, 348, 349, 350, 351, 352, 353, 354, 355, 356, 357, 358, 359, 360, 361, 362, 364, 365, 366, 367, 368, 369, 370, 371, 372, 373, 374, 375, 376, 377, 378, 379, 380, 381, 382, 383, 384, 385, 386, 387, 388, 389, 390, 391, 392, 393, 394, 395, 396, 397, 398, 399, 400, 401, 402, 403, 404, 405, 406, 407, 428, 429, 430, 431, 432, 433, 434, 435, 442, 443, 444, 445, 446, 447, 448, 450, 451, 452, 453, 454, 455, 456, 457, 458, 459, 460, 462, 463, 464, 465, 466, };
//    private static int[] idList = {270, 271, 272, 273, 273, 400, 401, 402, 403, 404, 405, 406, 407, 428, 429, 430, 431, 432, 433, 434, 435, 442, 443, 444, 445, 446, 447, 448, 450, 451, 452, 453, 454, 455, 456, 457, 458, 459, 460, 462, 463, 464, 465, 466, };
//    private static int[] idList = {1, 2,3,4,5};
	private static HashMap map;
	private static int searchID = 4;
	private static Set<String> uniqueElements;
	
	private static ArrayList<String> checkList;
	
	private static String username = "root";
	private static String password = "leinad";
	private static String connectionString = "jdbc:mysql://localhost:9999/wiser_symptoms";
	private static Connection connection;
	private static Statement command;
	private static ResultSet data;
	private static PreparedStatement statement;
	
	private static BufferedWriter bw;
	private static FileWriter fw;
	
    
	public static void main(String[] args){
		ArrayList result = new ArrayList<String>();
		map = new HashMap();
		uniqueElements = new HashSet<String>();
		checkList = new ArrayList<String>();
		checkList.add("low body temp");
		checkList.add("high body temp");
		checkList.add("chills");
		checkList.add("shivering");
		checkList.add("agitation");
		checkList.add("lowered mental state");
		checkList.add("unresponsive");
		checkList.add("drowsiness");
		checkList.add("fatigue/weakness");
		checkList.add("dizziness");
		checkList.add("headache");
		checkList.add("slurred speech");
		checkList.add("numbness/tingling");
		checkList.add("lack of coordination");
		checkList.add("spasms/seizures");
		checkList.add("paralysis");
		checkList.add("hearing loss");
		checkList.add("tinnitus");
		checkList.add("eye irritation/redness");
		checkList.add("eye swelling");
		checkList.add("light sensitivity");
		checkList.add("constricted pupils");
		checkList.add("dilated pupils");
		checkList.add("tearing");
		checkList.add("impaired vision");
		checkList.add("vision loss");
		checkList.add("bloody nose");
		checkList.add("runny nose");
		checkList.add("nasal irritation");
		checkList.add("sneezing");
		checkList.add("coughing/choking");
		checkList.add("mouth irritation");
		checkList.add("drooling/salivation");
		checkList.add("dry mouth/throat");
		checkList.add("throat irritation");
		checkList.add("chest pain");
		checkList.add("arrhythmia");
		checkList.add("bradycardia");
		checkList.add("tachycardia");
		checkList.add("hypertension");
		checkList.add("hypotension/shock");
		checkList.add("hypoxia/cyanosis");
		checkList.add("irregular breathing");
		checkList.add("slow breathing");
		checkList.add("rapid breathing");
		checkList.add("shortness of breath");
		checkList.add("wheezing");
		checkList.add("respiratory arrest");
		checkList.add("resp burning/irritation");
		checkList.add("pulmonary edema");
		checkList.add("chest discomfort");
		checkList.add("congestion");
		checkList.add("coughing blood");
		checkList.add("abdom. discomfort");
		checkList.add("abdom. distention");
		checkList.add("nausea");
		checkList.add("vomiting");
		checkList.add("vomiting blood");
		checkList.add("diarrhea");
		checkList.add("urinary incontinence");
		checkList.add("urinary pain/burning");
		checkList.add("urination, bloody");
		checkList.add("peeling/exfoliation");
		checkList.add("itching");
		checkList.add("dry skin");
		checkList.add("skin burns/burning");
		checkList.add("sweating");
		checkList.add("skin swelling");
		checkList.add("blistering");
		checkList.add("rash");
		checkList.add("skin redness");
		checkList.add("pale");
		checkList.add("jaundice/yellow");
		checkList.add("cyanosis/blue");
		checkList.add("frostbite");
		
		System.out.println("Start program");
		System.out.println("==================\n");
		
		
		
		for(int x = 0; x < idList.length; x++)
		{
			searchID = idList[x];
			try 
			{
				result = parseList(searchID);
				System.out.println(searchID + "     " + result);
				
				if(result != null)
				{
					ArrayList<String> symptomList = cleanList(result);
					if(symptomList.size() != 0)
					{
						try {connection = DriverManager.getConnection(connectionString, username, password);}
						catch (SQLException e) {}
						try 
						{
							String symptoms = "";
							String emptyField = "";
							String ones = "";
							
							HashSet<String> unique = new HashSet<String>();
							unique.addAll(symptomList);
							for(String s:unique)
							{
								symptoms+= "`" + s + "`" + ", ";
								emptyField+="?,";
								ones+="1,";								
							}
							
							symptoms = symptoms.substring(0, symptoms.length()-2); // Remove comma
							ones = ones.substring(0,ones.length()-1);

							System.out.println("INSERT INTO substances (`substanceID`, " + symptoms + ") VALUES(" + ones + "); \n");
							command = connection.createStatement();
							command.execute("INSERT INTO substances (`substanceID`, " + symptoms + ") VALUES(" + searchID + ", " + ones + ");");
						} 
						catch (SQLException e){
							e.printStackTrace();
						}
					}
					
					
					
//					uniqueElements.addAll(symptomList);
//					bw.close();

				}
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}	
		
//		for(String s : uniqueElements)
//		{
//			System.out.println(s);
//		}
		
		
  }
	
	
	
	
	/*
	 * 
	 * 
	 * Helper methods
	 * 
	 * 
	 */
	
	
	public static ArrayList<String> parseList(int substanceId) throws MalformedURLException, IOException
	{
		String sourceUrlString="http://webwiser.nlm.nih.gov/getSubstanceData.do?substanceId=" + substanceId + "&displaySubstanceName=&STCCID=&UNNAID=&selectedDataMenuItemID=2";
		String[] array = {};
		String result = "";
		ArrayList<String> list = null;
		
		Source source=new Source(new URL(sourceUrlString));
		
		try
		{
			result = source.getElementById("substanceDataContent").toString();
			
				
			try{result = "Temperature:"+ result.split("Temperature:")[1].split("<br><br>")[0];}
			catch(ArrayIndexOutOfBoundsException e)
			{
				try{result = "Neurological:"+ result.split("Neurological:")[1].split("<br><br>")[0];}
				catch(ArrayIndexOutOfBoundsException e1)
				{
					try{result = "Ears:"+ result.split("Ears:")[1].split("<br><br>")[0];}
					catch(ArrayIndexOutOfBoundsException e2)
					{
						try{result = "Eyes:"+ result.split("Eyes:")[1].split("<br><br>")[0];}
						catch(ArrayIndexOutOfBoundsException e3)
						{
							try{result = "Nose:"+ result.split("Nose:")[1].split("<br><br>")[0];}
							catch(ArrayIndexOutOfBoundsException e4)
							{
								try{result = "Mouth/Throat:"+ result.split("Mouth/Throat:")[1].split("<br><br>")[0];}
								catch(ArrayIndexOutOfBoundsException e5)
								{
									try{result = "Cardiovascular:"+ result.split("Cardiovascular:")[1].split("<br><br>")[0];}
									catch(ArrayIndexOutOfBoundsException e6)
									{
										try{result = "Respiratory:"+ result.split("Respiratory:")[1].split("<br><br>")[0];}
										catch(ArrayIndexOutOfBoundsException e7)
										{
											try{result = "Gastro/Urinary:"+ result.split("Gastro/Urinary:")[1].split("<br><br>")[0];}
											catch(ArrayIndexOutOfBoundsException e8)
											{
												try{result = "Skin:"+ result.split("Skin:")[1].split("<br><br>")[0];}
												catch(ArrayIndexOutOfBoundsException e9)
												{

												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			
			result = result.replace("&nbsp;","");
			array = result.split("<br>");
			list = new ArrayList<String>();
			for(String s:array)
			{
				if(!s.contains("Not Applicable"))
					list.add(s);
			}			
		}
		catch(NullPointerException e){
			System.out.println("ID " + substanceId + " does not exist");
			return null;
		}

		return list;
	}
	
	public static ArrayList<Integer> listIndexes(ArrayList<String> list)
	{
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		for(int x = 0; x < list.size(); x++)
		{
			if(list.contains(":"))
			{
				indexList.add(x);
			}
		}
		return indexList;
	}
	
	public static ArrayList<String> cleanList(ArrayList<String> result)
	{
		ArrayList<String> results = new ArrayList<String>();
		for(String s:result)
		{
			if(!s.contains(":") && checkList.contains(s))
			{
				results.add(s);
			}
		}
		List<Integer> indexList = listIndexes(result);
		
		

		for(int x = indexList.size()-1; x > 0; x--)
		{
			int index = indexList.get(x);
		}

		return results;
	}
}

// comments/testing suite

//List<String> neurological = results.subList(indexList.get(0)+1, indexList.get(1));
//List<String> ears = results.subList(indexList.get(1)+1, indexList.get(2));
//List<String> eyes = results.subList(indexList.get(2)+1, indexList.get(3));
//List<String> gastro = results.subList(indexList.get(3)+1, indexList.get(4));
//List<String> skin = results.subList(indexList.get(4)+1, results.size());
//
//
//for(int x = 0; x < skin.size(); x++)
//{
//	System.out.println(skin.get(x));
//}


//List<String> neurological = results.subList(indexList.get(0)+1, indexList.get(1));
//List<String> ears = results.subList(indexList.get(1)+1, indexList.get(2));
//List<String> eyes = results.subList(indexList.get(2)+1, indexList.get(3));
//List<String> gastro = results.subList(indexList.get(3)+1, indexList.get(4));
//List<String> skin = results.subList(indexList.get(4)+1, results.size());





//try {
//connection = DriverManager.getConnection(connectionString, username, password);
//} catch (SQLException e) {
//// TODO Auto-generated catch block
//e.printStackTrace();
//}
//try {
//command = connection.createStatement();
//data = command.executeQuery("SELECT DBID, EDRR FROM final WHERE 1");
//if(data.first())
//{
//	while(data.next())
//	{
//		System.out.println(data.getString("dbid") + "   " + data.getString("EDRR"));
//	}
//}
//} catch (SQLException e) {
//// TODO Auto-generated catch block
//e.printStackTrace();
//}



//try{result = (result.split("Vapor Density:")[1]).split("<br><br>")[1];}
//catch(ArrayIndexOutOfBoundsException e)
//{
//	try{result = result.split("Specific Gravity:")[1].split("<br><br>")[1];}
//	catch(ArrayIndexOutOfBoundsException e2)
//	{
//		try{result = result.split("Odor:")[1].split("<br><br>")[1];}
//		catch(ArrayIndexOutOfBoundsException e3)
//		{
//			try{result = result.split("solid")[1].split("<br><br>")[1];}
//			catch(ArrayIndexOutOfBoundsException e4)
//			{
//				result = "Temperature:"+ result.split("Temperature:")[1].split("<br><br>")[0];
//			}
//		}
//	}
//}