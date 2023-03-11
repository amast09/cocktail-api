module MixologistAppSpec exposing (suite)

import Expect
import Test exposing (Test)


suite : Test
suite =
    Test.describe "Passing test" [ Test.test "passes" (\_ -> Expect.equal 1 1) ]



-- TODO: Add tests like this :)
-- suite : Test
-- suite =
--     Test.describe "HelloWorld"
--         [ Test.test "renders and runs helloWorld" <|
--             \_ ->
--                 helloWorld 0
--                     |> Query.fromHtml
--                     |> Query.has [ Html.text "Hello, Vite + Elm!" ]
--         , Test.test "Displays the current count" <|
--             \_ ->
--                 helloWorld 5
--                     |> Query.fromHtml
--                     |> Query.has [ Html.text "Count is: 5" ]
--         , Test.test "clicking on the + button sends an increment message" <|
--             \_ ->
--                 helloWorld 0
--                     |> Query.fromHtml
--                     |> Query.find [ Html.tag "button", Html.containing [ Html.text "+" ] ]
--                     |> Event.simulate Event.click
--                     |> Event.expect Msg.Increment
--         , Test.test "clicking on the - button sends a decrement message" <|
--             \_ ->
--                 helloWorld 0
--                     |> Query.fromHtml
--                     |> Query.find [ Html.tag "button", Html.containing [ Html.text "-" ] ]
--                     |> Event.simulate Event.click
--                     |> Event.expect Msg.Decrement
--         ]
