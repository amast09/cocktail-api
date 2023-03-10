package cocktail.api

import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.io._

import sttp.tapir.{endpoint, Codec, PublicEndpoint}
import sttp.tapir._
import sttp.tapir.Codec.PlainCodec
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.http4s.Http4sServerInterpreter
import io.circe.generic.auto._
import sttp.tapir.generic.auto._
import sttp.tapir.generic.Configuration

import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.apispec.openapi.circe.yaml._

case class MixologistApi(mixologistService: MixologistService) {
  implicit val customConfiguration: Configuration = Configuration.default.withDiscriminator("discriminator")

  implicit val glassSchema: Schema[Glass]    = Schema.derivedEnumeration[Glass].defaultStringBased
  implicit val glassCodec: PlainCodec[Glass] = Codec.derivedEnumeration[String, Glass].defaultStringBased

  case class IngredientsResponse(data: List[Ingredient])
  case class PotentialCocktailsJsonPayload(ingredients: List[Ingredient])
  case class PotentialCocktailsResponse(data: List[PotentialCocktail])

  val getIngredientsEndpoint: PublicEndpoint[Unit, Unit, IngredientsResponse, Any] =
    endpoint.get.in("ingredients").out(jsonBody[IngredientsResponse])

  val getPotentialCocktailsEndpoint
    : PublicEndpoint[PotentialCocktailsJsonPayload, Unit, PotentialCocktailsResponse, Any] =
    endpoint.post
      .in(jsonBody[PotentialCocktailsJsonPayload])
      .in("potential-ingredients")
      .out(jsonBody[PotentialCocktailsResponse])

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

  val docsRoute: HttpRoutes[IO] = HttpRoutes.of[IO] { case GET -> Root / "openapi.yaml" =>
    val docs = OpenAPIDocsInterpreter()
      .toOpenAPI(List(getIngredientsEndpoint, getPotentialCocktailsEndpoint), "Mixologist", "1.0") // TODO: make not static
      .toYaml
    Ok(docs)
  }
}
