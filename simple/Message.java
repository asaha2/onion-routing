
public class Message {
	
	byte[] data;
	enum CellType {
		control,
		proxy
	}
	enum cmd {
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
	CellType type;
	int circID;
	
	public Message() {
		// TODO Auto-generated constructor stub
	}
	
	public byte[] createMessage() {
		// TODO Generate random circID 
		if(type == CellType.control) {
			
		} else {
			
		}
		return data;
	}
	
}
