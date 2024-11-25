# Project P25

## Introdução
Este repositório tem por objetivo armazenar o código referente a uma aplicação FulkStack, a aplicação é composta pelo Front-end escrito em React, BFF (Backend For Front-end) escrita em Go e uma API em Java/Spring para comunicação com o Banco de Dados MySQL.

Além do código da aplicação em si, armaneza também arquivos de configuração dos Helm Charts, manifestos Kubernetes, Dockerfiles, docker-compose.yml para ambiente de desenvolvimento e código referente a pipeline de CI (GitHub Actions).

Esta aplicação teve deploy em ambiente de produção feito através de Terraform com infraestrutura na Azure e utiliza ArgoCD para GitOps. O código Terraform referente ao deploy encontra-se no repositório [th1alexandre/terraform-azurerm-aks](https://github.com/th1alexandre/terraform-azurerm-aks).

## Ferramentas Utilizadas
#### Cloud Providers
- AWS
  - Relational Database Server (RDS)
- Azure
  - Virtual Network, Subnets e Security Groups
  - Azure Kubernetes Service (AKS)
  - Azure Public IP
  - Resource Groups
  - Storage Account e Container

#### Monitoramento e Observabilidade
- Prometheus
- Grafana
- Alertmanager

#### CI/CD e Automação IaC
- GitHub Actions
- ArgoCD
  - Add-on [ArgoCD Image Updater](https://argocd-image-updater.readthedocs.io/en/stable/)
- Terraform
  - [AWS provider](https://registry.terraform.io/providers/hashicorp/aws/latest)
  - [Azure provider](https://registry.terraform.io/providers/hashicorp/azurerm/latest)
  - [Azure API provider](https://registry.terraform.io/providers/Azure/azapi/latest)
  - [Kubernetes provider](https://registry.terraform.io/providers/hashicorp/kubernetes/latest)
  - [Helm provider](https://registry.terraform.io/providers/hashicorp/helm/latest)
  - [CloudFlare provider](https://registry.terraform.io/providers/cloudflare/cloudflare/latest)
  - [HashiCorp Local provider](https://registry.terraform.io/providers/hashicorp/local/latest)

#### Domínio, Segurança e Certificados SSL/TLS
- CloudFlare
  - Compra de domínios
  - Gerenciamento de DNS Records
- Cert-Manager
  - [CloudFlare ACME Issuer](https://cert-manager.io/docs/configuration/acme/dns01/cloudflare/)

#### Desenvolvimento e Aplicações
- Docker & Docker Compose
- [DockerHub](https://hub.docker.com/u/th1alexandre) (armazenamento de imagens)
- Kubernetes
- Nginx ([Ingress-nginx](https://github.com/kubernetes/ingress-nginx))
- Java/Spring
- Go (Lang)
- React

#### Banco de Dados
- MySQL
  - [AWS RDS MySQL](https://aws.amazon.com/rds/mysql/)
  - [Dockerhub Image](https://hub.docker.com/_/mysql/)

#### Outros
- [Kubectl](https://kubernetes.io/docs/reference/kubectl/)
- [Helm](https://helm.sh/)
- [Let's Encrypt](https://letsencrypt.org/)
- Linux (Ubuntu/Debian)
- Shell Bash e [Zsh](https://ohmyz.sh/)

## Domínios da aplicação disponíveis
#### **ATENÇÃO**:
- A infraestrutura Azure está hospedada na região da Índia Central por ser a região de menor custo geral, além disso o tipo de VM utilizado nos nodes possui poucos recursos (Standard_B2ps_v2 e Standard_B4ps_v2) visto que o ambiente está sendo mantido com recursos próprios. Por conta da infraestrutura estar em uma localização tão distante é esperado uma alta latência ao acessar os domínios da aplicação, peço que seja paciente e aguarde as aplicações carregarem, em especial o front-end que pode demorar algum tempo até exibir os dados que obtém do BFF/Backend/MySQL (normalmente leva apenas alguns segundos, caso o front-end demore a exibir os dados do banco de dados tente atualizar a página e aguardar mais alguns segundos)
- O banco de dados AWS RDS MySQL está na região sa-east-1 (São Paulo) com configurações free-tier da AWS.
- Acesse **TODOS** os domínios pela porta 443, utilizando HTTPS, caso tente outras portas/HTTP irá receber timeout da conexão. Todas as aplicações possuem certificado SSL/TLS válido emitido através do cert-manager em conjunto com Let's Encrypt para o domínio hospedado na CloudFlare.<br>

- https://frontend.p25.th1alexandre.uk
  - Aplicação React
- https://bff.p25.th1alexandre.uk
  - API Go BFF
- https://backend.p25.th1alexandre.uk
  - API Java Backend
- https://mysql.p25.th1alexandre.uk
  - Banco de dados AWS RDS MySQL (Acesso restrito por IP)
- https://argocd.p25.th1alexandre.uk
  - Solicite o usuário/senha caso seja um avaliador.
- https://grafana.p25.th1alexandre.uk
  - Solicite o usuário/senha caso seja um avaliador.
- https://prometheus.p25.th1alexandre.uk
- https://alertmanager.p25.th1alexandre.uk

## Front-end (Aplicação React)
O front-end é escrito em React, encontra-se dentro da pasta `frontend/` na raiz do repositório.<br>
Se comunica com o BFF para obtenção dos dados que serão exibidos ao usuário e possui a seguinte estrutura:
```plaintext
├── k8s/                     # Manifestos Kubernetes e arquivos Helm
│   ├── helm/frontend/       # Helm chart para a aplicação front-end (utilizado pelo ArgoCD)
├── public/                  # Imagens, arquivos HTML, etc.
├── src/                     # Código fonte da aplicação
├── .env.dev/                # Template com variáveis que a aplicação necessita para funcionar
├── Dockerfile               # Dockerfile da aplicação, com multi-stage build e usuário non-root para segurança

# Contém outros arquivos com nomes auto-descritivos
```

## BFF (Back-End For Front-End)
O BFF é uma aplicação intermediária escrita em Go que processa e fornece dados para o front-end, os dados são obtidos realizando a comunicação com o Backend (API Java).<br>
O código encontra-se na pasta `services/bff/` a partir da raiz do repositório e possui a seguinte estrutura:
```plaintext
├── k8s/                     # Manifestos Kubernetes
│   ├── helm/bff/            # Helm chart para a aplicação bff (utilizado pelo ArgoCD)
├── src/                     # Código fonte da aplicação
├── .env.dev/                # Template com variáveis que a aplicação necessita para funcionar
├── Dockerfile               # Dockerfile da aplicação, com multi-stage build e usuário non-root para segurança

# Contém outros arquivos com nomes auto-descritivos
```

## Backend (Java/Spring API)
O backend é a API principal da aplicação, escrita em Java com Spring, responsável pela comunicação com o banco de dados MySQL.<br>
Encontra-se na pasta api/ na raiz do repositório e possui a seguinte estrutura:
```
├── .mvn/                    # Configurações Maven
├── k8s/                     # Manifestos Kubernetes
│   ├── helm/backend/        # Helm chart para a aplicação backend (utilizado pelo ArgoCD)
├── src/                     # Código fonte da aplicação
├── .env.dev/                # Template com variáveis que a aplicação necessita para funcionar
├── Dockerfile               # Dockerfile da aplicação, com multi-stage build e usuário non-root para segurança
├── pom.xml                  # Configuração do Maven e dependências

# Contém outros arquivos com nomes auto-descritivos
```

## Arquitetura (Infraestrutura, DevOps e Observabilidade)
#### CI/CD e IaC
##### CI
- O ambiente de CI utiliza o GitHub Actions, os arquivos de configuração estão dentro da pasta `.github/workflows/` a partir da raiz do repositório. O CI é responsável por, primeiro, atualizar a tag `latest` a cada push de novo commit na branch `master` para apontar para os novos commits, essa tarefa é realizada pelo workflow `update-release.yaml`, ao final da execução desse workflow o mesmo realiza um trigger para o workflow seguinte `build-dockerfile.yaml`, esse workflow é responsável por buildar o Dockerfile de cada uma das aplicações e posteriormente fazer o push das imagens para um repositório [Dockerhub](https://hub.docker.com/u/th1alexandre).
##### CD
- O ambiente de CD utiliza ArgoCD onde o mesmo foi configurado com chave SSH para acesso a este repositório de forma privada, o ArgoCD observa o Helm Chart das aplicações nas pastas `frontend/k8s/helm/frontend`, `services/bff/k8s/helm/bff`, `services/api/k8s/helm/backend` e realiza a sincronização do ambiente de produção conforme modificações no repositório/chart.
  - Foi adicionado ao ArgoCD um plugin [ArgoCD Image Updater](https://argocd-image-updater.readthedocs.io/en/stable/), esse plugin é responsável por observar quando a imagem das aplicações contendo a tag `latest` é atualiza com um novo build, após a identificação esse plugin realiza o re-deploy dos PODs (trigger de novo deployment/replicaset) no cluster Kubernetes, mantendo assim a aplicação sempre atualiza no ambiente de produção (a checagem do plugin por atualizações na tag da imagem é feita a cada 2 minutos, a atualização das aplicações pode levar alguns minutos a mais após a identificação de nova imagem).
  - ATENÇÃO: as imagens Docker estão hospedadas em repositórios públicos no Dockerhub que possui limite de pull de imagens, sendo 100 Pulls a cada 6 horas.
##### IaC (Infraestrutura como Código)
- Para IaC foi utilizado Terraform, o código está em outro repositório aqui no GitHub ([th1alexandre/terraform-azurerm-aks](https://github.com/th1alexandre/terraform-azurerm-aks)), o Terraform foi responsável pelo Deploy de todos os recursos da Azure necessários para o funcionamento da aplicação, os recursos Azure utilizados encontram-se na sessão de ferramentas utilizadas, após o deploy dos recursos azure (principalmente o cluster AKS) alguns providers adicionais realizam configurações prévias no cluster como por exemplo instalação do [cert-manager](https://cert-manager.io/) e [Ingress-nginx](https://github.com/kubernetes/ingress-nginx), após o ingress-nginx estar disponível com um IP associado, o provider da cloudflare realiza a adição do IP a um DNS A Record no domínio que foi comprado para disponibilizar a aplicação (th1alexandre.uk)
##### Observabilidade e Monitoramento
- **Prometheus**: Responsável pela coleta de métricas de todos os componentes do cluster Kubernetes, incluindo pods, serviços, e métricas personalizadas das aplicações. Ele foi instalado no cluster utilizando um Helm Chart oficial e configurado para armazenar os dados de métricas em um volume persistente (PVC). O Prometheus também serve como base para os alertas configurados no Alertmanager.
- **Grafana**: Utilizado para visualização e análise das métricas coletadas pelo Prometheus. Dashboards personalizados foram criados para monitorar o desempenho e a saúde das aplicações, bem como o estado geral do cluster Kubernetes. O Grafana foi configurado com autenticação para garantir acesso seguro e está acessível por meio de um Ingress configurado no cluster.
- **Alertmanager**: Integrado ao Prometheus, o Alertmanager é responsável pelo gerenciamento de alertas baseados em condições definidas nas métricas do Prometheus. Ele envia notificações para canais configurados (como e-mail ou Slack) sempre que eventos críticos ou condições anormais são detectados no cluster ou nas aplicações.
- **Monitoramento do Cluster**: Além das métricas das aplicações, Prometheus e Grafana também monitoram a infraestrutura subjacente, como o uso de CPU, memória e disco nos nodes do Kubernetes. Dashboards dedicados foram criados para auxiliar na identificação de problemas de capacidade ou desempenho.
- Todos os componentes (Prometheus, Grafana e Alertmanager) foram instalados utilizando Helm Charts oficiais disponíveis no seguinte [repositório Helm](https://github.com/prometheus-community/helm-charts/tree/main/charts/kube-prometheus-stack).

## Deploy no Ambiente de Desenvolvimento (Local)
### Pré-requisitos
- Docker e Docker Compose instalados na máquina local.
- Acesso ao repositório do projeto.
### Passo a Passo
1. **Clonar o Repositório**:
  Clone o repositório do projeto em sua máquina local:
  ```bash
  git clone <URL_DO_REPOSITORIO>
  cd <NOME_DO_REPOSITORIO>
  ```

2. **Configurar Variáveis de Ambiente**:
  Copie o conteúdo do template de variáveis .env.dev (na raiz do repositório) para um novo arquivo .env (na raiz do repositório):
  ```bash
  cp .env.dev .env
  
  # Caso necessário, ajuste os valores das variáveis no arquivo .env
  ```

3. **Iniciar o Ambiente**:
  Utilize o Docker Compose para iniciar o ambiente local:
  ```bash
  docker-compose build && docker compose up

  # Caso desejar utilize a flag -d (docker compose up -d) para manter executando em background
  ```
Esse comando irá:
- Buildar o Dockerfile de cada aplicação
- Subir todas as aplicações e serviços necessários (frontend, BFF, backend e banco de dados).
- Criar e popular automaticamente o banco de dados caso ele ainda não exista, utilizando o script init.sql na raiz do projeto.

4. **Verificar os Serviços**:
   Confirme se os containers estão em execução:
  ```bash
  docker ps
  ```
  Você deve ver todos os serviços listados como "running".

5. **Acessar as Aplicações**:
- Frontend: http://localhost:3000
- BFF: http://localhost:8085
- Backend (API): http://localhost:8080
- MySQL: http://localhost:3306

6. **Finalizar o Ambiente**:
  Para encerrar todos os serviços, execute:
  ```bash
  docker compose down
  ```

7. **Excluir o banco de dados**:
  Caso deseje "resetar" o banco de dados, execute o comando abaixo:
  ```bash
  docker volume rm project-p25_mysql_data
  ```
  Após isso, caso suba novamente a aplicação, o banco de dados será iniciado conforme definido no script `init.sql`.

## Pontos de melhoria futura:
- O banco de dados de produção (AWS RDS MySQL) foi criado manualmente através do console da AWS, o ideal seria converter sua inicialização em um script Terraform.
- A tag utilizada pela aplicação tanto no repositório quanto na imagem dockerhub é `latest`, sendo o mais adequado migrar para a abordagem de [semantic versioning](https://semver.org/).
- Testes unitários deveriam ser escritos para cada uma das aplicações para garantir que novas modificações não quebrem o funcionamento da lógica anterior, além de garantir que as regras de negócio estão sendo seguidas corretamente, fator esse de **EXTREMA** importância para uma aplicação financeira.
- Configurar uma CDN (Content Delivery Network) para cache e redução da latência da aplicação ao usuário final, opção recomendada sendo o próprio CloudFlare ou utilizar AWS CloudFront.
- Instalar [HashiCorp Vault](https://www.hashicorp.com/products/vault) para gerenciar secrets, tokens, API keys entre outros, realizando a rotação segura após um tempo pré-determinado garantindo segurança e automação da rotação dos segredos.
- Deve ser configurado snapshots (backups) automatizados do banco de dados AWS RDS MySQL para garantir um bom [RPO](https://aws.amazon.com/blogs/mt/establishing-rpo-and-rto-targets-for-cloud-applications/) (Recovery Point Objective)
- O cluster Kubernetes possui um node pool responsável pelo deploy de toda a infraestrutura da aplicação, esse node pool possui apenas 1 VM em 1 zona de disponibilidade aleatória, apesar do Helm chart realizar o deploy de duas réplicas (PODs), caso a zona venha a falhar, toda a infraestrutura ficará fora do ar. O mais adequado para garantir a alta disponibilidade do serviço (Muito importante para aplicações financeiras) seria realizar o deploy do node pool com 3 VMs, sendo uma em cada zona de disponibilidade e configurar os Helm Charts para utilizar 3 réplicas, além disso configurar regras de node selector, affinity, taint, tolerations, etc, para garantir que cada réplica esteja em uma VM (zona) diferente, garantindo assim uma confiabilidade muito maior do sistema. Caso necessite ainda mais disponibilidade, pode-se configurar uma região adicional em stand-by para caso de queda total da região principal garantindo um bom [RTO](https://aws.amazon.com/blogs/mt/establishing-rpo-and-rto-targets-for-cloud-applications/) (Recovery Time Objective).

## Modificações realizadas no código das aplicações:
- **Front-end**: Alterado código fonte para definir a URL do BFF através de variáveis de ambiente, anteriormente o código com o endpoint da API estático não permitia um deploy funcional de ambos ambientes de produção e local com docker compose ao mesmo tempo.
- **Java Backend API**: no arquivo `services/api/src/main/resources/application.yaml` na linha 14 foi alterado parâmetro `ddl-auto` pois o mesmo estava impedindo a aplicação Java de executar por conta de diferenças no schema da aplicação e `init.sql`. No entando essa medida não é adequada, sendo o correto conferir com a equipe responsável pelo banco de dados/desenvolvimento para corrigir o código da API ou o arquivo SQL, retornando o bloqueio ao executar a aplicação caso haja diferenças nos schemas com o parametro ddl-auto.

## Modificações e configurações manuais realizadas:
- Deploy do banco AWS RDS MySQL
- Instalação do ArgoCD e ArgoCD Image Updater utilizando Helm no terminal.
- Criação das aplicações através da UI do ArgoCD
- Gerar chaves SSH e configuração no repositório GitHub e ArgoCD
- Criação de manifestos para ingress.yaml para disponibilizar ArgoCD, Grafana, Prometheus e Alertmanager através do domínio com certificado SSL/TLS.
- Criação de manifestos para adicionar secrets contendo variáveis de ambiente sensívels como senhas de banco de dados, tokens JWT e CLIENT_SECRET
  - conteúdo transformado em base64 utilizando shell/linux.
- Geração de API key CloudFlare para usar o provider no Terraform
- Configurações extras de IP nos Security Groups (AWS para RDS MySQL e Azure para load balancer ingress-nginx)
- Senha padrão Grafana obtida através do secret da aplicação via kubectl e alterada na UI.
