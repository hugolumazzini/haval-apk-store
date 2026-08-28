# Haval APK Store

Loja de aplicativos para a **central multimídia do Haval H6**. Traz um catálogo
com os apps que a comunidade usa no carro — Impulse, AutoPanel, Haval Radio,
Haval Climate Control, Waze — e instala cada um direto na tela da central, sem
cabo e sem computador.

Nasceu para tapar um buraco: a aba "Instalar Apps" do Impulse foi descontinuada
e o catálogo dela está obsoleto. Este app **não substitui o Impulse** — anda ao
lado dele, e serve inclusive para instalar e atualizar o próprio Impulse. A
interface segue a linguagem visual do Impulse de propósito, para os dois não
destoarem na mesma tela.

> Deve funcionar em qualquer Android em paisagem (outras centrais, TV box), mas
> só é testado no H6.

## Instalar

Cole este endereço no "Instalar via URL" do Impulse, que já está na central:

```
https://tinyurl.com/havalapkstore
```

O link é fixo e sempre entrega a versão mais recente — não muda a cada release.
Depois da primeira instalação a Haval APK Store se atualiza sozinha, porque ela
também está no próprio catálogo.

Ele é só um atalho para este endereço, que é o de verdade e funciona igual:

```
https://github.com/hugolumazzini/haval-apk-store/releases/latest/download/haval-apk-store.apk
```

Se o encurtador algum dia sair do ar, use o endereço completo — e recrie o
atalho `havalapkstore` em qualquer encurtador apontando para ele.

Requer Android 6.0 ou mais novo. Os arquivos ficam em
[Releases](https://github.com/hugolumazzini/haval-apk-store/releases).

## O que ele faz

- **Catálogo** — grade de apps vinda de um `catalog.json`, mostrando se cada um
  está *não instalado*, *instalado* ou *com atualização disponível*.
- **Por URL** — cola o endereço de um `.apk` e instala.
- **Instalados** — lista os apps do aparelho, abre e desinstala.
- **Ajustes** — mostra a versão instalada, procura versão nova e libera a
  permissão de "fontes desconhecidas".

A checagem de versão nova não é um mecanismo à parte: a loja está no próprio
`catalog.json`, então é a mesma comparação de `versionCode` usada para qualquer
outro app. Ela roda toda vez que o catálogo é carregado.

Detalhes de robustez:

- O catálogo tem três níveis de queda: URL remota → última cópia baixada →
  lista embutida no app. A grade nunca fica vazia por falta de internet.
- A instalação tenta o `PackageInstaller` (que devolve sucesso/erro); se a ROM
  recusar, cai para o diálogo clássico do sistema.
- Todo APK instalado pelo catálogo tem o `sha256` conferido antes de instalar.
  Entrada sem hash não instala.

## Como compilar

Precisa do Android Studio instalado (ele traz o Java necessário).

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew assembleDebug
```

O APK sai em `app/build/outputs/apk/debug/app-debug.apk`.

Para instalar num aparelho conectado por USB:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## O catálogo

A lista de apps vive em [`catalog.json`](catalog.json), na raiz deste
repositório, e é servida em:

```
https://raw.githubusercontent.com/hugolumazzini/haval-apk-store/main/catalog.json
```

O endereço é **fixo no código**, em `CATALOG_URL` dentro de
[`CatalogRepository.kt`](app/src/main/java/br/com/hugolumazzini/havalapkstore/data/CatalogRepository.kt).
Quem usa o app não tem como trocá-lo: um endereço mudado por engano
transformaria a loja num instalador de qualquer coisa. Para apontar para outra
lista é preciso recompilar.

Uma cópia do mesmo arquivo fica em `app/src/main/assets/` como último recurso,
para a central sem internet.

**Para adicionar um app:** edite o `catalog.json` e faça push. O app pega a
versão nova no próximo "Recarregar" — não precisa recompilar nada.

`packageName`, `name`, `apkUrl` e `sha256` são obrigatórios. Os demais campos:

| Campo | Para que serve |
|---|---|
| `versionCode` | Compara com a versão instalada para avisar que há atualização. Sem ele, o app nunca aparece como *atualizável*. |
| `versionName`, `sizeBytes`, `category` | Só exibição. |
| `iconUrl` | Ícone na grade. Sem ele, mostra a inicial do nome. |

Para levantar os valores de um APK:

```bash
aapt2 dump badging app.apk | head -1   # packageName, versionCode, versionName
shasum -a 256 app.apk                  # sha256
stat -f%z app.apk                      # sizeBytes
```

O `apkUrl` precisa terminar entregando o arquivo. Redirecionamentos funcionam —
links `shorturl.at` foram testados e passam. O que **não** funciona é página de
download que exige um clique de confirmação (Google Drive, APKMirror): o app
receberia HTML no lugar do APK.

### Sobre o `sha256` das entradas com link encurtado

Waze e ReVanced Manager Plus usam encurtadores, que apontam para "a versão
atual" e não para um arquivo fixo. Quando essa versão mudar, o hash gravado aqui
deixa de bater e o app vai **recusar a instalação** — que é o comportamento
desejado: melhor falhar do que instalar um arquivo que não é o esperado.

Quando isso acontecer, baixe o novo arquivo e atualize o `sha256`, o
`versionCode` e o `versionName` desta entrada. As entradas do GitHub não têm
esse problema: apontam para uma versão fixa, que nunca muda.

## Publicar uma versão nova

O padrão, para o link curto continuar valendo:

1. Suba `versionCode` e `versionName` em `app/build.gradle.kts`. O
   `versionCode` **precisa** aumentar — é por ele que o Android e o catálogo
   sabem que há atualização.
2. `./gradlew assembleRelease` e copie o resultado para `haval-apk-store.apk`.
   **O nome do arquivo é sempre esse, sem número de versão** — é o que faz
   `releases/latest/download/haval-apk-store.apk` funcionar, e é para onde o
   `tinyurl.com/havalapkstore` aponta.
3. `gh release create vX.Y.Z haval-apk-store.apk` — tag em três números.
4. Atualize a entrada da própria loja no `catalog.json` (`versionCode`,
   `versionName`, `sizeBytes`, `sha256` e a URL fixada da tag) e dê push.

Nas notas do release: link curto **e** o endereço completo do GitHub, o que
mudou, versão mínima do Android. **Sem SHA-256** — ele não serve para ninguém
ali. Quem confere o hash é o app, pelo `catalog.json`, automaticamente.

O `raw.githubusercontent.com` guarda cópia por uns 5 minutos, então o catálogo
novo só aparece no app depois disso.

## Assinatura de release

**Obrigatória.** Crie um `keystore.properties` na raiz com `storeFile`,
`storePassword`, `keyAlias` e `keyPassword`. Sem ele o `assembleRelease`
**falha de propósito**, com uma mensagem explicando o que fazer.

O motivo: a chave de debug do Android é pública (a senha é literalmente
`android`). Um release assinado com ela pode ser atualizado por qualquer pessoa
que monte um APK com o mesmo `applicationId` — e o estrago só apareceria depois
de publicado. Melhor não compilar.

Esse arquivo e os `.jks` estão no `.gitignore`: **nunca suba chaves.** Eles só
existem nesta máquina; perdê-los significa não conseguir mais publicar
atualizações para quem já instalou.

## Decisões de segurança

O app baixa arquivos da internet e os instala. Isso concentra bastante poder num
lugar só, então algumas portas ficam fechadas por escolha:

- **Só HTTPS.** Não há `usesCleartextTraffic`; há um
  [`network_security_config.xml`](app/src/main/res/xml/network_security_config.xml)
  que barra texto claro, e o "Instalar por URL" recusa `http://` no código
  (o que também cobre o Android 6.0, onde aquele arquivo não vale). O OkHttp
  segue redirecionamentos, mas **não** de https para http.
- **`sha256` obrigatório no catálogo.** Instalar pela grade só acontece se a
  entrada declarar o hash. É o único ponto onde o app instala algo por conta
  própria, a partir de uma lista que ele mesmo baixou. Colar uma URL à mão
  continua permitido sem hash — ali a escolha é de quem digitou.
- **Fonte do catálogo fixa no código**, sem campo editável na interface.
- **`allowBackup="false"`.** Não há nada que valha a pena restaurar, e o backup
  seria mais uma cópia do estado do app fora do aparelho.
