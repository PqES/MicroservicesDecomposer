# MicroservicesDecomposer

### A pasta Ferramenta contém os itens:

- Projeto IdentificadorEstatico

Criado pelo Plug-in da IDE do eclipse, que tem o objetivo de extrair informações das classes de um sistema monolítico.

- Classe ProfilerAspect.aj 

Criado com Aspect, tem o objetivo de extrair dados em tempo de execução de um sistema monolítico. 

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
