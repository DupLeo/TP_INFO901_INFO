# TP INFO901 - Systèmes répartis

## 📌 Description
Ce projet illustre la communication entre plusieurs processus (P0, P1, P2, P3) via des boîtes aux lettres (`Mailbox`) et différents types de messages (`MessageTo`, `ACKMessage`, `BroadcastMessage`, etc.).  
Il inclut également un mécanisme de synchronisation et d’exclusion mutuelle (section critique avec jeton).

Chaque classe/fonction du projet est documentée avec une **JavaDoc**, et une brève explication est donnée ci-dessous.

---

## 📝 Classes principales et fonctions

| Classe | Fonction | Description courte |
|--------|---------|------------------|
| `Process` | `run()` | Boucle principale d’un processus, envoie/recevoit messages et synchro avec autres processus. |
| `Com` | `sendTo(Object o, int to)` | Envoi asynchrone d’un message vers un autre processus. |
| `Com` | `sendToSync(Object o, int to)` | Envoi bloquant avec attente d’un ACK. |
| `Com` | `broadcast(Object o)` | Diffusion d’un message à tous les processus. |
| `Com` | `broadcastSync(Object o, int from)` | Diffusion synchronisée avec ACK de tous les destinataires. |
| `Com` | `requestSC()` / `releaseSC()` | Gestion de l’accès à la section critique via le jeton. |
| `Com` | `synchronize()` | Synchronisation globale entre tous les processus. |
| `Mailbox` | `addMessage(Message msg)` | Ajoute un message dans la file d’attente du processus. |
| `Mailbox` | `getMsg()` | Récupère un message (bloquant si vide). |
| `Message` | `getClock()` / `getSender()` / `getPayload()` | Accesseurs pour l’horloge, l’expéditeur et le contenu du message. |
| `MessageTo` | `getDest()` | Destinataire spécifique pour un message unicast. |
| `ACKMessage` | `getDest()` | Destinataire d’un ACK. |
| `TokenMessage` | `getDest()` | Destinataire du jeton pour section critique. |
| `BroadcastMessage` | - | Message diffusé à tous les processus. |

---

## ▶️ Compilation et exécution

### 1. Compilation avec Maven
```bash
mvn clean install


P0 id :0
P0 Loop : 0
P1 id :1
P1 Loop : 0
P2 id :2
P2 Loop : 0
P3 id :3
P3 Loop : 0
P0 envoie à P1 : j'appelle 1 et je te recontacte après [clock=1]
Pour P1 nombre de message : 0
P1 Loop : 1
P3 Loop : 1
>>> P1 reçoit le jeton de P0
P0 envoie (sync) à P2 : J'ai laissé un message à 1, je le rappellerai après, on se sychronise tous et on attaque la partie ? [clock=2]
P1 a reçu de P0 : j'appelle 1 et je te recontacte après [clock=2]
>>> P0 crée le jeton et l'envoie à P1
P2 a reçu de P0 : J'ai laissé un message à 1, je le rappellerai après, on se sychronise tous et on attaque la partie ? [clock=3]
...
...
P2 broadcast : J'ai gagné !!!
P3 a reçu un broadcast : J'ai gagné !!! [clock=12]
P0 a reçu un broadcast : J'ai gagné !!! [clock=12]
P1 a reçu un broadcast : J'ai gagné !!! [clock=12]
