package DataCompression;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.PriorityQueue;

public class HuffmanCompression {
    
    static PriorityQueue<Arvore> pq = new PriorityQueue<Arvore>();
	static int[] freq = new int[300];
	static String[] ss = new String[300];
	static int exbits;
	static byte bt;
	static int cnt; //contador de caracteres diferentes

	static class Arvore implements Comparable<Arvore> {
		Arvore filhoEsq;
		Arvore filhoDir;
		public String deb;
		public int Bite;
		public int Freqnc;

		public int compareTo(Arvore arvore) {
			if (this.Freqnc < arvore.Freqnc)
				return -1;
			if (this.Freqnc > arvore.Freqnc)
				return 1;
			return 0;
		}
	}

	static Arvore raiz;

	public static void huffmanCompression(String archieve, int version) {
		beginCompression();
		CalFreq(archieve);
		formatNode();
		if (cnt > 1) nodes(raiz, "");
		fakeCompression(archieve);
		realCompression("arqAux.txt", "arquivoHuffmanCompressao" + version + ".txt");																							// file
		beginCompression();
	}

	//Inicio do processo de compressão
	public static void beginCompression() {
		int i;
		cnt = 0;
		if (raiz != null) move(raiz);
		for (i = 0; i < 300; i++) freq[i] = 0;
		for (i = 0; i < 300; i++) ss[i] = "";
		pq.clear();
	}
	
	//Calculo da frequencia de cada digito do arquivo passado por parametro
	public static void CalFreq(String fname) {
		File file = null;
		Byte bt;

		file = new File(fname);
		try {
			FileInputStream file_input = new FileInputStream(file);
			DataInputStream data_in = new DataInputStream(file_input);
			while (true) {
				try {

					bt = data_in.readByte();
					freq[binaryComversion(bt)]++;
				} catch (EOFException eof) {
					break;
				}
			}
			file_input.close();
			data_in.close();
		} catch (IOException e) {
			System.out.println("IO Exception =: " + e);
		}
		file = null;
	}

	//Conversão binária dos bytes
	public static int binaryComversion(Byte var) {
		int ret = var;
		if (ret < 0) {
			ret = ~var;
			ret = ret + 1;
			ret = ret ^ 255;
			ret += 1;
		}
		return ret;
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
			ss[now.Bite] = st;
			return;
		}
		if (now.filhoEsq != null) nodes(now.filhoEsq, st + "0");
		if (now.filhoDir != null) nodes(now.filhoDir, st + "1");
	}

	//Formando os nós da árvore
	public static void formatNode() {
		int i;
		pq.clear();

		for (i = 0; i < 300; i++) {
			if (freq[i] != 0) {
				Arvore temp = new Arvore();
				temp.Bite = i;
				temp.Freqnc = freq[i];
				temp.filhoEsq = null;
				temp.filhoDir = null;
				pq.add(temp);
				cnt++;
			}

		}
		Arvore temp1, temp2;

		if (cnt == 0) {
			return;
		} else if (cnt == 1) {
			for (i = 0; i < 300; i++)
				if (freq[i] != 0) {
					ss[i] = "0";
					break;
				}
			return;
		}

		//O arquivo não pode estar vazio
		while (pq.size() != 1) {
			Arvore temp = new Arvore();
			temp1 = pq.poll();
			temp2 = pq.poll();
			temp.filhoEsq = temp1;
			temp.filhoDir = temp2;
			temp.Freqnc = temp1.Freqnc + temp2.Freqnc;
			pq.add(temp);
		}
		raiz = pq.poll();
	}

	//Criar um arquivo auxiliar para colocar os simbolos binarios
	public static void fakeCompression(String archieve) {

		File file, fileaux;
		//int i;

		file = new File(archieve);
		fileaux = new File("arqAux.txt");
		try {
			FileInputStream file_input = new FileInputStream(file);
			DataInputStream data_in = new DataInputStream(file_input);
			PrintStream ps = new PrintStream(fileaux);

			while (true) {
				try {
					bt = data_in.readByte();
					ps.print(ss[binaryComversion(bt)]);
				} catch (EOFException eof) {
					break;
				}
			}

			file_input.close();
			data_in.close();
			ps.close();

		} catch (IOException e) {
			System.out.println("IO Exception =: " + e);
		}
		file = null;
		fileaux = null;

	}

	//Criar o arquivo comprimido
	public static void realCompression(String archieve, String archieveAux) {
		File file, fileAux;
		int i; //j = 10;
		Byte btt;

		file = new File(archieve);
		fileAux = new File(archieveAux);

		try {
			FileInputStream file_input = new FileInputStream(file);
			DataInputStream data_in = new DataInputStream(file_input);
			FileOutputStream file_output = new FileOutputStream(fileAux);
			DataOutputStream data_out = new DataOutputStream(file_output);

			data_out.writeInt(cnt);
			for (i = 0; i < 256; i++) {
				if (freq[i] != 0) {
					btt = (byte) i;
					data_out.write(btt);
					data_out.writeInt(freq[i]);
				}
			}
			long texbits;
			texbits = file.length() % 8;
			texbits = (8 - texbits) % 8;
			exbits = (int) texbits;
			data_out.writeInt(exbits);
			while (true) {
				try {
					bt = 0;
					byte ch;
					for (exbits = 0; exbits < 8; exbits++) {
						ch = data_in.readByte();
						bt *= 2;
						if (ch == '1')
							bt++;
					}
					data_out.write(bt);

				} catch (EOFException eof) {
					int x;
					if (exbits != 0) {
						for (x = exbits; x < 8; x++) {
							bt *= 2;
						}
						data_out.write(bt);
					}

					exbits = (int) texbits;
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
		file.delete();
		file = null;
		fileAux = null;
	}

}
