package net.floodlightcontroller.floodlight_mstp;

import org.projectfloodlight.openflow.types.OFPort;

public class PortCost{
	private int cost;
	private OFPort port;
	public PortCost(OFPort port,int cost){
		this.port = port;
		this.cost = cost;
	}
	public void setPort(OFPort port){
		this.port = port;
	}
	public OFPort getPort(){
		return port;
	}
	public void setCost(int cost){
		this.cost = cost;
	}
	public int getCost(){
		return cost;
	}
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof PortCost)) return false;
		PortCost pc = (PortCost) obj;
		return (pc.cost == cost) && (pc.port.equals(port));
	}
} 
