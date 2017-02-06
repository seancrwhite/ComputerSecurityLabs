import java.io.File;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

/**
 * @author Sean White and Tyler Bass
 *
 */
public class Main {
	// make this static so it doesn't keep getting created
	// and destroyed every time md5() is called
	static MessageDigest md5digest;
	
	public static void main(String[] args) throws NoSuchAlgorithmException {
		md5digest = MessageDigest.getInstance("MD5");
		String[] hashes = {"6f047ccaa1ed3e8e05cde1c7ebc7d958",
		                   "275a5602cd91a468a0e10c226a03a39c",
		                   "b4ba93170358df216e8648734ac2d539",
		                   "dc1c6ca00763a1821c5af993e0b6f60a",
		                   "8cd9f1b962128bd3d3ede2f5f101f4fc",
		                   "554532464e066aba23aee72b95f18ba2"};
		try {
			// best list i have found yet is https://github.com/danielmiessler/SecLists/blob/master/Passwords/10_million_password_list_top_1000000.txt
			Scanner dictionary = new Scanner(new File("dictionary.txt"));
			while (dictionary.hasNextLine()) {
				String currentEntry = dictionary.nextLine();
				String currentMd5 = md5(currentEntry);
				/*System.out.println(currentEntry);
				System.out.println(currentMd5);*/
				for (String hash : hashes) {
					if (hash.equals(currentMd5)) {
						System.out.println("The password for hash value " + hash + " is " + currentEntry + ", it takes the program x sec to recover this password");
					}
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("dictionary file not found");
		}
	}
	
	public static String md5(String text) {
		byte[] md5bytes = md5digest.digest(text.getBytes());
		
		// best way i could find to get the hex value
		StringBuilder stringbuilder = new StringBuilder();
		for (byte md5byte : md5bytes) {
			stringbuilder.append(String.format("%02x", md5byte));
		}
		return stringbuilder.toString();
	}
}
