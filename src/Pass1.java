import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;




public class Pass1 {

	List<String> lines;
	static String[][] form = {{"FIX","FLOAT","HIO","NORM","SIO","TIO"},
		{"ADDR","CLEAR","COMPR","DIVR","MULR","RMO","SHIFTL","SHIFTR","SUBR","SVC","TIXR"},
		{"ADD","ADDF","AND","COMP","COMPF","DIV","DIVF","J","JEQ","JGT","JLT","JSUB","LDA",
			"LDB","LDCH","LDF","LDL","LDS","LDT","LDX","LPS","MUL","MULF","OR","RD","RSUB","SSK","STA","STB",
			"STCH","STF","STI","STL","STS","STSW","STT","STX","SUB","SUBF","TD","TIX","WD"},
			{"+ADD","+ADDF","+AND","+COMP","+COMPF","+DIV","+DIVF","+J","+JEQ","+JGT","+JLT","+JSUB","+LDA",
				"+LDB","+LDCH","+LDF","+LDL","+LDS","+LDT","+LDX","+LPS","+MUL","+MULF","+OR","+RD","+RSUB","+SSK","+STA","+STB",
				"+STCH","+STF","+STI","+STL","+STS","+STSW","+STT","+STX","+SUB","+SUBF","+TD","+TIX","+WD"},
				{"START","END","BYTE","WORD","RESB","RESW"}};
	static Integer LOCCTR;
	static List<String> new_parts = new ArrayList<String>();
	static ArrayList<ArrayList<String>> LT_Table = new ArrayList<ArrayList<String>>();
	static ArrayList<ArrayList<String>> LT_Table_Temp = new ArrayList<ArrayList<String>>();
	static Map<Integer, String> SYB_Table = new HashMap<Integer, String>();
	public Pass1(){
		LOCCTR = 0;
		lines = new ArrayList<String>();
	}

	public static boolean CheckForDuplicate(String Val)
	{
		for(int i=0;i<LT_Table.size();i++)
		{
			if(Val.equalsIgnoreCase(LT_Table.get(i).get(1)))
				return true;
		}
		return false;
	}

	public static int checkForm(String[] parts) throws Exception
	{
		int i=0,x=0;
		String part = parts[i];
		if(part.charAt(0) == '+')
			LOCCTR+=4;
		else if(Arrays.asList(form[2]).contains(part))
			LOCCTR += 3;
		else if(Arrays.asList(form[1]).contains(part))
			LOCCTR += 2;
		else if(Arrays.asList(form[0]).contains(part))
			LOCCTR++;
		else if(part.equalsIgnoreCase("LTORG"))
		{
			@SuppressWarnings("unused")
			String last_locctr;
			for(int j=0;j<LT_Table_Temp.size();j++)
			{
				LT_Table_Temp.get(j).add(Integer.toHexString(LOCCTR).toUpperCase());
				if(!CheckForDuplicate(LT_Table_Temp.get(j).get(1)))
				{
					new_parts.add((last_locctr = Integer.toHexString(LOCCTR).toUpperCase()) + "   *   " + LT_Table_Temp.get(j).get(0));
					LOCCTR += Integer.parseInt(LT_Table_Temp.get(j).get(2));
					LT_Table.add(LT_Table_Temp.get(j));
				}
			}
			LT_Table_Temp.clear();
		}
		else if(!part.equalsIgnoreCase("END") && !parts[i].equalsIgnoreCase("START"))
		{
			i++;
			part = parts[i];
			if(part.equals("BYTE"))
			{
				int NoOfBytes = parts[i+1].length()-3;
				if(parts[i+1].charAt(0)=='X')
					LOCCTR += (int) Math.ceil((double)(NoOfBytes)/2);
				else if(parts[i+1].charAt(0)=='C')
					LOCCTR += NoOfBytes;
			}
			else if(part.equals("WORD"))
				LOCCTR += 3;
			else if(part.equals("RESB"))
				LOCCTR += Integer.parseInt(parts[2]);
			else if(part.equals("RESW"))
				LOCCTR += 3*Integer.parseInt(parts[2]);
			else if(part.charAt(0) == '+')
				LOCCTR+=4;
			else if(Arrays.asList(form[2]).contains(part))
				LOCCTR += 3;
			else if(Arrays.asList(form[1]).contains(part))
				LOCCTR += 2;
			else if(Arrays.asList(form[0]).contains(part))
				LOCCTR++;
			x=1;
		}
		if(parts[parts.length-1].charAt(0) == '=')
		{
			i++;
			String content = parts[i].substring(parts[i].indexOf("'")+1,parts[i].length()-1);
			int NoOfBytes = parts[i].length()-4;
			if(parts[i].charAt(1)=='X' && !CheckForDuplicate(content))
			{
				int length = (int) Math.ceil((double)(NoOfBytes)/2);
				LT_Table_Temp.add(new ArrayList<String>(Arrays.asList(new String[] {parts[i],content.toUpperCase(),Integer.toString(length)})));
			}
			else if(parts[i].charAt(1)=='C' && !CheckForDuplicate(Hex.encodeHexString(content.getBytes()).replaceAll("\\s+","")))
			{
				LT_Table_Temp.add(new ArrayList<String>(Arrays.asList(new String[]{parts[i],Hex.encodeHexString(content.getBytes()).replaceAll("\\s+","").toUpperCase(),Integer.toString(NoOfBytes)})));
			}
		}
		return x;
	}

	public void LocationCounter(File selected) throws Exception
	{
		FileReader reader = new FileReader(selected);
		BufferedReader read = new BufferedReader(reader);
		int m=0,temp;
		String last_locctr = null;
		String s = read.readLine();
		String[] parts = new String[100];
		if(s!=null)
		{
			parts = s.split("\\s+");
			LOCCTR = Integer.parseInt(parts[2]);
			new_parts.add((last_locctr = LOCCTR.toString().toUpperCase()) + " " + s);
			s = read.readLine();
			if(s!=null)
			{
				new_parts.add((last_locctr = LOCCTR.toString().toUpperCase()) + " " + s);
				LOCCTR = Integer.parseInt(LOCCTR.toString(), 16);
			}
		}
		while(s != null)
		{
			parts = s.split("\\s+");
			if(parts.length>1 ? parts[1].equals("BYTE"):false)
			{
				parts[2] = s.substring(s.indexOf("'")-1);
			}
			temp = checkForm(parts);
			if(temp==0)
			{
				s = read.readLine();
				if(s!=null)
				{
					new_parts.add((last_locctr = Integer.toHexString(LOCCTR).toUpperCase()) + " " + s);
				}
			}
			else if(temp==1)
			{
				SYB_Table.put(m++,parts[0]+"  ->  "+last_locctr);
				s = read.readLine();
				if(s!=null)
				{
					new_parts.add((last_locctr = Integer.toHexString(LOCCTR).toUpperCase()) + " " + s);
				}
			}
		}
		if(!LT_Table_Temp.isEmpty())
		{
			for(int j=0;j<LT_Table_Temp.size();j++)
			{
				LT_Table_Temp.get(j).add(Integer.toHexString(LOCCTR).toUpperCase());
				if(!CheckForDuplicate(LT_Table_Temp.get(j).get(1)))
				{
					new_parts.add((last_locctr = Integer.toHexString(LOCCTR).toUpperCase()) + "   *   " + LT_Table_Temp.get(j).get(0));
					LOCCTR += Integer.parseInt(LT_Table_Temp.get(j).get(2));
					LT_Table.add(LT_Table_Temp.get(j));
				}
			}
			LT_Table_Temp.clear();
		}
		read.close();
	}

	public void SaveToFile() throws Exception
	{
		PrintWriter writer = new PrintWriter("SYB-Table.txt", "UTF-8");
		for(int i=0;i<SYB_Table.size();i++)
			writer.println(SYB_Table.get(i));
		// System.out.println(m1);
		writer.close();
		writer = new PrintWriter("LOCCTR.txt", "UTF-8");
		for(int i=0;i<new_parts.size();i++)
			writer.println(new_parts.get(i));
		writer.close();
		writer = new PrintWriter("LT_Table.txt", "UTF-8");
		for(int i=0;i<LT_Table.size();i++)
		{
			for (int j = 0; j < 4; j++) {
				writer.printf("%30s",LT_Table.get(i).get(j));
			}
			writer.println();
		}
		writer.close();
	}

}
