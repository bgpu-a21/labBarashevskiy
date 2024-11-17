-module(integral).
-export([main/0, calculate_integral/5, worker/5, collect_results/2]).

% Главная функция программы
main() ->
    % Параметры интегрирования
    A = 0.0,
    B = math:pi(),
    H = 1.0e-6,
    F = fun math:sin/1, % Задаем функцию sin(x)
    
    % Выполняем вычисления для числа потоков от 1 до 20
    Results = [run_calculation(N, A, B, H, F) || N <- lists:seq(1, 20)],
    
    % Сортируем результаты по времени выполнения
    SortedResults = lists:sort(fun({_, T1, _}, {_, T2, _}) -> T1 =< T2 end, Results),
    
    % Выводим отсортированные результаты
    lists:foreach(
    fun({Threads, Time, Result}) ->
        io:format("~p поток(ов) - ~.2f мс. (Результат: ~.2f)~n", [Threads, float(Time), Result])
    end,
    SortedResults).

% Запуск вычислений для заданного числа потоков
run_calculation(NumThreads, A, B, H, F) ->
    Start = erlang:monotonic_time(millisecond),
    Result = calculate_integral(A, B, H, F, NumThreads),
    End = erlang:monotonic_time(millisecond),
    {NumThreads, End - Start, Result}.

% Основной расчет интеграла
calculate_integral(A, B, H, F, NumThreads) ->
    N = round((B - A) / H),
    ChunkSize = N div NumThreads,

    % Создаем потоки
    Pids = lists:map(fun(I) -> 
        Start = A + I * ChunkSize * H,
        End = Start + ChunkSize * H,
        spawn(?MODULE, worker, [Start, End, H, F, self()])
    end, lists:seq(0, NumThreads - 1)),

    % Собираем результаты
    Results = collect_results(Pids, []),
    lists:sum(Results).

% Рабочий поток
worker(Start, End, H, F, Parent) ->
    Result = calculate_chunk(Start, End, H, F),
    Parent ! {self(), Result}.

% Генерация последовательности с плавающей точкой
generate_sequence(Start, End, _Step) when Start >= End ->
    [];
generate_sequence(Start, End, Step) ->
    [Start | generate_sequence(Start + Step, End, Step)].

% Расчет на участке методом трапеций
calculate_chunk(Start, End, H, F) ->
    lists:foldl(
        fun(X, Acc) -> Acc + (F(X) + F(X + H)) * H / 2 end,
        0,
        generate_sequence(Start, End, H)).

% Сбор результатов от потоков
collect_results([], Acc) ->
    Acc;
collect_results([Pid | Rest], Acc) ->
    receive
        {Pid, Result} -> collect_results(Rest, [Result | Acc])
    end.
