package mqsecparser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

	public static void main(String[] args) {
		
		if (args.length==0) {
			System.out.println("usage: mqsecparser sourceFile targetFile");
			return;
		}
		String sourceFile = args[0];
		String targetFile = args[1];
		
		// read files
		String sourceFileString  = "";String sourceParentFile = "";
		String targetFileString  = "";String targetParentFile = "";
		try {
			byte[] sourceFileBytes = Files.readAllBytes(Paths.get(sourceFile));
			sourceFileString = new String(sourceFileBytes);
			sourceParentFile = Paths.get(sourceFile).getParent().getFileName().toString();
			byte[] targetFileBytes = Files.readAllBytes(Paths.get(targetFile));
			targetFileString = new String(targetFileBytes);
			targetParentFile = Paths.get(targetFile).getParent().getFileName().toString();
			
		} catch (IOException e) {
			System.err.println(e);
		}
		
		
		// read source auths
		List<String> sourceAuths = new ArrayList<String>();
		Matcher m = Pattern.compile("'(.*?)@(.*?)'").matcher(sourceFileString);
		while (m.find()) {
			if (!m.group().equals("'@CLASS'"))
				if (!sourceAuths.contains(m.group()))
					sourceAuths.add(m.group());
		}
		System.out.println(sourceParentFile + " size: " + sourceAuths.size());
		
		// read target auths
		List<String> targetAuths = new ArrayList<String>();
		m = Pattern.compile("'(.*?)@(.*?)'").matcher(targetFileString);
		while (m.find()) {
			if (!m.group().equals("'@CLASS'"))
				if (!targetAuths.contains(m.group()))
					targetAuths.add(m.group());
		}
		System.out.println(targetParentFile + " size: " + targetAuths.size());
		System.out.println();
		
		// all sourceAuths in targetAuths ?
		List<String> missingAuths = findMissing(sourceAuths, targetAuths);
		System.out.println("Missing auths in " + targetParentFile + " size " + missingAuths.size()  );
		for (String missingAuth : missingAuths){
			System.out.println(sourceParentFile + " " + missingAuth + " not found in " + targetParentFile);
		}
		System.out.println();
		
		// all sourceAuths in targetAuths ?
		missingAuths = findMissing(targetAuths, sourceAuths);
		System.out.println("Missing auths in " + sourceParentFile + " size " + missingAuths.size()  );
		for (String missingAuth : missingAuths){
			System.out.println(targetParentFile + " " + missingAuth + " not found in " + sourceParentFile);	
		}
	}
	
	private static List<String> findMissing(List<String> sourceAuths,List<String> targetAuths){
		List<String> missingAuths = new ArrayList<String>();
		for (String sourceAuth : sourceAuths){
			boolean found = false;
			for (String targetAuth : targetAuths){
				if (getFirst(sourceAuth).equals(getFirst(targetAuth))){
					found = true;
					continue;
				}
					
			}
			if (!found)
				missingAuths.add(sourceAuth);
		}
		return missingAuths;
	}
	
	
	private static String getFirst(String s){
		return s.split("@")[0].replaceFirst("T","").replaceFirst("t","");
	}

}
