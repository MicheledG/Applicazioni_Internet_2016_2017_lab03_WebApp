package it.polito.ai.es03.model;

public class TakenBus implements Comparable<TakenBus> {

	private String lineId;
	private int numberOfCoveredPortions;
	public String getLineId() {
		return lineId;
	}
	public void setLineId(String lineId) {
		this.lineId = lineId;
	}
	public int getNumberOfCoveredPortions() {
		return numberOfCoveredPortions;
	}
	public void setNumberOfCoveredPortions(int numberOfCoveredPortions) {
		this.numberOfCoveredPortions = numberOfCoveredPortions;
	}
	public int compareTo(TakenBus o) {
		Integer thisNumberOfCoveredPortions = this.numberOfCoveredPortions;
		Integer thatNumberOfCoveredPortions = o.getNumberOfCoveredPortions();
		return thisNumberOfCoveredPortions.compareTo(thatNumberOfCoveredPortions);
	}
}
