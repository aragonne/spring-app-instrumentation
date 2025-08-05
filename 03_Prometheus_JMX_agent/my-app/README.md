# Démonstration Java Agent OpenTelemetry

Ce projet démontre l'utilisation d'un Java agent OpenTelemetry pour l'observabilité automatique d'une application Spring Boot.

## Avantages de l'agent OpenTelemetry

- **Observabilité automatique** : Pas besoin de modifier le code applicatif
- **Instrumentation complète** : Traces automatiques pour HTTP, JDBC, Spring MVC, etc.
- **Configuration flexible** : Configuration via fichier properties ou variables d'environnement
- **Compatibilité** : Fonctionne avec la plupart des frameworks Java

## Fichiers du projet

- `opentelemetry-javaagent.jar` : L'agent OpenTelemetry (téléchargé automatiquement)
- `otel.properties` : Configuration de l'agent OpenTelemetry
- `run-with-otel.sh` : Script de lancement avec l'agent
- `test-endpoints.sh` : Script de test des endpoints
- `BonjourEQLController.java` : Contrôleur avec plusieurs endpoints pour démonstration

## Utilisation

### 1. Lancer l'application avec l'agent OpenTelemetry

```bash
./run-with-otel.sh
```

### 2. Tester les endpoints (dans un autre terminal)

```bash
./test-endpoints.sh
```

## Endpoints disponibles

- `GET /bonjour-eql` : Endpoint principal avec délai simulé
- `GET /user/{id}` : Endpoint avec paramètre de chemin
- `POST /data` : Endpoint POST avec traitement de données
- `GET /error` : Endpoint qui peut générer des erreurs aléatoires

## Observabilité automatique

L'agent OpenTelemetry capture automatiquement :

- **Traces HTTP** : Toutes les requêtes entrantes et sortantes
- **Métriques** : Latence, débit, taux d'erreur
- **Logs** : Corrélation automatique avec les traces
- **Erreurs** : Capture automatique des exceptions

## Configuration OpenTelemetry

Le fichier `otel.properties` configure :

- **Service name** : `spring-demo-otel`
- **Exporters** : Console et OTLP (pour Jaeger/OTEL Collector)
- **Sampling** : Toutes les traces (`always_on`)
- **Instrumentation** : Spring MVC, HTTP, JDBC activés

## Visualisation des traces

Les traces sont exportées vers :

1. **Console** : Affichage direct dans les logs
2. **OTLP** : Compatible avec Jaeger, OTEL Collector, etc.

Pour utiliser avec Jaeger :
```bash
# Lancer Jaeger (Docker)
docker run -d --name jaeger \
  -p 16686:16686 \
  -p 14250:14250 \
  jaegertracing/all-in-one:latest

# L'application enverra automatiquement les traces à Jaeger
```

## Comparaison avec l'approche manuelle

| Aspect | Agent OpenTelemetry | Instrumentation manuelle |
|--------|-------------------|-------------------------|
| Code à modifier | ❌ Aucun | ✅ Beaucoup |
| Couverture | ✅ Automatique complète | ⚠️ Dépend de l'implémentation |
| Maintenance | ✅ Minimale | ⚠️ Continue |
| Performance | ✅ Optimisée | ⚠️ Variable |
| Flexibilité | ⚠️ Configuration limitée | ✅ Contrôle total |
