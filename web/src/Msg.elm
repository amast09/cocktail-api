module Msg exposing (Msg(..))

import Api.Data exposing (Ingredient, IngredientsResponse, PotentialCocktailsResponse)
import Http


type Msg
    = IngredientsRequestRetry
    | IngredientsRequestComplete (Result Http.Error IngredientsResponse)
    | PotentialCocktailsRequestSubmitted
    | PotentialCocktailsRequestComplete (Result Http.Error PotentialCocktailsResponse)
    | ToggleIngredient Ingredient
