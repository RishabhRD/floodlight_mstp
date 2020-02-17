
package net.floodlightcontroller.floodlight_mstp;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.projectfloodlight.openflow.types.OFPort;

import net.floodlightcontroller.packet.BasePacket;
import net.floodlightcontroller.packet.IPacket;
import net.floodlightcontroller.packet.LLC;

public class BPDU extends BasePacket {
    private final long destMac = 0x0180c2000000L; // 01-80-c2-00-00-00 
    private LLC llcHeader;
    private short protocolId = 0;
    private byte version = 0;
    private byte type;
    private byte flags;
    private byte[] rootBridgeId;
    private int rootPathCost;
    private byte[] senderBridgeId; // switch cluster MAC
    private short portId; // port it was transmitted from
    private int seqNumber; // sequence number of current packet
    private short helloTime; // 256ths of a second
    private short forwardDelay; // 256ths of a second
    
    public BPDU() {
        rootBridgeId = new byte[8];
        senderBridgeId = new byte[8];
        
        llcHeader = new LLC();
        llcHeader.setDsap((byte) 0x42);
        llcHeader.setSsap((byte) 0x42);
        llcHeader.setCtrl((byte) 0x03);
        
    }
    
    @Override
    public byte[] serialize() {
        byte[] data;
        // TODO check these
        if (type == 0x0) { 
            // config
            data = new byte[38];
        } else {
            // topology change
            data = new byte[7]; // LLC + TC notification
        }
        
        ByteBuffer bb = ByteBuffer.wrap(data);
        // Serialize the LLC header
        byte[] llc = llcHeader.serialize();
        bb.put(llc, 0, llc.length);
        bb.putShort(protocolId);
        bb.put(version);
        bb.put(type);
        
        if (type == 0x0) {
            bb.put(flags);
            bb.put(rootBridgeId, 0, rootBridgeId.length);
            bb.putInt(rootPathCost);
            bb.put(senderBridgeId, 0, senderBridgeId.length);
            bb.putShort(portId);
	    bb.putInt(seqNumber);
            bb.putShort(helloTime);
            bb.putShort(forwardDelay);
        }
        
        return data;
    }

    @Override
    public IPacket deserialize(byte[] data, int offset, int length) {
        ByteBuffer bb = ByteBuffer.wrap(data, offset, length);
        
        // LLC header
        llcHeader.deserialize(data, offset, 3);
        
        this.protocolId = bb.getShort();
        this.version = bb.get();
        this.type = bb.get();
        
        // These fields only exist if it's a configuration BPDU
        if (this.type == 0x0) {
            this.flags = bb.get();
            bb.get(rootBridgeId, 0, 6);
            this.rootPathCost = bb.getInt();
            bb.get(this.senderBridgeId, 0, 6);
            this.portId = bb.getShort();
	    this.seqNumber = bb.getInt();
            this.helloTime = bb.getShort();
            this.forwardDelay = bb.getShort();
        }
        
        return this;
    }

    public long getDestMac() {
        return destMac;
    }
    public void setPort(OFPort port){
	    this.portId = port.getShortPortNumber();
    }
    public OFPort getPort(){
	    return OFPort.ofShort(portId);
    }
    public int getSequenceNumber(){
	    return seqNumber;
    }
    public void setSequenceNumber(int seq){
	    this.seqNumber = seq;
    }
    public void setRootCostPath(int path){
	    this.rootPathCost = path;
    }
    public int getRootCostPath(){
	    return this.rootPathCost;
    }
    public byte[] getRootBridgeId(){
	    return this.rootBridgeId;
    }
    public void setRootBridgeId(byte[] id) throws UnsupportedEncodingException{
	    if(id.length!=8) throw new UnsupportedEncodingException("Not a valid bridge id");
	    else this.rootBridgeId = id;
    }
    public void setSenderBridgeId(byte[] id) throws UnsupportedEncodingException{
	    if(id.length!=8) throw new UnsupportedEncodingException("Now a valid bridge id");
	    this.senderBridgeId = id;
    }
    public byte[] getSenderBridgeId(){
	    return this.senderBridgeId;
    }
}
