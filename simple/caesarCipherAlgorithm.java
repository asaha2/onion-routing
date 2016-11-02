import java.util.Random;

public class caesarCipherAlgorithm {
	
	//to encrypt data
	public String dataEncryption(String input) {
		
		String result = "";
			
		Random random = new Random();
		int caesarShiftNumber = Math.abs(random.nextInt(10 - 1) + 1);	//change it to just random
		
		result += caesarShiftNumber;
		
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
		
		return reverseResult;
		
	}
	
	//to decrypt data
	public String dataDecryption(String input) {
		
		String reverseInput = "";
		String finalResult = "";
		
		//reverse string
		for (int i = input.length() - 1; i >= 0; i--) {
			reverseInput += input.charAt(i);
		}
		
		int caesarShiftNumber = reverseInput.charAt(0) - '0';		
		
		//caesar decryption
		for (int i = 1; i < reverseInput.length(); i++) {
			//if an upper case character
			if (reverseInput.charAt(i) >= 'A' && reverseInput.charAt(i) <= 'Z') {
				int fromZero = reverseInput.charAt(i) - 'A';
				int shift = (fromZero - caesarShiftNumber) % 26;
				
				if (shift < 0) {
					shift += 26;
				}
				
				finalResult += (char) (shift + 'A');
			}
			
			//if an lower case character
			else if (reverseInput.charAt(i) >= 'a' && reverseInput.charAt(i) <= 'z') {
				int fromZero = reverseInput.charAt(i) - 'a';
				int shift = (fromZero - caesarShiftNumber) % 26;
				
				if (shift < 0) {
					shift += 26;
				}

				finalResult += (char) (shift + 'a');

			}
			
			//if a number
			else if (reverseInput.charAt(i) >= '0' && reverseInput.charAt(i) <= '9') {
				int fromZero = reverseInput.charAt(i) - '0';
				int shift = (fromZero - caesarShiftNumber) % 10;
				
				if (shift < 0) {
					shift += 10;
				}
				
				finalResult += (char) (shift + '0');
			}
			
			//special characters for ASCII values between 32 and 47
			else if (reverseInput.charAt(i) >= ' ' && reverseInput.charAt(i) <= '/') {
				int fromZero = reverseInput.charAt(i) - ' ';
				int shift = (fromZero - caesarShiftNumber) % 16;
				
				if (shift < 0) {
					shift += 16;
				}
				
				finalResult += (char) (shift + ' ');
			}
			
			//special characters for ASCII values between 58 and 64
			else if (reverseInput.charAt(i) >= ':' && reverseInput.charAt(i) <= '@') {
				int fromZero = reverseInput.charAt(i) - ':';
				int shift = (fromZero - caesarShiftNumber) % 7;
				
				if (shift < 0) {
					shift += 7;
				}
				
				finalResult += (char) (shift + ':');
			}
			
			//special characters for ASCII values between 91 and 96
			else if (reverseInput.charAt(i) >= '[' && reverseInput.charAt(i) <= '`') {
				int fromZero = reverseInput.charAt(i) - '[';
				int shift = (fromZero - caesarShiftNumber) % 6;
				
				if (shift < 0) {
					shift += 6;
				}
				
				finalResult += (char) (shift + '[');
			}
			
			//special characters for ASCII values between 123 and 126
			else if (reverseInput.charAt(i) >= '{' && reverseInput.charAt(i) <= '~') {
				int fromZero = reverseInput.charAt(i) - '{';
				int shift = (fromZero - caesarShiftNumber) % 4;
				
				if (shift < 0) {
					shift += 4;
				}
				
				finalResult += (char) (shift + '{');
			}
			
			else {
				finalResult += input.charAt(i);
			}
				
		}

		return finalResult;
		
	}
	
	//verify if encryption/decryption works
	public boolean verifyEncryptionDecryption(String a, String b) {
		if (a.equals(b)) {
			return true;
		}
		
		return false;
	}
	
	public static void main (String[] args) {
		caesarCipherAlgorithm caesarCipherAlgorithm = new caesarCipherAlgorithm();
		String input = "oiuyrfn89eyrq34807r340r83r23-230r943-r4/.//``~  .erger ,.ergeruh3oi34....ddz";
		
		String encryption = caesarCipherAlgorithm.dataEncryption(input);
		String decryption = caesarCipherAlgorithm.dataDecryption(encryption);
		
		if (caesarCipherAlgorithm.verifyEncryptionDecryption(input, decryption)) {
			System.out.println("Encryption/Decryption works");
		}
		
		else {
			System.out.println("Encryption/decryption doesn't work");
		}
	}
	
}
