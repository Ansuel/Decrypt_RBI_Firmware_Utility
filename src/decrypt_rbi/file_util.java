package decrypt_rbi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class file_util {
	
	public static void processFile(ByteArrayOutputStream outputStream, byte[] osck,gui_construct Scene) throws IOException, DataFormatException, InvalidKeyException,
	NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, 
	IllegalBlockSizeException, BadPaddingException {
		ByteArrayInputStream data;
		
		while (true) {
			data = new ByteArrayInputStream(outputStream.toByteArray());
			outputStream.reset();
			byte[] payloadtype = new byte[1];
			data.read(payloadtype, 0, 1);

			if ( payloadtype[0] == (byte) 0xB0 ) {
				Scene.log.appendText("Cleaning file from magic bit...\n");
				finishProcess(data, outputStream);
				break;
			} else if ( payloadtype[0] == (byte) 0xB4 ) {
				Scene.log.appendText("Extracting decrypted rbi file...\n");
				extractRbi(data,outputStream);
			} else if ( payloadtype[0] == (byte) 0xB8 ) {
				Scene.log.appendText("Removing signature from tmp file...\n");
				removeSignature(data,outputStream);
			} else if ( payloadtype[0] == (byte) 0xB7 ) {
				Scene.log.appendText("Decrypting rbi firmware...\n");
				decryptFile(data,outputStream,osck);
			} else {
				throw new InvalidKeyException("Can't recognize magic bit! Assume wrong decryption!");
			}
		}
		
		data.close();
	}
	
	public static void saveFile(ByteArrayOutputStream outputStream,File file,gui_construct Scene) throws IOException {
		FileOutputStream outStream = new FileOutputStream(file);
		outStream.write(outputStream.toByteArray());
		outStream.close();
		outputStream.close();
		Scene.log.appendText("Decrypted file saved here: "+file.getAbsolutePath()+"\n");
		Scene.log.appendText("You can now use Binwalk to extract the decrypted file or flash it to the mtd flash\n");
	}

	public static void removeSignature(ByteArrayInputStream inputStream, ByteArrayOutputStream outputStream) throws IOException {

		//First byte is already removed by reading the magic bit
		inputStream.skip( 4 + 1 + 4 + 32);
		byte[] buffer = new byte[1024];
		int count = 0;
		while (inputStream.available() != 0) {
			count = inputStream.read(buffer);
			outputStream.write(buffer, 0 , count);
		}
	}

	public static void decryptFile(ByteArrayInputStream inputStream, ByteArrayOutputStream outputStream,byte[] osck)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {

		final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

		byte[] iv = new byte[16];
		byte[] keydata = new byte[48];

		//First byte is already removed by reading the magic bit 
		inputStream.skip(4 + 1 + 4);

		inputStream.read(iv, 0, 16);
		inputStream.read(keydata, 0, 48);

		IvParameterSpec ivParameterSpec = new IvParameterSpec(iv, 0, cipher.getBlockSize());
		SecretKeySpec secretSpec = new SecretKeySpec(osck, "AES");

		cipher.init(Cipher.DECRYPT_MODE, secretSpec, ivParameterSpec);

		keydata = cipher.doFinal(keydata);
		inputStream.read(iv, 0, 16);

		ivParameterSpec = new IvParameterSpec(iv, 0, cipher.getBlockSize());
		secretSpec = new SecretKeySpec(keydata, "AES");

		// We should investigate this 80 bit skipped
		// byte skipped = magic bit + keydata + iv + something

		cipher.init(Cipher.DECRYPT_MODE, secretSpec, ivParameterSpec);
		byte[] buffer = new byte[1024];
		int count = 0;
		while (inputStream.available() != 0) {
			count = inputStream.read(buffer);
			outputStream.write(buffer, 0 ,count);
		}

		byte[] decrypted = cipher.doFinal(outputStream.toByteArray());
		outputStream.reset();
		outputStream.write(decrypted);
	}

	public static void extractRbi(ByteArrayInputStream inputStream, ByteArrayOutputStream outputStream) throws IOException, DataFormatException {
		
		//First byte is already removed by reading the magic bit 
		inputStream.skip( 4 + 1 + 4);
		
		InflaterInputStream inflater = new InflaterInputStream(inputStream, new Inflater());
		
		int count = 0;
		  
		byte[] buffer = new byte[1024];
		while ((count = inflater.read(buffer)) != -1) {
			outputStream.write(buffer,0,count);
		}

	}
	
	public static void finishProcess(ByteArrayInputStream inputStream, ByteArrayOutputStream outputStream) throws IOException {
		//First byte is already removed by reading the magic bit
		inputStream.skip( 4 + 1 );
		byte[] buffer = new byte[1024];
		int count = 0;
		while (inputStream.available() != 0) {
			count = inputStream.read(buffer);
			outputStream.write(buffer, 0 , count);
		}
	}

	public static int calcPyloadStart(RandomAccessFile file_stream) throws IOException {
		byte[] pyloadstart = new byte[4];
		//first 0x2f byte are static and header size is in 0x28 and long 4 byte
		file_stream.seek(0x28);
		file_stream.read(pyloadstart, 0, 4);
		return new BigInteger(pyloadstart).intValue();
	}

	public static void readglobalHeader(RandomAccessFile file_stream,int offset,Map<String,String> header_table) throws IOException {

		byte[] globalHeader = new byte[offset];
		
		file_stream.seek(0);
		file_stream.read(globalHeader, 0, offset);
		
		header_parser.parse(globalHeader,header_table);
		
	}

	public static void loadFile(File file,ByteArrayOutputStream outputStream,Map<String,String> header_table) throws IOException {
		
		RandomAccessFile file_stream = new RandomAccessFile(file, "r");
		
		int offset = calcPyloadStart(file_stream);

		readglobalHeader(file_stream,offset,header_table);
		file_stream.seek(offset);
		int count = 0;
		byte[] buffer = new byte[1024];
		while ( (count = file_stream.read(buffer)) !=-1) {
			outputStream.write(buffer,0,count);
		}
		file_stream.close();
		
	}
}
