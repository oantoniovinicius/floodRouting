package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import control.mainControl;
import javafx.scene.shape.Polyline;

public class Nodes {
  private final int id;
  mainControl mC = new mainControl();
  private ArrayList<Integer> nodeConnection = new ArrayList<>(); // Qual No esse Roteador CONECTA
  private ArrayList<Polyline> pathConnection = new ArrayList<>(); //Caminho em px desse roteador ate o No que ele conecta
  private ArrayList<Packets> packetsCreated = new ArrayList<>(); //Pacotes Gerados por esse Router
  boolean controlRecebimento = false;
  private Map<Integer, Boolean> routingTable = new HashMap<>();

  public Nodes(){
    this.id = 0;
  }

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
        case 1: //Version 1
          for(int i = 0; i < nodeConnection.size(); i++){
            Packets packet = new Packets(this.id, nodeConnection.get(i), pathConnection.get(i), mC.getRoot(), -1, mC);
            packetsCreated.add(packet);
            packet.start();
            mC.addPackets();
          }
          break;
        case 2:
          for (int i = 0; i < nodeConnection.size(); i++) {
          // Criando pacote e iniciando ele
            if (nodeConnection.get(i) != firstNode) { // Se o No nao foi de quem ele recebeu, ele envia
              Packets packet = new Packets(this.id, nodeConnection.get(i), pathConnection.get(i), mC.getRoot(), -1, mC);
              packetsCreated.add(packet);
              packet.start();
              mC.addPackets();
            }
          }
          break;
        case 3:
          if (TTL != 0) { // Verifica se o TTL esta zero, caso sim, nao reenvia mais o pacote
            for (int i = 0; i < nodeConnection.size(); i++) { // Encaminha para todos execto para quem enviou e verifica o TTL
            // Criando pacote e iniciando ele
              if (nodeConnection.get(i) != firstNode) { // Se o No nao foi de quem ele recebeu, ele envia
                // Passa o TTL-1, subtraindo 1 pulo do pacote
                Packets Pacote = new Packets(this.id, nodeConnection.get(i), pathConnection.get(i), mC.getRoot(), TTL - 1, mC);
                packetsCreated.add(Pacote);
                Pacote.start();
                mC.addPackets();
              }
            }
          }
          break;

        case 4:
          if (TTL != 0) { // Verifica se o TTL esta zero, caso sim, nao reenvia mais o pacote
            for (int i = 0; i < nodeConnection.size(); i++) {
              int targetNode = nodeConnection.get(i);
              if (targetNode != firstNode && !routingTable.getOrDefault(targetNode, false)) { 
                // Se o No nao foi de quem ele recebeu e não está marcado como recebeu
                // Passa o TTL-1, subtraindo 1 pulo do pacote
                Packets packet = new Packets(this.id, targetNode, pathConnection.get(i), mC.getRoot(), TTL - 1, mC);
                packetsCreated.add(packet);
                packet.start();
                mC.addPackets();
                routingTable.put(targetNode, true); // Marca como recebeu
              }
            }
          }
          break;
        default:
            break;
      }
    } else {
      if(!controlRecebimento){
        System.out.println("Roteador [ "+ id + " ] recebeu o Pacote");
        controlRecebimento = !controlRecebimento;
        mC.packetReceived(this.id);
        mC.setReceived(true);
      }
    }
  }

  public void resetRoutingTable() {
    routingTable.clear();
  }

  public void listConnections() {
    System.out.println("Roteador [ " + id + " ] se conecta com:");
    for (int i = 0; i < nodeConnection.size(); i++) {
      System.out.print("Roteador ");
      System.out.println(nodeConnection.get(i));
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
