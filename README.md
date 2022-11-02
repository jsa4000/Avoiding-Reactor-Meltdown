# Non Blocking App

## Examples

Create Document

```bash
curl -X POST 'http://localhost:8080/documents' \
-H 'Content-Type: application/json' \
-d '{
    "documentId": "1234",
    "type": "DRAFT",
    "releaseDate": "2022-09-19T20:12:18.786Z"
}' \
| jq .

curl -X POST 'http://localhost:8080/documents' \
-H 'Content-Type: application/json' \
-d '{
    "documentId": "5678",
    "type": "DRAFT",
    "releaseDate": "2022-09-19T20:12:18.786Z"
}' \
| jq .
```

Generate Bulk Document

>>  Set CPU limits used by JVM: `-XX:ActiveProcessorCount=1

```bash
curl -X POST 'http://localhost:8080/documents/bulk' \
-H 'Content-Type: application/json' \
-d '[
   {
    "documentId": "1234",
    "type": "DRAFT",
    "releaseDate": "2022-09-19T20:12:18.786Z"
   },
   {
    "documentId": "5678",
    "type": "DRAFT",
    "releaseDate": "2022-09-19T20:12:18.786Z"
  },
   {
    "documentId": "9012",
    "type": "DRAFT",
    "releaseDate": "2022-09-19T20:12:18.786Z"
  }
]' \
| jq .
```

Get Document By Id

```bash
curl 'http://localhost:8080/documents/1234' \
| jq .
```

Get All Documents

```bash
curl 'http://localhost:8080/documents' \
| jq .
```

Benchmarks

```bash
# Sequential (22s)
curl 'http://localhost:8080/benchmark/sequential/12' -o /dev/null -s -w 'Total: %{time_total}s\n'

# Parallel (12s)
curl 'http://localhost:8080/benchmark/parallel/12' -o /dev/null -s -w 'Total: %{time_total}s\n'

# Reactive (3s)
curl 'http://localhost:8080/benchmark/reactive/12' -o /dev/null -s -w 'Total: %{time_total}s\n'
```

## Build Image

```bash
# Build Jar Package
mvn clean package

# Build the image (Does not support Multiple Platforms Builds)
# mvn spring-boot:build-image

# Build container image multi-platform using Docker buildx
# docker buildx build --platform=linux/amd64,linux/arm64 -t jsa4000/demo:0.0.1-SNAPSHOT --push .

# Using Jib (Almost supported, not locally)
# https://github.com/GoogleContainerTools/jib/blob/master/docs/faq.md#how-do-i-specify-a-platform-in-the-manifest-list-or-oci-index-of-a-base-image
mvn jib:build

# Get Docker images platforms
docker pull jsa4000/demo:0.0.1-SNAPSHOT
docker image inspect jsa4000/demo:0.0.1-SNAPSHOT

# Start the image exposing port 8080
# Profiles: sequential, parallel or  reactive
docker run --name demo -p 8080:8080 -e SPRING_PROFILES_ACTIVE=sequential \
  jsa4000/demo:0.0.1-SNAPSHOT

# Start the image exposing port 8080, using maximum 1024m memory and 1.5 CPU
# Profiles: sequential, parallel or  reactive
docker run --cpus=1.5 --memory=1024m  \
  --name demo -p 8080:8080 -e SPRING_PROFILES_ACTIVE=reactive \
  jsa4000/demo:0.0.1-SNAPSHOT
  
docker run --cpus=1.5 --memory=1024m  \
  --name demo -p 8080:8080 -e SPRING_PROFILES_ACTIVE=parallel \
  -e 'JAVA_TOOL_OPTIONS=-XX:ActiveProcessorCount=1' \
  jsa4000/demo:0.0.1-SNAPSHOT
  
# Kill and remove the container
docker kill demo
docker rm demo
```

## Openapi UI

http://localhost:8080/swagger-ui.html