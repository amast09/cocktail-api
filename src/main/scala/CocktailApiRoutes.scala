package cocktail.api

import cats.effect.Sync
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._
import io.circe.syntax._
import io.circe.generic.auto._
import org.http4s._
import cats.effect.IO
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._

import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._

object CocktailApiRoutes {

  case class PotentialCocktailsJsonPayload(ingredients: List[Ingredient])
  implicit val decoder = jsonOf[IO, PotentialCocktailsJsonPayload]

  def cocktailRoutes(cocktailService: CocktailService): HttpRoutes[IO] = {
    val dsl = new Http4sDsl[IO] {}
    import dsl._

    HttpRoutes.of[IO] {
      case GET -> Root / "ingredients" =>
        for {
          ingredients <- cocktailService.getIngredients()
          resp        <- Ok(ingredients.asJson)
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
