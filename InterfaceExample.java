/**
 * Copyright (c) 2012, Brandon Mintern, EasyESI, Berkeley, CA
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   * Neither gson-interface nor the names of its contributors may be used
 *     to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BRANDON MINTERN OR EASYESI BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
import com.google.gson.*;
import java.lang.reflect.Type;

/**
 * @author mintern
 */
public class InterfaceExample {
   static class Drink implements JsonSerialization<Drink>, JsonDeserialization<DrinkDeserializer>{
        protected final String name;

        Drink(String name) {
            this.name = name;
        }

        @Override public String toString() {
            return name;
        }

        @Override
        public JsonElement toJsonTree(GsonContext<Drink> context) {
            JsonObject object = context.thisToJsonTree(this).getAsJsonObject();
            object.add("virgin", new JsonPrimitive(true));
            return object;
        }
    }
    
    static class MixedDrink extends Drink {
        private final Drink mix;
        private final String alcohol;

        MixedDrink(String name, Drink mix, String alcohol) {
            super(name);
            this.mix = mix;
            this.alcohol = alcohol;
        }

        @Override public String toString() {
            return name + " (" + mix + "+" + alcohol + ")";
        }

        @Override
        public JsonElement toJsonTree(GsonContext<Drink> context) {
            return context.thisToJsonTree(this);
        }
    }

    static class DrinkDeserializer implements JsonDeserializes<Drink> {
        @Override
        public Drink fromJsonTree(JsonElement json, Type type, GsonContext<Drink> context) {
            JsonObject object = json.getAsJsonObject();
            if (object.has("alcohol")) {
                return context.thisFromJsonTree(json, MixedDrink.class);
            } else {
                return context.thisFromJsonTree(json);
            }
        }
    }
    public static void main(String[] args) {
        Drink orangeJuice = new Drink("Orange Juice");
        MixedDrink screwdriver = new MixedDrink("Screwdriver", orangeJuice, "Vodka");

        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new InterfaceAdapterFactory())
                .create();

        // exercise toJson
        System.out.println(gson.toJson(orangeJuice));
        System.out.println(gson.toJson(screwdriver));
        
        // exercise fromJson
        String s = "{'name':'Orange Juice','virgin':true}";
        String t = "{'mix':{'name':'Orange Juice','virgin':true},'alcohol':'Vodka','name':'Screwdriver'}";
        System.out.println(gson.fromJson(s, Drink.class));
        System.out.println(gson.fromJson(t, Drink.class));
    }
}
