package rs.raf.banka2_bek.interbank.protocol;

/**
 * Spec ref: protokol §2.7 Assets
 *
 * Sealed interface sa 3 variante: Monas (valutna sredstva), Stock (akcije),
 * OptionAsset (opcioni ugovori). Pri serijalizaciji u JSON se pojavljuje
 * polje `type` ('MONAS' | 'STOCK' | 'OPTION') + odgovarajuci `asset` payload.
 *
 * TODO: konfigurisati Jackson custom (de)serializer ili @JsonTypeInfo da
 * deserijalizuje JSON {type:'MONAS',asset:{...}} u pravu variantu.
 */
public sealed interface Asset permits Asset.Monas, Asset.Stock, Asset.OptionAsset {

    record Monas(MonetaryAsset asset) implements Asset {}

    record Stock(StockDescription asset) implements Asset {}

    record OptionAsset(OptionDescription asset) implements Asset {}
}
