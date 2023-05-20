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

public class LZWDecompression {
	public static int bitsz1;
	public static String bttost[] = new String[256];
	public static String big1;

	public static void lzwDecompression(String archieve, int version) {
		big1 = "";
		bitsz1 = 0;
		binToString();
		beginDecompression(archieve, version);
		big1 = "";
		bitsz1 = 0;
	}

	public static void beginDecompression(String archieve, int version) {
		int k;
		int dictSize = 256;
		int mpsz = 256;
		String ts;
		Map<Integer, String> dictionary = new HashMap<Integer, String>();
		
		for (int i = 0; i < 256; i++) dictionary.put(i, "" + (char) i);

		File file = null, fileAux = null;
		file = new File(archieve);
		fileAux = new File("arquivoLZW" + version + ".txt");
		try {
			FileInputStream file_input = new FileInputStream(file);
			DataInputStream data_in = new DataInputStream(file_input);
			FileOutputStream file_output = new FileOutputStream(fileAux);
			DataOutputStream data_out = new DataOutputStream(file_output);

			Byte c;
			bitsz1 = data_in.readInt();

			while (true) {
				try {
					c = data_in.readByte();
					big1 += bttost[byteToInt(c)];
					if (big1.length() >= bitsz1)
						break;
				} catch (EOFException eof) {
					break;
				}
			}

			if (big1.length() >= bitsz1) {
				k = stringToInt(big1.substring(0, bitsz1));
				big1 = big1.substring(bitsz1, big1.length());
			} else {
				data_in.close();
				data_out.close();
				return;
			}

			String w = "" + (char) k;

			data_out.writeBytes(w);

			while (true) {
				try {
					while (big1.length() < bitsz1) {
						c = data_in.readByte();
						big1 += bttost[byteToInt(c)];
					}
					k = stringToInt(big1.substring(0, bitsz1));
					big1 = big1.substring(bitsz1, big1.length());

					String entry = "";
					if (dictionary.containsKey(k)) {

						entry = dictionary.get(k);
					} else if (k == dictSize) {
						entry = w + w.charAt(0);

					}
					data_out.writeBytes(entry);

					if (mpsz < 100000) {
						ts = w + entry.charAt(0);
						dictionary.put(dictSize++, ts);
						mpsz += ts.length();
					}
					w = entry;
				} catch (EOFException eof) {
					break;
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

	//Conversão de binario para string
	public static void binToString() {
		int i, j;
		String r1;
		bttost[0] = "0";
		for (i = 0; i < 256; i++) {
			r1 = "";
			j = i;
			if (i != 0)
				bttost[i] = "";
			while (j != 0) {
				if ((j % 2) == 1)
					bttost[i] += "1";
				else
					bttost[i] += "0";
				j /= 2;
			}
			for (j = bttost[i].length() - 1; j >= 0; j--) {
				r1 += bttost[i].charAt(j);
			}
			while (r1.length() < 8) {
				r1 = "0" + r1;
			}
			bttost[i] = r1;
		}
	}

	//Byte para int
	public static int byteToInt(Byte var) {
		int ret = var;
		if (ret < 0)
			ret += 256;
		return ret;

	}

	//String para int
	public static int stringToInt(String str) {
		int ret = 0, i;
		for (i = 0; i < str.length(); i++) {
			ret *= 2;
			if (str.charAt(i) == '1')
				ret++;
		}
		return ret;
	}

}
