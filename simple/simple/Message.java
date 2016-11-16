package simple;

public class Message {
<<<<<<< HEAD

	byte[] data = new byte[0];
=======
	static byte[] dat = new byte[0];
	byte[] data = dat;

>>>>>>> 580d564... freezes
	enum CellType {
		not_selected, control, proxy
	}

	enum Cmd {
		not_selected,
		// Control
		padding, create, created, destroy,
		// Proxy
		relay, data, begin, end, teardown, connected, extend, extended, truncate, truncated, sendme, drop
	}

	public CellType type;
	public int circID;
	public Cmd cmd;

<<<<<<< HEAD

=======
>>>>>>> 580d564... freezes
	public Message() {
		// this.data= new byte[512];
		// TODO Auto-generated constructor stub
	}
<<<<<<< HEAD
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
=======

	public boolean isCreate() {
		return this.cmd == Cmd.create;
	}

	public byte[] createMessage() {
		// TODO Generate random circID
		byte[] data = new byte[512];
		shortToBytes(circID, data, 0);

		if (type == CellType.proxy) {
			data[2] = (byte) Cmd.relay.ordinal();
			data[13] = (byte) cmd.ordinal();
			int len = Math.min(498, this.data.length);
			shortToBytes(len, data, 11);
			System.arraycopy(this.data, 0, data, 14, len);

		} else {
			data[2] = (byte) cmd.ordinal();

			int len = Math.min(498, this.data.length);
			System.arraycopy(this.data, 0, data, 3, len);
>>>>>>> 580d564... freezes

		}
		return data;
	}

<<<<<<< HEAD
	public static Message receiveMessage(byte[] data){
=======
	public static Message receiveMessage(byte[] data) {
>>>>>>> 580d564... freezes
		Message m = new Message();
		byte x = data[2];
		m.cmd = Cmd.values()[x];
		if (x == Cmd.relay.ordinal()) {
			m.type = CellType.proxy;
			x = data[13];
			m.cmd = Cmd.values()[x];
			int len = Message.bytesToInt(data, 12, 2);
			m.data = new byte[len];
			System.arraycopy(data, 14, m.data, 0, len);
		}
		return m;

<<<<<<< HEAD
	}
	public static int unsignedToBytes(byte a)
	{
		int b = a & 0xFF;
		return b;
	}
	public static int unsignedToBytes(byte[] a,int offset,int len)
	{
		int result=0;
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
=======
	}

	private static int unsign(byte a) {
		int b = a & 0xFF;
		return b;
	}

	private static int bytesToInt(byte[] a, int offset, int len) {
		int result = 0;
		if (len > 4) {
			len = 4;
		}

		for (int i = 0; i < len; i++) {
			result |= unsign(a[offset - i]) << (i * 8);
		}
		return result;
	}

	private static byte[] shortToBytes(int a) {
		byte[] b = new byte[2];
		shortToBytes(a, b, 0);
		return b;
	}

	private static void shortToBytes(int a, byte[] data, int offset) {
		data[offset + 1] = (byte) a;
		data[offset] = (byte) (a >> 8);
	}

	public static Message[] decompose(String text) {
		byte[] wall = text.getBytes();
		int numPackets = wall.length / 498 + 1;

		int lastPacketLength = wall.length % 498; // can be 0
		if (lastPacketLength == 0) {
			lastPacketLength = 498;
			numPackets--;
		}
		Message[] messages = new Message[numPackets];
		for (int i = 0; i < numPackets; i++) {
			int len = 498;
			if (i == numPackets - 1) {
				len = lastPacketLength;
			}
			Message m = new Message();
			messages[i] = m;
			m.type = CellType.proxy;
			m.cmd = Cmd.data;
			m.data = new byte[len];
			System.arraycopy(wall, 498 * i, m.data, 0, len);
		}

		return messages;
	}

	public static String compose(Message[] messages) {
		int p = messages.length, numBytes = messages.length * 498;

		byte[] wall = new byte[numBytes];
		int len = 498;
		for (int i = 0; i < p; i++) {
			if (i == p - 1) {
				len = bytesToInt(messages[i].data, 11, 2);
			}

			System.arraycopy(messages[i].data, 0, wall, 498 * i, len);
		}

		return new String(wall, 0, numBytes - 498 + len);
>>>>>>> 580d564... freezes
	}

}
