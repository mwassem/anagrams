-module (wheedledict).
-compile (export_all).
-import (bag, [bag/1]).
-export ([snarf/0]).

dets_size (Dets)->
    {value, {size, S}} = lists:keysearch(size, 1, dets:info (Dets)),
    S.

snarf ()->
    {ok, Dets} = dets:open_file (make_ref (), [{file, "dict.dets"}]),
    case dets_size (Dets) of
        0 -> 
            {ok, S} = file:open (
                        %%"words-small",
                        "/usr/share/dict/words",
                        read),
            HT = hash_from_stream (S, dict:new ()),
            file:close (S),
            dict:map (fun (K, V) -> 
                              dets:insert (Dets, {K, V})
                              end,
                      HT),
            io:format ("Stored ~p.~n",
                       [dict:fold (fun (_Key, Value, AccIn)-> 
                                           {{bags, B}, {words, W}} = AccIn,
                                           {{bags, B + 1},
                                            {words, W+ length (Value)}}
                                   end,
                                   {{bags, 0}, {words, 0}},
                                   HT)]);
        _ -> whatever
    end,
    BigList = dets:traverse (Dets, fun (Object) -> {continue, Object} end),
    dets:close (Dets),
    BigList.

letters_only (String) ->
    [X || X <- String,  (X >= $a andalso X =< $z)].

acceptable (Word, _Sym)->
    io:format ("Testing ~p...", [Word]),
    RV = acceptable (Word),
    io:format ("=> ~p~n", [RV]),
    RV.

acceptable ([]) -> false;
acceptable ([$a])-> true;
acceptable ([$i])-> true;
acceptable ([_SingleLetter]) -> false;
acceptable ([H|T]) ->
    case lists:member (H, [$a, $e, $i, $o, $u]) of
        true -> true;
        _ -> acceptable (T)
    end.

hash_from_stream (S, HT) ->
    case io:get_line (S, '') of
        eof ->
            HT;
        Line ->
            %% Strip trailing newlines by (*sigh*) reversing the
            %% string, matching, then re-reversing.
            Chars = lists:reverse (string:to_lower (Line)),
            case Chars of
                [$\n | T] ->
                    Word = lists:reverse (letters_only (T)),
                    case acceptable (Word) of
                        true  -> 
                            hash_from_stream(S,
                                             dict:update (bag (Word),
                                                          fun (Val) ->
                                                                  case lists:member (Word, Val) of
                                                                      true -> Val;
                                                                      _ -> [Word|Val]
                                                                  end
                                                          end,
                                                          [Word],
                                                          HT)) ;
                        _ -> 
                            hash_from_stream (S, HT)
                    end
            end
    end.
