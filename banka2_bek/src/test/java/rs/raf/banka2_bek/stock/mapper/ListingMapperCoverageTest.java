package rs.raf.banka2_bek.stock.mapper;

import org.junit.jupiter.api.Test;
import rs.raf.banka2_bek.stock.model.Listing;
import rs.raf.banka2_bek.stock.model.ListingDailyPriceInfo;
import rs.raf.banka2_bek.stock.model.ListingType;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ListingMapperCoverageTest {

    @Test
    void toDtoReturnsNullForNullListing() {
        assertThat(ListingMapper.toDto(null)).isNull();
    }

    @Test
    void toDailyPriceDtoReturnsNullForNullInfo() {
        assertThat(ListingMapper.toDailyPriceDto(null)).isNull();
    }

    @Test
    void toDailyPriceDtoMapsAllFields() {
        ListingDailyPriceInfo info = new ListingDailyPriceInfo();
        info.setPrice(new BigDecimal("100"));
        info.setHigh(new BigDecimal("110"));
        info.setLow(new BigDecimal("90"));
        info.setChange(new BigDecimal("5"));
        info.setVolume(1000L);
        assertThat(ListingMapper.toDailyPriceDto(info).getPrice()).isEqualByComparingTo("100");
    }

    @Test
    void calculateChangePercentNullPrice() {
        Listing l = new Listing();
        l.setPriceChange(BigDecimal.ONE);
        assertThat(ListingMapper.calculateChangePercent(l)).isNull();
    }

    @Test
    void calculateChangePercentNullChange() {
        Listing l = new Listing();
        l.setPrice(BigDecimal.TEN);
        assertThat(ListingMapper.calculateChangePercent(l)).isNull();
    }

    @Test
    void calculateChangePercentPreviousZero() {
        Listing l = new Listing();
        l.setPrice(new BigDecimal("5"));
        l.setPriceChange(new BigDecimal("5"));
        assertThat(ListingMapper.calculateChangePercent(l)).isNull();
    }

    @Test
    void calculateChangePercentNormal() {
        Listing l = new Listing();
        l.setPrice(new BigDecimal("110"));
        l.setPriceChange(new BigDecimal("10"));
        assertThat(ListingMapper.calculateChangePercent(l)).isEqualByComparingTo("10");
    }

    @Test
    void calculateMaintenanceMarginNullPrice() {
        Listing l = new Listing();
        l.setListingType(ListingType.STOCK);
        assertThat(ListingMapper.calculateMaintenanceMargin(l)).isNull();
    }

    @Test
    void calculateMaintenanceMarginNullType() {
        Listing l = new Listing();
        l.setPrice(BigDecimal.TEN);
        assertThat(ListingMapper.calculateMaintenanceMargin(l)).isNull();
    }

    @Test
    void calculateMaintenanceMarginStock() {
        Listing l = new Listing();
        l.setPrice(new BigDecimal("100"));
        l.setListingType(ListingType.STOCK);
        assertThat(ListingMapper.calculateMaintenanceMargin(l)).isEqualByComparingTo("50.0000");
    }

    @Test
    void calculateMaintenanceMarginForexNullContractSize() {
        Listing l = new Listing();
        l.setPrice(new BigDecimal("100"));
        l.setListingType(ListingType.FOREX);
        assertThat(ListingMapper.calculateMaintenanceMargin(l)).isEqualByComparingTo("10.0000");
    }

    @Test
    void calculateMaintenanceMarginFutures() {
        Listing l = new Listing();
        l.setPrice(new BigDecimal("100"));
        l.setListingType(ListingType.FUTURES);
        l.setContractSize(10);
        assertThat(ListingMapper.calculateMaintenanceMargin(l)).isEqualByComparingTo("100.0000");
    }

    @Test
    void calculateInitialMarginCostNullMaintenanceReturnsNull() {
        Listing l = new Listing();
        // no price → maintenance is null → initial is null
        assertThat(ListingMapper.calculateInitialMarginCost(l)).isNull();
    }

    @Test
    void calculateInitialMarginCostForStock() {
        Listing l = new Listing();
        l.setPrice(new BigDecimal("100"));
        l.setListingType(ListingType.STOCK);
        assertThat(ListingMapper.calculateInitialMarginCost(l)).isEqualByComparingTo("55.0000");
    }

    @Test
    void calculateMarketCapNonStock() {
        Listing l = new Listing();
        l.setListingType(ListingType.FOREX);
        assertThat(ListingMapper.calculateMarketCap(l)).isNull();
    }

    @Test
    void calculateMarketCapNullShares() {
        Listing l = new Listing();
        l.setListingType(ListingType.STOCK);
        l.setPrice(BigDecimal.TEN);
        assertThat(ListingMapper.calculateMarketCap(l)).isNull();
    }

    @Test
    void calculateMarketCapNullPrice() {
        Listing l = new Listing();
        l.setListingType(ListingType.STOCK);
        l.setOutstandingShares(1000L);
        assertThat(ListingMapper.calculateMarketCap(l)).isNull();
    }

    @Test
    void calculateMarketCapStock() {
        Listing l = new Listing();
        l.setListingType(ListingType.STOCK);
        l.setOutstandingShares(1000L);
        l.setPrice(new BigDecimal("50"));
        assertThat(ListingMapper.calculateMarketCap(l)).isEqualByComparingTo("50000.00");
    }

    @Test
    void toDtoNullListingType() {
        Listing l = new Listing();
        l.setId(1L);
        l.setTicker("X");
        l.setName("X Inc");
        l.setExchangeAcronym("NYSE");
        // listingType null
        assertThat(ListingMapper.toDto(l).getListingType()).isNull();
    }
}
