# Análise do Projeto JusCom e Próximos Passos

Este documento resume o trabalho de desenvolvimento realizado, a metodologia utilizada e os próximos passos recomendados para o projeto JusCom.

---

## O Que Foi Feito (Até 24 de Outubro)

*   **Fundação e Backend:** Migração bem-sucedida de um protótipo estático para um aplicativo dinâmico, totalmente conectado ao Firebase (Auth e Firestore).
*   **Ciclo de Vida do Usuário:** Implementação completa do fluxo de registro, login, recuperação de senha e perfil de usuário dinâmico.
*   **Conteúdo Dinâmico:** As telas `Home`, `RoomList`, `Help` e `Settings` foram refatoradas para carregar dados diretamente do Firestore, eliminando conteúdo estático.
*   **Funcionalidades Essenciais:**
    *   Os usuários podem criar novas salas de discussão (`CreateRoomActivity`).
    *   O sistema de Q&A (Perguntas e Respostas) foi iniciado, com a capacidade de listar e visualizar perguntas de uma sala (`QuestionListActivity` e `QuestionDetailActivity`).
    *   Os usuários podem postar novas perguntas em uma sala (`CreateQuestionActivity`).
*   **Melhoria Arquitetural (MVVM):** A `HomeActivity` foi refatorada para usar o padrão MVVM, separando a lógica de UI da lógica de dados, resultando em um código mais limpo e robusto.

---

## Plano de Ação (Próximas 3 Semanas)

### **Semana 1: Conclusão do Q&A e Refatoração (24 de Outubro - 30 de Outubro)**

**Meta:** Finalizar o ciclo completo de interação no fórum e continuar a melhoria da arquitetura do código.

1.  **Implementar Votação nas Respostas (Phase 4 do Q&A):**
    *   **Tarefa:** Implementar a lógica de `Transaction` no `QuestionDetailViewModel` para garantir que o `voteCount` em uma resposta seja atualizado atomicamente.
    *   **Lógica:** Criar ou atualizar o documento na subcoleção `votes` e, na mesma operação, incrementar/decrementar o contador no documento da resposta.
    *   **Feedback Visual:** Atualizar a UI para refletir o novo `voteCount` e, opcionalmente, destacar os botões de voto para indicar a escolha do usuário.

2.  **Implementar o Envio de Respostas (Phase 4 do Q&A):**
    *   **Tarefa:** Adicionar um campo de texto e um botão "Enviar" na `QuestionDetailActivity`.
    *   **Lógica:** Criar a função no `ViewModel` para salvar um novo documento na coleção `answers`, associado ao `questionId`.
    *   **Atualização:** Fazer com que a lista de respostas seja atualizada automaticamente após o envio.

3.  **Refatorar `ProfileActivity` para MVVM:**
    *   **Tarefa:** Aplicar o mesmo padrão de refatoração da `HomeActivity`.
    *   **Ações:** Criar um `ProfileViewModel` para gerenciar a busca e a atualização dos dados do perfil do usuário, limpando a `ProfileActivity`.

### **Semana 2: Material de Estudo e UI/UX (31 de Outubro - 6 de Novembro)**

**Meta:** Implementar a seção de compartilhamento de materiais e refinar a experiência do usuário em todo o aplicativo.

1.  **Implementar `StudyMaterialActivity`:**
    *   **Definir Schema:** Criar uma nova coleção `study_materials` no Firestore. Documentos podem conter campos como `title`, `description`, `authorName`, `category` e `fileUrl` (para um link do Firebase Storage).
    *   **Lógica de Upload (MVP):** Inicialmente, podemos assumir que os arquivos PDF/DOCX são adicionados manualmente ao Firebase Storage e o link é colado no Firestore. A lógica de upload pelo app pode ser uma `Feature V2`.
    *   **Desenvolvimento da Tela:** Criar um `StudyMaterialViewModel` e refatorar a `StudyMaterialActivity` para listar os materiais usando um `RecyclerView`. Cada item deve permitir a abertura do link (`fileUrl`).

2.  **Refinar a Busca da Home:**
    *   **Tarefa:** Atualmente, o filtro de busca só funciona no `RoomListActivity`. Vamos fazê-lo funcionar na `HomeActivity` para a lista de salas em destaque.
    *   **Lógica:** A lógica de filtro no `RoomAdapter` já existe; precisamos apenas garantir que o `EditText` da `HomeActivity` esteja corretamente vinculado a ele.

3.  **Ajustes de UI/UX:**
    *   **Ícones e Cores:** Revisar a consistência de ícones, cores e espaçamentos em todas as telas implementadas.
    *   **Feedback de Carregamento:** Adicionar `ProgressBar`s em telas que carregam dados do Firestore (como `QuestionListActivity` e `QuestionDetailActivity`) para melhorar o feedback ao usuário.

### **Semana 3: Finalização e Testes (7 de Novembro - 13 de Novembro)**

**Meta:** Garantir que todas as funcionalidades estejam estáveis, a base de código limpa e o aplicativo pronto para um ciclo de feedback.

1.  **Refatorar `RoomListActivity` e `SettingsActivity` para MVVM:**
    *   **Tarefa:** Completar a migração para a arquitetura MVVM, aplicando o padrão às últimas Activities que ainda misturam lógica de UI e dados.
    *   **Ações:** Criar `RoomListViewModel` e `SettingsViewModel` e mover as chamadas ao Firestore para dentro deles.

2.  **Testes de Regressão e Fluxo Completo:**
    *   **Tarefa:** Realizar um teste completo de todos os fluxos de usuário implementados:
        *   Registro -> Login -> Logout.
        *   Navegar para uma sala -> Visualizar perguntas -> Criar uma pergunta.
        *   Visualizar uma pergunta -> Responder -> Votar em uma resposta.
        *   Editar perfil -> Trocar tema.
        *   Excluir conta.

3.  **Limpeza de Código:**
    *   **Tarefa:** Remover quaisquer arquivos obsoletos que possam ter sido esquecidos (como `QAActivity`, `QAAdapter`, etc., se ainda existirem).
    *   **Revisão:** Garantir que todos os `TODOs` deixados no código tenham sido resolvidos ou documentados como trabalho futuro.

---

Este plano de ação fornece um roteiro claro e incremental para as próximas semanas, focando em entregar valor a cada etapa e mantendo a alta qualidade da base de código.

Important note:
The project has these Firestore Database collections and subcollections:

answers- Main Collection. Fields:

authorId: string
authorName: string
body: string
questionId: string
timestamp: timestamp
voteCount: number

votes- Subcollection of Answers. Fields:

votetype: string (permanent "up" value)

help_items- Main Collection. Fields:
answer: string
order: number
question: string

questions- Main Collection. Fields:
answerCount: number
authorId: string
authorName: string
body: string
roomId: string
timestamp: timestamp
title: string
rooms- Main Collection. Fields:
category: string
description: string
name: string
subscribersCount: number
users- Main Collection. Fields:
email: string
institution: string
level: number
name: string
points: number
uf (Unidade Federativa): string
