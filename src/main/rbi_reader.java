/** *****************************************************************************
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
 ***************************************************************************** */
package main;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class rbi_reader {

    private final rbi_info info;
    private InputStream plainStream;

    private RandomAccessFile input_file_stream, output_file_stream;
    private int payloadOffset;

    public rbi_reader(rbi_info info) {
        this.info = info;
    }

    public InputStream getPlainStream() {
        return plainStream;
    }

    public void openFile() throws IOException {
        input_file_stream = new RandomAccessFile(info.getFile(), "r");
        payloadOffset = calcPyloadStart(input_file_stream);

        readGlobalHeader(input_file_stream, payloadOffset, info.getHeaderTable());
    }

    public int calcPyloadStart(RandomAccessFile file_stream) throws IOException {
        byte[] pyloadstart = new byte[4];
        //first 0x2f byte are static and header size is in 0x28 and long 4 byte
        file_stream.seek(0x28);
        file_stream.read(pyloadstart, 0, 4);
        return new BigInteger(pyloadstart).intValue();
    }

    public void readGlobalHeader(RandomAccessFile file_stream, int offset, Map<String, String> header_table) throws IOException {
        byte[] globalHeader = new byte[offset];

        file_stream.seek(0);
        file_stream.read(globalHeader, 0, offset);

        header_parser.parse(globalHeader, header_table);
    }

    public void processPayload(byte[] osck, byte[] osik, gui_construct Scene) throws IOException, DataFormatException, InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException {

        byte[] payloadType = new byte[1];

        input_file_stream.seek(payloadOffset);
        InputStream inputStream = new FileInputStream(input_file_stream.getFD());

        while (true) {
            inputStream.read(payloadType, 0, 1);

            if (payloadType[0] == (byte) 0xB0) {
                Scene.log.appendText("Cleaning payload from magic bit...\n");
                inputStream = plainPayload(inputStream);
                break;
            } else if (payloadType[0] == (byte) 0xB4) {
                Scene.log.appendText("Decompressing payload...\n");
                inputStream = inflatePayload(inputStream);
            } else if (payloadType[0] == (byte) 0xB8) {
                //if (osik.length != 2) {
                //    Scene.log.appendText("Checking payload signature...\n");
                // TODO sigCheck()
                //} else {
                Scene.log.appendText("Skipping payload signature check...\n");
                inputStream = unsignPayload(inputStream);
                //}
            } else if (payloadType[0] == (byte) 0xB7) {
                Scene.log.appendText("Decrypting rbi firmware...\n");
                inputStream = decryptPayload(inputStream, osck);
            } else {
                throw new InvalidKeyException("Can't recognize magic bit! Assume wrong decryption!");
            }
        }

        this.plainStream = inputStream;
    }

    public InputStream unsignPayload(InputStream inputStream) throws IOException {
        //First byte is already removed by reading the magic bit
        inputStream.skip(4 + 1 + 4 + 32);

        return inputStream;
    }

    public InputStream decryptPayload(InputStream inputStream, byte[] key1)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {

        //First byte is already removed by reading the magic bit 
        inputStream.skip(4 + 1);

        byte[] payloadSize = new byte[4];
        inputStream.read(payloadSize, 0, 4);

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

        return new CipherInputStream(inputStream, cipher);
    }

    public InputStream inflatePayload(InputStream inputStream) throws IOException, DataFormatException {
        //First byte is already removed by reading the magic bit 
        inputStream.skip(4 + 1 + 4);

        return new InflaterInputStream(inputStream, new Inflater());
    }

    public InputStream plainPayload(InputStream inputStream) throws IOException {
        //First byte is already removed by reading the magic bit
        inputStream.skip(4 + 1);

        return inputStream;
    }

    public void saveFile(File file, gui_construct Scene) throws IOException {
        Files.copy(this.plainStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

        Scene.log.appendText("Plain bank partition file saved here: " + file.getAbsolutePath() + "\n");
        Scene.log.appendText("You can now use Binwalk to unpack kernel and root filesystem\n");

        this.output_file_stream = new RandomAccessFile(file, "r");
        detectInfoBlockOffset(Scene);
    }

    public void detectInfoBlockOffset(gui_construct Scene) throws IOException {

        Map<String, String> infoblock_table = Scene.getRbiInfo().getInfoBlockTable();

        //First 16 byte are unknown data... 
        //We know that the first one is always FF FF FF FF
        // The last 4 block is the info block offset
        byte[] block = new byte[4];
        int offset, len;

        // Skip first 3 blocks (11 byte)
        output_file_stream.seek(12);
        // Read info block offset (last block)
        output_file_stream.read(block, 0, 4);

        offset = new BigInteger(block).intValue();
        if (offset > 0) {
            Scene.log.appendText("Detected InfoBlock at offset: " + offset + "\n");
            output_file_stream.seek(offset);
            output_file_stream.read(block, 0, 4);
            len = new BigInteger(block).intValue();
            Scene.log.appendText("Detected InfoBlock of size: " + len + "\n");

            consumeInfoBlock(output_file_stream, len - 4, infoblock_table);
            detectSignature(output_file_stream, infoblock_table);
            Scene.updateInfoBlockSubPanel();
        }

        output_file_stream.close();
    }

    private void consumeInfoBlock(RandomAccessFile stream, int len, Map<String, String> infoblock_table) throws IOException {
        int read, block_len;
        byte[] block = new byte[4];
        int data_convert;
        String tag, res;

        try (ByteArrayOutputStream data = new ByteArrayOutputStream()) {
            while (len > 0) {
                read = stream.read(block, 0, 4);
                block_len = new BigInteger(block).intValue();
                len -= block_len;
                block_len -= read;

                // TAG NAME
                read = stream.read(block, 0, 4);
                block_len -= read;
                tag = new String(block).trim();

                // DATA
                while (block_len > 0) {
                    read = stream.read(block, 0, 4);
                    block_len -= read;
                    data.write(block);
                }

                res = data.toString();
                if (data.size() == 4) {
                    data_convert = new BigInteger(block).intValue();
                    if (data_convert == 1 || data_convert == 0) {
                        res = data_convert == 1 ? "true" : "false";
                    }
                }

                infoblock_table.put(tag, res);

                data.reset();
            }
        }
    }

    private void detectSignature(RandomAccessFile stream, Map<String, String> infoblock_table) throws IOException {
        byte[] block = new byte[4];
        byte[] empty_block = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};

        int read, block_len;
        String tag, res;
        try (ByteArrayOutputStream data = new ByteArrayOutputStream()) {
            read = stream.read(block, 0, 4);
            if (Arrays.equals(block, empty_block)) {
                return;
            }

            block_len = new BigInteger(block).intValue();
            block_len -= read;

            // TAG NAME
            read = stream.read(block, 0, 4);
            block_len -= read;
            tag = new String(block).trim();

            // DATA
            while (block_len > 0) {
                read = stream.read(block, 0, 4);
                block_len -= read;
                data.write(block);
            }

            res = string_util.bytesToHexString(data.toByteArray());

            infoblock_table.put(tag, res);
        }
    }
}
