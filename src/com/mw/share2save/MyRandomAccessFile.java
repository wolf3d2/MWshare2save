package com.mw.share2save;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/** позволяет писать в любое место файла */
public class MyRandomAccessFile extends RandomAccessFile {
	 
	private byte[] buffer = new byte[8192];
 
	 /** конструктор
	  * @param mode - одно из значений работы с файлом - "r", "w", "rw"*/
	public MyRandomAccessFile(String filename, String mode) throws FileNotFoundException {
		super(filename, mode);
	}
 /** конструктор
  * @param mode - одно из значений работы с файлом - "r", "w", "rw"*/
	public MyRandomAccessFile(File file, String mode) throws FileNotFoundException {
		super(file, mode);
	}
 /** дописываем в файл с указанной позиции pos
  * @param pos - для строки позиция должна быть умножена на 2*/
	public void insert(byte[] buf, long pos) throws IOException {
		if (buf == null) {
			throw new NullPointerException();
		}
		if (pos < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (buf.length == 0) {
			return;
		}
 
		long oldLength = length();
 
		if (pos < oldLength) {
			setLength(oldLength + buf.length);
			long startChunk = oldLength - buffer.length;
			while (true) {
				if (startChunk >= pos) {
					seek(startChunk);
					readFully(buffer);
					seek(startChunk + buf.length);
					write(buffer);
				} else {
					seek(pos);
					int chunkLength = (int) (buffer.length + startChunk - pos);
					readFully(buffer, 0, chunkLength);
					seek(pos + buf.length);
					write(buffer, 0, chunkLength);
					break;
				}
				startChunk -= buffer.length;
			}
		} else {
			setLength(pos + buf.length);
		}
 
		seek(pos);
		write(buf);
	}
}