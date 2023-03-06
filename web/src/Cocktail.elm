module Cocktail exposing (Ingredient, IngredientsResponse, ingredientsResponseDecoder)

import Json.Decode exposing (Decoder, field, string)


type alias Ingredient =
    { name : String }

  


ingredientDecoder : Decoder Ingredient
ingredientDecoder =
    Json.Decode.map Ingredient
        (field "name" string)


type alias IngredientsResponse =
    { ingredients : List Ingredient }


ingredientsResponseDecoder : Decoder IngredientsResponse
ingredientsResponseDecoder =
    Json.Decode.map IngredientsResponse
        (field "data" (Json.Decode.list ingredientDecoder))
