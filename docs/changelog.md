# Changelog

## 0.2.2 — 24 ottobre 2026

**Novità**
- Targeting quickhack dal mirino: qualsiasi `LivingEntity` in linea di vista, fino a `min(render distance × 16, 64)` blocchi; il server valida fino a 64.
- Outline rosso sulle entità visibili nel campo visivo, con aggiornamento a 4 Hz e massimo 48 bersagli.
- Scheda statistiche cyberpunk sotto il mirino: nome, tipo, fazione, salute, armatura/robustezza, effetti attivi, vulnerabilità e upload; dissolvenza di 120 ms quando il mirino si sposta.
- Nuove chiavi lang EN/IT per fazione, tipo, stati e hint X/Z.
- Documento di design `docs/quickhack-targeting.md`.

**Correzioni**
- Rimossa l'intercettazione della rotella del mouse: con lo scanner aperto la rotella torna a cambiare lo slot della hotbar.
- `CastHackPayload` include l'entity id; il server verifica entità, distanza, linea di vista e mira prima di applicare l'hack.

**Verifiche**
- `gradlew build --offline` → `BUILD SUCCESSFUL`; jar `cybercraft-0.2.2.jar` 238.223 byte.
- Server: `cybercraft 0.2.2`, `Done`, nessun errore.
- Test grafico lato client (hotbar, outline non giocatore, fade, cast a distanza) ancora da eseguire in un mondo reale.

## 0.2.1 — 24 ottobre 2026

**Correzioni**
- Banco Impianti: il menu non si chiude più al primo tick. `ImplantForgeMenu` estende `CraftingMenu` e valida `cybercraft:implant_forge`.
- Schermata Cyberware: rimossi overlap e troncamenti; layout ridisegnato con corpo centrale, 5 slot per lato, barra capacità verticale e modalità compatta.
- Zona quickhack: pannello rifatto sulla reference Cyberpunk 2077, riga selezionata stabile e stato `BLOCKED` quando la RAM non basta.
- Barra RAM: 20 segmenti fissi per supportare il massimo 100.

**Novità**
- Rimozione degli impianti dalla schermata Cyberware, con restituzione dell'item.
- RAM massima portata a 100; l'Ex-Disk ora aumenta solo la rigenerazione.
- Effetto visivo Sandevistan: scie particellari, tinta teal-verde, vignettatura, speed line, scanline e countdown.

**Verifiche**
- `gradlew build --offline` → `BUILD SUCCESSFUL`; jar 229.824 byte, 359 entry.
- Server: `cybercraft 0.2.1`, `Done`, nessun errore.
- 203 JSON validi; 290 chiavi lang per EN e IT.

## 0.2.0 — 24 ottobre 2026

- Catalogo cyberware portato a 42 impianti nei 10 slot, con `ImplantEffects` centralizzato.
- Aggiunti titanio, neodimio e litio con lingotti, blocchi, fusione/blasting e worldgen.
- Aggiunti scheda circuito, servomotore, gel biologico e lente ottica.
- Aggiunti Banco Impianti e Tavolo di Installazione; 70 ricette, nessun legno negli impianti.
- Aggiunto Edgerunner's Book con descrizioni, icone e ricette.
- Aggiunta schermata Cyberware e HUD scanner cyberpunk.
- Aggiunte keybind rimappabili e navigazione quickhack con rotella.
- Rimosse Pistola Smart e Munizioni Smart.
- Jar: 218.537 byte.

## 0.1.1 — 24 ottobre 2026

- Cyberware persistente con Fabric Data Attachment e `copyOnDeath`.
- Capacità cyberware basata sul livello XP.
- Reinforced Tendons con doppio salto server-authoritative.
- Sandevistan con keybind, rallentamento mob dell'85%, durata 10 s e ricarica 30 s.
- 10 slot corporei della tassonomia Cyberpunk 2077.
- Jar: 65.029 byte.

## 0.1.0 — 24 ottobre 2026

- Port del progetto a Minecraft 1.21.1 su Fabric.
- Cyberware con Tolleranza, Cyberdeck con quickhack, Monocorda, Blocco al Neon e Metallo Scuro.
- Asset, lingue EN/IT, loot table e tag.
