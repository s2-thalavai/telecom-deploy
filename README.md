# Telecom Plans Service - multi-environment deployment

| Env | Profile | DB | Cache | Auth | Logs | Mgmt port |
|---|---|---|---|---|---|---|
| dev (laptop) | `dev` | H2 + seed data | Caffeine | Basic `dev/dev123` | text, DEBUG | 8080 |
| qa | `qa` | PostgreSQL | Caffeine | Basic (`API_PASSWORD`) | text, DEBUG | 8080 |
| staging | `staging` | PostgreSQL | Redis | OAuth2 JWT | JSON | 9091 |
| prod | `prod` | PostgreSQL | Redis (TLS) | OAuth2 JWT | JSON, WARN root | 9091 |

## Run locally
```bash
mvn spring-boot:run                                   # dev profile by default
docker compose up --build                             # same image + Postgres + Redis, qa profile
SPRING_PROFILES_ACTIVE=qa DB_URL=... java -jar target/telecom-plans-service-*.jar
```

## API
```bash
curl -u dev:dev123 localhost:8080/api/v1/plans
curl -u dev:dev123 localhost:8080/api/v1/plans/UNL299
curl -u dev:dev123 -X POST localhost:8080/api/v1/plans -H 'Content-Type: application/json' \
  -d '{"code":"DATA99","name":"Data Pack 99","price":99,"dataGb":6,"validityDays":7}'
curl -u dev:dev123 localhost:8080/api/v2/plans         # feature-flagged v2
curl localhost:8080/actuator/info                       # deployment.version / environment
```

## Kubernetes
```bash
kubectl apply -k k8s/overlays/qa
kubectl apply -k k8s/overlays/prod
scripts/smoke-test.sh https://plans.example.com http://localhost:9091 1.4.0-57-ab12cd3 prod $TOKEN
scripts/rollback.sh telecom-prod              # previous revision
scripts/rollback.sh telecom-prod 12           # specific revision
```

## CI/CD
`ci/Jenkinsfile`, `ci/.gitlab-ci.yml` (move to repo root), `ci/azure-pipelines.yml` + `ci/deploy-steps.yml`.
All three: build once -> test (incl. Testcontainers) -> immutable image `<semver>-<build>-<sha>` -> qa -> staging -> manual gate -> prod, with automatic `rollout undo` on failure.
