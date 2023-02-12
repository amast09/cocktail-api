package cocktail.api

import cats.Applicative
import cats.implicits._
import io.circe.{Encoder, Json}
import io.circe.generic.semiauto._
import org.http4s.EntityEncoder
import org.http4s.circe._

trait CocktailService[F[_]] {
  def hello(n: CocktailService.Name): F[CocktailService.Greeting]
}

object CocktailService {
  final case class Name(name: String) extends AnyVal

  /** More generally you will want to decouple your edge representations from your internal data structures, however
    * this shows how you can create encoders for your data.
    */
  final case class Greeting(greeting: String) extends AnyVal
  object Greeting {
    implicit val greetingEncoder: Encoder[Greeting] = deriveEncoder[Greeting]
    implicit def greetingEntityEncoder[F[_]]: EntityEncoder[F, Greeting] =
      jsonEncoderOf[F, Greeting]
  }

  def impl[F[_]: Applicative]: CocktailService[F] = new CocktailService[F] {
    def hello(n: CocktailService.Name): F[CocktailService.Greeting] =
      Greeting("Hello, " + n.name).pure[F]
  }
}
