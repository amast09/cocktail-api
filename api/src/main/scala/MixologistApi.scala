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

case class MixologistApi(cocktailService: CocktailService)
    extends endpoints4s.http4s.server.Endpoints[IO]
    with MixologistRoutes
    with endpoints4s.http4s.server.JsonEntitiesFromSchemas {

  val routes: HttpRoutes[IO] = HttpRoutes.of(
    routesFromEndpoints(
      getIngredients.implementedByEffect { _ =>
        cocktailService.getIngredients().map(IngredientsResponse)
      },
      getPotentialCocktails.implementedByEffect { request =>
        cocktailService.getPotentialCocktails(request.ingredients).map(PotentialCocktailsResponse)
      }
    )
  )
}

case class MixologistApi2(cocktailService: CocktailService) {
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
          val foo = cocktailService.getIngredients().map(r => Right(IngredientsResponse(r)))
          foo
        },
        getPotentialCocktailsEndpoint.serverLogic[IO] { payload =>
          val foo =
            cocktailService.getPotentialCocktails(payload.ingredients).map(r => Right(PotentialCocktailsResponse(r)))
          foo
        }
      )
    )

  val docsRoute: HttpRoutes[IO] = HttpRoutes.of[IO] { case GET -> Root / "openapi.yaml" =>
    val docs = OpenAPIDocsInterpreter()
      .toOpenAPI(List(getIngredientsEndpoint, getPotentialCocktailsEndpoint), "Mixologist", "1.0")
      .toYaml
    Ok(docs)
  }
}
