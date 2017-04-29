package it.polito.ai.es03.model;

import javax.persistence.Id;
import javax.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

@Entity
public class BusStop{
	
	@Id
	private String id;
	private String name;
	private double lat;
	private double lng;
	@OneToMany(mappedBy="primaryKey.busStop")
	private List<BusLineStop> stoppingLines = new ArrayList<BusLineStop>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public List<BusLineStop> getStoppingLines() {
		return stoppingLines;
	}
	public void setStoppingLines(List<BusLineStop> stoppingLines) {
		this.stoppingLines = stoppingLines;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		BusStop other = (BusStop) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
