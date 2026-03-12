import java.util.Random;

public class Ejecutor {

    public static void main(String[] args) {

        Random random = new Random();
//primer paso

        int [] energiaContenedores = new int[12];
        for (int i = 0; i < energiaContenedores.length; i++){
            energiaContenedores[i] = random.nextInt(101) + 50;
            
        }

        int[] multiplos10 = new int[12];
        int multiplo = 0; 

        for (int i = 0; i < energiaContenedores.length; i++){
            if (energiaContenedores[i] %10 == 0){
                multiplos10[multiplo] = energiaContenedores[i];
                multiplo++;
            }
        }

    //segundo paso

    int[][] mapaCarga [3][3];
    int num;

    for (int fila = 0; fila < 3; fila++){
        for ( int columna = 0; columna < 3; columna++){ 


            if (multiplos10 != null && num < multiplos10.length){
                mapaCarga[fila][columna] = multiplos10[num];
                num++;
            }else{
                mapaCarga[fila][columna] = -1;
                
            }
        }
    }
    
    //tercer paso 
     Suministro[] manifiesto = new Suministro[9];
        int i = 0;

        for (int fila = 0; fila < 3; fila++) {
            for (int col = 0; col < 3; col++) {

                int energia = matriz3x3[fila][col];

                if (energia != -1) {

                    String prioridad;

                    if (energia > 100) {
                        prioridad = "ALTA";
                    } else {
                        prioridad = "ESTANDAR";

                    }

                    manifiesto[i] = new Suministro("ID" + (i + 1), energia, prioridad);
                    i++;
                }
            }
        }
    }



        



    }

    
