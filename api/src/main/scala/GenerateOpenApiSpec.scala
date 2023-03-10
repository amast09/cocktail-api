package mixologist

import cats.effect.IOApp
import sttp.apispec.openapi.circe.yaml._

object GenerateOpenApiSpec extends IOApp.Simple {
  val openApiYaml = MixologistApi.openApiSpec.toYaml
  val run         = ResourceIO("../open-api-spec.yaml").write(openApiYaml)
}
