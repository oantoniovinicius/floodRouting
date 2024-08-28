package model;

import java.util.ArrayList;

import control.mainControl;
import javafx.scene.shape.Polyline;

public class Nodes {
  private final int id;
  mainControl mC = new mainControl();
  private ArrayList<Integer> nodeConnection = new ArrayList<>(); // Qual No esse Roteador CONECTA
  private ArrayList<Polyline> pathConnection = new ArrayList<>(); //Caminho em px desse roteador ate o No que ele conecta
  private ArrayList<Packets> packetsCreated = new ArrayList<>(); //Pacotes Gerados por esse Router

  public Nodes(int id){
    this.id = id;
  }

  public int getId(){
    return id;
  }

  public void setController(mainControl mainController){
    this.mC = mainController;
  }

  public void addConnection(int connected, Polyline route){
    nodeConnection.add(connected);
    pathConnection.add(route);
  }

  public void sendPackets(int TTL, int firstNode){
    if(mC.getNodeReceiver() != this.id){
      switch (mC.getVersionSelected()) {
        case 0://Version 1
          for(int i = 0; i < nodeConnection.size(); i++){
            Packets packet = new Packets(this.id, nodeConnection.get(i), pathConnection.get(i), mC.getRoot(), -1, mC);
            packetsCreated.add(packet);
            packet.start();
            mC.addPackets();
          }
        break;
      
        default:
            break;
      }
    }
  }

  public void listConnections() {
    System.out.println("Roteador ID: [ " + id + " ] Se Conecta com:");
    for (int i = 0; i < nodeConnection.size(); i++) {
      System.out.println("Conexoes: " + nodeConnection.get(i));
    }
    System.out.println("");
  }

  public void stopPackages(){
    for(Packets Pack: packetsCreated){
      Pack.setControlFinished(false);
      Pack.breakAnimation();
      Pack.interrupt();
    }
    packetsCreated.clear();
  }
}
