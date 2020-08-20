# MicroservicesDecomposer

A proposta da abordagem para decompor sistemas monolíticos em microsserviços tem o seguinte fluxo:

1- Execução da ferramenta IdentificadorEstatico, para obtenção do dados estaticamente.

2- Execução de todos os fluxos do sistema a ser analisado, configurando o projeto para ser AspectJ Project e adicionando a classe ProfilerAspect.aj

3- Execução da ferramenta sr_m_ms, onde deverá ser realiza o upload dos dados extraídos do monolítico.


### A pasta Ferramenta contém os itens:

- Projeto IdentificadorEstatico

Projeto criado pelo Plug-in da IDE do eclipse, que tem o objetivo de extrair informações das classes de um sistema monolítico.

- Classe ProfilerAspect.aj 

Classe implementada com Aspect, que tem o objetivo de extrair dados em tempo de execução de um sistema monolítico. 

- Projeto sr_m_ms

O projeto que tem a responsabilidade de agrupar e compilar os dados obtidos através das ferramentas de análise estática e dinâmica, além de recomendar a decomposição de um sistema monolítico para a arquitetura de microsserviços.



### A pasta AvaliacaoVEM contém os itens:

- SistemaMonolitco

Contém o projeto do sistema monolítico utilizado para análise da ferramenta.

- ExtracoesDadosMonolitico

Contém os arquivos obtidos através da análise estática e dinâmica.

- Ajustes

Contém o arquivo que realiza uma instrução sobre o grafo gerado do agrupamento dos dados das análises.

- Imagens

Contém as imagens de resultados obtidos em cada etapa do processo de recomendação de microsserviços.



