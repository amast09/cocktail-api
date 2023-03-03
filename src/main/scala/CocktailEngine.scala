package cocktail.api

import cats.data.NonEmptyList

final case class PotentialCocktail(
  cocktail: Cocktail,
  missingIngredients: List[Ingredient]
)

final case class CocktailEngine(cocktails: List[Cocktail]) {
  def getPotentialCocktails(ingredients: List[Ingredient]): List[PotentialCocktail] =
    cocktails
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
}
