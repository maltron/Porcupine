/**
 * Copyright 2014 Mauricio "Maltron" Leal <maltron@gmail.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.nortlam.porcupine.authorization.controller;

import java.io.Serializable;
import java.util.Locale;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author Mauricio "Maltron" Leal */
@Named("locale")
@ViewScoped
public class LocaleController extends AbstractController implements Serializable {

    private static final Logger LOG = Logger.getLogger(LocaleController.class.getName());

    public LocaleController() {
    }
    
    public void english() {
        getViewRoot().setLocale(Locale.ENGLISH);
    }
    
    public void brazilianPortuguese() {
        getViewRoot().setLocale(new Locale("pt", "BR"));
    }

}
