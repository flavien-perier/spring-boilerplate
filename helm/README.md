# helm

Helm chart for deploying the application to Kubernetes (K3s).

## Responsibilities

- Deploys the Spring Boot application, PostgreSQL, and Valkey (Redis)
- Manages ConfigMaps (non-secret env vars) and Secrets (sensitive values)
- Supports embedded (in-cluster) or external database/cache modes
- Optional Ingress configuration

## Chart structure

```
helm/
├── Chart.yaml                       Chart metadata (name: demo)
├── values.yaml                      Default values
└── templates/
    ├── application.*                App Deployment, Service, Ingress, ConfigMap, Secret
    ├── database.*                   PostgreSQL Deployment, Service, PVC
    └── valkey.*                     Valkey Deployment, Service, PVC
```

## Key facts

- App port: `8080`; health probes via Spring Actuator (`/actuator/health/liveness`, `/actuator/health/readiness`)
- Init containers wait for database and Valkey before the app starts (embedded mode only)
- Sensitive values (passwords) go in Secrets; everything else in ConfigMap

## Validation

```bash
helm lint helm/
helm template demo helm/
```
