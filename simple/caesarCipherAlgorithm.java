import java.util.Random;

public class caesarCipherAlgorithm {
	public static String dataEncryption(String input) {
		
		String result = "";
			
		Random random = new Random();
		int caesarShiftNumber = random.nextInt(5 - 2) + 2;	//change it to just random
		
		//Caesar's encryption
		for (int i = 0; i < input.length(); i++) {
			
			//if an upper case character
			if (input.charAt(i) >= 'A' && input.charAt(i) <= 'Z') {
				int fromZero = input.charAt(i) - 'A';
				int shift = (fromZero + caesarShiftNumber) % 26;
				result += (char) (shift + 'A');
			}
			
			//if a lower case character
			else if (input.charAt(i) >= 'a' && input.charAt(i) <= 'z') {
				int fromZero = input.charAt(i) - 'a';
				int shift = (fromZero + caesarShiftNumber) % 26;
				result += (char) (shift + 'a');
			}
			
			//if a number
			else if (input.charAt(i) >= '0' && input.charAt(i) <= '9') {
				int fromZero = input.charAt(i) - '0';
				int shift = (fromZero + caesarShiftNumber) % 10;
				result += (char) (shift + '0');
			}
			
			//special characters for ASCII values between 32 and 47
			else if (input.charAt(i) >= ' ' && input.charAt(i) <= '/') {
				int fromZero = input.charAt(i) - ' ';
				int shift = (fromZero + caesarShiftNumber) % 16;
				result += (char) (shift + ' ');
			}
			
			//special characters for ASCII values between 58 and 64
			else if (input.charAt(i) >= ':' && input.charAt(i) <= '@') {
				int fromZero = input.charAt(i) - ':';
				int shift = (fromZero + caesarShiftNumber) % 7;
				result += (char) (shift + ':');
			}
			
			//special characters for ASCII values between 91 and 96
			else if (input.charAt(i) >= '[' && input.charAt(i) <= '`') {
				int fromZero = input.charAt(i) - '[';
				int shift = (fromZero + caesarShiftNumber) % 6;
				result += (char) (shift + '[');
			}
			
			//special characters for ASCII values between 123 and 126
			else if (input.charAt(i) >= '{' && input.charAt(i) <= '~') {
				int fromZero = input.charAt(i) - '{';
				int shift = (fromZero + caesarShiftNumber) % 4;
				result += (char) (shift + '{');
			}
			
			else {
				result += input.charAt(i);
			}
		}
		
		//reverse string
		String reverseResult = "";
		for (int i = result.length() - 1; i >= 0; i--) {
			reverseResult += result.charAt(i);
		}
		
//		System.out.println(caesarShiftNumber);
//		System.out.println(result);
		return result;
		
	}
}
