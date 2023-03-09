package cocktail.api

import cats.effect.IO
import org.http4s.HttpRoutes



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
