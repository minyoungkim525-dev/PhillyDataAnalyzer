package common;

public class House {
    private Integer zip_code;
    private Integer market_value;
    private Integer total_livable_area;

    public House(Integer zip_code, Integer market_value, Integer total_livable_area) {
        this.zip_code = zip_code;
        this.market_value = market_value;
        this.total_livable_area = total_livable_area;
    }

    public Integer getZip_code() {
        return zip_code;
    }

    public Integer getMarket_value() {
        return market_value;
    }

    public Integer getTotal_livable_area() {
        return total_livable_area;
    }

    @Override
    public String toString() {
        return "House{" +
                "zip_code=" + zip_code +
                ", market_value=" + market_value +
                ", total_livable_area=" + total_livable_area +
                '}';
    }


}
