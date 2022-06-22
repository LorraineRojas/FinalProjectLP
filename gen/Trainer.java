import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

public class Trainer {

    static String name;
    static ArrayList<Double> stats = new ArrayList<>();
    static HashMap<String, ArrayList<Double>> users = new HashMap<>();
    static MyVisitor mv = new MyVisitor<>();
    private JTextField usuario;
    private JButton analizarButton;
    private JPanel content;
    private JLabel numLin;
    private JLabel spa;
    private JLabel eng;
    private JLabel abr;
    private JLabel numIf;
    private JLabel numWhile;
    private JLabel numFor;
    private JLabel numLam;
    private JLabel porMay;
    private JLabel porMin;
    private JLabel porBar;
    private JLabel porUpCamel;
    private JLabel similitud;
    private JLabel similUser;
    private JLabel porLowCamel;

    public Trainer() {
        analizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                name = usuario.getText();
                Trainer t = new Trainer();

                // Analisis Estilografico
                Python3Lexer lexer = null;
                try {
                    lexer = new Python3Lexer(CharStreams.fromFileName("input/entrada.txt"));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                CommonTokenStream tokens = new CommonTokenStream((TokenSource) lexer);
                Python3Parser parser = new Python3Parser(tokens);
                ParseTree tree = parser.file_input();
                MyVisitor<Object> loader = new MyVisitor<Object>();
                loader.visit(tree);

                // Resultados del Analisis Estilografico
                numLin.setText(String.valueOf(mv.numlines));
                spa.setText(String.valueOf(mv.spanish_words_cont));
                eng.setText(String.valueOf(mv.english_words_cont));
                abr.setText(String.valueOf(mv.abreviations_words_cont));
                numIf.setText(String.valueOf(mv.if_cont));
                numWhile.setText(String.valueOf(mv.while_cont));
                numFor.setText(String.valueOf(mv.for_cont));
                numLam.setText(String.valueOf(mv.lambda_cont));

                int total = mv.Ucamel_case_cont + mv.Lcamel_case_cont + mv.all_caps_count + mv.small_caps_count + mv.snake_case_cont;
                float perSnake = (float) mv.snake_case_cont / total;
                float perUcamel = (float) mv.Ucamel_case_cont / total;
                float perLcamel = (float) mv.Lcamel_case_cont / total;
                float perAllcaps = (float) mv.all_caps_count / total;
                float perSmallcaps = (float) mv.small_caps_count / total;

                porMay.setText(String.format("%.2f", perAllcaps * 100) + "%");
                porMin.setText(String.format("%.2f", perSmallcaps * 100) + "%");
                porBar.setText(String.format("%.2f", perSnake * 100) + "%");
                porUpCamel.setText(String.format("%.2f", perUcamel * 100) + "%");
                porLowCamel.setText(String.format("%.2f", perLcamel * 100) + "%");

                t.OutPut();
            }
        });
    }

    // leer el archivo de los usuarios
    public void Read(HashMap<String, ArrayList<Double>> db) {

        File file = new File("db/users_db");
        FileReader fileR;
        BufferedReader file2 = null;

        try {
            fileR = new FileReader(file);
            file2 = new BufferedReader(fileR);

        } catch (FileNotFoundException e) {
            System.out.println("No se encontro el archivo " + file.getName());
        }

        try {
            String linea;
            while ((linea = file2.readLine()) != null) {

                if (linea.contains(":")) {
                    String[] user = linea.split(":");
                    String[] params = user[1].split("&");
                    ArrayList<Double> param = new ArrayList<>();
                    for (int i = 0; i < params.length; i++)
                        param.add(Double.parseDouble(params[i].trim()));
                    users.put(user[0], param);
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Guarda en la db los datos de los usuarios
    public void WriteOut() {
        FileWriter file = null;
        try {
            file = new FileWriter("db/users_db", true);
            PrintWriter filePw = new PrintWriter(file);

            filePw.write(name + " : ");
            for (int i = 0; i < stats.size(); i++) {
                if (i == stats.size() - 1) {
                    filePw.write(stats.get(i) + "\n");
                } else {
                    filePw.write(stats.get(i) + "&");
                }

            }
            file.close();
        } catch (IOException e) {
            System.out.println("No se encontro el archivo " + file.getEncoding());
        }
    }


    // Calcula las Estadisticas de cada usuario

    public void Calstats() {

        // Estadisticas
        int total = mv.Ucamel_case_cont + mv.Lcamel_case_cont + mv.all_caps_count + mv.small_caps_count + mv.snake_case_cont;

        float perSnake = (float) mv.snake_case_cont / total;
        float perUcamel = (float) mv.Ucamel_case_cont / total;
        float perLcamel = (float) mv.Lcamel_case_cont / total;
        float perAllcaps = (float) mv.all_caps_count / total;
        float perSmallcaps = (float) mv.small_caps_count / total;

        double perSnakeF = (double) Math.round(perSnake * 100d) / 100d;
        stats.add(perSnakeF);
        double perUcamelF = (double) Math.round(perUcamel * 100d) / 100d;
        stats.add(perUcamelF);
        double perLcamelF = (double) Math.round(perLcamel * 100d) / 100d;
        stats.add(perLcamelF);
        double perAllcapsF = (double) Math.round(perAllcaps * 100d) / 100d;
        stats.add(perAllcapsF);
        double perSmallcapsF = (double) Math.round(perSmallcaps * 100d) / 100d;
        stats.add(perSmallcapsF);


        stats.add((double) mv.if_cont);
        stats.add((double) mv.while_cont);
        stats.add((double) mv.for_cont);
        stats.add((double) mv.spanish_words_cont);
        stats.add((double) mv.abreviations_words_cont);
        stats.add((double) mv.other_words_cont);
        stats.add((double) mv.english_words_cont);

        WriteOut();
    }

    // Busca la similitud del codigo de la persona con los demas usuarios
    // usuarios almacenados en la base de datos
    public void Simil() {

        System.out.println();
        //System.out.println("El codigo ingresado por el usuario: "+name);

        double tolerance;
        if (mv.numlines <= 15)
            tolerance = 0.3;
        else
            tolerance = 0.05;

        ArrayList<String> keys = new ArrayList<>();
        Iterator it = users.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            keys.add(String.valueOf(e.getKey()));
        }
        int maxl = 0, max = 0;
        String targetUser = "";
        ArrayList<Double> target = new ArrayList<>();

        for (int i = 0; i < users.size(); i++) {
            if (keys.get(i) != name) {

                //local = users.get(name);
                target = users.get(keys.get(i));
                for (int j = 0; j < users.get(keys.get(i)).size(); j++) {
                    if (stats.get(j) >= target.get(j) * tolerance && stats.get(j) <= target.get(j) + target.get(j) * tolerance)
                        maxl++;

                }

                // por el momento solo selecciona un usuario
                // se puede mostar un conjunto de usuarios si se desea
                if (maxl > max) {
                    max = maxl;
                    maxl = 0;
                    targetUser = keys.get(i);
                } else
                    maxl = 0;

            }
        }
        double ans = (double) Math.round(max / 8 * 100d) / 100d;
        if (ans > 0) {
            System.out.println("La entrada del usuario: " + name);
            similitud.setText(ans * 100 + " %");
            System.out.println("Tiene una similitud del: " + ans * 100 + " %");
            System.out.println("Con la entrada del usuario: " + targetUser);
        } else
            System.out.println("No existe similitud de" + name + "con otro usuario");

    }

    public void OutPut() {
        Read(users);
        if (users.size() >= 1) {
            Calstats();
            Simil();
        } else
            Calstats();

    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Trainer");
        frame.setContentPane(new Trainer().content);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}