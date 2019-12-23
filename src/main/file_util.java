/*******************************************************************************
 * Copyright (C) 2019, Christian Marangi
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package main;

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
import java.util.Arrays;
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
	
	public static void processFile(ByteArrayOutputStream outputStream, byte[] osck, byte[] osik, gui_construct Scene) throws IOException, DataFormatException, InvalidKeyException,
	NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, 
	IllegalBlockSizeException, BadPaddingException {
        
		ByteArrayInputStream payload;
        byte[] payloadType;
        
		while (true) {
			payload = new ByteArrayInputStream(outputStream.toByteArray());
            outputStream.reset();
			payloadType = new byte[1];
			payload.read(payloadType, 0, 1);

			if ( payloadType[0] == (byte) 0xB0 ) {
				Scene.log.appendText("Cleaning payload from magic bit...\n");
				finishProcess(payload, outputStream);
				break;
			} else if ( payloadType[0] == (byte) 0xB4 ) {
				Scene.log.appendText("Decompressing payload...\n");
				inflatePayload(payload, outputStream);
			} else if ( payloadType[0] == (byte) 0xB8 ) {
                if ( osik != null ) {
                    Scene.log.appendText("Checking payload signature...\n");
                    // TODO sigCheck()
                } else {
				    Scene.log.appendText("Skipping payload signature check...\n");
				    unsignPayload(payload, outputStream);
                }
			} else if ( payloadType[0] == (byte) 0xB7 ) {
				Scene.log.appendText("Decrypting rbi firmware...\n");
				decryptPayload(payload, outputStream, osck);
			} else {
				throw new InvalidKeyException("Can't recognize magic bit! Assume wrong decryption!");
			}
		}
		
		payload.close();
	}
	
	private static void consumeInfoBlock(RandomAccessFile stream, int len, Map<String,String> infoblock_table) throws IOException {
		int read,block_len;
		byte[] block = new byte[4];
		int data_convert;
		String tag, res;
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		
		while(len > 0) {
			read = stream.read(block, 0, 4);
			block_len = new BigInteger(block).intValue();
			len -= block_len;
			block_len -= read;
			
			// TAG NAME
			read = stream.read(block, 0, 4);
			block_len -= read;
			tag = new String(block).trim();
			
			// DATA
			while(block_len > 0) {
				read = stream.read(block, 0, 4);
				block_len -= read;
				data.write(block);
			}
			
			res = data.toString();
			if (data.size() == 4) {
				data_convert = new BigInteger(block).intValue();
				if (data_convert == 1 || data_convert == 0)
					res = data_convert == 1 ? "true" : "false";
			}
			
			infoblock_table.put(tag,res);
			
			data.reset();
		}
		
		data.close();
	}
	
	private static void detectSignature(RandomAccessFile stream, Map<String,String> infoblock_table) throws IOException {
		byte[] block = new byte[4];
		byte[] empty_block = {(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
		
		int read,block_len;
		String tag, res;
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		
		read = stream.read(block, 0, 4);
		if ( Arrays.equals(block, empty_block) )
			return;
		
		block_len = new BigInteger(block).intValue();
		block_len -= read;
		
		// TAG NAME
		read = stream.read(block, 0, 4);
		block_len -= read;
		tag = new String(block).trim();
		
		// DATA
		while(block_len > 0) {
			read = stream.read(block, 0, 4);
			block_len -= read;
			data.write(block);
		}
		
		res = string_util.bytesToHexString(data.toByteArray());
		
		infoblock_table.put(tag,res);
		
		data.close();
		
	}
	
	public static void detectInfoBlockOffset(File file, gui_construct Scene) throws IOException {
		
		RandomAccessFile file_stream = new RandomAccessFile(file, "r");
		
		Map<String,String> infoblock_table = Scene.getRbiConstructor().getInfoBlockTable();
		
		//First 16 byte are unknown data... 
		//We know that the first one is always FF FF FF FF
		// The last 4 block is the info block offset
		byte[] block = new byte[4];
		int offset,len;
		
		// Skip first 3 blocks (11 byte)
		file_stream.seek(12);
		// Read info block offset (last block)
		file_stream.read(block, 0, 4);
		
		
		offset = new BigInteger(block).intValue();
		if (offset > 0) {
			Scene.log.appendText("Detected InfoBlock at offset: "+offset+"\n");
			file_stream.seek(offset);
			file_stream.read(block, 0, 4);
			len = new BigInteger(block).intValue();
			Scene.log.appendText("Detected InfoBlock of size: "+len+"\n");
			
			consumeInfoBlock(file_stream, len - 4, infoblock_table);
			detectSignature(file_stream, infoblock_table);
			Scene.updateInfoBlockSubPanel();
		}
		
		file_stream.close();
	}
	
	public static void saveFile(ByteArrayOutputStream outputStream,File file,gui_construct Scene) throws IOException {
        try (FileOutputStream outStream = new FileOutputStream(file)) {
            outStream.write(outputStream.toByteArray());
        }
		outputStream.close();
		Scene.log.appendText("Firmware partition file saved here: "+file.getAbsolutePath()+"\n");
		Scene.log.appendText("You can now use Binwalk to unpack kernel and root filesystem, or directly flash it with mtd write\n");
		
		detectInfoBlockOffset(file,Scene);
	}

	public static void unsignPayload(ByteArrayInputStream inputStream, ByteArrayOutputStream outputStream) throws IOException {

		//First byte is already removed by reading the magic bit
		inputStream.skip( 4 + 1 + 4 + 32);
		byte[] buffer = new byte[1024];
		int count;
		while (inputStream.available() != 0) {
			count = inputStream.read(buffer);
			outputStream.write(buffer, 0 , count);
		}
	}

	public static void decryptPayload(ByteArrayInputStream inputStream, ByteArrayOutputStream outputStream, byte[] key1)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {

        //First byte is already removed by reading the magic bit 
		inputStream.skip(4 + 1);
        
        byte[] payloadSize = new byte[4];
        inputStream.read(payloadSize, 0, 4);
        BigInteger datalen = new BigInteger(payloadSize);
        
		final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        IvParameterSpec ivParameterSpec;
        SecretKeySpec secretKeySpec;
        
        byte[] encryptedData, decryptedData;
        
		byte[] iv1 = new byte[16];
		inputStream.read(iv1, 0, 16);
        
        ivParameterSpec = new IvParameterSpec(iv1, 0, cipher.getBlockSize());
		secretKeySpec = new SecretKeySpec(key1, "AES");
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

        encryptedData = new byte[48];
        inputStream.read(encryptedData, 0, 48);
        decryptedData = cipher.doFinal(encryptedData);
        
        byte[] key2 = decryptedData;
        byte[] iv2 = new byte[16];
		inputStream.read(iv2, 0, 16);

		ivParameterSpec = new IvParameterSpec(iv2, 0, cipher.getBlockSize());
		secretKeySpec = new SecretKeySpec(key2, "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

        // We should investigate this 80 bit skipped
		// byte skipped = magic bit + keydata + iv + something

		byte[] buffer = new byte[4096];
		int count;
		while (inputStream.available() != 0 && datalen.longValue() >= outputStream.size()) {
			count = inputStream.read(buffer);
			outputStream.write(buffer, 0, count);
		}
        //encryptedData = outputStream.toByteArray();
		decryptedData = cipher.doFinal(outputStream.toByteArray());
        
		outputStream.reset();
		outputStream.write(decryptedData);
	}

	public static void inflatePayload(ByteArrayInputStream inputStream, ByteArrayOutputStream outputStream) throws IOException, DataFormatException {
		
		//First byte is already removed by reading the magic bit 
		inputStream.skip( 4 + 1 + 4);
		
		InflaterInputStream inflater = new InflaterInputStream(inputStream, new Inflater());
		int count;
		byte[] buffer = new byte[1024];
		while ((count = inflater.read(buffer)) != -1) {
			outputStream.write(buffer,0,count);
		}

	}
	
	public static void finishProcess(ByteArrayInputStream inputStream, ByteArrayOutputStream outputStream) throws IOException {
		//First byte is already removed by reading the magic bit
		inputStream.skip( 4 + 1 );
		
		byte[] buffer = new byte[1024];
		int count;
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
		outputStream.reset();
        try (RandomAccessFile file_stream = new RandomAccessFile(file, "r")) {
            int offset = calcPyloadStart(file_stream);
            
            readglobalHeader(file_stream,offset,header_table);
            file_stream.seek(offset);
            int count;
            byte[] buffer = new byte[1024];
            while ( (count = file_stream.read(buffer)) !=-1) {
                outputStream.write(buffer, 0, count);
            }
        }
	}
}
