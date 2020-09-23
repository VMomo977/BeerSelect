import com.google.gson.*;

import java.io.FileReader;
import java.util.*;

public class Main {

    static class BeerSelect{
        JsonArray beers;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        public BeerSelect (JsonArray beers_ ) {
            this.beers = beers_;
        }

        /**
         * the Function should return an array of Brand objects which contain the array of Beers of that Brand
         * @return [ {brand: Dreher, beers: [ {},{}... ], brand: Balatoni Világos, beers: [...], ... } ]
         */
        public JsonArray groupByBrand () {
            String brand;
            JsonObject tmpBrands = new JsonObject();

            for (JsonElement beer : beers){
                brand = beer.getAsJsonObject().get("brand").toString();
                if (tmpBrands.has(brand)){
                    tmpBrands.get(brand).getAsJsonArray().add(beer);
                } else {
                    JsonArray beerArray = new JsonArray();
                    beerArray.add(beer);
                    tmpBrands.add(brand, beerArray);
                }
            }

            String brandJson;
            JsonObject brandObject;
            JsonArray brandBeers = new JsonArray();

            for (Map.Entry<String, JsonElement> b : tmpBrands.entrySet()){
                brandJson =  "{\"brand\": " + b.getKey() +",\"beers\": " + b.getValue() + "}";
                brandObject = new JsonParser().parse(brandJson).getAsJsonObject();
                brandBeers.add(brandObject);
            }
            return brandBeers;
        }

        public Set <String> getTypes() {
            Set<String> types = new HashSet<>();
            for(JsonElement beer : beers) {
                types.add( beer.getAsJsonObject().get("type").toString() );
            }

            return types;
        }

        /**
         * the Function should take BeerType and return all the beers in an array of that type
         @param type
         @return example: {type: "Barna", beers: [....]}
         */
        public String filterBrandsByType (String type){
            boolean sameType;
            String typeJson;
            JsonObject typeBeers = new JsonObject();
            for (JsonElement beer : beers){
                sameType = beer.getAsJsonObject().get("type").getAsString().equals(type);
                if (sameType) {
                    if (typeBeers.has("beers")){
                        typeBeers.get("beers").getAsJsonArray().add(beer);
                    } else {
                        typeJson =  "{\"type\": " + type +",\"beers\": [" + beer + "]}";
                        typeBeers = new JsonParser().parse(typeJson).getAsJsonObject();
                    }
                }

            }
            return toString(typeBeers);
        }

        /**
         * a Function that returns the name of the brand that has the cheapest average price
         * @return brandName
         */
        public String getCheapestBrand () {
            JsonArray brands = groupByBrand();
            JsonArray beersByBrand;
            Double tmp_price;
            Double priceSum = 0.0;
            Double priceAvg;
            JsonArray firstBrandBeers = brands.get(0).getAsJsonObject().get("beers").getAsJsonArray();

            for ( JsonElement beer : firstBrandBeers) {
                tmp_price = beer.getAsJsonObject().get("price").getAsDouble();
                priceSum += tmp_price;
            }

            priceAvg = priceSum / firstBrandBeers.size();
            Double minPriceBrand = priceAvg;
            String minPriceBrandName = brands.get(0).getAsJsonObject().get("brand").getAsString();

            for (int i=1; i < brands.size(); i++){
                beersByBrand = brands.get(i).getAsJsonObject().get("beers").getAsJsonArray();
                priceSum = 0.0;

                for ( JsonElement beer : beersByBrand) {
                    tmp_price = beer.getAsJsonObject().get("price").getAsDouble();
                    priceSum += tmp_price;
                }

                priceAvg = priceSum / beersByBrand.size();

                if (priceAvg < minPriceBrand) {
                    minPriceBrand = priceAvg;
                    minPriceBrandName = brands.get(i).getAsJsonObject().get("brand").getAsString();
                }

            }
            return minPriceBrandName;
        }

        public String toString(Object object) {

            return gson.toJson(object);
        }

    }

    private static String jsonFile="beers.json";


    public static void main(String[] args) {
        System.out.println("Hello World!");

        JsonParser parser = new JsonParser();
        try {

            Object obj = parser.parse(new FileReader(jsonFile));
            JsonArray beers_ = (JsonArray) obj;

            BeerSelect beerSelect = new BeerSelect(beers_);
            String menu = "You can choose between these options by enter one of their's number: \n" +
                          "1: Get beers \n" +
                          "2: Get grouped beers by brand \n" +
                          "3: Get beers by a given type \n" +
                          "4: Get the cheapest brand \n" +
                          "-1: Close this application";
            System.out.println(menu);
            System.out.println("Type the option's number: ");
            Scanner sc = new Scanner(System.in);
            String userInput = sc.nextLine();
            while (!userInput.equals("-1")){
                switch (userInput){
                    case "1":
                        System.out.println("Get beers");
                        System.out.println("Beers: \n"+ beerSelect.toString(beerSelect.beers) );
                        System.out.println(menu);
                        break;
                    case "2":
                        System.out.println("Get grouped beers by brand");
                        System.out.println("Brands: \n" + beerSelect.toString( beerSelect.groupByBrand() ));
                        System.out.println(menu);
                        break;
                    case "3":
                        System.out.println("Get beers by a given type");
                        Set <String> types = beerSelect.getTypes();
                        System.out.println("You can choose between these beer types by type theirs name:");
                        for (String type : types){
                            System.out.println(type);
                        }
                        System.out.println("Type the beer type: ");
                        Scanner sc2 = new Scanner(System.in);
                        String type = sc2.nextLine();
                        System.out.println("Beers by type: \n" + beerSelect.filterBrandsByType(type) );
                        System.out.println(menu);
                        break;
                    case "4":
                        System.out.println("Get the cheapest brand");
                        System.out.println("Cheapest brand name: " + beerSelect.getCheapestBrand() );
                        System.out.println(menu);
                        break;
                    default:
                        System.out.println("Invalid option");
                }
                userInput = sc.nextLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
