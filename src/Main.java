import com.google.gson.*;

import java.io.FileReader;
import java.util.Scanner;

public class Main {

    static class BeerSelect{
        JsonArray beers;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        public BeerSelect (JsonArray beers_ ) {
            this.beers = beers_;
        }

        public String groupByBrand (JsonArray beers) {
            JsonArray brandBeers = new JsonArray();
            String brand;
            String brandJson;
            JsonObject brandObject;

            int brandIdx;
            for (JsonElement beer : beers){
                brand = beer.getAsJsonObject().get("brand").toString();
                brandJson = "{ \"brand\": " + brand+" }";
                brandObject = new JsonParser().parse(brandJson).getAsJsonObject();
                brandIdx = getBrandIdx(brandBeers, brand);

                if (brandIdx != -1) {
                    brandBeers.get(brandIdx).getAsJsonObject().get("beers").getAsJsonArray().add(beer);
                } else {
                    JsonArray brandArray = new JsonArray();
                    brandArray.add( beer.getAsJsonObject() );
                    brandBeers.add(brandObject);
                    brandIdx = getBrandIdx(brandBeers, brand);
                    brandBeers.get(brandIdx).getAsJsonObject().add("beers", brandArray);
                }
            }
            return toString(brandBeers);
        }

        public int getBrandIdx (JsonArray brandBeers, String brand){
            for (int i=0; i < brandBeers.size(); i++){
                if( brandBeers.get(i).getAsJsonObject().get("brand").toString().equals(brand)) {
                    return i;
                }
            }
            return -1;
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
                          "3: Close this application";
            System.out.println(menu);
            Scanner sc = new Scanner(System.in);
            String userInput = sc.nextLine();
            while (!userInput.equals("3")){
                switch (userInput){
                    case "1":
                        System.out.println("Get beers");
                        System.out.println("Beers: "+ beerSelect.toString(beerSelect.beers) );
                        System.out.println(menu);
                        break;
                    case "2":
                        System.out.println("Get grouped beers by brand");
                        System.out.println("Brand: " + beerSelect.groupByBrand(beers_));
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
