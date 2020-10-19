package java.com.wine.to.up.am.parser.service.service.impl;

import com.wine.to.up.am.parser.service.domain.entity.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : bgubanov
 * @since : 13.10.2020
 **/

public class SampleRepositoryObjects {

    private static double winePrice = 1.0;
    static Wine getSampleWineEntity() {
        return new Wine("0", "http", getSampleBrandEntity(),
                getSampleCountryEntity(), 0.0, 0.0, getSampleColorEntity(),
                getSampleSugarEntity(), getSampleListGrapeEntity(), winePrice++);
    }

    private static Brand getSampleBrandEntity() {
        return new Brand("0", "brand");
    }

    private static Country getSampleCountryEntity() {
        return new Country("0", "country");
    }

    private static Sugar getSampleSugarEntity() {
        return new Sugar("0", "sugar");
    }

    private static Color getSampleColorEntity() {
        return new Color("0", "color");
    }

    private static List<Grape> getSampleListGrapeEntity() {
        var list = new ArrayList<Grape>();
        list.add(new Grape("0", "grape"));
        return list;
    }
    public static Grape getSampleGrapeEntity() {
        return new Grape("0", "grape");
    }
}
