package it.polito.ai.es03.model.postgis;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;

@Entity
@AssociationOverrides({
	@AssociationOverride(
			name="primaryKey.busLine", 
			joinColumns = @JoinColumn(name ="lineid")),
	@AssociationOverride(
			name="primaryKey.busStop",
			joinColumns = @JoinColumn(name ="stopid"))
})
public class BusLineStop implements Comparable<BusLineStop>{
	
	@EmbeddedId
	private BusLineStopPK primaryKey;
	public BusLineStopPK getPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(BusLineStopPK primaryKey) {
		this.primaryKey = primaryKey;
	}
	public BusLine getBusLine(){
		return this.primaryKey.getBusLine();
	}
	public void setBusLine(BusLine busLine){
		this.primaryKey.setBusLine(busLine);
	}
	public BusStop getBusStop(){
		return this.primaryKey.getBusStop();
	}
	public void setBusStop(BusStop busStop){
		this.primaryKey.setBusStop(busStop);
	}
	public short getSequenceNumber() {
		return this.primaryKey.getSequenceNumber();
	}
	public void setSequenceNumber(short sequenceNumber) {
		this.primaryKey.setSequenceNumber(sequenceNumber);
	}
	public int compareTo(BusLineStop o) {
		return this.primaryKey.compareTo(o.getPrimaryKey());
	}
	
	
}
