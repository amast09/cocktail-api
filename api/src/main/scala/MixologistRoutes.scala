package cocktail.api

import endpoints4s.algebra.Tag
import endpoints4s.generic
import cats.data.NonEmptyList

trait MixologistRoutes
    extends endpoints4s.algebra.Endpoints
    with endpoints4s.algebra.JsonEntitiesFromSchemas
    with generic.JsonSchemas {

  private val baseDocs    = EndpointDocs().withTags(List(Tag("Mixologist")))
  private val baseApiPath = path / "api"

  case class IngredientsResponse(data: List[Ingredient])

  case class PotentialCocktailsRequest(ingredients: List[Ingredient])

  case class PotentialCocktailsResponse(data: List[PotentialCocktail])

  implicit private val glassJsonSchema: JsonSchema[Glass]                           = genericTagged
  implicit private val amountJsonSchema: JsonSchema[Amount]                         = genericTagged
  implicit private val ingredientJsonSchema: JsonSchema[Ingredient]                 = genericJsonSchema
  implicit private val cocktailIngredientJsonSchema: JsonSchema[CocktailIngredient] = genericJsonSchema
  implicit private val cocktailJsonSchema: JsonSchema[Cocktail] =
    (field[String]("name") zip field[Glass]("glass") zip field[List[CocktailIngredient]]("ingredients"))
      .xmap(tuple => Cocktail(tuple._1, tuple._2, NonEmptyList.fromList(tuple._3).get))(cocktail =>
        (cocktail.name, cocktail.glass, cocktail.ingredients.toList)
      )
  implicit private val potentialCocktailJsonSchema: JsonSchema[PotentialCocktail] = genericJsonSchema
  implicit private val ingredientsResponseJsonSchema: JsonSchema[IngredientsResponse] =
    genericJsonSchema
  implicit private val potentialCocktailsRequestJsonSchema: JsonSchema[PotentialCocktailsRequest]   = genericJsonSchema
  implicit private val potentialCocktailsResponseJsonSchema: JsonSchema[PotentialCocktailsResponse] = genericJsonSchema

  val getIngredients = endpoint(
    request = get(baseApiPath / "ingredients"),
    response = ok(jsonResponse[IngredientsResponse]),
    docs = baseDocs
      .withSummary(Some("List of available ingredients"))
      .withDescription(Some("Get all the potential cocktail ingredients"))
  )

  val getPotentialCocktails = endpoint(
    request = post(baseApiPath / "potential-cocktails", jsonRequest[PotentialCocktailsRequest]),
    response = ok(jsonResponse[PotentialCocktailsResponse]),
    docs = baseDocs
      .withSummary(Some("List of potential cocktails"))
      .withDescription(Some("Get all the potential cocktails given a list of ingredients"))
  )

  private def notImplemented[A, B]: A => B = _ => ???
}
