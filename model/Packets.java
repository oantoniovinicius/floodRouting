package model;

import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;

import javafx.util.Duration;
import java.util.Random;

import control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Path;

public class Packets extends Thread{
  private int TTL; // numero de hops
  private int nodeSender; //No pai
  private int nodeReceiver; // pra quem enviou
  private Polyline pathToGo; // Pixels para percorrer
  private ImageView packet; // Imagem do pacote
  private Pane Root; // Adicione uma referência para o grupo raiz
  mainControl mC = new mainControl(); // Instanciando e Criando o Controller
  boolean controlFinished = true;
  PathTransition pathTransition = new PathTransition();
  private boolean invertRoute = false;


  public Packets(int sender, int receiver, Polyline path, Pane root, int ttl, mainControl control) {
    nodeSender = sender;
    nodeReceiver = receiver;
    pathToGo = path;
    TTL = ttl;
    mC = control;

    Root = root; // Salve a referência para o grupo raiz
    packet = new ImageView(new Image("./assets/pacote.png"));
    Root.getChildren().add(packet);
    if(nodeSender > nodeReceiver){
      invertRoute = true;
    }
  }

  public void sendPacket() {
    ObservableList<Double> points = pathToGo.getPoints();
    Path path = new Path();
  
    if(invertRoute){ //Verifica para ver se os pontos do caminho estao corretos
      path.getElements().add(new MoveTo(points.get(2), points.get(3)));
      path.getElements().add(new LineTo(points.get(0), points.get(1)));
    } else{
      path.getElements().add(new MoveTo(points.get(0), points.get(1)));
      path.getElements().add(new LineTo(points.get(2), points.get(3)));
    }

    Platform.runLater(() -> {
      pathTransition.setNode(packet);
      pathTransition.setPath(path);
      pathTransition.setDuration(Duration.millis(randomTimer())); // Define a duração da animação (em segundos)
      pathTransition.play();
      pathTransition.setOnFinished(event -> {
      if(controlFinished){
        packet.setVisible(false);
        mC.getNodes().get(nodeReceiver-1).sendPackets(TTL, nodeSender); // Roteador Recebeu o No
      }
    });});
  }

  @Override
  public void run() {
    sendPacket();
  }

  public int randomTimer() {
    Random random = new Random();
    int valorAleatorio = random.nextInt(1301) + 1700;
    return valorAleatorio;
  }

  public void breakAnimation(){
    pathTransition.stop();
    packet.setVisible(false);
    packet.setDisable(true);
  }

  //Getter and setters
  public void setControlFinished(boolean controlFinished) {
    this.controlFinished = controlFinished;
  }

  public boolean getControlFinished() {
    return this.controlFinished;
  }

  public int getNodeReceiver() {
    return nodeReceiver;
  }

  public int getNodeSender() {
    return nodeSender;
  }

  public Polyline getPathToGo() {
    return pathToGo;
  }

  public int getTTL() {
    return TTL;
  }

}
