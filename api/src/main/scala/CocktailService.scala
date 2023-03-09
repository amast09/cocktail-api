package cocktail.api

import cats.effect.IO

// TODO: Rename to MixologistService
trait CocktailService {
  def getIngredients(): IO[List[Ingredient]]
  def getPotentialCocktails(ingredients: List[Ingredient]): IO[List[PotentialCocktail]]
}

final case class PotentialCocktail(
  cocktail: Cocktail,
  missingIngredients: List[Ingredient]
)

case class CocktailServiceFromList(cocktails: List[Cocktail]) extends CocktailService {
  def getIngredients(): IO[List[Ingredient]] =
    IO.pure(
      cocktails
        .flatMap(_.ingredients.map(_.ingredient).toList)
        .distinct
    )

  def getPotentialCocktails(ingredients: List[Ingredient]): IO[List[PotentialCocktail]] = {
    val potentialCocktails = cocktails
      .foldLeft(List.empty[PotentialCocktail]) { (potentialCocktails, nextPotentialCocktail) =>
        val missingIngredients = nextPotentialCocktail.ingredients.foldLeft(List.empty[Ingredient]) {
          (missingIngredients, nextCocktailIngredient) =>
            val ingredientIsAvailable =
              ingredients.exists(_ == nextCocktailIngredient.ingredient)

            ingredientIsAvailable match {
              case true  => missingIngredients
              case false => nextCocktailIngredient.ingredient +: missingIngredients
            }
        }

        PotentialCocktail(nextPotentialCocktail, missingIngredients) +: potentialCocktails
      }
      .sortBy(potentialCocktail => potentialCocktail.missingIngredients.length)

    IO.pure(potentialCocktails)
  }
}
