package cocktail.api

import munit.ScalaCheckSuite
import munit.CatsEffectSuite
import munit.ScalaCheckEffectSuite
import org.scalacheck.Prop._
import org.scalacheck.Gen
import cats.data.NonEmptyList
import scala.util.Random
import cats.effect.IO
import org.scalacheck.effect.PropF

class CocktailServiceTest extends CatsEffectSuite with ScalaCheckEffectSuite {
  test("getPotentialCocktails returns all the cocktails with their missing ingredients") {
    PropF.forAllF(
      Generators.cocktailGen,
      Generators.nonEmptyListGen(Generators.cocktailIngredientGen),
      Generators.nonEmptyListGen(Generators.cocktailIngredientGen)
    ) {
      (
        baseCocktail: Cocktail,
        ingredientsPresent: NonEmptyList[CocktailIngredient],
        ingredientsMissing: NonEmptyList[CocktailIngredient]
      ) =>
        val allIngredientsForCocktail =
          NonEmptyList.fromList(Random.shuffle(ingredientsMissing.toList ++ ingredientsPresent.toList)).get
        val cocktail = baseCocktail.copy(ingredients = allIngredientsForCocktail)

        val potentialCocktailsIO = CocktailServiceFromList(List(cocktail))
          .getPotentialCocktails(ingredientsPresent.map(_.ingredient).toList)

        potentialCocktailsIO.map { potentialCocktails =>
          assertEquals(potentialCocktails.map(_.cocktail), List(cocktail))
          assertEquals(
            potentialCocktails.flatMap(_.missingIngredients).toSet,
            ingredientsMissing.map(_.ingredient).toList.toSet
          )
        }
    }
  }

  test("getPotentialCocktails sorts the cocktails by number of missing ingredients") {
    PropF.forAllF(
      Generators.cocktailGen,
      Generators.cocktailGen,
      Generators.cocktailGen,
      Generators.cocktailIngredientGen,
      Generators.cocktailIngredientGen,
      Generators.cocktailIngredientGen
    ) {
      (
        baseCocktail1: Cocktail,
        baseCocktail2: Cocktail,
        baseCocktail3: Cocktail,
        ingredient1: CocktailIngredient,
        ingredient2: CocktailIngredient,
        ingredient3: CocktailIngredient
      ) =>
        val cocktail1 = baseCocktail1.copy(ingredients = NonEmptyList(ingredient1, List()))
        val cocktail2 = baseCocktail2.copy(ingredients = NonEmptyList(ingredient1, List(ingredient2)))
        val cocktail3 = baseCocktail3.copy(ingredients = NonEmptyList(ingredient1, List(ingredient2, ingredient3)))
        val cocktails = Random.shuffle(List(cocktail1, cocktail2, cocktail3))

        val expectedOrder = List(cocktail1.name, cocktail2.name, cocktail3.name)

        val potentialCocktailsIO = CocktailServiceFromList(cocktails).getPotentialCocktails(List())

        potentialCocktailsIO.map { potentialCocktails =>
          assertEquals(potentialCocktails.map(_.cocktail.name), expectedOrder)
        }
    }
  }
}
