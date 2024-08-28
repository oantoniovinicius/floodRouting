package control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import model.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

public class mainControl implements Initializable{
  @FXML private ImageView version1;
  @FXML private ImageView version2;
  @FXML private ImageView version3;

  @FXML private ImageView background;
  @FXML private ImageView screen;
  @FXML private ImageView node;
  @FXML private ImageView nodeSent;
  @FXML private ImageView nodeReceive;

  @FXML private ImageView startButton;

  ArrayList<String> graph = new ArrayList<>(); // Nos do Grafo para leitura do txt e implementacao visual
  ArrayList<Nodes> nodes = new ArrayList<>(); // Roteadores
  ArrayList<ImageView> nodeImage = new ArrayList<>(); // Imagem dos roteadores
  ArrayList<Polyline> lines = new ArrayList<>(); //Linha visual que conecta os roteadores
  ArrayList<Label> numbers = new ArrayList<>(); // Numero dos Roteadores
  int versionSelected = 0;
  int TTL = 1;
  int nodeSender = -1;
  int nodeReceiver = -1;
  int totalPackets = 0;

  boolean received = false;
  boolean graphFlag = true;

  Pane root = new Pane();

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    screen.setVisible(false);
    startButton.setVisible(false);
    startButton.setDisable(true);

    ColorAdjust colorAdjust = new ColorAdjust();
    colorAdjust.setBrightness(0.5);

    version1.setOnMouseEntered(event -> {
      version1.setEffect(colorAdjust);
    });

    version1.setOnMouseExited(event -> {
      version1.setEffect(null);
    });

    version2.setOnMouseEntered(event -> {
      version2.setEffect(colorAdjust);
    });

    version2.setOnMouseExited(event -> {
      version2.setEffect(null);
    });

    version3.setOnMouseEntered(event -> {
      version3.setEffect(colorAdjust);
    });

    version3.setOnMouseExited(event -> {
      version3.setEffect(null);
    });
  }
    
  public boolean readBackbone(){
    String backbone = "./backbone.txt";

    try {
      File file = new File(backbone);
      System.out.println("Backbone valido: " + file.exists());

      // Criação de um BufferedReader para ler o arquivo
      BufferedReader reader = new BufferedReader(new FileReader(backbone));
      System.out.println("Leu");
      // String para armazenar cada linha do arquivo
      String line;
      // Loop para ler cada linha do arquivo
      while ((line = reader.readLine()) != null) {
        graph.add(line);
      }
      // Fecha o BufferedReader após a leitura
      reader.close();
      return true;
    } catch (IOException e) {
      Alert alert = new Alert(AlertType.WARNING);
      alert.setTitle("Aviso");
      alert.setHeaderText("Ocorreu um erro ao ler o arquivo!");
      alert.showAndWait();
      
      return false;
    } catch (NumberFormatException e) {
      Alert alert = new Alert(AlertType.WARNING);
      alert.setTitle("Aviso");
      alert.setHeaderText("Ocorreu um erro ao ler o arquivo!");
      alert.showAndWait();

      return false;
    }
  }

  public void addNode(Pane root) { 
    int totalNodes = Integer.parseInt(graph.get(0).replaceAll(";", ""));
    System.out.println("Numero de roteadores na rede: " + totalNodes);

    if (totalNodes > 20) { //total de roteadores = 20
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Erro!");
        alert.setHeaderText("O número de roteadores deve ser menor ou igual a 20!");
        alert.showAndWait();
    } else {
        ArrayList<Circle> routers = new ArrayList<>();
        int numCircles = totalNodes;
        double centerX = root.getWidth() / 2;
        double centerY = (root.getHeight() / 2) + 20;
        double radius = 210;  // Ajustar se necessário para acomodar 20 nós
        double angleIncrement = 2 * Math.PI / numCircles;

        for (int i = 0; i < numCircles; i++) {
            double angle = -Math.PI / 2 + i * angleIncrement;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);

            Circle node = new Circle(x, y, 25);
            routers.add(node);

            Nodes router = new Nodes(i + 1);
            router.setController(this);
            nodes.add(router);

            double labelX = x - 32;
            double labelY = y - 32;

            Label label = new Label(Integer.toString(i + 1));
            label.setLayoutX(labelX);
            label.setLayoutY(labelY);
            label.setTextFill(Color.WHITE);
            numbers.add(label);
        }

        for (int i = 0; i < graph.size(); i++) {
            String[] parts = graph.get(i).split(";");
            if (parts.length >= 2) {
                int node1 = Integer.parseInt(parts[0]);
                int node2 = Integer.parseInt(parts[1]);

                Polyline polyline = new Polyline(
                    routers.get(node1 - 1).getCenterX(), routers.get(node1 - 1).getCenterY(),
                    routers.get(node2 - 1).getCenterX(), routers.get(node2 - 1).getCenterY()
                );
                lines.add(polyline);
                nodes.get(node1 - 1).addConnection(node2, polyline);
                nodes.get(node2 - 1).addConnection(node1, polyline);
            }
        }

        for (Polyline polyline : lines) {
            polyline.setStroke(Color.BLACK);
            polyline.setStrokeWidth(2);
            root.getChildren().add(polyline);
        }

        for (Circle circle : routers) {
            ImageView firstNode = new ImageView(new Image("./imgs/node.png"));
            firstNode.setLayoutX(circle.getCenterX() - circle.getRadius());
            firstNode.setLayoutY(circle.getCenterY() - circle.getRadius());
            nodeImage.add(firstNode);
            root.getChildren().add(firstNode);
        }

        for (Label label : numbers) {
            root.getChildren().add(label);
        }

        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).listConnections();
        }

        changeScreen();
        selectFirstNode();
    }
}

  //Escolha no inicial
  public void selectFirstNode() {
    for (int i = 0; i < nodeImage.size(); i++) {
      nodeImage.get(i).setCursor(Cursor.HAND);
      final int posicao = i; // Armazena a posição atual do loop
      nodeImage.get(i).setOnMouseClicked(event -> {
        int aux = posicao+1;
        System.out.println("Roteador Transmissor: " + aux); // Imprime a posição
        setNodeInicial(aux); // Setando o valor do No Inicial
        nodeImage.get(posicao).setImage(new Image("./imgs/nodeSender.png"));
        // Remova o evento de clique de todas as imagens
        for (ImageView imageView : nodeImage) {
          imageView.setOnMouseClicked(null);
          imageView.setCursor(null);
        }
        //nodeSent.setVisible(false);
        //nodeSent.setDisable(true);
        selectFinalNode();
      });
    }
  }

  //Escolha no final
  public void selectFinalNode() {
    //nodeReceive.setVisible(true);
    //nodeReceive.setDisable(false);

    for (int i = 0; i < nodeImage.size(); i++) {
      if(i != getNodeSender()-1){ // Todos Nos possiveis, menos o inicial
        nodeImage.get(i).setCursor(Cursor.HAND);
        final int posicao = i; // Armazena a posição atual do loop
        nodeImage.get(i).setOnMouseClicked(event -> {
          int aux = posicao+1;
          System.out.println("Roteador Receptor: " + aux); // Imprime a posição
          setNodeFinal(aux); // Setando o valor do No Inicial
          nodeImage.get(posicao).setImage(new Image("./imgs/nodeReceiver.png"));
          // Remova o evento de clique de todas as imagens
          for (ImageView imageView : nodeImage) {
            imageView.setOnMouseClicked(null);
            imageView.setCursor(null);
          }
          //nodeReceive.setVisible(false);
          //nodeReceive.setDisable(true);
          /*if(getOpcaoEscolhida() == 3){
            BoxTTL.setVisible(true);
            BoxTTL.setDisable(false);
            valorTTL.setVisible(true);
            valorTTL.setDisable(false);
          }*/
        });
      }
    }
  }

  @FXML
  void clickStart(MouseEvent event) {
    switch (getVersionSelected()) {
      case 3:
        if(nodeReceiver != -1 && nodeSender != -1){
              /*TTL = valorTTL.getValue();
              valorTTL.setVisible(false);
              valorTTL.setDisable(true);
              BoxTTL.setVisible(false);
              BoxTTL.setDisable(true);
              //Inicia a Transmissao
              Roteadores.get(nodeInicial-1).EnviarPacotes(TTL, -1);
              botaoComecar.setVisible(false);
              botaoComecar.setDisable(true);
              botaoResetar.setVisible(true);
              botaoResetar.setDisable(false);*/
        }
        else{
          //alertaErro("Selecione o Transmissor e/ou Receptor");
        }
        break;

      default: //Casos 1 ou 2
      if(nodeReceiver != -1 && nodeSender != -1){
            nodes.get(nodeSender-1).sendPackets(TTL, -1);
            startButton.setVisible(false);
            startButton.setDisable(true);
            //botaoResetar.setVisible(true);
            //botaoResetar.setDisable(false);
        }
        else{
          //alertaErro("Selecione o Transmissor e/ou Receptor");
        }
        break;
    }
  }

    //Remove grafo da tela
  public void removeElementos(){
    for (ImageView image : nodeImage) {
      getRoot().getChildren().remove(image);
    }
    for (Polyline polyline : lines) {
      getRoot().getChildren().remove(polyline);
    }
    for (Label label : numbers) {
      getRoot().getChildren().remove(label);
    }
  }

  public void addPackets(){ 
    if(!received){
      totalPackets++;
        //somaPacotes.setText(Integer.toString(pacotesGerados));
    }
  }

  public void packetReceived(int node){
    nodeImage.get(node-1).setImage(new Image("./imgs/packetReceived.png"));
  }

  @FXML
  void versionSelected(MouseEvent event) {
    if(graphFlag){
      Node source = (Node) event.getSource();
      switch (source.getId()) { // Verifica qual botao foi escolhido
        case "version1":
          System.out.println("Versao 1 foi selecionada.");
          screen.setVisible(true);
          startButton.setVisible(true);
          startButton.setDisable(false);
          setVersionSelected(1);
        break;
        
        case "version2":
          System.out.println("Versao 2 foi selecionada.");
          screen.setVisible(true);
          startButton.setVisible(true);
          startButton.setDisable(false);
          setVersionSelected(2);
        break;
        
        case "version3":
          System.out.println("Versao 3 foi selecionada.");
          screen.setVisible(true);
          startButton.setVisible(true);
          startButton.setDisable(false);
          setVersionSelected(3);
        break;
      }
      if(readBackbone()){
        addNode(root);
      };
    }
  }

   //Alterna a troca entre telas
   public void changeScreen() {
    version1.setVisible(!version1.isVisible());
    version1.setDisable(!version1.isDisable());

    version2.setVisible(!version2.isVisible());
    version2.setDisable(!version2.isDisable());

    version3.setVisible(!version3.isVisible());
    version3.setDisable(!version3.isDisable());

    background.setVisible(!background.isVisible());
    background.setDisable(!background.isDisable());
  }

  //getter and setters
  public ArrayList<Nodes> getNodes() {
    return nodes;
  }

  public void setPane(Pane pane){
    root = pane;
  }

  public void setReceived(boolean received) {
    this.received = received;
  }

  public Pane getRoot(){
    return root;
  }

  public void setVersionSelected(int versionSelected){
    this.versionSelected = versionSelected;
  }

  public int getVersionSelected(){
    return versionSelected;
  }

  public void setNodeFinal(int nodeFinal) {
    this.nodeReceiver = nodeFinal;
  }

  public void setNodeInicial(int nodeInicial) {
    this.nodeSender = nodeInicial;
  }

  public int getNodeReceiver() {
    return nodeReceiver;
  }

  public int getNodeSender() {
    return nodeSender;
  }

}
