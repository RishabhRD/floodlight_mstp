package net.floodlightcontroller.floodlight_mstp;

import java.nio.ByteBuffer;

import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFPort;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.packet.Ethernet;

public class BPDUUtil{
	private static final long destMac = 0x0180c2000000L; // 01-80-c2-00-00-00
	public static byte[] createBridgeId(IOFSwitch sw,OFPort port){
		ByteBuffer bf = ByteBuffer.allocate(8);
		MacAddress addr = sw.getPort(port).getHwAddr();
		bf.putShort(port.getShortPortNumber());
		bf.put(addr.getBytes());
		return bf.array();
	}
	public static boolean isBPDU(Ethernet eth){
		if(eth.getDestinationMACAddress().equals(MacAddress.of(destMac)))return true;
		return false;
	}
	public static long getSTPMac(){
		return destMac;
	}
}
