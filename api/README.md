# mixologist-api

An API to determine cocktail possibilities based on available ingredients

## Generate an updated Open API Specification for the project

```
MIXOLOGIST_API_VERSION=$(sbt -Dsbt.supershell=false -Dsbt.log.noformat=true "print version" --error) sbt "runMain mixologist.GenerateOpenApiSpec"
```
