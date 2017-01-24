//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
//	Impact Application Parser
//		A short program to parse the RSS-formatted data from Osmek.com containing
//		  all Impact Retreat counselor applications, outputting the data into
//		  a TSV-formatted file, delimited by tabs, which can be opened as a
//		  spreadsheet by many applications
//		Developed February 6, 2013 for exclusive use by Impact Retreat
//		Program has been compiled and run successfully on Java 1.7.0_21. To build
//		  and run on any computer with the Java JDK installed, open a terminal or
//		  command prompt and enter
//
//			javac ImpactApplicationParser.java; java ImpactApplicationParser;
//
//		  Output will be two files, 'male_apps.tsv' and 'female_apps.tsv', and
//		  you will receive a message saying the program executed correctly
//
//	Developer Information:
//		Casey James Brooks
//		Email: cjbrooks12@gmail.com
//
//------------------------------------------------------------------------------
import java.io.*;
import java.util.*;

public class ImpactApplicationParser {
//Initialization
//------------------------------------------------------------------------------
	public static ArrayList<String> id;
	public static ArrayList<String> firstName;
	public static ArrayList<String> lastName;
	public static ArrayList<String> gender;
	public static ArrayList<String> session;
	public static ArrayList<String> classYear;
	public static ArrayList<String> GPR;
	public static ArrayList<String> fiveWords;
	public static ArrayList<String> church;
	public static ArrayList<String> essay1;
	public static ArrayList<String> essay2;
	public static ArrayList<String> essay3;
	public static ArrayList<String> essay4;
	public static ArrayList<String> essay5;

	public static void main(String[] args) {
		initializeArrays();
		getXMLData();
		printDataToTSV();
	}

	public static void initializeArrays() {
		id = new ArrayList<String>();
		firstName = new ArrayList<String>();
		lastName = new ArrayList<String>();
		gender = new ArrayList<String>();
		session = new ArrayList<String>();
		classYear = new ArrayList<String>();
		GPR = new ArrayList<String>();
		fiveWords = new ArrayList<String>();
		church = new ArrayList<String>();
		essay1 = new ArrayList<String>();
		essay2 = new ArrayList<String>();
		essay3 = new ArrayList<String>();
		essay4 = new ArrayList<String>();
		essay5 = new ArrayList<String>();
	}

//Parse raw data from Osmek, retrieved in RSS format. I queried the server to a
//	limit of 1000, which returns 632 entries, so I know this has all of them. Data
//	was pulled in the evening of February 6, 2013
//------------------------------------------------------------------------------
	public static void getXMLData() {
		try {
			BufferedReader in = new BufferedReader(new FileReader("raw_data.xml"));
			StringBuffer sb = new StringBuffer("");
			String l = "";
			String nl = System.getProperty("line.separator");

			while((l = in.readLine()) != null) {
				sb.append(l + nl);
				l = replaceHTMLCodes(l);
				l = checkSpecialCases(l);

				//Gets the ID for any given application
				if(l.contains("         <guid>http://impactretreat.com-")) {
					l = l.replaceAll("         <guid>http://impactretreat.com-", "");
					l = l.replaceAll("</guid>", "");
					id.add(l);
				}

				//This whole if-else block checks the raw data for any app. Because
				//	of poor formatting of raw data from the web API, it doesn't
				//	correctly parse the "Words to describe yoursef", so in some
				// 	cases, a given app may have fewer than 5 words, some showing
				// 	only one. Also, I could not find how to get the session info
				//	for all apps, and so because so many couldn't get this parsed,
				//	this column is left out of the TSV file.
				if(l.contains("<description>")) {
					l = l.replaceAll("<description>", "");
					l = l.replaceAll("<!", "");
					l = l.replaceAll("\\[", "");
					l = l.replaceAll("CDATA", "");
					l = l.replaceAll("<p>", "");

					String[] apps = l.split("<br /><br />");

					for(int i = 0; i < apps.length; i++) {
						checkString(apps[i]);
						if(apps[i].contains("can-attend:")) {
							String sessions = "";
							for(int j = 1; j <= 3; j++) {
								if(apps[i + j].contains("Alpha Session:")) {
									sessions = sessions + "Alpha ";
								}
								if(apps[i + j].contains("Delta Session:")) {
									sessions = sessions + "Delta ";
								}
								if(apps[i + j].contains("Omega Session:")) {
									sessions = sessions + "Omega ";
								}
							session.add(sessions);
							}
						}
					}
				}
				else {
					checkString(l);
				}
			}
			in.close();
		}
		catch(Exception e) {
			System.out.println("An unknown error occurred");
		}
	}

	//Often certain characters are encoded strangely in HTML. If you happen to see
	//	a character that resembles what is below, just add it to the list and set
	//	the second argument to be the character that it should be.
	public static String replaceHTMLCodes(String ll) {
		String l = ll;
		l = l.replaceAll("&ldquo;", "\"");
		l = l.replaceAll("&rdquo;", "\"");
		l = l.replaceAll("&nbsp;", " ");
		l = l.replaceAll("&rsquo;", "\'");
		l = l.replaceAll("&ndash;", "–");
		l = l.replaceAll("&amp;", "&");
		l = l.replaceAll("\t", "    ");
		l = l.replaceAll("<br/><br/>","");

		return l;
	}

	//Certain apps happened to be missing some data or it would not parse correctly.
	//	In these cases, I found the string for the email of the application that
	//	is buggy and add the missing data manually here. If we decide to search
	//	manually for the applications that don't have session info parsing rightly,
	//	this is where we would add it
	public static String checkSpecialCases(String l) {
		if(l.contains("Email: emily.dirksmeyer@yahoo.com")) {
			gender.add("female");
			session.add("Omega");
			return "";
		}
		else if(l.contains("Email: surfergurl818@hotmail.com")) {
			session.add("Alpha Delta");
			return "";
		}
		else if(l.contains("Email: tkiss76@gmail.com")) {
			gender.add("male");
			return "";
		}
		else if(l.contains("Class: Tue & Thur, 8 am-3pm")) {
			return "";
		}
		else if(l.contains("Commitments: Class: 15 HR/WEEK")) {
			return "";
		}
		return l;
	}

	public static void checkString(String apps) {
		if(apps.contains("First Name:")) {
			apps = apps.replaceAll("First Name:", "");
			firstName.add(apps);
		}
		else if(apps.contains("Last Name:")) {
			apps = apps.replaceAll("Last Name:", "");
			lastName.add(apps);
		}
		else if(apps.contains("Gender:")) {
			apps = apps.replaceAll("Gender:", "");
			gender.add(apps);
		}
		else if(apps.contains("Class:")) {
			apps = apps.replaceAll("Class:", "");
			classYear.add(apps);
		}
		else if(apps.contains("Current Gpr:")) {
			apps = apps.replaceAll("Current Gpr:", "");
			GPR.add(apps);
		}
		else if(apps.contains("Words That Describe You: ")) {
			apps = apps.replaceAll("Words That Describe You: ", "");
			apps = apps.replaceAll("\n", ", ");
			fiveWords.add(apps);
		}
		else if(apps.contains("Local Church: ")) {
			apps = apps.replaceAll("Local Church: ", "");
			church.add(apps);
		}
		else if(apps.contains("Essay 1:")) {
			apps = apps.replaceAll("Essay 1:", "");
			essay1.add(apps);
		}
		else if(apps.contains("Essay 2:")) {
			apps = apps.replaceAll("Essay 2:", "");
			essay2.add(apps);
		}
		else if(apps.contains("Essay 3:")) {
			apps = apps.replaceAll("Essay 3:", "");
			essay3.add(apps);
		}
		else if(apps.contains("Essay 4:")) {
			apps = apps.replaceAll("Essay 4:", "");
			essay4.add(apps);
		}
		else if(apps.contains("Essay 5:")) {
			apps = apps.replaceAll("Essay 5:", "");
			essay5.add(apps);
		}
	}

//Prints contents of file to TSV format (or more accurately, TSV, tab-separated values)
//	Note that some programs, like LibreOffice, will have no issue with the file being
//	tab-separated, just make sure to set the delimiter to tabs only. Google Docs
//	will only upload it correctly if the file extension if .tsv, so change it should
//	you need it in Google Docs. I have not tried opening it in Microsoft Excel, as
//	I do not have MS Office installed.
//------------------------------------------------------------------------------
	public static void printDataToTSV() {
		String males = "ID" + "\t" +
			"First Name" + "\t" +
			"Last Name" + "\t" +
			//"Sessions Available" + "\t" + //should I get the session this fixed, uncomment these lines
			"Class Year" + "\t" +
			"GPR" + "\t" +
			"Words that Describe You" + "\t" +
			"Home Church" + "\t" +
			"Essay 1" + "\t" +
			"Essay 2" + "\t" +
			"Essay 3" + "\t" +
			"Essay 4" + "\t" +
			"Essay 5" + "\n";

		String females = new String(males);

		for(int i = 0; i < firstName.size(); i++) {
			String text =
				id.get(i) + "\t" +
				firstName.get(i) + "\t" +
				lastName.get(i) + "\t" +
				//session.get(i) + "\t" +
				classYear.get(i) + "\t" +
				GPR.get(i) + "\t" +
				fiveWords.get(i) + "\t" +
				church.get(i) + "\t" +
				essay1.get(i) + "\t" +
				essay2.get(i) + "\t" +
				essay3.get(i) + "\t" +
				essay4.get(i) + "\t" +
				essay5.get(i) + "\n";

			if(gender.get(i).equals("  male") || gender.get(i).equals(" male") || gender.get(i).equals("male")) {
				males = males + text;
			}
			else {
				females = females + text;
			}
		}

		try {
			FileOutputStream fos = new FileOutputStream("male_apps.tsv");
			fos.write(males.getBytes());
			fos.close();

			fos = new FileOutputStream("female_apps.tsv");
			fos.write(females.getBytes());
			fos.close();
		}
		catch(Exception e) {
			System.out.println("An unknown error occurred");
		}
		System.out.println("Program executed successfully");
	}
}