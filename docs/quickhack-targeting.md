# Quickhack targeting e scanner HUD

**Stato:** implementato in `0.2.2`; test gameplay lato client da completare
**Data:** 2026-10-24
**Tipo:** modifica bounded allo scanner esistente

## Obiettivo

1. Rimuovere l'intercettazione della rotella del mouse: mentre lo scanner è aperto la rotella deve cambiare slot della hotbar come nel gioco vanilla.
2. Sostituire `crosshairPickEntity` con un raycast dal mirino, così da poter agganciare qualsiasi entità vivente in linea di vista, non solo quella entro la portata d'attacco vanilla.
3. Evidenziare in rosso ogni entità valida visibile nel campo visivo.
4. Mostrare la scheda statistiche solo quando il mirino è sopra un bersaglio evidenziato, con dissolvenza rapida quando il mirino si sposta.

## Decisioni proposte

| Tema | Scelta |
|------|--------|
| Portata | `min(renderDistance × 16, 64)` blocchi lato client; il server accetta al massimo 64 |
| Bersagli | tutte le `LivingEntity`: mob, animali, villager, giocatori, armor stand; esclusi sé stessi, morti e spettatori |
| Raycast client | `ProjectileUtil.getEntityHitResult` dalla posizione degli occhi lungo lo sguardo; un raycast per frame |
| Outline | `setGlowingTag(true)` sulle entità visibili + team client rosso; ripristino dello stato originale alla chiusura |
| Entità evidenziate | solo nel campo visivo, con linea di vista libera, entro portata; massimo 48 per aggiornamento |
| Aggiornamento outline | ogni 5 tick client; il bersaglio sotto il mirino viene ricalcolato ogni frame |
| Cast | `CastHackPayload` esteso con l'id dell'entità; il server verifica id, distanza, linea di vista e mira prima di applicare l'hack |
| HUD statistiche | nome, tipo, fazione, salute, armatura, effetti attivi; dissolvenza 120 ms quando il mirino lascia il bersaglio |
| Rotella | rimossi hook GLFW, callback e hint; navigazione lista solo con X/Z rimappabili |
| Versione | 0.2.2 |

## Comportamento

### Hotbar
- `CyberCraftClient` non installa più il callback GLFW della rotella.
- Lo scanner non apre una schermata: la rotella resta al gioco e cambia lo slot della hotbar.
- L'HUD mostra `[X]` e `[Z]` per scorrere i quickhack, usando i nomi reali delle keybind.

### Selezione
- Il client costruisce un raggio `occhi → sguardo × portata` e interroga le entità nell'AABB del raggio.
- Il bersaglio è l'entità colpita più vicina; non serve più che sia il `crosshairPickEntity` vanilla (limitato a circa 3 blocchi).
- Il server non ricalcola il bersaglio in modo approssimativo: riceve l'id, controlla che l'entità esista, sia viva, sia entro 64 blocchi, abbia linea di vista libera e sia effettivamente sotto il mirino. Se il controllo fallisce, l'hack viene rifiutato con il messaggio esistente.

### Outline rosso
- Un nuovo `EntityOutlineManager` mantiene un team client `cybercraft_scan` di colore rosso e aggiunge i nomi delle entità visibili.
- Le entità ricevono `setGlowingTag(true)`; il renderer vanilla usa il colore del team per l'outline.
- Alla chiusura dello scanner o al cambio di mondo vengono rimossi dal team e viene ripristinato il flag di glow originale.
- Il filtro di visibilità usa portata, campo visivo e `hasLineOfSight`; l'aggiornamento è a 4 Hz per non pesare sul frame.

### Scheda statistiche
- Compare solo con lo scanner attivo e il mirino sopra un bersaglio.
- Contenuto: nome, tipo, fazione (ostile/neutrale/passivo/giocatore/tecnologia), salute segmentata, armatura, effetti attivi con durata, vulnerabilità e barra di upload.
- Quando il bersaglio esce dal mirino, la scheda resta per 120 ms in dissolvenza e poi sparisce.

## File coinvolti

- `src/main/java/com/cybercraft/client/CyberCraftClient.java`: rimozione hook rotella, invio id bersaglio.
- `src/main/java/com/cybercraft/client/ScannerHud.java`: raycast, pannello statistiche, fade, hint X/Z.
- `src/main/java/com/cybercraft/client/EntityOutlineManager.java` (nuovo): outline rosso e ciclo di vita.
- `src/main/java/com/cybercraft/client/ScannerState.java`: stato del bersaglio in dissolvenza.
- `src/main/java/com/cybercraft/network/CastHackPayload.java`: id entità nel payload.
- `src/main/java/com/cybercraft/network/ModNetworking.java`: ricezione del nuovo payload.
- `src/main/java/com/cybercraft/netrunning/NetrunnerManager.java`: validazione server del bersaglio esplicito.
- `src/main/resources/assets/cybercraft/lang/en_us.json` e `it_it.json`: nuove chiavi HUD, rimozione chiave rotella.
- `README.md`, `docs/gameplay.md`, `docs/architecture.md`, `docs/changelog.md`, `docs/roadmap.md`, `docs/dev-log.md`: aggiornamento documentazione.

## Verifiche

- `./gradlew build --offline` con JDK 21 → `BUILD SUCCESSFUL`; jar `cybercraft-0.2.2.jar` 238.223 byte.
- Server dedicato → `cybercraft 0.2.2`, `Done`, nessun errore.
- Test in gioco lato client da eseguire: rotella/hotbar, outline rosso (anche su entità non giocatore), scheda statistiche, fade, cast entro e oltre la portata.
- Il test grafico non è stato possibile in questa sessione e va dichiarato come limite noto.

## Rischi

- Il colore rosso dell'outline dipende dal team client: se il renderer non applica il colore del team alle entità non giocatore, servirà un renderer custom. Da verificare in gioco.
- L'outline su molte entità può pesare: limite di 48 e aggiornamento a 4 Hz.
- Il cast con id può fallire se l'entità si muove tra invio e validazione; è il comportamento corretto, ma da verificare con latenza reale.
