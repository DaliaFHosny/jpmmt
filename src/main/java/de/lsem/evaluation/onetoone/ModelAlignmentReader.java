package de.lsem.evaluation.onetoone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ModelAlignmentReader {
	private String seperator = "#";
	
	public ModelAlignmentReader() {
	}
	
	public ModelAlignmentReader(String seperator) {
		this.seperator = seperator;
	}
	
	public String getSeperator() {
		return this.seperator;
	}
	
	public void setSeperator(String seperator) {
		this.seperator = seperator;
	}
	
	public ModelAlignment read(String filename) {
		List<String> lines = this.getLines(filename);
		
		if (lines != null && lines.size() > 2) {
			ModelAlignment alignment = new ModelAlignment(lines.get(0), lines.get(1));		
			for (int a = 2; a < lines.size(); a++) {
				String[] parts = lines.get(a).split(this.getSeperator());
				ActivityMatch match = new ActivityMatch(parts[0].toLowerCase(), parts[1].toLowerCase());
				alignment.addActivityMatch(match);
			}
			return alignment;
		}
		else {
			System.out.println(filename);
		}
		return null;
	}
	
	private List<String> getLines(String name) {
		ArrayList<String> lines = new ArrayList<String>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(name));
			String line = br.readLine();
			while (line != null) {
				lines.add(new String(line.getBytes(), "UTF-8"));
				line = br.readLine();
			}		
			br.close();			
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}	
		
		return lines;
	}
	
	public Map<String, Map<String, ModelAlignment>> bulkRead(Collection<String> alignmentFiles) {
		HashMap<String, Map<String, ModelAlignment>> standard = new HashMap<String, Map<String, ModelAlignment>>();
		
		for (String file : alignmentFiles) {
			ModelAlignment alignment = this.read(file);
			
			if (alignment != null) {
				this.addAlignmentToStandard(alignment, standard);
			}
		}
		
		return standard;
	}
	
	public Map<String, Map<String, ModelAlignment>> bulkRead(String alignmentFolder) {
		HashMap<String, Map<String, ModelAlignment>> standard = new HashMap<String, Map<String, ModelAlignment>>();
		File folder = new File(alignmentFolder);
		for (File file : folder.listFiles()) {
			ModelAlignment alignment = this.read(file.getAbsolutePath());
			
			if (alignment != null) {
				this.addAlignmentToStandard(alignment, standard);
			}
		}
		
		return standard;
	}
	
	private void addAlignmentToStandard(ModelAlignment alignment, Map<String, Map<String, ModelAlignment>> standard) {
		if (!standard.containsKey(alignment.getFirstModel())) {
			standard.put(alignment.getFirstModel(), new HashMap<String, ModelAlignment>());
		}
		
		standard.get(alignment.getFirstModel()).put(alignment.getSecondModel(), alignment);
	}
}
