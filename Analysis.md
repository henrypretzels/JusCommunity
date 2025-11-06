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

**Meta:** Finalizar o ciclo completo de interação no fórum e continuar a melhoria da arquitetura do código. **Status: Concluído.**

1.  **Implementar Votação nas Respostas (Phase 4 do Q&A):**
    *   **Status:** Concluído.
    *   **Tarefa:** Implementar a lógica de `Transaction` no `QuestionDetailViewModel` para garantir que o `voteCount` em uma resposta seja atualizado atomicamente.
    *   **Lógica:** Criar ou atualizar o documento na subcoleção `votes` e, na mesma operação, incrementar/decrementar o contador no documento da resposta.
    *   **Feedback Visual:** Atualizar a UI para refletir o novo `voteCount` e, opcionalmente, destacar os botões de voto para indicar a escolha do usuário.

2.  **Implementar o Envio de Respostas (Phase 4 do Q&A):**
    *   **Status:** Concluído.
    *   **Tarefa:** Adicionar um campo de texto e um botão "Enviar" na `QuestionDetailActivity`.
    *   **Lógica:** Criar a função no `ViewModel` para salvar um novo documento na coleção `answers`, associado ao `questionId`.
    *   **Atualização:** Fazer com que a lista de respostas seja atualizada automaticamente após o envio.

3.  **Refatorar `ProfileActivity` para MVVM:**
    *   **Status:** Concluído.
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
    *   **Ícones e Cores:** Revisar a consistência de ícones, cores e espaçamentos em todas as telas implementadas. **Nota:** O problema de contraste de cores no tema claro foi corrigido.
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
## UI/UX Audit (Sprint de Limpeza)

### **HomeActivity (`activity_home.xml`)**

*   **Análise:** A `HomeActivity` faz um excelente uso de atributos de tema (`?attr/...`), o que garante que a maioria dos componentes se adapte corretamente aos temas claro e escuro.
*   **Problema:** 
    *   Foi identificado um uso de cor estática no componente `descriptionTextView`, que utiliza `android:textColor="@color/medium_gray"`. Isso pode causar problemas de legibilidade no tema escuro.
    *   O `FloatingActionButton` (FAB) no tema claro tem um ícone (`tint`) e um fundo (`backgroundTint`) da mesma cor (`?attr/colorPrimary`), tornando o ícone invisível.
*   **Recomendação:** 
    *   Substituir a cor estática do `descriptionTextView` por `?attr/colorOnSurfaceVariant`.
    *   Corrigir o estilo `App.FloatingActionButton` no arquivo `themes.xml` para garantir que o ícone e o fundo tenham contraste.

#### Screenshots de Referência

**Tema Claro**
![Home - Light Theme](https://i.imgur.com/k9gVp3j.png)

**Tema Escuro**
![Home - Dark Theme](https://i.imgur.com/tY3F9sI.png)

*   **Análise das Screenshots:**
    *   **Tema Claro:** A screenshot confirma a auditoria. O texto de descrição (`descriptionTextView`) usa um cinza estático (`@color/medium_gray`) que, embora legível aqui, não se adapta. Mais criticamente, como apontado, o ícone do `FloatingActionButton` está invisível porque sua cor (`tint`) é a mesma do fundo do botão (`backgroundTint`), validando o problema no estilo `App.FloatingActionButton` do tema.
    *   **Tema Escuro:** A screenshot também valida a auditoria. O texto de descrição em cinza tem baixo contraste contra o fundo escuro. O FAB, no entanto, está correto, o que expõe a inconsistência de estilos entre os modos claro e escuro. A diferença de cores de fundo e dos itens de lista entre os dois temas confirma que a UI não é governada por um sistema de temas unificado, mas sim por uma mistura de cores estáticas e atributos de tema mal configurados.

### **ProfileActivity (`activity_profile.xml`)**

*   **Análise Geral:** Embora o layout XML em si não contenha cores estáticas, as screenshots revelam um problema de usabilidade crítico no tema claro.

*   **Análise das Screenshots:**
    *   **Problema Crítico (Tema Claro):** O texto dentro dos campos de input (`TextInputEditText`) está invisível. Isso ocorre porque o tema define a cor do texto para superfícies (`?attr/colorOnSurface`) como branco, mas o fundo desses campos de texto também é branco. O resultado é texto branco sobre fundo branco, tornando a tela inutilizável no modo claro.
    *   **Tema Escuro:** A tela funciona como esperado no modo escuro, pois o fundo dos campos de texto é escuro, contrastando com o texto branco.

*   **Recomendação:** Este problema é uma prova contundente de que a definição de cores no arquivo `themes.xml` está fundamentalmente quebrada. A correção não está no layout `activity_profile.xml`, mas sim na refatoração completa do sistema de temas, conforme detalhado na seção "Auditoria Crítica do Tema".

#### Screenshots de Referência

**Tema Claro**
![Profile - Light Theme](https://i.imgur.com/8QnC2pG.png)

**Tema Escuro**
![Profile - Dark Theme](https://i.imgur.com/S8t6bYp.png)


### **MainActivity (`activity_main.xml`)**

*   **Análise:** Trata-se de um layout padrão "Hello World". Não utiliza cores estáticas e está em conformidade com os temas.
*   **Problema:** Nenhum.
*   **Recomendação:** Nenhuma alteração necessária. Provavelmente é um arquivo de placeholder que não está em uso ativo no fluxo principal do aplicativo.

### **RoomDetailActivity (`activity_room_detail.xml`)**

*   **Análise Geral:** As screenshots e a auditoria do código revelam problemas significativos de consistência, contraste e hierarquia visual.

*   **Análise do Código (`activity_room_detail.xml`):** A auditoria do código confirma e detalha os problemas vistos nas screenshots.
    *   **Botões Inconsistentes (Causa Raiz):**
        *   O `subscribeButton` não tem estilo definido e, por padrão, usa `?attr/colorPrimary` como fundo e `?attr/colorOnPrimary` como texto, resultando no botão azul com texto branco.
        *   O `viewQaButton`, no entanto, é forçado a um estilo específico: é um `OutlinedButton` com o atributo `android:backgroundTint="?attr/colorOnPrimary"` (branco) e `android:textColor="?attr/colorPrimary"` (azul), criando o visual invertido. Esta é uma aplicação incorreta de `backgroundTint` e a causa da inconsistência.
    *   **Contraste do Texto:** O `roomDescription` usa `?attr/colorOnSurfaceVariant` para o texto sobre um fundo `?attr/colorSurface`. A falta de contraste é um resultado direto da má definição desses papéis de cor nos arquivos de tema.
    *   **Drawable Estático:** O `TextView` `roomCategory` usa `android:background="@drawable/category_badge"`, que provavelmente contém uma cor estática e não se adaptará ao tema.

*   **Recomendação:** Unificar os estilos de botão e refatorar o layout para depender exclusivamente de um sistema de temas corrigido. O `backgroundTint` do `viewQaButton` deve ser removido, e o estilo do `subscribeButton` deve ser definido explicitamente para corresponder ao seu propósito (seja um botão preenchido ou um `OutlinedButton`).

#### Screenshots de Referência

**Tema Claro**
![Room Detail - Light Theme](https://i.imgur.com/39Hw2g0.png)

**Tema Escuro**
![Room Detail - Dark Theme](https://i.imgur.com/kPZ3u7Q.png)

### **RoomListActivity (`activity_room_list.xml`)**

*   **Análise:** Esta tela apresenta um problema significativo de hardcoding de cores, o que impede a correta adaptação entre os temas claro e escuro.
*   **Problema:**
    *   O `ConstraintLayout` principal usa `android:background="@color/background_white"`.
    *   A `AppBarLayout` e a `Toolbar` usam `android:background="@color/primary_blue"`.
    *   O título da `Toolbar` usa `app:titleTextColor="@color/white"`.
*   **Recomendação:** Substituir todas as cores estáticas por atributos de tema para garantir a consistência visual:
    *   `@color/background_white` -> `?android:attr/colorBackground`
    *   `@color/primary_blue` -> `?attr/colorPrimary`
    *   `@color/white` -> `?attr/colorOnPrimary`

#### Screenshots de Referência

**Tema Claro**
![All Rooms - Light Theme](https://i.imgur.com/y1vKzF5.png)

**Tema Escuro**
![All Rooms - Dark Theme](https://i.imgur.com/i9Gg4gH.png)

*   **Análise das Screenshots:** As imagens são a prova definitiva de uma falha total na adaptação ao tema. As duas telas são completamente idênticas, o que confirma que o layout `activity_room_list.xml` (e seus itens de lista) está usando exclusivamente cores estáticas em vez de atributos de tema. No tema escuro, o fundo branco brilhante é especialmente gritante e quebra a imersão. Isso valida a auditoria de código e reforça a necessidade de uma refatoração completa do tema.

### **QuestionListActivity (`activity_question_list.xml` e `item_qa.xml`)**

*   **Análise:** O layout principal da tela (`activity_question_list.xml`) está bem configurado, mas o layout do item da lista (`item_qa.xml`) possui problemas graves de cores estáticas.
*   **Problema:**
    *   **`activity_question_list.xml`:** A `Toolbar` não possui a propriedade `android:background` definida.
    *   **`item_qa.xml`:** Quase todos os `TextView`s usam cores estáticas como `@color/text_primary`, `@color/text_secondary` e `@color/primary_blue`. Isso quebra completamente a adaptação ao tema escuro.
*   **Recomendação:**
    *   Em `activity_question_list.xml`, adicionar `android:background="?attr/colorPrimary"` à `Toolbar`.
    *   Em `item_qa.xml`, substituir todas as cores estáticas por atributos de tema:
        *   `@color/text_primary` -> `?attr/colorOnSurface`
        *   `@color/text_secondary` -> `?attr/colorOnSurfaceVariant`
        *   `@color/primary_blue` -> `?attr/colorPrimary`

#### Screenshots de Referência

**Tema Claro**
![Question List - Light Theme](https://i.imgur.com/gKHYP6C.png)

**Tema Escuro**
![Question List - Dark Theme](https://i.imgur.com/kQdF3zH.png)

*   **Análise das Screenshots:** As imagens são a prova definitiva de uma falha total na adaptação ao tema. As duas telas são praticamente idênticas, independentemente do modo. Isso confirma que os layouts (`activity_question_list.xml` e, especialmente, `item_qa.xml`) estão usando cores estáticas em vez de atributos de tema. O resultado é uma experiência de usuário inconsistente que ignora completamente as preferências do sistema.

### **QuestionDetailActivity (`activity_question_detail.xml` e `item_answer.xml`)**

*   **Análise Geral:** As screenshots e a auditoria do código confirmam múltiplos problemas de contraste, consistência e hierarquia visual, tornando esta uma das telas mais problemáticas.

*   **Análise das Screenshots:**
    *   **Problema Principal (Tema Claro):** As imagens provam o problema que você levantou: os cards de resposta têm um fundo bege/púrpura (`?attr/colorSurfaceContainer`) que oferece baixíssimo contraste com o fundo branco da tela, dificultando a distinção dos cards.
    *   **Toolbar Invisível (Tema Claro):** A `Toolbar` no tema claro não tem cor de fundo, misturando-se com o restante da tela e removendo a separação visual.
    *   **Área de Resposta Inconsistente:** A área de digitação de resposta na parte inferior tem um fundo azul sólido que destoa completamente do resto do tema claro e introduz outra tonalidade de azul inconsistente no tema escuro.
    *   **Texto de Baixo Contraste (Tema Escuro):** O texto do autor sob o título da pergunta ("por Nome para teste") é cinza claro sobre um fundo azul escuro, tornando-o difícil de ler e confirmando o uso de uma cor estática (`@color/text_secondary`).

*   **Recomendação:** A tela precisa de uma refatoração completa para usar atributos de tema corretamente. O problema do card de resposta (`item_answer.xml`) é o mais crítico e deve ser resolvido alterando seu `cardBackgroundColor` para `?attr/colorSurface`. A `Toolbar` precisa de um fundo, e a área de input de resposta deve ser reestilizada para se adequar ao tema.

#### Screenshots de Referência

**Tema Claro**
![Question Detail - Light Theme](https://i.imgur.com/P086i7C.png)

**Tema Escuro**
![Question Detail - Dark Theme](https://i.imgur.com/a5nC2P8.png)

### **Sidebar Header (`nav_header.xml`)**

*   **Análise:** O cabeçalho da barra de navegação lateral também apresenta problemas de cores estáticas.
*   **Problema:**
    *   O `LinearLayout` principal usa `android:background="@color/primary_blue"`.
    *   Os `TextView`s para o nome e email do usuário usam `android:textColor="@color/white"`.
*   **Recomendação:** Substituir as cores estáticas por atributos de tema:
    *   `@color/primary_blue` -> `?attr/colorPrimary`
    *   `@color/white` -> `?attr/colorOnPrimary`

#### Screenshots de Referência

**Tema Claro**
![Sidebar - Light Theme](https://i.imgur.com/u19A6Y8.png)

**Tema Escuro**
![Sidebar - Dark Theme](https://i.imgur.com/v2VvWf1.png)

### **SettingsActivity (`activity_settings.xml`)**

*   **Análise:** A tela de configurações utiliza atributos de tema para a `Toolbar` e para o botão de "Apagar Conta" (`?attr/colorError`), o que é uma ótima prática. Os demais componentes, como os `SwitchMaterial`, usam estilos padrão que se adaptam bem aos temas.
*   **Problema:** Nenhum problema crítico foi encontrado.
*   **Recomendação:** Nenhuma alteração é necessária. O layout está bem implementado para suportar os temas.

### **HelpActivity (`activity_help.xml` e `item_help.xml`)**

*   **Análise:** A tela de ajuda e seus itens de lista (`item_help.xml`) são um excelente exemplo de implementação de UI moderna e adaptável. Todos os componentes usam atributos de tema para suas cores e backgrounds.
*   **Problema:** Nenhum problema foi encontrado.
*   **Recomendação:** Nenhuma alteração é necessária.

### **CreateRoomActivity (`activity_create_room.xml`)**

*   **Análise:** O layout utiliza componentes Material Design (`TextInputLayout`, `MaterialButton`) e não possui cores estáticas, o que é excelente. O problema de contraste mencionado não está no layout em si, mas na definição de cores do tema do aplicativo.
*   **Problema:** O baixo contraste do botão "Criar" é causado por uma má configuração das cores `colorPrimary` e `colorOnPrimary` no arquivo de tema (provavelmente `themes.xml`). Se ambas as cores forem muito parecidas, o texto do botão se torna ilegível.
*   **Recomendação:** Corrigir a paleta de cores nos arquivos de tema (`themes.xml` e `themes.xml (night)`) para garantir que haja contraste suficiente entre as cores de background (como `colorPrimary`) e as cores de texto/ícone sobrepostas a elas (como `colorOnPrimary`).

### **CreateQuestionActivity (`activity_create_question.xml`)**

*   **Análise:** Este layout é bem construído, usando componentes Material Design e sem cores estáticas.
*   **Problema:** Assim como na tela de criação de sala, o botão "Enviar Pergunta" depende das cores `colorPrimary` e `colorOnPrimary` do tema. Se essas cores não tiverem contraste, o texto do botão ficará ilegível.
*   **Recomendação:** Nenhuma alteração é necessária no arquivo de layout. A correção deve ser feita nos arquivos de tema (`themes.xml` e `themes.xml (night)`) para garantir que as cores principais tenham contraste adequado com seu conteúdo.

### **StudyMaterialActivity (`activity_study_material.xml` e `item_study_material.xml`)**

*   **Análise:** A funcionalidade de material de estudo está completamente dependente de cores estáticas, o que a torna incompatível com os temas de modo claro e escuro.
*   **Problema:**
    *   **`activity_study_material.xml`:** O layout inteiro usa cores estáticas como `@color/background_white`, `@color/primary_blue`, e `@color/white`.
    *   **`item_study_material.xml`:** O item da lista é ainda mais problemático, com múltiplos usos de cores estáticas como `@color/card_background`, `@color/divider_color`, `@color/text_primary`, etc. Além disso, utiliza um drawable de fundo estático (`category_badge`) e uma imagem de ícone estática (`studyMaterialImage`), impedindo a adaptação ao tema escuro (`image_dark.png`).
*   **Recomendação:** Ambos os arquivos precisam de uma refatoração completa para usar atributos de tema. Todas as cores estáticas devem ser substituídas por seus equivalentes de tema (ex: `?attr/colorPrimary`, `?attr/colorOnSurface`, etc.). O drawable `category_badge` deve ser substituído por uma solução que se adapte ao tema, e a imagem do item deve ser colocada em diretórios de recursos qualificados (como `drawable-night`) para permitir a seleção automática pelo sistema.

#### Screenshots de Referência

**Tema (Claro/Escuro)**
![Study Material Screen](https://i.imgur.com/rXgS23x.png)

*   **Análise das Screenshots:**
    *   **Aparência Idêntica:** Como você bem observou, a tela é idêntica nos modos claro e escuro. Isso confirma visualmente que o layout está cheio de cores estáticas (`@color/background_white`, `@color/card_background`, etc.), ignorando completamente o tema do sistema.
    *   **Imagem Estática:** Você também notou que `image_dark.png` não está sendo usada. Isso acontece porque o `ImageView` no layout do item provavelmente usa `android:src="@drawable/image"` ou um nome similar estático. O sistema Android só pode alternar drawables automaticamente se eles estiverem nos diretórios corretos (ex: `res/drawable-night/`) e forem referenciados pelo mesmo nome. Este é mais um sintoma da falta de uma estratégia de tema.

---

## Auditoria Crítica do Tema (`themes.xml` e `themes.xml (night)`)

**A causa raiz de todas as inconsistências de UI, problemas de contraste e falhas no modo escuro foi encontrada nos arquivos de tema centrais.**

### **Análise Geral**

Ambos os arquivos `themes.xml` (claro) e `themes.xml (night)` (escuro) demonstram uma fundamental má interpretação dos papéis de cores do Material Design 3. Em vez de definir uma paleta de cores semântica e aplicá-la consistentemente, os temas misturam cores estáticas, atribuem a mesma cor a múltiplos papéis conflitantes (ex: `colorPrimary` e `colorSurface`) e definem estilos de componentes de forma inconsistente entre os modos claro e escuro.

### **Problemas Críticos Identificados**

1.  **Conflito de Papéis de Cor:** No tema claro, `colorPrimary` e `colorSurface` recebem a mesma cor (`@color/legal_blue`), mas ambos são combinados com a mesma cor "On" (`@color/white`). Isso causa o problema do texto do botão invisível e cria uma UI onde não há distinção clara entre superfícies e elementos primários.
2.  **Cores Estáticas Forçadas:** Os temas forçam cores de fundo estáticas (ex: `android:colorBackground` para `@color/background_white`), impedindo o sistema de usar os padrões do Material 3, que são projetados para acessibilidade e consistência.
3.  **Contraste Insuficiente:** No tema escuro, `colorOnSecondary` (`@color/dark_gray`) é combinado com `colorSecondary` (`@color/accent_gold`), o que quase certamente resulta em texto ilegível.
4.  **Estilos Inconsistentes:** O estilo para o `FloatingActionButton` é diferente entre o tema claro (fundo branco estático) e o tema escuro (fundo com base no tema), o que é um sintoma claro da falta de uma estratégia de tema unificada.

### **Plano de Ação Corretivo (Recomendação Final)**

**Para resolver todos os problemas de UI de uma vez por todas, é necessária uma refatoração completa do sistema de temas. Fazer pequenas correções layout por layout será ineficiente e propenso a erros.**

1.  **Passo 1: Gerar uma Nova Paleta de Cores:**
    *   **Ação:** Use o **Material Theme Builder** (disponível online ou como um plugin do Figma/Sketch). Escolha suas cores primárias (ex: o azul legal que você já usa) e deixe a ferramenta gerar a paleta completa de cores para os modos claro e escuro. Isso criará automaticamente cores com nomes semânticos (`colorPrimary`, `colorOnPrimary`, `colorSurface`, `colorOnSurface`, etc.) com contraste garantido.
    *   **Resultado:** Um novo arquivo `colors.xml` (ou um conjunto de arquivos) com uma paleta de cores completa e acessível.

2.  **Passo 2: Criar Novos Arquivos de Tema:**
    *   **Ação:** Crie novos arquivos `themes.xml` e `themes.xml (night)` do zero. Nesses arquivos, mapeie as cores geradas pelo Theme Builder para os atributos de tema do Material 3. Não use nenhuma cor estática diretamente nos temas; apenas referencie as cores da sua nova paleta (ex: `<item name="colorPrimary">@color/md_theme_light_primary</item>`).
    *   **Resultado:** Temas limpos, consistentes e que seguem as melhores práticas do Material 3.

3.  **Passo 3: Refatorar Todos os Layouts:**
    *   **Ação:** Percorra **todos** os arquivos de layout (`.xml`) do projeto.
    *   **Tarefa:** Remova **todas** as cores estáticas (`@color/...`) de todos os componentes (`TextView`, `Button`, `ImageView`, `CardView`, etc.). Substitua-as pelos atributos de tema apropriados (`?attr/...`). Por exemplo, a cor de um texto principal deve ser `?attr/colorOnSurface`, a de um texto secundário `?attr/colorOnSurfaceVariant`, o fundo de um botão primário `?attr/colorPrimary`, e assim por diante.
    *   **Resultado:** Uma base de código de UI 100% adaptável ao tema, eliminando todas as inconsistências visuais.

4.  **Passo 4: Remover Cores Obsoletas:**
    *   **Ação:** Após a refatoração completa dos layouts, exclua com segurança todas as definições de cores antigas e não semânticas (ex: `@color/legal_blue`, `@color/text_primary`, etc.) do seu arquivo `colors.xml` original.
    *   **Resultado:** Uma base de código mais limpa e fácil de manter.

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

study_materials- Main Collection. Fields:
title: string
description: string
authorName: string
category: string
fileURL: string
timestamp: timestamp

Implement one UI/UX Change at a given time.