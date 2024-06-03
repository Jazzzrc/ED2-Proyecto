package ed2_project;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import org.json.simple.JSONArray;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JComboBox;


class Node {
    String name;
    JSONObject jsonData;
    Node[] children;

    Node(String name) {
        this.name = name;
        this.jsonData = new JSONObject();
        this.children = new Node[3];
    }
}

public class Ed2_Project {
    private static final String DATA_DIRECTORY = "data";

    public static void realizarRecorridoInOrden(Node raizDelArbol, JTextArea textArea) {
        JComboBox<String> comboBox = new JComboBox<>(new String[]{"Por el nombre del Autor", "Por el nombre del Título"});
        Object[] message = {
            "¿Cómo quieres buscar tu libro?", comboBox
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Búsqueda de libros", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            int selectedIndex = comboBox.getSelectedIndex();

            String searchTerm;
            if (selectedIndex == 0) {
                searchTerm = JOptionPane.showInputDialog(null, "Ingresa el nombre del Autor para buscarlo:", "Búsqueda por Autor", JOptionPane.PLAIN_MESSAGE);
            } else {
                searchTerm = JOptionPane.showInputDialog(null, "Ingresa el nombre del Título para buscarlo:", "Búsqueda por Título", JOptionPane.PLAIN_MESSAGE);
            }

            if (searchTerm != null) {
                textArea.setText("");
                buscarLibro(raizDelArbol, selectedIndex, searchTerm, textArea);
            }
        }
    }

    public static void buscarLibro(Node node, int option, String searchTerm, JTextArea textArea) {
        if (node != null) {
            buscarLibro(node.children[0], option, searchTerm, textArea); // Recorrido inorden: izquierda
            JSONArray librosArray = (JSONArray) node.jsonData.get("data");
            if (librosArray != null) {
                for (Object libroObj : librosArray) {
                    JSONObject libro = (JSONObject) libroObj;
                    String value;
                    if (option == 0) {
                        value = (String) libro.get("Autor");
                    } else {
                        value = (String) libro.get("Titulo");
                    }

                    if (value.equalsIgnoreCase(searchTerm)) {
                        StringBuilder builder = new StringBuilder();
                        builder.append("| Título: ").append(libro.get("Titulo")).append("\n");
                        builder.append("| Autor: ").append(libro.get("Autor")).append("\n");
                        builder.append("| Lenguaje: ").append(libro.get("Lenguaje")).append("\n");
                        builder.append("| Año de publicación: ").append(libro.get("Año de publicación")).append("\n");
                        builder.append("| Edición: ").append(libro.get("Edición")).append("\n");
                        builder.append("| Link de búsqueda: ").append(libro.get("Link de búsqueda")).append("\n");
                        builder.append("-----------------------------\n");

                        textArea.append(builder.toString());
                    }
                }
            }
            buscarLibro(node.children[1], option, searchTerm, textArea); // Recorrido inorden: centro
            buscarLibro(node.children[2], option, searchTerm, textArea); // Recorrido inorden: derecha
        }
    }

    public static void subirInformacion(Node root) {
        String[] options = {"Cálculo", "Algoritmos", "POO"};
        JComboBox<String> comboBox = new JComboBox<>(options);
        Object[] message = {
            "Seleccione la sección:", comboBox
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Selección de sección", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            int selectedIndex = comboBox.getSelectedIndex();
            Node selectedNode = root.children[selectedIndex];

            JTextField tituloField = new JTextField();
            JTextField autorField = new JTextField();
            JTextField idiomaField = new JTextField();
            JTextField anioField = new JTextField();
            JTextField edicionField = new JTextField();
            JTextField linkField = new JTextField();

            Object[] fields = {
                "Título del libro:", tituloField,
                "Autor del libro:", autorField,
                "Idioma del libro:", idiomaField,
                "Año de publicación del libro:", anioField,
                "Edición del libro:", edicionField,
                "Link de búsqueda del libro (opcional):", linkField
            };

            int result = JOptionPane.showConfirmDialog(null, fields, "Subir información del libro", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                JSONArray librosArray;
                if (selectedNode.jsonData.containsKey("data")) {
                    librosArray = (JSONArray) selectedNode.jsonData.get("data");
                } else {
                    librosArray = new JSONArray();
                }

                JSONObject libroInfo = new JSONObject();
                libroInfo.put("Titulo", tituloField.getText());
                libroInfo.put("Autor", autorField.getText());
                libroInfo.put("Lenguaje", idiomaField.getText());
                libroInfo.put("Año de publicación", Integer.parseInt(anioField.getText()));
                libroInfo.put("Edición", edicionField.getText());
                libroInfo.put("Link de búsqueda", linkField.getText());

                librosArray.add(libroInfo);
                selectedNode.jsonData.put("data", librosArray);

                String nombreArchivo = selectedNode.name + ".json";
                crearJSONFile(nombreArchivo, selectedNode.jsonData);
                JOptionPane.showMessageDialog(null, "¡La información se ha subido correctamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public static void salirGuardarComentario() {
        JTextArea textArea = new JTextArea(10, 30);
        textArea.setEditable(false);

        try {
            Path filePath = Path.of("comentarios.txt");

            if (Files.exists(filePath)) {
                String existingContent = Files.readString(filePath);
                textArea.setText(existingContent);
            }

            JOptionPane.showMessageDialog(null, textArea, "Comentarios de nuestros usuarios", JOptionPane.PLAIN_MESSAGE);

            String comment = JOptionPane.showInputDialog(null, "Déjanos tu comentario aquí:", "Guardar comentario", JOptionPane.PLAIN_MESSAGE);

            if (comment != null) {
                Path comentariosPath = Path.of(DATA_DIRECTORY, "comentarios.txt");
                Files.writeString(comentariosPath, comment + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);

                JOptionPane.showMessageDialog(null, "¡El comentario se ha guardado correctamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("BooKeaper: Sistema de Almacenamiento");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(4, 1));

        JButton subirDatosButton = new JButton("Subir datos del libro");
        JButton buscarLibroButton = new JButton("Buscar libro (recorrido inOrden)");
        JButton salirComentarioButton = new JButton("Salir de BooKeaper y dejar un comentario");
        
        
        panel.setBackground(Color.darkGray);

        panel.add(subirDatosButton);
        panel.add(buscarLibroButton);
        panel.add(salirComentarioButton);

        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.setVisible(true);


        // Creación del árbol
        Node root = crearArbol();

        subirDatosButton.addActionListener(e -> subirInformacion(root));
        buscarLibroButton.addActionListener(e -> {
            JFrame searchFrame = new JFrame("Buscar libro");
            searchFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            searchFrame.setSize(400, 200);
            searchFrame.setLayout(new BorderLayout());

            JPanel searchPanel = new JPanel(new BorderLayout());

            JComboBox<String> comboBox = new JComboBox<>(new String[]{"Por el nombre del Autor", "Por el nombre del Título"});
            JTextField searchTermField = new JTextField();
            JButton searchButton = new JButton("Buscar");

            searchPanel.add(comboBox, BorderLayout.NORTH);
            searchPanel.add(searchTermField, BorderLayout.CENTER);
            searchPanel.add(searchButton, BorderLayout.SOUTH);

            searchFrame.getContentPane().add(searchPanel, BorderLayout.CENTER);
            searchFrame.setVisible(true);

            searchButton.addActionListener(e1 -> {
                int optionIndex = comboBox.getSelectedIndex();
                String searchTerm = searchTermField.getText();

                JFrame resultFrame = new JFrame("Resultados de búsqueda");
                resultFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                resultFrame.setSize(600, 400);
                resultFrame.setLayout(new BorderLayout());

                JTextArea textArea = new JTextArea();
                textArea.setEditable(false);

                JScrollPane scrollPane = new JScrollPane(textArea);

                resultFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);
                resultFrame.setVisible(true);

                buscarLibro(root, optionIndex, searchTerm, textArea);
            });
        });

        salirComentarioButton.addActionListener(e -> salirGuardarComentario());
    }

    public static Node crearArbol() {
        Node root = new Node("root");
        root.children[0] = new Node("Calculo");
        root.children[1] = new Node("Algoritmos");
        root.children[2] = new Node("POO");

        return root;
    }

    public static void crearJSONFile(String fileName, JSONObject jsonData) {
        try {
            Path directoryPath = Path.of(DATA_DIRECTORY);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            Path filePath = Path.of(directoryPath.toString(), fileName);

            FileWriter file = new FileWriter(filePath.toString());

            String jsonString = jsonData.toJSONString().replace(",", ",\n");

            file.write(jsonString);
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}