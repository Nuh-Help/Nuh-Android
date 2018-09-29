/*===========================================================================\
 * Copyright 2018. WANAT                                                     *
 *                                                                           *
 * Licensed under the Apache License, Version 2.0 (the "License");           *
 * you may not use this file except in compliance with the License.          *
 * You may obtain a copy of the License at                                   *
 *                                                                           *
 * http://www.apache.org/licenses/LICENSE-2.0                                *
 *                                                                           *
 * Unless required by applicable law or agreed to in writing, software       *
 * distributed under the License is distributed on an "AS IS" BASIS,         *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
 * See the License for the specific language governing permissions and       *
 * limitations under the License.                                            *
 \==========================================================================*/

package org.isa.nuh;

/**
 * Interface providing app classes with KEYs
 * for key-value based {@link android.content.SharedPreferences} storage system.
 */
public interface SPController {

    // Base url of the server.
    String URL = "http://192.168.1.18:8000";

    String LOGIN = "LOGIN";
    String IS_LOGGED_IN = "IS_LOGGED_IN";

    String USERNAME = "USERNAME";
    String PASSWORD = "PASSWORD";
    String FIRST_NAME = "FIRST_NAME";
    String LAST_NAME = "LAST_NAME";
    String EMAIL = "EMAIL";

    String LATITUDE = "LATITUDE";
    String LONGITUDE = "LONGITUDE";

    String HELP_CATEGORIES = "HELP_CATEGORIES";

    String ACCOMODATION_NEED = "ACCOMODATION_NEED";
    String FOOD_NEED = "FOOD_NEED";
    String CLOTHES_NEED = "CLOTHES_NEED";
    String MEDICINE_NEED = "MEDICINE_NEED";
    String OTHER_NEED = "OTHER_NEED";

    String ACCOMODATION_NEED_TEXT = "ACCOMODATION_NEED_TEXT";
    String FOOD_NEED_TEXT = "FOOD_NEED_TEXT";
    String CLOTHES_NEED_TEXT = "CLOTHES_NEED_TEXT";
    String MEDICINE_NEED_TEXT = "MEDICINE_NEED_TEXT";
    String OTHER_NEED_TEXT = "OTHER_NEED_TEXT";

    String ACCOMODATION_GIVE = "ACCOMODATION_GIVE";
    String FOOD_GIVE = "FOOD_GIVE";
    String CLOTHES_GIVE = "CLOTHES_GIVE";
    String MEDICINE_GIVE = "MEDICINE_GIVE";
    String OTHER_GIVE = "OTHER_GIVE";

    String ACCOMODATION_GIVE_TEXT = "ACCOMODATION_GIVE_TEXT";
    String FOOD_GIVE_TEXT = "FOOD_GIVE_TEXT";
    String CLOTHES_GIVE_TEXT = "CLOTHES_GIVE_TEXT";
    String MEDICINE_GIVE_TEXT = "MEDICINE_GIVE_TEXT";
    String OTHER_GIVE_TEXT = "OTHER_GIVE_TEXT";
}
