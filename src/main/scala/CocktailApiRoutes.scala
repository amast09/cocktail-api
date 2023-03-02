package cocktail.api

import cats.effect.Sync
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

object CocktailApiRoutes {

  def cocktailRoutes[F[_]: Sync](H: CocktailService[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] { case GET -> Root / "hello" / name =>
      for {
        greeting <- H.hello(CocktailService.Name(name))
        resp     <- Ok(greeting)
      } yield resp
    }
  }
}
