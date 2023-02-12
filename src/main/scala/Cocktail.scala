final case class Ingredient(name: String)

final case class CocktailIngredient(ingredient: Ingredient, amount: Amount)

sealed trait Amount
object Amount {
  case class Centiliter(quantity: Number) extends Amount
  case class Teaspoon(quantity: Number)   extends Amount
  case class Dash(quantity: Int)          extends Amount
  case class Drop(quantity: Int)          extends Amount
  case class Slice(quantity: Int)         extends Amount
  case class Sprig(quantity: Int)         extends Amount
  case class Pinch(quantity: Int)         extends Amount
  case class Relative(quantity: Double)   extends Amount // TODO: Make this better
  case class Splash()                     extends Amount
  case class ToTaste()                    extends Amount
  case class TopOff()                     extends Amount
}

final case class Cocktail(name: String, glass: String, ingredients: List[CocktailIngredient])
