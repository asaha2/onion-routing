package simple;

import java.util.ArrayList;

public class Message {

	static byte[] dat = new byte[0];
	byte[] data = dat;

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


	public Message() {
		// this.data= new byte[512];
		// TODO Auto-generated constructor stub
	}


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

		}
		return data;
	}


	public static Message receiveMessage(byte[] data) {
		Message m = new Message();
		byte x = data[2];
		m.cmd = Cmd.values()[x];
		if (x == Cmd.relay.ordinal()) {
			m.type = CellType.proxy;
			x = data[13];
			m.cmd = Cmd.values()[x];
			int len = Message.bytesToInt(data, 12, 2);
			m.data = new byte[len];
			m.length= len;
			System.arraycopy(data, 14, m.data, 0, len);
		}
		return m;


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
 int length;
 
 
	public static Message[] decompose(String text) {
		byte[] wall = text.getBytes();
		int numPackets = wall.length / 498 + 1;

		int lastPacketLength = wall.length % 498; // can be 0
		if (lastPacketLength == 0) {
			lastPacketLength = 498;
			numPackets--;
		}
		Message[] messages = new Message[numPackets+1];
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
		Message m = new Message();
		messages[numPackets ] = m;
		m.type = CellType.proxy;
		m.cmd = Cmd.end;
		m.data = new byte[0];

		return messages;
	}

	public static String compose(ArrayList<Message> messages) {
		int p = messages.size(), numBytes = (p-1) * 498 + messages.get(p-1).length, i = 0;

		byte[] wall = new byte[numBytes];
		int len = 498;
		for (Message m : messages ) {
			len=m.length;

			System.arraycopy(m.data, 0, wall, 498 * i, len);
			i++;
		}

		return new String(wall);
	}

}
