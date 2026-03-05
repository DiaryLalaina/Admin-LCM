package com.work.cashier.data_transfert_object.unitOption;

public class UnitConverter {

    public static double convert(double value, Unit from, Unit to) {
        if (from == to) return value;

        double valueInKg = switch (from) {
            case GRAM -> value / 1000;
            case KILOGRAM, LITER -> value;
        };

        return switch (to) {
            case GRAM -> valueInKg * 1000;
            case KILOGRAM, LITER -> valueInKg;
        };
    }
    public static String abbreviate(String unitType){
        int count = 0;
        String[] units = {"KG","G","L"};
        for(Unit unit : Unit.values()){
            if(unitType.equals(String.valueOf(unit)))   return units[count];
            count++;
        }
        return "";
    }
}
