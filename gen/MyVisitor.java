import org.antlr.v4.runtime.misc.ObjectEqualityComparator;
import org.stringtemplate.v4.ST;

import java.io.*;
import java.util.ArrayList;
import java.sql.SQLOutput;
import java.util.HashMap;

// Analisis Estilografico realizado para el codigo de Python 3
// realizado por Lorraine Rojas y Brian Barreto

public class MyVisitor<T> extends Python3BaseVisitor<T> {

    //Variables para las bases de datos de las palabras
    private HashMap<String, Integer> db_abreviations = new HashMap<>();
    private HashMap<String, Integer> db_spanish = new HashMap<>();
    private HashMap<String, Integer> db_english = new HashMap<>();
    private HashMap<String, Integer> db_posible_words = new HashMap<>();
    private HashMap<String, Integer> db_other_words = new HashMap<>();
    private HashMap<String, String> db_users = new HashMap<>();

    //Analisis de palabras
    static int english_words_cont;
    static int spanish_words_cont;
    static int abreviations_words_cont;
    static int other_words_cont;
    static int complete_words_cont;
    static int uncomplete_words_cont;
    static int Ucamel_case_cont;
    static int Lcamel_case_cont;
    static int all_caps_count;
    static int small_caps_count;
    static int snake_case_cont;
    static int numlines ;

    //Analisis condicionales
    static int if_cont;
    //private int switch_cont;

    //Analisis bucles
    static int for_cont;
    static int while_cont;
    static int lambda_cont;
    static boolean lambda_exist;
    // Funcion que se utiliza para leer las palabras de la base de datos
    // Las palabras son guardadas en un Diccionario
    private void readDb( String path, HashMap<String, Integer> db ){
        File file = new File( path );
        FileReader fileR;
        BufferedReader file2 = null;

        try {
            fileR = new FileReader(file);
            file2 = new BufferedReader(fileR);


        } catch (FileNotFoundException e) {
            System.out.println("No se encontro el archivo "+file.getName());
        }

        try {
            String lines;
            while( ( lines = file2.readLine()) != null ) {
                if( db.containsKey( lines ) )
                    db.put( lines, db.get( lines ) + 1);
                else
                    db.put( lines, 1 );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeDb( String path, String word ){
        FileWriter file = null;
        try {
            file = new FileWriter(path, true);
            PrintWriter filePw = new PrintWriter(file);

            filePw.write( word +"\n" );


            file.close();
        } catch (IOException e){
            System.out.println("No se encontro el archivo "+file.getEncoding());
        }
    }

    //Función que analizara las palabras de los nombres agregando a las variables
    private void wordAnalysis( String word ){

        //System.out.println("WORD in analysis: "+word);
        if( db_abreviations.containsKey( word ) ){
            abreviations_words_cont += 1;
            uncomplete_words_cont += 1;

        }else if( db_english.containsKey( word ) ){
            english_words_cont += 1;
            complete_words_cont += 1;

        } else if( db_spanish.containsKey( word ) ){
            spanish_words_cont += 1;
            complete_words_cont += 1;

        } else if( db_other_words.containsKey( word ) ){
            other_words_cont += 1;
            complete_words_cont += 1;

        } else if( db_posible_words.containsKey( word ) ){
            //System.out.println("else if: "+word);
            other_words_cont += 1;
            uncomplete_words_cont += 1;
            //lookPosibleToOther( word );

        } else {
            //System.out.println("else: "+word);
            other_words_cont += 1;
            uncomplete_words_cont += 1;
            addToPosible( word );
        }
    }

    private void addToPosible( String word ){

        db_posible_words.put( word, 1 );
        writeDb( "db/posible_words_db", word );
        //wordAnalysis( word );

    }

    private void StyleAnalysis( String word){


        if(word == null){
            StyleAnalysis("blank");
        }else{
            if (word.contains("_")){
                snake_case_cont++;
                String [] words = word.split("_");
                if(words.length > 1){
                    for (int i = 0; i < words.length ; i++)
                        wordAnalysis(words[0].toLowerCase());

                }else
                    wordAnalysis(word.toLowerCase());

            }
            else if(word.matches("[A-Z].*[a-z].*") ){
                //System.out.println("UCamelCase");
                Ucamel_case_cont++;
                int aux = 0;
                for (int i = 1; i < word.length() ; i++) {

                    String letter = String.valueOf(word.charAt(i)) ;
                    if(letter.matches("[A-Z].*")){
                        //System.out.println(word.substring(aux,i));
                        wordAnalysis(word.substring(aux,i).toLowerCase());
                        aux = i;
                    }else if(i == word.length()-1)
                        wordAnalysis(word.substring(aux,i+1).toLowerCase());

                }

            }else if(word.matches("[a-z].*[A-Z].*")){
                //System.out.println("LCamelCase");
                Lcamel_case_cont++;
                int aux = 0;
                for (int i = 1; i < word.length() ; i++) {

                    String letter = String.valueOf(word.charAt(i)) ;
                    if(letter.matches("[A-Z].*")){
                        //System.out.println(word.substring(aux,i).toLowerCase());
                        wordAnalysis(word.substring(aux,i).toLowerCase());
                        aux = i;
                    }else if(i == word.length()-1){
                        //System.out.println(word.substring(aux,i+1).toLowerCase());
                        wordAnalysis(word.substring(aux,i+1).toLowerCase());
                    }


                }

            }else if(word.matches("[A-Z].*")){
                //System.out.println("All_caps");
                all_caps_count++;
                wordAnalysis(word);

            }else if(word.matches("[a-z].*")){
                //System.out.println("Small_caps");
                small_caps_count++;
                wordAnalysis(word.toLowerCase());

            };
        }

    }


    //Función donde se inicializara las variables para el analisis del codigo.
    //Se inicializaran todas las variables que requieran un valor por defecto cuando se introdusca un codigo nuevo
    private void initializeVariables(){

        english_words_cont = 0;
        spanish_words_cont = 0;
        abreviations_words_cont = 0;
        other_words_cont = 0;
        complete_words_cont = 0;
        uncomplete_words_cont = 0;

        Ucamel_case_cont = 0;
        Lcamel_case_cont = 0;
        all_caps_count = 0;
        small_caps_count = 0;
        snake_case_cont = 0;

        if_cont = 0;
        //switch_cont = 0;

        for_cont = 0;
        while_cont = 0;
        lambda_cont = 0;
        lambda_exist = false;

        readDb( "db/abreviation_words_db", db_abreviations );
        readDb( "db/english_words_db", db_english );
        readDb( "db/spanish_words_db", db_spanish );
        readDb( "db/other_words_db", db_other_words );
        readDb( "db/posible_words_db", db_posible_words );
    }

    @Override
    public T visitSingle_input(Python3Parser.Single_inputContext ctx) {
        //System.out.println("Single_input");
        return visitChildren(ctx);
    }

    @Override
    public T visitFile_input(Python3Parser.File_inputContext ctx) {
        //System.out.println("File_input");
        numlines = ctx.stmt().size();
        initializeVariables();

        return visitChildren(ctx);
    }

    @Override
    public T visitEval_input(Python3Parser.Eval_inputContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public T visitDecorator(Python3Parser.DecoratorContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public T visitDecorators(Python3Parser.DecoratorsContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public T visitDecorated(Python3Parser.DecoratedContext ctx) { return visitChildren(ctx); }

    @Override
    public T visitAsync_funcdef(Python3Parser.Async_funcdefContext ctx) { return visitChildren(ctx); }

    @Override
    public T visitFuncdef(Python3Parser.FuncdefContext ctx) {
        //System.out.println("Funcdef");
        String name = ctx.NAME().getText();
        if (ctx.suite().isEmpty() == false)
            numlines +=  ctx.suite().stmt().size();

        // Analisis al nombre de las funciones
        StyleAnalysis(name);
        String params = ctx.parameters().getText();
        String [] parametros = params.substring(1,params.length()-1).split(",");
        for (int i = 0; i < parametros.length ; i++) {
            // Analisis a los parametros de las funciones
            StyleAnalysis(parametros[i]);
        }


        return null;

    }

    @Override
    public T visitParameters(Python3Parser.ParametersContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public T visitTypedargslist(Python3Parser.TypedargslistContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public T visitTfpdef(Python3Parser.TfpdefContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public T visitVarargslist(Python3Parser.VarargslistContext ctx) {
        //System.out.println("Vararglist");
        //System.out.println("lo que sea: "+ctx.vfpdef().size());
        return visitChildren(ctx);
    }

    @Override
    public T visitVfpdef(Python3Parser.VfpdefContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public T visitStmt(Python3Parser.StmtContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public T visitSimple_stmt(Python3Parser.Simple_stmtContext ctx) {
        //System.out.println("Simple_stmt");
        return visitChildren(ctx);
    }

    @Override
    public T visitSmall_stmt(Python3Parser.Small_stmtContext ctx) {
        //System.out.println("Small_stmt");
        return visitChildren(ctx);
    }



    @Override public T visitExpr_stmt(Python3Parser.Expr_stmtContext ctx) {
        //System.out.println("Expr_stmt: ");
        //System.out.println("ctx: "+ctx.testlist_star_expr(0).getText());
        String variable = (String)visitTestlist_star_expr(ctx.testlist_star_expr(0));
        // Se analiza el estilo de la variable

        //System.out.println("variable: "+variable);
        StyleAnalysis(variable);


        return null;
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitAnnassign(Python3Parser.AnnassignContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitTestlist_star_expr(Python3Parser.Testlist_star_exprContext ctx) {
        //System.out.println("Testlist_star_expr");

        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitAugassign(Python3Parser.AugassignContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitDel_stmt(Python3Parser.Del_stmtContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitPass_stmt(Python3Parser.Pass_stmtContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitFlow_stmt(Python3Parser.Flow_stmtContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitBreak_stmt(Python3Parser.Break_stmtContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitContinue_stmt(Python3Parser.Continue_stmtContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitReturn_stmt(Python3Parser.Return_stmtContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitYield_stmt(Python3Parser.Yield_stmtContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitRaise_stmt(Python3Parser.Raise_stmtContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitImport_stmt(Python3Parser.Import_stmtContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitImport_name(Python3Parser.Import_nameContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitImport_from(Python3Parser.Import_fromContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitImport_as_name(Python3Parser.Import_as_nameContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitDotted_as_name(Python3Parser.Dotted_as_nameContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitImport_as_names(Python3Parser.Import_as_namesContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitDotted_as_names(Python3Parser.Dotted_as_namesContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitDotted_name(Python3Parser.Dotted_nameContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitGlobal_stmt(Python3Parser.Global_stmtContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitNonlocal_stmt(Python3Parser.Nonlocal_stmtContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitAssert_stmt(Python3Parser.Assert_stmtContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitCompound_stmt(Python3Parser.Compound_stmtContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitAsync_stmt(Python3Parser.Async_stmtContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitIf_stmt(Python3Parser.If_stmtContext ctx) {
        if_cont++;
        numlines += ctx.suite(0).stmt().size();
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitWhile_stmt(Python3Parser.While_stmtContext ctx) {
        while_cont++;
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitFor_stmt(Python3Parser.For_stmtContext ctx) {
        for_cont++;
        numlines += ctx.suite(0).stmt().size();
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitTry_stmt(Python3Parser.Try_stmtContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitWith_stmt(Python3Parser.With_stmtContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitWith_item(Python3Parser.With_itemContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitExcept_clause(Python3Parser.Except_clauseContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitSuite(Python3Parser.SuiteContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitTest(Python3Parser.TestContext ctx) {
        //System.out.println("Test");
        //System.out.println("ctx: "+ctx.getText());
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitTest_nocond(Python3Parser.Test_nocondContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitLambdef(Python3Parser.LambdefContext ctx) {
        //System.out.println("lambdef");
        lambda_cont++;
        lambda_exist = true;
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitLambdef_nocond(Python3Parser.Lambdef_nocondContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitOr_test(Python3Parser.Or_testContext ctx) {
        //System.out.println("or_test");
        return visitChildren(ctx);
    }

    @Override
    public T visitAnd_test(Python3Parser.And_testContext ctx) {
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitNot_test(Python3Parser.Not_testContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitComparison(Python3Parser.ComparisonContext ctx) {
        //System.out.println("Comparison");
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitComp_op(Python3Parser.Comp_opContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitStar_expr(Python3Parser.Star_exprContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitExpr(Python3Parser.ExprContext ctx) {
        //System.out.println("Expr");
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitXor_expr(Python3Parser.Xor_exprContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitAnd_expr(Python3Parser.And_exprContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitShift_expr(Python3Parser.Shift_exprContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitArith_expr(Python3Parser.Arith_exprContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitTerm(Python3Parser.TermContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitFactor(Python3Parser.FactorContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitPower(Python3Parser.PowerContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitAtom_expr(Python3Parser.Atom_exprContext ctx) {
        //System.out.println("Atom Expr ");
        /*//System.out.println("Atom: "+visitAtom(ctx.atom()));
        System.out.println("casdfsdagf: "+ctx.trailer(0).getText());
        System.out.println("trailer; "+visitTrailer(ctx.trailer(0)));*/
        return visitChildren(ctx);

    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitAtom(Python3Parser.AtomContext ctx) {
        //System.out.println("Atom");
        if(ctx.NAME()!=null){
            //System.out.println("ID");
            //ident = "ID";
            return (T)ctx.NAME().getText();
        }else if(ctx.NUMBER()!=null){
            //System.out.println("NUMBER");
            //ident = "NUMBER";
            return (T)ctx.NUMBER().getText();
        }
        else if(ctx.STRING()!=null && ctx.STRING().size() > 0){

            //ident = "STRING";
            return (T)ctx.STRING(0).getText();
        }else if(ctx.FALSE()!=null){
            return (T)ctx.FALSE().getText();
        }
        else if(ctx.TRUE()!=null){
            return (T)ctx.TRUE().getText();
        }
        else{
            return (T)visitChildren(ctx);
        }
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitTestlist_comp(Python3Parser.Testlist_compContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitTrailer(Python3Parser.TrailerContext ctx) {

        if(ctx.DOT()!=null){
            return (T)ctx.NAME().getText();
        }else if(ctx.OPEN_PAREN()!=null){
            if(ctx.arglist()==null)
                return (T)" ";
            else{
                for (int i = 0; i < ctx.arglist().argument().size() ; i++)
                    return visitArgument(ctx.arglist().argument().get(i)) ;

            }

        }else{
            for (int i = 0; i < ctx.subscriptlist().subscript().size() ; i++)
                return visitSubscript(ctx.subscriptlist().subscript().get(i)) ;
        }
        //System.out.println("Trailer");
        //System.out.println("ctx: "+ctx.getText());
        /*for (int i = 0; i < ctx.arglist().argument().size() ; i++) {
            //System.out.println("los argumentos: "+ctx.arglist().argument().get(i).getText());
            return visitArgument(ctx.arglist().argument().get(i)) ;
            //StyleAnalysis(ctx.arglist().argument().get(i).getText());
        }*/
        //return visitChildren(ctx);
        return null;
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitSubscriptlist(Python3Parser.SubscriptlistContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitSubscript(Python3Parser.SubscriptContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitSliceop(Python3Parser.SliceopContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitExprlist(Python3Parser.ExprlistContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitTestlist(Python3Parser.TestlistContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitDictorsetmaker(Python3Parser.DictorsetmakerContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override
    public T visitClassdef(Python3Parser.ClassdefContext ctx) {
        //System.out.println("Classdef");
        String Name = ctx.NAME().getText();
        StyleAnalysis(Name);
        return visitChildren(ctx);
    }
    @Override
    public T visitArglist(Python3Parser.ArglistContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public T visitArgument(Python3Parser.ArgumentContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public T visitComp_iter(Python3Parser.Comp_iterContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public T visitComp_for(Python3Parser.Comp_forContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public T visitComp_if(Python3Parser.Comp_ifContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public T visitEncoding_decl(Python3Parser.Encoding_declContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public T visitYield_expr(Python3Parser.Yield_exprContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public T visitYield_arg(Python3Parser.Yield_argContext ctx) {
        return visitChildren(ctx);
    }

}
