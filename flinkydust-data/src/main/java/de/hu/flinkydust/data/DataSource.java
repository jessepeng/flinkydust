package de.hu.flinkydust.data;

import de.hu.flinkydust.data.aggregator.AggregatorFunction;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Basis-Interface für die drei Operationen, die auf einer Data Source möglich sein sollen.
 *
 * Created by Jan-Christopher on 09.11.2016.
 */
public interface DataSource<T>
{

    /**
     * Wählt Datensätze aus der Datenquelle aus, die ein gegebenes Prädikat erfüllen.
     * @param predicate
     *          Das Prädikat, das die Datensätze erfüllen müssen
     * @return
     *          Eine neue DataSource mit ausschließlich den Datensätzen, die das Prädikat erfüllen.
     */
    DataSource<T> selection(Predicate<T> predicate);

    /**
     * Projiziert die Datensätze in dieser Datenquelle auf einen neuen Datentyp.
     * @param projector
     *          Funktion, die einen Datensatz das ursprünglich in dieser DataSource gespeicherten Datensätze
     *          in den gewünschten neuen Datensatz umwandelt.
     * @param <R>
     *          Typ des neuen Datensatzes
     * @return
     *          Eine neue DataSource mit allen Datensätzen, nachdem sie in den neuen Datentyp projiziert wurden.
     */
    <R> DataSource<R> projection(Function<T, R> projector);

    /**
     * Reduziert die Datensätze in dieser Datenquelle auf einen einzigen Datensatz.
     * @param reducer
     *           Funktion, die zwei Datensätze des in dieser DataSource gespeicherten Typs erhält und einen kombinierten zurückgibt.
     * @return
     *           Eine neue DataSource mit einem reduzierte Datensatz.
     */
    DataSource<T> reduce(BiFunction<T, T, T> reducer);

    /**
     * Aggregiert die gewünschte Anzahl Datensätze mit der angegebenen Aggregationsfunktion.
     * @param aggregator
     *          Funktion, die die Datensätze aggregiert.
     * @param count
     *          Anzahl Datensätze, die aggregiert werden sollen. Wenn count = -1, aggregiere alle Datensätze.
     * @return
     *          Eine neue DataSource mit dem aggregierten Datensatz.
     * @param <R>
     *          Datentyp, der den Zwischenschritt im Aggregator angibt.
     *
     */
    default <R> DataSource<T> aggregation(AggregatorFunction<T, R> aggregator, int count) {
        return aggregator.aggregate(this, count);
    }

    /**
     * Aggregiert alle Datensätze mit der angegebenen Aggregationsfunktion.
     * @param aggregator
     *          Funktion, die die Datensätze aggregiert.
     * @return
     *          Eine neue DataSource mit dem aggregierten Datensatz.
     * @param <R>
     *          Datentyp, der den Zwischenschritt im Aggregator angibt.
     */
    default  <R> DataSource<T> aggregation(AggregatorFunction<T, R> aggregator) {
        return aggregation(aggregator, -1);
    }

    /**
     * Gibt alle Datensätze dieser DataSource als Collection aus.
     * @return
     *          Alle Datensätze, die in dieser DataSource gespeichert, als Collection.
     * @throws Exception
     *          Wirft eine Exception, wenn die Datensätze nicht ausgegeben werden konnten.
     */
    List<T> collect() throws Exception;


    /**
     * Gibt die ersten n Datensätze dieser DataSource zurück.
     * @param count
     *          Anzahl an Datensätzen, die zurückgegeben werden sollen.
     * @return
     *          Neue DataSource mit der gewünschten Anzahl an Datensätzen.
     */
    DataSource<T> firstN(int count);

}
