---
name: "deploy"
description: "runs the spring boot backend"
version: "1.0.0"
tools: ["read", "bash", "ask"]
permissions: ["project"]
---

# Deploy

Starts all dependencies and runs the spring boot backend

## Behavior

- Step 1: start dependencies by running !docker compose up --build if it's not running yet
- Step 2: build the app jar by running !mvn package -DskipTests=true
- Step 3: start the backend by running !java -jar ./target/nutrition-demo-0.0.1-SNAPSHOT.jar

## Guidelines
- If any of the steps fail with an error stop exceutions and wait for user's input

## Examples
### Deploy backend
```bash
/deploy
```
