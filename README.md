# Flood Routing
Aplicação realizada para fins acadêmicos no estudo de redes de computadores e algoritmos de roteamentos na camada de rede para a matéria de Redes de Computadores II.
é usado em redes para enviar pacotes de dados de um nó para todos os outros nós, sem controle sobre a rota. 
Ele retransmite o pacote a todos os vizinhos até atingir o destino. O problema principal é que ele gera redundância, sobrecarregando a rede com pacotes duplicados, o que pode causar congestionamento e desperdício de recursos.

## Sobre as soluções:
A aplicação conta com 4 versões do algoritmo de inundação, proposta em sala de aula:
- Versão 1 implementa o algoritmo: "Cada pacote que chega em um
roteador é enviado para TODAS as interfaces de rede deste roteador”
- Versão 2 implementa o algoritmo: "Cada pacote que chega em um
roteador é enviado para todas as interfaces de rede deste roteador,
EXCETO por aquela pela qual ele chegou”
- Versão 3 implementa o algoritmo: "Cada pacote que chega em um
roteador é enviado para todas as interfaces de rede deste roteador,
EXCETO por aquela pela qual ele chegou. E cada roteador VERIFICA a
informação de TTL para decidir se o pacote continua a circular na rede”
- Versão 4 implementa a Versão 3 mais uma otimização proposta por mim. A lógica dessa versão está documentada no código e também disponível ao apertar o botão 'About' no menu principal.
Basicamente a Versão 4 mantém a Versão 3 e a melhora ao criar uma tabela de roteamento dinâmica, impedindo certas duplicações de acontecerem e otimizando a criação e envio de pacotes na rede.

## Funcionalidades:

- Simulação de roteamento e envio de pacotes na rede, definida por um backbone proposto como parte do projeto;
- Escolha de Roteador transmissor e receptor;
- Interface gráfica inspirada em um mundo medieval (Dark Souls);

## Aplicação em execução:
### Menu principal:
![Layout](https://github.com/oantoniovinicius/floodRouting/blob/main/imgs/menuFlood.gif)

### Versão 1:
![Layout](https://github.com/oantoniovinicius/floodRouting/blob/main/imgs/version1.gif)

### Versão 2:
![Layout](https://github.com/oantoniovinicius/floodRouting/blob/main/imgs/version2.gif)

### Versão 3:
![Layout](https://github.com/oantoniovinicius/floodRouting/blob/main/imgs/version3.gif)

### Versão 4:
![Layout](https://github.com/oantoniovinicius/floodRouting/blob/main/imgs/version4.gif)

## Autores:
- [@oantoniovinicius](https://www.github.com/oantoniovinicius)
