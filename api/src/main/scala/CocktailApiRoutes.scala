package cocktail.api

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import cats.effect.IO
import io.circe.generic.auto._
import io.circe.syntax._

import org.http4s.circe._

object MixologistApiRoutes {

  case class IngredientsResponse(data: List[Ingredient])
  implicit val IngredientsResponseDecoder = jsonOf[IO, IngredientsResponse]

  case class PotentialCocktailsJsonPayload(ingredients: List[Ingredient])
  implicit val PotentialCocktailsJsonPayloadDecoder = jsonOf[IO, PotentialCocktailsJsonPayload]

  def cocktailRoutes(cocktailService: CocktailService): HttpRoutes[IO] = {
    val dsl = new Http4sDsl[IO] {}
    import dsl._

    HttpRoutes.of[IO] {
      case GET -> Root / "ingredients" =>
        for {
          ingredients <- cocktailService.getIngredients()
          resp        <- Ok(IngredientsResponse(ingredients).asJson)
        } yield resp
      case req @ POST -> Root / "potential-cocktails" =>
        for {
          decodedPayload     <- req.as[PotentialCocktailsJsonPayload]
          potentialCocktails <- cocktailService.getPotentialCocktails(decodedPayload.ingredients)
          resp               <- Ok(potentialCocktails.asJson)
        } yield resp
    }
  }
}
