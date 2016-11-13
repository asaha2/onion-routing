package simple;
public class Message {
	
	byte[] data;
	enum CellType {
		not_selected,
		control,
		proxy
	}
	enum Cmd {
		not_selected,
		//Control
		padding,
		create,
		created,
		destroy,
		//Proxy
		relay,
		data,
		begin,
		end,
		teardown, 
		connected,
		extend,
		extended,
		truncate,
		truncated,
		sendme,
		drop
	}
	public CellType type;
	public int circID;
	public Cmd cmd;
	
	public Message() {
		this.data= new byte[512];
		// TODO Auto-generated constructor stub
	}
	public boolean isCreate(){return this.cmd==Cmd.create;}
	public byte[][] createMessage() {
		// TODO Generate random circID 
		byte[][] dataArray= new byte[1][512];
		byte [] data=dataArray[0];
		data[0]=(byte) (circID>>8);
		data[1] = (byte) circID;
		
		if(type == CellType.proxy) {
			data[2]=(byte) Cmd.relay.ordinal();
			data[13]=(byte) cmd.ordinal();
			System.arraycopy(this.data,0,data,14,498);
		} else {
			data[2]=(byte) cmd.ordinal();
			System.arraycopy(this.data,0,data,3,509);
		}
		return dataArray;
	}
	
	public static Message receiveMessage(byte[] data){
		Message m = new Message();
		byte x = data[2];
		m.cmd=Cmd.values()[x];
		if (x==Cmd.relay.ordinal()) {
			m.type=CellType.proxy;
			x=data[13];
			m.cmd=Cmd.values()[x];
		}
		return m;
			
	}
}
