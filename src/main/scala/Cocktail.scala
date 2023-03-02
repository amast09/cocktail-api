package cocktail.api

import cats.data.NonEmptyList

final case class Ingredient(name: String)
final case class CocktailIngredient(ingredient: Ingredient, amount: Amount)

sealed trait Amount
case class Centiliter(quantity: Double) extends Amount
case class Teaspoon(quantity: Double)   extends Amount
case class Relative(quantity: Double)   extends Amount
case class Slice(quantity: Double)      extends Amount
case class Dash(quantity: Int)          extends Amount
case class Drop(quantity: Int)          extends Amount
case class Sprig(quantity: Int)         extends Amount
case class Pinch(quantity: Int)         extends Amount
case class Splash()                     extends Amount
case class ToTaste()                    extends Amount
case class TopOff()                     extends Amount

sealed trait Glass
case object Martini        extends Glass
case object OldFashioned   extends Glass
case object Collins        extends Glass
case object Highball       extends Glass
case object ChampagneFlute extends Glass
case object Margarita      extends Glass
case object ChampagneTulip extends Glass
case object Hurricane      extends Glass
case object Shot           extends Glass
case object HotDrink       extends Glass
case object WhiteWine      extends Glass

final case class Cocktail(name: String, glass: Glass, ingredients: NonEmptyList[CocktailIngredient])
