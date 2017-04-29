package it.polito.ai.es03.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class BusLine implements Comparable<BusLine>{
	
	@Id
	private String line;
	private String description;
	
	@OneToMany(mappedBy="primaryKey.busLine")
	private List<BusLineStop> lineStops = new ArrayList<BusLineStop>();
	
	public String getLine() {
		return line;
	}
	public void setLine(String line) {
		this.line = line;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public int compareTo(BusLine o) {
		return this.line.compareTo(o.getLine());
	}
	public List<BusLineStop> getLineStops() {
		return lineStops;
	}
	public void setLineStops(List<BusLineStop> lineStops) {
		this.lineStops = lineStops;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((line == null) ? 0 : line.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BusLine other = (BusLine) obj;
		if (line == null) {
			if (other.line != null)
				return false;
		} else if (!line.equals(other.line))
			return false;
		return true;
	}
	
}
