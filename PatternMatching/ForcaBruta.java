package PatternMatching;

import java.io.IOException;
import java.io.RandomAccessFile;

import Entities.Movies;

public class ForcaBruta {

    private Integer quant = 0;
    private Integer cont = 0;
    
    public ForcaBruta(){}

    //Realizar a comparação de cada caracter do padrao e do texto
    public void search(char txt[], char padrao[]){
        int i = 0; //Contador que representa a posição no texto
        int j = 0; //Contador que representa a posição no padrão

        while(i < txt.length){ 
            cont++;
            if(txt[i] == padrao[j]){ //Se os caracteres forem iguais, incrementa ambos contadores
                cont++;
                i++;
                j++;
            }
            else{ //Se forem diferentes, somente o "i" incrementado
                i++;
                j = 0;
            }
            if(j == padrao.length){ //Se achar o padrão, zera o contador "j" para proseeguir a busca até o fim do texto
                cont++;
                quant++;
                i++;
                j = 0;
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
        System.out.println("FORÇA BRUTA:");
        System.out.println("O padrão foi encontrado " + quant + " vezes no arquivo!");
        System.out.println("Foram realizadas " + cont + " comparações!");
    }
}
