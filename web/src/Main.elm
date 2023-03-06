module Main exposing (..)

import Browser
import Html exposing (..)
import Html.Events exposing (..)
import Http
import Json.Decode exposing (Decoder, field, list, string)



-- MAIN


main : Program () Model Msg
main =
    Browser.element
        { init = init
        , update = update
        , subscriptions = subscriptions
        , view = view
        }



-- MODEL


type Model
    = Failure
    | Loading
    | Success IngredientsResponse


init : () -> ( Model, Cmd Msg )
init _ =
    ( Loading, getIngredients )



-- UPDATE


type alias Ingredient =
    { name : String }


type alias IngredientsResponse =
    { ingredients : List Ingredient }


type Msg
    = RetryIngredientsRequest
    | IngredientsRequestComplete (Result Http.Error IngredientsResponse)


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        RetryIngredientsRequest ->
            ( Loading, getIngredients )

        IngredientsRequestComplete result ->
            case result of
                Ok quote ->
                    ( Success quote, Cmd.none )

                Err _ ->
                    ( Failure, Cmd.none )



-- SUBSCRIPTIONS


subscriptions : Model -> Sub Msg
subscriptions model =
    Sub.none



-- VIEW


view : Model -> Html Msg
view model =
    div []
        [ h2 [] [ text "Random Quotes" ]
        , viewQuote model
        ]


viewQuote : Model -> Html Msg
viewQuote model =
    case model of
        Failure ->
            div []
                [ text "Failed to load ingredients"
                , button [ onClick RetryIngredientsRequest ] [ text "Try Again!" ]
                ]

        Loading ->
            text "Loading..."

        Success ingredientsResponse ->
            ul []
                (List.map (\ingredient -> li [] [ text ingredient.name ]) ingredientsResponse.ingredients)



-- HTTP


getIngredients : Cmd Msg
getIngredients =
    Http.get
        { url = "http://localhost:8080/ingredients"
        , expect = Http.expectJson IngredientsRequestComplete ingredientsResponseDecoder
        }


ingredientDecoder : Decoder Ingredient
ingredientDecoder =
    Json.Decode.map Ingredient
        (field "name" string)


ingredientsResponseDecoder : Decoder IngredientsResponse
ingredientsResponseDecoder =
    Json.Decode.map IngredientsResponse
        (field "data" (list ingredientDecoder))
