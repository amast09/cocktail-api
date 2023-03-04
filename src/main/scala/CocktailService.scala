package cocktail.api

import cats.effect.IO

trait CocktailService {
  def getIngredients(): IO[List[Ingredient]]
}

case class CocktailServiceFromList(cocktails: List[Cocktail]) extends CocktailService {
  def getIngredients(): IO[List[Ingredient]] =
    IO.pure(
      cocktails
        .flatMap(_.ingredients.map(_.ingredient).toList)
        .distinct
    )
}
