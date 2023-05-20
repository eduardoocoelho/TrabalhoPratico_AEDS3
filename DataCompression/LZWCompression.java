package DataCompression;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LZWCompression {
	public static int btsz;
	public static String big;

	public static void lzwCompression(String archieve, int version) {
		btsz = 0;
		big = "";
		previousCalculation(archieve);
		beginCompression(archieve, version);
		btsz = 0;
		big = "";
	}

	public static void beginCompression(String fileis, int version) {
		Map<String, Integer> dictionary = new HashMap<String, Integer>();
		int dictSize = 256;
		big = "";
		for (int i = 0; i < 256; i++) dictionary.put("" + (char) i, i);

		int mpsz = 256;
		String w = "";
		File file, fileAux;
		file = new File(fileis);
		fileAux = new File("arquivoLZWCompressao" + version + ".txt");

		try {
			FileInputStream file_input = new FileInputStream(file);
			DataInputStream data_in = new DataInputStream(file_input);
			FileOutputStream file_output = new FileOutputStream(fileAux);
			DataOutputStream data_out = new DataOutputStream(file_output);

			data_out.writeInt(btsz);
			Byte c;
			int ch;
			while (true) {
				try {
					c = data_in.readByte();
					ch = byteToInt(c);

					String wc = w + (char) ch;
					if (dictionary.containsKey(wc))
						w = wc;
					else {
						big += intToBinary(dictionary.get(w));
						while (big.length() >= 8) {
							data_out.write(stringToByte(big.substring(0, 8)));
							big = big.substring(8, big.length());
						}

						if (mpsz < 100000) {
							dictionary.put(wc, dictSize++);
							mpsz += wc.length();
						}
						w = "" + (char) ch;
					}
				} catch (EOFException eof) {
					break;
				}
			}

			if (!w.equals("")) {
				big += intToBinary(dictionary.get(w));
				while (big.length() >= 8) {
					data_out.write(stringToByte(big.substring(0, 8)));
					big = big.substring(8, big.length());
				}
				if (big.length() >= 1) {
					data_out.write(stringToByte(big));
				}
			}
			data_in.close();
			data_out.close();
			file_input.close();
			file_output.close();
		} catch (IOException e) {
			System.out.println("IO exception = " + e);
		}

		file = null;
		fileAux = null;

	}

	public static int byteToInt(Byte var) {
		int ret = var;
		if (ret < 0) {
			ret += 256;
		}
		return ret;

	}

	//Transforma os inteiros para binários
	public static String intToBinary(int inp) {
		String ret = "", r1 = "";
		if (inp == 0)
			ret = "0";
		int i;
		while (inp != 0) {
			if ((inp % 2) == 1)
				ret += "1";
			else
				ret += "0";
			inp /= 2;
		}
		for (i = ret.length() - 1; i >= 0; i--) {
			r1 += ret.charAt(i);
		}
		while (r1.length() != btsz) {
			r1 = "0" + r1;
		}
		return r1;
	}

	//Transforma a string para bytes
	public static Byte stringToByte(String in) {

		int i, n = in.length();
		byte ret = 0;
		for (i = 0; i < n; i++) {
			ret *= 2.;
			if (in.charAt(i) == '1')
				ret++;
		}
		for (; n < 8; n++)
			ret *= 2.;
		Byte r = ret;
		return r;
	}

	public static void previousCalculation(String archieve) {
		Map<String, Integer> dictionary = new HashMap<String, Integer>();
		int dictSize = 256;
		for (int i = 0; i < 256; i++) dictionary.put("" + (char) i, i);

		int mpsz = 256;
		String w = "";

		File file = null;
		file = new File(archieve);

		try {
			FileInputStream file_input = new FileInputStream(file);
			DataInputStream data_in = new DataInputStream(file_input);

			Byte c;
			int ch;
			while (true) {
				try {
					c = data_in.readByte();
					ch = byteToInt(c);
					String wc = w + (char) ch;
					if (dictionary.containsKey(wc))
						w = wc;
					else {
						if (mpsz < 100000) {
							dictionary.put(wc, dictSize++);
							mpsz += wc.length();
						}
						w = "" + (char) ch;
					}
				} catch (EOFException eof) {
					break;
				}
			}
			file_input.close();
			data_in.close();
		} catch (IOException e) {
			System.out.println("IO exception = " + e);
		}

		//Se o arquivo estiver vazio
		if (dictSize <= 1) {
			btsz = 1;
		} else {
			btsz = 0;
			long i = 1;
			while (i < dictSize) {
				i *= 2;
				btsz++;
			}
		}
		file = null;

	}

}
