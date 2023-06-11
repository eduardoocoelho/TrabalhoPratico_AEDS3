package PatternMatching;

import java.io.IOException;
import java.io.RandomAccessFile;

import Entities.Movies;

public class BoyerMoore {

    static int NUM_CHARS = 256;
    private Integer quant = 0;
    private Integer cont = 0;
	
    public void search(char txt[], char padrao[]){
        int val=0;
        int s = 0, j;
        int m = padrao.length; //tamanho do padrão 
        int n = txt.length; //tamanho do texto
        
        //vetores de deslocamento
        int badchar[] = new int[NUM_CHARS];
        int []bpos = new int[m + 1];
        int []aux = new int[m + 1];
        
        //montagem do vetor Bad Character - que possuirá as posições de cada elemento do padrão
        badCharHeuristic(padrao, m, badchar);
        s = 0; 

        //inicializa todas as posições do array aux
        for(int i = 0; i < m + 1; i++){
            aux[i] = 0;
        }
        
        //montagem do vetor Good Suffix
        /*
        1- O sufixo formado aparece anteriormente precedido por algum caractere diferente?
        2- O sufixo formado é prefixo?
        3- Reduz o sufixo e questiona se o novo sufixo formado é prefixo.
         */
        caso1(aux, bpos, padrao, m); //Primeiro Caso
        caso2(aux, bpos, padrao, m); //Segundo e Terceiro Caso

        //percorre todo o texto ate a posição n-m (tamanho do texto - tamanho do padrão)
        while(s <= (n - m)){
            j = m-1;

            //enquanto as posições do padrão e do texto coincidirem, decrementa o valor de j
            //j representa a posição dentro do caractere 
            //assim, é possível localizar onde ocorre o caractere ruim (diferente)
            while(j >= 0 && padrao[j] == txt[s+j]){
                j--;
            }

            //se o padrão for encontrado
            if (j < 0){
                //System.out.println("padrão ocorreu na posição = " + s);
                s += (s+m < n)? m-badchar[txt[s+m]] : 1;
                quant++;
            }else{  //se o padrão não for encontrado, isso é, achar um caractere ruim
                //System.out.println("posi: "+(int)(txt[s+j]));
                int pos = txt[s+j];
                int val1 = 1;
                /*se o caractere ruim for um caractere especial, não será 
                encontrado no vetor de valores pois será maior que 256*/
                if(pos<=256){
                    //Deslocamento por carater ruim
                    //j(posição onde ocorreu a falha) - valor do carctere ruim do texto no vetor 
                    val1 = max(1, j - badchar[txt[s+j]]); //evita um deslocamento negativo
                }
                
                int val2 = aux[j + 1]; //Deslocamento por sufixo bom

                //testa qual deslocamento vale mais a pena (qual é maior)
                if(val1>val2){
                    s += val1;
                }else{
                    s += val2;
                } 
            }   
        }
    }

	//Compara dois inteiros e retorna o maior
	static int max (int a, int b) {  //os parametros sao as duas opções possiveis
        if(a>b){
            return a;
        }else{
            return b;
        }
    }
	
	static void badCharHeuristic(char []str, int tam, int badchar[]){
        //inicializa todas as 256 posições do vetor
        for (int i = 0; i < NUM_CHARS; i++)
            badchar[i] = -1;

        //preenche o vetor nas posições das letras que constituem o padrão
        for (int i = 0; i < tam; i++){
            badchar[(int) str[i]] = i;
        }   
	}

    public void caso1(int []aux, int []bpos, char []padrao, int m){
        // m é o tamanho do padrão 
        int i = m, j = m + 1; //inicializa as variaveis auxiliares com o tamanho do padrão
        bpos[i] = j;

        //percorre o padrão
        while(i > 0){
            //Enquanto as duas posições i-1 e j-1 forem diferentes, continua no loop
            while(j <= m && padrao[i - 1] != padrao[j - 1]){
                //devido a incompatibilidade, altera-se o padrão de i para j 
                if (aux[j] == 0){
                    cont++;
                    aux[j] = j - i;
                }
                //Atualiza a posição
                j = bpos[j];
            }
            //ao encontrar a posição (parar o loop), armazena a posição inicial
            i--; j--;
            bpos[i] = j;
        }
    }

    public void caso2(int []aux, int []bpos, char []padrao, int m){
        int i, j;
        j = bpos[0];

        //percorre o padrão
        for(i = 0; i <= m; i++){
            //atualiza todas as posições do array auxiliar 
            if(aux[i] == 0){
                cont++;
                aux[i] = j;
            }  
            //o sufixo fica menor e, assim, atualiza para uma posição maior
            if (i == j){
                cont++;
                j = bpos[j];
            }  
        }
    }

    public void readArq(String arquivo, String padrao) throws IOException{ //leitura do arquivo inteiro - todos os registros validos
        RandomAccessFile arq = new RandomAccessFile(arquivo, "rw");
        
        arq.seek(0); //posiciona o ponteiro no inicio do arquivo = cabecalho
        int cabecalho = arq.readInt(); //descobre qual e o ultimo registro do arquivo 
        int pos = 4; //posicao do primeiro registro
        int tam = 0;
        Movies temp = new Movies();
        boolean acabou = false;

        do{
            arq.seek(pos); //posiciona o ponteiro no inicio do proximo registro
            boolean lapide = arq.readBoolean(); //leitura da lapide
            tam = arq.readInt(); //leitura do tamanho do registro
            if(lapide==true){ //verifica se o registro e valido
                byte[] arrayByte = new byte[tam]; 
                arq.read(arrayByte); //leitura do array de bytes
                temp.fromByteArray(arrayByte); //transforma o array de bytes em um objeto Movie
                String texto = temp.toString();
                //System.out.println(texto);
                search(texto.toCharArray(), padrao.toCharArray()); //chamada do metodo para procurar o padrao no texto
                if(temp.getId()==cabecalho){ //testa se o arquivo chegou no fim
                    acabou = true;
                }
            } 
            pos += tam + 4 + 1; //adiciona a quantidade de bytes para chegar no inicio do proximo registro
        }while(!acabou);    
        arq.close();
    }      

    public void results(){
        System.out.println("BOYER MOORE:");
        System.out.println("O padrão foi encontrado " + quant + " vezes no arquivo!");
        System.out.println("Foram realizadas " + cont + " comparações!");
    }
}