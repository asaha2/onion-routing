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
		//this.data= new byte[512];
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
			int len = Math.min(498, this.data.length);
			data[12]=(byte)len;
			data[11]=(byte)(len>>8);
			System.arraycopy(this.data,0,data,14,len);
			
		} else {
			data[2]=(byte) cmd.ordinal();
			int len = Math.min(498, this.data.length);
			System.arraycopy(this.data,0,data,3,len);
			
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
			int len = Message.unsignedToBytes(data,12,2);
			m.data= new byte[len];
			System.arraycopy(data,14,m.data,0,len);
		}
		return m;
			
	}
	public static int unsignedToBytes(byte a)
	{
	    int b = a & 0xFF;
	    return b;
	}
	public static int unsignedToBytes(byte[] a,int offset,int len)
	{int result=0;
		if (len>4){
		   len =4;
		   }
		for(int i = 0 ; i < len; i++){
			result |= unsignedToBytes(a[ offset-i]) << (i * 8);
		}
	   return result;
//	   if (len>4){
//	   len =4;
//   }
//   if (len<0){
//	   len =0;
//   }
//   if (offset+len>=a.length){
//	   len =a.length-offset;
//   }
//   if(offset<0){
//	   offset=
//   }
	}
}
