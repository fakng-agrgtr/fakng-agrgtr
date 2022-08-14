Local run

Prerequisites:
- Docker installed locally

Steps:
1. Clone the repo
2. From the repo root package run command (this will create a DB container locally):
```docker-compose up -d```
3. Run the project using your favorite way for running Java projects
4. DB migrations will be executed automatically
5. After the project is run, it will be available on 8080 port locally
