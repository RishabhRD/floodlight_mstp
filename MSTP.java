package net.floodlightcontroller.floodlight_mstp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPortConfig;
import org.projectfloodlight.openflow.protocol.OFPortDesc;
import org.projectfloodlight.openflow.protocol.OFPortMod;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.OFPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IOFSwitchListener;
import net.floodlightcontroller.core.PortChangeType;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;

public class MSTP implements IFloodlightModule ,IOFMessageListener, IOFSwitchListener{
	protected static Logger log = LoggerFactory.getLogger(MSTP.class);
	protected IFloodlightProviderService floodlightProviderService;
	protected int curSeqNumber = 0;
	protected TreeMap<IOFSwitch,ArrayList<OFPort>> blockedMap;

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		// TODO Auto-generated method stub
		floodlightProviderService = context.getServiceImpl(IFloodlightProviderService.class);
		blockedMap = new TreeMap<>();

	}

	@Override
	public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
		// TODO Auto-generated method stub
		floodlightProviderService.addOFMessageListener(OFType.PACKET_IN, this);

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void switchAdded(DatapathId switchId) {
		// TODO Auto-generated method stub
		unblockAll();
		startProcess();
	}

	@Override
	public void switchRemoved(DatapathId switchId) {
		// TODO Auto-generated method stub
		unblockAll();
		startProcess();
	}

	@Override
	public void switchActivated(DatapathId switchId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void switchPortChanged(DatapathId switchId, OFPortDesc port, PortChangeType type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void switchChanged(DatapathId switchId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void switchDeactivated(DatapathId switchId) {
		// TODO Auto-generated method stub

	}
	private void startProcess(){

	}
	
	private void block(IOFSwitch sw,OFPort port){
		sendPortMod(sw,port,false);
	}

	private void unblockAll(){
		Set<Entry<IOFSwitch,ArrayList<OFPort>>> set = blockedMap.entrySet();
		Iterator<Entry<IOFSwitch,ArrayList<OFPort>>> itr = set.iterator();
		while(itr.hasNext()){
			Entry<IOFSwitch,ArrayList<OFPort>> entry = itr.next();
			for(OFPort port : entry.getValue()){
				sendPortMod(entry.getKey(),port,true);
			}
		}
		blockedMap.clear();
	}

	private void sendPortMod(IOFSwitch sw,OFPort port,boolean enable){
		OFPortMod.Builder builder = sw.getOFFactory().buildPortMod();
		HashSet<OFPortConfig> set = new HashSet<>();
		if(!enable){
			set.add(OFPortConfig.PORT_DOWN);
		}
		HashSet<OFPortConfig> mask = new HashSet<>();
		mask.add(OFPortConfig.PORT_DOWN);
		OFPortMod mod  = builder.setPortNo(port).setConfig(set).setMask(mask).setAdvertise(0x00).setHwAddr(sw.getPort(port).getHwAddr()).build();
		sw.write(mod);
	}

}
