package cocktail.api

import endpoints4s.openapi.model.{Info, OpenApi}
import org.http4s.{HttpRoutes, MediaType}
import org.http4s.dsl.io._
import cats.effect.IO
import org.http4s.headers.`Content-Type`

object MixologistOpenApiDocumentation
    extends MixologistRoutes
    with endpoints4s.openapi.Endpoints
    with endpoints4s.openapi.JsonEntitiesFromSchemas {

  override def coproductEncoding: CoproductEncoding = CoproductEncoding.OneOfWithBaseRef

  val api: OpenApi =
    openApi(
      Info(title = "Mixologist API", version = "1.0.0") // TODO: Drive in from environment
    )(getIngredients, getPotentialCocktails)

  val apiJson: String = OpenApi.stringEncoder.encode(api)

  val route: HttpRoutes[IO] = HttpRoutes.of[IO] { case GET -> Root / "openapi.json" =>
    Ok(apiJson, `Content-Type`(MediaType.application.json))
  }
}
