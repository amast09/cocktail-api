module IngredientCheckbox exposing (Props, component)

import Cocktail exposing (Ingredient)
import Html exposing (Html, input, label, text)
import Html.Attributes exposing (checked, type_)
import Html.Events exposing (onCheck)
import Msg exposing (Msg)


type alias Props =
    { ingredient : Ingredient, isChecked : Bool, onCheck : Ingredient -> Msg }


component : Props -> Html Msg
component props =
    label [] [ text props.ingredient.name, input [ type_ "checkbox", onCheck (\_ -> props.onCheck props.ingredient), checked props.isChecked ] [] ]
