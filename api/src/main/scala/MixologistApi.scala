package mixologist

import cats.data.NonEmptyList
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import io.circe.generic.extras.{Configuration => CirceConfiguration}
import io.circe.generic.extras.auto._
import sttp.tapir.{endpoint, PublicEndpoint}
import sttp.tapir._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.generic.auto._
import sttp.tapir.generic.Configuration
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.apispec.openapi.circe.yaml._
import cats.effect.IO

case class MixologistApi(mixologistService: MixologistService) {
  import MixologistApi._

  val routes: HttpRoutes[IO] =
    Http4sServerInterpreter[IO]().toRoutes(
      List(
        getIngredientsEndpoint.serverLogic[IO] { _ =>
          mixologistService.getIngredients().map(r => Right(IngredientsResponse(r)))
        },
        getPotentialCocktailsEndpoint.serverLogic[IO] { payload =>
          mixologistService.getPotentialCocktails(payload.ingredients).map(r => Right(PotentialCocktailsResponse(r)))
        }
      )
    )
}

object MixologistApi {
  private val DISCRIMINATOR                       = "discriminator"
  implicit val config: CirceConfiguration         = CirceConfiguration.default.copy(discriminator = Some(DISCRIMINATOR))
  implicit val customConfiguration: Configuration = Configuration.default.withDiscriminator(DISCRIMINATOR)

  implicit def schemaForNonEmptyList[V](implicit s: Schema[List[V]]): Schema[NonEmptyList[V]] =
    s.map(NonEmptyList.fromList)(_.toList)

  case class IngredientsResponse(data: List[Ingredient])
  case class PotentialCocktailsJsonPayload(ingredients: List[Ingredient])
  case class PotentialCocktailsResponse(data: List[PotentialCocktail])

  private val getIngredientsEndpoint: PublicEndpoint[Unit, Unit, IngredientsResponse, Any] =
    endpoint.get.in("ingredients").out(jsonBody[IngredientsResponse])

  private val getPotentialCocktailsEndpoint
    : PublicEndpoint[PotentialCocktailsJsonPayload, Unit, PotentialCocktailsResponse, Any] =
    endpoint.post
      .in(jsonBody[PotentialCocktailsJsonPayload])
      .in("potential-cocktails")
      .out(jsonBody[PotentialCocktailsResponse])

  val openApiSpec = OpenAPIDocsInterpreter()
    .toOpenAPI(
      List(getIngredientsEndpoint, getPotentialCocktailsEndpoint),
      "Mixologist",
      Environment.getFromEnv.apiVersion
    )

  val docsRoute: HttpRoutes[IO] = HttpRoutes.of[IO] { case GET -> Root / "open-api-spec.yaml" =>
    Ok(openApiSpec.toYaml)
  }
}
