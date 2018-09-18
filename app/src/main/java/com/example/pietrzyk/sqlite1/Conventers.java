package com.example.pietrzyk.sqlite1;



public class Conventers {

    // @@@@@@@@ ODLEGLOSCI @@@@@@@@

    public double kilometersToMiles (float kilometers){
        return kilometers * 0.62137;
    }

    public double milesToKilometers (float miles){
        return miles / 0.62137;
    }

    // @@@@@@@@ MIARY @@@@@@@@
    // Litry > Gal

    public double litresToGalUK (float litres){
        return litres * 0.21997;
    }

    public double litresToGalUS (float litres){
        return litres * 0.26417;
    }

    // Gal > Litry

    public double galUKtoLitres (float gal_uk){
        return gal_uk / 0.21997;
    }

    public double galUStoLitres (float gal_us){
        return gal_us / 0.26417;
    }

    // Gal <> Gal

    public double galUKtoUS (float gal_uk){
        return gal_uk * 1.2009;
    }

    public double galUStoUK (float gal_us){
        return gal_us * 0.83267;
    }

    // @@@@@@@@ SPALANIA @@@@@@@@
    // l/100km > mpg

    public double lp100ToMpgUK (double lp100){
        //return 282 / lp100;
        return (100*4.54609)/(1.609344*lp100);
    }

    public double lp100ToMpgUS (double lp100){
        //return 235 / lp100;
        return (100*3.785411784)/(1.609344*lp100);
    }

    // mpg > l/100km

    public double mpgUKtoLp100 (float mpg_uk){
        //return 282/mpg_uk;
        return (100*4.54609)/(1.609344*mpg_uk);
    }

    public double mpgUStoLp100 (float mpg_us){
        //return 235/mpg_us;
        return (100*3.785411784)/(1.609344*mpg_us);
    }

    // mpg <> mpg

    public double mpgUKtoMpgUS (float mpg_uk){
        return (3.785411784/4.54609) * mpg_uk;
    }

    public double mpgUStoMpgUK (float mpg_us){
        return (4.54609/3.785411784) * mpg_us;
    }
}

