package benchmarkschool;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Leonardo Oliveira Moreira
 *
 *         Classe principal que faz a criação do Banco de Dados school conforme
 *         o
 *         tamanho especificado.
 */
public class BenchmarkSchool {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // String url = "jdbc:postgresql://localhost/school";
        // String user = "postgres";
        // String password = "postgres";
        String url = null;
        String user = null;
        String password = null;

        if (args == null || args.length != 5) {
            System.err.println("Número de Parâmetros Inválido");
            System.out.println(
                    "Exemplo do Comando para Criar um Banco de Dados de 10 MB: java -jar benchmarkschool.jar -t 10 jdbc:mysql://localhost:3306/school user password");
            System.exit(-1);
        } else {
            if (args[0] == null || !args[0].equals("-t")) {
                System.err.println("O primeiro parâmetro está inválido");
                System.out.println(
                        "Exemplo do Comando para Criar um Banco de Dados de 10 MB: java -jar benchmarkschool.jar -t 10 jdbc:mysql://localhost:3306/school user password");
                System.exit(-1);
            }
            if (args[1] == null || !isPositiveNumber(args[1])) {
                System.err.println("O segundo parâmetro não é um número inteiro positivo");
                System.out.println(
                        "Exemplo do Comando para Criar um Banco de Dados de 10 MB: java -jar benchmarkschool.jar -t 10 jdbc:mysql://localhost:3306/school user password");
                System.exit(-1);
            }
            if (args[2] == null || args[2].trim().length() == 0) {
                System.err.println("O terceiro parâmetro não é uma cadeira de caracteres válida");
                System.out.println(
                        "Exemplo do Comando para Criar um Banco de Dados de 10 MB: java -jar benchmarkschool.jar -t 10 jdbc:mysql://localhost:3306/school user password");
                System.exit(-1);
            }
            url = args[2];
            if (args[3] == null || args[3].trim().length() == 0) {
                System.err.println("O quarto parâmetro não é uma cadeira de caracteres válida");
                System.out.println(
                        "Exemplo do Comando para Criar um Banco de Dados de 10 MB: java -jar benchmarkschool.jar -t 10 jdbc:mysql://localhost:3306/school user password");
                System.exit(-1);
            }
            user = args[3];
            if (args[4] == null || args[4].trim().length() == 0) {
                System.err.println("O quinto parâmetro não é uma cadeira de caracteres válida");
                System.out.println(
                        "Exemplo do Comando para Criar um Banco de Dados de 10 MB: java -jar benchmarkschool.jar -t 10 jdbc:mysql://localhost:3306/school user password");
                System.exit(-1);
            }
            password = args[4];
        }

        int sizeInMB = Integer.parseInt(args[1]);
        // int sizeInMB = 100;

        try {
            // Class.forName("org.postgresql.Driver");
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver MySQL Server Carregado");

            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println("Conexão com MySQL Server Iniciada");

            // System.out.println("Executando Limpeza da Tabela 'school'");
            // System.out.println("Número de Linha(s) Removida(s): " +
            // eraseDatabase(connection));

            System.out.println("Gerando o Banco de Dados 'school' ...");
            BigInteger bigInteger = BigInteger.valueOf(0);
            int linesBatch = 10000;
            while (getDatabaseSize(connection) <= sizeInMB) {
                /*
                 * int enrolled = getRandomNumber(3);
                 * int present = getRandomNumber(3);
                 * int absented = getRandomNumber(2);
                 * int released = getRandomNumber(1);
                 * insertLine(connection, "01M015", "01/07/2016", "20152016", enrolled, present,
                 * absented, released);
                 */
                insertBatch(connection, "01M015", "01/07/2016", "20152016", linesBatch);
                bigInteger = bigInteger.add(BigInteger.valueOf(linesBatch));
                if (bigInteger.longValue() % 1000 == 0) {
                    System.out.println("Número de Linhas Inseridas: " + bigInteger.longValue());
                    System.out.println(
                            "Tamanho Atual do Banco de Dados 'school': " + getDatabaseSize(connection) + " MB");
                }
            }

            System.out.println("Processo de Geração o Banco de Dados 'school' Concluído");
            System.out.println("Tamanho Final do Banco de Dados 'school': " + getDatabaseSize(connection) + " MB");

            connection.close();
            System.out.println("Conexão com MySQL Server Finalizada");
        } catch (ClassNotFoundException ex) {
            System.err.println(ex.getMessage());
            System.exit(-1);

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            System.exit(-1);
        }
    }

    /**
     * Método utilizado para inserir uma linha na tabela school.
     *
     * @param connection
     * @param idschool
     * @param date
     * @param schoolYear
     * @param enrolled
     * @param present
     * @param absented
     * @param released
     * @return
     * @throws SQLException
     */
    public static boolean insertBatch(Connection connection, String idschool, String date, String schoolYear,
            int countLines) throws SQLException {
        boolean result = false;
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO school"
                + "(idschool, date, schoolyear, enrolled, present, absented, released) "
                + "VALUES (?, ?, ?, ? , ?, ?, ?)")) {
            connection.setAutoCommit(false);
            for (int i = 0; i < countLines; i++) {
                int enrolled = getRandomNumber(3);
                int present = getRandomNumber(3);
                int absented = getRandomNumber(2);
                int released = getRandomNumber(1);
                preparedStatement.setString(1, idschool);
                preparedStatement.setString(2, date);
                preparedStatement.setString(3, schoolYear);
                preparedStatement.setInt(4, enrolled);
                preparedStatement.setInt(5, present);
                preparedStatement.setInt(6, absented);
                preparedStatement.setInt(7, released);
                preparedStatement.addBatch();
            }
            int[] updateCounts = preparedStatement.executeBatch();
            if (updateCounts.length > 0) {
                result = true;
            }
            connection.commit();

        }

        return result;
    }

    /**
     * Método utilizado para inserir uma linha na tabela school.
     *
     * @param connection
     * @param idschool
     * @param date
     * @param schoolYear
     * @param enrolled
     * @param present
     * @param absented
     * @param released
     * @return
     * @throws SQLException
     */
    public static boolean insertLine(Connection connection, String idschool, String date, String schoolYear,
            int enrolled, int present, int absented, int released) throws SQLException {
        boolean result = false;
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO school (idschool, date, schoolyear, enrolled, present, absented, released) VALUES (?, ?, ?, ? , ?, ?, ?)")) {
            preparedStatement.setString(1, idschool);
            preparedStatement.setString(2, date);
            preparedStatement.setString(3, schoolYear);
            preparedStatement.setInt(4, enrolled);
            preparedStatement.setInt(5, present);
            preparedStatement.setInt(6, absented);
            preparedStatement.setInt(7, released);
            result = preparedStatement.executeUpdate() == 1;
        }
        return result;
    }

    /**
     * Método utilizado para remover todas as linhas na tabela school.
     *
     * @param connection
     * @return
     * @throws SQLException
     */

    /**
     * public static int eraseDatabase(Connection connection) throws SQLException {
     * int result;
     * try (Statement statement = connection.createStatement()) {
     * statement.execute("ANALYZE TABLE school");
     * }
     * try (PreparedStatement preparedStatement =
     * connection.prepareStatement("DELETE FROM school")) {
     * result = preparedStatement.executeUpdate();
     * }
     * return result;
     * }
     * 
     * public static int backupDatabase(Connection connection) throws SQLException {
     * int result;
     * try (Statement statement = connection.createStatement()) {
     * statement.execute("ANALYZE TABLE school");
     * }
     * try (PreparedStatement preparedStatement =
     * connection.prepareStatement("DELETE FROM school")) {
     * result = preparedStatement.executeUpdate();
     * }
     * return result;
     * }
     **/

    /**
     * Método utilizado para recuperar o tamanho do Banco de Dados school em
     * megabytes.
     *
     * @param connection
     * @return
     * @throws SQLException
     */
    public static double getDatabaseSize(Connection connection) throws SQLException {
        double sizeInMB = 0;
        try (Statement statement = connection.createStatement()) {
            statement.execute("ANALYZE TABLE school");
        }
        try (
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT ROUND(SUM(data_length + index_length) / 1024 / 1024, 4) AS size FROM information_schema.tables WHERE table_schema = 'school' GROUP BY table_schema");
                ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                sizeInMB = resultSet.getDouble("size");
            }
        }
        return sizeInMB;
    }

    /**
     * Método utilizado para recuperar um caractere aleatório
     *
     * @param length
     * @return
     */
    public static String getRandomCharacter(int length) {
        String result = "";
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        while (result.length() <= length) {
            int index = (int) (characters.length() * Math.random());
            result += characters.charAt(index);
        }
        return result;
    }

    /**
     * Método utilizado para recuperar um número aleatório
     *
     * @param digits
     * @return
     */
    public static int getRandomNumber(int digits) {
        String result = "";
        String characters = "0123456789";
        while (result.length() <= digits) {
            int index = (int) (characters.length() * Math.random());
            result += characters.charAt(index);
            result = String.valueOf(Integer.parseInt(result));
        }
        return Integer.parseInt(result);
    }

    /**
     * Método utilizado para verificar se a string é um número inteiro positivo.
     *
     * @param string
     * @return
     */
    public static boolean isPositiveNumber(String string) {
        boolean result = false;
        try {
            int positiveNumber = Integer.parseInt(string);
            result = (positiveNumber > 0);
        } catch (Exception ex) {
            result = false;
        }
        return result;
    }
}
