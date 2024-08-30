package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import control.mainControl;
import javafx.scene.shape.Polyline;

public class Nodes {
  private final int id;
  mainControl mC = new mainControl();
  private ArrayList<Integer> nodeConnection = new ArrayList<>(); // conexoes do roteador
  private ArrayList<Polyline> pathConnection = new ArrayList<>(); // caminho em px desse roteador ate o outro roteador que ele conecta
  private ArrayList<Packets> packetsCreated = new ArrayList<>(); // pacotes gerados por esse roteador
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
        case 1: // opcao 1
          for(int i = 0; i < nodeConnection.size(); i++){
            Packets packet = new Packets(this.id, nodeConnection.get(i), pathConnection.get(i), mC.getRoot(), -1, mC);
            packetsCreated.add(packet);
            packet.start();
            mC.addPackets();
          }
          break;
        case 2: // opcao 2
          for (int i = 0; i < nodeConnection.size(); i++) {
            // criacao do pacote
            if (nodeConnection.get(i) != firstNode) { // envia para todos EXCETO o roteador que encaminhou o pacote para ele
              Packets packet = new Packets(this.id, nodeConnection.get(i), pathConnection.get(i), mC.getRoot(), -1, mC);
              packetsCreated.add(packet);
              packet.start();
              mC.addPackets();
            }
          }
          break;
        case 3: // opcao 3
          if (TTL != 0) { // se TTL = 0 nao envia mais o pacote
            for (int i = 0; i < nodeConnection.size(); i++) { // encaminha para todos EXCETO quem enviou e verifica o TTL
            // criacao do pacote
              if (nodeConnection.get(i) != firstNode) {// envia para todos EXCETO o roteador que encaminhou o pacote para ele
                // TTL-1 = subtrai 1 pulo do pacote
                Packets Pacote = new Packets(this.id, nodeConnection.get(i), pathConnection.get(i), mC.getRoot(), TTL - 1, mC);
                packetsCreated.add(Pacote);
                Pacote.start();
                mC.addPackets();
              }
            }
          }
          break;

        /* Version 4:
        Essa implementacao oferece uma melhoria em relacao ao algoritmo 3 ao implementar uma “tabela de roteamento dinamica”. 
        Ou seja, ao inves de enviar pacotes para todos os roteadores conectados a todo instante, 
        ele mantém uma tabela com informacoes sobre quais roteadores ja receberam pacotes com sucesso, 
        ajudando a evitar o reenvio desnecessario de pacotes para os mesmos roteadores.

        Vantagem: 
        1. Reducao de Trafego: Evita o reenvio de pacotes para nos que ja receberam a mensagem, reduzindo o trafego total na rede.
        2. Eficiencia: Melhora a eficiencia do algoritmo ao minimizar o numero de pacotes gerados assim como o numero de threads criadas.

        Funcionamento:
        1. Tabela de roteamento dinamico: 
        Cada roteador mantem uma tabela do tipo (Map<Integer, Boolean> routingTable)
        que rastreia se um roteador conectado ja recebeu o pacote.
        Antes de enviar um pacote, o roteador verifica a tabela. Se o roteador de destino ja estiver na tabela como "recebeu", 
        o pacote nao eh enviado para esse roteador.
        Caso contrario, o roteador envia o pacote e marca o roteador na tabela como "recebeu".

        2. Atualizacao Dinamica da Tabela:
        Quando o roteador recebe a confirmacao de que um pacote foi entregue com sucesso, 
        ele pode propagar essa informação aos seus vizinhos, permitindo que os roteadores atualizem suas tabelas de roteamento dinamicamente.
        
        Implementacao:
        Antes de enviar um pacote para um roteador conectado a ele, o roteador verifica se esse roteador ja recebeu um pacote.
        Para isso, ele verifica a "routingTable".
        Se o roteador nao estiver marcado ou se nao tiver informacao sobre ele (nao recebeu um pacote), o roteador envia um pacote
        e o roteador sera marcado como "recebeu"  */
        case 4:
          if (TTL != 0) { // se TTL = 0 nao envia mais o pacote
            for (int i = 0; i < nodeConnection.size(); i++) {
              int targetNode = nodeConnection.get(i);
              //verifica se o roteador de destino ja recebeu o pacote e se o pacote deve ser enviado de volta para o roteador que o enviou
              if (targetNode != firstNode && !routingTable.getOrDefault(targetNode, false)) { 
                // TTL-1 = subtrai 1 pulo do pacote
                Packets packet = new Packets(this.id, targetNode, pathConnection.get(i), mC.getRoot(), TTL - 1, mC);
                packetsCreated.add(packet);
                packet.start();
                mC.addPackets();
                routingTable.put(targetNode, true); //marca como recebeu
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
