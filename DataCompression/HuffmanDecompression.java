package DataCompression;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.PriorityQueue;

public class HuffmanDecompression {
	static PriorityQueue<Arvore> pq1 = new PriorityQueue<Arvore>();
	static int[] freq1 = new int[300];
	static String[] ss1 = new String[300]; 
	static String[] btost = new String[300]; 
	static String bigone; 
	static String temp; 
	static int exbits1;
	static int putit;
	static int cntu;

	static class Arvore implements Comparable<Arvore> {
		Arvore filhoEsq;
		Arvore filhoDir;
		public String deb;
		public int Bite;
		public int freq1nc;

		public int compareTo(Arvore arvore) {
			if (this.freq1nc < arvore.freq1nc)
				return -1;
			if (this.freq1nc > arvore.freq1nc)
				return 1;
			return 0;
		}
	}

	static Arvore raiz;

	public static void huffmanDecompression(String archieve, int version) {
		beginDecompression();
		readfreq1(archieve);
		createBinary();
		readBinary(archieve, "arquivoHuffman" + version + ".txt");
		beginDecompression();
	}

	public static void beginDecompression() {
		int i;
		if (raiz != null) move(raiz);
		for (i = 0; i < 300; i++) freq1[i] = 0;
		for (i = 0; i < 300; i++) ss1[i] = "";

		pq1.clear();
		bigone = "";
		temp = "";
		exbits1 = 0;
		putit = 0;
		cntu = 0;
	}

	public static void readBinary(String zip, String unz) {
		File f1 = null, f2 = null;
		int ok, bt;
		Byte b;
		int j, i;
		bigone = "";
		f1 = new File(zip);
		f2 = new File(unz);
		try {
			FileOutputStream file_output = new FileOutputStream(f2);
			DataOutputStream data_out = new DataOutputStream(file_output);
			FileInputStream file_input = new FileInputStream(f1);
			DataInputStream data_in = new DataInputStream(file_input);
			try {
				cntu = data_in.readInt();
				for (i = 0; i < cntu; i++) {
					b = data_in.readByte();
					j = data_in.readInt();
				}
				exbits1 = data_in.readInt();
			} catch (EOFException eof) {
			}

			while (true) {
				try {
					b = data_in.readByte();
					bt = formatNode(b);
					bigone += formatStringToEight(btost[bt]);

					while (true) {
						ok = 1;
						temp = "";
						for (i = 0; i < bigone.length() - exbits1; i++) {
							temp += bigone.charAt(i);
							if (got() == 1) {
								data_out.write(putit);
								ok = 0;
								String s = "";
								for (j = temp.length(); j < bigone.length(); j++) {
									s += bigone.charAt(j);
								}
								bigone = s;
								break;
							}
						}

						if (ok == 1)
							break;
					}
				} catch (EOFException eof) {
					break;
				}
			}
			file_output.close();
			data_out.close();
			file_input.close();
			data_in.close();
		} catch (IOException e) {
			System.out.println("IO Exception =: " + e);
		}

		f1 = null;
		f2 = null;
	}

	public static void move(Arvore now) {

		if (now.filhoEsq == null && now.filhoDir == null) {
			now = null;
			return;
		}
		if (now.filhoEsq != null) move(now.filhoEsq);
		if (now.filhoDir != null) move(now.filhoDir);
	}

	public static void nodes(Arvore now, String st) {
		now.deb = st;
		if ((now.filhoEsq == null) && (now.filhoDir == null)) {
			ss1[now.Bite] = st;
			return;
		}
		if (now.filhoEsq != null) nodes(now.filhoEsq, st + "0");
		if (now.filhoDir != null) nodes(now.filhoDir, st + "1");
	}

	public static void MakeNode1() {
		int i;
		cntu = 0;
		for (i = 0; i < 300; i++) {
			if (freq1[i] != 0) {

				Arvore temp = new Arvore();
				temp.Bite = i;
				temp.freq1nc = freq1[i];
				temp.filhoEsq = null;
				temp.filhoDir = null;
				pq1.add(temp);
				cntu++;
			}

		}
		Arvore temp1, temp2;

		if (cntu == 0) {
			return;
		} else if (cntu == 1) {
			for (i = 0; i < 300; i++)
				if (freq1[i] != 0) {
					ss1[i] = "0";
					break;
				}
			return;
		}

		//Arquivo não pode estar vazio
		while (pq1.size() != 1) {
			Arvore temp = new Arvore();
			temp1 = pq1.poll();
			temp2 = pq1.poll();
			temp.filhoEsq = temp1;
			temp.filhoDir = temp2;
			temp.freq1nc = temp1.freq1nc + temp2.freq1nc;
			pq1.add(temp);
		}
		raiz = pq1.poll();
	}

	public static void readfreq1(String archieve) {

		File file = new File(archieve);
		int fey, i;
		Byte bt;
		try {
			FileInputStream file_input = new FileInputStream(file);
			DataInputStream data_in = new DataInputStream(file_input);
			cntu = data_in.readInt();

			for (i = 0; i < cntu; i++) {
				bt = data_in.readByte();
				fey = data_in.readInt();
				freq1[formatNode(bt)] = fey;
			}
			data_in.close();
			file_input.close();
		} catch (IOException e) {
			System.out.println("IO exception = " + e);
		}

		MakeNode1();
		if (cntu > 1)
			nodes(raiz, "");

		for (i = 0; i < 256; i++) {
			if (ss1[i] == null)
				ss1[i] = "";
		}
		file = null;
	}

	public static int formatNode(Byte bt) {
		int ret = bt;
		if (ret < 0) {
			ret = ~bt;
			ret = ret + 1;
			ret = ret ^ 255;
			ret += 1;
		}
		return ret;
	}

	public static void createBinary() {
		int i, j;
		String t;
		for (i = 0; i < 256; i++) {
			btost[i] = "";
			j = i;
			while (j != 0) {
				if (j % 2 == 1)
					btost[i] += "1";
				else
					btost[i] += "0";
				j /= 2;
			}
			t = "";
			for (j = btost[i].length() - 1; j >= 0; j--) {
				t += btost[i].charAt(j);
			}
			btost[i] = t;
		}
		btost[0] = "0";
	}

	public static int got() {
		int i;

		for (i = 0; i < 256; i++) {
			if (ss1[i].compareTo(temp) == 0) {
				putit = i;
				return 1;
			}
		}
		return 0;

	}

	public static String formatStringToEight(String b) {
		String ret = "";
		int i;
		int len = b.length();
		for (i = 0; i < (8 - len); i++)
			ret += "0";
		ret += b;
		return ret;
	}

}
